package com.example.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalculationResponse {

    //nao e necessario criar um novo id a cada vez que é criado um objeto pois o id é o mesmo do request
    private String idRequest;

    private BigDecimal result;

}
