package com.cumulations.libreV2.activity

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.cumulations.libreV2.activity.BluetoothLeService.LocalBinder
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEPacket
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEPacket.BLEDataPacket
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEServiceToApplicationInterface
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEUtils.BLE_SAC_APP2DEV_FRIENDLYNAME
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEUtils.BLE_SAC_APP2DEV_SCAN_WIFI
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEUtils.BLE_SAC_MODE_SOFTAP_CONNECTED
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BleCommunication
import com.libreAlexa.R
import com.libreAlexa.constants.AppConstants
import com.libreAlexa.constants.Constants
import com.libreAlexa.databinding.ActivityBluetoothHearSoundQtnBinding
import com.libreAlexa.serviceinterface.LSDeviceClient
import com.libreAlexa.util.LibreLogger
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response

/**
 * This activity is converted from java to kotlin
 * Shaik 07-Dec-2023
 * Converted because of New BLE discovery improvements
 */
class CTBluetoothHearSoundQtn : CTDeviceDiscoveryActivity(), View.OnClickListener,
    BLEServiceToApplicationInterface {
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mDeviceName: String? = ""
    private var mDeviceAddress: String? = ""
    private var fromActivity: String? = ""
    private lateinit var binding: ActivityBluetoothHearSoundQtnBinding
    private var isDisconnectionHandled = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBluetoothHearSoundQtnBinding.inflate(layoutInflater)
        setContentView(binding.root)
        LibreLogger.d(TAG, "CTBluetoothHearSoundQtn on Create ")
        initViews()
        bundleValues
        initBluetoothAdapterAndListener()
    }

    private fun initBluetoothAdapterAndListener() {
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter
        val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE)
    }

    private val bundleValues: Unit
        private get() {
            mDeviceAddress = intent.getStringExtra(AppConstants.DEVICE_BLE_ADDRESS)
            mDeviceName = intent.getStringExtra(AppConstants.DEVICE_NAME)
            LibreLogger.d(TAG, "getBundleValues: $mDeviceName")
            connectedssid = intent.getStringExtra(AppConstants.DEVICE_SSID)
            if (connectedssid == null) {
                connectedssid = ""
            }
            fromActivity = intent.getStringExtra(Constants.FROM_ACTIVITY)
            if (fromActivity == null) {
                fromActivity = ""
            }
        }
    var connectedssid: String? = ""
    private fun initViews() {
        binding.btnYes.setOnClickListener(this)
        binding.btnRetry.setOnClickListener(this)
        binding.ivBack.setOnClickListener(this)
    }

    private fun callWifiSacCreditialsActivity(connectedssid: String?) {
        val intent = Intent(this@CTBluetoothHearSoundQtn, CTConnectToWifiActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(Constants.FROM_ACTIVITY, fromActivity)
        intent.putExtra(AppConstants.DEVICE_IP, AppConstants.SAC_IP_ADDRESS)
        intent.putExtra(AppConstants.DEVICE_NAME, mDeviceName)
        intent.putExtra(AppConstants.DEVICE_SSID, connectedssid)
        startActivity(intent)
    }

    private var mBluetoothLeService: BluetoothLeService? = null
    private val TAG = "CTBluetoothHearSoundQtn"
    private var mServiceConnected = false

    // Code to manage Service lifecycle.
    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        @RequiresApi(api = Build.VERSION_CODES.M)
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            LibreLogger.d(TAG, "Service connected$mServiceConnected")
            mBluetoothLeService = (service as LocalBinder).service
            if (!mBluetoothLeService!!.initialize(mBluetoothAdapter, this@CTBluetoothHearSoundQtn)) {
                LibreLogger.d(TAG, "Unable to initialize Bluetooth")
                finish()
            }
            mBluetoothLeService!!.connect(mDeviceAddress)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            LibreLogger.d(TAG, "Service Disconnected")
            mBluetoothLeService = null
            mServiceConnected = false
        }
    }

    override fun onClick(view: View) {
        when (view) {
            binding.btnYes -> {
                if (fromActivity?.isNotEmpty() == true) {
                    callWifiSacCreditialsActivity(connectedssid)
                } else {
                    callPassCredientialsActivity()
                }
            }

            binding.ivBack -> {
                if (fromActivity?.isNotEmpty() == true) {
                    onBackPressed()
                } else {
                    callBluetoothDeviceListActivity()
                }
            }

            binding.btnRetry -> {
                if (mRetryCount >= 3) {
                    callBluetoothSomethingWrongScreen()
                } else {
                    sendDataToHearSound()
                    mRetryCount += 1
                }
            }
        }
    }

    private fun callBluetoothDeviceListActivity() {
        val intent = Intent(this, CTBluetoothDeviceListActivity::class.java)
        startActivity(intent)
        finish()
    }

    private var mRetryCount = 1
    private fun callBluetoothSomethingWrongScreen() {
        val intent = Intent(this, CTBluetoothSomethingWrong::class.java)
        intent.putExtra(Constants.FROM_ACTIVITY, fromActivity)
        intent.putExtra(Constants.CONFIG_THRO_BLE, true)
        intent.putExtra(AppConstants.DEVICE_NAME, mDeviceName)
        intent.putExtra(AppConstants.DEVICE_BLE_ADDRESS, mDeviceAddress)
        startActivity(intent)
        finish()
    }

    private val friendlyNameThroBLE: Unit
        private get() {
            if (!didntReceiveFriendlyNameResponse) {
                val data = ByteArray(0)
                val mBlePlayToneButtonClicked = BLEPacket(data, BLE_SAC_APP2DEV_FRIENDLYNAME.toByte())
                BleCommunication.writeDataToBLEDevice(mBluetoothLeService!!.getCharacteristic(mDeviceAddress), mBlePlayToneButtonClicked)

            }
        }

    private fun sendDataToHearSound() {
        if (fromActivity!!.length > 0) {
            callPlayTestToneLink()
            return
        }
        if (mBluetoothLeService!!.getCharacteristic(mDeviceAddress) == null) {
            Toast.makeText(applicationContext, " Failed to Get Characteristic ", Toast.LENGTH_SHORT).show()
            return
        }
        val data = ByteArray(0)
        val mBlePlayToneButtonClicked = BLEPacket(data, BLE_SAC_MODE_SOFTAP_CONNECTED.toByte())
        BleCommunication.writeDataToBLEDevice(mBluetoothLeService!!.getCharacteristic(mDeviceAddress), mBlePlayToneButtonClicked)
    }

    private fun callPlayTestToneLink() {
        val lsDeviceClient = LSDeviceClient()
        val deviceNameService = lsDeviceClient.deviceNameService

        deviceNameService.getPlayTestSound(object : Callback<String?> {
            override fun success(t: String?, response: Response?) {}
            override fun failure(error: RetrofitError) {}
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(mServiceConnection)
    }

    private fun callPassCredientialsActivity() {
        mBluetoothLeService!!.removelistener(this)
        val intent = Intent(this, CTBluetoothPassCredentials::class.java)
        intent.putExtra(Constants.CONFIG_THRO_BLE, true)
        intent.putExtra(AppConstants.DEVICE_NAME, mDeviceName)
        intent.putExtra(AppConstants.DEVICE_BLE_ADDRESS, mDeviceAddress)
        intent.putExtra(AppConstants.prev_activity, "CTBluetoothHearSoundQtn")
        startActivity(intent)
        finish()
    }

    val handler = Handler(Looper.getMainLooper())
    private var mBluetoothGattCharac: BluetoothGattCharacteristic? = null
    override fun onConnectionSuccess(value: BluetoothGattCharacteristic) {
        LibreLogger.d(TAG, "onConnectionSuccess")
        if (mBluetoothGattCharac == null) {
            mBluetoothGattCharac = value
            runOnUiThread {
                handler.postDelayed({
                    sendDataToHearSound()
                    //  getFriendlyNameThroBLE();
                }, 500)
                handler.postDelayed({ //  sendDataToHearSound();
                    friendlyNameThroBLE
                }, 1000)
            }
        }
    }

    var didntReceiveFriendlyNameResponse = true
    override fun receivedBLEDataPacket(packet: BLEDataPacket, hexData: String) {
        when (packet.command) {
            BLE_SAC_APP2DEV_SCAN_WIFI -> {
                LibreLogger.d(TAG, " DeviceName" + String(packet.getcompleteMessage()));
                if ((packet.getcompleteMessage()[0].toInt() and 0xFF).toByte().toInt() != 0xAB) {
                    mDeviceName = String(packet.getcompleteMessage())
                }
            }

            BLE_SAC_APP2DEV_FRIENDLYNAME -> if (packet.getcompleteMessage().size > 0) {
                val mDeviceNameArray = ByteArray(packet.dataLength.toInt())
                var i = 0
                while (i < packet.dataLength) {
                    mDeviceNameArray[i] = packet.message[i]
                    i++
                }
                LibreLogger.d(TAG, "receivedBLEDataPacket from Array " + String(packet.message))
                mDeviceName = String(mDeviceNameArray)
                LibreLogger.d(TAG, "receivedBLEDataPacket $mDeviceName")
            }
        }
    }

    override fun writeSucess(status: Int) {

    }
    override fun onDisconnectionSuccess(status: Int) {
        if (!isDisconnectionHandled) {
            isDisconnectionHandled = true

            runOnUiThread {
                showAlertDialog(getString(R.string.somethingWentWrong_tryAgain), getString(R
                    .string.ok), 0, isDeviceLost =true, isLocationPermission =false,
                    isLocationPermissionRotational=false)
            }
        }
    }
}