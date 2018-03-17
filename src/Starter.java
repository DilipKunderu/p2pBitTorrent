import com.jcraft.jsch.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

public class Starter{

    private static final String scriptPrefix = "java p2p/src/peerProcess";
    public void handshake(List<PeerInfo> peers) {

        for (PeerInfo remotePeer : peers) {
            try {
                JSch jsch = new JSch();
/*
* Give the path to your private key. Make sure your public key
* is already within your remote CISE machine to ssh into it
* without a password. Or you can use the corressponding method
* of JSch which accepts a password.
*/
                jsch.addIdentity("/home/dilip/.ssh/p2p_id", "");
                Session session = jsch.getSession("dilip", remotePeer.getHostName(), 22);
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);

                session.connect();

                System.out.println("Session to peer# " + remotePeer.getPeerID() + " at " + remotePeer.getHostName());

                Channel channel = session.openChannel("exec");
                System.out.println("remotePeerID" + remotePeer.getPeerID());
                ((ChannelExec) channel).setCommand(scriptPrefix + remotePeer.getPeerID());

                channel.setInputStream(null);
                ((ChannelExec) channel).setErrStream(System.err);

                InputStream input = channel.getInputStream();
                channel.connect();

                System.out.println("Channel Connected to peer# " + remotePeer.getPeerID() + " at "
                        + remotePeer.getHostName() + " server with commands");

                (new Thread() {
                    @Override
                    public void run() {

                        InputStreamReader inputReader = new InputStreamReader(input);
                        BufferedReader bufferedReader = new BufferedReader(inputReader);
                        String line = null;

                        try {

                            while ((line = bufferedReader.readLine()) != null) {
                                System.out.println(remotePeer.getPeerID() + ">:" + line);
                            }
                            bufferedReader.close();
                            inputReader.close();
                        } catch (Exception ex) {
                            System.out.println(remotePeer.getPeerID() + " Exception >:");
                            ex.printStackTrace();
                        }

                        channel.disconnect();
                        session.disconnect();
                    }
                }).start();

            } catch (JSchException e) {
// TODO Auto-generated catch block
                System.out.println(remotePeer.getPeerID() + " JSchException >:");
                e.printStackTrace();
            } catch (IOException ex) {
                System.out.println(remotePeer.getPeerID() + " Exception >:");
                ex.printStackTrace();
            }

        }

    }

}
