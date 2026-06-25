package com.example.dualdb.config;


import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Primary Cache Manager using Caffeine (in-memory)
     */
    @Primary
    @Bean(name = "caffeineCacheManager")
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(500)                    // Maximum number of entries
                .expireAfterWrite(10, TimeUnit.MINUTES)  // Expire after 10 minutes
                .expireAfterAccess(5, TimeUnit.MINUTES)  // Expire after 5 minutes of no access
                .recordStats()                       // Enable statistics
                .weakKeys()                          // Use weak references for keys
                .weakValues()                        // Use weak references for values
        );
        
        // Cache names to pre-initialize
        cacheManager.setCacheNames(Set.of(
                "customers", "customerById", "orders", "products", 
                "productByCategory", "customerOrders", "topCustomers",
                "customerRecommendations", "neojCustomer", "neojProducts"
        ));
        
        return cacheManager;
    }

    /**
     * Secondary Cache Manager for short-lived caches (e.g., session data)
     */
    @Bean(name = "shortLivedCacheManager")
    public CacheManager shortLivedCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(200)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .expireAfterAccess(30, TimeUnit.SECONDS)
                .recordStats()
        );
        return cacheManager;
    }

    /**
     * Cache Manager for long-lived reference data (e.g., categories, static data)
     */
    @Bean(name = "longLivedCacheManager")
    public CacheManager longLivedCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(60, TimeUnit.MINUTES)  // 1 hour
                .expireAfterAccess(30, TimeUnit.MINUTES)
                .recordStats()
        );
        return cacheManager;
    }
}