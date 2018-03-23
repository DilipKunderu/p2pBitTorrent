package messages;

/**
 * Author: @DilipKunderu
 */
public class NotInterested extends Message {
    public NotInterested(byte message_type) {
        super(message_type);
        MessagePayload m = new MessagePayload();
        this.messagePayload = m.getPayload();
    }
}
