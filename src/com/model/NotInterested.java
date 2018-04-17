package com.model;

import com.messages.Message;
import com.messages.MessagePayload;

/**
 * Author: @DilipKunderu
 */
public class NotInterested extends Message {
    public NotInterested() {
        super((byte) 3);
    }
}
