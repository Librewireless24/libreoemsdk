package com.cumulations.libreV2.activity.oem

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity
import com.cumulations.libreV2.activity.CTDeviceSettingsActivity
import com.cumulations.libreV2.roomdatabase.CastLiteUUIDDataClass
import com.cumulations.libreV2.roomdatabase.LibreVoiceDatabase
import com.libreAlexa.R
import com.libreAlexa.constants.Constants
import com.libreAlexa.constants.LSSDPCONST.LUCI_SET
import com.libreAlexa.constants.LUCIMESSAGES
import com.libreAlexa.constants.MIDCONST
import com.libreAlexa.constants.MIDCONST.TOS_ACCEPT_REQUEST
import com.libreAlexa.databinding.ActivityActivateCastBinding
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
import java.util.UUID

/**
 * Created by SHAIK
 * 18/04/2023
 */
class ActivateCastActivity : CTDeviceDiscoveryActivity(), LibreDeviceInteractionListner {
    private lateinit var binding: ActivityActivateCastBinding
    var isHomeAppOpen: Boolean = false
    private val currentIpAddress: String? by lazy {
        intent?.getStringExtra(Constants.CURRENT_DEVICE_IP)
    }
    private val speakerName by lazy {
        intent.getStringExtra(Constants.DEVICE_NAME)
    }
    private val tosStatus: String? by lazy {
        intent?.getStringExtra(Constants.CAST_STATUS)
    }
    private val currentDeviceUUID: String? by lazy {
        intent?.getStringExtra(Constants.CURRENT_DEVICE_UUID)
    }
    private val crashReport: Boolean? by lazy {
        intent?.getBooleanExtra(Constants.CRASH_REPORT, false)
    }
    private val from by lazy {
        intent.getStringExtra(Constants.FROM_ACTIVITY)
    }
    private val libreVoiceDatabaseDao by lazy { LibreVoiceDatabase.getDatabase(this).castLiteDao() }
    private var deviceUUID: String? = null
    private var deviceTOSStatus: String? = null

