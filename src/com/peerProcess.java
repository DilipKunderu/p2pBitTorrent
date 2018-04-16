package com;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class peerProcess {
    private static BufferedReader bufferedReader;
    private static Peer peer;
    private static boolean completed;

    static boolean isCompleted() {
        return completed;
    }

    static synchronized void setCompleted(boolean completed) {
        peerProcess.completed = completed;
    }

    private static void setCommonConfigVars() throws IOException {
        bufferedReader = new BufferedReader(new FileReader(new File(Constants.common)));

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

    private static int buildRemotePeersList(int current) throws IOException {
        int numPeers = -1;

        bufferedReader = new BufferedReader(new FileReader(new File(Constants.peers)));

        peer.peerList = new ArrayList<>();

        String s;
        String[] t;

        while ((s = bufferedReader.readLine()) != null) {
            t = s.split("\\s+");

            if (current == Integer.parseInt(t[0])) {
                peer.set_peerID(current);
                peer.set_hostName(t[1]);
                peer.set_port(Integer.parseInt(t[2]));
                peer.set_hasFile(Integer.parseInt(t[3]));

                if (peer.get_hasFile() == 1) {
                    peer.setPieceSize();
                }
                break;
            } else
                peer.peerList.add(new RemotePeerInfo(Integer.parseInt(t[0]), t[1], Integer.parseInt(t[2]), Integer.parseInt(t[3])));
            numPeers++;
        }

        bufferedReader.close();
        return numPeers;
    }

    public static void main(String[] args) throws IOException {
        completed = false;
        int numberOfPeersToExpectConnectionsFrom = 0;

        if (args.length > 0) {
            peer = Peer.getPeerInstance();
            try {
                setCommonConfigVars();
            } catch (FileNotFoundException fileNotfoundException ) {
                //Log
                fileNotfoundException.printStackTrace();
            } finally {
                //Log successful setting of vars
            }
            try {
                numberOfPeersToExpectConnectionsFrom = buildRemotePeersList(Integer.parseInt(args[0]));
            } catch (FileNotFoundException fileNotfoundException ) {
                //Log
                fileNotfoundException.printStackTrace();
            } finally {
                //Log successful setting of vars
            }

            Server server = new Server(peer.get_port(), peer.get_peerID(), numberOfPeersToExpectConnectionsFrom);
            new Thread(server).start();


            //Now we need to send TCP connection requests to other nodes
            Client client = new Client(peer.peerList);
            new Thread(client).start();
        }
    }
}
