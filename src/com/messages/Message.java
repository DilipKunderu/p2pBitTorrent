package com.messages;

public abstract class Message {
    protected static int message_length;
    protected static byte message_type;
    protected byte[] messagePayload;

    public Message(byte message_type) {
        this.message_type = message_type;
    }

    private byte[] constructMessage () {
        message_length = 1;
        byte[] bytes = new byte[5];
        byte[] temp = toBytes(message_length);
        for (int i = 0; i < 4; i++) {
            bytes[i] = temp [i];
        }
        bytes[4] = this.message_type;

        return bytes;
    }

    private byte[] constructMessage (MessagePayload message_payload) {
        message_length = 1 + computeLength (message_payload);

        byte[] bytes = new byte[4 + message_length];
        byte[] temp = toBytes(message_length);
        for (int i = 0; i < 4; i++) {
            bytes[i] = temp [i];
        }

        bytes[4] = this.message_type;

        byte[] msg = message_payload.getPayload();

        for (int i = 5; i < bytes.length; i++) {
            bytes[i] = msg[i];
        }
        return bytes;
    }

    private int computeLength(MessagePayload message_payload) {
        return 1;
    }

    private byte[] toBytes(int i)
    {
        byte[] result = new byte[4];

        result[0] = (byte) (i >> 24);
        result[1] = (byte) (i >> 16);
        result[2] = (byte) (i >> 8);
        result[3] = (byte) (i /*>> 0*/);

        return result;
    }
}
