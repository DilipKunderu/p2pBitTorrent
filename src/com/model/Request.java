package com.model;

import com.messages.Message;

/**
 * Author: @DilipKunderu
 */
public class Request extends Message {
    private byte[] pieceIndex;

    Request(byte[] pieceIndex) {
        super((byte) 7);
        this.pieceIndex = pieceIndex;
    }



}
