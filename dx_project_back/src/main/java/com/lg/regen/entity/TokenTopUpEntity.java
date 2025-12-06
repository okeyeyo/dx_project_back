package com.lg.regen.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "token_top_up")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenTopUpEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 몇 kWh 충전했는지
    @Column(name = "amount_kwh", nullable = false)
    private double amountKwh;

    // 선택한 브랜드 (ITRON, HEXING, ACTARIS ...)
    @Column(name = "brand", length = 50, nullable = false)
    private String brand;

    // 언제 충전했는지
    @Column(name = "purchased_at", nullable = false)
    private LocalDateTime purchasedAt;

    // 어떤 계량기의 충전 기록인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meter_id", nullable = false)
    private MeterEntity meter;
}
