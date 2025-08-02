package com.example.rest.config;
import com.example.common.model.CalculationResponse;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineConfig {
        @Bean
        public Cache<String, CalculationResponse> calculationResponseCache() {
            return Caffeine.newBuilder()
                    .expireAfterWrite(100, TimeUnit.SECONDS)
                    .maximumSize(10_000)
                    .build();
        }
}
