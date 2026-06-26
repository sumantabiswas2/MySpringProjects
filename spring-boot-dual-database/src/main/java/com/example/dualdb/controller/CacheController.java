package com.example.dualdb.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Cache", description = "Cache management and monitoring operations")
public class CacheController {

    private final CacheManager cacheManager;

    @GetMapping("/stats")
    @Operation( summary = "Get cache statistics", description = "Returns hit/miss rates, eviction counts, and size for all Caffeine caches")
    @ApiResponse(responseCode = "200", description = "Cache statistics retrieved successfully")
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
    
    
    

    @DeleteMapping("/clear/{cacheName}")
    @Operation( summary = "Clear a specific cache", description = "Removes all entries from the named cache")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cache cleared successfully"),
            @ApiResponse(responseCode = "400", description = "Cache not found")
    })
    public ResponseEntity<String> clearCache(
            @Parameter(description = "Name of the cache to clear", required = true) @PathVariable String cacheName) {
        org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            return ResponseEntity.ok("Cache '" + cacheName + "' cleared successfully");
        }
        return ResponseEntity.badRequest().body("Cache '" + cacheName + "' not found");
    }
    
    
    
    

    @DeleteMapping("/clear/all")
    @Operation(summary = "Clear all caches", description = "Removes all entries from every registered cache")
    @ApiResponse(responseCode = "200", description = "All caches cleared successfully")
    public ResponseEntity<String> clearAllCaches() {
        for (String cacheName : cacheManager.getCacheNames()) {
            org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        }
        return ResponseEntity.ok("All caches cleared successfully");
    }
    
    
    

    @GetMapping("/names")
    @Operation(summary = "Get all cache names", description = "Returns the list of all registered cache names and the total count")
    @ApiResponse(responseCode = "200", description = "Cache names retrieved successfully")
    public ResponseEntity<Map<String, Object>> getCacheNames() {
        Map<String, Object> response = new HashMap<>();
        response.put("cacheNames", cacheManager.getCacheNames());
        response.put("total", cacheManager.getCacheNames().size());
        return ResponseEntity.ok(response);
    }
    
    
    
    

    @GetMapping("/exists/{cacheName}/{key}")
    @Operation(summary = "Check if cache key exists", description = "Returns whether a specific key is present in the given cache")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Key existence check completed"),
            @ApiResponse(responseCode = "400", description = "Cache not found")
    })
    public ResponseEntity<Boolean> keyExists(
            @Parameter(description = "Cache name", required = true) @PathVariable String cacheName,
            @Parameter(description = "Cache key", required = true) @PathVariable String key) {
        org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            return ResponseEntity.ok(cache.get(key) != null);
        }
        return ResponseEntity.badRequest().body(false);
    }
    
    
    
    

    @DeleteMapping("/evict/{cacheName}/{key}")
    @Operation(summary = "Evict a cache entry", description = "Removes a specific key from the named cache")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Key evicted successfully"),
            @ApiResponse(responseCode = "400", description = "Cache not found")
    })
    public ResponseEntity<String> evictKey(
            @Parameter(description = "Cache name", required = true) @PathVariable String cacheName,
            @Parameter(description = "Cache key to evict", required = true) @PathVariable String key) {
        org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
            return ResponseEntity.ok("Key '" + key + "' evicted from cache '" + cacheName + "'");
        }
        return ResponseEntity.badRequest().body("Cache '" + cacheName + "' not found");
    }
}
