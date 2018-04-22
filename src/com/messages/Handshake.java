package com.messages;

import java.io.*;
import java.util.Arrays;

import com.Constants;
import com.Peer;
import com.RemotePeerInfo;

public class Handshake implements Serializable {
	private String header;
	private String zero_bits;
	private int peer_ID;
	private RemotePeerInfo remotePeerInfo;

	public Handshake(int peer_ID, RemotePeerInfo remotePeerInfo) {
		this.header = Constants.HANDSHAKEHEADER;
		this.zero_bits = Constants.ZERO_BITS;
		this.peer_ID = peer_ID;
		this.remotePeerInfo = remotePeerInfo;
	}

	@Override
	public String toString() {
		return this.header + this.zero_bits + String.valueOf(this.peer_ID);
	}
	
	 public void sendHandshakeMsg(ObjectOutputStream out) throws IOException {
		 
		 Handshake sent = new Handshake(this.peer_ID, this.remotePeerInfo);
		 out.writeObject(sent);
	           /* byte[] handshakeMsg = MessageUtil.concatenateByteArrays(MessageUtil
	                    .concatenateByteArrays(this.header.getBytes(),
	                            this.zero_bits.getBytes()), 
	                    MessageUtil.intToByteArray(this.peer_ID));
	           out.write(handshakeMsg);
	           out.flush();*/
	    }
	 
	 public boolean recieveHandshake(ObjectInputStream in) throws IOException, ClassNotFoundException{
			Handshake received = (Handshake) in.readObject();
			if (received.header.equals(Constants.HANDSHAKEHEADER) && (this.remotePeerInfo.get_peerID() == received.peer_ID)) {
           	 return true;
            } else {
           	 return false;
            }
	 }
}
		 /*byte[] b = new byte[32];
         in.read(b);
         byte[] copyOfRange = Arrays.copyOfRange(b, 28, 32);
         byte[] header = Arrays.copyOfRange(b, 0, 18);
         int peerId = MessageUtil.byteArrayToInt(copyOfRange);
         String s = new String(header);
             if (s.equals(Constants.HANDSHAKEHEADER) && (this.remotePeerInfo.get_peerID() == peerId)) {
            	 return true;
             } else {
            	 return false;
             }
	 }*/
	 

