package com.cumulations.libreV2.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cumulations.libreV2.AppUtils
import com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity
import com.cumulations.libreV2.activity.CTHomeTabsActivity
import com.cumulations.libreV2.adapter.CTDeviceListAdapter
import com.cumulations.libreV2.model.SceneObject
import com.cumulations.libreV2.tcp_tunneling.TCPTunnelPacket
import com.cumulations.libreV2.tcp_tunneling.TunnelingControl
import com.cumulations.libreV2.tcp_tunneling.TunnelingData
import com.cumulations.libreV2.tcp_tunneling.TunnelingFragmentListener
import com.libreAlexa.LErrorHandeling.LibreError
import com.libreAlexa.LibreApplication
import com.libreAlexa.R
import com.libreAlexa.Scanning.ScanningHandler
import com.libreAlexa.constants.Constants.BT_SOURCE
import com.libreAlexa.constants.Constants.DMR_SONG_UNSUPPORTED
import com.libreAlexa.constants.Constants.EXTERNAL_SOURCE
import com.libreAlexa.constants.Constants.FAIL
import com.libreAlexa.constants.Constants.FAIL_ALERT_TEXT
import com.libreAlexa.constants.Constants.NO_NEXT_SONG
import com.libreAlexa.constants.Constants.NO_PREV_SONG
import com.libreAlexa.constants.Constants.NO_SOURCE
import com.libreAlexa.constants.Constants.NO_URL
import com.libreAlexa.constants.Constants.NO_URL_ALERT_TEXT
import com.libreAlexa.constants.Constants.PREPARATION_COMPLETED
import com.libreAlexa.constants.Constants.PREPARATION_INIT
import com.libreAlexa.constants.Constants.PREPARATION_TIMEOUT
import com.libreAlexa.constants.Constants.PREPARATION_TIMEOUT_CONST
import com.libreAlexa.constants.Constants.SUCCESS
import com.libreAlexa.constants.LUCIMESSAGES
import com.libreAlexa.constants.MIDCONST
import com.libreAlexa.databinding.CtFragmentDiscoveryListBinding
import com.libreAlexa.luci.LSSDPNodeDB
import com.libreAlexa.luci.LSSDPNodes
import com.libreAlexa.luci.LUCIPacket
import com.libreAlexa.netty.LibreDeviceInteractionListner
import com.libreAlexa.netty.NettyData
import com.libreAlexa.util.LibreLogger
import com.libreAlexa.util.PicassoTrustCertificates
import org.json.JSONObject
import java.util.Locale

class CTActiveDevicesFragment:Fragment(),LibreDeviceInteractionListner,TunnelingFragmentListener {
    private val mScanHandler = ScanningHandler.getInstance()
    private lateinit var deviceListAdapter:CTDeviceListAdapter
    private lateinit var binding: CtFragmentDiscoveryListBinding
    private val TAG = CTActiveDevicesFragment::class.java.simpleName
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = CtFragmentDiscoveryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    private fun initViews(view: View?) {
        if (LSSDPNodeDB.getInstance().GetDB().size <= 0) {
            (activity as CTHomeTabsActivity).openFragment(CTNoDeviceFragment::class.java.simpleName, animate = true)
            return
        }

        deviceListAdapter = CTDeviceListAdapter(requireActivity())
        binding.rvDeviceList.layoutManager = LinearLayoutManager(activity)
        binding.rvDeviceList.adapter = deviceListAdapter
    }

    override fun onResume() {
        super.onResume()

        (activity as CTDeviceDiscoveryActivity).registerForDeviceEvents(this)
        (activity as CTHomeTabsActivity).setTunnelFragmentListener(this)

        updateFromCentralRepositryDeviceList()
    }

