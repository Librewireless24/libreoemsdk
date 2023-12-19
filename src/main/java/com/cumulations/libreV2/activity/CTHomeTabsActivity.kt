package com.cumulations.libreV2.activity

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.cumulations.libreV2.AppUtils
import com.cumulations.libreV2.LocationPermissionCallback
import com.cumulations.libreV2.SharedPreferenceHelper
import com.cumulations.libreV2.WifiUtil
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEUtils
import com.cumulations.libreV2.fragments.CTActiveDevicesFragment
import com.cumulations.libreV2.fragments.CTDeviceSetupInfoFragment
import com.cumulations.libreV2.fragments.CTNoDeviceFragment
import com.cumulations.libreV2.fragments.CTNoWifiFragment
import com.cumulations.libreV2.fragments.CTSettingsFragment
import com.cumulations.libreV2.fragments.CTTutorialsFragment
import com.cumulations.libreV2.model.SceneObject
import com.cumulations.libreV2.removeShiftMode
import com.cumulations.libreV2.tcp_tunneling.TunnelingData
import com.cumulations.libreV2.tcp_tunneling.TunnelingFragmentListener
import com.libreAlexa.LibreApplication
import com.libreAlexa.R
import com.libreAlexa.Scanning.ScanThread
import com.libreAlexa.Scanning.ScanningHandler
import com.libreAlexa.constants.AppConstants
import com.libreAlexa.constants.Constants
import com.libreAlexa.databinding.CtActivityHomeTabsBinding
import com.libreAlexa.luci.LSSDPNodeDB
import com.libreAlexa.luci.LSSDPNodes
import com.libreAlexa.netty.LibreDeviceInteractionListner
import com.libreAlexa.netty.NettyData
import com.libreAlexa.util.LibreLogger


