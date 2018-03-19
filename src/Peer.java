import java.util.BitSet;

public class Peer extends PeerAbstract {
    public int get_peerID() {
        return _peerID;
    }

    public void set_peerID(int _peerID) {
        this._peerID = _peerID;
    }

    public String get_hostName() {
        return _hostName;
    }

    public void set_hostName(String _hostName) {
        this._hostName = _hostName;
    }

    public int get_port() {
        return _port;
    }

    public void set_port(int _port) {
        this._port = _port;
    }

    public boolean get_hasFile() {
        return _hasFile;
    }

    public void set_hasFile(int _hasFile) {
        this._hasFile = true;
    }


    private int _excessPieceSize;
    private int _pieceCount;

//    private BitSet _bitField;

    public boolean get_bitField(int i) {
        return _bitField.get(i);
    }

    public void set_bitField(int i) {
        this._bitField.set(i);
    }

    public int get_pieceCount() {
        return _pieceCount;
    }

    public void set_pieceCount(int _pieceCount) {
        this._pieceCount = _pieceCount;
    }

    public int get_excessPieceSize() {
        return _excessPieceSize;
    }

    public void set_excessPieceSize(int _excessPieceSize) {
        this._excessPieceSize = _excessPieceSize;
    }

    private static volatile Peer peer;

    private Peer() {
        if (peer != null) {
            throw new RuntimeException("Use getPeerInstance() for a single instance");
        }
        _bitField = new BitSet();
    }

    public static Peer getPeerInstance() {
        if (peer == null) {
            synchronized (Peer.class) {
                if (peer == null) peer = new Peer();
            }
        }
        return peer;
    }
}
