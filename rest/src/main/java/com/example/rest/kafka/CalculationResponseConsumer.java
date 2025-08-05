package com.example.rest.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.example.common.model.CalculationResponse;
import com.github.benmanes.caffeine.cache.Cache;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CalculationResponseConsumer {

    private final Cache<String, CalculationResponse> responseCache;

    private final ConcurrentHashMap<String, CompletableFuture<CalculationResponse>> responseFutures = new ConcurrentHashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(CalculationResponseConsumer.class);

    public CalculationResponseConsumer(Cache<String, CalculationResponse> responseCache) {
        this.responseCache = responseCache;
    }

    @KafkaListener(topics = "calculation-response-topic", groupId = "calculation-response-group")
    public void consume(ConsumerRecord<String, CalculationResponse> record) {

        logger.info("Received calculation response for request ID: {}", record.key());
        String requestId = record.key();
        CalculationResponse response = record.value();

        responseCache.put(requestId, response);

        CompletableFuture<CalculationResponse> future = responseFutures.get(requestId);
        if (future != null) {
            logger.info("Completing future for request ID: {}", requestId);
            future.complete(response);
            responseFutures.remove(requestId);
        }
    }

    public CalculationResponse getResponse(String requestId) {
        return responseCache.getIfPresent(requestId);
    }

    public CompletableFuture<CalculationResponse> getFuture(String requestId) {
        return responseFutures.computeIfAbsent(requestId, id -> new CompletableFuture<>());
    }


}
