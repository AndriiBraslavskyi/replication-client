package com.de.services;

import com.de.model.Message;
import reactor.core.publisher.Mono;

import java.util.Collection;

public interface MessageService {

    Mono<Message> addMessage(Message message);

    Mono<Collection<Message>> getMessages();
}
