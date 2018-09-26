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

    public void formatTHFullName(){
        //For title#FirstName##LastName      ���
        Log.d(TAG,"DEBUG -- formatTHFullName TH_FULLNAME ::"+TH_FULLNAME);
        String name = this.TH_FULLNAME.split("")[0];
        String[] split = name.split("#");
        this.TH_TTL = split[0];


        this.TH_FRST_NM = split[1];
        this.TH_SURNM = split[3];
    }
    public void formatBirthDate(){
        Log.d(TAG,"DEBUG -- formatBirthDate BRTH_DT ::"+BRTH_DT);
        String[] split = this.BRTH_DT.split(endOfChar);
        BRTH_DT = split[0];
    }


    public void transform(){
        formatTHFullName();
//        formatBirthDate();
    }

    public String getTHdisPlayName() {
        return this.TH_TTL +" "+ this.TH_FRST_NM +" "+ this.TH_SURNM;
    }

    public String transformJsonRequest(){

        JSONObject dipChipModel = new JSONObject();
        JSONObject jsonParam = new JSONObject();
        try {
            dipChipModel.put("IDENT_NO", this.IDENT_NO);
            dipChipModel.put("BRTH_DT",  this.BRTH_DT);
            dipChipModel.put("TH_FULLNAME",  this.TH_FULLNAME);
            dipChipModel.put("EN_FULLNAME",  this.EN_FULLNAME);

            jsonParam.put(reponse, dipChipModel);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonParam.toString();
    }


}
