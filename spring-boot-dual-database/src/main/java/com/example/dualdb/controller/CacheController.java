package com.example.dualdb.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
public class CacheController {

    private final CacheManager cacheManager;

    /**
     * Get cache statistics for all caches
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Map<String, Object>>> getCacheStats() {
        Map<String, Map<String, Object>> allStats = new HashMap<>();
        
        for (String cacheName : cacheManager.getCacheNames()) {
            org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
            if (cache instanceof CaffeineCache caffeineCache) {
                Cache nativeCache = caffeineCache.getNativeCache();
                CacheStats stats = nativeCache.stats();
                
                Map<String, Object> statsMap = new HashMap<>();
                statsMap.put("hitCount", stats.hitCount());
                statsMap.put("missCount", stats.missCount());
                statsMap.put("hitRate", String.format("%.2f%%", stats.hitRate() * 100));
                statsMap.put("loadSuccessCount", stats.loadSuccessCount());
                statsMap.put("loadFailureCount", stats.loadFailureCount());
                statsMap.put("totalLoadTime", stats.totalLoadTime() + "ms");
                statsMap.put("evictionCount", stats.evictionCount());
                statsMap.put("estimatedSize", nativeCache.estimatedSize());
                
                allStats.put(cacheName, statsMap);
            }
        }
        
        return ResponseEntity.ok(allStats);
    }

    /**
     * Clear a specific cache
     */
    @DeleteMapping("/clear/{cacheName}")
    public ResponseEntity<String> clearCache(@PathVariable String cacheName) {
        org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            return ResponseEntity.ok("Cache '" + cacheName + "' cleared successfully");
        }
        return ResponseEntity.badRequest().body("Cache '" + cacheName + "' not found");
    }

    /**
     * Clear all caches
     */
    @DeleteMapping("/clear/all")
    public ResponseEntity<String> clearAllCaches() {
        for (String cacheName : cacheManager.getCacheNames()) {
            org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        }
        return ResponseEntity.ok("All caches cleared successfully");
    }

    /**
     * Get all cache names
     */
    @GetMapping("/names")
    public ResponseEntity<Map<String, Object>> getCacheNames() {
        Map<String, Object> response = new HashMap<>();
        response.put("cacheNames", cacheManager.getCacheNames());
        response.put("total", cacheManager.getCacheNames().size());
        return ResponseEntity.ok(response);
    }

    /**
     * Check if a key exists in a cache
     */
    @GetMapping("/exists/{cacheName}/{key}")
    public ResponseEntity<Boolean> keyExists(@PathVariable String cacheName, @PathVariable String key) {
        org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            return ResponseEntity.ok(cache.get(key) != null);
        }
        return ResponseEntity.badRequest().body(false);
    }

    /**
     * Evict a specific entry from cache
     */
    @DeleteMapping("/evict/{cacheName}/{key}")
    public ResponseEntity<String> evictKey(@PathVariable String cacheName, @PathVariable String key) {
        org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
            return ResponseEntity.ok("Key '" + key + "' evicted from cache '" + cacheName + "'");
        }
        return ResponseEntity.badRequest().body("Cache '" + cacheName + "' not found");
    }
}