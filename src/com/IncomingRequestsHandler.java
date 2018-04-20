package com;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class IncomingRequestsHandler implements Runnable {
    private Socket clientSocket;
    private RemotePeerInfo remotePeerInfo;
    BufferedOutputStream out;
    BufferedInputStream in;

    IncomingRequestsHandler(Socket clientSocket, RemotePeerInfo remotePeerInfo) {
        this.clientSocket = clientSocket;
        this.remotePeerInfo = remotePeerInfo;
    }
    

    @Override
    public void run() {
        System.out.println("incoming request thread spawned");
        PeerCommunication peerCommunication = new PeerCommunication(remotePeerInfo);
        peerCommunication.log.TCPConnection(this.remotePeerInfo.get_peerID(), false);

        try {
            peerCommunication.startMessageExchange();
            this.in = new BufferedInputStream(this.clientSocket.getInputStream());
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


//        try {
//            BufferedOutputStream output = new BufferedOutputStream(clientSocket.getOutputStream());
//            output.flush();
//            BufferedInputStream input = new BufferedInputStream(clientSocket.getInputStream());
//
//            byte[] in = new byte[4];
//            int k = input.read(in);
//            System.out.println("incoming peer message : " + k);
//            output.write(("Client" + Peer.getPeerInstance().get_peerID() + " is sending message to the peer "
//            + k).getBytes());
//            output.flush();
//
//            input.close();
//            output.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
