package com.server.common.util;

import java.security.MessageDigest;

public class MD5Util {  
    
    public static String getMD5String(String inStr) {
    	String md5String = null;
        try {
        	MessageDigest md5 = MessageDigest.getInstance("MD5");
        	byte[] byteArray = inStr.getBytes("UTF-8");
            byte[] md5Bytes = md5.digest(byteArray);
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            md5String = hexValue.toString();
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
        return md5String;
    }
    public static String getMD5Password(String input){
        return getMD5String(input+"Password");
    }
}
