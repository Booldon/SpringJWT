package com.example.demo.config;

import com.example.demo.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

public class CustomLogoutHandler implements LogoutHandler {

    private final RefreshTokenService refreshTokenService;

    public CustomLogoutHandler(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        System.out.println(authentication);
        if(authentication != null) {
            String username = authentication.getName();
            System.out.println(username);
            refreshTokenService.deleteRefreshToken(username);
            System.out.println("deleteRefreshToken Finish");
        }
        System.out.println("XXX logoutHandler XXX");
    }
}
