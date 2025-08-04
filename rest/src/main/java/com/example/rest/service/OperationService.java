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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class OperationService {

    private final KafkaTemplate<String, CalculationRequest> calculationRequestKafkaTemplate;

    private final CalculationResponseConsumer responseConsumer;

    private static final Logger logger = LoggerFactory.getLogger(OperationService.class);


    public OperationService(KafkaTemplate<String, CalculationRequest> calculationRequestKafkaTemplate,
                            CalculationResponseConsumer responseConsumer) {
        this.calculationRequestKafkaTemplate = calculationRequestKafkaTemplate;
        this.responseConsumer = responseConsumer;
    }

    public ResponseEntity<BigDecimal> handleOperation(Operation operation, BigDecimal a, BigDecimal b) {
        logger.info("A executar operação");
        logger.info("Received {} request with operands: {} and {}", operation, a, b);
        if (operation == null || a == null || b == null) {
            logger.error("Invalid operation or operands: {} {} {}", operation, a, b);
            return ResponseEntity.badRequest().body(null);
        }


        CalculationRequest request = new CalculationRequest(operation, a, b);

        CompletableFuture<CalculationResponse> futureResponse = responseConsumer.getFuture(request.getIdRequest());
        calculationRequestKafkaTemplate.send("calculation-topic", request.getIdRequest(), request);

        try {
            CalculationResponse response = futureResponse.get(5, TimeUnit.SECONDS);
            logger.info("Received response for request ID: {} with result: {}", request.getIdRequest(), response.getResult());
            return ResponseEntity.ok()
                    .header("X-Request-ID", response.getIdRequest())
                    .body(response.getResult());
        } catch (Exception e) {
            logger.error("Error while waiting for response: {}", e.getMessage(), e);
            return ResponseEntity.status(504).body(null);
        }
    }

}