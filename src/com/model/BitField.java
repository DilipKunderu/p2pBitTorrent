package com.model;

import com.messages.Message;

import java.io.Serializable;

/**
 * Author: @DilipKunderu
 */
public class BitField extends Message implements Serializable {
    private byte[] bitField;

    public BitField(byte[] bitField) {
        super((byte) 5, bitField);
        this.bitField = bitField;
    }
}
