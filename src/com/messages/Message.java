package com.messages;

import java.io.Serializable;
import java.util.Arrays;

public class Message implements Serializable {
    private static final long serialVersionUID = 6529685098267757690L;

    private  byte[] message_length;
    private  byte message_type;
	private byte[] messagePayload;

    public byte[] getMessage_length() {
		return message_length;
	}

	public byte getMessage_type() {
		return message_type;
	}

	public byte[] getMessagePayload() {
		return messagePayload;
	}

	public Message () {
    }


    public Message(byte message_type) {
        this.message_type = message_type;
        this.message_length = MessageUtil.intToByteArray(1);
        this.messagePayload = null;
    }
    
    public Message(byte message_type, byte[] messagePayload) {
        this.message_type = message_type;
        this.messagePayload = messagePayload;
        this.message_length = MessageUtil.intToByteArray(messagePayload.length + 1);

    }

    @Override
    public String toString() {
        return "Message{" +
                "message_length=" + Arrays.toString(message_length) +
                ", message_type=" + message_type +
                ", messagePayload=" + Arrays.toString(messagePayload) +
                '}';
    }
}
