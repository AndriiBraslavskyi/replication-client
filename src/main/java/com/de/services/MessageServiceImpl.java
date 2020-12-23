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
    private static final int RANDOM_DELAY_UPPER_BOUND = 10000;

    private final MessageRepository messageRepository;
    private final int delay;
    private final boolean saveOnCancel;

    public MessageServiceImpl(MessageRepository messageRepository, Integer delay, boolean saveOnCancel) {
        this.messageRepository = Objects.requireNonNull(messageRepository);
        this.delay = Objects.requireNonNull(delay);
        this.saveOnCancel = saveOnCancel;
    }

    public Mono<Message> addMessage(Message message) {
        final int resolvedDelay = resolveDelay(delay);
        return Mono.just(message)
                .map(this::validateMessage)
                .doOnNext(unused -> logger.info("Message will be saved with delay {}", resolvedDelay))
                .delayElement(Duration.ofMillis(resolvedDelay))
                .map(this::saveMessage)
                .doOnCancel(() -> onSubscriptionCanceled(message));
    }

    private Message saveMessage(Message message) {
        messageRepository.persistMessage(message);
        logger.info("Message = {} was saved", message);
        return message;
    }

    private void onSubscriptionCanceled(Message message) {
        if (saveOnCancel) {
            logger.info("Message {} saved after client disconnected by timeout", message);
            messageRepository.persistMessage(message);
        } else {
            logger.info("Message was not saved after client disconnected by timeout");
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
    private static int resolveDelay(int delay) {
        return delay < 0 ? ThreadLocalRandom.current().nextInt(RANDOM_DELAY_UPPER_BOUND) : delay;
    }
}
