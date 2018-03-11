import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    Socket requestSocket;           //socket connect to the server
    ObjectOutputStream out;         //stream write to the socket
    public final static String FILE_TO_SEND = System.getProperty("user.dir") + "/testfile.pdf";

    String message;                //message send to the server
    String MESSAGE;                //capitalized message read from the server
    //    ObjectInputStream in;          //stream read from the socket
    FileInputStream fileInputStream;
    byte[] buffer;

    public static final String host = "192.168.0.2";

    //main method
    public static void main(String args[]) {
        Client client = new Client();
        client.run();
    }

    public void Client() {
    }

    void run() {
        try {
            //create a socket to connect to the server
            requestSocket = new Socket(host, 8000);
            System.out.println("Connected to localhost in port 8000");
            //initialize inputStream and outputStream
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush();
//            in = new ObjectInputStream(requestSocket.getInputStream());
            fileInputStream = new FileInputStream(FILE_TO_SEND);
            buffer = new byte[4096];

            while (fileInputStream.read(buffer) > 0) {
                out.write(buffer);
            }



            //get Input from standard input
//            BufferedReader br = new BufferedReader(new FileReader(FILE_TO_SEND));
//            byte[] b = new byte[48];

//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
//            while (true) {
//                System.out.print("Hello, please input a sentence: ");
//                //read a sentence from the standard input
//                message = bufferedReader.readLine();
//                //Send the sentence to the server
//                sendMessage(message);
//                //Receive the upperCase sentence from the server
//                MESSAGE = (String) in.readObject();
//                //show the message to the user
//                System.out.println("Receive message: " + MESSAGE);
//            }
        } catch (ConnectException e) {
            System.err.println("Connection refused. You need to initiate a server first.");
        }
//        catch (ClassNotFoundException e) {
//            System.err.println("Class not found");
//        }
        catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            //Close connections
            try {
//                in.close();
                fileInputStream.close();
                out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    //send a message to the output stream
    void sendMessage(String msg) {
        try {
            //stream write the message
            out.writeObject(msg);
            out.flush();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}