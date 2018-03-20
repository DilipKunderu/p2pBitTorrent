import com.jcraft.jsch.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * The task now is to establish persistent TCP connections between peers
 *
 * 1. Initiate a TCP connection from current peer to all the peers with peer ID less
 * than its own.
 */

/**
 * Aim is to connect to peers with whom the connection has not been established yet.
 *
 * If this class is run on peer 1001, we should initiate a TCP handshake
 * to all the peers in the RemotePeerInfo list.
 * Next when this class is run in 1002, we must
 * verify that the TCP connection has already been established with 1001,
 * and then proceed to initiate connections with the later ones in this peer's RemotePeerInfo list.
 *
 */
public class PeerConnectManager {
    private static List<RemotePeerInfo> remotePeers;

    public PeerConnectManager (List<RemotePeerInfo> remotePeers) {
        this.remotePeers = remotePeers;
    }

    private static void setConnections (Peer peer) {
        for (RemotePeerInfo remotePeerInfo : remotePeers) {
            /**
             * if (connection exists)
             *  continue;
             *  else
             *      initiate TCP connection with peerIDs less than it's own
             */

            if (peer.getPeerInstance().get_peerID() > remotePeerInfo.get_peerID()) {
                //initiate a TCP connection
            } else {
                //ping to test a connection
            }
        }
    }
}

//    private static final String scriptPrefix = "java p2p/src/peerProcess";
//
//    public static void main(String[] args) {
//
//        List<PeerInfo> peerList = new ArrayList<>();
//
//        String ciseUser = "dilip"; // change with your CISE username
//
///**
// * Make sure the below peer hostnames and peerIDs match those in
// * RemotePeerInfo.cfg in the remote CISE machines. Also make sure that the
// * peers which have the file initially have it under the 'peer_[peerID]'
// * folder.
// */
//
////        peerList.add(new RemotePeerInfo("1", "lin114-06.cise.ufl.edu"));
////        peerList.add(new RemotePeerInfo("2", "lin114-08.cise.ufl.edu"));
////        peerList.add(new RemotePeerInfo("3", "lin114-09.cise.ufl.edu"));
////        peerList.add(new RemotePeerInfo("4", "lin114-04.cise.ufl.edu"));
////        peerList.add(new RemotePeerInfo("5", "lin114-05.cise.ufl.edu"));
//
//        peerList.add(new PeerInfo("1", "192.168.2.2"));
//
//        for (PeerInfo remotePeer : peerList) {
//            try {
//                JSch jsch = new JSch();
///*
//* Give the path to your private key. Make sure your public key
//* is already within your remote CISE machine to ssh into it
//* without a password. Or you can use the corressponding method
//* of JSch which accepts a password.
//*/
//                jsch.addIdentity("/home/dilip/.ssh/id_rsa", "");
//                Session session = jsch.getSession(ciseUser, remotePeer.getHostName(), 22);
//                Properties config = new Properties();
//                config.put("StrictHostKeyChecking", "no");
//                session.setConfig(config);
//
//                session.connect();
//
//                System.out.println("Session to peer# " + remotePeer.getPeerID() + " at " + remotePeer.getHostName());
//
//                Channel channel = session.openChannel("exec");
//                System.out.println("remotePeerID" + remotePeer.getPeerID());
//                ((ChannelExec) channel).setCommand(scriptPrefix + remotePeer.getPeerID());
//
//                channel.setInputStream(null);
//                ((ChannelExec) channel).setErrStream(System.err);
//
//                InputStream input = channel.getInputStream();
//                channel.connect();
//
//                System.out.println("Channel Connected to peer# " + remotePeer.getPeerID() + " at "
//                        + remotePeer.getHostName() + " server with commands");
//
//                (new Thread() {
//                    @Override
//                    public void run() {
//
//                        InputStreamReader inputReader = new InputStreamReader(input);
//                        BufferedReader bufferedReader = new BufferedReader(inputReader);
//                        String line = null;
//
//                        try {
//
//                            while ((line = bufferedReader.readLine()) != null) {
//                                System.out.println(remotePeer.getPeerID() + ">:" + line);
//                            }
//                            bufferedReader.close();
//                            inputReader.close();
//                        } catch (Exception ex) {
//                            System.out.println(remotePeer.getPeerID() + " Exception >:");
//                            ex.printStackTrace();
//                        }
//
//                        channel.disconnect();
//                        session.disconnect();
//                    }
//                }).start();
//
//            } catch (JSchException e) {
//// TODO Auto-generated catch block
//                System.out.println(remotePeer.getPeerID() + " JSchException >:");
//                e.printStackTrace();
//            } catch (IOException ex) {
//                System.out.println(remotePeer.getPeerID() + " Exception >:");
//                ex.printStackTrace();
//            }
//
//        }
//    }
//
//    public static class PeerInfo {
//
//        private String peerID;
//        private String hostName;
//        private int portNo;
//
//        public PeerInfo(String peerID, String hostName) {
//            super();
//            this.peerID = peerID;
//            this.hostName = hostName;
//        }
//
//        public String getPeerID() {
//            return peerID;
//        }
//
//        public void setPeerID(String peerID) {
//            this.peerID = peerID;
//        }
//
//        public String getHostName() {
//            return hostName;
//        }
//
//        public void setHostName(String hostName) {
//            this.hostName = hostName;
//        }
//
//        public int getPortNo() {
//            return portNo;
//        }
//
//        public void setPortNo(int portNo) {
//            this.portNo = portNo;
//        }
//    }

//}
