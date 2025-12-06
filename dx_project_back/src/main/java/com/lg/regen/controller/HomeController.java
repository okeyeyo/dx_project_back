package com.lg.regen.controller;


import com.lg.regen.dto.DashboardResponseDTO;
import com.lg.regen.dto.WeatherDTO;
import com.lg.regen.dto.PrepaidDashboardDTO;
import com.lg.regen.entity.UserEntity;
import com.lg.regen.dto.PostpaidDashboardDTO;
import com.lg.regen.repository.UserRepository;
import com.lg.regen.service.WeatherService;
import com.lg.regen.service.PostpaidService;
import com.lg.regen.service.PrepaidMeterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/home") // 주소 : http://localhost:8082/home
public class HomeController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WeatherService weatherService;

    // 선불/후불 서비스 추가
    @Autowired
    private PrepaidMeterService prepaidMeterService;

    @Autowired
    private PostpaidService postpaidService;


    @GetMapping("/{userID}")
    public DashboardResponseDTO getDashboardData(@PathVariable Long userID) {
        // 사용자 찾기
        UserEntity user = userRepository.findById(userID)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        // 위치 정보
        double lat = user.getLatitude() != null ? user.getLatitude() : 0.0;
        double lon = user.getLongitude() != null ? user.getLongitude() : 0.0;
        String region = user.getRegion() != null ? user.getRegion() : "Unknown";

        // 날씨 서비스만 호출
        WeatherDTO weatherData = weatherService.getCurrentWeather(lat, lon, region);


        // 🔋 전력(선불/후불) 대시보드 조회
        PrepaidDashboardDTO prepaid = null;
        PostpaidDashboardDTO postpaid = null;

        try {
            prepaid = prepaidMeterService.getPrepaidDashboard(user.getId());
        } catch (IllegalStateException e) {
            // 선불 계량기가 없을 때 예외 던지는 경우 방어용
            // 필요하면 log.warn 정도만 찍고 무시
        }

        try {
            postpaid = postpaidService.getPostpaidDashboard(user.getId());
        } catch (IllegalStateException e) {
            // 후불 계량기 없을 때 방어
        }

        // 데이터 통합
        return DashboardResponseDTO.builder()
                .userId(user.getId())
                .userName(user.getName())
                .region(region)
                .weatherData(weatherData)
                .prepaidDashboard(prepaid)
                .postpaidDashboard(postpaid)
                .build();
    }
}
