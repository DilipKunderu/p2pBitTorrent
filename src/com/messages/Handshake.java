package com.messages;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
	
	 public void sendHandshakeMsg(BufferedOutputStream out) throws IOException {
	            byte[] handshakeMsg = MessageUtil.concatenateByteArrays(MessageUtil
	                    .concatenateByteArrays(this.header.getBytes(),
	                            this.zero_bits.getBytes()), 
	                    MessageUtil.intToByteArray(this.peer_ID));
	           out.write(handshakeMsg);
	           out.flush();
	    }
	 
	 public boolean recieveHandshake(BufferedInputStream in) throws IOException{
		 byte[] b = new byte[32];
         in.read(b);
         byte[] copyOfRange = Arrays.copyOfRange(b, 28, 32);
         byte[] header = Arrays.copyOfRange(b, 0, 18);
         Integer peerId = Integer.parseInt(new String(copyOfRange));  
         String s = null;
             if ((s = new String(header)).equals(Constants.HANDSHAKEHEADER) && Peer.getPeerInstance().getPeersToExpectConnectionsFrom().containsKey(peerId)) {
            	 return true;
             } else {
            	 return false;
             }
	 }
	 
}
