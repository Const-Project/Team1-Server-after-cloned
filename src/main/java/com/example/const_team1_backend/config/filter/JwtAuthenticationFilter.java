package com.example.const_team1_backend.config.filter;

import com.example.const_team1_backend.common.exception.InvalidJwtTokenException;
import com.example.const_team1_backend.common.exception.TokenExpiredException;
import com.example.const_team1_backend.config.provider.JwtTokenProvider;
import com.example.const_team1_backend.member.MemberToken;
import com.example.const_team1_backend.member.MemberTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberTokenRepository memberTokenRepository;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        logger.debug("[Request] {} {}", method, requestURI);

        // OPTIONS 요청 처리
        if ("OPTIONS".equalsIgnoreCase(method)) {
            logger.debug("[Preflight] Allowing OPTIONS request");
            chain.doFilter(request, response);
            return;
        }

        // 공개 엔드포인트 체크
        if (isPublicEndpoint(requestURI, method)) {
            logger.debug("[Public Endpoint] Allowing access to {}", requestURI);
            chain.doFilter(request, response);
            return;
        }

        // JWT 검증 시작
        try {
            String token = resolveToken(httpRequest);
            logger.debug("[Token Resolution] Extracted token: {}", (token != null ? token.substring(0, 10) + "..." : "null"));

            if (token != null && jwtTokenProvider.validateToken(token)) {
                if (!isValidTokenInDatabase(token)) {
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or logged out token");
                    return;
                }

                Authentication auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
                logger.info("[Authentication Success] User: {}", auth.getName());
            } else {
                logger.warn("[Authentication Failed] No valid token provided");
            }

            chain.doFilter(request, response);
        }
        catch (OptimisticLockingFailureException e) {
            logger.error("[Concurrency Error] {}", e.getMessage());
            sendErrorResponse(response, HttpStatus.CONFLICT.value(), "Another user is modifying the resource.");
        } catch (TokenExpiredException e) {
            logger.error("[Token Error] Expired token: {}", e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token has expired.");
        } catch (InvalidJwtTokenException e) {
            logger.error("[Token Error] Invalid token: {}", e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token.");
        } catch (Exception e) {
            logger.error("[System Error] Unexpected error: {}", e.getMessage(), e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error: " + e.getMessage());
        }
    }

    private boolean isPublicEndpoint(String requestURI, String method) {
        return requestURI.equals("/v1/members/join") ||
                requestURI.equals("/v1/members/login") ||
                requestURI.startsWith("/v3/api-docs") ||
                requestURI.startsWith("/swagger-ui") ||
                requestURI.startsWith("/swagger-resources") ||
                ("GET".equals(method) && (
                        requestURI.startsWith("/v1/buildings") ||
                                requestURI.startsWith("/v1/facilities") ||
                                requestURI.startsWith("/v1/category") ||
                                requestURI.startsWith("/v1/floor") ||
                                requestURI.startsWith("/v1/reviews/") ||
                                requestURI.startsWith("/v1/reactions")) ||
                        requestURI.startsWith("/v1/operatinghours"));
    }

    private void sendErrorResponse(ServletResponse response, int status, String message) throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setStatus(status);
        httpResponse.setContentType("application/json");
        httpResponse.getWriter().write("{\"error\": \"" + message + "\"}");
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean isValidTokenInDatabase(String token) {
        // access token으로 DB 조회
        Optional<MemberToken> memberToken = memberTokenRepository.findByAccessToken(token);
        return memberToken.isPresent();
    }
}
