package com.cumulations.libreV2.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.cumulations.libreV2.adapter.CTDIDLObjectListAdapter
import com.cumulations.libreV2.closeKeyboard
import com.libreAlexa.LErrorHandeling.LibreError
import com.libreAlexa.LibreApplication
import com.libreAlexa.R
import com.libreAlexa.Scanning.ScanningHandler
import com.libreAlexa.app.dlna.dmc.processor.impl.DMSProcessorImpl
import com.libreAlexa.app.dlna.dmc.processor.interfaces.DMSProcessor
import com.libreAlexa.app.dlna.dmc.server.MusicServer
import com.libreAlexa.app.dlna.dmc.utility.DMRControlHelper
import com.libreAlexa.app.dlna.dmc.utility.DMSBrowseHelper
import com.libreAlexa.app.dlna.dmc.utility.PlaybackHelper
import com.libreAlexa.app.dlna.dmc.utility.UpnpDeviceManager
import com.libreAlexa.constants.Constants.*
import com.libreAlexa.databinding.CtActivityFileBrowserBinding
import com.libreAlexa.util.LibreLogger
import org.fourthline.cling.model.meta.RemoteDevice
import org.fourthline.cling.model.types.ServiceType
import org.fourthline.cling.support.model.DIDLObject
import org.fourthline.cling.support.model.container.Container
import org.fourthline.cling.support.model.item.Item
import java.util.*
import kotlin.collections.set

class CTUpnpFileBrowserActivity : CTDeviceDiscoveryActivity(), DMSProcessor.DMSProcessorListener {
    companion object {
        private val TAG = CTUpnpFileBrowserActivity::class.java.name
    }

    private val currentIpAddress: String? by lazy {
        intent?.getStringExtra(CURRENT_DEVICE_IP)
    }
    private val clickedDIDLId: String? by lazy {
        intent?.getStringExtra(CLICKED_DIDL_ID)
    }
    private var dmsProcessor: DMSProcessor? = null
    private var didlObjectStack: Stack<DIDLObject>? = Stack()
    private var dmsBrowseHelper: DMSBrowseHelper? = null
    private var position = 0
    private var browsingCancelled = false
    private var needSetListViewScroll = false
    private var selectedDIDLObject: DIDLObject? = null

    private var didlObjectList:ArrayList<DIDLObject>? = ArrayList()
    private var didlObjectArrayAdapter: CTDIDLObjectListAdapter? = null

    private var remoteDeviceUDN: String? = null
    private var remoteDevice: RemoteDevice? = null

    private lateinit var binding: CtActivityFileBrowserBinding

    internal var handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                SERVICE_NOT_FOUND -> {
                    /*Error case when we not able to find AVTransport Service*/
                    val error = LibreError(currentIpAddress, resources.getString(R.string.AVTRANSPORT_NOT_FOUND))
                    showErrorMessage(error)
                    openNowPlaying(true)
                }

