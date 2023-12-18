package com.cumulations.libreV2.com.cumulations.libreV2.BLE;

import static com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEUtils.BLE_SAC_APP2DEV_CONNECT_WIFI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import com.cumulations.libreV2.model.WifiConnection;
import com.cumulations.libreV2.tcp_tunneling.TunnelingClientRunnable;
import com.libreAlexa.util.LibreLogger;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ConfigurationParameters {
    private static final String TAG = ConfigurationParameters.class.getSimpleName();
    //public static final String PRODUCT_ID = "Audio Design Experts Inc";
    public static final String PRODUCT_ID = "Audio"; //For testing I have  added
    public ConfigPacket mConfigPacket = null;

    public class ConfigPacket {
        public byte[] getEncodedData() {
            return encodedData;
        }

        public void setEncodedData(byte[] encodedData) {
            this.encodedData = encodedData;
        }

        public byte[] getIv() {
            return iv;
        }

        public void setIv(byte[] iv) {
            this.iv = iv;
        }

        byte[] encodedData;
        byte[] iv;
        public ConfigPacket(byte[] encodedData, byte[] iv){
            this.encodedData=encodedData;
            this.iv = iv;
        }
    }
    private static final ConfigurationParameters ourInstance = new ConfigurationParameters();

    public static ConfigurationParameters getInstance() {
        return ourInstance;
    }

    private ConfigurationParameters() {
    }

    public ConfigPacket getConfigPacket() {
        return mConfigPacket;
    }

    public String getDeviceCountryCode(Context context) {
        String countryCode;

        // try to get country code from TelephonyManager service
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if(tm != null) {
            // query first getSimCountryIso()
            countryCode = tm.getSimCountryIso();
            if (countryCode != null && countryCode.length() == 2)
                return countryCode.toLowerCase();

            if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
                // special case for CDMA Devices
                countryCode = getCDMACountryIso();
            } else {
                // for 3G devices (with SIM) query getNetworkCountryIso()
                countryCode = tm.getNetworkCountryIso();
            }

            if (countryCode != null && countryCode.length() == 2)
                return countryCode.toLowerCase();
        }

        // if network country not available (tablets maybe), get country code from Locale class
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            countryCode = context.getResources().getConfiguration().getLocales().get(0).getCountry();
        } else {
            countryCode = context.getResources().getConfiguration().locale.getCountry();
        }

        if (countryCode != null && countryCode.length() == 2)
            return  countryCode.toLowerCase();

        // general fallback to "us"
        return "us";
    }

    @SuppressLint("PrivateApi")
    public String getCDMACountryIso() {
        try {
            // try to get country code from SystemProperties private class
            Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            Method get = systemProperties.getMethod("get", String.class);

            // get homeOperator that contain MCC + MNC
            String homeOperator = ((String) get.invoke(systemProperties,
                    "ro.cdma.home.operator.numeric"));

            // first 3 chars (MCC) from homeOperator represents the country code
            int mcc = Integer.parseInt(homeOperator.substring(0, 3));

            // mapping just countries that actually use CDMA networks
            switch (mcc) {
                case 330: return "PR";
                case 310: return "US";
                case 311: return "US";
                case 312: return "US";
                case 316: return "US";
                case 283: return "AM";
                case 460: return "CN";
                case 455: return "MO";
                case 414: return "MM";
                case 619: return "SL";
                case 450: return "KR";
                case 634: return "SD";
                case 434: return "UZ";
                case 232: return "AT";
                case 204: return "NL";
                case 262: return "DE";
                case 247: return "LV";
                case 255: return "UA";
            }
        } catch (ClassNotFoundException ignored) {
        } catch (NoSuchMethodException ignored) {
        } catch (IllegalAccessException ignored) {
        } catch (InvocationTargetException ignored) {
        } catch (NullPointerException ignored) {
        }

        return null;
    }

    public ConfigPacket getEncryptedDataAndKey(String mSelectedSSID , String mSelectedPass , String mSelectedSecurity, String mDeviceName , String mSelectedCountryCode) {
        return doEncryption(getData(mSelectedSSID, mSelectedPass, mSelectedSecurity, mDeviceName, mSelectedCountryCode));
    }

    public String getKey(){
        return   generateSymmerticKey(PRODUCT_ID);//"RWAC01B";
    }

    public String generateSymmerticKey (String ProductIdText) {
        /*
         * Suma: Below Method : Generating the symmentric key from product ID dynamically using MD5 algorithmn
         *

         *
         * Start of code
         *
         * */
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hashInBytes = new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            if (md != null) {
                hashInBytes = md.digest(ProductIdText.getBytes(StandardCharsets.UTF_8));
            }
        }
        // bytes to hex
        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
        /*End of the code*/
    }

    public byte[]  getData(String mSelectedSSID , String mSelectedPass , String mSelectedSecurity, String mDeviceName , String mSelectedCountryCode) {

        WifiConnection.getInstance().setMainSSIDPwd(mSelectedPass);
        int mPayloadLength = mSelectedPass.length()+mSelectedSSID.length()+5+mSelectedSecurity.length()+mDeviceName.length()+mSelectedCountryCode.length();
        byte data[] = new byte[mPayloadLength+2];
        int i = 0;
        data[i++] = (byte) (mPayloadLength & 0x00FF);
        data[i++] = (byte) ((mPayloadLength & 0xFF00) >> 8);
        data[i++] = (byte) mSelectedSSID.length();
        for(byte b : mSelectedSSID.getBytes()) {
            data[i++] = b;
        }

        data[i++] = (byte)mSelectedPass.length();
        for(byte b : mSelectedPass.getBytes()){
            data[i++] = b;
        }

        data[i++] = (byte)mSelectedSecurity.length();
        for(byte b : mSelectedSecurity.getBytes()) {
            data[i++] = b;
        }

        data[i++] = (byte)mDeviceName.length();
        for(byte b : mDeviceName.getBytes()) {
            data[i++] = b;
        }

        data[i++] = (byte) mSelectedCountryCode.length();
        for(byte b : mSelectedCountryCode.getBytes()) {
            data[i++] = b;
        }
        LibreLogger.d(TAG, "KARUNAKARAN Print Data " );
        printByteArray(data);
        LibreLogger.d(TAG, " KARUNAKARAN print is done");
        return data;

    }

    public void toVerifyEncyrptionConversion() throws Exception{
        String mssid = "google mra";
        String mpassphrase = "1234567890";
        String mSecurity = "WPA-PSK";
        String deviceName = "KARUNAKARAN";
        String countrycode = "IN";
        String mEncodedSsid = new String(mssid.getBytes(), "UTF-8");
        String mEncodedPassphrase = new String(mpassphrase.getBytes(), "UTF-8");
        String mEncodedSecurity = new String(mSecurity.getBytes(),"UTF-8");
        String mEncodeddeviceName = new String(deviceName.getBytes(),"UTF-8");
        String mEncodedcountrycode = new String(countrycode.getBytes(),"UTF-8");
        byte[] toEncryptData = getData(mEncodedSsid,mEncodedPassphrase,mEncodedSecurity,mEncodeddeviceName,mEncodedcountrycode);
        doEncryption(toEncryptData);
    }
    public ConfigPacket doEncryption(byte[] toEncryptData ) {
        /**
         * ssid: google mra
         * passphrase : 1234567890
         * security : wpa-psk
         */
        ConfigPacket configPacket = null;
        Cipher cipher = null;
        byte[] sacPacket = new byte[0];
        byte[] encryptedData = new byte[0];
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        byte[] iv = new byte[0];
        if (cipher != null) {
            iv = new byte[cipher.getBlockSize()];
            System.out.println("block size : "+cipher.getBlockSize());
        }
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        final StringBuilder builder = new StringBuilder();
        for(byte b : ivParameterSpec.getIV()) {
            builder.append(String.format("%02x", b));
        }
        String ivHexString=builder.toString();
        byte[] convertedHexbyte = hexStrToByteArray(builder.toString());
        LibreLogger.d(TAG,"KARUNAKARAN Encyrption need to know IV bytes hex value"+builder.toString());

        LibreLogger.d(TAG,"KARUNAKARAN Encyrption need to know IV bytes hex value length"+iv.length);
        byte[] hexaKey =  hexStrToByteArray(getKey());
        LibreLogger.d(TAG,"KARUNAKARAN Encyrption need to know IV bytes app security key"+getKey());


        try {
            LibreLogger.d(TAG, "Encryption Print value To Encrypted  text " );
            printByteArray(toEncryptData);
            //String encodedSsid=new String("KARUNAKARAN".getBytes(),"UTF-8");

            encryptedData = encrypt(toEncryptData, hexaKey, convertedHexbyte);
            printByteArray(encryptedData);
            String decryptedText = decrypt(encryptedData, hexaKey, convertedHexbyte);
            LibreLogger.d(TAG, "Print value Decrypted text " + decryptedText );
            printByteArray(decryptedText.getBytes());
           //sacPacket = createSacPackets(false, encryptedData, iv);
            configPacket = new ConfigPacket(encryptedData, iv);
            this.mConfigPacket = configPacket;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return configPacket;
    }

    public byte[] getByteArrayFromOffset(int offset, int length, byte[] encodedData){
        byte[] mOutByteArray = new byte[length];

        for(int j = 0; j < length;j++) {
           // LibreLogger.d(this,"KARUNAKARAN i ,j , offset " +   j +"," +offset + " Length" + length);
            mOutByteArray[j] = encodedData[j+offset];
        }
        return mOutByteArray;
    }


    public String sendByteArrayToString(byte[] printByteArray) {
        int i = 0;
        StringBuilder sb = new StringBuilder();
        for(byte b : printByteArray) {
            sb.append(String.format("%02x", b));
            LibreLogger.d(TAG, " Print byte array at " + i++ + " value as " + b);
        }
        LibreLogger.d(TAG, " KARUNAKARAN Completed Print byte array as: " + sb.toString());
        return sb.toString();
    }

    public void printByteArray(byte[] printByteArray) {
        int i = 0;
        StringBuilder sb = new StringBuilder();
        for(byte b : printByteArray) {
            sb.append(String.format("%02x", b));
            LibreLogger.d(TAG, " Print byte array at " + i++ + " value as " + b);
        }
        LibreLogger.d(TAG, " KARUNAKARAN Completed Print byte array as: " + sb.toString());
    }

    public byte[]   hexStrToByteArray(String hex) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(hex.length() / 2);

        for (int i = 0; i < hex.length(); i += 2) {
            String output = hex.substring(i, i + 2);
            int decimal = Integer.parseInt(output, 16);
            baos.write(decimal);
        }
        return baos.toByteArray();
    }

    public byte[] createSacPackets(int enableMoreFlag, byte[] encryptedData , byte[] iv) {
        byte[] completedata = new byte[27 + encryptedData.length];
        int fragLength = iv.length + 1 + encryptedData.length;
        completedata[0] = (byte) 0xAB;
        completedata[1] = (byte) BLE_SAC_APP2DEV_CONNECT_WIFI;
        completedata[2] = (byte) 0xEF; //0xDE;
        completedata[3] = (byte) 0xBE; //0xAD;
        completedata[4] = (byte) 0xAD; //0xBE;
        completedata[5] = (byte) 0xDE; //0xEF;
        completedata[6] = (byte) (enableMoreFlag); // MultipletimeMoreFlag
        completedata[7] = (byte) (fragLength & 0x00FF);
        completedata[8] = (byte) ((fragLength & 0xFF00) >> 8);
        completedata[9] = (byte) iv.length;
        int i = 10;
        for(byte b : iv) {
            completedata[i++] = b;
        }
        for(byte b : encryptedData) {
            completedata[i++] = b;
        }
        completedata[i] = (byte) 0xCD;
        printByteArray(completedata);
        return completedata;

    }

    public byte[] createSacPackets(boolean enableMoreFlag, byte[] encryptedData , byte[] iv) {
        byte[] completedata = new byte[28 + encryptedData.length];
        int fragLength = iv.length + 1 + encryptedData.length;
        completedata[0] = (byte) 0xAB;
        completedata[1] = (byte) BLE_SAC_APP2DEV_CONNECT_WIFI;
        completedata[2] = (byte) 0xDE;
        completedata[3] = (byte) 0xAD;
        completedata[4] = (byte) 0xBE;
        completedata[5] = (byte) 0xEF;
        completedata[6] = (byte) (enableMoreFlag?1:0);
        completedata[7] = (byte) (fragLength & 0x00FF);
        completedata[8] = (byte) ((fragLength & 0xFF00) >> 8);
        completedata[9] = (byte) iv.length;
        int i = 10;
        for(byte b : iv) {
            completedata[i++] = b;
        }
        for(byte b : encryptedData) {
            completedata[i++] = b;
        }
        completedata[i] = (byte) 0XCD;
        printByteArray(completedata);

        return completedata;
    }

    public byte[] encrypt(byte[] plaintext, byte[] key, byte[] IV) throws Exception
    {
        //Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        //Create SecretKeySpec
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

        //Create IvParameterSpec
        IvParameterSpec ivSpec = new IvParameterSpec(IV);

        //Initialize Cipher for ENCRYPT_MODE
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        //Perform Encryption
        return cipher.doFinal(plaintext);
    }
    public String decrypt (byte[] cipherText, byte[] key, byte[] IV) throws Exception
    {
        //Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        //Create SecretKeySpec
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

        //Create IvParameterSpec
        IvParameterSpec ivSpec = new IvParameterSpec(IV);

        //Initialize Cipher for DECRYPT_MODE
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        //Perform Decryption
        byte[] decryptedText = cipher.doFinal(cipherText);

        return new String(decryptedText);

    }
}
