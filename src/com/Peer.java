package com;

import com.logger.EventLogger;
import com.messages.Message;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class Peer {
    public static Peer peer;
    private volatile BitSet _bitField;


    private RemotePeerInfo OptimisticallyUnchokedNeighbour;

    Map<Integer, RemotePeerInfo> peersToConnectTo; //set from peerProcess
    Map<Integer, RemotePeerInfo> peersToExpectConnectionsFrom; // set from peerProcess
    List<RemotePeerInfo> connectedPeers; //for choosing randomly; this would stay constant once it is set
    volatile Map<RemotePeerInfo, BitSet> preferredNeighbours; // giving access to messages classes


//    EventLogger log;

    Map<Integer, RemotePeerInfo> peersInterested; //used in messages classes

    /**
     * This map will be used to index running threads of the peer
     * */

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

    public Map<Integer, RemotePeerInfo> getPeersInterested() {
        return peersInterested;
    }

    BitSet getBitSet(){
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
//        this._pieceCount = _pieceCount;
        int f = Constants.getFileSize();
        int p = Constants.getPieceSize();

        this._pieceCount = (int) Math.ceil((double)f/p);
    }

    private int get_excessPieceSize() {
        return _excessPieceSize;
    }

    private void set_excessPieceSize(int _excessPieceSize) {
        this._excessPieceSize = _excessPieceSize;
    }

    public void setBitset() {
        for (int i = 0; i < get_pieceCount(); i++) {
            peer.set_bitField(i);
        }
    }

//    void setPieceSize() {
//        int n = 0;
//        int f = Constants.getFileSize();
//        int p = Constants.getPieceSize();
//
//        if (f % p == 0) {
//            n = f / p;
//        } else {
//            int temp = (f - p * (f / p));
//            peer.set_excessPieceSize(temp);
//            System.out.println(peer.get_excessPieceSize());
//            n = f / p;
//            ++n;
//        }
//
//        peer.set_pieceCount(n);
//
//        int temp = setBitset(n);
//        n = peer.get_excessPieceSize();
//        setBitset(temp + n);
//    }

    private Peer() {
//        set_pieceCount();
        this._bitField = new BitSet(this.get_pieceCount());
        this.peersToConnectTo = Collections.synchronizedMap(new LinkedHashMap<>());
        this.peersToExpectConnectionsFrom = Collections.synchronizedMap(new LinkedHashMap<>());
        this.connectedPeers = Collections.synchronizedList(new ArrayList<>());
        this.peersInterested = Collections.synchronizedMap(new HashMap<>());
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

    void OptimisticallyUnchokedNeighbour() {
        TimerTask repeatedTask = new TimerTask() {
            @Override
            public void run () {
                setOptimisticallyUnchokedNeighbour();
            }
        };
//        log.changeOfOptimisticallyUnchokedNeighbor(this.OptimisticallyUnchokedNeighbour.get_peerID());
        Timer opt_timer = new Timer();
        long delay = (long) Constants.getOptimisticUnchokingInterval() * 1000;
        long period = (long) Constants.getOptimisticUnchokingInterval() * 1000;
        opt_timer.scheduleAtFixedRate(repeatedTask, delay, period);
    }

    private void setOptimisticallyUnchokedNeighbour() {
        List<RemotePeerInfo> interestedPeers = new ArrayList<>(this.peersInterested.values());

        if (interestedPeers.size() == 0) {
            this.OptimisticallyUnchokedNeighbour = this.connectedPeers.get(ThreadLocalRandom.current().nextInt(interestedPeers.size()));
        }else
            this.OptimisticallyUnchokedNeighbour = interestedPeers.get(ThreadLocalRandom.current().nextInt(interestedPeers.size()));
        interestedPeers.clear();
    }


    void PreferredNeighbours() {
        preferredNeighbours = Collections.synchronizedMap(new HashMap<>());

        TimerTask repeatedTask = new TimerTask() {
            @Override
            public void run() {
                 setPreferredNeighbours();
            }
        };

        Timer pref_timer = new Timer();
      //  long delay = (long) Constants.getUnchokingInterval() * 1000;
        long delay = (long) Constants.getUnchokingInterval() * 1000;
        long period = (long) Constants.getUnchokingInterval() * 1000;
        pref_timer.scheduleAtFixedRate(repeatedTask, delay, period);
    }

    private synchronized void setPreferredNeighbours() {
        /**
         * This list gets populated whenever there is a file transfer going on between the local
         * peer and the corresponding remote peer. For that cycle, the state for the remote peer remains
         * unchoked.
         * */
        System.out.println("triggered preferredNeighbors");
        List<RemotePeerInfo> remotePeerInfoList = new ArrayList<>(this.peersInterested.values());
        /**
         * This queue is used to add remote peer objects into the preferred neighbours map, going by the
         * associated download rate.
         **/

        this.preferredNeighbours.clear();

        if (this._hasFile != 1) {
            Queue<RemotePeerInfo> neighborsQueue = new PriorityBlockingQueue<>(Constants.getNumberOfPreferredNeighbors(), (o1, o2) -> Math.toIntExact(o1.getDownload_rate() - o2.getDownload_rate()));

            for (RemotePeerInfo _remote : remotePeerInfoList) {
                neighborsQueue.add(_remote);
                remotePeerInfoList.remove(_remote);
            }

            RemotePeerInfo remote;

            while (!neighborsQueue.isEmpty()) {
                remote = neighborsQueue.poll();
                decider(remote);
                this.preferredNeighbours.put(remote, remote.getBitfield());
            }

            while (!neighborsQueue.isEmpty()) {
                remote = neighborsQueue.poll();
                try {
                    PeerCommunicationHelper.sendChokeMsg(remote.objectOutputStream);
                    remote.setState(MessageType.choke);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    throw new RuntimeException("Could not send choke message from the peer class", e);
                }
            }
        } else {
            int count = 0;
            //use remotePeerInfoList
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
                    PeerCommunicationHelper.sendChokeMsg(r.objectOutputStream);
                    r.setState(MessageType.choke);
                    remotePeerInfoList.remove(0);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    throw new RuntimeException("Could not send choke message from the peer class", e);
                }
            }
        }

//        log.changeOfPreferredNeighbors(preferredNeighbours);
    }

        private void decider (RemotePeerInfo r) {
            if ((r != null ? r.getState() : null) == MessageType.choke) {
                try {
                    PeerCommunicationHelper.sendUnChokeMsg(r.objectOutputStream);
                    r.setState(MessageType.unchoke);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    throw new RuntimeException("Could not send choke message from the peer class", e);
                }
            }
//            this.preferredNeighbours.put(r, r != null ? r.getBitfield() : null);

        }
}
