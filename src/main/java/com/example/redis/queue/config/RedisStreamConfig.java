package com.example.redis.queue.config;

import java.time.Duration;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamInfo;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

@Configuration
@RequiredArgsConstructor
public class RedisStreamConfig {

    private final RedisProperties redisProperties;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.redis.messaging.topic}")
    private String messageTopic;

    @Bean
    public Subscription subscription(
            final RedisConnectionFactory redisConnectionFactory,
            final StreamListener<String, ObjectRecord<String, String>> streamListener) {
        final StreamMessageListenerContainer.StreamMessageListenerContainerOptions<
                        String, ObjectRecord<String, String>>
                options =
                        StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                                .pollTimeout(Duration.ofSeconds(1))
                                .targetType(String.class)
                                .build();
        final StreamMessageListenerContainer<String, ObjectRecord<String, String>> listenerContainer =
                StreamMessageListenerContainer.create(redisConnectionFactory, options);
        final Subscription subscription =
                listenerContainer.receive(
                        Consumer.from(messageTopic, redisProperties.getHost()),
                        StreamOffset.create(messageTopic, ReadOffset.lastConsumed()),
                        streamListener);
        listenerContainer.start();

        // Create topic and consumer group
        redisTemplate.opsForStream().add(messageTopic, Map.of(messageTopic, messageTopic));
        final StreamInfo.XInfoGroups groups = redisTemplate.opsForStream().groups(messageTopic);
        if (groups.isEmpty()) {
            redisTemplate.opsForStream().createGroup(messageTopic, messageTopic);
        }

        return subscription;
    }
}
