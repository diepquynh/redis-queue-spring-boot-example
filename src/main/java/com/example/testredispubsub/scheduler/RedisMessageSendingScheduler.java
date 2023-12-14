package com.example.testredispubsub.scheduler;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.testredispubsub.publisher.RedisMessagePublisher;

@Component
@RequiredArgsConstructor
public class RedisMessageSendingScheduler {

    private final RedisMessagePublisher<String> redisMessagePublisher;

    @Scheduled(fixedDelay = 5000L)
    public void publishMessage() {
        int spamCount = 100;
        final String messageContent = "test-message";
        while (spamCount-- > 0) {
            redisMessagePublisher.send(messageContent);
            redisMessagePublisher.sendAsStream(messageContent);
        }
    }
}
