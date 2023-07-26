package com.cumulations.libreV2.com.cumulations.libreV2.BLE;

import android.Manifest;
import android.Manifest.permission;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.libreAlexa.util.LibreLogger;

public class BLEUtils {

  public static final String TAG = "BLEUtils";
  public static final int BLE_SAC_APP2DEV_SCAN_WIFI = 0;
  public static final int BLE_SAC_APP2DEV_CONNECT_WIFI = 1;
  public static final int BLE_SAC_APP2DEV_READ_WIFI_STATUS = 2;
  public static final int BLE_SAC_APP2DEV_STOP_M = 3;
  public static final int BLE_SAC_APP2DEV_STOP = 4;

  public static final int BLE_SAC_MODE_SOFTAP_CONNECTED = 4;
  public static final int BLE_SAC_APP2DEV_FRIENDLYNAME = 5;
  public static final int BLE_SAC_APP2DEV_SECURITY_CHECK = 27;

  public static final int BLE_SAC_DEV2APP_STARTED = 16;
  public static final int BLE_SAC_DEV2APP_SCAN_LIST_START = 17;
  public static final int BLE_SAC_DEV2APP_SCAN_LIST_DATA = 18;
  public static final int BLE_SAC_DEV2APP_SCAN_LIST_END = 19;
  public static final int BLE_SAC_DEV2APP_CRED_RECEIVED = 20;
  public static final int BLE_SAC_DEV2APP_CRED_SUCCESS = 21;
  public static final int BLE_SAC_DEV2APP_CRED_FAILURE = 22;
  public static final int BLE_SAC_DEV2APP_WIFI_CONNECTING = 23;
  public static final int BLE_SAC_DEV2APP_WIFI_CONNECTED = 24;
  public static final int BLE_SAC_DEV2APP_WIFI_CONNECTING_FAILED = 25;

  public static final int BLE_SAC_DEV2APP_WIFI_DISCONNECTED = 26;
  public static final int BLE_SAC_DEV2APP_WIFI_STATUS = 27;
  public static final int BLE_SAC_DEV2APP_WIFI_AP_NOT_FOUND = 28;

  public static int REQUEST_ENABLE_BT = 1;

  public static boolean checkBluetooth(BluetoothAdapter bluetoothAdapter) {

    // Ensures Bluetooth is available on the device and it is enabled. If not,
    // displays a dialog requesting user permission to enable Bluetooth.
    if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
      LibreLogger.d(TAG, "onClick: isEnabled: if " + bluetoothAdapter.isEnabled());
      return false;
    } else {
      LibreLogger.d(TAG, "onClick: isEnabled:else " + bluetoothAdapter.isEnabled());
      return true;
    }
  }

  public static void requestUserBluetooth(Activity activity) {
    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
      //Bluetooth Request Enable BT Check Permission
      return;
    }
    if (ActivityCompat.checkSelfPermission(activity, permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
      //Bluetooth Request Enable BT Check Permission
      return;
    }
    activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
  }

  public static IntentFilter makeGattUpdateIntentFilter() {

    final IntentFilter intentFilter = new IntentFilter();

    return intentFilter;
  }

  public static String hexToString(byte[] data) {
    final StringBuilder sb = new StringBuilder(data.length);

    for (byte byteChar : data) {
      sb.append(String.format("%02X ", byteChar));
    }

    return sb.toString();
  }

  public static int hasWriteProperty(int property) {
    return property & BluetoothGattCharacteristic.PROPERTY_WRITE;
  }

  public static int hasReadProperty(int property) {
    return property & BluetoothGattCharacteristic.PROPERTY_READ;
  }

  public static int hasNotifyProperty(int property) {
    return property & BluetoothGattCharacteristic.PROPERTY_NOTIFY;
  }

  public static void toast(Context context, String string) {

    Toast toast = Toast.makeText(context, string, Toast.LENGTH_SHORT);
    toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
    toast.show();
  }
}
