package com.example.calculator.kafka;

import com.example.common.model.CalculationRequest;
import com.example.common.model.CalculationResponse;
import com.example.common.model.Operation;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CalculationConsumerTest {

    private KafkaTemplate<String, CalculationResponse> kafkaTemplate;
    private CalculationConsumer calculationConsumer;

    @BeforeEach
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        calculationConsumer = new CalculationConsumer(kafkaTemplate);
    }

    private void simulateConsume(Operation operation, BigDecimal a, BigDecimal b, BigDecimal expectedResult) {
        CalculationRequest request = new CalculationRequest(operation, a, b);
        ConsumerRecord<String, CalculationRequest> record = new ConsumerRecord<>("calculation-topic", 0, 0L, request.getIdRequest(), request);
        calculationConsumer.consume(record);

        ArgumentCaptor<CalculationResponse> captor = ArgumentCaptor.forClass(CalculationResponse.class);
        verify(kafkaTemplate).send(eq("calculation-response-topic"), eq(request.getIdRequest()), captor.capture());
        assertEquals(expectedResult, captor.getValue().getResult());
    }

    @Test
    void testSum() {
        simulateConsume(Operation.SUM, BigDecimal.TEN, BigDecimal.valueOf(5), BigDecimal.valueOf(15));
    }

    @Test
    void testSubtract() {
        simulateConsume(Operation.SUBTRACT, BigDecimal.TEN, BigDecimal.valueOf(5), BigDecimal.valueOf(5));
    }

    @Test
    void testMultiply() {
        simulateConsume(Operation.MULTIPLY, BigDecimal.TEN, BigDecimal.valueOf(5), BigDecimal.valueOf(50));
    }

    @Test
    void testDivide() {
        simulateConsume(Operation.DIVIDE, BigDecimal.TEN, BigDecimal.valueOf(5), BigDecimal.valueOf(2));
    }

    @Test
    void testDivideByZeroThrowsException() {
        CalculationRequest request = new CalculationRequest(Operation.DIVIDE, BigDecimal.TEN, BigDecimal.ZERO);
        ConsumerRecord<String, CalculationRequest> record = new ConsumerRecord<>("calculation-topic", 0, 0L, request.getIdRequest(), request);

        assertThrows(ArithmeticException.class, () -> calculationConsumer.consume(record));
        verify(kafkaTemplate, never()).send(any(), any(), any());
    }

}
