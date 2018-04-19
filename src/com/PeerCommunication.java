package com;

import com.messages.Handshake;
import com.messages.Message;
import com.messages.MessageHandler;
import com.messages.MessageUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.BitSet;
import java.util.Map;

/**
 * Author: @DilipKunderu
 */
public class PeerCommunication {
    RemotePeerInfo remote;
    Socket socket;
    Handshake handshake;
    BufferedOutputStream out;
    BufferedInputStream in;

   public PeerCommunication (RemotePeerInfo remotePeerInfo) {
        this.remote = remotePeerInfo;
        initSocket();
    }

    private void initSocket() {
        try{
            this.socket = new Socket (this.remote.get_hostName(), this.remote.get_portNo());
            this.out = new BufferedOutputStream(this.socket.getOutputStream());
            this.in = new BufferedInputStream(this.socket.getInputStream());
            this.handshake = new Handshake(this.remote.get_peerID());

            this.handshake.sendHandshakeMsg(this.out);
            if(this.handshake.recieveHandshake(this.in)){
            	//TODO logger
            }
            else{
            	//TODO logger
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not open client socket", e);
        }
    }

    
    public void startMessageExchange () throws Exception {
        Message message = null;
    	if(!Peer.getPeerInstance().getBitSet().isEmpty()){
    		message = PeerCommunicationHelper.sendBitSetMsg(this.out);
    	}
    	
    	byte[] b = PeerCommunicationHelper.readActualMessage(this.in);
    	BitSet bitset = MessageUtil.fromByteArraytoBitSet(b);
    	if(PeerCommunicationHelper.isInterseted(bitset,Peer.getPeerInstance().getBitSet())){
    		PeerCommunicationHelper.sendInterestedMsg(this.out);
    	}
    	else{
    		 PeerCommunicationHelper.sendNotInterestedMsg(this.out);
    	}
    	if(PeerCommunicationHelper.checkRecievedMsg(this.in) == (byte)2){
    		Peer.getPeerInstance().peersInterested.put(this.remote.get_peerID(), this.remote);
    		
    	}
    	else{
    		if(Peer.getPeerInstance().peersInterested.containsKey(this.remote.get_peerID()))
    		Peer.getPeerInstance().peersInterested.remove(this.remote.get_peerID());
    	}

    }
    
    public void chokeOrUnchoke(){
   
    	Message message = null;
    	for(Map.Entry<Integer,RemotePeerInfo> entry: Peer.getPeerInstance().peersInterested.entrySet()){
    		if(preferredNeighbours.containsKey(entry.getKey()){
    			PeerCommunicationHelper.sendChokeMsg(this.out);
    		}
    		else{
    			PeerCommunicationHelper.sendUnChokeMsg(this.out);
    		}
    	}
    }
    
}
