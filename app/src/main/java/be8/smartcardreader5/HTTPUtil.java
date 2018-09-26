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
    private String accessToken = "Bearer 00D5D0000008uo3!AREAQJXoN9vPN52HAmjS7ZZaQk9hdOxvUAfU5e.ZySVr5Ovp1TG3eO968I0y7.dS0SN6UPWV.PzxWbVCTTxf0784MCzRO2oo";
    private String message;
    public HTTPUtil(String message){
        this.message = message;
//        this.message = mockMessage();
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

//    private void sendRequest2() throws JSONException, IOException {
//        //can catch a variety of wonderful things
//        InputStream is = null;
//        OutputStream os = null;
//        HttpURLConnection conn = null;
//        JSONObject dipChipModel = new JSONObject();
//        dipChipModel.put("IDENT_NO", "xxxxxxxxxxxxx");
//        dipChipModel.put("BRTH_DT", "");
//        dipChipModel.put("TH_TTL", "");
//
//        JSONObject jsonParam = new JSONObject();
//        jsonParam.put("jsonResponse", dipChipModel);
//
//        try {
//            //constants
//            URL url = new URL("https://kasikornbank--dipchip.cs72.my.salesforce.com/services/apexrest/DipChipService");
//            String message = jsonParam.toString();
//
//            conn = (HttpURLConnection) url.openConnection();
//            conn.setReadTimeout( 10000 /*milliseconds*/ );
//            conn.setConnectTimeout( 15000 /* milliseconds */ );
//            conn.setRequestMethod("POST");
//            conn.setDoInput(true);
//            conn.setDoOutput(true);
//            conn.setFixedLengthStreamingMode(message.getBytes().length);
//
//            //make some HTTP header nicety
//            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
//            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
//            conn.setRequestProperty("Authorization", "Bearer 00D5D0000008uo3!AREAQEYKwjY.oLmdKP1qBrqhaXgQBrlVNMAPGl9qs0Ybs8LpbDTOxsAoyb2ialyGiR19RPFeTbSbNdPBKvd5mP6Z2S5yftU.");
//
//            //open
//            conn.connect();
//
//            //setup send
//            os = new BufferedOutputStream(conn.getOutputStream());
//            os.write(message.getBytes());
//            //clean up
//            os.flush();
//
//            //do somehting with response
//            is = conn.getInputStream();
//            readStream(is);
//        } catch (ProtocolException e) {
//            e.printStackTrace();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            //clean up
//            os.close();
//            is.close();
//            conn.disconnect();
//        }
//    }
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

    private String mockMessage(){

        JSONObject dipChipModel = new JSONObject();
        JSONObject jsonParam = new JSONObject();
        try {
            dipChipModel.put("IDENT_NO", "xxxxxxxxxxxxx");
            dipChipModel.put("BRTH_DT", "");
            dipChipModel.put("TH_TTL", "");

            jsonParam.put("jsonResponse", dipChipModel);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonParam.toString();
    }
}
