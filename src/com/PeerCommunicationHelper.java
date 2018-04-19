package com;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.BitSet;

import com.messages.Message;
import com.messages.MessageHandler;
import com.messages.MessageUtil;

public class PeerCommunicationHelper {
	
	public static Message sendBitSetMsg(BufferedOutputStream out) throws Exception{
		MessageHandler messageHandler = new MessageHandler((byte)5,Peer.getPeerInstance().getBitSet().toByteArray());
		Message message = messageHandler.buildMessage();
		byte[] messageToSend = MessageUtil.concatenateByteArrays(MessageUtil
				.concatenateByte(message.getMessage_length(), message.getMessage_type()),message.getMessagePayload());
		out.write(messageToSend);
		out.flush();

		return message;
	}
	
	public static Message sendInterestedMsg(BufferedOutputStream out) throws Exception{
		MessageHandler messageHandler = new MessageHandler((byte)2);
		Message message = messageHandler.buildMessage();
		byte[] messageToSend = MessageUtil.concatenateByte(message.getMessage_length(), message.getMessage_type());
		out.write(messageToSend);
		out.flush();
		return message;
	}
	
	public static Message sendNotInterestedMsg(BufferedOutputStream out) throws Exception{
		MessageHandler messageHandler = new MessageHandler((byte)3);
		Message message = messageHandler.buildMessage();
		byte[] messageToSend = MessageUtil.concatenateByte(message.getMessage_length(), message.getMessage_type());
		out.write(messageToSend);
		out.flush();
		return message;
	}
	
	public static Message sendChokeMsg(BufferedOutputStream out) throws Exception{
		MessageHandler messageHandler = new MessageHandler((byte)0);
		Message message = messageHandler.buildMessage();
		byte[] messageToSend = MessageUtil.concatenateByte(message.getMessage_length(), message.getMessage_type());
		out.write(messageToSend);
		out.flush();
		return message;
	}
	
	public static Message sendUnChokeMsg(BufferedOutputStream out) throws Exception{
		MessageHandler messageHandler = new MessageHandler((byte)1);
		Message message = messageHandler.buildMessage();
		byte[] messageToSend = MessageUtil.concatenateByte(message.getMessage_length(), message.getMessage_type());
		out.write(messageToSend);
		out.flush();
		return message;
	}

    public static byte[] readActualMessage(BufferedInputStream in) {
        byte[] lengthByte = new byte[4];
        int read = -1;
        byte[] data = null;
        try {
            read = in.read(lengthByte);
            if (read != 4) {
                System.out.println("Message length is not proper!!!");
            }
            int dataLength = MessageUtil.byteArrayToInt(lengthByte);
            //read msg type
            byte[] msgType = new byte[1];
            in.read(msgType);
            if (msgType[0] == (byte)5) {
                int actualDataLength = dataLength - 1;
                data = new byte[actualDataLength];
                data = MessageUtil.readBytes(in, data, actualDataLength);
            } else {
                System.out.println("Wrong message type sent");
            }

        } catch (IOException e) {
            System.out.println("Could not read length of actual message");
            e.printStackTrace();
        }
        return data;
    }
    
    public static boolean isInterseted(BitSet b1, BitSet b2){
    	for(int i=0; i<b2.length();i++){
    		if(b1.get(i)!=b2.get(i)){
    			return false;
    		}
    	}
		return true;
    }
    
    public static byte checkRecievedMsg(BufferedInputStream in) throws IOException{
    	byte[] msg = new byte[5];
    	in.read(msg);
    	 return msg[4];
    }

}
