package com.cumulations.libreV2.com.cumulations.libreV2.BLE

import android.Manifest.permission
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanRecord
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.cumulations.libreV2.activity.CTBluetoothDeviceListActivity
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEGattAttributes.BLE_DEVICE_UUID
import com.libreAlexa.util.LibreLogger
import java.util.UUID

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
            }
            else{
                LibreLogger.d(TAG, "Bluetooth Scanner null so scan didn't called")
            }

        } else {
            isScanning = false
            if(mBluetoothScaneer!=null) {
                LibreLogger.d(TAG, "Stop Scan called with scanCallback")
                mBluetoothScaneer!!.stopScan(scanCallback)
            }else{
                val bluetoothManager = mBLActivity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                mBluetoothAdapter = bluetoothManager.adapter
                mBluetoothScaneer = mBluetoothAdapter.bluetoothLeScanner
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
        private val TAG_BLE = "Mansoor"
    }

//    private val scanCallback = object : ScanCallback() {
//        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//        override fun onScanResult(callbackType: Int, result: ScanResult) {
//            if (VERSION.SDK_INT >= 31) {
//                if (ActivityCompat.checkSelfPermission(mBLActivity, permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    LibreLogger.d(TAG, "Permissions are missing")
//                    return
//                }
//            }
//            if (result.device.name != null) {
//                mBLActivity.runOnUiThread {
//                    val scanRecord = result.scanRecord
//                    val device: BluetoothDevice = result.device
//                    val deviceName = device.name ?: "Unknown Device"
//                    val rssi = result.rssi
//
//                    // Extract the Shortened Local Name from the advertisement data
//                    val shortenedLocalName = extractShortenedLocalName(scanRecord)
//
//
//                    // Print the Shortened Local Name
//                    if(!shortenedLocalName.contains("LSAA")) {
//                        mBLActivity.addDevice(result.device, result.rssi)
//                        LibreLogger.d(TAG_BLE, "Shaik Device Name starts with LSAA $deviceName, " + "RSSI:$rssi, Shortened Local Name: $shortenedLocalName")
//                    }else{
//                        LibreLogger.d(TAG_BLE,"Shaik Device Name:not starts LSAA  $deviceName, " + "RSSI:$rssi, " + "Shortened Local Name: $shortenedLocalName")
//                    }
//                }
//            } else {
//                LibreLogger.d(TAG, "Shaik BT device name not available ")
//            }
//        }
//
//        override fun onScanFailed(errorCode: Int) {
//            LibreLogger.d(TAG, "Shaik onScanFailed $errorCode")
//        }
//    }

    private val scanCallback = object : ScanCallback() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            if (VERSION.SDK_INT >= 31) {
                if (ActivityCompat.checkSelfPermission(mBLActivity, permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                    LibreLogger.d(TAG, "Permissions are missing")
                    return
                }
            }
            if (result.device.name != null) {
                mBLActivity.runOnUiThread {
                    val scanRecord = result.scanRecord

                    val device: BluetoothDevice = result.device
                    LibreLogger.d(TAG_BLE, "Shaik got Ble data  ${scanRecord!!.serviceData}")
                    // Get the service data map
                    val serviceData: Map<ParcelUuid, ByteArray> = scanRecord.serviceData
                    // Iterate through the service data
                    for ((serviceUuid, data) in serviceData) {
                        // Process the service data
                        Log.d(TAG, "Shaik ExtractServiceUUID: ${device.name}, " +
                                "Address: ${device.address}, " +
                                "Service UUID: ${serviceUuid.toString()}, " +
                                "Service Data: ${bytesToHex(data)}")

                        if (BLE_DEVICE_UUID == UUID.fromString(serviceUuid.toString()))
                        {
                            mBLActivity.addDevice(result.device, result.rssi)
                        }
                        else {
                            LibreLogger.d(TAG_BLE, "Shaik Device UUID is not matching  ${result.scanRecord!!.serviceData}")
                        }
                    }
                    LibreLogger.d(TAG_BLE, "Shaik outside for loop")

                    /* // Extract the Shortened Local Name from the advertisement data
                    val shortenedLocalName = extractShortenedLocalName(scanRecord)
                     if(shortenedLocalName.contains("LSAA")) {
                         mBLActivity.addDevice(result.device, result.rssi)
                         LibreLogger.d(TAG_BLE, "Shaik Device Name starts with   Shortened Local Name: $shortenedLocalName")
                     }else{
                         LibreLogger.d(TAG_BLE,"Shaik Device Name:not starts LSAA Shortened Local Name: $shortenedLocalName")
                     }*/
                }
            } else {
                LibreLogger.d(TAG, "Shaik BT device name not available ")
            }
        }

        override fun onScanFailed(errorCode: Int) {
            LibreLogger.d(TAG, "Shaik onScanFailed $errorCode")
        }
    }
    private fun extractShortenedLocalName(scanRecord: ScanRecord?): String {
        val adData = scanRecord?.bytes ?: return "No Advertisement Data"

        /**
         * Length: The length of the data field, including the type byte.
         * Type: The type of data, specifying what kind of information is present.
         * Data: The actual data, which can vary in length.
         */

        // Advertisement data format: Length - Type - Data
        var index = 0

        //The loop stops when it reaches the end of the data or encounters a length of 0, indicating the end of the advertisement data.
        while (index < adData.size) {
            //It extracts the length byte at the current index. The and 0xFF operation is used to ensure that the value is treated as an unsigned byte (8 bits), preventing negative values.
            val length = adData[index].toInt() and 0xFF
            if (length == 0) {
                break
            }
            /**
             *              ---> val type = adData[index + 1].toInt() and 0xFF
             * It extracts the type byte, which follows the length byte in the advertisement data.
             *              --->type == 8
             * It checks if the type is equal to 8, which is typically associated with the Shortened Local Name in BLE advertisement data.
             *              --->return String(adData.copyOfRange(index + 2, index + 1 + length))
             * If the type is 8, it means the Shortened Local Name is found. The function returns
             * the Shortened Local Name by converting the corresponding portion of the
             * advertisement data to a String. The copyOfRange method is used to create a subarray from index + 2 to index + 1 + length.
             */
            val type = adData[index + 1].toInt() and 0xFF
            if (type == 8 /* Shortened Local Name */) {
                return String(adData.copyOfRange(index + 2, index + 1 + length))
            }

            index += length + 1
        }
        return "No Shortened Local Name"

    }

    private fun bytesToHex(bytes: ByteArray): String {
        val stringBuilder = StringBuilder()
        for (b in bytes) {
            stringBuilder.append(String.format("%02X ", b))
        }
        return stringBuilder.toString().trim { it <= ' ' }
    }

}
