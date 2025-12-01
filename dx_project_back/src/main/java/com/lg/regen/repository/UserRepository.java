package com.lg.regen.repository;

import com.lg.regen.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    // 아이디(email)와 비밀번호(password)가 둘 다 맞는 사람 찾기
    Optional<UserEntity> findByEmailAndPassword(String email, String password);
}
