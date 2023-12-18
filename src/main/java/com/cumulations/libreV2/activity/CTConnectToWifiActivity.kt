package com.cumulations.libreV2.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.cumulations.libreV2.AppUtils
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.ConfigurationParameters
import com.cumulations.libreV2.model.ScanResultItem
import com.cumulations.libreV2.model.WifiConnection
import com.cumulations.libreV2.roomdatabase.PasswordRememberDataClass
import com.libreAlexa.LErrorHandeling.LibreError
import com.libreAlexa.LibreApplication
import com.libreAlexa.R
import com.libreAlexa.constants.AppConstants
import com.libreAlexa.constants.Constants
import com.libreAlexa.databinding.CtActivityConnectToWifiBinding
import com.libreAlexa.netty.BusProvider
import com.libreAlexa.serviceinterface.LSDeviceClient
import com.libreAlexa.util.LibreLogger
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import java.util.*

//
class CTConnectToWifiActivity: CTDeviceDiscoveryActivity(),View.OnClickListener {
    private val activityName by lazy {
        intent?.getStringExtra(Constants.FROM_ACTIVITY)
    }
    companion object {
        const val TAG = "CTConnectToWifiActivity"
    }
    private lateinit var binding: CtActivityConnectToWifiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CtActivityConnectToWifiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        LibreLogger.d(TAG, "onCreate: CTConnectToWifiActivity")
        initViews()
        setListeners()

    }

    private fun setListeners() {
        binding.ivBack.setOnClickListener(this)
        binding.btnCancel.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)
        binding.llSelectWifi.setOnClickListener(this)
    }

    private fun initViews() {
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val deviceName = intent?.getStringExtra(AppConstants.DEVICE_SSID)
        binding.etDeviceName.setText(deviceName)
        binding.etDeviceName.post {
            binding.etDeviceName.setSelection( binding.etDeviceName.length())
        }
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.iv_back -> onBackPressed()
            R.id.ll_select_wifi -> {
                LibreLogger.d(TAG, "onCreate: select wifi called")
                customStartActivityForResult(AppConstants.GET_SELECTED_SSID_REQUEST_CODE, Intent(this, CTWifiListActivity::class.java).apply {
                    putExtra(Constants.FROM_ACTIVITY, this@CTConnectToWifiActivity::class.java.simpleName)
                    putExtra(AppConstants.DEVICE_IP, intent?.getStringExtra(AppConstants.DEVICE_IP))
                })/* startActivityForResult(Intent(this,CTWifiListActivity::class.java).apply {
                    putExtra(Constants.FROM_ACTIVITY,this@CTConnectToWifiActivity::class.java.simpleName)
                    putExtra(AppConstants.DEVICE_IP,intent?.getStringExtra(AppConstants.DEVICE_IP))
                }, AppConstants.GET_SELECTED_SSID_REQUEST_CODE)*/
            }
            R.id.btn_next -> {
                if (fieldsValid()){
                    wifiConnect.setMainSSIDPwd(binding.etWifiPassword.text.toString())
                    writeSacConfig(binding.etDeviceName.text.toString())
                }
            }
            R.id.btn_cancel -> onBackPressed()
        }
    }

    private fun fieldsValid():Boolean{
        if (binding.etDeviceName.text.toString().isEmpty()) {
            showToast(getString(R.string.device_name_empty))
            return false
        }

        if (binding.etWifiPassword.text.toString().isEmpty()) {

            if(LibreApplication.isPasswordEmpty){
                LibreLogger.d(TAG,"suma in pwd empty")
            }
            else{
                showToast(getString(R.string.password_empty_error))

            }
        }

        if (binding.etWifiPassword.text.toString().length<8) {
            if(LibreApplication.isPasswordEmpty){
                LibreLogger.d(TAG,"suma in pwd empty")
            }
            else{
                showToast(getString(R.string.wifi_password_invalid))
            }
        }

        if (binding.tvSelectedWifi.text?.toString()?.isEmpty()!!) {
            showToast(getString(R.string.please_selecte_wifi))
            return false
        }

        val ssid = getConnectedSSIDName(this)
        if (!(ssid.contains(Constants.SA_SSID_RIVAA_CONCERT) || ssid.contains(Constants.SA_SSID_RIVAA_STADIUM) || ssid.contains(".d"))) {
            AppUtils.showAlertForNotConnectedToSAC(this)
            return false
        }

        return true
    }


    override fun customOnActivityResult(data: Intent?, requestCode: Int, resultCode: Int) {
        super.customOnActivityResult(data, requestCode, resultCode)
        LibreLogger.d(TAG, "onActivityResult: requestCode: $requestCode")
        when(requestCode){
            AppConstants.GET_SELECTED_SSID_REQUEST_CODE->{
                LibreLogger.d(TAG,"suma in get the stored CredValue getSSID RequestCode")
                if (resultCode == Activity.RESULT_OK){
                    LibreLogger.d(TAG,"suma in get the stored CredValue getSSID inSIDE")

                    val scanResultItem = data?.getSerializableExtra(AppConstants.SELECTED_SSID) as ScanResultItem
                    binding.tvSelectedWifi.visibility = View.VISIBLE
                    binding.tvSelectedWifi.text = scanResultItem.ssid
                    binding.ivRightArrow.visibility = View.GONE

                    getSSIDPasswordFromSharedPreference(scanResultItem.ssid)

                    binding.tvSecurity.text = "Security Type : ${scanResultItem.security}"
                    if(scanResultItem.security=="NONE"){
                        binding.passwordWifi.visibility=View.GONE
                        LibreApplication.isPasswordEmpty=true
                    }
                    else{
                        binding.passwordWifi.visibility=View.VISIBLE
                        LibreApplication.isPasswordEmpty=false


                    }
                    LibreLogger.d(TAG,"suma in onSelected Request code"+scanResultItem.security)
                    wifiConnect.setMainSSID(scanResultItem.ssid)
                    wifiConnect.setMainSSIDSec(scanResultItem.security)
                }
            }
        }
    }
    private var mDeviceNameChanged = false

    private val mHandler = object : Handler(Looper.myLooper()!!){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what){

                Constants.HTTP_POST_FAILED->{
                    val error = LibreError("Not able to connect ", wifiConnect.getMainSSID())
                    BusProvider.getInstance().post(error)
                }
            }
        }
    }

    private var wifiConnect = WifiConnection.getInstance()

    private fun writeSacConfig(etDeviceName: String) {
        showProgressDialog(getString(R.string.configuring_device))
        var baseUrl = ""
        val deviceIp = intent?.getStringExtra(AppConstants.DEVICE_IP)
        /* HTTP Post */
        baseUrl = if (activityName == CTDeviceSettingsActivity::class.java.simpleName) {
            wifiConnect.mPreviousSSID = getConnectedSSIDName(this)
            "http://$deviceIp:80"
        } else {
            "http://192.168.43.1:80"
        }

        LibreLogger.d(TAG,"writeSacConfig Base Url = $baseUrl")
        try {
            wifiConnect = WifiConnection.getInstance()

            val deviceSSID = intent?.getStringExtra(AppConstants.DEVICE_SSID)
            val ssidDeviceName = wifiConnect.getssidDeviceNameSAC(deviceSSID)
            val params = LinkedHashMap<String, String>()
            params[AppConstants.SAC_DATA] = etDeviceName
            if (activityName == CTDeviceSettingsActivity::class.java.simpleName) {
                params[AppConstants.SAC_SSID] = wifiConnect.getMainSSID() + "\n"
            } else {
                params[AppConstants.SAC_SSID] = wifiConnect.getMainSSID()
            }

            LibreLogger.d(TAG,"writeSacConfig sending wifi ssid " + wifiConnect.getMainSSID())
            if (wifiConnect.getMainSSIDPwd() != null)
                params[AppConstants.SAC_PASSPHRASE] = wifiConnect.getMainSSIDPwd().trim { it <= ' ' }
            else
                params[AppConstants.SAC_PASSPHRASE] = ""
            LibreLogger.d(TAG,"writeSacConfig sending wifi passphrase " + wifiConnect
                .getMainSSIDPwd())

            params[AppConstants.SAC_SECURITY] = wifiConnect.getMainSSIDSec()
            LibreLogger.d(TAG, "sending wifi security as " + wifiConnect.getMainSSIDSec())


            if (wifiConnect.getMainSSIDSec().equals("WEP", ignoreCase = true)) {
                val mValue = WifiConnection.getInstance().getKeyIndexForWEP()
                if (mValue != null) {
                    params[AppConstants.SAC_KEY_INDEX] = mValue
                }
            }


            if (etDeviceName.isEmpty()
                    || activityName == CTDeviceSettingsActivity::class.java.simpleName
                    || etDeviceName == ssidDeviceName) {
                mDeviceNameChanged = false
                params[AppConstants.SAC_DEVICE_NAME] = ""
            } else {
                if (binding.etDeviceName.text!!.isNotEmpty() && binding.etDeviceName.text.toString().toByteArray().size <= 50) {
                    mDeviceNameChanged = true
                    val modifiedEtDeviceName = etDeviceName.trim { it <= ' ' } /*etDeviceName.replaceAll("\n", "");*/
                    params[AppConstants.SAC_DEVICE_NAME] = modifiedEtDeviceName
                } else {
                    if (!isFinishing) {
                        if (etDeviceName.isEmpty()) {
                            AlertDialog.Builder(this)
                                    .setTitle(getString(R.string.deviceNameChanging))
                                    .setMessage(getString(R.string.failed) + "\n " + getString(R.string.deviceNamecannotBeEmpty))
                                    .setPositiveButton(android.R.string.yes) { dialog, which ->
                                        dialog.cancel()
                                    }
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show()
                        } else if (etDeviceName.toByteArray().size > 50) {
                            AlertDialog.Builder(this)
                                    .setTitle(getString(R.string.deviceNameChanging))
                                    .setMessage(getString(R.string.failed) + " \n " + getString(R.string.deviceLength))
                                    .setPositiveButton(android.R.string.yes) { dialog, which ->
                                        dialog.cancel()
                                    }
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show()
                        }
                    }
                    params[AppConstants.SAC_DEVICE_NAME] = ""
                }

            }

            val lsDeviceClient = LSDeviceClient(baseUrl)
            val deviceNameService = lsDeviceClient.deviceNameService
            val mSelectedSSID = String(params[AppConstants.SAC_SSID]!!.toByteArray(), Charsets.UTF_8)
            val mSelectedPass = String(params[AppConstants.SAC_PASSPHRASE]!!.toByteArray(), Charsets.UTF_8)
            val mSelectedSecurity = String(params[AppConstants.SAC_SECURITY]!!.toByteArray(), Charsets.UTF_8)
            val mSelectedCountryCode =
                String(ConfigurationParameters.getInstance().getDeviceCountryCode(this).toByteArray(), Charsets.UTF_8).uppercase(Locale.getDefault())
            val mDeviceName = String(params[AppConstants.SAC_DEVICE_NAME]!!.toByteArray(),Charsets.UTF_8)
            val mConfigPacket  = ConfigurationParameters.getInstance().getEncryptedDataAndKey(mSelectedSSID, mSelectedPass, mSelectedSecurity, mDeviceName, mSelectedCountryCode)
            val packets = ConfigurationParameters.getInstance().createSacPackets(0, mConfigPacket.encodedData,mConfigPacket.iv)
            val byteArray = String(packets, Charsets.UTF_8)
            LibreLogger.d(TAG, "Countrycode $mSelectedCountryCode, result")
            params[AppConstants.SAC_COUNTRY_CODE] = mSelectedCountryCode

            deviceNameService.getFeatureCheck( object : Callback<String> {
                override fun success(content: String, response: Response?) {


                    LibreLogger.d(TAG, "feature content $content, seeking scan result")
                    if (content != null) {
                        if(content.contains(AppConstants.CONFIG_1_1)) {
                            params.clear()
                            val mConfData = ConfigurationParameters.getInstance().sendByteArrayToString(packets)
                            params[AppConstants.SAC_CONF_DATA] = mConfData.uppercase(Locale.getDefault())
                            doSacConfiguration(deviceNameService, params)
                        }
                    }

                }

                override fun failure(error: RetrofitError?) {
                    LibreLogger.d(TAG, "feature content failed " )
                    doSacConfiguration(deviceNameService, params)
                }
            })

            //params.clear();

            // val mConfData = ConfigurationParameters.getInstance().sendByteArrayToString(packets);
            // params[AppConstants.SAC_CONF_DATA] = mConfData.toUpperCase()
             //doSacConfiguration(deviceNameService, params)


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun doSacConfiguration(deviceNameService :LSDeviceClient.DeviceNameService, params : LinkedHashMap<String, String>) {
        deviceNameService.handleSacConfiguration(params, object : Callback<String> {
            override fun success(t: String?, response: Response?) {
                dismissDialog()
                if (t == null)
                    return

                if (t.contains("SAC credentials received")) {

                  LibreLogger.d(TAG,"suma in get the stored CredValue\n")
                    if(binding.rememCheckBox.isChecked){
                        /**
                         * Have to comment the SharedPrefs code and then
                         */
                    AppUtils.storeSSIDInfoToSharedPreferences(this@CTConnectToWifiActivity, wifiConnect.getMainSSID(), wifiConnect.getMainSSIDPwd())
                       /* val passwordRememberDataClass = PasswordRememberDataClass(0,wifiConnect
                            .getMainSSID(), wifiConnect.getMainSSIDPwd())
                        insertDeviceSSIDPWD(passwordRememberDataClass)*/

                   }
                    if (mDeviceNameChanged) {
                        showProgressDialog(getString(R.string.deviceRebooting))
                        mDeviceNameChanged = false
                    }

                    goToSpeakerSetupScreen(t)
                } else {
                    val error = LibreError("Error, connecting to Main Network Credentials  ," +
                            "and Got Response Message as ", t)
                    BusProvider.getInstance().post(error)
                }
            }

            override fun failure(error: RetrofitError?) {
                error?.printStackTrace()
                LibreLogger.d(TAG,"handleSacFailure: "+ error?.message!!)

                dismissDialog()
                val ssid = getConnectedSSIDName(this@CTConnectToWifiActivity)
                if (!(ssid.contains(Constants.SA_SSID_RIVAA_CONCERT) || ssid.contains(Constants.SA_SSID_RIVAA_STADIUM))) {
                    /*Sometimes in OnePlus 6, after posting sac credentials, device P2P goes off before giving success response
                * to retrofit*/
                    goToSpeakerSetupScreen(error.message!!)
                } else {
                    wifiConnect.setmSACDevicePostDone(false)
                    mHandler.sendEmptyMessage(Constants.HTTP_POST_FAILED)
                }
            }

        })
    }

    private fun goToSpeakerSetupScreen(message: String) {
        wifiConnect.setmSACDevicePostDone(true)

        val error = LibreError("Credentials sent to speaker",message)
        BusProvider.getInstance().post(error)

//                        unbindWifiNetwork(this@CTConnectToWifiActivity)

        LibreApplication.sacDeviceNameSetFromTheApp = binding.etDeviceName.text.toString()
        startActivity(Intent(this@CTConnectToWifiActivity, CTConnectingToMainNetwork::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
        finish()
    }

    /*It will get password for corresponding ssid */
    private fun getSSIDPasswordFromSharedPreference(deviceSSID: String): String? {
        val pref = applicationContext
            .getSharedPreferences("Your_Shared_Prefs", MODE_PRIVATE)
        val editor = pref.edit()
        editor.commit()
        LibreLogger.d(TAG, "suma in get the stored CredValue saved password" + pref.getString(deviceSSID, ""))
        binding.etWifiPassword.setText(pref.getString(deviceSSID, ""))
        return pref.getString(deviceSSID, "")
    }
    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
    }
}