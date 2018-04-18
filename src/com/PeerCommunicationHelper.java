package com;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return data;
    }

}
