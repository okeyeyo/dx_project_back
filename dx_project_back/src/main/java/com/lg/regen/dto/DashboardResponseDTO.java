package com.lg.regen.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@Builder // 안전하고 편리하게 객체를 생성
public class DashboardResponseDTO { // 통합 대시보드 API (서버 호출 횟수 감소, 데이터 일관성 보장, API 게이트웨이 역할 분담)
    // 사용자 정보 (UserEntity)
    private Long userId;
    private String userName;
    private String region;

    // 날씨 정보 (WeatherService)
    private WeatherDTO weatherData;

    // 전력 정보 추가

}
