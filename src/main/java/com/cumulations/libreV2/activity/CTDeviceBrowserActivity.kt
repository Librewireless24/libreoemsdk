package com.cumulations.libreV2.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.cumulations.libreV2.AppUtils
import com.cumulations.libreV2.adapter.CTDeviceBrowserListAdapter
import com.cumulations.libreV2.closeKeyboard
import com.cumulations.libreV2.model.DataItem
import com.cumulations.libreV2.model.SceneObject
import com.libreAlexa.LErrorHandeling.LibreError
import com.libreAlexa.LibreApplication
import com.libreAlexa.LibreApplication.isUSBBackPressed
import com.libreAlexa.R
import com.libreAlexa.Scanning.ScanningHandler
import com.libreAlexa.constants.Constants
import com.libreAlexa.constants.Constants.NETWORK_TIMEOUT
import com.libreAlexa.constants.LSSDPCONST
import com.libreAlexa.constants.LUCIMESSAGES
import com.libreAlexa.constants.LUCIMESSAGES.BACK
import com.libreAlexa.constants.MIDCONST
import com.libreAlexa.databinding.CtActivityDeviceBrowserBinding
import com.libreAlexa.luci.LSSDPNodeDB
import com.libreAlexa.luci.LSSDPNodes
import com.libreAlexa.luci.LUCIControl
import com.libreAlexa.luci.LUCIPacket
import com.libreAlexa.netty.BusProvider
import com.libreAlexa.netty.LibreDeviceInteractionListner
import com.libreAlexa.netty.NettyData
import com.libreAlexa.util.LibreLogger
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class CTDeviceBrowserActivity : CTDeviceDiscoveryActivity(), LibreDeviceInteractionListner {

    companion object {
        const val TAG_CMD_ID = "CMD ID"
        const val TAG_WINDOW_CONTENT = "Window CONTENTS"
        const val TAG_BROWSER = "Browser"
        const val TAG_CUR_INDEX = "Index"
        const val TAG_ITEM_COUNT = "Item Count"
        const val TAG_ITEM_LIST = "ItemList"
        const val TAG_ITEM_ID = "Item ID"
        const val TAG_ITEM_TYPE = "ItemType"
        const val TAG_ITEM_NAME = "Name"
        const val TAG_ITEM_FAVORITE = "Favorite"
        const val TAG_ITEM_ALBUMURL = "StationImage"
        //PageNo":1,"TotalItems
        const val TAG_TOTALITEM = "TotalItems"
        const val TAG_PAGE_NO = "PageNo"
        const val GET_PLAY = "GETUI:PLAY"
        const val SELECTITEM = "SELECTITEM:"

    }

    private var currentIpaddress: String? = null
    private var luciControl: LUCIControl? = null
    private var dataItems: ArrayList<DataItem>? = ArrayList()
    private var deviceBrowserListAdapter: CTDeviceBrowserListAdapter? = null
    private var mLayoutManager: LinearLayoutManager? = null
    private var gotolastpostion: Boolean = false
    private var browser = ""
    private var current_source_index_selected = -1
    internal var alert: AlertDialog? = null
    // private boolean isSearchEnabled;
    private var isSongSelected = false
    private var searchJsonHashCode: Int = 0
    private var presentJsonHashCode: Int = 0
    //searchOptionClicked is true when user clicks on search option. And then we calculate hash code for search JSON result
    private var searchOptionClicked = false
    private val mScanHandler = ScanningHandler.getInstance()
    private var TAG = "==CTDeviceBrowserActivity=="
    @SuppressLint("HandlerLeak")
    internal var handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == NETWORK_TIMEOUT) {
                LibreLogger.d(TAG, "handler message recieved")

                closeLoader()
                /*showing error*/
                val error = LibreError(currentIpaddress, getString(R.string.requestTimeout))
                showErrorMessage(error)

                binding.ibHome.performClick()
            }
        }
    }
    private lateinit var binding: CtActivityDeviceBrowserBinding
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CtActivityDeviceBrowserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentIpaddress = intent.getStringExtra(Constants.CURRENT_DEVICE_IP)
        current_source_index_selected = intent.getIntExtra(Constants.CURRENT_SOURCE_INDEX_SELECTED, -1)
        setTitleForTheBrowser(current_source_index_selected)

        binding.layData.visibility=View.INVISIBLE
        binding.layShimmer.startShimmer()

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

        if (current_source_index_selected < 0) {
            showToast(R.string.sourceIndexWrong)
            val intent = Intent(this@CTDeviceBrowserActivity, CTMediaSourcesActivity::class.java)
            intent.putExtra(Constants.CURRENT_DEVICE_IP, currentIpaddress)
            val sceneObject = ScanningHandler.getInstance().getSceneObjectFromCentralRepo(currentIpaddress)
            intent.putExtra(Constants.CURRENT_SOURCE, sceneObject!!.currentSource)
            startActivity(intent)
            finish()
        }

        LibreLogger.d(TAG," Registered for the device " + currentIpaddress!!)

        luciControl = LUCIControl(currentIpaddress)
        luciControl!!.SendCommand(MIDCONST.MID_REMOTE_UI.toInt(), LUCIMESSAGES.SELECT_ITEM + ":" + current_source_index_selected, LSSDPCONST.LUCI_SET)
        luciControl!!.SendCommand(MIDCONST.MID_CURRENT_SOURCE.toInt(), null, LSSDPCONST.LUCI_GET)
        luciControl!!.SendCommand(MIDCONST.MID_CURRENT_PLAY_STATE.toInt(), null, LSSDPCONST.LUCI_GET)
        showLoader(R.string.loading_next_items)
        LibreLogger.d(TAG,"ItemCLick showloader9")

        //////////// timeout for dialog - showLoader() ///////////////////
        if (current_source_index_selected == 0) {
            /*increasing timeout for media servers only*/
            handler.sendEmptyMessageDelayed(NETWORK_TIMEOUT, 60000)
        } else {
            handler.sendEmptyMessageDelayed(NETWORK_TIMEOUT, 15000)
        }

        mLayoutManager = LinearLayoutManager(this)
        deviceBrowserListAdapter = CTDeviceBrowserListAdapter(this, dataItems)
        binding.rvDeviceBrowser.layoutManager = mLayoutManager
        binding.rvDeviceBrowser.adapter = deviceBrowserListAdapter
        /**
         * Commented, discuss with Suma
         */
        /*binding.rvDeviceBrowser.setOnTouchListener(object : View.OnTouchListener {
            var beginY = 0f
            var endY = 0f

            override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                //ACTION_DOWN when the user first touches the screen. We are taking the beginY co ordinate here
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    LibreLogger.d(TAG,"action down, touched y co ordinate is " + motionEvent.y)
                    beginY = motionEvent.y
                }
                //ACTION_UP, when the user finally releases the touch. We are taking the endY co ordinate here
                if (motionEvent.action == MotionEvent.ACTION_UP) {
                    LibreLogger.d(TAG,"action up, touch lifted y co ordinate is " + motionEvent.y)
                    endY = motionEvent.y
                    if (beginY > endY) {
                        LibreLogger.d(TAG,"touched from bottom -> top, send scroll down")
                        sendScrollDown()
                    } else if (beginY < endY) {
                        LibreLogger.d(TAG,"touched from top -> bottom, send scroll up")
                        sendScrollUp()
                    }
                    //return false - means other touch events like onclick on the view item will now work.
                    return false
                }

                return false
            }
        })*/


        ///////////////////////////////////////////////////////////////// limit end //////////////////////////////

        binding.ibBack.setOnClickListener {
           exitOnBackPressed()
        }

        binding.ibHome.setOnClickListener {
            LibreLogger.d(TAG, "user pressed home button ")
            unRegisterForDeviceEvents()
            //   luciControl.SendCommand(MIDCONST.MID_REMOTE_UI, GET_HOME, LSSDPCONST.LUCI_SET);

            val intent = Intent(this@CTDeviceBrowserActivity, CTMediaSourcesActivity::class.java)
            intent.putExtra(Constants.CURRENT_DEVICE_IP, currentIpaddress)
            val currentSceneObject = ScanningHandler.getInstance().getSceneObjectFromCentralRepo(currentIpaddress)
            intent.putExtra(Constants.CURRENT_SOURCE, "" + currentSceneObject?.currentSource)
            startActivity(intent)
            finish()
        }
        val musicSceneObject = ScanningHandler.getInstance().getSceneObjectFromCentralRepo(currentIpaddress)
        val mNodeWeGotForControl = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(currentIpaddress)
        binding.idMusicWidget.ivPlayPause.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                if (musicSceneObject.currentSource == Constants.AUX_SOURCE
                    /*|| musicSceneObject?.currentSource == EXTERNAL_SOURCE*/
                    || musicSceneObject.currentSource == Constants.VTUNER_SOURCE
                    || musicSceneObject.currentSource == Constants.TUNEIN_SOURCE
                    /*|| musicSceneObject.currentSource == BT_SOURCE*/
                    && ((mNodeWeGotForControl?.getgCastVerision() == null
                            && (mNodeWeGotForControl?.bT_CONTROLLER == SceneObject.CURRENTLY_NOTPLAYING
                            || mNodeWeGotForControl?.bT_CONTROLLER == SceneObject.CURRENTLY_PLAYING))
                            || (mNodeWeGotForControl.getgCastVerision() != null
                            && mNodeWeGotForControl.bT_CONTROLLER < SceneObject.CURRENTLY_PAUSED))) {
                    val error = LibreError("", resources.getString(R.string.PLAY_PAUSE_NOT_ALLOWED), 1)
                    BusProvider.getInstance().post(error)
                    return

                }
                if (musicSceneObject.currentSource == Constants.NO_SOURCE || (musicSceneObject.currentSource == Constants.DMR_SOURCE && (musicSceneObject.playstatus == SceneObject.CURRENTLY_STOPPED || musicSceneObject.playstatus == SceneObject.CURRENTLY_NOTPLAYING))) {
                    LibreLogger.d(TAG, "currently not playing, so take user to sources option activity")
                    gotoSourcesOption(musicSceneObject.ipAddress, musicSceneObject.currentSource)
                    return
                }

                if (musicSceneObject.playstatus == SceneObject.CURRENTLY_PLAYING) {
                    LUCIControl.SendCommandWithIp(MIDCONST.MID_PLAYCONTROL.toInt(), LUCIMESSAGES.PAUSE, LSSDPCONST.LUCI_SET, musicSceneObject.ipAddress)
                } else {
                    if (musicSceneObject.currentSource == Constants.BT_SOURCE) {
                        LUCIControl.SendCommandWithIp(MIDCONST.MID_PLAYCONTROL.toInt(), LUCIMESSAGES.PLAY, LSSDPCONST.LUCI_SET, musicSceneObject.ipAddress)
                    } else {
                        LUCIControl.SendCommandWithIp(MIDCONST.MID_PLAYCONTROL.toInt(), LUCIMESSAGES.RESUME, LSSDPCONST.LUCI_SET, musicSceneObject.ipAddress)
                    }
                }
            }
        })

        binding.idMusicWidget.flMusicPlayWidget.setOnClickListener {
            if (binding.idMusicWidget.tvTrackName.text?.toString()?.contains(getString(R.string.app_name))!! || binding.idMusicWidget.tvTrackName.text?.toString()?.contains(getString(R.string.login_to_enable_cmds))!!) {
                return@setOnClickListener
            }

            if (!musicSceneObject.trackName.isNullOrEmpty() || !musicSceneObject.album_art.isNullOrEmpty() || musicSceneObject.playstatus == SceneObject.CURRENTLY_PLAYING ) {
                    isUSBBackPressed=true
                    startActivity(Intent(this, CTNowPlayingActivity::class.java).apply {
                    putExtra(Constants.CURRENT_DEVICE_IP, currentIpaddress)
                })
            }
        }
    }

    private fun gotoSourcesOption(ipaddress: String, currentSource: Int) {
        val mActiveScenesList = Intent(this, CTMediaSourcesActivity::class.java)
        mActiveScenesList.putExtra(Constants.CURRENT_DEVICE_IP, ipaddress)
        mActiveScenesList.putExtra(Constants.CURRENT_SOURCE, "" + currentSource)
        val error = LibreError("", resources.getString(R.string.no_active_playlist), 1)
        BusProvider.getInstance().post(error)
        startActivity(mActiveScenesList)
    }
    override fun onStart() {
        super.onStart()
        if (isSongSelected){
            deviceBrowserListAdapter?.dataItemList?.clear()
            deviceBrowserListAdapter?.notifyDataSetChanged()
            isSongSelected = false
           // onBackPressed()
            exitOnBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        /*Registering to receive messages*/
        registerForDeviceEvents(this)

       /* val musicPlayerView = findViewById<LinearLayout>(R.id.fl_music_play_widget)
        setMusicPlayerWidget(musicPlayerView, currentIpaddress!!)*/
    }

    private fun sendScrollUp() {
        if (mLayoutManager!!.findFirstVisibleItemPosition() == 0) {

            luciControl?.SendCommand(MIDCONST.MID_REMOTE.toInt(), LUCIMESSAGES.SCROLLUP, LSSDPCONST.LUCI_SET)
            gotolastpostion = true
            //  mLayoutManager.scrollToPosition(49);
            showProgressDialog(R.string.loading_next_items)
            LibreLogger.d(TAG,"ItemCLick showloader5")

            //////////// timeout for dialog - showLoader() ///////////////////
            if (current_source_index_selected == 0) {
                /*increasing timeout for media servers only*/
                handler.sendEmptyMessageDelayed(NETWORK_TIMEOUT, 60000)
            } else {
                handler.sendEmptyMessageDelayed(NETWORK_TIMEOUT, 15000)
            }

            LibreLogger.d(TAG,"recycling " + "SCROLL UP" + "and find first visible item " +
                    "position=" +
                    mLayoutManager!!.findFirstVisibleItemPosition() + "find last visible item=" +
                    mLayoutManager!!.findLastVisibleItemPosition() + "find last completely visible item=" +
                    mLayoutManager!!.findLastCompletelyVisibleItemPosition())
        }
    }

    private fun sendScrollDown() {
        if (mLayoutManager!!.findLastVisibleItemPosition() == 49) {
            LibreLogger.d(TAG,"recycling " + "last visible Item Position" + mLayoutManager!!
            .findLastVisibleItemPosition())
            luciControl?.SendCommand(MIDCONST.MID_REMOTE.toInt(), LUCIMESSAGES.SCROLLDOWN, LSSDPCONST.LUCI_SET)
            gotolastpostion = false
            LibreLogger.d(TAG,"recycling " + "SCROLL DOWN")
            // mLayoutManager.scrollToPosition(0);
            showProgressDialog(R.string.loading_prev_items)
            //////////// timeout for dialog - showLoader() ///////////////////
            if (current_source_index_selected == 0) {
                /*increasing timeout for media servers only*/
                handler.sendEmptyMessageDelayed(NETWORK_TIMEOUT, 60000)
            } else {
                handler.sendEmptyMessageDelayed(NETWORK_TIMEOUT, 15000)
            }
            LibreLogger.d(TAG,"recycling " + "Last song")
            showToast(R.string.lastSong)
        }
    }

    /*Searching dialog */
    private fun searchingDialog(position: Int) {
        // custom dialog
        val dialog = Dialog(this@CTDeviceBrowserActivity)
        dialog.setContentView(R.layout.deezer_auth_dialog)
        dialog.setTitle(getString(R.string.searchTextPlaceHolder))

        // set the custom dialog components - text, image and button
        val searchString = dialog.findViewById<View>(R.id.user_name) as EditText
        val userPassword = dialog.findViewById<View>(R.id.user_password) as EditText

        searchString.hint = getString(R.string.enterText)
        userPassword.visibility = View.GONE

        val submitButton = dialog.findViewById<View>(R.id.deezer_ok_button) as Button
        // if button is clicked, close the custom dialog
        submitButton.setOnClickListener(View.OnClickListener { v ->
            if (TextUtils.isEmpty(searchString.text.toString().trim { it <= ' ' })) {
                Toast.makeText(this@CTDeviceBrowserActivity, getString(R.string.searchText), Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            closeKeyboard(this@CTDeviceBrowserActivity, v)

            val searchDataToBeSent = "SEARCH_" + searchString.text.toString()
            val selectedPositionToBeSent = LUCIMESSAGES.SELECT_ITEM + ":" + position

            LibreLogger.d("SearchString", "--$searchDataToBeSent")
            val luciPackets = ArrayList<LUCIPacket>()

            val searchPacket = LUCIPacket(searchDataToBeSent.toByteArray(), searchDataToBeSent.length.toShort(), MIDCONST.MID_REMOTE, LSSDPCONST.LUCI_SET.toByte())
            luciPackets.add(searchPacket)

            val positionPacket = LUCIPacket(selectedPositionToBeSent.toByteArray(),
                    selectedPositionToBeSent.length.toShort(), MIDCONST.MID_REMOTE, LSSDPCONST.LUCI_SET.toByte())
            luciPackets.add(positionPacket)
            luciControl!!.SendCommand(luciPackets)

            dialog.dismiss()
        })

        dialog.show()
    }

    /*showing dialog*/
    private fun showLoader(messageId: Int) {

        if (this@CTDeviceBrowserActivity.isFinishing)
            return
        showProgressDialog(messageId)
    }

    private fun closeLoader() {
        if (isFinishing) return
        dismissDialog()
    }

    override fun deviceDiscoveryAfterClearingTheCacheStarted() {

    }

    override fun newDeviceFound(node: LSSDPNodes) {

    }

    override fun deviceGotRemoved(mIpAddress: String) {

    }

    override fun messageRecieved(dataRecived: NettyData) {

        val buffer = dataRecived.getMessage()
        val ipaddressRecieved = dataRecived.getRemotedeviceIp()

        LibreLogger.d(TAG,"Message recieved for ipaddress $ipaddressRecieved")
        val sceneObject = mScanHandler.getSceneObjectFromCentralRepo(ipaddressRecieved)

        if (currentIpaddress!!.equals(ipaddressRecieved, ignoreCase = true)) {
            val packet = LUCIPacket(dataRecived.getMessage())
            when (packet.command) {

                MIDCONST.SET_UI -> {
                    val message = String(packet.getpayload())
                    LibreLogger.d(TAG, " message 42 recieved  $message")

                    try {
                        presentJsonHashCode = message.hashCode()
                        LibreLogger.d(TAG, " present hash code : the hash code for $message is $presentJsonHashCode")

                        LibreLogger.d(TAG,"==trackName==  ${sceneObject.trackName}")
                        setCurrentAlbumArt(sceneObject, binding.idMusicWidget.ivAlbumArt)
                        setCurrentTrackName(sceneObject, binding.idMusicWidget.tvTrackName)
                        setCurrentAlbumArtistName(sceneObject, binding.idMusicWidget.tvAlbumName)
                        if (LibreApplication.isUSBSrc == true) {
                            parseJsonAndReflectInUIUSB(message)
                            /*binding.idMusicWidget.tvTrackName.text = getText(R.string.title_libre_caps)*/
                        } else {
                            parseJsonAndReflectInUI(message)
                        }
                        binding.layShimmer.stopShimmer()
                        binding.layData.visibility = View.VISIBLE
                    } catch (e: JSONException) {
                        binding.layShimmer.stopShimmer()
                        binding.layData.visibility = View.VISIBLE
                        e.printStackTrace()
                        LibreLogger.d(TAG, " Json exception ")
                        closeLoader()
                    }

                }
                MIDCONST.MID_CURRENT_PLAY_STATE.toInt() -> {
                    val message = String(packet.getpayload())
                    LibreLogger.d(TAG,"==trackName== message 51 recieved  $message")
                    setCurrentPlayPauseIcon(sceneObject, binding.idMusicWidget.ivPlayPause,binding.layData,binding.layShimmer)
                }
                MIDCONST.MID_CURRENT_SOURCE.toInt() -> {
                    val message = String(packet.getpayload())

                    setCurrentSourceIcon(sceneObject, binding.idMusicWidget.ivCurrentSource)
                    /**
                     * If no source setting the default text and album art
                     */
                    handleAlexaViews(sceneObject,13,binding.idMusicWidget)
                    LibreLogger.d(TAG,"==trackName== message 50 recieved  $message")
                }
                MIDCONST.MID_PLAYTIME.toInt() -> {
                    val msg=String(packet.getpayload())
                    LibreLogger.d(TAG, "current duration of song $msg")
                    try {
                        if(msg.isNotEmpty()) {
                            val longDuration = java.lang.Long.parseLong(msg)
                            sceneObject.currentPlaybackSeekPosition = longDuration.toFloat()
                            ScanningHandler.getInstance().putSceneObjectToCentralRepo(dataRecived.getRemotedeviceIp(), sceneObject)
                            /**
                             * The below function will take care for setting the playback seek
                             * position
                             */
                            setCurrentPlaybackSeekPosition(sceneObject,binding.idMusicWidget.seekBarSong)
                        }
                    } catch (ex: Exception) {
                        LibreLogger.d(TAG, "current duration Exception ${ex.message}")
                        ex.printStackTrace()
                    }
                }

                MIDCONST.MID_DEVICE_ALERT_STATUS.toInt() -> {
                    val message = String(packet.getpayload())
                    LibreLogger.d(TAG," message 54 recieved  $message")
                    try {
                        var error: LibreError? = null
                        when {
                            message.contains(Constants.FAIL) -> error = LibreError(currentIpaddress, Constants.FAIL_ALERT_TEXT)
                            message.contains(Constants.SUCCESS) -> {
                                closeLoader()
                                handler.removeMessages(NETWORK_TIMEOUT)
                            }
                            message.contains(Constants.NO_URL) -> error = LibreError(currentIpaddress, getString(R.string.NO_URL_ALERT_TEXT))
                            message.contains(Constants.NO_PREV_SONG) -> error = LibreError(currentIpaddress, getString(R.string.NO_PREV_SONG_ALERT_TEXT))
                            message.contains(Constants.NO_NEXT_SONG) -> error = LibreError(currentIpaddress, getString(R.string.NO_NEXT_SONG_ALERT_TEXT))
                        }
                        if (error != null){
                            closeLoader()
                            handler.removeMessages(NETWORK_TIMEOUT)
                            showErrorMessage(error)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        LibreLogger.d(TAG," Json exception ")

                    }

                }
            }

        }
    }



    /**
     * This function gets the Json string
     */
    @Throws(JSONException::class)
    private fun parseJsonAndReflectInUI(jsonStr: String?) {
        LibreLogger.d(TAG,"Json Recieved from remote devicebrowser$jsonStr")
        if (jsonStr != null) {
            try {
                val root = JSONObject(jsonStr)
                val cmd_id = root.getInt(TAG_CMD_ID)
                val window = root.getJSONObject(TAG_WINDOW_CONTENT)
                LibreLogger.d(TAG,"Command Id$cmd_id")

                if (cmd_id == 3) {
                    closeLoader()
                    handler.removeMessages(NETWORK_TIMEOUT)
                    /* This means user has selected the song to be playing and hence we will need to navigate
                     him to the Active scene list
                      */
                    unRegisterForDeviceEvents()

                    val mScanHandler = ScanningHandler.getInstance()
                    var currentSceneObject = mScanHandler.getSceneObjectFromCentralRepo(currentIpaddress)
                    currentSceneObject = AppUtils.updateSceneObjectWithPlayJsonWindow(window, currentSceneObject!!)

                    if (mScanHandler!!.isIpAvailableInCentralSceneRepo(currentIpaddress)) {
                        mScanHandler.putSceneObjectToCentralRepo(currentIpaddress, currentSceneObject)
                    }

                    //Intent intent = new Intent(RemoteSourcesList.this, ActiveScenesListActivity.class);
                    LibreLogger.d("==BACK==","parseJsonAndReflectInUI 3")
                    isUSBBackPressed=true
                    val intent = Intent(this@CTDeviceBrowserActivity, CTNowPlayingActivity::class.java)
                    intent.putExtra(Constants.CURRENT_DEVICE_IP, currentIpaddress)
                    startActivity(intent)

                }

                else if (cmd_id == 1) {

                    browser = window.getString(TAG_BROWSER)
                    val Cur_Index = window.getInt(TAG_CUR_INDEX)
                    val item_count = window.getInt(TAG_ITEM_COUNT)

                    if (browser.equals("HOME", ignoreCase = true))
                    {
                        closeLoader()

//                        handler.removeMessages(NETWORK_TIMEOUT)
//                        /* This means we have reached the home collection and hence we need to lauch the SourcesOptionEntry Activity */
//                        unRegisterForDeviceEvents()

                        val intent = Intent(this@CTDeviceBrowserActivity, CTMediaSourcesActivity::class.java)
                        intent.putExtra(Constants.CURRENT_DEVICE_IP, currentIpaddress)

                        val currentSceneObject = ScanningHandler.getInstance().getSceneObjectFromCentralRepo(currentIpaddress)
                        intent.putExtra(Constants.CURRENT_SOURCE, "" + currentSceneObject!!.currentSource)
                        startActivity(intent)
                        finish()
                        // return
                    }

                    if (item_count == 0) {
                        val error = LibreError(currentIpaddress, getString(R.string.no_item_empty))
                        showErrorMessage(error)
                        closeLoader()
                        handler.removeMessages(NETWORK_TIMEOUT)
                        binding.tvNoData.visibility = View.VISIBLE
                    } else {
                        binding.tvNoData.visibility = View.GONE
                    }

                    val ItemList = window.getJSONArray(TAG_ITEM_LIST)
                    LibreLogger.d(TAG,"JSON PARSER item_count =  " + item_count + "  Array SIZE =" + ItemList.length())

                    val tempDataItem = ArrayList<DataItem>()
                    for (i in 0 until ItemList.length()) {
                        val item = ItemList.getJSONObject(i)
                        val dataItem = DataItem()
                        dataItem.itemID = item.getInt(TAG_ITEM_ID)
                        dataItem.itemType = item.getString(TAG_ITEM_TYPE)
                        dataItem.itemName = item.getString(TAG_ITEM_NAME)
                        dataItem.favorite = item.getInt(TAG_ITEM_FAVORITE)

                        if (item.has(TAG_ITEM_ALBUMURL)
                            && item.getString(TAG_ITEM_ALBUMURL) != null
                            && item.getString(TAG_ITEM_ALBUMURL).isNotEmpty()) {
                            dataItem.itemAlbumURL = item.getString(TAG_ITEM_ALBUMURL)
                        }

                        if (searchOptionClicked) {
                            //This JSON is the result,when user clicked search
                            // put to hashcode
                            searchJsonHashCode = jsonStr.hashCode()
                            LibreLogger.d(TAG, "Search hash code : the hash code for $jsonStr is$searchJsonHashCode")
                            searchOptionClicked = false

                            //save it in shared preference
                            val savedInPref = saveInSharedPreference(searchJsonHashCode)
                            if (savedInPref) {
                                LibreLogger.d(TAG,"saved in shared preference")
                            } else {
                                LibreLogger.d(TAG,"not saved in shared preference")
                            }

                        }
                        tempDataItem.add(dataItem)
                    }

                    dataItems!!.clear()
                    dataItems!!.addAll(tempDataItem)
                    deviceBrowserListAdapter?.updateList(dataItems)

                    val luciControl = LUCIControl(currentIpaddress)
                    //  luciControl.SendCommand(MIDCONST.MID_BLUETOOTH, CTMediaSourcesActivity.BLUETOOTH_DISCONNECT, LSSDPCONST.LUCI_SET)
                    //      TunnelingControl(currentIpaddress).sendCommand(PayloadType.DEVICE_SOURCE,0x01)

                    if (gotolastpostion)
                        mLayoutManager!!.scrollToPosition(49)
                    else
                        mLayoutManager!!.scrollToPosition(0)
                    gotolastpostion = false

                    if (handler.hasMessages(NETWORK_TIMEOUT)) handler.removeMessages(NETWORK_TIMEOUT)
                    closeLoader()

                }
            } catch (e: Exception) {
                e.printStackTrace()
                closeLoader()
            }

        } else closeLoader()


    }

    fun sendData(str: String) {
        // sendFMCommand()
        Log.v("LuciMessageSent", "Send Data :" + str)
        val devicIp = intent.getStringExtra(Constants.CURRENT_DEVICE_IP)
        //currentSourceIndexSelected = 3
        LUCIControl(devicIp).SendCommand(MIDCONST.MID_REMOTE_UI.toInt(), str, LSSDPCONST.LUCI_SET)
        //showLoader()
    }
    private fun showNextPreviousButtons(window: JSONObject) {
        try {
            val TAG = "showPrevNext"
            val browser = window.getString(TAG_BROWSER)
            val curIndex = window.getString(TAG_CUR_INDEX)
            val itemCount = window.getString(TAG_ITEM_COUNT)
            val totalItem = window.getString(TAG_TOTALITEM)
            val pageNo = window.getString(TAG_PAGE_NO)
            Log.v(TAG, "browser :$browser")
            Log.v(TAG, "curIndex :$curIndex")
            Log.v(TAG, "itemCount :$itemCount")
            Log.v(TAG, "totalItem :$totalItem")
            Log.v(TAG, "pageNo :$pageNo")
            var totalNoOfPages: Int = totalItem.toInt() / 20;

            if (totalItem.toInt() % 20 != 0) {
                totalNoOfPages += 1;
            }
            Log.v(TAG, "totalNo of Pages :$totalNoOfPages")
            binding.idTvPrevious.setOnClickListener(View.OnClickListener {
                sendData("SCROLLUP")
            })

            binding.idTvNext.setOnClickListener(View.OnClickListener {
                sendData("SCROLLDOWN")
            })
            if (totalNoOfPages > 1) {
                //show next page button
                binding.idPrevNextLayout.visibility = View.VISIBLE

                if (pageNo.toInt() > 1) {
                    // show prev button
                    binding.idTvPrevious.visibility = View.VISIBLE
                } else {
                    binding.idTvPrevious.visibility = View.INVISIBLE
                    // hide prev button
                }

                if (pageNo.toInt() == totalNoOfPages) {
                    // hide next page button
                    binding.idTvNext.visibility = View.INVISIBLE
                }
                else
                {
                    binding.idTvNext.visibility = View.VISIBLE
                }

            } else {
                //hide button ,previous button
                binding.idPrevNextLayout.visibility = View.INVISIBLE
            }

        } catch (ex: Exception) {
            Log.v("showPrevNext", "Exception :" + ex)
        }


    }

    /**
     * This function gets the Json string
     */
    @Throws(JSONException::class)
    private fun parseJsonAndReflectInUIUSB(jsonStr: String?) {
        LibreLogger.d(TAG, "====01Json Recieved from remote device $jsonStr")
        if (jsonStr != null) {
            try {
                val root = JSONObject(jsonStr)
                val cmd_id = root.getInt(TAG_CMD_ID)
                LibreLogger.d(TAG, "====04Command Id$cmd_id")

                val window = root.getJSONObject(TAG_WINDOW_CONTENT)
                showNextPreviousButtons(window)
//                val window1 = window.getJSONArray(TAG_ITEM_LIST)
//                for (i in 0 until window1.length()) {
//                    val item = window1.getJSONObject(i)
//                  //  LibreLogger.d(this, "====02Command As${item.get(AS)}")
//
////                    if(current_source_index_selected!=3)/* other than usb source check this condition*/
////                    {
////                        As= item.get(AS).toString()
////                    }
//                  // for usb --  As= item.get(AS).toString()
//                    //LibreLogger.d(this, "====03Command As${As}")
//                }
//                if(current_source_index_selected!=3) {
//                    setTitleForTheBrowser(current_source_index_selected,As)
//                }


//                LibreLogger.d(this, "====05Command window$window")
//                LibreLogger.d(this, "====06Command root$root")
//                LibreLogger.d(this, "====07Command window1$window1")


                if (cmd_id == 3) {
                    closeLoader()
                    handler.removeMessages(NETWORK_TIMEOUT)
                    /* This means user has selected the song to be playing and hence we will need to navigate
                     him to the Active scene list
                      */
                    unRegisterForDeviceEvents()

                    val mScanHandler = ScanningHandler.getInstance()
                    var currentSceneObject = mScanHandler.getSceneObjectFromCentralRepo(currentIpaddress)
                    currentSceneObject = AppUtils.updateSceneObjectWithPlayJsonWindow(window, currentSceneObject!!)
                    LibreLogger.d(TAG,"suma in get the cnd id 3 value\n"+currentSceneObject.currentSource)

                    if (mScanHandler!!.isIpAvailableInCentralSceneRepo(currentIpaddress)) {
                        mScanHandler.putSceneObjectToCentralRepo(currentIpaddress, currentSceneObject)
                    }

                    //Intent intent = new Intent(RemoteSourcesList.this, ActiveScenesListActivity.class);
                    LibreLogger.d("==BACK==","parseJsonAndReflectInUIUSB 3")
                    isUSBBackPressed=true
                    val intent = Intent(this@CTDeviceBrowserActivity, CTNowPlayingActivity::class.java)
                    intent.putExtra(Constants.CURRENT_DEVICE_IP, currentIpaddress)
                    startActivity(intent)

                }
                else if (cmd_id == 1) {

                    browser = window.getString(TAG_BROWSER)
                    val Cur_Index = window.getInt(TAG_CUR_INDEX)
                    val item_count = window.getInt(TAG_ITEM_COUNT)
                    LibreLogger.d(TAG, "Recieved from remote device item count"+"---"+item_count)
                    if (browser.equals("HOME", ignoreCase = true)) {
                        LibreLogger.d(TAG, "====07In side if")
                        closeLoader()
                        LibreLogger.d(TAG, "close laoder 7")

                        handler.removeMessages(NETWORK_TIMEOUT)
                        /* This means we have reached the home collection and hence we need to lauch the SourcesOptionEntry Activity */
                        unRegisterForDeviceEvents()
                        val intent = Intent(this@CTDeviceBrowserActivity, CTMediaSourcesActivity::class.java)
                        intent.putExtra(Constants.CURRENT_DEVICE_IP, currentIpaddress)

                        val currentSceneObject = ScanningHandler.getInstance().getSceneObjectFromCentralRepo(currentIpaddress)
                        intent.putExtra(Constants.CURRENT_SOURCE, "" + currentSceneObject!!.currentSource)
                        startActivity(intent)
                        finish()
                        return
                    }

                    if (item_count == 0) {
                        val error = LibreError(currentIpaddress, getString(R.string.no_item_empty))
                        showErrorMessage(error)
                        closeLoader()
                        LibreLogger.d(TAG, "close laoder 8 ")

                        handler.removeMessages(NETWORK_TIMEOUT)
                       //suma usb commnet  tv_no_data?.visibility = View.VISIBLE
                    } else {
                      ///suma usb comment  tv_no_data?.visibility = View.GONE
                    }

                    val ItemList = window.getJSONArray(TAG_ITEM_LIST)
                    LibreLogger.d(TAG, "====08JSON PARSER item_count =  " + item_count + "  Array SIZE = " + ItemList.length())

                    val tempDataItem = java.util.ArrayList<DataItem>()
                    for (i in 0 until ItemList.length()) {
                        val item = ItemList.getJSONObject(i)
                        val dataItem = DataItem()
                        dataItem.itemID = item.getInt(TAG_ITEM_ID)
                        dataItem.itemType = item.getString(TAG_ITEM_TYPE)
                        dataItem.itemName = item.getString(TAG_ITEM_NAME)
                        // for usb - dataItem.si = item.getString(SI)

//                        if(current_source_index_selected!=3) {
//                            dataItem.si = item.getString(SI)
//                        }
                        // dataItem.at = item.getString(AT)

                        if(current_source_index_selected!=8)
                        //for airable -  dataItem.favorite = item.getInt(TAG_ITEM_FAVORITE)
//                            if(current_source_index_selected!=5) {
//                                dataItem.favorite = item.getInt(TAG_ITEM_FAVORITE)
//                            }

                            if (item.has(TAG_ITEM_ALBUMURL) && item.getString(TAG_ITEM_ALBUMURL).isNotEmpty()) {
                                dataItem.itemAlbumURL = item.getString(TAG_ITEM_ALBUMURL)
                            }

                        if (searchOptionClicked) {
                            //This JSON is the result,when user clicked search
                            // put to hashcode
                            searchJsonHashCode = jsonStr.hashCode()
                            LibreLogger.d(TAG, "====09Search hash code : the hash code for $jsonStr is $searchJsonHashCode")
                            searchOptionClicked = false

                            //save it in shared preference
                            val savedInPref = saveInSharedPreference(searchJsonHashCode)
                            if (savedInPref) {
                                LibreLogger.d(TAG, "====10saved in shared preference")
                            } else {
                                LibreLogger.d(TAG, "====11not saved in shared preference")
                            }
                        }
                        tempDataItem.add(dataItem)
                    }

                    dataItems!!.clear()
                    dataItems!!.addAll(tempDataItem)
                    deviceBrowserListAdapter?.updateList(dataItems)
                    if (gotolastpostion)
                        mLayoutManager!!.scrollToPosition(49)
                    else
                        mLayoutManager!!.scrollToPosition(0)
                    gotolastpostion = false

                    if (handler.hasMessages(NETWORK_TIMEOUT)) handler.removeMessages(NETWORK_TIMEOUT)
                    closeLoader()

                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                LibreLogger.d("this","====012${e.message}")
                closeLoader()
                LibreLogger.d(TAG, "close laoder 9")

            }

        } else closeLoader()
        LibreLogger.d(TAG, "close laoder 11 ")


    }
    private fun saveInSharedPreference(hashResult: Int): Boolean {
        try {
            getSharedPreferences(Constants.SEARCH_RESULT_HASH_CODE, Context.MODE_PRIVATE).apply {
                edit()
                        .putInt(Constants.SEARCH_RESULT_HASH_CODE_VALUE, hashResult)
                        .apply()
            }
        } catch (e: Exception) {
            return false
        }

        return true
    }

    override fun onStop() {
        super.onStop()
        /*removing handler*/
        handler.removeCallbacksAndMessages(null)
        unRegisterForDeviceEvents()
    }

     fun exitOnBackPressed() {
        /* Sends the back command issues*/
        //showLoader(R.string.loading_prev_items)
        luciControl!!.SendCommand(MIDCONST.MID_REMOTE_UI.toInt(), BACK, LSSDPCONST.LUCI_SET)

        // finish()
        //////////// timeout for dialog - showLoader() ///////////////////
        if (current_source_index_selected == 0) {
            /*increasing timeout for media servers only*/
            handler.sendEmptyMessageDelayed(NETWORK_TIMEOUT, 60000)
        } else {
            handler.sendEmptyMessageDelayed(NETWORK_TIMEOUT, 15000)
        }

    }

    private fun setTitleForTheBrowser(current_source_index_selected: Int) {
        when (current_source_index_selected) {

            0 -> {

                binding.browserTitle.text = "USB"
                binding.browserTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
            1 -> {
                binding.browserTitle.text = ""
                binding.browserTitle.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.vtuner_logo_remotesources_title, 0, 0, 0)
            }
            2 -> {
                binding.browserTitle.text = "Tune In"
                binding.browserTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
            3 -> {
                binding.browserTitle.text = "Music Server"
                binding.browserTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
            4 -> {
                binding.browserTitle.text = "SD Card"
                binding.browserTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
            5 -> {
                binding.browserTitle.text = "Deezer"
                binding.browserTitle.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.deezer_crtification_logo, 0, 0, 0)
            }
            6 -> {
                binding.browserTitle.text = ""
                binding.browserTitle.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.tidal_title, 0, 0, 0)
            }
            7 -> {
                binding.browserTitle.text = "Favourites"
                binding.browserTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        }

    }

    fun onDataItemClicked(position: Int) {
        LibreLogger.d(TAG,"Play is clciked at position $position")

        if (browser.equals("TUNEIN", ignoreCase = true) && dataItems!![position].itemName.equals("search", ignoreCase = true)) {
            searchingDialog(position)

        } else if (browser.equals("VTUNER", ignoreCase = true) && dataItems!![position].itemName.equals("search", ignoreCase = true)) {
            searchingDialog(position)

        } else if ((browser.equals("TIDAL", ignoreCase = true) || browser.equals("DEEZER", ignoreCase = true))
                //  && !isSearchEnabled
                && dataItems!![position].itemName.equals("search", ignoreCase = true)) {

            luciControl!!.SendCommand(MIDCONST.MID_REMOTE.toInt(), LUCIMESSAGES.SELECT_ITEM + ":" + position, LSSDPCONST.LUCI_SET)
            //isSearchEnabled = true;
            searchOptionClicked = true
            LibreLogger.d(TAG, "Next JSON that comes is search result. Make hash code with the next result")

        } else if ((browser.equals("TIDAL", ignoreCase = true) || browser.equals("DEEZER", ignoreCase = true))
                //  && isSearchEnabled
                && (dataItems!![position].itemName.lowercase(Locale.getDefault()).startsWith("playlist")
                        ||
                        dataItems!![position].itemName.lowercase(Locale.getDefault()).startsWith("artist")
                        ||
                        dataItems!![position].itemName.lowercase(Locale.getDefault()).startsWith("album")
                        ||
                        dataItems!![position].itemName.lowercase(Locale.getDefault()).startsWith("track")
                        ||
                        dataItems!![position].itemName.equals("podcast", ignoreCase = true))) {
            // dialog should be shown only when it is one stage above "search"

            if (presentJsonHashCode == searchJsonHashCode) {
                LibreLogger.d(TAG,"hash codes matched. Can show dialog")
                searchingDialog(position)
            } else {
                LibreLogger.d(TAG,"hash codes did not match.")
                luciControl!!.SendCommand(MIDCONST.MID_REMOTE.toInt(), LUCIMESSAGES.SELECT_ITEM + ":" + position, LSSDPCONST.LUCI_SET)
                showLoader(R.string.loading_next_items)
                LibreLogger.d(TAG,"ItemCLick showloader3")

            }


        } else {
            if (dataItems!![position].itemType.contains("File")) {
                isSongSelected = true
            }

            luciControl!!.SendCommand(MIDCONST.MID_REMOTE.toInt(), LUCIMESSAGES.SELECT_ITEM + ":" + position, LSSDPCONST.LUCI_SET)
            //////////// timeout for dialog - showLoader() ///////////////////
            if (current_source_index_selected == 0) {
                /*increasing timeout for media servers only*/
                handler.sendEmptyMessageDelayed(NETWORK_TIMEOUT, 60000)
            } else {
                handler.sendEmptyMessageDelayed(NETWORK_TIMEOUT, 15000)
            }
            showLoader(R.string.loading_next_items)
            LibreLogger.d(TAG,"ItemCLick showloader2")

        }
    }

    fun onFavClicked(position: Int) {
        val dataItem = dataItems!![position]

        if (dataItem.favorite == 2) {
            /*remove here*/
            luciControl!!.SendCommand(MIDCONST.MID_REMOTE.toInt(), LUCIMESSAGES.REMOVE_FAVORITE_ITEM + ":" + position, LSSDPCONST.LUCI_SET)
            dataItem.favorite = 1
            deviceBrowserListAdapter!!.notifyDataSetChanged()
        }

        if (dataItem.favorite == 1) {
            /*make fav here*/
            luciControl!!.SendCommand(MIDCONST.MID_REMOTE.toInt(), LUCIMESSAGES.FAVORITE_ITEM + ":" + position, LSSDPCONST.LUCI_SET)
            dataItem.favorite = 2
            deviceBrowserListAdapter!!.notifyDataSetChanged()
        }
    }

    fun selectItem(row: Int) {
        Log.i(CTDeviceBrowserActivity::class.simpleName, "selectItem: ${row}")
        showLoader(R.string.loading_next_items)
        LibreLogger.d(TAG,"ItemCLick showloader1")
        val devicIp = intent.getStringExtra(Constants.CURRENT_DEVICE_IP)
        LUCIControl(devicIp).SendCommand(MIDCONST.MID_REMOTE_UI.toInt(), SELECTITEM + row, LSSDPCONST.LUCI_SET)
    }

}
