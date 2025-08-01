package com.example.rest.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.example.common.model.CalculationResponse;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class CalculationResponseConsumer {

    private final ConcurrentHashMap<String, CalculationResponse> responseCache = new ConcurrentHashMap<>();

    @KafkaListener(topics = "calculation-response-topic", groupId = "calculation-response-group")
    public void consume(ConsumerRecord<String, CalculationResponse> record) {
        responseCache.put(record.key(), record.value());
    }

    public CalculationResponse getResponse(String requestId) {
        return responseCache.get(requestId);
    }
}
