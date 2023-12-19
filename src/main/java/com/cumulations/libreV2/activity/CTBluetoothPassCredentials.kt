package com.cumulations.libreV2.activity

import android.annotation.TargetApi
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.text.Html
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.text.set
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cumulations.libreV2.AppUtils
import com.cumulations.libreV2.AppUtils.getConnectedSSID
import com.cumulations.libreV2.AppUtils.networkMismatchMessage
import com.cumulations.libreV2.AppUtils.networkMismatchSsidMessage
import com.cumulations.libreV2.activity.BluetoothLeService.LocalBinder
import com.cumulations.libreV2.adapter.WifiListBottomSheetAdapterForSecurityType
import com.cumulations.libreV2.adapter.WifiSecurityConfigurationItemClickInterface
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEPacket
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEPacket.BLEDataPacket
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEPacket.decodeBleData
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEServiceToApplicationInterface
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEUtils
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BleCommunication
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.ConfigurationParameters
import com.cumulations.libreV2.model.ScanResultItem
import com.cumulations.libreV2.model.WifiConnection
import com.cumulations.libreV2.roomdatabase.LibreVoiceDatabase
import com.cumulations.libreV2.roomdatabase.PasswordRememberDao
import com.cumulations.libreV2.roomdatabase.PasswordRememberDataClass
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.libreAlexa.LibreApplication
import com.libreAlexa.R
import com.libreAlexa.constants.AppConstants
import com.libreAlexa.constants.Constants
import com.libreAlexa.databinding.CtActivityConnectToWifiBinding
import com.libreAlexa.luci.LSSDPNodes
import com.libreAlexa.netty.LibreDeviceInteractionListner
import com.libreAlexa.netty.NettyData
import com.libreAlexa.util.LibreLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.Locale


