package com;

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
        PeerCommunication peerCommunication = new PeerCommunication(this.remotePeerInfo);
    }
}
