package com.de.services;

import com.de.model.Message;

import java.util.List;

public interface MessageService {

    void addMessage(Message message);

    List<String> getMessages();
}
