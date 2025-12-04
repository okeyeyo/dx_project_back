package com.lg.regen.repository;

import com.lg.regen.entity.MeterEntity;
import com.lg.regen.entity.PrepaidMeterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrepaidMeterRepository extends JpaRepository<PrepaidMeterEntity, Long> {

    // 하나의 Meter에 대응하는 선불 정보 1개 찾기
    Optional<PrepaidMeterEntity> findByMeter(MeterEntity meter);
}