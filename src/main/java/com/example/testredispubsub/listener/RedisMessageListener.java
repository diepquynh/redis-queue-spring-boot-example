package com.example.testredispubsub.listener;

import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.PendingMessagesSummary;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.HashMapper;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisMessageListener
        implements MessageListener, StreamListener<String, ObjectRecord<String, String>> {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void onMessage(final Message message, @Nullable final byte[] pattern) {
        final String body = new String(message.getBody());
        final String topic = new String(message.getChannel());
        log.info("Redis PubSub: Message received from topic {}: {}", topic, body);
    }

    @Override
    public void onMessage(final ObjectRecord<String, String> message) {
        final String topic = message.getRequiredStream();
        readAndAck(topic, topic, message.getValue(), message.getId().getValue());

        final PendingMessagesSummary summary =
                Objects.requireNonNull(redisTemplate.opsForStream().pending(topic, topic));
        if (summary.getTotalPendingMessages() == 0) {
            return;
        }

        final RecordId minRecordId = summary.minRecordId();
        final RecordId maxRecordId = summary.maxRecordId();
        final String fromTimestamp = Objects.requireNonNull(minRecordId.getTimestamp()).toString();
        final String toTimestamp = Objects.requireNonNull(maxRecordId.getTimestamp()).toString();
        final Range<String> closedRange = Range.closed(fromTimestamp, toTimestamp);
        final List<MapRecord<String, Object, Object>> pendingMessages =
                redisTemplate.opsForStream().range(topic, closedRange);

        if (!CollectionUtils.isEmpty(pendingMessages)) {
            pendingMessages.forEach(entries -> readPendingMessage(topic, entries));
        }
    }

    private void readPendingMessage(
            final String topic, final MapRecord<String, Object, Object> pendingMessage) {
        final HashMapper<String, Object, Object> hashMapper =
                redisTemplate.opsForStream().getHashMapper(String.class);
        final ObjectRecord<String, String> objectRecord = pendingMessage.toObjectRecord(hashMapper);
        readAndAck(
                topic,
                objectRecord.getRequiredStream(),
                objectRecord.getValue(),
                objectRecord.getId().getValue());
    }

    private void readAndAck(
            final String topic, final String stream, final String value, final String messageId) {
        log.info("Redis stream: Message received from topic {}, stream {}: {}", topic, stream, value);
        redisTemplate.opsForStream().acknowledge(topic, stream, messageId);
    }
}
