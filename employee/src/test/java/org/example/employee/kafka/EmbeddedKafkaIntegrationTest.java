package org.example.employee.kafka;

import org.example.Application;
import org.example.employee.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = Application.class)
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@Sql({"/data/insert_test_data.sql", "/data/insert_user_data.sql"})
@Sql(scripts = "/data/clear_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class EmbeddedKafkaIntegrationTest {
    @Autowired
    private KafkaTemplate<String, Long> kafkaTemplate;
    @Autowired
    EmployeeService service;

    @Test
    public void kafkaTest() throws Exception {
        kafkaTemplate.send("userDeleted", 2L);
        Thread.sleep(2000);
        assertTrue(service.getEmployeeById(2L).getIsUserDeleted());
    }
}