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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import android.widget.TextView;

import com.feitian.readerdk.Tool.DK;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_MULTIPLE_TASK;

@SuppressLint({ "NewApi", "NewApi", "NewApi" })
@TargetApi(12)
public class MainActivity extends AppCompatActivity implements Runnable,View.OnClickListener {

    private static String TAG = MainActivity.class.getName();
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private ArrayAdapter<String> mAdapter;
    private List<String> list;

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
        mEmployeeId.setText("0000000009");
        mPhoto = findViewById(R.id.BPhoto);

        mTH_FULLName = findViewById(R.id.THName);
        mEN_FULLName = findViewById(R.id.ENName);
        mGender = findViewById(R.id.TGender);
        mBIRTH_DATE = findViewById(R.id.TBirthDate);
        mIdent = findViewById(R.id.TIdentNo);
        mAddress = findViewById(R.id.TAdress);
        mCreateLead = findViewById(R.id.BCreateLead);
        mCreateLead.setOnClickListener(this);

        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
    }
    @Override
    public void onPause() {
        Log.d(TAG,"onPause");
        super.onPause();
        unregisterReceiver(mUsbReceiver);
    }

    @Override
    public void onResume() {
        Log.d(TAG,"onResume");
        super.onResume();
        StartCardReader();
        OpenCard();
    }

    @Override
    public void onDestroy() { //
        // m_thread.stop();;
        Log.d(TAG,"onDestroy");
        super.onDestroy();
    }
    @Override
    public void run() {
        // TODO Auto-generated method stub

    }

    private void StartCardReader(){
        // find List device
        //mAdapter.clear();
        list = new ArrayList<String>();
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);// start service process
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
                ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, filter);

        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        Log.d(TAG,"======List the device======");
        int nIndex = 1;
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            Log.d(TAG,String.valueOf(nIndex++) + ": ==> " + device.getDeviceName());
            mDevice = device;
            mAdapter.add(device.getDeviceName());
        }

    }

    private void OpenCard(){
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

            Log.d(TAG,"open success ");
            Log.d(TAG,"ManufacturerName: " + mCard.getManufacturerName());
            Log.d(TAG,"Reader: " + mCard.getReaderName());
            Log.d(TAG,"DK Version:" + mCard.getDkVersion());
            byte ReaderVersion[] = new byte[512];
            int []len = new int[1];
            mCard.getVersion(ReaderVersion, len);
            Log.d(TAG,"Reader Version:"+ReaderVersion[0]+"."+ReaderVersion[1]);
            /**/
            mCard.startCardStatusMonitoring(mHandler);
        } catch (Exception e) {
            Log.d(TAG,"Exception: => " + e.toString());
        }
    }
    private void ReadCardInfo()  {
        Log.d(TAG,"Debug -- ReadCardInfo");
        //Main thread
//        this.pModel = mCard.newProspectModel();
//        pModel.transform();
//        setCustomerInfo(pModel);
        //Main thread
        new ReadCard().execute(mCard);
        Log.d(TAG,"Debug -- ReadCardInfo Complete");
        mCreateLead.setEnabled(true);
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
        intent.addFlags(FLAG_ACTIVITY_MULTIPLE_TASK);
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

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DK.CARD_STATUS:
                    switch (msg.arg1) {
                        case DK.CARD_ABSENT:
                            Log.d(TAG,"IFD card absent");
                            onCardAbsent();
                            try {
                                mCard.PowerOff();
                            } catch (FtBlueReadException e) {
                                e.printStackTrace();
                            }
                            break;
                        case DK.CARD_PRESENT:
                            Log.d(TAG,"IFD card persent");
                            try {
                                mCard.PowerOff();
                                mCard.PowerOn();
                                ReadCardInfo();
                            } catch (FtBlueReadException e) {
                                // TODO Auto-generated catch block
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
    };

    // get notification of plug in/out
    @SuppressLint("NewApi")
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        //
        @TargetApi(12)
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                UsbDevice device = (UsbDevice) intent
                        .getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
                }
                Log.d(TAG,"Add:  DeviceName:  " + device.getDeviceName()
                        + "  DeviceProtocol: " + device.getDeviceProtocol()
                        + "\n");

            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                UsbDevice device = intent
                        .getParcelableExtra(UsbManager.EXTRA_DEVICE);
                Log.d(TAG,"Del: DeviceName:  " + device.getDeviceName()
                        + "  DeviceProtocol: " + device.getDeviceProtocol()
                        + "\n");
                if (null != mCard) {
                    /* off our */
                    // finish();
                }

            }
        }
    };


    @Override
    public void onClick(View v) {
        if (v == mCreateLead) {
            Log.d(TAG,"Debug -- click create Prospect");
            Log.d(TAG,"Debug -- click  mEmployeeId.getText()"+mEmployeeId.getText());
            this.pModel.EMPLOYEE_ID = String.valueOf(mEmployeeId.getText());
            String message = pModel.transformJsonRequest();
            Log.d(TAG,"Debug --  creat Prospect message ::"+message);
            doSendRequest(message);
        }
    }

    private class ReadCard extends AsyncTask<ft_reader, Void, ProspectModel> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(200);
            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);
        }



        @Override
        protected ProspectModel doInBackground(ft_reader... ft_readers) {
            ProspectModel pModel = mCard.newProspectModel();
            pModel.transform();
            return pModel;
        }

        @Override
        protected void onPostExecute(ProspectModel pModel) {
            setCustomerInfo(pModel);
            outAnimation = new AlphaAnimation(1f, 0f);
            outAnimation.setDuration(200);
            progressBarHolder.setAnimation(outAnimation);
            progressBarHolder.setVisibility(View.GONE);
        }


    }
}
