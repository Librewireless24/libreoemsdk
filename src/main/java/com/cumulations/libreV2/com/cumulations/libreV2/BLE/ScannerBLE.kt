package com.cumulations.libreV2.com.cumulations.libreV2.BLE

import android.Manifest.permission
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.cumulations.libreV2.activity.CTBluetoothDeviceListActivity
import com.libreAlexa.util.LibreLogger

/**
 * This class is converted from java to kotlin 11/OCT/2023
 * Shaik
 */
class ScannerBLE(private val mBLActivity: CTBluetoothDeviceListActivity,
    private val scanPeriod: Long,
    private val signalStrength: Int) {
    private var mBluetoothAdapter: BluetoothAdapter
    private var mBluetoothScaneer: BluetoothLeScanner? = null
    var isScanning = false
    private val mHandler: Handler = Handler(Looper.getMainLooper())
    fun getmBluetoothAdapter(): BluetoothAdapter {
        return mBluetoothAdapter
    }

    fun getBluetoothScaneer(): BluetoothLeScanner {
        return mBluetoothScaneer!!
    }

    private val scanSettings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()

    fun start(activity: Activity) {
        scanLeDevice(true)
        /**
         * Commented By Shaik becuase before this only we are checking BT enable or not
         */
    /*if (!BLEUtils.checkBluetooth(activity)) {
            mBLActivity.requestUserBluetooth()
            LibreLogger.d(TAG, "Bt not enabled ")
            mBLActivity.stopScan()
        } else {
            LibreLogger.d(TAG, "Bt not enabled and start Scanning")
            scanLeDevice(true)
        }*/
    }

    fun stop() {
        scanLeDevice(false)
    }

    // If you want to scan for only specific types of peripherals,
    // you can instead call startLeScan(UUID[], BluetoothAdapter.LeScanCallback),
    // providing an array of UUID objects that specify the GATT services your app supports.
    private fun scanLeDevice(enable: Boolean) {
        if (enable && !isScanning) {
            LibreLogger.d(TAG, "scanLeDevice enable $enable")
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(object : Runnable {
                override fun run() {
                    isScanning = false
                    val currentAPIVersion = VERSION.SDK_INT
                    if (currentAPIVersion >= 31) {
                        if (ActivityCompat.checkSelfPermission(mBLActivity, permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                            return
                        }
                    }
                    LibreLogger.d(TAG, "stopScan called after some time")
                    if(mBluetoothScaneer!=null) {
                        LibreLogger.d(TAG, "Stop Scan called ")
                        mBluetoothScaneer!!.stopScan(scanCallback)
                    }else{
                        LibreLogger.d(TAG, "Bluetooth Scanner null")
                    }
                    mBLActivity.stopScan()
                }
            }, scanPeriod)
            isScanning = true
            val currentAPIVersion = VERSION.SDK_INT
            if (currentAPIVersion >= 31) {
                if (ActivityCompat.checkSelfPermission(mBLActivity, permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    return
                }
            }
            LibreLogger.d(TAG, "startScan called")
            mBLActivity.startScan()
            if(mBluetoothScaneer!=null) {
                LibreLogger.d(TAG, "Start Scan called with scanCallback")
                mBluetoothScaneer!!.startScan(null, scanSettings, scanCallback)
            }else{
                LibreLogger.d(TAG, "Bluetooth Scanner null so scan didn't called")
            }

        } else {
            isScanning = false
            if(mBluetoothScaneer!=null) {
                LibreLogger.d(TAG, "Stop Scan called with scanCallback")
                mBluetoothScaneer!!.stopScan(scanCallback)
            }else{
                LibreLogger.d(TAG, "Bluetooth Scanner null so stopScan didn't called")
            }
        }
    }



    init {
        val bluetoothManager = mBLActivity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter
        mBluetoothScaneer = mBluetoothAdapter.bluetoothLeScanner
    }

    companion object {
        private val TAG = ScannerBLE::class.java.simpleName
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            if (VERSION.SDK_INT >= 31) {
                if (ActivityCompat.checkSelfPermission(mBLActivity, permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    LibreLogger.d(TAG, "Permissions are missing")
                    return
                }
            }
            if (result.device.name != null) {
                mBLActivity.runOnUiThread {
                    mBLActivity.addDevice(result.device, result.rssi)
                    LibreLogger.d(TAG, "Shaik Device Name available ${result.device.name}")
                }
            } else {
                LibreLogger.d(TAG, "Shaik BT device name not available ")
            }
        }

        override fun onScanFailed(errorCode: Int) {
            LibreLogger.d(TAG, "Shaik onScanFailed $errorCode")
        }
    }

}