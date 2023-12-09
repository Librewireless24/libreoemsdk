package com.cumulations.libreV2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cumulations.libreV2.AppUtils
import com.cumulations.libreV2.activity.CTBluetoothPassCredentials
import com.cumulations.libreV2.activity.CTWifiListActivity
import com.cumulations.libreV2.model.ScanResultItem
import com.libreAlexa.R
import com.libreAlexa.databinding.CtListItemWifiBinding
import com.libreAlexa.util.LibreLogger
import java.util.Locale

class CTWifiListAdapter(val context: Context, var scanResultList: MutableList<ScanResultItem>?) :
    RecyclerView.Adapter<CTWifiListAdapter.ScanResultItemViewHolder>() {
    val TAG: String = CTWifiListAdapter::class.java.simpleName
    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ScanResultItemViewHolder {
        val itemBinding = CtListItemWifiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScanResultItemViewHolder(itemBinding)
    }


    override fun onBindViewHolder(viewHolder: ScanResultItemViewHolder, position: Int) {
        val scanResultItem = scanResultList?.get(position)
        viewHolder.bindScanResultItem(scanResultItem, position)
    }

    override fun getItemCount(): Int {
        return scanResultList?.size!!
    }

    inner class ScanResultItemViewHolder(val itemBinding: CtListItemWifiBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bindScanResultItem(scanResultItem: ScanResultItem?, position: Int) {
            LibreLogger.d(CTBluetoothPassCredentials.TAG_SCAN, "bindScanResultItem  " + scanResultItem?.ssid)
            itemBinding.tvSsidName.text = scanResultItem?.ssid
            itemBinding.tvSsidSecurity.text = scanResultItem?.security?.uppercase(Locale.getDefault())
            if(scanResultItem!!.ssid==AppUtils.getConnectedSSID(context)){
                itemBinding.tvSsidName.setTextColor(ContextCompat.getColor(context, R.color.brand_orange))
            }else{
                itemBinding.tvSsidName.setTextColor(ContextCompat.getColor(context, R.color.white))
            }
            if (scanResultItem.rssi.toInt() == 100) {
                itemBinding.imgWifiRangeIcon.visibility = View.VISIBLE
                if(scanResultItem.security == "Open" ||scanResultItem.security == "None"){
                    itemBinding.imgWifiRangeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.full_range_withoutlock))
                }else {
                    itemBinding.imgWifiRangeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.full_range))
                }
            } else if (scanResultItem.rssi.toInt() <= 0 && scanResultItem.rssi.toInt() > -45) {
                itemBinding.imgWifiRangeIcon.visibility = View.VISIBLE
                //very good signal
                if(scanResultItem.security == "Open" ||scanResultItem.security == "None"){
                    itemBinding.imgWifiRangeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.full_range_withoutlock))
                }else {
                    itemBinding.imgWifiRangeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.full_range))
                }
            } else if (scanResultItem.rssi.toInt() <= -45 && scanResultItem.rssi.toInt() > -70) {
                itemBinding.imgWifiRangeIcon.visibility = View.VISIBLE
                //ok
                if(scanResultItem.security == "Open" ||scanResultItem.security == "None"){
                    itemBinding.imgWifiRangeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.mid_range_withoutlock))
                }else {
                    itemBinding.imgWifiRangeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.mid_range))
                }
            } else if (scanResultItem.rssi.toInt() <= -70 && scanResultItem.rssi.toInt() > -82) {
                itemBinding.imgWifiRangeIcon.visibility = View.VISIBLE
                //low
                if(scanResultItem.security == "Open" ||scanResultItem.security == "None"){
                    itemBinding.imgWifiRangeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.low_range_withoutlock))
                }else {
                    itemBinding.imgWifiRangeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.low_range))
                }
            } else if (scanResultItem.rssi.toInt() <= -82) {
                itemBinding.imgWifiRangeIcon.visibility = View.VISIBLE
                //very low
                if(scanResultItem.security == "Open" ||scanResultItem.security == "None"){
                    itemBinding.imgWifiRangeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.low_range_withoutlock))
                }else {
                    itemBinding.imgWifiRangeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.low_range))
                }
            } else if (scanResultItem.rssi.toInt() == 200) {
                itemBinding.imgWifiRangeIcon.visibility = View.INVISIBLE
            } else {
                itemBinding.imgWifiRangeIcon.visibility = View.VISIBLE
            }


            itemBinding.llSsid.setOnClickListener {
                if (context is CTWifiListActivity) context.goBackToConnectWifiScreen(scanResultItem!!)

            }
        }
    }

    fun updateList(scanResultList: MutableList<ScanResultItem>?) {
        this.scanResultList = scanResultList
        notifyDataSetChanged()
    }
}



