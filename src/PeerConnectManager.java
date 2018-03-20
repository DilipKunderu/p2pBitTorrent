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
    private static List<RemotePeerInfo> remotePeers = peerProcess.getPeerList();

    private static void setConnections (Peer peer) {
        for (RemotePeerInfo remotePeerInfo : remotePeers) {
            /**
             * if (connection exists)
             *  continue;
             *  else
             *      initiate TCP connection with peerIDs less than it's own
             */
            int peer1 = peer.getPeerInstance().get_peerID();
            int peer2 = remotePeerInfo.get_peerID();
            if ( peer1 > peer2 ) {
                setUpConnection (peer1, peer2);
            } else {
                //ping to test a connection
                System.out.println("Connected well !");
            }
        }
    }

    private static void setUpConnection(int peer1, int peer2) {
        /**
         * peer with the higher peerID value initiates connection
         * with the other peer
         */

    }
}
