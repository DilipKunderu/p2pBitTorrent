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
    int recentReceievdPiece;

    
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
}
