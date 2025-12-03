package com.lg.regen.repository;

import com.lg.regen.entity.TokenTopUpEntity;
import com.lg.regen.entity.MeterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TokenTopUpRepository extends JpaRepository<TokenTopUpEntity, Long> {

    // 필요하면 한 계량기의 충전 내역 조회할 때 사용
    List<TokenTopUpEntity> findByMeter(MeterEntity meter);
}
