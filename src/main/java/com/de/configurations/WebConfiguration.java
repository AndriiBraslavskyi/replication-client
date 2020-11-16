package com.de.configurations;

import com.de.repositories.InMemoryMessageRepository;
import com.de.repositories.MessageRepository;
import com.de.services.MessageService;
import com.de.services.MessageServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfiguration {

    @Bean
    MessageService messageService(MessageRepository messageRepository,
                                  @Value("${replication-client.delay}") Integer delay) {
        return new MessageServiceImpl(messageRepository, delay);
    }

    @Bean
    MessageRepository messageRepository() {
        return new InMemoryMessageRepository();
    }
}
