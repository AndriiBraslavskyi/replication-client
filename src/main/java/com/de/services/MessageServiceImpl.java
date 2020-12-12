package com.de.services;

import com.de.model.Message;
import com.de.repositories.MessageRepository;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class MessageServiceImpl implements MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);
    private static final int RANDOM_DELAY_UPPER_BOUND = 15000;

    private final MessageRepository messageRepository;
    private final int delay;

    public MessageServiceImpl(MessageRepository messageRepository, Integer delay) {
        this.messageRepository = Objects.requireNonNull(messageRepository);
        this.delay = Objects.requireNonNull(delay);
    }

    public void addMessage(Message message) {
        if (message.getId() == null || message.getPayload() == null) {
            throw new IllegalArgumentException("Validation for message {} failed. Missing required `id` or `payload`" +
                    " field.");
        }
        final int resolvedDelay = resolveDelay(delay);
        logger.info("Message {} will be saved with delay {}", message, resolvedDelay);
        delay(resolvedDelay);
        messageRepository.persistMessage(message);
    }

    @SneakyThrows
    private void delay(int resolveDelay) {
        Thread.sleep(resolveDelay);
    }

    public Collection<String> getMessages() {
        return messageRepository.readAll();
    }

    @SneakyThrows
    private static int resolveDelay(int delay) {
        return delay < 0 ? ThreadLocalRandom.current().nextInt(RANDOM_DELAY_UPPER_BOUND) : delay;
    }
}
