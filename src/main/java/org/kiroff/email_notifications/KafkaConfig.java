package org.kiroff.email_notifications;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.kiroff.kafka.events.ProductCreatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig
{
    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.producer.key-serializer}")
    private String keySerializerClassConfig;
    @Value("${spring.kafka.producer.value-serializer}")
    private String valueSerializerClassConfig;
    @Value("${spring.kafka.producer.acks}")
    private String acksConfig;
    @Value("${spring.kafka.producer.properties.delivery.timeout.ms}")
    private String deliveryTimeoutMsConfig;
    @Value("${spring.kafka.producer.properties.linger.ms}")
    private String lingerMsConfig;
    @Value("${spring.kafka.producer.properties.request.timeout.ms}")
    private String requestTimeoutMsConfig;
    @Value("${spring.kafka.producer.properties.enable.idempotence}")
    private String enableIdempotenceConfig;
    @Value("${spring.kafka.producer.properties.max.in.flight.requests.per.connection}")
    private String maxInFlightRequestsPerConnection;

    @Bean
    Map<String, Object> producerConfigs()
    {
        final Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializerClassConfig);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializerClassConfig);
        props.put(ProducerConfig.ACKS_CONFIG, acksConfig);
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeoutMsConfig);
        props.put(ProducerConfig.LINGER_MS_CONFIG, lingerMsConfig);
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMsConfig);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, enableIdempotenceConfig);
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, maxInFlightRequestsPerConnection);
//        props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);//Default
        return props;
    }

    @Bean
    ProducerFactory<String, ProductCreatedEvent> producerFactory()
    {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate()
    {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    NewTopic createTopic() {
        return TopicBuilder.name("product-created-events-topic")
                .partitions(3)
                .replicas(3)
                .configs(Map.of("min.insync.replicas", "2"))
                .build();
    }
}
