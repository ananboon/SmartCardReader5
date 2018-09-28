package be8.smartcardreader5;

import java.io.UnsupportedEncodingException;

public class Converter {

    public static String GetUTF8FromBytes(byte[] bytes){
        String str = null;
        try {
            String theString = new String(bytes, "TIS620");
            bytes = theString.getBytes("UTF-8");
            str = new String(bytes,"UTF-8");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return str;
    }








}
