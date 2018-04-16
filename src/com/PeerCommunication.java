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

            this.handshake = new Handshake(this.remote.get_peerID());
        } catch (IOException e) {
            throw new RuntimeException("Could not open client socket", e);
        }
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
//            throw new RuntimeException("Unable to read in and out in Handshake validator", e);
//        }
//        return res;
//    }
}
