package be8.smartcardreader5;

import android.annotation.SuppressLint;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.util.Log;

import com.feitian.reader.devicecontrol.Card;
import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

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
//    private byte[] CMD_ISSUER = new byte[]{(byte)0x80, (byte)0xb0, 0x00, (byte)0xF6, 0x02, 0x00, 0x64};
//    private byte[] CMD_ISSUE = new byte[]{(byte)0x80, (byte)0xb0, 0x01, 0x67, 0x02, 0x00, 0x08};
//    private byte[] CMD_EXPIRE = new byte[]{(byte)0x80, (byte)0xb0, 0x01, 0x6F, 0x02, 0x00, 0x08};
    private byte[] CMD_ADDRESS = new byte[]{(byte)0x80, (byte)0xb0, 0x15, 0x79, 0x02, 0x00, 0x64};

    private byte[][] CMD_PHOTO = new byte[][]{
            new byte[]{(byte)0x80, (byte)0xb0, 0x01, 0x7B, 0x02, 0x00, (byte)0xFF},
            new byte[]{(byte)0x80, (byte)0xb0, 0x02, 0x7A, 0x02, 0x00, (byte)0xFF},
            new byte[]{(byte)0x80, (byte)0xb0, 0x03, 0x79, 0x02, 0x00, (byte)0xFF},
            new byte[]{(byte)0x80, (byte)0xb0, 0x04, 0x78, 0x02, 0x00, (byte)0xFF},
            new byte[]{(byte)0x80, (byte)0xb0, 0x05, 0x77, 0x02, 0x00, (byte)0xFF},
            new byte[]{(byte)0x80, (byte)0xb0, 0x06, 0x76, 0x02, 0x00, (byte)0xFF},
            new byte[]{(byte)0x80, (byte)0xb0, 0x07, 0x75, 0x02, 0x00, (byte)0xFF},
            new byte[]{(byte)0x80, (byte)0xb0, 0x08, 0x74, 0x02, 0x00, (byte)0xFF},
            new byte[]{(byte)0x80, (byte)0xb0, 0x09, 0x73, 0x02, 0x00, (byte)0xFF},
            new byte[]{(byte)0x80, (byte)0xb0, 0x0A, 0x72, 0x02, 0x00, (byte)0xFF},
            new byte[]{(byte)0x80, (byte)0xb0, 0x0B, 0x71, 0x02, 0x00, (byte)0xFF},
            new byte[]{(byte)0x80, (byte)0xb0, 0x0C, 0x70, 0x02, 0x00, (byte)0xFF},
            new byte[]{(byte)0x80, (byte)0xb0, 0x0D, 0x6F, 0x02, 0x00, (byte)0xFF},
            new byte[]{(byte)0x80, (byte)0xb0, 0x0E, 0x6E, 0x02, 0x00, (byte)0xFF},
            new byte[]{(byte)0x80, (byte)0xb0, 0x0F, 0x6D, 0x02, 0x00, (byte)0xFF},
            new byte[]{(byte)0x80, (byte)0xb0, 0x10, 0x6C, 0x02, 0x00, (byte)0xFF},
            new byte[]{(byte)0x80, (byte)0xb0, 0x11, 0x6B, 0x02, 0x00, (byte)0xFF},
            new byte[]{(byte)0x80, (byte)0xb0, 0x12, 0x6A, 0x02, 0x00, (byte)0xFF},
            new byte[]{(byte)0x80, (byte)0xb0, 0x13, 0x69, 0x02, 0x00, (byte)0xFF},
            new byte[]{(byte)0x80, (byte)0xb0, 0x14, 0x68, 0x02, 0x00, (byte)0xFF}
    };

    @SuppressLint({ "NewApi", "NewApi" })
    public ft_reader(UsbManager mUsbManager, UsbDevice mDevice) {
        inner_card = new Card(mUsbManager, mDevice, null);
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
            throw new FtBlueReadException("Power Off Failed");
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
        if (!isPowerOn) {
            throw new FtBlueReadException("Power Off already");
        }
        return inner_card.getProtocol();
    }

    public byte[] getAtr() throws FtBlueReadException {
        if (!isPowerOn) {
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
        if (!isPowerOn) {
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
        return inner_card.close();
    }
    /* for dukpt test */

    public ProspectModel newProspectModel(){
        Log.d(TAG,"newProspectModel");

        firstConnect();

        ProspectModel pModel = new ProspectModel();
        pModel.TH_FULLNAME = Converter.GetUTF8FromBytes(getData(CMD_THFULLNAME));
        pModel.EN_FULLNAME = Converter.GetUTF8FromBytes(getData(CMD_ENFULLNAME));
        pModel.IDENT_NO = Converter.GetUTF8FromBytes(getData(CMD_CID));
        pModel.BRTH_DT = Converter.GetUTF8FromBytes(getData(CMD_BIRTH));
        pModel.GENDER = Converter.GetUTF8FromBytes(getData(CMD_GENDER));
        pModel.ADDRESS = Converter.GetUTF8FromBytes(getData(CMD_ADDRESS));
        pModel.setImagebyteArray(getImage());
        return pModel;
    }

    private void firstConnect(){
        inner_card.transApdu(SELECT.length+THAI_CARD.length, ArrayUtils.addAll(SELECT,THAI_CARD), new int[2], new byte[1024]);
    }

    private byte[] getImage(){
        Log.d(TAG,"Debug -- getImage:: ");

        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer;
        for(Integer i = 0; i<this.CMD_PHOTO.length;i++){
            Log.d(TAG,"Debug -- byte i:: "+i);
            buffer = getData(CMD_PHOTO[i]);
            byteBuffer.write(buffer,0,buffer.length);
        }

        return byteBuffer.toByteArray();
    }

    private byte[] getData(byte[] cmd){
        Integer checkByte = 2;
        byte[] array = new byte[258];
        int[] receiveln = new int[2];
        Log.d(TAG,"DEBUG cmd :: "+cmd);
        int ret = inner_card.transApdu(cmd.length, cmd, receiveln, array);
        Log.d(TAG,"DEBUG ret :: "+ret);
        LogCardStatus(ret,"Request");
        ret = inner_card.transApdu(req.length+1, ArrayUtils.addAll(req,cmd[cmd.length-1]), receiveln, array);
        LogCardStatus(ret,"Response");
        return Arrays.copyOfRange(array, 0, array.length-1-checkByte);
    }

    private void LogCardStatus(Integer ret,String type){
        if (ret == STATUS.BUFFER_NOT_ENOUGH) {
            Log.d(TAG,type +":: receive buffer not enough");
        } else if (ret == STATUS.TRANS_RETURN_ERROR) {
            Log.d(TAG,type+":: trans apdu error");
        }else if (ret != STATUS.RETURN_SUCCESS){
            Log.d(TAG,type+":: trans apdu error");
        }
    }


}
