package FileProcessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class FileManager {
    Map<Integer,File> pieceMap;

    private void fileSplit(File inputFile, int pieceSize){
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
                pieceMap.put(count,newFilePart);
                partOfFile.write(filePiece);
                partOfFile.flush();
                partOfFile.close();
            }


        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

    }

    private void getFilePart(int filePart){

    }

    private void merge(){

    }
    public static void main(String[] args) {
        FileManager fm = new FileManager();
        fm.fileSplit(new File("dummyfile"),(int)(Math.random()*10));   //for now dummy file path and dummy number
    }
}


