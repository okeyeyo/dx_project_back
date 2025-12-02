package com.lg.regen.repository;

import com.lg.regen.entity.BillEntity;
import com.lg.regen.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<BillEntity, Long> {

    // 이 유저의 가장 최신 청구서 1개
    Optional<BillEntity> findTop1ByUserOrderByPeriodEndDesc(UserEntity user);

    // 이 유저의 최근 청구서 3개
    List<BillEntity> findTop3ByUserOrderByPeriodEndDesc(UserEntity user);
}