    internal var handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {

            if (msg.what == PREPARATION_INIT) {
                val ipaddress = msg.data.getString("ipAddress")
                triggerTimeOutHandlerForIpAddress(ipaddress!!)
            }
            if (msg.what == PREPARATION_COMPLETED) {
                val ipaddress = msg.data.getString("ipAddress")
                removeTimeOutTriggerForIpaddress(ipaddress!!)
            }
        }
    }

    private fun removeTimeOutTriggerForIpaddress(ipAddress: String) {

        handler.removeMessages(PREPARATION_TIMEOUT_CONST, ipAddress)
        val mSceneObject = mScanHandler.getSceneObjectFromCentralRepo(ipAddress)
        if (mSceneObject != null) {
            mSceneObject.preparingState = SceneObject.PREPARING_STATE.PREPARING_SUCCESS
        }
        deviceListAdapter.notifyDataSetChanged()
    }

    private fun triggerTimeOutHandlerForIpAddress(ipAddress: String) {
        val timeOutMessage = Message()
        /* Added by Praveen to address the crash for PREPARATION_TIMEOUT_CONST */
        val data = Bundle()
        data.putString("ipAddress", ipAddress)
        timeOutMessage.data = data

        timeOutMessage.obj = ipAddress
        timeOutMessage.what = PREPARATION_TIMEOUT_CONST
        handler.sendMessageDelayed(timeOutMessage, PREPARATION_TIMEOUT.toLong())

        val mSceneObject = mScanHandler.getSceneObjectFromCentralRepo(ipAddress)
        if (mSceneObject != null) {
            mSceneObject.preparingState = SceneObject.PREPARING_STATE.PREPARING_INITIATED
        }
        deviceListAdapter.notifyDataSetChanged()
    }

    private var mDeviceFound: Boolean = false
    private var mMasterFound: Boolean = false

    fun updateFromCentralRepositryDeviceList() {
        var sceneKeySet = ScanningHandler.getInstance().sceneObjectMapFromRepo.keys.toTypedArray()
        LibreLogger.d(TAG, "Master is Getting Added T Active Scenes Adapter" + mScanHandler.sceneObjectMapFromRepo.keys)
        /* Fix For If Device is Available But SceneObject is Not Available in the Network */
        if (sceneKeySet.isEmpty()) {
            if (LSSDPNodeDB.getInstance().isDeviceInNodeRepo) {
                for (nodes in LSSDPNodeDB.getInstance().GetDB()) {
                    if (!ScanningHandler.getInstance().isIpAvailableInCentralSceneRepo(nodes.ip)
                            /*&& LSSDPNodeDB.getInstance().hasFilteredModels(nodes)*/) {
                        val sceneObject = SceneObject(" ", nodes.friendlyname, 0f, nodes.ip)
                        ScanningHandler.getInstance().putSceneObjectToCentralRepo(nodes.ip, sceneObject)
                        LibreLogger.d(TAG, "Device is not available in Central Repo So Created SceneObject " + nodes.ip)
                    }
                }
            }
        }

        sceneKeySet = ScanningHandler.getInstance().sceneObjectMapFromRepo.keys.toTypedArray()

        deviceListAdapter.clear()
        if (sceneKeySet.isEmpty()){
//            refreshDevices()
            (activity as CTHomeTabsActivity).openFragment(CTNoDeviceFragment::class.java.simpleName,animate = false)
        } else {
            for (sceneIp in sceneKeySet) {
                val sceneObject = ScanningHandler.getInstance().getSceneObjectFromCentralRepo(sceneIp)
                sceneObject?.clearBatteryStats()
                deviceListAdapter.addDeviceToList(sceneObject)
                (activity as CTDeviceDiscoveryActivity).requestLuciUpdates(sceneIp)
             //  enabling  LibreLogger.d(TAG,"requestLuciUpdates ONE ")
                /*Get Tunneling Data*/
                TunnelingControl(sceneIp).sendDataModeCommand()
            }
        }

    }

    override fun deviceDiscoveryAfterClearingTheCacheStarted() {

    }

    override fun newDeviceFound(node: LSSDPNodes) {
        /*if (!LSSDPNodeDB.getInstance().hasFilteredModels(node)){
            LibreLogger.d(TAG,"newDeviceFound, non filter = "+node.friendlyname)
            return
        }*/

        LibreLogger.d(TAG,"newDeviceFound device found ip ${node.ip} Device State ${node.deviceState}")
        mDeviceFound = true

       //(activity as CTDeviceDiscoveryActivity).requestLuciUpdates(node.ip)
       // enabling LibreLogger.d(TAG,"requestLuciUpdates TWO ")

        val sceneObject = SceneObject(" ", node.friendlyname, 0f, node.ip)
        if (!mScanHandler.isIpAvailableInCentralSceneRepo(node.ip)) {
            mScanHandler.putSceneObjectToCentralRepo(node.ip, sceneObject)
        } else {
            val repoSceneObject = mScanHandler.getSceneObjectFromCentralRepo(node.ip)
            if (repoSceneObject != null)
                sceneObject.sceneName = repoSceneObject.sceneName
        }

        deviceListAdapter.addDeviceToList(sceneObject)
    }

    override fun deviceGotRemoved(ipaddress: String) {
        LibreLogger.d(TAG, "Device is Removed with the ip address = $ipaddress")

        val sceneObject = deviceListAdapter.getDeviceSceneFromAdapter(ipaddress)
        if (sceneObject != null) {
            deviceListAdapter.removeDeviceFromList(sceneObject)
            LibreLogger.d(TAG, "" + sceneObject.sceneName)

            if (deviceListAdapter.itemCount == 0) {
                (activity as CTHomeTabsActivity).refreshDevices()
            }
        }

        if (LSSDPNodeDB.getInstance().GetDB().size <= 0) {
            (activity as CTHomeTabsActivity).openFragment(CTNoDeviceFragment::class.java.simpleName,animate = true)
        }
    }

    override fun messageRecieved(nettyData: NettyData) {

        var sceneObject = mScanHandler.getSceneObjectFromCentralRepo(nettyData.getRemotedeviceIp())

        val luciPacket = LUCIPacket(nettyData.getMessage())
        val msg = String(luciPacket.payload)

        if (sceneObject == null){
            LibreLogger.d(TAG, "messageRecieved device " + nettyData.getRemotedeviceIp()+" sceneObject null")
            return
        }

        when(luciPacket.command){
            MIDCONST.MID_DEVICE_STATE_ACK -> {
                try {
                    LibreLogger.d(TAG, "A device became master found with the ip addrs = " + nettyData.getRemotedeviceIp())
                    val node = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(nettyData.getRemotedeviceIp())

                    val newSceneObject: SceneObject
                    if (node != null) {
                        newSceneObject = SceneObject(" ", node.friendlyname, 0f, nettyData.getRemotedeviceIp())
                        LibreLogger.d(TAG, "device became master its name  = " + node.friendlyname)
                        if (!mScanHandler.isIpAvailableInCentralSceneRepo(node.ip)) {
                            mScanHandler.putSceneObjectToCentralRepo(node.ip, newSceneObject)
                        }
                        deviceListAdapter.addDeviceToList(newSceneObject)

                        // enabling
                         (activity as CTDeviceDiscoveryActivity).requestLuciUpdates(nettyData.remotedeviceIp)
                        LibreLogger.d(TAG,"requestLuciUpdates THREE ")

                    }

                    val ipKeySet = mScanHandler.sceneObjectMapFromRepo.keys.toTypedArray()
                    if (ipKeySet.isEmpty()) {
                        LibreLogger.d(TAG, "Handle the case when there is no scene object remaining in the UI" + nettyData.getRemotedeviceIp())
                        (activity as CTHomeTabsActivity).refreshDevices()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            MIDCONST.MID_SCENE_NAME -> {
                /* if command Type 1 , then Scene Name information will be come in the same packet
                if command type 2 and command status is 1 , then data will be empty., at that time
                we should not update the value .*/
                if (luciPacket.commandStatus == 1 && luciPacket.commandType == 2) {
                    return
                }

                //int duration = Integer.parseInt(message);
                try {
                    sceneObject.sceneName = msg// = duration/60000.0f;
                    LibreLogger.d(TAG, "Scene Name " + sceneObject.sceneName)
                    mScanHandler.putSceneObjectToCentralRepo(nettyData.getRemotedeviceIp(), sceneObject)
                    deviceListAdapter.addDeviceToList(sceneObject)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            MIDCONST.MID_PLAYTIME.toInt() -> {
                if (msg.isEmpty()) return
                /* This message box indicates the current playing status of the scene, information like current seek position*/
                var duration = 0
                try {
                    duration = Integer.parseInt(msg)
                } catch (e: Exception) {
                    e.printStackTrace()
                    LibreLogger.d(TAG, "Handling the app crash for 49 message box value sent =$duration")
                }

                sceneObject.currentPlaybackSeekPosition = duration.toFloat()// = duration/60000.0f;
                LibreLogger.d(TAG, "Recieved the current Seek position to be " + sceneObject.currentPlaybackSeekPosition)
                mScanHandler.putSceneObjectToCentralRepo(nettyData.getRemotedeviceIp(), sceneObject)
                deviceListAdapter.addDeviceToList(sceneObject)
            }

            /**For RIVA speakers, during AUX we won't get volume in MB 64 for volume changes
             * done through speaker hardware buttons**/
            MIDCONST.VOLUME_CONTROL -> {
               // LibreLogger.d(TAG, "VOLUME_CONTROL volume msg = $msg")
                if (msg.isNotEmpty()) {
                    try {
                        val volume = Integer.parseInt(msg)
                        if (sceneObject.volumeValueInPercentage != volume) {
                            sceneObject.volumeValueInPercentage = volume
                            mScanHandler.putSceneObjectToCentralRepo(nettyData.getRemotedeviceIp(), sceneObject)
                            LibreApplication.INDIVIDUAL_VOLUME_MAP[sceneObject.ipAddress] = sceneObject.volumeValueInPercentage
                            deviceListAdapter.addDeviceToList(sceneObject)
                        } else {
                            LibreApplication.INDIVIDUAL_VOLUME_MAP[sceneObject.ipAddress] = volume
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            MIDCONST.MID_CURRENT_SOURCE.toInt() -> {
                /*this MB to get current sources*/
                try {
                    val mSource = Integer.parseInt(msg)
                    if (mSource == EXTERNAL_SOURCE)
                        return
                    LibreLogger.d(TAG, " ${sceneObject.sceneName} MID_CURRENT_SOURCE $mSource")
                    sceneObject.currentSource = mSource
                    mScanHandler.putSceneObjectToCentralRepo(nettyData.getRemotedeviceIp(), sceneObject)
                    deviceListAdapter.addDeviceToList(sceneObject)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            MIDCONST.SET_UI -> {
                try {

                    LibreLogger.d(TAG, "MB : 42, msg = $msg")
                    val root = JSONObject(msg)
                    val cmdId = root.getInt(LUCIMESSAGES.TAG_CMD_ID)
                    val window = root.getJSONObject(LUCIMESSAGES.ALEXA_KEY_WINDOW_CONTENT)
                    LibreLogger.d(TAG, "PLAY JSON is \n= $msg")


                    if (cmdId == 3) {

                        val handlerMessage = Message()
                        handlerMessage.what = PREPARATION_COMPLETED
                        val data = Bundle()
                        data.putString("ipAddress", nettyData.getRemotedeviceIp())
                        handlerMessage.data = data
                        handler.sendMessage(handlerMessage)
                        deviceListAdapter.notifyDataSetChanged()

                        sceneObject = AppUtils.updateSceneObjectWithPlayJsonWindow(window, sceneObject)
                        sceneObject.playUrl = window.getString("PlayUrl")
                            .lowercase(Locale.getDefault())
                        sceneObject.album_art = window.getString("CoverArtUrl")
                        sceneObject.artist_name = window.getString("Artist")
                        sceneObject.totalTimeOfTheTrack = window.getLong("TotalTime")
                        sceneObject.trackName = window.getString("TrackName")
                        sceneObject.album_name = window.getString("Album")

                        LibreApplication.currentAlbumnName = sceneObject.album_name
                        LibreApplication.currentArtistName = sceneObject.artist_name
                        LibreApplication.AlbumArtURL = sceneObject.album_art
                        LibreApplication.currentTrackName =  sceneObject.trackName
                        /* this is done to avoid  image refresh everytime the 42 message is recieved and the song playing back is the same */
                        LibreLogger.d(TAG, "Invalidating the scene name for  = " + sceneObject.ipAddress + "/" + "coverart.jpg")

                        LibreLogger.d(TAG, "Recieved the scene details as trackname = " + sceneObject.trackName + ": " + sceneObject.currentPlaybackSeekPosition)
                        LibreLogger.d(TAG, "Recieved the scene details as zoneVolume For checking  = " + sceneObject.getvolumeZoneInPercentage())

                        mScanHandler.putSceneObjectToCentralRepo(nettyData.getRemotedeviceIp(), sceneObject)
                        deviceListAdapter.addDeviceToList(sceneObject)
                    }


                } catch (e: Exception) {
                    e.printStackTrace()

                }

            }

            MIDCONST.MID_CURRENT_PLAY_STATE.toInt() -> {
                LibreLogger.d(TAG, "MB : 51, msg = $msg")
                if (msg.isNotEmpty()) {

                    val playstatus = Integer.parseInt(msg)
                    val mGcastCheckNode = mScanHandler.getLSSDPNodeFromCentralDB(nettyData.getRemotedeviceIp())

                    val handlerMsg = Message()

                    sceneObject.playstatus = playstatus
                    mScanHandler.putSceneObjectToCentralRepo(nettyData.getRemotedeviceIp(), sceneObject)
                    deviceListAdapter.addDeviceToList(sceneObject)

                    /*update stop all button*/
//                    if (activity is CTHomeTabsActivity) {
//                        (activity as CTHomeTabsActivity).toggleStopAllButtonVisibility()
//                    }

                    if (sceneObject.currentSource != BT_SOURCE
                            && mGcastCheckNode != null
                            && mGcastCheckNode.currentSource != MIDCONST.GCAST_SOURCE) {

                        if (sceneObject.playstatus == SceneObject.CURRENTLY_PLAYING) {
                            LibreLogger.d(TAG, "playing state is received")

                            handlerMsg.what = PREPARATION_COMPLETED
                            val data = Bundle()
                            data.putString("ipAddress", nettyData.getRemotedeviceIp())
                            handlerMsg.data = data
                            handler.sendMessage(handlerMsg)
                        } else if (sceneObject.playstatus == SceneObject.CURRENTLY_PAUSED) {
                            LibreLogger.d(TAG, "Pause state is received")
                        } else {
                            /*initiating progress loader*/
                            if (sceneObject.playstatus == SceneObject.CURRENTLY_STOPPED
                                    && sceneObject.currentSource != NO_SOURCE) {
                                handlerMsg.what = PREPARATION_INIT
                                val data = Bundle()
                                data.putString("ipAddress", nettyData.getRemotedeviceIp())
                                handlerMsg.data = data
                                handler.sendMessage(handlerMsg)
                                LibreLogger.d(TAG, "Stop state is received")
                            }

                        }
                        deviceListAdapter.notifyDataSetChanged()
                    }
                }
            }

            MIDCONST.MID_DEVICE_ALERT_STATUS.toInt() -> {
                LibreLogger.d(TAG, " message 54 recieved  $msg")
                try {
                    var error: LibreError? = null
                    when {
                        msg.contains(FAIL) -> error = LibreError(nettyData.getRemotedeviceIp(), FAIL_ALERT_TEXT)
                        msg.contains(SUCCESS) -> {

                            val handlerMsg = Message()
                            handlerMsg.what = PREPARATION_COMPLETED
                            val data = Bundle()
                            data.putString("ipAddress", nettyData.getRemotedeviceIp())
                            handlerMsg.data = data
                            handler.sendMessage(handlerMsg)


                        }
                        msg.contains(NO_URL) -> error = LibreError(nettyData.getRemotedeviceIp(), NO_URL_ALERT_TEXT)
                        msg.contains(NO_PREV_SONG) -> error = LibreError(nettyData.getRemotedeviceIp(), resources.getString(R.string.NO_PREV_SONG_ALERT_TEXT))
                        msg.contains(NO_NEXT_SONG) -> error = LibreError(nettyData.getRemotedeviceIp(), resources.getString(R.string.NO_NEXT_SONG_ALERT_TEXT))
                        msg.contains(DMR_SONG_UNSUPPORTED) -> error = LibreError(nettyData.getRemotedeviceIp(), resources.getString(R.string.SONG_NOT_SUPPORTED))
                    }
                    PicassoTrustCertificates.getInstance(activity).invalidate(sceneObject.album_art)

                    if (error != null)
                        (activity as CTDeviceDiscoveryActivity).showErrorMessage(error)

                } catch (e: Exception) {
                    LibreLogger.d(TAG,"exception MB 54 ${e.message}")
                    e.printStackTrace()
                }

            }

            MIDCONST.MID_MIC -> {
                if (msg.contains("BLOCKED",true)) {
                    deviceListAdapter.audioRecordUtil?.stopRecording()
                    (activity as CTDeviceDiscoveryActivity).showToast(getString(R.string.deviceMicOn))
                    sceneObject.isAlexaBtnLongPressed = false
                    /*updating boolean status to repo as well*/
                    ScanningHandler.getInstance().putSceneObjectToCentralRepo(sceneObject.ipAddress,sceneObject)
                    deviceListAdapter.addDeviceToList(sceneObject)
                }
            }

            MIDCONST.MID_ENV_READ -> {
                if (msg.contains("AlexaRefreshToken")) {
                    val token = msg.substring(msg.indexOf(":") + 1)
                    val mNode = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(nettyData.getRemotedeviceIp())
                    if (mNode != null) {
                        mNode.alexaRefreshToken = token
                    }
                    deviceListAdapter.addDeviceToList(sceneObject)
                }
            }
            MIDCONST.MUTE_UNMUTE_STATUS -> {
                LibreLogger.d(TAG, "Volume Controls MB : 63, msg = $msg")
                try {
                    if(msg.isNotEmpty()) {
                        sceneObject.mute_status = msg
                        mScanHandler.putSceneObjectToCentralRepo(nettyData.getRemotedeviceIp(), sceneObject)
                        deviceListAdapter.addDeviceToList(sceneObject)
                    }
                }catch (ex:Exception){
                    ex.printStackTrace()
                }
            }
        }
    }

    private fun clearScenePreparationFlags() {
        val mSceneList = mScanHandler.sceneObjectMapFromRepo
        for (masterIp in mSceneList.keys) {
            val sceneObject = mScanHandler.getSceneObjectFromCentralRepo(masterIp)
            if (sceneObject != null)
                sceneObject.preparingState = SceneObject.PREPARING_STATE.PREPARING_SUCCESS
        }
    }

    override fun onDestroyView() {
        clearScenePreparationFlags()
        (activity as CTDeviceDiscoveryActivity).libreApplication.unregisterMicException()
        handler.removeCallbacksAndMessages(null)
        (activity as CTDeviceDiscoveryActivity).unRegisterForDeviceEvents()
        (activity as CTHomeTabsActivity).removeTunnelFragmentListener()
        super.onDestroyView()
    }

    override fun onFragmentTunnelDataReceived(tunnelingData: TunnelingData?) {
        var tunnelDataChanged = false
        LibreLogger.d(TAG,"onFragmentTunnelDataReceived, ip = ${tunnelingData?.remoteClientIp}")
        if (tunnelingData?.remoteMessage?.size!! >= 24) {
            val tcpTunnelPacket = TCPTunnelPacket(tunnelingData.remoteMessage)
            val sceneObject = ScanningHandler.getInstance().getSceneObjectFromCentralRepo(tunnelingData.remoteClientIp)

            if (sceneObject == null){
                LibreLogger.d(TAG,"onFragmentTunnelDataReceived sceneObject null")
                return
            }

            if (tcpTunnelPacket.isAuxPluggedIn!= sceneObject.isAuxPluggedIn){
                sceneObject.isAuxPluggedIn = tcpTunnelPacket.isAuxPluggedIn
                tunnelDataChanged = true
            }

            if (tcpTunnelPacket.isBatteryPluggedIn && !sceneObject.isBatteryPluggedIn){
                sceneObject.isBatteryPluggedIn = tcpTunnelPacket.isBatteryPluggedIn
                tunnelDataChanged = true
            }

            if (tcpTunnelPacket.isBatteryCharging!= sceneObject.isBatteryCharging){
                sceneObject.isBatteryCharging = tcpTunnelPacket.isBatteryCharging
                tunnelDataChanged = true
            }

            if (tcpTunnelPacket.batteryLevel!=null
                    && tcpTunnelPacket.batteryLevel!=sceneObject.batteryType){
                sceneObject.batteryType = tcpTunnelPacket.batteryLevel
                tunnelDataChanged = true
            }

            LibreLogger.d(TAG,"tunnelDataReceived, current source = ${tcpTunnelPacket.currentSource}")
            if (tcpTunnelPacket.currentSource!= sceneObject.tunnelingCurrentSource){
                sceneObject.tunnelingCurrentSource = tcpTunnelPacket.currentSource
                tunnelDataChanged = true
            }

            LibreLogger.d(TAG,"onFragmentTunnelDataReceived, tunnelDataChanged $tunnelDataChanged")
            if (tunnelDataChanged) {
                deviceListAdapter.addDeviceToList(sceneObject)
            }
        }
    }
}