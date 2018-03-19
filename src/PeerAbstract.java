import java.util.BitSet;

public abstract class PeerAbstract {
    protected int _peerID;
    protected String _hostName;
    protected int _port;
    protected boolean _hasFile;
    protected BitSet _bitField;

    protected abstract int get_peerID();

    protected abstract void set_peerID(int _peerID);

    protected abstract String get_hostName();

    protected abstract void set_hostName(String _hostName);

    protected abstract int get_port();

    protected abstract void set_port(int _port);

    protected abstract boolean get_hasFile();

    protected abstract void set_hasFile(int _hasFile);
}
