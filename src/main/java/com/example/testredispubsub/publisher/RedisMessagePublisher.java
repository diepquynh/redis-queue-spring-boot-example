package com.example.testredispubsub.publisher;

public interface RedisMessagePublisher<T> {

    void send(T content);

    void sendAsStream(T content);
}
