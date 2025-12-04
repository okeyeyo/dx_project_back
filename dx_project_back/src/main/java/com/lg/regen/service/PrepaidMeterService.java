package com.lg.regen.service;

import com.lg.regen.dto.PrepaidDashboardDTO;
import com.lg.regen.dto.PrepaidRequestDTO;

public interface PrepaidMeterService {

    /**
     * 선불 토큰 충전 (또는 설정) 후 대시보드 정보 반환.
     */
    PrepaidDashboardDTO topUpPrepaid(Long userId, PrepaidRequestDTO request);

    /**
     * (선택) 단순히 대시보드만 조회하고 싶을 때 사용할 메서드
     */
    PrepaidDashboardDTO getPrepaidDashboard(Long userId);
}