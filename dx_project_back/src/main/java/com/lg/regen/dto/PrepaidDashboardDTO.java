package com.lg.regen.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrepaidDashboardDTO {

    private double totalTokenKwh;       // 지금까지 충전된 총 토큰(kWh) → 게이지의 "총 토큰 / 잔여 토큰"에 사용
    private double usedTokenKwh;        // 사용한 토큰 (지금은 0으로 두고, 나중에 사용량 로직 만들면 채우기)
}
