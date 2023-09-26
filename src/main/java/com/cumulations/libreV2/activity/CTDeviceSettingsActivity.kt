package com.cumulations.libreV2.activity


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.cumulations.libreV2.AppUtils
import com.cumulations.libreV2.SendDataFragmentToActivity
import com.cumulations.libreV2.activity.oem.ActivateCastActivity
import com.cumulations.libreV2.closeKeyboard
import com.cumulations.libreV2.fragments.CTAlexaLocaleDialogFragment
import com.cumulations.libreV2.fragments.CTAudioOutputDialogFragment
import com.cumulations.libreV2.fragments.TimeZoneFragment
import com.cumulations.libreV2.isConnectedToSAMode
import com.cumulations.libreV2.roomdatabase.CastLiteUUIDDataClass
import com.cumulations.libreV2.roomdatabase.LibreVoiceDatabase
import com.cumulations.libreV2.tcp_tunneling.TCPTunnelPacket
import com.cumulations.libreV2.tcp_tunneling.TunnelingControl
import com.cumulations.libreV2.tcp_tunneling.TunnelingData
import com.cumulations.libreV2.tcp_tunneling.enums.AQModeSelect
import com.cumulations.libreV2.tcp_tunneling.enums.PayloadType
import com.cumulations.libreV2.writeAwayModeSettingsToDevice
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.libreAlexa.LibreApplication.isForgetNetworkCalled
import com.libreAlexa.R
import com.libreAlexa.Scanning.ScanningHandler
import com.libreAlexa.alexa.AlexaUtils
import com.libreAlexa.constants.Constants
import com.libreAlexa.constants.LSSDPCONST
import com.libreAlexa.constants.LUCIMESSAGES
import com.libreAlexa.constants.LibreAlexaConstants
import com.libreAlexa.constants.MIDCONST
import com.libreAlexa.databinding.CtDeviceSettingsBinding
import com.libreAlexa.databinding.CtDlgFragmentEditAwayModeBinding
import com.libreAlexa.luci.LSSDPNodeDB
import com.libreAlexa.luci.LSSDPNodes
import com.libreAlexa.luci.LUCIControl
import com.libreAlexa.luci.LUCIPacket
import com.libreAlexa.luci.Utils
import com.libreAlexa.netty.LibreDeviceInteractionListner
import com.libreAlexa.netty.NettyData
import com.libreAlexa.util.LibreLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Locale


/**
 * Added ChromeCast Settings by SHAIK
 * 18/04/2023
 */
