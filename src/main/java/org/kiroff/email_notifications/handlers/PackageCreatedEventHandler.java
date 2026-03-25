package org.kiroff.email_notifications.handlers;

import org.kiroff.kafka.errors.NotRetryableException;
import org.kiroff.kafka.errors.RetryableException;
import org.kiroff.kafka.events.ProductCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
@KafkaListener(topics = {"product-created-events-topic"},
        groupId = "product-created-event",
        containerFactory = "kafkaConcurrentKafkaListenerContainerFactory")//Should match the name in KafkaConfig, by default Spring uses a bean named kafkaListenerContainerFactory.
public class PackageCreatedEventHandler
{
    private final Logger logger = LoggerFactory.getLogger(PackageCreatedEventHandler.class);

    private RestTemplate restTemplate;

    public PackageCreatedEventHandler(RestTemplate restTemplate)
    {
        this.restTemplate  = restTemplate;
    }

    @KafkaHandler
    public void handle(ProductCreatedEvent event)
    {
        logger.info("Received product created event: {}", event.getTitle());
        String url = "http://localhost:8899/products/" + event.getProductId();

        try
        {
            final ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

            if (HttpStatus.OK.equals(response.getStatusCode()))
            {
                logger.info("Successfully received product created event: {}", response.getBody());
            }
        } catch (ResourceAccessException e) {
            logger.error(e.getMessage());
            throw new RetryableException(e.getMessage());
        } catch (HttpServerErrorException e) {
            logger.error(e.getMessage());
            throw new NotRetryableException(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new NotRetryableException(e.getMessage());
        }
    }
}
