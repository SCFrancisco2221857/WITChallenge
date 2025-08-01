package com.example.rest.controller;

import com.example.rest.kafka.CalculationResponseConsumer;
import com.example.common.model.CalculationRequest;
import com.example.common.model.CalculationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/calculation")
public class CalculationController {

    @Autowired
    private KafkaTemplate<String, CalculationRequest> kafkaTemplate;

    @Autowired
    private CalculationResponseConsumer responseConsumer;

    @GetMapping("/sum")
    public ResponseEntity<CalculationResponse> sum(@RequestParam BigDecimal firstOperand, @RequestParam BigDecimal secondOperand) {
        System.out.println("Received sum request with operands: " + firstOperand + " and " + secondOperand);
        CalculationRequest request = new CalculationRequest("sum", firstOperand, secondOperand);
        kafkaTemplate.send("calculation-topic", request.getIdRequest(), request);
        while (responseConsumer.getResponse(request.getIdRequest()) == null) {
            System.out.println("Waiting for response for request ID: " + request.getIdRequest());
            try {
                Thread.sleep(100); // Wait for the response to be available
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return ResponseEntity.status(500).build(); // Handle interruption
            }
        }
        CalculationResponse response = responseConsumer.getResponse(request.getIdRequest());
        System.out.println("Received response: " + response);
        return ResponseEntity.ok(response);
    }
}
