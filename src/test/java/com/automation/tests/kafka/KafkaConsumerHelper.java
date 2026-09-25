package com.automation.tests.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaConsumerHelper {

    private final KafkaConsumer<String, String> consumer;
    private final String topic;

    public KafkaConsumerHelper(
            String bootstrapServers,
            String groupId,
            String topic) {

        this.topic = topic;

        Properties properties = new Properties();

        properties.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        properties.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                groupId
        );

        properties.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer"
        );

        properties.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer"
        );

        properties.put(
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "earliest"
        );

        consumer = new KafkaConsumer<>(properties);

        consumer.subscribe(
                Collections.singletonList(topic)
        );
    }

    public String findEventByKey(String expectedKey) {

        var records = consumer.poll(
                Duration.ofMillis(500)
        );

        for (ConsumerRecord<String, String> record : records) {

            if (expectedKey.equals(record.key())) {
                return record.value();
            }
        }

        return null;
    }

    public void close() {
        consumer.close();
    }
}