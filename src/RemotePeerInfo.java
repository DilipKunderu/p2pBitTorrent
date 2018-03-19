public class RemotePeerInfo extends PeerAbstract {
    public RemotePeerInfo(int _peerID, String _hostName, int _portNo, int _hasFile) {
        this._peerID = _peerID;
        this._hostName = _hostName;
        this._port = _portNo;
        this._hasFile = (_hasFile == 1);
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

}
