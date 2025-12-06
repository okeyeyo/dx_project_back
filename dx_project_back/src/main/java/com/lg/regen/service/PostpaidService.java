package com.lg.regen.service;

import com.lg.regen.dto.PostpaidDashboardDTO;

public interface PostpaidService {

    /**
     * 후불(postpaid) 대시보드 정보 조회
     * - 이번 달 예상 요금
     * - 결제일
     * - 오늘/이번 달 사용량
     * - 최근 청구 내역 리스트
     */
    PostpaidDashboardDTO getPostpaidDashboard(Long userId);


    // 🔥 후불제 요금 계산 공식
    // Total = (kWh × 1,444.70 + 45,950) × 1.11
    private long calculatePostpaidAmount(double usageKwh) {
        double energyCharge = usageKwh * 1444.70;   // 전력량 요금
        double subtotal = energyCharge + 45950;     // + 기본요금
        double total = subtotal * 1.11;             // × 1.11 (세금/부가요금)

        // Rp 단위 정수로 반올림
        return Math.round(total);
    }
}