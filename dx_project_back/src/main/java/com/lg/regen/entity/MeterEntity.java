package com.lg.regen.entity;

import com.lg.regen.enums.MeterBrand;
import com.lg.regen.enums.PaymentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 선불(Prepaid) 계량기 엔티티.
 * - 한 유저가 여러 개의 선불 계량기를 가질 수 있는 구조 (N:1).
 */
@Entity
@Table(name = "meters")
@Getter
@Setter
public class MeterEntity {

    // 기본 키(Primary Key)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 유저의 계량기인지 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)   // FK: users.id
    private UserEntity user;

    // 선불 / 후불 타입 (지금은 선불(PREPAID)만 사용)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentType paymentType = PaymentType.PREPAID;

    // 계량기 브랜드 (ITRON / HEXING / ACTARIS)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MeterBrand brand;

    // ───────── 선불(Prepaid) 관련 필드 ─────────

    // 지금까지 충전된 총 토큰 양 (kWh 단위)
    @Column(nullable = false)
    private double totalTokenKwh = 0.0;

    // ───────── 공통 메타 데이터 ─────────
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}