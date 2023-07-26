package com.cumulations.libreV2.activity;

import static com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEUtils.BLE_SAC_APP2DEV_FRIENDLYNAME;
import static com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEUtils.BLE_SAC_APP2DEV_SCAN_WIFI;
import static com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEUtils.BLE_SAC_MODE_SOFTAP_CONNECTED;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEPacket;
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEServiceToApplicationInterface;
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BleCommunication;
import com.libreAlexa.R;
import com.libreAlexa.constants.AppConstants;
import com.libreAlexa.constants.Constants;
import com.libreAlexa.serviceinterface.LSDeviceClient;
import com.libreAlexa.util.LibreLogger;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CTBluetoothHearSoundQtn extends CTDeviceDiscoveryActivity implements View.OnClickListener , BLEServiceToApplicationInterface {

    private BluetoothAdapter mBluetoothAdapter;
    private Button btnYes,btnRetry;
    private String mDeviceName = "";
    private String mDeviceAddress = "";
    private ImageView iv_back;


    private String fromActivity = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_hear_sound_qtn);
        LibreLogger.d(TAG,"CTBluetoothHearSoundQtn on Create ");
        initViews();
        getBundleValues();

        initBluetoothAdapterAndListener();

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void initBluetoothAdapterAndListener(){
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }


    private void getBundleValues(){
        mDeviceAddress = getIntent().getStringExtra(AppConstants.DEVICE_BLE_ADDRESS);
        mDeviceName = getIntent().getStringExtra(AppConstants.DEVICE_NAME);
        LibreLogger.d(TAG, "getBundleValues: "+mDeviceName);
        connectedssid = getIntent().getStringExtra(AppConstants.DEVICE_SSID);
        if(connectedssid == null){
            connectedssid = "";
        }
        fromActivity = getIntent().getStringExtra(Constants.FROM_ACTIVITY);
        if(fromActivity == null) {
            fromActivity = "";
        }
    }

    String connectedssid = "";

    private void initViews(){
        btnYes = findViewById(R.id.btnYes);
        btnYes.setOnClickListener(this);

        btnRetry = findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(this);

        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
    }

    private void callWifiSacCreditialsActivity(String connectedssid) {
        Intent intent = new Intent(CTBluetoothHearSoundQtn.this, CTConnectToWifiActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.FROM_ACTIVITY,fromActivity);
        intent.putExtra(AppConstants.DEVICE_IP,AppConstants.SAC_IP_ADDRESS);
        intent.putExtra(AppConstants.DEVICE_NAME, mDeviceName);
        intent.putExtra(AppConstants.DEVICE_SSID, connectedssid);
        startActivity(intent);
    }

    private BluetoothLeService mBluetoothLeService;
    private String TAG  = "CTBluetoothHearSoundQtn";
    private boolean mServiceConnected = false;
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {


        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {

            LibreLogger.d(TAG, "Service connected" + mServiceConnected);
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize( mBluetoothAdapter ,CTBluetoothHearSoundQtn.this)) {
                LibreLogger.d(TAG,"Unable to initialize Bluetooth");
                finish();
            }
            mBluetoothLeService.connect(mDeviceAddress);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            LibreLogger.d(TAG, "Service Disconnected");
            mBluetoothLeService = null;
            mServiceConnected = false;
        }
    };

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnYes) {
            if (fromActivity.length() > 0) {
                callWifiSacCreditialsActivity(connectedssid);
            } else {
                callPassCredientialsActivity();
            }
        } else if (id == R.id.iv_back) {
            if (fromActivity.length() > 0) {
                onBackPressed();
            } else {
                callBluetoothDeviceListActivity();
            }
        } else if (id == R.id.btnRetry) {
            if (mRetryCount >= 3) {
                callBluetoothSomethingWrongScreen();
            } else {
                sendDataToHearSound();
                mRetryCount = mRetryCount + 1;
            }
        }
    }

    private void callBluetoothDeviceListActivity() {
        //  mBluetoothLeService.disconnect();
       //mBluetoothLeService.close();
        Intent intent = new Intent(this, CTBluetoothDeviceListActivity.class);
        startActivity(intent);
        finish();
    }

    private int mRetryCount = 1 ;
    private void callBluetoothSomethingWrongScreen()  {
        Intent intent = new Intent(this, CTBluetoothSomethingWrong.class);
        intent.putExtra(Constants.FROM_ACTIVITY, fromActivity);
        intent.putExtra(Constants.CONFIG_THRO_BLE, true);
        intent.putExtra(AppConstants.DEVICE_NAME, mDeviceName);
        intent.putExtra(AppConstants.DEVICE_BLE_ADDRESS,mDeviceAddress);
        startActivity(intent);
        finish();
    }
    private void getFriendlyNameThroBLE() {
        if(!didntReceiveFriendlyNameResponse) {
            byte data[] = new byte[0];
            BLEPacket mBlePlayToneButtonClicked = new BLEPacket(data, (byte) BLE_SAC_APP2DEV_FRIENDLYNAME);
            BleCommunication.writeDataToBLEDevice(mBluetoothLeService.getCharacteristic(mDeviceAddress), mBlePlayToneButtonClicked);
          // LibreLogger.d("SUMA_SCAN","******** SUMA GET THE BLE PACKET INFO FriendlyName");
        }
    }
    private void sendDataToHearSound() {
        if(fromActivity.length() > 0) {
            callPlayTestToneLink();
            return;
        }
        if(mBluetoothLeService.getCharacteristic(mDeviceAddress) == null) {
            Toast.makeText(getApplicationContext(), " Failed to Get Characteristic ", Toast.LENGTH_SHORT).show();
            return;
        }
        byte data[] = new byte[0];
        BLEPacket mBlePlayToneButtonClicked = new BLEPacket(data, (byte) BLE_SAC_MODE_SOFTAP_CONNECTED);
        BleCommunication.writeDataToBLEDevice(mBluetoothLeService.getCharacteristic(mDeviceAddress), mBlePlayToneButtonClicked);
      //  LibreLogger.d("SUMA_SCAN","******** SUMA GET THE BLE PACKET INFO HEARSOUND");

    }

    private void callPlayTestToneLink() {
        LSDeviceClient lsDeviceClient = new LSDeviceClient();
        LSDeviceClient.DeviceNameService deviceNameService = lsDeviceClient.getDeviceNameService();

        deviceNameService.getPlayTestSound(new Callback<String>() {
            @Override
            public void success(String s, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }

    private void callPassCredientialsActivity(){

        mBluetoothLeService.removelistener(this);
        Intent intent = new Intent(this, CTBluetoothPassCredentials.class);
        intent.putExtra(Constants.CONFIG_THRO_BLE, true);
        intent.putExtra(AppConstants.DEVICE_NAME, mDeviceName);
        intent.putExtra(AppConstants.DEVICE_BLE_ADDRESS,mDeviceAddress);
        intent.putExtra(AppConstants.prev_activity,"CTBluetoothHearSoundQtn");
        startActivity(intent);
        finish();
    }
    final Handler handler = new Handler();
    private BluetoothGattCharacteristic mBluetoothGattCharac = null;
    @Override
    public void onConnectionSuccess(BluetoothGattCharacteristic value) {
        LibreLogger.d(TAG, "onConnectionSuccess");

        if(mBluetoothGattCharac == null ) {
            mBluetoothGattCharac = value;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendDataToHearSound();
                          //  getFriendlyNameThroBLE();
                        }
                    }, 500);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                          //  sendDataToHearSound();
                            getFriendlyNameThroBLE();
                        }
                    }, 1000);
                }
            });

        }

    }
    boolean didntReceiveFriendlyNameResponse = true;
    @Override
    public void receivedBLEDataPacket(BLEPacket.BLEDataPacket packet) {
        switch(packet.getCommand()){
            case BLE_SAC_APP2DEV_SCAN_WIFI:
                LibreLogger.d(TAG, " DeviceName"+ new String(packet.getcompleteMessage()));
                if ((byte)(packet.getcompleteMessage()[0] & 0xFF )!= 0xAB) {
                    mDeviceName = new String(packet.getcompleteMessage());
                }
                break;
            case BLE_SAC_APP2DEV_FRIENDLYNAME:
                if(packet.getcompleteMessage().length > 0) {
                    byte mDeviceNameArray[] = new byte[packet.getDataLength()];
                  for(int i =0 ;i<packet.getDataLength();i++) {
                        mDeviceNameArray[i] = packet.getMessage()[i];
                    }

                    LibreLogger.d(TAG, "receivedBLEDataPacket from Array " + new String(packet.getMessage())) ;
                    mDeviceName = new String(mDeviceNameArray);
                    LibreLogger.d(TAG, "receivedBLEDataPacket " + mDeviceName) ;
                }
                break;
        }
    }

    @Override
    public void writeSucess(int status) {

    }

    @Override
    public void onDisconnectionSuccess(int status) {

    }
}
