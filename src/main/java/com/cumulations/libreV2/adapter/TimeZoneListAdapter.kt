package com.cumulations.libreV2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.cumulations.libreV2.fragments.TimeZoneFragment
import com.cumulations.libreV2.model.TimeZoneDataClass
import com.libreAlexa.R
/**
 * Created By Shaik
 */
class TimeZoneListAdapter(
    private var timeZoneList: ArrayList<TimeZoneDataClass>,
    private var timeZoneFragment: TimeZoneFragment) :
    RecyclerView.Adapter<TimeZoneListAdapter.TimeZoneViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup,
        p1: Int) = TimeZoneViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.time_zone, parent, false))

    override fun getItemCount() = timeZoneList.size

    override fun onBindViewHolder(holder: TimeZoneViewHolder, position: Int) {
        holder.bind(timeZoneList[position], position,timeZoneFragment)

    }

    class TimeZoneViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val txtTimeZoneID: AppCompatTextView = view.findViewById(R.id.txt_timeZoneId)
        private val txtTimeZoneName: AppCompatTextView = view.findViewById(R.id.txt_timeZoneName)
        private val txtTimeZoneParent: ConstraintLayout = view.findViewById(R.id.lay_timezone_parent)

        fun bind(timeZones: TimeZoneDataClass, position: Int, timeZoneFragment: TimeZoneFragment) {
            txtTimeZoneID.text = "${position + 1}. ${timeZones.timeZoneId}"
            txtTimeZoneName.text = timeZones.timeZoneName
            txtTimeZoneParent.setOnClickListener {
                timeZoneFragment.handleUserData(timeZones.timeZoneId)
            }

        }
    }
    interface SendDataToFragment {
        fun handleUserData(data: String)
    }

}