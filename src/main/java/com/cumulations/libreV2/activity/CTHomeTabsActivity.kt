package com.cumulations.libreV2.activity

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import com.cumulations.libreV2.AppUtils
import com.cumulations.libreV2.WifiUtil
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEUtils
import com.cumulations.libreV2.fragments.*
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


class CTHomeTabsActivity : CTDeviceDiscoveryActivity(),LibreDeviceInteractionListner {
    private var wifiUtil:WifiUtil? = null
    private var tabSelected: String = ""
    private var loadFragmentName:String? = null
    private var isDoubleTap: Boolean = false

    private val mTaskHandlerForSendingMSearch = Handler(Looper.getMainLooper())
    private val mMyTaskRunnableForMSearch = Runnable {
        showLoader(false)
        ScanThread.getInstance().UpdateNodes()


        if (!wifiUtil?.isWifiOn()!!) {
            openFragment(CTNoWifiFragment::class.java.simpleName,animate = false)
            return@Runnable
        }

        if (LSSDPNodeDB.getInstance().GetDB().size <= 0) {
            openFragment(CTNoDeviceFragment::class.java.simpleName,animate = false)
        } else {
            openFragment(CTActiveDevicesFragment::class.java.simpleName,animate = false)
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

        wifiUtil = WifiUtil(this)
        if (!wifiUtil?.isWifiOn()!!) {
            openFragment(CTNoWifiFragment::class.java.simpleName,animate = false)
            return
        }

        loadFragmentName = intent?.getStringExtra(AppConstants.LOAD_FRAGMENT)
        if(loadFragmentName == null){
            loadFragmentName = if (LSSDPNodeDB.getInstance().GetDB().size > 0) {
                CTActiveDevicesFragment::class.java.simpleName
            } else {
                CTNoDeviceFragment::class.java.simpleName
            }
        }

        openFragment(loadFragmentName!!,animate = false)
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
            tabSelected!=CTDeviceSetupInfoFragment::class.java.simpleName -> binding.bottomNavigation.selectedItemId = R.id.action_discover
        }
        checkLocationPermission()
    }

    fun toggleStopAllButtonVisibility() {
        if (AppUtils.isAnyDevicePlaying())
           binding.ivStopAll.visibility = View.VISIBLE
        else{
            binding.ivStopAll.visibility = View.GONE
        }
    }

    private fun setListeners() {
        val bundle: Bundle = Bundle()
        var fragmentToLoad: Fragment? = null
        binding.bottomNavigation.setOnNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.action_discover -> {

                    if (!wifiUtil?.isWifiOn()!! /*&& isNetworkOffCallBackEnabled*/) {
                        openFragment(CTNoWifiFragment::class.java.simpleName,animate = false)
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
                    val fineLocationPermission=AppUtils.isPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        if(fineLocationPermission) {
                            val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
                            val mBluetoothAdapter = bluetoothManager!!.adapter
                            if (!BLEUtils.checkBluetooth(mBluetoothAdapter)) {
                                val intent = Intent(this, CTBluetoothSetupInstructionsActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                val intent = Intent(this, CTBluetoothDeviceListActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }else{
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
            if (fragmentToLoad == null){
                false
            } else {
                openFragment(fragmentToLoad!!::class.java.simpleName,animate = true)
                true
            }
        }

        binding.ivRefresh.setOnClickListener {
            refreshDevices()
        }

        binding.ivStopAll.setOnClickListener {
            AppUtils.stopAllDevicesPlaying()
        }
    }

    private fun initViews() {
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        removeShiftMode(binding.bottomNavigation)
    }

    private fun showLoader(show:Boolean){
        if (show) binding.progressBar.visibility = View.VISIBLE else binding.progressBar.visibility = View.GONE
    }

    private fun removeAllFragments(){
        for (fragment in supportFragmentManager.fragments) {
            try {
                supportFragmentManager.beginTransaction().remove(fragment).commit()
            } catch (e:Exception){
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

    private fun showScreenAfterDelay(){
        Handler(Looper.myLooper()!!).postDelayed({
            if (otherTabClicked)
                return@postDelayed

            runOnUiThread {
                val sceneKeySet = ScanningHandler.getInstance().sceneObjectMapFromRepo.keys.toTypedArray()
                LibreLogger.d(TAG,"showScreenAfterDelay, sceneKeySet size = ${sceneKeySet.size}")
                if (LSSDPNodeDB.getInstance().GetDB().size > 0) {
                    openFragment(CTActiveDevicesFragment::class.java.simpleName,animate = false)
                } /*else {
                    openFragment(CTNoDeviceFragment::class.java.simpleName,animate = false)
                }*/
            }
        },2000)
    }

    fun openFragment(fragmentClassName:String,animate:Boolean){
        var fragment: Fragment? = null
        binding.ivRefresh.visibility = View.VISIBLE
        when(fragmentClassName){
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
        loadFragment(fragment,animate)
    }

    private fun loadFragment(fragment: Fragment?,animate: Boolean): Boolean {
        //switching fragment
        if (fragment != null && isActivityVisible) {
            try {
                supportFragmentManager
                        .beginTransaction()
                        .apply {
                            if (animate) setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left)
                            replace(binding.flContainer.id, fragment,fragment::class.java.simpleName)
                            commit()
                        }
                tabSelected = fragment::class.java.simpleName
            } catch (e:Exception){
                e.printStackTrace()
                LibreLogger.d(TAG,"loadFragment exception ${e.message}")
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
        if (connected){
            refreshDevices()
        } else{
            openFragment(CTNoWifiFragment::class.java.simpleName,animate = false)
        }
    }

    override fun deviceDiscoveryAfterClearingTheCacheStarted() {}

    override fun newDeviceFound(node: LSSDPNodes?) {
        LibreLogger.d(TAG,"newDeviceFound ${node?.friendlyname}")
        mTaskHandlerForSendingMSearch.removeCallbacks(mMyTaskRunnableForMSearch)
        val sceneKeySet = ScanningHandler.getInstance().sceneObjectMapFromRepo.keys.toTypedArray()
        LibreLogger.d(TAG,"sceneKeySet size = ${sceneKeySet.size}")
        if (/*sceneKeySet.isNotEmpty()*/LSSDPNodeDB.getInstance().GetDB().size>0) {
            openFragment(CTActiveDevicesFragment::class.java.simpleName,animate = false)
        }
    }

    override fun deviceGotRemoved(ipaddress: String?) {
    }

    override fun messageRecieved(packet: NettyData?) {
    }

    override fun onStop() {
        super.onStop()
        isActivityVisible = false
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        ensureDMRPlaybackStopped()
        killApp()
    }

    fun setTunnelFragmentListener(tunnelingFragmentListener: TunnelingFragmentListener){
        this.tunnelingFragmentListener = tunnelingFragmentListener
    }

    fun removeTunnelFragmentListener(){
        tunnelingFragmentListener = null
    }

    override fun tunnelDataReceived(tunnelingData: TunnelingData) {
        super.tunnelDataReceived(tunnelingData)
        tunnelingFragmentListener?.onFragmentTunnelDataReceived(tunnelingData)
    }

    private fun clearBatteryInfoForDevices(){
        ScanningHandler.getInstance().sceneObjectMapFromRepo.forEach { (ip: String?, sceneObject: SceneObject?) ->
            LibreLogger.d(TAG,"clearBatteryInfoForDevices device ${sceneObject.sceneName}")
            sceneObject?.clearBatteryStats()
        }
    }
}
