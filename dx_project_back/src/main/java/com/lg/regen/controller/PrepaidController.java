package com.lg.regen.controller;

import com.lg.regen.dto.PrepaidDashboardDTO;
import com.lg.regen.dto.PrepaidRequestDTO;
import com.lg.regen.entity.MeterEntity;
import com.lg.regen.entity.TokenTopUpEntity;
import com.lg.regen.entity.UserEntity;
import com.lg.regen.enums.MeterBrand;
import com.lg.regen.enums.PaymentType;
import com.lg.regen.repository.MeterRepository;
import com.lg.regen.repository.TokenTopUpRepository;
import com.lg.regen.repository.UserRepository;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class PrepaidController {

    private final UserRepository userRepository;                    // 유저 정보 찾기
    private final MeterRepository meterRepository;                  // 선불 계량기(MeterEntity) 찾기/저장
    private final TokenTopUpRepository tokenTopUpRepository;        // 충전 내역(TokenTopUpEntity) 저장

    public PrepaidController(UserRepository userRepository,
                             MeterRepository meterRepository,
                             TokenTopUpRepository tokenTopUpRepository) {
        this.userRepository = userRepository;
        this.meterRepository = meterRepository;
        this.tokenTopUpRepository = tokenTopUpRepository;
    }

    /**
     * 선불 토큰 충전 API
     *  - Flutter에서 "설정 완료 및 저장" 버튼 눌렀을 때 호출
     *  - 이번에 추가한 토큰만큼 Meter.totalTokenKwh에 더해주고,
     *    TokenTopUpEntity에 기록까지 남긴 뒤
     *    최신 totalTokenKwh / usedTokenKwh를 돌려준다.
     */
    @PostMapping("/users/{userId}/prepaid-topup")
    public PrepaidDashboardDTO topUpPrepaid(
            @PathVariable Long userId,
            @RequestBody PrepaidRequestDTO request
    ) {
        // 1) 유저 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        // 2) 문자열로 온 브랜드를 enum으로 변환 (대소문자 방지용 upperCase)
        MeterBrand brand = MeterBrand.valueOf(request.getBrand().toUpperCase());

        // 3) 이 유저의 선불 계량기 찾기 (없으면 새로 생성)
        MeterEntity meter = meterRepository
                .findByUserIdAndPaymentType(userId, PaymentType.PREPAID)
                .orElseGet(() -> {
                    MeterEntity m = new MeterEntity();
                    m.setUser(user);
                    m.setPaymentType(PaymentType.PREPAID);
                    m.setBrand(brand);
                    m.setTotalTokenKwh(0.0);
                    return meterRepository.save(m);
                });

        // 4) 총 토큰 양 업데이트
        double newTotal = meter.getTotalTokenKwh() + request.getAmountKwh();
        meter.setTotalTokenKwh(newTotal);
        meter.setBrand(brand); // 마지막에 선택한 브랜드로 업데이트
        meterRepository.save(meter);

        // 5) 충전 내역 기록
        TokenTopUpEntity topUp = new TokenTopUpEntity();
        topUp.setMeter(meter);
        topUp.setBrand(brand);
        topUp.setAmountKwh(request.getAmountKwh());
        tokenTopUpRepository.save(topUp);

        // 6) Flutter에 돌려줄 대시보드 데이터 구성
        PrepaidDashboardDTO dto = new PrepaidDashboardDTO();
        dto.setTotalTokenKwh(newTotal); // 새로 계산한 토큰 총량
        dto.setUsedTokenKwh(0.0); // TODO: 나중에 사용량 로직 만들면 채우기

        return dto;
    }
}
