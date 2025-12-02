package com.lg.regen.controller;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.lg.regen.dto.LoginDTO;
import com.lg.regen.entity.UserEntity;
import com.lg.regen.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public UserEntity login(@RequestBody LoginDTO loginDTO) {
        // 1. 아이디만으로 검색: 아이디가 DB에 있는지 확인합니다.
        Optional<UserEntity> userCheck = userRepository.findByEmail(loginDTO.getEmail());

        if (userCheck.isEmpty()) {
            System.out.println("❌ [인증 실패] 아이디(ID)가 DB에 존재하지 않습니다. (최초 불일치)");
            return null; // 아이디가 없으면 즉시 실패
        }

        // 1. DTO에 회원 찾기
        Optional<UserEntity> user = userRepository.findByEmailAndPassword(
                loginDTO.getEmail(),
                loginDTO.getPassword()
        );

        // 2. 결과가 있으면 정보 건네주고 없으면 null(빈값) 주기
        if (user.isPresent()) {
            System.out.println("로그인 성공: " + user.get().getName() + "님, 환영합니다.");
            return user.get();
        } else {
            System.out.println("로그인 실패: 아이디 혹은 비밀번호가 틀렸습니다");
            return null;
        }
    }
}
