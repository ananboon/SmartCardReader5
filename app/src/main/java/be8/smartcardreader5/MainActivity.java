package be8.smartcardreader5;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.feitian.readerdk.Tool.DK;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


@SuppressLint({ "NewApi", "NewApi", "NewApi" })
@TargetApi(12)
public class MainActivity extends AppCompatActivity implements Runnable,View.OnClickListener {

    private static String TAG = MainActivity.class.getName();
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private Spinner mSpinner;
    private ArrayAdapter<String> mAdapter;
    private List<String> list;

    private UsbManager mUsbManager;
    private UsbDevice mDevice;

    private PendingIntent mPermissionIntent;
    private ft_reader mCard;

    private TextView mTH_FULLName;
    private TextView mBIRTH_DATE;
    private TextView mIdent;
    private Button mCreateLead;
    private ProspectModel pModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTH_FULLName = findViewById(R.id.TName);
        mBIRTH_DATE = findViewById(R.id.TBirthDate);
        mIdent = findViewById(R.id.TIdentNo);

        mCreateLead = findViewById(R.id.BCreateLead);
        mCreateLead.setOnClickListener(this);

        StartCardReader();
        OpenCard();
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() { //
        // m_thread.stop();
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
        this.pModel = mCard.newProspectModel();
        pModel.transform();
        setCustomerInfo(pModel);
//        final HTTPUtil requestUtil = new HTTPUtil(jsonObj);
//
//        new Thread(new Runnable(){
//            @Override
//            public void run() {
//                try {
//                    requestUtil.sendRequest();
//                }
//                catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//        }).start();

    }

    private void setCustomerInfo(ProspectModel pModel){
        mTH_FULLName.setText(pModel.getDisPlayTHName());
        mBIRTH_DATE.setText(pModel.getDisplayBirthDate());
        mIdent.setText(pModel.getDisplayIdentNo());
    }

    private void resetView(){

    }

    private void doSendRequest(String messsage){
        final HTTPUtil requestUtil = new HTTPUtil(messsage);
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    requestUtil.sendRequest();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    private void onCardAbsent(){

    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DK.CARD_STATUS:
                    switch (msg.arg1) {
                        case DK.CARD_ABSENT:
                            Log.d(TAG,"IFD card absent");
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
            Log.d(TAG,"Debug -- click creat Prospect");
            String message = pModel.transformJsonRequest();
            Log.d(TAG,"Debug --  creat Prospect message ::"+message);
            doSendRequest(message);
        }
    }
}
