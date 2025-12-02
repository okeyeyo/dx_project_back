package com.lg.regen.controller;

import com.lg.regen.dto.BillSummaryDTO;
import com.lg.regen.dto.PostpaidDashboardDTO;
import com.lg.regen.entity.BillEntity;
import com.lg.regen.entity.UserEntity;
import com.lg.regen.repository.BillRepository;
import com.lg.regen.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class BillingController {

    private final UserRepository userRepository;
    private final BillRepository billRepository;

    public BillingController(UserRepository userRepository,
                             BillRepository billRepository) {
        this.userRepository = userRepository;
        this.billRepository = billRepository;
    }

    @GetMapping("/users/{userId}/postpaid-dashboard")
    public PostpaidDashboardDTO getPostpaidDashboard(@PathVariable Long userId) {

        // 1) 유저 정보 조회 (후불은 계량기 없이 유저 기준)
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        PostpaidDashboardDTO dto = new PostpaidDashboardDTO();

        // 2) (지금은) 오늘/이번 달 사용량에 임의 값 넣기
        //    → 나중에 BillEntity.usageKwh 기반으로 계산하고 싶으면 여기 로직 수정
        dto.setTodayUsageKwh(42);   // TODO: 임시 데모 값
        dto.setMonthUsageKwh(850);  // TODO: 임시 데모 값

        // 3) 가장 최근 청구서 1개 → 이번 달 예상 요금 + 결제일
        billRepository.findTop1ByUserOrderByPeriodEndDesc(user)
                .ifPresent(bill -> {
                    dto.setExpectedAmount(bill.getAmount());  // 예: 245000
                    dto.setDueDate(bill.getDueDate());        // 예: 2025-12-05
                });

        // 4) 최근 3개 청구 내역 리스트 만들기
        List<BillEntity> billEntities =
                billRepository.findTop3ByUserOrderByPeriodEndDesc(user);

        List<BillSummaryDTO> billDTOs = billEntities.stream()
                .map(b -> {
                    BillSummaryDTO row = new BillSummaryDTO();
                    row.setBillId(b.getId());
                    row.setYear(b.getPeriodEnd().getYear());
                    row.setMonth(b.getPeriodEnd().getMonthValue());
                    row.setAmount(b.getAmount());
                    row.setStatus(b.getStatus());
                    return row;
                })
                .collect(Collectors.toList());

        dto.setBills(billDTOs);

        return dto;
    }
}