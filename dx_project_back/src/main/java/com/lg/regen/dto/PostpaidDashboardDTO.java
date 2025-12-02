package com.lg.regen.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
public class PostpaidDashboardDTO {

    // 상단 카드
    private double expectedAmount;    // 이번 달 예상 요금
    private LocalDate dueDate;        // 결제일 (예: 2025-12-05)

    // 사용량 카드
    private double todayUsageKwh;
    private double monthUsageKwh;

    // 청구 내역 리스트
    private List<BillSummaryDTO> bills;
}
