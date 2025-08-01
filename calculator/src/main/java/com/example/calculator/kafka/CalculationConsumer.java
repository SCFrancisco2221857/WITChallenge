package com.example.calculator.kafka;

import com.example.common.model.CalculationRequest;
import com.example.common.model.CalculationResponse;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CalculationConsumer {

    @Autowired
    private KafkaTemplate<String, CalculationResponse> kafkaTemplate;


    @KafkaListener(topics = "calculation-topic", groupId = "calculation-group")
    public void consume(ConsumerRecord<String, CalculationRequest> record){
        System.out.println("Received calculation request: " + record.value());
        CalculationRequest request = record.value();
        BigDecimal result= null;

        switch(request.getOperation()){
            case "sum":
                result = request.getFirstOperand().add(request.getSecondOperand());
                break;
            case "subtract":
                result = request.getFirstOperand().subtract(request.getSecondOperand());
                break;
            case "multiply":
                result = request.getFirstOperand().multiply(request.getSecondOperand());
                break;
            case "divide":
                if (request.getSecondOperand().compareTo(BigDecimal.ZERO) == 0) {
                    throw new ArithmeticException("Division by zero is not allowed");
                }
                result = request.getFirstOperand().divide(request.getSecondOperand());
                break;
        }

        CalculationResponse response = new CalculationResponse(request.getIdRequest(), result);
        kafkaTemplate.send("calculation-response-topic", response.getIdRequest(), response);

    }
}
