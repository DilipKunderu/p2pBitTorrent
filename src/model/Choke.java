package messages;

/**
 * Author: @DilipKunderu
 */
public class Choke extends Message {
    public Choke(byte message_type) {
        super(message_type);
        MessagePayload m = new MessagePayload();
        this.messagePayload = m.getPayload();
    }
}
