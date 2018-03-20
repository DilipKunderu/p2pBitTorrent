import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static File file;
    private static final int port = Peer.getPeerInstance().get_port();   //The server will be listening on this port number
    private static final String DEST_FILE = System.getProperty("user.dir");

    private static void createDirectory () {
        File dir = new File (DEST_FILE + "/peer_" + port);
        boolean success = false;
        try {
            success = dir.mkdir();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (success){
            file = new File (DEST_FILE + "/peer_" + port + "/file.dat");
        } else {
            //Log failure to create corresponding directory
        }
    }

    public static void main(String[] args) throws Exception {
        ServerSocket listener = new ServerSocket(port);

        int clientNum = 1;

        try {
            while (true) {
                new Handler(listener.accept(), clientNum).start();
                System.out.println("Client " + clientNum + " is connected!");
                clientNum++;
            }
        } finally {
            listener.close();
        }

    }

    /**
     * A handler thread class.  Handlers are spawned from the listening
     * loop and are responsible for dealing with a single client's requests.
     */
    private static class Handler extends Thread {
        private String message;    //message received from the client
        private String MESSAGE;    //uppercase message send to the client

        private Socket connection;

        private ObjectInputStream in;    //stream read from the socket
        private ObjectOutputStream out;    //stream write to the socket

        FileOutputStream fileOutputStream;

        byte[] buffer;
        private int no;        //The index number of the client

        public Handler(Socket connection, int no) {
            this.connection = connection;
            this.no = no;
        }

        public void run() {
            try {
                //initialize Input and Output streams
                out = new ObjectOutputStream(connection.getOutputStream());
                out.flush();
                in = new ObjectInputStream(connection.getInputStream());
                fileOutputStream = new FileOutputStream(DEST_FILE);
                buffer = new byte[4096];
                int filesize = 592200;
                int read = 0;
                int totalRead = 0;
                int remaining = filesize;
                try {
                    while ((read = in.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
                        totalRead += read;
                        remaining -= read;
                        System.out.println("read " + totalRead + " bytes.");
                        fileOutputStream.write(buffer, 0, read);
                    }
                } catch (Exception classnot) {
                    System.err.println("Data received in unknown format");
                }
            } catch (IOException ioException) {
                System.out.println("Disconnect with Client " + no);
            } finally {
                //Close connections
                try {
                    fileOutputStream.close();
                    in.close();
//                    out.close();
                    connection.close();
                } catch (IOException ioException) {
                    System.out.println("Disconnect with Client " + no);
                }
            }
        }
    }
}