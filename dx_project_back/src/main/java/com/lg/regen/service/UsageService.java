package com.lg.regen.service;

import com.lg.regen.entity.UserEntity;

import java.time.LocalDate;
import java.time.YearMonth;

public interface UsageService {
    double getTodayUsageKwh(UserEntity user, LocalDate today);
    double getMonthUsageKwh(UserEntity user, YearMonth yearMonth);

    // 🔥 Flutter에서 보낸 "오늘 총 사용량"을 저장(있으면 업데이트, 없으면 생성)
    void upsertDailyUsage(UserEntity user, LocalDate date, double usageKwh);
}
