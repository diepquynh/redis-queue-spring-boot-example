package com.example.redis.queue.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisPubSubConfig {

    @Value("${spring.redis.messaging.topic}")
    private String messageTopic;

    @Bean
    public ChannelTopic topic() {
        return new ChannelTopic(messageTopic);
    }

    @Bean
    public RedisMessageListenerContainer messageListenerContainer(
            final RedisConnectionFactory redisConnectionFactory,
            final MessageListener messageListener,
            final ChannelTopic channelTopic) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(new MessageListenerAdapter(messageListener), channelTopic);
        return container;
    }
}
