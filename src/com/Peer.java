package com;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Peer {
    private Timer pref_timer, opt_timer;
    private static Peer peer;
    private volatile BitSet _bitField;

    private volatile RemotePeerInfo optimisticallyUnchokedNeighbour;  //timer based

    Map<Integer, RemotePeerInfo> peersToConnectTo; //set in peerProcess and then not changed
    Map<Integer, RemotePeerInfo> peersToExpectConnectionsFrom;  //set in peerProcess and then not changed

    volatile List<RemotePeerInfo> connectedPeers; //set right after heach handshake
//    List<RemotePeerInfo> connectedPeersAux;

    volatile List<RemotePeerInfo> chokedPeers;   //just a reference, being manipulated in this class
    volatile List<RemotePeerInfo> unchokedPeers;

    volatile Map<RemotePeerInfo, BitSet> preferredNeighbours; //timer based
    volatile Map<Integer, RemotePeerInfo> peersInterested; //set by threads

    /**
     * This map will be used to index running threads of the peer
     */

    private int _peerID;
    private String _hostName;
    private int _port;
    public int _hasFile;
    private int _pieceCount;

//    public int handShakeCount;

    /**
     * This Bitset is used for terminating conditions
     */
    BitSet idealBitset;

    public RemotePeerInfo getOptimisticallyUnchokedNeighbour() {
        return this.optimisticallyUnchokedNeighbour;
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
//        this.connectedPeersAux = Collections.synchronizedList(new ArrayList<>());
        this.chokedPeers = Collections.synchronizedList(new ArrayList<>());
        this.unchokedPeers = Collections.synchronizedList(new ArrayList<>());
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
//	    this.optimisticallyUnchokedNeighbour = this.connectedPeers.get(ThreadLocalRandom.current().nextInt(this.connectedPeers.size()));

        TimerTask repeatedTask = new TimerTask() {
            @Override
            public void run() {
                if (!Peer.getPeerInstance().checkKill()) {
                    setOptimisticallyUnchokedNeighbour();
                } else {
                    Peer.getPeerInstance().opt_timer.cancel();
                    Peer.getPeerInstance().opt_timer.purge();
                    System.exit(0);
                }
            }
        };

        this.opt_timer = new Timer();
        long delay = (long) Constants.getOptimisticUnchokingInterval() * 1000;
        long period = (long) Constants.getOptimisticUnchokingInterval() * 1000;
        this.opt_timer.scheduleAtFixedRate(repeatedTask, delay, period);
    }

    private synchronized void setOptimisticallyUnchokedNeighbour() {
        List<RemotePeerInfo> interestedPeers = new ArrayList<>(this.chokedPeers);
        RemotePeerInfo optimisticPeer;

        if (interestedPeers.size() != 0) {
            optimisticPeer = interestedPeers.get(ThreadLocalRandom.current().nextInt(interestedPeers.size()));
//			System.out.println("There are no choked Peers currently");
//			optimisticPeer = this.connectedPeers.get(ThreadLocalRandom.current().nextInt(this.connectedPeers.size()));

            this.chokedPeers.remove(optimisticPeer);
            this.unchokedPeers.add(optimisticPeer);
            interestedPeers.clear();

            this.optimisticallyUnchokedNeighbour.setState(MessageType.choke);
            try {
                PeerCommunicationHelper.sendMessage(this.optimisticallyUnchokedNeighbour.objectOutputStream, MessageType.choke);
            } catch (Exception e) {
                e.printStackTrace();
            }

            optimisticPeer.setState(MessageType.unchoke);
            try {
                PeerCommunicationHelper.sendMessage(optimisticPeer.objectOutputStream, MessageType.unchoke);
            } catch (Exception e) {
                e.printStackTrace();
            }

            this.optimisticallyUnchokedNeighbour = optimisticPeer;
            peerProcess.log.changeOfOptimisticallyUnchokedNeighbor(this.optimisticallyUnchokedNeighbour.get_peerID());
        }
    }

    void PreferredNeighbours() {
        preferredNeighbours = Collections.synchronizedMap(new HashMap<>());

        TimerTask repeatedTask = new TimerTask() {
            @Override
            public void run() {
                if (!Peer.getPeerInstance().checkKill()) {
                    setPreferredNeighbours();
                } else {
                    Peer.getPeerInstance().pref_timer.cancel();
                    Peer.getPeerInstance().pref_timer.purge();
                    System.exit(0);
                }
            }
        };

        this.pref_timer = new Timer();
        long delay = (long) Constants.getUnchokingInterval() * 1000;
        long period = (long) Constants.getUnchokingInterval() * 1000;
        this.pref_timer.scheduleAtFixedRate(repeatedTask, delay, period);
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

        if (remotePeerInfoList.size() > 0) {
            this.preferredNeighbours.clear();

            if (this.getBitSet().equals(this.idealBitset)) { //randomly choose preferred
                int count = 0;
                while (remotePeerInfoList.size() > 0 && count < Constants.getNumberOfPreferredNeighbors() ) {
                    count++;
                    RemotePeerInfo r = remotePeerInfoList.get(ThreadLocalRandom.current().nextInt(remotePeerInfoList.size()));
                    decider(r);
                    this.preferredNeighbours.put(r, r.getBitfield());
                    Peer.getPeerInstance().unchokedPeers.add(r);
                    if (this.chokedPeers.contains(r)) this.chokedPeers.remove(r);
                    remotePeerInfoList.remove(r);
                }

                while (remotePeerInfoList.size() != 0) {
                    choker(remotePeerInfoList.get(0));
                    remotePeerInfoList.remove(0);
                }
            } else {

                Collections.sort(remotePeerInfoList);

                RemotePeerInfo remote;

                int count = 0;

                while (!remotePeerInfoList.isEmpty() && count < Constants.getNumberOfPreferredNeighbors()) {
                    remote = remotePeerInfoList.get(0);
                    decider(remote);
                    this.preferredNeighbours.put(remote, remote.getBitfield());
                    this.unchokedPeers.add(remote);
                    if (this.chokedPeers.contains(remote)) this.chokedPeers.remove(remote);
                    count++;
                    remotePeerInfoList.remove(0);
                }

                this.chokedPeers.clear();
                for (RemotePeerInfo r : remotePeerInfoList) {
                    choker(r);
                }
            }
        }

        if (!preferredNeighbours.isEmpty())
            peerProcess.log.changeOfPreferredNeighbors(this.preferredNeighbours);
    }

    private void decider(RemotePeerInfo r) {
        if ((r != null ? r.getState() : null) == MessageType.choke) {
            try {
                r.setState(MessageType.unchoke);
                PeerCommunicationHelper.sendMessage(r.objectOutputStream, MessageType.unchoke);
            } catch (Exception e) {
                throw new RuntimeException("Could not send unchoke message from the peer class", e);
            }
        }
    }

    private void choker (RemotePeerInfo r) {
        try {
            PeerCommunicationHelper.sendMessage(r.objectOutputStream, MessageType.choke);
            r.setState(MessageType.choke);
            if (!this.chokedPeers.contains(r)) this.chokedPeers.add(r);
        } catch (Exception e) {
            throw new RuntimeException("Could not send choke message from the peer class", e);
        }
    }

    public boolean checkKill() {
        if (Peer.getPeerInstance().getBitSet().equals(this.idealBitset)) {
            for (RemotePeerInfo remotePeerInfo : this.connectedPeers) {
                if (!remotePeerInfo.getBitfield().equals(this.idealBitset)) return false;
            }
        } else return false;

        return true;
    }
}