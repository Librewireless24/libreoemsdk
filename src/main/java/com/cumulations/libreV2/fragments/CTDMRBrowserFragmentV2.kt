package com.cumulations.libreV2.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cumulations.libreV2.activity.CTDeviceSettingsActivity
import com.cumulations.libreV2.adapter.CTDIDLObjectListAdapter
import com.libreAlexa.R
import com.libreAlexa.constants.Constants
import com.libreAlexa.databinding.CtFragmentDmsBrowserBinding
import com.libreAlexa.util.LibreLogger
import org.fourthline.cling.support.model.DIDLObject


class CTDMRBrowserFragmentV2 : Fragment() {
    private var didlObjectArrayAdapter: CTDIDLObjectListAdapter? = null
    private val musicType: String? by lazy {
        arguments?.getString(Constants.MUSIC_TYPE)
    }
    private var binding: CtFragmentDmsBrowserBinding? = null
    private val TAG = CTDMRBrowserFragmentV2::class.java.simpleName
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        LibreLogger.d(TAG,"onCreateView called $musicType")
        binding = CtFragmentDmsBrowserBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        didlObjectArrayAdapter = CTDIDLObjectListAdapter(requireActivity(), ArrayList())
       binding!!.rvBrowserList.layoutManager = LinearLayoutManager(activity)
        binding!!.rvBrowserList.adapter = didlObjectArrayAdapter
        binding!!.rvBrowserList.setEmptyView(binding!!.tvNoData)
    }

    fun updateBrowserList(didlObjectList: List<DIDLObject>?) {
        activity?.runOnUiThread {
            didlObjectArrayAdapter?.updateList(didlObjectList as MutableList<DIDLObject>?)
            if (didlObjectArrayAdapter?.didlObjectList?.isEmpty()!!){
                binding!!.tvNoData.text = getText(R.string.noItems)
                binding!!.tvNoData.visibility = View.VISIBLE
//                (activity as CTDeviceDiscoveryActivity).showToast(R.string.noContent)
            } else {
                binding!!.tvNoData.visibility = View.GONE
            }
        }
    }

    fun browsingOver(){
        LibreLogger.d(TAG,"browsingOver, type = $musicType")
        activity?.runOnUiThread {
            if (didlObjectArrayAdapter?.didlObjectList?.isEmpty()!!){
                binding!!.tvNoData.text = getText(R.string.noItems)
                binding!!.tvNoData.visibility = View.VISIBLE
            } else {
                binding!!.tvNoData.visibility = View.GONE
            }
        }
    }

    fun scrollToPosition(position: Int) {
        binding!!.rvBrowserList.scrollToPosition(position)
    }

    fun getFirstVisibleItemPosition(): Int {
        return (binding!!.rvBrowserList.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
    }

    fun getCurrentDIDLObjectList(): ArrayList<DIDLObject>? {
        return /*dataItemList*/(didlObjectArrayAdapter?.didlObjectList as ArrayList<DIDLObject>?)!!
    }
}