class CTHomeTabsActivity : CTDeviceDiscoveryActivity(), LibreDeviceInteractionListner,
    LocationPermissionCallback {
    private var wifiUtil: WifiUtil? = null
    private var tabSelected: String = ""
    private var loadFragmentName: String? = null
    private var isDoubleTap: Boolean = false
    private var speakerNode: LSSDPNodes? = null
    private var alertDialog: AlertDialog? = null
    private var isLocationGranted: Boolean? = null
    private var isBTTurnedOn: Boolean? = null
    private var isGPSOn: Boolean? = null
    private lateinit var sharedPreference: SharedPreferenceHelper
    private val mTaskHandlerForSendingMSearch = Handler(Looper.getMainLooper())
    private val mMyTaskRunnableForMSearch = Runnable {
        showLoader(false)
        ScanThread.getInstance().UpdateNodes()


        if (!wifiUtil?.isWifiOn()!!) {
            openFragment(CTNoWifiFragment::class.java.simpleName, animate = false)
            return@Runnable
        }

        if (LSSDPNodeDB.getInstance().GetDB().size <= 0) {
            openFragment(CTNoDeviceFragment::class.java.simpleName, animate = false)
        } else {
            openFragment(CTActiveDevicesFragment::class.java.simpleName, animate = false)
        }
    }

    private var isActivityVisible = true
    private var tunnelingFragmentListener: TunnelingFragmentListener? = null
    private var otherTabClicked = false
    private lateinit var binding: CtActivityHomeTabsBinding
    private val TAG = CTHomeTabsActivity::class.java.name
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CtActivityHomeTabsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        setListeners()
        sharedPreference = SharedPreferenceHelper.getInstance(this)
        wifiUtil = WifiUtil(this)
        if (!wifiUtil?.isWifiOn()!!) {
            openFragment(CTNoWifiFragment::class.java.simpleName, animate = false)
            return
        }

        loadFragmentName = intent?.getStringExtra(AppConstants.LOAD_FRAGMENT)
        if (loadFragmentName == null) {
            loadFragmentName = if (LSSDPNodeDB.getInstance().GetDB().size > 0) {
                CTActiveDevicesFragment::class.java.simpleName
            } else {
                CTNoDeviceFragment::class.java.simpleName
            }
        }

        openFragment(loadFragmentName!!, animate = false)
    }


    override fun onStart() {
        super.onStart()
        isActivityVisible = true
        when {
            LibreApplication.isSacFlowStarted -> {
                binding.bottomNavigation.selectedItemId = R.id.action_discover
                LibreApplication.isSacFlowStarted = false
            }

            tabSelected == CTActiveDevicesFragment::class.java.simpleName -> {
                if (supportFragmentManager.findFragmentByTag(tabSelected) == null)
                    return
                val ctActiveDevicesFragment = supportFragmentManager.findFragmentByTag(tabSelected) as CTActiveDevicesFragment
                ctActiveDevicesFragment.updateFromCentralRepositryDeviceList()
            }

            tabSelected != CTDeviceSetupInfoFragment::class.java.simpleName -> binding.bottomNavigation.selectedItemId = R.id.action_discover
        }
        // checkLocationPermission(3)
    }

    private fun showBottomSheet() {
        isLocationGranted = AppUtils.isPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION)
        isBTTurnedOn = BLEUtils.checkBluetooth(this@CTHomeTabsActivity)
        isGPSOn = AppUtils.isLocationServiceEnabled(this@CTHomeTabsActivity)
        LibreLogger.d(TAG_, "isLocationGranted:- $isLocationGranted")
        LibreLogger.d(TAG_, "isBTTurnedOn:- $isBTTurnedOn")
        LibreLogger.d(TAG_, "isNearByDevices Granted:- " + checkPermissionsGranted())
        LibreLogger.d(TAG_, "isGpsOn:- $isGPSOn")

        /**
         * ===IF Condition====
         * If All the permissions are granted hide the UI
         * ===ELSE Condition====
         * If Phone BT is turned on disable the click and grey out
         */
        val currentAPIVersion = Build.VERSION.SDK_INT
        if (currentAPIVersion >= 31) {
            if (isLocationGranted!! && isBTTurnedOn!! && checkPermissionsGranted() && isGPSOn == true) {
                LibreLogger.d(TAG_, "Android above 12 all granted")
                binding.layPermissionBottom.visibility = View.GONE
            } else {
                binding.layPermissionBottom.visibility = View.VISIBLE
                if (isBTTurnedOn == true) {
                    LibreLogger.d(TAG_, "Android above 12 BT not granted")
                    binding.layPermissionBottomSheet.layBluetooth.isClickable = false
                    binding.layPermissionBottomSheet.layBluetooth.isEnabled = false
                    binding.layPermissionBottomSheet.layBluetooth.alpha = 0.5.toFloat()
                    binding.layPermissionBottomSheet.imgBtToggle.setImageDrawable(getDrawable(R.drawable.check_orange))
                } else if (isLocationGranted!! && checkPermissionsGranted() && isGPSOn == true) {
                    LibreLogger.d(TAG_, "Android above 12 GPS,AppLocation, NearByDevices not granted")
                    binding.layPermissionBottomSheet.layLocation.isClickable = false
                    binding.layPermissionBottomSheet.layLocation.isEnabled = false
                    binding.layPermissionBottomSheet.layLocation.alpha = 0.5.toFloat()
                    binding.layPermissionBottomSheet.imgLocToggle.setImageDrawable(getDrawable(R.drawable.check_orange))
                } else {
                    LibreLogger.d(TAG_, "Android above 12 anyone Permission Missed " +
                            "App Location:- $isLocationGranted" +
                            "NearByDevices:- ${checkPermissionsGranted()}" +
                            "PhoneGps:- $isGPSOn" +
                            "isBTTurnedOn:- $isBTTurnedOn")
                }
            }
        } else {
            if (isLocationGranted!! && isBTTurnedOn!! && isGPSOn == true) {
                LibreLogger.d(TAG_, "Android below 12 all granted")
                binding.layPermissionBottom.visibility = View.GONE
            } else {
                if (isBTTurnedOn == true) {
                    LibreLogger.d(TAG_, "Android below 12 BT Not granted")
                    binding.layPermissionBottomSheet.layBluetooth.isClickable = false
                    binding.layPermissionBottomSheet.layBluetooth.isEnabled = false
                    binding.layPermissionBottomSheet.layBluetooth.alpha = 0.5.toFloat()
                    binding.layPermissionBottomSheet.imgBtToggle.setImageDrawable(getDrawable(R.drawable.check_orange))
                } else if (isLocationGranted!! && isGPSOn == true) {
                    LibreLogger.d(TAG_, "Android below 12 GPS,AppLocation not granted")
                    binding.layPermissionBottomSheet.layLocation.isClickable = false
                    binding.layPermissionBottomSheet.layLocation.isEnabled = false
                    binding.layPermissionBottomSheet.layLocation.alpha = 0.5.toFloat()
                    binding.layPermissionBottomSheet.imgLocToggle.setImageDrawable(getDrawable(R.drawable.check_orange))
                } else {
                    LibreLogger.d(TAG_, "below below 12 anyone Permission Missed " +
                            "App Location:- $isLocationGranted" +
                            "NearByDevices:- ${checkPermissionsGranted()}" +
                            "PhoneGps:- $isGPSOn" +
                            "isBTTurnedOn:- $isBTTurnedOn")

                }
            }
        }
    }

    fun toggleStopAllButtonVisibility() {
        if (AppUtils.isAnyDevicePlaying())
            binding.ivStopAll.visibility = View.VISIBLE
        else {
            binding.ivStopAll.visibility = View.GONE
        }
    }
    private fun setListeners() {
        var fragmentToLoad: Fragment? = null
        binding.bottomNavigation.setOnNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.action_discover -> {

                    if (!wifiUtil?.isWifiOn()!! /*&& isNetworkOffCallBackEnabled*/) {
                        openFragment(CTNoWifiFragment::class.java.simpleName, animate = false)
                        return@setOnNavigationItemSelectedListener true
                    }

                    binding.ivRefresh.visibility = View.VISIBLE
                    otherTabClicked = false
                    refreshDevices()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.action_add -> {
                    otherTabClicked = true
                    //Checking Location Permission before going to Setup Screen
                    val fineLocationPermission = AppUtils.isPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    if (fineLocationPermission) {
                        val intent = Intent(this, CTBluetoothDeviceListActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        checkLocationPermission()
                    }
                }

                R.id.action_tutorial -> {
                    otherTabClicked = true
                    binding.ivRefresh.visibility = View.GONE
                    fragmentToLoad = CTTutorialsFragment()
                }

                R.id.action_settings -> {
                    otherTabClicked = true
                    binding.ivRefresh.visibility = View.GONE
                    fragmentToLoad = CTSettingsFragment()
                }
            }


            mTaskHandlerForSendingMSearch.removeCallbacks(mMyTaskRunnableForMSearch)
            if (fragmentToLoad == null) {
                false
            } else {
                openFragment(fragmentToLoad!!::class.java.simpleName, animate = true)
                true
            }
        }

        binding.ivRefresh.setOnClickListener {
            refreshDevices()
        }

        binding.ivStopAll.setOnClickListener {
            AppUtils.stopAllDevicesPlaying()
        }

        binding.layPermissionBottomSheet.btnSetupLater.setOnClickListener {
            binding.layPermissionBottom.visibility = View.GONE
        }
        binding.layPermissionBottomSheet.layLocation.setOnClickListener {
            checkLocationPermission()
        }
        binding.layPermissionBottomSheet.layBluetooth.setOnClickListener {
            isLocationGranted = AppUtils.isPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION)
            val currentAPIVersion = Build.VERSION.SDK_INT
            if (currentAPIVersion >= 31) {
                if (isLocationGranted == true && checkPermissionsGranted()) {
                    if (!BLEUtils.checkBluetooth(this@CTHomeTabsActivity)) {
                        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        customStartActivityForResult(AppConstants.BT_ENABLED_REQUEST_CODE, enableBtIntent)
                    } else {
                        showToast(getString(R.string.bluetooth_is_already_turned_on))
                    }
                } else {
                    showToast(getString(R.string.please_grant_the_required_location_permissions))
                }
            } else {
                if (isLocationGranted == true) {
                    if (!BLEUtils.checkBluetooth(this@CTHomeTabsActivity)) {
                        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        customStartActivityForResult(AppConstants.BT_ENABLED_REQUEST_CODE, enableBtIntent)
                    } else {
                        showToast(getString(R.string.bluetooth_is_already_turned_on))
                    }
                } else {
                    showToast(getString(R.string.please_grant_the_required_location_permissions))
                }
            }
        }

    }

    private fun initViews() {
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        removeShiftMode(binding.bottomNavigation)
    }

    private fun showLoader(show: Boolean) {
        if (show) binding.progressBar.visibility = View.VISIBLE else binding.progressBar.visibility = View.GONE
    }

    private fun removeAllFragments() {
        for (fragment in supportFragmentManager.fragments) {
            try {
                supportFragmentManager.beginTransaction().remove(fragment).commit()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refreshDevices() {
        showLoader(true)
        removeAllFragments()
        clearBatteryInfoForDevices()
        ScanThread.getInstance().UpdateNodes()
        /*Send m-search packet after 5 seconds*/
        mTaskHandlerForSendingMSearch.postDelayed(mMyTaskRunnableForMSearch, Constants.LOADING_TIMEOUT.toLong())
        showScreenAfterDelay()
    }

    private fun showScreenAfterDelay() {
        Handler(Looper.myLooper()!!).postDelayed({
            if (otherTabClicked)
                return@postDelayed

            runOnUiThread {
                val sceneKeySet = ScanningHandler.getInstance().sceneObjectMapFromRepo.keys.toTypedArray()

                LibreLogger.d(TAG, "showScreenAfterDelay, sceneKeySet size = ${sceneKeySet.size}")
//                if(speakerNode!=null) {
//                    requestLuciUpdates(speakerNode!!.ip)
//                }
                if (LSSDPNodeDB.getInstance().GetDB().size > 0) {
                    openFragment(CTActiveDevicesFragment::class.java.simpleName, animate = false)
                } /*else {
                    openFragment(CTNoDeviceFragment::class.java.simpleName,animate = false)
                }*/
            }
        }, 2000)
    }

    fun openFragment(fragmentClassName: String, animate: Boolean) {
        var fragment: Fragment? = null
        binding.ivRefresh.visibility = View.VISIBLE
        when (fragmentClassName) {
            CTNoDeviceFragment::class.java.simpleName -> {
                fragment = CTNoDeviceFragment()
                binding.bottomNavigation.menu.getItem(0).isChecked = true
            }

            CTActiveDevicesFragment::class.java.simpleName -> {
                fragment = CTActiveDevicesFragment()
                binding.bottomNavigation.menu.getItem(0).isChecked = true
            }

            CTDeviceSetupInfoFragment::class.java.simpleName -> {
                binding.ivRefresh.visibility = View.GONE
                fragment = CTDeviceSetupInfoFragment()
                binding.bottomNavigation.menu.getItem(1).isChecked = true

            }

            CTNoWifiFragment::class.java.simpleName -> {
                binding.ivRefresh.visibility = View.GONE
                fragment = CTNoWifiFragment()
                binding.bottomNavigation.menu.getItem(0).isChecked = true
            }

            CTTutorialsFragment::class.java.simpleName -> {
                binding.ivRefresh.visibility = View.GONE
                fragment = CTTutorialsFragment()
                binding.bottomNavigation.menu.getItem(2).isChecked = true
            }

            CTSettingsFragment::class.java.simpleName -> {
                binding.ivRefresh.visibility = View.GONE
                fragment = CTSettingsFragment()
                binding.bottomNavigation.menu.getItem(3).isChecked = true
            }
        }
        loadFragment(fragment, animate)
    }

    private fun loadFragment(fragment: Fragment?, animate: Boolean): Boolean {
        //switching fragment
        if (fragment != null && isActivityVisible) {
            try {
                supportFragmentManager
                    .beginTransaction()
                    .apply {
                        if (animate) setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        replace(binding.flContainer.id, fragment, fragment::class.java.simpleName)
                        commit()
                    }
                tabSelected = fragment::class.java.simpleName
            } catch (e: Exception) {
                e.printStackTrace()
                LibreLogger.d(TAG, "loadFragment exception ${e.message}")
            }
            return true
        }
        return false
    }

    override fun wifiConnected(connected: Boolean) {
        /*making method to be called in parent activity as well*/
        super.wifiConnected(connected)
        /*Avoid changing when activity is not visible i.e when user goes to wifi settings
        * and stays in that screen for a while*/
        if (!isActivityVisible || tabSelected == CTDeviceSetupInfoFragment::class.java.simpleName || LibreApplication.isSacFlowStarted)
            return
        if (connected) {
            refreshDevices()
        } else {
            openFragment(CTNoWifiFragment::class.java.simpleName, animate = false)
        }
    }

    override fun deviceDiscoveryAfterClearingTheCacheStarted() {}

    override fun newDeviceFound(node: LSSDPNodes?) {
        LibreLogger.d(TAG, "newDeviceFound  CTHomeTABS${node?.friendlyname}")
        mTaskHandlerForSendingMSearch.removeCallbacks(mMyTaskRunnableForMSearch)
        speakerNode = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(node!!.ip)

        val sceneKeySet = ScanningHandler.getInstance().sceneObjectMapFromRepo.keys.toTypedArray()
        LibreLogger.d(TAG, "sceneKeySet size = ${sceneKeySet.size}")
        if (/*sceneKeySet.isNotEmpty()*/LSSDPNodeDB.getInstance().GetDB().size > 0) {
            openFragment(CTActiveDevicesFragment::class.java.simpleName, animate = false)
        }
    }

    override fun deviceGotRemoved(ipaddress: String?) {
    }

    override fun messageRecieved(packet: NettyData?) {
    }

    override fun onStop() {
        super.onStop()
        isActivityVisible = false
        unregisterLocationPermissionCallback()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        ensureDMRPlaybackStopped()
        killApp()
    }

    fun setTunnelFragmentListener(tunnelingFragmentListener: TunnelingFragmentListener) {
        this.tunnelingFragmentListener = tunnelingFragmentListener
    }

    fun removeTunnelFragmentListener() {
        tunnelingFragmentListener = null
    }

    override fun tunnelDataReceived(tunnelingData: TunnelingData) {
        super.tunnelDataReceived(tunnelingData)
        tunnelingFragmentListener?.onFragmentTunnelDataReceived(tunnelingData)
    }

    private fun clearBatteryInfoForDevices() {
        ScanningHandler.getInstance().sceneObjectMapFromRepo.forEach { (ip: String?, sceneObject: SceneObject?) ->
            LibreLogger.d(TAG, "clearBatteryInfoForDevices device ${sceneObject.sceneName}")
            sceneObject?.clearBatteryStats()
        }
    }

    override fun onPermissionGranted() {
        isGPSOn= AppUtils.isLocationServiceEnabled(this@CTHomeTabsActivity)
        val currentAPIVersion = Build.VERSION.SDK_INT
        if (currentAPIVersion >= 31) {
            if (!checkPermissionsGranted()) {
                LibreLogger.d(TAG_, "onPermissionGranted Android above 12 NearBydDevices not granted")
                checkPermissions()
            }
        } else {
            isLocationGranted = AppUtils.isPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION)
            if(isGPSOn==true && checkPermissionsGranted() && isLocationGranted==true){
                LibreLogger.d(TAG_, "onPermissionGranted Android below GPS and NearByDevice granted")
                binding.layPermissionBottom.visibility=View.GONE
                binding.layPermissionBottomSheet.imgLocToggle.setImageDrawable(getDrawable(R.drawable.check_orange))
            }/*else if (BLEUtils.checkBluetooth(this@CTHomeTabsActivity)) {
                LibreLogger.d(TAG_, "onPermissionGranted Android below 12 BT granted")
                binding.layPermissionBottom.visibility = View.VISIBLE
                binding.layPermissionBottomSheet.imgBtToggle.setImageDrawable(getDrawable(R.drawable.check_orange))
            }*//* else{
               // checkPermissions()
                showLocationMustBeEnabledDialog()
                LibreLogger.d(TAG_, "onPermissionGranted Android below 12 GPS,LOC,NFC not granted")
            }*/
        }
    }

    override fun onPermissionDenied() {
        LibreLogger.d(TAG_, "onPermissionDenied called")
        binding.layPermissionBottom.visibility = View.VISIBLE
        binding.layPermissionBottomSheet.imgLocToggle.setImageDrawable(getDrawable(R.drawable.img_toggle))
    }

    override fun onGPSGranted() {
        LibreLogger.d(TAG_, "onGPSGranted called")
    }

    override fun onResume() {
        super.onResume()
        showBottomSheet()
        registerLocationPermissionCallback(this@CTHomeTabsActivity)
    }

    override fun customOnActivityResult(data: Intent?, requestCode: Int, resultCode: Int) {
        super.customOnActivityResult(data, requestCode, resultCode)
        if (requestCode == AppConstants.OPEN_APP_SETTINGS) {
            if (resultCode == RESULT_OK) {
                /** The user may have granted the permission in settings
                 *  You can check again if the permission is granted here
                 */
                LibreLogger.d(TAG_, "Permissions 5 OPEN_APP_SETTINGS ")
            }

        } else if (requestCode == AppConstants.BT_ENABLED_REQUEST_CODE) {
            /**
             * The Below Source Code will take the user to BT Settings screen, now default BT
             * Enable Alert Box is triggering so we don't need to call the below intent and
             * as well as don't need to handle the result also
             * Note:- Most of the BLE Apps and smart watch apps are doing same
             */
            if (resultCode == RESULT_OK) {
                isLocationGranted = AppUtils.isPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION)
                if (isLocationGranted!! && checkPermissionsGranted()) {
                    binding.layPermissionBottom.visibility = View.GONE
                } else {
                    val currentAPIVersion = Build.VERSION.SDK_INT
                    if (currentAPIVersion >= 31) {
                        binding.layPermissionBottom.visibility = View.VISIBLE
                        binding.layPermissionBottomSheet.imgBtToggle.setImageDrawable(getDrawable(R.drawable.check_orange))
                    } else {
                        LibreLogger.d(TAG_, "customOnActivityResult below 12")
                        binding.layPermissionBottom.visibility = View.GONE
                       /* binding.layPermissionBottom.visibility = View.VISIBLE
                        binding.layPermissionBottomSheet.imgBtToggle.setImageDrawable(getDrawable(R.drawable.check_orange))*/
                    }
                }

            } else {
                LibreLogger.d(TAG_, "customOnActivityResult inside BT_ENABLED_REQUEST_CODE else " +
                        "condition")
            }
        } else {
            if (isLocationGranted == false) {
                LibreLogger.d(TAG_, "customOnActivityResult BT_ENABLED_REQUEST_CODE outside if " +
                        "condition")
            } else {
                checkPermissions()
                LibreLogger.d(TAG_, "customOnActivityResult BT_ENABLED_REQUEST_CODE outside else " +
                        "condition")
            }
        }
    }

    private fun checkPermissions() {
        val currentAPIVersion = Build.VERSION.SDK_INT
        if (currentAPIVersion >= 31) {
            if (!checkPermissionsGranted()) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_SCAN)) {
                    /**
                     * Request the Permission with Rationale with educating the user
                     */
                    LibreLogger.d(TAG_, "checkPermissions above 12 LOC show Rationale ")
                    showAlertDialog(getString(R.string.permission_required), getString(R.string.ok), getString(R.string.cancel), 0)
                } else {
                    /**
                     *  Request the permission directly, without explanation first time launch
                     */
                    LibreLogger.d(TAG_, "checkPermissions above 12 LOC explain to user ")
                    requestPermissionLauncher.launch(CTBluetoothDeviceListActivity.permissions)
                }
            } else {
                /**
                 *  Permission is already granted, proceed with your logic
                 */
                LibreLogger.d(TAG_, "checkPermissions above 12 LOC granted ")
                if (alertDialog != null && alertDialog!!.isShowing) alertDialog?.dismiss()

            }
        }else{
            if (isLocationGranted!! && isBTTurnedOn!! && isGPSOn==true){
                LibreLogger.d(TAG_, "checkPermissions below 12 BT&LOC not granted ")
                binding.layPermissionBottom.visibility = View.GONE
            }else{
               /* if (isBTTurnedOn == true) {
                    LibreLogger.d(TAG_, "checkPermissions below 12 BT not granted ")
                    binding.layPermissionBottomSheet.layBluetooth.isClickable = false
                    binding.layPermissionBottomSheet.layBluetooth.isEnabled = false
                    binding.layPermissionBottomSheet.layBluetooth.alpha = 0.5.toFloat()
                    binding.layPermissionBottomSheet.imgBtToggle.setImageDrawable(getDrawable(R.drawable.check_orange))
                }else*/
                if(isGPSOn==true && isLocationGranted!!) {
                    LibreLogger.d(TAG_, "checkPermissions below 12 LOC  and GPS not granted ")
                    binding.layPermissionBottomSheet.layLocation.isClickable = false
                    binding.layPermissionBottomSheet.layLocation.isEnabled = false
                    binding.layPermissionBottomSheet.layLocation.alpha = 0.5.toFloat()
                    binding.layPermissionBottomSheet.imgLocToggle.setImageDrawable(getDrawable(R.drawable.check_orange))
                }else{
                    showLocationMustBeEnabledDialog()
                    LibreLogger.d(TAG_, "checkPermissions below 12 some permissions is missing ")
                }
            }
        }
    }

    private fun checkPermissionsGranted(): Boolean {
        // Iterate through the permissions and check if any of them are not granted
        for (permission in CTBluetoothDeviceListActivity.permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
    { result ->
        var allGranted = true
        for (isGranted in result.values) {
            allGranted = allGranted && isGranted
        }
        if (allGranted) {
            /**
             * If BT already turned on and enabled location no need to show the
             * location UI
             */
            if (allGranted && isBTTurnedOn == true) {
                LibreLogger.d(TAG_, "requestPermissionLauncher above 12 NearByDevices & BT granted ")
                binding.layPermissionBottom.visibility = View.GONE
            } else if(isGPSOn==true){
                LibreLogger.d(TAG_, "requestPermissionLauncher above 12 GPS granted ")
                isLocationGranted = true
                binding.layPermissionBottomSheet.layLocation.alpha = 0.5.toFloat()
                binding.layPermissionBottom.visibility = View.VISIBLE
                binding.layPermissionBottomSheet.imgLocToggle.setImageDrawable(getDrawable(R.drawable.check_orange))
                if (alertDialog != null && alertDialog!!.isShowing) alertDialog?.dismiss()
            }else{
                LibreLogger.d(TAG_, "requestPermissionLauncher above 12 GPS not granted ")
                showLocationMustBeEnabledDialog()
            }
        } else {
            if (!sharedPreference.isFirstTimeAskingPermission(Manifest.permission.BLUETOOTH_SCAN)) {
                sharedPreferenceHelper.firstTimeAskedPermission(Manifest.permission.BLUETOOTH_SCAN, true)
                LibreLogger.d(TAG_, "requestPermissionLauncher above 12 nearByDevices not  1st " +
                        "time ")
                showAlertDialog(getString(R.string.permission_required), getString(R.string.ok), getString(R.string.cancel), 0)
            } else {
                /**
                 * This will take care of user didn't give NFC permission
                 */
                LibreLogger.d(TAG_, "requestPermissionLauncher above 12 nearByDevices not granted" +
                        "2nd time")
                showAlertDialog(getString(R.string.permission_denied), getString(R.string.open_settings), getString(R.string.cancel), 100)
            }

        }

    }

    private fun showAlertDialog(message: String,
        positiveButtonString: String,
        negativeButtonString: String,
        requestCode: Int) {
        if (alertDialog != null && alertDialog!!.isShowing) alertDialog!!.dismiss()
        else alertDialog = null

        if (alertDialog == null) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(message)
            builder.setMessage(getString(R.string.permission_connect))
            builder.setCancelable(false)
            builder.setPositiveButton(positiveButtonString) { dialogInterface, i ->
                alertDialog!!.dismiss()
                if (requestCode == 0) {
                    LibreLogger.d(TAG_, "showAlertDialog requestCode 0 ")
                    requestPermissionLauncher.launch(CTBluetoothDeviceListActivity.permissions)
                } else {
                    LibreLogger.d(TAG_, "showAlertDialog requestCode open AppSettings ")
                    /**
                     * Open App Settings if user don't allow the permission
                     */
                    val packageName = packageName
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.fromParts("package", packageName, null)
                    customStartActivityForResult(AppConstants.OPEN_APP_SETTINGS, intent)
                }
            }
            /**
             * Reserved for future -> Negative Button is commented because if any customer or
             * Requirement come we can use the below code
             */
            /* builder.setNegativeButton(negativeButtonString) { dialogInterface, i ->
                 alertDialog!!.dismiss()
             }*/
            alertDialog = builder.create()
        }
        if (!alertDialog!!.isShowing) alertDialog!!.show()

    }

}
