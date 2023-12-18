package com.cumulations.libreV2.activity

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteException
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cumulations.libreV2.*
import com.cumulations.libreV2.AppUtils.providesSharedPreference
import com.cumulations.libreV2.AppUtils.setPlayPauseLoader
import com.cumulations.libreV2.activity.oem.CastToSActivity
import com.cumulations.libreV2.fragments.CTActiveDevicesFragment
import com.cumulations.libreV2.fragments.CTNoDeviceFragment
import com.cumulations.libreV2.model.SceneObject
import com.cumulations.libreV2.model.SceneObject.CURRENTLY_BUFFERING
import com.cumulations.libreV2.model.SceneObject.CURRENTLY_NOTPLAYING
import com.cumulations.libreV2.model.SceneObject.CURRENTLY_PAUSED
import com.cumulations.libreV2.model.SceneObject.CURRENTLY_PLAYING
import com.cumulations.libreV2.model.SceneObject.CURRENTLY_STOPPED
import com.cumulations.libreV2.roomdatabase.CastLiteUUIDDataClass
import com.cumulations.libreV2.roomdatabase.LibreVoiceDatabase
import com.cumulations.libreV2.tcp_tunneling.TCPTunnelPacket
import com.cumulations.libreV2.tcp_tunneling.TunnelingControl
import com.cumulations.libreV2.tcp_tunneling.TunnelingData
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import com.libreAlexa.DMRPlayerService
import com.libreAlexa.LErrorHandeling.LibreError
import com.libreAlexa.LibreApplication
import com.libreAlexa.LibreApplication.*
import com.libreAlexa.LibreEntryPoint
import com.libreAlexa.Ls9Sac.FwUpgradeData
import com.libreAlexa.Ls9Sac.GcastUpdateStatusAvailableListView
import com.libreAlexa.R
import com.libreAlexa.Scanning.ScanThread
import com.libreAlexa.Scanning.ScanningHandler
import com.libreAlexa.alexa.*
import com.libreAlexa.app.dlna.dmc.gui.abstractactivity.UpnpListenerActivity
import com.libreAlexa.app.dlna.dmc.processor.impl.UpnpProcessorImpl
import com.libreAlexa.app.dlna.dmc.processor.upnp.CoreUpnpService
import com.libreAlexa.app.dlna.dmc.server.ContentTree
import com.libreAlexa.app.dlna.dmc.utility.UpnpDeviceManager
import com.libreAlexa.constants.AppConstants
import com.libreAlexa.constants.AppConstants.LOCATION_PERMISSION_REQUEST_CODE
import com.libreAlexa.constants.AppConstants.LOCATION_PERM_SETTINGS_REQUEST_CODE
import com.libreAlexa.constants.AppConstants.LOCATION_SETTINGS_REQUEST_CODE
import com.libreAlexa.constants.AppConstants.MICROPHONE_PERMISSION_REQUEST_CODE
import com.libreAlexa.constants.AppConstants.MICROPHONE_PERM_SETTINGS_REQUEST_CODE
import com.libreAlexa.constants.AppConstants.READ_STORAGE_REQUEST_CODE
import com.libreAlexa.constants.AppConstants.STORAGE_PERM_SETTINGS_REQUEST_CODE
import com.libreAlexa.constants.AppConstants.WIFI_SETTINGS_REQUEST_CODE
import com.libreAlexa.constants.Constants.*
import com.libreAlexa.constants.LSSDPCONST
import com.libreAlexa.constants.LUCIMESSAGES
import com.libreAlexa.constants.MIDCONST
import com.libreAlexa.constants.MIDCONST.MID_BLUETOOTH_STATUS
import com.libreAlexa.databinding.MusicPlayingWidgetBinding
import com.libreAlexa.luci.*
import com.libreAlexa.netty.BusProvider
import com.libreAlexa.netty.LibreDeviceInteractionListner
import com.libreAlexa.netty.NettyData
import com.libreAlexa.netty.RemovedLibreDevice
import com.libreAlexa.util.LibreLogger
import com.libreAlexa.util.PicassoTrustCertificates
import com.squareup.otto.Subscribe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jboss.netty.channel.ChannelFuture
import org.json.JSONObject
import java.text.DateFormat
import java.util.*
import java.util.UUID.randomUUID


/**
 * Created by Amit on 10/05/19.
 */

