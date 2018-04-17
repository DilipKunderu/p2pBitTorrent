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

    //writing for case where current peer receives a handshake request first
    private boolean handShakeValidator () {
        boolean res = false;

        byte[] data = new byte[1024];

        byte[] outgoingHandshake = (this.handshake.toString()).getBytes();
        byte[] incomingHandshake = new byte[32];

        for (int i = 0; i < 32; i++) {
            try {
                int input = in.read(data);
                incomingHandshake[i] = (byte) input;
            } catch (IOException e) {
                throw new RuntimeException("Failed to read the input stream from " + remote.get_hostName(), e);
            }
        }

        for (int i = 0; i < 18; i++) {
            if (outgoingHandshake[i] != incomingHandshake[i]) {
                    System.out.println("Mismatch in Handshake Header");
                    break;
                }
        }

//        if (this.remote.get_peerID() )
        return res;
    }

//    public boolean handShakeValidator () {
//        boolean res = false;
//        try {
//            byte[] data = new byte[1024];
//
//            int input;
//            byte[] outgoingHandshake = (handshake.toString()).getBytes();
//
//            byte[] incomingHandshake = new byte[32];
//
//            for (int i = 0; i < 32; i++) {
//                input = in.read(data);
//                incomingHandshake[i] = (byte) input;
//            }
//
//            for (int i = 0; i < 18; i++) {
//                if (outgoingHandshake[i] != incomingHandshake[i]) {
//                    System.out.println("Mismatch in Handshake Header");
//                    break;
//                }
//            }
//
//            //How to check if the peerID is of the expected value
//            if (!(peerID < Peer.getPeerInstance().get_peerID())) {
//                System.out.println("Tried to connect to unexpected Peer");
//                throw new RuntimeException("Unexpected Peer connection");
//            }
//            res = true;
//        } catch (IOException e) {
//            throw new RuntimeException("Error in handshake validator for " + remote.get_hostName(), e);
//        }
//        return res;
//    }
}
