package com.cumulations.libreV2.com.cumulations.libreV2.BLE

import android.app.Activity
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.IntentFilter
import android.view.Gravity
import android.widget.Toast
import com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity
import com.libreAlexa.util.LibreLogger

object BLEUtils {
    const val TAG = "BLEUtils"
    const val BLE_SAC_APP2DEV_SCAN_WIFI = 0
    const val BLE_SAC_APP2DEV_CONNECT_WIFI = 1
    const val BLE_SAC_APP2DEV_READ_WIFI_STATUS = 2
    const val BLE_SAC_APP2DEV_STOP_M = 3
    const val BLE_SAC_APP2DEV_STOP = 4
    const val BLE_SAC_MODE_SOFTAP_CONNECTED = 4
    const val BLE_SAC_APP2DEV_FRIENDLYNAME = 5
    const val BLE_SAC_APP2DEV_SECURITY_CHECK = 27
    const val BLE_SAC_DEV2APP_STARTED = 16
    const val BLE_SAC_DEV2APP_SCAN_LIST_START = 17
    const val BLE_SAC_DEV2APP_SCAN_LIST_DATA = 18
    const val BLE_SAC_DEV2APP_SCAN_LIST_END = 19
    const val BLE_SAC_DEV2APP_CRED_RECEIVED = 20
    const val BLE_SAC_DEV2APP_CRED_SUCCESS = 21
    const val BLE_SAC_DEV2APP_CRED_FAILURE = 22
    const val BLE_SAC_DEV2APP_WIFI_CONNECTING = 23
    const val BLE_SAC_DEV2APP_WIFI_CONNECTED = 24
    const val BLE_SAC_DEV2APP_WIFI_CONNECTING_FAILED = 25
    const val BLE_SAC_DEV2APP_WIFI_DISCONNECTED = 26
    const val BLE_SAC_DEV2APP_WIFI_STATUS = 27
    const val BLE_SAC_DEV2APP_WIFI_AP_NOT_FOUND = 28

    @JvmField
    var REQUEST_ENABLE_BT = 1

    @JvmStatic
    fun checkBluetooth(activity: Activity): Boolean {
        val bluetoothManager = activity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
        val bluetoothAdapter = bluetoothManager!!.adapter
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        return if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            LibreLogger.d(TAG, "checkBluetooth if " + bluetoothAdapter!!.isEnabled)
            false
        } else {
            LibreLogger.d(TAG, "checkBluetooth :else " + bluetoothAdapter.isEnabled)
            true
        }
    }

    fun makeGattUpdateIntentFilter(): IntentFilter {
        return IntentFilter()
    }

    fun hexToString(data: ByteArray): String {
        val sb = StringBuilder(data.size)
        for (byteChar in data) {
            sb.append(String.format("%02X ", byteChar))
        }
        return sb.toString()
    }

    fun hasWriteProperty(property: Int): Int {
        return property and BluetoothGattCharacteristic.PROPERTY_WRITE
    }

    fun hasReadProperty(property: Int): Int {
        return property and BluetoothGattCharacteristic.PROPERTY_READ
    }

    fun hasNotifyProperty(property: Int): Int {
        return property and BluetoothGattCharacteristic.PROPERTY_NOTIFY
    }

    @JvmStatic
    fun toast(context: Context?, string: String?) {
        val toast = Toast.makeText(context, string, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER or Gravity.BOTTOM, 0, 0)
        toast.show()
    }
}