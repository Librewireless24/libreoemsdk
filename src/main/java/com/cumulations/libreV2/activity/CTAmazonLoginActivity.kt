package com.cumulations.libreV2.activity

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.amazon.identity.auth.device.AuthError
import com.amazon.identity.auth.device.api.authorization.*
import com.amazon.identity.auth.device.api.workflow.RequestContext
import com.amazon.identity.auth.device.authorization.api.AmazonAuthorizationManager
import com.libreAlexa.LErrorHandeling.LibreError
import com.libreAlexa.R
import com.libreAlexa.alexa.AlexaUtils
import com.libreAlexa.alexa.CompanionProvisioningInfo
import com.libreAlexa.alexa.DeviceProvisioningInfo
import com.libreAlexa.constants.AlexaConstants
import com.libreAlexa.constants.AppConstants
import com.libreAlexa.constants.Constants
import com.libreAlexa.constants.LSSDPCONST
import com.libreAlexa.constants.LUCIMESSAGES
import com.libreAlexa.constants.LibreAlexaConstants.*
import com.libreAlexa.constants.MIDCONST
import com.libreAlexa.databinding.CtActivityAmazonLoginBinding
import com.libreAlexa.luci.LSSDPNodeDB
import com.libreAlexa.luci.LSSDPNodes
import com.libreAlexa.luci.LUCIControl
import com.libreAlexa.luci.LUCIPacket
import com.libreAlexa.netty.LibreDeviceInteractionListner
import com.libreAlexa.netty.NettyData
import com.libreAlexa.util.LibreLogger
import org.json.JSONException
import org.json.JSONObject

class CTAmazonLoginActivity : CTDeviceDiscoveryActivity(), View.OnClickListener, LibreDeviceInteractionListner {

    companion object {
        const val ALEXA_META_DATA_TIMER = 0x12
        const val ACCESS_TOKEN_TIMEOUT = 301
    }

    private var mAuthManager: AmazonAuthorizationManager? = null
    private lateinit var requestContext: RequestContext

    private var deviceProvisioningInfo: DeviceProvisioningInfo? = null
    private val speakerIpAddress by lazy {
        intent.getStringExtra(Constants.CURRENT_DEVICE_IP)
    }
    private val from by lazy {
        intent.getStringExtra(Constants.FROM_ACTIVITY)
    }
    private var alertDialog: AlertDialog? = null
    private var speakerNode: LSSDPNodes? = null
    private var isMetaDateRequestSent = false
    var alexaLoginStatus: Boolean? = false
    var alexaLogginginCount = 0
    private lateinit var binding: CtActivityAmazonLoginBinding
    private var TAG = CTAmazonLoginActivity::class.java.simpleName

