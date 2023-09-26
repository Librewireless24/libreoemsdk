package com.cumulations.libreV2.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cumulations.libreV2.SendDataFragmentToActivity
import com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity.Companion.TIMEZONE_UPDATE
import com.cumulations.libreV2.activity.CTDeviceSettingsActivity
import com.cumulations.libreV2.adapter.TimeZoneListAdapter
import com.cumulations.libreV2.model.TimeZoneDataClass
import com.libreAlexa.R
import com.libreAlexa.constants.LSSDPCONST
import com.libreAlexa.constants.LUCIMESSAGES.HOUR_FORMAT
import com.libreAlexa.constants.LUCIMESSAGES.IPADRESS
import com.libreAlexa.constants.LUCIMESSAGES.TIMEZONE_OEM
import com.libreAlexa.constants.MIDCONST
import com.libreAlexa.databinding.FragmentTimeZoneBinding
import com.libreAlexa.luci.LUCIControl
import com.libreAlexa.util.LibreLogger
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.util.TimeZone

/**
 * Created By Shaik
 */

class TimeZoneFragment : Fragment(), TimeZoneListAdapter.SendDataToFragment{
    private var currentIPAddress: String? = null
    private lateinit var binding: FragmentTimeZoneBinding
    private lateinit var timeZoneListAdapter: TimeZoneListAdapter
    private var timeZoneData = java.util.ArrayList<TimeZoneDataClass>()
    private var sendDataFragmentToActivity: SendDataFragmentToActivity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentIPAddress = it.getString(IPADRESS)
            LibreLogger.d(TIMEZONE_UPDATE, "Getting the Ip from Activity $currentIPAddress")
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentTimeZoneBinding.inflate(inflater, container, false)
        timeZoneData = ArrayList()
        val items = TimeZone.getAvailableIDs()
        for (tzId in items) {
            val tz = TimeZone.getTimeZone(tzId)
            val name = tz.displayName
            val id = tz.id
            val timeZoneDataClass = TimeZoneDataClass(id, name)
            timeZoneData.add(timeZoneDataClass)

        }
        timeZoneListAdapter = TimeZoneListAdapter(timeZoneData, this)
        binding.rlTimeZone.layoutManager = LinearLayoutManager(activity)
        binding.rlTimeZone.adapter = timeZoneListAdapter
        binding.btnBack.setOnClickListener {
           closeFragment()
        }
        return binding.root

    }

    private fun closeFragment() {
        val manager = requireActivity().supportFragmentManager
        manager.beginTransaction().remove(this@TimeZoneFragment).commit()
    }

    companion object {
        fun newInstance(name: String?): TimeZoneFragment {
            val fragment = TimeZoneFragment()
            val bundle = Bundle().apply {
                putString(IPADRESS, name)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun handleUserData(data: String) {
        LibreLogger.d(TIMEZONE_UPDATE,"Getting user selected data from adapter $data  and IpAddress $currentIPAddress")
        (activity as CTDeviceSettingsActivity).showProgressDialog(R.string.pleaseWait)
        updateTimeZone(currentIPAddress,data)
        lifecycleScope.launch {
            delay(2000)
            (activity as CTDeviceSettingsActivity).dismissDialog()
            sendDataFragmentToActivity?.communicate(data)
            closeFragment()
        }

    }

    private fun updateTimeZone(ipAddress: String?, data: String?) {
        val control = LUCIControl(ipAddress)
        LibreLogger.d(TIMEZONE_UPDATE,"updateTimeZone ipAddress $ipAddress and data $data")
        if (ipAddress!=null && data!= null) {
            val postData = JSONObject()
            try {
                postData.put(TIMEZONE_OEM, data)
                postData.put(HOUR_FORMAT, "")
            } catch (ex: JSONException) {
                ex.printStackTrace()
                LibreLogger.d(TIMEZONE_UPDATE,"updateTimeZone JSONException: " + ex.message)
            }
            control.SendCommand(MIDCONST.UPDATE_TIMEZONE, postData.toString(), LSSDPCONST.LUCI_SET)
            LibreLogger.d(TIMEZONE_UPDATE,"updateTimeZone Success $postData")
        } else {
            LibreLogger.d(TIMEZONE_UPDATE,"updateTimeZone failed because Ip address and data is null")
        }

    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        sendDataFragmentToActivity = try {
            context as SendDataFragmentToActivity
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement FragmentToActivity")
        }
    }

    override fun onDestroy() {
        sendDataFragmentToActivity = null
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        (activity as CTDeviceSettingsActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as CTDeviceSettingsActivity?)!!.supportActionBar!!.show()
    }

}