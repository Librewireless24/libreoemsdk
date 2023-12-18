package com.cumulations.libreV2.fragments

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cumulations.libreV2.AppUtils
import com.cumulations.libreV2.activity.CTBluetoothDeviceListActivity
import com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity
import com.cumulations.libreV2.activity.CTHomeTabsActivity
import com.cumulations.libreV2.model.SceneObject
import com.libreAlexa.R
import com.libreAlexa.Scanning.ScanningHandler
import com.libreAlexa.databinding.CtFragmentNoDeviceBinding
import com.libreAlexa.luci.LSSDPNodeDB
import com.libreAlexa.luci.LSSDPNodes
import com.libreAlexa.netty.LibreDeviceInteractionListner
import com.libreAlexa.netty.NettyData

class CTNoDeviceFragment:Fragment(),LibreDeviceInteractionListner,View.OnClickListener {
    private val mScanHandler = ScanningHandler.getInstance()
    private var binding: CtFragmentNoDeviceBinding? = null
    private val TAG:String = CTNoDeviceFragment::class.java.simpleName
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = CtFragmentNoDeviceBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setListeners()
    }

    private fun setListeners() {
        binding!!.tvRefresh.setOnClickListener(this)
        binding!!.tvSetupSpeaker.setOnClickListener(this)
    }

    private fun initViews() {
//        toolbar.title = ""
    }

    override fun onResume() {
        super.onResume()
        (activity as CTDeviceDiscoveryActivity).registerForDeviceEvents(this)
        handler?.sendEmptyMessageDelayed(1, 3000)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.iv_refresh,R.id.tv_refresh -> {
                (activity as CTHomeTabsActivity).refreshDevices()

            }
            R.id.tv_setup_speaker -> {
                val fineLocationPermission = AppUtils.isPermissionGranted(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                if (fineLocationPermission) {
                    val intent = Intent(activity, CTBluetoothDeviceListActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }else{
                    (activity as CTDeviceDiscoveryActivity).checkLocationPermission()
                }
            }
        }
    }

    internal var handler: Handler? = object : Handler(Looper.getMainLooper()) {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (LSSDPNodeDB.getInstance().GetDB().size > 0) {
                (activity as CTHomeTabsActivity).openFragment(CTActiveDevicesFragment::class.java.simpleName,animate = true)
            }
            this.sendEmptyMessageDelayed(1, 3000)
        }
    }

    override fun deviceDiscoveryAfterClearingTheCacheStarted() {

    }

    override fun newDeviceFound(node: LSSDPNodes) {
        updateSceneObjectAndOpenDeviceList(node.ip)
    }

    /*this method will get called when we are getting master 103*/
    private fun updateSceneObjectAndOpenDeviceList(ipaddress: String) {

        val node = mScanHandler.getLSSDPNodeFromCentralDB(ipaddress)
        if (node != null) {
            if (!mScanHandler.isIpAvailableInCentralSceneRepo(ipaddress)) {
                val sceneObject = SceneObject(" ", node.friendlyname, 0f, node.ip)
                mScanHandler.putSceneObjectToCentralRepo(ipaddress, sceneObject)
                (activity as CTHomeTabsActivity).openFragment(CTActiveDevicesFragment::class.java.simpleName,animate = true)
                handler?.removeMessages(1)
            }
        }

    }
    override fun deviceGotRemoved(ipaddress: String) {

    }

    override fun messageRecieved(packet: NettyData) {

    }

    override fun onStop() {
        super.onStop()
        handler?.removeMessages(1)
        (activity as CTDeviceDiscoveryActivity).unRegisterForDeviceEvents()
    }
}