                DO_BACKGROUND_DMR -> {
                    LibreLogger.d(TAG, "DMR search DO_BACKGROUND_DMR")
                    upnpProcessor!!.searchDMR()

                    var renderingDevice: RemoteDevice? = UpnpDeviceManager.getInstance().getRemoteDMRDeviceByIp(currentIpAddress)
                    if (renderingDevice == null && upnpProcessor != null) {

                        LibreLogger.d(TAG, "we dont have the renderer in the Device Manager")
                        LibreLogger.d(TAG, "Checking the renderer in the registry")
                        /* Special check to make sure we dont have the device in the registry the */
                        renderingDevice = upnpProcessor!!.getTheRendererFromRegistryIp(currentIpAddress)
                        if (renderingDevice != null) {
                            LibreLogger.d(TAG, "WOW! We found the device in registry and hence we" + " " + "will start the" +
                            "playback with the new helper")
                            dismissDialog()
                            play(selectedDIDLObject!!)
                            this.removeMessages(DO_BACKGROUND_DMR)
                            return
                        }
                    }

                    if (renderingDevice != null) {
                        dismissDialog()
                        play(selectedDIDLObject!!)
                        this.removeMessages(DO_BACKGROUND_DMR)
                    } else {
                        val DO_BACKGROUND_DMR_TIMEOUT = 6000
                        this.sendEmptyMessageDelayed(DO_BACKGROUND_DMR, DO_BACKGROUND_DMR_TIMEOUT.toLong())
                        LibreLogger.d(TAG, "DMR search request issued")
                    }
                }
            }
        }
    }

    private var mTaskHandler: Handler? = null
    private var mMyTaskRunnable: Runnable = object : Runnable {

        override fun run() {

            if (MusicServer.getMusicServer().isMediaServerReady) {
                handleDIDLObjectClick(adapterClickedPosition)
                adapterClickedPosition = -1
                showToast("Local content loading Done!")
                return
            }

            LibreLogger.d(TAG, "Loading the Songs..")
            /* and here comes the "trick" */
            mTaskHandler!!.postDelayed(this, 100)

        }

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CtActivityFileBrowserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.ct_activity_file_browser)
        setViews()
    }

    override fun onStartComplete() {

        remoteDeviceUDN = intent?.getStringExtra(DEVICE_UDN)
        if (remoteDeviceUDN == null){
            LibreLogger.d(TAG,"onStartComplete deviceUDN null")
            finish()
            return
        }

        val playbackHelper = LibreApplication.PLAYBACK_HELPER_MAP[remoteDeviceUDN]
        if (playbackHelper == null){
            LibreLogger.d(TAG,"onStartComplete playbackHelper null")
            finish()
            return
        }

        if (dmsBrowseHelper == null) {
            LibreLogger.d(TAG,"dmsBrowseHelper null")
            dmsBrowseHelper = playbackHelper.dmsBrowseHelper?.clone()
        }

        val dmsDevice = dmsBrowseHelper?.getDevice(UpnpDeviceManager.getInstance())
        LibreLogger.d("onStartComplete", "didlObjectStack = $didlObjectStack")
        if (dmsDevice == null) {
            LibreLogger.d(TAG,"dmsDevice null")
            upnpProcessor?.searchDMR()
            AlertDialog.Builder(this).apply {
                setMessage(getString(R.string.deviceMissing))
                setCancelable(false)
                setPositiveButton(getString(R.string.retry)) { dialog, id -> onStartComplete() }
                setNegativeButton(getString(R.string.cancel)) { dialog, id -> dialog.dismiss() }
                show()
            }
        } else {
            didlObjectStack = dmsBrowseHelper?.browseObjectStack?.clone() as Stack<DIDLObject>
            dmsProcessor = DMSProcessorImpl(dmsDevice, upnpProcessor?.controlPoint)
            if (dmsProcessor == null) {
                showToast(R.string.cannotCreateDMS)
                finish()
            } else if (intent.hasExtra(FROM_ACTIVITY)) {
                val remoteDmrDevice = UpnpDeviceManager.getInstance().getRemoteDMRDeviceByIp(currentIpAddress)
                if (remoteDmrDevice != null) {
                    val renderingUDN = remoteDmrDevice.identity.udn.toString()
                    val remotePlaybackHelper = LibreApplication.PLAYBACK_HELPER_MAP[renderingUDN]
                    if (remotePlaybackHelper != null && remotePlaybackHelper.dmsBrowseHelper != null) {
                        didlObjectStack = remotePlaybackHelper.dmsBrowseHelper.browseObjectStack
                        LibreLogger.d("onStartComplete", "FROM_ACTIVITY, didlObjectStack = $didlObjectStack")
                        if (didlObjectStack != null) {
                            dmsProcessor?.addListener(this)
                            needSetListViewScroll = true
                            /*Peek : Get the item at the top of the stack without removing it*/
                            browse(didlObjectStack?.peek())
                        }
                    }
                }
            } else {
                dmsProcessor?.addListener(this)
                needSetListViewScroll = true
                /*Peek : Get the item at the top of the stack without removing it*/
//                browse(didlObjectStack?.peek())
                /*When we don't have clickedDIDLId ie when opening DMS devices use browseObjectStack.peek*/
                if (clickedDIDLId.isNullOrEmpty()) {
                    browse(didlObjectStack?.peek())
                } else browseByDIDLId(clickedDIDLId)
            }
        }
    }
    
    private fun setViews(){
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        if (intent?.getStringExtra(DIDL_TITLE)!=null)
            updateTitle(intent?.getStringExtra(DIDL_TITLE)!!)

        binding.tvFolderName.isSelected = true

        didlObjectArrayAdapter = CTDIDLObjectListAdapter(this, ArrayList())
        binding.rvBrowserList.layoutManager = LinearLayoutManager(this)
        binding.rvBrowserList.adapter = didlObjectArrayAdapter
        
        setListeners()
    }

    private fun setListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun updateTitle(title: String) {
        binding.tvFolderName.text = title
    }

    override fun onResume() {
        super.onResume()
        setMusicPlayerWidget(binding.ilMusicPlayingWidget.flMusicPlayWidget,currentIpAddress!!)
        upnpProcessor?.addListener(this)
    }

    private fun browseByDIDLId(didlObjectId:String?){
        LibreLogger.d(TAG,"browse Browse id:$didlObjectId")
        browsingCancelled = false
        runOnUiThread {
            showProgressDialog(R.string.pleaseWait)
        }

        try {
            dmsProcessor?.browse(didlObjectId)
        } catch (t: Throwable) {
            dismissDialog()
            showToast(R.string.browseFailed)
        }
    }

    private fun browse(didlObject: DIDLObject?) {
        val id = didlObject!!.id
        LibreLogger.d(TAG,"browse Browse id:$id")
        browsingCancelled = false
        updateTitle(didlObject.title)
        runOnUiThread {
            showProgressDialog(R.string.pleaseWait)
        }

        try {
            dmsProcessor?.browse(id)
        } catch (t: Throwable) {
            dismissDialog()
            showToast(R.string.browseFailed)
        }
    }

    fun play(didlObject: DIDLObject) {
        LibreLogger.d(TAG, "play item title:" + didlObject.title)
        closeKeyboard(this, currentFocus)

        LibreLogger.d(TAG, "play, position$position")
        createOrUpdatePlaybackHelperAndBrowser()

        try {
            val playbackHelper = LibreApplication.PLAYBACK_HELPER_MAP[remoteDeviceUDN]
            playbackHelper?.playSong()
            openNowPlaying(true)
        } catch (e: Exception) {
            //handling the excetion when the device is rebooted
            LibreLogger.d(TAG, "EXCEPTION while setting the song!!!")
        }

    }

    private fun createOrUpdatePlaybackHelperAndBrowser() {

        dmsBrowseHelper!!.saveDidlListAndPosition(didlObjectList, position)
        dmsBrowseHelper!!.browseObjectStack = didlObjectStack
        dmsBrowseHelper!!.scrollPosition = (binding.rvBrowserList.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

        LibreLogger.d("play", "didlObjectStack = $didlObjectStack")
        LibreLogger.d("play", "scrollPosition = ${dmsBrowseHelper?.scrollPosition}")

        /* here first find the UDN of the current selected device */
        remoteDevice = UpnpDeviceManager.getInstance().getRemoteDMRDeviceByIp(currentIpAddress)
        remoteDevice = upnpProcessor?.getTheRendererFromRegistryIp(currentIpAddress)

        if (remoteDevice == null && upnpProcessor != null) {
            if (remoteDevice == null) {
//                runOnUiThread { showToast(R.string.deviceNotFound) }
//                return
            }
        }

        remoteDeviceUDN = remoteDevice?.identity?.udn.toString()
        var playbackHelper:PlaybackHelper? = LibreApplication.PLAYBACK_HELPER_MAP[remoteDeviceUDN]
        val service = remoteDevice?.findService(
                ServiceType(DMRControlHelper.SERVICE_NAMESPACE, DMRControlHelper.SERVICE_AVTRANSPORT_TYPE)
        )

        if (playbackHelper == null) {
            /* If it is local device then assign local audio manager */
            if (LibreApplication.LOCAL_UDN.equals(remoteDeviceUDN, ignoreCase = true)) {
                val audioManager = applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                val dmrControlHelper = DMRControlHelper(audioManager)
                playbackHelper = PlaybackHelper(dmrControlHelper)
            } else {
                if (service == null) {
                    /*AVTransport not found so showing error to user*/
                    handler.sendEmptyMessage(SERVICE_NOT_FOUND)
                    return
                }
                /*crash Fix when Service is Null , stil lwe trying to get Actions.*/
                val dmrControlHelper = DMRControlHelper(remoteDeviceUDN, upnpProcessor?.controlPoint, remoteDevice, service)
                playbackHelper = PlaybackHelper(dmrControlHelper)
            }
        } else {
            if (playbackHelper.playbackStopped) {
                if (service == null) {
                    /*AVTransport not found so showing error to user*/
                    handler.sendEmptyMessage(SERVICE_NOT_FOUND)
                    return
                }
                val dmrControlHelper = DMRControlHelper(remoteDeviceUDN, upnpProcessor?.controlPoint, remoteDevice, service)
                playbackHelper = PlaybackHelper(dmrControlHelper)
            }
        }

        playbackHelper.dmsBrowseHelper = dmsBrowseHelper?.clone()
        LibreApplication.PLAYBACK_HELPER_MAP[remoteDeviceUDN] = playbackHelper
    }

    override fun onBrowseComplete(parentObjectId: String?, result: Map<String, List<DIDLObject>>) {
        LibreLogger.d(TAG, "Browse Completed , parentObjectId = $parentObjectId")
        if (browsingCancelled) {
            browsingCancelled = false
            return
        }

        runOnUiThread {
            if (parentObjectId.equals("0")){
                if (intent?.getStringExtra(DIDL_TITLE)!=null)
                    updateTitle(intent?.getStringExtra(DIDL_TITLE)!!)
            }

            didlObjectList?.clear()
            val containersList = result["Containers"]
            if (containersList?.isNotEmpty()!!) {
                for (container in containersList){
                    didlObjectList?.add(container)
                }
            }

            val itemsList = result["Items"] as List<Item>
            if (itemsList.isNotEmpty()) {
                for (item in itemsList){
                    didlObjectList?.add(item)
                }
            }

            dismissDialog()
            didlObjectArrayAdapter?.updateList(didlObjectList as MutableList<DIDLObject>?)

            if (didlObjectArrayAdapter?.didlObjectList?.isEmpty()!!){
                showToast(R.string.noContent)
                return@runOnUiThread
            }

            if (needSetListViewScroll) {
                needSetListViewScroll = false
                binding.rvBrowserList.scrollToPosition(dmsBrowseHelper?.scrollPosition!!)
            }
        }
    }

    override fun onBrowseFail(message: String) {
        LibreLogger.d(TAG, "Browse Failed = $message")
        if (browsingCancelled) {
            browsingCancelled = false
            return
        }
        runOnUiThread {
            dismissDialog()
            showToast(/*getString(R.string.loadingMusic) + */message)
//            onBackPressed()
        }
    }

    override fun onBackPressed() {
        closeKeyboard(this, currentFocus)
        if (intent?.getStringExtra(DIDL_TITLE)!=null && intent?.getStringExtra(DIDL_TITLE)!! == binding.tvFolderName.text?.toString()!!) {
            super.onBackPressed()
        } else {
            if (!didlObjectStack?.isEmpty()!!) {
                didlObjectStack?.pop()
                browse(didlObjectStack!!.peek())
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onRemoteDeviceAdded(remoteDevice: RemoteDevice?) {
        super.onRemoteDeviceAdded(remoteDevice)
        val ip = remoteDevice?.identity?.descriptorURL?.host
        LibreLogger.d(TAG, "Remote device with added with ip $ip")
        if (ip.equals(currentIpAddress!!, ignoreCase = true)) {
            if (selectedDIDLObject != null) {
                /* This is a case where user has selected a DMS source but that time  rendering device was null and hence we play with the storedPlayer object*/
                runOnUiThread {
                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        dismissDialog()
                        play(selectedDIDLObject!!)
                    }, 2000)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        unRegisterForDeviceEvents()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onRemoteDeviceRemoved(device: RemoteDevice) {
        super.onRemoteDeviceRemoved(device)
        val ip = device.identity.descriptorURL.host
        LibreLogger.d(TAG, "Remote device with removed with ip $ip")
        try {
            /* KK , Change for Continuing the play after Device Got connected */
            if (ip.equals(currentIpAddress!!, ignoreCase = true)) {
                selectedDIDLObject = null
            }
            LibreLogger.d(TAG, "Remote device removed $ip")
            //mDeviceGotRemoved(ip);
        } catch (e: Exception) {
            LibreLogger.d(TAG, "Remote device removed " + ip + "And Exception Happend")
        }

    }

    private var adapterClickedPosition = -1

    fun handleDIDLObjectClick(clickedDIDLPosition: Int) {
        if (!MusicServer.getMusicServer().isMediaServerReady) {
            showToast("Loading all contents,Please Wait")
            adapterClickedPosition = clickedDIDLPosition
            showProgressDialog(R.string.pleaseWait)
            mTaskHandler = Handler(Looper.getMainLooper())
            mTaskHandler!!.postDelayed(mMyTaskRunnable, 0)
            return
        }

        val clickedDIDLObject = didlObjectList?.get(clickedDIDLPosition)
        this@CTUpnpFileBrowserActivity.position = clickedDIDLPosition

        LibreLogger.d(TAG, "handleDIDLObjectClick position:" + this@CTUpnpFileBrowserActivity.position)

        closeKeyboard(this@CTUpnpFileBrowserActivity, currentFocus)

        if (clickedDIDLObject is Container) {
            didlObjectStack!!.push(clickedDIDLObject)
            LibreLogger.d("handleClick", "didlObjectStack = $didlObjectStack")
            browse(clickedDIDLObject)
        } else if (clickedDIDLObject is Item) {
            val renderingDevice = UpnpDeviceManager.getInstance().getRemoteDMRDeviceByIp(currentIpAddress)
            if (renderingDevice != null && !UpnpDeviceManager.getInstance().getRemoteRemoved(currentIpAddress)) {
                play(clickedDIDLObject)
            } else {
                selectedDIDLObject = clickedDIDLObject
                handler.sendEmptyMessage(DO_BACKGROUND_DMR)
            }
        }
    }

    private fun openNowPlaying(setDMR: Boolean) {
        unRegisterForDeviceEvents()

        val intent = Intent(this@CTUpnpFileBrowserActivity, CTNowPlayingActivity::class.java)
        /* This change is done to make sure that album art is reflectd after source swithing from Aux to other-START*/
        if (setDMR) {
            val sceneObjectFromCentralRepo = ScanningHandler.getInstance().getSceneObjectFromCentralRepo(currentIpAddress)
            if (sceneObjectFromCentralRepo != null) {
                /* Setting the DMR source */
                sceneObjectFromCentralRepo.currentSource = DMR_SOURCE
                sceneObjectFromCentralRepo.currentPlaybackSeekPosition = 0f
                sceneObjectFromCentralRepo.totalTimeOfTheTrack = 0
            }
        }
        /* This change is done to make sure that album art is reflectd after source swithing from Aux to other*- END*/
        intent.putExtra(CURRENT_DEVICE_IP, currentIpAddress)
        startActivity(intent)
//        finish()
    }
}
