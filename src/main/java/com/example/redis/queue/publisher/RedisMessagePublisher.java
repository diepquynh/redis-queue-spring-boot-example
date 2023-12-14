package com.example.redis.queue.publisher;

public interface RedisMessagePublisher<T> {

    void send(T content);

    void sendAsStream(T content);
}
