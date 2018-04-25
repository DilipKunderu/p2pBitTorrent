package com;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
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

        Iterator<Map.Entry<Integer, RemotePeerInfo>> itr = Peer.getPeerInstance().getPeersToExpectConnectionsFrom().entrySet().iterator();

        while (itr.hasNext()) {
            Socket clientSocket;

            try {
                clientSocket = serverSocket.accept();
                this.inThreadPool.execute(
                        new IncomingRequestsHandler(clientSocket, itr.next().getValue())
                );
            } catch (IOException e) {
                throw new RuntimeException("Error accepting client connection", e);
            }
        }
        this.inThreadPool.shutdown();
        try {
			this.serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		}
        System.out.println("Server stopped");
    }
}