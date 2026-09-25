package com.automation.tests.kafka;

import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.EmbeddedKafkaKraftBroker;

public final class EmbeddedKafkaManager {

    public static final String TOPIC = "order-events";

    private static final EmbeddedKafkaBroker BROKER =
            new EmbeddedKafkaKraftBroker(
                    1,
                    1,
                    TOPIC
            );

    private EmbeddedKafkaManager() {
    }

    public static void start() throws Exception {
        BROKER.afterPropertiesSet();
    }

    public static EmbeddedKafkaBroker getBroker() {
        return BROKER;
    }

    public static String getBootstrapServers() {
        return BROKER.getBrokersAsString();
    }

    public static void stop() throws Exception {
        BROKER.destroy();
    }
}