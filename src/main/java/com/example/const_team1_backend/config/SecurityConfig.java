package com.example.const_team1_backend.config;

import com.example.const_team1_backend.config.filter.JwtAuthenticationFilter;
import com.example.const_team1_backend.config.provider.JwtTokenProvider;
import com.example.const_team1_backend.config.service.CustomUserDetailsService;
import com.example.const_team1_backend.member.MemberTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    @Autowired
    private MemberTokenRepository memberTokenRepository;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .requiresChannel(channel -> {
                    channel.anyRequest().requiresSecure();
                })
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                            // Public endpoints
                            .requestMatchers(HttpMethod.POST, "/v1/members/join", "/v1/members/login").permitAll()
                            .requestMatchers(HttpMethod.GET,
                                    "/v1/buildings/**",
                                    "/favicon.ico",
                                    "/v1/facilities/**",
                                    "/v1/category/**",
                                    "/v1/reviews/**",
                                    "/v1/reactions/**",
                                    "/v1/reviewlikes/**",
                                    "/v1/floor/**","/v3/api-docs/**",           // 추가
                                    "/swagger-ui/**",            // 추가
                                    "/swagger-ui.html",          // 추가
                                    "/swagger-resources/**",
                                    "/v1/operatinghours/**").permitAll()

                            // Protected endpoints
                            .requestMatchers(HttpMethod.POST,
                                    "/v1/facilities/**",
                                    "/v1/reviewlikes/**",
                                    "/v1/reviews/**",
                                    "/v1/reactions/**").authenticated()
                            .requestMatchers(HttpMethod.DELETE,
                                    "/v1/facilities/**",
                                    "/v1/reviewlikes/**",
                                    "/v1/reviews/**",
                                    "/v1/reactions/**").authenticated()
                            .anyRequest().authenticated();
                })
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider,memberTokenRepository),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // SecurityConfig.java 수정
        configuration.setAllowedOriginPatterns(List.of(
                "*" // 임시로 모든 origin 허용 (테스트 후 제한)
        ));

        // 2. 허용할 메서드 설정
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));


        configuration.setAllowedHeaders(Arrays.asList("*")); // 모든 헤더 허용
        configuration.setExposedHeaders(Arrays.asList("*")); // 모든 헤더 노출

        // 5. 인증 허용 설정 (쿠키/인증헤더 사용 시 필수)
        configuration.setAllowCredentials(true);  // 주석 해제!

        // 6. Preflight 캐시 시간 설정
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http,
            PasswordEncoder passwordEncoder
    ) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
        return builder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}