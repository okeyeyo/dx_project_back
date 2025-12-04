package com.lg.regen.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.lg.regen.dto.WeatherDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {
    private final RestTemplate restTemplate = new RestTemplate();

    public WeatherDTO getCurrentWeather(double latitude, double longitude, String region) {
        // 현재 온도와 습도 요청, forecast_days=1 : 오늘 데이터만 요청
        String url = String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&current=temperature_2m,relative_humidity_2m,weather_code,is_day&forecast_days=1&timezone=auto",
                latitude, longitude
        );

        System.out.println("Open-Meteo API 호출 => " + url);

        try {
            // 외부 API 호출 및 응답 받기 (JSON 형태로 응답 받음)
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);

            if (response != null && response.has("current")) {
                JsonNode current = response.get("current");

                // 변수 정의 및 JSON 파싱 수행
                int wmoCode = current.has("weather_code") ? current.get("weather_code").asInt() : 0;
                int isDay = current.has("is_day") ? current.get("is_day").asInt() : 1; // 기본값 낮(1)
                // 안전하게 데이터 가져오기
                double temperature = current.get("temperature_2m").asDouble();
                double humidity = current.get("relative_humidity_2m").asDouble();
                // 아이콘 변환
                String icon = mapWmoCodeToIcon(wmoCode, isDay);

                // 성공적으로 데이터를 받은 경우에만 DTO 반환
                return new WeatherDTO(
                        region, // 매개변수에서 받은 region 사용
                        temperature,
                        humidity,
                        icon
                );
            }
        } catch (Exception e) {
            // 통신 실패 시 통신 오류 로그 남김
            System.err.println("weather API 통신 오류: " + e.getMessage());
        }

        // 오류 발생 시 null 반환
        return null;
    }

    // 🌤️ 날씨 코드(WMO)를 정밀하게 이모지로 변환하는 함수 (Open-Meteo 공식 기준)
    private String mapWmoCodeToIcon(int code, int isDay) {

        // 1. 밤(Night)일 경우 특별 처리 (맑음/구름만 다름)
        if (isDay == 0) {
            switch (code) {
                case 0: return "🌙"; // 맑은 밤
                case 1:
                case 2: return "☁️"; // 밤에 구름은 그냥 구름으로
                // 비, 눈, 안개 등은 밤에도 똑같은 아이콘 사용
            }
        }

        // 2. 낮(Day)이거나 공통 날씨
        switch (code) {
            // ☀️ 맑음 & 구름
            case 0: return "☀️"; // 맑음
            case 1: return "🌤"; // 해+구름
            case 2: return "⛅"; // 구름 많음
            case 3: return "☁️"; // 흐림

            // 🌫 안개
            case 45: case 48: return "🌫️";

            // 🌧 비 (이슬비, 일반 비, 소나기 통합)
            case 51: case 53: case 55:
            case 61: case 63: case 65:
            case 80: case 81: case 82:
                return "🌧️";

            // 🌨 눈 (진눈깨비, 눈, 눈소나기 통합)
            case 56: case 57:
            case 66: case 67: // 진눈깨비도 눈으로 표현
            case 71: case 73: case 75:
            case 77:
            case 85: case 86:
                return "🌨️";

            // ⚡ 천둥번개
            case 95: case 96: case 99:
                return "⚡";

            default: return "❓";
        }
    }
}
