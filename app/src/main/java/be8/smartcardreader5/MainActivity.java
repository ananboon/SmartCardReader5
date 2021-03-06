package be8.smartcardreader5;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import android.widget.TextView;


import com.feitian.readerdk.Tool.DK;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.Iterator;

@SuppressLint({ "NewApi", "NewApi", "NewApi" })
@TargetApi(12)
public class MainActivity extends AppCompatActivity implements Runnable,View.OnClickListener {

    private static String TAG = MainActivity.class.getName();
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

//    private ArrayAdapter<String> mAdapter;
//    private List<String> list;
    private final MHandler mHandler = new MHandler(this);
    private UsbManager mUsbManager;
    private UsbDevice mDevice;

    private PendingIntent mPermissionIntent;
    private ft_reader mCard;

    private EditText mEmployeeId;
    private ImageView mPhoto;
    private TextView mTH_FULLName;
    private TextView mEN_FULLName;
    private TextView mGender;
    private TextView mBIRTH_DATE;
    private TextView mIdent;
    private TextView mAddress;
    private Button mCreateLead;
    private ProspectModel pModel;

    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    FrameLayout progressBarHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mEmployeeId = findViewById(R.id.EmployeeId);
//        mEmployeeId.setText("0000000009");
        mPhoto = findViewById(R.id.BPhoto);

        mTH_FULLName = findViewById(R.id.THName);
        mEN_FULLName = findViewById(R.id.ENName);
        mGender = findViewById(R.id.TGender);
        mBIRTH_DATE = findViewById(R.id.TBirthDate);
        mIdent = findViewById(R.id.TIdentNo);
        mAddress = findViewById(R.id.TAdress);
        mCreateLead = findViewById(R.id.BCreateLead);
        mCreateLead.setOnClickListener(this);

