package be8.smartcardreader5;

import android.annotation.SuppressLint;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.util.Log;

import com.feitian.reader.devicecontrol.Card;
import org.apache.commons.lang3.ArrayUtils;

import java.io.UnsupportedEncodingException;

public class ft_reader {
    private static String TAG = ft_reader.class.getName();
    private boolean isPowerOn = false;
    private Card inner_card;


    private byte[] req = new byte[]{0x00, (byte)0xc0, 0x00, 0x00};
    // Check card
    private byte[] SELECT = new byte[]{0x00,(byte)0xA4, 0x04, 0x00, 0x08};
    private byte[] THAI_CARD = new byte[]{(byte)0xA0, 0x00, 0x00, 0x00, 0x54, 0x48, 0x00, 0x01};

    // # CID
    private byte[] CMD_CID = new byte[]{(byte)0x80, (byte)0xb0, 0x00, 0x04, 0x02, 0x00, 0x0d};
    private byte[] CMD_THFULLNAME = new byte[]{(byte)0x80, (byte)0xb0, 0x00, 0x11, 0x02, 0x00, 0x64};
    private byte[] CMD_ENFULLNAME = new byte[]{(byte)0x80, (byte)0xb0, 0x00, 0x75, 0x02, 0x00, 0x64};
    private byte[] CMD_BIRTH = new byte[]{(byte)0x80, (byte)0xb0, 0x00, (byte)0xD9, 0x02, 0x00, 0x08};
    private byte[] CMD_GENDER = new byte[]{(byte)0x80, (byte)0xb0, 0x00,(byte) 0xE1, 0x02, 0x00, 0x01};
    private byte[] CMD_ISSUER = new byte[]{(byte)0x80, (byte)0xb0, 0x00, (byte)0xF6, 0x02, 0x00, 0x64};
    private byte[] CMD_ISSUE = new byte[]{(byte)0x80, (byte)0xb0, 0x01, 0x67, 0x02, 0x00, 0x08};
    private byte[] CMD_EXPIRE = new byte[]{(byte)0x80, (byte)0xb0, 0x01, 0x6F, 0x02, 0x00, 0x08};
    private byte[] CMD_ADDRESS = new byte[]{(byte)0x80, (byte)0xb0, 0x15, 0x79, 0x02, 0x00, 0x64};

