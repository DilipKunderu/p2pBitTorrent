package com.messages;

public abstract class Message {
    private  byte[] message_length;
    private  byte message_type;
    protected byte[] messagePayload;

    public Message(byte message_type) {
        this.message_type = message_type;
        this.message_length = MessageUtil.intToByteArray(1);
        this.messagePayload = null;
    }
    
    public Message(byte message_type,byte[] messagePayload) {
        this.message_type = message_type;
        this.messagePayload = messagePayload;
        this.message_length = MessageUtil.intToByteArray(messagePayload.length);

    }

  /*  private byte[] constructMessage () {
        byte[] bytes = new byte[5];
        byte[] temp = toBytes(message_length);

        System.arraycopy(temp, 0, bytes, 0, 4);

        bytes[4] = message_type;

        return bytes;
    }

    private byte[] constructMessage (MessagePayload message_payload) {
        message_length = 1 + computeLength (message_payload);

        byte[] bytes = new byte[4 + message_length];
        byte[] temp = toBytes(message_length);

        System.arraycopy(temp, 0, bytes, 0, 4);

        bytes[4] = message_type;

        byte[] msg = message_payload.getPayload();

        System.arraycopy(msg, 5, bytes, 5, bytes.length - 5);
        return bytes;
    }*/   
}
