package com;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Author: @DilipKunderu
 */
public class IncomingRequestsHandler implements Runnable {
    private Socket clientSocket;
    private RemotePeerInfo remotePeerInfo;

    IncomingRequestsHandler(Socket clientSocket, RemotePeerInfo remotePeerInfo) {
        this.clientSocket = clientSocket;
        this.remotePeerInfo = remotePeerInfo;
    }
    

    @Override
    public void run() {
        long threadID = Thread.currentThread().getId();
        Peer.getPeerInstance();
        System.out.println("incoming request thread spawned");
        PeerCommunication peerCommunication = new PeerCommunication(remotePeerInfo);
        
        try {
            BufferedOutputStream output = new BufferedOutputStream(clientSocket.getOutputStream());
            output.flush();
            BufferedInputStream input = new BufferedInputStream(clientSocket.getInputStream());

            byte[] in = new byte[4];
            int k = input.read(in);
            System.out.println("incoming peer message : " + k);
            output.write(("Client" + Peer.getPeerInstance().get_peerID() + " is sending message to the peer "
            + k).getBytes());
            output.flush();

            input.close();
            output.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
