package com.cumulations.libreV2.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.cumulations.libreV2.activity.oem.SetUpDeviceActivity
import com.libreAlexa.R
import com.libreAlexa.alexa.DeviceProvisioningInfo
import com.libreAlexa.constants.AppConstants
import com.libreAlexa.constants.Constants
import com.libreAlexa.databinding.CtActivityAmazonSigninSetupBinding
import com.libreAlexa.luci.LSSDPNodeDB
import com.libreAlexa.luci.LSSDPNodes
import com.libreAlexa.util.LibreLogger


class CTAmazonInfoActivity : CTDeviceDiscoveryActivity(), View.OnClickListener {
    private lateinit var binding: CtActivityAmazonSigninSetupBinding
    private val speakerIpAddress by lazy {
        intent.getStringExtra(Constants.CURRENT_DEVICE_IP)
    }
    private val from by lazy {
        intent.getStringExtra(Constants.FROM_ACTIVITY)
    }
    private val isSetupScreen by lazy {
        intent.getBooleanExtra(Constants.PREV_SCREEN, false)
    }
    private var speakerNode: LSSDPNodes? = null
    val TAG = CTAmazonInfoActivity::class.java.toString()
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        this.intent = intent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CtActivityAmazonSigninSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        speakerNode = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(speakerIpAddress)
        if (intent?.hasExtra(AppConstants.DEVICE_PROVISIONING_INFO)!! && intent?.getSerializableExtra(AppConstants.DEVICE_PROVISIONING_INFO) != null) {
            val deviceProvisioningInfo =
                intent.getSerializableExtra(AppConstants.DEVICE_PROVISIONING_INFO) as DeviceProvisioningInfo
            speakerNode?.mdeviceProvisioningInfo = deviceProvisioningInfo
        }
        LibreLogger.d(TAG, "onCreate: " + speakerIpAddress + " Name: ${speakerNode!!.friendlyname} and " +
                "From " + "$from isSetupScreen: $isSetupScreen")
        binding.ivBack.setOnClickListener(this)
        binding.btnSigninAmazon.setOnClickListener(this)
        binding.btnSigninLater.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_signin_amazon -> {
                val amazonLoginScreen =
                    Intent(this@CTAmazonInfoActivity, CTAmazonLoginActivity::class.java)
                amazonLoginScreen.putExtra(Constants.CURRENT_DEVICE_IP, speakerIpAddress)
                amazonLoginScreen.putExtra(AppConstants.DEVICE_PROVISIONING_INFO, speakerNode!!.mdeviceProvisioningInfo)
                amazonLoginScreen.putExtra(Constants.FROM_ACTIVITY, CTAmazonInfoActivity::class.java.simpleName)
                startActivity(amazonLoginScreen)
                finish()
            }

            R.id.iv_back -> handleBackPress()
            R.id.btn_signin_later -> {
                handleBackPress()
            }
        }
    }

    override fun onBackPressed() {
        handleBackPress()
    }

    //Created By SHAIk
    private fun handleBackPress() {
        if (!from.isNullOrEmpty() && from.equals(SetUpDeviceActivity::class.java.simpleName, ignoreCase = true) || from.equals(CTAmazonLoginActivity::class.java.simpleName, ignoreCase = true)) {
            val newIntent = Intent(this@CTAmazonInfoActivity, SetUpDeviceActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            newIntent.putExtra(Constants.CURRENT_DEVICE_IP, speakerIpAddress)
            newIntent.putExtra(Constants.DEVICE_NAME, speakerNode!!.friendlyname)
            newIntent.putExtra(Constants.FROM_ACTIVITY, CTAmazonInfoActivity::class.java.simpleName)
            startActivity(newIntent)
        } else {
            intentToHome(this)
        }
        finish()
    }

}
