package com.example.rest.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.example.common.model.CalculationResponse;
import com.github.benmanes.caffeine.cache.Cache;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class CalculationResponseConsumer {

    private final Cache<String, CalculationResponse> responseCache;

    private final ConcurrentHashMap<String, CompletableFuture<CalculationResponse>> responseFutures = new ConcurrentHashMap<>();

    public CalculationResponseConsumer(Cache<String, CalculationResponse> responseCache) {
        this.responseCache = responseCache;
    }

    @KafkaListener(topics = "calculation-response-topic", groupId = "calculation-response-group")
    public void consume(ConsumerRecord<String, CalculationResponse> record) {
        String requestId = record.key();
        CalculationResponse response = record.value();

        responseCache.put(requestId, response);

        CompletableFuture<CalculationResponse> future = responseFutures.get(requestId);
        if (future != null) {
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
