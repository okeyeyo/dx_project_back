package com.lg.regen.entity;

import com.lg.regen.enums.MeterBrand;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * 선불(Prepaid) 토큰 충전 내역을 저장하는 JPA 엔티티.
 * - Flutter에서 토큰 충전 팝업에서 입력하는 값들을 기록해두는 용도.
 * - 하나의 MeterEntity에 대해 여러 번 충전할 수 있으므로 N:1 관계.
 */
@Entity
@Table(name = "token_top_up") // 실제 테이블 이름: token_top_up
@Getter
@Setter

public class TokenTopUpEntity {

    // 기본 키(Primary Key)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 계량기에 대한 충전인지 (N:1 관계)
    // Meter N개가 한 UserEntity 1개에 연결될 수 있음
    @ManyToOne(fetch = FetchType.LAZY)                // 지연 로딩: 필요할 때만 UserEntity 조회
    @JoinColumn(name = "meter_id", nullable = false)  // FK 컬럼 이름: meter_id
    private MeterEntity meter;

    // 충전할 때 선택된 계량기 브랜드
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MeterBrand brand;

    // 이번 충전으로 추가된 토큰 양 (kWh 단위)
    @Column(nullable = false)
    private double amountKwh;

    // 충전 일시
    // - 기본값: 엔티티 생성 시점의 현재 시각
    @Column(nullable = false)
    private LocalDateTime purchasedAt = LocalDateTime.now();

     // 엔티티가 처음 저장될 때 호출됨.
     // - purchasedAt이 비어있다면 현재 시간으로 세팅.
    @PrePersist
    protected void onCreate() {
        if (purchasedAt == null) {
            purchasedAt = LocalDateTime.now();
        }
    }
}