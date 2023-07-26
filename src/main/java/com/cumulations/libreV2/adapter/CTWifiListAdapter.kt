package com.cumulations.libreV2.adapter

import android.content.Context
import android.nfc.Tag
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cumulations.libreV2.activity.CTWifiListActivity
import com.cumulations.libreV2.model.ScanResultItem
import com.libreAlexa.databinding.CtListItemWifiBinding
import com.libreAlexa.util.LibreLogger
import java.util.Locale

class CTWifiListAdapter(val context: Context,
                        var scanResultList: MutableList<ScanResultItem>?) : RecyclerView.Adapter<CTWifiListAdapter.ScanResultItemViewHolder>() {
    val TAG: String = CTWifiListAdapter::class.java.simpleName
    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ScanResultItemViewHolder {
        val itemBinding = CtListItemWifiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScanResultItemViewHolder(itemBinding)
    }


    override fun onBindViewHolder(viewHolder: ScanResultItemViewHolder, position: Int) {
        val scanResultItem = scanResultList?.get(position)
        viewHolder.bindScanResultItem(scanResultItem,position)
    }

    override fun getItemCount(): Int {
        return scanResultList?.size!!
    }

    inner class ScanResultItemViewHolder(val itemBinding:  CtListItemWifiBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bindScanResultItem(scanResultItem: ScanResultItem?, position: Int) {
            itemBinding.tvSsidName.text = scanResultItem?.ssid
            itemBinding.tvSsidSecurity.text = scanResultItem?.security?.uppercase(Locale.getDefault())

            itemBinding.llSsid.setOnClickListener {
                if (context is CTWifiListActivity)
                    context.goBackToConnectWifiScreen(scanResultItem!!)
                LibreLogger.d(TAG,"suma in wifi scan list items"+scanResultItem?.security)
                LibreLogger.d(TAG,"suma in wifi scan list items"+scanResultItem?.ssid)

            }
        }
    }

    fun updateList(scanResultList: MutableList<ScanResultItem>?) {
        LibreLogger.d(TAG,"updateList called size ${scanResultList!!.size}")
        this.scanResultList = scanResultList
        notifyDataSetChanged()
    }
}



