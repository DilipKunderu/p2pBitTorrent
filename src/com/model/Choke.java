package com.model;

import com.messages.Message;
import com.messages.MessagePayload;

/**
 * Author: @DilipKunderu
 */
public class Choke extends Message {
    public Choke() {
        super((byte) 0);
//        MessagePayload m = new MessagePayload();
//        this.messagePayload = m.getPayload();
    }
}
