package com.cumulations.libreV2.adapter

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.cumulations.libreV2.AppUtils
import com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity
import com.cumulations.libreV2.activity.CTDeviceSettingsActivity
import com.cumulations.libreV2.activity.CTMediaSourcesActivity
import com.cumulations.libreV2.activity.CTNowPlayingActivity
import com.cumulations.libreV2.activity.oem.ActivateCastActivity
import com.cumulations.libreV2.fragments.CTActiveDevicesFragment
import com.cumulations.libreV2.isConnectedToSAMode
import com.cumulations.libreV2.model.SceneObject
import com.cumulations.libreV2.tcp_tunneling.enums.BatteryType
import com.libreAlexa.LErrorHandeling.LibreError
import com.libreAlexa.LibreApplication
import com.libreAlexa.R
import com.libreAlexa.Scanning.ScanningHandler
import com.libreAlexa.alexa.AudioRecordCallback
import com.libreAlexa.alexa.AudioRecordUtil
import com.libreAlexa.alexa.MicExceptionListener
import com.libreAlexa.alexa.MicTcpServer
import com.libreAlexa.constants.Constants
import com.libreAlexa.constants.LSSDPCONST
import com.libreAlexa.constants.LUCIMESSAGES
import com.libreAlexa.constants.MIDCONST
import com.libreAlexa.databinding.CtListItemSpeakersBinding
import com.libreAlexa.luci.LSSDPNodeDB
import com.libreAlexa.luci.LUCIControl
import com.libreAlexa.luci.Utils
import com.libreAlexa.netty.BusProvider
import com.libreAlexa.util.LibreLogger
import com.libreAlexa.util.PicassoTrustCertificates
import java.util.concurrent.ConcurrentMap

class CTDeviceListAdapter(val context: Context) : RecyclerView.Adapter<CTDeviceListAdapter.SceneObjectItemViewHolder>(), AudioRecordCallback, MicExceptionListener {
    private var sceneObjectMap: LinkedHashMap<String, SceneObject> = LinkedHashMap()
    var audioRecordUtil: AudioRecordUtil? = null
    private var micTcpServer: MicTcpServer? = null
    private val TAG = CTDeviceListAdapter::class.java.simpleName