class CTDeviceSettingsActivity : CTDeviceDiscoveryActivity(), LibreDeviceInteractionListner,
    SendDataFragmentToActivity {
    private var currentLocale: String? = null
    private var currentDeviceNode: LSSDPNodes? = null
    private lateinit var luciControl: LUCIControl
    private var switchStatus: String? = null
    private var seekbarVolumeValue: String? = null

    private val currentDeviceIp by lazy {
        intent?.getStringExtra(Constants.CURRENT_DEVICE_IP)
    }

    private var awayModeSettingsDialog: AlertDialog? = null
    private lateinit var binding: CtDeviceSettingsBinding
    private var deviceUUID: String? = null
    private var tosStatus: String? = null
    private var crashReport: Boolean? = null
    private var mDeviceNameChanged: Boolean = false
    private lateinit var savedDeviceUUIDList: List<CastLiteUUIDDataClass>
    private var deviceName: String? = null

    companion object {
        private val TAG = CTDeviceSettingsActivity::class.java.simpleName
    }

    var mScanHandler: ScanningHandler = ScanningHandler.getInstance()
    private val libreVoiceDatabaseDao by lazy { LibreVoiceDatabase.getDatabase(this).castLiteDao() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CtDeviceSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fetchUUIDFromDB(currentDeviceIp!!)
    }

    override fun onStart() {
        super.onStart()
        luciControl = LUCIControl(currentDeviceIp)
        initViews()
        setListeners()
        lifecycleScope.launch {
            delay(2000)
            checkCastActivateStatus(currentDeviceIp)
        }
    }

    private fun initViews() {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        binding.toolbar.title = ""

        showLoader(binding.audioProgressBar.id)
        showLoader(binding.systemFirmwareProgressBar.id)
        showLoader(binding.hostFirmwareProgressBar.id)
        showLoader(binding.networkNameProgressBar.id)
        showLoader(binding.loginProgressBar.id)
        showLoader(binding.localeProgressBar.id) //        showLoader(soft_update_progress_bar.id)
        showLoader(binding.chromecastLoginProgressBar.id)  //ChromeCast Status

        Handler(Looper.getMainLooper()).postDelayed({
            if (isFinishing) return@postDelayed
            closeLoader(binding.audioProgressBar.id)
            closeLoader(binding.systemFirmwareProgressBar.id)
            closeLoader(binding.hostFirmwareProgressBar.id)
            closeLoader(binding.networkNameProgressBar.id)
            closeLoader(binding.loginProgressBar.id)
            closeLoader(binding.localeProgressBar.id)
            closeLoader(binding.chromecastLoginProgressBar.id)
        }, Constants.ITEM_CLICKED_TIMEOUT.toLong())

        currentDeviceNode = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(currentDeviceIp)

        binding.tvToolbarTitle.text = currentDeviceNode?.friendlyname
        deviceName=currentDeviceNode?.friendlyname
        binding.tvDeviceName.setText(deviceName)


        if (currentDeviceNode != null && !currentDeviceNode?.version.isNullOrEmpty()) {
            var dutExistingFirmware = currentDeviceNode?.version

            val arrayString =
                dutExistingFirmware?.split("\\.".toRegex())?.dropLastWhile { it.isEmpty() }
                    ?.toTypedArray()
            dutExistingFirmware =
                dutExistingFirmware?.substring(0, dutExistingFirmware.indexOf('.'))
            val dutExistingHostVersion = arrayString?.get(1)?.replace("[a-zA-z]".toRegex(), "")/*String mFirmwareVersionToDisplay = dutExistingFirmware.replaceAll("[a-zA-z]", "")+"."+arrayString[1]+"."+
                    arrayString[2];*/ //            tv_system_firmware.text = dutExistingFirmware.replace("[a-zA-z]".toRegex(), "")
            binding.tvSystemFirmware.text = currentDeviceNode?.version
            binding.tvHostFirmware.text = dutExistingHostVersion
            binding.tvMacAddress.text = Utils.convertToMacAddress(currentDeviceNode?.usn)
            closeLoader(binding.systemFirmwareProgressBar.id)
            closeLoader(binding.hostFirmwareProgressBar.id)
        }

        binding.tvIpAddress.text = currentDeviceNode?.ip

        /*this is the data for Audio Presets*/
        val presetSpinnerData = resources.getStringArray(R.array.audio_preset_array)
        val presetDataAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, presetSpinnerData)
        presetDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        /*Speaker type data*/
        val spinnerData = resources.getStringArray(R.array.audio_output_array)
        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerData)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        closeLoader(binding.softUpdateProgressBar.id)

        val ssid = AppUtils.getConnectedSSID(this)

        if (currentDeviceNode != null) {
            if (currentDeviceNode?.getmDeviceCap()?.getmSource()?.isAlexaAvsSource!! && ssid != null && !isConnectedToSAMode(ssid)) {
                binding.llAlexaSettings.visibility = View.VISIBLE
            } else {
                closeLoader(binding.loginProgressBar.id)
                closeLoader(binding.localeProgressBar.id)
                binding.llAlexaSettings.visibility = View.GONE
            }
            if (currentDeviceNode?.getmDeviceCap()?.getmSource()?.isGoogleCast!! && ssid != null && !isConnectedToSAMode(ssid)) {
                binding.llTopChromecastSettings.visibility = View.VISIBLE
            } else {
                binding.llTopChromecastSettings.visibility = View.GONE
            }
        }
        toggleTunnelingVisibility(show = TunnelingControl.isTunnelingClientPresent(currentDeviceIp))
    }

    private fun setListeners() {
        val lssdpNodes = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(currentDeviceIp)

        binding.tvAmazonLogin.setOnClickListener {

            LibreLogger.d(TAG, "suma in get the alexa login status\n" + lssdpNodes.alexaRefreshToken)

            if (lssdpNodes.getmDeviceCap().getmSource().isAlexaAvsSource) {
                if (lssdpNodes.alexaRefreshToken != null) if (/*lssdpNodes.alexaRefreshToken == null ||*/ lssdpNodes.alexaRefreshToken.isEmpty() || lssdpNodes.alexaRefreshToken == "0") {
                    LibreLogger.d(TAG, "suma in get the alexa NOLOGIN\n" + lssdpNodes.alexaRefreshToken)

                    startActivity(Intent(this@CTDeviceSettingsActivity, CTAmazonLoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        putExtra(Constants.CURRENT_DEVICE_IP, currentDeviceIp)
                        putExtra(Constants.FROM_ACTIVITY, CTDeviceSettingsActivity::class.java.simpleName)
                    })
                } else {
                    LibreLogger.d(TAG, "suma in get the alexa \n" + lssdpNodes.alexaRefreshToken)

                    startActivity(Intent(this@CTDeviceSettingsActivity, CTAlexaThingsToTryActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        putExtra(Constants.CURRENT_DEVICE_IP, currentDeviceIp)
                        putExtra(Constants.FROM_ACTIVITY, CTDeviceSettingsActivity::class.java.simpleName)
                    })
                }
            } else {
                showToast(getString(R.string.alexa_not_supported))
            }
        }

        binding.tvAlexaLocale.setOnClickListener {
            CTAlexaLocaleDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.CURRENT_LOCALE, currentLocale)
                }
                show(supportFragmentManager, this::class.java.simpleName)
            }

        }

        binding.switchSpeechVolumeFollow.setOnCheckedChangeListener { compoundButton, b ->
            val luciControl = LUCIControl(currentDeviceIp)
            if (b) {
                binding.tvSwitchStatus.text =
                    getText(R.string.on).toString().uppercase(Locale.getDefault())
                binding.speechVolume.visibility = View.INVISIBLE
                switchStatus = "1"
                luciControl.SendCommand(MIDCONST.MID_MIC, "SV:$seekbarVolumeValue,$switchStatus", LSSDPCONST.LUCI_SET)
                LibreLogger.d(TAG, "suma in ct device setting on $currentDeviceIp")

            } else {
                binding.tvSwitchStatus.text =
                    getText(R.string.off).toString().uppercase(Locale.getDefault())
                switchStatus = "0"
                luciControl.SendCommand(MIDCONST.MID_MIC, "SV:$seekbarVolumeValue,$switchStatus", LSSDPCONST.LUCI_SET)
                LibreLogger.d(TAG, "suma in ct device setting off")
                binding.speechVolume.visibility = View.VISIBLE

            }
        }

        binding.seekBarVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                LibreLogger.d(TAG, "Seekbar Position track " + seekBar.progress + "  " + seekBar.max)
                binding.tvVolumeValue.text = "" + progress + "dB"
                seekbarVolumeValue = "" + progress
                if (fromUser) {
                    val luciControl = LUCIControl(currentDeviceIp)
                    luciControl.SendCommand(MIDCONST.MID_MIC, "SV:$seekbarVolumeValue,$switchStatus", 2)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                LibreLogger.d(TAG, "Seekbar Position trackstop" + seekBar.progress + "  " + seekBar
                    .max)
            }
        })
        binding.btnForgetNetwork.setOnClickListener {
            isForgetNetworkCalled=true
            showAwayModeAlert(resources.getString(R.string.forget_network) , resources.getString(R.string.forget_network_confirmation),true)
        }

        binding.seekBarBass.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {/*LibreLogger.d(TAG, "seek_bar_bass " + seekBar.progress + "  " + seekBar.max)
                tv_bass_value.text = "${progress-5}dB"

                TunnelingControl(currentDeviceIp).sendCommand(PayloadType.BASS_VOLUME, (progress-5).toByte())*/
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                LibreLogger.d(TAG, "seek_bar_bass " + seekBar.progress + "  " + seekBar.max) //
                //            tv_bass_value.text = "${seekBar.progress - 5}dB"
                TunnelingControl(currentDeviceIp).sendCommand(PayloadType.BASS_VOLUME, seekBar.progress.toByte())
            }
        })

        binding.seekBarTreble.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {/*LibreLogger.d(TAG, "seek_bar_treble " + seekBar.progress + "  " + seekBar.max)
                tv_treble_value.text = "${progress-5}dB"

                TunnelingControl(currentDeviceIp).sendCommand(PayloadType.TREBLE_VOLUME, (progress-5).toByte())*/
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                LibreLogger.d(TAG, "seek_bar_treble " + seekBar.progress + "  " + seekBar.max) //
                //              tv_treble_value.text = "${seekBar.progress - 5}dB"
                TunnelingControl(currentDeviceIp).sendCommand(PayloadType.TREBLE_VOLUME, seekBar.progress.toByte())
            }
        })

        binding.tvEditAwayMode.setOnClickListener {
            showAwayModeAlert(binding.tvNetworkName.text.toString(), binding.tvWifiPwd.text.toString(),false)
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.tvAudioOutput.setOnClickListener {
            if (binding.tvAudioOutput.text?.toString().isNullOrEmpty()) return@setOnClickListener

            val audioOutput = binding.tvAudioOutput.text?.toString()

            CTAudioOutputDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.AUDIO_OUTPUT, audioOutput)
                }
                show(supportFragmentManager, this::class.java.simpleName)
            }
        }
        binding.llChromecastSettings.setOnClickListener {
            //If the status is empty
            if (binding.tvChromecastStatus.text.toString().isEmpty()) return@setOnClickListener
            callActivateCastActivity()

        }
        binding.tvChromecastStatus.setOnClickListener {
            if (binding.tvChromecastStatus.text.toString().isEmpty()) return@setOnClickListener
            callActivateCastActivity()
        }
        //SHAIK Edit Device and Time Zone New Changes
        binding.btnEditSpeakerName.setOnClickListener {
            editDeviceName()
        }
        binding.txtTimeZone.setOnClickListener {
            if (binding.txtTimeZone.text?.toString().isNullOrEmpty()) return@setOnClickListener
            val timeZoneFragment: TimeZoneFragment = TimeZoneFragment.newInstance(currentDeviceIp)
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.lay_timeZone, timeZoneFragment).addToBackStack("tag")
                .commit()
        }
        val mTextWatching = arrayOfNulls<String>(1)
        binding.tvDeviceName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (binding.tvDeviceName.text.toString().toByteArray().size == 50 && binding.tvDeviceName.isEnabled) {
                    Toast.makeText(this@CTDeviceSettingsActivity, getString(R.string.deviceLengthReached), Toast.LENGTH_SHORT).show()
                }
                if (binding.tvDeviceName.text.toString().toByteArray().size > 50) {
                    if (mTextWatching[0]!!.isEmpty() || mTextWatching[0] == null) {
                        binding.tvDeviceName.setText(utf8truncate(binding.tvDeviceName.text.toString(), 50))
                        binding.tvDeviceName.setSelection(mTextWatching[0]!!.length)
                    } else {
                        binding.tvDeviceName.setText(mTextWatching[0])
                        binding.tvDeviceName.setSelection(mTextWatching[0]!!.length)
                    }
                    Toast.makeText(this@CTDeviceSettingsActivity, getString(R.string.deviceLength), Toast.LENGTH_SHORT).show()
                } else {
                    mTextWatching[0] = binding.tvDeviceName.text.toString()
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                mDeviceNameChanged = true
            }
        })
        binding.tvDeviceName.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                val DRAWABLE_RIGHT = 2
                if (event.action == MotionEvent.ACTION_UP) {
                    try {
                        if (event.rawX >= binding.tvDeviceName.right - binding.tvDeviceName.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                            // your action here
                            binding.tvDeviceName.setText("")
                            return true
                        }
                    } catch (e: Exception) {
                        LibreLogger.d(TAG, "ignore this log")
                    }
                }
                return false
            }
        })
    }
    private fun callActivateCastActivity() {
        val goToActivateCastActivity =
            Intent(this@CTDeviceSettingsActivity, ActivateCastActivity::class.java)
        goToActivateCastActivity.putExtra(Constants.CURRENT_DEVICE_IP, currentDeviceIp)
        goToActivateCastActivity.putExtra(Constants.CAST_STATUS, tosStatus)
        goToActivateCastActivity.putExtra(Constants.CURRENT_DEVICE_UUID, deviceUUID)
        goToActivateCastActivity.putExtra(Constants.CRASH_REPORT, crashReport)
        goToActivateCastActivity.putExtra(Constants.FROM_ACTIVITY, CTDeviceSettingsActivity::class.java.simpleName)
        goToActivateCastActivity.putExtra(Constants.DEVICE_NAME, currentDeviceNode?.friendlyname)
        startActivity(goToActivateCastActivity)
    }

    override fun onResume() {
        super.onResume()
        registerForDeviceEvents(this)
        requestLuciUpdates()
        TunnelingControl(currentDeviceIp).sendDataModeCommand()
        AlexaUtils.sendAlexaRefreshTokenRequest(currentDeviceIp)
        binding.tvChromecastStatus.text=""

    }

    private fun showLoader(progressBarId: Int) {
        findViewById<ProgressBar>(progressBarId).visibility = View.VISIBLE
        when (progressBarId) {
            R.id.audio_progress_bar -> binding.tvAudioOutput.visibility = View.INVISIBLE
            R.id.system_firmware_progress_bar -> binding.tvSystemFirmware.visibility =
                View.INVISIBLE

            R.id.host_firmware_progress_bar -> binding.tvHostFirmware.visibility = View.INVISIBLE
            R.id.network_name_progress_bar -> binding.tvNetworkName.visibility = View.INVISIBLE
            R.id.login_progress_bar -> binding.tvAmazonLogin.visibility = View.INVISIBLE
            R.id.locale_progress_bar -> binding.tvAlexaLocale.visibility = View.INVISIBLE
            R.id.soft_update_progress_bar -> binding.tvSoftUpdate.visibility = View.INVISIBLE
        }
    }

    private fun closeLoader(progressBarId: Int) {
        findViewById<ProgressBar>(progressBarId).visibility = View.INVISIBLE
        when (progressBarId) {
            R.id.audio_progress_bar -> binding.tvAudioOutput.visibility = View.VISIBLE
            R.id.system_firmware_progress_bar -> binding.tvSystemFirmware.visibility = View.VISIBLE
            R.id.host_firmware_progress_bar -> binding.tvHostFirmware.visibility = View.VISIBLE
            R.id.network_name_progress_bar -> binding.tvNetworkName.visibility = View.VISIBLE
            R.id.login_progress_bar -> binding.tvAmazonLogin.visibility = View.VISIBLE
            R.id.locale_progress_bar -> binding.tvAlexaLocale.visibility = View.VISIBLE
            R.id.soft_update_progress_bar -> binding.tvSoftUpdate.visibility = View.VISIBLE

        }
    }

    override fun deviceDiscoveryAfterClearingTheCacheStarted() {

    }

    override fun newDeviceFound(node: LSSDPNodes) {

    }

    override fun deviceGotRemoved(ipaddress: String?) {
        if (ipaddress != null && currentDeviceIp != null && ipaddress == currentDeviceIp) {
            intentToHome(this)
        }
    }

    override fun messageRecieved(nettyData: NettyData) {

        val remoteDeviceIp = nettyData.getRemotedeviceIp()

        val packet = LUCIPacket(nettyData.getMessage())
        LibreLogger.d(TAG, "Message received for ipaddress  in DeviceSetting" + remoteDeviceIp + ", command is " + packet.command + "msg is\n" + String(packet.payload))

        if (currentDeviceIp!! == remoteDeviceIp) {

            LibreLogger.d(TAG, "Command = " + packet.command + ", payload msg = ${String(packet.payload)}")
            when (packet.command) {

                MIDCONST.MID_ENV_READ -> {
                    val message = String(packet.payload)
                    LibreLogger.d(TAG, "208 ENV READ SUMA in Device Setting " + packet.command + message)

                    if (message.contains("ddms_SSID") /*ddms_SSID:RIVAACONCERT*/) {
                        closeLoader(binding.networkNameProgressBar.id)
                        binding.tvNetworkName.text = message.substring(message.indexOf(":") + 1)
                    }

                    if (message.contains("ddms_password") /*ddms_password:12345678*/) {
                        binding.tvWifiPwd.text = message.substring(message.indexOf(":") + 1)
                    }

                    if (message.contains("CurrentLocale") /*CurrentLocale:en-US*/) {
                        currentLocale = message.substring(message.indexOf(":") + 1)
                        updateLang()
                    }

                    if (message.contains("speechvolume") /*speechvolume:8,1*/) {
                        val speechvolume = message.substring(message.indexOf(":") + 1)
                        val mNode =
                            LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(remoteDeviceIp)
                        if (mNode != null) {
                            mNode.speechVolume = speechvolume
                            var splitSpeechVolume: Array<String>? = null
                            if (!mNode.speechVolume.isNullOrEmpty()) {
                                LibreLogger.d(TAG, "mnNode.getSpeechVolume(): " + mNode.speechVolume)
                                splitSpeechVolume = mNode.speechVolume.split(",".toRegex())
                                    .dropLastWhile { it.isEmpty() }.toTypedArray()

                                val size: Int =
                                    splitSpeechVolume.size/*SUMA : Preventing array index out of bound*/
                                try {
                                    for (i in 0 until size) {
                                        seekbarVolumeValue = splitSpeechVolume[0]
                                        switchStatus = splitSpeechVolume[1]
                                        LibreLogger.d(TAG,"speechvolume seekbarVolumeValue: " + seekbarVolumeValue + "switchStatus: " + switchStatus)

                                        if (switchStatus != null) {
                                            when {
                                                switchStatus!!.equals("-1", ignoreCase = true) -> {
                                                    switchStatus = "0"
                                                    binding.tvSwitchStatus.text = getText(R.string.off).toString().uppercase(Locale.getDefault())
                                                }

                                                switchStatus!!.equals("0", ignoreCase = true) -> {
                                                    binding.switchSpeechVolumeFollow.isChecked =
                                                        false
                                                    binding.tvSwitchStatus.text = getText(R.string.off).toString().uppercase(Locale.getDefault())
                                                    LibreLogger.d(TAG, "suma in ct device setting on switch status")

                                                }

                                                switchStatus!!.equals("1", ignoreCase = true) -> {
                                                    binding.switchSpeechVolumeFollow.isChecked =
                                                        true
                                                    binding.tvSwitchStatus.text = getText(R.string.on).toString().uppercase(Locale.getDefault())
                                                    val luciControl = LUCIControl(currentDeviceIp)
                                                    LibreLogger.d(TAG, "suma in ct device setting off switch status")

                                                    luciControl.SendCommand(MIDCONST.MID_MIC, "SV:$seekbarVolumeValue,$switchStatus", LSSDPCONST.LUCI_SET)
                                                }
                                            }
                                        }

                                        if (seekbarVolumeValue != null) {
                                            binding.seekBarVolume.progress =
                                                seekbarVolumeValue?.toInt()!!
                                        } else {
                                            binding.seekBarVolume.progress = 0
                                        }
                                        binding.tvVolumeValue.text =
                                            "${binding.seekBarVolume.progress}dB"
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    LibreLogger.d(TAG, "Exception occurred while finding speechVlume Array")
                                }

                            }
                        }
                    }

                    if (message.contains("AlexaRefreshToken")) {
                        closeLoader(binding.loginProgressBar.id)
                        val token = message.substring(message.indexOf(":") + 1)
                        val mNode = LSSDPNodeDB.getInstance()
                            .getTheNodeBasedOnTheIpAddress(nettyData.getRemotedeviceIp())
                        if (mNode != null) {
                            mNode.alexaRefreshToken = token

                        }
//                        if (token.isEmpty()) binding.tvAmazonLogin.text =
//                            getString(R.string.logged_out)
//                        else binding.tvAmazonLogin.text = getString(R.string.logged_in)

                        if (mNode.getmDeviceCap()!=null && mNode.getmDeviceCap().getmSource()
                            .isAlexaAvsSource) {
                            if (mNode.alexaRefreshToken == null || mNode.alexaRefreshToken.isEmpty() || mNode.alexaRefreshToken == "0") {
                                binding.tvAmazonLogin.text = getString(R.string.logged_out)
                            } else {
                                binding.tvAmazonLogin.text = getString(R.string.logged_in)
                            }
                        } else {
                            binding.tvAmazonLogin.text = getString(R.string.alexa_not_supported)

                        }
                    }

                    val messageArray =
                        message.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                    if (messageArray.isEmpty()) return
                    val audioPreset = messageArray[0]
                    var status = ""
                    if (messageArray.size > 1) {
                        status = messageArray[1]
                    }
                    if (audioPreset != null && audioPreset.equals("audiopreset", ignoreCase = true) && !status.equals("0", ignoreCase = true)) {
                        return
                    }
                }

                91 -> {
                    val macID = String(packet.getpayload())
                    LibreLogger.d(TAG, "suma  mac is id $macID")
                }

                145 -> {
                    val audioMessage = String(packet.getpayload())
                    LibreLogger.d(TAG,"AudioPresetValue Received Data is - $audioMessage")
                }

                MIDCONST.CAST_ACCEPT_STATUS,
                MIDCONST.CAST_ACCEPT_STATUS_572 -> {
                    val message = String(packet.getpayload())
                    if (message.isNotEmpty()) {
                        val root = JSONObject(message)
                        crashReport = if(root.has("crash_report")) {
                            root.getBoolean("crash_report")
                        }else{
                            false
                        }
                        tosStatus = if(root.has("tos")){
                            root.getString("tos")
                        }else{
                            ""
                        }

                        val castStatus = tosStatus!!.substring(0, 1).uppercase(Locale.getDefault()) + tosStatus!!.substring(1).lowercase(Locale.getDefault()).replace("_", " ")
                        binding.tvChromecastStatus.text = castStatus
                        closeLoader(binding.chromecastLoginProgressBar.id)
                    }else{
                        LibreLogger.d(TAG,"Message is Empty")
                    }
                }
                MIDCONST.UPDATE_TIMEZONE_DUMMY,
                MIDCONST.UPDATE_TIMEZONE, -> {
                    val message = String(packet.getpayload())
                    try {
                        if (message.isNotEmpty()) {
                            binding.txtTimeZone.text = message
                        } else {
                            binding.txtTimeZone.text = "Time Zone Not Available"
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            }

        }

    }

    private fun requestLuciUpdates() {
        val luciPackets = ArrayList<LUCIPacket>()

        val ddmsSSIDLUCIPacket =
            LUCIPacket(LUCIMESSAGES.READ_DDMS_SSID.toByteArray(), LUCIMESSAGES.READ_DDMS_SSID.length.toShort(), MIDCONST.MID_ENV_READ.toShort(), LSSDPCONST.LUCI_GET.toByte())
        val ddmsPwdLUCIPacket =
            LUCIPacket(LUCIMESSAGES.READ_DDMS_PWD.toByteArray(), LUCIMESSAGES.READ_DDMS_PWD.length.toShort(), MIDCONST.MID_ENV_READ.toShort(), LSSDPCONST.LUCI_GET.toByte())
        val currentLocaleLUCIPacket =
            LUCIPacket(LUCIMESSAGES.READ_CURRENT_LOCALE.toByteArray(), LUCIMESSAGES.READ_CURRENT_LOCALE.length.toShort(), MIDCONST.MID_ENV_READ.toShort(), LSSDPCONST.LUCI_GET.toByte())

        val alexaRefreshTokenPacket =
            LUCIPacket(LUCIMESSAGES.READ_ALEXA_REFRESH_TOKEN_MSG.toByteArray(), LUCIMESSAGES.READ_ALEXA_REFRESH_TOKEN_MSG.length.toShort(), MIDCONST.MID_ENV_READ.toShort(), LSSDPCONST.LUCI_GET.toByte())

        val readSpeechVolumePacket =
            LUCIPacket(LUCIMESSAGES.READ_SPEECH_VOLUME.toByteArray(), LUCIMESSAGES.READ_SPEECH_VOLUME.length.toShort(), MIDCONST.MID_ENV_READ.toShort(), LSSDPCONST.LUCI_GET.toByte())

        val timeZone = LUCIPacket(null, 0.toShort(), MIDCONST.UPDATE_TIMEZONE.toShort(), LSSDPCONST.LUCI_GET.toByte())

        luciPackets.add(ddmsSSIDLUCIPacket)
        luciPackets.add(ddmsPwdLUCIPacket)
        luciPackets.add(readSpeechVolumePacket)
        luciPackets.add(timeZone)
        if (currentDeviceNode != null) {
            if (currentDeviceNode?.getmDeviceCap()?.getmSource()?.isAlexaAvsSource!!) {
                luciPackets.add(currentLocaleLUCIPacket)
                luciPackets.add(alexaRefreshTokenPacket)
            }
        }
        luciControl.SendCommand(luciPackets)
        //luciControl.SendCommand(MIDCONST.TEST_561, null, LSSDPCONST.LUCI_GET)
    }

    private fun updateLang() {
        when (currentLocale) {
            LibreAlexaConstants.Languages.ENG_US -> {
                binding.tvAlexaLocale.text = getString(R.string.engUSLang)
            }

            LibreAlexaConstants.Languages.ENG_GB -> {
                binding.tvAlexaLocale.text = getString(R.string.engUKLang)
            }

            LibreAlexaConstants.Languages.DE -> {
                binding.tvAlexaLocale.text = getString(R.string.deutschLang)
            }

            LibreAlexaConstants.Languages.FR -> {
                binding.tvAlexaLocale.text = getString(R.string.frenchLang)
            }

            LibreAlexaConstants.Languages.IT -> {
                binding.tvAlexaLocale.text = getString(R.string.italyLang)
            }

            LibreAlexaConstants.Languages.ES -> {
                binding.tvAlexaLocale.text = getString(R.string.spainLang)
            }

        }
        closeLoader(binding.localeProgressBar.id)
    }

    fun sendUpdatedLangToDevice(selectedLang: String) {
        currentLocale = selectedLang
        updateLang()
        luciControl.SendCommand(MIDCONST.ALEXA_COMMAND.toInt(), LUCIMESSAGES.UPDATE_LOCALE + currentLocale, LSSDPCONST.LUCI_SET)
    }

    fun updateAudioOutputOfDevice(aqModeSelect: AQModeSelect) {
        showLoader(binding.audioProgressBar.id) //        tv_audio_output?.text = aqModeSelect.name
        TunnelingControl(currentDeviceIp).sendCommand(PayloadType.AQ_MODE_SELECT, aqModeSelect.value.toByte())
    }

    private fun showAwayModeAlert(ssid: String, pwd: String, isForgetNetwork: Boolean) {
        if (awayModeSettingsDialog != null && awayModeSettingsDialog?.isShowing!!) awayModeSettingsDialog?.dismiss()

        val builder = AlertDialog.Builder(this)
        val binding = CtDlgFragmentEditAwayModeBinding.inflate(layoutInflater)
        builder.setView(binding.root)
        builder.setCancelable(false)
        if(!isForgetNetwork) {
            binding.layEditText.visibility=View.VISIBLE
            binding.etNetworkName.apply {
                setText(ssid)
                post {
                    setSelection(length())
                }
            }

            binding.etPwd.apply {
                setText(pwd)
                post {
                    setSelection(length())
                }
            }
        }else{
          binding.layTxtView.visibility=View.VISIBLE
          binding.txtHeader.text=ssid
          binding.txtMessage.text=pwd
          binding.btnOk.text=this.resources.getText(R.string.confirm)
        }

        binding.btnOk.setOnClickListener {
            if(!isForgetNetwork) {
                if (binding.etNetworkName.text?.isEmpty()!!) {
                    showToast("Enter network name")
                    return@setOnClickListener
                }

                if (binding.etPwd.text?.isEmpty()!!) {
                    showToast("Enter password")
                    return@setOnClickListener
                }

                if (binding.etNetworkName.text.toString() != ssid || binding.etPwd.text.toString() != pwd) {
                    closeKeyboard(this, it)/*Write env items to device*/
                    writeAwayModeSettingsToDevice(binding.etNetworkName.text.toString().trim(), binding.etPwd.text.toString().trim(), currentDeviceIp!!)
                    awayModeSettingsDialog?.dismiss()
                }
            }else{
                if(currentDeviceIp!=null) {
                    LUCIControl.SendCommandWithIp(MIDCONST.FORGET_NETWORK, "0", LSSDPCONST.LUCI_SET, currentDeviceIp)
                  if(awayModeSettingsDialog!=null) {
                      awayModeSettingsDialog!!.dismiss()
                  }
                    runOnUiThread {
                        showProgressDialog("Please Wait")
                    }
                    lifecycleScope.launch(Dispatchers.IO) {
                        delay(10000)
                        removeTheDeviceFromRepo(currentDeviceIp)
                        intentToHome(this@CTDeviceSettingsActivity)
                    }
                }else{
                    LibreLogger.d(TAG,"Ip address null while rebooting the device")
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            closeKeyboard(this, it)
            awayModeSettingsDialog?.dismiss()
        }

        if (awayModeSettingsDialog == null || !awayModeSettingsDialog?.isShowing!!) awayModeSettingsDialog =
            builder.create()
        awayModeSettingsDialog?.show()

    }

    override fun onStop() {
        super.onStop()
        unRegisterForDeviceEvents()
    }

    override fun tunnelDataReceived(tunnelingData: TunnelingData) {
        super.tunnelDataReceived(tunnelingData)
        if (tunnelingData.remoteClientIp == currentDeviceIp && tunnelingData.remoteMessage.size >= 24) {
            val tcpTunnelPacket = TCPTunnelPacket(tunnelingData.remoteMessage)

            LibreLogger.d(TAG, "tunnelDataReceived, ip ${tunnelingData.remoteClientIp} treble ${tcpTunnelPacket.trebleValue}")
            if (tcpTunnelPacket.trebleValue >= 0) {
                binding.tvTrebleValue.text = "${tcpTunnelPacket.trebleValue - 5}dB"
                binding.seekBarTreble.progress = tcpTunnelPacket.trebleValue
                binding.seekBarTreble.max = 10
            }

            LibreLogger.d(TAG, "tunnelDataReceived, ip ${tunnelingData.remoteClientIp} bass ${tcpTunnelPacket.bassValue}")
            if (tcpTunnelPacket.bassValue >= 0) {
                binding.tvBassValue.text = "${tcpTunnelPacket.bassValue - 5}dB"
                binding.seekBarBass.progress = tcpTunnelPacket.bassValue
                binding.seekBarBass.max = 10
            }

            binding.tvAudioOutput.text = tcpTunnelPacket.aqMode?.name
            closeLoader(binding.audioProgressBar.id)

        }
    }

    private fun toggleTunnelingVisibility(show: Boolean) {
        if (show) binding.llTunnelingControls.visibility = View.VISIBLE
        else binding.llTunnelingControls.visibility = View.GONE
    }

    private fun fetchUUIDFromDB(speakerIpAddress: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            savedDeviceUUIDList = libreVoiceDatabaseDao.getAllDeviceUUID()
            LibreLogger.d(TAG, "fetchUUIDFromDB:uuid size ${savedDeviceUUIDList.size}")
        }
    }

    private fun checkCastActivateStatus(currentDeviceIp: String?) {
        deviceUUID = getUUIDWithIP(currentDeviceIp)
        LibreLogger.d(TAG, "SendCommand:UUID $deviceUUID and $currentDeviceIp")
        val postData = JSONObject()
        postData.put(LUCIMESSAGES.REQUEST_TYPE, "get")
        postData.put(LUCIMESSAGES.ID, "status")
        postData.put(LUCIMESSAGES.DEVICE_UUID, deviceUUID)
        val control = LUCIControl(currentDeviceIp)
        control.SendCommand(MIDCONST.TOS_ACCEPT_REQUEST, postData.toString(), LSSDPCONST.LUCI_SET)
    }

    private fun getUUIDWithIP(currentDeviceIp: String?): String? {
        if (::savedDeviceUUIDList.isInitialized) {
            for (i in savedDeviceUUIDList.indices) {
                if (savedDeviceUUIDList[i].deviceIP == currentDeviceIp) {
                    deviceUUID = savedDeviceUUIDList[i].deviceUuid
                    break
                } else {
                    deviceUUID = ""
                }
            }
            return deviceUUID
        }
        return deviceUUID
    }
    private fun editDeviceName() {
        val nodes = LSSDPNodeDB.getInstance().getTheNodeBasedOnTheIpAddress(currentDeviceIp)
        if (!binding.tvDeviceName.isEnabled) {
            binding.btnEditSpeakerName.setImageResource(R.drawable.check)
            binding.tvDeviceName.isClickable = true
            binding.tvDeviceName.isEnabled = true
            binding.tvDeviceName.isFocusableInTouchMode = true
            binding.tvDeviceName.isFocusable = true
            binding.tvDeviceName.requestFocus()
            binding.tvDeviceName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cancwel, 0)
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.tvDeviceName, InputMethodManager.SHOW_IMPLICIT)
        } else {
            binding.btnEditSpeakerName.setImageResource(R.drawable.baseline_edit_24)
            binding.tvDeviceName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            if (nodes != null && nodes.friendlyname.equals(binding.tvDeviceName.text.toString())) {
                LibreLogger.d(TAG, "No need to update since both names are same")
                binding.tvDeviceName.isClickable = false
                binding.tvDeviceName.isEnabled = false
                mDeviceNameChanged = false
                return
            }
            if (mDeviceNameChanged) {
                if (binding.tvDeviceName.text.toString() != "" && !binding.tvDeviceName.text.toString().trim { it <= ' ' }.equals("NULL", ignoreCase = true)) {
                    if (binding.tvDeviceName.text.toString().toByteArray().size > 50) {
                        android.app.AlertDialog.Builder(this@CTDeviceSettingsActivity)
                            .setTitle(getString(R.string.deviceNameChanging))
                            .setMessage(getString(R.string.deviceLength))
                            .setPositiveButton(R.string.yes) { dialog, which -> dialog.cancel()
                            }.setIcon(android.R.drawable.ic_dialog_alert)
                            .show()
                        return
                    } else {
                        val mLuci = LUCIControl(currentDeviceIp)
                        mLuci.SendCommand(MIDCONST.MID_DEVNAME, binding.tvDeviceName.text.toString(), LSSDPCONST.LUCI_SET)
                        UpdateLSSDPNodeDeviceName(currentDeviceIp, binding.tvDeviceName.text.toString())
                        binding.tvDeviceName.isClickable = false
                        binding.tvDeviceName.isEnabled = false
                    }
                } else {
                    if (!this@CTDeviceSettingsActivity.isFinishing) {
                        if (binding.tvDeviceName.text.toString() == "") {
                            android.app.AlertDialog.Builder(this@CTDeviceSettingsActivity)
                                .setTitle(getString(R.string.deviceNameChanging))
                                .setMessage(getString(R.string.deviceNameEmpty))
                                .setPositiveButton(R.string.yes) { dialog, which -> dialog.cancel() }.setIcon(android.R.drawable.ic_dialog_alert)
                                .show()
                        }
                    }
                }
            }
        }

    }


    private fun UpdateLSSDPNodeDeviceName(ipaddress: String?, mDeviceName: String) {
        val mToBeUpdateNode: LSSDPNodes = mScanHandler.getLSSDPNodeFromCentralDB(ipaddress)
        val mNodeDB = LSSDPNodeDB.getInstance()
        if (mToBeUpdateNode != null) {
            mToBeUpdateNode.friendlyname = mDeviceName
            mNodeDB.renewLSSDPNodeDataWithNewNode(mToBeUpdateNode)
        }else{
            LibreLogger.d(TAG,"else condition")
        }
    }
    fun utf8truncate(input: String, length: Int): String {
        val result = StringBuffer(length)
        var resultlen = 0
        for (i in 0 until input.length) {
            val c = input[i]
            var charlen = 0
            if (c.code <= 0x7f) {
                charlen = 1
            } else if (c.code <= 0x7ff) {
                charlen = 2
            } else if (c.code <= 0xd7ff) {
                charlen = 3
            } else if (c.code <= 0xdbff) {
                charlen = 4
            } else if (c.code <= 0xdfff) {
                charlen = 0
            } else if (c.code <= 0xffff) {
                charlen = 3
            }
            if (resultlen + charlen > length) {
                break
            }
            result.append(c)
            resultlen += charlen
        }
        return result.toString()
    }

    override fun communicate(comm: String?) {
        LibreLogger.d(TIMEZONE_UPDATE,"Got the data from Activity $comm")
        binding.txtTimeZone.text=comm
    }
}
