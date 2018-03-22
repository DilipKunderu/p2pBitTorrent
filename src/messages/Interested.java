package messages;

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