    @SuppressLint("HandlerLeak")
    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == ACCESS_TOKEN_TIMEOUT || msg.what == ALEXA_META_DATA_TIMER) {
                closeLoader()/*showing error*/
                val error = LibreError(speakerIpAddress, getString(R.string.requestTimeout))
                showErrorMessage(error)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CtActivityAmazonLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestContext = RequestContext.create(this@CTAmazonLoginActivity)

        requestContext.registerListener(AuthListener())
        if (intent != null) {
            speakerNode = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(speakerIpAddress)

            if (intent?.hasExtra(AppConstants.DEVICE_PROVISIONING_INFO)!! && intent?.getSerializableExtra(AppConstants.DEVICE_PROVISIONING_INFO) != null) {
                deviceProvisioningInfo =
                    intent.getSerializableExtra(AppConstants.DEVICE_PROVISIONING_INFO) as DeviceProvisioningInfo
            }

            if (deviceProvisioningInfo != null) {
                isMetaDateRequestSent = true
                speakerNode?.mdeviceProvisioningInfo = deviceProvisioningInfo
            }
        }

        binding.ivBack.setOnClickListener(this)
        binding.btnLoginAmazon.setOnClickListener(this)
        binding.tvSkip.setOnClickListener(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        this.intent = intent
    }

    private inner class AuthListener : AuthorizeListener() {
        override fun onSuccess(response: AuthorizeResult?) {
            try {
                speakerNode =
                    LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(speakerIpAddress)

                val authorizationCode = response?.authorizationCode
                val redirectUri = response?.redirectURI
                val clientId = response?.clientId
                val sessionId = speakerNode?.mdeviceProvisioningInfo?.sessionId
                runOnUiThread {
                    showProgressDialog("LoggingIn..")
                }
                if (authorizationCode != null && redirectUri != null && clientId != null && sessionId != null) {
                    LibreLogger.d(TAG, " Alexa Value From 234, session ID: $sessionId \n".plus(
                        ("AmzLoginStatus" +
                    "Alexa Value From 234, authCode)".plus(authorizationCode.plus("\n")))
                        .plus("AmzLoginStatus Alexa Value From 234, clientId".plus(clientId)
                            .plus("\n"))
                        .plus("AmzLoginStatus Alexa Value From 234, redirectUri".plus(redirectUri)
                            .plus("\n"))))
                }

                val companionProvisioningInfo =
                    CompanionProvisioningInfo(sessionId, clientId, redirectUri, authorizationCode)
                val luciControl = LUCIControl(speakerIpAddress)

                LibreLogger.d(TAG, "234_alexa_data_sent".plus(companionProvisioningInfo.toJson()
                    .toString()))
                luciControl.SendCommand(MIDCONST.ALEXA_COMMAND.toInt(), "AUTHCODE_EXCH:" + companionProvisioningInfo.toJson()
                    .toString(), LSSDPCONST.LUCI_SET)


                luciControl.SendCommand(MIDCONST.CHECK_ALEXA_LOGIN_STATUS, "GETLOGINSTAT", LSSDPCONST.LUCI_SET)
                LibreLogger.d(TAG, "AmazonLogin 234 msg sent" + companionProvisioningInfo.toJson().toString())

                handler.sendEmptyMessageDelayed(ACCESS_TOKEN_TIMEOUT, 25000)
            } catch (authError: AuthError) {
                authError.printStackTrace()
                LibreLogger.d(TAG, "AmazonLogin AuthError during authorization" + authError.printStackTrace())
                runOnUiThread(Runnable {
                    if (!isFinishing) {
                        closeLoader()
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onCancel(cause: AuthCancellation?) {
            LibreLogger.d(TAG, "AmazonLogin User cancelled authorization$cause")
        }

        override fun onError(ae: AuthError) {
            LibreLogger.d(TAG, "AmazonLogin AuthError_during_authorization: $ae")

            var error = ae.message
            if (error == null || error.isEmpty()) error = ae.toString()
            if (!isFinishing) {/*under observation for error case*/
                //if user skips amazon login and then again
                // clicks no thanks button upon asking again.
            }

        }
    }

    override fun onResume() {
        super.onResume()

        try {
            requestContext.onResume()
            // invalidApiKey = false
        } catch (e: java.lang.Exception) {
            LibreLogger.d(TAG, """amazon auth exception${e.message}${e.stackTrace}""".trimIndent())
            // invalidApiKey = true
            setMetaDateRequestSent(false)
        }

        registerForDeviceEvents(this)

    }

    private fun disableViews() {
        binding.btnLoginAmazon.isEnabled = false
        binding.btnLoginAmazon.alpha = 0.5f
    }

    override fun onClick(view: View) {

        when (view.id) {

            R.id.btn_login_amazon -> {

                LibreLogger.d("AMAZON SUMA", "LOGIN DATA ONLOGIN CLICK DATTA\n")

//                if (speakerNode?.mdeviceProvisioningInfo == null || !isMetaDateRequestSent) {
//                    showLoader()
//                    AlexaUtils.sendAlexaMetaDataRequest(speakerIpAddress)
//                    handler.sendEmptyMessageDelayed(ALEXA_META_DATA_TIMER, 15 * 1000)
//                    return
//                }
                if (speakerNode != null && !isMetaDateRequestSent()) {
                    handler.sendEmptyMessageDelayed(ALEXA_META_DATA_TIMER, 20000)
                    AlexaUtils.sendAlexaMetaDataRequest(speakerIpAddress)
                    setMetaDateRequestSent(true)
                    return
                }
                LibreLogger.d("AMAZON SUMA", "LOGIN DATA CODE IP\n" + speakerIpAddress)


                val scopeData = JSONObject()
                LibreLogger.d("AMAZON SUMA", "LOGIN DATA SCOPE DATA\n" + scopeData)
                val productInstanceAttributes = JSONObject()
                try {
                    //codeChallenge
                    if (speakerNode != null) {
                        val codeChallenge = speakerNode?.mdeviceProvisioningInfo?.codeChallenge
                        val codeChallengeMethod =
                            speakerNode?.mdeviceProvisioningInfo?.codeChallengeMethod
                        LibreLogger.d("AMAZON SUMA", "LOGIN DATA CODE CHALLENGE\n" + codeChallenge)
                        LibreLogger.d("AMAZON SUMA", "LOGIN DATA CODEMETHOD\n" + codeChallengeMethod)

                        productInstanceAttributes.put(DEVICE_SERIAL_NUMBER, speakerNode?.mdeviceProvisioningInfo?.dsn)
                        LibreLogger.d("AMAZON SUMA", "LOGIN DATA ProductSerialNumber\n" + speakerNode?.mdeviceProvisioningInfo?.dsn)

                        scopeData.put(PRODUCT_INSTANCE_ATTRIBUTES, productInstanceAttributes)
                        scopeData.put(PRODUCT_ID, speakerNode?.mdeviceProvisioningInfo?.productId)
                        //Try Catch block added by Shaik, Because app is crashing here with
                        // "Invalid API Key"
                        try {
                            AuthorizationManager.authorize(AuthorizeRequest.Builder(requestContext)
                                .addScopes(ScopeFactory.scopeNamed("alexa:voice_service:pre_auth"), ScopeFactory.scopeNamed("alexa:all", scopeData))
                                .forGrantType(AuthorizeRequest.GrantType.AUTHORIZATION_CODE)
                                .withProofKeyParameters(codeChallenge, codeChallengeMethod).build())
                        } catch (e: Exception) {
                            e.printStackTrace()
                            showToast(e.message.toString())
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    LibreLogger.d(TAG, "AmazonLogin_Auth" + "json ex")
                }

            }

            R.id.iv_back, R.id.tv_skip -> handleBackPressed()
        }
    }

    override fun deviceDiscoveryAfterClearingTheCacheStarted() {

    }

    override fun newDeviceFound(node: LSSDPNodes) {

    }

    override fun deviceGotRemoved(ipaddress: String) {

    }

    override fun messageRecieved(packet: NettyData) {
        LibreLogger.d(TAG, "AlexaSignInActivity: New message for " + packet.getRemotedeviceIp())
        val messagePacket = LUCIPacket(packet.getMessage())
        when (messagePacket.command) {
            MIDCONST.ALEXA_COMMAND.toInt() -> {

                val alexaMessage = String(messagePacket.getpayload())
                LibreLogger.d(TAG, "Alexa Value From 234  $alexaMessage")

                try {
                    if (alexaMessage.isNotEmpty()) {
                        val jsonRootObject = JSONObject(alexaMessage)
                        if (jsonRootObject.has(AppConstants.TITLE)) {
                            val title = jsonRootObject.getString(AppConstants.TITLE)
                            if (title == AlexaConstants.ACCESS_TOKENS_STATUS) {
                                val status = jsonRootObject.getBoolean(AppConstants.STATUS)
                                handler.removeMessages(ACCESS_TOKEN_TIMEOUT)
                                closeLoader()
                                if (status) {
                                    intentToThingToTryActivity()
                                } else {
                                    showSomethingWentWrongAlert(this@CTAmazonLoginActivity)
                                }
                            } else {
                                val jsonObject =
                                    jsonRootObject.getJSONObject(LUCIMESSAGES.ALEXA_KEY_WINDOW_CONTENT)
                                val productId =
                                    jsonObject.optString(LUCIMESSAGES.ALEXA_KEY_PRODUCT_ID)
                                val dsn = jsonObject.optString(LUCIMESSAGES.ALEXA_KEY_DSN)
                                val sessionId =
                                    jsonObject.optString(LUCIMESSAGES.ALEXA_KEY_SESSION_ID)
                                val codeChallenge =
                                    jsonObject.optString(LUCIMESSAGES.ALEXA_KEY_CODE_CHALLENGE)
                                val codeChallengeMethod =
                                    jsonObject.optString(LUCIMESSAGES.ALEXA_KEY_CODE_CHALLENGE_METHOD)
                                var locale = ""
                                if (jsonObject.has(LUCIMESSAGES.ALEXA_KEY_LOCALE)) locale =
                                    jsonObject.optString(LUCIMESSAGES.ALEXA_KEY_LOCALE)
                                if (speakerNode != null) {
                                    val mDeviceProvisioningInfo =
                                        DeviceProvisioningInfo(productId, dsn, sessionId, codeChallenge, codeChallengeMethod, locale)
                                    speakerNode!!.mdeviceProvisioningInfo = mDeviceProvisioningInfo
                                    handler.removeMessages(ALEXA_META_DATA_TIMER)
                                    isMetaDateRequestSent = true
                                    setAlexaViews()
                                    if (isMetaDateRequestSent) {
//                                        closeLoader()
                                        binding.btnLoginAmazon.performClick()
                                    }
                                }
                            }
                        }
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                    closeLoader()
                    handler.removeMessages(ALEXA_META_DATA_TIMER)
                    handler.removeMessages(ACCESS_TOKEN_TIMEOUT)
                }
            }

            MIDCONST.CHECK_ALEXA_LOGIN_STATUS -> {
                val payload: String = String(messagePacket.getpayload())
                LibreLogger.d("AlexaLognStatusAmazon", "receivedSkillPayloadMessage: $payload")
                when (payload) {
                    "LOGGINGIN" -> {
                        val luciControl = LUCIControl(speakerIpAddress)
                        LibreLogger.d("AlexaLognStatusAmazon", "receivedSkillPayloadMessage logging IN:$payload")
                        LibreLogger.d(TAG, "suma in get the login status logging before ******\n" +
                        alexaLogginginCount)

                        alexaLogginginCount++
                        if (alexaLogginginCount < 6) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                LibreLogger.d(TAG, "suma in get the login status logging in STILL ******\n" +
                                alexaLogginginCount)
                                luciControl.SendCommand(MIDCONST.CHECK_ALEXA_LOGIN_STATUS, "GETLOGINSTAT", LSSDPCONST.LUCI_SET)
                            }, 6000.toLong())
                        } else {
                            LibreLogger.d(TAG, "suma in get the login status loggingIN else\n" +
                            alexaLogginginCount)
                            dismissDialog()
                            val error =
                                LibreError(speakerIpAddress, getString(R.string.alexa_login_failed))
                            showErrorMessage(error)
                            handleBackPressed()
                        }
                        alexaLoginStatus = false
                    }

                    "NOLOGIN" -> {
                        LibreLogger.d("AlexaLognStatusAmazon", "receivedSkillPayloadMessage NO LOGIN:$payload")
                        alexaLoginStatus = false
                        dismissDialog()
                    }

                    "LOGGEDIN", "READY" -> {
                        LibreLogger.d("AlexaLognStatusAmazon", "receivedSkillPayloadMessage loggedIN: " + "$payload")

                        alexaLoginStatus = true
                        intentToThingToTryActivity()
                    }

                    "LOGGINGOUT" -> {
                        LibreLogger.d("AlexaLognStatusAmazon", "receivedSkillPayloadMessage loggingOUT: " + "$payload")
                        dismissDialog()
                        alexaLoginStatus = false
                    }
                }
            }

        }
    }


    private fun setAlexaViews() {
        binding.btnLoginAmazon.isEnabled = true
        binding.btnLoginAmazon.alpha = 1f
        binding.tvSkip.visibility = View.GONE
    }

    private fun intentToThingToTryActivity() {
        val alexaLangScreen =
            Intent(this@CTAmazonLoginActivity, CTAlexaThingsToTryActivity::class.java)
        alexaLangScreen.putExtra(Constants.CURRENT_DEVICE_IP, speakerIpAddress)
        if (intent?.hasExtra(Constants.FROM_ACTIVITY)!!) alexaLangScreen.putExtra(Constants.FROM_ACTIVITY, CTAmazonLoginActivity::class.java.simpleName)
        startActivity(alexaLangScreen)
        finish()
    }

    private fun closeLoader() {
        runOnUiThread {
            dismissDialog()
//            progress_bar!!.visibility = View.GONE
        }
    }

    fun isMetaDateRequestSent(): Boolean {
        return isMetaDateRequestSent
    }

    private fun showLoader() {
        runOnUiThread {
            showProgressDialog(R.string.pleaseWait)
//            progress_bar!!.visibility = View.VISIBLE
        }
    }


    override fun onStop() {
        super.onStop()
        unRegisterForDeviceEvents()
        handler.removeCallbacksAndMessages(null)
    }

    private fun showAlertDialog(error: String?) {

        if (alertDialog != null && alertDialog!!.isShowing) alertDialog!!.dismiss()

        val builder = AlertDialog.Builder(this@CTAmazonLoginActivity)
        builder.setTitle("Amazon Login Error")
        builder.setMessage(error)
        builder.setNeutralButton("Close") { dialogInterface, i -> alertDialog!!.dismiss() }

        if (alertDialog == null) {
            alertDialog = builder.create()
            alertDialog!!.show()
        }

    }

    private fun handleBackPressed() {
        if (!from.isNullOrEmpty() && from!!.equals(CTConnectingToMainNetwork::class.java.simpleName, ignoreCase = true)) {
            intentToHome(this)
        } else if (!from.isNullOrEmpty() && from!!.equals(CTAmazonInfoActivity::class.java.simpleName, ignoreCase = true)) {
            val newIntent =
                Intent(this@CTAmazonLoginActivity, CTAmazonInfoActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            newIntent.putExtra(Constants.CURRENT_DEVICE_IP, speakerIpAddress)
            newIntent.putExtra(AppConstants.DEVICE_PROVISIONING_INFO, speakerNode!!.mdeviceProvisioningInfo)
            newIntent.putExtra(Constants.FROM_ACTIVITY, CTAmazonLoginActivity::class.java.simpleName)
            startActivity(newIntent)
            finish()
        } else finish()
    }

    override fun onBackPressed() {
        handleBackPressed()
    }

    fun setMetaDateRequestSent(metaDateRequestSent: Boolean) {
        isMetaDateRequestSent = metaDateRequestSent
    }
}
