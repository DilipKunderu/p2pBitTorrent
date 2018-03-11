import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PeerProcess {
    private static BufferedReader bufferedReader;
    private static List<String> commonList;
    private static List<Peer> peerList;

    public static void main (String [] args) throws FileNotFoundException, IOException {
        File file1 = new File (Constants.common);
        File file2 = new File(Constants.peers);
        bufferedReader = new BufferedReader(new FileReader(new File(Constants.common)));

        String s1;
        String[] t1;

        commonList = new ArrayList<>();

        while ((s1 = bufferedReader.readLine()) != null) {
            t1 = s1.split(" ");
            commonList.add(t1[1]);
        }

        Constants.setNumberOfPreferredNeighbors(Integer.parseInt(commonList.get(0)));
        Constants.setUnchokingInterval(Integer.parseInt(commonList.get(1)));
        Constants.setOptimisticUnchokingInterval(Integer.parseInt(commonList.get(2)));
        Constants.setFileName(commonList.get(3));
        Constants.setFileSize(Integer.parseInt (commonList.get(4)));
        Constants.setPieceSize(Integer.parseInt (commonList.get(5)));

        bufferedReader.close();

        bufferedReader = new BufferedReader(new FileReader(new File(Constants.peers)));

        peerList = new ArrayList<>();

        int _peerID, _port, _hasFile;
        String s, _hostName;
        String[] t;

        while ((s = bufferedReader.readLine()) != null) {
            t = s.split(" ");

            _peerID = Integer.parseInt (t[0]);
            _hostName = t[1];
            _port = Integer.parseInt(t[2]);
            _hasFile = Integer.parseInt (t[3]);

            peerList.add(new Peer(_peerID, _hostName, _port, _hasFile));
        }

        bufferedReader.close();
    }
}
