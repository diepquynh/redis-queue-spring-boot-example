# Redis Pub/Sub - Redis Stream message queue publisher/subscriber implementation in Spring Boot

## Requirements
- Java 17
- Docker
- Node >= 16 (for Spotless code formatter)

## Run
1. Fire up Redis cluster Docker container
```shell
docker compose up -d
```
2. Run application
```shell
mvn clean spring-boot:run
```

### Optional: Run code formatting (Spotless)
```shell
mvn spotless:apply
```