    init {
        audioRecordUtil = AudioRecordUtil.getAudioRecordUtil()
        micTcpServer = MicTcpServer.getMicTcpServer()
        (context as CTDeviceDiscoveryActivity).libreApplication.registerForMicException(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): SceneObjectItemViewHolder {
        val itemBinding =
            CtListItemSpeakersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SceneObjectItemViewHolder(itemBinding)
    }


    override fun onBindViewHolder(viewHolder: SceneObjectItemViewHolder, position: Int) {
        val ipAddress = sceneObjectMap.keys.toTypedArray()[position]
        val sceneObject = sceneObjectMap[ipAddress]
        viewHolder.bindSceneObject(sceneObject, position)
    }

    override fun getItemCount(): Int {
        return sceneObjectMap.keys.size
    }

    inner class SceneObjectItemViewHolder(val itemBinding: CtListItemSpeakersBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        private var currentTrackName: String? = "-1"
//        private var previousSourceIndex: Int = 0

        fun bindSceneObject(sceneObject: SceneObject?, position: Int) {
            LibreLogger.d(TAG, "Scene Ipaddress ${sceneObject.toString()}")
            try {
                val ipAddress = sceneObject?.ipAddress

                LibreLogger.d(TAG, "Scene Ipaddress $ipAddress")

                /* Fix by KK , When Album art is not updating properly */
//                if (currentTrackName == null)
//                    currentTrackName = ""

                /* if (sceneObject.getCurrentSource() != 14) {*/ /* Karuna Commenting For the Single Function to Update the UI*/

                clearViews()
                if (sceneObject != null) {
                    if (!sceneObject.sceneName.isNullOrEmpty() && !sceneObject.sceneName.equals("NULL", ignoreCase = true)) {
                        if (itemBinding.tvDeviceName.text.toString() != sceneObject.sceneName) {
                            itemBinding.tvDeviceName.text = sceneObject.sceneName
                            itemBinding.tvDeviceName.isSelected = true
                        }
                    } else {
                        itemBinding.tvDeviceName.text = ""
                    }

                    if (!sceneObject.trackName.isNullOrEmpty() && !sceneObject.trackName.equals("NULL", ignoreCase = true)) {
                        var trackname = sceneObject.trackName

                        /* This change is done to handle the case of deezer where the song name is appended by radio or skip enabled */
                        if (trackname.contains(Constants.DEZER_RADIO)) trackname =
                            trackname.replace(Constants.DEZER_RADIO, "")

                        if (trackname.contains(Constants.DEZER_SONGSKIP)) trackname =
                            trackname.replace(Constants.DEZER_SONGSKIP, "")
                        trackname = trackname.replace(Constants.DEZER_SONGSKIP, "")

                        if (itemBinding.ilMusicPlayingWidget.tvTrackName.text.toString() != trackname) {
                            itemBinding.ilMusicPlayingWidget.tvTrackName.text = trackname
                            itemBinding.ilMusicPlayingWidget.tvTrackName.isSelected = true
                        }
                    } else {
                        itemBinding.ilMusicPlayingWidget.tvTrackName.text = ""
                    }

                    if (!sceneObject.artist_name.isNullOrEmpty() && !sceneObject.artist_name.equals("null", ignoreCase = true)) {
                        if (itemBinding.ilMusicPlayingWidget.tvAlbumName.text.toString() != sceneObject.artist_name) {
                            itemBinding.ilMusicPlayingWidget.tvAlbumName.text =
                                sceneObject.artist_name
                            itemBinding.ilMusicPlayingWidget.tvAlbumName.isSelected = true
                        }
                    } else if (!sceneObject.album_name.isNullOrEmpty() && !sceneObject.album_name.equals("null", ignoreCase = true)) {
                        if (itemBinding.ilMusicPlayingWidget.tvAlbumName.text.toString() != sceneObject.album_name) {
                            itemBinding.ilMusicPlayingWidget.tvAlbumName.text =
                                sceneObject.album_name
                            itemBinding.ilMusicPlayingWidget.tvAlbumName.isSelected = true
                        }
                    } else {
                        itemBinding.ilMusicPlayingWidget.tvAlbumName.text = ""
                    }

                    /*this is to show loading dialog while we are preparing to play*/
                    if (sceneObject.currentSource != Constants.AUX_SOURCE/*&& sceneObject!!.currentSource != Constants.EXTERNAL_SOURCE*/ && sceneObject.currentSource != Constants.BT_SOURCE && sceneObject.currentSource != Constants.GCAST_SOURCE) {

                        /*Album Art For All other Sources Except */
                        if (!sceneObject.album_art.isNullOrEmpty() && sceneObject.album_art.equals("coverart.jpg", ignoreCase = true)) {
                            val albumUrl =
                                "http://" + sceneObject.ipAddress + "/" + "coverart.jpg"/* If Track Name is Different just Invalidate the Path And if we are resuming the Screen(Screen OFF and Screen ON) , it will not re-download it */

                            if (sceneObject.trackName != null && !currentTrackName.equals(sceneObject.trackName, ignoreCase = true)) {
                                currentTrackName = sceneObject.trackName!!
                                val mInvalidated =
                                    (context as CTDeviceDiscoveryActivity).mInvalidateTheAlbumArt(sceneObject, albumUrl)
                                LibreLogger.d(TAG, "Invalidated the URL $albumUrl Status $mInvalidated")
                            }

                            PicassoTrustCertificates.getInstance(context)
                                .load(albumUrl)/*.memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE)*/
                                .placeholder(R.mipmap.album_art).error(R.mipmap.album_art)
                                .into(itemBinding.ilMusicPlayingWidget.ivAlbumArt)

                        } else {
                            when {
                                !sceneObject.album_art.isNullOrEmpty() -> {

                                    if (sceneObject.trackName != null && !currentTrackName.equals(sceneObject.trackName, ignoreCase = true)) {
                                        currentTrackName = sceneObject.trackName!!
                                        val mInvalidated =
                                            (context as CTDeviceDiscoveryActivity).mInvalidateTheAlbumArt(sceneObject, sceneObject.album_art)
                                        LibreLogger.d(TAG, "Invalidated the URL ${sceneObject.album_art} Status $mInvalidated")
                                    }

                                    PicassoTrustCertificates.getInstance(context)
                                        .load(sceneObject.album_art)/*   .memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE)*/
                                        .placeholder(R.mipmap.album_art).error(R.mipmap.album_art)
                                        .into(itemBinding.ilMusicPlayingWidget.ivAlbumArt)
                                }

                                else -> {
                                    itemBinding.ilMusicPlayingWidget.ivAlbumArt.setImageResource(R.mipmap.album_art)
                                }
                            }
                        }
                    }

                    if (sceneObject.playstatus == SceneObject.CURRENTLY_PLAYING) {
                        itemBinding.ilMusicPlayingWidget.ivPlayPause.setImageResource(R.drawable.pause_orange)
                    } else {
                        itemBinding.ilMusicPlayingWidget.ivPlayPause.setImageResource(R.drawable.play_orange)
                    }

                    if (sceneObject.currentSource == Constants.ALEXA_SOURCE) {
                        setControlIconsForAlexa(sceneObject, itemBinding.ilMusicPlayingWidget.ivPlayPause)
                    }
                    val lsdpNodes = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(sceneObject?.ipAddress)
                    if(lsdpNodes.getmDeviceCap().getmSource().isAlexaAvsSource){
                        itemBinding.ilMusicPlayingWidget.ibAlexaAvsBtn.visibility=View.VISIBLE
                        itemBinding.ivDeviceCast.visibility=View.GONE
                    }else{
                        itemBinding.ilMusicPlayingWidget.ibAlexaAvsBtn.visibility=View.GONE
                        itemBinding.ivDeviceCast.visibility=View.VISIBLE
                    }

                    /* Setting the current seekbar progress -Start*/
                    val duration = sceneObject.currentPlaybackSeekPosition
                    itemBinding.ilMusicPlayingWidget.seekBarSong.max =
                        sceneObject.totalTimeOfTheTrack.toInt() / 1000
                    itemBinding.ilMusicPlayingWidget.seekBarSong.secondaryProgress =
                        sceneObject.totalTimeOfTheTrack.toInt() / 1000
                    LibreLogger.d(TAG,"seek_bar_song Duration = " + duration / 1000)
                    itemBinding.ilMusicPlayingWidget.seekBarSong.progress = duration.toInt() / 1000

                    /*For free speakers irrespective of the state use Individual volume*/
                    if (LibreApplication.INDIVIDUAL_VOLUME_MAP.containsKey(ipAddress)) {
                        itemBinding.seekBarVolume.progress = LibreApplication.INDIVIDUAL_VOLUME_MAP[ipAddress]!!
                    } else {
                        LUCIControl(ipAddress).SendCommand(MIDCONST.VOLUME_CONTROL, null, LSSDPCONST.LUCI_GET)
                        if (sceneObject.volumeValueInPercentage >= 0) {
                            itemBinding.seekBarVolume.progress = sceneObject.volumeValueInPercentage
                        }
                    }

                    if (itemBinding.seekBarVolume.progress == 0) {
                        itemBinding.ivVolumeMute.setImageResource(R.drawable.ic_volume_mute)
                    } else itemBinding.ivVolumeMute.setImageResource(R.drawable.volume_low_enabled)

                    /* This line should always be here do not move this line above the ip-else loop where we check the current Source*/
//                    previousSourceIndex = sceneObject!!.currentSource

//                    toggleAVSViews(sceneObject?.isAlexaBtnLongPressed)

                    updateViews(sceneObject)
                    updatePlayPause(sceneObject)
                    handleThePlayIconsForGrayoutOption(sceneObject)
                    setBatteryViews(sceneObject)

                    handleClickListeners(sceneObject, position)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun setBatteryViews(sceneObject: SceneObject?) {
            if (sceneObject?.isBatteryPluggedIn!!) {
                itemBinding.ivBatteryStatus.visibility = View.VISIBLE
                if (sceneObject.batteryType == null) {
                    itemBinding.ivBatteryStatus.visibility = View.GONE
                    return
                }
                when (sceneObject.batteryType!!) {
                    BatteryType.BATTERY_LOW, BatteryType.BATTERY_WARNING -> {
                        if (sceneObject.isBatteryCharging) {
                            itemBinding.ivBatteryStatus.setImageResource(R.drawable.riva_battery_low_charging)
                        } else {
                            itemBinding.ivBatteryStatus.setImageResource(R.drawable.riva_battery_low)
                        }
                    }

                    BatteryType.BATTERY_MIDDLE -> {
                        if (sceneObject.isBatteryCharging) {
                            itemBinding.ivBatteryStatus.setImageResource(R.drawable.riva_battery_medium_charging)
                        } else {
                            itemBinding.ivBatteryStatus.setImageResource(R.drawable.riva_battery_medium)
                        }
                    }

                    BatteryType.BATTERY_HIGH -> {
                        if (sceneObject.isBatteryCharging) {
                            itemBinding.ivBatteryStatus.setImageResource(R.drawable.riva_battery_high_charging)
                        } else {
                            itemBinding.ivBatteryStatus.setImageResource(R.drawable.riva_battery_high)
                        }
                    }
                }
            } else {
                itemBinding.ivBatteryStatus.visibility = View.GONE
            }
        }

        private fun clearViews() {
            itemBinding.tvDeviceName.text = ""
            itemBinding.ilMusicPlayingWidget.tvTrackName.text = ""
            itemBinding.ilMusicPlayingWidget.tvAlbumName.text = ""
        }

        private fun handleClickListeners(sceneObject: SceneObject?, position: Int) {

            itemBinding.cvSpeaker.setOnClickListener {
                if (sceneObjectMap.keys.isNotEmpty()) {
                    gotoSourcesOption(sceneObject?.ipAddress!!, sceneObject.currentSource)
                } else {

                    /**notifying adapter as sometime sceneObject is present but not lssdp node */
                    LibreLogger.d(TAG,"notifyDataSetChanged handleClickListeners")
                    notifyDataSetChanged()
                }
            }

            itemBinding.ilMusicPlayingWidget.flMusicPlayWidget.setOnClickListener {
                if (itemBinding.ilMusicPlayingWidget.tvTrackName.text?.toString()
                        ?.contains(context.getString(R.string.app_name))!! || itemBinding.ilMusicPlayingWidget.tvTrackName.text?.toString()
                        ?.contains(context.getString(R.string.login_to_enable_cmds))!! || itemBinding.ilMusicPlayingWidget.ivPlayPause.visibility == View.GONE) {
                    return@setOnClickListener
                }

                context.startActivity(Intent(context, CTNowPlayingActivity::class.java).apply {
                    putExtra(Constants.CURRENT_DEVICE_IP, sceneObject?.ipAddress)
                })
            }

            itemBinding.ilMusicPlayingWidget.ivPlayPause.setOnClickListener {

                val mScanHandler = ScanningHandler.getInstance()
                val mNodeWeGotForControl =
                    mScanHandler.getLSSDPNodeFromCentralDB(sceneObject?.ipAddress)
                        ?: return@setOnClickListener
                if (sceneObject == null) return@setOnClickListener

                if (sceneObject.currentSource == Constants.AUX_SOURCE/*|| sceneObject!!.currentSource == Constants.EXTERNAL_SOURCE*/ || sceneObject.currentSource == Constants.GCAST_SOURCE || sceneObject.currentSource == Constants.VTUNER_SOURCE || sceneObject.currentSource == Constants.TUNEIN_SOURCE || sceneObject.currentSource == Constants.BT_SOURCE && (mNodeWeGotForControl.getgCastVerision() == null && (mNodeWeGotForControl.bT_CONTROLLER == SceneObject.CURRENTLY_NOTPLAYING || mNodeWeGotForControl.bT_CONTROLLER == SceneObject.CURRENTLY_PLAYING) || (mNodeWeGotForControl.getgCastVerision() != null && mNodeWeGotForControl.bT_CONTROLLER < SceneObject.CURRENTLY_PAUSED))) {
                    val error =
                        LibreError("", Constants.PLAY_PAUSE_NOT_ALLOWED, 1)//TimeoutValue !=0 means ,its VERYSHORT
                    BusProvider.getInstance().post(error)
                    return@setOnClickListener
                }

                /* For Playing , If DMR is playing then we should give control for Play/Pause*/
                if (AppUtils.isActivePlaylistNotAvailable(sceneObject)) {
                    LibreLogger.d(TAG, "currently not playing, so take user to sources option activity")

                    val error = LibreError("", context.getString(R.string.no_active_playlist), 1)
                    BusProvider.getInstance().post(error)

                    gotoSourcesOption(sceneObject.ipAddress, sceneObject.currentSource)
                    return@setOnClickListener
                }

                LibreLogger.d(TAG, "current source is not DMR" + sceneObject.currentSource)

                if (sceneObject.playstatus == SceneObject.CURRENTLY_PLAYING) {
                    LUCIControl.SendCommandWithIp(MIDCONST.MID_PLAYCONTROL.toInt(), LUCIMESSAGES.PAUSE, LSSDPCONST.LUCI_SET, sceneObject.ipAddress)
                    itemBinding.ilMusicPlayingWidget.ivPlayPause.setImageResource(R.drawable.play_orange)
                } else {
                    if (sceneObject.currentSource == Constants.BT_SOURCE) { /* Change Done By Karuna, Because for BT Source there is no RESUME*/
                        LUCIControl.SendCommandWithIp(MIDCONST.MID_PLAYCONTROL.toInt(), LUCIMESSAGES.PLAY, LSSDPCONST.LUCI_SET, sceneObject.ipAddress)
                    } else {
                        LUCIControl.SendCommandWithIp(MIDCONST.MID_PLAYCONTROL.toInt(), LUCIMESSAGES.RESUME, LSSDPCONST.LUCI_SET, sceneObject.ipAddress)
                    }
                    itemBinding.ilMusicPlayingWidget.ivPlayPause.setImageResource(R.drawable.pause_orange)
                }
            }


            itemBinding.seekBarVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    if (seekBar.progress == 0) {
                        itemBinding.ivVolumeMute.setImageResource(R.drawable.ic_volume_mute)
                    } else itemBinding.ivVolumeMute.setImageResource(R.drawable.volume_low_enabled)

                    LUCIControl.SendCommandWithIp(MIDCONST.VOLUME_CONTROL, "" + seekBar.progress, LSSDPCONST.LUCI_SET, sceneObject?.ipAddress)
                    if(sceneObject!=null) {
                        sceneObject.volumeValueInPercentage = seekBar.progress
                        ScanningHandler.getInstance().putSceneObjectToCentralRepo(sceneObject.ipAddress, sceneObject)
                    }
                   /* LibreLogger.d(TAG, "seekBarVolume  AT last ${sceneObject!!.volumeValueInPercentage}")*/
                }
            })

            itemBinding.ilMusicPlayingWidget.ibAlexaAvsBtn.setOnLongClickListener {

                if (!isMicrophonePermissionGranted()) {
                    (context as CTDeviceDiscoveryActivity).requestRecordPermission()
                    return@setOnLongClickListener true
                }

                val lssdpNodes = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(sceneObject?.ipAddress)
                if (lssdpNodes == null || lssdpNodes.alexaRefreshToken.isNullOrEmpty() || lssdpNodes.alexaRefreshToken == "0") {
                    toggleAVSViews(false)
                    (context as CTDeviceDiscoveryActivity).showLoginWithAmazonAlert(sceneObject?.ipAddress!!)
                    return@setOnLongClickListener true
                }

                val phoneIp = Utils().getIPAddress(true)
                if (!sceneObject?.ipAddress.isNullOrEmpty() && !phoneIp.isNullOrEmpty()) {

                    LibreLogger.d(TAG,"OnLongClick phone ip: " + phoneIp + "port: " + MicTcpServer.MIC_TCP_SERVER_PORT)
                    LUCIControl(sceneObject?.ipAddress).SendCommand(MIDCONST.MID_MIC, Constants.START_MIC + phoneIp + "," + MicTcpServer.MIC_TCP_SERVER_PORT, LSSDPCONST.LUCI_SET)

                    toggleAVSViews(true)
                    audioRecordUtil?.startRecording(this@CTDeviceListAdapter)
                } else {
                    toggleAVSViews(showListening = false)
                    (context as CTDeviceDiscoveryActivity).showToast("Ip not available")
                }

                return@setOnLongClickListener true
            }

            itemBinding.ilMusicPlayingWidget.ibAlexaAvsBtn.setOnTouchListener { view, motionEvent ->
                LibreLogger.d(TAG,"AlexaBtn motionEvent = " + motionEvent.action)
                if (motionEvent.action == MotionEvent.ACTION_UP || motionEvent.action == MotionEvent.ACTION_CANCEL) {/*if (motionEvent.action != MotionEvent.ACTION_DOWN) {*/
                    LibreLogger.d(TAG,"AlexaBtn long press release, sceneObject isLongPressed = " + sceneObject?.isAlexaBtnLongPressed)
                    toggleAVSViews(false)
                }
                return@setOnTouchListener false
            }

            itemBinding.ivDeviceSettings.setOnClickListener {
                context.startActivity(Intent(context, CTDeviceSettingsActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra(Constants.CURRENT_DEVICE_IP, sceneObject?.ipAddress)
                    putExtra(Constants.FROM_ACTIVITY, CTActiveDevicesFragment::class.java.simpleName)
                })
            }
            itemBinding.ivDeviceCast.setOnClickListener {
                context.startActivity(Intent(context, ActivateCastActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra(Constants.CURRENT_DEVICE_IP, sceneObject?.ipAddress)
                    putExtra(Constants.DEVICE_NAME, sceneObject?.sceneName)
                    putExtra(Constants.FROM_ACTIVITY, CTActiveDevicesFragment::class.java.simpleName)
                })
            }
        }

        private fun updatePlayPause(sceneObject: SceneObject?) {
            if (sceneObject != null && (sceneObject.currentSource == Constants.VTUNER_SOURCE || sceneObject.currentSource == Constants.TUNEIN_SOURCE || sceneObject.currentSource == Constants.BT_SOURCE || sceneObject.currentSource == Constants.AUX_SOURCE/*|| sceneObject.currentSource == Constants.EXTERNAL_SOURCE*/ || sceneObject.currentSource == Constants.NO_SOURCE || sceneObject.currentSource == Constants.GCAST_SOURCE || (sceneObject.currentSource == Constants.DMR_SOURCE && (sceneObject.playstatus == SceneObject.CURRENTLY_STOPPED || sceneObject.playstatus == SceneObject.CURRENTLY_NOTPLAYING)))) {
                itemBinding.ilMusicPlayingWidget.ivPlayPause.isEnabled = false
                itemBinding.ilMusicPlayingWidget.ivPlayPause.setImageResource(R.drawable.play_white)
                itemBinding.ilMusicPlayingWidget.seekBarSong.progress = 0
                itemBinding.ilMusicPlayingWidget.seekBarSong.secondaryProgress = 0
                itemBinding.ilMusicPlayingWidget.seekBarSong.max = 100
                itemBinding.ilMusicPlayingWidget.seekBarSong.isEnabled = false
            } else {
                itemBinding.ilMusicPlayingWidget.ivPlayPause.isEnabled = true
                itemBinding.ilMusicPlayingWidget.seekBarSong.isEnabled = true
            }
        }

        private fun setControlIconsForAlexa(currentSceneObject: SceneObject?, playPause: AppCompatImageView) {
            if (currentSceneObject == null) {
                return
            }
            val controlsArr = currentSceneObject.controlsValue ?: return
            playPause.isEnabled = controlsArr[0]
            playPause.isClickable = controlsArr[0]
            if (!controlsArr[0]) {
                playPause.setImageResource(R.drawable.play_white)
            }
        }

        private fun updateViews(sceneObject: SceneObject?) {
            LibreLogger.d(TAG, "updateViews ${sceneObject?.sceneName} current source = " +
                    "${sceneObject?.currentSource}")

            itemBinding.ilMusicPlayingWidget.ivCurrentSource.visibility = View.GONE
            itemBinding.ivAuxBt.visibility = View.GONE
            when (sceneObject?.currentSource) {

                Constants.NO_SOURCE, Constants.DDMSSLAVE_SOURCE -> {
                    itemBinding.ilMusicPlayingWidget.ivPlayPause.isEnabled = false
                    if (sceneObject.currentSource == Constants.NO_SOURCE) {
                        handleAlexaViews(sceneObject)
                    }
                }

                Constants.VTUNER_SOURCE, Constants.TUNEIN_SOURCE -> {
                    itemBinding.ilMusicPlayingWidget.ivPlayPause.setImageResource(R.drawable.play_white)
                    itemBinding.ilMusicPlayingWidget.seekBarSong.visibility = View.VISIBLE
                    itemBinding.ilMusicPlayingWidget.seekBarSong.progress = 0
                    itemBinding.ilMusicPlayingWidget.seekBarSong.isEnabled = false
                }

                /*For Riva Tunneling, When switched to Aux, its External Source*/
                Constants.AUX_SOURCE -> {
                    handleAlexaViews(sceneObject)
                    itemBinding.ivAuxBt.visibility = View.VISIBLE
                    itemBinding.ivAuxBt.setImageResource(R.drawable.ic_aux_in)
                }

                Constants.BT_SOURCE -> {/*itemBinding.tv_track_name.text = context.getText(R.string.bluetooth)
                    itemBinding.ilMusicPlayingWidget.tvAlbumName.visibility = View.GONE
                    itemBinding.iv_album_art.visibility = View.GONE
                    itemBinding.seek_bar_song.progress = 0
                    itemBinding.seek_bar_song.isEnabled = false

                    val mNode = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(sceneObject.ipAddress)
                            ?: return
                    LibreLogger.d(TAG, "BT controller value in sceneobject " + mNode.bT_CONTROLLER)
                    if (mNode.bT_CONTROLLER != 1 && mNode.bT_CONTROLLER != 2 && mNode.bT_CONTROLLER != 3) {
                        itemBinding.iv_play_pause.setImageResource(R.drawable.play_white)
                    }*/

                    handleAlexaViews(sceneObject)
                    itemBinding.ivAuxBt.visibility = View.VISIBLE
                    itemBinding.ivAuxBt.setImageResource(R.drawable.ic_bt_on)

                }

                Constants.GCAST_SOURCE -> {
                    //gCast is Playing
                    itemBinding.ilMusicPlayingWidget.ivPlayPause.setImageResource(R.drawable.play_orange)
                    itemBinding.ilMusicPlayingWidget.tvTrackName.text = "Casting"
                    itemBinding.ilMusicPlayingWidget.seekBarSong.isEnabled = false
                    itemBinding.ilMusicPlayingWidget.seekBarSong.progress = 0
                    itemBinding.ilMusicPlayingWidget.seekBarSong.isClickable = false

                    itemBinding.ilMusicPlayingWidget.seekBarSong.progress = 0
                    itemBinding.ilMusicPlayingWidget.seekBarSong.isEnabled = false
                }

                Constants.ALEXA_SOURCE, Constants.DMR_SOURCE, Constants.DMP_SOURCE, Constants.SPOTIFY_SOURCE, Constants.USB_SOURCE -> {
                    if (!sceneObject.trackName.isNullOrEmpty() && !sceneObject.trackName.equals("NULL", ignoreCase = true)) {
                        itemBinding.ilMusicPlayingWidget.ivPlayPause.visibility = View.VISIBLE
                        itemBinding.ilMusicPlayingWidget.tvTrackName.visibility = View.VISIBLE

                        if ((!sceneObject.album_name.isNullOrEmpty() && !sceneObject.album_name.equals("NULL", ignoreCase = true)) || (!sceneObject.artist_name.isNullOrEmpty() && !sceneObject.artist_name.equals("NULL", ignoreCase = true))) {
                            itemBinding.ilMusicPlayingWidget.tvAlbumName.visibility = View.VISIBLE
                        }

                        if (!sceneObject.album_art.isNullOrEmpty() && !sceneObject.album_art.equals("NULL", ignoreCase = true)) {
                            itemBinding.ilMusicPlayingWidget.ivAlbumArt.visibility = View.VISIBLE
                        }

                        if (sceneObject.totalTimeOfTheTrack > 0 && sceneObject.currentPlaybackSeekPosition >= 0) {
                            itemBinding.ilMusicPlayingWidget.seekBarSong.visibility = View.VISIBLE
                            itemBinding.ilMusicPlayingWidget.seekBarSong.isEnabled = true
                        }
                    } else {
                        handleAlexaViews(sceneObject)
                    }

                    if (sceneObject.currentSource == Constants.SPOTIFY_SOURCE) {
                        itemBinding.ilMusicPlayingWidget.ivCurrentSource.visibility = View.VISIBLE
                        itemBinding.ilMusicPlayingWidget.ivCurrentSource.setImageResource(R.mipmap.spotify)
                    }

                    if (sceneObject.currentSource == Constants.ALEXA_SOURCE) {

                        itemBinding.ilMusicPlayingWidget.ivCurrentSource.visibility = View.VISIBLE
                        itemBinding.ilMusicPlayingWidget.tvTrackName.visibility = View.VISIBLE
                        itemBinding.ilMusicPlayingWidget.ivAlbumArt.visibility = View.VISIBLE
                        itemBinding.ilMusicPlayingWidget.tvAlbumName.visibility = View.VISIBLE
                        itemBinding.ilMusicPlayingWidget.ivPlayPause.visibility = View.VISIBLE

//                        itemBinding.tv_track_name?.text=sceneObject.trackName
                        itemBinding.ilMusicPlayingWidget.tvAlbumName.text =
                            LibreApplication.currentArtistName


                        when {
                            sceneObject.playUrl?.contains("Spotify", true)!! -> itemBinding.ilMusicPlayingWidget.ivCurrentSource.setImageResource(R.mipmap.spotify)
                            sceneObject.playUrl?.contains("Deezer", true)!! -> itemBinding.ilMusicPlayingWidget.ivCurrentSource.setImageResource(R.mipmap.riva_deezer_icon)
                            sceneObject.playUrl?.contains("Pandora", true)!! -> itemBinding.ilMusicPlayingWidget.ivCurrentSource.setImageResource(R.mipmap.riva_pandora_icon)
                            sceneObject.playUrl?.contains("SiriusXM", true)!! -> itemBinding.ilMusicPlayingWidget.ivCurrentSource.setImageResource(R.drawable.small_sirius_logo)
                            sceneObject.playUrl?.contains("TuneIn", true)!! -> itemBinding.ilMusicPlayingWidget.ivCurrentSource.setImageResource(R.drawable.smal_tunein_logo)
                            sceneObject.playUrl?.contains("iHeartRadio", true)!! -> itemBinding.ilMusicPlayingWidget.ivCurrentSource.setImageResource(R.drawable.second_iheartradio_image2)
                            sceneObject.playUrl?.contains("Amazon Music", true)!! -> itemBinding.ilMusicPlayingWidget.ivCurrentSource.setImageResource(R.drawable.amazon_music_small)
                            else -> itemBinding.ilMusicPlayingWidget.ivCurrentSource.visibility =
                                View.GONE
                        }
                    }

                    if (sceneObject.currentSource == Constants.USB_SOURCE) {
                        itemBinding.ivAuxBt.visibility = View.VISIBLE
                        itemBinding.ivAuxBt.setImageResource(R.drawable.usb_storage_enabled)
                    }
                }

                else -> handleAlexaViews(sceneObject)
            }

            toggleAlexaBtnForSAMode()

            when (sceneObject?.tunnelingCurrentSource) {
                Constants.AUX_SOURCE -> {
                    itemBinding.ivAuxBt.visibility = View.VISIBLE
                    itemBinding.ivAuxBt.setImageResource(R.drawable.ic_aux_in)
                }

                Constants.BT_SOURCE -> {
                    itemBinding.ivAuxBt.visibility = View.VISIBLE
                    itemBinding.ivAuxBt.setImageResource(R.drawable.ic_bt_on)
                }

                Constants.USB_SOURCE -> {
                    itemBinding.ivAuxBt.visibility = View.VISIBLE
                    itemBinding.ivAuxBt.setImageResource(R.drawable.usb_storage_enabled)
                }

                Constants.TUNNELING_WIFI_SOURCE, -1 -> {
                    itemBinding.ivAuxBt.visibility = View.GONE
                }
            }

            if (AppUtils.isActivePlaylistNotAvailable(sceneObject)) {
                LibreLogger.d(TAG, "isActivePlaylistNotAvailable true")
                handleAlexaViews(sceneObject)
            }

            if (AppUtils.isDMRPlayingFromOtherPhone(sceneObject)) {
                LibreLogger.d(TAG, "isDMRPlayingFromOtherPhone true")
                itemBinding.ilMusicPlayingWidget.ivPlayPause.visibility = View.GONE
            }

            if (AppUtils.isLocalDMRPlaying(sceneObject)) {
                itemBinding.ilMusicPlayingWidget.ivPlayPause.visibility = View.VISIBLE
            }
        }

        private fun handleAlexaViews(sceneObject: SceneObject?) {
            itemBinding.ilMusicPlayingWidget.seekBarSong.visibility = View.GONE
            itemBinding.ilMusicPlayingWidget.ivAlbumArt.visibility = View.GONE
            itemBinding.ilMusicPlayingWidget.ivPlayPause.visibility = View.GONE
            itemBinding.ilMusicPlayingWidget.tvTrackName.text =
                context.getText(R.string.libre_voice)

            val node = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(sceneObject?.ipAddress)
           if(node.getmDeviceCap().getmSource().isAlexaAvsSource) {
               if (node?.alexaRefreshToken.isNullOrEmpty()) {
                   itemBinding.ilMusicPlayingWidget.tvTrackName.visibility = View.VISIBLE
                   itemBinding.ilMusicPlayingWidget.tvTrackName.text = context.getText(R.string.login_to_enable_cmds)
                   itemBinding.ilMusicPlayingWidget.tvAlbumName.visibility = View.GONE
               } else {
                   itemBinding.ilMusicPlayingWidget.tvAlbumName.visibility = View.VISIBLE
                   itemBinding.ilMusicPlayingWidget.tvTrackName.visibility = View.VISIBLE
                   itemBinding.ilMusicPlayingWidget.tvTrackName.text = context.getText(R.string.app_name)
                   itemBinding.ilMusicPlayingWidget.tvAlbumName.text = context.getText(R.string.speaker_ready_for_cmds)

               }
           }else{
               itemBinding.ilMusicPlayingWidget.tvTrackName.visibility = View.VISIBLE
               itemBinding.ilMusicPlayingWidget.tvTrackName.text = context.getText(R.string.app_name)
               itemBinding.ilMusicPlayingWidget.tvAlbumName.visibility = View.VISIBLE
               itemBinding.ilMusicPlayingWidget.tvAlbumName.text = context.getText(R.string.speaker_ready_for_use)
           }
        }

        private fun toggleAlexaBtnForSAMode() {
            val ssid = AppUtils.getConnectedSSID(context)
            itemBinding.ilMusicPlayingWidget.ibAlexaAvsBtn.isEnabled =
                ssid != null && !isConnectedToSAMode(ssid)
            if (itemBinding.ilMusicPlayingWidget.ibAlexaAvsBtn.isEnabled) {
                itemBinding.ilMusicPlayingWidget.ibAlexaAvsBtn.alpha = 1f
            } else itemBinding.ilMusicPlayingWidget.ibAlexaAvsBtn.alpha = .5f
        }

        private fun handleThePlayIconsForGrayoutOption(sceneObject: SceneObject?) {
            if (itemView != null) {
                if (!itemBinding.ilMusicPlayingWidget.ivPlayPause.isEnabled) {
                    itemBinding.ilMusicPlayingWidget.ivPlayPause.setImageResource(R.drawable.play_white)
                } else if (sceneObject?.playstatus == SceneObject.CURRENTLY_PLAYING) {
                    itemBinding.ilMusicPlayingWidget.ivPlayPause.setImageResource(R.drawable.pause_orange)
                } else {
                    itemBinding.ilMusicPlayingWidget.ivPlayPause.setImageResource(R.drawable.play_orange)
                }
            }
        }

        private fun toggleAVSViews(showListening: Boolean) {
            if (showListening) {
                itemBinding.ilMusicPlayingWidget.tvAlexaListening.visibility = View.VISIBLE
                itemBinding.ilMusicPlayingWidget.llPlayingLayout.visibility = View.INVISIBLE
            } else {
                itemBinding.ilMusicPlayingWidget.tvAlexaListening.visibility = View.GONE
                itemBinding.ilMusicPlayingWidget.llPlayingLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun gotoSourcesOption(ipaddress: String, currentSource: Int) {
        context.startActivity(Intent(context, CTMediaSourcesActivity::class.java).apply {
            putExtra(Constants.CURRENT_DEVICE_IP, ipaddress)
            putExtra(Constants.CURRENT_SOURCE, "" + currentSource)
        })
    }

    fun addDeviceToList(sceneObject: SceneObject?) {
        if (sceneObject == null) return
        LibreLogger.d(TAG,"addDevice ip ${sceneObject.ipAddress} scene ${sceneObject.trackName}")
        sceneObjectMap[sceneObject.ipAddress!!] = sceneObject
        LibreLogger.d(TAG,"notifyDataSetChanged addDeviceToList")
//        notifyDataSetChanged()

        /*To update particular item only*/
        val pos = ArrayList<SceneObject>(sceneObjectMap.values).indexOf(sceneObject)
        LibreLogger.d(TAG,"addDeviceToList pos = $pos, ${sceneObject.sceneName}")
        LibreLogger.d(TAG,"notifyDataSetChanged  notifyItemChanged addDeviceToList")
        notifyItemChanged(pos, sceneObject)
    }

    fun getDeviceSceneFromAdapter(deviceIp: String): SceneObject? {
        LibreLogger.d(TAG,"getDevice ip $deviceIp")
        return sceneObjectMap[deviceIp]
    }

    fun removeDeviceFromList(sceneObject: SceneObject?) {
        LibreLogger.d(TAG,"removeDevice ip ${sceneObject?.ipAddress} scene ${sceneObject?.trackName}")
        sceneObjectMap.remove(sceneObject?.ipAddress)
        LibreLogger.d(TAG,"notifyDataSetChanged removeDeviceFromList")
        notifyDataSetChanged()
    }

    fun addAllDevices(sceneObjectConcurrentMap: ConcurrentMap<String, SceneObject>) {

        sceneObjectMap.clear()
        sceneObjectConcurrentMap.forEach { (ipAddress, sceneObject) ->
            LibreLogger.d(TAG,"addAllDevices ip $ipAddress scene ${sceneObject.trackName}")
            sceneObjectMap[ipAddress] = sceneObject
        }
        LibreLogger.d(TAG,"notifyDataSetChanged addAllDevices")
        notifyDataSetChanged()
    }

    fun clear() {
        sceneObjectMap.clear()
        LibreLogger.d(TAG,"notifyDataSetChanged clear")
        notifyDataSetChanged()
    }

    override fun recordError(error: String?) {
        (context as CTDeviceDiscoveryActivity).showToast(error!!)
    }

    override fun recordStopped() {
        LibreLogger.d(TAG, "recordStopped")
    }

    override fun recordProgress(byteBuffer: ByteArray?) {
        LibreLogger.d(TAG, "recordProgress")
    }

    override fun sendBufferAudio(audioBufferBytes: ByteArray?) {
        micTcpServer?.sendDataToClient(audioBufferBytes)
    }

    override fun micExceptionCaught(e: java.lang.Exception?) {
        LibreLogger.d(TAG, "micExceptionCaught ${e?.message}")
        audioRecordUtil?.stopRecording()
    }

    private fun isMicrophonePermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return AppUtils.isPermissionGranted(context, Manifest.permission.RECORD_AUDIO)
        }
        return true
    }
}



