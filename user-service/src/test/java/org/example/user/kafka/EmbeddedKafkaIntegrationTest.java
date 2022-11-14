package org.example.user.kafka;

import org.example.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.Assert.assertEquals;

@SpringBootTest(classes = Application.class)
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
class EmbeddedKafkaIntegrationTest {
    @Autowired
    private KafkaTemplate producer;

    private long receivedId;

    @KafkaListener(topics = "userDeleted")
    public void userDeletionListener(Long userId) {
        receivedId = userId;
    }

    @Test
    public void kafkaTest() throws Exception {
        producer.send("userDeleted", 2L);
        Thread.sleep(2000);
        assertEquals(2, receivedId);
    }
}