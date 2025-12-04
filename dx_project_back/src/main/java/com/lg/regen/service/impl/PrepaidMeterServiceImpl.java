// src/main/java/com/lg/regen/service/impl/PrepaidMeterServiceImpl.java
package com.lg.regen.service.impl;

import com.lg.regen.dto.PrepaidDashboardDTO;
import com.lg.regen.dto.PrepaidRequestDTO;
import com.lg.regen.entity.MeterEntity;
import com.lg.regen.entity.PrepaidMeterEntity;
import com.lg.regen.entity.UserEntity;
import com.lg.regen.enums.MeterBrand;
import com.lg.regen.enums.PaymentType;
import com.lg.regen.repository.MeterRepository;
import com.lg.regen.repository.PrepaidMeterRepository;
import com.lg.regen.repository.UserRepository;
import com.lg.regen.service.PrepaidMeterService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PrepaidMeterServiceImpl implements PrepaidMeterService {

    private final MeterRepository meterRepository;
    private final PrepaidMeterRepository prepaidMeterRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PrepaidDashboardDTO topUpPrepaid(Long userId, PrepaidRequestDTO request) {
        // 0) 브랜드 enum 변환
        MeterBrand brand = MeterBrand.valueOf(request.getBrand().toUpperCase());

        // 1) 유저 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다."));

        // 2) 유저의 선불 계량기 조회 or 생성
        MeterEntity meter = meterRepository
                .findByUserIdAndPaymentType(userId, PaymentType.PREPAID)
                .orElseGet(() -> {
                    MeterEntity m = new MeterEntity();
                    m.setUser(user);
                    m.setPaymentType(PaymentType.PREPAID);
                    m.setBrand(brand);
                    return meterRepository.save(m);
                });

        // 기존 계량기가 있으면, 선택된 브랜드로 업데이트(선택사항)
        meter.setBrand(brand);

        // 3) 선불 정보(PrepaidMeterEntity) 조회 or 생성
        PrepaidMeterEntity prepaid = prepaidMeterRepository
                .findByMeter(meter)
                .orElseGet(() -> PrepaidMeterEntity.builder()
                        .meter(meter)
                        .totalTokenKwh(0.0)
                        .usedTokenKwh(0.0)
                        .build()
                );

        // 4) 토큰 누적
        double currentTotal = prepaid.getTotalTokenKwh();
        prepaid.setTotalTokenKwh(currentTotal + request.getAmountKwh());
        prepaid.setLastTopupAt(LocalDateTime.now());

        // 5) 저장
        prepaidMeterRepository.save(prepaid);

        // 6) 대시보드 DTO 반환
        PrepaidDashboardDTO dto = new PrepaidDashboardDTO();
        dto.setTotalTokenKwh(prepaid.getTotalTokenKwh());
        dto.setUsedTokenKwh(prepaid.getUsedTokenKwh());

        return dto;
    }

    @Override
    @Transactional
    public PrepaidDashboardDTO getPrepaidDashboard(Long userId) {
        // 1) 유저의 선불 계량기 찾기
        MeterEntity meter = meterRepository
                .findByUserIdAndPaymentType(userId, PaymentType.PREPAID)
                .orElseThrow(() -> new IllegalStateException("해당 유저의 선불 계량기가 없습니다."));

        // 2) 선불 정보 찾기
        PrepaidMeterEntity prepaid = prepaidMeterRepository
                .findByMeter(meter)
                .orElseThrow(() -> new IllegalStateException("선불 계량기 토큰 정보가 없습니다."));

        // 3) 대시보드 DTO 반환
        PrepaidDashboardDTO dto = new PrepaidDashboardDTO();
        dto.setTotalTokenKwh(prepaid.getTotalTokenKwh());
        dto.setUsedTokenKwh(prepaid.getUsedTokenKwh());

        return dto;
    }
}
