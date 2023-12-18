package com.cumulations.libreV2.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cumulations.libreV2.AppUtils
import com.cumulations.libreV2.activity.CTBluetoothHearSoundQtn
import com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity
import com.cumulations.libreV2.model.WifiConnection
import com.cumulations.libreV2.toHtmlSpanned
import com.libreAlexa.LErrorHandeling.LibreError
import com.libreAlexa.LibreApplication
import com.libreAlexa.R
import com.libreAlexa.constants.AppConstants
import com.libreAlexa.constants.Constants
import com.libreAlexa.databinding.CtFragmentDeviceSetupInstructionsBinding
import com.libreAlexa.serviceinterface.LSDeviceClient
import com.libreAlexa.util.LibreLogger
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response

class CTDeviceSetupInfoFragment:Fragment(),View.OnClickListener {
    private val deviceDiscoveryActivity by lazy {
        activity as CTDeviceDiscoveryActivity
    }
    private var mDeviceName: String? = null
    private var ssid: String? = null
    private var binding: CtFragmentDeviceSetupInstructionsBinding? = null
    private val TAG:String = CTDeviceSetupInfoFragment::class.java.simpleName
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = CtFragmentDeviceSetupInstructionsBinding.inflate(inflater, container, false)
        return binding!!.root
        //return inflater.inflate(R.layout.ct_fragment_device_setup_instructions,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setListeners()
    }

    private fun setListeners() {
        binding!!.btnWifiSettings.setOnClickListener(this)
        binding!!.btnNext.setOnClickListener(this)
    }

    private fun initViews() {
        binding!!.btnNext.isEnabled = false
    }

    override fun onStart() {
        super.onStart()
        deviceDiscoveryActivity.checkLocationPermission()
    }

    override fun onResume() {
        super.onResume()

//        This is hot fix. Need to check scenario when speaker in SAC/SA mode connected to more then 1 app
//        phoneIp = 192.168.43.149 when app wifi connected to SA
//        phoneIp = 192.168.255.254 when app wifi connected to SAC*/
//        For sac gateway 192.168.255.249
//        For sa gateway 192.168.43.2
//        btn_next.isEnabled = deviceDiscoveryActivity.phoneIpAddress().contains(AppConstants.SAC_MODE_IP)

        Handler(Looper.getMainLooper()).postDelayed({
            ssid = deviceDiscoveryActivity.getConnectedSSIDName(deviceDiscoveryActivity)
            LibreLogger.d(TAG, "onResume:ssid: $ssid")
            binding!!.tvConnectedSsid.text = "Connected ssid : $ssid"
            binding!!.btnNext.isEnabled =
                ssid?.contains(Constants.SA_SSID_CAST_LITE)!!||ssid?.contains(Constants
            .SA_SSID_RIVAA_CONCERT)!! || ssid?.contains(Constants.SA_SSID_RIVAA_STADIUM)!!|| ssid?.contains(".d")!!
        },300)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btn_wifi_settings -> {
                WifiConnection.getInstance().mPreviousSSID = AppUtils.getConnectedSSID(requireActivity())
                LibreApplication.activeSSID = WifiConnection.getInstance().mPreviousSSID

                deviceDiscoveryActivity.customStartActivityForResult(AppConstants
                    .WIFI_SETTINGS_REQUEST_CODE,Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                })

//                btn_next?.isEnabled = true
            }

            R.id.btn_next -> {
                LibreLogger.d(TAG, "DeviceSetup retrieveDeviceName:btn_next clicked")
                if (!(ssid?.contains(Constants.SA_SSID_RIVAA_CONCERT)!! || ssid?.contains
                        (Constants.SA_SSID_CAST_LITE)!!|| ssid?.contains(Constants
                        .SA_SSID_RIVAA_STADIUM)!!|| ssid?.contains(".d")!!)) {
                    AppUtils.showAlertForNotConnectedToSAC(deviceDiscoveryActivity)
                    return
                }

                retrieveDeviceName()
            }
        }
    }

    private fun retrieveDeviceName(){
        LibreLogger.d(TAG, "retrieveDeviceName: mDeviceName $mDeviceName")
        if (mDeviceName == null) {
            deviceDiscoveryActivity.showProgressDialog(getString(R.string.retrieving))
            if (handler?.hasMessages(AppConstants.GETTING_DEVICE_NAME)!!)
                handler?.removeMessages(AppConstants.GETTING_DEVICE_NAME)
            handler?.sendEmptyMessageDelayed(AppConstants.GETTING_DEVICE_NAME, 15000)
            getDeviceName()
        } else {
            LibreLogger.d(TAG,"retrieveDeviceName Device name = $mDeviceName")
            val connectedSSID = deviceDiscoveryActivity.getConnectedSSIDName(deviceDiscoveryActivity)
            WifiConnection.getInstance().putssidDeviceNameSAC(connectedSSID, mDeviceName)
            /*startActivity(Intent(deviceDiscoveryActivity, CTConnectToWifiActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra(Constants.FROM_ACTIVITY,CTDeviceSetupInfoFragment::class.java.simpleName)
                putExtra(AppConstants.DEVICE_IP, AppConstants.SAC_IP_ADDRESS)
                putExtra(AppConstants.DEVICE_NAME, mDeviceName)
                putExtra(AppConstants.DEVICE_SSID, connectedSSID)
            })*/
            callPlayToneRequest(connectedSSID)
        }
    }

    private fun getDeviceName() {
        LibreLogger.d(TAG,"getDeviceName start")
        val lsDeviceClient = LSDeviceClient()
        val deviceNameService = lsDeviceClient.deviceNameService

        deviceNameService.getSacDeviceName(object : Callback<String> {
            override fun success(deviceName: String, response: Response) {
                deviceDiscoveryActivity.dismissDialog()
                mDeviceName = deviceName.toHtmlSpanned().toString()
               LibreLogger.d(TAG, "Device name $mDeviceName, seeking scan result")
                if (mDeviceName != null) {
                    LibreLogger.d(TAG, mDeviceName!!)
                    val connectedSSID = deviceDiscoveryActivity.getConnectedSSIDName(deviceDiscoveryActivity)
                    WifiConnection.getInstance().putssidDeviceNameSAC(connectedSSID, mDeviceName)
                    callPlayToneRequest(connectedSSID)/*startActivity(Intent(deviceDiscoveryActivity, CTConnectToWifiActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        putExtra(Constants.FROM_ACTIVITY,CTDeviceSetupInfoFragment::class.java.simpleName)
                        putExtra(AppConstants.DEVICE_IP, AppConstants.SAC_IP_ADDRESS)
                        putExtra(AppConstants.DEVICE_NAME, mDeviceName)
                        putExtra(AppConstants.DEVICE_SSID, connectedSSID)
                    }) */
                }
            }

            override fun failure(error: RetrofitError) {
                deviceDiscoveryActivity.dismissDialog()
                error.printStackTrace()
                LibreLogger.d(TAG,"getDeviceName error ${error.message}")
                retrieveDeviceName()
            }
        })
    }

    private fun callPlayToneRequest(connectedSSID :String) {
        val lsDeviceClient = LSDeviceClient()
        val deviceNameService = lsDeviceClient.deviceNameService

        deviceNameService.getPlayTestSound(object : Callback<String> {
            override fun success(deviceName: String, response: Response) {
                deviceDiscoveryActivity.dismissDialog()
                mDeviceName = deviceName.toHtmlSpanned().toString()
                LibreLogger.d(TAG, "Device name $mDeviceName, seeking scan result")
                if (mDeviceName != null) {
                    LibreLogger.d(TAG, mDeviceName!!)
                    val connectedSSID = deviceDiscoveryActivity.getConnectedSSIDName(deviceDiscoveryActivity)
                    WifiConnection.getInstance().putssidDeviceNameSAC(connectedSSID, mDeviceName)
                    startActivity(Intent(deviceDiscoveryActivity, CTBluetoothHearSoundQtn::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        putExtra(Constants.FROM_ACTIVITY,CTDeviceSetupInfoFragment::class.java.simpleName)
                        putExtra(AppConstants.DEVICE_IP, AppConstants.SAC_IP_ADDRESS)
                        putExtra(AppConstants.DEVICE_NAME, mDeviceName)
                        putExtra(AppConstants.DEVICE_SSID, connectedSSID)
                    })
                }
            }

            override fun failure(error: RetrofitError) {
                deviceDiscoveryActivity.dismissDialog()
                error.printStackTrace()
                LibreLogger.d(TAG,"getPlayToneRequest error ${error.message}")
                //retrieveDeviceName()
            }
        })
      }

    internal var handler: Handler? = object : Handler(Looper.getMainLooper()) {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what){
                AppConstants.GETTING_DEVICE_NAME ->{
                    LibreLogger.d(TAG,"handler ${msg.what} timeout")
                    deviceDiscoveryActivity.dismissDialog()
                    /*showing error*/
                    val error = LibreError("", getString(R.string.requestTimeout))
                    deviceDiscoveryActivity.showErrorMessage(error)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        deviceDiscoveryActivity.dismissDialog()
        handler?.removeMessages(AppConstants.GETTING_DEVICE_NAME)
    }
}