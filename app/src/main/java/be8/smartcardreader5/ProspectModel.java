package be8.smartcardreader5;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class ProspectModel {
    private static String TAG = ProspectModel.class.getName();
    private String IDENT_NO;
    private String BRTH_DT;
    private String TH_TTL;
    private String TH_FRST_NM;
    private String TH_SURNM;
    private String EN_TTL;
    private String EN_FRST_NM;
    private String EN_SURNM;
    private String IMAGE;

    private String TH_FULLNAME;
    private String EN_FULLNAME;

    private static String reponse = "jsonResponse";
    private static String endOfChar = "\u0090";
    public ProspectModel(){}

    public void setTHFullName(String thFullName){
        this.TH_FULLNAME = thFullName;
    }

    public void setENFullName(String enFullName){
        this.EN_FULLNAME = enFullName;
    }
    public void setIdentificationNo(String identNo){
        this.IDENT_NO = identNo;
    }
    public void setBirthDate(String birthDate){
        this.BRTH_DT = birthDate;
    }
    public void setImage(String image){
        this.IMAGE = image;
    }

    private void formatTHFullName(){
        //For title#FirstName##LastName      ���
        Log.d(TAG,"DEBUG -- formatTHFullName TH_FULLNAME ::"+TH_FULLNAME);
        String name = this.TH_FULLNAME.split(" ")[0];
        String[] split = name.split("#");
        this.TH_TTL = split[0];
        this.TH_FRST_NM = split[1];
        this.TH_SURNM = split[3];
    }
    private void formatENFullName(){
        //For title#FirstName##LastName      ���
        Log.d(TAG,"DEBUG -- formatENFullName EN_FULLNAME ::"+EN_FULLNAME);
        String name = this.EN_FULLNAME.split(" ")[0];
        String[] split = name.split("#");
        this.EN_TTL = split[0];
        this.EN_FRST_NM = split[1];
        this.EN_SURNM = split[3];
        Log.d(TAG,"DEBUG -- after formatENFullName EN_FULLNAME ::"+EN_FULLNAME);
    }

    private void formatBirthDate(){
        // yearMonthDay 20180226
        Log.d(TAG,"DEBUG -- before formatBirthDate BRTH_DT ::"+BRTH_DT);
        String[] split = this.BRTH_DT.split(endOfChar);
        String temp_BRTH_DT = split[0];
        BRTH_DT = temp_BRTH_DT.substring(0,4) + "-" + temp_BRTH_DT.substring(4,6) + "-" + temp_BRTH_DT.substring(6,8);
        Log.d(TAG,"DEBUG -- after formatBirthDate BRTH_DT ::"+BRTH_DT);
    }
    private void formatIdent(){
        // xxxxxxxxxxxx
        Log.d(TAG,"DEBUG -- before formatIdent IDENT_NO ::"+IDENT_NO);
        String[] split = this.IDENT_NO.split(endOfChar);
        this.IDENT_NO = split[0];
        Log.d(TAG,"DEBUG -- after formatIdent IDENT_NO ::"+IDENT_NO);
    }


    public void transform(){
        formatTHFullName();
        formatENFullName();
        formatBirthDate();
        formatIdent();
    }

    public String getDisPlayTHName() {
        return this.TH_TTL +" "+ this.TH_FRST_NM +" "+ this.TH_SURNM;
    }

    public String getDisplayBirthDate(){
        return this.BRTH_DT;
    }

    public String getDisplayIdentNo(){
        return this.IDENT_NO;
    }

    public String transformJsonRequest(){

        JSONObject dipChipModel = new JSONObject();
        JSONObject jsonParam = new JSONObject();
        try {
            dipChipModel.put("IDENT_NO", this.IDENT_NO);
            dipChipModel.put("BRTH_DT",  this.BRTH_DT);

            dipChipModel.put("TH_TTL",  this.TH_TTL);
            dipChipModel.put("TH_FRST_NM",  this.TH_FRST_NM);
            dipChipModel.put("TH_SURNM",  this.TH_SURNM);

            dipChipModel.put("EN_TTL",  this.EN_TTL);
            dipChipModel.put("EN_FRST_NM",  this.EN_FRST_NM);
            dipChipModel.put("EN_SURNM",  this.EN_SURNM);

            dipChipModel.put("IMAGE",  this.IMAGE);
            jsonParam.put(reponse, dipChipModel);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonParam.toString();
    }


}
