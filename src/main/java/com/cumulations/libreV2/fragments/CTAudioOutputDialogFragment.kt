package com.cumulations.libreV2.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.cumulations.libreV2.activity.CTDeviceSettingsActivity
import com.cumulations.libreV2.tcp_tunneling.enums.AQModeSelect
import com.libreAlexa.R
import com.libreAlexa.constants.Constants
import com.libreAlexa.databinding.CtDlgFragmentAudioOutputBinding


/**
 * Created by Amit Tumkur on 05-06-2018.
 */
class CTAudioOutputDialogFragment: DialogFragment() {
    private lateinit var binding: CtDlgFragmentAudioOutputBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val dialog = Dialog(requireActivity(), R.style.TransparentDialogTheme)
        //dialog.setContentView(R.layout.ct_dlg_fragment_audio_output)
        binding = CtDlgFragmentAudioOutputBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.setCanceledOnTouchOutside(true)
        isCancelable = true

        val lp = dialog.window!!.attributes
        lp.gravity = Gravity.BOTTOM //position
        lp.width = WindowManager.LayoutParams.MATCH_PARENT // full screen
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = lp


        arguments?.let {
            when (it.getString(Constants.AUDIO_OUTPUT)) {
                AQModeSelect.Trillium.name -> {
                    binding.rgAudioOutput.check(R.id.rb_trillium)
                }
                AQModeSelect.Power.name -> {
                    binding.rgAudioOutput.check(R.id.rb_power)
                }
                AQModeSelect.Left.name -> {
                    binding.rgAudioOutput.check(R.id.rb_left)
                }

                AQModeSelect.Right.name -> {
                    binding.rgAudioOutput.check(R.id.rb_right)
                }
            }
        }

        binding.rgAudioOutput.setOnCheckedChangeListener { radioGroup, i ->
            when(i/*checkedId*/){
                R.id.rb_trillium -> {
                    (activity as CTDeviceSettingsActivity).updateAudioOutputOfDevice(AQModeSelect.Trillium)
                    dismiss()
                }

                R.id.rb_power -> {
                    (activity as CTDeviceSettingsActivity).updateAudioOutputOfDevice(AQModeSelect.Power)
                    dismiss()
                }

                R.id.rb_left -> {
                    (activity as CTDeviceSettingsActivity).updateAudioOutputOfDevice(AQModeSelect.Left)
                    dismiss()
                }

                R.id.rb_right -> {
                    (activity as CTDeviceSettingsActivity).updateAudioOutputOfDevice(AQModeSelect.Right)
                    dismiss()
                }
            }
        }

        return dialog
    }
}