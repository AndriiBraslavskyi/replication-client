package com.de.repositories;

import com.de.exceptions.DuplicatedMessageException;
import com.de.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class InMemoryMessageRepository implements MessageRepository {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryMessageRepository.class);
    private final Map<Long, Message> messages;

    public InMemoryMessageRepository() {
        this.messages = new ConcurrentSkipListMap<>();
    }

    @Override
    public void persistMessage(Message message) {
        if (!messages.containsKey(message.getId())) {
            messages.put(message.getId(), message);
        } else {
            final String errorMessage = String.format("Failed to save message: %s with id: %s with a"
                    + " reason: Message is already saved, duplicated message", message.getPayload(), message.getId());
            logger.warn(errorMessage);
            throw new DuplicatedMessageException(errorMessage);
        }
    }

    @Override
    public Mono<Collection<Message>> readAll() {
        return Mono.just(messages.values());
    }
}
