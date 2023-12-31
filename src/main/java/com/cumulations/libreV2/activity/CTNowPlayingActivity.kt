package com.cumulations.libreV2.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.KeyEvent
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import com.cumulations.libreV2.AppUtils
import com.cumulations.libreV2.AppUtils.setPlayPauseLoader
import com.cumulations.libreV2.BlurTransformation
import com.cumulations.libreV2.model.SceneObject
import com.cumulations.libreV2.model.SceneObject.CURRENTLY_BUFFERING
import com.cumulations.libreV2.model.SceneObject.CURRENTLY_PAUSED
import com.cumulations.libreV2.model.SceneObject.CURRENTLY_PLAYING
import com.cumulations.libreV2.model.SceneObject.CURRENTLY_STOPPED
import com.cumulations.libreV2.tcp_tunneling.TCPTunnelPacket
import com.cumulations.libreV2.tcp_tunneling.TunnelingData
import com.libreAlexa.LErrorHandeling.LibreError
import com.libreAlexa.LibreApplication
import com.libreAlexa.LibreApplication.isUSBBackPressed
import com.libreAlexa.R
import com.libreAlexa.Scanning.ScanningHandler
import com.libreAlexa.app.dlna.dmc.processor.interfaces.DMRProcessor
import com.libreAlexa.app.dlna.dmc.utility.UpnpDeviceManager
import com.libreAlexa.constants.Constants
import com.libreAlexa.constants.Constants.*
import com.libreAlexa.constants.LSSDPCONST
import com.libreAlexa.constants.LUCIMESSAGES
import com.libreAlexa.constants.MIDCONST
import com.libreAlexa.databinding.CtAcitvityNowPlayingBinding
import com.libreAlexa.luci.LSSDPNodeDB
import com.libreAlexa.luci.LSSDPNodes
import com.libreAlexa.luci.LUCIControl
import com.libreAlexa.luci.LUCIPacket
import com.libreAlexa.netty.BusProvider
import com.libreAlexa.netty.LibreDeviceInteractionListner
import com.libreAlexa.netty.NettyData
import com.libreAlexa.util.LibreLogger
import com.libreAlexa.util.PicassoTrustCertificates
import org.fourthline.cling.model.ModelUtil
import org.fourthline.cling.model.message.UpnpResponse
import org.fourthline.cling.model.meta.Action
import org.fourthline.cling.model.meta.RemoteDevice
import org.json.JSONObject


/**
 * Created by AMit on 13/5/2019.
 */

