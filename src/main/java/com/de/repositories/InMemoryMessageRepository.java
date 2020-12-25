package com.de.repositories;

import com.de.exceptions.DuplicatedMessageException;
import com.de.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class InMemoryMessageRepository implements MessageRepository {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryMessageRepository.class);
    private final NavigableMap<Long, Message> savedMessages;
    private final NavigableMap<Long, Message> waitingForSaveMessages;
    private final Lock saveLock = new ReentrantLock();

    public InMemoryMessageRepository() {
        this.savedMessages = new ConcurrentSkipListMap<>();
        this.waitingForSaveMessages = new ConcurrentSkipListMap<>();
    }

    @Override
    public void persistMessage(Message message) {
        if (!savedMessages.containsKey(message.getId())) {
            final Long lastSavedId = getLastKey(savedMessages);
            final long currentMessageId = message.getId();
            if (isFirstMessage(message, lastSavedId) || isNextInOrderMessage(message, lastSavedId)) {
                saveCurrentAndAwaitingMessages(message);
            } else {
                doubleCheckAndSaveToAwaitingIfNotReady(message, currentMessageId);
            }
        } else {
            final String errorMessage = String.format("Failed to save message: %s with id: %s with a"
                    + " reason: Message is already saved, duplicated message", message.getPayload(), message.getId());
            logger.warn(errorMessage);
            throw new DuplicatedMessageException(errorMessage);
        }
    }

    private boolean isNextInOrderMessage(Message message, Long lastSavedId) {
        return lastSavedId != null && lastSavedId == message.getId() - 1;
    }

    private boolean isFirstMessage(Message message, Long lastSavedId) {
        return lastSavedId == null && message.getId() == 1;
    }

    private void doubleCheckAndSaveToAwaitingIfNotReady(Message message, long currentMessageId) {
        saveLock.lock();
        try {
            final Long lastSavedIdDoubleChecked = getLastKey(savedMessages);
            if (lastSavedIdDoubleChecked != null && lastSavedIdDoubleChecked == message.getId() - 1) {
                saveCurrentAndAwaitingMessages(message);
            } else {
                waitingForSaveMessages.put(currentMessageId, message);
            }
        } finally {
            saveLock.unlock();
        }
    }

    private <T, V> T getLastKey(NavigableMap<T, V> navigableMap) {
        return navigableMap.isEmpty() ? null : navigableMap.lastKey();
    }

    private void saveCurrentAndAwaitingMessages(Message message) {
        saveLock.lock();
        try {
            savedMessages.put(message.getId(), message);
            tryToSaveReadyToSaveMessages(message.getId());
        } finally {
            saveLock.unlock();
        }
    }

    private void tryToSaveReadyToSaveMessages(long currentMessageId) {
        long nextMessageToSaveId = currentMessageId + 1;
        Message nextMessageToSave = waitingForSaveMessages.get(nextMessageToSaveId);
        while (nextMessageToSave != null) {
            savedMessages.put(nextMessageToSaveId, nextMessageToSave);
            nextMessageToSaveId += 1;
            nextMessageToSave = waitingForSaveMessages.get(nextMessageToSaveId);
        }
    }

    @Override
    public Mono<Collection<Message>> readAll() {
        return Mono.just(savedMessages.values());
    }
}
