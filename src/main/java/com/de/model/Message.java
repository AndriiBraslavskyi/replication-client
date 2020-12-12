package com.de.model;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Message {
    String payload;

    Long id;
}
