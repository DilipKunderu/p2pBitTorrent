package messages;

import java.util.BitSet;

/**
 * Author: @DilipKunderu
 */
public class BitField extends Message {
    private BitSet bitField;

    public BitField(byte message_type, BitSet bitField) {
        super(message_type);
        this.bitField = bitField;
    }
}
