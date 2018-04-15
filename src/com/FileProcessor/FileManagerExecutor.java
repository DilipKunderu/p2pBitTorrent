package com.FileProcessor;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class FileManagerExecutor implements FileManager {
    Map<Integer,File> pieceMap;
    Map<Integer,File> fileSoFar = new TreeMap<>();

    public void fileSplit(File inputFile, int pieceSize){
        pieceMap = new HashMap<>();
        FileInputStream inputStream;
        FileOutputStream partOfFile;
        File newFilePart;
        int fileSize;
        int bytesRead,count = 0;
        byte[] filePiece;
        try{
            inputStream = new FileInputStream(inputFile);
            fileSize = (int)inputFile.length();
            while(fileSize>0){
                filePiece = new byte[pieceSize];
                bytesRead = inputStream.read(filePiece);
                fileSize-=bytesRead;
                count++;
                newFilePart = new File("Part"+Integer.toString(count));
                partOfFile = new FileOutputStream(newFilePart);
                partOfFile.write(filePiece);
                pieceMap.put(count,newFilePart);
                partOfFile.flush();
                partOfFile.close();
            }
            inputStream.close();

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

    }

    public void sendFilePart(int filePart, Socket socket) {
        File fileToSend;
        FileInputStream fileInputStream;
        BufferedInputStream bufferedInputStream;
        OutputStream outputStream;
        fileToSend = pieceMap.get(filePart);
        byte[] byteFile = new byte[(int) fileToSend.length()];
        try {

            fileInputStream = new FileInputStream(fileToSend);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            bufferedInputStream.read(byteFile, 0, byteFile.length);
            outputStream = socket.getOutputStream();
            outputStream.write(byteFile, 0, byteFile.length);
            outputStream.flush();

        }catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public void getFilePart(int filePart, Socket socket) {
        int fileSize = 10000000;                                         //resolve the filesize
        FileOutputStream fileOutputStream;
        InputStream inputStream;
        BufferedOutputStream bufferedOutputStream;
        File fileToWrite;
        int bytesRead, currTotal;
        byte[] byteFile = new byte[fileSize];
        try {

            inputStream = socket.getInputStream();
            fileToWrite = new File("Part" + Integer.toString(filePart));
            fileOutputStream = new FileOutputStream(fileToWrite);
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            bytesRead = inputStream.read(byteFile, 0, byteFile.length);
            currTotal = bytesRead;
            do {
                bytesRead = inputStream.read(byteFile, currTotal, byteFile.length - currTotal);
                if (bytesRead >= 0) currTotal += bytesRead;
            } while (bytesRead > -1);
            bufferedOutputStream.write(byteFile, 0, currTotal);
            fileSoFar.put(filePart, fileToWrite);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();

        }catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public void filesmerge(){
        File mergeFile = new File("SharedFile");    // change file name
        FileOutputStream fileOutputStream;
        FileInputStream fileInputStream;
        byte[] fileBytes;
        int bytesRead=0;
        try{
            fileOutputStream = new FileOutputStream(mergeFile,true);
            Set<Integer> keys = fileSoFar.keySet();

            for(Integer key : keys){
                fileInputStream = new FileInputStream(fileSoFar.get(key));
                fileBytes = new byte[(int)fileSoFar.get(key).length()];
                bytesRead = fileInputStream.read(fileBytes,0,(int) fileSoFar.get(key).length());
                fileOutputStream.write(fileBytes);
                fileOutputStream.flush();
                fileInputStream.close();
            }
            fileOutputStream.close();

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        FileManagerExecutor fm = new FileManagerExecutor();
        fm.fileSplit(new File("dummyfile"),(int)(Math.random()*10));   //for now dummy file path and dummy number
    }
}


