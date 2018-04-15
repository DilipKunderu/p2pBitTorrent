package com.model;

import com.messages.Message;

import java.util.BitSet;

/**
 * Author: @DilipKunderu
 */
public class BitField extends Message {
    private BitSet bitField;

    public BitField(BitSet bitField) {
        super((byte) 5);
        this.bitField = bitField;
    }
}
