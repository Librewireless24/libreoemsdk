package com.cumulations.libreV2.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.*
import com.libreAlexa.LibreApplication
import com.libreAlexa.R
import com.libreAlexa.app.dlna.dmc.processor.upnp.LoadLocalContentService
import com.libreAlexa.constants.Constants
import com.libreAlexa.constants.Constants.MediaEnum.*
import com.libreAlexa.luci.Utils
import com.libreAlexa.util.LibreLogger
import org.fourthline.cling.model.meta.LocalDevice


class CTLocalDMSActivity : CTDeviceDiscoveryActivity() {
    private var mThread: HandlerThread? = null
    private var mServiceHandler: ServiceHandler? = null
    private val TAG = CTLocalDMSActivity::class.java.name
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ct_activity_local_dms)
        LibreLogger.d(TAG, "onCreate of the activity is getting called")

        if (LibreApplication.LOCAL_UDN.isNotEmpty()) {
            /* Local device is already exists in the registry and hence launch the DMSBrowsingActivity or else wait*/
            openDMSBrowser()
        } else {
            LibreLogger.d(TAG, "creating Upnp DMR for phone")
            mThread = HandlerThread(CTLocalDMSActivity::class.java.simpleName, Process.THREAD_PRIORITY_BACKGROUND)
            mThread?.start()
            mServiceHandler = ServiceHandler(mThread?.looper!!)
            /* this is the case where the content is not present and hence we need to load */
            mServiceHandler?.sendEmptyMessage(MEDIA_PROCESS_INIT)
        }
    }

    override fun onLocalDeviceAdded(device: LocalDevice) {
        super.onLocalDeviceAdded(device)
        LibreLogger.d(TAG, "Added local device")
    }

    override fun onStartComplete() {
        LibreLogger.d(TAG, "on Start complete")
    }

    @SuppressLint("HandlerLeak")
    internal var mediaHandler: Handler = object : Handler(Looper.getMainLooper()) {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            when (msg.what) {

                MEDIA_PROCESS_DONE -> {
                    dismissDialog()
                    LibreLogger.d(TAG, "DMR ready")
                    /*loading stopMediaServer with success message*/
                    showToast("Local content loading Done!")
                    openDMSBrowser()
                }

                MEDIA_LOADING_FAIL -> {
                    dismissDialog()
                    showToast(R.string.restartAppManually)
                }
            }
        }
    }

    private fun openDMSBrowser() {
        startActivity(Intent(this@CTLocalDMSActivity, CTDMSBrowserActivityV2::class.java).apply {
            putExtra(Constants.DEVICE_UDN, LibreApplication.LOCAL_UDN)
            putExtra(Constants.CURRENT_DEVICE_IP, intent?.getStringExtra(Constants.CURRENT_DEVICE_IP))
        })
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        mServiceHandler?.removeCallbacksAndMessages(null)
        mediaHandler.removeCallbacksAndMessages(null)
    }

    internal inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {

            try {
                when (msg.what) {
                    MEDIA_PROCESS_INIT -> {
                        val mNetIf = Utils.getActiveNetworkInterface()
                        if (mNetIf == null) {
                            LibreLogger.d(TAG, "My Netif is Null")
                            mediaHandler.sendEmptyMessage(MEDIA_LOADING_FAIL)
                        } else {
                            startService(Intent(this@CTLocalDMSActivity, LoadLocalContentService::class.java))
                            showProgressDialog(R.string.loadingLocalContent)
                        }

                        while (LibreApplication.LOCAL_UDN.isEmpty()) {
                            Thread.sleep(200)
                        }

                        mediaHandler.sendEmptyMessageDelayed(MEDIA_PROCESS_DONE, 1000L)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                mediaHandler.sendEmptyMessage(MEDIA_LOADING_FAIL)
            }

        }
    }
}
