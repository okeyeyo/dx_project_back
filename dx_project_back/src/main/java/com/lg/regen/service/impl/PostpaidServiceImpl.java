package com.lg.regen.service.impl;

import com.lg.regen.dto.BillSummaryDTO;
import com.lg.regen.dto.PostpaidDashboardDTO;
import com.lg.regen.entity.BillEntity;
import com.lg.regen.entity.UserEntity;
import com.lg.regen.repository.BillRepository;
import com.lg.regen.repository.UserRepository;
import com.lg.regen.service.PostpaidService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostpaidServiceImpl implements PostpaidService {

    private final UserRepository userRepository;
    private final BillRepository billRepository;

    @Override
    @Transactional
    public PostpaidDashboardDTO getPostpaidDashboard(Long userId) {

        // 1) 유저 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        PostpaidDashboardDTO dto = new PostpaidDashboardDTO();

        // 2) 가장 최근 청구서 1개 가져와서
        //    - 이번 달 예상 요금
        //    - 결제일
        //    - 이번 달 사용량(usageKwh) 세팅
        billRepository.findTop1ByUserOrderByPeriodEndDesc(user)
                .ifPresent(bill -> {
                    dto.setExpectedAmount(bill.getAmount());      // 이번 달 예상 요금
                    dto.setDueDate(bill.getDueDate());            // 결제일
                    dto.setMonthUsageKwh(bill.getUsageKwh());    // 이번 달 사용량
                });

        // 3) 오늘 사용량은 아직 별도 데이터가 없으니,
        //    일단 데모 값 또는 간단한 로직으로 채워두기 (TODO: 추후 실제 사용량 연동)
        if (dto.getMonthUsageKwh() > 0) {
            // 예시: 이번 달 사용량을 일수로 나눠서 대략적인 오늘 사용량으로 사용
            dto.setTodayUsageKwh(dto.getMonthUsageKwh() / 30.0);
        } else {
            // 청구서가 아직 없으면 데모 값
            dto.setTodayUsageKwh(0.0);
            dto.setMonthUsageKwh(0.0);
        }

        // 4) 최근 3개 청구 내역 리스트 → BillSummaryDTO로 변환
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
