package com.cumulations.libreV2.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.cumulations.libreV2.model.SceneObject
import com.libreAlexa.LibreApplication
import com.libreAlexa.R
import com.libreAlexa.Scanning.ScanningHandler
import com.libreAlexa.app.dlna.dmc.processor.upnp.LoadLocalContentService
import com.libreAlexa.constants.AppConstants.DEVICES_FOUND
import com.libreAlexa.constants.AppConstants.MEDIA_PROCESS_DONE
import com.libreAlexa.constants.AppConstants.MEDIA_PROCESS_INIT
import com.libreAlexa.constants.AppConstants.MSEARCH_REQUEST
import com.libreAlexa.constants.AppConstants.TIME_EXPIRED
import com.libreAlexa.constants.Constants
import com.libreAlexa.databinding.CtActivitySplashBinding
import com.libreAlexa.luci.LSSDPNodes
import com.libreAlexa.netty.LibreDeviceInteractionListner
import com.libreAlexa.netty.NettyData
import com.libreAlexa.util.LibreLogger

class CTSplashScreenActivity : CTDeviceDiscoveryActivity(), LibreDeviceInteractionListner {
    private lateinit var binding: CtActivitySplashBinding
    private val TAG = CTSplashScreenActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CtActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /*override fun onStartComplete() {
        super.onStartComplete()
        btn_get_started.setOnClickListener {
            refreshDeviceNodes()
        }
    }*/

    override fun onResume() {
        super.onResume()
        LibreApplication.activeSSID = getConnectedSSIDName(this)
        LibreApplication.mActiveSSIDBeforeWifiOff = LibreApplication.activeSSID
        registerForDeviceEvents(this)
    }

    override fun proceedToHome() {
        refreshDeviceNodes()
    }

    override fun deviceDiscoveryAfterClearingTheCacheStarted() {

    }

    override fun newDeviceFound(node: LSSDPNodes?) {
        /*if (node?.deviceState == "M") {
            LibreLogger.d(this, "Master found")
            handler.sendEmptyMessage(DEVICES_FOUND)
            handler.removeMessages(TIME_EXPIRED)
            val sceneObject = SceneObject(" ", node?.friendlyname, 0f, node?.ip)
            ScanningHandler.getInstance().putSceneObjectToCentralRepo(node?.ip, sceneObject)
        } else if (node?.deviceState == "F") {
            handler.sendEmptyMessageDelayed(DEVICES_FOUND, 3000)
            handler.removeMessages(TIME_EXPIRED)
        }*/

        /*we will display any speakers in network and hence don't check for state*/
        handler.sendEmptyMessage(DEVICES_FOUND)
        handler.removeMessages(TIME_EXPIRED)
        val sceneObject = SceneObject(" ", node?.friendlyname, 0f, node?.ip)
        ScanningHandler.getInstance().putSceneObjectToCentralRepo(node?.ip, sceneObject)
    }

    override fun deviceGotRemoved(ipaddress: String?) {
    }

    override fun messageRecieved(packet: NettyData?) {
    }
    
    private fun refreshDeviceNodes(){
        getSharedPreferences(Constants.SHOWN_GOOGLE_TOS, Context.MODE_PRIVATE)
                .edit()
                .putString(Constants.SHOWN_GOOGLE_TOS, "Yes")
                .apply()
        LibreApplication.GOOGLE_TOS_ACCEPTED = true
        binding.loader.visibility = View.VISIBLE
        /*if (libreApplication.scanThread != null) {
            libreApplication.scanThread.clearNodes()
            libreApplication.scanThread.UpdateNodes()
            handler.sendEmptyMessageDelayed(MSEARCH_REQUEST, 1000)
            handler.sendEmptyMessageDelayed(MSEARCH_REQUEST, 2000)
            handler.sendEmptyMessageDelayed(MSEARCH_REQUEST, 3000)
            handler.sendEmptyMessageDelayed(MSEARCH_REQUEST, 4000)
        }*/
        /* initiating the search DMR */
        upnpProcessor?.searchDMR()
        //suma
        upnpProcessor?.searchDMS()
        upnpProcessor?.searchAll()
        //suma
        LibreLogger.d(TAG,"suma in search dms suma check")
        handler.sendEmptyMessage(MEDIA_PROCESS_INIT)
    }

    @SuppressLint("HandlerLeak")
    private var handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                DEVICES_FOUND -> {
                    LibreLogger.d(TAG, "Devices found")
                }

                TIME_EXPIRED -> {
                    binding.loader.visibility = View.INVISIBLE
                    if (!this@CTSplashScreenActivity.isFinishing) {
                        val builder = AlertDialog.Builder(this@CTSplashScreenActivity)
                        builder.setMessage(getString(R.string.noNetworkFound))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.ok)) { dialog, id ->
                                    System.exit(0)
                                    finish()
                                }

                        val alert = builder.create()
                        alert.show()
                    }
                }

                MSEARCH_REQUEST -> {
                    LibreLogger.d(TAG, "Sending the msearch nodes")
                   // libreApplication.scanThread.UpdateNodes()
                }

                MEDIA_PROCESS_INIT -> {
                    val startTime = System.currentTimeMillis()
                    /*val mNetIf = Utils.getActiveNetworkInterface()
                    if (mNetIf == null)
                        sendEmptyMessage(TIME_EXPIRED)
                    else {*/
                    if (!LibreApplication.LOCAL_IP.isNullOrEmpty()) {
                        startService(Intent(this@CTSplashScreenActivity, LoadLocalContentService::class.java))
                    }
                    LibreApplication.activeSSID = getConnectedSSIDName(this@CTSplashScreenActivity)
                    LibreApplication.mActiveSSIDBeforeWifiOff = LibreApplication.activeSSID
                    val finishedTime = System.currentTimeMillis()
                    if (finishedTime - startTime < 4500)
                        sendEmptyMessageDelayed(MEDIA_PROCESS_DONE, 4500 - (finishedTime - startTime))
                    else
                        sendEmptyMessage(MEDIA_PROCESS_DONE)
//                    }
                }

                MEDIA_PROCESS_DONE -> {
                    openNextScreen()
                }
            }


        }
    }

    private fun openNextScreen() {

        binding.loader.visibility = View.GONE
        intentToHome(this)


    }

    override fun onStop() {
        super.onStop()
        unRegisterForDeviceEvents()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        killApp()
    }
}
