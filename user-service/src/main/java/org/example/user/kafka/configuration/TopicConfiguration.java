package org.example.user.kafka.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicConfiguration {
    @Value("${tpd.userDeletionTopicName}")
    private String topicName;
    @Bean
    public NewTopic topic() {
        return new NewTopic(topicName, 1, (short) 1);
    }
}
