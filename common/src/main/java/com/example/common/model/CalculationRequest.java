package com.example.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
public class CalculationRequest {

    private String operation;

    //arbitrary precision signed decimal numbers
    private BigDecimal firstOperand;

    private BigDecimal secondOperand;

    //devido a ser gets criar um novo id a cada vez que é criado um objeto
    private String idRequest;

    public CalculationRequest(String operation, BigDecimal firstOperand, BigDecimal secondOperand) {
        this.operation = operation;
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
        this.idRequest = UUID.randomUUID().toString();
    }
}
