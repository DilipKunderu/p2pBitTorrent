package com;


import com.messages.Handshake;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

/**
 * Author: @DilipKunderu
 */
public class Requests implements Runnable {
    List<RemotePeerInfo> peersToConnectTo;

    Requests (List<RemotePeerInfo> peersToConnectTo) {
        this.peersToConnectTo = peersToConnectTo;
    }

    @Override
    public void run() {
        for (RemotePeerInfo remote : this.peersToConnectTo) {
            try {
                Socket clientSocket = new Socket(remote.get_hostName(), remote.get_portNo());
                BufferedInputStream input = new BufferedInputStream(clientSocket.getInputStream());
                BufferedOutputStream output = new BufferedOutputStream(clientSocket.getOutputStream());

                //Write Handshake message
                output.write((new Handshake(remote.get_peerID()).toString()).getBytes());

            } catch (IOException e) {
                throw new RuntimeException("could not send TCP connection request", e);
            }
        }
    }
}
