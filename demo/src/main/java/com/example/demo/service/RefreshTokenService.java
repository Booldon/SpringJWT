package com.example.demo.service;

import com.example.demo.entity.RefreshToken;
import com.example.demo.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void createRefreshToken(String username, String role, String randomkey) {

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsername(username);
        refreshToken.setRole(role);
        refreshToken.setLimitTime();
        refreshToken.setRandomKey(randomkey);

        refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken findRefreshToken(String username) {
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findById(username);
        return refreshTokenOptional.orElse(null);
    }

    public Boolean isExist(String username) { // 존재하는가?
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findById(username);
        return refreshToken.isPresent();
    }

    public void deleteRefreshToken(String username) {
        // findById를 사용하여 Optional<RefreshToken> 반환
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findById(username);
        // 존재하는 경우, 해당 RefreshToken 삭제
        refreshTokenOptional.ifPresent(refreshToken -> refreshTokenRepository.delete(refreshToken));
    }

}
