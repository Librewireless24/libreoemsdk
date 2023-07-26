package com.cumulations.libreV2.activity

import android.os.Bundle
import com.libreAlexa.databinding.CtActivityDeviceSetupFailedInfoBinding

class CTSetupFailedActivity : CTDeviceDiscoveryActivity() {
    private lateinit var binding: CtActivityDeviceSetupFailedInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CtActivityDeviceSetupFailedInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.ivBack.setOnClickListener {
            intentToHome(this)
        }

        binding.btnOkGotIt.setOnClickListener {
            intentToHome(this)
        }
    }
}
