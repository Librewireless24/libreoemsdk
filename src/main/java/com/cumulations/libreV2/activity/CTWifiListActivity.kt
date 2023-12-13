package com.cumulations.libreV2.activity

import android.app.Activity
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
import android.os.Message
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cumulations.libreV2.AppUtils
import com.cumulations.libreV2.activity.CTBluetoothPassCredentials.Companion.fromHtml
import com.cumulations.libreV2.adapter.CTWifiListAdapter
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEPacket
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEServiceToApplicationInterface
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEUtils
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BleCommunication
import com.cumulations.libreV2.model.ScanResultItem
import com.cumulations.libreV2.model.ScanResultResponse
import com.cumulations.libreV2.model.WifiConnection
import com.cumulations.libreV2.toHtmlSpanned
import com.google.gson.Gson
import com.libreAlexa.LErrorHandeling.LibreError
import com.libreAlexa.LibreApplication
import com.libreAlexa.LibreApplication.isBackPressed
import com.libreAlexa.R
import com.libreAlexa.constants.AppConstants
import com.libreAlexa.constants.Constants
import com.libreAlexa.databinding.CtActivityWifiListBinding
import com.libreAlexa.serviceinterface.LSDeviceClient
import com.libreAlexa.util.LibreLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response


class CTWifiListActivity : CTDeviceDiscoveryActivity(), BLEServiceToApplicationInterface {
    private var wifiListAdapter: CTWifiListAdapter? = null
    private var filteredScanResults: ArrayList<ScanResultItem>? = ArrayList()
    private var mConfiguringThroughBLE = false
    private lateinit var binding: CtActivityWifiListBinding
    private val TAG = CTWifiListActivity::class.java.simpleName
    private var value: BluetoothGattCharacteristic? = null
    private var mIntentExtraScanResults = false
    var mBluetoothAdapter: BluetoothAdapter? = null
    var mDeviceAddress: String? = null
    private var isItDying = false
    private var mBluetoothLeService: BluetoothLeService? = null
    private var constructJSonString = StringBuilder()
    private var scanListMap: MutableMap<String, Pair<String, Int>> = LinkedHashMap()
    private var scanListLength: Int? = null
    private var taskJob: Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CtActivityWifiListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        setListeners()
        intentExtra
        val gattCharacteristic_Libre = LibreApplication().btGattCharacteristic
        wifiListAdapter?.notifyDataSetChanged()
        initBluetoothAdapterAndListener()
        runOnUiThread {
            val timeout = 500
            handler!!.postDelayed({
                if (!LibreApplication.scanAlreadySent) {
                    val data = ByteArray(0)
                    val mBlePacketScanWIFI = BLEPacket(data, BLEUtils.BLE_SAC_APP2DEV_SCAN_WIFI.toByte(), true)
                    BleCommunication.writeDataToBLEDevice(gattCharacteristic_Libre, mBlePacketScanWIFI)

                }
            }, (timeout + 1500).toLong())
            handler!!.postDelayed(runnable, 9000)
        }
        getScanResultsFromDevice()
    }

    private val intentExtra: Unit
        get() {
            mIntentExtraScanResults = intent.getBooleanExtra(Constants.CONFIG_THRO_BLE, true)
            mDeviceAddress = intent.getStringExtra(AppConstants.DEVICE_BLE_ADDRESS)
        }

    private fun initBluetoothAdapterAndListener() {
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter
        val gattServiceIntent = Intent(this@CTWifiListActivity, BluetoothLeService::class.java)
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE)
    }


    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            isItDying = false
            mBluetoothLeService = (service as BluetoothLeService.LocalBinder).service
            if (!mBluetoothLeService!!.initialize(mBluetoothAdapter, this@CTWifiListActivity)) {
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

    private fun setListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.ivRefresh.setOnClickListener {
            val gattCharacteristic_Libre = LibreApplication().btGattCharacteristic
            WifiConnection.getInstance().clearWifiScanResult()
            filteredScanResults?.clear()
            wifiListAdapter?.scanResultList?.clear()
            wifiListAdapter?.notifyDataSetChanged()
            binding.tvNoData.visibility = View.GONE
            initBluetoothAdapterAndListener()
            runOnUiThread {
                if ((this@CTWifiListActivity).isFinishing) {
                    return@runOnUiThread
                }
                lifecycleScope.launch {
                    initiateJob()
                }
                showProgressDialog(getString(R.string.get_scan_results))
                val timeout = 500
                handler!!.postDelayed({
                    val data = ByteArray(0)
                    val mBlePacketScanWIFI = BLEPacket(data, BLEUtils.BLE_SAC_APP2DEV_SCAN_WIFI.toByte(), true)
                    BleCommunication.writeDataToBLEDevice(gattCharacteristic_Libre, mBlePacketScanWIFI)
                }, (timeout + 1500).toLong())
                handler!!.postDelayed(runnable, 12000)
            }
        }

    }

    private fun initViews() {
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        wifiListAdapter = CTWifiListAdapter(this, ArrayList())
        binding.rvWifiList.layoutManager = LinearLayoutManager(this)
        binding.rvWifiList.adapter = wifiListAdapter
        mConfiguringThroughBLE = intent.getBooleanExtra(Constants.CONFIG_THRO_BLE, false)
    }

    override fun onStart() {
        super.onStart()
    }

    private fun getScanResultsFromDevice() {
        val ssid = getConnectedSSIDName(this)
        if (!mConfiguringThroughBLE) {
            if (!(ssid.contains(Constants.SA_SSID_RIVAA_CONCERT) || ssid.contains(Constants.SA_SSID_RIVAA_STADIUM) || ssid.contains(".d"))) {
                AppUtils.showAlertForNotConnectedToSAC(this)
                return
            }
        }
        if (WifiConnection.getInstance().savedScanResults.isEmpty()) {
            if (intent?.getStringExtra(AppConstants.DEVICE_IP) != null) {
                binding.rvWifiList.visibility = View.GONE
                binding.tvNoData.visibility = View.VISIBLE
            }
        } else {
            runOnUiThread {
                binding.rvWifiList.visibility = View.VISIBLE
                binding.tvNoData.visibility = View.GONE
                filteredScanResults = WifiConnection.getInstance().savedScanResults as ArrayList<ScanResultItem>?

                wifiListAdapter?.updateList(filteredScanResults)
            }
        }
    }


    internal var handler: Handler? = object : Handler(Looper.getMainLooper()) {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                Constants.GETTING_SCAN_RESULTS -> {
                    LibreLogger.d(TAG, "handler  ${msg.what} timeout")
                    dismissDialog()/*showing error*/
                    val error = LibreError("", getString(R.string.requestTimeout))
                    showErrorMessage(error)
                }
            }
        }
    }

    private fun sortAndSaveScanResults(list: List<ScanResultItem>?) {
        /*Sorted in ascending order for keys*/
        val unSortedHashmap = HashMap<String, String>()
        for (item in list!!) {
            item.ssid = item.ssid.toHtmlSpanned().toString()
            item.security = item.security.toHtmlSpanned().toString()
            unSortedHashmap[item.ssid] = item.security
            if (!item.ssid.contains(Constants.RIVAA_WAC_SSID)) {
                filteredScanResults?.add(item)
            }
        }

        val sortedMap = unSortedHashmap.toSortedMap()
        sortedMap.forEach { (key, value) ->
          //  WifiConnection.getInstance().putWifiScanResultSecurity(key, value, rssi)
        }
    }

    fun goBackToConnectWifiScreen(scanResultItem: ScanResultItem) {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(AppConstants.SELECTED_SSID, scanResultItem)
        })
        finish()
    }

    override fun onStop() {
        super.onStop()
        handler?.removeCallbacksAndMessages(null)
    }

    val runnable = Runnable {}

    override fun onConnectionSuccess(cat: BluetoothGattCharacteristic?) {
        if (isBackPressed) {
            runOnUiThread {
                if (this@CTWifiListActivity.isFinishing) {
                    return@runOnUiThread
                }
                if (WifiConnection.getInstance().savedScanResults.isEmpty()) {
                    showProgressDialog(getString(R.string.get_scan_results))
                }
                val timeout = 500
                handler!!.postDelayed({
                    if (!LibreApplication.scanAlreadySent) {
                        val data = ByteArray(0)
                        val mBlePacketScanWIFI = BLEPacket(data, BLEUtils.BLE_SAC_APP2DEV_SCAN_WIFI.toByte(), true)
                        BleCommunication.writeDataToBLEDevice(cat, mBlePacketScanWIFI)
                    }
                }, (timeout + 1500).toLong())
                handler!!.postDelayed(runnable, 12000)
            }
        }
    }

    override fun receivedBLEDataPacket(packet: BLEPacket.BLEDataPacket?, hexData: String) {

        when (packet!!.command) {
            BLEUtils.BLE_SAC_DEV2APP_WIFI_AP_NOT_FOUND -> runOnUiThread { setMessageProgressDialog(getString(R.string.ap_notfound)) }
            BLEUtils.BLE_SAC_DEV2APP_SCAN_LIST_START -> {
                constructJSonString = StringBuilder()
              //  scanListLength = String(packet.message).toInt()
            }
            BLEUtils.BLE_SAC_DEV2APP_SCAN_LIST_DATA -> constructJSonString.append(String(packet.message))

            BLEUtils.BLE_SAC_DEV2APP_SCAN_LIST_END -> {
                lifecycleScope.launch {
                    cancelJob()
                }
                populateScanListMap(constructJSonString.toString())
                dismissDialog()
               /* if (scanListLength == constructJSonString.length) {
                    populateScanListMap(constructJSonString.toString())
                }else{
                    //showSomethingWentWrongAlert(this@CTWifiListActivity)
                    populateScanListMap(constructJSonString.toString())
                    LibreLogger.d(CTBluetoothPassCredentials.TAG, "SCAN_LIST List size is not matching ${constructJSonString.length}")
                }*/
            }
        }
    }

    override fun writeSucess(status: Int) {
    }

    override fun onDisconnectionSuccess(status: Int) {
    }

    private fun populateScanListMap(scanList: String?) {
        scanListMap.clear()
        try {
            val mainObj = JSONObject(scanList)
            val scanListArray = mainObj.getJSONArray("Items")
            for (i in 0 until scanListArray.length()) {
                val obj = scanListArray[i] as JSONObject
                if (obj.getString("SSID") == null || obj.getString("SSID").isEmpty()) {
                    continue
                }
                val ssid = fromHtml(obj.getString("SSID")).toString()
                val security = fromHtml(obj.getString("Security")).toString()
                val rssi = obj.getInt("rssi")
                LibreLogger.d(CTBluetoothPassCredentials.TAG_SCAN, "populateScanListMap before " + "Map $ssid and $security and $rssi")
                scanListMap[ssid] = Pair(security, rssi)
            }

            // Add "Other Options" SSID with dummy security and RSSI
            val otherOptionsSSID = "Other Options"
            val otherOptionsSecurity = "WPA-PSK"
            val otherOptionsRssi = 200
            scanListMap[otherOptionsSSID] = Pair(otherOptionsSecurity, otherOptionsRssi)
            for ((ssid, securityAndRssi) in scanListMap) {
                val security = securityAndRssi.first
                val rssi = securityAndRssi.second

                LibreLogger.d(CTBluetoothPassCredentials.TAG_SCAN, "populateScanListMap  $ssid and $security and $rssi")
                WifiConnection.getInstance().putWifiScanResultSecurity(ssid, security, rssi)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            LibreLogger.d(TAG, "populateScanListMap Exception " + e.message)
        }
        getScanResultsFromDevice()
    }


    /*  private fun populateScanListMap(scanList: String?) {
          scanListMap.clear()
          try {
              val mainObj = JSONObject(scanList)
              val scanListArray = mainObj.getJSONArray("Items")
              for (i in 0 until scanListArray.length()) {
                  val obj = scanListArray[i] as JSONObject
                  if (obj.getString("SSID") == null || obj.getString("SSID").isEmpty()) {
                      continue
                  }
                  val ssid = fromHtml(obj.getString("SSID")).toString()
                  val security = fromHtml(obj.getString("Security")).toString()
                  val rssi = obj.getInt("rssi")
                  LibreLogger.d(CTBluetoothPassCredentials.TAG_SCAN, "populateScanListMap before " +
                          "Map $ssid and $security and $rssi")
                  scanListMap[ssid] = Pair(security, rssi)
                  *//*scanListMap[CTBluetoothPassCredentials.fromHtml(obj.getString("SSID")).toString()] = CTBluetoothPassCredentials.fromHtml(obj.getString("Security")).toString()*//**//*  LibreLogger.d(TAG, "populateScanListMap " + scanListMap[obj.getString("SSID")] + " ssid: " + obj.getString("SSID"))*//*
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            LibreLogger.d(TAG, "populateScanListMap Exception " + e.message)
        }
        *//*for (str in scanListMap.keys) {
            WifiConnection.getInstance().putWifiScanResultSecurity(str, scanListMap[str], rssi)
        }*//*
        for ((ssid, securityAndRssi) in scanListMap) {
            val security = securityAndRssi.first
            val rssi = securityAndRssi.second

            LibreLogger.d(CTBluetoothPassCredentials.TAG_SCAN, "populateScanListMap  $ssid and $security and $rssi")
            WifiConnection.getInstance().putWifiScanResultSecurity(ssid, security, rssi)
        }
        getScanResultsFromDevice()
    }*/

    override fun onBackPressed() {
        super.onBackPressed()
        isBackPressed = true
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissDialog()
        mBluetoothLeService = null
    }

    private fun getScanResultsForIp(deviceIp: String) {
        if ((this@CTWifiListActivity).isFinishing) {
            return;
        }
        val BASE_URL = "http://$deviceIp:80"
        val lsDeviceClient = LSDeviceClient(BASE_URL)
        val deviceNameService = lsDeviceClient.deviceNameService

        deviceNameService.getScanResultV2(object : Callback<String> {
            override fun success(stringResponse: String?, response: Response?) {
                dismissDialog()
                if (stringResponse == null) return

                /*val listType = object : TypeToken<List<ScanResultItem>>() {}.type
                val scanResultItems:List<ScanResultItem> = Gson().fromJson(stringResponse, listType)*/

                val scanResultResponse = Gson().fromJson(stringResponse, ScanResultResponse::class.java) ?: return

                if (scanResultResponse.items?.isEmpty()!!) {
                    binding.tvNoData.visibility = View.VISIBLE
                    binding.rvWifiList.visibility = View.GONE
                } else {
                    binding.rvWifiList.visibility = View.VISIBLE
                    binding.tvNoData.visibility = View.GONE

                  //  sortAndSaveScanResults(scanResultResponse.items)
                    wifiListAdapter?.updateList(filteredScanResults)
                }
            }

            override fun failure(error: RetrofitError?) {
                dismissDialog()
                showToast(error!!.message!!)
                if (error.message?.contains("failed to connect to")!!) {
                    finish()
                } /*else getScanResultsForIp(deviceIp)*/
            }

        })
    }
    private fun initiateJob() {
        taskJob = lifecycleScope.launch(Dispatchers.IO) {
            delay(60000)
            runOnUiThread {
                dismissDialog()
              //  showSomethingWentWrongAlert(this@CTWifiListActivity)
            }
        }
    }

    private fun cancelJob() {
        taskJob?.cancel()
    }
}