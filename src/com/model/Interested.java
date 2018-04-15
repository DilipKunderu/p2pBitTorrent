package com.model;


import com.messages.Message;
import com.messages.MessagePayload;

/**
 * Author: @DilipKunderu
 */
public class Interested extends Message {
    public Interested(byte message_type) {
        super(message_type);
        MessagePayload m = new MessagePayload();
        this.messagePayload = m.getPayload();
    }
}
