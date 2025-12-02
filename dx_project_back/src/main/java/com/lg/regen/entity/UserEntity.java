package com.lg.regen.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity // 1. "이건 DB 테이블 설계도야"라고 알려주는 표시
@Getter @Setter // 2. 데이터를 넣고 빼는 기능 자동 생성 (Lombok)
@Table(name = "users") // 3. MySQL에 'users'라는 이름으로 저장됨
public class UserEntity {

    @Id // 4. 주민등록번호 같은 고유 번호 (PK)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 번호 자동 증가 (1, 2, 3...)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email; // 아이디 (이메일)

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column(name="name")
    private String name; // 이름 (예: DemoUser)

    // --- 무슬림 루틴 앱을 위한 핵심 데이터 ---
    // 나중에 이 좌표로 '기도 시간'을 계산합니다.
    private Double latitude;  // 위도 (예: -6.2088)
    private Double longitude; // 경도 (예: 106.8456)
    private String region;    // 지역명 (예: Jakarta)
}