// src/main/java/com/lg/regen/service/impl/PrepaidMeterServiceImpl.java
package com.lg.regen.service.impl;

import com.lg.regen.dto.PrepaidDashboardDTO;
import com.lg.regen.dto.PrepaidRequestDTO;
import com.lg.regen.entity.MeterEntity;
import com.lg.regen.entity.PrepaidMeterEntity;
import com.lg.regen.entity.TokenTopUpEntity;
import com.lg.regen.entity.UserEntity;
import com.lg.regen.enums.MeterBrand;
import com.lg.regen.enums.PaymentType;
import com.lg.regen.repository.MeterRepository;
import com.lg.regen.repository.PrepaidMeterRepository;
import com.lg.regen.repository.TokenTopUpRepository;
import com.lg.regen.repository.UserRepository;
import com.lg.regen.service.PrepaidMeterService;
import com.lg.regen.service.UsageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.YearMonth;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PrepaidMeterServiceImpl implements PrepaidMeterService {

    private final MeterRepository meterRepository;
    private final PrepaidMeterRepository prepaidMeterRepository;
    private final UserRepository userRepository;
    private final TokenTopUpRepository tokenTopUpRepository;
    private final UsageService usageService;

    @Override
    @Transactional
    public PrepaidDashboardDTO topUpPrepaid(Long userId, PrepaidRequestDTO request) {
        System.out.println(">>> topUpPrepaid called: user=" + userId +
                ", brand=" + request.getBrand() +
                ", amount=" + request.getAmountKwh());
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
        double added = request.getAmountKwh();
        prepaid.setTotalTokenKwh(currentTotal + request.getAmountKwh());
        prepaid.setLastTopupAt(LocalDateTime.now());

        // 🔥 4-1) 토큰 충전 내역(token_top_up)에 기록
        TokenTopUpEntity history = TokenTopUpEntity.builder()
                .meter(meter)
                .amountKwh(added)
                .brand(brand.name())           // brand 컬럼이 String 이면 .name(), enum이면 brand 그대로
                .purchasedAt(LocalDateTime.now())
                .build();

        tokenTopUpRepository.save(history);

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

        // 2) 선불 정보 찾기 (총 토큰은 여기서 읽음)
        PrepaidMeterEntity prepaid = prepaidMeterRepository
                .findByMeter(meter)
                .orElseThrow(() -> new IllegalStateException("선불 계량기 토큰 정보가 없습니다."));

        // 🔥 이번 달 사용량 = UsageLog 기반
        UserEntity user = meter.getUser();
        YearMonth nowYm = YearMonth.now();
        double usedThisMonth = usageService.getMonthUsageKwh(user, nowYm);

        // 🔥 DB 컬럼에도 반영하고 싶다면 이 두 줄 추가
        prepaid.setUsedTokenKwh(usedThisMonth);
        prepaidMeterRepository.save(prepaid);

        // 4) 대시보드 DTO 구성
        PrepaidDashboardDTO dto = new PrepaidDashboardDTO();
        dto.setTotalTokenKwh(prepaid.getTotalTokenKwh());  // 충전된 총 토큰
        dto.setUsedTokenKwh(usedThisMonth);                // 이번 달 사용 토큰 (UsageLog 기준)

        return dto;
    }
}
