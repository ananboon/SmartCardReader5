package be8.smartcardreader5;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
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
    private static final String  client_id = "3MVG910YPh8zrcR1.6xM.eQOAiIAYRgKQlmcRyCEvLDQGCogs2_lZaGg0GB3sNW72zBmUp00_uyjd689c.10p";
    private static final String client_secret = "8584147721397286853";
    private static final String refresh_token = "5Aep861915i4NP4R5PhP5i3bV7srdSW6FwcqqR6SE7cKzmHDFyRxmgkfeYcwJNoaf8_NqYtDq3yCd5L9EQyJrRM";
    private static final String tokenURL = "https://test.salesforce.com/services/oauth2/token";
    private String urlStr = "https://kasikornbank--dipchip.cs72.my.salesforce.com/services/apexrest/DipChipService";
    private String access_token;
    private String token_type;
    private String message;
    private String responseMessage;
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
            conn.setRequestProperty("Authorization", this.token_type+" "+this.access_token);

            //open
            conn.connect();

            //setup send
            os = new BufferedOutputStream(conn.getOutputStream());
            os.write(message.getBytes());
            //clean up
            os.flush();

            //do somehting with response
            is = conn.getInputStream();
            responseMessage = readStream(is);
        }finally {
            //clean up
            os.close();
            is.close();
            conn.disconnect();
        }

    }

    public void getAccessToken(){
        String urlRarameter = "grant_type=refresh_token";
        urlRarameter += "&client_id="+client_id;
        urlRarameter += "&client_secret="+client_secret;
        urlRarameter += "&refresh_token="+refresh_token;
        HttpURLConnection conn = null;

        try {
            URL url = new URL(tokenURL+"?"+urlRarameter);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setInstanceFollowRedirects(false);
            conn.setUseCaches(false);
            conn.setRequestProperty("charset","utf-8");
            conn.setDoInput(true);

            InputStream in = new BufferedInputStream(conn.getInputStream());
            String responseJson = readStream(in);
            try {
                Log.d(TAG,"DEBUG -- responseJson ::"+responseJson);
                JSONObject jsonObj = new JSONObject(responseJson);
                this.token_type =  jsonObj.getString("token_type");
                this.access_token = jsonObj.getString("access_token");
                Log.d(TAG,"DEBUG -- jsonObject ::"+jsonObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            conn.disconnect();
        }
    }

    private String readStream(InputStream in) throws IOException {
        Log.d(TAG,"readStream ::");
        BufferedReader br=new BufferedReader(new InputStreamReader(in));
        StringBuffer sb = new StringBuffer("");
        String line;

        while((line = br.readLine()) != null) {
            sb.append(line);
            break;
        }
        return sb.toString();

    }

    public String getResponseMessage(){
        return this.responseMessage;
    }

}
