package com.de.configurations;

import com.de.repositories.InMemoryMessageRepository;
import com.de.repositories.MessageRepository;
import com.de.services.MessageService;
import com.de.services.MessageServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfiguration {

    @Bean
    MessageService messageService(MessageRepository messageRepository) {
        return new MessageServiceImpl(messageRepository);
    }

    @Bean
    MessageRepository messageRepository() {
        return new InMemoryMessageRepository();
    }
}
