package com.cumulations.libreV2.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.cumulations.libreV2.activity.CTDeviceSettingsActivity
import com.libreAlexa.R
import com.libreAlexa.constants.Constants
import com.libreAlexa.constants.LibreAlexaConstants
import com.libreAlexa.databinding.CtDlgFragmentSelectLocaleBinding
import com.libreAlexa.util.LibreLogger

/**
 * Created by Amit Tumkur on 05-06-2018.
 */
class CTAlexaLocaleDialogFragment : DialogFragment() {
    private var _binding: CtDlgFragmentSelectLocaleBinding? = null
    private val binding get() = _binding!!
    private val TAG:String = CTAlexaLocaleDialogFragment::class.java.simpleName

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        _binding = CtDlgFragmentSelectLocaleBinding.inflate(LayoutInflater.from(context))
        var dialog = activity?.let {
            Dialog(it)
        }
        dialog = Dialog(requireActivity(), R.style.TransparentDialogTheme)
        dialog.setContentView(_binding!!.root)
        LibreLogger.d(TAG, "updateLang: CTAlexaLocaleDialogFragment")
        dialog.setCanceledOnTouchOutside(true)
        isCancelable = true


        val lp = dialog.window!!.attributes
        lp.gravity = Gravity.BOTTOM //psotion
        lp.width = WindowManager.LayoutParams.MATCH_PARENT // fuill screen
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = lp


        arguments?.let {
            when (it.getString(Constants.CURRENT_LOCALE)) {
                LibreAlexaConstants.Languages.ENG_US -> {
                    binding.rgSelectLocale.check(R.id.rb_en_us)
                }
                LibreAlexaConstants.Languages.ENG_GB -> {
                    binding.rgSelectLocale.check(R.id.rb_en_uk)
                }
                LibreAlexaConstants.Languages.DE -> {
                    binding.rgSelectLocale.check(R.id.rb_deu)
                }
                LibreAlexaConstants.Languages.FR -> {
                    binding.rgSelectLocale.check(R.id.rb_fr)
                }
                LibreAlexaConstants.Languages.IT -> {
                    binding.rgSelectLocale.check(R.id.rb_it)
                }
                LibreAlexaConstants.Languages.ES -> {
                    binding.rgSelectLocale.check(R.id.rb_es)
                }
            }
        }

        binding.rgSelectLocale.setOnCheckedChangeListener { radioGroup, i ->
            when (i/*checkedId*/) {
                R.id.rb_en_us -> {
                    (activity as CTDeviceSettingsActivity).sendUpdatedLangToDevice(
                        LibreAlexaConstants.Languages.ENG_US
                    )
                    dismiss()
                }

                R.id.rb_en_uk -> {
                    (activity as CTDeviceSettingsActivity).sendUpdatedLangToDevice(
                        LibreAlexaConstants.Languages.ENG_GB
                    )
                    dismiss()
                }

                R.id.rb_deu -> {
                    (activity as CTDeviceSettingsActivity).sendUpdatedLangToDevice(
                        LibreAlexaConstants.Languages.DE
                    )
                    dismiss()
                }

                R.id.rb_fr -> {
                    (activity as CTDeviceSettingsActivity).sendUpdatedLangToDevice(
                        LibreAlexaConstants.Languages.FR
                    )
                    dismiss()
                }

                R.id.rb_it -> {
                    (activity as CTDeviceSettingsActivity).sendUpdatedLangToDevice(
                        LibreAlexaConstants.Languages.IT
                    )
                    dismiss()
                }

                R.id.rb_es -> {
                    (activity as CTDeviceSettingsActivity).sendUpdatedLangToDevice(
                        LibreAlexaConstants.Languages.ES
                    )
                    dismiss()
                }
            }
        }

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}