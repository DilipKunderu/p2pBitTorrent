package com.messages;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.Constants;
import com.Peer;

public class Handshake {
	private String header;
	private String zero_bits;
	private int peer_ID;
	
	public Handshake(int peer_ID) {
		this.header = Constants.HANDSHAKEHEADER;
		this.zero_bits = Constants.ZERO_BITS;
		this.peer_ID = peer_ID;
	}

	@Override
	public String toString() {
		return this.header + this.zero_bits + String.valueOf(this.peer_ID);
	}
	
	 public byte[] sendHandshakeMsg() {
	            byte[] handshakeMsg = MessageUtil.concatenateByteArrays(MessageUtil
	                    .concatenateByteArrays(this.header.getBytes(),
	                            this.zero_bits.getBytes()), 
	                    MessageUtil.intToByteArray(this.peer_ID));
	            return handshakeMsg;
	           
	    }
	 
	 
}
