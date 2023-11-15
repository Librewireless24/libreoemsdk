package com.cumulations.libreV2.adapter

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.cumulations.libreV2.AppUtils
import com.cumulations.libreV2.AppUtils.setPlayPauseLoader
import com.cumulations.libreV2.ProgressButtonImageView
import com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity
import com.cumulations.libreV2.activity.CTDeviceSettingsActivity
import com.cumulations.libreV2.activity.CTMediaSourcesActivity
import com.cumulations.libreV2.activity.CTNowPlayingActivity
import com.cumulations.libreV2.activity.oem.ActivateCastActivity
import com.cumulations.libreV2.fragments.CTActiveDevicesFragment
import com.cumulations.libreV2.isConnectedToSAMode
import com.cumulations.libreV2.model.SceneObject
import com.cumulations.libreV2.model.SceneObject.CURRENTLY_BUFFERING
import com.cumulations.libreV2.model.SceneObject.CURRENTLY_PAUSED
import com.cumulations.libreV2.model.SceneObject.CURRENTLY_PLAYING
import com.cumulations.libreV2.model.SceneObject.CURRENTLY_STOPPED
import com.cumulations.libreV2.tcp_tunneling.enums.BatteryType
import com.libreAlexa.LErrorHandeling.LibreError
import com.libreAlexa.LibreApplication
import com.libreAlexa.R
import com.libreAlexa.Scanning.ScanningHandler
import com.libreAlexa.alexa.AudioRecordCallback
import com.libreAlexa.alexa.AudioRecordUtil
import com.libreAlexa.alexa.MicExceptionListener
import com.libreAlexa.alexa.MicTcpServer
import com.libreAlexa.constants.Constants.*
import com.libreAlexa.constants.LSSDPCONST
import com.libreAlexa.constants.LUCIMESSAGES
import com.libreAlexa.constants.MIDCONST
import com.libreAlexa.databinding.CtListItemSpeakersBinding
import com.libreAlexa.databinding.MusicPlayingWidgetBinding
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
        val itemBinding = CtListItemSpeakersBinding.inflate(LayoutInflater.from(context), parent,
            false)
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

        fun bindSceneObject(sceneObject: SceneObject?, position: Int) {
            LibreLogger.d(TAG, "bindSceneObject called currentSource ${sceneObject!!.currentSource}")
            try {
                val ipAddress = sceneObject.ipAddress
                //Shaik Commented to see for long and if not found it as unusable remove it
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
                    setCurrentTrackName(sceneObject,itemBinding.ilMusicPlayingWidget.tvTrackName)
                    setAlbumArtistName(sceneObject,itemBinding.ilMusicPlayingWidget.tvAlbumName)

                    /*this is to show loading dialog while we are preparing to play*/
                    if (sceneObject.currentSource != AUX_SOURCE/*&& sceneObject!!.currentSource != Constants.EXTERNAL_SOURCE*//* && sceneObject.currentSource != BT_SOURCE*/ && sceneObject.currentSource != GCAST_SOURCE) {

                        /*Album Art For All other Sources Except */
                        if (!sceneObject.album_art.isNullOrEmpty() && sceneObject.album_art.equals("coverart.jpg", ignoreCase = true)) {
                            val albumUrl = "http://" + sceneObject.ipAddress + "/" + "coverart.jpg"/* If Track Name is Different just Invalidate the Path And if we are resuming the Screen(Screen OFF and Screen ON) , it will not re-download it */

                            if (sceneObject.trackName != null && !currentTrackName.equals(sceneObject.trackName, ignoreCase = true)) {
                                currentTrackName = sceneObject.trackName!!
                                val mInvalidated =
                                    (context as CTDeviceDiscoveryActivity).mInvalidateTheAlbumArt(sceneObject, albumUrl)
                                LibreLogger.d(TAG, "Invalidated the URL $albumUrl Status $mInvalidated")
                            }

                            PicassoTrustCertificates.getInstance(context)
                                .load(albumUrl)
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
                    }else {
                        if (itemBinding.ilMusicPlayingWidget.ivAlbumArt.visibility != View.VISIBLE) {
                            itemBinding.ilMusicPlayingWidget.ivAlbumArt.visibility=View.VISIBLE
                        }
                        if (sceneObject.album_art != null) {
                            PicassoTrustCertificates.getInstance(context).load(sceneObject.album_art).placeholder(R.mipmap.album_art).error(R.mipmap.album_art).into(itemBinding.ilMusicPlayingWidget.ivAlbumArt)
                        }else{
                            itemBinding.ilMusicPlayingWidget.ivAlbumArt.setImageResource(R.mipmap.album_art)
                        }
                    }
                    LibreLogger.d(TAG, "Album art visibility Status " + sceneObject.playstatus)
                    when (sceneObject.playstatus) {
                        CURRENTLY_BUFFERING -> {
                            setPlayPauseLoader(itemBinding.ilMusicPlayingWidget.ivPlayPause, isEnabled = false, isLoader = true, image = 0)
                        }
                        CURRENTLY_PLAYING -> {
                            setPlayPauseLoader(itemBinding.ilMusicPlayingWidget.ivPlayPause, isEnabled = true, isLoader = false, image = R.drawable.pause_orange)
                        }
                         CURRENTLY_PAUSED -> {
                            setPlayPauseLoader(itemBinding.ilMusicPlayingWidget.ivPlayPause, isEnabled = true, isLoader = false, image = R.drawable.play_orange)
                        }
                        else -> setPlayPauseLoader(itemBinding.ilMusicPlayingWidget.ivPlayPause, isEnabled = false, isLoader = false, image = 0)
                    }

                    if (sceneObject.currentSource == ALEXA_SOURCE) {
                        setControlIconsForAlexa(sceneObject, itemBinding.ilMusicPlayingWidget.ivPlayPause)
                    }

                    val lsdpNodes = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(sceneObject.ipAddress)
                    /**
                     * Commented because Ramya & Sathish confirmed no need to show any icon in the
                     * discovery screen
                     */
                    /*if(lsdpNodes.getmDeviceCap().getmSource().isAlexaAvsSource){
                        itemBinding.ilMusicPlayingWidget.ibAlexaAvsBtn.visibility=View.VISIBLE
                    }else{
                        itemBinding.ilMusicPlayingWidget.ibAlexaAvsBtn.visibility=View.GONE
                    }*/

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
                    } else {
                        itemBinding.ivVolumeMute.setImageResource(R.drawable.volume_low_enabled)
                    }

                    //Seeting the Mute and UnMute status
                    if(sceneObject.mute_status==LUCIMESSAGES.MUTE){
                        itemBinding.ivVolumeMute.setImageResource(R.drawable.ic_volume_mute)
                    }else{
                        itemBinding.ivVolumeMute.setImageResource(R.drawable.volume_low_enabled)
                    }
                    updateViews(sceneObject)
                    updatePlayPause(sceneObject)
                    handleThePlayIconsForGrayOutOption(sceneObject)
                    setBatteryViews(sceneObject)
                    handleClickListeners(sceneObject, position)
                }else{
                    LibreLogger.d(TAG,"SceneObject is null")
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
                    val lsdpNodes = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(sceneObject?.ipAddress)
                    /*
                         * LatestDiscoveryChanges
                         */
                    val secureIP=ScanningHandler.getInstance().getSecureIP()
                    LibreLogger.d(TAG,"Is SecureDevice IP:- "+secureIP +" and " + "current Ip address: "+sceneObject?.ipAddress)
//                    if(secureIP==sceneObject?.ipAddress){
//                        val error = LibreError(lsdpNodes.secureIpaddress, SECURE_DEVICE, 1)
//                        BusProvider.getInstance().post(error) //not proper msg displayed here//suma
//                    }else {
                        gotoSourcesOption(sceneObject?.ipAddress!!, sceneObject.currentSource)
                    //}
                } else {
                    /**notifying adapter as sometime sceneObject is present but not lssdp node */
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
                    putExtra(CURRENT_DEVICE_IP, sceneObject?.ipAddress)
                })
            }

            itemBinding.ilMusicPlayingWidget.ivPlayPause.setOnClickListener {

                val mScanHandler = ScanningHandler.getInstance()
                val mNodeWeGotForControl = mScanHandler.getLSSDPNodeFromCentralDB(sceneObject?.ipAddress) ?: return@setOnClickListener
                if (sceneObject == null) return@setOnClickListener

                if (sceneObject.currentSource == AUX_SOURCE/*|| sceneObject!!.currentSource == Constants.EXTERNAL_SOURCE*//* || sceneObject.currentSource == GCAST_SOURCE*/ || sceneObject.currentSource == VTUNER_SOURCE || sceneObject.currentSource == TUNEIN_SOURCE /*|| sceneObject.currentSource == BT_SOURCE*/ && (mNodeWeGotForControl.getgCastVerision() == null && (mNodeWeGotForControl.bT_CONTROLLER == SceneObject.CURRENTLY_NOTPLAYING || mNodeWeGotForControl.bT_CONTROLLER == CURRENTLY_PLAYING) || (mNodeWeGotForControl.getgCastVerision() != null && mNodeWeGotForControl.bT_CONTROLLER < CURRENTLY_PAUSED))) {
                    val error = LibreError("", PLAY_PAUSE_NOT_ALLOWED, 1)//TimeoutValue !=0 means ,its VERYSHORT
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

                if (sceneObject.playstatus == CURRENTLY_PLAYING) {
                    LUCIControl.SendCommandWithIp(MIDCONST.MID_PLAYCONTROL.toInt(), LUCIMESSAGES.PAUSE, LSSDPCONST.LUCI_SET, sceneObject.ipAddress)
                    //SHAIK Commented Because of this is not the right way added CustomProgressBar
                    /*itemBinding.ilMusicPlayingWidget.ivPlayPause.setImageResource(R.drawable.play_orange)*/
                } else {
                    if (sceneObject.currentSource == BT_SOURCE) { /* Change Done By Karuna, Because for BT Source there is no RESUME*/
                        LUCIControl.SendCommandWithIp(MIDCONST.MID_PLAYCONTROL.toInt(), LUCIMESSAGES.PLAY, LSSDPCONST.LUCI_SET, sceneObject.ipAddress)
                    } else {
                        LUCIControl.SendCommandWithIp(MIDCONST.MID_PLAYCONTROL.toInt(), LUCIMESSAGES.RESUME, LSSDPCONST.LUCI_SET, sceneObject.ipAddress)
                    }
                    //SHAIK Commented Because of this is not the right way CustomProgressBar
              /*      itemBinding.ilMusicPlayingWidget.ivPlayPause.setImageResource(R.drawable.pause_orange)*/
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
                        sceneObject.mute_status=LUCIMESSAGES.UNMUTE
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
                    LUCIControl(sceneObject?.ipAddress).SendCommand(MIDCONST.MID_MIC, START_MIC + phoneIp + "," + MicTcpServer.MIC_TCP_SERVER_PORT, LSSDPCONST.LUCI_SET)

                    toggleAVSViews(true)
                    audioRecordUtil?.startRecording(this@CTDeviceListAdapter)
                } else {
                    toggleAVSViews(showListening = false)
                    (context as CTDeviceDiscoveryActivity).showToast("Ip not available")
                }

                return@setOnLongClickListener true
            }

            itemBinding.ilMusicPlayingWidget.ibAlexaAvsBtn.setOnTouchListener { view, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_UP || motionEvent.action == MotionEvent.ACTION_CANCEL) {/*if (motionEvent.action != MotionEvent.ACTION_DOWN) {*/
                    LibreLogger.d(TAG,"AlexaBtn long press release, sceneObject isLongPressed = " + sceneObject?.isAlexaBtnLongPressed)
                    toggleAVSViews(false)
                }
                return@setOnTouchListener false
            }

            itemBinding.ivDeviceSettings.setOnClickListener {
                context.startActivity(Intent(context, CTDeviceSettingsActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra(CURRENT_DEVICE_IP, sceneObject?.ipAddress)
                    putExtra(FROM_ACTIVITY, CTActiveDevicesFragment::class.java.simpleName)
                })
            }
            itemBinding.ivDeviceCast.setOnClickListener {
                context.startActivity(Intent(context, ActivateCastActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra(CURRENT_DEVICE_IP, sceneObject?.ipAddress)
                    putExtra(DEVICE_NAME, sceneObject?.sceneName)
                    putExtra(FROM_ACTIVITY, CTActiveDevicesFragment::class.java.simpleName)
                })
            }
            itemBinding.ivVolumeMute.setOnClickListener {
                if (sceneObject != null && sceneObject.mute_status!=null) {
                    if (sceneObject.mute_status == LUCIMESSAGES.UNMUTE) {
                        LUCIControl.SendCommandWithIp(MIDCONST.MID_PLAYCONTROL.toInt(), LUCIMESSAGES.MUTE, LSSDPCONST.LUCI_SET, sceneObject.ipAddress)
                    } else {
                        LUCIControl.SendCommandWithIp(MIDCONST.MID_PLAYCONTROL.toInt(), LUCIMESSAGES.UNMUTE, LSSDPCONST.LUCI_SET, sceneObject.ipAddress)
                    }
                }else{
                    LibreLogger.d(TAG,"sceneObject or mute status null")
                }
            }
        }

        private fun updatePlayPause(sceneObject: SceneObject?) {
            if (sceneObject != null && (sceneObject.currentSource == VTUNER_SOURCE || sceneObject.currentSource == TUNEIN_SOURCE || sceneObject.currentSource == BT_SOURCE || sceneObject.currentSource == AUX_SOURCE || sceneObject.currentSource == NO_SOURCE || (sceneObject.currentSource == DMR_SOURCE && (sceneObject.playstatus == CURRENTLY_STOPPED || sceneObject.playstatus == SceneObject.CURRENTLY_NOTPLAYING)))) {
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

        private fun setControlIconsForAlexa(currentSceneObject: SceneObject?, playPause: ProgressButtonImageView) {
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
            LibreLogger.d(TAG, "updateViews ${sceneObject?.playUrl} current source =  ${sceneObject?.currentSource}")
            itemBinding.ilMusicPlayingWidget.ivCurrentSource.visibility = View.GONE
            itemBinding.ivAuxBt.visibility = View.GONE
            if(sceneObject!=null) {
                updateCurrentSourceIcon(itemBinding.ilMusicPlayingWidget, sceneObject)
                LibreLogger.d(TAG, "updateCurrentSourceIcon source from:-  ${sceneObject.currentSource}")
            }
            when (sceneObject?.currentSource) {
                NO_SOURCE, DDMSSLAVE_SOURCE -> {
                    itemBinding.ilMusicPlayingWidget.ivPlayPause.isEnabled = false
                    /**
                     *Commented because we have handled in the new function called@setCurrentTrackName
                     */
                   /* if (sceneObject.currentSource == NO_SOURCE) {
                        handleAlexaViews(sceneObject)
                    }*/
                }

                VTUNER_SOURCE, TUNEIN_SOURCE -> {
                    itemBinding.ilMusicPlayingWidget.ivPlayPause.setImageResource(R.drawable.play_white)
                    itemBinding.ilMusicPlayingWidget.seekBarSong.visibility = View.VISIBLE
                    itemBinding.ilMusicPlayingWidget.seekBarSong.progress = 0
                    itemBinding.ilMusicPlayingWidget.seekBarSong.isEnabled = false
                }

                /*For Riva Tunneling, When switched to Aux, its External Source*/
                AUX_SOURCE -> {
                    /**
                     *Commented because we have handled in the new function called@setCurrentTrackName
                     */
                   // handleAlexaViews(sceneObject)
                    itemBinding.ivAuxBt.visibility = View.VISIBLE
                    itemBinding.ivAuxBt.setImageResource(R.drawable.ic_aux_in)
                }

                BT_SOURCE -> {
                    itemBinding.ilMusicPlayingWidget.ivCurrentSource.visibility = View.VISIBLE
                 //   itemBinding.ilMusicPlayingWidget.tvTrackName.visibility = View.VISIBLE
                    itemBinding.ilMusicPlayingWidget.ivAlbumArt.visibility = View.VISIBLE
                   // itemBinding.ilMusicPlayingWidget.tvAlbumName.visibility = View.VISIBLE
                    itemBinding.ilMusicPlayingWidget.ivPlayPause.visibility = View.VISIBLE
                }

                GCAST_SOURCE -> {
                    //itemBinding.ilMusicPlayingWidget.tvTrackName.text = sceneObject.trackName
                    itemBinding.ilMusicPlayingWidget.seekBarSong.isEnabled = false
                    itemBinding.ilMusicPlayingWidget.seekBarSong.progress = 0
                    itemBinding.ilMusicPlayingWidget.seekBarSong.isClickable = false
                }
                ALEXA_SOURCE, DMR_SOURCE, DMP_SOURCE, SPOTIFY_SOURCE, USB_SOURCE, TIDAL_SOURCE, AIRPLAY_SOURCE -> {
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
                    }
                    /**
                     *Commented because we have handled in the new function called@setCurrentTrackName
                     */
                    /*else {
                        handleAlexaViews(sceneObject)
                    }*/

                    if (sceneObject.currentSource == ALEXA_SOURCE) {
                        itemBinding.ilMusicPlayingWidget.ivCurrentSource.visibility = View.VISIBLE
                        itemBinding.ilMusicPlayingWidget.tvTrackName.visibility = View.VISIBLE
                        itemBinding.ilMusicPlayingWidget.ivAlbumArt.visibility = View.VISIBLE
                        itemBinding.ilMusicPlayingWidget.tvAlbumName.visibility = View.VISIBLE
                        itemBinding.ilMusicPlayingWidget.ivPlayPause.visibility = View.VISIBLE
                        itemBinding.ilMusicPlayingWidget.tvAlbumName.text = LibreApplication.currentArtistName

                        when {
                            sceneObject.playUrl?.contains("Spotify", true)!! -> itemBinding.ilMusicPlayingWidget.ivCurrentSource.setImageResource(R.mipmap.spotify)
                            sceneObject.playUrl?.contains("Deezer", true)!! -> itemBinding.ilMusicPlayingWidget.ivCurrentSource.setImageResource(R.mipmap.riva_deezer_icon)
                            sceneObject.playUrl?.contains("Pandora", true)!! -> itemBinding.ilMusicPlayingWidget.ivCurrentSource.setImageResource(R.mipmap.riva_pandora_icon)
                            sceneObject.playUrl?.contains("SiriusXM", true)!! -> itemBinding.ilMusicPlayingWidget.ivCurrentSource.setImageResource(R.drawable.small_sirius_logo)
                            sceneObject.playUrl?.contains("TuneIn", true)!! -> itemBinding.ilMusicPlayingWidget.ivCurrentSource.setImageResource(R.drawable.smal_tunein_logo)
                            sceneObject.playUrl?.contains("iHeartRadio", true)!! -> itemBinding.ilMusicPlayingWidget.ivCurrentSource.setImageResource(R.drawable.second_iheartradio_image2)
                            sceneObject.playUrl?.contains("Amazon Music", true)!! -> itemBinding.ilMusicPlayingWidget.ivCurrentSource.setImageResource(R.drawable.amazon_music_small)
                            else -> itemBinding.ilMusicPlayingWidget.ivCurrentSource.visibility = View.GONE

                        }
                    }

                    if (sceneObject.currentSource == USB_SOURCE) {
                        itemBinding.ivAuxBt.visibility = View.GONE
                        itemBinding.ivAuxBt.setImageResource(R.drawable.usb_storage_enabled)
                    }
                }
                /**
                 *Commented because we have handled in the new function called@setCurrentTrackName
                 */
                //else -> handleAlexaViews(sceneObject)
            }

            toggleAlexaBtnForSAMode()

            when (sceneObject?.tunnelingCurrentSource) {
                AUX_SOURCE -> {
                    itemBinding.ivAuxBt.visibility = View.VISIBLE
                    itemBinding.ivAuxBt.setImageResource(R.drawable.ic_aux_in)
                }

                BT_SOURCE -> {
                    itemBinding.ivAuxBt.visibility = View.VISIBLE
                    itemBinding.ivAuxBt.setImageResource(R.drawable.ic_bt_on)
                }

                USB_SOURCE -> {
                    itemBinding.ivAuxBt.visibility = View.GONE
                    itemBinding.ivAuxBt.setImageResource(R.drawable.usb_storage_enabled)
                }

                TUNNELING_WIFI_SOURCE, -1 -> {
                    itemBinding.ivAuxBt.visibility = View.GONE
                }
            }

            if (AppUtils.isActivePlaylistNotAvailable(sceneObject)) {
                handleAlexaViews(sceneObject,1)
            }

            if (AppUtils.isDMRPlayingFromOtherPhone(sceneObject)) {
                itemBinding.ilMusicPlayingWidget.ivPlayPause.visibility = View.GONE
            }

            if (AppUtils.isLocalDMRPlaying(sceneObject)) {
                itemBinding.ilMusicPlayingWidget.ivPlayPause.visibility = View.VISIBLE
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

        private fun handleThePlayIconsForGrayOutOption(sceneObject: SceneObject?) {
            LibreLogger.d(TAG,"handleThePlayIconsForGrayOutOption "+sceneObject?.playstatus)
                when (sceneObject?.playstatus) {
                    CURRENTLY_BUFFERING -> {
                        setPlayPauseLoader(itemBinding.ilMusicPlayingWidget.ivPlayPause, isEnabled = false, isLoader = true, image = 0)
                    }

                    CURRENTLY_PLAYING -> {
                        setPlayPauseLoader(itemBinding.ilMusicPlayingWidget.ivPlayPause, isEnabled = true, isLoader = false, image = R.drawable.pause_orange)
                    }

                     CURRENTLY_PAUSED -> {
                        setPlayPauseLoader(itemBinding.ilMusicPlayingWidget.ivPlayPause, isEnabled = true, isLoader = false, image = R.drawable.play_orange)
                    }

                    else -> setPlayPauseLoader(itemBinding.ilMusicPlayingWidget.ivPlayPause, isEnabled = false, isLoader = false, image = 0)
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
        private fun setCurrentTrackName(sceneObject: SceneObject, tvTrackName: AppCompatTextView) {
            LibreLogger.d(TAG,"setCurrentTrackName trackName:- ${sceneObject.trackName}  and sourceName:- ${sceneObject.currentSource} ")
            if(sceneObject.currentSource!= NO_SOURCE ||sceneObject.currentSource!= AUX_SOURCE ) {
                if (!sceneObject.trackName.isNullOrEmpty() && !sceneObject.trackName.equals("NULL", ignoreCase = true)) {
                    var trackname = sceneObject.trackName

                    /* This change is done to handle the case of deezer where the song name is appended by radio or skip enabled */
                    if (trackname.contains(DEZER_RADIO)) trackname = trackname.replace(DEZER_RADIO, "")

                    if (trackname.contains(DEZER_SONGSKIP)) trackname = trackname.replace(DEZER_SONGSKIP, "")
                    trackname = trackname.replace(DEZER_SONGSKIP, "")
                    if (tvTrackName.text.toString() != trackname) {
                        tvTrackName.text = trackname
                    }
                }
                else
                {
                    //Setting the not available because to debug the issue
                    tvTrackName.setText(R.string.trackname_not_available)
                }
            }
            else{
                handleAlexaViews(sceneObject, 2)
            }

        }
        private fun setAlbumArtistName(sceneObject: SceneObject, tvAlbumName: AppCompatTextView) {
            LibreLogger.d(TAG,"setCurrentTrackName trackName:- ${sceneObject.trackName}  and sourceName:- ${sceneObject.currentSource} ")
            if(sceneObject.currentSource!= NO_SOURCE ||sceneObject.currentSource!= AUX_SOURCE ) {
                if (!sceneObject.artist_name.isNullOrEmpty() && !sceneObject.artist_name.equals("null", ignoreCase = true)) {
                    if (tvAlbumName.text.toString() != sceneObject.artist_name) {
                        tvAlbumName.text = sceneObject.artist_name
                    }
                }
                else if (!sceneObject.album_name.isNullOrEmpty() && !sceneObject.album_name.equals("null", ignoreCase = true)) {
                    if (tvAlbumName.text.toString() != sceneObject.album_name) {
                        tvAlbumName.text = sceneObject.album_name
                    }
                }
                else {
                    //Setting the not available because to debug the issue
                    tvAlbumName.setText(R.string.albumname_not_available)
                }
            }
        }

        private fun handleAlexaViews(sceneObject: SceneObject?, i: Int) {
            LibreLogger.d(TAG,"setCurrentTrackName handleAlexaViews called from  $i")
            itemBinding.ilMusicPlayingWidget.seekBarSong.visibility = View.GONE
            itemBinding.ilMusicPlayingWidget.ivAlbumArt.visibility = View.GONE
            itemBinding.ilMusicPlayingWidget.ivPlayPause.visibility = View.GONE
            itemBinding.ilMusicPlayingWidget.tvTrackName.text = context.getText(R.string.libre_voice)

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

    }


    private fun gotoSourcesOption(ipaddress: String, currentSource: Int) {
        context.startActivity(Intent(context, CTMediaSourcesActivity::class.java).apply {
            putExtra(CURRENT_DEVICE_IP, ipaddress)
            putExtra(CURRENT_SOURCE, "" + currentSource)
        })
    }

    fun addDeviceToList(sceneObject: SceneObject?) {
        if (sceneObject == null) return
        LibreLogger.d(TAG,"addDevice ip ${sceneObject.ipAddress} scene ${sceneObject.trackName}")
        sceneObjectMap[sceneObject.ipAddress!!] = sceneObject
//        notifyDataSetChanged()

        /*To update particular item only*/
        val pos = ArrayList<SceneObject>(sceneObjectMap.values).indexOf(sceneObject)
        LibreLogger.d(TAG,"addDeviceToList pos = $pos, ${sceneObject.sceneName}")
        notifyItemChanged(pos, sceneObject)
    }

    fun getDeviceSceneFromAdapter(deviceIp: String): SceneObject? {
        return sceneObjectMap[deviceIp]
    }

    fun removeDeviceFromList(sceneObject: SceneObject?) {
        LibreLogger.d(TAG,"removeDevice ip ${sceneObject?.ipAddress} scene ${sceneObject?.trackName}")
        sceneObjectMap.remove(sceneObject?.ipAddress)
        notifyDataSetChanged()
    }

    fun addAllDevices(sceneObjectConcurrentMap: ConcurrentMap<String, SceneObject>) {

        sceneObjectMap.clear()
        sceneObjectConcurrentMap.forEach { (ipAddress, sceneObject) ->
            sceneObjectMap[ipAddress] = sceneObject
        }
        notifyDataSetChanged()
    }

    fun clear() {
        sceneObjectMap.clear()
        notifyDataSetChanged()
    }

    override fun recordError(error: String?) {
        (context as CTDeviceDiscoveryActivity).showToast(error!!)
    }

    override fun recordStopped() {
    }

    override fun recordProgress(byteBuffer: ByteArray?) {
    }

    override fun sendBufferAudio(audioBufferBytes: ByteArray?) {
        micTcpServer?.sendDataToClient(audioBufferBytes)
    }

    override fun micExceptionCaught(e: java.lang.Exception?) {
        audioRecordUtil?.stopRecording()
    }

    private fun isMicrophonePermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return AppUtils.isPermissionGranted(context, Manifest.permission.RECORD_AUDIO)
        }
        return true
    }


    /**
     *  The below function will work for setting the source icon
     *  if we want to show icon we can set the icon other wise make visibility gone
     *  for some sources we are making visibility gone for now once we get confirmation from
     *  Device team we can enable or dependents on customer requirement
     */
    private fun updateCurrentSourceIcon(itemBinding: MusicPlayingWidgetBinding, sceneObject: SceneObject?) {
        LibreLogger.d(TAG,"Current source is :- "+sceneObject?.currentSource)
        if (itemBinding.ivCurrentSource.visibility == View.GONE) {
            itemBinding.ivCurrentSource.visibility = View.VISIBLE
            when (sceneObject?.currentSource) {
                NO_SOURCE -> itemBinding.ivCurrentSource.visibility = View.GONE
                AIRPLAY_SOURCE -> itemBinding.ivCurrentSource.setImageResource(R.drawable.airplay)
                DMR_SOURCE -> itemBinding.ivCurrentSource.setImageResource(R.drawable.my_device_enabled)
                DMP_SOURCE -> itemBinding.ivCurrentSource.setImageResource(R.drawable.media_servers_enabled)
                SPOTIFY_SOURCE -> itemBinding.ivCurrentSource.setImageResource(R.mipmap.spotify)
                USB_SOURCE -> itemBinding.ivCurrentSource.setImageResource(R.drawable.usb_storage_enabled)
                SDCARD_SOURCE -> itemBinding.ivCurrentSource.setImageResource(R.mipmap.sdcard)
                MELON_SOURCE -> itemBinding.ivCurrentSource.visibility = View.GONE
                VTUNER_SOURCE -> itemBinding.ivCurrentSource.setImageResource(R.mipmap.vtuner_logo)
                TUNEIN_SOURCE -> itemBinding.ivCurrentSource.setImageResource(R.mipmap.tunein_logo1)
                MIRACAST_SOURCE -> itemBinding.ivCurrentSource.visibility = View.GONE
                DDMSSLAVE_SOURCE ->itemBinding.ivCurrentSource.visibility = View.GONE
                AUX_SOURCE -> itemBinding.ivCurrentSource.setImageResource(R.drawable.ic_aux_in)
                //Dummy Image for now
                APPLEDEVICE_SOURCE -> itemBinding.ivCurrentSource.setImageResource(R.drawable.airplay_logo)
                DIRECTURL_SOURCE -> itemBinding.ivCurrentSource.visibility = View.GONE
                BT_SOURCE -> itemBinding.ivCurrentSource.setImageResource(R.drawable.bluetooth_icon)
                DEEZER_SOURCE -> itemBinding.ivCurrentSource.setImageResource(R.mipmap.deezer_logo)
                TIDAL_SOURCE -> itemBinding.ivCurrentSource.setImageResource(R.mipmap.tidal_white_logo)
                FAVOURITES_SOURCE -> itemBinding.ivCurrentSource.setImageResource(R.mipmap.ic_remote_favorite)
                GCAST_SOURCE -> itemBinding.ivCurrentSource.setImageResource(R.drawable.chrome_cast_enabled)
                EXTERNAL_SOURCE -> itemBinding.ivCurrentSource.visibility = View.GONE
                OPTICAL_SOURCE -> itemBinding.ivCurrentSource.visibility = View.GONE
                TUNNELING_WIFI_SOURCE -> itemBinding.ivCurrentSource.visibility = View.GONE
            }
        } else {
            itemBinding.ivCurrentSource.visibility = View.GONE
        }

    }
}



