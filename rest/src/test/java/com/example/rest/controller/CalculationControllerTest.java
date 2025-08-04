package com.example.rest.controller;

import com.example.common.model.Operation;
import com.example.rest.service.OperationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(CalculationController.class)
class CalculationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OperationService operationService;

    @Test
    void testSum() throws Exception {
        when(operationService.handleOperation(Operation.SUM, BigDecimal.valueOf(10), BigDecimal.valueOf(5)))
                .thenReturn(ResponseEntity.ok(BigDecimal.valueOf(15)));

        mockMvc.perform(get("/api/calculation/sum").param("a", "10").param("b", "5"))
                .andExpect(status().isOk())
                .andExpect(content().string("15"));
    }

    @Test
    void testSubtract() throws Exception {
        when(operationService.handleOperation(Operation.SUBTRACT, BigDecimal.valueOf(10), BigDecimal.valueOf(5)))
                .thenReturn(ResponseEntity.ok(BigDecimal.valueOf(5)));

        mockMvc.perform(get("/api/calculation/subtract").param("a", "10").param("b", "5"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    void testMultiply() throws Exception {
        when(operationService.handleOperation(Operation.MULTIPLY, BigDecimal.valueOf(10), BigDecimal.valueOf(5)))
                .thenReturn(ResponseEntity.ok(BigDecimal.valueOf(50)));

        mockMvc.perform(get("/api/calculation/multiply").param("a", "10").param("b", "5"))
                .andExpect(status().isOk())
                .andExpect(content().string("50"));
    }

    @Test
    void testDivide() throws Exception {
        when(operationService.handleOperation(Operation.DIVIDE, BigDecimal.valueOf(10), BigDecimal.valueOf(5)))
                .thenReturn(ResponseEntity.ok(BigDecimal.valueOf(2)));

        mockMvc.perform(get("/api/calculation/divide").param("a", "10").param("b", "5"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }


}