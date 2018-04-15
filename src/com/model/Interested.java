package com.model;


import com.messages.Message;
import com.messages.MessagePayload;

/**
 * Author: @DilipKunderu
 */
public class Interested extends Message {
    public Interested() {
        super((byte) 2);
//        MessagePayload m = new MessagePayload();
//        this.messagePayload = m.getPayload();
    }
}
