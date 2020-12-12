package com.de.configurations;

import com.de.repositories.InMemoryMessageRepository;
import com.de.repositories.MessageRepository;
import com.de.services.MessageService;
import com.de.services.MessageServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfiguration {

    final private static Logger logger = LoggerFactory.getLogger(WebConfiguration.class);

    @Bean
    MessageService messageService(MessageRepository messageRepository,
                                  @Value("${replication-client.persist-delay}") Integer delay) {
        logger.info("Client started with delay = {}", delay);
        return new MessageServiceImpl(messageRepository, delay);
    }

    @Bean
    MessageRepository messageRepository() {
        return new InMemoryMessageRepository();
    }
}
