package com;

import com.messages.Handshake;
import com.messages.Message;
import com.messages.MessageHandler;
import com.messages.MessageUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

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

    
    public void startMessageExachane() throws Exception{
    	if(!Peer.getPeerInstance().getBitSet().isEmpty()){
    		PeerCommunicationHelper.sendBitSetMsg(this.out);
    	}
    }
    
}
