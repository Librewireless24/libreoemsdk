package com.cumulations.libreV2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cumulations.libreV2.getAppVersion
import com.libreAlexa.databinding.CtFragmentSettingsBinding


class CTSettingsFragment:Fragment() {

    private var binding: CtFragmentSettingsBinding? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = CtFragmentSettingsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        binding!!.tvAppVersion.text = getAppVersion(requireActivity())
    }
}