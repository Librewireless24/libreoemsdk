package com.cumulations.libreV2.activity.oem

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.webkit.WebViewClient
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity
import com.cumulations.libreV2.model.SceneObject
import com.cumulations.libreV2.roomdatabase.CastLiteUUIDDataClass
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
import com.libreAlexa.constants.MIDCONST.CAST_ACCEPT_STATUS
import com.libreAlexa.constants.MIDCONST.CAST_ACCEPT_STATUS_572
import com.libreAlexa.constants.MIDCONST.TOS_ACCEPT_REQUEST
import com.libreAlexa.databinding.ActivityCastToSactivityBinding
import com.libreAlexa.luci.LSSDPNodeDB
import com.libreAlexa.luci.LSSDPNodes
import com.libreAlexa.luci.LUCIControl
import com.libreAlexa.luci.LUCIPacket
import com.libreAlexa.netty.LibreDeviceInteractionListner
import com.libreAlexa.netty.NettyData
import com.libreAlexa.util.LibreLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID


/**
 * Created by SHAIK
 * 18/04/2023
 */
class CastToSActivity : CTDeviceDiscoveryActivity(), LibreDeviceInteractionListner {
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
    private lateinit var binding: ActivityCastToSactivityBinding
    var isWebViewOpen: Boolean = false
    private var currentSceneObject: SceneObject? = null
    private val libreVoiceDatabaseDao by lazy { LibreVoiceDatabase.getDatabase(this).castLiteDao() }
    private var deviceUUID: String? = null
    val TAG = "CastToSActivity"
    private var deviceTOSStatus: String? = null
    private var currentDeviceNode: LSSDPNodes? = null
    private var taskJob: Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCastToSactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= 33) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT) {
                exitOnBackPressed()
            }
        } else {
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    exitOnBackPressed()
                }
            })
        }
        binding.layLoader.visibility = View.VISIBLE
        binding.parentLayout.visibility = View.GONE
        if (currentIpAddress != null) {
            fetchUUIDFromDB(currentIpAddress!!)
        } else {
            showToast(getString(R.string.somethingWentWrong))
            intentToHome(this)
        }/*LibreLogger.d(TAG_SECUREROOM, "onCreate:speakerIpAddress $currentIpAddress\n and DeviceName: $speakerName\n from $from\n and uuid $deviceUUID currentDeviceUUID $currentDeviceUUID")*/
        val tos: String = getString(R.string.tos_accept)
        val tosString = SpannableString(tos)
        val clickableTermsSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                binding.webView.visibility = View.VISIBLE
                binding.parentLayout.visibility = View.GONE
                binding.webView.webViewClient = WebViewClient()
                binding.webView.settings.javaScriptEnabled = true
                binding.webView.loadUrl("https://policies.google.com/terms?color_scheme=dark")
                binding.toolbar.visibility = View.VISIBLE
                isWebViewOpen = true
            }
        }
        val clickablePrivacySpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                binding.webView.visibility = View.VISIBLE
                binding.parentLayout.visibility = View.GONE
                binding.webView.webViewClient = WebViewClient()
                binding.webView.settings.javaScriptEnabled = true
                binding.webView.loadUrl("https://policies.google.com/privacy?color_scheme=dark")
                binding.toolbar.visibility = View.VISIBLE
                isWebViewOpen = true
            }
        }
        tosString.setSpan(clickableTermsSpan, 173, 196, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tosString.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue)), 173, 196, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tosString.setSpan(clickablePrivacySpan, 201, 222, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tosString.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue)), 201, 222, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.txtCastTos.text = tosString
        binding.txtCastTos.movementMethod = LinkMovementMethod.getInstance()
        binding.btnAccept.setOnClickListener {
            binding.layLoader.visibility = View.VISIBLE
            if(currentIpAddress!=null && deviceUUID!=null) {
                val postData = JSONObject()
                postData.put(REQUEST_TYPE, "set")
                postData.put(ID, "tos")
                postData.put(ACTION, "accepted")
                postData.put(USER_IP, currentIpAddress)
                postData.put(DEVICE_UUID, deviceUUID)
                sendLuciCommand(postData.toString())
            }else{
                handleBackPress()
                LibreLogger.d(TAG_FW_UPDATE,"currentIpAddress or device uuid is null")
            }

            //Shaik If no response from device go to home screen showed to suma
            lifecycleScope.launch {
                initiateJob()
            }
        }
        binding.btnSkip.setOnClickListener {
            handleBackPress()
        }
        binding.imgClose.setOnClickListener {
            binding.webView.visibility = View.GONE
            binding.parentLayout.visibility = View.VISIBLE
            binding.toolbar.visibility = View.GONE
        }

    }

    private fun exitOnBackPressed() {
        if (binding.webView.isFocused && binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            if (isWebViewOpen) {
                recreate()
            } else {
                handleBackPress()
            }
        }
    }

    private fun handleBackPress() {
        if (!from.isNullOrEmpty() && from.equals(SetUpDeviceActivity::class.java.simpleName, ignoreCase = true)) {
            val newIntent = Intent(this@CastToSActivity, SetUpDeviceActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            newIntent.putExtra(Constants.CURRENT_DEVICE_IP, currentIpAddress)
            newIntent.putExtra(Constants.DEVICE_NAME, speakerName)
            newIntent.putExtra(Constants.FROM_ACTIVITY, SetUpDeviceActivity::class.java.simpleName)
            startActivity(newIntent)
            finish()
        } else {
            intentToHome(this)
        }
    }

    private fun sendLuciCommand(data: String) {
        val control = LUCIControl(currentSceneObject?.ipAddress)
        control.SendCommand(TOS_ACCEPT_REQUEST, data, LSSDPCONST.LUCI_SET)
    }

    override fun onStart() {
        super.onStart()
        if (currentIpAddress != null) {
            currentSceneObject = ScanningHandler.getInstance().getSceneObjectFromCentralRepo(currentIpAddress)
            currentDeviceNode = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(currentIpAddress)
        }
    }

    override fun deviceDiscoveryAfterClearingTheCacheStarted() {
    }

    override fun newDeviceFound(node: LSSDPNodes?) {

    }

    override fun deviceGotRemoved(ipaddress: String?) {
    }

    override fun messageRecieved(nettyData: NettyData?) {
        lifecycleScope.launch {
            cancelJob()
        }
        binding.layLoader.visibility = View.GONE
        val remoteDeviceIp = nettyData!!.getRemotedeviceIp()
        val packet = LUCIPacket(nettyData.getMessage())/*LibreLogger.d(TAG_NETWORK, "messageRecieved: " + remoteDeviceIp + ", command is " + packet.command + "msg is\n" + String(packet.payload))*/
        if (packet.command == CAST_ACCEPT_STATUS || packet.command == CAST_ACCEPT_STATUS_572) {
            val message = String(packet.getpayload())
            val root = JSONObject(message)
            var id: String? = null
            if (root.has("id")) {
                id = root.getString("id")
            }
            if (id != "status") {
                val uuid = root.getString("device_uuid")
                val status = root.getString("status")
                val statusMessage = root.getString("status_msg")
                if (status == SUCCESS && id == "tos") {
                    val goToHelpImproveChromeCastActivity = Intent(this, HelpImproveChromeCastActivity::class.java)
                    goToHelpImproveChromeCastActivity.putExtra(Constants.CURRENT_DEVICE_IP, currentIpAddress)
                    goToHelpImproveChromeCastActivity.putExtra(Constants.CURRENT_DEVICE_UUID, uuid)
                    goToHelpImproveChromeCastActivity.putExtra(Constants.FROM_ACTIVITY, from)
                    goToHelpImproveChromeCastActivity.putExtra(Constants.DEVICE_NAME, speakerName)
                    startActivity(goToHelpImproveChromeCastActivity)
                    finish()
                } else if (status == FAILURE) {
                    showToast(statusMessage)
                    handleBackPress()
                } else {
                    showToast(statusMessage)
                    handleBackPress()
                }
            } else if (id == "status") {
                val deviceUUID = root.getString("device_uuid")
                deviceTOSStatus = root.getString("tos")
                val tosStatus = root.getString("tos")
                val crashReport = root.getBoolean("crash_report")
                if (deviceTOSStatus == "activated") {
                    val goToActivateCastActivity = Intent(this@CastToSActivity, ActivateCastActivity::class.java)
                    goToActivateCastActivity.putExtra(Constants.CURRENT_DEVICE_IP, currentIpAddress)
                    goToActivateCastActivity.putExtra(Constants.CAST_STATUS, tosStatus)
                    goToActivateCastActivity.putExtra(Constants.CURRENT_DEVICE_UUID, deviceUUID)
                    goToActivateCastActivity.putExtra(Constants.CRASH_REPORT, crashReport)
                    goToActivateCastActivity.putExtra(Constants.FROM_ACTIVITY, CastToSActivity::class.java.simpleName)
                    goToActivateCastActivity.putExtra(Constants.DEVICE_NAME, currentDeviceNode?.friendlyname)
                    startActivity(goToActivateCastActivity)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        registerForDeviceEvents(this)
        binding.layLoader.visibility = View.VISIBLE
        binding.parentLayout.visibility = View.GONE
        if(currentIpAddress!=null) {
            lifecycleScope.launch {
                delay(5000)
                checkCastActivateStatus(currentIpAddress)
                delay(1000)
                binding.layLoader.visibility = View.GONE
                binding.parentLayout.visibility = View.VISIBLE
            }
        }else{
            intentToHome(this)
        }
    }

    private fun checkCastActivateStatus(currentIpAddress: String?) {
        val postData = JSONObject()
        postData.put(REQUEST_TYPE, "get")
        postData.put(ID, "status")
        postData.put(DEVICE_UUID, deviceUUID)
        val control = LUCIControl(currentIpAddress)
        control.SendCommand(TOS_ACCEPT_REQUEST, postData.toString(), LSSDPCONST.LUCI_SET)
    }

    override fun onStop() {
        super.onStop()
        unRegisterForDeviceEvents()
    }

    private fun fetchUUIDFromDB(speakerIpAddress: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            delay(1000)
            try {
                deviceUUID = libreVoiceDatabaseDao.getDeviceUUID(speakerIpAddress).deviceUuid
            } catch (e: NullPointerException) {
                e.printStackTrace()
                val uuid: String = UUID.randomUUID().toString()
                val addDeviceDate = CastLiteUUIDDataClass(0, speakerIpAddress, speakerName!!, uuid)
                libreVoiceDatabaseDao.addDeviceUUID(addDeviceDate)
                delay(2000)
                fetchUUIDFromDB(speakerIpAddress)
            }
        }
    }
    private fun initiateJob() {
        taskJob = lifecycleScope.launch(Dispatchers.IO) {
            delay(30000)
            runOnUiThread {
                dismissDialog()
                LibreLogger.d(TAG_FW_UPDATE,"initiateJob called")
                showSomethingWentWrongAlert(this@CastToSActivity)
            }
        }
    }
    private fun cancelJob() {
        taskJob?.cancel()
    }
}