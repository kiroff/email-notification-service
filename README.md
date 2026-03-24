# Email Notification Service

Spring Boot service intended to consume email-notification events (Kafka) and expose HTTP endpoints for health and integration needs. The codebase is currently a minimal scaffold.

## Tech Stack
- Java 25
- Spring Boot 4.0.4
- Maven Wrapper
- Apache Kafka (via Spring for Apache Kafka)

## Project Status
This repository is a starting point. No Kafka consumers, HTTP controllers, or email-sending integrations are implemented yet.

## Getting Started

### Prerequisites
- JDK 25 installed and configured
- Internet access to download Maven dependencies

### Build
```bash
./mvnw clean verify
```

### Run
```bash
./mvnw spring-boot:run
```

### Package (Jar)
```bash
./mvnw package
```

## Configuration
The only configured setting is the Spring application name:

- `spring.application.name=email-notification-service`

Add Kafka broker settings and any email provider credentials in `src/main/resources/application.yaml` when the implementation is added.

## Suggested Next Steps
- Define Kafka topics and message schema.
- Add Kafka consumer(s) to process notification events.
- Implement email provider integration (SMTP or API-based).
- Add HTTP endpoints for health, metrics, and manual triggers.

## Tests
```bash
./mvnw test
```
