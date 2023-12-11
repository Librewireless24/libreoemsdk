package com.cumulations.libreV2.activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import com.cumulations.libreV2.activity.oem.OpenGHomeAppActivity
import com.cumulations.libreV2.activity.oem.SetUpDeviceActivity
import com.libreAlexa.R
import com.libreAlexa.constants.Constants
import com.libreAlexa.databinding.ActivityGoToHomeBinding
import com.libreAlexa.databinding.ActivityOpenHomeAppBinding
import com.libreAlexa.util.LibreLogger

class GoToHomeActivity : CTDeviceDiscoveryActivity() {

    private lateinit var binding: ActivityGoToHomeBinding
    private var TAG= "GoToHomeActivity"
    private val speakerName by lazy {
        intent.getStringExtra(Constants.DEVICE_NAME)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoToHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= 33) {
            //Android 13 and Above
            onBackInvokedDispatcher.registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT) {
              LibreLogger.d(TAG,"android 13")
                intentToHome(this)
            }
        } else {
            //Android 12 and below
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    LibreLogger.d(TAG,"android 12")
                    intentToHome(this@GoToHomeActivity)
                }
            })
        }
        binding.backButton.setOnClickListener {
            intentToHome(this)
        }
        binding.btnGoHome.setOnClickListener{
            intentToHome(this)
        }
        binding.txtSetupDevice.text=speakerName+" "+getString(R.string.has_been_successfully_setup)


    }
}