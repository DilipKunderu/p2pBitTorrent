package messages;

/**
 * Author: @DilipKunderu
 */
public class Have extends Message {
    private byte[] piece_index;

    public byte[] getPiece_index() {
        return piece_index;
    }

    public void setPiece_index(byte[] piece_index) {
        this.piece_index = piece_index;
    }

    public Have(byte message_type, byte[] piece_index) {
        super(message_type);
        this.piece_index = piece_index;
        MessagePayload m = new MessagePayload();
        this.messagePayload = m.getPayload();
    }
}