@SuppressLint("Registered")
open class CTDeviceDiscoveryActivity : UpnpListenerActivity(), AudioRecordCallback,
    MicExceptionListener {

    companion object {
        private val TAG = CTDeviceDiscoveryActivity::class.java.simpleName
        const val TAG_DEVICE_REMOVED = "TAG_DEVICE_REMOVED"
        var isKeyStored = false
        var isBTPermissionAsked = false
        val TAG_ = "SHAIKM"
    }

    private var libreDeviceInteractionListner: LibreDeviceInteractionListner? = null
    private var locationPermissionCallback: LocationPermissionCallback? = null
    var upnpProcessor: UpnpProcessorImpl? = null
    private var localNetworkStateReceiver: LocalNetworkStateReceiver? = null
    private val progressDialog by lazy { CustomProgressDialog(this) }

    var isNetworkChangesCallBackEnabled = true
        private set
    var isNetworkOffCallBackEnabled = true
        private set
    protected var alertDialog1: AlertDialog? = null
    private var alertRestartApp: AlertDialog? = null
    private var isActivityPaused = false
    lateinit var libreApplication: LibreEntryPoint
   // lateinit var libreApplication: LibreEntryPoint

    private var mandateDialog: AlertDialog? = null
    private var alertDialog: AlertDialog? = null
  //  lateinit var sharedPreferenceHelper: SharedPreferenceHelper
    private var parentView: View? = null
    private var onReceiveSSID: String? = null

    private var musicPlayerIp: String? = null
    private var musicPlayerWidget: ViewGroup? = null
    private var albumArtView: AppCompatImageView? = null
    private var currentSourceView: AppCompatImageView? = null
    private var playPauseView: ProgressButtonImageView? = null
    private var alexaButton: AppCompatImageButton? = null
    private var songSeekBar: AppCompatSeekBar? = null
    private var trackNameView: AppCompatTextView? = null
    private var albumNameView: AppCompatTextView? = null
    private var listeningView: AppCompatTextView? = null
    private var playinLayout: LinearLayout? = null
    private var shimmerView: ShimmerFrameLayout? = null
    private var currentTrackName = "-1"
    private var audioRecordUtil: AudioRecordUtil? = null
    private var micTcpServer: MicTcpServer? = null
    private var requestCode: Int = -1
    private var resultHandler: ActivityResultLauncher<Intent>? = null
   // private val libreVoiceDatabaseDao:LibreVoiceDatabase?=null
/*    private val libreVoiceDatabaseDao by lazy { LibreVoiceDatabase.getDatabase(this).castLiteDao() }*/

    private var busEventListener: Any = object : Any() {
        @Subscribe
        fun newDeviceFound(nodes: LSSDPNodes) {
           /* GlobalScope.launch {
                    insertDeviceIntoDb(nodes, "DI")
            }*/
            if (libreDeviceInteractionListner != null) {
                libreDeviceInteractionListner!!.newDeviceFound(nodes)
            }
            if (nodes.getgCastVerision() != null) checkForTheSACDeviceSuccessDialog(nodes)

            if (alertDialog1 != null && alertDialog1?.isShowing!!) {
                alertDialog1?.dismiss()
            }
        }

        @Subscribe
        fun newMessageRecieved(nettyData: NettyData?) {
            val dummyPacket = LUCIPacket(nettyData?.getMessage())
            val time = DateFormat.getInstance().format(System.currentTimeMillis())
            LibreLogger.d(TAG, "newMessageRecieved, ip = ${nettyData?.getRemotedeviceIp()}, " + "MB = " + "${dummyPacket.command}, " + "msg = ${String(dummyPacket.payload)} at $time")
            LibreLogger.d(TAG, "suma in new message received getting the data MB: " + "${
                dummyPacket.command
            }" + " msg = ${String(dummyPacket.payload)}")
            if (nettyData != null) {
                libreDeviceInteractionListner?.messageRecieved(nettyData)
                parseMessageForMusicPlayer(nettyData)
            }

        }

        @Subscribe
        fun deviceGotRemovedFromIpAddress(deviceIpAddress: String?) {
            LibreLogger.d(TAG_DEVICE_REMOVED, "deviceGotRemovedFromIpAddress, $deviceIpAddress")
            if (deviceIpAddress != null) {
                if (libreDeviceInteractionListner != null) {
                    libreDeviceInteractionListner!!.deviceGotRemoved(deviceIpAddress)
                }
            }
        }

        @Subscribe
        fun deviceGotRemoved(removedLibreDevice: RemovedLibreDevice?) {
            LibreLogger.d(TAG, "==SHAIK==deviceGotRemoved CTDevice " + removedLibreDevice?.getmIpAddress())
            if (removedLibreDevice != null) {
                GlobalScope.launch {
                    removeUUIDFromDB(removedLibreDevice.getmIpAddress())
                }
                val nodes = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(removedLibreDevice.getmIpAddress())
                LibreLogger.d(TAG_DEVICE_REMOVED, "deviceGotRemoved before appInForeground ")
                if (appInForeground(this@CTDeviceDiscoveryActivity)) {
                    if (nodes != null) {
                          LibreLogger.d(TAG_DEVICE_REMOVED, "deviceGotRemoved, class name = " + this.javaClass.simpleName)
                        if (libreDeviceInteractionListner !is CTConnectingToMainNetwork) {
                             LibreLogger.d(TAG_DEVICE_REMOVED, "deviceGotRemoved " +
                                     "CTConnectingToMainNetwork $isForgetNetworkCalled")
                            if(!isForgetNetworkCalled) {
                                alertDialogForDeviceNotAvailable(LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(removedLibreDevice.getmIpAddress()))
                            }
                            else{
                                isForgetNetworkCalled=false
                            }
                        } else {
                            LibreLogger.d(TAG_DEVICE_REMOVED, "deviceGotRemoved Not " + "CTConnectingToMainNetwork")
                            removeTheDeviceFromRepo(removedLibreDevice.getmIpAddress())
                        }
                    }
                } else {
                    LibreLogger.d(TAG_DEVICE_REMOVED, "deviceGotRemoved App Not InForeground ")
                    removeTheDeviceFromRepo(removedLibreDevice.getmIpAddress())
                }
            }else{
                LibreLogger.d(TAG_DEVICE_REMOVED, "deviceGotRemoved called ELSE")
            }
        }

        @Subscribe
        fun fwUpdateInternetFound(fwUpgradeData: FwUpgradeData) {
            LibreLogger.d(TAG, "fwUpdateInternetFound, device = " + fwUpgradeData.getmDeviceName() + ",[" + FW_UPDATE_AVAILABLE_LIST.keys.toString() + "]")
            val intent = Intent(this@CTDeviceDiscoveryActivity, GcastUpdateStatusAvailableListView::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        @Subscribe
        fun libreErrorReceived(libreError: LibreError) {
            if (libreDeviceInteractionListner != null && !isActivityPaused || !isFinishing) {
                showErrorMessage(libreError)
            }
        }

        @Subscribe
        fun tunnelingMessageReceived(tunnelingData: TunnelingData) {
            LibreLogger.d(TAG, "tunnelingMessageReceived, data = ${
                TunnelingControl.getReadableHexByteArray(tunnelingData.remoteMessage)
            }")

            if (tunnelingData.remoteMessage.size == 4) {/*Model Id only when 0x01 0x05 0x05 0x01~0x08*/
                val byteArray = tunnelingData.remoteMessage
                if (byteArray[0].toInt() == 0x01 && byteArray[1].toInt() == 0x05 && byteArray[2].toInt() == 0x05) {

                    val tcpTunnelPacket = TCPTunnelPacket(tunnelingData.remoteMessage, tunnelingData.remoteMessage.size)
                    if (tcpTunnelPacket.modelId != null) {
                        val lssdpNodes = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(tunnelingData.remoteClientIp)
                        lssdpNodes?.modelId = tcpTunnelPacket.modelId
                        LibreLogger.d(TAG, "tunnelingMessageReceived, modelId = ${tcpTunnelPacket.modelId}")
                    }
                }
            }

            if (libreDeviceInteractionListner != null) {
                LibreLogger.d(TAG, "tunnelDataReceived, libreDeviceInteractionListner = " + libreDeviceInteractionListner!!::class.java.simpleName)
                if (libreDeviceInteractionListner is Activity) {
                    if ((libreDeviceInteractionListner as Activity).isVisibleToUser()) {
                        tunnelDataReceived(tunnelingData)
                    }
                } else if (libreDeviceInteractionListner is CTActiveDevicesFragment) {
                    tunnelDataReceived(tunnelingData)
                }
            }
        }
    }

    /*Will be overridden by Child classes who want this data*/
    open fun tunnelDataReceived(tunnelingData: TunnelingData) {
        LibreLogger.d(TAG, "tunnelDataReceived, ip = ${tunnelingData.remoteClientIp}")
        if (tunnelingData.remoteMessage?.size!! >= 24) {
            val tcpTunnelPacket = TCPTunnelPacket(tunnelingData.remoteMessage)

            val sceneObject = ScanningHandler.getInstance().getSceneObjectFromCentralRepo(tunnelingData.remoteClientIp) ?: return

//            if (tcpTunnelPacket.volume >= 0) {
//                LibreLogger.d(TAG, "tunnelDataReceived, ip ${tunnelingData.remoteClientIp}, volume = ${tcpTunnelPacket.volume}")/*Fucking don't multiply by 5 again, we are handling it from packet only*/
//        SUMA VOLUME COMMENT        sceneObject.volumeValueInPercentage = tcpTunnelPacket.volume
//                INDIVIDUAL_VOLUME_MAP[sceneObject.ipAddress] = sceneObject.volumeValueInPercentage
//                ScanningHandler.getInstance().putSceneObjectToCentralRepo(sceneObject.ipAddress, sceneObject)
//            }

        }
    }

    private val isMicrophonePermissionGranted: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AppUtils.isPermissionGranted(this, Manifest.permission.RECORD_AUDIO)
        } else true

    private val upnpBinder: CoreUpnpService.Binder?
        get() = upnpProcessor!!.binder
    private var sAcalertDialog: AlertDialog? = null
    private var fwUpdateAlertDialog: AlertDialog? = null
    private var libreVoiceDatabase: LibreVoiceDatabase? = null
    val sharedPreferenceHelper by lazy { SharedPreferenceHelper.getInstance(this)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        libreApplication = LibreEntryPoint()
        //sharedPreferenceHelper = SharedPreferenceHelper.getInstance(this)
        //Key Storing logic getting late so added in in the onCreate
        lifecycleScope.launch {
            delay(2000)
            if (!isKeyStored) {
                doSharedOperations()
            }
        }
        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        localNetworkStateReceiver = LocalNetworkStateReceiver()
        registerForActivityResult()
        registerReceiver(localNetworkStateReceiver, intentFilter)
        LibreLogger.d(TAG,"CTDeviceDiscoveryActivity onCreate called ")
        lifecycleScope.launch {
            delay(2000)
            libreVoiceDatabase = LibreVoiceDatabase.getDatabase(applicationContext)
        }

    }

    private fun initUpnpProcessorListeners() {
        upnpProcessor = UpnpProcessorImpl(this)
        upnpProcessor?.addListener(this)
        upnpProcessor?.addListener(UpnpDeviceManager.getInstance())
        upnpProcessor?.bindUpnpService()
    }

    override fun onStart() {
        super.onStart()
//        if (!isKeyStored) {
//            doSharedOperations()
//        }
        initBusEvent()
        initUpnpProcessorListeners()
        initDMRForegroundService()
        parentView = window.decorView.rootView
        proceedToHome()
    }

    private fun initDMRForegroundService() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(Intent(this@CTDeviceDiscoveryActivity, DMRPlayerService::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    action = ACTION.START_DMR_SERVICE
                })
            } else {
                startService(Intent(this@CTDeviceDiscoveryActivity, DMRPlayerService::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    action = ACTION.START_DMR_SERVICE
                })
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            LibreLogger.d(TAG, "initDMRForegroundService exception = ${e.message}")
        }
    }

    open fun proceedToHome() {
    }

    fun checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !AppUtils.isPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            LibreLogger.d(TAG_, "checkLocationPermission if")
            requestLocationPermission()
        } else {
            LibreLogger.d(TAG_,"checkLocationPermission else")
            locationPermissionCallback?.onPermissionGranted()
            if (mandateDialog != null && mandateDialog!!.isShowing) mandateDialog!!.dismiss()

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                onReceiveSSID = getConnectedSSIDName(this)
                return
            }

            if (AppUtils.isLocationServiceEnabled(this)) {
                LibreLogger.d(TAG_,"checkLocationPermission yes")
                onReceiveSSID = getConnectedSSIDName(this)
            } else {
                if (isWifiON) {
                    LibreLogger.d(TAG_,"checkLocationPermission no")
                    showLocationMustBeEnabledDialog()
                }
            }
        }
    }

    open fun storagePermissionAvailable() {}

    fun checkReadStoragePermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !AppUtils.isPermissionGranted(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            LibreLogger.d(TAG, "checkStoragePermission Not granted")
            requestStoragePermission()
            return false
        } else {
            LibreLogger.d(TAG, "checkStoragePermission Permission already granted.")
            storagePermissionAvailable()
        }
        return true
    }

    private fun requestLocationPermission() {
        if (AppUtils.shouldShowPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            /**
             * Shaik Added the snackBar above to BottomSheet
             */
            showAlertDialog(getString(R.string.permission_location_rationale), getString(R.string.ok),
                LOCATION_PERM_SETTINGS_REQUEST_CODE, isDeviceLost = false, isLocationPermission
                =true,  isLocationPermissionRotational=false)
           /* val snackBar = Snackbar.make(parentView!!, R.string.permission_location_rationale, Snackbar.LENGTH_INDEFINITE)
            snackBar.setAction(R.string.ok) {
                AppUtils.requestPermission(this@CTDeviceDiscoveryActivity, Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_REQUEST_CODE)
            }
            snackBar.anchorView = findViewById(R.id.bottom_navigation)
            snackBar.setActionTextColor(ContextCompat.getColor(this@CTDeviceDiscoveryActivity, R.color.brand_orange))
            snackBar.show()*/
        } else {
            if (!sharedPreferenceHelper.isFirstTimeAskingPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                sharedPreferenceHelper.firstTimeAskedPermission(Manifest.permission.ACCESS_FINE_LOCATION, true)
                // No explanation needed, we can request the permission.
                // Camera permission has not been granted yet. Request it directly.
                AppUtils.requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_REQUEST_CODE)
            } else {
                // showAlertForLocationPermissionRequired()
                showAlertDialog(getString(R.string.enableLocationPermit), getString(R.string
                    .action_settings), LOCATION_PERM_SETTINGS_REQUEST_CODE, isDeviceLost = false,
                    isLocationPermission =false,  isLocationPermissionRotational=true)
            }

        }
    }

    private fun showStoragePermDisabledSnackBar() {
        //Permission disable by device policy or user denied permanently. Show proper error message
        Snackbar.make(parentView!!, R.string.storagePermitToast, Snackbar.LENGTH_INDEFINITE).setAction(R.string.open) {
            customStartActivityForResult(STORAGE_PERM_SETTINGS_REQUEST_CODE, Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package", packageName, null)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY).addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS))
        }.show()
    }

    fun showLocationMustBeEnabledDialog() {
        if (mandateDialog != null && mandateDialog!!.isShowing) mandateDialog!!.dismiss()

        if (mandateDialog == null) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(getString(R.string.enable_location))
            builder.setCancelable(false)
            builder.setPositiveButton(R.string.ok) { dialogInterface, i ->
                mandateDialog!!.dismiss()
                displayLocationSettingsRequest()
            }
            builder.setNegativeButton(R.string.cancel) { dialogInterface, i ->
                mandateDialog!!.dismiss()
                //If user denies the phone GPS location, have to educate to user for now if user
                // clicks popup is coming, we can do modren way alos like automaticlaly trigger
                // popup
                locationPermissionCallback?.onPermissionDenied()
                //                    killApp();
            }
            mandateDialog = builder.create()
        }

        if (!mandateDialog!!.isShowing) mandateDialog!!.show()
    }

    private fun createLocationRequest(): LocationRequest {
        val locationRequest = LocationRequest.create()
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        return locationRequest
    }

    private fun displayLocationSettingsRequest() {
        val googleApiClient = GoogleApiClient.Builder(this).addApi(LocationServices.API).build()
        googleApiClient.connect()

        val builder = LocationSettingsRequest.Builder().addLocationRequest(createLocationRequest()).setAlwaysShow(true)

        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(this) {
            LibreLogger.d(TAG_, "displayLocationSettingsRequest success")
            locationPermissionCallback?.onGPSGranted()
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
        }

        task.addOnFailureListener(this) { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    locationPermissionCallback?.onGPSGranted()
                    LibreLogger.d(TAG_, "displayLocationSettingsRequest Failure")
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(this@CTDeviceDiscoveryActivity, LOCATION_SETTINGS_REQUEST_CODE)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                    sendEx.printStackTrace()
                }

            }
        }
    }

    private fun requestStoragePermission() {
        if (AppUtils.shouldShowPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            LibreLogger.d(TAG, "requestLocPerm Displaying location permission rationale to provide" + " additional context.")
            Snackbar.make(parentView!!, R.string.storagePermitToast, Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok) { AppUtils.requestPermission(this@CTDeviceDiscoveryActivity, Manifest.permission.READ_EXTERNAL_STORAGE, READ_STORAGE_REQUEST_CODE) }.show()
        } else {
            if (!sharedPreferenceHelper.isFirstTimeAskingPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                sharedPreferenceHelper.firstTimeAskedPermission(Manifest.permission.READ_EXTERNAL_STORAGE, true)
                // No explanation needed, we can request the permission.
                // Camera permission has not been granted yet. Request it directly.
                AppUtils.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, READ_STORAGE_REQUEST_CODE)
            } else {
                showStoragePermDisabledSnackBar()
            }

        }
    }

    fun killApp() {
        stopDmrPlayerService()
        finishActivitiesAndKillAppProcess()
    }


    private fun stopDmrPlayerService() {/* Stopping ForeGRound Service When we are Restarting the APP */
        stopService(Intent(this, DMRPlayerService::class.java))
        Log.d(TAG, "stopDmrPlayerService done")
    }

    private fun finishActivitiesAndKillAppProcess() {
        ActivityCompat.finishAffinity(this)/* Killing our Android App with The PID For the Safe Case */
        val pid = android.os.Process.myPid()
        android.os.Process.killProcess(pid)
    }

    fun showSomethingWentWrongAlert(context: Context) {
        LibreLogger.d(TAG,"class name ${context::class.java.simpleName}")
        try {
            if (alertDialog1 != null && alertDialog1!!.isShowing) alertDialog1!!.dismiss()
            val builder = AlertDialog.Builder(context)
            builder.setMessage(resources.getString(R.string.somethingWentWrong)).setCancelable(false).setPositiveButton("OK") { dialog, id ->
                     if (context::class.java.simpleName == CTBluetoothPassCredentials::class.java.simpleName
                    || context::class.java.simpleName == CastToSActivity::class.java.simpleName
                    || context::class.java.simpleName == CTWifiListActivity::class.java.simpleName) {
                    intentToHome(context)
                }
                alertDialog1!!.dismiss()
            }

            if (alertDialog1 == null) {
                alertDialog1 = builder.create()
                val messageView = alertDialog1!!.findViewById<TextView>(android.R.id.message)
                messageView?.gravity = Gravity.CENTER
            }
            alertDialog1!!.show()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun getConnectedSSIDName(mContext: Context): String {
        return AppUtils.getConnectedSSID(mContext)!!
    }

    override fun onPause() {
        super.onPause()
        isActivityPaused = true
        progressDialog.stop()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setMusicPlayerWidget(musicPlayerWidget: ViewGroup, musicPlayerIp: String) {
        try {
            LibreLogger.d(TAG, "setMusicPlayerWidget, class = ${this::class.java.simpleName} and $musicPlayerIp")
            this.musicPlayerWidget = musicPlayerWidget
            this.musicPlayerIp = musicPlayerIp
            audioRecordUtil = AudioRecordUtil.getAudioRecordUtil()
            micTcpServer = MicTcpServer.getMicTcpServer()
            libreApplication.registerForMicException(this)
            songSeekBar = musicPlayerWidget.findViewById(R.id.seek_bar_song)
            albumArtView = musicPlayerWidget.findViewById(R.id.iv_album_art)
            trackNameView = musicPlayerWidget.findViewById(R.id.tv_track_name)
            albumNameView = musicPlayerWidget.findViewById(R.id.tv_album_name)
            currentSourceView = musicPlayerWidget.findViewById(R.id.iv_current_source)
            playPauseView = musicPlayerWidget.findViewById(R.id.iv_play_pause)
            alexaButton = musicPlayerWidget.findViewById(R.id.ib_alexa_avs_btn)
            listeningView = musicPlayerWidget.findViewById(R.id.tv_alexa_listening)
            playinLayout = musicPlayerWidget.findViewById(R.id.ll_playing_layout)
            /*shimmerView = musicPlayerWidget.findViewById(R.id.shimmer_View)
            musicPlayerWidget.visibility=View.INVISIBLE
            shimmerView?.startShimmer()*/
            setMusicPlayerListeners()

            /*val sceneObject = ScanningHandler.getInstance().getSceneObjectFromCentralRepo(musicPlayerIp)
            if (sceneObject != null) {
                LibreLogger.d(TAG, "updateMusicPlayViews: called from  $musicPlayerIp")
                updateMusicPlayViews(sceneObject,1,musicPlayerIp)
                LibreLogger.d(TAG, "suma in main scene object" + sceneObject.playUrl)
            }*/
        } catch (e: KotlinNullPointerException) {
            e.printStackTrace()
        }
    }

    private fun setMusicPlayerListeners() {
        val musicSceneObject = ScanningHandler.getInstance().getSceneObjectFromCentralRepo(musicPlayerIp)
        LibreLogger.d(TAG, "setMusicPlayerListeners $musicSceneObject")
        if (musicSceneObject == null) return
        LibreLogger.d(TAG, "setMusicPlayerListeners null")
        val mNodeWeGotForControl = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(musicPlayerIp)
        val control = LUCIControl(musicPlayerIp)

        playPauseView?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                if (musicSceneObject.currentSource == AUX_SOURCE/*|| musicSceneObject?.currentSource == EXTERNAL_SOURCE*/ || musicSceneObject.currentSource == VTUNER_SOURCE || musicSceneObject.currentSource == TUNEIN_SOURCE /*|| musicSceneObject.currentSource == BT_SOURCE*/ && ((mNodeWeGotForControl?.getgCastVerision() == null && (mNodeWeGotForControl?.bT_CONTROLLER == CURRENTLY_NOTPLAYING || mNodeWeGotForControl?.bT_CONTROLLER == SceneObject.CURRENTLY_PLAYING)) || (mNodeWeGotForControl.getgCastVerision() != null && mNodeWeGotForControl.bT_CONTROLLER < SceneObject.CURRENTLY_PAUSED))) {
                    val error = LibreError("", resources.getString(R.string.PLAY_PAUSE_NOT_ALLOWED), 1)
                    BusProvider.getInstance().post(error)
                    return

                }
                if (musicSceneObject.currentSource == NO_SOURCE || (musicSceneObject.currentSource == DMR_SOURCE && (musicSceneObject.playstatus == CURRENTLY_STOPPED || musicSceneObject.playstatus == CURRENTLY_NOTPLAYING))) {
                    LibreLogger.d(TAG, "currently not playing, so take user to sources option activity")
                    gotoSourcesOption(musicSceneObject.ipAddress, musicSceneObject.currentSource)
                    return
                }

                if (musicSceneObject.playstatus == CURRENTLY_PLAYING) {
                    LUCIControl.SendCommandWithIp(MIDCONST.MID_PLAYCONTROL.toInt(), LUCIMESSAGES.PAUSE, LSSDPCONST.LUCI_SET, musicSceneObject.ipAddress)
                } else {
                    if (musicSceneObject.currentSource == BT_SOURCE) {
                        LUCIControl.SendCommandWithIp(MIDCONST.MID_PLAYCONTROL.toInt(), LUCIMESSAGES.PLAY, LSSDPCONST.LUCI_SET, musicSceneObject.ipAddress)
                    } else {
                        LUCIControl.SendCommandWithIp(MIDCONST.MID_PLAYCONTROL.toInt(), LUCIMESSAGES.RESUME, LSSDPCONST.LUCI_SET, musicSceneObject.ipAddress)
                    }
                }
            }
        })

        alexaButton?.setOnLongClickListener(View.OnLongClickListener {
            if (!isMicrophonePermissionGranted) {
                requestRecordPermission()
                return@OnLongClickListener true
            }

            if (mNodeWeGotForControl == null || mNodeWeGotForControl.alexaRefreshToken?.isNullOrEmpty()!!) {
                musicSceneObject.isAlexaBtnLongPressed = false
                toggleAVSViews(false)
                showLoginWithAmazonAlert(musicSceneObject.ipAddress)
                return@OnLongClickListener true
            }

            val phoneIp = phoneIpAddress()
            if (!musicSceneObject.ipAddress.isNullOrEmpty() && phoneIp.isNotEmpty()) {

                LibreLogger.d(TAG, "OnLongClick phone ip: " + phoneIp + "port: " + MicTcpServer.MIC_TCP_SERVER_PORT)
                control.SendCommand(MIDCONST.MID_MIC, START_MIC + phoneIp + "," + MicTcpServer.MIC_TCP_SERVER_PORT, LSSDPCONST.LUCI_SET)

                musicSceneObject.isAlexaBtnLongPressed = true
                toggleAVSViews(true)
                audioRecordUtil?.startRecording(this@CTDeviceDiscoveryActivity)
            } else {
                musicSceneObject.isAlexaBtnLongPressed = false
                toggleAVSViews(false)

            }
            true
        })

        alexaButton?.setOnTouchListener { view, motionEvent ->
            LibreLogger.d(TAG, "AlexaBtn motionEvent = " + motionEvent.action)
            if (motionEvent.action == MotionEvent.ACTION_UP || motionEvent.action == MotionEvent.ACTION_CANCEL) {
                LibreLogger.d(TAG, "AlexaBtn long press release, sceneObject isLongPressed = " + musicSceneObject.isAlexaBtnLongPressed)/*if (sceneObject?.isAlexaBtnLongPressed!!) {
                        sceneObject?.isAlexaBtnLongPressed = false
                        toggleAVSViews(false)
                    }*/
                musicSceneObject.isAlexaBtnLongPressed = false
                toggleAVSViews(false)
            }
            false
        }

        musicPlayerWidget?.setOnClickListener {
            if (trackNameView?.text?.toString()?.contains(getString(R.string.app_name))!! || trackNameView?.text?.toString()?.contains(getString(R.string.login_to_enable_cmds))!!) {
                return@setOnClickListener
            }

            if (!musicSceneObject.trackName.isNullOrEmpty() || !musicSceneObject.album_art.isNullOrEmpty() || musicSceneObject.playstatus == SceneObject.CURRENTLY_PLAYING) {
                startActivity(Intent(this, CTNowPlayingActivity::class.java).apply {
                    putExtra(CURRENT_DEVICE_IP, musicPlayerIp)
                })
            }
        }
    }

    private fun toggleAVSViews(showListening: Boolean) {
        if (showListening) {
            listeningView?.visibility = View.VISIBLE
            playinLayout?.visibility = View.INVISIBLE
        } else {
            listeningView?.visibility = View.GONE
            playinLayout?.visibility = View.VISIBLE
        }
    }
    fun updateMusicPlayViews(sceneObject: SceneObject?, i: Int, deviceIP: String) {
        if(musicPlayerIp.equals(deviceIP)) {
            LibreLogger.d(TAG, "updateMusicPlayViews: $musicPlayerIp called from " +
                    "$i and musicPlayerIp $deviceIP")
            if (sceneObject == null) {
                return
            }
            try {
                val sceneObject = ScanningHandler.getInstance().getSceneObjectFromCentralRepo(this.musicPlayerIp)
                /*LibreLogger.d(TAG, "updateMusicPlayViews, Ip address: " + sceneObject.ipAddress + " called from $i")*/
                if (musicPlayerWidget?.id == R.id.fl_alexa_widget || AppUtils.isActivePlaylistNotAvailable(sceneObject)) {
                   // handleAlexaViews(sceneObject, 1, null)
                    return
                }
               /* musicPlayerWidget!!.visibility=View.VISIBLE
                shimmerView!!.visibility=View.INVISIBLE
                shimmerView!!.stopShimmer()*/
                   //   setCurrentTrackName(sceneObject, trackNameView)
               // setAlbumArtistName(sceneObject, albumNameView)


                /*if (!sceneObject.trackName.isNullOrEmpty() && !sceneObject.trackName.equals("NULL", ignoreCase = true)) {
                var trackname = sceneObject.trackName

                *//* This change is done to handle the case of deezer where the song name is appended by radio or skip enabled *//*
                if (trackname.contains(DEZER_RADIO)) trackname = trackname.replace(DEZER_RADIO, "")

                if (trackname.contains(DEZER_SONGSKIP)) trackname = trackname.replace(DEZER_SONGSKIP, "")

                trackNameView?.text = trackname
                trackNameView?.isSelected = true

                if (!sceneObject.album_name.isNullOrEmpty() && !sceneObject.album_name.equals("null", ignoreCase = true)) {
                    albumNameView?.text = sceneObject.album_name
                    albumNameView?.isSelected = true

                } else if (!sceneObject.artist_name.isNullOrEmpty() && !sceneObject.artist_name.equals("null", ignoreCase = true)) {
                    albumNameView?.text = sceneObject.artist_name
                    albumNameView?.isSelected = true
                }
            } else {
                LibreLogger.d(TAG, "updateMusicPlayViews else")
                handleAlexaViews(sceneObject)
            }*/

                /*Handling album art*/
                /*this is to show loading dialog while we are preparing to play*/
//                if (sceneObject.currentSource != AUX_SOURCE/*&& sceneObject!!.currentSource != EXTERNAL_SOURCE*/ && sceneObject.currentSource != BT_SOURCE /*&& sceneObject.currentSource != GCAST_SOURCE*/) {
//
//                    /*Album Art For All other Sources Except */
//                    if (!sceneObject.album_art.isNullOrEmpty() && sceneObject.album_art.equals("coverart.jpg", ignoreCase = true)) {
//                        val albumUrl = "http://" + sceneObject.ipAddress + "/" + "coverart.jpg"/* If Track Name is Different just Invalidate the Path And if we are resuming the Screen(Screen OFF and Screen ON) , it will not re-download it */
//
//                        if (sceneObject.trackName != null && !currentTrackName.equals(sceneObject.trackName, ignoreCase = true)) {
//                            if (sceneObject == null) {
//                                return
//                            }
//                            // currentTrackName = sceneObject.trackName!!
////                        if (sceneObject.album_name != null) {
////                            currentAlbumnName = sceneObject.album_name!!
////                        }
////                        currentArtistName = sceneObject.artist_name!!
//                            LibreLogger.d("==Albumart==", " 1 ${sceneObject.currentSource} and ip ${sceneObject.ipAddress}")
//                            AlbumArtURL = sceneObject.album_art!!
//
//                            val mInvalidated = mInvalidateTheAlbumArt(sceneObject, albumUrl)
//                            LibreLogger.d(TAG, "Invalidated the URL $albumUrl Status $mInvalidated")
//                        }
//
//                        if (albumArtView != null) {
//                            LibreLogger.d("==Albumart==", " 2 ${sceneObject.currentSource} and ip " + "${sceneObject.ipAddress}")
//                            LibreLogger.d("==Albumart==333", "  ${Glide.with(this).load(albumUrl).isMemoryCacheable}")
//                            Glide.with(this)
//                                .load(sceneObject.album_art)
//                                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                .centerCrop()
//                                .placeholder(R.mipmap.album_art)
//                                .into(albumArtView!!)
//
//                        }
//                    } else {
//                        when {
//                            !sceneObject.album_art.isNullOrEmpty() && !sceneObject.album_art.equals("null", ignoreCase = true) -> {
//                                if (sceneObject.trackName != null && !currentTrackName.equals(sceneObject.trackName, ignoreCase = true)) {
//                                    if (sceneObject == null) {
//                                        return
//                                    }
//                                    // currentTrackName = sceneObject.trackName!!
//                                    /*if (sceneObject.album_name != null) {
//                                    currentAlbumnName = sceneObject.album_name!!
//                                }*/
//                                    // currentArtistName = sceneObject.artist_name!!
//
//                                    val mInvalidated = mInvalidateTheAlbumArt(sceneObject, sceneObject.album_art)
//                                    LibreLogger.d("==Albumart==", " 3 ${sceneObject.currentSource} and" + " ip ${sceneObject.ipAddress}")
//                                    AlbumArtURL = sceneObject.album_art!!
//                                    LibreLogger.d(TAG, "Invalidated the URL $sceneObject.album_art " + "Status: $mInvalidated")
//
//                                }
//
//                                if (albumArtView != null) {
//                                    LibreLogger.d("==Albumart==", " 4 ${sceneObject.currentSource} and" + " ip ${sceneObject.ipAddress}")
//                                    albumArtView!!.visibility = View.VISIBLE
//                                    Glide.with(this)
//                                        .load(sceneObject.album_art)
//                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                        .centerCrop()
//                                        .placeholder(R.mipmap.album_art)
//                                        .into(albumArtView!!)
//                                }
//                            }
//
//                            else -> {
//                                LibreLogger.d("==Albumart==", " 5 ${sceneObject.currentSource} and ip " + "${sceneObject.ipAddress}")
//                                albumArtView?.setImageResource(R.mipmap.album_art)
//                            }
//                        }
//                    }
//
//                } else {
//                    LibreLogger.d("==Albumart==", " 5.5 ${sceneObject.currentSource} and ip ${sceneObject.ipAddress}")
//                }


                if (currentSourceView != null) {
                    currentSourceView?.visibility = View.GONE
                    //setCurrentSourceIcon(currentSourceView!!, sceneObject)
                } else {
                    LibreLogger.d(TAG, "currentSourceView is null")
                }

                when (sceneObject.currentSource) {
                    NO_SOURCE, DDMSSLAVE_SOURCE -> {
                        LibreLogger.d(TAG, "SHAIK playPauseView 6 ${sceneObject.currentSource}")
                        playPauseView?.isClickable = false
                        if (sceneObject.currentSource == NO_SOURCE) {
                           // handleAlexaViews(sceneObject, 2, null)
                        }
                    }

                    VTUNER_SOURCE, TUNEIN_SOURCE -> {
                        LibreLogger.d(TAG, "SHAIK playPauseView 8 ${sceneObject.currentSource}")
                        playPauseView?.setImageResource(R.drawable.play_white)
                        songSeekBar?.visibility = View.VISIBLE
                        songSeekBar?.progress = 0
                        songSeekBar?.isEnabled = false
                    }/*For Riva Tunneling, When switched to Aux, its External Source*/
                    AUX_SOURCE/*, EXTERNAL_SOURCE */ -> {
                       // handleAlexaViews(sceneObject, 3, null)
                    }

                    BT_SOURCE -> {
                        //handleAlexaViews(sceneObject, 4, null)
                    }

                    GCAST_SOURCE -> {
                        trackNameView?.text = sceneObject.trackName
                        songSeekBar?.progress = 0
                        songSeekBar?.isEnabled = false
                    }

                    ALEXA_SOURCE -> {
                        albumNameView?.text = sceneObject.artist_name
                        currentSourceView?.visibility = View.VISIBLE
                        when {
                            sceneObject.playUrl?.contains("Spotify", true)!! -> currentSourceView?.setImageResource(R.mipmap.spotify)
                            sceneObject.playUrl?.contains("Deezer", true)!! -> currentSourceView?.setImageResource(R.mipmap.riva_deezer_icon)
                            sceneObject.playUrl?.contains("Pandora", true)!! -> currentSourceView?.setImageResource(R.mipmap.riva_pandora_icon)
                            sceneObject.playUrl?.contains("SiriusXM", true)!! -> currentSourceView?.setImageResource(R.drawable.small_sirius_logo)
                            sceneObject.playUrl?.contains("TuneIn", true)!! -> currentSourceView?.setImageResource(R.drawable.smal_tunein_logo)
                            sceneObject.playUrl?.contains("iHeartRadio", true)!! -> currentSourceView?.setImageResource(R.drawable.second_iheartradio_image2)
                            sceneObject.playUrl?.contains("Amazon Music", true)!! -> currentSourceView?.setImageResource(R.drawable.amazon_music_small)
                            else -> currentSourceView?.visibility = View.GONE
                        }
                    }

                    DMR_SOURCE, DMP_SOURCE -> {
                        if (sceneObject.currentSource == SPOTIFY_SOURCE) {
                            if (currentArtistName != null) {
                                if (currentArtistName != "-1") {
                                    albumNameView?.text = currentArtistName
                                    trackNameView?.isSelected = true
                                }
                            }
                            if (currentAlbumnName != null) {
                                if (!currentAlbumnName.isEmpty()) {
                                    LibreLogger.d("==Albumart==", " 6 ${sceneObject.currentSource} and" + " ip ${sceneObject.ipAddress}")
                                    albumArtView?.visibility = View.VISIBLE

                                    if (albumArtView != null) {
                                        LibreLogger.d("==Albumart==", " 6 ${sceneObject.currentSource}" + " and ip ${sceneObject.ipAddress}")
                                        if (AlbumArtURL != null) {
                                            if (!AlbumArtURL.isEmpty()) {
                                                LibreLogger.d("==Albumart==", " 7 ${
                                                    sceneObject.currentSource
                                                } and ip ${sceneObject.ipAddress}")
                                                PicassoTrustCertificates.getInstance(this).load(AlbumArtURL)
                                                    //.memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE)
                                                    .placeholder(R.mipmap.album_art).error(R.mipmap.album_art).into(albumArtView)
                                            }
                                        } else {
                                            LibreLogger.d("==Albumart==", " 8 ${
                                                sceneObject.currentSource
                                            } and ip ${sceneObject.ipAddress}")
                                        }
                                    }
                                } else {
                                    albumArtView?.visibility = View.GONE
                                    LibreLogger.d("==Albumart==", " 9 ${sceneObject.currentSource} and" + " ip ${sceneObject.ipAddress}")
                                }
                            }
                        }

                    }

                    USB_SOURCE -> {
                        if (sceneObject != null) {
                            if (!sceneObject.trackName.isNullOrEmpty() && !sceneObject.trackName.equals("null", ignoreCase = true)) {
                                LibreLogger.d(TAG, "SHAIK playPauseView 10 ${sceneObject.currentSource}")
                                playPauseView?.visibility = View.VISIBLE
                                trackNameView?.visibility = View.VISIBLE
                                if (sceneObject.album_name != null) {
                                    if ((!sceneObject.album_name.isEmpty() && !sceneObject.album_name.equals("null", ignoreCase = true)) || (!sceneObject.artist_name.isEmpty() && !sceneObject.artist_name.equals("null", ignoreCase = true))) {
                                        albumNameView?.visibility = View.VISIBLE
                                    }
                                }
                                if (!sceneObject.album_art.isEmpty() && !sceneObject.album_art.equals("null", ignoreCase = true)) {
                                    LibreLogger.d("==Albumart==", " 10 ${sceneObject.currentSource} " + "and ip ${sceneObject.ipAddress}")
                                    albumArtView?.visibility = View.VISIBLE
                                }

                                if (sceneObject.totalTimeOfTheTrack > 0 && sceneObject.currentPlaybackSeekPosition >= 0) {
                                    songSeekBar?.visibility = View.VISIBLE
                                    songSeekBar?.isEnabled = true
                                }
                            }
                        } else {
                            //handleAlexaViews(sceneObject,5)
                        }
                    }
                    //else -> handleAlexaViews(sceneObject,6)
                }
                if (sceneObject.currentSource == VTUNER_SOURCE || sceneObject.currentSource == TUNEIN_SOURCE || sceneObject.currentSource == BT_SOURCE || sceneObject.currentSource == AUX_SOURCE/*|| sceneObject.currentSource == EXTERNAL_SOURCE*/ || sceneObject.currentSource == NO_SOURCE || sceneObject.currentSource == GCAST_SOURCE) {
                    songSeekBar?.progress = 0
                    songSeekBar?.secondaryProgress = 0
                    songSeekBar?.max = 100
                    songSeekBar?.isEnabled = false
                }

               /*
                LibreLogger.d(TAG, "playStatus : " + sceneObject.playstatus)
                if (playPauseView != null) {
                    when (sceneObject.playstatus) {
                        CURRENTLY_BUFFERING -> {
                            LibreLogger.d(TAG, "SHAIK playPauseView 11 ${sceneObject.currentSource}")
                            setPlayPauseLoader(playPauseView!!, isEnabled = false, isLoader = true, image = 0)
                        }

                        CURRENTLY_PLAYING -> {
                            if (playPauseNextPrevAllowed(sceneObject)) {
                                LibreLogger.d(TAG, "SHAIK playPauseView 12 ${sceneObject.currentSource}")
                                setPlayPauseLoader(playPauseView!!, isEnabled = true, isLoader = false, image = R.drawable.pause_orange)
                            } else {
                                LibreLogger.d(TAG, "SHAIK playPauseView 13 ${sceneObject.currentSource}")
                                if (sceneObject.currentSource != EXTERNAL_SOURCE) {
                                    setPlayPauseLoader(playPauseView!!, isEnabled = false, isLoader = false, image = R.drawable.play_white)
                                } else {
                                    setPlayPauseLoader(playPauseView!!, isEnabled = false, isLoader = false, image = 0)
                                }
                            }
                        }

                        CURRENTLY_STOPPED, CURRENTLY_PAUSED -> {
                            if (playPauseNextPrevAllowed(sceneObject)) {
                                LibreLogger.d(TAG, "SHAIK playPauseView 14 ${sceneObject.currentSource}")
                                setPlayPauseLoader(playPauseView!!, isEnabled = true, isLoader = false, image = R.drawable.play_orange)
                            } else {
                                LibreLogger.d(TAG, "SHAIK playPauseView 15 ${sceneObject.currentSource}")
                                setPlayPauseLoader(playPauseView!!, isEnabled = false, isLoader = false, image = R.drawable.play_white)
                            }
                        }

                        else -> {
                            LibreLogger.d(TAG, "SHAIK playPauseView 4 ${sceneObject.currentSource}")
                            setPlayPauseLoader(playPauseView!!, isEnabled = false, isLoader = false, image = 0)

                        }

                    }
                } else {
                    LibreLogger.d(TAG, "updateMusicPlayViews playPauseView is null")
                }
                if (sceneObject.currentSource == VTUNER_SOURCE || sceneObject.currentSource == TUNEIN_SOURCE *//*|| sceneObject.currentSource == BT_SOURCE*//* || sceneObject.currentSource == AUX_SOURCE || sceneObject.currentSource == NO_SOURCE) {
                    LibreLogger.d(TAG, "SHAIK playPauseView 1 ${sceneObject.currentSource}")
                    setPlayPauseLoader(playPauseView!!, isEnabled = false, isLoader = false, image = R.drawable.play_white)
                }*/

                /* Setting the current seekbar progress -Start*/
               /* val duration = sceneObject.currentPlaybackSeekPosition
                songSeekBar?.max = sceneObject.totalTimeOfTheTrack.toInt() / 1000
                songSeekBar?.secondaryProgress = sceneObject.totalTimeOfTheTrack.toInt() / 1000
                LibreLogger.d(TAG, "seek_bar_song Duration = " + duration / 1000)
                songSeekBar?.progress = duration.toInt() / 1000*/

                toggleAlexaBtnForSAMode()

                if (AppUtils.isActivePlaylistNotAvailable(sceneObject)) {
                    LibreLogger.d(TAG, "isActivePlaylistNotAvailable true")
                  //  handleAlexaViews(sceneObject, 7, null)
                }

                if (AppUtils.isDMRPlayingFromOtherPhone(sceneObject)) {
                    LibreLogger.d(TAG, "SHAIK currentPlayPauseIcon 2 ${sceneObject.currentSource}")
                    playPauseView?.visibility = View.GONE
                }

                if (AppUtils.isLocalDMRPlaying(sceneObject)) {
                    LibreLogger.d(TAG, "SHAIK currentPlayPauseIcon 3 ${sceneObject.currentSource}")
                    playPauseView?.visibility = View.VISIBLE
                }
            } catch (e: KotlinNullPointerException) {
                e.printStackTrace()
                LibreLogger.d(TAG, "updateMusicPlayViews  KotlinNullPointerException ${e.message}")
            }
        }
    }

    fun setCurrentAlbumArt(currentSceneObject: SceneObject, currentIvAlbumArt: AppCompatImageView) {
        LibreLogger.d("==Albumart==", " 2 ${currentSceneObject.currentSource} and ip " + "${currentSceneObject.ipAddress}")
        if (currentSceneObject.currentSource != AUX_SOURCE
            && currentSceneObject.currentSource != EXTERNAL_SOURCE
            && currentSceneObject.currentSource != NO_SOURCE
            && currentSceneObject.currentSource != BT_SOURCE) {
            currentIvAlbumArt.visibility = View.VISIBLE
            /*Album Art For All other Sources Except */
            if (!currentSceneObject.album_art.isNullOrEmpty() && currentSceneObject.album_art.equals("coverart.jpg", ignoreCase = true)) {
                val albumUrl = "http://" + currentSceneObject.ipAddress + "/" + "coverart.jpg"
                /**
                 * Commented Because we don't required this logic as we using GLide and
                 * Cross checked with screen on and Off and song change also
                 */
                /* If Track Name is Different just Invalidate the Path And if we are resuming the Screen(Screen OFF and Screen ON) , it will not re-download it */

                /*  if (currentSceneObject.trackName != null && !currentTrackName.equals(currentSceneObject.trackName, ignoreCase = true)) {
                      if (currentSceneObject == null) {
                          return
                      }
                      // currentTrackName = sceneObject.trackName!!
  //                        if (sceneObject.album_name != null) {
  //                            currentAlbumnName = sceneObject.album_name!!
  //                        }
  //                        currentArtistName = sceneObject.artist_name!!
                      LibreLogger.d("==Albumart==", " 1 ${currentSceneObject.currentSource} and ip ${currentSceneObject.ipAddress}")
                      AlbumArtURL = currentSceneObject.album_art!!

                      val mInvalidated = mInvalidateTheAlbumArt(currentSceneObject, albumUrl)
                      LibreLogger.d(TAG, "Invalidated the URL $albumUrl Status $mInvalidated")
                  }*/
                val mInvalidated = mInvalidateTheAlbumArt(currentSceneObject, albumUrl)
                LibreLogger.d(TAG, "Invalidated the URL $albumUrl Status $mInvalidated")
                LibreLogger.d("==Albumart==333", "  $albumUrl")
                Glide.with(this).load(albumUrl).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop()
                    .placeholder(R.mipmap.album_art).into(currentIvAlbumArt)

            } else {
                when {
                    !currentSceneObject.album_art.isNullOrEmpty() && !currentSceneObject.album_art.equals("null", ignoreCase = true) -> {
                        /*  if (currentSceneObject.trackName != null && !currentTrackName.equals(currentSceneObject.trackName, ignoreCase = true)) {
                            if (currentSceneObject == null) {
                                return
                            }
                            // currentTrackName = sceneObject.trackName!!
                            *//*if (sceneObject.album_name != null) {
                            currentAlbumnName = sceneObject.album_name!!
                        }*//*
                            // currentArtistName = sceneObject.artist_name!!

                            val mInvalidated = mInvalidateTheAlbumArt(currentSceneObject, currentSceneObject.album_art)
                            LibreLogger.d("==Albumart==", " 3 ${currentSceneObject.currentSource} and" + " ip ${currentSceneObject.ipAddress}")
                            AlbumArtURL = currentSceneObject.album_art!!
                            LibreLogger.d(TAG, "Invalidated the URL $currentSceneObject.album_art " + "Status: $mInvalidated")


                        }*/
                        LibreLogger.d("==Albumart==", " 4 ${currentSceneObject.currentSource} and" + " ip ${currentSceneObject.ipAddress}")

                        Glide.with(this).load(currentSceneObject.album_art).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().placeholder(R.mipmap.album_art).into(currentIvAlbumArt)


                    }

                    else -> {
                        LibreLogger.d("==Albumart==", " 5 ${currentSceneObject.currentSource} and ip " + "${currentSceneObject.ipAddress}")
                        currentIvAlbumArt.setImageResource(R.mipmap.album_art)
                    }
                }
            }

        } else  {
            LibreLogger.d("==Albumart==", " 5.5 ${currentSceneObject.currentSource} and ip ${currentSceneObject.ipAddress}")
        }
    }

    fun setCurrentPlaybackSeekPosition(currentSceneObject: SceneObject, currentSeekBarSong: AppCompatSeekBar) {
        try {
            val duration = currentSceneObject.currentPlaybackSeekPosition
            currentSeekBarSong.max = currentSceneObject.totalTimeOfTheTrack.toInt() / 1000
            currentSeekBarSong.secondaryProgress = currentSceneObject.totalTimeOfTheTrack.toInt() / 1000
            LibreLogger.d(TAG, "seek_bar_song Duration = " + duration / 1000)
            currentSeekBarSong.progress = duration.toInt() / 1000
        }catch (ex:Exception){
            LibreLogger.d(TAG, "setCurrentPlaybackSeekPosition Exception = " + ex.message)
        }
    }

    fun setCurrentTrackName(currentSceneObject: SceneObject, trackNameView: AppCompatTextView?) {
        LibreLogger.d(TAG,"setCurrentTrackName trackName:- ${currentSceneObject.trackName}  and sourceName:- ${currentSceneObject.currentSource} ")
        if(currentSceneObject.currentSource!= NO_SOURCE ||currentSceneObject.currentSource!= AUX_SOURCE ) {
            if (!currentSceneObject.trackName.isNullOrEmpty() && !currentSceneObject.trackName.equals("NULL", ignoreCase = true)) {
                var trackname = currentSceneObject.trackName

                /* This change is done to handle the case of deezer where the song name is appended by radio or skip enabled */
                if (trackname.contains(DEZER_RADIO)) trackname = trackname.replace(DEZER_RADIO, "")

                if (trackname.contains(DEZER_SONGSKIP)) trackname = trackname.replace(DEZER_SONGSKIP, "")
                trackname = trackname.replace(DEZER_SONGSKIP, "")
                if (trackNameView?.text.toString() != trackname) {
                    trackNameView?.text = trackname
                }
                trackNameView!!.post {
                    trackNameView.isSelected = true
                }
            } else {
                if (currentSceneObject.currentSource == AUX_SOURCE
                    || currentSceneObject.currentSource == EXTERNAL_SOURCE
                    || currentSceneObject.tunnelingCurrentSource == AUX_SOURCE
                    || currentSceneObject.tunnelingCurrentSource == EXTERNAL_SOURCE) {
                    trackNameView!!.setText(R.string.aux_playing)
                } else {
                    if (currentSceneObject.currentSource == NO_SOURCE) {
                       // handleAlexaViews(currentSceneObject, 17)
                    } else {
                        //Setting the not available because to debug the issue
                        trackNameView?.setText(R.string.trackname_not_available)
                    }
                }
            }
        } else{
          //  handleAlexaViews(currentSceneObject, 8, null)
        }

    }
    fun setCurrentAlbumArtistName(currentSceneObject: SceneObject, currentAlbumName: AppCompatTextView) {
        LibreLogger.d(TAG, "setAlbumArtistName :- ${currentSceneObject.album_name}  and sourceName:- ${currentSceneObject.artist_name} ")
        if (currentSceneObject.currentSource != NO_SOURCE || currentSceneObject.currentSource != AUX_SOURCE) {
            if (!currentSceneObject.album_name.isNullOrEmpty() && !currentSceneObject.album_name?.equals("NULL", ignoreCase = true)!! && !currentSceneObject.album_name?.equals(currentAlbumName.text?.toString())!!) {
                currentAlbumName.text = currentSceneObject.album_name

            }

            if (!currentSceneObject.artist_name.isNullOrEmpty() && !currentSceneObject.artist_name?.equals("NULL", ignoreCase = true)!!) {
                currentAlbumName.text = "${currentAlbumName.text}, ${currentSceneObject.artist_name}"
            }
            currentAlbumName.post {
                currentAlbumName.isSelected = true
            }
        } else {
            if (currentSceneObject.currentSource == AUX_SOURCE
                || currentSceneObject.currentSource == EXTERNAL_SOURCE
                || currentSceneObject.currentSource == NO_SOURCE
                || currentSceneObject.tunnelingCurrentSource == AUX_SOURCE
                || currentSceneObject.tunnelingCurrentSource == EXTERNAL_SOURCE) {
                /**
                 * When Aux is playing no need show the Album Name because we already
                 * showing the trackName as "Aux is playing"
                 */
                //currentAlbumName.setText(R.string.aux_playing)
            } else {
                //Setting the not available because to debug the issue
                currentAlbumName.setText(R.string.albumname_not_available)
            }
        }

    }

     fun setCurrentSourceIcon(currentSceneObject: SceneObject,
         currentSourceView: AppCompatImageView) {
         LibreLogger.d("-->SHAIK", "setCurrentSourceIcon  ${currentSceneObject.currentSource} " +
                 "visibilty ${currentSourceView.visibility == View.GONE} ")

            when (currentSceneObject.currentSource) {
                NO_SOURCE -> currentSourceView.visibility = View.GONE
                AIRPLAY_SOURCE -> currentSourceView.setImageResource(R.drawable.airplay_no_bg)
                DMR_SOURCE -> currentSourceView.setImageResource(R.drawable.my_device_enabled)
                DMP_SOURCE -> currentSourceView.setImageResource(R.drawable.media_servers_enabled)
                SPOTIFY_SOURCE -> currentSourceView.setImageResource(R.mipmap.spotify)
                USB_SOURCE -> currentSourceView.setImageResource(R.drawable.usb_storage_enabled)
                SDCARD_SOURCE -> currentSourceView.setImageResource(R.mipmap.sdcard)
                MELON_SOURCE -> currentSourceView.visibility = View.GONE
                VTUNER_SOURCE -> currentSourceView.setImageResource(R.mipmap.vtuner_logo)
                TUNEIN_SOURCE -> currentSourceView.setImageResource(R.mipmap.tunein_logo1)
                MIRACAST_SOURCE -> currentSourceView.visibility = View.GONE
                DDMSSLAVE_SOURCE ->currentSourceView.visibility = View.GONE
                AUX_SOURCE -> currentSourceView.setImageResource(R.drawable.ic_aux_in)
                //Dummy Image for now
                APPLEDEVICE_SOURCE -> currentSourceView.setImageResource(R.drawable.airplay_logo)
                DIRECTURL_SOURCE -> currentSourceView.visibility = View.GONE
                BT_SOURCE -> currentSourceView.setImageResource(R.drawable.bluetooth_icon)
                DEEZER_SOURCE -> currentSourceView.setImageResource(R.mipmap.deezer_logo)
                TIDAL_SOURCE -> currentSourceView.setImageResource(R.mipmap.tidal_white_logo)
                FAVOURITES_SOURCE -> currentSourceView.setImageResource(R.mipmap.ic_remote_favorite)
                GCAST_SOURCE -> currentSourceView.setImageResource(R.mipmap.ic_cast_white_24dp_2x)
                ROON -> currentSourceView.setImageResource(R.drawable.roon)
                EXTERNAL_SOURCE ->currentSourceView.setImageResource(R.drawable.ic_aux_in)
                OPTICAL_SOURCE -> currentSourceView.visibility = View.GONE
                TUNNELING_WIFI_SOURCE -> currentSourceView.visibility = View.GONE
            }


    }
    fun setCurrentPlayPauseIcon(currentSceneObject: SceneObject,
        currentPlayPauseIcon: ProgressButtonImageView,
        layData: LinearLayout,
        layShimmer: ShimmerFrameLayout) {

        LibreLogger.d("==>SHAIK", "PlayPauseStatus: " + currentSceneObject.playstatus)
        when (currentSceneObject.playstatus) {
            CURRENTLY_BUFFERING -> {
                //Start Shimmer Effect for loading
                layData.visibility=View.INVISIBLE
                layShimmer.startShimmer()

                LibreLogger.d(TAG, "SHAIK currentPlayPauseIcon 11 ${currentSceneObject.currentSource}")
                setPlayPauseLoader(currentPlayPauseIcon, isEnabled = false, isLoader = true, image = 0)
            }
            CURRENTLY_PLAYING -> {
                //Stop Shimmer Effect for loading
                layShimmer.stopShimmer()
                layData.visibility = View.VISIBLE

                if (playPauseNextPrevAllowed(currentSceneObject)) {
                    LibreLogger.d(TAG, "SHAIK currentPlayPauseIcon 12 ${currentSceneObject.currentSource}")
                    setPlayPauseLoader(currentPlayPauseIcon, isEnabled = true, isLoader = false, image = R.drawable.pause_orange)
                } else {
                    LibreLogger.d(TAG, "SHAIK currentPlayPauseIcon 13 ${currentSceneObject.currentSource}")
                    if (currentSceneObject.currentSource != EXTERNAL_SOURCE) {
                        setPlayPauseLoader(currentPlayPauseIcon, isEnabled = false, isLoader = false, image = R.drawable.play_white)
                    } else {
                        setPlayPauseLoader(currentPlayPauseIcon, isEnabled = false, isLoader = false, image = 0)
                    }
                }
            }

            CURRENTLY_STOPPED, CURRENTLY_PAUSED -> {
                if (playPauseNextPrevAllowed(currentSceneObject)) {
                    LibreLogger.d(TAG, "SHAIK currentPlayPauseIcon 14 ${currentSceneObject.currentSource}")
                    setPlayPauseLoader(currentPlayPauseIcon, isEnabled = true, isLoader = false, image = R.drawable.play_orange)
                } else {
                    LibreLogger.d(TAG, "SHAIK currentPlayPauseIcon 15 ${currentSceneObject.currentSource}")
                    if(currentSceneObject.currentSource== EXTERNAL_SOURCE){
                        setPlayPauseLoader(currentPlayPauseIcon, isEnabled = false, isLoader = false, image = 0)
                    }else {
                        setPlayPauseLoader(currentPlayPauseIcon, isEnabled = false, isLoader = false, image = R.drawable.play_white)
                    }
                }
            }else -> {
                LibreLogger.d(TAG, "SHAIK currentPlayPauseIcon 4 ${currentSceneObject.currentSource}")
                setPlayPauseLoader(currentPlayPauseIcon, isEnabled = false, isLoader = false, image = 0)

            }

        }
        if (currentSceneObject.currentSource == VTUNER_SOURCE || currentSceneObject.currentSource == TUNEIN_SOURCE /*|| sceneObject.currentSource == BT_SOURCE*/ || currentSceneObject.currentSource == AUX_SOURCE || currentSceneObject.currentSource == NO_SOURCE) {
            LibreLogger.d(TAG, "SHAIK currentPlayPauseIcon 1 ${currentSceneObject.currentSource}")
            /**
             * For No source no need image so
             */
            setPlayPauseLoader(currentPlayPauseIcon, isEnabled = false, isLoader = false, image = 0)
            /*setPlayPauseLoader(currentPlayPauseIcon, isEnabled = false, isLoader = false, image = R.drawable.play_white)*/
        }

    }

    private fun playPauseNextPrevAllowed(currentSceneObject: SceneObject?): Boolean {
        val mNodeWeGotForControl = ScanningHandler.getInstance().getLSSDPNodeFromCentralDB(currentSceneObject?.ipAddress)
        return (currentSceneObject!!.currentSource != AUX_SOURCE
                && currentSceneObject.currentSource != EXTERNAL_SOURCE
             /*   && currentSceneObject.currentSource != GCAST_SOURCE*/ //Not Availble for cast
                && currentSceneObject.currentSource != VTUNER_SOURCE
                && currentSceneObject.currentSource != TUNEIN_SOURCE
                && currentSceneObject.currentSource != NO_SOURCE
               /* && (currentSceneObject.currentSource != BT_SOURCE*/
                || (mNodeWeGotForControl.getgCastVerision() != null
                && mNodeWeGotForControl.bT_CONTROLLER != CURRENTLY_NOTPLAYING
                && mNodeWeGotForControl.bT_CONTROLLER != CURRENTLY_PLAYING)
                || (mNodeWeGotForControl.getgCastVerision() == null
                && mNodeWeGotForControl.bT_CONTROLLER >= CURRENTLY_PAUSED))
    }

    fun handleAlexaViews(sceneObject: SceneObject, i: Int, ilMusicPlayingWidget: MusicPlayingWidgetBinding) {
        LibreLogger.d(TAG, "==>SHAIK setCurrentTrackName handleAlexaViews  and ${AppUtils
            .isActivePlaylistNotAvailable(sceneObject)} and coming from $i and ${sceneObject.currentSource}")
        if (AppUtils.isActivePlaylistNotAvailable(sceneObject)) {

            ilMusicPlayingWidget.seekBarSong.visibility = View.VISIBLE
            LibreLogger.d(TAG, "SHAIk ivPlayPause 10 ${sceneObject.ipAddress}")
            ilMusicPlayingWidget.ivPlayPause.visibility = View.GONE
            ilMusicPlayingWidget.tvTrackName.text = getText(R.string.title_libre_caps)

            /**
             * Commented by Shiak Because Shiva/RK told to change it for now, once requirement come
             * we can enable it ->https://jira-librewireless.atlassian.net/browse/LCB2-970
             * https://jira-librewireless.atlassian.net/browse/LCB2-969
             */
          /*  val node = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(sceneObject?.ipAddress)
            if (node.getmDeviceCap().getmSource().isAlexaAvsSource) {
                if (node?.alexaRefreshToken.isNullOrEmpty()) {
                    ilMusicPlayingWidget.tvTrackName.visibility = View.VISIBLE
                    ilMusicPlayingWidget.tvTrackName.text = getText(R.string.login_to_enable_cmds)
                    ilMusicPlayingWidget.tvAlbumName.visibility = View.GONE
                } else {
                    LibreLogger.d(TAG, "SHAIk ivPlayPause 22 ${sceneObject.trackName}")
                    ilMusicPlayingWidget.tvTrackName.visibility = View.VISIBLE
                    ilMusicPlayingWidget.tvTrackName.text = getText(R.string.app_name)
                    ilMusicPlayingWidget.tvAlbumName.visibility = View.VISIBLE
                    ilMusicPlayingWidget.tvAlbumName.text = getText(R.string.speaker_ready_for_cmds)
                    ilMusicPlayingWidget.ivAlbumArt.visibility = View.GONE

                }
            } else {
                LibreLogger.d(TAG, "SHAIk ivPlayPause 23 ${sceneObject.trackName}")
                ilMusicPlayingWidget.tvTrackName.visibility = View.VISIBLE
                ilMusicPlayingWidget.tvTrackName.text = getText(R.string.app_name)
                ilMusicPlayingWidget.tvAlbumName.visibility = View.VISIBLE
                ilMusicPlayingWidget.tvAlbumName.text = getText(R.string.speaker_ready_for_use)
            }*/

        }

//        songSeekBar?.visibility = View.VISIBLE
//        LibreLogger.d(TAG, "handleAlexaViews " + sceneObject.currentSource)
//        val node = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(sceneObject.ipAddress)
//        if (currentAlbumnName != null) {
//            if (!currentAlbumnName.isEmpty()) {
//                albumArtView?.visibility = View.VISIBLE
//            } else {
//                albumArtView?.visibility = View.GONE
//            }
//        }
//        if (sceneObject.playstatus == CURRENTLY_STOPPED || node?.alexaRefreshToken == "" || node?.alexaRefreshToken == null) {
//            if (sceneObject.currentSource == SPOTIFY_SOURCE || sceneObject.currentSource == DMR_SOURCE || sceneObject.currentSource == DMP_SOURCE || sceneObject.currentSource == USB_SOURCE || sceneObject.currentSource == ALEXA_SOURCE) {
//                albumNameView?.visibility = View.VISIBLE
//                trackNameView?.visibility = View.VISIBLE
//                trackNameView?.text = LibreApplication.currentTrackName
//                playPauseView?.visibility = View.VISIBLE
//                albumArtView?.visibility = View.VISIBLE
//                LibreLogger.d(TAG, "suma in handle alexaplay icon currently stopped1" + sceneObject.playstatus)
//                if (node?.alexaRefreshToken.isNullOrEmpty()) {
//                    trackNameView?.text = getText(R.string.login_to_enable_cmds)
//
//                } else {
//                    trackNameView?.text = getText(R.string.speaker_ready_for_cmds)
//                }
//            } else {
//                trackNameView?.text = getText(R.string.app_name)
//                playPauseView?.visibility = View.INVISIBLE
//                albumArtView?.visibility = View.GONE
//                LibreLogger.d(TAG, "suma in handle alexaplay icon currently stopped12" + sceneObject.playstatus)
//                if(node.getmDeviceCap().getmSource()!=null)
//                if (node.getmDeviceCap().getmSource().isAlexaAvsSource) {
//                    if (node?.alexaRefreshToken.isNullOrEmpty()) {
//                        trackNameView?.text = getText(R.string.login_to_enable_cmds)
//                    } else {
//                        trackNameView?.text = getText(R.string.app_name)
//                        albumNameView?.text = getText(R.string.speaker_ready_for_cmds)
//                       // trackNameView?.text = getText(R.string.speaker_ready_for_cmds)
//                    }
//                } else {
//                    trackNameView?.visibility = View.VISIBLE
//                    trackNameView?.text = getText(R.string.app_name)
//                    albumNameView?.visibility = View.VISIBLE
//                    albumNameView?.text = getText(R.string.speaker_ready_for_use)
//                }
//            }
//
//
//        } else {
//            if (sceneObject.currentSource == AUX_SOURCE || sceneObject.currentSource == EXTERNAL_SOURCE || sceneObject.currentSource == NO_SOURCE) {
//                if (node.alexaRefreshToken.isNullOrEmpty()) {
//                    albumNameView?.visibility = View.GONE
//                    playPauseView?.visibility = View.GONE
//                    albumArtView?.visibility = View.GONE
//                    trackNameView?.text = getText(R.string.login_to_enable_cmds)
//                    LibreLogger.d(TAG, "suma in handle alexaplay icon BT  & Aux if refreshtoken" + sceneObject.currentSource)
//                } else {
//                    albumNameView?.visibility = View.GONE
//                    playPauseView?.visibility = View.GONE
//                    albumArtView?.visibility = View.GONE
//                    trackNameView?.text = getText(R.string.speaker_ready_for_cmds)
//                    LibreLogger.d(TAG, "suma in handle alexaplay icon BT  & Aux else refreshtoken" + sceneObject.currentSource)
//
//                }
//                LibreLogger.d(TAG, "suma in handle alexaplay icon BT  & Aux" + sceneObject.currentSource)
//
//            } else {
//                LibreLogger.d(TAG, "suma in handle LibreApplication.currentTrackName " +LibreApplication.currentTrackName)
//                if (LibreApplication.currentTrackName != null) {
//                    if (LibreApplication.currentTrackName.isNotEmpty()) {
//                        LibreLogger.d(TAG, "suma in handle sceneObject.trackName " +sceneObject.trackName)
//                        trackNameView?.text = sceneObject.trackName
//                        if (node.alexaRefreshToken.isNullOrEmpty()) {
//                            trackNameView?.text = getText(R.string.login_to_enable_cmds)
//                        } else {
//                            if(sceneObject.trackName!=null && !sceneObject.trackName.isNullOrEmpty()) {
//                                trackNameView?.text = sceneObject.trackName
//                            }else{
//                                trackNameView?.text = getText(R.string.speaker_ready_for_cmds)
//                            }
//                        }
//                    } else {
//                        if (node.alexaRefreshToken.isNullOrEmpty()) {
//                            trackNameView?.text = getText(R.string.login_to_enable_cmds)
//
//                        } else {
//                            trackNameView?.text = getText(R.string.speaker_ready_for_cmds)
//                        }
//                    }
//                }
//                if (currentArtistName != null) {
//                    if (!currentArtistName.isEmpty()) {
//                        albumNameView?.visibility = View.VISIBLE
//
//                    } else {
//                        albumNameView?.visibility = View.GONE
//
//                    }
//                }
//                songSeekBar?.visibility = View.VISIBLE
//
//                if (sceneObject.album_art != null) {
//                    albumArtView?.visibility = View.VISIBLE
//                    if (albumArtView != null) {
////                                   if (sceneObject != null) {
////                                       PicassoTrustCertificates.getInstance(this)
////                                               .load(sceneObject!!.album_art)
////                                               /*   .memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE)*/
////                                               .placeholder(R.mipmap.album_art)
////                                               .error(R.mipmap.album_art)
////                                               .into(albumArtView)
////                                   }
//                    }
//                } else {
//                    albumArtView?.visibility = View.GONE
//
//                }
//                LibreLogger.d(TAG, "suma in handle alexaplay icon BT  & Aux else" + sceneObject.currentSource)
//
//            }
//        }

    }

        private fun toggleAlexaBtnForSAMode() {
            val ssid = AppUtils.getConnectedSSID(this)
            alexaButton?.isEnabled = ssid != null && !isConnectedToSAMode(ssid)
            if (alexaButton?.isEnabled!!) {
                alexaButton?.alpha = 1f
            } else alexaButton?.alpha = .5f
        }

        fun mInvalidateTheAlbumArt(sceneObject: SceneObject, album_url: String): Boolean {
            if (!sceneObject.getmPreviousTrackName().equals(sceneObject.trackName, ignoreCase = true)) {
                PicassoTrustCertificates.getInstance(this).invalidate(album_url)
                sceneObject.setmPreviousTrackName(sceneObject.trackName)
                val sceneObjectFromCentralRepo = ScanningHandler.getInstance().getSceneObjectFromCentralRepo(sceneObject.ipAddress)
                sceneObjectFromCentralRepo?.setmPreviousTrackName(sceneObject.trackName)
                return true
            }
            return false
        }

        private fun gotoSourcesOption(ipaddress: String, currentSource: Int) {
            val mActiveScenesList = Intent(this, CTMediaSourcesActivity::class.java)
            mActiveScenesList.putExtra(CURRENT_DEVICE_IP, ipaddress)
            mActiveScenesList.putExtra(CURRENT_SOURCE, "" + currentSource)
            val error = LibreError("", resources.getString(R.string.no_active_playlist), 1)
            BusProvider.getInstance().post(error)
            startActivity(mActiveScenesList)
        }

        private fun parseMessageForMusicPlayer(nettyData: NettyData?) {
           // musicPlayerIp = nettyData?.remotedeviceIp
          /*  LibreLogger.d(TAG, "parseForMusicPlayer musicIp = $musicPlayerIp, nettyData Ip = " + "${nettyData?.remotedeviceIp}")*/
            if (musicPlayerIp == null || !musicPlayerIp?.equals(nettyData?.remotedeviceIp)!!) {
                LibreLogger.d(TAG, "parseForMusicPlayer returning musicPlayerIp $musicPlayerIp " + "remotedeviceIp ${nettyData?.remotedeviceIp}")
                return
            }

            if (musicPlayerWidget == null) {
                LibreLogger.d(TAG, "parseForMusicPlayer musicPlayerWidget null")
                return
            }
            LibreLogger.d(TAG, "parseForMusicPlayer After  luci packet $musicPlayerIp")
            val luciPacket = LUCIPacket(nettyData?.message)
            val msg = String(luciPacket.payload)

            val sceneObject = ScanningHandler.getInstance().getSceneObjectFromCentralRepo(nettyData?.remotedeviceIp)
            if (sceneObject == null) {
                LibreLogger.d(TAG, "MessageForMusicPlayer sceneObject null for " + nettyData?.remotedeviceIp)
                return
            }
            LibreLogger.d(TAG, "parseForMusicPlayer before When "+luciPacket.command)
            when (luciPacket.command) {
                MIDCONST.SET_UI -> {
                    try {
                        LibreLogger.d(TAG, "MB : 42, msg = $msg")
                        val root = JSONObject(msg)
                        val cmdId = root.getInt(LUCIMESSAGES.TAG_CMD_ID)
                        val window = root.getJSONObject(LUCIMESSAGES.ALEXA_KEY_WINDOW_CONTENT)
                        LibreLogger.d(TAG, "PLAY JSON is \n= $msg")
                        if (cmdId == 3) {
                            LibreLogger.d(TAG, "c" + sceneObject.trackName)
                            sceneObject.playUrl = window.getString("PlayUrl").lowercase(Locale.getDefault())
                            sceneObject.album_art = window.getString("CoverArtUrl")
                            sceneObject.artist_name = window.getString("Artist")
                            sceneObject.totalTimeOfTheTrack = window.getLong("TotalTime")
                            sceneObject.trackName = window.getString("TrackName")
                            sceneObject.album_name = window.getString("Album")

                           // updateMusicPlayViews(sceneObject,2,nettyData?.remotedeviceIp!!)

                            currentTrackName = sceneObject.trackName
                            currentArtistName = sceneObject.artist_name
                            currentAlbumnName = sceneObject.album_name
                            LibreLogger.d("==Albumart=="," 11 ${sceneObject.currentSource} and ip" + " ${sceneObject.ipAddress}")
                            AlbumArtURL = sceneObject.album_art
                            handlePlayJsonUi(window, sceneObject)
                            LibreLogger.d(TAG, "Handle Play Json UI" + sceneObject.trackName + "PLAY URL\n" + sceneObject.playUrl + "ARTIST NAME" + sceneObject.artist_name)
                           // handleAlexaViews(sceneObject,9)
                            //MID_STOP_PREV_SOURCE
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                MIDCONST.MID_MIC -> {
                    if (msg.contains("BLOCKED", true)) {
                        audioRecordUtil?.stopRecording()
                        showToast(getString(R.string.deviceMicOn))
                        sceneObject.isAlexaBtnLongPressed = false/*updating boolean status to repo as well*/
                        ScanningHandler.getInstance().putSceneObjectToCentralRepo(sceneObject.ipAddress, sceneObject)
                        toggleAVSViews(sceneObject.isAlexaBtnLongPressed)
                    }
                }

                MIDCONST.MID_CURRENT_PLAY_STATE.toInt() -> {
                    LibreLogger.d(TAG, "MB : 51, msg = $msg")
                    if (msg.isNotEmpty()) {
                        val playStatus = Integer.parseInt(msg)
                        if (sceneObject != null) {
                            sceneObject.playstatus = playStatus
                            ScanningHandler.getInstance().putSceneObjectToCentralRepo(nettyData?.getRemotedeviceIp(), sceneObject)
                            //updateMusicPlayViews(sceneObject,3,nettyData?.remotedeviceIp!!)
                        }
                    }
                }

                MIDCONST.MID_CURRENT_SOURCE.toInt() -> {/*this MB to get current sources*/
                    try {
                         LibreLogger.d(TAG, "Recieved the current source as  " + sceneObject?.currentSource)
                        val mSource = Integer.parseInt(msg)
                        if (sceneObject != null) {
                            sceneObject.currentSource = mSource
                            ScanningHandler.getInstance().putSceneObjectToCentralRepo(nettyData?.getRemotedeviceIp(), sceneObject)
                           //suma comment
                           //  updateMusicPlayViews(sceneObject,4,nettyData?.remotedeviceIp!!)
                            if (sceneObject.trackName != null) {
                                currentTrackName = sceneObject.trackName
                            }
                            currentArtistName = sceneObject.artist_name
                            currentAlbumnName = sceneObject.album_name
                            LibreLogger.d("==Albumart=="," 12 ${sceneObject.currentSource} and " +
                                    "ip" + " ${sceneObject.ipAddress} and ${sceneObject.album_art}")
                            AlbumArtURL = sceneObject.album_art
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                MIDCONST.MID_ENV_READ -> {
                    if (msg.contains("AlexaRefreshToken")) {
                        val token = msg.substring(msg.indexOf(":") + 1)
                        val mNode = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(nettyData?.getRemotedeviceIp())
                        if (mNode != null) {
                            mNode.alexaRefreshToken = token
                        }
//                    handleAlexaViews(sceneObject)
//                        if (sceneObject != null) {
//                          suma comment
 //                                             updateMusicPlayViews(sceneObject,5,nettyData?.remotedeviceIp!!)
//                        }
                    }
                }

                MIDCONST.MID_PLAYTIME.toInt() -> {
                    if (msg.isNotEmpty()) {
                        val longDuration = java.lang.Long.parseLong(msg)
                        LibreLogger.d(TAG, "MID_PLAYTIME: $longDuration ")
                        sceneObject.currentPlaybackSeekPosition = longDuration.toFloat()
                        ScanningHandler.getInstance().putSceneObjectToCentralRepo(nettyData?.getRemotedeviceIp(), sceneObject)

                       //suma comment
                     //updateMusicPlayViews(sceneObject,6,nettyData?.remotedeviceIp!!)
                    }
                }
                MIDCONST.MUTE_UNMUTE_STATUS -> {
                    val msg=String(luciPacket.getpayload())
                    LibreLogger.d(TAG, "MB : 63, msg = $msg")
                    try {
                        if(msg.isNotEmpty()) {
                            sceneObject.mute_status = msg
                            //Setting the Mute and UnMute status
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            }

        }

        private fun handlePlayJsonUi(window: JSONObject, sceneObject: SceneObject?) {

            val updatedSceneObject = AppUtils.updateSceneObjectWithPlayJsonWindow(window, sceneObject!!)
            if (updatedSceneObject != null) {
                ScanningHandler.getInstance().putSceneObjectToCentralRepo(sceneObject.ipAddress, updatedSceneObject)

 //               updateMusicPlayViews(updatedSceneObject,7,musicPlayerIp!!)
            }
        }

        fun setControlIconsForAlexa(currentSceneObject: SceneObject?,
            play: ProgressButtonImageView,
            next: ImageView,
            previous: ImageView) {
            if (currentSceneObject == null) {
                return
            }
            val controlsArr = currentSceneObject.controlsValue ?: return
            LibreLogger.d(TAG, "setControlIconsForAlexa controls $controlsArr")
            play.isEnabled = controlsArr[0]
            play.isClickable = controlsArr[0]
            LibreLogger.d(TAG, "setControlIconsForAlexa play ${play.isEnabled}")
            if (!play.isEnabled) {
                play.setImageResource(R.drawable.play_white)
            }

            next.isEnabled = controlsArr[1]
            next.isClickable = controlsArr[1]
            if (next.isEnabled) {
                next.setImageResource(R.drawable.next_enabled)
            } else {
                next.setImageResource(R.drawable.next_disabled)
            }

            previous.isEnabled = controlsArr[2]
            previous.isClickable = controlsArr[2]
            if (previous.isEnabled) {
                previous.setImageResource(R.drawable.prev_enabled)
            } else {
                previous.setImageResource(R.drawable.prev_disabled)
            }
        }

        private fun handleGCastMessage(nettyData: NettyData) {

            val packet = LUCIPacket(nettyData.getMessage())
            val msg = String(packet.getpayload())
            val mNode = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(nettyData.getRemotedeviceIp())
            LibreLogger.d(TAG, "handleGCastMessage: " + packet.command + " Message is " + msg)
            when (packet.command) {
                MIDCONST.FW_UPGRADE_PROGRESS.toInt() -> {
                    LibreLogger.d(TAG, "handleGCastMessage:UPGRADE_PROGRESS " + packet.command + "FW update  Message is " + msg)
                    if (mNode == null) return
                    var fwUpgradeData: FwUpgradeData? = FW_UPDATE_AVAILABLE_LIST[nettyData.getRemotedeviceIp()]
                    var mNewData = false
                    if (fwUpgradeData == null) {
                        fwUpgradeData = FwUpgradeData(mNode.ip, mNode.friendlyname, "", 0)
                        mNewData = true
                        FW_UPDATE_AVAILABLE_LIST[mNode.ip] = fwUpgradeData
                    }

                    /*if Gcast COmplete State or For SAC Device then we are showing Device will reboot
                 * because after firmwareupgraade COmpleted we are giving ok and coming back to PlayNEwScreen */
                    if (msg.equals(GCAST_COMPLETE, ignoreCase = true) && !mNode.friendlyname.equals(sacDeviceNameSetFromTheApp, ignoreCase = true)) {
                        fwUpgradeData.setmProgressValue(100)
                        fwUpgradeData.updateMsg = getString(R.string.gcast_update_done)


                        showAlertForFwMsgBox("Firmware update completed for ", mNode, ", " + getString(R.string.deviceRebooting))
                        return
                    }

                    /*if Gcast Failed State  back to PlayNEwScreen */
                    if (msg.equals("255", ignoreCase = true) && !mNode.friendlyname.equals(sacDeviceNameSetFromTheApp, ignoreCase = true)) {
                        showAlertForFwMsgBox("Firmware Update Failed For ", mNode)
                        fwUpgradeData.updateMsg = getString(R.string.fwUpdateFailed)
                        return
                    }

                    try {
                        fwUpgradeData.setmProgressValue(Integer.valueOf(msg))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    if (mNewData) showAlertForFirmwareUpgrade(mNode, fwUpgradeData)
                }

                MIDCONST.FW_UPGRADE_INTERNET_LS9.toInt() -> {
                    LibreLogger.d(TAG, "handleGCastMessage:INTERNET_LS9 " + packet.command + " INTERNET_LS9  Message is " + msg)
                    if (mNode == null) return/* LibreLogger.d(TAG,_FW_UPDATE, "handleGCastMessage:INTERNET_LS9 before " + "Node:- $sacDeviceNameSetFromTheApp FriendlyName: ${mNode.friendlyname}")*/
                    if (mNode.friendlyname.equals(sacDeviceNameSetFromTheApp, ignoreCase = true)) {
                        if (msg.equals(NO_UPDATE, ignoreCase = true)) {
                            return
                        }/* LibreLogger.d(TAG,_FW_UPDATE, "handleGCastMessage:INTERNET_LS9 Before FW list ${packet.command} INTERNET_LS9  Message is "+msg)*/
                        var fwUpgradeData: FwUpgradeData? = FW_UPDATE_AVAILABLE_LIST[nettyData.getRemotedeviceIp()]
                        var mNewData = false
                        if (fwUpgradeData == null) {
                            fwUpgradeData = FwUpgradeData(mNode.ip, mNode.friendlyname, "", 0)
                            mNewData = true
                            FW_UPDATE_AVAILABLE_LIST[mNode.ip] = fwUpgradeData
                        }
                        LibreLogger.d(TAG, "handleGCastMessage:INTERNET_LS9  Before When Message is $msg")
                        when {
                            msg.equals(UPDATE_STARTED, ignoreCase = true) -> fwUpgradeData.updateMsg = getString(R.string.mb223_update_started)

                            msg.equals(UPDATE_STARTED, ignoreCase = true) -> fwUpgradeData.updateMsg = getString(R.string.mb223_update_started)

                            msg.equals(UPDATE_DOWNLOAD, ignoreCase = true) -> fwUpgradeData.updateMsg = getString(R.string.mb223_update_download)

                            msg.equals(UPDATE_IMAGE_AVAILABLE, ignoreCase = true) -> fwUpgradeData.updateMsg = getString(R.string.mb223_update_image_available)

                            msg.equals(NO_UPDATE, ignoreCase = true) -> fwUpgradeData.updateMsg = getString(R.string.noupdateAvailable)

                            msg.equals(DOWNLOAD_FAIL, ignoreCase = true) || msg.equals(CRC_CHECK_ERROR, ignoreCase = true) -> fwUpgradeData.updateMsg = getString(R.string.mb223_download_fail)

                            else -> try {
                                if (msg.isNotEmpty()) {
                                    val mProgressValue = Integer.valueOf(msg)
                                    fwUpgradeData.updateMsg = getString(R.string.downloadingtheFirmare)
                                    fwUpgradeData.setmProgressValue(mProgressValue)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                LibreLogger.d(TAG, "handleGCastMessage:INTERNET_LS9 Exception:- ${e.message}")
                            }
                        }

                        if (mNewData) showAlertForFirmwareUpgrade(mNode, fwUpgradeData)
                    } else {
                        LibreLogger.d(TAG, "handleGCastMessage:INTERNET_LS9 SAC and FriendlyName " + "didn't match")
                    }
                }
            }
        }

        /*this method is to show error in whole application*/
        fun showErrorMessage(message: LibreError?) {
            if (hideErrorMessage) return
            if (isActivityPaused || isFinishing) return

            LibreLogger.d(TAG, "showErrorMessage ${System.currentTimeMillis()} ${message!!.errorMessage}")
            runOnUiThread {
                showToast(message.errorMessage)
            }
        }
    fun registerLocationPermissionCallback(callback: LocationPermissionCallback) {
        locationPermissionCallback = callback
    }

    fun unregisterLocationPermissionCallback() {
        locationPermissionCallback = null
    }

    fun registerForDeviceEvents(libreListner: LibreDeviceInteractionListner) {
        this.libreDeviceInteractionListner = libreListner
    }

    fun unRegisterForDeviceEvents() {
        this.libreDeviceInteractionListner = null
    }

        fun intentToHome(context: Context) {
            //  LibreLogger.d(TAG,TAG_DEVICE_REMOVED, "intentToHome:- ${context::class.java.simpleName}")
            LibreLogger.d(TAG, "intentToHome called by " + context::class.java.simpleName)
            val newIntent: Intent
            if (LSSDPNodeDB.getInstance().GetDB().size > 0) {
                newIntent = Intent(context, CTHomeTabsActivity::class.java)
                newIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK
                newIntent.putExtra(AppConstants.LOAD_FRAGMENT, CTActiveDevicesFragment::class.java.simpleName)
                startActivity(newIntent)
            } else {
                newIntent = Intent(context, CTHomeTabsActivity::class.java)
                newIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK
                newIntent.putExtra(AppConstants.LOAD_FRAGMENT, CTNoDeviceFragment::class.java.simpleName)
                startActivity(newIntent)
            }
        }

        override fun onResume() {
            super.onResume()
            isActivityPaused = false
        }

        private fun initBusEvent() {
            try {
                BusProvider.getInstance().register(busEventListener)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (mCleanUpIsDoneButNotRestarted) {
                mCleanUpIsDoneButNotRestarted = false
//            restartApp(this@CTDeviceDiscoveryActivity)
            }
        }

        fun onWifiScanDone(scanResults: List<ScanResult>) {

        }

        override fun onStop() {
            unRegisterForDeviceEvents()

            if (upnpProcessor != null) {
                upnpProcessor!!.removeListener(UpnpDeviceManager.getInstance())
                upnpProcessor!!.unbindUpnpService()
            }

            try {
                BusProvider.getInstance().unregister(busEventListener)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            super.onStop()
        }

        override fun onDestroy() {
            super.onDestroy()
            try {
                unregisterReceiver(localNetworkStateReceiver)
            } catch (e: Exception) {
                LibreLogger.d(TAG, "Exception happened while unregistering the reciever!! " + e.message)
            }
            progressDialog.stop()

        }

        private inner class LocalNetworkStateReceiver : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                LibreLogger.d(TAG, "onReceive action = " + intent.action + ", listenToNetworkChanges = " + isNetworkChangesCallBackEnabled)

                val networkInfo = intent.getParcelableExtra<NetworkInfo>(ConnectivityManager.EXTRA_NETWORK_INFO)
                val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiManager.connectionInfo

                onReceiveSSID = getConnectedSSIDName(this@CTDeviceDiscoveryActivity)/*       LibreLogger.d(TAG,TAG, "onReceive onReceiveSSID $onReceiveSSID")
            LibreLogger.d(TAG,TAG, "onReceive wifiInfo ssid " + wifiInfo.ssid)
            LibreLogger.d(TAG, "onReceive networkInfo type, State " + networkInfo!!.typeName + ", " + networkInfo.state)
            LibreLogger.d(TAG, "onReceive networkInfo isConnected " + networkInfo.isConnected)
            LibreLogger.d(TAG, "onReceive networkInfo Detailed State " + networkInfo.detailedState)
            LibreLogger.d(TAG, "onReceive wifiInfo Supplicant State " + wifiInfo.supplicantState)*/

                connectivityOnReceiveCalled(context, intent)

                if (!intent.action?.equals(ConnectivityManager.CONNECTIVITY_ACTION)!! && !intent.action?.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)!!) {
                    return
                }

                val wifiUtil = WifiUtil(this@CTDeviceDiscoveryActivity)
                val isConnected = wifiUtil.isWifiOn()
                isWifiON = isConnected

                if (!isConnected) {
                    if (activeSSID != null && activeSSID != "") {
                        mActiveSSIDBeforeWifiOff = activeSSID
                    }
                    activeSSID = ""
                    wifiConnected(false)
                }

                if (networkInfo!!.type == ConnectivityManager.TYPE_WIFI) {
                    if (onReceiveSSID == null || onReceiveSSID == "<unknown ssid>") {
                        if (networkInfo.extraInfo == null) {
                        } else {
                            onReceiveSSID = networkInfo.extraInfo.replace("\"", "")
                            if (onReceiveSSID == null || onReceiveSSID == "<unknown ssid>") {
                            }
                        }
                    }
                    wifiConnected(networkInfo.isConnected)
                } else if (networkInfo.type == ConnectivityManager.TYPE_MOBILE || networkInfo.type == ConnectivityManager.TYPE_MOBILE_HIPRI) {
                    wifiConnected(isConnected)
                }
            }
        }

        open fun wifiConnected(connected: Boolean) {
            if (libreDeviceInteractionListner != null && libreDeviceInteractionListner is CTConnectingToMainNetwork) {
                return
            }
            if (!connected) {
                cleanUpCode(false)
            } else {
                if (!mLuciThreadInitiated) {
                    restartAllSockets()
                }

            }
        }

        fun showWifiNetworkOffAlert() {
            if (!this@CTDeviceDiscoveryActivity.isFinishing && !isNetworkOffCallBackEnabled) {

                if (alertRestartApp != null && alertRestartApp!!.isShowing) {
                    alertRestartApp!!.dismiss()
                }

                val alertDialogBuilder = AlertDialog.Builder(this@CTDeviceDiscoveryActivity)

                // set title
                alertDialogBuilder.setTitle(getString(R.string.wifiConnectivityStatus))

                // set dialog message
                alertDialogBuilder.setMessage(getString(R.string.connectToWifi)).setCancelable(false).setPositiveButton(getString(R.string.ok)) { dialog, id ->
                    // if this button is clicked, close
                    // current activity
                    dialog.cancel()
                    //                            LibreApplication.prevConnectedSSID = LibreApplication.onReceiveSSID;
                    //                            LibreApplication.onReceiveSSID = null;
                    mCleanUpIsDoneButNotRestarted = false/*  startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));*/
                    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    customStartActivityForResult(WIFI_SETTINGS_REQUEST_CODE, intent)
                }

                // create alertDialog1 dialog
                if (alertRestartApp == null || !alertRestartApp!!.isShowing) alertRestartApp = alertDialogBuilder.show()
            }
        }


        open fun removeTheDeviceFromRepo(ipadddress: String?) {
            if (ipadddress != null) {
                val mNodeDB = LSSDPNodeDB.getInstance()
                val nodes = mNodeDB.getTheNodeBasedOnTheIpAddress(ipadddress)

                if (nodes != null) {
                    if (libreDeviceInteractionListner != null) libreDeviceInteractionListner!!.deviceGotRemoved(ipadddress)

                    try {
                        if (ScanningHandler.getInstance().isIpAvailableInCentralSceneRepo(ipadddress)) {
                            val status = ScanningHandler.getInstance().removeSceneMapFromCentralRepo(ipadddress)
                            LibreLogger.d(TAG, "Active Scene Adapter For the Master $status")
                        }
                        try {
                            val renderingDevice = UpnpDeviceManager.getInstance().getRemoteDMRDeviceByIp(ipadddress)
                            val renderingUDN = renderingDevice!!.identity.udn.toString()
                            val playbackHelper = PLAYBACK_HELPER_MAP[renderingUDN]

                            val mScanHandler = ScanningHandler.getInstance()
                            val currentSceneObject = mScanHandler.getSceneObjectFromCentralRepo(ipadddress)

                            try {
                                if (playbackHelper != null && renderingDevice != null && currentSceneObject != null && currentSceneObject.playUrl != null && !currentSceneObject.playUrl.equals("", ignoreCase = true) && currentSceneObject.playUrl.contains(LOCAL_IP) && currentSceneObject.playUrl.contains(ContentTree.AUDIO_PREFIX)) {
                                    playbackHelper.stopPlayback()
                                }
                            } catch (e: Exception) {

                                LibreLogger.d(TAG, "Handling the exception while sending the stopMediaServercommand ")
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    } catch (e: Exception) {
                        LibreLogger.d(TAG, "Active Scene Adapter" + "Removal Exception ")
                    }
                }

                mNodeDB.clearNode(ipadddress)
            }

        }

        fun alertDialogForDeviceNotAvailable(mNode: LSSDPNodes?) {
            LibreLogger.d(TAG_DEVICE_REMOVED, "deviceGotRemoved, alertDialogForDeviceNotAvailable" +
                    " called")
            if (!this@CTDeviceDiscoveryActivity.isFinishing) {
                if (mNode == null) return
                alertDialog1 = null
                val alertDialogBuilder = AlertDialog.Builder(this@CTDeviceDiscoveryActivity)
                alertDialogBuilder.setTitle(getString(R.string.deviceNotAvailable))
                alertDialogBuilder.setMessage(getString(R.string.removeDeviceMsg) + " " + mNode.friendlyname + " " + getString(R.string.removeDeviceMsg2)).setCancelable(false).setPositiveButton(getString(R.string.ok)) { dialog, id ->
//                    alertDialog1!!.dismiss()
//                    removeTheDeviceFromRepo(mNode.ip)
//                    intentToHome(this)
                    alertDialog1!!.dismiss()
                    removeTheDeviceFromRepo(mNode.ip)
                    LUCIControl.luciSocketMap.remove(mNode.ip)
                    LibreApplication.securecertExchangeSucessDevices.clear()
                    LUCIControl.handshake.clear()
                    if(!localClassName.isEmpty()) {
                        if (localClassName.contains("CTSplashScreenActivity") || localClassName.contains(
                                "CTHomeTabsActivity"
                            ) || localClassName.contains("CTDeviceSettingsActivity"))
                        {
                            intentToHome(this)
                        }
                        else {
                            alertDialog1!!.dismiss()
                        }
                    }
                    else{
                        //this print will not come just added for crash handling
                        alertDialog1!!.dismiss()
                    }

                }
                if (alertDialog1 == null) alertDialog1 = alertDialogBuilder.create()
                alertDialog1!!.show()
            }else{
                LibreLogger.d(TAG_DEVICE_REMOVED, "deviceGotRemoved, " +
                        "alertDialogForDeviceNotAvailable ELSE")
            }
        }

        /* Whenever we are going to Do app Restarting,its not restarting the app properly and We are doing Cleanup and then We are restarting the APP
     * Till i HAave to Analyse more on this code */
        fun cleanUpCode(mCompleteCleanup: Boolean) {
            mCleanUpIsDoneButNotRestarted = true
            //  val application = application as LibreApplication
            try {

                for (mAndroid in LUCIControl.luciSocketMap.values.toTypedArray()) {
                    if (mAndroid != null && mAndroid.handler != null && mAndroid.handler.mChannelContext != null) mAndroid.handler.mChannelContext.close()
                }

                for (mAndroidChannelHandlerContext in LUCIControl.channelHandlerContextMap.values.toTypedArray()) {
                    mAndroidChannelHandlerContext?.channel?.close()
                }

                if (mCompleteCleanup) {
                    this.libreDeviceInteractionListner = null
                    BusProvider.getInstance().unregister(busEventListener)
                }

                if (ScanThread.getInstance().nettyServer.mServerChannel != null && ScanThread.getInstance().nettyServer.mServerChannel.isBound()) {
                    ScanThread.getInstance().nettyServer.mServerChannel.unbind()
                    val serverClose: ChannelFuture = ScanThread.getInstance().nettyServer.mServerChannel.close()
                    serverClose.awaitUninterruptibly()
                }

                ScanThread.getInstance().closeMSearchThread()

                ScanThread.getInstance().clearNodes()

                ScanThread.getInstance().mRunning = false
                ScanThread.getInstance().close()

                ScanThread.getInstance().mAliveNotifyListenerSocket = null
                ScanThread.getInstance().mDatagramSocketForSendingMSearch = null
                ScanningHandler.getInstance().clearSceneObjectsFromCentralRepo()

            LUCIControl.luciSocketMap.clear()
            LibreApplication.securecertExchangeSucessDevices.clear()
                LUCIControl.handshake.clear()

                LUCIControl.channelHandlerContextMap.clear()
            LSSDPNodeDB.getInstance().clearDB()
            ensureDMRPlaybackStopped()
            LibreEntryPoint.getInstance().clearApplicationCollections()
            LibreApplication().micTcpClose()

                if (upnpBinder == null) return

                if (upnpProcessor != null) {
                    upnpProcessor!!.removeListener(this)
                    upnpProcessor!!.removeListener(UpnpDeviceManager.getInstance())
                    upnpProcessor!!.unbindUpnpService()

                    LibreLogger.d(TAG, "Cleanup unbindUpnpService")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                LibreLogger.d(TAG, "Cleanup exception ${e.message}")
            }

            LibreLogger.d(TAG, "Cleanup is done Successfully")
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        fun restartApp(context: Context) {
            LibreLogger.d("NetworkChanged", "App is Restarting")/* Stopping ForeGRound Service When we are Restarting the APP */
            stopDmrPlayerService()

            restartSplashScreen()

            finishActivitiesAndKillAppProcess()
        }

        private fun restartSplashScreen() {
            LibreLogger.d(TAG, "restartSplashScreen starting ")
            val mStartActivity = Intent(this, CTSplashScreenActivityV2::class.java)/*sending to let user know that app is restarting*/
            val mPendingIntentId = 123456
            val mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            val alarmManager = this.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 200, mPendingIntent)
        }

        fun restartAllSockets() {
            Log.d(GLOBAL_TAG, "initLUCIServices restartAllSockets: Called")
            return LibreEntryPoint.getInstance().initLUCIServices()
        }

        private fun phoneIpAddress(): String {
            return Utils().getIPAddress(true)
        }

        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            when (requestCode) {
                LOCATION_PERMISSION_REQUEST_CODE -> {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        LibreLogger.d(TAG_, "Permission 22 $grantResults")
                        locationPermissionCallback?.onPermissionGranted()
                    }else {
                        LibreLogger.d(TAG_, "Permission 21 $grantResults")
                        locationPermissionCallback?.onPermissionDenied()
                        checkLocationPermission()
                    }
                }
                MICROPHONE_PERMISSION_REQUEST_CODE -> checkMicrophonePermission()
                READ_STORAGE_REQUEST_CODE -> checkReadStoragePermission()
            }
        }

        fun setMessageProgressDialog(message: String) {
            if(!((this)).isFinishing()) {
                showProgressDialog(message)
            }
        }

        private fun checkForTheSACDeviceSuccessDialog(node: LSSDPNodes?) {

            val sharedPreferences = applicationContext.getSharedPreferences("sac_configured", Context.MODE_PRIVATE)
            val str = sharedPreferences.getString("deviceFriendlyName", "")
            if (str != null && str.equals(node!!.friendlyname.toString(), ignoreCase = true) && node.getgCastVerision() != null) {


                thisSACDeviceNeeds226 = node.friendlyname
                if (FW_UPDATE_AVAILABLE_LIST.containsKey(node.ip)) FW_UPDATE_AVAILABLE_LIST.remove(node.ip)

                val alertDialogBuilder = AlertDialog.Builder(this)

                // set title
                alertDialogBuilder.setTitle("Configure successful")

                // set dialog message
                alertDialogBuilder.setMessage("Cast your favorite music and radio apps from your phone or tablet to your " + node.friendlyname.toString()).setCancelable(false).setNegativeButton("No Thanks") { dialogInterface, i -> dialogInterface.cancel() }.setPositiveButton("Learn More") { dialog, id ->
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.data = Uri.parse("https://www.google.com/cast/audio/learn")
                    startActivity(intent)
                }

                val sAcalertDialog = alertDialogBuilder.create()/*sAcalertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);*/
                sAcalertDialog.show()
                sharedPreferences.edit().remove("deviceFriendlyName").apply()
            } else if (node != null && node.getgCastVerision() == null && str!!.equals(node.friendlyname, ignoreCase = true)) {
                sharedPreferences.edit().remove("deviceFriendlyName").apply()
            }

        }

        private fun showAlertForFwMsgBox(Message: String, node: LSSDPNodes?) {
            if (!sacDeviceNameSetFromTheApp.equals("", ignoreCase = true)) return

            val alertDialogBuilder = AlertDialog.Builder(this@CTDeviceDiscoveryActivity)
            alertDialogBuilder.setTitle("Firmware Upgrade")
            alertDialogBuilder.setMessage(Message + node!!.friendlyname).setCancelable(false).setPositiveButton("OK") { dialog, id -> sAcalertDialog = null }
            if (sAcalertDialog == null) sAcalertDialog = alertDialogBuilder.create()

            /*sAcalertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);*/
            sAcalertDialog!!.show()
            try {
                sAcalertDialog!!.show()
            } catch (e: Exception) {
                LibreLogger.d(TAG, "Permission Denied")
            }

        }

        // overloaded this method because, on success we have to show "Device is Rebooting(an extra text at the end)
        private fun showAlertForFwMsgBox(Message: String, node: LSSDPNodes, Message2: String) {
            if (!sacDeviceNameSetFromTheApp.equals("", ignoreCase = true)) return

            val alertDialogBuilder = AlertDialog.Builder(this@CTDeviceDiscoveryActivity)
            alertDialogBuilder.setTitle("Firmware Upgrade")
            alertDialogBuilder.setMessage(Message + node.friendlyname.toString() + Message2).setCancelable(false).setPositiveButton("OK") { dialog, id -> sAcalertDialog = null }
            if (sAcalertDialog == null) sAcalertDialog = alertDialogBuilder.create()
            sAcalertDialog!!.show()
        }

        private fun showAlertForFirmwareUpgrade(mNode: LSSDPNodes, fwUpgradeData: FwUpgradeData) {
            //  if (!sacDeviceNameSetFromTheApp.equals("", ignoreCase = true)) return
            val builder = AlertDialog.Builder(this@CTDeviceDiscoveryActivity)
            builder.setTitle("Firmware Upgrade")
            builder.setMessage(mNode.friendlyname + ": New update available. Firmware is upgrading with new update").setCancelable(false).setPositiveButton("OK") { dialog, id ->
                dialog.dismiss()
                fwUpdateAlertDialog = null
                BusProvider.getInstance().post(fwUpgradeData)
            }

            if (fwUpdateAlertDialog == null) fwUpdateAlertDialog = builder.create()
            if (!fwUpdateAlertDialog?.isShowing!!) fwUpdateAlertDialog!!.show()
        }

        fun showToast(message: String) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        fun showToast(messageID: Int) {
            Toast.makeText(this, messageID, Toast.LENGTH_SHORT).show()
        }

        fun showProgressDialog(message: String) {
            progressDialog.start(message)
        }

        fun showProgressDialog(messageID: Int) {
            progressDialog.start(getString(messageID))
        }

        fun dismissDialog() {
            progressDialog.stop()
        }

        /*Override this method in child activity that extends this activity,
    so as to be notified every time there's network change*/
        open fun connectivityOnReceiveCalled(context: Context,
            intent: Intent) {/*Let child classes handle*/
        }

        /*this method will release local DMR playback songs*/
        fun ensureDMRPlaybackStopped() {

            val mScanningHandler = ScanningHandler.getInstance()
            val centralSceneObjectRepo = mScanningHandler.sceneObjectMapFromRepo

            /*which means no master present hence all devices are free so need to do anything*/
            if (centralSceneObjectRepo == null || centralSceneObjectRepo.size == 0) {
                LibreLogger.d(TAG, "No master present")
                return
            }

            try {
                for (masterIPAddress in centralSceneObjectRepo.keys) {
                    val renderingDevice = UpnpDeviceManager.getInstance().getRemoteDMRDeviceByIp(masterIPAddress)

                    val mScanHandler = ScanningHandler.getInstance()
                    val currentSceneObject = mScanHandler.getSceneObjectFromCentralRepo(masterIPAddress)

                    if (renderingDevice != null && currentSceneObject != null && currentSceneObject.playUrl != null && !currentSceneObject.playUrl.equals("", ignoreCase = true) && currentSceneObject.playUrl.contains(LOCAL_IP) && currentSceneObject.playUrl.contains(ContentTree.AUDIO_PREFIX)) {
                        LUCIControl(masterIPAddress).SendCommand(MIDCONST.MID_PLAYCONTROL.toInt(), LUCIMESSAGES.STOP, LSSDPCONST.LUCI_SET)

                        val renderingUDN = renderingDevice.identity.udn.toString()
                        val playbackHelper = PLAYBACK_HELPER_MAP[renderingUDN]
                        playbackHelper?.stopPlayback()

                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun requestLuciUpdates(devicIp: String) {
            LibreLogger.d(TAG,"requestLuciUpdates deviceIp:- $devicIp")
            try {
                val luciControl = LUCIControl(devicIp)
                val luciPackets = ArrayList<LUCIPacket>()
                val mSceneNamePacket = LUCIPacket(null, 0.toShort(), MIDCONST.MID_SCENE_NAME.toShort(), LSSDPCONST.LUCI_GET.toByte())/*removing it to resolve flickering issue*/
                val volumePacket = LUCIPacket(null, 0.toShort(), MIDCONST.VOLUME_CONTROL.toShort(), LSSDPCONST.LUCI_GET.toByte())
                val mutePacket = LUCIPacket(null, 0.toShort(), MIDCONST.MUTE_UNMUTE_STATUS.toShort(), LSSDPCONST.LUCI_GET.toByte())
                val currentSourcePacket = LUCIPacket(null, 0.toShort(), MIDCONST.MID_CURRENT_SOURCE, LSSDPCONST.LUCI_GET.toByte())
                val currentPlayStatePacket = LUCIPacket(null, 0.toShort(), MIDCONST.MID_CURRENT_PLAY_STATE, LSSDPCONST.LUCI_GET.toByte())/*   val getUIPacket = LUCIPacket(null, 0.toShort(), MIDCONST.MID_REMOTE_UI, LSSDPCONST.LUCI_GET.toByte())*//*val getUIPacket = LUCIPacket(null, 0.toShort(), MIDCONST.MID_REMOTE_UI_OEM, LSSDPCONST.LUCI_SET.toByte())*/
                val getUIPacket = LUCIPacket(LUCIMESSAGES.GET_PLAY.toByteArray(), LUCIMESSAGES.GET_PLAY.length.toShort(), MIDCONST.MID_REMOTE_UI, LSSDPCONST.LUCI_SET.toByte())
                val currentPlayTime = LUCIPacket(null, 0.toShort(), MIDCONST.MID_PLAYTIME, LSSDPCONST.LUCI_GET.toByte())
                val alexaRefreshTokenPacket = LUCIPacket(LUCIMESSAGES.READ_ALEXA_REFRESH_TOKEN_MSG.toByteArray(), LUCIMESSAGES.READ_ALEXA_REFRESH_TOKEN_MSG.length.toShort(), MIDCONST.MID_ENV_READ.toShort(), LSSDPCONST.LUCI_GET.toByte())
                val readBTState = LUCIPacket(null, 0.toShort(), MID_BLUETOOTH_STATUS.toShort(), LSSDPCONST.LUCI_GET.toByte())
                luciPackets.add(getUIPacket)
                luciPackets.add(alexaRefreshTokenPacket)
                luciPackets.add(volumePacket)
                luciPackets.add(mSceneNamePacket)
                luciPackets.add(mutePacket)
                luciPackets.add(currentSourcePacket)
                luciPackets.add(currentPlayStatePacket)
                luciPackets.add(currentPlayTime)
                luciPackets.add(readBTState)
                luciControl.SendCommand(luciPackets)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d(TAG, "requestLuciUpdates:exception " + e.message)

            }

        }

        fun showLoginWithAmazonAlert(speakerIpAddress: String): Boolean {
            if (isFinishing) return true

            AlertDialog.Builder(this@CTDeviceDiscoveryActivity).setTitle(R.string.login_with_amazon).setMessage(R.string.login_with_amazon_warn).setCancelable(false).setPositiveButton(R.string.amazon_login) { dialog, id ->
                dialog.dismiss()
                val amazonLoginScreen = Intent(this@CTDeviceDiscoveryActivity, CTAmazonLoginActivity::class.java)
                amazonLoginScreen.putExtra(CURRENT_DEVICE_IP, speakerIpAddress)
                startActivity(amazonLoginScreen)
            }.setNegativeButton(R.string.cancel) { dialogInterface, i -> dialogInterface.dismiss() }.show()
            return true
        }

        private fun checkMicrophonePermission() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !AppUtils.isPermissionGranted(this, Manifest.permission.RECORD_AUDIO)) {
                requestRecordPermission()
            } else {
                LibreLogger.d("checkMicrophonePerm", "Permission already granted.")
            }
        }

        fun requestRecordPermission() {
            /*  LibreLogger.d(TAG,"==mandateDialog==18", "Record permission has NOT been granted. Requesting " + "permission.")*/
            if (AppUtils.shouldShowPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                // Provide an additional rationale to the user if the permission was not granted
                // and the user would benefit from additional context for the use of the permission.
                // For example if the user has previously denied the permission.
                /*  LibreLogger.d(TAG,"==mandateDialog==19", "Displaying Record permission rationale to provide " + "additional context.")*/
                Snackbar.make(parentView!!, R.string.permission_record_rationale, Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok) { AppUtils.requestPermission(this@CTDeviceDiscoveryActivity, Manifest.permission.RECORD_AUDIO, MICROPHONE_PERMISSION_REQUEST_CODE) }.show()
            } else {
                if (!sharedPreferenceHelper.isFirstTimeAskingPermission(Manifest.permission.RECORD_AUDIO)) {
                    sharedPreferenceHelper.firstTimeAskedPermission(Manifest.permission.RECORD_AUDIO, true)
                    // No explanation needed, we can request the permission.
                    // Camera permission has not been granted yet. Request it directly.
                    AppUtils.requestPermission(this, Manifest.permission.RECORD_AUDIO, MICROPHONE_PERMISSION_REQUEST_CODE)
                } else {
                    /*LibreLogger.d(TAG,"==mandateDialog==21", "Displaying Record permission Else showAlertDialog")*/
                    //showAlertForRecordPermissionRequired()
                    //   if (mandateDialog != null && mandateDialog!!.isShowing) mandateDialog!!.dismiss()
                    showAlertDialog(getString(R.string.enableRecordPermit), getString(R.string
                        .action_settings), MICROPHONE_PERM_SETTINGS_REQUEST_CODE, isDeviceLost
                    =false, isLocationPermission =false, isLocationPermissionRotational=false)
                }

            }
        }

        private fun showRecordPermissionDisabledSnackBar() {
            //Permission disable by device policy or user denied permanently. Show proper error message
            Snackbar.make(parentView!!, R.string.enableRecordPermit, Snackbar.LENGTH_INDEFINITE).setAction(R.string.action_settings) {
                customStartActivityForResult(1220, Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package", packageName, null)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY).addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS))
            }.show()
        }


        override fun recordError(error: String) {
            // LibreLogger.d(TAG, "recordError = $error")
            showToast(error)
        }

        override fun recordStopped() {
            // LibreLogger.d(TAG, "recordStopped")
        }

        override fun recordProgress(byteBuffer: ByteArray) {

        }

        override fun sendBufferAudio(audioBufferBytes: ByteArray) {
            micTcpServer!!.sendDataToClient(audioBufferBytes)
        }

        override fun micExceptionCaught(e: Exception) {
            // LibreLogger.d(TAG, "micExceptionCaught = " + e.message)
            audioRecordUtil!!.stopRecording()
        }

        /**
         * Created By Shaik Mansoor
         */
        fun showAlertDialog(message: String,
            positiveButtonString: String,
            requestCode: Int,
            isDeviceLost: Boolean,
            isLocationPermission: Boolean,
            isLocationPermissionRotational: Boolean) {
            LibreLogger.d(TAG_, "showAlertDialog and requestCode: $requestCode")
            if (mandateDialog != null && mandateDialog!!.isShowing) mandateDialog!!.dismiss()
            else mandateDialog = null

            if (mandateDialog == null) {
                val builder = AlertDialog.Builder(this)
                if(isDeviceLost) {
                    builder.setTitle(getString(R.string.device_connection_los))
                }else if(isLocationPermission){
                    builder.setTitle(getString(R.string.permission_required))
                }
                builder.setMessage(message)
                builder.setCancelable(false)
                builder.setPositiveButton(positiveButtonString) { dialogInterface, i ->
                    mandateDialog!!.dismiss()
                    if(isDeviceLost) {
                        intentToHome(this)
                    }else if (isLocationPermission){
                        AppUtils.requestPermission(this@CTDeviceDiscoveryActivity, Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_REQUEST_CODE)
                    } else if(isLocationPermissionRotational){
                         val packageName = packageName
                      val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                      intent.data = Uri.fromParts("package", packageName, null)
                      customStartActivityForResult(LOCATION_PERM_SETTINGS_REQUEST_CODE,intent)
                    } else {
                        LibreLogger.d(TAG_, "showAlertDialog else")
                        customStartActivityForResult(requestCode, Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            .setData(Uri.fromParts("package", packageName, null))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                            .addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS))
                    }
                }
                /*if (isNetworkMisMatch) {
                    builder.setNegativeButton("Cancel") { dialogInterface, i ->
                        mandateDialog!!.dismiss()
                    }
                }*/
                mandateDialog = builder.create()
            }
            if(!((this)).isFinishing()) {
                if (!mandateDialog!!.isShowing) mandateDialog!!.show()
            }

        }

        /**
         * Created By Shaik Mansoor
         */
        private fun registerForActivityResult() {
            resultHandler = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                customOnActivityResult(result.data, requestCode, result.resultCode)
                this.requestCode = -1
            }
        }

        /**
         * Created By Shaik Mansoor
         */
        fun customStartActivityForResult(requestCode: Int, intent: Intent) {
            this.requestCode = requestCode
            resultHandler?.launch(intent)
        }

        /**
         * Created By Shaik Mansoor
         */
        protected open fun customOnActivityResult(data: Intent?, requestCode: Int, resultCode: Int) {
            LibreLogger.d(TAG, "customOnActivityResult requestCode: $requestCode")
            if (requestCode == WIFI_SETTINGS_REQUEST_CODE) {
                //  LibreLogger.d(TAG,"==mandateDialog==12", "onActivityResult wifi list")
                val connManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)/*For routers without Internet*/

                if (mWifi!!.isConnected || mWifi.detailedState == NetworkInfo.DetailedState.CONNECTED) {
                    // LibreLogger.d(TAG, "came back from wifi list and checking Connected status ")
                    if (!isFinishing) {
                        //  LibreLogger.d(TAG, "came back from wifi list show alert ")\
                        /**
                         * Commented By SHIAK, as of now we don't need this method
                         * Discussed with SUMA
                         */
                        // showNetworkChangeRestartAppAlert()
                    }
                } else {
                    //LibreLogger.d(TAG, "came back from wifi list and checking Connected status OFFF")
                }
            }

            if (requestCode == LOCATION_PERM_SETTINGS_REQUEST_CODE || requestCode == LOCATION_SETTINGS_REQUEST_CODE) {
                if(resultCode==RESULT_CANCELED) {
                    Log.d(TAG_, "Permission 30  $resultCode")
                    checkLocationPermission()
                }else{
                    Log.d(TAG_, "Permission 31  $resultCode")
                    if (mandateDialog != null && mandateDialog!!.isShowing) mandateDialog!!.dismiss()
                }
            }

            if (requestCode == MICROPHONE_PERM_SETTINGS_REQUEST_CODE) {
                //Log.d(TAG, "came back from wifi list micro phone")
                checkMicrophonePermission()
            }

            if (requestCode == STORAGE_PERM_SETTINGS_REQUEST_CODE) {
                //  Log.d(TAG, "came back from wifi list storage")
                checkReadStoragePermission()
            }
        }

        /**
         * Created By Shaik Mansoor
         */
        open fun getDeviceName(): String? {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.lowercase(Locale.getDefault()).startsWith(manufacturer.lowercase(Locale.getDefault()))) {
                capitalize(model)
            } else {
                capitalize(manufacturer) + " " + model
            }
        }

        /**
         * Created By Shaik Mansoor
         */
        private fun capitalize(s: String?): String {
            if (s.isNullOrEmpty()) {
                return ""
            }
            val first = s[0]
            return if (Character.isUpperCase(first)) {
                s
            } else {
                first.uppercaseChar().toString() + s.substring(1)
            }
        }
 /**
    * Created By Shaik Mansoor
    */
 private suspend fun insertDeviceIntoDb(lsdNodes: LSSDPNodes){
     val uuid: String = randomUUID().toString()
     val addDeviceDate = CastLiteUUIDDataClass(0, lsdNodes.ip, lsdNodes.friendlyname, uuid)
     withContext(Dispatchers.IO) {
         delay(5000)
         try {
            // val id = libreVoiceDatabaseDao.addDeviceUUID(addDeviceDate)
             val id:Long
             if(libreVoiceDatabase!=null) {
                  id = libreVoiceDatabase!!.castLiteDao().addDeviceUUID(addDeviceDate)
                 LibreLogger.d(TAG, "insertDeviceIntoDb:id  $id")
             }else{
                 libreVoiceDatabase =LibreVoiceDatabase.getDatabase(this@CTDeviceDiscoveryActivity)
                 id = libreVoiceDatabase?.castLiteDao()?.addDeviceUUID(addDeviceDate) ?: 0
                 LibreLogger.d(TAG, "insertDeviceIntoDb else $id")
             }
         } catch (e: SQLiteException) {
             e.printStackTrace()
             LibreLogger.d(TAG, "insertDeviceIntoDb:ex  ${e.message}")
         }

     }
 }
        /**
         * Created By Shaik Mansoor
         */
        private suspend fun removeUUIDFromDB(deviceIpAddress: String?) {
            withContext(Dispatchers.IO) {
                delay(2000)
                try {
                    libreVoiceDatabase!!.castLiteDao().deleteDeviceUUID(deviceIpAddress.toString())
                }catch (ex: Exception){
                    LibreLogger.d(TAG, "removeUUIDFromDB:ex  ${ex.message}")
                }

            }
        }

        /**
         * Created By Shaik Mansoor
         */
        private fun doSharedOperations() {
            lifecycleScope.launch {
                delay(1000)
                val sharedPrefKey = providesSharedPreference(this@CTDeviceDiscoveryActivity).getString("", null)
                if (sharedPrefKey != null) {
                    LibreEntryPoint().setKey(sharedPrefKey)
                } else {
                    LibreEntryPoint().setKey("LibreVoice")
                }
                isKeyStored = true
            }
        }

    fun insertDeviceIntoDbFromCoreUPNP(nodes: LSSDPNodes) {
        lifecycleScope.launch {
            insertDeviceIntoDb(nodes)
        }
    }

}
