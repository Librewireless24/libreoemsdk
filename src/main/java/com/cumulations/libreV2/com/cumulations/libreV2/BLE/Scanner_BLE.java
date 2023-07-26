package com.cumulations.libreV2.com.cumulations.libreV2.BLE;

import android.Manifest;
import android.Manifest.permission;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import androidx.core.app.ActivityCompat;
import com.cumulations.libreV2.activity.CTBluetoothDeviceListActivity;
import com.cumulations.libreV2.activity.CTDMSDeviceListActivity;
import com.libreAlexa.util.LibreLogger;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class Scanner_BLE {
    private static final String TAG = Scanner_BLE.class.getSimpleName();
    private final CTBluetoothDeviceListActivity mBLEactivity;

    private final BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private final Handler mHandler;

    private final long scanPeriod;
    private final int signalStrength;

    public Scanner_BLE(CTBluetoothDeviceListActivity mainActivity, long scanPeriod, int signalStrength) {
        mBLEactivity = mainActivity;

        mHandler = new Handler();

        this.scanPeriod = scanPeriod;
        this.signalStrength = signalStrength;

        final BluetoothManager bluetoothManager =
                (BluetoothManager) mBLEactivity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public boolean isScanning() {
        return mScanning;
    }

    public void start() {
        if (!BLEUtils.checkBluetooth(mBluetoothAdapter)) {
            BLEUtils.requestUserBluetooth(mBLEactivity);
            mBLEactivity.stopScan();
        } else {
            scanLeDevice(true);

        }
    }

    public void stop() {
        scanLeDevice(false);
    }

    // If you want to scan for only specific types of peripherals,
    // you can instead call startLeScan(UUID[], BluetoothAdapter.LeScanCallback),
    // providing an array of UUID objects that specify the GATT services your app supports.
    private void scanLeDevice(final boolean enable) {
        if (enable && !mScanning) {
            //BLEUtils.toast(mBLEactivity.getApplicationContext(), "Starting BLE scan...");
            LibreLogger.d(TAG,"suma in scanner ble 1");
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    int currentAPIVersion = Build.VERSION.SDK_INT;
                    LibreLogger.d(TAG,"suma in scanner ble 2");

                    if (currentAPIVersion >= 31) {
                        if (ActivityCompat.checkSelfPermission(mBLEactivity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                    }
                    LibreLogger.d(TAG,"suma in scanner ble 3");

                    mBluetoothAdapter.stopLeScan(mLeScanCallback);

                    mBLEactivity.stopScan();
                }
            }, scanPeriod);

            mScanning = true;
            int currentAPIVersion = Build.VERSION.SDK_INT;
            if (currentAPIVersion >= 31) {
                if (ActivityCompat.checkSelfPermission(mBLEactivity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
            }
            LibreLogger.d(TAG,"suma in scanner ble 5");
            mBLEactivity.startScan();
            mBluetoothAdapter.startLeScan(mLeScanCallback);

        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    // Device scan callback.
    private final BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                    final int new_rssi = rssi;
                    if (VERSION.SDK_INT >= 31) {
                        if (ActivityCompat.checkSelfPermission(mBLEactivity, permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            //suma perm
                            return;
                        }
                    }

                    if (device.getName() != null) {

                        LibreLogger.d(TAG, "suma in adding device name1");
//                    if (rssi > signalStrength) {
                        mBLEactivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mBLEactivity.addDevice(device, new_rssi);
                                LibreLogger.d(TAG, "suma in adding device name2");
                            }
                        });

                    }
                }


            };
}