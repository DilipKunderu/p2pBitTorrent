package com.logger;

import java.io.IOException;

public class EventLogger
{
    public Logger logWriter;

    public EventLogger(int peer_ID){
        logWriter = new Logger(peer_ID);
    }

    public void TCPConnection(int peer_ID, boolean isConnectionMakingPeer){
        String msg = (isConnectionMakingPeer)? " makes a connection to Peer " + peer_ID + ".": "is connected from Peer " + peer_ID + "." ;
        write(msg);
    }

    public void changeOfPreferredNeighbors(String preferredNeighbors){
        String msg = " has the preferred neighbors " + preferredNeighbors + ".";
        write(msg);
    }

    public void changeOfOptimisticallyUnchokedNeighbor(String unchockedNeighbor){
        String msg = " has the optimistically unchoked neighbor " + unchockedNeighbor + ".";
        write(msg);
    }

    public void unchoking(int peer_ID){
        String msg = " is unchoked by " + peer_ID + ".";
        write(msg);
    }

    public void choking(int peer_ID){
        String msg = " is choked by " + peer_ID + ".";
        write(msg);
    }

    public void have(int peer_ID, int pieceIndex){
        String msg = " received the ‘have’ message from " + peer_ID + " for the piece " + pieceIndex + ".";
        write(msg);
    }

    public void interested(int peer_ID){
        String msg = " received the ‘interested’ message from " + peer_ID + ".";
        write(msg);
    }

    public void notInterested(int peer_ID){
        String msg = " received the ‘not interested’ message from " + peer_ID + ".";
        write(msg);
    }

    public void downloadAPiece(int peer_ID, int pieceIndex, int numberOfPieces){
        String msg = " has downloaded the piece " + pieceIndex + " from " + peer_ID + ". Now the number of pieces it has is " + numberOfPieces + ".";
        write(msg);
    }

    public void completionOfDownload(){
        String msg = " has downloaded the complete file.";
        write(msg);
    }

    private void write(String msg) {
        try {
            logWriter.log(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
