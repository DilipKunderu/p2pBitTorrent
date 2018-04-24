package com.FileProcessor;

import java.io.*;
import java.util.*;

import com.Constants;
import com.Peer;
import com.messages.Message;
import com.messages.MessageUtil;

public class FileManagerExecutor {

	static Map<Integer, byte[]> pieceMap;
	static Map<Integer, byte[]> fileSoFar = new TreeMap<>();

	public static void fileSplit(File inputFile, int pieceSize) {
		pieceMap = new HashMap<>();
		FileInputStream inputStream;
		int fileSize;
		int remainingFileSize;
		int bytesRead, count = 0;
		byte[] filePiece;
		try {
			inputStream = new FileInputStream(inputFile);
			fileSize = Constants.getFileSize();
			remainingFileSize = fileSize;
			while (fileSize > 0) {
				if (remainingFileSize < pieceSize) {
					filePiece = new byte[remainingFileSize];
				} else {
					filePiece = new byte[pieceSize];
				}
				bytesRead = inputStream.read(filePiece);
				fileSize -= bytesRead;
				pieceMap.put(count, filePiece);
				count++;
				remainingFileSize -= pieceSize;
			}
			inputStream.close();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}

	}

	public static byte[] getFilePart(int filePartNumber) {
		if (fileSoFar.get(filePartNumber) == null)
			return pieceMap.get(filePartNumber);
		else
			return fileSoFar.get(filePartNumber);
	}

	public static void acceptFilePart(int filePart, Message message) {
		byte[] payLoadWithIndex = message.getMessagePayload();
		byte[] payLoad = MessageUtil.removeFourBytes(payLoadWithIndex);
		fileSoFar.put(filePart, payLoad);
	}

	public static void filesmerge() throws IOException {
		FileOutputStream fileOutputStream;
		File mergeFile = new File(Constants.root + "/peer_" + String.valueOf(Peer.getPeerInstance().get_peerID()) + "/"
				+ Constants.getFileName());
		byte[] combinedFile = new byte[Constants.getFileSize()];
		int count= 0;
		for (Map.Entry<Integer, byte[]> e : fileSoFar.entrySet()) {
			for(int i=0;i<e.getValue().length;i++){
				combinedFile[count] = e.getValue()[i];
				count++;
			}
		}
		fileOutputStream = new FileOutputStream(mergeFile);
		fileOutputStream.write(combinedFile);
		fileOutputStream.flush();
		fileOutputStream.close();
	}
}
