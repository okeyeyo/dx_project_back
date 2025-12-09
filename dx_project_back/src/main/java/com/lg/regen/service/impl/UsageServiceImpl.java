package com.lg.regen.service.impl;

import com.lg.regen.entity.UsageLogEntity;
import com.lg.regen.entity.UserEntity;
import com.lg.regen.repository.UsageLogRepository;
import com.lg.regen.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;

import java.time.LocalDate;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class UsageServiceImpl implements UsageService {

    private final UsageLogRepository usageLogRepository;

    @Override
    public double getTodayUsageKwh(UserEntity user, LocalDate today) {
        return usageLogRepository.findByUserAndDate(user, today)
                .map(UsageLogEntity::getUsageKwh)
                .orElse(0.0);
    }

    @Override
    public double getMonthUsageKwh(UserEntity user, YearMonth ym) {
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        return usageLogRepository.findByUserAndDateBetween(user, start, end)
                .stream()
                .mapToDouble(UsageLogEntity::getUsageKwh)
                .sum();
    }

    @Override
    @Transactional
    public void upsertDailyUsage(UserEntity user, LocalDate date, double usageKwh) {
        // ✅ 같은 날짜(user + date) 로그가 있으면 그대로 가져오고, 없으면 새로 생성
        UsageLogEntity log = usageLogRepository.findByUserAndDate(user, date)
                .orElseGet(() -> {
                    UsageLogEntity e = new UsageLogEntity();
                    e.setUser(user);
                    e.setDate(date);
                    return e;
                });

        // 항상 최신 사용량으로 덮어쓰기
        log.setUsageKwh(usageKwh);

        usageLogRepository.save(log);
    }
}
