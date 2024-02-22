package com.example.demo.config;

import com.example.demo.entity.RefreshToken;
import com.example.demo.jwt.JWTFilter;
import com.example.demo.jwt.JWTUtil;
import com.example.demo.jwt.LoginFilter;
import com.example.demo.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;

    private final RefreshTokenService refreshTokenService;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil, RefreshTokenService refreshTokenService) {

        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //cors 설정
        http
                .cors((cors) -> cors
                        .configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                CorsConfiguration configuration = new CorsConfiguration();

                                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                                configuration.setAllowedMethods(Collections.singletonList("*"));
                                configuration.setAllowCredentials(true);
                                configuration.setAllowedHeaders(Collections.singletonList("*"));
                                configuration.setMaxAge(3600L);

                                configuration.setExposedHeaders(Collections.singletonList("Authorization"));
                                return configuration;
                            }
                        }));

        //csrf disable
        http
                .csrf((auth) -> auth.disable());

        //Form Login 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        //http basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login", "/", "/join").permitAll() // 모두 인가
                        .requestMatchers("/admin").hasRole("ADMIN") // admin 인가
                        .anyRequest().authenticated()); // 로그인한 사용자만 가능

        //필터 추가(addFilterAt: 해당 필터 자리를 원하는 필터로 대체, addFilterBefore: 특정 필터 앞에 필터 생성)
        //필터 위치 설정 : 세션이 stateless 상태이기 때문에 한 개의 인증 요청이 완료 되면 securityContext 값은 초기화 된다.
        //따라서 로그아웃 시에도 클라이언트가 가지고있는 JWT를 검증하고, 통과 된다면 securityContext에 값을 다시 저장한 후 인증 로직 실행
        http
                .addFilterBefore(new JWTFilter(jwtUtil,refreshTokenService), LogoutFilter.class);

        //로그아웃 설정
        http
                .logout((logout) -> logout
                        .logoutSuccessUrl("/")
                        .addLogoutHandler(new CustomLogoutHandler(refreshTokenService))
                        .deleteCookies("JSESSIONID"));


        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshTokenService), UsernamePasswordAuthenticationFilter.class);


        //세션 설정
        //JWT를 통한 인증/인가를 위해선 세션을 STATELESS로 설정하는것이 중요함
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        return http.build();

    }
}
