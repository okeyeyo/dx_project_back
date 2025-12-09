package com.lg.regen.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "usage_logs",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_usage_logs_user_date",
                        columnNames = {"user_id", "date"}
                )
        }
)
@Getter
@Setter
public class UsageLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 유저의 사용 기록인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // 사용일 (2025-12-05)
    @Column(nullable = false)
    private LocalDate date;

    // 하루 사용량 (kWh)
    @Column(nullable = false)
    private double usageKwh;

    // 생성 시간
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 수정 시간(선택, 있으면 디버깅에 도움)
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
