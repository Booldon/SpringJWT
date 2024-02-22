package com.example.demo.jwt;

import com.example.demo.dto.CustomUserDetails;
import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.UserEntity;
import com.example.demo.service.RefreshTokenService;
import com.example.demo.util.SecureRandomStringGenerator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//발급받은 JWT필터 검증
//OncePerRequestFilter : 한 번 만 검증함
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    private final RefreshTokenService refreshTokenService;

    public JWTFilter(JWTUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //request에서 Authorization 헤더를 찾음
        String authorization = request.getHeader("Authorization");


        //Authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {

            System.out.println("token null");
            filterChain.doFilter(request, response); //doFilter로 다음 필터로 넘겨줌

            //조건이 해당되면 메소드 종료
            return;
        }

        //token 추출
        String token = authorization.split(" ")[1];

        //토큰에서 username과 role 획득
        String username = jwtUtil.getUsername(token);

        //토큰 소멸 시간 검증
        if (jwtUtil.isExpired(token)) { //true : 토큰 만료

            System.out.println("token expire... searching RefreshToken");

            //RefreshToken 검증
            if(refreshTokenService.isExist(username)) { //RefreshToken이 존재하면
                RefreshToken refreshToken = refreshTokenService.findRefreshToken(username);

                if(refreshToken.getLimitTime().getTime() < System.currentTimeMillis()){ //존재하지만, 만료되었을때
                    System.out.println("RefreshToken is expired");

                    filterChain.doFilter(request, response);
                    //조건이 해당 되면 메소드 종료
                    return;
                }

                if(!refreshToken.getRandomKey().equals(jwtUtil.getRandomKey(token))){ //randomkey matching
                    System.out.println("randomkey didn't matching");

                    filterChain.doFilter(request, response);
                    //조건이 해당 되면 메소드 종료
                    return;
                }


                System.out.println("create JWT");

                String randomkey = SecureRandomStringGenerator.generateRandomString(32);

                //JWT 생성
                String newToken = jwtUtil.createJwt(refreshToken.getUsername(), refreshToken.getRole(), randomkey,15*1000L);

                //refreshToken 재생성
                refreshTokenService.createRefreshToken(refreshToken.getUsername(),refreshToken.getRole(),randomkey);

                //헤더 추가
                response.addHeader("Authorization", "Bearer " + newToken);

                //userEntity를 생성하여 값 set
                UserEntity userEntity = new UserEntity();
                userEntity.setUsername(refreshToken.getUsername());
                userEntity.setPassword("temppassword"); //임의의 값 지정
                userEntity.setRole(refreshToken.getRole());

                //UserDetails에 회원 정보 객체에 담기
                CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

                //스프링 시큐리티 인증 토큰 생성
                Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

                //세션에 사용자 등록
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("Base RefreshToken");

                filterChain.doFilter(request, response);

                return;
            }

            else { //RefreshToken이 존재하지 않으면
                System.out.println("null RefreshToken");
                filterChain.doFilter(request, response);

                return;
            }

        }

        String role = jwtUtil.getRole(token);

        //userEntity를 생성하여 값 set
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword("temppassword"); //임의의 값 지정
        userEntity.setRole(role);

        //UserDetails에 회원 정보 객체에 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);
        System.out.println("Base LoginInfo");

        filterChain.doFilter(request, response);

    }
}
