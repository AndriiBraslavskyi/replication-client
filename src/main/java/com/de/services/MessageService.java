package com.de.services;

import com.de.model.Message;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface MessageService {

    void addMessage(Message message);

    Mono<Set<Message>> getMessages();
}
