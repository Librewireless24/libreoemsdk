package com.cumulations.libreV2.activity.oem

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity
import com.cumulations.libreV2.activity.CTDeviceSettingsActivity
import com.cumulations.libreV2.activity.GoToHomeActivity
import com.libreAlexa.R
import com.libreAlexa.constants.Constants
import com.libreAlexa.constants.Constants.DEVICE_REMOVED
import com.libreAlexa.databinding.ActivityOpenHomeAppBinding
import com.libreAlexa.luci.LSSDPNodes
import com.libreAlexa.netty.LibreDeviceInteractionListner
import com.libreAlexa.netty.NettyData
import com.libreAlexa.util.LibreLogger
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by SHAIK
 * 24/04/2023
 */
class OpenGHomeAppActivity : CTDeviceDiscoveryActivity(), LibreDeviceInteractionListner {
    companion object {
        val TAG: String = OpenGHomeAppActivity::class.java.simpleName
    }

    private val currentIpAddress: String? by lazy {
        intent?.getStringExtra(Constants.CURRENT_DEVICE_IP)
    }
    private val currentDeviceUUID: String? by lazy {
        intent?.getStringExtra(Constants.CURRENT_DEVICE_UUID)
    }
    private val speakerName by lazy {
        intent.getStringExtra(Constants.DEVICE_NAME)
    }
    private val from by lazy {
        intent.getStringExtra(Constants.FROM_ACTIVITY)
    }
    private var isHomeAppOpen: Boolean = false

    private lateinit var binding: ActivityOpenHomeAppBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpenHomeAppBinding.inflate(layoutInflater)
        setContentView(binding.root)
        LibreLogger.d(TAG, "onCreate:sSpeakerIpAddress $currentIpAddress\n and DeviceName: $speakerName\n from $from\n and uuid $currentDeviceUUID")
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
        binding.btnSkip.setOnClickListener {
            /*intentToHome(this)*/
            val newIntent = Intent(this@OpenGHomeAppActivity, GoToHomeActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            newIntent.putExtra(Constants.FROM_ACTIVITY, OpenGHomeAppActivity::class.java.simpleName)
            newIntent.putExtra(Constants.CURRENT_DEVICE_IP, currentIpAddress)
            newIntent.putExtra(Constants.DEVICE_NAME, speakerName)
            startActivity(newIntent)
            finish()
        }
        binding.btnOpenHomeApp.setOnClickListener {
            val gHomeIntent = packageManager.getLaunchIntentForPackage("com.google.android.apps.chromecast.app")
            if (gHomeIntent != null) {
                startActivity(gHomeIntent)
                isHomeAppOpen = true
            } else {
                showAppNotInstalledAlertDialog()
            }
        }
    }

    private fun showAppNotInstalledAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.app_not_installed))
        builder.setMessage(getString(R.string.google_home_install))
        builder.setPositiveButton(getString(R.string.yes_str)) { dialog, which -> //Navigate to Play Store
            val uri = Uri.parse(getString(R.string.ghome_link))
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
            isHomeAppOpen = true
        }
        builder.show()
    }

    private fun exitOnBackPressed() {
        if (!from.isNullOrEmpty() && from!!.equals(CTDeviceSettingsActivity::class.java.simpleName, ignoreCase = true)) {
            LibreLogger.d("GoToHomeActivity","android 13 Opne else")
            val newIntent = Intent(this@OpenGHomeAppActivity, CTDeviceSettingsActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            newIntent.putExtra(Constants.FROM_ACTIVITY, OpenGHomeAppActivity::class.java.simpleName)
            newIntent.putExtra(Constants.CURRENT_DEVICE_IP, currentIpAddress)
            newIntent.putExtra(Constants.DEVICE_NAME, speakerName)
            startActivity(newIntent)
            finish()
        } else if (!from.isNullOrEmpty() && from!!.equals(SetUpDeviceActivity::class.java.simpleName, ignoreCase = true)) {
            LibreLogger.d("GoToHomeActivity","android 13 Opne else if")
           val newIntent = Intent(this@OpenGHomeAppActivity, SetUpDeviceActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            newIntent.putExtra(Constants.FROM_ACTIVITY, OpenGHomeAppActivity::class.java.simpleName)
            newIntent.putExtra(Constants.CURRENT_DEVICE_IP, currentIpAddress)
            newIntent.putExtra(Constants.DEVICE_NAME, speakerName)
            startActivity(newIntent)
            finish()
        } else {
            LibreLogger.d("GoToHomeActivity","android 13 Opne else" )
            //intentToHome(this)
            val newIntent = Intent(this@OpenGHomeAppActivity, GoToHomeActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            newIntent.putExtra(Constants.FROM_ACTIVITY, OpenGHomeAppActivity::class.java.simpleName)
            newIntent.putExtra(Constants.CURRENT_DEVICE_IP, currentIpAddress)
            newIntent.putExtra(Constants.DEVICE_NAME, speakerName)
            startActivity(newIntent)
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        registerForDeviceEvents(this)
        if (isHomeAppOpen) {
            isHomeAppOpen = false
            binding.layParent.visibility = View.GONE
            binding.layLoader.visibility = View.VISIBLE
            lifecycleScope.launch {
                delay(3000)
                exitOnBackPressed()
            }
        }
    }

    override fun deviceDiscoveryAfterClearingTheCacheStarted() {

    }

    override fun newDeviceFound(node: LSSDPNodes?) {

    }

    override fun deviceGotRemoved(ipaddress: String?) {
        LibreLogger.d(DEVICE_REMOVED, "deviceGotRemoved called OpenG $ipaddress and speakerIpAddress: $currentIpAddress")
    }

    override fun messageRecieved(packet: NettyData?) {

    }

    override fun onStop() {
        super.onStop()
        unRegisterForDeviceEvents()
    }

}