package com;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    private ExecutorService inThreadPool;
    private int serverPort;
    private ServerSocket serverSocket;
    private Thread runningThread;

    Server() {
        this.runningThread = null;
        this.serverPort = Peer.getPeerInstance().get_port();
        inThreadPool = Executors.newFixedThreadPool(Peer.getPeerInstance().peersToExpectConnectionsFrom.size());
    }

    @Override
    public void run() {
        System.out.println("Spawned the SERVER super thread");
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + this.serverPort, e);
        }

        int key = Peer.getPeerInstance().get_peerID();

        while (!Peer.getPeerInstance().checkKill()) {
            Socket clientSocket;

            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                throw new RuntimeException("Error accepting client connection", e);
            }

            this.inThreadPool.execute(
                    new IncomingRequestsHandler(clientSocket, Peer.getPeerInstance().getPeersToExpectConnectionsFrom().get(++key))
            );

            System.out.println("accepted connection from " + key);
        }
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Unable to close server", e);
        }
        System.out.println("Server stopped");
        this.inThreadPool.shutdown();
    }
}