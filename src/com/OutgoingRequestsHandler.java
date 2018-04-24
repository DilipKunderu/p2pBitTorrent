package com;

public class OutgoingRequestsHandler implements Runnable {
	private RemotePeerInfo remotePeerInfo;

	OutgoingRequestsHandler(RemotePeerInfo remote) {
		this.remotePeerInfo = remote;
	}

	@Override
	public void run() {
		System.out.println("Thread from outgoing pool spawned");
		PeerCommunication peerCommunication = null;
		try {
			peerCommunication = new PeerCommunication(this.remotePeerInfo);
            peerProcess.log.TCPConnection(this.remotePeerInfo.get_peerID(), true);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			peerCommunication.startMessageExchange();
		} catch (Exception e) {
			throw new RuntimeException("Error starting message exchange in outgoing request handler for "
					+ this.remotePeerInfo.get_peerID(), e);
		}
		System.out.println("finished messaging with " + this.remotePeerInfo.get_peerID());
		return;
	}
}
