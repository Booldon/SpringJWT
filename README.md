Sping Security - JWT

JWT 발급 시 Refresh Token도 발급 함

JWT주기는 15초, Refresh Token은 24시간

Refresh Token은 Local의 Mysql에 저장하여 관리하도록 하였으며, JWT 재발급시 Refresh Token도 재발급하는 RTR 방식으로 구현함

-- backend(demo) --

로그인 : /login에 post로 접근시에 로그인 로직이 동작하며 Servlet에서 동작하는 방식이 아닌 Spring Security Filter의 UUsernamePasswordAuthenticationFilterf를 extend하여 로그인으로 동작 구현
기능 : Database에 등록된 사용자가 맞다면 Role에 따른 JWT및 refreshToken 발급 JWT는 로컬 스토리지, refreshToken은 로컬 서버의 Mysql에 저장

로그아웃 : /logout에 get으로 접근 시에 동작하여 마찬가지로 Spring Security Filter의 LogoutFilter - LogoutHandler를 동해 로그아웃 동작 구현
기능 : 로그아웃 시도한 회원에 해당하는 JWT, refreshToken, cookie 삭제

admin : /admin get 접근, Role을 확인해 접근이 가능한 회원인지 검증 ROLE_ADMIN이면 통과 @EnableWebSecurity 설정 값을 이용해 인가 이용자 분리
기능 : Role에 따른 인가 작업 JWT가 만료되었을 시에는 회원과 DB에 저장되어있는 refreshToken을 검증하여 JWT와 refreshToken 재발급 검증은 randomkey를 생성하여 하도록 구현함

-- frontend(node-demo) --

Express.js을 활용해 간단하게 구현함
username, password 기입 후 login -> /login post 접근, 로컬 스토리지에 응답으로 받아온 JWT 저장
admin -> /admin get접근, 만약 토큰을 응답으로 받았다면, 로컬 스토리지에 저장
logout -> /logout get접근, 로컬 스토리지 삭제

///////////////////////
cd node-demo/myapp
node server.js
