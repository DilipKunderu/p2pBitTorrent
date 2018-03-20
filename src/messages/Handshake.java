package messages;

import java.util.Arrays;

public class Handshake {
	private String header;
	private byte[] zero_bits;
	private int peer_ID;
	
	public Handshake(int peer_ID) {
		setHeader("P2PFILESHARINGPROJ");
		byte[] bytes = new byte[10];
		Arrays.fill( bytes, (byte) 1 );
		setZero_bits(bytes);
		this.peer_ID = peer_ID;
	}
	
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public byte[] getZero_bits() {
		return zero_bits;
	}
	public void setZero_bits(byte[] zero_bits) {
		this.zero_bits = zero_bits;
	}
	public int getPeer_ID() {
		return peer_ID;
	}
	public void setPeer_ID(int peer_ID) {
		this.peer_ID = peer_ID;
	}
}
