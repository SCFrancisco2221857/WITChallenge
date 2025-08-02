package com.example.common.config;

import com.example.common.model.CalculationRequest;
import com.example.common.model.CalculationResponse;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    private Map<String, Object> commonConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<String, CalculationRequest> calculationRequestProducerFactory() {
        return new DefaultKafkaProducerFactory<>(commonConfig());
    }


    @Bean
    public ProducerFactory<String, CalculationResponse> calculationResponseProducerFactory() {
        return new DefaultKafkaProducerFactory<>(commonConfig());
    }

    @Bean
    public KafkaTemplate<String, CalculationRequest> calculationRequestKafkaTemplate(ProducerFactory<String, CalculationRequest> calculationRequestProducerFactory) {
        return new KafkaTemplate<>(calculationRequestProducerFactory);
    }

    @Bean
    public KafkaTemplate<String, CalculationResponse> calculationResponseKafkaTemplate(ProducerFactory<String, CalculationResponse> calculationResponseProducerFactory) {
        return new KafkaTemplate<>(calculationResponseProducerFactory);
    }

}
