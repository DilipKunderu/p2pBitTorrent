package com;

import com.logger.EventLogger;
import com.messages.Handshake;
import com.messages.Message;
import com.messages.MessageUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.BitSet;
import java.util.Map;

public class PeerCommunication {
    RemotePeerInfo remote;
    Socket socket;
    Handshake handshake;
    BufferedOutputStream out;
    BufferedInputStream in;
	EventLogger log = new EventLogger(Peer.getPeerInstance().get_peerID());

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
            	System.out.println("Recieved Handshake");
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
    		message = PeerCommunicationHelper.sendInterestedMsg(this.out);
    	}
    	else{
    		message = PeerCommunicationHelper.sendNotInterestedMsg(this.out);
    	}
    	if(PeerCommunicationHelper.checkRecievedMsg(this.in) == (byte)2){
    		Peer.getPeerInstance().peersInterested.put(this.remote.get_peerID(), this.remote);
    		
    	}
    	else{       //should this not be else if(PeerCommunicationHelper.checkRecievedMsg(this.in) == (byte)3) ?
    		if(Peer.getPeerInstance().peersInterested.containsKey(this.remote.get_peerID()))
    		Peer.getPeerInstance().peersInterested.remove(this.remote.get_peerID());
    	}



    }
    
    public void chokeOrUnchoke() throws Exception{
   
    	Message message = null;
    	for(Map.Entry<Integer,RemotePeerInfo> entry: Peer.getPeerInstance().peersInterested.entrySet()){
    		if(Peer.getPeerInstance().preferredNeighbours.containsKey(entry.getValue())){
    			PeerCommunicationHelper.sendUnChokeMsg(this.out);
    			int pieceIndex = MessageUtil.byteArrayToInt(PeerCommunicationHelper.getPieceIndex(this.remote));
    			if(pieceIndex!=-1){
    				message = PeerCommunicationHelper.sendRequestMsg(out,this.remote);
    			}
    			if(pieceIndex == 1){
    				message = PeerCommunicationHelper.sendNotInterestedMsg(this.out);    			
    				}
    			if(pieceIndex == -1 && !(Peer.getPeerInstance().getBitSet().equals(this.remote.getBitfield()))){
    				message = PeerCommunicationHelper.sendInterestedMsg(this.out);
    			}
    		}
    		else{
    			message = PeerCommunicationHelper.sendChokeMsg(this.out);
    		}
    	}


    }
    
    
}
