package com.automation.tests.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class KafkaProducerHelper {

    private final KafkaProducer<String, String> producer;

    public KafkaProducerHelper(String bootstrapServers) {

        Properties properties = new Properties();

        properties.put(
                "bootstrap.servers",
                bootstrapServers
        );

        properties.put(
                "key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer"
        );

        properties.put(
                "value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer"
        );

        producer = new KafkaProducer<>(properties);
    }

    public void send(
            String topic,
            String key,
            String message) {

        producer.send(
                new ProducerRecord<>(topic, key, message)
        );

        producer.flush();
    }

    public void close() {
        producer.close();
    }
}