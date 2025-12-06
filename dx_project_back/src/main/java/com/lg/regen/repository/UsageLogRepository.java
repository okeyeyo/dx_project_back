package com.lg.regen.repository;

import com.lg.regen.entity.UsageLogEntity;
import com.lg.regen.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface UsageLogRepository extends JpaRepository<UsageLogEntity, Long> {

    // 오늘 사용량
    List<UsageLogEntity> findByUserAndDate(UserEntity user, LocalDate date);

    // 월간 사용량
    List<UsageLogEntity> findByUserAndDateBetween(UserEntity user, LocalDate start, LocalDate end);
}

