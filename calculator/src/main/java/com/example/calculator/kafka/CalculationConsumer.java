package com.example.calculator.kafka;

import com.example.common.model.CalculationRequest;
import com.example.common.model.CalculationResponse;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CalculationConsumer {

    private final KafkaTemplate<String, CalculationResponse> calculationResponseKafkaTemplate;

    private static final Logger logger = LoggerFactory.getLogger(CalculationConsumer.class);

    public CalculationConsumer(KafkaTemplate<String, CalculationResponse> calculationResponseKafkaTemplate) {
        this.calculationResponseKafkaTemplate = calculationResponseKafkaTemplate;
    }

    @KafkaListener(topics = "calculation-topic", groupId = "calculation-group")
    public void consume(ConsumerRecord<String, CalculationRequest> record) {

        CalculationRequest request = record.value();

        try {
            MDC.put("x-request-id", request.getIdRequest());
            logger.info("Received calculation request: {}", request);

            BigDecimal result;
            //futuramente pode-se passar para um serviço a parte de cálculos
            if (request.getFirstOperand() == null || request.getSecondOperand() == null) {
                logger.error("Invalid operands for request ID: {} - First Operand: {}, Second Operand: {}",
                        request.getIdRequest(), request.getFirstOperand(), request.getSecondOperand());
                throw new IllegalArgumentException("Operands cannot be null");
            }
            switch (request.getOperation()) {
                case SUM -> result = request.getFirstOperand().add(request.getSecondOperand());
                case SUBTRACT -> result = request.getFirstOperand().subtract(request.getSecondOperand());
                case MULTIPLY -> result = request.getFirstOperand().multiply(request.getSecondOperand());
                case DIVIDE -> {
                    if (request.getSecondOperand().compareTo(BigDecimal.ZERO) == 0) {
                        logger.error("Division by zero attempted for request ID: {}", request.getIdRequest());
                        throw new ArithmeticException("Division by zero is not allowed");
                    }
                    result = request.getFirstOperand().divide(request.getSecondOperand(), MathContext.UNLIMITED);
                }
                default ->
                        throw new UnsupportedOperationException("Operation not supported: " + request.getOperation());
            }

            CalculationResponse response = new CalculationResponse(request.getIdRequest(), result);
            logger.info("Calculated result for request ID: {} - Result: {}", request.getIdRequest(), result);
            calculationResponseKafkaTemplate.send("calculation-response-topic", response.getIdRequest(), response);

        } finally {

            logger.info("Finished processing request ID: {}", record.key());
            MDC.remove("x-request-id");

        }
    }
}

