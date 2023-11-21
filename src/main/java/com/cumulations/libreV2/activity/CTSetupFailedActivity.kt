package com.cumulations.libreV2.activity

import android.os.Bundle
import com.cumulations.libreV2.activity.CTConnectingToMainNetwork.TAG
import com.libreAlexa.databinding.CtActivityDeviceSetupFailedInfoBinding
import com.libreAlexa.luci.LSSDPNodes
import com.libreAlexa.netty.LibreDeviceInteractionListner
import com.libreAlexa.netty.NettyData
import com.libreAlexa.util.LibreLogger

class CTSetupFailedActivity : CTDeviceDiscoveryActivity() ,LibreDeviceInteractionListner{
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

    override fun deviceDiscoveryAfterClearingTheCacheStarted() {
    }

    override fun newDeviceFound(node: LSSDPNodes?) {
        LibreLogger.d("CTSetupFailedActivity","192.168.0.109 in new device found\n"+ node!!.friendlyname)
    }

    override fun deviceGotRemoved(ipaddress: String?) {
    }

    override fun messageRecieved(packet: NettyData?) {
        LibreLogger.d("CTSetupFailedActivity","new device msg received \n")

    }

    override fun onResume() {
        super.onResume()
        registerForDeviceEvents(this)
    }

    override fun onStop() {
        super.onStop()
        unRegisterForDeviceEvents()
    }
}
