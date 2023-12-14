package com.example.testredispubsub.publisher.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.example.testredispubsub.publisher.RedisMessagePublisher;

@Component
@RequiredArgsConstructor
public class RedisMessagePublisherImpl implements RedisMessagePublisher<String> {

    private final RedisTemplate<String, String> redisTemplate;
    private final ChannelTopic channelTopic;

    @Async
    public void send(String message) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }

    @Override
    public void sendAsStream(String content) {
        final ObjectRecord<String, String> objectRecord =
                StreamRecords.newRecord().ofObject(content).withStreamKey(channelTopic.getTopic());
        redisTemplate.opsForStream().add(objectRecord);
    }
}
