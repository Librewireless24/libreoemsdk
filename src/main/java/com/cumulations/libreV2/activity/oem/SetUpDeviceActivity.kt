package com.cumulations.libreV2.activity.oem

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.cumulations.libreV2.activity.CTAmazonInfoActivity
import com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity
import com.cumulations.libreV2.roomdatabase.LibreVoiceDatabase
import com.libreAlexa.R
import com.libreAlexa.alexa.DeviceProvisioningInfo
import com.libreAlexa.constants.AppConstants
import com.libreAlexa.constants.Constants
import com.libreAlexa.constants.LSSDPCONST
import com.libreAlexa.constants.LUCIMESSAGES
import com.libreAlexa.constants.MIDCONST
import com.libreAlexa.databinding.ActivitySetUpDeviceBinding
import com.libreAlexa.luci.LSSDPNodeDB
import com.libreAlexa.luci.LSSDPNodes
import com.libreAlexa.luci.LUCIControl
import com.libreAlexa.luci.LUCIPacket
import com.libreAlexa.netty.LibreDeviceInteractionListner
import com.libreAlexa.netty.NettyData
import com.libreAlexa.util.LibreLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 * Created by SHAIK
 * 26/04/2023
 *
 * TC
 * Lazy variables, speakerNode, Ipaddress
 */
class SetUpDeviceActivity : CTDeviceDiscoveryActivity(), LibreDeviceInteractionListner {
    private lateinit var binding: ActivitySetUpDeviceBinding
    private val speakerIpAddress by lazy {
        intent.getStringExtra(Constants.CURRENT_DEVICE_IP)
    }
    private val from by lazy {
        intent.getStringExtra(Constants.FROM_ACTIVITY)
    }
    private val speakerName by lazy {
        intent.getStringExtra(Constants.DEVICE_NAME)
    }
    private val libreVoiceDatabaseDao by lazy { LibreVoiceDatabase.getDatabase(this).castLiteDao() }
    private var deviceUUID: String? = null

    companion object {
        @JvmField
        var TAG: String = SetUpDeviceActivity::class.java.simpleName
    }

    private var speakerNode: LSSDPNodes? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetUpDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        speakerNode = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(speakerIpAddress)
        if (intent?.hasExtra(AppConstants.DEVICE_PROVISIONING_INFO)!! && intent?.getSerializableExtra(AppConstants.DEVICE_PROVISIONING_INFO) != null) {
            val deviceProvisioningInfo = intent.getSerializableExtra(AppConstants.DEVICE_PROVISIONING_INFO) as DeviceProvisioningInfo
            speakerNode?.mdeviceProvisioningInfo = deviceProvisioningInfo
        }
        if (speakerIpAddress != null) {
            fetchUUIDFromDB(speakerIpAddress!!)
        } else {
            showToast(getString(R.string.somethingWentWrong))
            intentToHome(this)
        }
      /*  LibreLogger.d(TAG, "onCreate:speakerIpAddress $speakerIpAddress\n and DeviceName: $speakerName\n" + " from $from\n and uuid $deviceUUID avsEnabled: ${
            speakerNode!!.getmDeviceCap().getmSource().isAlexaAvsSource
        } castEnabled: ${speakerNode!!.getmDeviceCap().getmSource().isGoogleCast}")*/
        val speakerSetUpString = getString(R.string.speaker) + speakerName + getString(R.string.successfully_setup)
        binding.txtSpeakerName.text = speakerSetUpString
        if (!from.isNullOrEmpty() && from.equals(OpenGHomeAppActivity::class.java.simpleName, ignoreCase = true) || !from.isNullOrEmpty() && from.equals(CTAmazonInfoActivity::class.java.simpleName, ignoreCase = true)) {
            binding.layLoader!!.visibility = View.VISIBLE
            lifecycleScope.launch {
                delay(2000)
                val postData = JSONObject()
                postData.put(LUCIMESSAGES.REQUEST_TYPE, "get")
                postData.put(LUCIMESSAGES.ID, "status")
                postData.put(LUCIMESSAGES.DEVICE_UUID, deviceUUID)
                sendLuciCommand(postData.toString())
            }

        }
        binding.btnSetupChromecast.setOnClickListener {
            val goToCastTOSActivity = Intent(this, CastToSActivity::class.java)
            goToCastTOSActivity.putExtra(Constants.CURRENT_DEVICE_IP, speakerIpAddress)
            goToCastTOSActivity.putExtra(Constants.DEVICE_NAME, speakerName)
            goToCastTOSActivity.putExtra(Constants.CURRENT_DEVICE_UUID, deviceUUID)
            goToCastTOSActivity.putExtra(Constants.FROM_ACTIVITY, SetUpDeviceActivity::class.java.simpleName)
            startActivity(goToCastTOSActivity)
            finish()
        }
        binding.btnSigninAmazon.setOnClickListener {
            val goToCastTOSActivity = Intent(this, CTAmazonInfoActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            goToCastTOSActivity.putExtra(Constants.CURRENT_DEVICE_IP, speakerIpAddress)
            goToCastTOSActivity.putExtra(AppConstants.DEVICE_PROVISIONING_INFO, speakerNode!!.mdeviceProvisioningInfo)
            goToCastTOSActivity.putExtra(Constants.FROM_ACTIVITY, SetUpDeviceActivity::class.java.simpleName)
            startActivity(goToCastTOSActivity)
            finish()
        }
        binding.btnSkipToHome.setOnClickListener {
            intentToHome(this)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        registerForDeviceEvents(this)
    }

    override fun onStop() {
        super.onStop()
        unRegisterForDeviceEvents()
    }

    private fun fetchUUIDFromDB(speakerIpAddress: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            deviceUUID = libreVoiceDatabaseDao.getDeviceUUID(speakerIpAddress).deviceUuid
        }
    }

    private fun sendLuciCommand(data: String) {
        val control = LUCIControl(speakerIpAddress)
        control.SendCommand(MIDCONST.TOS_ACCEPT_REQUEST, data, LSSDPCONST.LUCI_SET)
    }

    override fun deviceDiscoveryAfterClearingTheCacheStarted() {
    }

    override fun newDeviceFound(node: LSSDPNodes?) {
    }

    override fun deviceGotRemoved(ipaddress: String?) {
        LibreLogger.d(TAG_DEVICE_REMOVED, "deviceGotRemoved called SetUPDevice $ipaddress and speakerIpAddress $speakerIpAddress")

    }

    override fun messageRecieved(nettyData: NettyData?) {
        binding.layLoader!!.visibility = View.GONE
        val remoteDeviceIp = nettyData!!.getRemotedeviceIp()
        val packet = LUCIPacket(nettyData.getMessage())/*  LibreLogger.d(TAG, "messageReceived: " + remoteDeviceIp + ", command is " + packet.command + "msg" + " is\n" + String(packet.payload))*/
        if (packet.command == MIDCONST.CAST_ACCEPT_STATUS || packet.command == MIDCONST.CAST_ACCEPT_STATUS_572) {
            val message = String(packet.getpayload())
            val root = JSONObject(message)
            val tosStatus = root.getString("tos")
            if (tosStatus == "activated") {
                binding.btnSetupChromecast.text = getString(R.string.cast_enabled)
                binding.btnSetupChromecast.isEnabled = false
            } else {
                binding.btnSetupChromecast.text = getString(R.string.setup_chromecast)
                binding.btnSetupChromecast.isEnabled = true
            }
        }
    }
}