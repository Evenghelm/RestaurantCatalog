package org.example.employee.kafka;

import org.example.Application;
import org.example.employee.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = Application.class)
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@Sql({"/data/insert_test_data.sql", "/data/insert_user_data.sql"})
@Sql(scripts = "/data/clear_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class EmbeddedKafkaIntegrationTest {
    @Autowired
    private KafkaTemplate<String, Long> kafkaTemplate;
    @Autowired
    EmployeeServiceImpl service;

    @Test
    public void kafkaTest() throws Exception {
        kafkaTemplate.send("userDeleted", 2L);
        Thread.sleep(2000);
        assertTrue(service.getEmployeeById(2L).getIsUserDeleted());
    }
}