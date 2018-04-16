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
    private int clientID;

    Server(int port, int peerID, int max) {
        this.runningThread = null;
        this.serverPort = port;
        this.clientID = peerID;

        inThreadPool = Executors.newFixedThreadPool(max);
    }

    @Override
    public void run() {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        try {
            serverSocketOpen();
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + this.serverPort, e);
        }

        while (!peerProcess.isCompleted()) {
            Socket clientSocket;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                if (peerProcess.isCompleted()) {
                    System.out.println("Server stopped");
                    break;
                }
                throw new RuntimeException("Error accepting client connection", e);
            }
            this.inThreadPool.execute(
                    new ClientHandler(clientSocket, ++this.clientID)
            );
        }
        this.inThreadPool.shutdown();
        System.out.println("Server stopped");
    }

    public synchronized void stop() {
        peerProcess.setCompleted(true);
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Unable to close server", e);
        }
    }

    private void serverSocketOpen() throws IOException {
        this.serverSocket = new ServerSocket(this.serverPort);
    }
}