class CTBluetoothPassCredentials : CTDeviceDiscoveryActivity(), BLEServiceToApplicationInterface,
    View.OnClickListener, LibreDeviceInteractionListner,
    WifiSecurityConfigurationItemClickInterface {
    private var mIntentExtraScanResults = false
    private var mDeviceName: String? = null
    private var prev_Activity: String? = null
    private var isSacTimedOutCalled = false
    private var mBluetoothLeService: BluetoothLeService? = null
    private var configurationParameters = ConfigurationParameters.getInstance()
    private var passphrase: String? = null
    private lateinit var savePwdList: List<PasswordRememberDataClass>
    private lateinit var libreVoicePasswordRememberDao: PasswordRememberDao
    private lateinit var binding: CtActivityConnectToWifiBinding
    private var btCharacteristic: BluetoothGattCharacteristic? = null
    private var taskJob: Job? = null
    private var scanListLength: Int? = null
    private var isDisconnectionHandled = false
    private var state = true
    private var mandateDialog: AlertDialog? = null
    private var wifiReceiver: WifiReceiver? = null
var bottomSheetDialogForSecurity: BottomSheetDialog? = null
    var tv_no_data: TextView? = null
    var rv_wifi_list: RecyclerView? = null
    var wifiListBottomSheetAdapterForSecurityType: WifiListBottomSheetAdapterForSecurityType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CtActivityConnectToWifiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        libreVoicePasswordRememberDao = LibreVoiceDatabase.getDatabase(this).passwordRememberDao()
        lifecycleScope.launch(Dispatchers.IO) {
            getAllSSIDWithPWD()
        }
        initViews()
        intentExtra
        initBluetoothAdapterAndListener()
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun initBluetoothAdapterAndListener() {
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter
        val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE)
    }
    override fun onResume() {
        super.onResume()
        wifiReceiver = WifiReceiver(mandateDialog,binding.tvSelectedWifi.text.toString())
        val intentFilter = IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        registerReceiver(wifiReceiver, intentFilter)
    }
    override fun onPause() {
        super.onPause()
        mBluetoothLeService = null
        unregisterReceiver(wifiReceiver)
    }

    var mBluetoothAdapter: BluetoothAdapter? = null
    var mDeviceAddress: String? = null

    // Code to manage Service lifecycle.
    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            isItDying = false
            mBluetoothLeService = (service as LocalBinder).service
            if (!mBluetoothLeService!!.initialize(mBluetoothAdapter, this@CTBluetoothPassCredentials)) {
                finish()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mBluetoothLeService!!.connect(mDeviceAddress)
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mBluetoothLeService = null
        }

        override fun onBindingDied(name: ComponentName) {
            isItDying = true
        }
    }
    private var isItDying = false

    private val intentExtra: Unit
        get() {
            mIntentExtraScanResults = intent.getBooleanExtra(Constants.CONFIG_THRO_BLE, true)
            mDeviceName = intent.getStringExtra(AppConstants.DEVICE_NAME)
            prev_Activity = intent.getStringExtra(AppConstants.prev_activity)
            binding.etDeviceName.setText(mDeviceName)
            mDeviceAddress = intent.getStringExtra(AppConstants.DEVICE_BLE_ADDRESS)
        }

    override fun onBackPressed() {
        if (mBluetoothLeService != null) {
            mBluetoothLeService!!.close()
            mBluetoothLeService!!.disconnect()
        }
        callBluetoothDeviceListActivity()
        super.onBackPressed()
    }

    fun initViews() {
        binding.etDeviceName.isEnabled = true
        binding.btnNext.setOnClickListener(this)
        binding.btnCancel.setOnClickListener(this)
        binding.btnCancel.isEnabled = true
        binding.ivRightArrow.setOnClickListener(this)
        binding.tvSelectedWifi.setOnClickListener(this)
        binding.ivBack.setOnClickListener(this)
        binding.ivSecArrow.setOnClickListener(this)
       binding.manualSsidLayout.visibility=View.GONE
        binding.manualSecurityLayout.visibility=View.GONE

        binding.togglePasswordImageview.setOnClickListener {
            if (state == true) {
                state = false
                binding.etWifiPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.togglePasswordImageview.setImageResource(R.drawable.ic_password_invisible)
            } else if (state == false) {
                state = true
                binding.etWifiPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.togglePasswordImageview.setImageResource(R.drawable.ic_password_visible)
            }
        }
        try {
            val connectedPhoneSSID: Pair<String, String?> = AppUtils.getConnectedSSIDAndSecurityType(this)
            binding.tvSelectedWifi.text = connectedPhoneSSID.first
            LibreLogger.d(TAG, "Shaik setSsidPwd ${connectedPhoneSSID.first} and  Security " +
                    "${connectedPhoneSSID.second}")
            val scanResultItem = ScanResultItem(connectedPhoneSSID.second!!,connectedPhoneSSID.first)
            setSsidPwd(scanResultItem)
        } catch (ex: Exception) {
            //suma thursday
            binding.tvSecurity.text = "Security Type : " +"NONE"

            binding.passwordWifi.visibility=View.GONE
            binding.ivRememPasswordlyt.visibility=View.GONE
            LibreLogger.d(TAG_, "Shaik setSsidPwd Exception ${ex.message}")
        }
    }

    val handler = Handler(Looper.myLooper()!!)
    val runnable = Runnable {
        dismissDialog()
    }

    override fun onConnectionSuccess(value: BluetoothGattCharacteristic) {
        btCharacteristic = value
        runOnUiThread {
            var timeout = 500
            if (mDeviceName!!.isEmpty()) {
                handler.postDelayed({
                    val data = ByteArray(0)
                    val mBlePacketScanWIFI = BLEPacket(data, BLEUtils.BLE_SAC_APP2DEV_FRIENDLYNAME.toByte(), true)
                    BleCommunication.writeDataToBLEDevice(value, mBlePacketScanWIFI)
                     LibreLogger.d("SUMA_SCAN", "******** SUMA GET THE BLE PACKET INFO FriendlyNameBLEPASS")

                    //  getFriendlyNameThroBLE();
                }, timeout.toLong())
            } else {
                timeout = 0
            }

//            handler.postDelayed({
//                val data = ByteArray(0)
//                val mBlePacketScanWIFI = BLEPacket(data, BLEUtils.BLE_SAC_APP2DEV_SECURITY_CHECK.toByte(), true)
//                BleCommunication.writeDataToBLEDevice(value, mBlePacketScanWIFI)
//                LibreLogger.d("SUMA_SCAN", "******** SUMA GET THE BLE PACKET INFO SECURITYCHECK_BLEPASS")
//
//  don remove dis block of code:SUMA              //  getFriendlyNameThroBLE();
//            }, (timeout + 1000).toLong())

            if (prev_Activity.equals("CTBluetoothHearSoundQtn")) {
                LibreLogger.d("SUMA SCAN", "suma in get scan value BLE_PASS")
//               LibreLogger.d(
//                   "SUMA_SCAN",
//                   "***** SCAN Suma in get the wifi scan sent BLEPASS" + LibreApplication.scanAlreadySent + "Scan_Results_Empty\n" + WifiConnection.getInstance().savedScanResults.isEmpty()
//               )
                LibreLogger.d(TAG, "Security check 27 writing SUMA GET THE BLE PACKET " + "$isSacTimedOutCalled")
                if (!isSacTimedOutCalled) {
                    isSacTimedOutCalled = true
                    handler.postDelayed({
                        val data = ByteArray(0)
                        val mBlePacketScanWIFI = BLEPacket(data, BLEUtils.BLE_SAC_APP2DEV_SECURITY_CHECK.toByte(), true)
                        BleCommunication.writeDataToBLEDevice(value, mBlePacketScanWIFI)
                        LibreLogger.d("SUMA_SCAN", "******** SUMA GET THE BLE PACKET INFO two")

                        LibreLogger.d(TAG, "Security check 27 writing SUMA GET THE BLE PACKET " + "INFO" + " " + "SECURITYCHECK_BLEPASS")

                        //  getFriendlyNameThroBLE();
                    }, (timeout + 1000).toLong())

                    LibreApplication.scanAlreadySent = true;
                  //  showProgressDialog(getString(R.string.get_scan_results))

                    handler.postDelayed({
                        val data = ByteArray(0)
                        val mBlePacketScanWIFI = BLEPacket(data, BLEUtils.BLE_SAC_APP2DEV_SCAN_WIFI.toByte(), true)
                        BleCommunication.writeDataToBLEDevice(value, mBlePacketScanWIFI)
                        LibreLogger.d("SUMA_SCAN", "******** SUMA GET THE BLE PACKET INFO THREE")

                        //  getFriendlyNameThroBLE();
                    }, (timeout + 1500).toLong())

                    handler.postDelayed(runnable, 12000)
                }
            }
            // }
        }
    }

    /**
     * public static final int BLE_SAC_DEV2APP_CRED_RECEIVED = 20;
     * public static final int BLE_SAC_DEV2APP_CRED_SUCCESS = 21;
     * public static final int BLE_SAC_DEV2APP_CRED_FAILURE = 22;
     * public static final int BLE_SAC_DEV2APP_WIFI_CONNECTING = 23;
     * public static final int BLE_SAC_DEV2APP_WIFI_CONNECTED = 24;
     * public static final int BLE_SAC_DEV2APP_WIFI_CONNECTING_FAILED = 25;
     *
     * public static final int BLE_SAC_DEV2APP_WIFI_STATUS = 26;
     * public static final int BLE_SAC_DEV2APP_WIFI_AP_NOT_FOUND = 27;
     * public static final int BLE_SAC_DEV2APP_WIFI_DISCONNECTED = 28;
     */
    override fun receivedBLEDataPacket(packet: BLEDataPacket, hexData: String) {
        when (packet.command) {
            BLEUtils.BLE_SAC_APP2DEV_FRIENDLYNAME -> if (packet.getcompleteMessage().isNotEmpty()) {
                val mDeviceNameArray = ByteArray(packet.dataLength.toInt())
                var i = 0
                while (i < packet.dataLength) {
                    mDeviceNameArray[i] = packet.message[i]
                    i++
                }
                mDeviceName = String(mDeviceNameArray)
                runOnUiThread { binding.etDeviceName.setText(mDeviceName) }
            }

            /*  BLEUtils.BLE_SAC_DEV2APP_CRED_RECEIVED -> runOnUiThread { setMessageProgressDialog(getString(R.string.cred_received)) }
              BLEUtils.BLE_SAC_DEV2APP_CRED_SUCCESS -> runOnUiThread { setMessageProgressDialog(getString(R.string.cred_success)) }*/
            BLEUtils.BLE_SAC_DEV2APP_CRED_FAILURE -> runOnUiThread {
                showAlertMessageRegardingSAC(" ", getString(R.string.credientials_invalid)) }
                BLEUtils.BLE_SAC_DEV2APP_WIFI_CONNECTING -> runOnUiThread {
                dismissDialog()
                binding.laySsidPwdDetails.visibility = View.GONE
                binding.laySpeakerSetupWithImage.visibility = View.VISIBLE
                //setMessageProgressDialog(getString(R.string.connecting_wifi))
                binding.laySpeakerSetup.tvSetupInfo.text = getString(R.string.connecting_wifi)
            }

            BLEUtils.BLE_SAC_DEV2APP_WIFI_CONNECTED -> {
                lifecycleScope.launch {
                    cancelJob()
                }
                runOnUiThread {
                    dismissDialog()
                    binding.laySsidPwdDetails.visibility = View.GONE
                    binding.laySpeakerSetupWithImage.visibility = View.VISIBLE
                    try {
                        val connectedSSID = AppUtils.getConnectedSSID(this)
                        val message = getString(R.string.connected) +" " +connectedSSID +" " +getString(R.string.successfully)
                        //setMessageProgressDialog(message)
                        binding.laySpeakerSetup.tvSetupInfo.text = message
                    } catch (ex: Exception) {
                        binding.laySsidPwdDetails.visibility = View.GONE
                        binding.laySpeakerSetupWithImage.visibility = View.VISIBLE
                        setMessageProgressDialog(getString(R.string.speaker_connected))
                        ex.printStackTrace()
                    }
                }
                lifecycleScope.launch {
                    delay(2000)
                    val data1 = ByteArray(0)
                    val mBleStop = BLEPacket(data1, BLEUtils.BLE_SAC_APP2DEV_STOP_M.toByte())
                    BleCommunication.writeDataToBLEDevice(mBleStop)
                    goToConnectToMainNetwork()
                }
            }

            BLEUtils.BLE_SAC_DEV2APP_WIFI_CONNECTING_FAILED -> {
                val connectedPhoneSSID: Pair<String, String?> = AppUtils.getConnectedSSIDAndSecurityType(this)
                val data = decodeBleData(hexData)
                if (data != -1) {
                    when (data) {
                        0 -> {
                            showAlertMessage(getString(R.string.wi_fi_connection_failed), getString(R.string.something_went_wrong))
                        }

                        1 -> {
                            showAlertMessage(getString(R.string.wi_fi_connection_failed),
                                getString(R.string.incorrect_password_entered_for_the_selected) +
                                        " " + connectedPhoneSSID.first)
                        }

                        2 -> {
                            showAlertMessage(getString(R.string.wi_fi_connection_failed), getString(R.string.it_seems_that_your_device_is_currently_out_of_wi_fi_range_please_move_closer_to_a_wi_fi_hotspot_or_ensure_that_your_wi_fi_is_turned_on))
                        }

                        else -> {
                            showAlertMessage(getString(R.string.wi_fi_connection_failed), getString(R.string.something_went_wrong))
                        }
                    }
                } else {
                    showAlertMessage(getString(R.string.wi_fi_connection_failed), getString(R.string.something_went_wrong))
                }

                /*val mMessageInt = packet.getcompleteMessage()
                val message = mMessageInt[3].toInt()
                if (message == 26) {
                    runOnUiThread { showAlertMessageRegardingSAC(getString(R.string.configuration_failed_hd), getString(R.string.configuration_failed_msg)) }
                } else {
                    runOnUiThread { showAlertMessageRegardingSAC(getString(R.string.configuration_failed_hd), getString(R.string.configuration_timeout_msg)) }
                }*/
            }

            BLEUtils.BLE_SAC_DEV2APP_WIFI_AP_NOT_FOUND -> runOnUiThread { setMessageProgressDialog(getString(R.string.ap_notfound)) }
            BLEUtils.BLE_SAC_DEV2APP_WIFI_DISCONNECTED -> {}
            BLEUtils.BLE_SAC_DEV2APP_STARTED -> {}
            BLEUtils.BLE_SAC_DEV2APP_SCAN_LIST_START -> {
                constructJSonString = StringBuilder()
              //  scanListLength = String(packet.message).toInt()
            }

            BLEUtils.BLE_SAC_DEV2APP_SCAN_LIST_DATA -> {
                constructJSonString.append(String(packet.message))
            }

            BLEUtils.BLE_SAC_DEV2APP_SCAN_LIST_END -> {
                /** Shaik New change, if size match then only show the scan list, according to
                 * device team(Sampath),tested with development FW and working fine
                 * Later Have to test in the newly released FW not in 3020
                 * Note:-  Have to Add the same change in the @CTWifiListActivity Activity while
                 * releasing this app
                 * Commented the code because there is some issue from device end once it's
                 * resolved we have to enable the below code
                 */
                populateScanListMap(constructJSonString.toString())

                /*if (scanListLength == constructJSonString.length) {
                    populateScanListMap(constructJSonString.toString())
                } else {
                  //  showSomethingWentWrongAlert(this@CTBluetoothPassCredentials)
                    populateScanListMap(constructJSonString.toString())
                    LibreLogger.d(TAG, "Scan List size is not matching " + "${constructJSonString.length}")
                }*/
                dismissDialog()
            }

            BLEUtils.BLE_SAC_APP2DEV_SECURITY_CHECK -> {
                LibreLogger.d(TAGSCAN, "KARUNAKARAN  " + packet.dataLength)
                var security = 0
                if (packet.dataLength > 0) {
                    val mMessageInt1 = packet.getcompleteMessage()
                    security = mMessageInt1[4].toInt() //Integer.parseInt(mMessageInt1[4])
                    LibreLogger.d(TAGSCAN, "KARUNAKARAN  security$security") // ;
                }
                mSecurityCheckEnabled = security != 0
            }
        }
    }

    private fun showAlertMessage(title: String, message: String) {
        runOnUiThread {
            if (!isFinishing) {
                LibreLogger.d(TAGBLE_SHAIk, "alert dialog enter")
                val builder = AlertDialog.Builder(this@CTBluetoothPassCredentials).also {
                    it.setMessage(message)
                        .setTitle(title)
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.retry)) { dialog, id ->
                            dialog.dismiss()
                            dismissDialog()
                            try {
                                binding.laySsidPwdDetails.visibility = View.VISIBLE
                                binding.laySpeakerSetupWithImage.visibility = View.GONE
                            } catch (ex: Exception) {
                                binding.laySsidPwdDetails.visibility = View.VISIBLE
                                binding.laySpeakerSetupWithImage.visibility = View.GONE
                                ex.printStackTrace()
                            }

                        }
                        .setNegativeButton(getString(R.string.go_to_home)) { _, id ->
                            intentToHome(this@CTBluetoothPassCredentials)
                        }
                }
                //Creating dialog box
                val alert = builder.create()
                alert.show()
            } else {
                LibreLogger.d(TAGBLE_SHAIk, "alert dialog else")
            }
        }
    }

    private var mSecurityCheckEnabled = false


    private var scanListMap: MutableMap<String, Pair<String, Int>> = LinkedHashMap()

    private fun populateScanListMap(scanList: String?) {
        scanListMap.clear()
        try {
            val mainObj = JSONObject(scanList!!)
            LibreLogger.d(TAGSCAN, "populateScanListMap scanList " + scanList)
            val scanListArray = mainObj.getJSONArray("Items")

            for (i in 0 until scanListArray.length()) {
                val obj = scanListArray[i] as JSONObject
                if (obj.getString("SSID") == null || obj.getString("SSID").isEmpty()) {
                    continue
                }

                val ssid = fromHtml(obj.getString("SSID")).toString()
                val security = fromHtml(obj.getString("Security")).toString()
                val rssi = obj.getInt("rssi")

                scanListMap[ssid] = Pair(security, rssi)


            }

            // Add "Other Options" SSID with dummy security and RSSI
            val otherOptionsSSID = "Other Options"
            val otherOptionsSecurity = "WPA-PSK"
            val otherOptionsRssi = 255
            scanListMap[otherOptionsSSID] = Pair(otherOptionsSecurity, otherOptionsRssi)

            for ((ssid, securityAndRssi) in scanListMap) {
                val security = securityAndRssi.first
                val rssi = securityAndRssi.second

                LibreLogger.d(TAGSCAN, "populateScanListMap  $ssid and $security and $rssi")
                WifiConnection.getInstance().putWifiScanResultSecurity(ssid, security, rssi)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            LibreLogger.d(TAGSCAN, "populateScanListMap exception " + e.message)
        }
    }
    private var constructJSonString = StringBuilder()
    private fun showAlertMessageRegardingSAC(title: String?, message: String) {
        dismissDialog()
        if (!isFinishing) {
            val builder = AlertDialog.Builder(this)
            //Uncomment the below code to Set the message and title from the strings.xml file
            builder.setMessage(message).setTitle(title).setCancelable(false).setPositiveButton(getString(R.string.ok)) { dialog, id ->
                if (message.equals(getString(R.string.credientials_invalid), ignoreCase = true)) {
                    // byte[] mData = new byte[0];
                    // BLEPacket mBleWifiStatus = new BLEPacket(mData, (byte) BLE_SAC_APP2DEV_TRIGGER_SAC);
                    // BleCommunication.writeDataToBLEDevice(mBleWifiStatus);
                }
                dialog.dismiss()
                dismissDialog()
                try {
                    binding.laySsidPwdDetails.visibility = View.VISIBLE
                    binding.laySpeakerSetupWithImage.visibility = View.GONE
                } catch (ex: Exception) {
                    binding.laySsidPwdDetails.visibility = View.VISIBLE
                    binding.laySpeakerSetupWithImage.visibility = View.GONE
                    ex.printStackTrace()
                }

            }
            //Creating dialog box
            val alert = builder.create()
            alert.show()
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

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(view: View) {
        when (view.id) {
            R.id.et_device_name -> {}

            R.id.btn_next ->

            try {
                var mSelectedSSID = String(
                    WifiConnection.getInstance().getMainSSID()
                        .toByteArray(), StandardCharsets.UTF_8
                )

                                        if (LibreApplication.manualSSID!=null&&LibreApplication.manualSSID == "Other Options")
                                        {
                                            /*Other Network Flow*/
                                            LibreLogger.d(TAG,"suma in btnNext getting my SSID name inside\n"+mSelectedSSID)

                                            val wifiConnect = WifiConnection.getInstance()
                                            wifiConnect.setMainSSID(
                                                binding.tvSelectedWifi.getText().toString()
                                            )
                                            mSelectedSSID = String(
                                                WifiConnection.getInstance().getMainSSID()
                                                    .toByteArray(), StandardCharsets.UTF_8
                                            )
                                            val mSelectedPass = String(
                                                binding.etWifiPassword.getText().toString().toByteArray(),
                                                StandardCharsets.UTF_8
                                            )
                                            val mSelectedSecurity = String(
                                                WifiConnection.getInstance().getMainSSIDSec()
                                                    .toByteArray(), StandardCharsets.UTF_8
                                            )
                                            LibreLogger.d(
                                                TAG, """suma in device SSID manual ssid
 ${mSelectedSSID}pwd
${mSelectedPass}security
$mSelectedSecurity"""
                                            )
                                            LibreLogger.d(TAG,"suma in other Network  n/w ssid  \n"+binding.tvSelectedWifi.text+"phone ssid\n"+getConnectedSSIDName(this))
                                            if(!binding.tvSelectedWifi.text.isEmpty()){
                                              if(!mSelectedSecurity.equals("WPA-PSK")){

                                                   if(LibreApplication.manualsecurity.equals("OPEN/NONE")&&binding.etWifiPassword.text.isEmpty()){
                                                       if(binding.tvSelectedWifi.text.toString()==AppUtils.getConnectedSSID(context = this)) {
                                                           btnNextClickedOther()
                                                           LibreLogger.d(TAG,"BTN NEXT CALL case VALID TWO\n"+binding.tvSelectedWifi.text.toString()+"getconnectessid\n"+getConnectedSSIDName(this))

                                                       }
                                                       else{
                                                           LibreLogger.d(TAG,"BTN NEXT CALL case VALID THREE MISMATCH\n"+binding.tvSelectedWifi.text.toString()+"getconnectessid\n"+getConnectedSSIDName(this))

                                                           showNetworkMisMatchAlertDialog(networkMismatchSsidMessage(getConnectedSSID(context = this),binding.tvSelectedWifi.text.toString()), getString(R.string.continue_txt),
                                                               getString(R.string.cancel), isNetworkMisMatch=true,
                                                               isConfigureCancel=false)
                                                       }
                                                       LibreLogger.d(TAG, "suma in other Network AllValidation Done  \n "+LibreApplication.manualsecurity+"ssidtext"+binding.tvSelectedWifi.text+"phone ssid\n"+getConnectedSSIDName(this))

                                                   }
                                                  else{
                                                      if(binding.etWifiPassword.text.isEmpty()) {
                                                          showAlertMessageRegardingSAC(
                                                              getString(R.string.error),
                                                              getString(R.string.password_empty_error)
                                                          )
                                                      }
                                                       else{
                                                          if(binding.tvSelectedWifi.text.toString()==AppUtils.getConnectedSSID(context = this)) {
                                                              btnNextClickedOther()
                                                              LibreLogger.d(TAG,"BTN NEXT CALL case VALID TWO\n"+binding.tvSelectedWifi.text.toString()+"getconnectessid\n"+getConnectedSSIDName(this))

                                                          }
                                                          else{
                                                              showNetworkMisMatchAlertDialog(networkMismatchSsidMessage(getConnectedSSID(context = this),binding.tvSelectedWifi.text.toString()), getString(R.string.continue_txt),
                                                                  getString(R.string.cancel), isNetworkMisMatch=true,
                                                                  isConfigureCancel=false)
                                                          }
                                                          LibreLogger.d(TAG, "suma in other Network AllValidation Done else  \n ")
                                                      }
                                                   }

                                              }
                                                else{
                                                  showAlertMessageRegardingSAC(getString(R.string.error), getString(R.string.manualsec_error_empty))

                                              }

                                            }
                                            else{
                                    showAlertMessageRegardingSAC(getString(R.string.error), getString(R.string.manualssid_error_empty))

                                            }

                                            LibreLogger.d(TAG, "suma in other Network if   \n ")

                                        }
                                        else {
                                           //common Wifi List Fetch SSID Configuration flow

                                            if (binding.etDeviceName.text.toString().isNotEmpty()) {
                                                if (binding.tvSelectedWifi.text.toString().isNotEmpty()) {

                                                    if (binding.passwordWifi.isVisible) {
                                                    if (binding.etWifiPassword.getText()
                                                            .toString().length < 8 || binding.etWifiPassword.getText()
                                                            .toString().length > 64
                                                    ) {
                                                        if(binding.etWifiPassword.getText().isEmpty()){
                                                            showToast(getString(R.string.password_empty_error))

                                                        }
                                                        if(binding.etWifiPassword.text.length<8){
                                                            showToast(getString(R.string.password_should_8_char))


                                                        }
                                                        if(binding.etWifiPassword.text.length>64){
                                                            showToast(getString(R.string.password_less_64_char))

                                                        }

                                                        LibreLogger.d(
                                                            TAG,
                                                            "suma in common ssid not valid " + binding.etWifiPassword.getText()
                                                                .toString().length
                                                        )
                                                        LibreLogger.d(TAG,"BTN NEXT CALL case NOT VALID")

                                                    }
                                                    else {
                                  //all validation  passed
                                                        if(binding.tvSelectedWifi.text.toString()==AppUtils.getConnectedSSID(context = this)) {
                                                             btnNextClicked()
                                                            LibreLogger.d(TAG,"BTN NEXT CALL case VALID TWO\n"+binding.tvSelectedWifi.text.toString()+"getconnectessid\n"+getConnectedSSIDName(this))

                                                        }
                                                        else{
                                                            showNetworkMisMatchAlertDialog(networkMismatchSsidMessage(getConnectedSSID(context = this),binding.tvSelectedWifi.text.toString()), getString(R.string.continue_txt),
                                                                getString(R.string.cancel), isNetworkMisMatch=true,
                                                                isConfigureCancel=false)
                                                        }
                                                        LibreLogger.d(
                                                            TAG,
                                                            "suma in common ssid is valid " + binding.etWifiPassword.getText()
                                                                .toString().length
                                                        )

                                                    }
                                                }
                                                    else {

                                                        LibreLogger.d(TAG,"BTN NEXT CALL case VALID ONE"+binding.etWifiPassword.text.length)
                                                        //call btnnextclicked method for none security SSID

                                                        if(binding.tvSelectedWifi.text.toString()==AppUtils.getConnectedSSID(context = this)) {
                                                            btnNextClicked()
                                                            LibreLogger.d(TAG,"BTN NEXT CALL case VALID THREE\n"+binding.tvSelectedWifi.text.toString()+"getconnectessid\n"+getConnectedSSIDName(this))

                                                        }
                                                        else{
                                                            LibreLogger.d(TAG,"BTN NEXT CALL case VALID FOUR\n"+binding.tvSelectedWifi.text.toString()+"getconnectessid\n"+getConnectedSSIDName(this))

                                                            showNetworkMisMatchAlertDialog(networkMismatchSsidMessage(getConnectedSSID(context = this),binding.tvSelectedWifi.text.toString()), getString(R.string.continue_txt),
                                                                getString(R.string.cancel), isNetworkMisMatch=true,
                                                                isConfigureCancel=false)
                                                        }

                                                }
                                            }
                                            else {
                                                    showToast(getString(R.string.please_selecte_wifi))

                                                }
                                            }
                                            else {
                                                showToast(getString(R.string.device_name_empty))

                                            }

                                        }
            }
            catch (e: Exception) {
                e.printStackTrace()
            }

            R.id.btn_cancel -> btnCancelClicked()
             R.id.iv_right_arrow-> ivRightArrowClicked()

            R.id.iv_back ->
                //mBluetoothLeService.disconnect();
                //mBluetoothLeService.close();
                //mBluetoothLeService.removelistener(this);
                callBluetoothDeviceListActivity()
            R.id.iv_sec_arrow ->

                setupBottomSheetForWifiSecurityList()
            R.id.tv_selected_wifi -> {
                binding.tvSelectedWifi.isClickable=true
                LibreLogger.d(TAG,"suma in selected wifi  ")
                binding.tvSelectedWifi.isCursorVisible=true
                binding.tvSelectedWifi.isFocusableInTouchMode=true
                binding.tvSelectedWifi.inputType=InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
                binding.tvSelectedWifi.requestFocus()
            }
        }
    }

    fun setupBottomSheetForWifiSecurityList() {
        val view: View = layoutInflater.inflate(R.layout.show_wifi_security, null)
       tv_no_data = view.findViewById<TextView>(R.id.tv_no_data)
        rv_wifi_list = view.findViewById<RecyclerView>(R.id.rv_wifi_list)
        val iv_close_icon = view.findViewById<AppCompatImageView>(R.id.iv_close_icon)
        setWifiListBottomSheetAdapterForSecurity()
        iv_close_icon.setOnClickListener { bottomSheetDialogForSecurity!!.dismiss() }
        bottomSheetDialogForSecurity = BottomSheetDialog(this@CTBluetoothPassCredentials)
        bottomSheetDialogForSecurity!!.setContentView(view)
        bottomSheetDialogForSecurity!!.setCancelable(false)
        bottomSheetDialogForSecurity!!.show()
    }

    fun setWifiListBottomSheetAdapterForSecurity() {
        val securityList: MutableList<String> = ArrayList()
        securityList.add("OPEN/NONE")
        securityList.add("WPA/WPA2/WPA3")

//        securityList.add("NONE")
//        securityList.add("WEP")
//        securityList.add("WPA")
//        securityList.add("WPA-PSK")for future keeping it
//        securityList.add("WPA/WPA2")
//        securityList.add("WPA2-PSK")
//        securityList.add("WPA2-Personal")
//        securityList.add("WPA2-Enterprise")
//        securityList.add("WPA2/WPA3-Personal")
//        securityList.add("WPA3-PSK")
//        securityList.add("WPA3-Personal")

        val linearLayoutManager = LinearLayoutManager(this@CTBluetoothPassCredentials)
        wifiListBottomSheetAdapterForSecurityType = WifiListBottomSheetAdapterForSecurityType(
            this@CTBluetoothPassCredentials,
            securityList
        )

        rv_wifi_list!!.setAdapter(wifiListBottomSheetAdapterForSecurityType)
        wifiListBottomSheetAdapterForSecurityType!!.setWifiConfigurationForSeurity(this)
        rv_wifi_list!!.setLayoutManager(linearLayoutManager)
    }
    /**
     * -> BLE_SAC_APP2DEV_CONNECT_WIFI
     * : |0xAB|0x01|Len| Data|0xCB|
     * Data Format: |SSID_LEN|SSID|PASSPHRASE_LEN|PASSPHRASE
     * SSID_LEN               : 1Byte                 -> Length of the AP SSID ( Max: 32)
     * SSID                   : <=32 bytes            -> Name of the AP
     * PASSPHRASE_LEN         : 1Byte 	 			-> Length of the Password ( 8 to 63 Bytes)
     * PASSPHRASE         	: >=8 && <=63           -> Password
     */
    private fun btnNextClicked() {
        val mSelectedSSID = String(WifiConnection.getInstance().getMainSSID().toByteArray(), StandardCharsets.UTF_8)
        val mSelectedPass = String(binding.etWifiPassword.text.toString().toByteArray(), StandardCharsets.UTF_8)
        val mSelectedSecurity = String(WifiConnection.getInstance().getMainSSIDSec().toByteArray(), StandardCharsets.UTF_8)
        val mSelectedCountryCode = String(configurationParameters.getDeviceCountryCode(this@CTBluetoothPassCredentials).toByteArray(), StandardCharsets.UTF_8).uppercase(Locale.getDefault())
        mDeviceName = String(binding.etDeviceName.text.toString().toByteArray(), StandardCharsets.UTF_8)
        WifiConnection.getInstance().setMainSSIDPwd(mSelectedPass)
        LibreLogger.d(TAG, "btnNextClicked: Security check is enabled or Not 0(Disabled)/1(Enabled) " + mSecurityCheckEnabled + "Countrycode " + mSelectedCountryCode)
        /**
         * SHAIK Added the 2 Sec Delay after user click on Next Button
         */
        lifecycleScope.launch {
            delay(2000)
            if (!mSecurityCheckEnabled) {
                val data = ByteArray(mSelectedPass.length + mSelectedSSID.length + 5 + mSelectedSecurity.length + binding.etDeviceName.text!!.length + mSelectedCountryCode.length)
                LibreLogger.d(TAG, "btnNextClicked: mSecurityCheckEnabled if $data")
                var i = 0
                data[i++] = mSelectedSSID.length.toByte()
                for (b in mSelectedSSID.toByteArray()) {
                    data[i++] = b
                }
                data[i++] = mSelectedPass.length.toByte()
                for (b in mSelectedPass.toByteArray()) {
                    data[i++] = b
                }
                data[i++] = mSelectedSecurity.length.toByte()
                for (b in mSelectedSecurity.toByteArray()) {
                    data[i++] = b
                }
                data[i++] = binding.etDeviceName.text!!.length.toByte()
                for (b in binding.etDeviceName.text.toString().toByteArray()) {
                    data[i++] = b
                }
                data[i++] = mSelectedCountryCode.length.toByte()
                for (b in mSelectedCountryCode.toByteArray()) {
                    data[i++] = b
                }
                LibreLogger.d(TAG, "btnNextClicked: mSecurityCheckEnabled IF writing data  $data")
                val mBlePacketScanWIFI = BLEPacket(data, BLEUtils.BLE_SAC_APP2DEV_CONNECT_WIFI.toByte(), true)
                BleCommunication.writeDataToBLEDevice(mBlePacketScanWIFI)
            } else {
                /*Code we are sending ssid password*/
                val mConfigPacket = ConfigurationParameters.getInstance().getEncryptedDataAndKey(mSelectedSSID, mSelectedPass, mSelectedSecurity, mDeviceName, mSelectedCountryCode)
                //ConfigurationParameters.getInstance().createSacPackets(false, mConfigPacket.getEncodedData(),mConfigPacket.getIv());
                /*byte[] ThreeTimesOfEncodedData = new byte[mConfigPacket.getEncodedData().length * 3];
        int i =0;
        for(byte b : mConfigPacket.getEncodedData()) {
            ThreeTimesOfEncodedData[i++] =  b;
        }
        for(byte b : mConfigPacket.getEncodedData()) {
            ThreeTimesOfEncodedData[i++] =  b;
        }
        for(byte b : mConfigPacket.getEncodedData()) {
            ThreeTimesOfEncodedData[i++] =  b;
        }*/
                val encodedData = mConfigPacket.encodedData //ThreeTimesOfEncodedData ;//mConfigPacket.getEncodedData();
                val ivData = mConfigPacket.iv
                var NumberOfPacketsToSplitted = encodedData.size / 150 // MTCU_SIZE - header just to make it as whole number
                if (encodedData.size % 150 != 0) {
                    NumberOfPacketsToSplitted += 1
                }
                var offset = 0
                var lengthTocopy = 150
                if (encodedData.size < 150) {
                    lengthTocopy = encodedData.size
                }
                while (NumberOfPacketsToSplitted > 0) {
                    LibreLogger.d(TAG, " KARUNAKARAN " + " NumberofPacketstoBeSplitted " +
                            NumberOfPacketsToSplitted + " Offset " + offset + " LengthToCopy " + lengthTocopy + " EncodedData Length " + encodedData.size)
                    NumberOfPacketsToSplitted--
                    val offsetEncodedData = configurationParameters.getByteArrayFromOffset(offset, lengthTocopy, encodedData)
                    val dataToSendToDevice = configurationParameters.createSacPackets(NumberOfPacketsToSplitted, offsetEncodedData, ivData)
                    BleCommunication.writeDataToBLEDevice(dataToSendToDevice)
                    offset += 150
                    if (NumberOfPacketsToSplitted == 1) {
                        lengthTocopy = encodedData.size - offset
                    }
                }
            }
        }
        LibreLogger.d(TAG, "_btnNextClicked: Before Posting credentials " + binding.etDeviceName.text.toString())
        mDeviceName = binding.etDeviceName.text.toString()
        setMessageProgressDialog(getString(R.string.sending_cred))
        /**
         * Shaik initiateJob is for,If we didn't get the response from device we are going to
         * home screen for the re initiating the setup mode
         */
        lifecycleScope.launch {
            initiateJob()
        }
        if (binding.rememCheckBox.isChecked) {/*storeSSIDInfoToSharedPreferences(this@CTBluetoothPassCredentials, WifiConnection.getInstance().getMainSSID(), WifiConnection.getInstance().getMainSSIDPwd())*/
            val passwordRememberDataClass = PasswordRememberDataClass(0, WifiConnection.getInstance().getMainSSID(), WifiConnection.getInstance().getMainSSIDPwd())
            insertDeviceSSIDPWD(passwordRememberDataClass)
        } else if (!binding.rememCheckBox.isChecked) {
            deleteWifiPassword(binding.tvSelectedWifi.text.toString())
        }
    }
  private fun btnNextClickedOther() {
        val mSelectedSSID = String(WifiConnection.getInstance().getMainSSID().toByteArray(), StandardCharsets.UTF_8)
        val mSelectedPass = String(binding.etWifiPassword.text.toString().toByteArray(), StandardCharsets.UTF_8)
        val mSelectedSecurity = String(WifiConnection.getInstance().getMainSSIDSec().toByteArray(), StandardCharsets.UTF_8)
        val mSelectedCountryCode = String(configurationParameters.getDeviceCountryCode(this@CTBluetoothPassCredentials).toByteArray(), StandardCharsets.UTF_8).uppercase(Locale.getDefault())
        mDeviceName = String(binding.etDeviceName.text.toString().toByteArray(), StandardCharsets.UTF_8)
        WifiConnection.getInstance().setMainSSIDPwd(mSelectedPass)
        LibreLogger.d(TAG, "btnNextClicked: Security check mSelectedSSID\n " + mSelectedSSID + "Password " + mSelectedPass+"security\n"+mSelectedSecurity+"countryCode"+mSelectedCountryCode)
        /**
         * SHAIK Added the 2 Sec Delay after user click on Next Button
         */
        lifecycleScope.launch {
            delay(2000)
            if (!mSecurityCheckEnabled) {
                val data = ByteArray(mSelectedPass.length + mSelectedSSID.length + 5 + mSelectedSecurity.length + binding.etDeviceName.text!!.length + mSelectedCountryCode.length)
                LibreLogger.d(TAG, "btnNextClicked: mSecurityCheckEnabled if $data")
                var i = 0
                data[i++] = mSelectedSSID.length.toByte()
                for (b in mSelectedSSID.toByteArray()) {
                    data[i++] = b
                }
                data[i++] = mSelectedPass.length.toByte()
                for (b in mSelectedPass.toByteArray()) {
                    data[i++] = b
                }
                data[i++] = mSelectedSecurity.length.toByte()
                for (b in mSelectedSecurity.toByteArray()) {
                    data[i++] = b
                }

                data[i++] = binding.etDeviceName.text!!.length.toByte()
                for (b in binding.etDeviceName.text.toString().toByteArray()) {
                    data[i++] = b
                }
                data[i++] = mSelectedCountryCode.length.toByte()
                for (b in mSelectedCountryCode.toByteArray()) {
                    data[i++] = b
                }
                LibreLogger.d(TAG, "btnNextClicked: mSecurityCheckEnabled IF writing data  ${data.toString()}")
                val mBlePacketScanWIFI = BLEPacket(data, BLEUtils.BLE_SAC_APP2DEV_CONNECT_WIFI.toByte(), true)
                BleCommunication.writeDataToBLEDevice(mBlePacketScanWIFI)
            } else {
                /*Code we are sending ssid password*/
                val mConfigPacket = ConfigurationParameters.getInstance().getEncryptedDataAndKey(mSelectedSSID, mSelectedPass, mSelectedSecurity, mDeviceName, mSelectedCountryCode)
                //ConfigurationParameters.getInstance().createSacPackets(false, mConfigPacket.getEncodedData(),mConfigPacket.getIv());
                /*byte[] ThreeTimesOfEncodedData = new byte[mConfigPacket.getEncodedData().length * 3];
        int i =0;
        for(byte b : mConfigPacket.getEncodedData()) {
            ThreeTimesOfEncodedData[i++] =  b;
        }
        for(byte b : mConfigPacket.getEncodedData()) {
            ThreeTimesOfEncodedData[i++] =  b;
        }
        for(byte b : mConfigPacket.getEncodedData()) {
            ThreeTimesOfEncodedData[i++] =  b;
        }*/
                val encodedData = mConfigPacket.encodedData //ThreeTimesOfEncodedData ;//mConfigPacket.getEncodedData();
                val ivData = mConfigPacket.iv
                var NumberOfPacketsToSplitted = encodedData.size / 150 // MTCU_SIZE - header just to make it as whole number
                if (encodedData.size % 150 != 0) {
                    NumberOfPacketsToSplitted += 1
                }
                var offset = 0
                var lengthTocopy = 150
                if (encodedData.size < 150) {
                    lengthTocopy = encodedData.size
                }
                while (NumberOfPacketsToSplitted > 0) {
                    LibreLogger.d(TAG, " KARUNAKARAN " + " NumberofPacketstoBeSplitted " + NumberOfPacketsToSplitted + " Offset " + offset + " LengthToCopy " + lengthTocopy + " EncodedData Length " + encodedData.size)
                    NumberOfPacketsToSplitted--
                    val offsetEncodedData = configurationParameters.getByteArrayFromOffset(offset, lengthTocopy, encodedData)
                    val dataToSendToDevice = configurationParameters.createSacPackets(NumberOfPacketsToSplitted, offsetEncodedData, ivData)
                    BleCommunication.writeDataToBLEDevice(dataToSendToDevice)
                    offset += 150
                    if (NumberOfPacketsToSplitted == 1) {
                        lengthTocopy = encodedData.size - offset
                    }
                }
            }
        }
        LibreLogger.d(TAG, "btnNextClicked: Before Posting credentials " + binding.etDeviceName.text.toString())
        mDeviceName = binding.etDeviceName.text.toString()
        setMessageProgressDialog(getString(R.string.posting_cred))
        /**
         * Shaik initiateJob is for,If we didn't get the response from device we are going to
         * home screen for the re initiating the setup mode
         */
        lifecycleScope.launch {
            initiateJob()
        }
        if (binding.rememCheckBox.isChecked) {/*storeSSIDInfoToSharedPreferences(this@CTBluetoothPassCredentials, WifiConnection.getInstance().getMainSSID(), WifiConnection.getInstance().getMainSSIDPwd())*/
            val passwordRememberDataClass = PasswordRememberDataClass(0, WifiConnection.getInstance().getMainSSID(), WifiConnection.getInstance().getMainSSIDPwd())
            insertDeviceSSIDPWD(passwordRememberDataClass)
        } else if (!binding.rememCheckBox.isChecked) {
            deleteWifiPassword(binding.tvSelectedWifi.text.toString())
        }
    }

    private fun btnCancelClicked() {
        showNetworkMisMatchAlertDialog(message = getString(R.string.do_you_want_to_cancel_the_setup),
            positiveButtonString = getString(R.string.yes),
            negativeButtonString = getString(R.string.no),
            isNetworkMisMatch=false,
            isConfigureCancel=true)
    }


    private fun ivRightArrowClicked() {
        val mIntent = Intent(this, CTWifiListActivity::class.java)
        mIntent.putExtra(Constants.CONFIG_THRO_BLE, mIntentExtraScanResults)
        mIntent.putExtra(AppConstants.DEVICE_IP, AppConstants.SAC_IP_ADDRESS)
        mIntent.putExtra(AppConstants.DEVICE_BLE_ADDRESS, mDeviceAddress)
        customStartActivityForResult(AppConstants.GET_SELECTED_SSID_REQUEST_CODE, mIntent)
    }

    override fun customOnActivityResult(data: Intent?, requestCode: Int, resultCode: Int) {
        super.customOnActivityResult(data, requestCode, resultCode)
        LibreLogger.d(TAG, "Shaik customOnActivityResult requestCode $requestCode")
        if (requestCode == AppConstants.GET_SELECTED_SSID_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val mScanResultItem = data!!.getSerializableExtra(AppConstants.SELECTED_SSID) as ScanResultItem
                setSsidPwd(mScanResultItem)
               LibreLogger.d(TAG,"Adding Other N/w options RESULT_OK"+mScanResultItem.ssid)

                if(mScanResultItem.ssid.equals("Other Options")){
                    LibreLogger.d(TAG,"Adding Other N/w options RESULT_OK if "+mScanResultItem.ssid)
                    binding.tvSelectedWifi.isTextSelectable
                    binding.tvSelectedWifi.hint="Enter Network Name"
                    binding.ivRememPasswordlyt.visibility=View.GONE
                    binding.tvSelectedWifi.isEnabled=true
                    binding.tvSelectedWifi.isClickable=true
                    binding.manualSecurityLayout.visibility=View.VISIBLE
                    binding.tvSecurity.visibility=View.GONE
                    binding.manualSecText.setText("")
                    binding.passwordWifi.visibility=View.GONE

                    val wifiConnect = WifiConnection.getInstance()
                    wifiConnect.setMainSSID(mScanResultItem.ssid)
                    LibreApplication.manualSSID="Other Options"
                }
                else{

                    val wifiConnect = WifiConnection.getInstance()
                    wifiConnect.setMainSSID(mScanResultItem.ssid)
                    !binding.tvSelectedWifi.isTextSelectable
                    binding.tvSelectedWifi.isEnabled=false
                    binding.tvSelectedWifi.isClickable=false

                  // thursday suma addition
                    // binding.ivRememPasswordlyt.visibility=View.VISIBLE

                    LibreLogger.d(TAG,"Adding Other N/w options RESULT_OK else "+mScanResultItem.ssid)
                    binding.manualSsidLayout.visibility=View.GONE
                    binding.manualSecurityLayout.visibility=View.GONE
                    LibreApplication.manualSSID=""
                   LibreApplication.manualsecurity=""
                    binding.tvSecurity.visibility=View.VISIBLE
                  // thursday suma addition
                // binding.passwordWifi.visibility=View.VISIBLE

                }

            }
        }else if(requestCode == AppConstants.OPEN_PHONE_WIFI_REQUEST_CODE){
                try {
                    val selectedSSID:String =binding.tvSelectedWifi.text.toString()
                    val connectedPhoneSSID: Pair<String, String?> = AppUtils.getConnectedSSIDAndSecurityType(this)
                    if(selectedSSID!=connectedPhoneSSID.first){
                        showNetworkMisMatchAlertDialog(networkMismatchMessage(
                            getConnectedSSID(context = this), binding.tvSelectedWifi.text.toString()),
                            getString(R.string.open_settings), getString(R.string.cancel),
                            isNetworkMisMatch=false,
                            isConfigureCancel=false)
                    }else {
                        binding.tvSelectedWifi.text = connectedPhoneSSID.first
                       /* LibreLogger.d(TAG, "Shaik customOnActivityResult setSsidPwd " +
                       "${connectedPhoneSSID.first} and  Security" + "${connectedPhoneSSID.second} selectedSSID $selectedSSID")*/
                        val scanResultItem = ScanResultItem(connectedPhoneSSID.second!!, connectedPhoneSSID.first)
                        setSsidPwd(scanResultItem)
                        btnNextClicked()
                    }
                  } catch (ex: Exception) {
                    LibreLogger.d(TAG, "customOnActivityResult setSsidPwd Exception ${ex.message}")
                }
        }
    }

    private fun setSsidPwd(mScanResultItem: ScanResultItem?) {
        LibreLogger.d(TAG_, "Setting setSsidPwd ${mScanResultItem.toString()}")
        if(mScanResultItem!!.ssid.equals("Other Options")){
            binding.tvSelectedWifi.text = ""
            binding.tvSelectedWifi.hint="Enter Network Name"
            LibreLogger.d(TAG_, "Setting setSsidPwd if ${mScanResultItem.toString()}")

        }
        else{
            binding.tvSelectedWifi.text = mScanResultItem!!.ssid
            LibreLogger.d(TAG_, "Setting setSsidPwd else ${mScanResultItem.toString()}")

        }
        binding.tvSecurity.text = "Security Type : " + mScanResultItem.security
        val wifiConnect = WifiConnection.getInstance()
        wifiConnect.setMainSSID(mScanResultItem.ssid)
        wifiConnect.setMainSSIDSec(mScanResultItem.security)

        if (mScanResultItem.security == "NONE"||mScanResultItem.security == "OPEN") {
            binding.passwordWifi.visibility = View.GONE
            binding.etWifiPassword.text.clear()
            LibreLogger.d(TAG_, "Setting ScanResult Item if \n"+mScanResultItem.security)
            //thursday suma addition
             binding.ivRememPasswordlyt.visibility = View.GONE
        }
        else {
            binding.passwordWifi.visibility = View.VISIBLE
            //thursday suma addition
            binding.ivRememPasswordlyt.visibility = View.VISIBLE

            LibreLogger.d(TAG_, "Setting ScanResult Item else \n"+mScanResultItem.security)

        }

        passphrase = getPWDWithDeviceSSID(mScanResultItem.ssid)
        if (passphrase != null) {
            binding.etWifiPassword.setText(passphrase)
            LibreLogger.d(TAG_, "Setting passphrase $passphrase  and length ${passphrase!!.length}")
            binding.rememCheckBox.isChecked = passphrase!!.isNotEmpty()
        } else {
            lifecycleScope.launch {
                passphrase = getPWDWithDeviceSSID(mScanResultItem.ssid)
            }
        }
    }

    private fun callBluetoothDeviceListActivity() {
        val intent = Intent(this, CTBluetoothDeviceListActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToConnectToMainNetwork() {
        LibreLogger.d(TAG, "goToConnectToMainNetwork")
        WifiConnection.getInstance().setmSACDevicePostDone(true)
        LibreApplication.sacDeviceNameSetFromTheApp = binding.etDeviceName.text.toString()
        startActivity(Intent(this, CTConnectingToMainNetwork::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        finish()
    }

    private fun goToConnectToMainNetwork(mNode: LSSDPNodes) {
        LibreLogger.d(TAG, "goToConnectToMainNetwork with node")
        WifiConnection.getInstance().setmSACDevicePostDone(true)
        LibreApplication.sacDeviceNameSetFromTheApp = binding.etDeviceName.text.toString()
        startActivity(Intent(this, CTConnectingToMainNetwork::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra(AppConstants.DEVICE_IP, mNode.ip))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun deviceDiscoveryAfterClearingTheCacheStarted() {

    }

    override fun newDeviceFound(node: LSSDPNodes) {
        if (LibreApplication.sacDeviceNameSetFromTheApp == node.friendlyname) {
            goToConnectToMainNetwork(node)
        }
    }

    override fun deviceGotRemoved(ipaddress: String) {

    }

    override fun messageRecieved(packet: NettyData) {

    }

    companion object {
        const val TAG = "==CTBluetoothPass"
        const val TAGSCAN = "TAGSCAN"
        const val TAGBLE_SHAIk = "TAGBLE_SHAIk"
        fun fromHtml(html: String?): Spanned {
            return if (html == null) {
                // return an empty spannable if the html is null
                SpannableString("")
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // FROM_HTML_MODE_LEGACY is the behaviour that was used for versions below android N
                // we are using this flag to give a consistent behaviour
                Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
            } else {
                Html.fromHtml(html)
            }
        }
    }

    private fun getPWDWithDeviceSSID(deviceSsid: String): String? {
        return if (::savePwdList.isInitialized) {
            for (i in savePwdList.indices) {
                if (savePwdList[i].deviceSSID == deviceSsid) {
                    passphrase = savePwdList[i].password
                    break
                } else {
                    passphrase = ""
                }
            }
            passphrase
        }else {
            passphrase
        }
    }

    private fun getAllSSIDWithPWD() {
        try {
            savePwdList = libreVoicePasswordRememberDao.getAllSSISandPWDS()
        } catch (e: Exception) {
            e.printStackTrace()
            LibreLogger.d(TAG, "getAllSSIDWithPWD exception ${e.message}")
        }
    }

    private fun insertDeviceSSIDPWD(passwordRememberDataClass: PasswordRememberDataClass) {
        lifecycleScope.launch(Dispatchers.IO) {
            libreVoicePasswordRememberDao.addDeviceSSIDPWD(passwordRememberDataClass)
        }
    }

    private fun deleteWifiPassword(ssid: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            libreVoicePasswordRememberDao.deletePassword(ssid)
        }
    }

    private fun initiateJob() {
        taskJob = lifecycleScope.launch(Dispatchers.IO) {
            delay(10 * 9000)
            runOnUiThread {
                dismissDialog()
                showSomethingWentWrongAlert(this@CTBluetoothPassCredentials)
            }
        }
    }

    private fun cancelJob() {
        taskJob?.cancel()
    }
    private fun showNetworkMisMatchAlertDialog(message: String,
        positiveButtonString: String,
        negativeButtonString: String,
        isNetworkMisMatch: Boolean,
        isConfigureCancel: Boolean) {
        LibreLogger.d(TAG, "showAlertDialog and requestCode: positiveButtonString $positiveButtonString")
        if (mandateDialog != null && mandateDialog!!.isShowing) mandateDialog!!.dismiss()
        else mandateDialog = null

        if (mandateDialog == null) {
            val builder = AlertDialog.Builder(this)
            if (!isConfigureCancel) {
                builder.setTitle(getString(R.string.network_mismatch))
            }
            builder.setMessage(message)
            builder.setCancelable(false)
            builder.setPositiveButton(positiveButtonString) { dialogInterface, i ->
                mandateDialog!!.dismiss()
                if(isNetworkMisMatch){
                    binding.laySsidPwdDetails.visibility = View.GONE
                    binding.laySpeakerSetupWithImage.visibility = View.VISIBLE
                    showNetworkMisMatchAlertDialog(networkMismatchMessage(getConnectedSSID(context = this), binding.tvSelectedWifi.text.toString()), getString(R.string.open_settings), getString(R.string.cancel),
                        isNetworkMisMatch=false,
                        isConfigureCancel=false)
                } else if(isConfigureCancel) {
                    if (mBluetoothLeService != null) {
                        mBluetoothLeService!!.removelistener(this)
                    }
                    callBluetoothDeviceListActivity()
                } else {
                    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                    customStartActivityForResult(AppConstants.OPEN_PHONE_WIFI_REQUEST_CODE, intent)
                }
            }
            if (isNetworkMisMatch) {
                builder.setNegativeButton(negativeButtonString) { dialogInterface, i ->
//                  SUMA: we will enbale dis logic when QA raise the concerns
//                  binding.tvSelectedWifi.text=""
//                    binding.etWifiPassword.text.clear()

                    mandateDialog!!.dismiss()
                }
            }else if(isConfigureCancel){
                builder.setNegativeButton(negativeButtonString) { dialogInterface, i ->
                    mandateDialog!!.dismiss()
                }
            }
            mandateDialog = builder.create()
        }
        if (!mandateDialog!!.isShowing) mandateDialog!!.show()

    }
  override fun onSecurityTypeSelected(security: Int) {
        val securityList: MutableList<String> = java.util.ArrayList()
        securityList.add("OPEN/NONE")
        securityList.add("WPA/WPA2/WPA3")
//        securityList.add("WPA")
//        securityList.add("WPA-PSK")
//        securityList.add("WPA/WPA2")
//        securityList.add("WPA2-PSK")in future keeping it
//        securityList.add("WPA2-Personal")
//        securityList.add("WPA2-Enterprise")
//        securityList.add("WPA2/WPA3-Personal")
//        securityList.add("WPA3-PSK")
//        securityList.add("WPA3-Personal")


        if (bottomSheetDialogForSecurity != null) {
            bottomSheetDialogForSecurity!!.dismiss()
        }

        LibreLogger.d(TAG, "suma in get the position list item \n$"+securityList.get(security))
      val wifiConnect = WifiConnection.getInstance()
      wifiConnect.setMainSSIDSec(securityList.get(security))

        LibreApplication.manualsecurity=securityList.get(security)

        binding.manualSecText.setText(securityList.get(security))
        if(securityList.get(security).equals("OPEN/NONE")){
            binding.passwordWifi.visibility=View.GONE
          //  wifiConnect.setMainSSIDSec(mScanResultItem.security)

        }
        else{
            binding.passwordWifi.visibility=View.VISIBLE

        }

    }
    class WifiReceiver(private val mandateDialog: AlertDialog?, private val selectedSSid: String) : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION == action) {
                // Wi-Fi state has changed
                LibreLogger.d(TAG, "Shaik Phone Connected ssid "+ getConnectedSSID(context))
                LibreLogger.d(TAG, "Shaik user selectedSSid  $selectedSSid")
                if(getConnectedSSID(context)==selectedSSid){
                    mandateDialog?.dismiss()
                }
                handleWifiStateChanged(context,mandateDialog)
            }
        }

         private fun handleWifiStateChanged(context: Context, mandateDialog: AlertDialog?) {

        }
    }
}
