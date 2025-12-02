package com.lg.regen.entity;

import com.lg.regen.enums.BillStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * 후불(Postpaid) 전력 요금 청구서를 나타내는 JPA 엔티티.
 * - 한 계량기(MeterEntity)에 대해 월별/기간별로 청구서가 여러 개 생길 수 있음.
 */
@Entity
@Table(name = "bills")   // 실제 테이블 이름: bills
@Getter
@Setter

public class BillEntity {
    // 기본 키(Primary Key)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 계량기(전력 계정)의 청구서인지 (N:1 관계)
    // 하나의 MeterEntity에 대해 여러 달의 청구서가 생길 수 있음 → Bill N개 : Meter 1개

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // FK: user_id
    private UserEntity user;

    // 청구 기간 시작일 (예: 2025-12-01)
    @Column(nullable = false)
    private LocalDate periodStart;

    // 청구 기간 종료일 (예: 2025-12-31)
    @Column(nullable = false)
    private LocalDate periodEnd;

    // 해당 기간 동안 사용한 전력량 (kWh)
    @Column(nullable = false)
    private double usageKwh;

    // 이번 청구 금액
    @Column(nullable = false)
    private double amount;

    // 통화 단위 (예: "IDR", "KRW")
    @Column(length = 20)
    private String currency = "IDR";

    // 청구서 상태 (UNPAID / PAID)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BillStatus status = BillStatus.UNPAID;

    // 결제 기한 (예: 매월 5일)
    private LocalDate dueDate;

    // 실제 결제 완료 시각 (결제된 경우에만 값 존재)
    private LocalDateTime paidAt;
}
