package messages;

/**
 * Author: @DilipKunderu
 */
public class Unchoke extends Message {
    public Unchoke(byte message_type) {
        super(message_type);
        MessagePayload m = new MessagePayload();
        this.messagePayload = m.getPayload();
    }
}
