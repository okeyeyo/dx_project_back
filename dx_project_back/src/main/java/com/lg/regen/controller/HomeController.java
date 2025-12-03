package com.lg.regen.controller;


import com.lg.regen.dto.DashboardResponseDTO;
import com.lg.regen.dto.WeatherDTO;
import com.lg.regen.entity.UserEntity;
import com.lg.regen.repository.UserRepository;
import com.lg.regen.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/home") // 주소 : http://localhost:8082/api/home
public class HomeController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WeatherService weatherService;

//    @Autowired
//    private  PrayerService prayerService;


    @GetMapping("/{userID}")
    public DashboardResponseDTO getDashboardDate(@PathVariable Long userID) {
        // 사용자 찾기
        Optional<UserEntity> userEntityOptional = userRepository.findById(userID);
        if(userEntityOptional.isEmpty()) return null;

        UserEntity user = userEntityOptional.get();

        // 위치 정보
        double lat = user.getLatitude() != null ? user.getLatitude() : 0.0;
        double lon = user.getLongitude() != null ? user.getLongitude() : 0.0;
        String region = user.getRegion() != null ? user.getRegion() : "Unkown";

        // 날씨 서비스만 호출
        WeatherDTO weatherData = weatherService.getCurrentWeather(lat, lon, region);

        // 데이터 통합 (기도 시간은 나중에 수정 바람)
        return DashboardResponseDTO.builder()
                .userId(user.getId())
                .userName(user.getName())
                .region(region)
                .weatherData(weatherData)
                .prayerTimes(null)
                .build();
    }
}
