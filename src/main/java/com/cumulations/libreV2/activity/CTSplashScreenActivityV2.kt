package com.cumulations.libreV2.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.cumulations.libreV2.AppUtils
import com.cumulations.libreV2.appInForeground
import com.libreAlexa.LibreApplication
import com.libreAlexa.LibreApplication.GLOBAL_TAG
import com.libreAlexa.LibreEntryPoint
import com.libreAlexa.R
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
        binding.txtAppVersion.text = getVersion(applicationContext)
        LibreEntryPoint.getInstance().init(this@CTSplashScreenActivityV2)
        val isFirsTime = AppUtils.getIsFirstTimeLaunch(this)
        Log.d(GLOBAL_TAG, "CTSplashScreenActivityV2  called isFirsTime:- $isFirsTime ")

    }

    override fun proceedToHome() {
        lifecycleScope.launch {
            delay(1000)
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

    private fun getVersion(context: Context): String {
        var version = getString(R.string.title_activity_welcome)
        var pInfo: PackageInfo? = null
        try {
            pInfo = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_META_DATA)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        if (pInfo != null) version = pInfo.versionName
        return version
    }

}