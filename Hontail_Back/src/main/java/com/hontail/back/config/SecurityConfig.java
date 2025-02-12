//package com.hontail.back.config;
//
//import com.hontail.back.oauth.CustomOAuth2UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import com.hontail.back.security.JwtProvider;
//import com.hontail.back.security.JwtAuthenticationFilter;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import java.util.Arrays;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    private final CustomOAuth2UserService customOAuth2UserService;
//    private final JwtProvider jwtProvider;
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .csrf(AbstractHttpConfigurer::disable)
//                .sessionManagement(session ->
//                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
//                                .maximumSessions(1)
//                                .maxSessionsPreventsLogin(true)
//                )
//                .authorizeHttpRequests(auth ->
//                        auth
//                                // 완전 공개 경로
//                                .requestMatchers(
//                                        "/",
//                                        "/error",
//                                        "/api/login/**",
//                                        "/api/users/public-profile",
//                                        "/api/home",
//                                        "/api/public-info",
//                                        "/login/**",
//                                        "/oauth2/**",
//                                        "/login/oauth2/code/**",
//                                        "/v3/api-docs/**",
//                                        "/swagger-ui/**",
//                                        "/swagger-resources/**"
//                                ).permitAll()
//                                // 인증 필요 경로
//                                .requestMatchers(
//                                        "/api/cocktail/**",
//                                        "/api/user/**"
//                                ).authenticated()
//                                // 기타 요청은 선택적 접근
//                                .anyRequest().permitAll()
//                )
//                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider),
//                        UsernamePasswordAuthenticationFilter.class)
//                .oauth2Login(oauth2 ->
//                        oauth2
//                                .loginPage("/api/login/")
//                                .userInfoEndpoint(userInfo ->
//                                        userInfo.userService(customOAuth2UserService)
//                                )
//                                .defaultSuccessUrl("/api/auth/login-success", true)
//                                .failureUrl("/api/login/providers?error=true")
//                )
//                .logout(logout ->
//                        logout
//                                .logoutSuccessUrl("/api/home")
//                                .invalidateHttpSession(true)
//                                .clearAuthentication(true)
//                                .deleteCookies("JSESSIONID")
//                );
//
//        return http.build();
//    }
//
//    // CORS 설정
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList(
//                "http://localhost:8080",   // Spring Boot 개발 서버
//                "https://yourproductiondomain.com"  // 프로덕션 도메인
//        ));
//        configuration.setAllowedMethods(Arrays.asList(
//                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
//        ));
//        configuration.setAllowedHeaders(Arrays.asList(
//                "authorization",
//                "content-type",
//                "x-auth-token"
//        ));
//        configuration.setExposedHeaders(Arrays.asList(
//                "x-auth-token"
//        ));
//        configuration.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//}

package com.hontail.back.config;

import com.hontail.back.oauth.CustomOAuth2UserService;
import com.hontail.back.oauth.OAuth2SuccessHandler;
import com.hontail.back.security.JwtAuthenticationFilter;
import com.hontail.back.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtProvider jwtProvider;
    private final OAuth2SuccessHandler oauth2SuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS, CSRF, 세션 관리 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                                .maximumSessions(1)
                                .maxSessionsPreventsLogin(true)
                )
                // URL 접근 권한 설정
                .authorizeHttpRequests(auth ->
                        auth
                                // 완전 공개 경로
                                .requestMatchers(
                                        "/",
                                        "/error",
                                        "/api/login/**",
                                        "/api/users/public-profile",
                                        "/api/home",
                                        "/api/public-info",
                                        "/login/**",
                                        "/oauth2/**",
                                        "/login/oauth2/code/**",
                                        "/v3/api-docs/**",
                                        "/swagger-ui/**",
                                        "/swagger-resources/**"
                                ).permitAll()
                                // 인증 필요 경로
                                .requestMatchers(
                                        "/api/cocktail/**",
                                        "/api/user/**"
                                ).authenticated()
                                // 그 외 모든 요청 허용
                                .anyRequest().permitAll()
                )
                // JWT 인증 필터 추가 (UsernamePasswordAuthenticationFilter 전에 실행)
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/api/login/")  // 커스텀 로그인 페이지 (없으면 기본 제공 페이지 사용)
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(customOAuth2UserService)
                        )
                        // OAuth2 로그인 성공 시 커스텀 SuccessHandler 호출
                        .successHandler(oauth2SuccessHandler)
                        // 로그인 실패 시 이동할 URL
                        .failureUrl("/api/login/providers?error=true")
                )
                // 로그아웃 설정
                .logout(logout ->
                        logout
                                .logoutSuccessUrl("/api/home")
                                .invalidateHttpSession(true)
                                .clearAuthentication(true)
                                .deleteCookies("JSESSIONID")
                );

        return http.build();
    }

    // CORS 설정 (원하는 도메인으로 수정)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:8080",             // 개발 서버
                "https://yourproductiondomain.com"   // 프로덕션 도메인 (필요 시 수정)
        ));
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        configuration.setAllowedHeaders(Arrays.asList(
                "authorization",
                "content-type",
                "x-auth-token"
        ));
        configuration.setExposedHeaders(Arrays.asList("x-auth-token"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
