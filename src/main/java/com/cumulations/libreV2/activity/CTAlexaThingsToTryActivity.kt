package com.cumulations.libreV2.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.cumulations.libreV2.launchTheApp
import com.libreAlexa.R
import com.libreAlexa.alexa.AlexaUtils
import com.libreAlexa.constants.Constants
import com.libreAlexa.constants.LSSDPCONST
import com.libreAlexa.constants.LUCIMESSAGES
import com.libreAlexa.constants.MIDCONST
import com.libreAlexa.databinding.CtActivityAlexaThingsToTryBinding
import com.libreAlexa.luci.LSSDPNodeDB
import com.libreAlexa.luci.LUCIControl
import com.libreAlexa.util.LibreLogger


/**
 * Created by Amit on 12/14/2016.
 */

class CTAlexaThingsToTryActivity : CTDeviceDiscoveryActivity(), View.OnClickListener {

    private val deviceIp by lazy {
        intent?.getStringExtra(Constants.CURRENT_DEVICE_IP)
    }
    private val fromActivity by lazy {
        intent?.getStringExtra(Constants.FROM_ACTIVITY)
    }
    private lateinit var binding: CtActivityAlexaThingsToTryBinding
    private val TAG = CTAlexaThingsToTryActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CtActivityAlexaThingsToTryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(deviceIp).apply {
            if (this.alexaRefreshToken.isNullOrEmpty()){
                tv_done?.text = getText(R.string.done)
            } else tv_done?.text = getText(R.string.logout)
        }*/

        LibreLogger.d(TAG,"from = $fromActivity")

        if (!fromActivity.isNullOrEmpty() && fromActivity?.equals(CTConnectingToMainNetwork::class.java.simpleName)!!){
            binding.tvDone.text = getText(R.string.done)
        } else binding.tvDone.text = getText(R.string.logout)

        binding.ivBack.setOnClickListener(this)
        binding.tvDone.setOnClickListener(this)
        binding.llAlexaApp.setOnClickListener(this)
        binding.tvAlexaApp.setOnClickListener(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        this.intent = intent
    }

    override fun onResume() {
        super.onResume()
        AlexaUtils.sendAlexaRefreshTokenRequest(deviceIp)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_done ->{
                if(v.id == R.id.tv_done &&  binding.tvDone.text.toString() == getString(R.string.logout)){
                    /*Logout clicked*/
                    amazonLogout()
                } else {
                    handleBackPress()
                }
            }

            R.id.ll_alexa_app,R.id.tv_alexa_app -> launchTheApp(this,"com.amazon.dee.app")

            R.id.iv_back -> handleBackPress()
        }

    }

    private fun amazonLogout(){
        LUCIControl(deviceIp).SendCommand(MIDCONST.ALEXA_COMMAND.toInt(), LUCIMESSAGES.SIGN_OUT, LSSDPCONST.LUCI_SET)
        val mNode = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(deviceIp)
        mNode?.alexaRefreshToken = ""

        try {
            Thread.sleep(500)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        handleBackPress()
    }

    private fun handleBackPress(){
        if (!fromActivity.isNullOrEmpty() && fromActivity == CTConnectingToMainNetwork::class.java.simpleName) {
            intentToHome(this)
        } else onBackPressed()
    }

}
