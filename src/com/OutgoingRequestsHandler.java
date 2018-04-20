package com;

import com.messages.Handshake;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class OutgoingRequestsHandler implements Runnable {
    private RemotePeerInfo remotePeerInfo;
    Socket socket;
    BufferedOutputStream out;
    BufferedInputStream in;

    OutgoingRequestsHandler(RemotePeerInfo remote) {
        this.remotePeerInfo = remote;
    }

    @Override
    public void run() {
        System.out.println("Thread from outgoing pool spawned");
        PeerCommunication peerCommunication = new PeerCommunication(this.remotePeerInfo);
        peerCommunication.log.TCPConnection(this.remotePeerInfo.get_peerID(), true);
        try {
            peerCommunication.startMessageExchange();
            this.in = new BufferedInputStream(this.socket.getInputStream());
            byte[] MsgtillLength = new byte[5];
            int MsgType = in.read(MsgtillLength,4,1);
            while(!peerProcess.isCompleted()) {
                switch (MsgType) {
                    //choke
                    case 0: {
                        peerCommunication.chokeOrUnchoke();
                        break;
                    }
                    //unchoke
                    case 1: {
                        peerCommunication.chokeOrUnchoke();
                        break;
                    }
                    //interested
                    case 2: {
                        Peer.getPeerInstance().peersInterested.put(this.remotePeerInfo.get_peerID(), this.remotePeerInfo);
                        break;
                    }
                    //not interested
                    case 3: {
                        if(Peer.getPeerInstance().peersInterested.containsKey(this.remotePeerInfo.get_peerID()))
                            Peer.getPeerInstance().peersInterested.remove(this.remotePeerInfo.get_peerID());
                        break;
                    }
                    //have
                    case 4: {
                        //update the bitfield.
                        //check if you need the piece and send the interested or not interested message.
                        //if sending interested message, send the request message.
                        break;
                    }
                    /* handled in startMessageExchange() and later have will take care of it
                    //bitfield
                    case 5:{
                        break;
                    }*/
                    //request
                    case 6: {
                        //if unchocked then
                        //send the requested piece.
                        //else
                        //ignore
                        break;
                    }
                    //piece
                    case 7: {
                        //download the arrived piece.
                        //update the bitset.
                        //send have message to the preferred neighbour list
                        break;
                    }
                    default:{
                        //invalid message type
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

//        try{
//            this.socket = new Socket(InetAddress.getByName(this.remotePeerInfo.get_hostName()), this.remotePeerInfo.get_portNo());
//            this.out = new BufferedOutputStream(this.socket.getOutputStream());
//            out.flush();
//            this.in = new BufferedInputStream(this.socket.getInputStream());
//
//            out.write(String.valueOf(this.remotePeerInfo.get_peerID()).getBytes());
//            out.flush();
//            int input = in.read(new byte[1024]);
//            System.out.println(String.valueOf(input));
//
//            in.close();
//            out.close();
//        } catch (IOException e) {
//            throw new RuntimeException("Could not open client socket", e);
//        }
    }
}
