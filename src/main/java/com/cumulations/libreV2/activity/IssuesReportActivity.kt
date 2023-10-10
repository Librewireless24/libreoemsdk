package com.cumulations.libreV2.activity

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import com.cumulations.libreV2.AppUtils
import com.libreAlexa.R
import com.libreAlexa.constants.Constants
import com.libreAlexa.constants.LSSDPCONST
import com.libreAlexa.constants.LUCIMESSAGES.*
import com.libreAlexa.constants.MIDCONST
import com.libreAlexa.databinding.ActivityIssuesReportBinding
import com.libreAlexa.luci.LUCIControl
import com.libreAlexa.util.LibreLogger
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.UUID


class IssuesReportActivity : CTDeviceDiscoveryActivity() {
    private lateinit var binding: ActivityIssuesReportBinding
    private val currentDeviceIp by lazy {
        intent?.getStringExtra(Constants.CURRENT_DEVICE_IP)
    }
    private val currentDeviceName by lazy {
        intent?.getStringExtra(Constants.DEVICE_NAME)
    }
    private val maxCharLimit = 250 // Set your character limit here
    private var selectedTime = ""
    private var uuid: String? = null
    val TAG = "IssuesReportActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIssuesReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uuid = UUID.randomUUID().toString()
        binding.txtHeader.text = getString(R.string.please_share_details) + " " + currentDeviceName
        binding.txtCharCount.text = "$maxCharLimit ${getString(R.string.characters)}"
        binding.edtIssueReport.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val charCount = s?.length ?: 0
                if (binding.edtIssueReport.text.toString().trim().isNotEmpty()) {
                    binding.txtCharCount.text = "$charCount / $maxCharLimit ${getString(R.string.characters_remaining)}"
                }else{
                    showToast(getString(R.string.descriptionNameEmpty))
                }
            }
        })
        binding.btnBack.setOnClickListener {
            finish()
        }
        val feelings = resources.getStringArray(R.array.timeframes)
        val arrayAdapter = ArrayAdapter(this, R.layout.report_issue_timeframe_list, feelings)
        binding.txtTimeframe.setAdapter(arrayAdapter)
        selectedTime = binding.txtTimeframe.text.toString()
        binding.txtTimeframe.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            selectedTime = parent.getItemAtPosition(position).toString()
        }
        binding.btnSubmitIssue.setOnClickListener {
            if (binding.edtIssueReport.text.toString().trim().isNotEmpty()) {
                val userComment = binding.edtIssueReport.text.toString()
                val manufacturer = Build.MANUFACTURER
                val model = Build.MODEL
                val appVersion = AppUtils.getVersion(applicationContext)
                val calendar = Calendar.getInstance()
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z")
                val dateTime = simpleDateFormat.format(calendar.time).toString()
                if (currentDeviceIp != null && uuid != null) {
                    val createAppJSON = JSONObject()
                    createAppJSON.put(BRAND, manufacturer)
                    createAppJSON.put(MODEL, model)
                    createAppJSON.put(APP_VERSION, appVersion)
                    createAppJSON.put(DATE_TIME, dateTime)

                    val postData = JSONObject()
                    postData.put(SOURCE, "App")
                    postData.put(RANDOM_UUID, uuid)
                    postData.put(DESCRIPTION, userComment)
                    postData.put(TIME, selectedTime)
                    postData.put(APP_INFO, createAppJSON.toString())
                    sendLuciCommand(postData.toString(), currentDeviceIp!!)
                    binding.layLoader.visibility = View.VISIBLE
                    binding.layMain.visibility = View.GONE
                } else {
                    LibreLogger.d(TAG, "currentIpAddress or device uuid is null")
                }
            } else {
                showToast(getString(R.string.descriptionNameEmpty))
            }

        }
    }

    private fun sendLuciCommand(data: String, currentDeviceIp: String) {
        val control = LUCIControl(currentDeviceIp)
        control.SendCommand(MIDCONST.ISSUE_REPORT, data, LSSDPCONST.LUCI_SET)
        lifecycleScope.launch {
            showToast("Report Sent")
            delay(2000)
            finish()
        }
    }

}