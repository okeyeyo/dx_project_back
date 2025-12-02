package com.lg.regen.enums;

// 후불(postpaid) 모드에서 한 달 사용 후 청구된 요금의 상태를 관리하는 데 사용
public enum BillStatus {
    PAID,    // 완납 상태 (사용자가 요금을 모두 납부함)
    UNPAID   // 미납 상태 (아직 납부되지 않은 청구서)
}
