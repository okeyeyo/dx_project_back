// src/main/java/com/lg/regen/entity/MeterEntity.java
package com.lg.regen.entity;

import com.lg.regen.enums.MeterBrand;
import com.lg.regen.enums.PaymentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 계량기 공통 엔티티.
 * - 한 유저가 여러 계량기를 가질 수 있는 구조 (N:1).
 * - 선불 토큰 정보는 PrepaidMeterEntity 에서 관리.
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

    // 선불 / 후불 타입 (지금은 선불(PREPAID) 위주)
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false, length = 20)
    private PaymentType paymentType = PaymentType.PREPAID;

    // 계량기 브랜드 (ITRON / HEXING / ACTARIS)
    @Enumerated(EnumType.STRING)
    @Column(name = "brand", nullable = false, length = 20)
    private MeterBrand brand;

    // 계약 전력 (VA) – 인도네시아에서 많이 쓰는 1300VA 기본
    @Column(name = "contract_power_va", nullable = false)
    private int contractPowerVa = 1300;

    // ───────── 선불 정보 엔티티와의 1:1 관계 ─────────
    @OneToOne(
            mappedBy = "meter",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private PrepaidMeterEntity prepaidInfo;

    // ───────── 공통 메타 데이터 ─────────
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ───────── 편의 메서드 (DB에는 안 저장됨) ─────────

    @Transient
    public double getRemainingTokenKwh() {
        if (prepaidInfo == null) return 0.0;
        return Math.max(0.0,
                prepaidInfo.getTotalTokenKwh() - prepaidInfo.getUsedTokenKwh());
    }

    // 기존 코드 호환용: totalTokenKwh / usedTokenKwh 호출하던 부분을 위해
    @Transient
    public double getTotalTokenKwh() {
        return (prepaidInfo != null) ? prepaidInfo.getTotalTokenKwh() : 0.0;
    }

    @Transient
    public double getUsedTokenKwh() {
        return (prepaidInfo != null) ? prepaidInfo.getUsedTokenKwh() : 0.0;
    }
}
