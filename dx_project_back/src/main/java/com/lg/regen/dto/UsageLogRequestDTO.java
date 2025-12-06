package com.lg.regen.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UsageLogRequestDTO {

    // Flutter에서 보낸 날짜 (예: 2025-12-06)
    // 안 보내면 서버에서 LocalDate.now()를 쓸 거라 필수는 아님
    private LocalDate date;

    // 해당 날짜의 총 사용량(kWh)
    private double usageKwh;
}