    companion object {
        val TAG = "ActivateCastActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActivateCastBinding.inflate(layoutInflater)
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
        binding.imgRightArrow.setOnClickListener {
            val goToActivateCastActivity = Intent(this, CastToSActivity::class.java)
            goToActivateCastActivity.putExtra(Constants.CURRENT_DEVICE_IP, currentIpAddress)
            goToActivateCastActivity.putExtra(Constants.DEVICE_NAME, speakerName)
            goToActivateCastActivity.putExtra(Constants.FROM_ACTIVITY, from)
            goToActivateCastActivity.putExtra(Constants.CURRENT_DEVICE_UUID, currentDeviceUUID)
            startActivity(goToActivateCastActivity)
        }
        val voiceControl: String = getString(R.string.set_up_voice_control)
        val voiceControlString = SpannableString(voiceControl)
        val clickableTermsSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val gHomeIntent = packageManager.getLaunchIntentForPackage("com.google.android.apps.chromecast.app")
                if (gHomeIntent != null) {
                    if (deviceTOSStatus == "activated") {
                        binding.txtSetUpVoice.isClickable = true
                        val uri = Uri.parse("https://madeby.google.com/home-app/?deeplink=DEVICE_SETUP")
                        val intentOpenGHome = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(intentOpenGHome)
                        isHomeAppOpen = true
                    } else {
                        binding.txtSetUpVoice.isClickable = false
                    }
                } else {
                    showAppNotInstalledAlertDialog()
                }
            }
        }
        voiceControlString.setSpan(clickableTermsSpan, 42, 57, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        voiceControlString.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue)), 42, 57, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.txtSetUpVoice.text = voiceControlString
        binding.txtSetUpVoice.movementMethod = LinkMovementMethod.getInstance()
        binding.btnSendCrashReports.setOnCheckedChangeListener { _buttonView, isChecked ->
            if (!_buttonView.isPressed) return@setOnCheckedChangeListener
            if (isChecked) {
                val postData = JSONObject()
                postData.put(LUCIMESSAGES.REQUEST_TYPE, "set")
                postData.put(LUCIMESSAGES.ID, "crash_report")
                postData.put(LUCIMESSAGES.ACTION, "accepted")
                postData.put(LUCIMESSAGES.USER_IP, currentIpAddress)
                postData.put(LUCIMESSAGES.DEVICE_UUID, deviceUUID)
                sendLuciCommand(postData.toString())
            } else {
                val postData = JSONObject()
                postData.put(LUCIMESSAGES.REQUEST_TYPE, "set")
                postData.put(LUCIMESSAGES.ID, "crash_report")
                postData.put(LUCIMESSAGES.ACTION, "declined")
                postData.put(LUCIMESSAGES.USER_IP, currentIpAddress)
                postData.put(LUCIMESSAGES.DEVICE_UUID, deviceUUID)
                sendLuciCommand(postData.toString())

            }
        }
    }

    private fun getCrashReportStatus() {
        binding.layLoader.visibility = View.VISIBLE
        lifecycleScope.launch {
            delay(2000)
            val postData = JSONObject()
            postData.put(LUCIMESSAGES.REQUEST_TYPE, "get")
            postData.put(LUCIMESSAGES.ID, "status")
            postData.put(LUCIMESSAGES.DEVICE_UUID, deviceUUID)
            sendLuciCommand(postData.toString())
        }
    }

    private fun sendLuciCommand(data: String) {
        val control = LUCIControl(currentIpAddress)
        control.SendCommand(TOS_ACCEPT_REQUEST, data, LUCI_SET)
    }


    private fun showAppNotInstalledAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.app_not_installed))
        builder.setMessage(getString(R.string.google_home_install))

        builder.setPositiveButton(getString(R.string.yes_str)) { dialog, which ->
            val uri = Uri.parse(getString(R.string.ghome_link))
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
            isHomeAppOpen = true
        }
        builder.show()
    }

    override fun deviceDiscoveryAfterClearingTheCacheStarted() {

    }

    override fun newDeviceFound(node: LSSDPNodes?) {

    }

    override fun deviceGotRemoved(ipaddress: String?) {
    }

    override fun messageRecieved(nettyData: NettyData?) {
        binding.layLoader.visibility = View.GONE
        val packet = LUCIPacket(nettyData!!.getMessage())
        val remoteDeviceIp = nettyData.getRemotedeviceIp()/*  LibreLogger.d(TAG, "messageReceived: " + remoteDeviceIp + ", command is " + packet.command + "msg" + " is\n" + String(packet.payload))*/
        if (packet.command == MIDCONST.CAST_ACCEPT_STATUS || packet.command == MIDCONST.CAST_ACCEPT_STATUS_572) {
            val message = String(packet.getpayload())
            val root = JSONObject(message)
            if (root.getString("id") == "status") {
                if (root.getString("status") == LUCIMESSAGES.SUCCESS) {
                    deviceTOSStatus = root.getString("tos")
                    val crashReport = root.getBoolean("crash_report")
                    binding.btnSendCrashReports.isChecked = crashReport
                    if (deviceTOSStatus != "activated") {
                        binding.txtSendDevice.alpha = .5F
                        binding.txtGoogleAccountControls.alpha = .5F
                        binding.txtSetUpVoice.alpha = .5F
                        binding.txtActivateCast.text = getString(R.string.activate_cast)
                        binding.imgRightArrow.isClickable = true
                        binding.imgRightArrow.visibility = View.VISIBLE
                        binding.btnSendCrashReports.isClickable = false
                    } else {
                        binding.txtActivateCast.text = getString(R.string.cast_enabled)
                        binding.imgRightArrow.isClickable = false
                        binding.imgRightArrow.visibility = View.GONE
                        binding.btnSendCrashReports.isClickable = true
                    }
                } else {
                    gotoHome()
                }
            }
        }
    }

    private fun gotoHome() {
        showToast(getString(R.string.somethingWentWrong))
        intentToHome(this)
    }

    override fun onResume() {
        super.onResume()
        registerForDeviceEvents(this)
        getCrashReportStatus()
    }

    override fun onStop() {
        super.onStop()
        unRegisterForDeviceEvents()
    }

    private fun fetchUUIDFromDB(speakerIpAddress: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                deviceUUID = libreVoiceDatabaseDao.getDeviceUUID(speakerIpAddress).deviceUuid
            } catch (ex: Exception) {
                ex.printStackTrace()
                LibreLogger.d(TAG, "Retrieving is not working properly ")
                val uuid: String = UUID.randomUUID().toString()
                val addDeviceDate = CastLiteUUIDDataClass(0, speakerIpAddress, speakerName!!, uuid)
                libreVoiceDatabaseDao.addDeviceUUID(addDeviceDate)
                delay(2000)
                fetchUUIDFromDB(speakerIpAddress)
            }
        }
    }

    private fun exitOnBackPressed() {
        if (!from.isNullOrEmpty() && from!!.equals(CTDeviceSettingsActivity::class.java.simpleName, ignoreCase = true)) {
            val newIntent = Intent(this@ActivateCastActivity, CTDeviceSettingsActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            newIntent.putExtra(Constants.FROM_ACTIVITY, OpenGHomeAppActivity::class.java.simpleName)
            newIntent.putExtra(Constants.CURRENT_DEVICE_IP, currentIpAddress)
            newIntent.putExtra(Constants.DEVICE_NAME, speakerName)
            startActivity(newIntent)
            finish()
        } else {
            intentToHome(this)
        }
    }
}