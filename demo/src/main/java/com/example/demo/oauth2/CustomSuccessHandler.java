package com.example.demo.oauth2;

import com.example.demo.dto.CustomOAuth2User;
import com.example.demo.jwt.JWTUtil;
import com.example.demo.service.RefreshTokenService;
import com.example.demo.util.SecureRandomStringGenerator;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public CustomSuccessHandler(JWTUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //OAuth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String username = customUserDetails.getUsername();
        System.out.println("++authentication.authentication++" + authentication);
        System.out.println("++authentication.username++" + username);


        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        System.out.println("++authentication.getAuthorities()++" + authentication.getAuthorities());
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        String randomkey = SecureRandomStringGenerator.generateRandomString(32);
        System.out.println("refreshTokenService.isExist(username) : " + refreshTokenService.isExist(username));

        //refreshToken 생성
        refreshTokenService.createRefreshToken(username, role, randomkey);

        String token = jwtUtil.createJwt(username, role, randomkey, 60*60*60*60L);

        response.addCookie(createCookie("Authorization", token));
        response.sendRedirect("http://localhost:3000/");
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*60*60);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
