package com.de.services;

import com.de.exceptions.InternalServerError;
import com.de.model.Message;
import com.de.repositories.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class MessageServiceImpl implements MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    private final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = Objects.requireNonNull(messageRepository);
    }

    public void addMessage(Message message) {
        logger.info(message.toString());
        if (true) {
            throw new InternalServerError("failed to add message");
        }
        messageRepository.persistMessage(message);
    }

    public Mono<Set<Message>> getMessages() {
        return Mono.just(new HashSet<>());
    }
}
