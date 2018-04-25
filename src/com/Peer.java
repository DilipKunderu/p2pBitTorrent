package com;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class Peer {
	private static Peer peer;
	private volatile BitSet _bitField;

	private RemotePeerInfo OptimisticallyUnchokedNeighbour;

	Map<Integer, RemotePeerInfo> peersToConnectTo;
	Map<Integer, RemotePeerInfo> peersToExpectConnectionsFrom;
	List<RemotePeerInfo> connectedPeers;
	volatile Map<RemotePeerInfo, BitSet> preferredNeighbours;
	Map<Integer, RemotePeerInfo> peersInterested;

	/**
	 * This map will be used to index running threads of the peer
	 */

	private int _peerID;
	private String _hostName;
	private int _port;
	private int _hasFile;
	private int _pieceCount;
//	public int handShakeCount;

	/**
	 * This Bitset is used for terminating conditions
	 */
	public BitSet idealBitset;

	public RemotePeerInfo getOptimisticallyUnchokedNeighbour() {
		return OptimisticallyUnchokedNeighbour;
	}

	public Map<Integer, RemotePeerInfo> getPeersToConnectTo() {
		return peersToConnectTo;
	}

	public Map<Integer, RemotePeerInfo> getPeersToExpectConnectionsFrom() {
		return peersToExpectConnectionsFrom;
	}

	public Map<Integer, RemotePeerInfo> getPeersInterested() {
		return peersInterested;
	}

	BitSet getBitSet() {
		return _bitField;
	}

	public int get_peerID() {
		return _peerID;
	}

	void set_peerID(int _peerID) {
		this._peerID = _peerID;
	}

	String get_hostName() {
		return _hostName;
	}

	int get_port() {
		return _port;
	}

	void set_hostName(String _hostName) {
		this._hostName = _hostName;
	}

	void set_port(int _port) {
		this._port = _port;
	}

	int get_hasFile() {
		return _hasFile;
	}

	public boolean get_bitField(int i) {
		return _bitField.get(i);
	}

	void set_hasFile(int _hasFile) {
		this._hasFile = _hasFile;
	}

	int get_pieceCount() {
		return _pieceCount;
	}

	private void set_bitField(int i) {
		this._bitField.set(i);
	}

	public void set_pieceCount() {
		int f = Constants.getFileSize();
		int p = Constants.getPieceSize();

		this._pieceCount = (int) Math.ceil((double) f / p);
	}

	public void setBitset() {
		for (int i = 0; i < get_pieceCount(); i++) {
			peer.set_bitField(i);
		}
	}

	private Peer() {
		this._bitField = new BitSet(this.get_pieceCount());
		this.peersToConnectTo = Collections.synchronizedMap(new LinkedHashMap<>());
		this.peersToExpectConnectionsFrom = Collections.synchronizedMap(new LinkedHashMap<>());
		this.connectedPeers = Collections.synchronizedList(new ArrayList<>());
		this.peersInterested = Collections.synchronizedMap(new HashMap<>());
		this.idealBitset = new BitSet(this.get_pieceCount());

		for (int i = 0; i < this.idealBitset.length(); i++)
			this.idealBitset.set(i);
	}

	public static Peer getPeerInstance() {
		if (peer == null) {
			synchronized (Peer.class) {
				if (peer == null)
					peer = new Peer();
			}
		}
		return peer;
	}

	public void sendHaveToAll(int receivedPieceIndex) {
		for (RemotePeerInfo remote : this.connectedPeers) {
			try {
				PeerCommunicationHelper.sendHaveMsg(remote.objectOutputStream, receivedPieceIndex);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/***************************************
	 * Timer Based Tasks
	 ************************************************************/

	void OptimisticallyUnchokedNeighbour() {
        RemotePeerInfo OptimisticNeighbour = setOptimisticallyUnchokedNeighbour();
		peerProcess.log.changeOfOptimisticallyUnchokedNeighbor(OptimisticNeighbour.get_peerID());
		TimerTask repeatedTask = new TimerTask() {
			@Override
			public void run() {
				setOptimisticallyUnchokedNeighbour();
			}
		};
		Timer opt_timer = new Timer();
		long delay = (long) Constants.getOptimisticUnchokingInterval() * 1000;
		long period = (long) Constants.getOptimisticUnchokingInterval() * 1000;
		opt_timer.scheduleAtFixedRate(repeatedTask, delay, period);
	}

	private RemotePeerInfo setOptimisticallyUnchokedNeighbour() {
		List<RemotePeerInfo> interestedPeers = new ArrayList<>(this.peersInterested.values());
		RemotePeerInfo optimisticPeer;

		if (interestedPeers.size() == 0) {

			optimisticPeer = this.connectedPeers.get(ThreadLocalRandom.current().nextInt(this.connectedPeers.size()));
		} else
			optimisticPeer = interestedPeers.get(ThreadLocalRandom.current().nextInt(interestedPeers.size()));

//		this.preferredNeighbours.put(optimisticPeer, optimisticPeer.getBitfield());
		interestedPeers.clear();
		return optimisticPeer;
	}

	void PreferredNeighbours() {
		preferredNeighbours = Collections.synchronizedMap(new HashMap<>());
		setPreferredNeighbours();

		TimerTask repeatedTask = new TimerTask() {
			@Override
			public void run() {
				setPreferredNeighbours();
			}
		};

		Timer pref_timer = new Timer();
		long delay = (long) Constants.getUnchokingInterval() * 1000;
		long period = (long) Constants.getUnchokingInterval() * 1000;
		pref_timer.scheduleAtFixedRate(repeatedTask, delay, period);
	}

	private synchronized void setPreferredNeighbours() {
		/**
		 * This list gets populated whenever there is a file transfer going on
		 * between the local peer and the corresponding remote peer. For that
		 * cycle, the state for the remote peer remains unchoked.
		 */
		List<RemotePeerInfo> remotePeerInfoList = new ArrayList<>(this.peersInterested.values());

		/**
		 * This queue is used to add remote peer objects into the preferred
		 * neighbours map, going by the associated download rate.
		 **/

		this.preferredNeighbours.clear();

		if (remotePeerInfoList.size() == 0) {
			int count = 0;

			for (RemotePeerInfo r : this.connectedPeers) {
				remotePeerInfoList.add(r);
			}

			while (remotePeerInfoList.size() != 0 && count < Constants.getNumberOfPreferredNeighbors()) {
				int index = ThreadLocalRandom.current().nextInt(remotePeerInfoList.size());
				RemotePeerInfo r = remotePeerInfoList.get(index);
				this.preferredNeighbours.put(r, r.getBitfield());
				remotePeerInfoList.remove(index);
				count++;
			}
		} else {
			if (this._hasFile != 1) {
				Queue<RemotePeerInfo> neighborsQueue = new PriorityBlockingQueue<>(
						Constants.getNumberOfPreferredNeighbors(),
						(o1, o2) -> Math.toIntExact(o1.getDownload_rate() - o2.getDownload_rate()));

				for (RemotePeerInfo _remote : remotePeerInfoList) {
					neighborsQueue.add(_remote);
					remotePeerInfoList.remove(_remote);
				}

				RemotePeerInfo remote;

				int count = 0;

				while (!neighborsQueue.isEmpty() && count < Constants.getNumberOfPreferredNeighbors()) {
					remote = neighborsQueue.poll();
					decider(remote);
					this.preferredNeighbours.put(remote, remote.getBitfield());
					count++;
				}

				while (!neighborsQueue.isEmpty()) {
					remote = neighborsQueue.poll();
					try {
						PeerCommunicationHelper.sendMessage(remote.objectOutputStream, MessageType.choke);
						remote.setState(MessageType.choke);
					} catch (Exception e) {
						throw new RuntimeException("Could not send choke message from the peer class", e);
					}
				}
			} else {
				int count = 0;
				while (remotePeerInfoList.size() != 0 && count < Constants.getNumberOfPreferredNeighbors()) {
					int randIndex = ThreadLocalRandom.current().nextInt(remotePeerInfoList.size());
					RemotePeerInfo r = remotePeerInfoList.get(randIndex);
					decider(r);
					this.preferredNeighbours.put(r, r.getBitfield());
					remotePeerInfoList.remove(randIndex);
					count++;
				}

				while (remotePeerInfoList.size() != 0) {
					RemotePeerInfo r = remotePeerInfoList.get(0);
					try {
						PeerCommunicationHelper.sendMessage(r.objectOutputStream, MessageType.choke);
						r.setState(MessageType.choke);
						remotePeerInfoList.remove(0);
					} catch (Exception e) {
						throw new RuntimeException("Could not send choke message from the peer class", e);
					}
				}
			}
		}

		if(!preferredNeighbours.isEmpty())
        peerProcess.log.changeOfPreferredNeighbors(this.preferredNeighbours);
	}

	private void decider(RemotePeerInfo r) {
		if ((r != null ? r.getState() : null) == MessageType.choke) {
			try {
				PeerCommunicationHelper.sendMessage(r.objectOutputStream, MessageType.unchoke);
				r.setState(MessageType.unchoke);
			} catch (Exception e) {
				throw new RuntimeException("Could not send choke message from the peer class", e);
			}
		}
	}
}
