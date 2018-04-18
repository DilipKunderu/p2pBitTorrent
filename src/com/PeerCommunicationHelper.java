package com;

import java.io.BufferedOutputStream;

import com.messages.Message;
import com.messages.MessageHandler;
import com.messages.MessageUtil;

public class PeerCommunicationHelper {
	public static void sendBitSetMsg(BufferedOutputStream out) throws Exception{
		MessageHandler messageHandler = new MessageHandler((byte)5,Peer.getPeerInstance().getBitSet().toByteArray());
		Message message = messageHandler.buildMessage();
		byte[] messageToSend = MessageUtil.concatenateByteArrays(MessageUtil
				.concatenateByte(message.getMessage_length(), message.getMessage_type()),message.getMessagePayload());
		out.write(messageToSend);
		out.flush();
	}
}
