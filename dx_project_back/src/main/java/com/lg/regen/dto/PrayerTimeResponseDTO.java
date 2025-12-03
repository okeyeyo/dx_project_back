package com.lg.regen.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class PrayerTimeResponseDTO {
    // 날짜 정보 (Aladhan API 응답 기반)
    private String date;

    // 기도 시간 5가지 + 일출 시간 (hh:mm)
    private String Fajr;     // 새벽 예배
    private String Sunrise;  // 일출 시간 (Shurooq)
    private String Dhuhr;    // 정오 예배
    private String Asr;      // 오후 예배
    private String Maghrib;  // 일몰 예배
    private String Isha;     // 저녁 예배
}
