package be8.smartcardreader5;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import java.net.URL;

public class HTTPUtil {
    private static String TAG = HTTPUtil.class.getName();
    private String urlStr = "https://kasikornbank--dipchip.cs72.my.salesforce.com/services/apexrest/DipChipService";
    private String accessToken = "Bearer 00D5D0000008uo3!AREAQA09MN6XpRthybgdqW1W2UfPnxHUvsdmcw_Uak2YGTALMErEpNobTgLYoH1bI8yc4nwZlHwwJxEixDqTCQZLF7pR5Aik";
    private String message;
    public HTTPUtil(String message){
        this.message = message;
        Log.d(TAG,"Debug -- message ::"+message);
    }

    public void sendRequest() throws IOException {
        InputStream is = null;
        OutputStream os = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /*milliseconds*/);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(message.getBytes().length);
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            conn.setRequestProperty("Authorization", this.accessToken);

            //open
            conn.connect();

            //setup send
            os = new BufferedOutputStream(conn.getOutputStream());
            os.write(message.getBytes());
            //clean up
            os.flush();

            //do somehting with response
            is = conn.getInputStream();
            readStream(is);
        }finally {
            //clean up
            os.close();
            is.close();
            conn.disconnect();
        }

    }

    private void readStream(InputStream in) throws IOException {
        Log.d(TAG,"readStream ::");
        BufferedReader br=new BufferedReader(new InputStreamReader(in));
        StringBuffer sb = new StringBuffer("");
        String line="";

        while((line = br.readLine()) != null) {
            sb.append(line);
            break;
        }
        Log.d(TAG,"readStream ::"+sb.toString());

    }

}
