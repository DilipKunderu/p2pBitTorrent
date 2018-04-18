package com;

import com.messages.Handshake;

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

    PeerCommunication (RemotePeerInfo remotePeerInfo) {
        this.remote = remotePeerInfo;
        initSocket();
    }

    private void initSocket() {
        try{
            this.socket = new Socket (this.remote.get_hostName(), this.remote.get_portNo());
            this.out = new BufferedOutputStream(this.socket.getOutputStream());
            this.in = new BufferedInputStream(this.socket.getInputStream());
            this.handshake = new Handshake(this.remote.get_peerID());
        } catch (IOException e) {
            throw new RuntimeException("Could not open client socket", e);
        }
    }

    //Validate handshake
    //exchange bitfields
    //check what pieces remote peer has that local peer does not have and send interested. Only if it has all pieces that B has,
    //send not interested

    
}
