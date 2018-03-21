package logger;

import java.io.IOException;

public class EventLogger
{
    public Logger logWriter;

    public EventLogger(int peer_ID){
        logWriter = new Logger(peer_ID);
    }

    public void TCPConnection(int peer_ID, boolean isConnectionMakingPeer){
        String msg = "";
        write(msg);
    }

    public void changeOfPreferredNeighbors(String preferredNeighbors){

    }

    public void changeOfOptimisticallyUnchokedNeighbors(String unchockedNeighbors){

    }

    public void unchoking(int peer_ID){

    }

    public void choking(int peer_ID){

    }

    public void have(int peer_ID){

    }

    public void interested(int peer_ID){

    }

    public void notInterested(int peer_ID){

    }

    public void downloadAPiece(int peer_ID, int pieceIndex, int numberOfPieces){

    }

    public void completionOfDownload(){

    }

    private void write(String msg) {
        try {
            logWriter.log(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