    private byte[] CMD_PHOTO1 = new byte[]{(byte)0x80, (byte)0xb0, 0x01, 0x7B, 0x02, 0x00, (byte)0xFF};
    private byte[] CMD_PHOTO2 = new byte[]{(byte)0x80, (byte)0xb0, 0x02, 0x7A, 0x02, 0x00, (byte)0xFF};
    private byte[] CMD_PHOTO3 = new byte[]{(byte)0x80, (byte)0xb0, 0x03, 0x79, 0x02, 0x00, (byte)0xFF};
    private byte[] CMD_PHOTO4 = new byte[]{(byte)0x80, (byte)0xb0, 0x04, 0x78, 0x02, 0x00, (byte)0xFF};
    private byte[] CMD_PHOTO5 = new byte[]{(byte)0x80, (byte)0xb0, 0x05, 0x77, 0x02, 0x00, (byte)0xFF};
    private byte[] CMD_PHOTO6 = new byte[]{(byte)0x80, (byte)0xb0, 0x06, 0x76, 0x02, 0x00, (byte)0xFF};
    private byte[] CMD_PHOTO7 = new byte[]{(byte)0x80, (byte)0xb0, 0x07, 0x75, 0x02, 0x00, (byte)0xFF};
    private byte[] CMD_PHOTO8 = new byte[]{(byte)0x80, (byte)0xb0, 0x08, 0x74, 0x02, 0x00, (byte)0xFF};
    private byte[] CMD_PHOTO9 = new byte[]{(byte)0x80, (byte)0xb0, 0x09, 0x73, 0x02, 0x00, (byte)0xFF};
    private byte[] CMD_PHOTO10 = new byte[]{(byte)0x80, (byte)0xb0, 0x0A, 0x72, 0x02, 0x00,(byte)0xFF};
    private byte[] CMD_PHOTO11 = new byte[]{(byte)0x80, (byte)0xb0, 0x0B, 0x71, 0x02, 0x00,(byte)0xFF};
    private byte[] CMD_PHOTO12 = new byte[]{(byte)0x80, (byte)0xb0, 0x0C, 0x70, 0x02, 0x00, (byte)0xFF};
    private byte[] CMD_PHOTO13 = new byte[]{(byte)0x80, (byte)0xb0, 0x0D, 0x6F, 0x02, 0x00, (byte)0xFF};
    private byte[] CMD_PHOTO14 = new byte[]{(byte)0x80, (byte)0xb0, 0x0E, 0x6E, 0x02, 0x00, (byte)0xFF};
    private byte[] CMD_PHOTO15 = new byte[]{(byte)0x80, (byte)0xb0, 0x0F, 0x6D, 0x02, 0x00, (byte)0xFF};
    private byte[] CMD_PHOTO16 = new byte[]{(byte)0x80, (byte)0xb0, 0x10, 0x6C, 0x02, 0x00, (byte)0xFF};
    private byte[] CMD_PHOTO17 = new byte[]{(byte)0x80, (byte)0xb0, 0x11, 0x6B, 0x02, 0x00, (byte)0xFF};
    private byte[] CMD_PHOTO18 = new byte[]{(byte)0x80, (byte)0xb0, 0x12, 0x6A, 0x02, 0x00, (byte)0xFF};
    private byte[] CMD_PHOTO19 = new byte[]{(byte)0x80, (byte)0xb0, 0x13, 0x69, 0x02, 0x00, (byte)0xFF};
    private byte[] CMD_PHOTO20 = new byte[]{(byte)0x80, (byte)0xb0, 0x14, 0x68, 0x02, 0x00, (byte)0xFF};

    @SuppressLint({ "NewApi", "NewApi" })
    public ft_reader(UsbManager mUsbManager, UsbDevice mDevice) {
        // public Card(UsbManager mManager, UsbDevice mDev,Handler mHandler)
        inner_card = new Card(mUsbManager, mDevice, null);
        // TODO Auto-generated constructor stub
    }

    public String getReaderName() {
        return inner_card.getReaderName();
    }

    public String getManufacturerName() {
        return inner_card.getManufacturerName();
    }

    public int open() throws FtBlueReadException {
        int ret = inner_card.open();
        if (ret != STATUS.RETURN_SUCCESS) {
            if (ret == STATUS.READER_NOT_SUPPORT) {
                throw new FtBlueReadException(
                        "sorry we just support FeiTian reader");
            }
            throw new FtBlueReadException("open device error");
        }
        return ret;
    }

    public boolean isPowerOn() {
        return isPowerOn;
    }

    public int PowerOn() throws FtBlueReadException {
        // if(inner_card.getcardStatus() == STATUS.CARD_ABSENT){
        // throw new FtBlueReadException("card is absent");
        // }
        int ret = inner_card.PowerOn();
        if (ret != STATUS.RETURN_SUCCESS) {
            throw new FtBlueReadException("Power On Failed");
        }
        isPowerOn = true;
        return ret;
    }

    public int PowerOff() throws FtBlueReadException {
        int ret = inner_card.PowerOff();
        if (ret != STATUS.RETURN_SUCCESS) {
            throw new FtBlueReadException("Power On Failed");
        }
        isPowerOn = false;
        return ret;
    }
    public String getDkVersion(){
        return inner_card.GetDkVersion();
    }
    public int getVersion(byte []recvBuf,int []recvBufLen){
        return inner_card.getVersion(recvBuf, recvBufLen);
    }
    /*==--==*/
    public int getSerialNum(byte[] serial,int serialLen[]){
        return inner_card.FtGetSerialNum(serial, serialLen);
    }
    public int readFlash(byte[] buf,int offset,int len){
        return inner_card.FtReadFlash(buf, offset, len);
    }
    public int writeFlash(byte[] buf,int offset,int len){
        return inner_card.FtWriteFlash(buf, offset, len);
    }
    public int getProtocol() throws FtBlueReadException {
        if (isPowerOn == false) {
            throw new FtBlueReadException("Power Off already");
        }
        return inner_card.getProtocol();
    }

