package com.cumulations.libreV2.activity.oem

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity
import com.cumulations.libreV2.model.SceneObject
import com.cumulations.libreV2.roomdatabase.LibreVoiceDatabase
import com.libreAlexa.R
import com.libreAlexa.Scanning.ScanningHandler
import com.libreAlexa.constants.Constants
import com.libreAlexa.constants.LSSDPCONST
import com.libreAlexa.constants.LUCIMESSAGES.ACTION
import com.libreAlexa.constants.LUCIMESSAGES.DEVICE_UUID
import com.libreAlexa.constants.LUCIMESSAGES.FAILURE
import com.libreAlexa.constants.LUCIMESSAGES.ID
import com.libreAlexa.constants.LUCIMESSAGES.REQUEST_TYPE
import com.libreAlexa.constants.LUCIMESSAGES.SUCCESS
import com.libreAlexa.constants.LUCIMESSAGES.USER_IP
import com.libreAlexa.constants.MIDCONST
import com.libreAlexa.databinding.ActivityHelpImproveChromeCastBinding
import com.libreAlexa.luci.LSSDPNodes
import com.libreAlexa.luci.LUCIControl
import com.libreAlexa.luci.LUCIPacket
import com.libreAlexa.netty.LibreDeviceInteractionListner
import com.libreAlexa.netty.NettyData
import com.libreAlexa.util.LibreLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject


/**
 * Created by SHAIK
 * 20/04/2023
 */
class HelpImproveChromeCastActivity : CTDeviceDiscoveryActivity(), LibreDeviceInteractionListner {
    private lateinit var binding: ActivityHelpImproveChromeCastBinding
    var isWebViewOpen: Boolean = false
    private val currentIpAddress: String? by lazy {
        intent?.getStringExtra(Constants.CURRENT_DEVICE_IP)
    }
    private val currentDeviceUUID: String? by lazy {
        intent?.getStringExtra(Constants.CURRENT_DEVICE_UUID)
    }
    private val speakerName by lazy {
        intent.getStringExtra(Constants.DEVICE_NAME)
    }
    private val from by lazy {
        intent.getStringExtra(Constants.FROM_ACTIVITY)
    }
    private var currentSceneObject: SceneObject? = null
    private val libreVoiceDatabaseDao by lazy { LibreVoiceDatabase.getDatabase(this).castLiteDao() }
    private var deviceUUID: String? = null
    val TAG = HelpImproveChromeCastActivity::class.java.toString()
    val TAG_MESSAGE = "HelpImprove"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpImproveChromeCastBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= 33) {
            //Android 13 and Above
            onBackInvokedDispatcher.registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT) {
                exitOnBackPressed()
            }
        } else {
            //Android 12 and below
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    exitOnBackPressed()
                }
            })
        }
        if (currentIpAddress != null) {
            fetchUUIDFromDB(currentIpAddress!!)
        } else {
            showToast(getString(R.string.somethingWentWrong))
            intentToHome(this)
        }
        LibreLogger.d(TAG, "onCreate:speakerIpAddress $currentIpAddress\n and DeviceName: " +
                "$speakerName\n from $from\n and uuid $deviceUUID currentDeviceUUID $currentDeviceUUID")
        binding.txtLearnMore.setOnClickListener {
            binding.webView.visibility = View.VISIBLE
            binding.parentLayout.visibility = View.GONE
            binding.webView.webViewClient = WebViewClient()
            binding.webView.settings.javaScriptEnabled = true
            binding.webView.loadUrl("https://support.google.com/chromecast/answer/9001232")
            binding.toolbar.visibility = View.VISIBLE
            isWebViewOpen = true
        }

        binding.btnYesImIn.setOnClickListener {
            val postData = JSONObject()
            postData.put(REQUEST_TYPE, "set")
            postData.put(ID, "crash_report")
            postData.put(ACTION, "accepted")
            postData.put(USER_IP, currentIpAddress)
            postData.put(DEVICE_UUID, currentDeviceUUID)
            sendLuciCommand(postData.toString())

        }
        binding.btnNoThanks.setOnClickListener {
            val postData = JSONObject()
            postData.put(REQUEST_TYPE, "set")
            postData.put(ID, "crash_report")
            postData.put(ACTION, "declined")
            postData.put(USER_IP, currentIpAddress)
            postData.put(DEVICE_UUID, currentDeviceUUID)
            sendLuciCommand(postData.toString())
        }
        binding.imgClose.setOnClickListener {
            binding.webView.visibility = View.GONE
            binding.parentLayout.visibility = View.VISIBLE
            binding.toolbar.visibility = View.GONE
        }

    }

    private fun sendLuciCommand(data: String) {
        val control = LUCIControl(currentSceneObject!!.ipAddress)
        control.SendCommand(MIDCONST.TOS_ACCEPT_REQUEST, data, LSSDPCONST.LUCI_SET)
    }


    private fun goToOpenHomeAppActivity() {
        val goToOpenGHomeAppActivity = Intent(this, OpenGHomeAppActivity::class.java)
        goToOpenGHomeAppActivity.putExtra(Constants.CURRENT_DEVICE_IP, currentIpAddress)
        goToOpenGHomeAppActivity.putExtra(Constants.CURRENT_DEVICE_UUID, currentDeviceUUID)
        goToOpenGHomeAppActivity.putExtra(Constants.FROM_ACTIVITY, from)
        goToOpenGHomeAppActivity.putExtra(Constants.DEVICE_NAME, speakerName)
        startActivity(goToOpenGHomeAppActivity)
    }

    override fun onStart() {
        super.onStart()
        if (currentIpAddress != null) {
            currentSceneObject = ScanningHandler.getInstance().getSceneObjectFromCentralRepo(currentIpAddress)
        }
    }

    override fun deviceDiscoveryAfterClearingTheCacheStarted() {
    }

    override fun newDeviceFound(node: LSSDPNodes?) {
    }

    override fun deviceGotRemoved(ipaddress: String?) {
        LibreLogger.d(TAG_DEVICE_REMOVED, "deviceGotRemoved called Help $ipaddress and speakerIpAddress $currentIpAddress")
    }

    override fun messageRecieved(nettyData: NettyData?) {
        val remoteDeviceIp = nettyData!!.getRemotedeviceIp()
        val packet = LUCIPacket(nettyData.getMessage())
        LibreLogger.d(TAG_MESSAGE, "messageRecieved: " + remoteDeviceIp + ", command is " + packet.command + "msg is\n" + String(packet.payload))
        if (packet.command == MIDCONST.CAST_ACCEPT_STATUS || packet.command == MIDCONST.CAST_ACCEPT_STATUS_572) {
            val message = String(packet.getpayload())
            val root = JSONObject(message)
            val action = root.getString("action")
            val uuid = root.getString("device_uuid")
            val id = root.getString("id")
            val status = root.getString("status")
            val statusMessage = root.getString("status_msg")
            if (status == SUCCESS) {
                goToOpenHomeAppActivity()
            } else if (status == FAILURE ) {
                showToast(statusMessage)
                goToOpenHomeAppActivity()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        registerForDeviceEvents(this)
    }

    private fun exitOnBackPressed() {
        if (binding.webView.isFocused && binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            if (isWebViewOpen) {
                recreate()
            }
        }
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
}
