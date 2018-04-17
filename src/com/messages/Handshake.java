package com.messages;

import com.Constants;

public class Handshake {
	private String header;
	private String zero_bits;
	private int peer_ID;
	
	public Handshake(int peer_ID) {
		this.header = Constants.HANDSHAKEHEADER;
		this.zero_bits = "0000000000";
		this.peer_ID = peer_ID;
	}

	@Override
	public String toString() {
		return this.header + this.zero_bits + String.valueOf(this.peer_ID);
	}
}
