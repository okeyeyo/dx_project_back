// src/main/java/com/lg/regen/controller/PrepaidController.java
package com.lg.regen.controller;

import com.lg.regen.dto.PrepaidDashboardDTO;
import com.lg.regen.dto.PrepaidRequestDTO;
import com.lg.regen.service.PrepaidMeterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PrepaidController {

    private final PrepaidMeterService prepaidMeterService;

    /**
     * 선불 토큰 충전 API
     * - Flutter에서 "설정 완료 및 저장" 버튼 눌렀을 때 호출
     */
    @PostMapping("/users/{userId}/prepaid-topup")
    public PrepaidDashboardDTO topUpPrepaid(
            @PathVariable Long userId,
            @RequestBody PrepaidRequestDTO request
    ) {
        return prepaidMeterService.topUpPrepaid(userId, request);
    }

    /**
     * (선택) 선불 대시보드 조회 API
     * - 나중에 필요하면 Flutter에서 호출
     */
    @GetMapping("/users/{userId}/prepaid-dashboard")
    public PrepaidDashboardDTO getPrepaidDashboard(@PathVariable Long userId) {
        return prepaidMeterService.getPrepaidDashboard(userId);
    }
}
