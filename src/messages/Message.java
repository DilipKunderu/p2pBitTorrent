package messages;

import  messages.MessageType;
import  messages.MessagePayload;

public class Message {
    private int message_length;
    private byte message_type;
    private MessagePayload message_payload;

    public Message(byte type) throws InvalidMessageTypeException{
        this.message_type = type;
        message_payload = new MessagePayload(type);
        message_length = (message_payload == null)? 0 : message_payload.payload.length;
    }
}
