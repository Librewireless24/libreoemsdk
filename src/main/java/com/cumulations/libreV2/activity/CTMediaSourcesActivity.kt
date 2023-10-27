package com.cumulations.libreV2.activity


import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cumulations.libreV2.AppUtils
import com.cumulations.libreV2.SharedPreferenceHelper
import com.cumulations.libreV2.isConnectedToSAMode
import com.cumulations.libreV2.model.SceneObject
import com.cumulations.libreV2.tcp_tunneling.TCPTunnelPacket
import com.cumulations.libreV2.tcp_tunneling.TunnelingControl
import com.cumulations.libreV2.tcp_tunneling.TunnelingData
import com.cumulations.libreV2.tcp_tunneling.enums.PayloadType
import com.libreAlexa.LErrorHandeling.LibreError
import com.libreAlexa.LibreApplication
import com.libreAlexa.R
import com.libreAlexa.Scanning.ScanningHandler
import com.libreAlexa.alexa.AlexaUtils
import com.libreAlexa.constants.Constants
import com.libreAlexa.constants.LSSDPCONST
import com.libreAlexa.constants.LUCIMESSAGES
import com.libreAlexa.constants.MIDCONST
import com.libreAlexa.databinding.AlertCheckboxBinding
import com.libreAlexa.databinding.CtActivityMediaSourcesBinding
import com.libreAlexa.databinding.CtListItemMediaSourcesBinding
import com.libreAlexa.luci.LSSDPNodeDB
import com.libreAlexa.luci.LSSDPNodes
import com.libreAlexa.luci.LUCIControl
import com.libreAlexa.luci.LUCIPacket
import com.libreAlexa.netty.LibreDeviceInteractionListner
import com.libreAlexa.netty.NettyData
import com.libreAlexa.util.LibreLogger
import com.libreAlexa.util.PicassoTrustCertificates
import com.libreAlexa.util.spotifyInstructions
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class CTMediaSourcesActivity : CTDeviceDiscoveryActivity(),LibreDeviceInteractionListner {

    companion object {
        const val TAG_CMD_ID = "CMD ID"
        const val TAG_WINDOW_CONTENT = "Window CONTENTS"
        const val TAG_BROWSER = "Browser"
        const val GET_HOME = "GETUI:HOME"
        const val BLUETOOTH_OFF = "OFF"
        const val BLUETOOTH_ON = "ON"
        const val BLUETOOTH_DISCONNECT="DISCONNECT"
        const val BLUETOOTH_ENTPAIR="ENTPAIR"
        const val BLUETOOTH_ENTCONNECTABLE="ENTCONNECTABLE"
        private var currentSceneObject: SceneObject? = null

        const val NETWORK_TIMEOUT = 1
        const val AUX_BT_TIMEOUT = 0x2
        const val ALEXA_REFRESH_TOKEN_TIMER = 0x12
        const val ACTION_INITIATED = 12345
        const val BT_AUX_INITIATED = ACTION_INITIATED
    }

    private lateinit var adapter: CTSourcesListAdapter

    private val mScanHandler = ScanningHandler.getInstance()

    private val myDevice = "My Device"

    private var currentIpAddress: String? = null
    private var currentSource: String? = null
    private var currentSourceIndexSelected = -1

    private val mediaSourcesList: MutableList<String> = ArrayList()
    private lateinit var binding: CtActivityMediaSourcesBinding
    private val TAG = CTMediaSourcesActivity::class.java.simpleName
    @SuppressLint("HandlerLeak")
    private val timeOutHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {

            if (msg.what == NETWORK_TIMEOUT) {
                LibreLogger.d(TAG, "recieved handler msg")
                closeLoader()
                /*showing error to user*/
                val error = LibreError(currentIpAddress, getString(R.string.requestTimeout))
                showErrorMessage(error)
            }

            if (msg.what == ACTION_INITIATED) {
                showLoader()
            }

            if (msg.what == BT_AUX_INITIATED) {
                val showMessage = msg.data.getString("MessageText")
                showLoaderAndAskSource(showMessage)
            }
            if (msg.what == AUX_BT_TIMEOUT) {
                closeLoader()
                val error = LibreError(currentIpAddress, getString(R.string.requestTimeout))
                showErrorMessage(error)
            }
            if (msg.what == ALEXA_REFRESH_TOKEN_TIMER) {
                closeLoader()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CtActivityMediaSourcesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        currentIpAddress = intent.getStringExtra(Constants.CURRENT_DEVICE_IP)
        currentSource = intent.getStringExtra(Constants.CURRENT_SOURCE)

    }

    override fun onStart() {
        super.onStart()
        initViews()
        setListeners()
        if (currentIpAddress != null) {
            currentSceneObject = ScanningHandler.getInstance().getSceneObjectFromCentralRepo(currentIpAddress) // ActiveSceneAdapter.mMasterSpecificSlaveAndFreeDeviceMap.get(currentIpAddress);
            if (currentSceneObject == null) {
                LibreLogger.d(TAG,"NetworkChanged NowPlayingFragment onCreateView")

            }
        }
    }

    private fun setListeners() {
        binding.ivToggleBluetooth.setOnClickListener {
            val luciControl = LUCIControl(currentIpAddress)
            if (binding.ivToggleBluetooth.isChecked) {
                if (binding.ivToggleAux.isChecked) {
                    /*WiFi == No Source*/
                    TunnelingControl(currentIpAddress).sendCommand(PayloadType.DEVICE_SOURCE,0x01)
                    //luciControl.SendCommand(MIDCONST.MID_AUX_STOP, null, LSSDPCONST.LUCI_SET)
                }
                // sleep when BT to AUX switch
//                luciControl.SendCommand(MIDCONST.MID_BLUETOOTH, BLUETOOTH_OFF, LSSDPCONST.LUCI_SET)
                TunnelingControl(currentIpAddress).sendCommand(PayloadType.DEVICE_SOURCE,0x01)
                timeOutHandler!!.sendEmptyMessageDelayed(AUX_BT_TIMEOUT, Constants.LOADING_TIMEOUT.toLong())

                val msg = Message().apply {
                    what = BT_AUX_INITIATED
                    data = Bundle().apply {
                        putString("MessageText","Turning off BT")
                    }
                }
                timeOutHandler.sendMessage(msg)
            } else {
               luciControl.SendCommand(MIDCONST.MID_BLUETOOTH, BLUETOOTH_ON, LSSDPCONST.LUCI_SET)
//                luciControl.SendCommand(MIDCONST.MID_BLUETOOTH, BLUETOOTH_ENTPAIR, LSSDPCONST.LUCI_SET)
//                luciControl.SendCommand(MIDCONST.MID_BLUETOOTH, BLUETOOTH_ENTCONNECTABLE, LSSDPCONST.LUCI_SET)

             /*   TunnelingControl(currentIpAddress).sendCommand(PayloadType.DEVICE_SOURCE,0x02*//*BT*//*)*/
                timeOutHandler!!.sendEmptyMessageDelayed(AUX_BT_TIMEOUT, Constants.LOADING_TIMEOUT.toLong())
                val msg = Message().apply {
                    what = BT_AUX_INITIATED
                    data = Bundle().apply {
                        putString("MessageText", getString(R.string.BtOnAlert))
                    }
                }
                timeOutHandler.sendMessage(msg)
            }
        }

        binding.ivToggleAux.setOnClickListener {
            val luciControl = LUCIControl(currentIpAddress)
            if (binding.ivToggleAux.isChecked) {
                if (binding.ivToggleBluetooth.isChecked) {
                    TunnelingControl(currentIpAddress).sendCommand(PayloadType.DEVICE_SOURCE,0x01)
                            //  luciControl.SendCommand(MIDCONST.MID_BLUETOOTH, BLUETOOTH_OFF, LSSDPCONST.LUCI_SET)
                }

               luciControl.SendCommand(MIDCONST.MID_AUX_STOP, null, LSSDPCONST.LUCI_SET)
                TunnelingControl(currentIpAddress).sendCommand(PayloadType.DEVICE_SOURCE,0x01)
                timeOutHandler!!.sendEmptyMessageDelayed(AUX_BT_TIMEOUT, Constants.LOADING_TIMEOUT.toLong())

                val msg = Message().apply {
                    what = BT_AUX_INITIATED
                    data = Bundle().apply {
                        putString("MessageText","Turning Off Aux")
                    }
                }
                timeOutHandler.sendMessage(msg)
            } else {
                /* Setting the source to default */
               luciControl.SendCommand(MIDCONST.MID_AUX_START, null, LSSDPCONST.LUCI_SET)
                TunnelingControl(currentIpAddress).sendCommand(PayloadType.DEVICE_SOURCE,0x00/*AUX*/)
                timeOutHandler!!.sendEmptyMessageDelayed(AUX_BT_TIMEOUT, Constants.LOADING_TIMEOUT.toLong())

                val msg = Message().apply {
                    what = BT_AUX_INITIATED
                    data = Bundle().apply {
                        putString("MessageText", getString(R.string.AuxOnAlert))
                    }
                }
                timeOutHandler.sendMessage(msg)
            }
        }

        binding.seekBarVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                LibreLogger.d(TAG,"onProgressChanged $progress fromUser $fromUser")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                LibreLogger.d(TAG,"onStartTracking ${seekBar.progress}")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

                LibreLogger.d("onStopTracking", "${seekBar.progress}")

                if (seekBar.progress==0){
                    binding.ivVolumeMute.setImageResource(R.drawable.ic_volume_mute)
                } else binding.ivVolumeMute.setImageResource(R.drawable.volume_low_enabled)

                val sceneObject = mScanHandler.getSceneObjectFromCentralRepo(currentIpAddress)
                LUCIControl.SendCommandWithIp(MIDCONST.VOLUME_CONTROL, "" + seekBar.progress, LSSDPCONST.LUCI_SET, sceneObject?.ipAddress)
                sceneObject?.volumeValueInPercentage = seekBar.progress
                sceneObject.mute_status=LUCIMESSAGES.UNMUTE
                mScanHandler.putSceneObjectToCentralRepo(sceneObject?.ipAddress, sceneObject)

//                TunnelingControl(currentIpAddress).sendCommand(PayloadType.DEVICE_VOLUME,(seekBar.progress/5).toByte())
            }
        })

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.ivAlexaSettings.setOnClickListener {
            val mNode = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(currentIpAddress)

            if (mNode.getmDeviceCap().getmSource().isAlexaAvsSource) {
                if (mNode.alexaRefreshToken == null || mNode.alexaRefreshToken.isEmpty()
                    || mNode.alexaRefreshToken == "0"
                ) {
                    startActivity(Intent(this@CTMediaSourcesActivity, CTAmazonLoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        putExtra(Constants.CURRENT_DEVICE_IP, currentIpAddress)
                        putExtra(Constants.FROM_ACTIVITY, CTMediaSourcesActivity::class.java.simpleName)
                    })
                } else {
                    val i = Intent(
                        this@CTMediaSourcesActivity,
                        CTAlexaThingsToTryActivity::class.java
                    )
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    i.putExtra(Constants.CURRENT_DEVICE_IP, currentIpAddress)
                    i.putExtra(
                        Constants.FROM_ACTIVITY,
                        CTConnectingToMainNetwork::class.java.simpleName
                    )
                    startActivity(i)
                    finish()
                }
            }
            else{
                /* SUMA : For Alexa not suported doing this transition  for now , add respective CAST OEM related Changes here*/
                    LibreLogger.d(TAG,"suma alexa login not supported")
                showToast(getString(R.string.alexa_not_supported))
            }
        }

        binding.ivDeviceSettings.setOnClickListener {
            startActivity(Intent(this@CTMediaSourcesActivity, CTDeviceSettingsActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra(Constants.CURRENT_DEVICE_IP, currentIpAddress)
                putExtra(Constants.FROM_ACTIVITY, CTMediaSourcesActivity::class.java.simpleName)
            })
        }
        binding.ivVolumeMute.setOnClickListener {
            val sceneObject = mScanHandler.getSceneObjectFromCentralRepo(currentIpAddress)
            if (sceneObject != null && sceneObject?.mute_status != null) {
                if (sceneObject.mute_status == LUCIMESSAGES.UNMUTE) {
                    LUCIControl.SendCommandWithIp(MIDCONST.MID_PLAYCONTROL.toInt(), LUCIMESSAGES.MUTE, LSSDPCONST.LUCI_SET, sceneObject!!.ipAddress)
                } else {
                    LUCIControl.SendCommandWithIp(MIDCONST.MID_PLAYCONTROL.toInt(), LUCIMESSAGES.UNMUTE, LSSDPCONST.LUCI_SET, sceneObject!!.ipAddress)
                }
            } else {
                LibreLogger.d(TAG, "currentSceneObject or mute status null")
            }
        }
    }

    private fun initViews() {
        val lssdpNodes = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(currentIpAddress)
        binding.tvDeviceName.text = lssdpNodes?.friendlyname
        binding.tvDeviceName.isSelected = true
        mediaSourcesList.clear()
        if (lssdpNodes?.getmDeviceCap() != null && lssdpNodes.getmDeviceCap().getmSource() != null) {
            for (i in lssdpNodes.getmDeviceCap().getmSource().capitalCities) {
                if (i.value == true) {
                    mediaSourcesList.add(i.key)
                } else {
                    LibreLogger.d(TAG, "Source list:else")
                }
            }
        } else {
            LibreLogger.d(TAG, "Source device cap null or source null")
        }
        adapter = CTSourcesListAdapter(this, mediaSourcesList)
        binding.rvMediaSourcesList.layoutManager = LinearLayoutManager(this)
        binding.rvMediaSourcesList.adapter = adapter

        if (currentSource != null) {
            val currentSource = Integer.valueOf(currentSource)
            val sceneObject = mScanHandler.getSceneObjectFromCentralRepo(currentIpAddress) ?: return

            /* Karuna , if Zone is playing in BT/AUX and We Released the Zone
            and we creating the same Guy as a Master then Aux should not Switch ON as a Default*/
            if ((currentSource == Constants.AUX_SOURCE /*|| currentSource == Constants.EXTERNAL_SOURCE*/)
                    && (sceneObject.playstatus == SceneObject.CURRENTLY_STOPPED
                            || sceneObject.playstatus == SceneObject.CURRENTLY_NOTPLAYING)) {
                LibreLogger.d(TAG,"AUXSTATE "  + sceneObject.playstatus)
                binding.ivToggleAux.isChecked = false
            }
            if (currentSource == Constants.BT_SOURCE
                    && (sceneObject.playstatus == SceneObject.CURRENTLY_STOPPED
                            || sceneObject.playstatus == SceneObject.CURRENTLY_NOTPLAYING)) {
                LibreLogger.d(TAG,"BTSTATE "  + sceneObject.playstatus)
                binding.ivToggleBluetooth.isChecked = false
            }
        }

        /*For free speakers irrespective of the state use Individual volume*/
        if (LibreApplication./*ZONE_VOLUME_MAP*/INDIVIDUAL_VOLUME_MAP.containsKey(currentIpAddress)) {
            binding.seekBarVolume.progress = LibreApplication.INDIVIDUAL_VOLUME_MAP[currentIpAddress]!!
        } else {
            LUCIControl(currentIpAddress).SendCommand(MIDCONST./*ZONE_VOLUME*/VOLUME_CONTROL, null, LSSDPCONST.LUCI_GET)
            val sceneObject = mScanHandler.getSceneObjectFromCentralRepo(currentIpAddress)
            if (sceneObject?.volumeValueInPercentage!! >= 0)
                binding.seekBarVolume.progress = sceneObject.volumeValueInPercentage
        }
        if (binding.seekBarVolume.progress==0){
            binding.ivVolumeMute.setImageResource(R.drawable.ic_volume_mute)
        } else binding.ivVolumeMute.setImageResource(R.drawable.volume_low_enabled)

        val ssid = AppUtils.getConnectedSSID(this)
        if (ssid != null && !isConnectedToSAMode(ssid)) {
            binding.ivAlexaSettings.visibility = View.VISIBLE
            if (lssdpNodes?.alexaRefreshToken.isNullOrEmpty()|| lssdpNodes?.alexaRefreshToken ==
                "0" && !SharedPreferenceHelper.getInstance(this).isAlexaLoginAlertDontAskChecked
                    (currentIpAddress!!)){

                if (lssdpNodes.getmDeviceCap().getmSource().isAlexaAvsSource()) {
                    showAlexaLoginAlert()
                }
                else{
                   LibreLogger.d(TAG,"suma in alexa login not supported on this device")
                }
            }
        } else {
            binding.ivAlexaSettings.visibility = View.GONE
        }
    }

    private fun showAlexaLoginAlert() {
        AlertDialog.Builder(this).apply {
//            setTitle(R.string.alexa_not_connected)
//            setMessage(getString(R.string.sign_in_az))
            //val checkBoxView = View.inflate(this@CTMediaSourcesActivity,R.layout.alert_checkbox,null)
            val binding = AlertCheckboxBinding.inflate(layoutInflater)
            binding.cbDont.setOnCheckedChangeListener { compoundButton, b ->
                val sharedPreferenceHelper = SharedPreferenceHelper.getInstance(this@CTMediaSourcesActivity)
                if (/*compoundButton.isPressed && */b){
                    sharedPreferenceHelper.alexaLoginAlertDontAsk(currentIpAddress!!,dontAsk = true)
                } else {
                    sharedPreferenceHelper.alexaLoginAlertDontAsk(currentIpAddress!!,dontAsk = false)
                }
            }
            setView(binding.root)

            setPositiveButton(R.string.amazon_login) { dialogInterface, i ->
                dialogInterface.dismiss()
                startActivity(Intent(this@CTMediaSourcesActivity, CTAmazonLoginActivity::class.java).apply {
                    putExtra(Constants.CURRENT_DEVICE_IP, currentIpAddress)
                })
            }

            setNegativeButton(R.string.cancel) { dialogInterface, i ->
                dialogInterface.dismiss()
            }

            show()
        }
    }

    private fun fetchAuxBtStatus(){
        readBTControllerStatus()
        readBluetoothStatus()
        getAuxStatus()
    }

    private fun readBTControllerStatus() {
        LUCIControl(currentIpAddress).SendCommand(MIDCONST.MID_ENV_READ, LUCIMESSAGES.READ_BT_CONTROLLER, LSSDPCONST.LUCI_GET)
    }

    private fun readBluetoothStatus() {
        LUCIControl(currentIpAddress).SendCommand(MIDCONST.MID_BLUETOOTH, null, LSSDPCONST.LUCI_GET)
    }

    private fun getAuxStatus() {
        LUCIControl(currentIpAddress).SendCommand(MIDCONST.MID_CURRENT_SOURCE.toInt(), null, LSSDPCONST.LUCI_GET)
    }

    private fun showLoaderAndAskSource(source: String?) {
        if (this@CTMediaSourcesActivity.isFinishing)
            return
        //asking source
//        val luciControl = LUCIControl(currentIpAddress)
//        luciControl.SendCommand(MIDCONST.MID_CURRENT_SOURCE.toInt(), null, LSSDPCONST.LUCI_GET)
        showLoader()
        showToast(source!!)
//        fetchAuxBtStatus()
    }

    private fun showLoader() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun closeLoader() {
        binding.progressBar.visibility = View.INVISIBLE
    }

    override fun deviceDiscoveryAfterClearingTheCacheStarted() {

    }

    override fun newDeviceFound(node: LSSDPNodes) {

    }

    override fun deviceGotRemoved(mIpAddress: String) {

    }

    override fun messageRecieved(nettyData: NettyData) {

        val remotedeviceIp = nettyData.getRemotedeviceIp()

        val luciPacket = LUCIPacket(nettyData.getMessage())
        LibreLogger.d(TAG, "Message received for " + remotedeviceIp
                + "\tCommand is " + luciPacket.command
                + "\tmsg is " + String(luciPacket.getpayload()))

        val currentNode = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(remotedeviceIp)
        val sceneObject = mScanHandler.getSceneObjectFromCentralRepo(remotedeviceIp)

        if (currentIpAddress!!.equals(remotedeviceIp, ignoreCase = true)) {
            when (luciPacket.command) {

                MIDCONST.SET_UI -> {
                    val message = String(luciPacket.getpayload())
                    LibreLogger.d(TAG, " message 42 recieved PLAY JSON  $message")
                    try {
                        parseJsonAndReflectInUI(message)
                        val sceneObject = mScanHandler.getSceneObjectFromCentralRepo(currentIpAddress)
                        val root = JSONObject(message)
                        val cmd_id = root.getInt(LUCIMESSAGES.TAG_CMD_ID)
                        LibreLogger.d(TAG, "Handle Play Json UI" + sceneObject.trackName)
                        val window = root.getJSONObject(LUCIMESSAGES.ALEXA_KEY_WINDOW_CONTENT)
                        currentSceneObject = ScanningHandler.getInstance().getSceneObjectFromCentralRepo(currentIpAddress)
                        // ActiveSceneAdapter.mMasterSpecificSlaveAndFreeDeviceMap.get(currentIpAddress);
                        currentSceneObject = AppUtils.updateSceneObjectWithPlayJsonWindow(window, currentSceneObject!!)
                        if(currentSceneObject!=null) {
                            updateMusicPlayViews(currentSceneObject)
                        }
                        if (cmd_id == 3) {
                             if(sceneObject!=null) {
                                 LibreLogger.d(TAG, "Handle Play Json UI" + sceneObject.trackName)
                                 sceneObject.playUrl = window.getString("PlayUrl").lowercase(Locale.getDefault())
                                 sceneObject.album_art = window.getString("CoverArtUrl")
                                 sceneObject.artist_name = window.getString("Artist")
                                 sceneObject.totalTimeOfTheTrack = window.getLong("TotalTime")
                                 sceneObject.trackName = window.getString("TrackName")
                                 sceneObject.album_name = window.getString("Album")
                                 updateMusicPlayViews(sceneObject)
                                 LibreApplication.currentAlbumnName = sceneObject.album_name
                                 LibreApplication.currentArtistName = sceneObject.artist_name
                                 LibreApplication.currentAlbumArtView = sceneObject.album_art

                                 LibreLogger.d(TAG, "Handle Play Json UI" + sceneObject.trackName + "PLAY URL\n" + sceneObject.playUrl + "ARTIST NAME" + sceneObject.artist_name)
                                 //MID_STOP_PREV_SOURCE
                             }
                        }
//                        val luciControl = LUCIControl(currentIpAddress)
//                        luciControl.SendCommand(MIDCONST.MID_STOP_PREV_SOURCE, "STOP", LSSDPCONST.LUCI_SET)

                    } catch (e: JSONException) {
                        e.printStackTrace()
                        LibreLogger.d(TAG, " Json exception ")
                    }

                }

                /* This indicates the bluetooth status*/
                MIDCONST.MID_BLUETOOTH -> {
                    val message = String(luciPacket.getpayload())
                    LibreLogger.d(TAG, " message 209 is recieved  $message")
                    when(message){
                        BLUETOOTH_ON -> {
                            timeOutHandler?.removeMessages(AUX_BT_TIMEOUT)
                            closeLoader()
                            binding.ivToggleBluetooth.isChecked = true
                            if (binding.ivToggleAux.isChecked) {
                                binding.ivToggleAux.isChecked = false
                            }
                        }
                        else -> {
                            timeOutHandler?.removeMessages(AUX_BT_TIMEOUT)
                            closeLoader()
                            binding.ivToggleBluetooth.isChecked = false
                        }
                    }
                }

                MIDCONST.MID_ENV_READ -> {
                    val messages = String(luciPacket.getpayload())
                    if (messages.contains("BT_CONTROLLER")) {
                        val BTvalue = Integer.parseInt(messages.substring(messages.indexOf(":") + 1))
                        LibreLogger.d(TAG, "BT_CONTROLLER value after parse is $BTvalue")
                        sceneObject?.bT_CONTROLLER = BTvalue
                    }
                }

                MIDCONST.MID_AUX_START -> {
                    val message = String(luciPacket.getpayload())
                    LibreLogger.d(TAG, " message 95 is $message")
                    timeOutHandler?.removeMessages(AUX_BT_TIMEOUT)
                    closeLoader()
                    binding.ivToggleAux.isChecked = true
                    if (binding.ivToggleBluetooth.isChecked) {
                        binding.ivToggleBluetooth.isChecked = false
                    }

                    if (message.contains("SUCCESS",true)){
                        timeOutHandler?.removeMessages(AUX_BT_TIMEOUT)
                        closeLoader()
                        binding.ivToggleAux.isChecked = true
                        if (binding.ivToggleBluetooth.isChecked) {
                            binding.ivToggleBluetooth.isChecked = false
                        }
                    }
                }

                MIDCONST.MID_AUX_STOP -> {
                    val message = String(luciPacket.getpayload())
                    LibreLogger.d(TAG, " message 96 is $message")
                    if (message.contains("SUCCESS",true)){
                        timeOutHandler?.removeMessages(AUX_BT_TIMEOUT)
                        closeLoader()
                        binding.ivToggleAux.isChecked = false
                    }
                }
                MIDCONST.MID_STOP_PREV_SOURCE -> {
                    val message = String(luciPacket.getpayload())
                    LibreLogger.d(TAG, " message 97 is $message")
                    val luciControl = LUCIControl(currentIpAddress)
                    luciControl.SendCommand(MIDCONST.MID_BLUETOOTH, BLUETOOTH_OFF, LSSDPCONST.LUCI_SET)


                }
                MIDCONST.MID_CURRENT_PLAY_STATE.toInt() -> {

                    val message = String(luciPacket.getpayload())
                    try {
                        val duration = Integer.parseInt(message)
                        sceneObject?.playstatus = duration

                        if (mScanHandler.isIpAvailableInCentralSceneRepo(currentIpAddress)) {
                            mScanHandler.putSceneObjectToCentralRepo(currentIpAddress, sceneObject)
                        }

                        when {
                            sceneObject?.currentSource == Constants.AUX_SOURCE
                                    /*|| sceneObject?.currentSource == Constants.EXTERNAL_SOURCE*/-> {
                                if (binding.ivToggleBluetooth.isChecked) {
                                    binding.ivToggleBluetooth.isChecked = false
                                }
                                if (sceneObject.playstatus == SceneObject.CURRENTLY_PLAYING) {
                                    binding.ivToggleAux.isChecked = true
                                }
                            }
                            sceneObject?.currentSource == Constants.BT_SOURCE -> {
                                if (binding.ivToggleAux.isChecked) {
                                    binding.ivToggleAux.isChecked = false
                                }
                                if (sceneObject.playstatus == SceneObject.CURRENTLY_PLAYING)
                                    binding.ivToggleBluetooth.isChecked = true
                            }
                            /*else -> {
                                binding.ivToggleAux.isChecked = false
                                binding.ivToggleBluetooth.isChecked = false
                            }*/
                        }
                        LibreLogger.d(TAG, "Recieved the playstate to be" + sceneObject?.playstatus)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                MIDCONST.MID_CURRENT_SOURCE.toInt() -> {
                    val currentSource = String(luciPacket.getpayload())
                    LibreLogger.d(TAG, " message 50 is $currentSource")
                    if (sceneObject != null) {
                        sceneObject.currentSource = currentSource.toInt()
                        mScanHandler.putSceneObjectToCentralRepo(nettyData.remotedeviceIp, sceneObject)
                        LibreLogger.d(TAG, "suma in current source")
                        updateMusicPlayViews(sceneObject)
                    }
                    when {
                        currentSource.contains(Constants.AUX_SOURCE.toString())
                                /*|| currentSource.contains(Constants.EXTERNAL_SOURCE.toString())*/-> {
                            timeOutHandler!!.removeMessages(AUX_BT_TIMEOUT)
                            closeLoader()
                            binding.ivToggleAux.isChecked = true
                            if (binding.ivToggleBluetooth.isChecked)
                                binding.ivToggleBluetooth.isChecked = false
                        }
                        currentSource.contains(Constants.BT_SOURCE.toString()) -> {
                            /*removing timeout and closing loader*/
                            timeOutHandler!!.removeMessages(AUX_BT_TIMEOUT)
                            closeLoader()
                            binding.ivToggleBluetooth.isChecked = true
                            if (binding.ivToggleAux.isChecked)
                                binding.ivToggleAux.isChecked = false
                        }
                        currentSource.contains(Constants.NO_SOURCE.toString()) || currentSource.contains("NO_SOURCE") -> {
                            LibreLogger.d(TAG, " No Source received hence closing dialog")
                            /**Closing loader after 1.5 second */
                            if (timeOutHandler?.hasMessages(AUX_BT_TIMEOUT)!!) {
                                timeOutHandler.removeMessages(AUX_BT_TIMEOUT)
//                                timeOutHandler!!.sendEmptyMessageDelayed(AUX_BT_TIMEOUT, 1500)
                                Handler(Looper.myLooper()!!).postDelayed({
                                    runOnUiThread { closeLoader() }
                                },1500)
                            }
//                            closeLoader()
                            binding.ivToggleBluetooth.isChecked = false
                            binding.ivToggleAux.isChecked = false
                        }

                        /*else -> {
                            closeLoader()
                            binding.ivToggleAux.isChecked = false
                            binding.ivToggleBluetooth.isChecked = false
                        }*/
                    }
                }
                MIDCONST.MID_BLUETOOTH_STATUS -> {
                    val message = String(luciPacket.getpayload())
                    LibreLogger.d(TAG, "message 210 is recieved  $message")
                    when(message){
                        BLUETOOTH_ON -> {
                            timeOutHandler.removeMessages(AUX_BT_TIMEOUT)
                            closeLoader()
                            binding.ivToggleBluetooth.isChecked = true
                            if (binding.ivToggleAux.isChecked) {
                                binding.ivToggleAux.isChecked = false
                            }
                        }
                        else -> {
                            timeOutHandler.removeMessages(AUX_BT_TIMEOUT)
                            closeLoader()
                            binding.ivToggleBluetooth.isChecked = false
                        }
                    }
                }
                /**For RIVA speakers, during AUX we won't get volume in MB 64 for volume changes
                 * done through speaker hardware buttons**/
                MIDCONST.VOLUME_CONTROL -> {
                    try {
                        val msg = String(luciPacket.getpayload())
                        val volume = Integer.parseInt(msg)


                        if (sceneObject.currentSource == Constants.AUX_SOURCE)
                            return

                        LibreLogger.d(TAG, "VOLUME_CONTROL volume $volume")
                        if (sceneObject?.volumeValueInPercentage != volume) {
                            sceneObject?.volumeValueInPercentage = volume
                            mScanHandler.putSceneObjectToCentralRepo(nettyData.getRemotedeviceIp(), sceneObject)

                            binding.seekBarVolume.progress = sceneObject?.volumeValueInPercentage!!

                            if (binding.seekBarVolume.progress==0){
                                binding.ivVolumeMute.setImageResource(R.drawable.ic_volume_mute)
                            } else binding.ivVolumeMute.setImageResource(R.drawable.volume_low_enabled)

                            binding.seekBarVolume.max = 100
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                MIDCONST.MUTE_UNMUTE_STATUS -> {
                    val msg=String(luciPacket.getpayload())
                    LibreLogger.d(TAG, "MB : 63, msg = $msg")
                    try {
                        if(msg.isNotEmpty()) {
                            sceneObject.mute_status = msg
                            mScanHandler.putSceneObjectToCentralRepo(nettyData.getRemotedeviceIp(), sceneObject)
                            //Setting the Mute and UnMute status
                            if (sceneObject.mute_status == LUCIMESSAGES.MUTE) {
                                binding.ivVolumeMute.setImageResource(R.drawable.ic_volume_mute)
                            } else {
                                binding.ivVolumeMute.setImageResource(R.drawable.volume_low_enabled)
                            }
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            }

        }
    }


    @Throws(JSONException::class)
    private fun parseJsonAndReflectInUI(jsonStr: String?) {

        LibreLogger.d(TAG, "Json Recieved from remote device " + jsonStr!!)
        try {
            runOnUiThread { closeLoader() }

            val root = JSONObject(jsonStr)
            val cmd_id = root.getInt(TAG_CMD_ID)
            val window = root.getJSONObject(TAG_WINDOW_CONTENT)

            LibreLogger.d(TAG, "Command Id$cmd_id")

            if (cmd_id == 1) {
                val Browser = window.getString(TAG_BROWSER)
                if (Browser.equals("HOME", ignoreCase = true)) {
                    /* Now we have successfully got the stack intialiized to home */
                    timeOutHandler.removeMessages(NETWORK_TIMEOUT)
                    unRegisterForDeviceEvents()
                    val intent = Intent(this@CTMediaSourcesActivity, CTDeviceBrowserActivity::class.java)
                    intent.putExtra(Constants.CURRENT_DEVICE_IP, currentIpAddress)
                    intent.putExtra(Constants.CURRENT_SOURCE_INDEX_SELECTED, currentSourceIndexSelected)
                    LibreLogger.d(TAG, "removing handler message")
                    startActivity(intent)
                    finish()
                }
            }
            if (cmd_id == 3) {
                if(currentSceneObject!=null) {
                    LibreLogger.d(TAG, "Handle Play Json UI" + currentSceneObject!!.trackName)
                    currentSceneObject!!.playUrl = window.getString("PlayUrl")
                        .lowercase(Locale.getDefault())
                    currentSceneObject!!.album_art = window.getString("CoverArtUrl")
                    currentSceneObject!!.artist_name = window.getString("Artist")
                    currentSceneObject!!.totalTimeOfTheTrack = window.getLong("TotalTime")
                    currentSceneObject!!.trackName = window.getString("TrackName")
                    currentSceneObject!!.album_name = window.getString("Album")
                    updateMusicPlayViews(currentSceneObject)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        /*Registering to receive messages*/
        LibreLogger.d(TAG,"suma in on resume media source activity")
        registerForDeviceEvents(this)
        try {
            setMusicPlayerWidget(binding.ilMusicPlayingWidget.flMusicPlayWidget, currentIpAddress!!)
        }
        catch(e:KotlinNullPointerException){
            e.printStackTrace()
        }
        AlexaUtils.sendAlexaRefreshTokenRequest(currentIpAddress)
        LUCIControl(currentIpAddress).SendCommand(MIDCONST.MID_CURRENT_SOURCE.toInt(), null, LSSDPCONST.LUCI_GET)
        LUCIControl(currentIpAddress).SendCommand(MIDCONST.MID_BLUETOOTH_STATUS, null, LSSDPCONST.LUCI_GET)
        LUCIControl(currentIpAddress).SendCommand( MIDCONST.MUTE_UNMUTE_STATUS,null,LSSDPCONST.LUCI_GET)
//        val luciControl = LUCIControl(currentIpAddress)
//        luciControl.SendCommand(MIDCONST.MID_BLUETOOTH, BLUETOOTH_OFF, LSSDPCONST.LUCI_SET)

       val sceneObject = mScanHandler.getSceneObjectFromCentralRepo(currentIpAddress)
        //            updateMusicPlayViews(currentsceneObject) before
        if(sceneObject!=null) {
            updateMusicPlayViews(sceneObject)
            handleAlexaViews(sceneObject)
        }
    }

    override fun onStop() {
        super.onStop()
        /*unregister events */
        if (timeOutHandler != null)
            timeOutHandler.removeCallbacksAndMessages(null)
        unRegisterForDeviceEvents()
        closeLoader()
    }


    private fun updateAlbumArt() {
        val currentSceneObject = mScanHandler.getSceneObjectFromCentralRepo(currentIpAddress)

        if (currentSceneObject!!.currentSource != Constants.AUX_SOURCE
                /*&& currentSceneObject!!.currentSource != EXTERNAL_SOURCE*/
                && currentSceneObject.currentSource != Constants.BT_SOURCE
                && currentSceneObject.currentSource != Constants.GCAST_SOURCE) {
            var album_url = ""
            if (!currentSceneObject.album_art.isNullOrEmpty() && currentSceneObject.album_art?.equals("coverart.jpg", ignoreCase = true)!!) {

                album_url = "http://" + currentSceneObject.ipAddress + "/" + "coverart.jpg"
                /* If Track Name is Different just Invalidate the Path
                 * And if we are resuming the Screen(Screen OFF and Screen ON) , it will not re-download it */
                if (currentSceneObject.trackName != null
                       /* && !currentTrackName.equals(currentSceneObject!!.trackName, ignoreCase = true)*/) {
                   // currentTrackName = currentSceneObject?.trackName!!
                    val mInvalidated = mInvalidateTheAlbumArt(currentSceneObject, album_url)
                    LibreLogger.d(TAG, "Invalidated the URL $album_url Status $mInvalidated")
                }

                PicassoTrustCertificates.getInstance(this)
                        .load(album_url)
                        .error(R.mipmap.album_art).placeholder(R.mipmap.album_art)
                        .into(binding.ilMusicPlayingWidget.ivAlbumArt)

            } else {
                when {
                    !currentSceneObject.album_art.isNullOrEmpty() -> {
                        album_url = currentSceneObject.album_art

                        if (currentSceneObject.trackName != null
                              /*  && !currentTrackName.equals(currentSceneObject!!.trackName, ignoreCase = true)*/) {
                           // currentTrackName = currentSceneObject?.trackName!!
                            val mInvalidated = mInvalidateTheAlbumArt(currentSceneObject, album_url)
                            LibreLogger.d(TAG, "Invalidated the URL $album_url Status $mInvalidated")
                        }

                        PicassoTrustCertificates.getInstance(this)
                                .load(album_url)
                                .placeholder(R.mipmap.album_art)
                                /*.memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE)*/
                                .error(R.mipmap.album_art)
                                .into(binding.ilMusicPlayingWidget.ivAlbumArt)

                    }

                    else -> {
                        binding.ilMusicPlayingWidget.ivAlbumArt.setImageResource(R.mipmap.album_art)
                    }
                }
            }
        }
    }

    internal inner class CTSourcesListAdapter(val context: Context, private var sourcesList: MutableList<String>?) : RecyclerView.Adapter<CTSourcesListAdapter.SourceItemViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, i: Int): SourceItemViewHolder {
            val itemBinding = CtListItemMediaSourcesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return SourceItemViewHolder(itemBinding)
        }


        override fun onBindViewHolder(viewHolder: SourceItemViewHolder, position: Int) {
            val sourceItem = sourcesList?.get(position)
            viewHolder.bindSourceItem(sourceItem,position)

        }

        override fun getItemCount(): Int {
            return sourcesList?.size!!
        }

        inner class SourceItemViewHolder(private val itemBinding: CtListItemMediaSourcesBinding) : RecyclerView.ViewHolder(itemBinding.root) {

            fun bindSourceItem(source: String?, position: Int) {
                itemBinding.tvSourceType.text = source
                when(source) {
                    context.getString(R.string.airplay) -> itemBinding.ivSourceIcon.setImageResource(R.drawable.airplay)
                    context.getString(R.string.dmr) -> itemBinding.ivSourceIcon.setImageResource(R.drawable.add_device_selected)
                    context.getString(R.string.dmp) -> itemBinding.ivSourceIcon.setImageResource(R.drawable.add_device_selected)
                    context.getString(R.string.spotify) -> itemBinding.ivSourceIcon.setImageResource(R.mipmap.spotify)
                    context.getString(R.string.usb) -> itemBinding.ivSourceIcon.setImageResource(R.drawable.usb_storage_enabled)
                    context.getString(R.string.sdcard) -> itemBinding.ivSourceIcon.setImageResource(R.mipmap.sdcard)
                    context.getString(R.string.melon) -> itemBinding.ivSourceIcon.setImageResource(R.drawable.add_device_selected)
                    context.getString(R.string.v_tuner) -> itemBinding.ivSourceIcon.setImageResource(R.mipmap.vtuner_logo_remotesources_title)
                    context.getString(R.string.tune_in) -> itemBinding.ivSourceIcon.setImageResource(R.mipmap.tunein_logo1)
                    context.getString(R.string.miracast) -> itemBinding.ivSourceIcon.setImageResource(R.drawable.add_device_selected)
                    context.getString(R.string.ddms_salve) -> itemBinding.ivSourceIcon.setImageResource(R.drawable.add_device_selected)
                    context.getString(R.string.aux_in) -> itemBinding.ivSourceIcon.setImageResource(R.drawable.aux_enabled)
                    context.getString(R.string.apple_device) -> itemBinding.ivSourceIcon.setImageResource(R.drawable.add_device_selected)
                    context.getString(R.string.direct_url) -> itemBinding.ivSourceIcon.setImageResource(R.drawable.add_device_selected)
                    context.getString(R.string.bluetooth) -> itemBinding.ivSourceIcon.setImageResource(R.drawable.bluetooth_icon)
                    context.getString(R.string.deezer) -> itemBinding.ivSourceIcon.setImageResource(R.mipmap.deezer_logo)
                    context.getString(R.string.tidal) -> itemBinding.ivSourceIcon.setImageResource(R.mipmap.tidal_white_logo)
                    context.getString(R.string.favourites) -> itemBinding.ivSourceIcon.setImageResource(R.mipmap.ic_remote_favorite)
                    context.getString(R.string.google_cast) -> itemBinding.ivSourceIcon.setImageResource(R.drawable.chromecast_built_in_logo_primary)
                    context.getString(R.string.external_source) -> itemBinding.ivSourceIcon.setImageResource(R.drawable.add_device_selected)
                    context.getString(R.string.alexa_source) -> itemBinding.ivSourceIcon.setImageResource(R.drawable.airable_amazon_music)
                }
                //Shaik We have to add the Listeners also waiting for Device team
                itemBinding.llMediaSource.setOnClickListener {
                    if (this@CTMediaSourcesActivity.isFinishing)
                        return@setOnClickListener

                    if (source != myDevice && getConnectedSSIDName(this@CTMediaSourcesActivity).contains(Constants.DDMS_SSID)) {
                        val error = LibreError(currentIpAddress, Constants.NO_INTERNET_CONNECTION)
                        showErrorMessage(error)
                        return@setOnClickListener
                    }

                    when (source) {

                        context.getString(R.string.airplay),
                        context.getString(R.string.dmr),
                        context.getString(R.string.dmp),
                        context.getString(R.string.sdcard),
                        context.getString(R.string.melon),
                        context.getString(R.string.v_tuner),
                        context.getString(R.string.tune_in),
                        context.getString(R.string.miracast),
                        context.getString(R.string.ddms_salve),
                        context.getString(R.string.aux_in),
                        context.getString(R.string.apple_device),
                        context.getString(R.string.direct_url),
                        context.getString(R.string.bluetooth),
                        context.getString(R.string.deezer),
                        context.getString(R.string.favourites),
                        context.getString(R.string.external_source),
                        context.getString(R.string.usb),
                        context.getString(R.string.alexa_source)->{
                            showToast("We are not supporting now")
                        }

                        context.getString(R.string.my_device) -> {
                            if (!checkReadStoragePermission()){
                                return@setOnClickListener
                            }
                        }
                        context.getString(R.string.tidal) ->{
                            openTidal()
                        }
                        context.getString(R.string.google_cast) ->{
                            openGhome()
                        }
                        context.getString(R.string.spotify)->{
                            if (this@CTMediaSourcesActivity.isFinishing)
                                return@setOnClickListener

                            val spotifyIntent = Intent(this@CTMediaSourcesActivity, spotifyInstructions::class.java)
                            spotifyIntent.putExtra("current_ipaddress", currentIpAddress)
                            spotifyIntent.putExtra("current_source", "" + currentSource)
                            startActivity(spotifyIntent)
                        }
                        //Commented because crashing

                      /*  context.getString(R.string.usb) -> {

                            currentSourceIndexSelected = 3
                            *//*Reset the UI to Home ,, will wait for the confirmation of home command completion and then start the required activity*//*
                            LUCIControl(currentIpAddress).SendCommand(MIDCONST.MID_REMOTE_UI.toInt(), GET_HOME, LSSDPCONST.LUCI_SET)
                            ///////////// timeout for dialog - showLoader() ///////////////////
                            timeOutHandler!!.sendEmptyMessageDelayed(NETWORK_TIMEOUT, Constants.ITEM_CLICKED_TIMEOUT.toLong())
                            showLoader()
//                            val luciControl = LUCIControl(currentIpAddress)
//
//                           luciControl.SendCommand(MIDCONST.MID_BLUETOOTH, BLUETOOTH_DISCONNECT, LSSDPCONST.LUCI_SET)
//                            TunnelingControl(currentIpAddress).sendCommand(PayloadType.DEVICE_SOURCE,0x01)

                            // luciControl.SendCommand(MIDCONST.MID_STOP_PREV_SOURCE, "STOP", LSSDPCONST.LUCI_SET)

                          //  LUCIControl(currentIpAddress).SendCommand(MIDCONST.MID_STOP_PREV_SOURCE, BLUETOOTH_OFF, LSSDPCONST.LUCI_SET)

                        }*/

                        context.getString(R.string.mediaserver) -> {
                          //  LUCIControl(currentIpAddress).SendCommand(MIDCONST.MID_BLUETOOTH, BLUETOOTH_OFF, LSSDPCONST.LUCI_SET)

                            currentSourceIndexSelected = 0
                            /*Reset the UI to Home ,, will wait for the confirmation of home command completion and then start the required activity*/
                            LUCIControl(currentIpAddress).SendCommand(MIDCONST.MID_REMOTE_UI.toInt(), GET_HOME, LSSDPCONST.LUCI_SET)
                            ///////////// timeout for dialog - showLoader() ///////////////////
                            timeOutHandler!!.sendEmptyMessageDelayed(NETWORK_TIMEOUT, Constants.ITEM_CLICKED_TIMEOUT.toLong())
                            LibreLogger.d(TAG, "sending handler msg")
                            showLoader()

                            /*startActivity(Intent(this@CTMediaSourcesActivity, CTDMSDeviceListActivity::class.java).apply {
                                putExtra(AppConstants.IS_LOCAL_DEVICE_SELECTED, false)
                                putExtra(Constants.CURRENT_DEVICE_IP, currentIpAddress)
                            })
                            finish()*/

                            /*CTMediaServerListFragment().apply {
                                arguments = Bundle().apply {
                                    putString(Constants.CURRENT_DEVICE_IP,currentIpAddress)
                                }
                                show(supportFragmentManager,this::class.java.simpleName)
                            }*/
                        }

                    }
                }
            }
        }

        fun updateList(sourcesList: MutableList<String>?) {
            this.sourcesList = sourcesList
            notifyDataSetChanged()
        }
    }

    private fun openGhome() {
        val gHomeIntent = packageManager.getLaunchIntentForPackage("com.google.android.apps.chromecast.app")
        if (gHomeIntent != null) {
            startActivity(gHomeIntent)
        } else {
            showAppNotInstalledAlertDialog()
        }
    }
    private fun showAppNotInstalledAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.app_not_installed))
        builder.setMessage(getString(R.string.google_home_install))
        builder.setPositiveButton(getString(R.string.yes_str)) { dialog, which -> //Navigate to Play Store
            val uri = Uri.parse(getString(R.string.ghome_link))
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        builder.show()
    }

    private fun openTidal() {
        if (!LaunchApp(this, "com.aspiro.tidal")) {
            launchPlayStoreWithAppPackage(this, "com.aspiro.tidal")
        }

    }
    private fun launchPlayStoreWithAppPackage(context: Context, packageName: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
        context.startActivity(i)
    }
    private fun LaunchApp(context: Context, packageName: String?): Boolean {
        val manager = context.packageManager
        return try {
            val i = manager.getLaunchIntentForPackage(packageName!!) ?: return false
            i.addCategory(Intent.CATEGORY_LAUNCHER)
            context.startActivity(i)
            true
        } catch (e: ActivityNotFoundException) {
            false
        }
    }

    private fun openLocalDMS() {
        val localIntent = Intent(this@CTMediaSourcesActivity, CTLocalDMSActivity::class.java)
        localIntent.putExtra(Constants.CURRENT_DEVICE_IP, currentIpAddress)
        startActivity(localIntent)
    }

    override fun tunnelDataReceived(tunnelingData: TunnelingData) {
        super.tunnelDataReceived(tunnelingData)
        if (tunnelingData.remoteClientIp == currentIpAddress && tunnelingData.remoteMessage.size >= 24) {
            val tcpTunnelPacket = TCPTunnelPacket(tunnelingData.remoteMessage)

            val sceneObject = mScanHandler.getSceneObjectFromCentralRepo(currentIpAddress)
            binding.seekBarVolume.progress = sceneObject?.volumeValueInPercentage!!

            if (binding.seekBarVolume.progress == 0) {
                binding.ivVolumeMute.setImageResource(R.drawable.ic_volume_mute)
            } else binding.ivVolumeMute.setImageResource(R.drawable.volume_low_enabled)

            when {
                tcpTunnelPacket.currentSource == Constants.BT_SOURCE -> {
                    binding.ivToggleBluetooth.isChecked = true
                    //suma source close loader
                    closeLoader()
                    if (timeOutHandler?.hasMessages(AUX_BT_TIMEOUT)!!) timeOutHandler.removeMessages(AUX_BT_TIMEOUT)
                }
                tcpTunnelPacket.currentSource == Constants.AUX_SOURCE -> {
                    binding.ivToggleAux.isChecked = true
                    closeLoader()
                    //suma source close loader
                    if (timeOutHandler?.hasMessages(AUX_BT_TIMEOUT)!!) timeOutHandler.removeMessages(AUX_BT_TIMEOUT)
                }
                tcpTunnelPacket.currentSource == Constants.TUNNELING_WIFI_SOURCE -> {
                    binding.ivToggleAux.isChecked = false
                    binding.ivToggleBluetooth.isChecked = false
                    //suma source close loader
                     closeLoader()
                    if (timeOutHandler?.hasMessages(AUX_BT_TIMEOUT)!!) timeOutHandler.removeMessages(AUX_BT_TIMEOUT)
                }
            }
        }
    }

    override fun storagePermissionAvailable() {
        super.storagePermissionAvailable()
        openLocalDMS()
    }

    /** Handling volume changes from phone volume hardware buttons
     * Called only when button is pressed, not when released**/
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val volumeControl = LUCIControl(currentIpAddress)
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                if (binding.seekBarVolume.progress > 85) {
                    volumeControl.SendCommand(MIDCONST./*ZONE_VOLUME*/VOLUME_CONTROL, "" + 100, LSSDPCONST.LUCI_SET)
                } else {
                    volumeControl.SendCommand(MIDCONST./*ZONE_VOLUME*/VOLUME_CONTROL, "" + (binding.seekBarVolume.progress + 5), LSSDPCONST.LUCI_SET)
                }
                return true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                if (binding.seekBarVolume.progress < 15) {
                    volumeControl.SendCommand(MIDCONST./*ZONE_VOLUME*/VOLUME_CONTROL, "" + 0, LSSDPCONST.LUCI_SET)
                } else {
                    volumeControl.SendCommand(MIDCONST./*ZONE_VOLUME*/VOLUME_CONTROL, "" + (binding.seekBarVolume.progress - 5), LSSDPCONST.LUCI_SET)
                }
                return true
            }
            else -> return super.onKeyDown(keyCode, event)
        }
    }
}
