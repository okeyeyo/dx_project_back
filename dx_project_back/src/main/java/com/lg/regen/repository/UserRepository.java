package com.lg.regen.repository;

import com.lg.regen.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // 🔑 1. 아이디만 찾는 기능: LOWER()와 TRIM() 적용
    @Query("SELECT u FROM UserEntity u WHERE LOWER(TRIM(u.email)) = LOWER(:email)")
    Optional<UserEntity> findByEmail(@Param("email") String email);

    // 🔑 2. 비밀번호 포함 로그인 검증: LOWER()와 TRIM() 적용
    @Query("SELECT u FROM UserEntity u WHERE LOWER(TRIM(u.email)) = LOWER(:email) AND TRIM(u.password) = :password")
    Optional<UserEntity> findByEmailAndPassword(@Param("email") String email, @Param("password") String password);
}