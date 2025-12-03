package com.lg.regen.dto;

import lombok.*;

@Getter @Setter @ToString
@AllArgsConstructor // 4개 인수를 받는 생성자가 자동으로 생성되어 호출이 가능해진다. (즉, 자동 완성 생성자)
public class WeatherDTO {
    private String region;      // 도시 이름
    private double temperature; // 현재 온도
    private  double humidity;   // 현재 습도
    private String weatherIcon; // 날씨 이모지
}
