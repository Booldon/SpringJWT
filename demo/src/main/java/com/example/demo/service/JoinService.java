package com.example.demo.service;

import com.example.demo.dto.JoinDTO;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void joinProcess(JoinDTO joinDTO) {

        String usernanme = joinDTO.getUsername();
        String password = joinDTO.getPassword();

        Boolean isExist = userRepository.existsByUsername(usernanme);

        if(isExist) {
            System.out.println("이미 존재 하는 유저입니다.");
            return;
        }

        UserEntity data = new UserEntity();

        data.setUsername(usernanme);
        data.setPassword(bCryptPasswordEncoder.encode(password));
        data.setRole("ROLE_NORMAL");

        userRepository.save(data);
    }
}
