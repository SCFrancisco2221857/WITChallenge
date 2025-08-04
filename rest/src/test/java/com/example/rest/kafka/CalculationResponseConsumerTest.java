package com.example.rest.kafka;

import com.example.common.model.CalculationResponse;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;


class CalculationResponseConsumerTest {

    private final Cache<String, CalculationResponse> responseCache= Caffeine.newBuilder().build();
    private final ConcurrentHashMap<String, CompletableFuture<CalculationResponse>> responseFutures = new ConcurrentHashMap<>();

    private CalculationResponseConsumer calculationResponseConsumer;

    @BeforeEach
    void setUp() {
        calculationResponseConsumer = new CalculationResponseConsumer(responseCache);
    }

    @Test
    void testGetFutureCreatesNewFutureIfAbsent() {
        String requestId = "test-request-id";
        CompletableFuture<CalculationResponse> future = calculationResponseConsumer.getFuture(requestId);

        assertNotNull(future, "Future should not be null");
        assertFalse(future.isDone(), "Future should not be completed yet");
    }

    @Test
    void testConsumeStoresResponseAndCompletesFuture() {
        String requestId = "test-request-id";
        CalculationResponse response = new CalculationResponse();
        response.setResult(BigDecimal.valueOf(42));

        CompletableFuture<CalculationResponse> future = calculationResponseConsumer.getFuture(requestId);

        ConsumerRecord<String, CalculationResponse> record = new ConsumerRecord<>("topic", 0, 0L, requestId, response);
        calculationResponseConsumer.consume(record);

        assertEquals(response, calculationResponseConsumer.getResponse(requestId), "Response should match the consumed value");

        assertTrue(future.isDone(), "Future should be completed after consuming");
        assertEquals(response, future.join(), "Future result should match the consumed response");
    }


    @Test
    void testConsumeWitFutureNull(){
        String requestId = "test-request-id";
        CalculationResponse response = new CalculationResponse();
        response.setResult(BigDecimal.valueOf(100));

        ConsumerRecord<String, CalculationResponse> record = new ConsumerRecord<>("topic", 0, 0L, requestId, response);
        calculationResponseConsumer.consume(record);

        assertNotNull(calculationResponseConsumer.getResponse(requestId), "Response should be cached");

        CompletableFuture<CalculationResponse> future = calculationResponseConsumer.getFuture(requestId);
        assertFalse(future.isDone(), "Future should not be completed if didnt exist before consuming");
    }
}