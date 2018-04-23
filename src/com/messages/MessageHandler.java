package com.messages;

import com.MessageType;
import com.model.*;

public class MessageHandler {
	private MessageType message_type;
	private byte[] messagePayload;

	public MessageHandler(MessageType message_type) {
		this.message_type = message_type;
	}

	public MessageHandler(MessageType message_type, byte[] messagePayload) {
		this.message_type = message_type;
		this.messagePayload = messagePayload;
	}

	public Message buildMessage() throws Exception {
		Message message;
		switch (this.message_type) {
		case choke: {
			message = new Choke();
			break;
		}
		case unchoke: {
			message = new UnChoke();
			break;
		}
		case interested: {
			message = new Interested();
			break;
		}
		case notinterested: {
			message = new NotInterested();
			break;
		}
		case have: {
			message = new Have(this.messagePayload);
			break;
		}
		case bitfield: {
			message = new BitField(this.messagePayload);
			break;
		}
		case request: {
			message = new Request(this.messagePayload);
			break;
		}
		case piece: {
			message = new Piece(this.messagePayload);
			break;
		}
		default: {
			throw new Exception("Not a valid message type: " + message_type);
		}
		}
		return message;
	}

}
