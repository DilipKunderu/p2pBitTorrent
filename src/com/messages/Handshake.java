package com.messages;

import com.Constants;

import java.util.Arrays;

public class Handshake {
	private String header;
	private byte[] zero_bits;
	private int peer_ID;
	
	public Handshake(int peer_ID) {
		setHeader("P2PFILESHARINGPROJ");
		byte[] bytes = new byte[10];
		Arrays.fill( bytes, (byte) 0 );
//		setZero_bits(bytes);
		this.peer_ID = peer_ID;
	}
	
	public String getHeader() {
		return header;
	}
	private void setHeader(String header) {
		this.header = header;
	}
//	public byte[] getZero_bits() {
//		return zero_bits;
//	}
//	private void setZero_bits(byte[] zero_bits) {
//		this.zero_bits = zero_bits;
//	}
	public int getPeer_ID() {
		return peer_ID;
	}
	public void setPeer_ID(int peer_ID) {
		this.peer_ID = peer_ID;
	}

	@Override
	public String toString() {
		return Constants.HANDSHAKEHEADER + header + "='" + header + "\'" +
				", zero_bits=" + Arrays.toString(zero_bits) +
				", peer_ID=" + peer_ID +
				'}';
	}
}
