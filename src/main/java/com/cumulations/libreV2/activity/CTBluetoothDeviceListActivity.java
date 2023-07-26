package com.cumulations.libreV2.activity;

import android.Manifest;
import android.Manifest.permission;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import com.cumulations.libreV2.adapter.CTBLEDeviceListAdapter;
import com.cumulations.libreV2.adapter.OnClickInterfaceListener;
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEDevice;
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEPacket;
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEServiceToApplicationInterface;
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BleCommunication;
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.Scanner_BLE;
import com.libreAlexa.LibreApplication;
import com.libreAlexa.R;
import com.libreAlexa.constants.AppConstants;
import com.libreAlexa.constants.Constants;
import com.libreAlexa.util.LibreLogger;
import java.util.ArrayList;
import java.util.HashMap;

public class CTBluetoothDeviceListActivity extends CTDeviceDiscoveryActivity implements
    BLEServiceToApplicationInterface, OnClickInterfaceListener {

  ListView mBLEDeviceListView;
  private final String TAG = "BluetoothListActivity";
  CTBLEDeviceListAdapter mBLEListAdapter;
  ImageView mRefreshView, mBackView;
  ArrayList<BLEDevice> mBleDevices = new ArrayList<BLEDevice>();
  private final HashMap<String, BLEDevice> mBTDevicesHashMap = new HashMap<String, BLEDevice>();
  private Scanner_BLE mBTLeScanner;
  private BleCommunication mBleCommunication;
  private BluetoothLeService mBluetoothLeService;
  private final int TIMEOUT_WIFI = 10000;
  LinearLayout no_ble_device_frame_layout;
  RelativeLayout lay_deviceCount;
  AppCompatTextView txt_deviceCount;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ctbluetooth_device_list);

    mBTLeScanner = new Scanner_BLE(this, 7000, -100);
    mBleCommunication = new BleCommunication(this);
    lay_deviceCount = findViewById(R.id.lay_deviceCount);
    txt_deviceCount = findViewById(R.id.txt_deviceCount);
    no_ble_device_frame_layout = findViewById(R.id.no_ble_device_frame_layout);
    mRefreshView = (ImageView) findViewById(R.id.iv_refresh);
    mRefreshView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        handler.removeCallbacks(showWifiConfigurationScreen);
        startScan();
        no_ble_device_frame_layout.setVisibility(View.GONE);
        lay_deviceCount.setVisibility(View.GONE);
      }
    });
    mBackView = (ImageView) findViewById(R.id.iv_back);
    mBackView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        intentToHome(CTBluetoothDeviceListActivity.this);
      }
    });
    mBLEDeviceListView = (ListView) findViewById(R.id.iv_bledevicelist);

    mBleDevices = new ArrayList<>();
    mBleDevices.add(new BLEDevice("weco", "llle", 1));
    mBLEListAdapter = new CTBLEDeviceListAdapter(this, R.layout.ct_bledevice_adapter, mBleDevices);
    mBLEDeviceListView.setAdapter(mBLEListAdapter);
    startScan();

  }


  @Override
  protected void onStart() {
    super.onStart();
    Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    LibreLogger.d(TAG, "suma in startScan2\n");
    startScan();

  }


  private BLEDevice mDeviceClicked = null;
  private String mDeviceAddress = "";

  @Override
  protected void onDestroy() {
    super.onDestroy();
    //mBluetoothLeService.close();
    // mBluetoothLeService.disconnect();
    // unbindService(mServiceConnection);
    stopScan();
    mBluetoothLeService = null;
  }

  // Code to manage Service lifecycle.
  private final ServiceConnection mServiceConnection = new ServiceConnection() {

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {

      LibreLogger.d(TAG, "Service connected");
      mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
      mBluetoothLeService.disconnect();
      mBluetoothLeService.close();
      if (!mBluetoothLeService.initialize(mBTLeScanner.getmBluetoothAdapter(),
          CTBluetoothDeviceListActivity.this)) {
        LibreLogger.d(TAG, "Unable to initialize Bluetooth");
      }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
      LibreLogger.d(TAG, "Service Disconnected");
      mBluetoothLeService = null;
    }
  };

  @Override
  protected void onResume() {
    super.onResume();
    int currentAPIVersion = Build.VERSION.SDK_INT;

    if (mBluetoothLeService != null) {
      mBluetoothLeService.addBLEServiceToApplicationInterfaceListener(this);
    }
    IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
    IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
    IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);

    this.registerReceiver(BTReceiver, filter1);
    this.registerReceiver(BTReceiver, filter2);
    this.registerReceiver(BTReceiver, filter3);

    LibreLogger.d(TAG, "suma in get the BTReceiver Intent");

    /* SUMA : Change according to the 33+ APi changes , Below code snippet may change */
    if (currentAPIVersion >= 31) {
      checkPermissions();
      LibreLogger.d(TAG, "suma in on Resume api 33");
    }


  }

  private void checkPermissions() {
    int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN);

    if (permission != PackageManager.PERMISSION_GRANTED) {
      LibreLogger.d(TAG, "suma in get the permission granted IF");

      ActivityCompat.requestPermissions(
          this,
          PERMISSIONS_LOCATION,
          1
      );
    } else {
      LibreLogger.d(TAG, "suma in startScan1\n");

      handler.removeCallbacks(showWifiConfigurationScreen
      );
      startScan();

    }
  }


  @Override
  public void onBackPressed() {
    intentToHome(CTBluetoothDeviceListActivity.this);
    super.onBackPressed();
  }

  //The BroadcastReceiver that listens for bluetooth broadcasts
  private final BroadcastReceiver BTReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      LibreLogger.d(TAG, "BLE STATE *** ACTION \n" + action);

      if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {

        LibreLogger.d(TAG, "BLE STATE *** ACL_CONNECTED");
      } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
        LibreLogger.d(TAG, "BLE STATE *** ACL_DISCONNETED");

        if (mBluetoothLeService != null) {
          mBluetoothLeService.close();
          mBluetoothLeService.disconnect();
          showToast("BLE_STATE: Disconnected");
          LibreLogger.d(TAG, "BLE STATE *** ACL_DISCONNETED CLOSE");
          intentToHome(CTBluetoothDeviceListActivity.this);

        }

      }
    }
  };

  public void startScan() {
    mBleDevices.clear();
    mBTDevicesHashMap.clear();
    mBLEListAdapter.notifyDataSetChanged();
    no_ble_device_frame_layout.setVisibility(View.GONE);
    lay_deviceCount.setVisibility(View.GONE);
    showProgressDialog(getString(R.string.looking_for_devices));
    mBTLeScanner.start();
    handler.postDelayed(showWifiConfigurationScreen, TIMEOUT_WIFI);
  }

  private static String[] PERMISSIONS_LOCATION = {
      Manifest.permission.ACCESS_FINE_LOCATION,
      Manifest.permission.ACCESS_COARSE_LOCATION,
      Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
      Manifest.permission.BLUETOOTH_SCAN,
      Manifest.permission.BLUETOOTH_CONNECT,
      Manifest.permission.BLUETOOTH_PRIVILEGED
  };

  public void stopScan() {
    dismissDialog();
    mBTLeScanner.stop();
    if (mBleDevices.size() == 0) {
      no_ble_device_frame_layout.setVisibility(View.VISIBLE);
      lay_deviceCount.setVisibility(View.GONE);
    } else {
      no_ble_device_frame_layout.setVisibility(View.GONE);
      lay_deviceCount.setVisibility(View.VISIBLE);
      if(mBleDevices.size()>1){
        txt_deviceCount.setText(mBleDevices.size()+" Devices are available to setup");
      }else{
        txt_deviceCount.setText(mBleDevices.size()+" Device are available to setup");
      }

    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    unbindService(mServiceConnection);
    stopScan();
    mBluetoothLeService.removelistener(this);

  }

  public boolean isRivaSpeaker(BluetoothDevice device) {

      if (VERSION.SDK_INT >= 31) {
          if (ActivityCompat.checkSelfPermission(this, permission.BLUETOOTH_CONNECT)
              != PackageManager.PERMISSION_GRANTED) {
              //suma perm(check may change based on future target SDK)
          }
      }
      return ((device.getName() != null) && device.getName().contains("nuco"));
  }
   /* return ((device.getName() != null) && device.getName().contains("nuConn") || device.getName()
        .contains(("E300")) || device.getName().contains(("VSSL SX")) || device.getName()
        .contains(("EA-300-LYNK"))
        || device.getName().contains(("CS1_")));
  }*/

  public void addDevice(BluetoothDevice device, int rssi) {
    String address = device.getAddress();
    String name = "";
    if (VERSION.SDK_INT >= 31) {
      if (ActivityCompat.checkSelfPermission(this, permission.BLUETOOTH_CONNECT)
          != PackageManager.PERMISSION_GRANTED) {
        //suma perm(check may change based on future target SDK)
        return;
      }
    }
    if (device.getName() != null) {
      name = device.getName();
    }
    LibreLogger.d(TAG, " New Ble device found " + name + " Mac Id " + device.getAddress());
    /* if its not a riva speaker just retun it */
       /* if(!isRivaSpeaker(device))
            return;*/

    if (!mBTDevicesHashMap.containsKey(address)) {
      BLEDevice btleDevice = new BLEDevice(device, rssi);
      btleDevice.setRssi(rssi);

      mBTDevicesHashMap.put(address, btleDevice);
      LibreLogger.d(TAG, " Added BLE Device " + name + " MAC id " + btleDevice.getAddress());
      mBleDevices.add(btleDevice);
      handler.removeCallbacks(showWifiConfigurationScreen);
    } else {
      mBTDevicesHashMap.get(address).setRssi(rssi);
    }
    mBLEListAdapter.notifyDataSetChanged();
  }

  final Handler handler = new Handler();
  final Runnable runnable = new Runnable() {
    @Override
    public void run() {
      dismissDialog();
      LibreLogger.d(TAG, "suma in get no ble device frame layout1");
      if (mBleDevices.size() == 0) {
        no_ble_device_frame_layout.setVisibility(View.VISIBLE);
        lay_deviceCount.setVisibility(View.GONE);
      } else {
        no_ble_device_frame_layout.setVisibility(View.GONE);
        lay_deviceCount.setVisibility(View.VISIBLE);
      }
    }
  };
  final Runnable showWifiConfigurationScreen = new Runnable() {
    @Override
    public void run() {
      callConnetToWifiConfiguration();
    }
  };

  public void callConnetToWifiConfiguration() {
    if (mBleDevices.size() > 0) {
      return;
    }
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        no_ble_device_frame_layout.setVisibility(View.VISIBLE);
        lay_deviceCount.setVisibility(View.GONE);
        dismissDialog();
        LibreLogger.d(TAG, "suma in get no ble device frame layout2");

      }
    });
    LibreLogger.d(TAG, "suma in get the intent for setupInfoInstruction Adding the no device found screen");
      /*  dismissDialog();
        Intent newIntent = new Intent(this, CTHomeTabsActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        newIntent.putExtra(AppConstants.LOAD_FRAGMENT, CTDeviceSetupInfoFragment.class.getSimpleName());
        startActivity(newIntent);
        finish();*/
  }

  final Runnable disconnectBle = new Runnable() {
    @Override
    public void run() {
      if (mBluetoothLeService != null && blPlayToneButtonClicked) {
        blPlayToneButtonClicked = false;
        mBluetoothLeService.disconnect();
        mBluetoothLeService.close();
      }
    }
  };

  @Override
  public void onConnectionSuccess(BluetoothGattCharacteristic btGattChar) {
    LibreLogger.d(TAG, "onConnectionSuccess");
    byte data[] = new byte[0];
    // BLEPacket mBlePlayToneButtonClicked = new BLEPacket(data, (byte) BLE_SAC_MODE_SOFTAP_CONNECTED);
    /// BleCommunication.writeDataToBLEDevice(btGattChar, mBlePlayToneButtonClicked);
    // mBluetoothLeService.disconnect();
    //mBluetoothLeService.close();;
    mBluetoothLeService.removelistener(this);
    callConnetToWifiActivityPage();

  }

  @Override
  public void receivedBLEDataPacket(BLEPacket.BLEDataPacket packet) {

  }

  @Override
  public void writeSucess(int status) {

  }

  @Override
  public void onDisconnectionSuccess(int status) {
    // mBluetoothLeService.removelistener(this);
    LibreApplication.betweenDisconnectedCount = 0;
    LibreApplication.disconnectedCount = 0;
    // callConnetToWifiActivityPage();
  }

  public void callConnetToWifiActivityPage() {
    dismissDialog();
    Intent intent = new Intent(this, CTBluetoothHearSoundQtn.class);
    intent.putExtra(Constants.CONFIG_THRO_BLE, true);
    LibreLogger.d(TAG," Device Name "+ mDeviceClicked.getName());
    intent.putExtra(AppConstants.DEVICE_NAME, "");
    intent.putExtra(AppConstants.DEVICE_BLE_ADDRESS, mDeviceClicked.getAddress());
    startActivity(intent);
    finish();
  }

  private boolean blPlayToneButtonClicked;

  @Override
  public void onInterfaceClick(View view, int position) {
    int id = view.getId();
    if (id == R.id.btnPlayTone) {/* runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgressDialog(getString(R.string.play_tone_request));
                        handler.postDelayed(runnable, 3000);
                    }
                });
                blPlayToneButtonClicked = true;
             //   mBluetoothLeService.connect(mBleDevices.get(position).getAddress());
                //BLE_SAC_MODE_SOFTAP_CONNECTED*/
    } else if (id == R.id.tv_bledevice_layout) {
      if (blPlayToneButtonClicked) {
        return;
      }
      mDeviceClicked = mBleDevices.get(position);
      if (VERSION.SDK_INT >= VERSION_CODES.M) {
        mBluetoothLeService.connect(mDeviceClicked.getAddress());
      }

      mDeviceAddress = mDeviceClicked.getAddress();
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          showProgressDialog(getString(R.string.get_connecting));
          handler.postDelayed(runnable, 12000);

        }
      });
    }
  }
}
