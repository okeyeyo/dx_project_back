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
                .stream()
                .mapToDouble(UsageLogEntity::getUsageKwh)
                .sum();
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
        // 같은 날짜 기록이 있으면 첫 번째 것만 사용
        List<UsageLogEntity> logs = usageLogRepository.findByUserAndDate(user, date);

        UsageLogEntity log;
        if (logs.isEmpty()) {
            log = new UsageLogEntity();
            log.setUser(user);
            log.setDate(date);
        } else {
            log = logs.get(0);
        }
        log.setUsageKwh(usageKwh);

        usageLogRepository.save(log);
    }
}
