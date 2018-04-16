package com;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

/**
 * Author: @DilipKunderu
 */
public class IncomingRequestsHandler implements Runnable {
    private Socket clientSocket;
    private int clientID;

    IncomingRequestsHandler(Socket clientSocket, int clientID) {
        this.clientSocket = clientSocket;
        this.clientID = clientID;
    }

    @Override
    public void run() {
        try {
            BufferedOutputStream output = new BufferedOutputStream(clientSocket.getOutputStream());
            output.flush();
            BufferedInputStream input = new BufferedInputStream(clientSocket.getInputStream());

            createDirectory(this.clientID);

            output.write(("Client is communicating").getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createDirectory(int _peerID) {
        File dir = new File(Constants.DEST_FILE + "/peer_" + _peerID);
        boolean success = false;
        try {
            success = dir.mkdir();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (success) {
            File file = new File(Constants.DEST_FILE + "/peer_" + _peerID + "/file.dat");
        } else {
            //Log failure to create corresponding directory
        }
    }
}
