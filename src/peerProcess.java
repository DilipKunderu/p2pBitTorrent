import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class peerProcess {
    private static BufferedReader bufferedReader;
    private static List<String> commonList;
    private static List<Peer> peerList;
    private static Peer peer;
    private static int _peerID;

    public static int setBitset (int n) {
        int i = 0;
        for (; i < n; i++) {
            peer.set_bitField(i);
//            System.out.println(peer.get_bitField(i));
        }

        return i;
    }

    public static void setConfigVars () throws IOException, FileNotFoundException {
        bufferedReader = new BufferedReader(new FileReader(new File(Constants.common)));

        String s;
        String[] t;

        commonList = new ArrayList<>();

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

        int n = 0;
        int f = Constants.getFileSize();
        int p = Constants.getPieceSize();

        if (f % p == 0) {
            n = f/p;
        } else {
            int temp = (int) (f - p * (f/p));
            peer.set_excessPieceSize(temp);
            System.out.println (peer.get_excessPieceSize());
            n = f/p;
            ++n;
        }

        peer.set_pieceCount(n);

        int temp = setBitset(n);
        n = peer.get_excessPieceSize();
        int temp1 = setBitset(temp + n);
    }

    public static void buildPeersList () throws IOException, FileNotFoundException {
        bufferedReader = new BufferedReader(new FileReader(new File(Constants.peers)));

        peerList = new ArrayList<>();

        String s;
        String[] t;

        while ((s = bufferedReader.readLine()) != null) {
            t = s.split("\\s+");

//            peer = Peer.getPeerInstance();
            peer.set_peerID (Integer.parseInt (t[0]));
            peer.set_hostName (t[1]);
            peer.set_port (Integer.parseInt(t[2]));
            peer.set_hasFile (Integer.parseInt (t[3]));
            peerList.add(peer);
        }

        bufferedReader.close();
    }



    public static void main (String [] args) throws InterruptedException, IOException {
        int _currentPeer;
        if (args.length > 0) {
            try {
                _currentPeer = Integer.parseInt(args[0]);
                peer = Peer.getPeerInstance();
                setConfigVars();
                buildPeersList();
                Starter s = new Starter();
                List<PeerInfo> l = new ArrayList<>();
                l.add(new PeerInfo("1001", "192.168.2.2"));
                s.handshake(l);
//                for (Peer p : peerList) {
//                    System.out.println(p.get_peerID());
//                }

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        _peerID = Integer.parseInt(args[0]);
    }
}
