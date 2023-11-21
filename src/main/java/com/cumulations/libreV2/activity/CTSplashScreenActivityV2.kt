package com.cumulations.libreV2.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.cumulations.libreV2.AppUtils
import com.cumulations.libreV2.appInForeground
import com.libreAlexa.LibreApplication
import com.libreAlexa.LibreEntryPoint
import com.libreAlexa.app.dlna.dmc.processor.upnp.LoadLocalContentService
import com.libreAlexa.databinding.CtActivitySplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class CTSplashScreenActivityV2 : CTDeviceDiscoveryActivity() {
    companion object {
        val TAG: String = CTSplashScreenActivityV2::class.java.simpleName
    }

    private lateinit var binding: CtActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CtActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.txtAppVersion.text = AppUtils.getVersion(applicationContext)
        Log.d(TAG, "SplashScreen onCreate called")
        LibreEntryPoint.getInstance().init(this@CTSplashScreenActivityV2)

    }

    override fun proceedToHome() {
        lifecycleScope.launch {
            delay(1000)
            Log.d(TAG, "proceedToHome called from SplashScreen")
            openNextScreen()
        }
    }

    private fun openNextScreen() {

        Handler(Looper.getMainLooper()).post {
            if (!LibreApplication.LOCAL_IP.isNullOrEmpty() && appInForeground(this)) {
                startService(Intent(this@CTSplashScreenActivityV2, LoadLocalContentService::class.java))
            }
        }
        intentToHome(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        killApp()
    }

}