package com.de.controllers;

import com.de.model.Message;
import com.de.services.MessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Objects;

@RestController
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = Objects.requireNonNull(messageService);
    }

    @PostMapping("/messages")
    public Mono<Void> addMessage(@RequestBody Message message) {
        return messageService.addMessage(message);
    }

    @GetMapping("/messages")
    public Mono<Collection<Message>> getMessages() {
        return messageService.getMessages();
    }
}
