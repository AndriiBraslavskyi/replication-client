package com.de.services;

import com.de.model.Message;
import com.de.repositories.MessageRepository;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class MessageServiceImpl implements MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);
    private final MessageRepository messageRepository;
    private final int delay;
    private final boolean saveOnCancel;
    private final int maxRandomTimeout;

    public MessageServiceImpl(MessageRepository messageRepository,
                              Integer delay,
                              boolean saveOnCancel,
                              Integer maxRandomTimeout) {
        this.messageRepository = Objects.requireNonNull(messageRepository);
        this.delay = Objects.requireNonNull(delay);
        this.saveOnCancel = saveOnCancel;
        this.maxRandomTimeout = Objects.requireNonNull(maxRandomTimeout);
    }

    public Mono<Void> addMessage(Message message) {
        final int resolvedDelay = resolveDelay(delay);
        return Mono.just(message)
                .map(this::validateMessage)
                .doOnNext(unused -> logger.info("Message {} will be saved with delay {}", message, resolvedDelay))
                .delayElement(Duration.ofMillis(resolvedDelay))
                .flatMap(this::saveMessage)
                .doOnCancel(() -> onSubscriptionCanceled(message));
    }

    private Mono<Void> saveMessage(Message message) {
        logger.info("Message = {} was saved", message);
        messageRepository.persistMessage(message);
        return Mono.empty();
    }

    private void onSubscriptionCanceled(Message message) {
        if (saveOnCancel) {
            logger.info("Message {} saved after client disconnected by timeout", message);
            messageRepository.persistMessage(message);
        } else {
            logger.info("Message {} was not saved after client disconnected by timeout", message);
        }
    }

    private Message validateMessage(Message message) {
        if (message.getId() == null || message.getPayload() == null) {
            throw new IllegalArgumentException("Validation for message {} failed. Missing required `id` or `payload`"
                    + " field.");
        }
        return message;
    }

    public Mono<Collection<Message>> getMessages() {
        return messageRepository.readAll();
    }

    @SneakyThrows
    private  int resolveDelay(int delay) {
        return delay < 0 ? ThreadLocalRandom.current().nextInt(maxRandomTimeout) : delay;
    }
}
