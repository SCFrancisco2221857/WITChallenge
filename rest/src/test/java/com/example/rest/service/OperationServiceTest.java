// Pacote onde está este ficheiro de teste
package com.example.rest.service;

// Importações das classes a testar e das dependências
import com.example.common.model.CalculationRequest;
import com.example.common.model.CalculationResponse;
import com.example.common.model.Operation;
import com.example.rest.kafka.CalculationResponseConsumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

// Métodos estáticos para facilitar asserções e mocks
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Classe de teste unitário para a classe OperationService.
 * Usa JUnit 5 para estruturar os testes e Mockito para simular dependências.
 */
class OperationServiceTest {

    // Dependências simuladas (mocks)
    private KafkaTemplate<String, CalculationRequest> kafkaTemplate;
    private CalculationResponseConsumer responseConsumer;

    // Instância real do service a testar (com mocks injetados)
    private OperationService operationService;

    /**
     * Este método corre antes de cada teste.
     * Inicializa os mocks e cria o service com as dependências simuladas.
     */
    @BeforeEach
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class); // simula o KafkaTemplate
        responseConsumer = mock(CalculationResponseConsumer.class); // simula o consumer
        operationService = new OperationService(kafkaTemplate, responseConsumer);
    }

    /**
     * Teste que simula uma operação bem-sucedida.
     * Verifica se o método handleOperation devolve corretamente o resultado
     * quando o consumidor responde com sucesso.
     */
    @Test
    void testHandleOperationSuccess() throws Exception {
        // --- ARRANGE (preparar dados) ---
        BigDecimal a = BigDecimal.valueOf(10);
        BigDecimal b = BigDecimal.valueOf(5);
        Operation operation = Operation.SUM;

        // Criar resposta simulada do Kafka
        CalculationResponse mockResponse = new CalculationResponse();
        mockResponse.setResult(BigDecimal.valueOf(15));

        // Simular que o consumidor devolve esta resposta imediatamente
        CompletableFuture<CalculationResponse> future = CompletableFuture.completedFuture(mockResponse);
        when(responseConsumer.getFuture(anyString())).thenReturn(future);

        // --- ACT (executar o código a testar) ---
        ResponseEntity<BigDecimal> result = operationService.handleOperation(operation, a, b);

        // --- ASSERT (verificar resultado) ---
        assertEquals(200, result.getStatusCodeValue()); // código de sucesso HTTP
        assertEquals(BigDecimal.valueOf(15), result.getBody()); // valor correto

        // Verifica se o service enviou corretamente a mensagem para Kafka
        verify(kafkaTemplate).send(
                eq("calculation-topic"), // tópico correto
                anyString(),             // ID do pedido (é uma UUID)
                any(CalculationRequest.class) // o pedido em si
        );
    }

    /**
     * Teste que simula um timeout (Kafka não responde).
     * Verifica se o método handleOperation devolve status 504 (Gateway Timeout).
     */
    @Test
    void testHandleOperationTimeout() {
        // --- ARRANGE ---
        BigDecimal a = BigDecimal.valueOf(7);
        BigDecimal b = BigDecimal.valueOf(3);
        Operation operation = Operation.SUBTRACT;

        // Simular que o futuro nunca é resolvido (Kafka não responde)
        CompletableFuture<CalculationResponse> future = new CompletableFuture<>();
        when(responseConsumer.getFuture(anyString())).thenReturn(future);

        // --- ACT ---
        ResponseEntity<BigDecimal> result = operationService.handleOperation(operation, a, b);

        // --- ASSERT ---
        assertEquals(504, result.getStatusCodeValue()); // código HTTP de timeout
        assertNull(result.getBody()); // sem corpo na resposta

        // Confirma que o serviço tentou enviar a mensagem para Kafka
        verify(kafkaTemplate).send(
                eq("calculation-topic"),
                anyString(),
                any(CalculationRequest.class)
        );
    }


}
