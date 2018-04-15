package com.model;

import com.messages.Message;
import com.messages.MessagePayload;

/**
 * Author: @DilipKunderu
 */
public class Unchoke extends Message {
    public Unchoke(byte message_type) {
        super(message_type);
        MessagePayload m = new MessagePayload();
        this.messagePayload = m.getPayload();
    }
}
