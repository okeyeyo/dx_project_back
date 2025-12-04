// src/main/java/com/lg/regen/entity/PrepaidMeterEntity.java
package com.lg.regen.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "prepaid_meters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrepaidMeterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 계량기(MeterEntity)에 대한 선불 정보인지
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meter_id", nullable = false, unique = true)
    private MeterEntity meter;

    // 지금까지 충전한 총 토큰(kWh)
    @Builder.Default
    @Column(name = "total_token_kwh", nullable = false)
    private double totalTokenKwh = 0.0;

    // 지금까지 사용한 토큰(kWh) - 나중에 실제 사용량 연동
    @Builder.Default
    @Column(name = "used_token_kwh", nullable = false)
    private double usedTokenKwh = 0.0;

    // 마지막 충전 시각
    @Column(name = "last_topup_at")
    private LocalDateTime lastTopupAt;
}
