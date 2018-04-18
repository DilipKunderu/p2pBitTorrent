package com.messages;

import java.util.BitSet;

public class MessageUtil {
	
	public static byte[] concatenateByteArrays(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
	//int to byte array
	 public static byte[] intToByteArray(final int integer) {
	        byte[] result = new byte[4];

	        result[0] = (byte) ((integer & 0xFF000000) >> 24);
	        result[1] = (byte) ((integer & 0x00FF0000) >> 16);
	        result[2] = (byte) ((integer & 0x0000FF00) >> 8);
	        result[3] = (byte) (integer & 0x000000FF);

	        return result;
	    }
	 //biset to byteArray
/*	 public static byte[] toByteArray(BitSet bits) {
		 byte[] bytes = new byte[(bits.length() + 7) / 8];
		    for (int i=0; i<bits.length(); i++) {
		        if (bits.get(i)) {
		            bytes[bytes.length-i/8-1] |= 1<<(i%8);
		        }
		    }
		    return bytes;
		}*/
	 
	 public static byte[] concatenateByte(byte[] a, byte b) {
	        byte[] result = new byte[a.length + 1];
	        System.arraycopy(a, 0, result, 0, a.length);
	        result[a.length] = b;
	        return result;
	    }
}
