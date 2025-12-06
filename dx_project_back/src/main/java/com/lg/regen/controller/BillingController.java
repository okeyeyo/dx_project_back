package com.lg.regen.controller;

import com.lg.regen.dto.PostpaidDashboardDTO;
import com.lg.regen.dto.UsageLogRequestDTO;
import com.lg.regen.entity.UserEntity;
import com.lg.regen.repository.UserRepository;
import com.lg.regen.service.PostpaidService;
import com.lg.regen.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BillingController {

    private final PostpaidService postpaidService;
    private final UserRepository userRepository;
    private final UsageService usageService;

    @GetMapping("/users/{userId}/postpaid-dashboard")
    public PostpaidDashboardDTO getPostpaidDashboard(@PathVariable Long userId) {
        // 모든 로직은 서비스에서 처리
        return postpaidService.getPostpaidDashboard(userId);
    }

    // Flutter가 오늘 사용량(todayKwh)을 보내는 엔드포인트
    @PostMapping("/users/{userId}/usage/today")
    public void saveTodayUsage(@PathVariable Long userId,
                               @RequestBody UsageLogRequestDTO request) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        LocalDate date = (request.getDate() != null)
                ? request.getDate()
                : LocalDate.now();

        usageService.upsertDailyUsage(user, date, request.getUsageKwh());
    }
}