        progressBarHolder = findViewById(R.id.progressBarHolder);

        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
                ACTION_USB_PERMISSION), 0);

        mEmployeeId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mCreateLead.setEnabled(getEnableCreateLeadButton());
            }
        });
    }
    @Override
    public void onPause() {
        Log.d(TAG,"onPause");
        super.onPause();
        clearAllListener();

    }
    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy");
        super.onDestroy();
        clearAllListener();
    }

    private void clearAllListener(){
        unregisterReceiver(mUsbReceiver);
        clearCardAndDevice();
        this.mUsbManager = null;
    }

    private void clearCardAndDevice(){
        if(mCard != null) {
            try {
                mCard.PowerOff();
            } catch (FtBlueReadException e) {
                e.printStackTrace();
            }
            mCard.close();
            this.mCard = null;
        }
        this.mDevice = null;
    }


    @Override
    public void onResume() {
        Log.d(TAG,"onResume");
        super.onResume();
        StartCardReader();


    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }

    private void StartCardReader(){

        registerUsbManager();
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        if(deviceIterator.hasNext()){
            mDevice = deviceIterator.next();
            OpenCard(mDevice);
        }


    }

    private void OpenCard(UsbDevice mDevice){
        if(!mUsbManager.hasPermission(mDevice)) {
            mUsbManager.requestPermission(mDevice,mPermissionIntent);
        }
        if (!mUsbManager.hasPermission(mDevice)) {
            Log.d(TAG,"Don;t have Device Permission ");
            return;
        }
        mCard = new ft_reader(mUsbManager, mDevice);
        try {
            int ret = mCard.open();
            byte ReaderVersion[] = new byte[512];
            int []len = new int[1];
            mCard.getVersion(ReaderVersion, len);
            mCard.startCardStatusMonitoring(mHandler);
        } catch (Exception e) {
            Log.d(TAG,"Exception: => " + e.toString());
        }
    }

    private void registerUsbManager(){
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);// start service process
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, filter);
    }
    private void ReadCardInfo()  {
        Log.d(TAG,"Debug -- ReadCardInfo");
        //Main thread
//        this.pModel = mCard.newProspectModel();
//        pModel.transform();
//        setCustomerInfo(pModel);
        //Main thread



//        Handler handler = new Handler();
//        TaskCanCeler taskCanceler;
        ReadCard readCardTask = new ReadCard(this);
//        taskCanceler = new TaskCanCeler(readCardTask);
//        handler.postDelayed( taskCanceler, 20*1000); // task should be cancelled after 20 secs
        readCardTask.execute(mCard);

    }

    private void setCustomerInfo(ProspectModel pModel){
        Log.d(TAG,"Debug -- setCustomerInfo");
        mTH_FULLName.setText(pModel.getDisPlayTHName());
        mEN_FULLName.setText(pModel.getDisplayEnName());
        mGender.setText(pModel.getDisplayGender());
        mBIRTH_DATE.setText(pModel.getDisplayBirthDate());
        mIdent.setText(pModel.getDisplayIdentNo());
        mAddress.setText(pModel.getDisplayAddress());
        byte[] imageInByte =  pModel.getDisplayImageByte();
        Bitmap bm = BitmapFactory.decodeByteArray(imageInByte, 0, imageInByte.length);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mPhoto.setMinimumHeight(dm.heightPixels);
        mPhoto.setMinimumWidth(dm.widthPixels);
        mPhoto.setImageBitmap(bm);


    }



    private void goToSF1(String id){
       // <scheme_name>sObject/<id>/view
        // salesforce1://sObject/001D000000Jwj9v/view
        Log.d(TAG,"Debug gotoSF1");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String navigateTo = "salesforce1://sObject/"+id+"/view";
        intent.setData(Uri.parse(navigateTo));

        Log.d(TAG,"Debug gotoSF1 naviage to "+navigateTo);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        startActivity(intent);
//        finish();
    }

    private void onResponseMessage(String responseJson){
        Log.d(TAG,"Debug -- onResponseMessage ::");
        Log.d(TAG,"Debug -- responseMessage ::"+responseJson);
        try {
            JSONObject jsonObj = new JSONObject(responseJson);
            String targetId = jsonObj.getString("targetId");
            goToSF1(targetId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void doSendRequest(String messsage){
        final HTTPUtil requestUtil = new HTTPUtil(messsage);

        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    requestUtil.getAccessToken();
                    requestUtil.sendRequest();
                    String responseMessage = requestUtil.getResponseMessage();
                    onResponseMessage(responseMessage);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    private void onCardAbsent(){
        resetView();
        this.pModel = null;
    }
    private void resetView(){
        mCreateLead.setEnabled(false);
        mTH_FULLName.setText("");
        mEN_FULLName.setText("");
        mGender.setText("");
        mBIRTH_DATE.setText("");
        mIdent.setText("");
        mAddress.setText("");
//        byte[] imageInByte =  pModel.getDisplayImageByte();
//        Bitmap bm = BitmapFactory.decodeByteArray(imageInByte, 0, imageInByte.length);
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        mPhoto.setMinimumHeight(dm.heightPixels);
//        mPhoto.setMinimumWidth(dm.widthPixels);
//        mPhoto.setImageBitmap(bm);
    }

    // get notification of plug in/out
    @SuppressLint("NewApi")
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        //
        @TargetApi(12)
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                UsbDevice device =  intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
                    OpenCard(device);
                }

            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                UsbDevice device = intent
                        .getParcelableExtra(UsbManager.EXTRA_DEVICE);
                Log.d(TAG,"Del: DeviceName:  " + device.getDeviceName()
                        + "  DeviceProtocol: " + device.getDeviceProtocol()
                        + "\n");
                if (mCard != null) {
                    clearCardAndDevice();
                }

            }
        }
    };


    @Override
    public void onClick(View v) {
        if (v == mCreateLead) {
            this.pModel.EMPLOYEE_ID =  String.valueOf(mEmployeeId.getText());
            String message = pModel.transformJsonRequest();
            Log.d(TAG,"Debug --  creat Prospect message ::"+message);
//            doSendRequest(message);

            HTTPCaller caller = new HTTPCaller(this);
            caller.execute(message);

        }
    }
    private Boolean getEnableCreateLeadButton(){
        return pModel != null && mEmployeeId.length() > 0;
    }
    private static class ReadCard extends AsyncTask<ft_reader, Void, ProspectModel> {
        private final WeakReference<MainActivity> mActivity;

        private ReadCard(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            MainActivity activity = mActivity.get();
            super.onPreExecute();
            activity.inAnimation = new AlphaAnimation(0f, 1f);
            activity.inAnimation.setDuration(200);
            activity.progressBarHolder.setAnimation(activity.inAnimation);
            activity.progressBarHolder.setVisibility(View.VISIBLE);
        }

        @Override
        protected ProspectModel doInBackground(ft_reader... ft_readers) {
            MainActivity activity = mActivity.get();
            ProspectModel pModel = activity.mCard.newProspectModel();
            pModel.transform();
            return pModel;
        }

        @Override
        protected void onPostExecute(ProspectModel tempPModel) {
            MainActivity activity = mActivity.get();
            activity.pModel = tempPModel;
            activity.setCustomerInfo(tempPModel);
            activity.mCreateLead.setEnabled(activity.getEnableCreateLeadButton());

            removeAnimation(activity);

        }

        private void removeAnimation(MainActivity activity){
            activity.outAnimation = new AlphaAnimation(1f, 0f);
            activity.outAnimation.setDuration(200);
            activity.progressBarHolder.setAnimation(activity.outAnimation);
            activity.progressBarHolder.setVisibility(View.GONE);
        }

    }

    private static class HTTPCaller  extends AsyncTask<String, Void, String> {
        private final WeakReference<MainActivity> mActivity;

        private HTTPCaller(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            MainActivity activity = mActivity.get();
            super.onPreExecute();
            activity.inAnimation = new AlphaAnimation(0f, 1f);
            activity.inAnimation.setDuration(200);
            activity.progressBarHolder.setAnimation(activity.inAnimation);
            activity.progressBarHolder.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(String... message) {
//            MainActivity activity = mActivity.get();
            final HTTPUtil requestUtil = new HTTPUtil(message[0]);
            try {
                requestUtil.getAccessToken();
                requestUtil.sendRequest();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return requestUtil.getResponseMessage();
        }

        protected void onPostExecute(String msg) {
            MainActivity activity = mActivity.get();
            removeAnimation(activity);
            activity.onResponseMessage(msg);
        }

        private void removeAnimation(MainActivity activity){
            activity.outAnimation = new AlphaAnimation(1f, 0f);
            activity.outAnimation.setDuration(200);
            activity.progressBarHolder.setAnimation(activity.outAnimation);
            activity.progressBarHolder.setVisibility(View.GONE);
        }

    }

    private static class MHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;
        private MHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if(activity != null){
                switch (msg.what) {
                    case DK.CARD_STATUS:
                        switch (msg.arg1) {
                            case DK.CARD_ABSENT:
                                Log.d(TAG,"IFD card absent");
                                activity.onCardAbsent();
                                try {
                                    activity.mCard.PowerOff();
                                } catch (FtBlueReadException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case DK.CARD_PRESENT:
                                Log.d(TAG,"IFD card persent");
                                try {
                                    activity.mCard.PowerOff();
                                    activity.mCard.PowerOn();
                                    activity.ReadCardInfo();
                                } catch (FtBlueReadException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case DK.CARD_UNKNOWN:
                                Log.d(TAG,"IFD card unknown");
                                break;
                            case DK.IFD_COMMUNICATION_ERROR:
                                Log.d(TAG,"IFD IFD error");
                                break;
                        }
                    default:
                        break;
                }
            }
        }
    }
}
