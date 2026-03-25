package org.kiroff.email_notifications;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class EmailNotificationServiceApplication
{

    @Bean
    RestTemplate restTemplate()
    {
        return new RestTemplate();
    }

    static void main(String[] args)
    {
        SpringApplication.run(EmailNotificationServiceApplication.class, args);
    }

}
