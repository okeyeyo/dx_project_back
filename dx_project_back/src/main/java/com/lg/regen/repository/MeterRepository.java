package com.lg.regen.repository;


import com.lg.regen.entity.MeterEntity;
import com.lg.regen.enums.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeterRepository extends JpaRepository<MeterEntity, Long> {

    // 특정 유저의 선불/후불 계량기 한 개를 조회하는 메서드
    Optional<MeterEntity> findByUserIdAndPaymentType(Long userId, PaymentType paymentType);
}
