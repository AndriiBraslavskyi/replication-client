package com.de.repositories;

import com.de.exceptions.DuplicatedMessageException;
import com.de.model.Message;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InMemoryMessageRepository implements MessageRepository {

    final List<Message> messages;
    final Set<String> messagesUids;

    public InMemoryMessageRepository() {
        this.messages = new ArrayList<>();
        this.messagesUids = new HashSet<>();
    }

    @Override
    public void persistMessage(Message message) {
        if (!messagesUids.contains(message.getId())) {
            messages.add(message);
            messagesUids.add(message.getId());
        } else {
            throw new DuplicatedMessageException(String.format("Failed to save message: %s with id: %s with a"
                    + " reason: Message is already saved, duplicated message", message.getPayload(), message.getId()));
        }
    }

    @Override
    public List<Message> readAll() {
        return messages;
    }
}
