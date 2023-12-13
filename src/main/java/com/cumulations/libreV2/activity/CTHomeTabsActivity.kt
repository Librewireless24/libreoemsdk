package com.cumulations.libreV2.activity

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
        isLocationGranted = AppUtils.isPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION)
        isBTTurnedOn = BLEUtils.checkBluetooth(this@CTHomeTabsActivity)
        LibreLogger.d(TAG_, "Permissions isLocationGranted $isLocationGranted")
        LibreLogger.d(TAG_, "Permissions isBTTurnedOn $isBTTurnedOn")
        LibreLogger.d(TAG_, "Permissions checkPermissionsGranted " + checkPermissionsGranted())

        /**
         * ===IF Condition====
         * If All the permissions are granted hide the UI
         * ===ELSE Condition====
         * If Phone BT is turned on disable the click and grey out
         */
        if (isLocationGranted!! && isBTTurnedOn!! && checkPermissionsGranted()) {
            binding.layPermissionBottom.visibility = View.GONE
        } else {
            LibreLogger.d(TAG_, "Permissions if")
            binding.layPermissionBottom.visibility = View.VISIBLE
            if (isBTTurnedOn == true) {
                LibreLogger.d(TAG_, "Permissions else onstart")
                binding.layPermissionBottomSheet.layBluetooth.isClickable = false
                binding.layPermissionBottomSheet.layBluetooth.isEnabled = false
                binding.layPermissionBottomSheet.layBluetooth.alpha = 0.5.toFloat()
                binding.layPermissionBottomSheet.imgBtToggle.setImageDrawable(getDrawable(R.drawable.check_orange))
            } else if (isLocationGranted!! && checkPermissionsGranted()) {
                binding.layPermissionBottomSheet.layLocation.isClickable = false
                binding.layPermissionBottomSheet.layLocation.isEnabled = false
                binding.layPermissionBottomSheet.layLocation.alpha = 0.5.toFloat()
                binding.layPermissionBottomSheet.imgLocToggle.setImageDrawable(getDrawable(R.drawable.check_orange))
                LibreLogger.d(TAG_, "Permissions else")
            } else {
                LibreLogger.d(TAG_, "Permissions last else")
            }
        }
        // checkLocationPermission(3)
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
        if (!checkPermissionsGranted()) {
            checkPermissions()
        } else {
            if (BLEUtils.checkBluetooth(this@CTHomeTabsActivity)) {
                binding.layPermissionBottom.visibility = View.VISIBLE
                binding.layPermissionBottomSheet.imgBtToggle.setImageDrawable(getDrawable(R.drawable.check_orange))
            } else {
                binding.layPermissionBottom.visibility = View.VISIBLE
                binding.layPermissionBottomSheet.imgLocToggle.setImageDrawable(getDrawable(R.drawable.check_orange))
            }
        }
    }

    override fun onPermissionDenied() {
        binding.layPermissionBottom.visibility = View.VISIBLE
        binding.layPermissionBottomSheet.imgLocToggle.setImageDrawable(getDrawable(R.drawable.img_toggle))
    }

    override fun onResume() {
        super.onResume()
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
                    LibreLogger.d(TAG_, "Permissions 6  BT_ENABLED_REQUEST_CODE RESULT_OK  ")
                    binding.layPermissionBottom.visibility = View.VISIBLE
                    binding.layPermissionBottomSheet.imgBtToggle.setImageDrawable(getDrawable(R.drawable.check_orange))
                }

            } else {
                LibreLogger.d(TAG_, "Permissions 7 BT_ENABLED_REQUEST_CODE NOT RESULT_OK")
            }
        } else {
            if (isLocationGranted == false) {
                LibreLogger.d(TAG_, "Permissions 8 RequestCode are not handled")
            } else {
                checkPermissions()
                LibreLogger.d(TAG_, "Permissions 88 RequestCode are not handled")
            }
        }
    }

    private fun checkPermissions() {
        if (!checkPermissionsGranted()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_SCAN)) {
                /**
                 * Request the Permission with Rationale with educating the user
                 */
                LibreLogger.d(TAG_, "Permissions 9 RequestCode are not handled")
                showAlertDialog(getString(R.string.permission_required), getString(R.string.ok), getString(R.string.cancel), 0)
            } else {
                /**
                 *  Request the permission directly, without explanation first time launch
                 */
                LibreLogger.d(TAG_, "Permissions 10 RequestCode are not handled")
                requestPermissionLauncher.launch(CTBluetoothDeviceListActivity.permissions)
            }
        } else {
            /**
             *  Permission is already granted, proceed with your logic
             */
            LibreLogger.d(TAG_, "Permissions 11 RequestCode are not handled")
            if (alertDialog != null && alertDialog!!.isShowing) alertDialog?.dismiss()

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
            LibreLogger.d(TAG_, "Permissions 12 ")
            /**
             * If BT already turned on and enabled location no need to show the
             * location UI
             */
            if (allGranted && isBTTurnedOn == true) {
                LibreLogger.d(TAG_, "Permissions 122 ")
                binding.layPermissionBottom.visibility = View.GONE
            } else {
                LibreLogger.d(TAG_, "Permissions 123 ")
                isLocationGranted = true
                binding.layPermissionBottomSheet.layLocation.alpha = 0.5.toFloat()
                binding.layPermissionBottom.visibility = View.VISIBLE
                binding.layPermissionBottomSheet.imgLocToggle.setImageDrawable(getDrawable(R.drawable.check_orange))
                if (alertDialog != null && alertDialog!!.isShowing) alertDialog?.dismiss()
            }
        } else {
            if (!sharedPreference.isFirstTimeAskingPermission(Manifest.permission.BLUETOOTH_SCAN)) {
                sharedPreferenceHelper.firstTimeAskedPermission(Manifest.permission.BLUETOOTH_SCAN, true)
                LibreLogger.d(TAG_, "Permissions 13 ")
                showAlertDialog(getString(R.string.permission_required), getString(R.string.ok), getString(R.string.cancel), 0)
            } else {
                /**
                 * This will take care of user didn't give NFC permission
                 */
                LibreLogger.d(TAG_, "Permissions 14 ")
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
                    LibreLogger.d(TAG_, "Permissions 15 RequestCode are not handled")
                    requestPermissionLauncher.launch(CTBluetoothDeviceListActivity.permissions)
                } else {
                    LibreLogger.d(TAG_, "Permissions 16 RequestCode are not handled")
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
