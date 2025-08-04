package com.example.rest.service;

import com.example.common.model.CalculationRequest;
import com.example.common.model.CalculationResponse;
import com.example.common.model.Operation;
import com.example.rest.kafka.CalculationResponseConsumer;

import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class OperationService {

    private final KafkaTemplate<String, CalculationRequest> calculationRequestKafkaTemplate;

    private final CalculationResponseConsumer responseConsumer;

    public OperationService(KafkaTemplate<String, CalculationRequest> calculationRequestKafkaTemplate,
                            CalculationResponseConsumer responseConsumer) {
        this.calculationRequestKafkaTemplate = calculationRequestKafkaTemplate;
        this.responseConsumer = responseConsumer;
    }

    public ResponseEntity<BigDecimal> handleOperation(Operation operation, BigDecimal a, BigDecimal b) {
        System.out.println("Received " + operation + " request with operands: " + a + " and " + b);
        if(operation == null || a == null || b == null) {
            return ResponseEntity.badRequest().body(null);
        }


        CalculationRequest request = new CalculationRequest(operation, a, b);

        CompletableFuture<CalculationResponse> futureResponse = responseConsumer.getFuture(request.getIdRequest());
        calculationRequestKafkaTemplate.send("calculation-topic", request.getIdRequest(), request);

        try {
            CalculationResponse response = futureResponse.get(5, TimeUnit.SECONDS);
            System.out.println("Received response: " + response.getResult());
            return ResponseEntity.ok(response.getResult());
        } catch (Exception e) {
            System.err.println("Error waiting for response: " + e.getMessage());
            return ResponseEntity.status(504).body(null);
        }
    }

}