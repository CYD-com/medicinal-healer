package com.example.usergenerator.config;

import com.example.usergenerator.common.Result;
import com.example.usergenerator.util.JwtUtil;
import com.example.usergenerator.util.TokenBlacklistUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final TokenBlacklistUtil tokenBlacklistUtil;
    private final ObjectMapper objectMapper;

    private static final List<String> PUBLIC_URLS = Arrays.asList(
            "/api/user/login",
            "/api/user/login/form",
            "/api/user/register",
            "/api/user/generate",
            "/api/captcha"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (isPublicUrl(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractToken(request);

        if (token == null || token.isEmpty()) {
            writeError(response, 401, "未登录，请先登录");
            return;
        }

        if (tokenBlacklistUtil.isBlacklisted(token)) {
            writeError(response, 401, "Token已失效，请重新登录");
            return;
        }

        if (!jwtUtil.validateToken(token)) {
            writeError(response, 401, "Token无效或已过期，请重新登录");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicUrl(String path) {
        return PUBLIC_URLS.stream().anyMatch(path::startsWith);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return request.getHeader("X-Token");
    }

    private void writeError(HttpServletResponse response, int code, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        Result<Void> result = Result.error(code, message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
