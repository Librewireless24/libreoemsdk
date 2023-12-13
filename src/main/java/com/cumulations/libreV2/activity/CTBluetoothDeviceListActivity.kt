package com.cumulations.libreV2.activity

import android.Manifest.permission
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cumulations.libreV2.SharedPreferenceHelper
import com.cumulations.libreV2.activity.BluetoothLeService.LocalBinder
import com.cumulations.libreV2.adapter.CTBLEDeviceListAdapter
import com.cumulations.libreV2.adapter.OnClickInterfaceListener
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEDevice
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEPacket.BLEDataPacket
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEServiceToApplicationInterface
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEUtils
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BleCommunication
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BluetoothStateReceiver
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.ScannerBLE
import com.libreAlexa.LibreApplication
import com.libreAlexa.R
import com.libreAlexa.constants.AppConstants
import com.libreAlexa.constants.AppConstants.OPEN_APP_SETTINGS
import com.libreAlexa.constants.Constants
import com.libreAlexa.databinding.ActivityCtbluetoothDeviceListBinding
import com.libreAlexa.util.LibreLogger

/**
 * This activity is converted from java to kotlin
 * Shaik
 */

class CTBluetoothDeviceListActivity : CTDeviceDiscoveryActivity(), BLEServiceToApplicationInterface,
    OnClickInterfaceListener {
    private val TAG = "BluetoothListActivity"
    private var mBLEListAdapter: CTBLEDeviceListAdapter? = null
    private var mBleDevices = ArrayList<BLEDevice>()
    private val mBTDevicesHashMap = HashMap<String, BLEDevice>()
    private var mBTLeScanner: ScannerBLE? = null
    private var mBleCommunication: BleCommunication? = null
    private var mBluetoothLeService: BluetoothLeService? = null
    private val TIMEOUT_WIFI = 10000
    private lateinit var binding: ActivityCtbluetoothDeviceListBinding
    private lateinit var sharedPreference: SharedPreferenceHelper
    private var alertDialog: AlertDialog? = null
    private var mDeviceClicked: BLEDevice? = null
    private var mDeviceAddress = ""
    private var bluetoothStateReceiver: BluetoothStateReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCtbluetoothDeviceListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreference = SharedPreferenceHelper.getInstance(this)
        mBTLeScanner = ScannerBLE(this, 7000, -100)
        mBleCommunication = BleCommunication(this)

        binding.ivRefresh.setOnClickListener {
            handler.removeCallbacks(showWifiConfigurationScreen)
            startScan()
            binding.noBleDeviceFrameLayout.visibility = View.GONE
            binding.layDeviceCount.visibility = View.GONE
        }
        binding.ivBack.setOnClickListener {
            intentToHome(this@CTBluetoothDeviceListActivity)
        }
        binding.btnTurnOnBt.setOnClickListener {
            startScan()
        }
        mBleDevices = ArrayList()
        mBLEListAdapter = CTBLEDeviceListAdapter(this, R.layout.ct_bledevice_adapter, mBleDevices)
        binding.ivBledevicelist.adapter = mBLEListAdapter
    }

    override fun onStart() {
        super.onStart()
        val currentAPIVersion = VERSION.SDK_INT
        if (currentAPIVersion >= 31) {
            checkPermissions()
        }else{
            startScan()
        }
        val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE)
        bluetoothStateReceiver= BluetoothStateReceiver(this)
        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(bluetoothStateReceiver,filter)

    }

    override fun onDestroy() {
        super.onDestroy()
        stopScan()
        mBluetoothLeService = null
    }

    // Code to manage Service lifecycle.
    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            LibreLogger.d(TAG, "Service connected")
            mBluetoothLeService = (service as LocalBinder).service
            mBluetoothLeService?.disconnect()
            mBluetoothLeService?.close()
            if (!mBluetoothLeService?.initialize(mBTLeScanner!!.getmBluetoothAdapter(), this@CTBluetoothDeviceListActivity)!!) {
                LibreLogger.d(TAG, "Unable to initialize Bluetooth")
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            LibreLogger.d(TAG, "Service Disconnected")
            mBluetoothLeService = null
        }
    }

    override fun onResume() {
        super.onResume()
        if (mBluetoothLeService != null) {
            mBluetoothLeService!!.addBLEServiceToApplicationInterfaceListener(this)
        }
        val filter1 = IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED)
        val filter2 = IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED)
        val filter3 = IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        this.registerReceiver(BTReceiver, filter1)
        this.registerReceiver(BTReceiver, filter2)
        this.registerReceiver(BTReceiver, filter3)
    }

    override fun onBackPressed() {
        intentToHome(this@CTBluetoothDeviceListActivity)
        super.onBackPressed()
    }

    //The BroadcastReceiver that listens for bluetooth broadcasts
    private val BTReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            LibreLogger.d(TAG, "BLE STATE *** ACTION \n$action")
            if (BluetoothDevice.ACTION_ACL_CONNECTED == action) {
                LibreLogger.d(TAG, "BLE STATE *** ACL_CONNECTED")
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED == action) {
                LibreLogger.d(TAG, "BLE STATE *** ACL_DISCONNETED")
                if (mBluetoothLeService != null) {
//          iN fUTURE we need to add logic of mulitple disconnection fixes
                    //     mBluetoothLeService!!.close()
//                    mBluetoothLeService!!.disconnect()

                    LibreLogger.d(CTBluetoothPassCredentials.TAG,"BLE backpress 3")
                    //showToast("BLE_STATE: Disconnected")
                    LibreLogger.d(TAG, "BLE STATE *** ACL_DISCONNETED CLOSE")
                    //SUMA BACK PRESS intentToHome(this@CTBluetoothDeviceListActivity)
                }
            }
        }
    }

    /**
     * StartScan will call after permissions allowed if no permissions, checkPermissions will
     * trigger
     */
    fun startScan() {
        binding.noBleDeviceFrameLayout.visibility = View.GONE
        binding.layDeviceCount.visibility = View.GONE
        binding.ivBledevicelist.visibility = View.GONE
        binding.layNoBtOn.visibility = View.GONE
        if (!BLEUtils.checkBluetooth(this@CTBluetoothDeviceListActivity)) {
            promptBluetoothEnable()
        }else {
            mBleDevices.clear()
            mBTDevicesHashMap.clear()
            mBLEListAdapter!!.notifyDataSetChanged()
            binding.layTurnOnBt.visibility=View.VISIBLE
            binding.ivRefresh.visibility=View.VISIBLE
            binding.layNoBtOn.visibility=View.GONE
            showProgressDialog(getString(R.string.looking_for_devices))
            if (mBTLeScanner != null) {
                mBTLeScanner?.start(this@CTBluetoothDeviceListActivity)
            } else {
                mBTLeScanner = ScannerBLE(this, 7000, -100)
                mBTLeScanner?.start(this@CTBluetoothDeviceListActivity)
            }
            handler.postDelayed(showWifiConfigurationScreen, TIMEOUT_WIFI.toLong())
        }
    }



    fun stopScan() {
        dismissDialog()
        if (BLEUtils.checkBluetooth(this@CTBluetoothDeviceListActivity)) {
            mBTLeScanner!!.stop()
        } else {
           LibreLogger.d(TAG,"Bluetooth is not enabled")
        }
        if (mBleDevices.size == 0) {
            binding.noBleDeviceFrameLayout.visibility = View.VISIBLE
            binding.layDeviceCount.visibility = View.GONE
        } else {
            binding.noBleDeviceFrameLayout.visibility = View.GONE
            binding.layDeviceCount.visibility = View.VISIBLE
            binding.ivBledevicelist.visibility = View.VISIBLE
            if (mBleDevices.size > 1) {
                binding.txtDeviceCount.text = mBleDevices.size.toString() + getString(R.string.devices_are_available_new)
            } else {
                binding.txtDeviceCount.text = mBleDevices.size.toString() + getString(R.string.device_are_available_new)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(mServiceConnection)
        stopScan()
        mBluetoothLeService?.removelistener(this)
        unregisterReceiver(bluetoothStateReceiver)
    }

    fun isRivaSpeaker(device: BluetoothDevice): Boolean {
        if (VERSION.SDK_INT >= 31) {
            if (ActivityCompat.checkSelfPermission(this, permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                //suma perm(check may change based on future target SDK)
            }
        }
        return device.name != null && device.name.contains("nuco")
    }

    /* return ((device.getName() != null) && device.getName().contains("nuConn") || device.getName()
        .contains(("E300")) || device.getName().contains(("VSSL SX")) || device.getName()
        .contains(("EA-300-LYNK"))
        || device.getName().contains(("CS1_")));
  }*/
    fun addDevice(device: BluetoothDevice, rssi: Int) {
        val address = device.address
        var name = ""
        if (VERSION.SDK_INT >= 31) {
            if (ActivityCompat.checkSelfPermission(this, permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                //suma perm(check may change based on future target SDK)
                return
            }
        }
        if (device.name != null) {
            name = device.name
        }
        LibreLogger.d(TAG, " New Ble device found " + name + " Mac Id " + device.address)/*  if its not a riva speaker just retun it *//*     if(!isRivaSpeaker(device))
            return;*/
        if (!mBTDevicesHashMap.containsKey(address)) {
            val btleDevice = BLEDevice(device, rssi)
            btleDevice.rssi = rssi
            mBTDevicesHashMap[address] = btleDevice
            LibreLogger.d(TAG, " Added BLE Device " + name + " MAC id " + btleDevice.address)
            mBleDevices.add(btleDevice)
            handler.removeCallbacks(showWifiConfigurationScreen)
        } else {
            mBTDevicesHashMap[address]!!.rssi = rssi
        }
        mBLEListAdapter!!.notifyDataSetChanged()
    }

    val handler = Handler(Looper.myLooper()!!)
    val runnable = Runnable {
        dismissDialog()
        if (mBleDevices.size == 0) {
            binding.noBleDeviceFrameLayout.visibility = View.VISIBLE
            binding.layDeviceCount.visibility = View.GONE
        } else {
            binding.noBleDeviceFrameLayout.visibility = View.GONE
            binding.layDeviceCount.visibility = View.VISIBLE
        }
    }
    private val showWifiConfigurationScreen = Runnable { callConnectToWifiConfiguration() }
    private fun callConnectToWifiConfiguration() {
        if (mBleDevices.size > 0) {
            return
        }
        runOnUiThread {
            dismissDialog()
            if (mBleDevices.size == 0) {
                binding.noBleDeviceFrameLayout.visibility = View.VISIBLE
                binding.layDeviceCount.visibility = View.GONE
                binding.ivBledevicelist.visibility = View.GONE
            } else {
                binding.noBleDeviceFrameLayout.visibility = View.GONE
                binding.layDeviceCount.visibility = View.VISIBLE
                binding.ivBledevicelist.visibility = View.VISIBLE
            }
        }
    }

    val disconnectBle = Runnable {
        if (mBluetoothLeService != null && blPlayToneButtonClicked) {
            blPlayToneButtonClicked = false
            mBluetoothLeService!!.disconnect()
            mBluetoothLeService!!.close()
        }
    }

    override fun onConnectionSuccess(btGattChar: BluetoothGattCharacteristic) {
        mBluetoothLeService!!.removelistener(this)
        callConnectToWifiActivityPage()
    }

    override fun receivedBLEDataPacket(packet: BLEDataPacket, hexData: String) {}
    override fun writeSucess(status: Int) {}
    override fun onDisconnectionSuccess(status: Int) {
        LibreApplication.betweenDisconnectedCount = 0
        LibreApplication.disconnectedCount = 0
        // callConnetToWifiActivityPage();
    }

    private fun callConnectToWifiActivityPage() {
        dismissDialog()
        val intent = Intent(this, CTBluetoothHearSoundQtn::class.java)
        intent.putExtra(Constants.CONFIG_THRO_BLE, true)
        LibreLogger.d(TAG, " Device Name " + mDeviceClicked!!.name)
        intent.putExtra(AppConstants.DEVICE_NAME, "")
        intent.putExtra(AppConstants.DEVICE_BLE_ADDRESS, mDeviceClicked!!.address)
        startActivity(intent)
        finish()
    }

    private var blPlayToneButtonClicked = false
    override fun onInterfaceClick(view: View, position: Int) {
        val id = view.id
        if (id == R.id.btnPlayTone) {
        /* runOnUiThread(new Runnable() {
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
            if(BLEUtils.checkBluetooth(this@CTBluetoothDeviceListActivity)) {
                if (blPlayToneButtonClicked) {
                    return
                }
                mDeviceClicked = mBleDevices[position]
                if (VERSION.SDK_INT >= VERSION_CODES.M) {
                    mBluetoothLeService!!.connect(mDeviceClicked!!.address)
                }
                mDeviceAddress = mDeviceClicked!!.address
                runOnUiThread {
                    showProgressDialog(getString(R.string.get_connecting) + " " + mDeviceClicked!!.name)
                    handler.postDelayed(runnable, 12000)
                }
            }else{
                promptBluetoothEnable()
            }

        }
    }
    private fun promptBluetoothEnable() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        customStartActivityForResult(AppConstants.BT_ENABLED_REQUEST_CODE, enableBtIntent)
    }

    companion object {
        val permissions = arrayOf(BLUETOOTH_CONNECT,
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION,
            BLUETOOTH_SCAN,
            BLUETOOTH_CONNECT)
    }

     private fun checkPermissions() {
        if (!checkPermissionsGranted()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, BLUETOOTH_SCAN)) {
                /**
                 * Request the Permission with Rationale with educating the user
                 */
                showAlertDialog(getString(R.string.permission_required), getString(R.string.ok), getString(R.string.cancel), 0)
            } else {
                /**
                 *  Request the permission directly, without explanation first time launch
                 */
                requestPermissionLauncher.launch(permissions)
            }
        } else {
            /**
             *  Permission is already granted, proceed with your logic
             */
            if (alertDialog != null && alertDialog!!.isShowing) alertDialog?.dismiss()
            handler.removeCallbacks(showWifiConfigurationScreen)
            startScan()
        }

    }

    private fun checkPermissionsGranted(): Boolean {
        // Iterate through the permissions and check if any of them are not granted
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true

    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
    { result ->
        var allGranted = true
        for (isGranted in result.values) {
            allGranted = allGranted && isGranted
        }
        if (allGranted) {
            if (alertDialog != null && alertDialog!!.isShowing) alertDialog?.dismiss()
            handler.removeCallbacks(showWifiConfigurationScreen)
            startScan()
        }
        else {
            if (!sharedPreference.isFirstTimeAskingPermission(BLUETOOTH_SCAN)) {
                sharedPreferenceHelper.firstTimeAskedPermission(BLUETOOTH_SCAN, true)
                showAlertDialog(getString(R.string.permission_required), getString(R.string.ok), getString(R.string.cancel), 0)
            } else {
                /**
                 * This will take care of user didn't give NFC permission
                 */
                showAlertDialog(getString(R.string.permission_denied), getString(R.string.open_settings), getString(R.string.cancel), 100)
            }

        }

    }

    private fun showAlertDialog(message: String,
        positiveButtonString: String,
        negativeButtonString: String,
        requestCode: Int) {
        if (alertDialog != null && alertDialog!!.isShowing) alertDialog!!.dismiss()
        else alertDialog = null

        if (alertDialog == null) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(message)
            builder.setMessage(getString(R.string.permission_connect))
            builder.setCancelable(false)
            builder.setPositiveButton(positiveButtonString) { dialogInterface, i ->
                alertDialog!!.dismiss()
                if (requestCode == 0) {
                    requestPermissionLauncher.launch(permissions)
                } else {
                    /**
                     * Open App Settings if user don't allow the permission
                     */
                    val packageName = packageName
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.fromParts("package", packageName, null)
                  //  openSettingsLauncher.launch(intent)
                    customStartActivityForResult(OPEN_APP_SETTINGS,intent)
                }
            }
            /**
             * Reserved for future -> Negative Button is commented because if any customer or
             * Requirement come we can use the below code
             */
            /* builder.setNegativeButton(negativeButtonString) { dialogInterface, i ->
                 alertDialog!!.dismiss()
             }*/
            alertDialog = builder.create()
        }
        if (!alertDialog!!.isShowing) alertDialog!!.show()

    }
//    private fun requestUserBluetooth():Boolean {
//        LibreLogger.d("===SHAIK===", "7")
//        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//        customStartActivityForResult(AppConstants.BT_ENABLED_REQUEST_CODE, enableBtIntent)
//    /*  return if (!isBTPermissionAsked) {
//            isBTPermissionAsked = true
//            LibreLogger.d(TAG,"requestUserBluetooth trigger")
//            LibreLogger.d("===SHAIK===","7")
//            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//            customStartActivityForResult(AppConstants.BT_ENABLED_REQUEST_CODE, enableBtIntent)
//            true
//        } else {
//            LibreLogger.d("===SHAIK===","8")
//            LibreLogger.d(TAG,"requestUserBluetooth trigger ELSE")
//            isBTPermissionAsked=false
//            false
//        }*/
//    }

    override fun customOnActivityResult(data: Intent?, requestCode: Int, resultCode: Int) {
        super.customOnActivityResult(data, requestCode, resultCode)
        if (requestCode == OPEN_APP_SETTINGS) {
            if(resultCode == RESULT_OK) {
                /** The user may have granted the permission in settings
                 *  You can check again if the permission is granted here
                 */
                if (alertDialog != null && alertDialog!!.isShowing) alertDialog?.dismiss()
                handler.removeCallbacks(showWifiConfigurationScreen)
                startScan()
            }

        }else if (requestCode == AppConstants.BT_ENABLED_REQUEST_CODE) {
            /**
             * The Below Source Code will take the user to BT Settings screen, now default BT
             * Enable Alert Box is triggering so we don't need to call the below intent and
             * as well as don't need to handle the result also
             * Note:- Most of the BLE Apps and smart watch apps are doing same
             */
            if (resultCode == RESULT_OK) {
                binding.layTurnOnBt.visibility = View.VISIBLE
                binding.layNoBtOn.visibility = View.GONE
                binding.ivRefresh.visibility = View.VISIBLE
                startScan()
            } else {
                binding.layNoBtOn.visibility = View.VISIBLE
                binding.layTurnOnBt.visibility = View.GONE
                binding.ivRefresh.visibility = View.GONE
            }
            /* val intentOpenBluetoothSettings = Intent()
                intentOpenBluetoothSettings.action = Settings.ACTION_BLUETOOTH_SETTINGS
                startActivity(intentOpenBluetoothSettings)*/
        }else{
            LibreLogger.d(TAG, "RequestCode are not handled")
        }
    }
    //Commented by SHAIK,once QA confirmed we can remove this code
    /*private val openSettingsLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            *//** The user may have granted the permission in settings
             *  You can check again if the permission is granted here
             *//*
            if (alertDialog != null && !alertDialog!!.isShowing) alertDialog?.dismiss()
            handler.removeCallbacks(showWifiConfigurationScreen)
            startScan()
        }
    }*/

}
