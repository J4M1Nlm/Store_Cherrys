package com.cherrytwins.shop.common.util;

import com.cherrytwins.shop.common.exception.BadRequestException;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimiterService {

    // key -> timestamp placeholder
    private final Cache<String, Long> cache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(1))
            .maximumSize(50_000)
            .build();

    /**
     * Permite 1 acción por minuto por key.
     * Si se excede, NO falles con error si es forgot password (mejor responder 204 silencioso).
     */
    public boolean tryAcquire(String key) {
        Long existing = cache.getIfPresent(key);
        if (existing != null) return false;
        cache.put(key, System.currentTimeMillis());
        return true;
    }
}
