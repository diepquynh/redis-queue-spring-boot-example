package com.example.redis.queue;

import java.io.File;
import java.time.Duration;

import jakarta.annotation.PreDestroy;

import org.springframework.boot.test.context.TestConfiguration;

import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

@TestConfiguration
@Testcontainers
public class RedisTestContainerConfig {

    // 60 seconds startup timeout, as the container
    // may have slow startup time depends on hardware spec
    private static final int STARTUP_TIMEOUT = 60;

    private final DockerComposeContainer<?> testContainer;

    public RedisTestContainerConfig() {
        testContainer =
                new DockerComposeContainer<>(new File("docker-compose.yaml"))
                        .withExposedService(
                                "redis",
                                6379,
                                Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(STARTUP_TIMEOUT)));
        testContainer.start();
    }

    @PreDestroy
    public void destroy() {
        testContainer.stop();
    }
}
