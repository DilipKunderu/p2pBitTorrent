package com;

import com.messages.Handshake;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Author: @DilipKunderu
 */
public class OutgoingRequestsHandler implements Runnable {
    private RemotePeerInfo remotePeerInfo;

    OutgoingRequestsHandler(RemotePeerInfo remote) {
        this.remotePeerInfo = remote;
    }

    @Override
    public void run() {
        try {
            Socket initClientSocket = new Socket(remotePeerInfo.get_hostName(), remotePeerInfo.get_portNo());
            BufferedInputStream input = new BufferedInputStream(initClientSocket.getInputStream());
            BufferedOutputStream output = new BufferedOutputStream(initClientSocket.getOutputStream());
            output.flush();

            output.write(((new Handshake(remotePeerInfo.get_peerID())).toString()).getBytes());

        } catch (IOException e) {
            throw new RuntimeException("Unable to send TCP requests to  other nodes", e);
        }
    }
}
