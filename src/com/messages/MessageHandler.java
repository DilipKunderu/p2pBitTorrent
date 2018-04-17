package com.messages;

import com.model.*;

public class MessageHandler {
    byte message_type;

    public MessageHandler(byte message_type) {
        this.message_type = message_type;
    }


    public Message buildMessage(byte message_type) throws InvalidMessageTypeException {
        Message message;
        switch (message_type){
            case (byte)0:{
               message = new Choke();
               break;
            }
            case (byte)1:{
                message = new UnChoke();
                break;
            }
            case (byte)2:{
                message = new Interested();
                break;
            }
            case (byte)3:{
                message = new NotInterested();
                break;
            }
//            case (byte)4:{
//                message = new Have();
//                break;
//            }
//            case (byte)5:{
//                message = new BitField();
//                break;
//            }
//            case (byte)6:{
//                message = new Request();
//                break;
//            }
//            case (byte)7:{
//                message = new Piece();
//                break;
//            }
            default:{
                throw new InvalidMessageTypeException("Not a valid message type: " + message_type );
            }
        }
        return message;
    }

}
