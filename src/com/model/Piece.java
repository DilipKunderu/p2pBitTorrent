package com.model;

import com.messages.Message;

/**
 * Author: @DilipKunderu
 */
public class Piece extends Message {
    private byte[] piece_index;
    private byte[] file_part;

    public byte[] getPiece_index() {
        return piece_index;
    }

    public void setPiece_index(byte[] piece_index) {
        this.piece_index = piece_index;
    }

    public Piece(byte[] piece_index, byte[] file_part) {
        super((byte) 7);
        this.piece_index = piece_index;
        this.file_part = file_part;
    }


}
