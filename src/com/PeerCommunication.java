package com;

import com.FileProcessor.FileManagerExecutor;
import com.messages.Handshake;
import com.messages.Message;
import com.messages.MessageUtil;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.BitSet;

public class PeerCommunication {
	public ObjectOutputStream out;
	RemotePeerInfo remote;
	Socket socket;
	Handshake handshake;
	ObjectInputStream in;
	int recentReceievdPiece;
	Long downloadStart;
	Long downloadEnd;
	boolean flag;
	boolean terminateFlag = true;

	public PeerCommunication(RemotePeerInfo remotePeerInfo) throws ClassNotFoundException {
		this.remote = remotePeerInfo;
		this.socket = null;
		initSocket();
	}

	public PeerCommunication(RemotePeerInfo remotePeerInfo, Socket socket) throws ClassNotFoundException {
		this.remote = remotePeerInfo;
		this.socket = socket;
		initSocket();
	}

	private void initSocket() throws ClassNotFoundException {
		try {
			if (socket == null) {
				this.socket = new Socket(InetAddress.getByName(this.remote.get_hostName()), this.remote.get_portNo());
			}
			this.out = new ObjectOutputStream((this.socket.getOutputStream()));
			this.in = new ObjectInputStream((this.socket.getInputStream()));
			this.remote.objectOutputStream = this.out;
			this.out.flush();

			this.handshake = new Handshake(Peer.getPeerInstance().get_peerID());
			this.handshake.sendHandshakeMsg(this.out, this.handshake);
			if (this.handshake.recieveHandshake(this.in)) {
				// TODO logger
			} else {
				// TODO logger
			}
		} catch (IOException e) {
			throw new RuntimeException("Could not open client socket", e);
		}

	}

	public void startMessageExchange() throws Exception {
		byte[] pieceIndexField = null;
		if (!Peer.getPeerInstance().getBitSet().isEmpty()) {
			PeerCommunicationHelper.sendBitSetMsg(this.out);
		}
		while (terminateFlag) {
			Message message = PeerCommunicationHelper.getActualObjectMessage(this.in, this.remote);
			byte msgType = message.getMessage_type();
			byte[] msgPayloadReceived = message.getMessagePayload();
			if (this.flag && msgType != (byte) 7) {
				this.downloadStart = 0L;
			}
			if (msgType == (byte) 7 || msgType == (byte) 4) {
				pieceIndexField = new byte[4];
				for (int i = 0; i < 4; i++) {
					pieceIndexField[i] = msgPayloadReceived[i];
				}
			}

			switch (msgType) {
			case (byte) 0: {
				while (in.readObject() == null) {
				}
				break;
			}

			case (byte) 5: {
				BitSet bitset = MessageUtil.fromByteArray(msgPayloadReceived);
				this.remote.setBitfield(bitset);
				if (PeerCommunicationHelper.isInterseted(bitset, Peer.getPeerInstance().getBitSet())) {
					PeerCommunicationHelper.sendMessage(this.out, MessageType.interested);
					PeerCommunicationHelper.sendRequestMsg(this.out, this.remote);
				} else {
					PeerCommunicationHelper.sendMessage(this.out, MessageType.notinterested);
				}
				break;
			}

			case (byte) 2: {
				Peer.getPeerInstance().peersInterested.putIfAbsent(this.remote.get_peerID(), this.remote);
				break;
			}

			case (byte) 3: {
				if (this.remote.getBitfield().equals(Peer.getPeerInstance().idealBitset)) {
					terminateFlag = false;
				}
				if (Peer.getPeerInstance().peersInterested.containsKey(this.remote.get_peerID()))
					Peer.getPeerInstance().peersInterested.remove(this.remote.get_peerID());
				break;
			}

			case (byte) 4: {
				this.remote.getBitfield().set(MessageUtil.byteArrayToInt(msgPayloadReceived));
				if (!Peer.getPeerInstance().getBitSet().get(MessageUtil.byteArrayToInt(msgPayloadReceived))) {
					if (Peer.getPeerInstance().preferredNeighbours.containsKey(this.remote)
							|| Peer.getPeerInstance().getOptimisticallyUnchokedNeighbour() == this.remote)
						PeerCommunicationHelper.sendRequestMsg(this.out, this.remote);
				}
				break;

			}

			case (byte) 1: {
				int pieceIndex = PeerCommunicationHelper.getPieceIndex(this.remote);
				if (pieceIndex != -1) {
					PeerCommunicationHelper.sendRequestMsg(this.out, this.remote);
					this.downloadStart = System.nanoTime();
					this.flag = true;
				}
				if (pieceIndex == -1) {
					PeerCommunicationHelper.sendMessage(this.out, MessageType.notinterested);
				}
				break;
			}

			case (byte) 6: {
				if (Peer.getPeerInstance().preferredNeighbours.containsKey(this.remote)
						|| Peer.getPeerInstance().getOptimisticallyUnchokedNeighbour() == this.remote)
					PeerCommunicationHelper.sendPieceMsg(this.out, MessageUtil.byteArrayToInt(msgPayloadReceived));
				this.downloadEnd = System.nanoTime();
				break;
			}

			case (byte) 7: {
				FileManagerExecutor.acceptFilePart(MessageUtil.byteArrayToInt(pieceIndexField), message);
				if (!Peer.getPeerInstance().getBitSet().get(MessageUtil.byteArrayToInt(pieceIndexField))) {
					Peer.getPeerInstance().getBitSet().set(MessageUtil.byteArrayToInt(pieceIndexField));
					Peer.getPeerInstance().sendHaveToAll(MessageUtil.byteArrayToInt(pieceIndexField));
				}
				PeerCommunicationHelper.sendRequestMsg(this.out, this.remote);
				break;
			}
			}
			if (Peer.getPeerInstance().get_hasFile() != 1
					&& Peer.getPeerInstance().getBitSet().equals(Peer.getPeerInstance().idealBitset)) {
				FileManagerExecutor.filesmerge();
				peerProcess.log.completionOfDownload();
				terminateFlag = false;

			}
		}
	}
}
