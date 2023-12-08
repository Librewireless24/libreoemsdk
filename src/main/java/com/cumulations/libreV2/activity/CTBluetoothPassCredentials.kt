package com.cumulations.libreV2.activity

import android.annotation.TargetApi
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
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.cumulations.libreV2.activity.BluetoothLeService.LocalBinder
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEPacket
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEPacket.BLEDataPacket
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEServiceToApplicationInterface
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEUtils
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BleCommunication
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.ConfigurationParameters
import com.cumulations.libreV2.model.ScanResultItem
import com.cumulations.libreV2.model.WifiConnection
import com.cumulations.libreV2.roomdatabase.LibreVoiceDatabase
import com.cumulations.libreV2.roomdatabase.PasswordRememberDao
import com.cumulations.libreV2.roomdatabase.PasswordRememberDataClass
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
import java.util.TreeMap

class CTBluetoothPassCredentials : CTDeviceDiscoveryActivity(), BLEServiceToApplicationInterface,
    View.OnClickListener, LibreDeviceInteractionListner {
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

    override fun onPause() {
        super.onPause()
        mBluetoothLeService = null
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
                    // LibreLogger.d("SUMA_SCAN", "******** SUMA GET THE BLE PACKET INFO FriendlyNameBLEPASS")

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
                        LibreLogger.d(TAG, "Security check 27 writing SUMA GET THE BLE PACKET " + "INFO" + " " + "SECURITYCHECK_BLEPASS")

                        //  getFriendlyNameThroBLE();
                    }, (timeout + 1000).toLong())

                    LibreApplication.scanAlreadySent = true;
                    showProgressDialog(getString(R.string.get_scan_results))

                    handler.postDelayed({
                        val data = ByteArray(0)
                        val mBlePacketScanWIFI = BLEPacket(data, BLEUtils.BLE_SAC_APP2DEV_SCAN_WIFI.toByte(), true)
                        BleCommunication.writeDataToBLEDevice(value, mBlePacketScanWIFI)
                        // LibreLogger.d("SUMA_SCAN", "******** SUMA GET THE BLE PACKET INFO SCAN_WIFI_BLEPASS")

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
    override fun receivedBLEDataPacket(packet: BLEDataPacket) {
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

            BLEUtils.BLE_SAC_DEV2APP_CRED_RECEIVED -> runOnUiThread { setMessageProgressDialog(getString(R.string.cred_received)) }
            BLEUtils.BLE_SAC_DEV2APP_CRED_SUCCESS -> runOnUiThread { setMessageProgressDialog(getString(R.string.cred_success)) }
            BLEUtils.BLE_SAC_DEV2APP_CRED_FAILURE -> runOnUiThread { showAlertMessageRegardingSAC(" ", getString(R.string.credientials_invalid)) }
            BLEUtils.BLE_SAC_DEV2APP_WIFI_CONNECTING -> runOnUiThread { setMessageProgressDialog(getString(R.string.start_connecting)) }
            BLEUtils.BLE_SAC_DEV2APP_WIFI_CONNECTED -> {
                lifecycleScope.launch {
                    cancelJob()
                }
                runOnUiThread { setMessageProgressDialog(getString(R.string.speaker_connected)) }
                val data1 = ByteArray(0)
                val mBleStop = BLEPacket(data1, BLEUtils.BLE_SAC_APP2DEV_STOP_M.toByte())
                BleCommunication.writeDataToBLEDevice(mBleStop)
                goToConnectToMainNetwork()
            }

            BLEUtils.BLE_SAC_DEV2APP_WIFI_CONNECTING_FAILED -> {
                val mMessageInt = packet.getcompleteMessage()
                val message = mMessageInt[3].toInt()
                if (message == 26) {
                    runOnUiThread { showAlertMessageRegardingSAC(getString(R.string.configuration_failed_hd), getString(R.string.configuration_failed_msg)) }
                } else {
                    runOnUiThread { showAlertMessageRegardingSAC(getString(R.string.configuration_failed_hd), getString(R.string.configuration_timeout_msg)) }
                }
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
                LibreLogger.d(TAG_SCAN, "KARUNAKARAN  " + packet.dataLength)
                var security = 0
                if (packet.dataLength > 0) {
                    val mMessageInt1 = packet.getcompleteMessage()
                    security = mMessageInt1[4].toInt() //Integer.parseInt(mMessageInt1[4])
                    LibreLogger.d(TAG_SCAN, "KARUNAKARAN  security$security") // ;
                }
                mSecurityCheckEnabled = security != 0
            }
        }
    }

    private var mSecurityCheckEnabled = false
    private var scanListMap: MutableMap<String, String> = TreeMap()
    private fun populateScanListMap(scanList: String?) {
        scanListMap.clear()
        try {
            val mainObj = JSONObject(scanList!!)
            LibreLogger.d(TAG_SCAN, "populateScanListMap scanList " + scanList)
            val scanListArray = mainObj.getJSONArray("Items")
            for (i in 0 until scanListArray.length()) {
                val obj = scanListArray[i] as JSONObject
                if (obj.getString("SSID") == null || obj.getString("SSID").isEmpty()) {
                    continue
                }
                scanListMap[fromHtml(obj.getString("SSID")).toString()] = fromHtml(obj.getString("Security")).toString()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            LibreLogger.d(TAG_SCAN, "populateScanListMap exception " + e.message)
        }
        for (str in scanListMap.keys) {
            WifiConnection.getInstance().putWifiScanResultSecurity(str, scanListMap[str])
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
                showAlertDialog(getString(R.string.somethingWentWrong_tryAgain), getString(R.string.ok), 0, true)
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(view: View) {
        when (view.id) {
            R.id.et_device_name -> {}
            R.id.btn_next -> try {
                if (binding.etDeviceName.text.toString().isNotEmpty()) {
                    if (binding.tvSelectedWifi.text.toString().isNotEmpty()) {
                        if (binding.etWifiPassword.text.toString().isNotEmpty()) {
                            btnNextClicked()
                        } else {
                            showToast(getString(R.string.password_empty_error))
                        }
                    } else {
                        showToast(getString(R.string.please_selecte_wifi))
                    }
                } else {
                    showToast(getString(R.string.device_name_empty))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            R.id.btn_cancel -> btnCancelClicked()
            R.id.ll_select_wifi, R.id.iv_right_arrow, R.id.tv_selected_wifi -> ivRightArrowClicked()
            R.id.iv_back ->
                //mBluetoothLeService.disconnect();
                //mBluetoothLeService.close();
                //mBluetoothLeService.removelistener(this);
                callBluetoothDeviceListActivity()
        }
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
    fun btnNextClicked() {
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
        if (mBluetoothLeService != null) {
            mBluetoothLeService!!.removelistener(this)
        }
        callBluetoothDeviceListActivity()
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
        if (requestCode == AppConstants.GET_SELECTED_SSID_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val mScanResultItem = data!!.getSerializableExtra(AppConstants.SELECTED_SSID) as ScanResultItem?
                binding.tvSelectedWifi.visibility = View.VISIBLE
                binding.tvSelectedWifi.text = mScanResultItem!!.ssid
                //   LibreLogger.d(TAG,TAG_ROOM_DB, "suma  in get the stored CredValue getSSID\n")
                binding.ivRightArrow.visibility = View.GONE
                binding.tvSecurity.text = "Security Type : " + mScanResultItem.security
                val wifiConnect = WifiConnection.getInstance()
                wifiConnect.setMainSSID(mScanResultItem.ssid)
                wifiConnect.setMainSSIDSec(mScanResultItem.security)
                if (mScanResultItem.security == "NONE") {
                    binding.passwordWifi.visibility = View.GONE
                } else {
                    binding.passwordWifi.visibility = View.VISIBLE
                }
                passphrase = getPWDWithDeviceSSID(mScanResultItem.ssid)
                if (passphrase != null) {
                    binding.etWifiPassword.setText(passphrase)
                    LibreLogger.d(TAG, "Setting passphrase $passphrase  and length ${passphrase!!.length}")
                    binding.rememCheckBox.isChecked = passphrase!!.isNotEmpty()
                } else {
                    lifecycleScope.launch {
                        passphrase = getPWDWithDeviceSSID(mScanResultItem.ssid)
                    }
                }
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
        const val TAG_SCAN = "TAG_SCAN"
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
}