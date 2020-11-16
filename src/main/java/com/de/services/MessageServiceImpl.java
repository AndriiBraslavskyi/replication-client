package com.de.services;

import com.de.model.Message;
import com.de.repositories.MessageRepository;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class MessageServiceImpl implements MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    private final MessageRepository messageRepository;
    private final int delay;

    public MessageServiceImpl(MessageRepository messageRepository, Integer delay) {
        this.messageRepository = Objects.requireNonNull(messageRepository);
        this.delay = Objects.requireNonNull(delay);
    }

    public void addMessage(Message message) {
        logger.info("Message {} will be saved with delay {}", message, delay);
        delay(delay);
        messageRepository.persistMessage(message);
    }

    public List<String> getMessages() {
        return messageRepository.readAll().stream()
                .map(Message::getPayload)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private static void delay(int delay) {
        Thread.sleep(delay);
    }
}
