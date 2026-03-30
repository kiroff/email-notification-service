package org.kiroff.email_notifications.handlers;

import jakarta.transaction.Transactional;
import org.kiroff.email_notifications.io.ProcessedEventEntity;
import org.kiroff.email_notifications.io.ProcessedEventRepository;
import org.kiroff.kafka.errors.NotRetryableException;
import org.kiroff.kafka.errors.RetryableException;
import org.kiroff.kafka.events.ProductCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
@KafkaListener(topics = {"product-created-events-topic"},
        groupId = "product-created-events",
        containerFactory = "kafkaConcurrentKafkaListenerContainerFactory")//Should match the name in KafkaConfig, by default Spring uses a bean named kafkaListenerContainerFactory.
public class PackageCreatedEventHandler
{
    private final Logger logger = LoggerFactory.getLogger(PackageCreatedEventHandler.class);

    private final RestTemplate restTemplate;

    private final ProcessedEventRepository processedEventRepository;

    public PackageCreatedEventHandler(RestTemplate restTemplate, ProcessedEventRepository processedEventRepository)
    {
        this.restTemplate  = restTemplate;
        this.processedEventRepository = processedEventRepository;
    }

    @KafkaHandler
    @Transactional
    public void handle(@Payload ProductCreatedEvent event,
            @Header(value = "messageId", required = true) String messageId,
            @Header(KafkaHeaders.RECEIVED_KEY) String messageKey)
    {
        logger.info("Received product created event: {} with id {}", event.getTitle(), event.getProductId());
        String url = "http://localhost:8899/products/" + event.getProductId();

        final ProcessedEventEntity existing = processedEventRepository.findByMessageId(messageId);

        if(existing != null) {
            logger.info("Found existing product created event: messageId={}, productId={}", existing.getMessageId(), existing.getProductId());
            return;
        }

        try
        {
            final ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

            if (HttpStatus.OK.equals(response.getStatusCode()))
            {
                logger.info("Successfully received product created event: {}", response.getBody());
            }
        }
        catch (ResourceAccessException e)
        {
            logger.error(e.getMessage());
            throw new RetryableException(e.getMessage());
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            throw new NotRetryableException(e.getMessage());
        }

        // Save
        try
        {
            processedEventRepository.save(new ProcessedEventEntity(messageId, messageKey));
        }
        catch (DataIntegrityViolationException e)
        {
            throw new NotRetryableException(e);
        }
    }
}
