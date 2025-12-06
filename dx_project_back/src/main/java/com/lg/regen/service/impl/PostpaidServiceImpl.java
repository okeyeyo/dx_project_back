package com.lg.regen.service.impl;

import com.lg.regen.dto.BillSummaryDTO;
import com.lg.regen.dto.PostpaidDashboardDTO;
import com.lg.regen.entity.BillEntity;
import com.lg.regen.entity.UserEntity;
import com.lg.regen.enums.BillStatus;
import com.lg.regen.repository.BillRepository;
import com.lg.regen.repository.UserRepository;
import com.lg.regen.service.PostpaidService;
import com.lg.regen.service.UsageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostpaidServiceImpl implements PostpaidService {

    private final UserRepository userRepository;
    private final BillRepository billRepository;
    private final UsageService usageService;

    private long calculatePostpaidAmount(double usageKwh) {
        double energy = usageKwh * 1444.70;
        double subtotal = energy + 45950;
        double total = subtotal * 1.11;
        return Math.round(total);
    }

    @Override
    @Transactional
    public PostpaidDashboardDTO getPostpaidDashboard(Long userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        LocalDate today = LocalDate.now();
        YearMonth nowYm = YearMonth.from(today);

        // 🔥 1) 먼저 지난달 청구서 생성 (월 마감)
        closeLastMonthIfNeeded(user, nowYm);

        // 🔥 2) 오늘/이번달 사용량 (UsageLog 기반)
        double todayUsage = usageService.getTodayUsageKwh(user, today);
        double monthUsage = usageService.getMonthUsageKwh(user, nowYm);

        // 🔥 3) 이번 달 예상 요금
        long expectedAmount = calculatePostpaidAmount(monthUsage);

        // 🔥 4) 결제일 = 다음달 5일
        YearMonth nextYm = nowYm.plusMonths(1);
        LocalDate dueDate = nextYm.atDay(5);

        // 🔥 5) 최근 청구내역 3개
        List<BillSummaryDTO> bills = billRepository
                .findTop3ByUserOrderByPeriodEndDesc(user)
                .stream()
                .map(BillSummaryDTO::from)
                .toList();

        // 🔥 6) DTO 조립
        PostpaidDashboardDTO dto = new PostpaidDashboardDTO();
        dto.setExpectedAmount(expectedAmount);
        dto.setDueDate(dueDate);
        dto.setTodayUsageKwh(todayUsage);
        dto.setMonthUsageKwh(monthUsage);
        dto.setBills(bills);

        return dto;
    }

    // 🔥 월 마감: 지난달 사용량 → 청구 생성
    private void closeLastMonthIfNeeded(UserEntity user, YearMonth nowYm) {

        YearMonth lastYm = nowYm.minusMonths(1);

        LocalDate start = lastYm.atDay(1);
        LocalDate end = lastYm.atEndOfMonth();

        // 이미 지난달 청구가 있으면 skip
        if (billRepository.existsByUserAndPeriodStartAndPeriodEnd(user, start, end)) {
            return;
        }

        // 지난달 사용량 계산
        double lastMonthUsage = usageService.getMonthUsageKwh(user, lastYm);

        if (lastMonthUsage <= 0) return;

        long amount = calculatePostpaidAmount(lastMonthUsage);
        LocalDate dueDate = nowYm.atDay(5);

        BillEntity bill = BillEntity.builder()
                .user(user)
                .periodStart(start)
                .periodEnd(end)
                .usageKwh(lastMonthUsage)
                .amount(amount)
                .currency("IDR")
                .status(BillStatus.UNPAID)
                .dueDate(dueDate)
                .build();

        billRepository.save(bill);
    }
}

