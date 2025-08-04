package com.example.rest.controller;


import com.example.common.model.Operation;
import com.example.rest.service.OperationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/calculation")
public class CalculationController {


    private final OperationService operationService;

    public CalculationController(OperationService operationService) {
        this.operationService = operationService;
    }

    @GetMapping("/sum")
    public ResponseEntity<BigDecimal> sum(@RequestParam BigDecimal a, @RequestParam BigDecimal b) {
        return operationService.handleOperation(Operation.SUM, a, b);
    }

    @GetMapping("/subtract")
    public ResponseEntity<BigDecimal> subtract(@RequestParam BigDecimal a, @RequestParam BigDecimal b) {
        return operationService.handleOperation(Operation.SUBTRACT, a, b);
    }

    @GetMapping("/multiply")
    public ResponseEntity<BigDecimal> multiply(@RequestParam BigDecimal a, @RequestParam BigDecimal b) {
        return operationService.handleOperation(Operation.MULTIPLY, a, b);
    }

    @GetMapping("/divide")
    public ResponseEntity<BigDecimal> divide(@RequestParam BigDecimal a, @RequestParam BigDecimal b) {
        return operationService.handleOperation(Operation.DIVIDE, a, b);
    }



}
