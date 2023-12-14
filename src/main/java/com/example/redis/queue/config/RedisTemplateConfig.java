package com.example.redis.queue.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisTemplateConfig {

    @Bean
    public RedisTemplate<String, ?> redisTemplate(final RedisConnectionFactory connectionFactory) {
        final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }

    /**
     * Config bean hashOperations
     *
     * @param redisTemplate bean
     * @param <K> hash key type
     * @param <V> value type
     * @return bean hashOperations
     */
    @Bean
    <K, V>
            HashOperations<String, K, V> // NOSONAR
                    hashOperations(final RedisTemplate<String, V> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    /**
     * ListOperations bean configuration
     *
     * @param redisTemplate inject bean
     * @param <V> value type
     * @return listOperations
     */
    @Bean
    <V> ListOperations<String, V> listOperations(final RedisTemplate<String, V> redisTemplate) {
        return redisTemplate.opsForList();
    }

    /**
     * ZSetOperations configuration
     *
     * @param redisTemplate inject bean
     * @param <V> value type
     * @return ZSetOperations<String, V>
     */
    @Bean
    <V> ZSetOperations<String, V> zSetOperations(final RedisTemplate<String, V> redisTemplate) {
        return redisTemplate.opsForZSet();
    }

    /**
     * SetOperations configuration
     *
     * @param redisTemplate inject bean
     * @param <V> value type
     * @return SetOperations<String, V>
     */
    @Bean
    <V> SetOperations<String, V> setOperations(final RedisTemplate<String, V> redisTemplate) {
        return redisTemplate.opsForSet();
    }

    /**
     * ValueOperations configuration
     *
     * @param redisTemplate inject bean
     * @param <V> value type
     * @return ValueOperations<String, V>
     */
    @Bean
    <V> ValueOperations<String, V> valueOperations(final RedisTemplate<String, V> redisTemplate) {
        return redisTemplate.opsForValue();
    }
}
