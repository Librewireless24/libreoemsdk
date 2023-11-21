package com.cumulations.libreV2.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity
import com.cumulations.libreV2.activity.CTUpnpFileBrowserActivity
import com.libreAlexa.R
import com.libreAlexa.app.dlna.dmc.processor.impl.UpnpProcessorImpl
import com.libreAlexa.app.dlna.dmc.processor.interfaces.UpnpProcessor
import com.libreAlexa.constants.Constants
import com.libreAlexa.databinding.CtDlgFragmentMediaServersBinding
import com.libreAlexa.util.LibreLogger
import org.fourthline.cling.model.meta.LocalDevice
import org.fourthline.cling.model.meta.RemoteDevice


/**
 * Created by Amit Tumkur on 05-06-2018.
 */
class CTMediaServerListFragment: DialogFragment(), UpnpProcessor.UpnpProcessorListener {
    private var listAdapter: ArrayAdapter<String>? = null
    private val nameToUDNMap = HashMap<String, String>()
    private val currentDeviceIp: String? by lazy {
        arguments?.getString(Constants.CURRENT_DEVICE_IP)
    }
    private var dialogBinding: CtDlgFragmentMediaServersBinding?=null
    // onDestroyView.
    private val binding get() = dialogBinding!!
    private val TAG = CTMediaServerListFragment::class.java.simpleName
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        dialogBinding = CtDlgFragmentMediaServersBinding.inflate(LayoutInflater.from(context))
        var dialog = activity?.let {
            Dialog(it)
        }
        dialog = Dialog(requireActivity(), R.style.TransparentDialogTheme)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        isCancelable = true

        val lp = dialog.window!!.attributes
        lp.gravity = Gravity.BOTTOM //position
        dialog.window!!.attributes = lp

        listAdapter = ArrayAdapter(requireActivity(), R.layout.ct_list_item_dms_device)
        val textView:TextView = requireView().findViewById(R.id.text1)
        textView.isSelected = true
        dialogBinding!!.deviceList.adapter = listAdapter

        dialogBinding!!.deviceList.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            activity?.runOnUiThread {
                showLoader()
            }

            val friendlyName = listAdapter?.getItem(i)
            val intent = Intent(activity, CTUpnpFileBrowserActivity::class.java)
            intent.putExtra(Constants.DIDL_TITLE, friendlyName)
            intent.putExtra(Constants.DEVICE_UDN, nameToUDNMap[friendlyName])
            intent.putExtra(Constants.CURRENT_DEVICE_IP, currentDeviceIp)
            startActivity(intent)
        }

        dialogBinding!!.ivRefresh.setOnClickListener {
            showLoader()
            listAdapter?.clear()
            listAdapter?.notifyDataSetChanged()
            (activity as CTDeviceDiscoveryActivity).upnpProcessor!!.searchAll()
        }

        return dialog
    }

    override fun onStart() {
        super.onStart()
        if ((activity as CTDeviceDiscoveryActivity).upnpProcessor != null) {
            (activity as CTDeviceDiscoveryActivity).upnpProcessor!!.addListener(this)
        }
    }

    override fun onStartComplete() {}

    override fun onResume() {
        super.onResume()
        showLoader()
        (activity as CTDeviceDiscoveryActivity).upnpProcessor?.searchAll()
    }

    override fun onRemoteDeviceAdded(device: RemoteDevice?) {
        LibreLogger.d(TAG, "Added Remote device")

        activity?.runOnUiThread {
            LibreLogger.d("onRemoteDeviceAdded", "runOnUiThread " + device?.identity?.udn.toString())
            if (device?.type?.namespace == UpnpProcessorImpl.DMS_NAMESPACE && device.type.type == UpnpProcessorImpl.DMS_TYPE) {
                val position = listAdapter?.getPosition(device.details.friendlyName)
                if (position!! >= 0) {
                    // Device already in the list, re-set new value at same position
                    listAdapter?.remove(device.details.friendlyName)
                    listAdapter?.insert(device.details.friendlyName, position)
                } else {
                    listAdapter?.add(device.details.friendlyName)
                    listAdapter?.notifyDataSetChanged()
                }
                closeLoader()
            }
        }

        val udn = device?.identity?.udn.toString()
        nameToUDNMap[device?.details?.friendlyName!!] = udn
    }

    override fun onRemoteDeviceRemoved(device: RemoteDevice?) {
        val udn = device?.identity?.udn.toString()
        LibreLogger.d(TAG, "onRemoteDeviceRemoved $udn")
        if (nameToUDNMap.containsKey(device?.details?.friendlyName))
            nameToUDNMap.remove(device?.details?.friendlyName)

        activity?.runOnUiThread {
            LibreLogger.d("onRemoteDeviceAdded", "runOnUiThread " + device?.identity?.udn.toString())
            val position = listAdapter?.getPosition(device?.details?.friendlyName)
            if (position!! >= 0) {
                // Device exist in the list
                listAdapter?.remove(device?.details?.friendlyName)
                listAdapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onLocalDeviceAdded(device: LocalDevice?) {
        LibreLogger.d(TAG, "onLocalDeviceAdded udn = ${device?.identity?.udn}")
    }

    override fun onLocalDeviceRemoved(device: LocalDevice?) {
        LibreLogger.d(TAG, "onLocalDeviceRemoved udn = ${device?.identity?.udn}")
    }

    private fun showLoader() {
        dialogBinding?.ivRefresh?.visibility = View.INVISIBLE
        dialogBinding?.loader?.visibility = View.VISIBLE
    }

    private fun closeLoader() {
        dialogBinding?.ivRefresh?.visibility = View.VISIBLE
        dialogBinding?.loader?.visibility = View.INVISIBLE
    }

    override fun onDismiss(dialog: DialogInterface) {
        closeLoader()
        if ((activity as CTDeviceDiscoveryActivity).upnpProcessor != null) {
            (activity as CTDeviceDiscoveryActivity).upnpProcessor!!.removeListener(this)
        }
        super.onDismiss(dialog)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        dialogBinding = null
    }
}