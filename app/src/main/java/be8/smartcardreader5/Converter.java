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

    public static String byte2HexStr(byte[] b, int len) {
        String stmp;
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < len; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }








}
