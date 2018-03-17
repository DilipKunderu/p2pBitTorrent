public class PeerInfo {

    private String peerID;
    private String hostName;
    private int portNo;

    public PeerInfo(String peerID, String hostName) {
        super();
        this.peerID = peerID;
        this.hostName = hostName;
    }

    public String getPeerID() {
        return peerID;
    }

    public void setPeerID(String peerID) {
        this.peerID = peerID;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPortNo() {
        return portNo;
    }

    public void setPortNo(int portNo) {
        this.portNo = portNo;
    }
}
