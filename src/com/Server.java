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
            serverSocketOpen();
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + this.serverPort, e);
        }

        int key = Peer.getPeerInstance().get_peerID();

        while (!peerProcess.isCompleted()) {
            Socket clientSocket;

            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                
                throw new RuntimeException("Error accepting client connection", e);
            }

            this.inThreadPool.execute(
                    new IncomingRequestsHandler(clientSocket, Peer.getPeerInstance().getPeersToExpectConnectionsFrom().get(++key))
            );
        }
        try {
			this.serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		}

        this.inThreadPool.shutdown();
        System.out.println("Server stopped");
    }

    

    private void serverSocketOpen() throws IOException {
        this.serverSocket = new ServerSocket(this.serverPort);
    }
}