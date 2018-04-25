package com;

import java.io.*;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.FileProcessor.FileManagerExecutor;
import com.messages.Message;
import com.messages.MessageHandler;
import com.messages.MessageUtil;

public class PeerCommunicationHelper {
	
	public static Message sendMessage(ObjectOutputStream out,MessageType messageType) throws Exception{
		MessageHandler messageHandler = new MessageHandler(messageType);
		Message message = messageHandler.buildMessage();
		out.writeObject(message);
		out.flush();
		return message;
	}
	
	public static Message sendBitSetMsg(ObjectOutputStream out) throws Exception{
		MessageHandler messageHandler = new MessageHandler(MessageType.bitfield, MessageUtil.toByteArray(Peer.getPeerInstance().getBitSet()));
		Message message = messageHandler.buildMessage();
		out.writeObject(message);
		out.flush();
		return message;
	}

	public static Message sendRequestMsg(ObjectOutputStream out, RemotePeerInfo remote) throws Exception{
		int a = getPieceIndex(remote);
		if(a== -1){
			sendMessage(out, MessageType.notinterested);
			return null;
		}
		MessageHandler messageHandler = new MessageHandler(MessageType.request,MessageUtil.intToByteArray(a));
		Message message = messageHandler.buildMessage();
		out.writeObject(message);
		out.flush();
		return message;
	}
	
	public static Message sendRequestWhenHave(ObjectOutputStream out, byte[] pieceIndex) throws Exception{
		MessageHandler messageHandler = new MessageHandler(MessageType.request,pieceIndex);
		Message message = messageHandler.buildMessage();
		out.writeObject(message);
		out.flush();
		return message;
	}

	public static Message sendHaveMsg(ObjectOutputStream out, int recentReceivedPieceIndex) throws Exception{
		MessageHandler messageHandler = new MessageHandler(MessageType.have,MessageUtil.intToByteArray(recentReceivedPieceIndex));
		Message message = messageHandler.buildMessage();
		out.writeObject(message);
		out.flush();
		return message;
	}
	
	public static Message sendPieceMsg(ObjectOutputStream out, int pieceIndex) throws Exception{
		byte[] index = MessageUtil.intToByteArray(pieceIndex);
		byte[] payload = FileManagerExecutor.getFilePart(pieceIndex);
		byte[] payloadWithIndex = MessageUtil.concatenateByteArrays(index, payload);
		MessageHandler messageHandler = new MessageHandler(MessageType.piece,payloadWithIndex );
		Message message = messageHandler.buildMessage();
		out.writeObject(message);
		out.flush();
		return message;
	}

	public static Message getActualObjectMessage(ObjectInputStream in, RemotePeerInfo remote) {
		try {
			Message received = (Message) in.readObject();
			logHelper(received, remote);
			if (received == null) System.out.println("received null");
			else System.out.println("object received");

			System.out.println(received.toString());
			return received;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

    private static void logHelper(Message received, RemotePeerInfo remote) {
	    switch (received.getMessage_type()){
            case 0:{
                peerProcess.log.choking(remote.get_peerID());
                break;
            }
            case 1:{
                peerProcess.log.unchoking(remote.get_peerID());
                break;
            }
            case 2:{
                peerProcess.log.interested(remote.get_peerID());
                break;
            }
            case 3:{
                peerProcess.log.notInterested(remote.get_peerID());
                break;
            }
            case 4:{
                peerProcess.log.have(remote.get_peerID(), MessageUtil.byteArrayToInt(received.getMessagePayload()));
                break;
            }
        }
    }
    
    public static byte getMessageType(BufferedInputStream in) throws IOException{
    	byte[] lengthBytePlusMsgType = new byte[5];
    	in.read(lengthBytePlusMsgType);
    	return lengthBytePlusMsgType[4];
    }
    
    public static boolean isInterseted(BitSet b1, BitSet b2){
    	for(int i=0; i<b2.length();i++){
    		if(b1.get(i)!=b2.get(i)){
    			return false;
    		}
    	}
		return true;
    }
    
    public static int getPieceIndex(RemotePeerInfo remote){
    	BitSet b1 = remote.getBitfield();
    	BitSet b2 = Peer.getPeerInstance().getBitSet();
    	int pieceIndex = compare(b1,b2);
    	return pieceIndex;
    }

    public static int compare(BitSet lhs, BitSet rhs) {
    	if(lhs.isEmpty() && rhs.isEmpty()){
            return -1;
        }
        if(rhs.isEmpty()){
            return lhs.nextSetBit(0);
        } if (lhs.equals(rhs)) return -1;
        List<Integer> temp = new ArrayList<>(); 
        for(int i=0; i < lhs.length(); i++)
        {
        	if(!rhs.get(i))
        	{
        		temp.add(i);
        	}     	
        }
        if(temp.size()==0) return -1;
 
        int index = ThreadLocalRandom.current().nextInt(0, temp.size());
        return temp.get(index);
    }
}