    public byte[] getAtr() throws FtBlueReadException {
        if (isPowerOn == false) {
            throw new FtBlueReadException("Power Off already");
        }
        return inner_card.getAtr();
    }

    public int getCardStatus() {
        return inner_card.getcardStatus();
    }

    public void startCardStatusMonitoring(Handler Handler)
            throws FtBlueReadException {
        if (inner_card.registerCardStatusMonitoring(Handler) != 0) {
            throw new FtBlueReadException("not support cardStatusMonitoring");
        }
    }

    public int transApdu(int tx_length, final byte tx_buffer[],
                         int rx_length[], final byte rx_buffer[]) throws FtBlueReadException {

        int ret = 0;
        if (isPowerOn == false) {
            throw new FtBlueReadException("Power Off already");
        }

        ret = inner_card.transApdu(tx_length, tx_buffer, rx_length,
                rx_buffer);

        if (ret == STATUS.BUFFER_NOT_ENOUGH) {
            throw new FtBlueReadException("receive buffer not enough");
        } else if (ret == STATUS.TRANS_RETURN_ERROR) {
            throw new FtBlueReadException("trans apdu error");
        }

        return ret;

    }

    private void delay(int ms){
        try {
            Thread.currentThread();
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int close() {
        // TODO Auto-generated method stub
        return inner_card.close();
    }
    /* for dukpt test */

    public ProspectModel newProspectModel(){
        Log.d(TAG,"newProspectModel");

        firstConnect();
        String identNo = getCID();
        String THFullName = getTHFullName();
        String ENFullName = getENFullName();
        String BirthDate = getBirthDate();
        String image = getImage();

        ProspectModel pModel = new ProspectModel();
        pModel.setTHFullName(THFullName);
        pModel.setENFullName(ENFullName);
        pModel.setIdentificationNo(identNo);
        pModel.setBirthDate(BirthDate);
        pModel.setImage(image);

        return pModel;
    }

    private void firstConnect(){
        inner_card.transApdu(SELECT.length+THAI_CARD.length, ArrayUtils.addAll(SELECT,THAI_CARD), new int[2], new byte[1024]);
    }
    private String getCID(){
        byte[] array = getData(CMD_CID);
        String identNo = Converter.getUTF8String(Converter.convertTis620ToUTF8(array));
        return identNo;
    }

    private String getTHFullName(){
        byte[] array = getData(CMD_THFULLNAME);
        String THFullName = Converter.getUTF8String(Converter.convertTis620ToUTF8(array));
        return THFullName;
    }

    private String getENFullName(){
        byte[] array = getData(CMD_ENFULLNAME);
        String ENFullName = Converter.getUTF8String(Converter.convertTis620ToUTF8(array));
        return ENFullName;
    }
    private String getBirthDate(){
        byte[] array = getData(CMD_BIRTH);
        String BIRTH_DATE = Converter.getUTF8String(Converter.convertTis620ToUTF8(array));
        return BIRTH_DATE;
    }
    private String getImage(){
        Log.d(TAG,"Debug -- getImage:: ");
        byte[] array = getData(CMD_PHOTO1);
        array = ArrayUtils.addAll(array,getData(CMD_PHOTO2));
        array = ArrayUtils.addAll(array,getData(CMD_PHOTO3));
        array = ArrayUtils.addAll(array,getData(CMD_PHOTO4));
        array = ArrayUtils.addAll(array,getData(CMD_PHOTO5));
        Log.d(TAG,"Debug -- getImage 5:: ");
        array = ArrayUtils.addAll(array,getData(CMD_PHOTO6));
        array = ArrayUtils.addAll(array,getData(CMD_PHOTO7));
        array = ArrayUtils.addAll(array,getData(CMD_PHOTO8));
        array = ArrayUtils.addAll(array,getData(CMD_PHOTO9));
        array = ArrayUtils.addAll(array,getData(CMD_PHOTO10));
        Log.d(TAG,"Debug -- getImage 10:: ");
        array = ArrayUtils.addAll(array,getData(CMD_PHOTO11));
        array = ArrayUtils.addAll(array,getData(CMD_PHOTO12));
        array = ArrayUtils.addAll(array,getData(CMD_PHOTO13));
        array = ArrayUtils.addAll(array,getData(CMD_PHOTO14));
        array = ArrayUtils.addAll(array,getData(CMD_PHOTO15));
        Log.d(TAG,"Debug -- getImage 15:: ");
        array = ArrayUtils.addAll(array,getData(CMD_PHOTO16));
        array = ArrayUtils.addAll(array,getData(CMD_PHOTO17));
        array = ArrayUtils.addAll(array,getData(CMD_PHOTO18));
        array = ArrayUtils.addAll(array,getData(CMD_PHOTO19));
        array = ArrayUtils.addAll(array,getData(CMD_PHOTO20));
        return Converter.byte2HexStr(Converter.convertTis620ToUTF8(array),array.length);
    }

    private byte[] getData(byte[] cmd){
        byte[] array = new byte[1024];
        int[] receiveln = new int[2];
        inner_card.transApdu(cmd.length, cmd, receiveln, array);
        inner_card.transApdu(req.length+1, ArrayUtils.addAll(req,cmd[cmd.length-1]), receiveln, array);
        return array;
    }

//    public String printCardInfo() throws UnsupportedEncodingException {
//        String str = "";
//        Log.d(TAG,"PrintCard Info");
//        byte[] array = new byte[1024];
//        int[] receiveln = new int[2];
//
//        Log.d(TAG,"PrintCard 2::");
//        inner_card.transApdu(SELECT.length+THAI_CARD.length, ArrayUtils.addAll(SELECT,THAI_CARD), receiveln, array);
//
//        array = getData(CMD_CID);
//        String identNo = getUTF8String(convertTis620ToUTF8(array));
//        Log.d(TAG,"PrintCard idenNo ::"+identNo);
//        str += "idenNo = "+identNo+"\n";
//
//        array = getData(CMD_THFULLNAME);
//        String THFullName = getUTF8String(convertTis620ToUTF8(array));
//        str += "THFullName = "+THFullName+"\n";
//        Log.d(TAG,"PrintCard THFullName ::"+THFullName);
//
//        array = getData(CMD_ENFULLNAME);
//        String ENFULLNAME = getUTF8String(convertTis620ToUTF8(array));
//        Log.d(TAG,"PrintCard ENFULLNAME ::"+ENFULLNAME);
//        str += "ENFULLNAME = "+ENFULLNAME+"\n";
//
//        array = getData(CMD_BIRTH);
//        String BIRTH_DATE = getUTF8String(convertTis620ToUTF8(array));
//        str += "BIRTH_DATE = "+BIRTH_DATE+"\n";
//        Log.d(TAG,"PrintCard BIRTH_DATE ::"+BIRTH_DATE);
//        return str;
//    }
//
//    private String getUTF8String(byte[] bytes) throws UnsupportedEncodingException {
//        String str = new String(bytes,"UTF-8");
//        return str;
//    }
//
//    private byte[] convertTis620ToUTF8(byte[] encoded) throws UnsupportedEncodingException {
//
//        String theString = new String(encoded, "TIS620");
//        return theString.getBytes("UTF-8");
//    }
}
