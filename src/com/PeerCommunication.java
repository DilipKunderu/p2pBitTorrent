package com;

import com.FileProcessor.FileManagerExecutor;
import com.messages.Handshake;
import com.messages.Message;
import com.messages.MessageUtil;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.BitSet;

public class PeerCommunication {
    RemotePeerInfo remote;
    Socket socket;
    Handshake handshake;
 //   BufferedOutputStream out;
 //   BufferedInputStream in;
    ObjectInputStream in;
  public  ObjectOutputStream out;
    int recentReceievdPiece;
    Long downloadStart;
    Long downloadEnd;
    boolean flag;
    boolean terminateFlag = true;
    
//	EventLogger log = new EventLogger(Peer.getPeerInstance().get_peerID());

   public PeerCommunication (RemotePeerInfo remotePeerInfo) throws ClassNotFoundException {
        this.remote = remotePeerInfo;
        this.socket = null;
        initSocket();
    }

    public PeerCommunication (RemotePeerInfo remotePeerInfo, Socket socket) throws ClassNotFoundException {
       this.remote = remotePeerInfo;
       this.socket = socket;
       initSocket();
    }

    private void initSocket() throws ClassNotFoundException {
        try{
            if (socket == null){
                this.socket = new Socket (InetAddress.getByName(this.remote.get_hostName()), this.remote.get_portNo());
            }
       //     this.out = new BufferedOutputStream(this.socket.getOutputStream());
            this.out = new ObjectOutputStream((this.socket.getOutputStream()));
            this.in = new ObjectInputStream((this.socket.getInputStream()));
            this.remote.objectOutputStream = this.out;
            this.out.flush();
        //    this.in = new BufferedInputStream(this.socket.getInputStream());
//            this.remote.bufferedInputStream = this.in;

        } catch (IOException e) {
            throw new RuntimeException("Could not open client socket", e);
        }

            this.handshake = new Handshake(Peer.getPeerInstance().get_peerID());

        try {
            this.handshake.sendHandshakeMsg(this.out, this.handshake);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if(this.handshake.recieveHandshake(this.in)){
                    System.out.println("Recieved Handshake");
                }
                else{
                    //TODO logger
                }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        } catch (IOException e) {
//            throw new RuntimeException("Could not open client socket", e);
//        }
    }
    
    public void startMessageExchange () throws Exception {
        Message message = null;
        byte[] pieceIndexField = null;
    	if(!Peer.getPeerInstance().getBitSet().isEmpty()){
    		message = PeerCommunicationHelper.sendBitSetMsg(this.out);
    	}
    	while(terminateFlag){
    		
    	    Message message1 = PeerCommunicationHelper.getActualObjectMessage(this.in);
    	    byte msgType = message1.getMessage_type();
    	    byte[] msgPayloadReceived = message1.getMessagePayload();
//    		byte msgType = PeerCommunicationHelper.getMessageType(this.in);
//    		byte[] msgPayloadReceived = PeerCommunicationHelper.getActualMessage(this.in);
    		if(this.flag && msgType != (byte)7){
    			this.downloadStart = 0L;
    		}
    		if(msgType == (byte)7 || msgType == (byte)4){
        		pieceIndexField = new byte[4];
        		for(int i=0;i<4;i++){
        			pieceIndexField[i] = msgPayloadReceived[i];
        		}
    		}
    		
    		switch(msgType){
    		//choke(If choked map maintained put in it or else nothing to do. 
    		case (byte)0:{
//                Peer.peer.log.choking(this.remote.get_peerID());
    		//	while (this.in.available() == 0) {}
    			while(in.readObject()==null){ }
    				break;
    		}
    		//bitset(In this send interesetd or Not Interseted)
    		case (byte)5:{
    	    	BitSet bitset = MessageUtil.fromByteArray(msgPayloadReceived);
    	    	this.remote.setBitfield(bitset);
    			if(PeerCommunicationHelper.isInterseted(bitset,Peer.getPeerInstance().getBitSet())){
    	    		message = PeerCommunicationHelper.sendInterestedMsg(this.out);
    	    	}
    	    	else{
    	    		message = PeerCommunicationHelper.sendNotInterestedMsg(this.out);
    	    	}
    			break;
    		}
    		
    		//Interseted(put in interseted map and compare bitset and send have msg)
    		case (byte)2:{
        		Peer.getPeerInstance().peersInterested.putIfAbsent(this.remote.get_peerID(), this.remote);
//                Peer.peer.log.interested(this.remote.get_peerID());
               if (Peer.getPeerInstance().preferredNeighbours.containsKey(this.remote)) {
                    int haveIndexField = PeerCommunicationHelper.compare(Peer.getPeerInstance().getBitSet(), this.remote.getBitfield());
                    if (haveIndexField > -1){
                        PeerCommunicationHelper.sendHaveMsg(this.out, haveIndexField);
                }
                    }
        		break;
    		}
    		
    		//Not Interested (remove from the interseted map)
    		case (byte)3:{
//                Peer.peer.log.notInterested(this.remote.get_peerID());
    			if(this.remote.getBitfield().equals(Peer.getPeerInstance().idealBitset)){
    				terminateFlag = false;
    			}
    			if(Peer.getPeerInstance().peersInterested.containsKey(this.remote.get_peerID()))
    	    		Peer.getPeerInstance().peersInterested.remove(this.remote.get_peerID());
    			break;
    		}
    		
    		//Have (
    		case (byte)4:{
    			//check is prefreferref neighbours and optimistically unchoked
    			//request or else send not interested
    			this.remote.getBitfield().set(MessageUtil.byteArrayToInt(msgPayloadReceived));
    		    if(Peer.getPeerInstance().preferredNeighbours.containsKey(this.remote) || Peer.getPeerInstance().getOptimisticallyUnchokedNeighbour() == this.remote)
    		    	PeerCommunicationHelper.sendRequestMsg(this.out, this.remote);
    		    else
    		    	PeerCommunicationHelper.sendNotInterestedMsg(this.out);
//                Peer.peer.log.have(this.remote.get_peerID(), MessageUtil.byteArrayToInt(pieceIndexField));
    			break;
    			
    		}
    		
    		//Unchoke(send request msg or else Not interested)
    		case (byte)1:{
    			int pieceIndex = PeerCommunicationHelper.getPieceIndex(this.remote);
//                Peer.peer.log.unchoking(this.remote.get_peerID());
    			if(pieceIndex!=-1){
    				message = PeerCommunicationHelper.sendRequestMsg(this.out,this.remote);
    				this.downloadStart = System.nanoTime();
    				this.flag=true;
    			}
    			if(pieceIndex == 1){
    				message = PeerCommunicationHelper.sendNotInterestedMsg(this.out);
    			}
    			break;
    		}
    		
    		//Request (send piece msg if preferred neighbour or optimistically unchoked
    		case (byte)6:{
    			//TODO write condition
    		    if(Peer.getPeerInstance().preferredNeighbours.containsKey(this.remote) || Peer.getPeerInstance().getOptimisticallyUnchokedNeighbour() == this.remote)
    			PeerCommunicationHelper.sendPieceMsg(this.out, MessageUtil.byteArrayToInt(msgPayloadReceived));
    		    this.downloadEnd = System.nanoTime();
    		 //   this.remote.setDownload_rate(this.downloadEnd-this.downloadStart);
    			break;
    		}
    		//download the arrived piece.
            //update the bitset.
            //send have message to the preferred neighbour list
    		//compare bitsets again and send request
    		case (byte)7:{
    			//Try to do a check
    			FileManagerExecutor.acceptFilePart(MessageUtil.byteArrayToInt(pieceIndexField),message1);
    			if(!Peer.getPeerInstance().getBitSet().get(MessageUtil.byteArrayToInt(pieceIndexField))){
        			Peer.getPeerInstance().getBitSet().set(MessageUtil.byteArrayToInt(pieceIndexField));
        			Peer.getPeerInstance().sendHaveToAll(MessageUtil.byteArrayToInt(pieceIndexField));
    			}
    			
    			int numberOfPieces = Peer.getPeerInstance().getBitSet().cardinality();
//                Peer.peer.log.downloadAPiece(Peer.getPeerInstance().get_peerID(),MessageUtil.byteArrayToInt(pieceIndexField),numberOfPieces);
    			PeerCommunicationHelper.sendRequestMsg(this.out, this.remote);
    			break;
    		}
    	}//switch end
    		if(Peer.getPeerInstance().get_hasFile()!=1 && Peer.getPeerInstance().getBitSet().equals(Peer.getPeerInstance().idealBitset)){
    			FileManagerExecutor.filesmerge();
    			terminateFlag = false;
    			
    		}
    	}//while end
    	}
    }

