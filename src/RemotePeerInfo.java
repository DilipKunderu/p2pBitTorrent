public class RemotePeerInfo {
    private int _peerID;
    private String _hostName;
    private int _portNo;
    private int _hasFile;

    public RemotePeerInfo(int _peerID, String _hostName, int _portNo, int _hasFile) {
        this._peerID = _peerID;
        this._hostName = _hostName;
        this._portNo = _portNo;
        this._hasFile = _hasFile;
    }

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

    public int get_portNo() {
        return _portNo;
    }

    public void set_portNo(int _portNo) {
        this._portNo = _portNo;
    }

    public int get_hasFile() {
        return _hasFile;
    }

    public void set_hasFile(int _hasFile) {
        this._hasFile = _hasFile;
    }
}
