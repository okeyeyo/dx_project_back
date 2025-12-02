package com.lg.regen.dto;

import com.lg.regen.enums.BillStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BillSummaryDTO {
    private Long billId;
    private int year;
    private int month;         // 8, 9, 10 ...
    private double amount;
    private BillStatus status; // PAID / UNPAID
}

/*
* Flutter 후불 화면의 이 부분이랑 1:1로 대응됨:
* 2024년 10월 | 완납 | Rp 232,000
* */