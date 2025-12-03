package com.lg.regen.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrepaidRequestDTO {

    private String brand;           // Flutter에서 보낼 계량기 브랜드 (예: "ITRON", "HEXING", "ACTARIS")
    private double amountKwh;       // 이번에 추가할 토큰 양 (kWh)
}
