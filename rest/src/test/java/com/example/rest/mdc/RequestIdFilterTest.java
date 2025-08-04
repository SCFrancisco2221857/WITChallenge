package com.example.rest.mdc;

import com.example.rest.service.OperationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class RequestIdFilterTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OperationService operationService;

    @Test
    void testRequestIdHeader() throws Exception {

        when(operationService.handleOperation(any(), any(), any()))
                .thenReturn(ResponseEntity.ok(new BigDecimal("15")));

        mockMvc.perform(get("/api/calculation/sum")
                        .param("a", "10")
                        .param("b", "5")
                        .header("X-Request-ID", "1234-abc"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Request-ID", "1234-abc"));
    }
}