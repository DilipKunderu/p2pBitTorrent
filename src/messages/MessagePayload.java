package messages;

import com.sun.javaws.exceptions.InvalidArgumentException;

public class MessagePayload {
    public byte[] payload;

    public MessagePayload(byte message_type) throws InvalidMessageTypeException {
        switch (message_type){
            case (byte)0:{
                break;
            }
            case (byte)1:{
                break;
            }
            case (byte)2:{
                break;
            }
            case (byte)3:{
                break;
            }
            case (byte)4:{
                break;
            }
            case (byte)5:{
                break;
            }
            case (byte)6:{
                break;
            }
            case (byte)7:{
                break;
            }
            default:{
                throw new InvalidMessageTypeException("Not a valid message type: " + message_type );
            }

        }
    }

}
