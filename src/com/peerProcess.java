package com;

import java.io.*;

import java.util.*;


public class peerProcess {
    private static Peer peer;
    private static boolean completed;

    static boolean isCompleted() {
        return completed;
    }

    static synchronized void setCompleted(boolean completed) {
        peerProcess.completed = completed;
    }

    private static void setCommonConfigVars() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(Constants.common)));

        String s;
        String[] t;

        List<String> commonList = new LinkedList<>();

        while ((s = bufferedReader.readLine()) != null) {
            t = s.split(" ");
            commonList.add(t[1]);
        }

        Constants.setNumberOfPreferredNeighbors(Integer.parseInt(commonList.get(0)));
        Constants.setUnchokingInterval(Integer.parseInt(commonList.get(1)));
        Constants.setOptimisticUnchokingInterval(Integer.parseInt(commonList.get(2)));
        Constants.setFileName(commonList.get(3));
        Constants.setFileSize(Integer.parseInt (commonList.get(4)));
        Constants.setPieceSize(Integer.parseInt (commonList.get(5)));

        bufferedReader.close();
    }

    private static void buildRemotePeersList(int current) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(Constants.peers)));
        String s;
        String[] t;

        RemotePeerInfo remote;
        while ((s = bufferedReader.readLine()) != null) {
            t = s.split("\\s+");

            int currPeerID = Integer.parseInt(t[0]);

            if (current == Integer.parseInt(t[0])) {
                peer.set_peerID(current);
                checkFileExists(current);
                peer.set_hostName(t[1]);
                peer.set_port(Integer.parseInt(t[2]));
                peer.set_hasFile(Integer.parseInt(t[3]));
            } else {
                remote = new RemotePeerInfo(Integer.parseInt(t[0]), t[1], Integer.parseInt(t[2]), Integer.parseInt(t[3]));
                if (current < currPeerID) {
                    peer.peersToExpectConnectionsFrom.put(currPeerID, remote);
                } else {
                    peer.peersToConnectTo.put(currPeerID, remote);
                }
                peer.connectedPeers.add(remote);
            }
        }

        bufferedReader.close();
    }

    private static boolean checkFileExists(int peerID) throws FileNotFoundException {
        File f = new File (Constants.root + "/peer_" + String.valueOf(peerID) + "/" + Constants.getFileName());
        boolean res;
        if (!f.exists()) {
            throw new FileNotFoundException("Required File not found");
        } else {
            res = true;
        }

        return res;
    }

    private static void createDirectory() {
        File file = new File (Constants.root + "/peer_" + String.valueOf(peer.get_peerID()));
        if (!file.mkdir()) {
            throw new RuntimeException("Unable to create directory");
        }
    }

    public static void main(String[] args) throws IOException {
        completed = false;

        if (args.length > 0) {
            peer = Peer.getPeerInstance();

            peer.peersToConnectTo = Collections.synchronizedMap(new LinkedHashMap<>());
            peer.peersToExpectConnectionsFrom = Collections.synchronizedMap(new LinkedHashMap<>());
            peer.connectedPeers = Collections.synchronizedList(new ArrayList<>());

            try {
                setCommonConfigVars();
            } catch (FileNotFoundException fileNotfoundException ) {
                //Log
                fileNotfoundException.printStackTrace();
            } finally {
                //Log successful setting of vars
            }
            try {
                buildRemotePeersList(Integer.parseInt(args[0]));
                if (peer.get_hasFile() == 1) {
                    if (!checkFileExists(peer.get_peerID())) {
                        throw new RuntimeException("No file found in peer which is supposed to have the file");
                    }
                } else {
                    createDirectory();
                }
            } catch (FileNotFoundException fileNotfoundException ) {
                //Log
                fileNotfoundException.printStackTrace();
            } finally {
                //Log successful setting of vars
            }

//            InetAddress.getByName(peer.remotePeerInfo.get_hostName()
            if (peer.getPeersToExpectConnectionsFrom().size() > 0) {
                Server server = new Server();
                new Thread(server).start();
            }

            if (peer.getPeersToConnectTo().size() > 0) {
                Client client = new Client(peer.peersToConnectTo);
                new Thread(client).start();
            }
            //Now we need to send TCP connection requests to other nodes
        }
    }
}
