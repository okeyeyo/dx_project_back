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
                "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&current=temperature_2m,relative_humidity_2m,weather_code&forecast_days=1",
                latitude, longitude
        );

        System.out.println("Open-Meteo API 호출 => " + url);

        try {
            // 외부 API 호출 및 응답 받기 (JSON 형태로 응답 받음)
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);

            if (response != null && response.has("current")) {
                JsonNode current = response.get("current");

                // 변수 정의 및 JSON 파싱 수행
                int wmoCode = current.get("weather_code").asInt(); //
                double temperature = current.get("temperature_2m").asDouble(); //
                double humidity = current.get("relative_humidity_2m").asDouble(); //

                String icon = mapWmoCodeToIcon(wmoCode);

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

    private String mapWmoCodeToIcon(int code) {
        if (code <= 3) return "☀️"; // 맑음/부분 흐림
        if (code >= 45 && code <= 48) return "🌫️"; // 안개
        if (code >= 51 && code <= 67) return "🌧️"; // 이슬비, 비
        if (code >= 71 && code <= 77) return "❄️"; // 눈
        if (code >= 80 && code <= 82) return "☔"; // 소나기
        return "☁️"; // 기타 흐림
    }
}
