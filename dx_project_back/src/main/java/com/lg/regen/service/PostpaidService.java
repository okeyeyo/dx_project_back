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
}