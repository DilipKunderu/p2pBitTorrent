package com;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class Peer {
    private Timer opt_timer;
    private Timer pref_timer;

    private static volatile Peer peer;
    private BitSet _bitField;
    private RemotePeerInfo OptimisticallyUnchokedNeighbour;

    Map<Integer, RemotePeerInfo> peersToConnectTo;
    Map<Integer, RemotePeerInfo> peersToExpectConnectionsFrom;

    List<RemotePeerInfo> connectedPeers;
    Queue<RemotePeerInfo> neighborsQueue;
    Map<RemotePeerInfo, BitSet> preferredNeighbours;
    Map<Integer, RemotePeerInfo> peersInterested;

    private int _peerID;
    private String _hostName;
    private int _port;
    private int _hasFile;

    private int _excessPieceSize;
    private int _pieceCount;

    public RemotePeerInfo getOptimisticallyUnchokedNeighbour() {
        return OptimisticallyUnchokedNeighbour;
    }


    public Map<Integer, RemotePeerInfo> getPeersToConnectTo() {
        return peersToConnectTo;
    }

    public Map<Integer, RemotePeerInfo> getPeersToExpectConnectionsFrom() {
        return peersToExpectConnectionsFrom;
    }


    public BitSet getBitSet(){
    	return _bitField;
    }

    int get_peerID() {
        return _peerID;
    }

    void set_peerID(int _peerID) {
        this._peerID = _peerID;
    }

    String get_hostName() {
        return _hostName;
    }

    public int get_port() {
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

    public int get_pieceCount() {
        return _pieceCount;
    }

    void set_bitField(int i) {
        this._bitField.set(i);
    }

    void set_pieceCount(int _pieceCount) {
        this._pieceCount = _pieceCount;
    }

    int get_excessPieceSize() {
        return _excessPieceSize;
    }

    void set_excessPieceSize(int _excessPieceSize) {
        this._excessPieceSize = _excessPieceSize;
    }

    int setBitset(int n) {
        int i = 0;
        for (; i < n; i++) {
            peer.set_bitField(i);
        }
        return i;
    }

    void setPieceSize() {
        int n = 0;
        int f = Constants.getFileSize();
        int p = Constants.getPieceSize();

        if (f % p == 0) {
            n = f / p;
        } else {
            int temp = (f - p * (f / p));
            peer.set_excessPieceSize(temp);
            System.out.println(peer.get_excessPieceSize());
            n = f / p;
            ++n;
        }

        peer.set_pieceCount(n);

        int temp = setBitset(n);
        n = peer.get_excessPieceSize();
        setBitset(temp + n);
    }

    private Peer() {
        _bitField = new BitSet(this.get_pieceCount());
    }

    public static Peer getPeerInstance() {
        if (peer == null) {
            synchronized (Peer.class) {
                if (peer == null) peer = new Peer();
            }
        }
        return peer;
    }

    //Timer based tasks :-

    /***************************************************************************************************/

    private void OptimisticallyUnchokedNeighbour() {
        TimerTask repeatedTask = new TimerTask() {
            @Override
            public void run () {
                setOptimisticallyUnchokedNeighbour();
            }
        };

        opt_timer = new Timer();
        long delay = 0L;
        long period = (long) Constants.getOptimisticUnchokingInterval();
        opt_timer.scheduleAtFixedRate(repeatedTask, delay, period);
    }

    private void setOptimisticallyUnchokedNeighbour() {
        this.OptimisticallyUnchokedNeighbour = this.connectedPeers.get(ThreadLocalRandom.current().nextInt(this.connectedPeers.size()));
    }


    private void PreferredNeighbours (RemotePeerInfo remote, BitSet bitset) {
        neighborsQueue = new PriorityBlockingQueue<>(Constants.getNumberOfPreferredNeighbors(), (o1, o2) -> Math.toIntExact(o1.getDownload_rate() - o2.getDownload_rate()));
        preferredNeighbours = Collections.synchronizedMap(new HashMap<>());

        TimerTask repeatedTask = new TimerTask() {
            @Override
            public void run() {
                 setPreferredNeighbours();
            }
        };

        pref_timer = new Timer();
        long delay = 0L;
        long period = (long) Constants.getUnchokingInterval();
        opt_timer.scheduleAtFixedRate(repeatedTask, delay, period);
    }

    private synchronized void setPreferredNeighbours() {
        RemotePeerInfo remote;

        this.preferredNeighbours.clear();

        for (int i = Constants.getNumberOfPreferredNeighbors(); i > 0; i--) {
            if (this._hasFile != 1) {
                remote = this.neighborsQueue.poll();
//                if ((remote != null ? remote.getState() : null) == MessageType.choke)
//                    remote.setState(MessageType.unchoke);
                this.preferredNeighbours.put(remote, remote != null ? remote.getBitfield() : null);
            } else{
                remote = this.connectedPeers.get(ThreadLocalRandom.current().nextInt(this.connectedPeers.size()));
//                if (remote.getState() == MessageType.choke || remote.getState() == null)
//                    remote.setState(MessageType.unchoke);
                this.preferredNeighbours.put(remote, remote.getBitfield());
            }
        }
    }
}
