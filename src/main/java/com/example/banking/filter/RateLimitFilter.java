package com.example.banking.filter;

import com.example.banking.config.RateLimitConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {
    private final RateLimitConfig rateLimitConfig;
    private final ObjectMapper objectMapper;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String ip = getClientIp(request);
        String path = request.getRequestURI();

        Bucket bucket = resolveBucket(ip, path);

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining",
                    String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            long waitSeconds = probe.getNanosToWaitForRefill() / 1_000_000_000;

            log.warn("Rate limit exceeded for IP: {} on path: {}", ip, path);

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.addHeader("X-Rate-Limit-Retry-After-Seconds",
                    String.valueOf(waitSeconds));

            Map<String, Object> error = new HashMap<>();
            error.put("status", 429);
            error.put("message", "Too many requests. Please try again in "
                    + waitSeconds + " seconds.");
            error.put("timestamp", LocalDateTime.now().toString());

            response.getWriter().write(
                    objectMapper.writeValueAsString(error));
        }
    }

    private Bucket resolveBucket(String ip, String path) {
        if (path.startsWith("/api/auth")) {
            return rateLimitConfig.resolveAuthBucket(ip);
        } else if (path.startsWith("/api/transactions")) {
            return rateLimitConfig.resolveTransactionBucket(ip);
        } else if (path.startsWith("/api/admin")) {
            return rateLimitConfig.resolveAdminBucket(ip);
        } else {
            return rateLimitConfig.resolveGeneralBucket(ip);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs");
    }
}
