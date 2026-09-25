package com.automation.tests.kafka;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class KafkaAsyncTest {

    private KafkaProducerHelper producer;
    private KafkaConsumerHelper consumer;

    @BeforeClass
    public void setup() throws Exception {

        EmbeddedKafkaManager.start();

        String bootstrapServers =
                EmbeddedKafkaManager.getBootstrapServers();

        producer = new KafkaProducerHelper(
                bootstrapServers
        );

        consumer = new KafkaConsumerHelper(
                bootstrapServers,
                "automation-test-group",
                EmbeddedKafkaManager.TOPIC
        );
    }

    @Test
    public void shouldConsumeKafkaEvent() {

        String orderId = "ORD-123";

        String message = """
                {
                    "eventType": "ORDER_CREATED",
                    "orderId": "ORD-123",
                    "status": "CREATED"
                }
                """;

        producer.send(
                EmbeddedKafkaManager.TOPIC,
                orderId,
                message
        );

        await()
                .atMost(10, SECONDS)
                .pollInterval(500, MILLISECONDS)
                .untilAsserted(() -> {

                    String event =
                            consumer.findEventByKey(orderId);

                    assertThat(event)
                            .isNotNull();

                    assertThat(event)
                            .contains("ORDER_CREATED");

                    assertThat(event)
                            .contains("CREATED");
                });
    }

    @AfterClass
    public void teardown() throws Exception {

        producer.close();
        consumer.close();

        EmbeddedKafkaManager.stop();
    }
}