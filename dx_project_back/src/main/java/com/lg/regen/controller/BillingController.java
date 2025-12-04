package com.lg.regen.controller;

import com.lg.regen.dto.PostpaidDashboardDTO;
import com.lg.regen.service.PostpaidService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BillingController {

    private final PostpaidService postpaidService;

    @GetMapping("/users/{userId}/postpaid-dashboard")
    public PostpaidDashboardDTO getPostpaidDashboard(@PathVariable Long userId) {
        // 모든 로직은 서비스에서 처리
        return postpaidService.getPostpaidDashboard(userId);
    }
}