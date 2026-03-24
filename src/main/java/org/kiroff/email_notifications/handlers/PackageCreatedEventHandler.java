package org.kiroff.email_notifications.handlers;

import org.kiroff.kafka.events.ProductCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = {"product-created-events-topic"}, groupId = "product-created-event")
public class PackageCreatedEventHandler
{
    private final Logger logger = LoggerFactory.getLogger(PackageCreatedEventHandler.class);

    @KafkaHandler
    public void handle(ProductCreatedEvent event)
    {
        logger.warn("Received product created event: {}", event.getTitle());
    }
}
