package com.messages;

public abstract class Message {
    private static int message_length;
    private static byte message_type;
    protected byte[] messagePayload;

    public Message(byte message_type) {
        Message.message_type = message_type;
    }

    private byte[] constructMessage () {
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
    }

    //TODO
    private int computeLength(MessagePayload message_payload) {
        return 1;
    }

    private byte[] toBytes(int i) {
        byte[] result = new byte[4];

        result[0] = (byte) (i >> 24);
        result[1] = (byte) (i >> 16);
        result[2] = (byte) (i >> 8);
        result[3] = (byte) (i /*>> 0*/);

        return result;
    }
}
