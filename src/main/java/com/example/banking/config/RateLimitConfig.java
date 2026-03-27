package com.example.banking.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitConfig {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket resolveAuthBucket(String ip) {
        return buckets.computeIfAbsent(
                "auth:" + ip, k -> createBucket(10, Duration.ofMinutes(1)));
    }

    public Bucket resolveTransactionBucket(String ip) {
        return buckets.computeIfAbsent(
                "txn:" + ip, k -> createBucket(20, Duration.ofMinutes(1)));
    }

    public Bucket resolveGeneralBucket(String ip) {
        return buckets.computeIfAbsent(
                "general:" + ip, k -> createBucket(60, Duration.ofMinutes(1)));
    }

    public Bucket resolveAdminBucket(String ip) {
        return buckets.computeIfAbsent(
                "admin:" + ip, k -> createBucket(30, Duration.ofMinutes(1)));
    }

    private Bucket createBucket(int capacity, Duration duration) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillGreedy(capacity, duration)
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}