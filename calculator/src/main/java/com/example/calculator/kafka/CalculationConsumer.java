package com.example.calculator.kafka;

import com.example.common.model.CalculationRequest;
import com.example.common.model.CalculationResponse;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;

@Service
public class CalculationConsumer {

    private final KafkaTemplate<String, CalculationResponse> calculationResponseKafkaTemplate;

    public CalculationConsumer(KafkaTemplate<String, CalculationResponse> calculationResponseKafkaTemplate) {
        this.calculationResponseKafkaTemplate = calculationResponseKafkaTemplate;
    }

    @KafkaListener(topics = "calculation-topic", groupId = "calculation-group")
    public void consume(ConsumerRecord<String, CalculationRequest> record) {
        System.out.println("Received calculation request: " + record.value());
        CalculationRequest request = record.value();
        BigDecimal result;

        switch (request.getOperation()) {
            case SUM -> result = request.getFirstOperand().add(request.getSecondOperand());
            case SUBTRACT -> result = request.getFirstOperand().subtract(request.getSecondOperand());
            case MULTIPLY -> result = request.getFirstOperand().multiply(request.getSecondOperand());
            case DIVIDE -> {
                if (request.getSecondOperand().compareTo(BigDecimal.ZERO) == 0) {
                    throw new ArithmeticException("Division by zero is not allowed");
                }
                result = request.getFirstOperand().divide(request.getSecondOperand(), MathContext.UNLIMITED);

            }
            default -> throw new UnsupportedOperationException("Operation not supported: " + request.getOperation());
        }

        CalculationResponse response = new CalculationResponse(request.getIdRequest(), result);

        calculationResponseKafkaTemplate.send("calculation-response-topic", response.getIdRequest(), response);
    }
}

