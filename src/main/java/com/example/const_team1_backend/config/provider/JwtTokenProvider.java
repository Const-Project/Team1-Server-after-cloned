package com.example.const_team1_backend.config.provider;

import com.example.const_team1_backend.common.exception.InvalidJwtTokenException;
import com.example.const_team1_backend.common.exception.TokenExpiredException;
import com.example.const_team1_backend.config.service.CustomUserDetailsService;
import com.example.const_team1_backend.member.MemberTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    @Autowired
    private MemberTokenRepository memberTokenRepository;

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-validity}")
    private long accessTokenValidityInMilliseconds;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidityInMilliseconds;

    private final CustomUserDetailsService customUserDetailsService;

    public JwtTokenProvider(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }


    public String createAccessToken(String loginId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenValidityInMilliseconds); // ✅ 수정

        return Jwts.builder()
                .setSubject(loginId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // [수정 2] 변수명 오타 수정 (refreshTokenExpirationInMs → refreshTokenValidityInMilliseconds)
    private String createRefreshToken(String loginId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenValidityInMilliseconds); // ✅ 수정

        return Jwts.builder()
                .setSubject(loginId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // 기존 createToken (로그인 시 사용)
    public Map<String, String> createToken(String loginId) {
        String accessToken = createAccessToken(loginId);
        String refreshToken = createRefreshToken(loginId);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    // [변경 없음] 기존 유효성 검사 로직 유지
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            logger.debug("[토큰 검증] 토큰 유효 - {}", token.substring(0, 10) + "...");
            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            logger.warn("[토큰 검증] 토큰 만료: {}", e.getMessage());
            throw new TokenExpiredException("토큰이 만료되었습니다");
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("[토큰 검증] 잘못된 토큰: {}", e.getMessage());
            throw new InvalidJwtTokenException("올바르지 않은 토큰입니다");
        }
    }

    // [변경 없음] 인증 객체 생성 로직 유지
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        String username = claims.getSubject();
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // [변경 없음] 사용자명 추출 로직 유지
    public String getUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}