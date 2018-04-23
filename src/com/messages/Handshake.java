package com.messages;

import java.io.*;
import com.Constants;

public class Handshake implements Serializable {
	private static final long serialVersionUID = 6529685098267857690L;

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

	public void sendHandshakeMsg(ObjectOutputStream out, Handshake handshake) throws IOException {
		out.writeObject(handshake);
	}

	public boolean recieveHandshake(ObjectInputStream in) throws IOException, ClassNotFoundException {
		Handshake received = (Handshake) in.readObject();
		if (received.header.equals(Constants.HANDSHAKEHEADER)) {
			return true;
		} else {
			return false;
		}
	}
}