class CTNowPlayingActivity : CTDeviceDiscoveryActivity(), View.OnClickListener,
    LibreDeviceInteractionListner, DMRProcessor.DMRProcessorListener {

    companion object {
        const val REPEAT_OFF = 0
        const val REPEAT_ONE = 1
        const val REPEAT_ALL = 2
        const val PLAYBACK_TIMER_TIMEOUT = 5000
    }

    private var currentSceneObject: SceneObject? = null
    private val currentIpAddress: String? by lazy {
        intent?.getStringExtra(CURRENT_DEVICE_IP)
    }
    private var isLocalDMRPlayback = false

    /* Added to handle the case where trackname is empty string"*/
    private var currentDeviceNode: LSSDPNodes? = null
    private var currentTrackName = "-1"
    private var isStillPlaying = false
    private var is49MsgBoxReceived = false
    private var durationInSeeconds = 0.0f
    private var durationInSeeconds1 = 0.0f
    private var mScanHandler: ScanningHandler? = null
    private var remoteDevice: RemoteDevice? = null
    private lateinit var binding: CtAcitvityNowPlayingBinding
    private val startPlaybackTimerhandler = Handler(Looper.getMainLooper())
    private val TAG = CTNowPlayingActivity::class.java.simpleName
    private val META_DATA = "-->SHAIKCT:"
    private val startPlaybackTimerRunnable = object : Runnable {
        override fun run() {
            if (isStillPlaying) {
                if (is49MsgBoxReceived) {
                    // Do nothing, set is49MsgBoxReceived to false so that for next 49 msg box received
                    // this value gets updated
                    is49MsgBoxReceived = false
                    /*this is hack to check again if is49MsgBoxReceived is still false after making it false
                     * which will confirm no 49 msg box received after total 10 seconds*/
                    startPlaybackTimerhandler.postDelayed(this, PLAYBACK_TIMER_TIMEOUT.toLong())
                } else {

                    if (!isFinishing) {
                        disablePlayback()
                    }
                    /*Hack : send any luci command to check device still alive or not when user pauses and stays on same
                     * screen and does nothing*/
                    LUCIControl(currentIpAddress).SendCommand(MIDCONST.MID_CURRENT_SOURCE.toInt(), null, LSSDPCONST.LUCI_GET)
                }
            }
        }
    }


    internal var showLoaderHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {

            when (msg.what) {
                PREPARATION_INITIATED -> {
                    showLoader(true)
                    /*disabling views while initiating */
                    preparingToPlay(false)
                }

                PREPARATION_COMPLETED -> {
                    showLoader(false)
                    /*enabling views while initiating */
                    preparingToPlay(true)
                }

                PREPARATION_TIMEOUT_CONST -> {
                    preparingToPlay(true)
                    showLoader(false)
                }

                ALEXA_NEXT_PREV_HANDLER -> dismissDialog()
            }
        }

    }

    @SuppressLint("HandlerLeak")
    private val timeOutHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {

            if (msg.what == CTMediaSourcesActivity.NETWORK_TIMEOUT) {
                LibreLogger.d(TAG, "recieved handler msg")
                // closeLoader()
                /*showing error to user*/
                val error = LibreError(currentIpAddress, getString(R.string.requestTimeout))
                showErrorMessage(error)
            }

            if (msg.what == CTMediaSourcesActivity.ACTION_INITIATED) {
                //  showLoader()
            }

            if (msg.what == CTMediaSourcesActivity.BT_AUX_INITIATED) {
                val showMessage = msg.data.getString("MessageText")
                //showLoaderAndAskSource(showMessage)
            }
            if (msg.what == CTMediaSourcesActivity.AUX_BT_TIMEOUT) {
                // closeLoader()
                val error = LibreError(currentIpAddress, getString(R.string.requestTimeout))
                showErrorMessage(error)
            }
            if (msg.what == CTMediaSourcesActivity.ALEXA_REFRESH_TOKEN_TIMER) {
                //closeLoader()
            }
        }
    }

    private fun disablePlayback() {
        /*make player status to paused, ie show play icon*/
        binding.ivPlayPause.setImageResource(R.drawable.play_white)
        binding.ivNext.setImageResource(R.drawable.next_disabled)
        binding.ivPrevious.setImageResource(R.drawable.prev_disabled)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CtAcitvityNowPlayingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mScanHandler = ScanningHandler.getInstance()
        getTheRenderer(currentIpAddress)
        remoteDevice = UpnpDeviceManager.getInstance().getRemoteDMRDeviceByIp(currentIpAddress)
        onRemoteDeviceAdded(remoteDevice)
        if (Build.VERSION.SDK_INT >= 33) {
            //Android 13 and Above
            onBackInvokedDispatcher.registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT) {
                LibreLogger.d(TAG,"isUSBBackPressed $isUSBBackPressed")
                if(isUSBBackPressed) {
                    isUSBBackPressed=false
                    exitOnBackPressed()
                }else{
                    finish()
                }
            }
        } else {
            //Android 12 and below
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    LibreLogger.d(TAG,"isUSBBackPressed $isUSBBackPressed")
                    if(isUSBBackPressed) {
                        isUSBBackPressed=false
                        exitOnBackPressed()
                    }else{
                        finish()
                    }
                }
            })
        }
    }

    override fun onStart() {
        super.onStart()
        initViews()
        if (currentIpAddress != null) {
            currentSceneObject = ScanningHandler.getInstance().getSceneObjectFromCentralRepo(currentIpAddress) // ActiveSceneAdapter.mMasterSpecificSlaveAndFreeDeviceMap.get(currentIpAddress);
            if (currentSceneObject == null) {
                LibreLogger.d("NetworkChanged", "NowPlayingFragment onCreateView")
            }
        }
    }
    private fun exitOnBackPressed() {
        if(currentIpAddress!=null) {
            val luciControl = LUCIControl(currentIpAddress)
            luciControl.SendCommand(MIDCONST.MID_REMOTE_UI.toInt(), LUCIMESSAGES.BACK, LSSDPCONST.LUCI_SET)
        }else{
            intentToHome(this)
        }
        finish()
    }
    private fun gotoSourcesOption(ipaddress: String?, currentSource: Int) {
        val error = LibreError("", resources.getString(R.string.no_active_playlist), 1)
        BusProvider.getInstance().post(error)
        startActivity(Intent(this, CTMediaSourcesActivity::class.java).apply {
            putExtra(CURRENT_DEVICE_IP, ipaddress)
            putExtra(CURRENT_SOURCE, "" + currentSource)
            putExtra(FROM_ACTIVITY, CTNowPlayingActivity::class.java.simpleName)
        })
    }

    private fun isLocalDMR(sceneObject: SceneObject): Boolean {
        val renderingDevice = UpnpDeviceManager.getInstance().getRemoteDMRDeviceByIp(sceneObject.ipAddress)
        if (renderingDevice != null) {
            val renderingUDN = renderingDevice.identity.udn.toString()
            val playbackHelper = LibreApplication.PLAYBACK_HELPER_MAP[renderingUDN]
            val mIsDmrPlaying = ScanningHandler.getInstance().ToBeNeededToLaunchSourceScreen(sceneObject)
            LibreLogger.d(TAG, " local DMR is playing$mIsDmrPlaying")
            return !mIsDmrPlaying
        }
        return false
    }

    private fun isActivePlayListNotAvailable(sceneObject: SceneObject?): Boolean {
        return sceneObject != null
                && sceneObject.currentSource == NO_SOURCE
                || (sceneObject!!.currentSource == DMR_SOURCE
                && ScanningHandler.getInstance().ToBeNeededToLaunchSourceScreen(sceneObject))
    }

    /*initialize variables here*/
    private fun initViews() {
        showLoader(false)

        binding.ivPlayPause.setOnClickListener(this)
        binding.ivPrevious.setOnClickListener(this)
        binding.ivNext.setOnClickListener(this)
        binding.ivShuffle.setOnClickListener(this)
        binding.ivRepeat.setOnClickListener(this)
        binding.mediaBtnSkipNext.setOnClickListener(this)
        binding.mediaBtnSkipPrev.setOnClickListener(this)
        binding.ivAlbumArt.setOnClickListener(this)
        binding.ivVolumeDown.setOnClickListener(this)

        binding.ivBack.setOnClickListener {
            LibreLogger.d(TAG,"isUSBBackPressed $isUSBBackPressed")
            if(isUSBBackPressed) {
                isUSBBackPressed=false
                exitOnBackPressed()
            }else{
              finish()
            }
        }
        val mNode = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(currentIpAddress)
        if(mNode!=null && mNode.getmDeviceCap()!=null && mNode.getmDeviceCap().getmSource().isAlexaAvsSource) {
            binding.ivAlexaAccount.visibility=View.VISIBLE
            //SUMA SATURDAY
        }
        else{
            binding.ivAlexaAccount.visibility=View.GONE

        }
        binding.ivAlexaAccount.setOnClickListener {
            if (mNode?.alexaRefreshToken?.isEmpty()!!) {
                startActivity(Intent(this@CTNowPlayingActivity, CTAmazonLoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra(CURRENT_DEVICE_IP, currentIpAddress)
                    putExtra(FROM_ACTIVITY, CTNowPlayingActivity::class.java.simpleName)
                })
            } else {
                startActivity(Intent(this@CTNowPlayingActivity, CTAlexaThingsToTryActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra(CURRENT_DEVICE_IP, currentIpAddress)
                    putExtra(FROM_ACTIVITY, CTNowPlayingActivity::class.java.simpleName)
                })
            }
        }

        binding.seekBarVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {

                if (!doVolumeChange(seekBar.progress))
                    showToast(R.string.actionFailed)
            }
        })

        binding.seekBarSong.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                LibreLogger.d(TAG, "Bhargav SEEK:Seekbar Position track " + seekBar.progress + "" + seekBar.max)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                remoteDevice = UpnpDeviceManager.getInstance().getRemoteDMRDeviceByIp(currentIpAddress)

                val theRenderer = getTheRenderer(currentIpAddress)
                LibreLogger.d(TAG, " Suma SEEK:Seekbar Position trackstop $isLocalDMRPlayback " + "Renderer $theRenderer")

                if (isLocalDMRPlayback && theRenderer == null) {
                    showToast(R.string.NoRenderer)
                } else if (!doSeek(seekBar.progress)) {
                    if (currentSceneObject != null) {
                        val error = LibreError(currentSceneObject!!.ipAddress, resources.getString(R.string.RENDERER_NOT_PRESENT))
                        BusProvider.getInstance().post(error)
                    }
                }

            }
        })
        currentDeviceNode = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(currentIpAddress)
        binding.tvDeviceName.text = currentDeviceNode?.friendlyname
    }

    private fun showLoader(show: Boolean) {
        if (show)
            binding.loader.visibility = View.VISIBLE
        else
            binding.loader.visibility = View.GONE
    }

    /*this method will take care of views while we are preparing to play*/
    private fun preparingToPlay(value: Boolean) {

        if (value) {
            setTheSourceIconFromCurrentSceneObject(1)
        } else {
            enableViews(value)
        }
    }

    private fun disableViews(currentSrc: Int, message: String?, i: Int) {

        /* Dont have to call this function explicitly make use of setrceIco */
        LibreLogger.d(META_DATA,"disableViews called $i and $currentSrc")
        /** For Aux we are hiding the playControls So we are making visible here
         * and gone in AUX Source. and While AUX Playing Source is registering as
         * EXTERNAL SOURCE
         */
        binding.layPlayControls.visibility=View.VISIBLE
        when (currentSrc) {

            NO_SOURCE,
            DDMSSLAVE_SOURCE -> {
                /*setting seek to zero*/
                binding.seekBarSong.progress = 0
                binding.seekBarSong.isEnabled = false
                binding.seekBarSong.isClickable = false
                binding.tvCurrentDuration.text = "0:00"
                binding.tvTotalDuration.text = "0:00"
                binding.ivPlayPause.isClickable = false
                binding.ivPlayPause.isEnabled = false

//                artistName!!.text = ""
                binding.tvAlbumName.text = ""
                binding.tvTrackName.text = ""
            }

            AUX_SOURCE, EXTERNAL_SOURCE -> {

                binding.seekBarSong.progress = 0
                binding.seekBarSong.isEnabled = false
                binding.seekBarSong.isClickable = false
                binding.tvCurrentDuration.text = "0:00"
                binding.tvTotalDuration.text = "0:00"
                binding.layPlayControls.visibility=View.GONE
                //For Aux instead of disable the icons  making visibility gone
/*              binding.ivPlayPause.setImageResource(R.drawable.play_white)
                binding.ivNext.setImageResource(R.drawable.next_disabled)
                binding.ivPrevious.setImageResource(R.drawable.prev_disabled)*/
                binding.tvAlbumName.text = ""
                binding.tvTrackName.text = message
                LibreLogger.d(TAG, "we set the song name to empty for disabling " + currentSceneObject!!.trackName + " in disabling view where artist name is " + message)

            }

            DEEZER_SOURCE -> {
                if (message != null && message.contains(DEZER_RADIO)) {
                    binding.ivPrevious.isEnabled = false
                    binding.ivPrevious.isClickable = false
                }
            }

            ALEXA_SOURCE -> {
                binding.ilMusic.visibility = View.VISIBLE
                binding.seekBarSong.isClickable = false
                binding.ivPlayPause.setImageResource(R.drawable.play_orange)
                binding.ivNext.setImageResource(R.drawable.next_enabled)
                binding.ivPrevious.setImageResource(R.drawable.prev_enabled)
                if (currentSceneObject != null && !currentSceneObject?.genre.isNullOrEmpty() && !currentSceneObject!!.genre.equals("null", ignoreCase = true))
                    binding.tvAlbumName.text = "${binding.tvAlbumName.text}, ${currentSceneObject?.genre}"
            }
        }


    }

    private fun enableViews(enable: Boolean) {
        binding.seekBarSong.isEnabled = enable
        binding.seekBarSong.isClickable = enable
        binding.ivPlayPause.isClickable = enable
        binding.ivPlayPause.isEnabled = enable
        binding.ivPrevious.isClickable = enable
        binding.ivPrevious.isEnabled = enable
        binding.ivNext.isEnabled = enable
        binding.ivNext.isClickable = enable
        binding.seekBarVolume.isEnabled = enable
        binding.seekBarVolume.isClickable = enable
        binding.layPlayControls.visibility=View.VISIBLE
    }

    private fun playPauseNextPrevAllowed(): Boolean {
        val mNodeWeGotForControl = mScanHandler!!.getLSSDPNodeFromCentralDB(currentIpAddress)
        return (currentSceneObject!!.currentSource != AUX_SOURCE
                /*&& currentSceneObject!!.currentSource != EXTERNAL_SOURCE*/
                && currentSceneObject!!.currentSource != VTUNER_SOURCE
                && currentSceneObject!!.currentSource != TUNEIN_SOURCE
                && currentSceneObject!!.currentSource != NO_SOURCE
                /* && (currentSceneObject!!.currentSource != BT_SOURCE*/
                || (mNodeWeGotForControl.getgCastVerision() != null
                && mNodeWeGotForControl.bT_CONTROLLER != SceneObject.CURRENTLY_NOTPLAYING
                && mNodeWeGotForControl.bT_CONTROLLER != CURRENTLY_PLAYING)
                || (mNodeWeGotForControl.getgCastVerision() == null
                && mNodeWeGotForControl.bT_CONTROLLER >= CURRENTLY_PAUSED))
    }

    override fun onExceptionHappend(actionCallback: Action<*>, mTitle: String, cause: String) {
        LibreLogger.d(TAG, "Exception Happend for the Title $mTitle for the cause of $cause")
    }

    override fun onClick(view: View) {
        /*stopMediaServer any binding.ivPrevious started playback handler started by removing them*/
        startPlaybackTimerhandler.removeCallbacks(startPlaybackTimerRunnable)

        if (currentSceneObject == null) {
            showToast(R.string.Devicenotplaying)
        } else {
//            isLocalDMRPlayback = currentSceneObject!!.currentSource == DMR_SOURCE

            when (view.id) {
                R.id.iv_playPause -> {
                    if (!playPauseNextPrevAllowed()) {
                        val error = LibreError("", resources.getString(R.string.PLAY_PAUSE_NOT_ALLOWED), 1)
                        BusProvider.getInstance().post(error)
                        return
                    }
                    if (AppUtils.isActivePlaylistNotAvailable(currentSceneObject)) {
                        LibreLogger.d(TAG, "currently not playing, so take user to sources optionactivity")
                        gotoSourcesOption(currentSceneObject!!.ipAddress, currentSceneObject!!.currentSource)
                        return
                    }

                    val control = LUCIControl(currentSceneObject!!.ipAddress)
                    if (currentSceneObject!!.playstatus == CURRENTLY_PLAYING) {
                        control.SendCommand(MIDCONST.MID_PLAYCONTROL.toInt(), LUCIMESSAGES.PAUSE, LSSDPCONST.LUCI_SET)
                        isStillPlaying = false
                    } else {
                        isStillPlaying = true
                        if (currentSceneObject!!.currentSource == BT_SOURCE) {
                            control.SendCommand(MIDCONST.MID_PLAYCONTROL.toInt(), LUCIMESSAGES.PLAY, LSSDPCONST.LUCI_SET)
                        } else {
                            control.SendCommand(MIDCONST.MID_PLAYCONTROL.toInt(), LUCIMESSAGES.RESUME, LSSDPCONST.LUCI_SET)
                        }
                    }

                    if (currentSceneObject!!.currentSource != BT_SOURCE
                        && currentSceneObject!!.currentSource != 15
                        && currentSceneObject!!.currentSource != GCAST_SOURCE
                        && currentSceneObject!!.currentSource != ALEXA_SOURCE) {
                        showLoaderHandler.sendEmptyMessageDelayed(PREPARATION_TIMEOUT_CONST, PREPARATION_TIMEOUT.toLong())
                        showLoaderHandler.sendEmptyMessage(PREPARATION_INITIATED)
                    }
                }

                R.id.iv_previous -> {
                    LibreLogger.d(TAG, "currently not playing, so take user to sources option " +
                            "activity" + currentSceneObject!!.previousControl)

                    if (currentSceneObject!!.previousControl == true && currentSceneObject!!.currentSource == SPOTIFY_SOURCE) {
                        if (!playPauseNextPrevAllowed()) {
                            val error = LibreError("", resources.getString(R.string.NEXT_PREVIOUS_NOT_ALLOWED), 1)
                            BusProvider.getInstance().post(error)
                            return
                        }

                        if (isActivePlayListNotAvailable(currentSceneObject)) {
                            LibreLogger.d(TAG, "currently not playing, so take user to sources option activity")
                            gotoSourcesOption(currentIpAddress, currentSceneObject!!.currentSource)
                            return
                        }
                        if (binding.ivPrevious.isEnabled) {
                            doNextPrevious(false)
                        } else {
                            val error = LibreError(currentIpAddress, getString(R.string.requestTimeout))
                            BusProvider.getInstance().post(error)
                        }
                    } else {
                        if (currentSceneObject!!.currentSource != SPOTIFY_SOURCE) {
                            if (!playPauseNextPrevAllowed()) {
                                val error = LibreError("", resources.getString(R.string.NEXT_PREVIOUS_NOT_ALLOWED), 1)
                                BusProvider.getInstance().post(error)
                                return
                            }

                            if (isActivePlayListNotAvailable(currentSceneObject)) {
                                LibreLogger.d(TAG, "currently not playing, so take user to sources option activity")
                                gotoSourcesOption(currentIpAddress, currentSceneObject!!.currentSource)
                                return
                            }
                            if (binding.ivPrevious.isEnabled) {
                                doNextPrevious(false)
                            } else {
                                val error = LibreError(currentIpAddress, getString(R.string.requestTimeout))
                                BusProvider.getInstance().post(error)
                            }
                        }
                    }
                }

                R.id.iv_next -> {
                    if (currentSceneObject!!.nextControl == true && currentSceneObject!!.currentSource == SPOTIFY_SOURCE) {
                        if (!playPauseNextPrevAllowed()) {
                            val error = LibreError("", resources.getString(R.string.NEXT_PREVIOUS_NOT_ALLOWED), 1)
                            BusProvider.getInstance().post(error)
                            return
                        }

                        if (isActivePlayListNotAvailable(currentSceneObject)) {
                            LibreLogger.d(TAG, "currently not playing, so take user to sourcesoptionactivity")
                            gotoSourcesOption(currentIpAddress, currentSceneObject!!.currentSource)
                            return
                        }
                        if (binding.ivNext.isEnabled) {
                            LibreLogger.d(TAG, "bhargav12")
                            doNextPrevious(true)
                        } else {
                            LibreLogger.d(TAG, "bhargav1else")
                            val error = LibreError(currentIpAddress, getString(R.string.requestTimeout))
                            BusProvider.getInstance().post(error)
                        }
                    } else {
                        if (currentSceneObject!!.currentSource != SPOTIFY_SOURCE) {
                            if (!playPauseNextPrevAllowed()) {
                                val error = LibreError("", resources.getString(R.string.NEXT_PREVIOUS_NOT_ALLOWED), 1)
                                BusProvider.getInstance().post(error)
                                return
                            }

                            if (isActivePlayListNotAvailable(currentSceneObject)) {
                                LibreLogger.d(TAG, "currently not playing, so take user to sourcesoptionactivity")
                                gotoSourcesOption(currentIpAddress, currentSceneObject!!.currentSource)
                                return
                            }
                            if (binding.ivNext.isEnabled) {
                                LibreLogger.d(TAG, "suma12")
                                doNextPrevious(true)
                            } else {
                                LibreLogger.d(TAG, "suma1else")
                                val error = LibreError(currentIpAddress, getString(R.string.requestTimeout))
                                BusProvider.getInstance().post(error)
                            }
                        }
                    }

                }

                R.id.media_btn_skip_prev -> {
                    var duration = currentSceneObject!!.currentPlaybackSeekPosition
                    LibreLogger.d(TAG, "suma in podcast previous")
                    durationInSeeconds = duration / 1000
                    if (durationInSeeconds < 15) {
                        duration = 0f
                        binding.seekBarSong.progress = 0
                        binding.tvCurrentDuration.text = "0:00"
                        binding.tvCurrentDuration.text = "0:00"
                        LibreLogger.d(TAG, "suma in skip less than 5 sec$durationInSeeconds")
                    } else {
                        durationInSeeconds = durationInSeeconds - 15
                        duration = durationInSeeconds * 1000
                        LibreLogger.d("pixel", "suma in skip more than 5 sec$durationInSeeconds")

                    }
                    val control = LUCIControl(currentSceneObject!!.ipAddress)
                    control.SendCommand(40, "SEEK:$duration", LSSDPCONST.LUCI_SET)
                }

                R.id.media_btn_skip_next -> {
                    var duration1 = currentSceneObject!!.currentPlaybackSeekPosition
                    LibreLogger.d(TAG, "suma in podcast next")
                    durationInSeeconds1 = duration1 / 1000
                    durationInSeeconds1 = durationInSeeconds1 + 15
                    duration1 = durationInSeeconds1 * 1000
                    val control = LUCIControl(currentSceneObject!!.ipAddress)
                    control.SendCommand(40, "SEEK:$duration1", LSSDPCONST.LUCI_SET)
                }

                R.id.iv_album_art -> {
                    if (currentSceneObject!!.currentSource == SPOTIFY_SOURCE) {
                        val appPackageName = "com.spotify.music"
                        launchTheApp(appPackageName)
                    }
                }

                R.id.iv_shuffle ->

                    /*this is added to support shuffle and repeat option for Local Content*/
                    if (isLocalDMRPlayback) {
                        val renderingDevice = UpnpDeviceManager.getInstance().getRemoteDMRDeviceByIp(currentSceneObject!!.ipAddress)
                        if (renderingDevice != null) {
                            val renderingUDN = renderingDevice.identity.udn.toString()
                            val playbackHelper = LibreApplication.PLAYBACK_HELPER_MAP[renderingUDN]
                            if (playbackHelper != null) {
                                if (currentSceneObject!!.shuffleState == 0) {
                                    /*which means shuffle is off hence making it on*/
                                    playbackHelper.setIsShuffleOn(true)
                                    currentSceneObject!!.shuffleState = 1
                                } else {
                                    /*which means shuffle is on hence making it off*/
                                    currentSceneObject!!.shuffleState = 0
                                    playbackHelper.setIsShuffleOn(false)
                                }
                                /*inserting to central repo*/
                                mScanHandler!!.putSceneObjectToCentralRepo(currentIpAddress, currentSceneObject)
                                setViews()
                            }
                        }
                    } else {
                        val luciControl = LUCIControl(currentSceneObject!!.ipAddress)
                        if (currentSceneObject!!.shuffleState == 0) {
                            /*which means shuffle is off*/
                            luciControl.SendCommand(MIDCONST.MID_PLAYCONTROL.toInt(), "SHUFFLE:ON", LSSDPCONST.LUCI_SET)
                        } else
                            luciControl.SendCommand(MIDCONST.MID_PLAYCONTROL.toInt(), "SHUFFLE:OFF", LSSDPCONST.LUCI_SET)
                    }


                R.id.iv_repeat ->
                    /*this is added to support shuffle and repeat option for Local Content*/
                    if (isLocalDMRPlayback) {
                        LibreLogger.d("====", "if")
                        val renderingDevice = UpnpDeviceManager.getInstance().getRemoteDMRDeviceByIp(currentSceneObject!!.ipAddress)
                        if (renderingDevice != null) {
                            val renderingUDN = renderingDevice.identity.udn.toString()
                            val playbackHelper = LibreApplication.PLAYBACK_HELPER_MAP[renderingUDN]
                            if (playbackHelper != null) {
                                when {
                                    currentSceneObject!!.repeatState == REPEAT_ALL -> {
                                        playbackHelper.repeatState = REPEAT_OFF
                                        currentSceneObject!!.repeatState = REPEAT_OFF
                                    }

                                    currentSceneObject!!.repeatState == REPEAT_OFF -> {
                                        playbackHelper.repeatState = REPEAT_ONE
                                        currentSceneObject!!.repeatState = REPEAT_ONE
                                    }

                                    currentSceneObject!!.repeatState == REPEAT_ONE -> {
                                        playbackHelper.repeatState = REPEAT_ALL
                                        currentSceneObject!!.repeatState = REPEAT_ALL
                                    }

                                    /*inserting to central repo*/
                                }
                                /*inserting to central repo*/
                                mScanHandler!!.putSceneObjectToCentralRepo(currentIpAddress, currentSceneObject)
                                setViews()
                            }
                        }
                    } else {
                        /**/
                        LibreLogger.d("====", "else " + currentSceneObject!!.repeatState)
                        val shuffleLuciControl = LUCIControl(currentSceneObject!!.ipAddress)
                        when {
                            currentSceneObject!!.repeatState == REPEAT_ALL -> {
                                shuffleLuciControl.SendCommand(MIDCONST.MID_PLAYCONTROL.toInt(), "REPEAT:OFF", LSSDPCONST.LUCI_SET)
                                binding.ivRepeat.setImageResource(R.drawable.repeat_disabled)
                                currentSceneObject!!.repeatState = REPEAT_OFF
                            }

                            currentSceneObject!!.repeatState == REPEAT_OFF /*&& currentSceneObject!!.currentSource != SPOTIFY_SOURCE*/ -> {
                                shuffleLuciControl.SendCommand(MIDCONST.MID_PLAYCONTROL.toInt(), "REPEAT:ONE", LSSDPCONST.LUCI_SET)
                                currentSceneObject!!.repeatState = REPEAT_ONE
                            }

                            currentSceneObject!!.repeatState == REPEAT_OFF/* && currentSceneObject!!.currentSource == SPOTIFY_SOURCE */ -> {
                                shuffleLuciControl.SendCommand(MIDCONST.MID_PLAYCONTROL.toInt(), "REPEAT:ALL", LSSDPCONST.LUCI_SET)
                                currentSceneObject!!.repeatState = REPEAT_ALL
                            }

                            currentSceneObject!!.repeatState == REPEAT_ONE -> {
                                shuffleLuciControl.SendCommand(MIDCONST.MID_PLAYCONTROL.toInt(), "REPEAT:ALL", LSSDPCONST.LUCI_SET)
                                currentSceneObject!!.repeatState = REPEAT_ALL
                            }
                            /* If the current source is spotify  *///Not equal to spotify
                        }/* If the current source is spotify  *///Not equal to spotify
                        setViews()
                        LibreLogger.d("====", "else setViews called")
                    }

                R.id.iv_volume_down -> {
                    if (currentSceneObject != null && currentSceneObject?.mute_status != null) {
                        if (currentSceneObject?.mute_status == LUCIMESSAGES.UNMUTE) {
                            LUCIControl.SendCommandWithIp(MIDCONST.MID_PLAYCONTROL.toInt(), LUCIMESSAGES.MUTE, LSSDPCONST.LUCI_SET, currentSceneObject!!.ipAddress)
                        } else {
                            LUCIControl.SendCommandWithIp(MIDCONST.MID_PLAYCONTROL.toInt(), LUCIMESSAGES.UNMUTE, LSSDPCONST.LUCI_SET, currentSceneObject!!.ipAddress)
                        }
                    } else {
                        LibreLogger.d(TAG, "currentSceneObject or mute status null")
                    }
                }
            }
        }
    }

    override fun onRemoteDeviceAdded(remoteDevice: RemoteDevice?) {
        super.onRemoteDeviceAdded(remoteDevice)
        val ip = remoteDevice?.identity?.descriptorURL?.host
        LibreLogger.d(TAG, "Remote remoteDevice with added with ip $ip")
        if (ip.equals(currentIpAddress!!, ignoreCase = true)) {
//            if (selectedDIDLObject != null) {
//                /* This is a case where user has selected a DMS source but that time  rendering remoteDevice was null and hence we play with the storedPlayer object*/
//                runOnUiThread {
//                    val handler = Handler()
//                    handler.postDelayed({
//                        dismissDialog()
//                        play(selectedDIDLObject!!)
//                    }, 2000)
//                }
//            }
        }
    }


    override fun onResume() {
        super.onResume()
        registerForDeviceEvents(this)
        getTheRenderer(currentIpAddress)
        remoteDevice = UpnpDeviceManager.getInstance().getRemoteDMRDeviceByIp(currentIpAddress)
        LibreLogger.d("UpnpDeviceDiscovery", "udn" + remoteDevice)

        if (upnpProcessor == null) {
            upnpProcessor!!.bindUpnpService()
            upnpProcessor!!.addListener(this)
            upnpProcessor!!.addListener(UpnpDeviceManager.getInstance())
            upnpProcessor!!.renew()
            upnpProcessor!!.getTheRendererFromRegistryIp(currentIpAddress)
            LibreLogger.d("UpnpDeviceDiscovery", "onStart")
        }

        if (currentSceneObject == null)
            return
        isLocalDMRPlayback = AppUtils.isLocalDMRPlaying(currentSceneObject)
        requestLuciUpdates(currentIpAddress!!)
        setViews()
    }

    override fun onStop() {
        super.onStop()
        unRegisterForDeviceEvents()
        showLoaderHandler.removeCallbacksAndMessages(null)
        startPlaybackTimerhandler.removeCallbacksAndMessages(null)
    }

    override fun deviceDiscoveryAfterClearingTheCacheStarted() {

    }

    override fun newDeviceFound(node: LSSDPNodes) {

    }

    override fun deviceGotRemoved(mIpAddress: String) {

    }

    override fun messageRecieved(nettyData: NettyData) {
        LibreLogger.d(META_DATA, " New message appeared for the device " + nettyData.getRemotedeviceIp())
        if (currentSceneObject != null && nettyData.getRemotedeviceIp().equals(currentIpAddress!!, ignoreCase = true)) {
            val packet = LUCIPacket(nettyData.getMessage())
            LibreLogger.d(META_DATA, "Packet is _" + packet.command)

            when (packet.command) {
                MIDCONST.MID_PLAYTIME.toInt() -> {
                    // This message box indicates the current playing status of the scene, information like current seek position*/
                    val message = String(packet.getpayload())
                    if (message.isNotEmpty()) {
                        val longDuration = java.lang.Long.parseLong(message)

                        currentSceneObject!!.currentPlaybackSeekPosition = longDuration.toFloat()

                        /* Setting the current seekbar progress -Start*/
                        val duration = currentSceneObject!!.currentPlaybackSeekPosition
                        binding.seekBarSong.max = currentSceneObject!!.totalTimeOfTheTrack.toInt() / 1000
                        binding.seekBarSong.secondaryProgress = currentSceneObject!!.totalTimeOfTheTrack.toInt() / 1000
                        LibreLogger.d("SEEK", "Duration = " + duration / 1000)
                        binding.seekBarSong.progress = duration.toInt() / 1000

                        /* Setting the current seekbar progress -END*/
                       // setTheSourceIconFromCurrentSceneObject(2)
                        binding.tvCurrentDuration.text = convertMillisToSongTime((duration / 1000).toLong())
                        binding.tvTotalDuration.text = convertMillisToSongTime(currentSceneObject!!.totalTimeOfTheTrack / 1000)

                        LibreLogger.d("Suma SEEK", "Duration = " + duration / 1000)
                        LibreLogger.d("SEEK", "Duration = " + duration / 1000)

                        is49MsgBoxReceived = true
                        if (isStillPlaying) {
                            /*For now we check this only in NowPlaying screen, hence add and remove handler only when fragment is active*/
                            if (!isFinishing) {
                                /*stopMediaServer any binding.ivPrevious handler started by removing them*/
                                startPlaybackTimerhandler.removeCallbacks(startPlaybackTimerRunnable)
                                /*start fresh handler as timer to montior device got removed or 49 not recieved after 5 seconds*/
                                startPlaybackTimerhandler.postDelayed(startPlaybackTimerRunnable, PLAYBACK_TIMER_TIMEOUT.toLong())
                            }
                        }

                        if (mScanHandler!!.isIpAvailableInCentralSceneRepo(currentIpAddress)) {
                            mScanHandler!!.putSceneObjectToCentralRepo(currentIpAddress, currentSceneObject)
                        }
                        LibreLogger.d(TAG, "Nowplaying: Recieved the current Seek position to be" + currentSceneObject!!.currentPlaybackSeekPosition.toInt())
                    }
                }

                MIDCONST.MID_CURRENT_SOURCE.toInt() -> {

                    val message = String(packet.getpayload())
                    try {
                        val source = Integer.parseInt(message)
                        currentSceneObject!!.currentSource = source
                        setTheSourceIconFromCurrentSceneObject(3)
                        if (currentSceneObject!!.currentSource == 14 || currentSceneObject!!.currentSource == 19 || currentSceneObject!!.currentSource == 0 || currentSceneObject!!.currentSource == 12) {
                            binding.ivAlbumArt.setImageResource(R.mipmap.album_art)
                        }
                        LibreLogger.d(META_DATA, "Recieved the current source as  " + currentSceneObject!!.currentSource)

                        if (currentSceneObject!!.currentSource == ALEXA_SOURCE/*alexa*/) {
                            disableViews(currentSceneObject!!.currentSource, "",1)
                        }

                        if (mScanHandler!!.isIpAvailableInCentralSceneRepo(currentIpAddress)) {
                            mScanHandler!!.putSceneObjectToCentralRepo(currentIpAddress, currentSceneObject)
                        }
                        /*Song name, artist name, genre being empty*/
                         setViews()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                MIDCONST.MID_CURRENT_PLAY_STATE.toInt() -> {
                    val message = String(packet.getpayload())
                    LibreLogger.d(META_DATA, "MB : 51, msg = $message")
                    try {
                        val duration = Integer.parseInt(message)
                        currentSceneObject!!.playstatus = duration
                        if (currentSceneObject!!.playstatus == CURRENTLY_PLAYING) {
                            if (currentSceneObject!!.currentSource != MIDCONST.GCAST_SOURCE) {
                                binding.ivPlayPause.setImageResource(R.drawable.pause_orange)
                                binding.ivNext.setImageResource(R.drawable.next_enabled)
                                binding.ivPrevious.setImageResource(R.drawable.prev_enabled)
                            }
                            if (currentSceneObject!!.currentSource != BT_SOURCE) {
                                showLoaderHandler.sendEmptyMessage(PREPARATION_COMPLETED)
                                showLoaderHandler.removeMessages(PREPARATION_TIMEOUT_CONST)
                            }
                            isStillPlaying = true

                        } else {
                            if (currentSceneObject!!.currentSource != MIDCONST.GCAST_SOURCE) {
                                binding.ivPlayPause.setImageResource(R.drawable.play_orange)
                                binding.ivNext.setImageResource(R.drawable.next_disabled)
                                binding.ivPrevious.setImageResource(R.drawable.prev_disabled)

                            }
                            if (currentSceneObject!!.playstatus == CURRENTLY_PAUSED) {
                                isStillPlaying = false
                                /* this case happens only when the user has paused from the App so close the existing loader if any */
                                if (currentSceneObject!!.currentSource != 19) {
                                    showLoaderHandler.sendEmptyMessage(PREPARATION_COMPLETED)
                                    showLoaderHandler.removeMessages(PREPARATION_TIMEOUT_CONST)
                                }

                            }
                            if (currentSceneObject!!.playstatus == SceneObject.CURRENTLY_STOPPED && currentSceneObject!!.currentSource != 0) {

                                if (currentSceneObject!!.currentSource != BT_SOURCE
                                    && currentSceneObject!!.currentSource != 15
                                    && currentSceneObject!!.currentSource != GCAST_SOURCE
                                    && currentSceneObject!!.currentSource != ALEXA_SOURCE) {
                                    showLoaderHandler.sendEmptyMessageDelayed(PREPARATION_TIMEOUT_CONST, PREPARATION_TIMEOUT.toLong())
                                    showLoaderHandler.sendEmptyMessage(PREPARATION_INITIATED)
                                }
                            }
                        }

                        if (mScanHandler!!.isIpAvailableInCentralSceneRepo(currentIpAddress)) {
                            mScanHandler!!.putSceneObjectToCentralRepo(currentIpAddress, currentSceneObject)
                        }
                        setViews()
                       // setTheSourceIconFromCurrentSceneObject(4)
                        LibreLogger.d(TAG, "Stop state is received")
                        LibreLogger.d(TAG, "Recieved the playstate to be" + currentSceneObject!!
                            .playstatus)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                MIDCONST.MID_PLAYCONTROL.toInt() -> {

                    val message = String(packet.getpayload())
                    val ERROR_FAIL = "Error_Fail"
                    val ERROR_NOURL = "Error_NoURL"
                    val ERROR_LASTSONG = "Error_LastSong"
                    LibreLogger.d(META_DATA, "recieved 40 $message")
                    try {
                        when {
                            message.equals(ERROR_FAIL, ignoreCase = true) -> {
                                val error = LibreError(currentSceneObject!!.ipAddress, Constants.ERROR_FAIL)
                                BusProvider.getInstance().post(error)
                            }

                            message.equals(ERROR_NOURL, ignoreCase = true) -> {
                                val error = LibreError(currentSceneObject!!.ipAddress, Constants.ERROR_NOURL)
                                BusProvider.getInstance().post(error)
                            }

                            message.equals(ERROR_LASTSONG, ignoreCase = true) -> {
                                val error = LibreError(currentSceneObject!!.ipAddress, Constants.ERROR_LASTSONG)
                                BusProvider.getInstance().post(error)

                                showLoaderHandler.sendEmptyMessage(PREPARATION_COMPLETED)
                                preparingToPlay(false)
                                showLoaderHandler.removeMessages(PREPARATION_TIMEOUT_CONST)
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                /**For RIVA speakers, during AUX we won't get volume in MB 64 for volume changes
                 * done through speaker hardware buttons**/
                MIDCONST.VOLUME_CONTROL -> {

                    val message = String(packet.getpayload())
                    try {
                        val duration = Integer.parseInt(message)
                        currentSceneObject!!.volumeValueInPercentage = duration
                        LibreApplication.INDIVIDUAL_VOLUME_MAP[nettyData.getRemotedeviceIp()] = duration
                        if (mScanHandler!!.isIpAvailableInCentralSceneRepo(currentIpAddress)) {
                            mScanHandler!!.putSceneObjectToCentralRepo(currentIpAddress, currentSceneObject)
                            binding.seekBarVolume.progress = LibreApplication.INDIVIDUAL_VOLUME_MAP[currentSceneObject?.ipAddress!!]!!
                            if (binding.seekBarVolume.progress == 0) {
                                binding.ivVolumeDown.setImageResource(R.drawable.ic_volume_mute)
                            } else binding.ivVolumeDown.setImageResource(R.drawable.volume_low_enabled)
                        }
                        LibreLogger.d(META_DATA, "Recieved the current volume to be " +
                                currentSceneObject!!
                                    .volumeValueInPercentage)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                MIDCONST.SET_UI -> {
                    try {
                        val message = String(packet.payload)
                        LibreLogger.d(META_DATA, "MB : 42, msg = $message")
                        val root = JSONObject(message)
                        val cmd_id = root.getInt(LUCIMESSAGES.TAG_CMD_ID)
                        val window = root.getJSONObject(LUCIMESSAGES.ALEXA_KEY_WINDOW_CONTENT)
                        LibreLogger.d(TAG, "PLAY JSON is \n= $message\n For ip$currentIpAddress")
                        if (cmd_id == 3) {

                            showLoaderHandler.sendEmptyMessageDelayed(PREPARATION_COMPLETED, 1000)
                            showLoaderHandler.removeMessages(PREPARATION_TIMEOUT_CONST)

                            currentSceneObject = AppUtils.updateSceneObjectWithPlayJsonWindow(window, currentSceneObject!!)

                            if (currentSceneObject!!.currentSource == SPOTIFY_SOURCE) {
                                currentSceneObject!!.seekEnabled = window.getBoolean("Seek")
                                currentSceneObject!!.previousControl = window.getBoolean("Prev")
                                currentSceneObject!!.nextControl = window.getBoolean("Next")
                            }
                            LibreLogger.d(TAG, "suma in 42 MB get ui control seekenabled, previouscontrol," +
                                    "nextControl" + currentSceneObject!!.previousControl)
                            /*if (LibreApplication.LOCAL_IP.isNotEmpty() && currentSceneObject!!.playUrl.contains(LibreApplication.LOCAL_IP))
                                isLocalDMRPlayback = true
                            else if (currentSceneObject!!.currentSource == DMR_SOURCE)
                                isLocalDMRPlayback = true*/

                            if (AppUtils.isLocalDMRPlaying(currentSceneObject)) {
                                isLocalDMRPlayback = true
                            }


                            /*Added for Shuffle and Repeat*/
                            if (isLocalDMRPlayback) {
                                /**this is for local content */
                                val renderingDevice = UpnpDeviceManager.getInstance().getRemoteDMRDeviceByIp(currentSceneObject!!.ipAddress)
                                if (renderingDevice != null) {
                                    val renderingUDN = renderingDevice.identity.udn.toString()
                                    val playbackHelper = LibreApplication.PLAYBACK_HELPER_MAP[renderingUDN]
                                    if (playbackHelper != null) {
                                        LibreLogger.d(TAG, "SET_UI, playbackHelper shuffle, repeat")
                                        /**shuffle is on */
                                        if (playbackHelper.isShuffleOn) {
                                            currentSceneObject!!.shuffleState = 1
                                        } else {
                                            currentSceneObject!!.shuffleState = 0
                                        }
                                        /*setting by default*/
                                        if (playbackHelper.repeatState == REPEAT_ONE) {
                                            currentSceneObject!!.repeatState = REPEAT_ONE
                                        }
                                        if (playbackHelper.repeatState == REPEAT_ALL) {
                                            currentSceneObject!!.repeatState = REPEAT_ALL
                                        }
                                        if (playbackHelper.repeatState == REPEAT_OFF) {
                                            currentSceneObject!!.repeatState = REPEAT_OFF
                                        }

                                    }
                                }
                            } else {
                                /**this check made as we will not get shuffle and repeat state in 42, case of DMR
                                 * so we are updating it locally */
                                LibreLogger.d(TAG, "SET_UI, set 42 MB shuffle, repeat")
                                currentSceneObject!!.shuffleState = window.getInt("Shuffle")
                                currentSceneObject!!.repeatState = window.getInt("Repeat")
                            }

                            if (mScanHandler!!.isIpAvailableInCentralSceneRepo(currentIpAddress)) {
                                mScanHandler!!.putSceneObjectToCentralRepo(currentIpAddress, currentSceneObject)
                            }
                            setViews() //This will take care of disabling the views
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                MIDCONST.MID_DEVICE_ALERT_STATUS.toInt() -> {
                    val message = String(packet.payload)
                    LibreLogger.d(META_DATA, " message 54 recieved  $message")
                    try {
                        var error: LibreError? = null
                        if (message != null && (message.equals(DMR_PLAYBACK_COMPLETED, ignoreCase = true) || message.contains(FAIL))) {

                            /* If the song completes then make the seekbar to the starting of the song */
                            currentSceneObject!!.currentPlaybackSeekPosition = 0f
                            binding.seekBarSong.progress = 0
                        } else if (message.contains(FAIL)) {
                            error = LibreError(currentIpAddress, resources.getString(R.string.FAIL_ALERT_TEXT))
                        } else if (message.contains(SUCCESS)) {
                            showLoader(false)
                        } else if (message.contains(NO_URL)) {
                            error = LibreError(currentIpAddress, resources.getString(R.string.NO_URL_ALERT_TEXT))
                        } else if (message.contains(NO_PREV_SONG)) {
                            error = LibreError(currentIpAddress, resources.getString(R.string.NO_PREV_SONG_ALERT_TEXT))
                        } else if (message.contains(NO_NEXT_SONG)) {
                            error = LibreError(currentIpAddress, resources.getString(R.string.NO_NEXT_SONG_ALERT_TEXT))
                        } else if (message.contains(DMR_SONG_UNSUPPORTED)) {
                            error = LibreError(currentIpAddress, resources.getString(R.string.SONG_NOT_SUPPORTED))
                        }
                        //spotify
                        else if (message.contains(SPOTIFY_NOT_AVAILABLE_FOR_FREE)) run {
                            //error = new LibreError(currentIpAddress, getResources().getString(R.string.SPOTIFY_NOT_AVAILABLE_FOR_FREE));
                            /* adding below code because unwanted loader showing and need to dismiss when spotify is playing would disturb user*/
                            Toast.makeText(applicationContext, resources.getString(R.string.SPOTIFY_NOT_AVAILABLE_FOR_FREE), Toast.LENGTH_SHORT).show()

                        } else if (message.contains(SPOTIFY_NOT_SUPPORTED_FOR_FREE)) run {
                            //error = new LibreError("", getResources().getString(R.string.SPOTIFY_NOT_SUPPORTED_FOR_FREE));
                            Toast.makeText(applicationContext, resources.getString(R.string.SPOTIFY_NOT_SUPPORTED_FOR_FREE), Toast.LENGTH_SHORT).show()


                        } else if (message.contains(SPOTIFY_SHUFFLE_OFF_NOT_SUPPORTED_FOR_FREE)) run {
                            // error = new LibreError("", getResources().getString(R.string.SPOTIFY_SHUFFLE_OFF_NOT_SUPPORTED_FOR_FREE));
                            Toast.makeText(applicationContext, resources.getString(R.string.SPOTIFY_SHUFFLE_OFF_NOT_SUPPORTED_FOR_FREE), Toast.LENGTH_SHORT).show()


                        } else if (message.contains(SPOTIFY_ADS_PLAYING)) run {
                            // error = new LibreError("", getResources().getString(R.string.SPOTIFY_ADS_PLAYING));
                            Toast.makeText(applicationContext, resources.getString(R.string.SPOTIFY_ADS_PLAYING), Toast.LENGTH_SHORT).show()
                        }
                        else if (message.contains(LUCIMESSAGES.NEXTMESSAGE)) {
                            handleNextPrevForMB(true)
                        } else if (message.contains(LUCIMESSAGES.PREVMESSAGE)) {
                            handleNextPrevForMB(false)
                        }
                        //PicassoTrustCertificates.getInstance(getActivity()).invalidate(currentSceneObject.getAlbum_art());
                        showLoader(false)
                        if (error != null)
                            BusProvider.getInstance().post(error)

                    } catch (e: Exception) {
                        e.printStackTrace()
                        LibreLogger.d(TAG, " Json exception ")

                    }

                }

                MIDCONST.MUTE_UNMUTE_STATUS -> {
                    val message = String(packet.payload)
                    LibreLogger.d(META_DATA, "MB : 63, msg = $message")
                    try {
                        if (message.isNotEmpty()) {
                            currentSceneObject!!.mute_status = message
                            mScanHandler!!.putSceneObjectToCentralRepo(nettyData.getRemotedeviceIp(), currentSceneObject)
                            setViews() //This will take care of disabling the views
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            }
        }

    }

    /*Setting views here*/
    private fun setViews() {
        if (currentSceneObject == null) {
            return
        }
        if (LibreApplication.INDIVIDUAL_VOLUME_MAP.containsKey(currentSceneObject!!.ipAddress)) {
            binding.seekBarVolume.progress = LibreApplication.INDIVIDUAL_VOLUME_MAP[currentSceneObject?.ipAddress!!]!!
        } else {
            val control = LUCIControl(currentSceneObject!!.ipAddress)
            control.SendCommand(MIDCONST.VOLUME_CONTROL, null, LSSDPCONST.LUCI_GET)
            if (currentSceneObject!!.volumeValueInPercentage >= 0)
                binding.seekBarVolume.progress = currentSceneObject!!.volumeValueInPercentage
        }

        if (binding.seekBarVolume.progress == 0) {
            binding.ivVolumeDown.setImageResource(R.drawable.ic_volume_mute)
        } else {
            //Setting the Mute and UnMute status
            if (currentSceneObject!!.mute_status == LUCIMESSAGES.MUTE) {
                binding.ivVolumeDown.setImageResource(R.drawable.ic_volume_mute)
            } else {
                binding.ivVolumeDown.setImageResource(R.drawable.volume_low_enabled)
            }
        }
        if (!currentSceneObject?.trackName.isNullOrEmpty()
            && !currentSceneObject?.trackName?.equals("NULL", ignoreCase = true)!!
            && !currentSceneObject?.trackName?.equals(binding.tvTrackName.text?.toString())!!) {
            var trackname: String? = currentSceneObject!!.trackName
            /* This change is done to handle the case of deezer where the song name is appended by radio or skip enabled */
            if (trackname != null && trackname.contains(DEZER_RADIO)) {
                trackname = trackname.replace(DEZER_RADIO, "")
            }

            if (trackname != null && trackname.contains(DEZER_SONGSKIP)) {
                trackname = trackname.replace(DEZER_SONGSKIP, "")
            }

            binding.tvTrackName.text = trackname
            binding.tvTrackName.post {
                binding.tvTrackName.isSelected = true
            }
        }

        if (!currentSceneObject?.album_name.isNullOrEmpty() &&
            !currentSceneObject?.album_name.equals("NULL", ignoreCase = true) &&
            !currentSceneObject?.album_name.equals(binding.tvAlbumName.text?.toString(), ignoreCase = true)) {
            if (!currentSceneObject?.artist_name.isNullOrEmpty() &&
                !currentSceneObject?.artist_name.equals("NULL", ignoreCase = true) &&
                !currentSceneObject?.artist_name.equals(binding.tvAlbumName.text?.toString(), ignoreCase = true)
            ) {
                binding.tvAlbumName.text = "${currentSceneObject?.album_name}, ${currentSceneObject?.artist_name}"
                binding.tvAlbumName.post {
                    binding.tvAlbumName.isSelected = true
                }
            } else {
                binding.tvAlbumName.text = currentSceneObject?.album_name
                binding.tvAlbumName.post {
                    binding.tvAlbumName.isSelected = true
                }
            }
        }
        if (currentSceneObject!!.currentSource == SPOTIFY_SOURCE) {
            LibreLogger.d(TAG, "suma in setviews spotify next \n" + currentSceneObject!!.nextControl + "prev " + "control\n" + currentSceneObject!!.previousControl)
            // if(currentSceneObject.getNextControl()||currentSceneObject.getPreviousControl()){
            if (currentSceneObject!!.nextControl) {
                binding.ivNext.alpha = 1f
                binding.ivNext.isEnabled = true
                binding.ivNext.isClickable = true
                binding.ivNext.isFocusable = true
            } else {
                binding.ivNext.alpha = 0.5f
                binding.ivNext.isEnabled = false
                binding.ivNext.isClickable = false
                binding.ivNext.isFocusable = false

            }

            if (currentSceneObject!!.previousControl) {
                binding.ivPrevious.alpha = 1f
                binding.ivPrevious.isEnabled = true
                binding.ivPrevious.isClickable = true
                binding.ivPrevious.isFocusable = true
            } else {
                binding.ivPrevious.alpha = 0.5f
                binding.ivPrevious.isEnabled = false
                binding.ivPrevious.isClickable = false
                binding.ivPrevious.isFocusable = false
            }
            LibreLogger.d(TAG, "suma in getting seek enabled value" + currentSceneObject!!.seekEnabled)
            if (!currentSceneObject!!.seekEnabled) {
                binding.seekBarSong.isFocusable = false
                binding.seekBarSong.isEnabled = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    binding.seekBarSong.splitTrack = false
                    LibreLogger.d(TAG, "spotify seek split suma in seekbar thumb9")

                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    binding.seekBarSong.thumb.mutate().alpha = 0
                    LibreLogger.d(TAG, "spotify seek split suma in seekbar thumb11")

                }
            } else {
                binding.seekBarSong.isFocusable = true
                binding.seekBarSong.isEnabled = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    binding.seekBarSong.splitTrack = true
                    LibreLogger.d(TAG, "spotify seek split suma in seekbar thumb9")

                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    binding.seekBarSong.thumb.mutate().alpha = 255
                    LibreLogger.d(TAG, "spotify seek split suma in seekbar thumb11")

                }
            }

        } else {
            binding.ivNext.alpha = 1f
            binding.ivNext.isEnabled = true
            binding.ivNext.isClickable = true
            binding.ivNext.isFocusable = true
            binding.ivPrevious.alpha = 1f
            binding.ivPrevious.isEnabled = true
            binding.ivPrevious.isClickable = true
            binding.ivPrevious.isFocusable = true
            binding.seekBarSong.isFocusable = true
            binding.seekBarSong.isEnabled = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.seekBarSong.splitTrack = true
                LibreLogger.d(TAG, "spotify seek split suma in seekbar thumb9")

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                binding.seekBarSong.thumb.mutate().alpha = 255
                LibreLogger.d(TAG, "spotify seek split suma in seekbar thumb11")

            }
        }

        /**
         * And for some sources like CAST,AIRPLAY,BT, DMR_SOURCE, AUX_SOURCE Shuffle & Repeat will not
         * support, so we are making visibility GONE for now "hideShuffleRepeat()"
         * As Discussed with Sathish in the future for Repeat & Shuffle status we have to
         * dependent on the Device response(JSON), and make dynamic UI
         */
        if (currentSceneObject?.currentSource == GCAST_SOURCE
            || currentSceneObject?.currentSource == BT_SOURCE
            || currentSceneObject?.currentSource == AIRPLAY_SOURCE
           /* || currentSceneObject!!.currentSource == USB_SOURCE*/
            || currentSceneObject!!.currentSource == SDCARD_SOURCE
            || currentSceneObject!!.currentSource == DEEZER_SOURCE
            || currentSceneObject!!.currentSource == NETWORK_DEVICES
            || currentSceneObject!!.currentSource == FAVOURITES_SOURCE
            || currentSceneObject!!.currentSource == DMR_SOURCE
            || currentSceneObject!!.currentSource == AUX_SOURCE
            || (currentSceneObject!!.playUrl != null
                    && !currentSceneObject!!.playUrl.contains(LibreApplication.LOCAL_IP)
                    && currentSceneObject!!.currentSource == DMR_SOURCE)) {
            hideShuffleRepeat()
        } else {
            /**For Spotify Podcasts PlayBackCommands like Repeat,Shuffle,Next & Previous will not
             * support so we are making visibility Gone
             */
            if (currentSceneObject!!.playUrl != null)
                if (currentSceneObject!!.playUrl != null && currentSceneObject!!.playUrl.contains("spotify:episode") || currentSceneObject!!.playUrl.contains("spotify:show")) {
                    binding.mediaBtnSkipNext.visibility = View.VISIBLE
                    binding.mediaBtnSkipPrev.visibility = View.VISIBLE
                    binding.ivPrevious.visibility = View.GONE
                    binding.ivNext.visibility = View.GONE
                    binding.ivRepeat.visibility = View.GONE
                    binding.ivShuffle.visibility = View.GONE
                } else {
                    binding.mediaBtnSkipNext.visibility = View.GONE
                    binding.mediaBtnSkipPrev.visibility = View.GONE
                    binding.ivPrevious.visibility = View.VISIBLE
                    binding.ivNext.visibility = View.VISIBLE
                    binding.ivRepeat.visibility = View.VISIBLE
                    binding.ivShuffle.visibility = View.VISIBLE

                }
            /** Comment By SHAIK
             * If Shuffle state is "0"  which means -> Shuffle is OFF(Disabled)
             * If Shuffle state is "1"  which means -> Shuffle is ON(Enabled)
             */
            if (currentSceneObject!!.shuffleState == 0) {
                binding.ivShuffle.setImageResource(R.drawable.shuffle_disabled)
            } else {
                binding.ivShuffle.setImageResource(R.drawable.shuffle_enabled)
            }
            /*this for repeat state*/
            when (currentSceneObject!!.repeatState) {
                REPEAT_OFF -> binding.ivRepeat.setImageResource(R.drawable.repeat_disabled)
                REPEAT_ONE -> binding.ivRepeat.setImageResource(R.drawable.ic_repeat_one)
                REPEAT_ALL -> binding.ivRepeat.setImageResource(R.drawable.ic_repeatall)
            }

        }
        LibreLogger.d(TAG, "playStatus CT: " + currentSceneObject!!.playstatus)

        when (currentSceneObject!!.playstatus) {
            CURRENTLY_BUFFERING -> {
                showLoader(true)
                setPlayPauseLoader(binding.ivPlayPause, isEnabled = false, isLoader = true, image = 0)
            }

            CURRENTLY_PLAYING -> {
                showLoader(false)
                if (playPauseNextPrevAllowed()) {
                    setPlayPauseLoader(binding.ivPlayPause, isEnabled = true, isLoader = false, image = R.drawable.pause_orange)
                    binding.ivNext.setImageResource(R.drawable.next_enabled)
                    binding.ivPrevious.setImageResource(R.drawable.prev_enabled)
                } else {
                    setPlayPauseLoader(binding.ivPlayPause, isEnabled = false, isLoader = false, image = R.drawable.play_white)
                    binding.ivNext.setImageResource(R.drawable.next_disabled)
                    binding.ivPrevious.setImageResource(R.drawable.prev_disabled)
                }
            }

            CURRENTLY_PAUSED -> {
                showLoader(false)
                if (playPauseNextPrevAllowed()) {
                    setPlayPauseLoader(binding.ivPlayPause, isEnabled = true, isLoader = false, image = R.drawable.play_orange)
                    binding.ivNext.setImageResource(R.drawable.next_enabled)
                    binding.ivPrevious.setImageResource(R.drawable.prev_enabled)
                } else {
                    setPlayPauseLoader(binding.ivPlayPause, isEnabled = false, isLoader = false, image = R.drawable.play_white)
                    binding.ivNext.setImageResource(R.drawable.next_disabled)
                    binding.ivPrevious.setImageResource(R.drawable.prev_disabled)
                }
            }

            CURRENTLY_STOPPED -> {
                showLoader(false)
                setPlayPauseLoader(binding.ivPlayPause, isEnabled = true, isLoader = false, image = R.drawable.play_white)
                binding.ivNext.setImageResource(R.drawable.next_disabled)
                binding.ivPrevious.setImageResource(R.drawable.prev_disabled)
            }
        }

        /* Setting the current seekbar progress -Start*/
        val duration = currentSceneObject!!.currentPlaybackSeekPosition
        binding.seekBarSong.max = currentSceneObject!!.totalTimeOfTheTrack.toInt() / 1000
        binding.seekBarSong.secondaryProgress = currentSceneObject!!.totalTimeOfTheTrack.toInt() / 1000
        LibreLogger.d("SEEK", "Duration = " + duration / 1000)
        binding.seekBarSong.progress = duration.toInt() / 1000

        binding.tvCurrentDuration.text = convertMillisToSongTime((duration.toInt() / 1000).toLong())
        LibreLogger.d("SEEK", "convertMillisToSongTime = " + convertMillisToSongTime((duration.toInt() / 1000).toLong()))
        binding.tvTotalDuration.text = convertMillisToSongTime(currentSceneObject!!.totalTimeOfTheTrack / 1000)

        /*if (!currentSceneObject?.trackName.isNullOrEmpty() && !currentTrackName?.equals(currentSceneObject?.trackName, ignoreCase = true)) {
            currentTrackName = currentSceneObject?.trackName!!
        }*/

        updateAlbumArt()
       // setTheSourceIconFromCurrentSceneObject(5)

    }

    private fun hideShuffleRepeat() {
        binding.ivShuffle.visibility = View.GONE
        binding.ivRepeat.visibility = View.GONE
    }

    private fun setSourceIconsForAlexaSource(currentSceneObject: SceneObject?) {
        binding.ivSourceIcon.visibility = View.VISIBLE
        setControlIconsForAlexa(currentSceneObject, binding.ivPlayPause, binding.ivNext, binding.ivPrevious)
        /* String alexaSourceURL = currentSceneObject.getAlexaSourceImageURL();
        if (alexaSourceURL!=null){
            setImageFromURL(alexaSourceURL,R.drawable.default_album_art,alexaSourceImage);
        }*/

        binding.ivSourceIcon.visibility = View.VISIBLE
        when {
            currentSceneObject?.playUrl?.contains("tunein", true)!! -> binding.ivSourceIcon.setImageResource(R.drawable.tunein_image2_nowplaying3)
            currentSceneObject.playUrl?.contains("iheartradio", true)!! -> binding.ivSourceIcon.setImageResource(R.drawable.iheartradio_image2_nowplaying)
            currentSceneObject.playUrl?.contains("amazon music", true)!! -> binding.ivSourceIcon.setImageResource(R.drawable.amazon_image2)
            currentSceneObject.playUrl?.contains("siriusxm", true)!! -> binding.ivSourceIcon.setImageResource(R.drawable.sirius_image2_nowplaying3)
            currentSceneObject.playUrl?.contains("Deezer", true)!! -> binding.ivSourceIcon.setImageResource(R.mipmap.riva_deezer_icon)
            currentSceneObject.playUrl?.contains("Pandora", true)!! -> binding.ivSourceIcon.setImageResource(R.mipmap.riva_pandora_icon)
            else -> binding.ivSourceIcon.visibility = View.GONE
        }
    }

    private fun updateAlbumArt() {
        if (currentSceneObject!!.currentSource != AUX_SOURCE
        /*&& currentSceneObject!!.currentSource != EXTERNAL_SOURCE*/
        /* && currentSceneObject!!.currentSource != BT_SOURCE*/) {
            var album_url = ""
            if (!currentSceneObject!!.album_art.isNullOrEmpty() && currentSceneObject?.album_art?.equals("coverart.jpg", ignoreCase = true)!!) {

                album_url = "http://" + currentSceneObject!!.ipAddress + "/" + "coverart.jpg"
                /* If Track Name is Different just Invalidate the Path
                 * And if we are resuming the Screen(Screen OFF and Screen ON) , it will not re-download it */
                if (currentSceneObject!!.trackName != null && !currentTrackName.equals(currentSceneObject!!.trackName, ignoreCase = true)) {
                    currentTrackName = currentSceneObject?.trackName!!
                    val mInvalidated = mInvalidateTheAlbumArt(currentSceneObject!!, album_url)
                    LibreLogger.d(TAG, "Invalidated the URL $album_url Status $mInvalidated")
                }

                PicassoTrustCertificates.getInstance(this)
                    .load(album_url)
                    .error(R.mipmap.album_art).placeholder(R.mipmap.album_art)
                    .into(binding.ivAlbumArt)

                /*Blurred Album art*/
                PicassoTrustCertificates.getInstance(this)
                    .load(album_url)
                    .transform(BlurTransformation(this/*,20*/))
                    .error(R.mipmap.blurred_album_art).placeholder(R.mipmap.blurred_album_art)
                    .into(binding.ivBlurredAlbumArt)
            } else {
                when {
                    !currentSceneObject?.album_art.isNullOrEmpty() -> {
                        album_url = currentSceneObject!!.album_art
                        if (currentSceneObject!!.trackName != null && !currentTrackName.equals(currentSceneObject!!.trackName, ignoreCase = true)) {
                            currentTrackName = currentSceneObject?.trackName!!
                            val mInvalidated = mInvalidateTheAlbumArt(currentSceneObject!!, album_url)
                            LibreLogger.d(TAG, "Invalidated the URL ELSE $album_url Status$mInvalidated")
                        }

                        PicassoTrustCertificates.getInstance(this)
                            .load(album_url)
                            .placeholder(R.mipmap.album_art)
                            /*.memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE)*/
                            .error(R.mipmap.album_art)
                            .into(binding.ivAlbumArt)

                        /*Blurred Album art*/
                        PicassoTrustCertificates.getInstance(this)
                            .load(album_url)
                            .transform(BlurTransformation(this/*,20*/))
                            .error(R.mipmap.blurred_album_art).placeholder(R.mipmap.blurred_album_art)
                            .into(binding.ivBlurredAlbumArt)
                    }

                    else -> {
                        binding.ivAlbumArt.setImageResource(R.mipmap.album_art)
                        binding.ivBlurredAlbumArt.setImageResource(R.mipmap.blurred_album_art)
                    }
                }
            }
        }
        /*else iv_album_art?.setImageResource(R.mipmap.album_art)*/
    }

    /* This function takes care of setting the image next to sources button depending on the
    current source that is being played.
    */
    private fun setTheSourceIconFromCurrentSceneObject(i: Int) {
        if (currentSceneObject == null) {
            return
        }
        /* Enabling the views by default which gets updated to disabled based on the need */
        enableViews(enable = true)
        var imgResId = R.drawable.songs_borderless
        LibreLogger.d(META_DATA, "CurrentSource ${currentSceneObject?.currentSource} and tunnle " +
                "Source " +
                "is${currentSceneObject!!.tunnelingCurrentSource} and coming from $i")
        when (currentSceneObject?.currentSource) {
            NO_SOURCE -> imgResId = 0
            AIRPLAY_SOURCE -> imgResId = R.drawable.airplay_no_bg
            DMR_SOURCE -> imgResId = R.drawable.my_device_enabled
            DMP_SOURCE -> imgResId = R.drawable.media_servers_enabled
            SPOTIFY_SOURCE -> imgResId = R.drawable.spotify_image2
            USB_SOURCE -> imgResId = R.drawable.usb_storage_enabled
            SDCARD_SOURCE -> imgResId = R.mipmap.sdcard
            MELON_SOURCE -> imgResId = 0
            VTUNER_SOURCE -> {
                imgResId = R.mipmap.vtuner_logo
                /*disabling views for VTUNER*/
                disableViews(currentSceneObject!!.currentSource, currentSceneObject!!.album_name,2)
            }

            TUNEIN_SOURCE -> {
                imgResId = R.mipmap.tunein_logo1
                disableViews(currentSceneObject!!.currentSource, currentSceneObject!!.album_name,3)
            }

            MIRACAST_SOURCE -> imgResId = 0
            DDMSSLAVE_SOURCE -> imgResId = 0
            AUX_SOURCE -> {
                imgResId = R.drawable.ic_aux_in
                /* added to make sure we dont show the album art during aux */
                disableViews(currentSceneObject!!.currentSource, getString(R.string.aux),4)
            }

            APPLEDEVICE_SOURCE -> imgResId = R.drawable.airplay_logo
            DIRECTURL_SOURCE -> imgResId = 0
            BT_SOURCE -> {
                imgResId = R.drawable.bluetooth_icon
                when (currentSceneObject!!.playstatus) {
                    CURRENTLY_STOPPED -> disableViews(currentSceneObject!!.currentSource,
                        getString(R.string.btOn),5)
                    CURRENTLY_PAUSED -> disableViews(currentSceneObject!!.currentSource,
                        getString(R.string.btOn),6)
                    else -> disableViews(currentSceneObject!!.currentSource, getString(R.string
                        .btOn),7)
                }
                /* added to make sure we dont show the album art during aux */
                disableViews(currentSceneObject!!.currentSource, getString(R.string.aux),8)
            }

            DEEZER_SOURCE -> {
                imgResId = R.mipmap.deezer_logo
                disableViews(currentSceneObject!!.currentSource, currentSceneObject!!.trackName,9)
            }

            TIDAL_SOURCE -> imgResId = R.mipmap.tidal_white_logo
            FAVOURITES_SOURCE -> imgResId = R.mipmap.ic_remote_favorite
            GCAST_SOURCE -> imgResId = R.mipmap.ic_cast_white_24dp_2x
            ALEXA_SOURCE -> imgResId = R.drawable.alexa_blue_white_100px
            EXTERNAL_SOURCE ->{
                imgResId = R.drawable.ic_aux_in
                disableViews(currentSceneObject!!.currentSource, getString(R.string.aux_playing),12)
            }
            ROON ->{
                imgResId = R.drawable.roon
            }
            OPTICAL_SOURCE -> imgResId = 0
            TUNNELING_WIFI_SOURCE -> imgResId = 0
            else ->{
                LibreLogger.d(META_DATA, "CurrentSource ELSE ${currentSceneObject?.currentSource}" +
                        " and tunnle " + "Source " + "is${currentSceneObject!!.tunnelingCurrentSource}")
                    when (currentSceneObject?.tunnelingCurrentSource) {
                        AUX_SOURCE -> {
                            imgResId = R.drawable.ic_aux_in
                            disableViews(currentSceneObject!!.currentSource, getString(R.string.aux_playing),122)
                        }

                        BT_SOURCE -> {
                            imgResId = R.drawable.bluetooth_icon
                            when (currentSceneObject!!.playstatus) {
                                CURRENTLY_STOPPED -> disableViews(currentSceneObject!!.currentSource,
                                    getString(R.string.btOn),55)
                                CURRENTLY_PAUSED -> disableViews(currentSceneObject!!.currentSource,
                                    getString(R.string.btOn),66)
                                else -> disableViews(currentSceneObject!!.currentSource, getString(R.string
                                    .btOn),77)
                            }
                            /* added to make sure we dont show the album art during aux */
                            disableViews(currentSceneObject!!.currentSource, getString(R.string
                                .aux),88)
                        }

                        USB_SOURCE -> {
                            imgResId = R.drawable.usb_storage_enabled
                        }
                    }
            }
        }

        binding.ivSourceIcon.setImageResource(imgResId)
        //handleThePlayIconsForGrayoutOption()
        if (currentSceneObject?.currentSource == ALEXA_SOURCE) {
            setSourceIconsForAlexaSource(currentSceneObject)
        }
    }

    private fun handleThePlayIconsForGrayoutOption() {
        if (binding.ivPrevious.isEnabled) {
            binding.ivPrevious.setImageResource(R.drawable.prev_enabled)
        } else
            binding.ivPrevious.setImageResource(R.drawable.prev_disabled)

        if (binding.ivNext.isEnabled) {
            binding.ivNext.setImageResource(R.drawable.next_enabled)
        } else {
            binding.ivNext.setImageResource(R.drawable.next_disabled)
        }
        if (!binding.ivPlayPause.isEnabled) {
            binding.ivPlayPause.setImageResource(R.drawable.play_white)
        } else if (currentSceneObject!!.playstatus == CURRENTLY_PLAYING) {
            binding.ivPlayPause.setImageResource(R.drawable.pause_orange)
        } else {
            binding.ivPlayPause.setImageResource(R.drawable.play_orange)
        }
        updatePlayPauseNextPrevForCurrentSource(currentSceneObject)
    }

    private fun updatePlayPauseNextPrevForCurrentSource(sceneObject: SceneObject?) {
        if (sceneObject?.currentSource == VTUNER_SOURCE
            || sceneObject?.currentSource == TUNEIN_SOURCE
            /* || sceneObject?.currentSource == BT_SOURCE*/
            || sceneObject?.currentSource == AUX_SOURCE
            || sceneObject?.currentSource == NO_SOURCE) {
            binding.ivPlayPause.setImageResource(R.drawable.play_white)
            binding.ivNext.setImageResource(R.drawable.next_disabled)
            binding.ivPrevious.setImageResource(R.drawable.prev_disabled)
        }
    }

    private fun convertMillisToSongTime(time: Long): String {
        val seconds = time.toInt() % 60
        val mins = (time / 60).toInt() % 60
        return if (seconds < 10) "$mins:0$seconds" else "$mins:$seconds"
    }

    fun getTheRenderer(ipAddress: String?): DMRProcessor? {
        //not sure but check it
        onRemoteDeviceAdded(remoteDevice)
        val renderingDevice = UpnpDeviceManager.getInstance().getRemoteDMRDeviceByIp(currentIpAddress)
        if (renderingDevice != null) {
            val renderingUDN = renderingDevice.identity.udn.toString()
            val playbackHelper = LibreApplication.PLAYBACK_HELPER_MAP[renderingUDN]
            return if (playbackHelper != null) {
                LibreApplication.PLAYBACK_HELPER_MAP[renderingUDN] = playbackHelper

                val dmrControlHelper = playbackHelper.dmrControlHelper
                if (dmrControlHelper != null) {
                    val dmrProcessor = dmrControlHelper.dmrProcessor
                    dmrProcessor.removeListener(this)
                    dmrProcessor.addListener(this)
                    dmrProcessor
                } else
                    null
            } else
                null
        } else
            return null
    }


    /* Seek should work on both Luci command and DMR command */
    internal fun doSeek(pos: Int): Boolean {
        val duration: String
        if (isLocalDMRPlayback) {
            remoteDevice = UpnpDeviceManager.getInstance().getRemoteDMRDeviceByIp(currentIpAddress)
            upnpProcessor!!.getTheRendererFromRegistryIp(currentIpAddress)
            val theRenderer = getTheRenderer(currentIpAddress) ?: return false

            val format = ModelUtil.toTimeString(pos.toLong())
            theRenderer.seek(format)
            LibreLogger.d(TAG, "LocalDMR pos = " + pos + " total time of the song " +
                    currentSceneObject!!
                        .totalTimeOfTheTrack / 1000 + "format = " + format)
            return true
        } else if (currentSceneObject!!.currentSource == VTUNER_SOURCE || currentSceneObject!!
                .currentSource == TUNEIN_SOURCE || currentSceneObject!!.currentSource ==
            GCAST_SOURCE || currentSceneObject!!.currentSource == TIDAL_SOURCE) {
            showToast(R.string.seek_not_allowed)
            binding.seekBarSong.isClickable = false
            binding.seekBarSong.isEnabled = false
            binding.seekBarSong.progress = 0
            return true
        } else {
            val control = LUCIControl(currentSceneObject!!.ipAddress)
            LibreLogger.d(TAG, "Remote seek = " + pos + " total time of the song " + currentSceneObject!!.totalTimeOfTheTrack)
            //duration = (pos * 0.01 * currentSceneObject.getTotalTimeOfTheTrack()) + "";
            duration = (pos * 1000).toString() + ""
            binding.seekBarSong.progress = pos
            LibreLogger.d(TAG, "Rempote Seek  = " + duration + " total time of the song " + currentSceneObject!!.totalTimeOfTheTrack)
            control.SendCommand(MIDCONST.MID_PLAYCONTROL.toInt(), "SEEK:$duration", LSSDPCONST.LUCI_SET)

        }/*this is to prevent playback pause when clicking on seekbar while vtuner and tunein */
        return true

    }


    private fun doNextPrevious(isNextPressed: Boolean) {
        if (isLocalDMRPlayback && currentSceneObject != null && (currentSceneObject!!.currentSource == NO_SOURCE
                    || currentSceneObject!!.currentSource == DMR_SOURCE
                    || currentSceneObject!!.currentSource == DDMSSLAVE_SOURCE)) {
            val renderingDevice = UpnpDeviceManager.getInstance().getRemoteDMRDeviceByIp(currentIpAddress)
            if (renderingDevice != null) {
                val renderingUDN = renderingDevice.identity.udn.toString()
                val playbackHelper = LibreApplication.PLAYBACK_HELPER_MAP[renderingUDN]

                if (playbackHelper == null
                    || playbackHelper.dmsBrowseHelper == null
                    || !currentSceneObject!!.playUrl.contains(LibreApplication.LOCAL_IP)) {

                    showToast(R.string.no_active_playlist)
                    /* In SA mode we need to go to local content while in HN mode we will go to sources option */
                    if (LibreApplication.activeSSID.contains(DDMS_SSID)) {
                        val localIntent = Intent(this, CTLocalDMSActivity::class.java)
                        localIntent.putExtra(CURRENT_DEVICE_IP, currentIpAddress)
                        startActivity(localIntent)
                    } else {
                        val localIntent = Intent(this, CTMediaSourcesActivity::class.java)
                        localIntent.putExtra(CURRENT_DEVICE_IP, currentIpAddress)
                        localIntent.putExtra(CURRENT_SOURCE, "" + currentSceneObject!!.currentSource)
                        localIntent.putExtra(FROM_ACTIVITY, CTNowPlayingActivity::class.java.simpleName)
                        startActivity(localIntent)
                    }
                    this.finish()
                    return
                }

                if ((currentSceneObject?.shuffleState == 0) && (currentSceneObject?.repeatState == REPEAT_OFF)) {
                    if (isNextPressed) {
                        if (playbackHelper.isThisTheLastSong || playbackHelper.isThisOnlySong) {
                            showToast(R.string.lastSongPlayed)
                            showLoaderHandler.sendEmptyMessage(PREPARATION_COMPLETED)
                            return
                        }
                    } else {
                        if (playbackHelper.isThisFirstSong || playbackHelper.isThisOnlySong) {
                            showToast(R.string.onlyOneSong)
                            showLoaderHandler.sendEmptyMessage(PREPARATION_COMPLETED)
                            return
                        }
                    }
                }

                showLoaderHandler.sendEmptyMessageDelayed(PREPARATION_TIMEOUT_CONST, PREPARATION_TIMEOUT.toLong())
                showLoaderHandler.sendEmptyMessage(PREPARATION_INITIATED)
                if (isNextPressed) {
                    playbackHelper.playNextSong(1)
                } else
                    playbackHelper.playNextSong(-1)
            }
        } else {
            if (currentSceneObject != null && currentSceneObject!!.currentSource == DEEZER_SOURCE) {
                val trackname = currentSceneObject!!.trackName
                if (isNextPressed) {
                    if (trackname != null && trackname.contains(DEZER_SONGSKIP)) {
                        showLoader(false)
                        Toast.makeText(this, "Activate Deezer Premium+ from your computer", Toast.LENGTH_LONG).show()
                        return
                    }
                }
            }

            if (currentSceneObject!!.currentSource != BT_SOURCE
                && currentSceneObject!!.currentSource != AUX_SOURCE
                && currentSceneObject!!.currentSource != GCAST_SOURCE) {

                showLoaderHandler.sendEmptyMessageDelayed(PREPARATION_TIMEOUT_CONST, PREPARATION_TIMEOUT.toLong())
                showLoaderHandler.sendEmptyMessage(PREPARATION_INITIATED)
            }
            val control = LUCIControl(currentSceneObject!!.ipAddress)
            if (isNextPressed)
                control.SendCommand(MIDCONST.MID_PLAYCONTROL.toInt(), LUCIMESSAGES.PLAY_NEXT, LSSDPCONST.LUCI_SET)
            else
                control.SendCommand(MIDCONST.MID_PLAYCONTROL.toInt(), LUCIMESSAGES.PLAY_PREV, LSSDPCONST.LUCI_SET)
        }


    }

    private fun handleNextPrevForMB(isNextPressed: Boolean) {

        if (isLocalDMRPlayback && currentSceneObject != null && (currentSceneObject!!.currentSource == 0 || currentSceneObject!!.currentSource == 2)) {
            val renderingDevice = UpnpDeviceManager.getInstance().getRemoteDMRDeviceByIp(currentIpAddress)
            if (renderingDevice != null) {
                val renderingUDN = renderingDevice.identity.udn.toString()
                val playbackHelper = LibreApplication.PLAYBACK_HELPER_MAP[renderingUDN]

                if (playbackHelper == null
                    || playbackHelper.dmsBrowseHelper == null
                    || currentSceneObject != null && !currentSceneObject!!.playUrl.contains(LibreApplication.LOCAL_IP)) {
                    return
                }

                showLoaderHandler.sendEmptyMessageDelayed(PREPARATION_TIMEOUT_CONST, PREPARATION_TIMEOUT.toLong())
                showLoaderHandler.sendEmptyMessage(PREPARATION_INITIATED)
                if (isNextPressed) {
                    playbackHelper.playNextSong(1)
                } else {
                    /* Setting the current seekbar progress -Start*/
                    val duration = currentSceneObject!!.currentPlaybackSeekPosition
                    LibreLogger.d("Current Duration ", "Duration = " + duration / 1000)
                    val durationInSeeconds = duration / 1000
                    if (durationInSeeconds < 5) {
                        playbackHelper.playNextSong(-1)
                    } else {
                        playbackHelper.playNextSong(0)
                    }
                }
            }
        }
    }

    internal fun doVolumeChange(currentVolumePosition: Int): Boolean {
        /* We can make use of CurrentIpAddress instead of CurrenScneObject.getIpAddress*/
        val control = LUCIControl(currentIpAddress)
        control.SendCommand(MIDCONST.VOLUME_CONTROL, "" + currentVolumePosition, LSSDPCONST.LUCI_SET)
        currentSceneObject!!.volumeValueInPercentage = currentVolumePosition
        currentSceneObject!!.mute_status = LUCIMESSAGES.UNMUTE
        mScanHandler!!.putSceneObjectToCentralRepo(currentIpAddress, currentSceneObject)
        return true
    }

    override fun onUpdatePosition(position: Long, duration: Long) {

    }

    override fun onUpdateVolume(currentVolume: Int) {

    }

    override fun onPaused() {

    }

    override fun onStoped() {

    }

    override fun onSetURI() {

    }

    override fun onPlayCompleted() {

    }

    override fun onPlaying() {

    }

    override fun onActionSuccess(action: Action<*>) {

    }

    override fun onActionFail(actionCallback: String?, response: UpnpResponse, cause: String?) {
        var cause = cause
        LibreLogger.d(TAG, " fragment that recieved the callback is " + currentSceneObject!!.ipAddress)

        if (cause != null && cause.contains("Seek:Error")) {
            cause = "Seek Failed!"
        }
        val error = LibreError(currentIpAddress, cause)
        //  BusProvider.getInstance().post(error)
        LibreLogger.d(TAG, " fragment posted the error " + currentSceneObject!!.ipAddress)

        runOnUiThread {
            /*we are setting seek bar to binding.ivPrevious position once seek got failed*/
            if (actionCallback != null && actionCallback.contains(resources.getString(R.string.SEEK_FAILED)))
                binding.seekBarSong.progress = (currentSceneObject!!.currentPlaybackSeekPosition / 1000).toInt()
        }

    }

    override fun tunnelDataReceived(tunnelingData: TunnelingData) {
        super.tunnelDataReceived(tunnelingData)
        if (tunnelingData.remoteClientIp == currentIpAddress && tunnelingData.remoteMessage.size >= 24) {
            val sceneObject = mScanHandler?.getSceneObjectFromCentralRepo(currentIpAddress)
//            binding.seekBarVolume.progress = sceneObject?.volumeValueInPercentage!!
//
//            if (binding.seekBarVolume.progress == 0) {
//       Suma for now commenting on TCP tunneling
//       binding.ivVolumeDown.setImageResource(R.drawable.ic_volume_mute)
//            } else binding.ivVolumeDown.setImageResource(R.drawable.volume_low_enabled)

            //Added BY SHAIK , Have to TC  closeLoader() and timeOutHandler
            val tcpTunnelPacket = TCPTunnelPacket(tunnelingData.remoteMessage)
            LibreLogger.d(META_DATA, "tcpTunnelPacket currentSource " + tcpTunnelPacket.currentSource)
            when (tcpTunnelPacket.currentSource) {
                BT_SOURCE -> {
                    //binding.ivToggleBluetooth.isChecked = true
                    //  closeLoader()
                    if (timeOutHandler.hasMessages(CTMediaSourcesActivity.AUX_BT_TIMEOUT))
                        timeOutHandler.removeMessages(CTMediaSourcesActivity.AUX_BT_TIMEOUT)
                }

                AUX_SOURCE -> {
                    disableViews(currentSceneObject!!.currentSource, getString(R.string.aux),13)
                    //  binding.ivToggleAux.isChecked = true
                    // closeLoader()
                    if (timeOutHandler.hasMessages(CTMediaSourcesActivity.AUX_BT_TIMEOUT))
                        timeOutHandler.removeMessages(CTMediaSourcesActivity.AUX_BT_TIMEOUT)
                }

                TUNNELING_WIFI_SOURCE -> {
                    // binding.ivToggleAux.isChecked = false
                    // binding.ivToggleBluetooth.isChecked = false
                    // closeLoader()
                    if (timeOutHandler.hasMessages(CTMediaSourcesActivity.AUX_BT_TIMEOUT))
                        timeOutHandler.removeMessages(CTMediaSourcesActivity.AUX_BT_TIMEOUT)
                }
            }
        }
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


    private fun launchTheApp(appPackageName: String) {

        val intent = applicationContext.packageManager.getLaunchIntentForPackage(appPackageName)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            redirectingToPlayStore(intent, appPackageName)
        }

    }

    private fun redirectingToPlayStore(intent: Intent?, appPackageName: String) {
        var intent = intent

        try {
            intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.parse("market://details?id=$appPackageName")
            startActivity(intent)

        } catch (anfe: android.content.ActivityNotFoundException) {

            intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.parse("http://play.google.com/store/apps/details?id=$appPackageName")
            startActivity(intent)

        }

    }
}


