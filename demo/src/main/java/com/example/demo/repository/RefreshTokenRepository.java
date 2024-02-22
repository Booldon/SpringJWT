package com.example.demo.repository;

import com.example.demo.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    //username을 받아 해당 Refresh 토큰을 찾는다.
    @Override
    RefreshToken getReferenceById(String string);
}
