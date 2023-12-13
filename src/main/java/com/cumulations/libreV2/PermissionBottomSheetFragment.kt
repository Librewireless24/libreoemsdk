package com.cumulations.libreV2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.libreAlexa.R

class PermissionBottomSheetFragment : BottomSheetDialogFragment() {

    private var layoutBottomSheet:LinearLayout?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_permission_bottom_sheet, container, false)
        layoutBottomSheet = view.findViewById<View>(R.id.lay_custom) as LinearLayout
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Toast.makeText(requireContext(), "mansoor", Toast.LENGTH_SHORT).show()

        /*// Find views and set up click listeners
        val locationPermissionCheckBox = view.findViewById<CheckBox>(R.id.locationPermissionCheckBox)
        val bluetoothPermissionCheckBox = view.findViewById<CheckBox>(R.id.bluetoothPermissionCheckBox)
        val localNetworkPermissionCheckBox = view.findViewById<CheckBox>(R.id.localNetworkPermissionCheckBox)
        val grantPermissionButton = view.findViewById<Button>(R.id.grantPermissionButton)

        // Check if permissions are already granted
        locationPermissionCheckBox.isChecked = checkLocationPermission()
        bluetoothPermissionCheckBox.isChecked = checkBluetoothPermission()
        localNetworkPermissionCheckBox.isChecked = checkLocalNetworkPermission()

        // Set up click listener for the grant permission button
        grantPermissionButton.setOnClickListener {
            // Request permissions if not granted
            if (!locationPermissionCheckBox.isChecked) {
                requestLocationPermission()
            }

            if (!bluetoothPermissionCheckBox.isChecked) {
                requestBluetoothPermission()
            }

            if (!localNetworkPermissionCheckBox.isChecked) {
                requestLocalNetworkPermission()
            }

            // Dismiss the bottom sheet dialog
            dismiss()
        }*/
    }

    // Helper methods for checking and requesting permissions
    private fun checkLocationPermission(): Boolean {
        // Implement logic to check location permission
        return false
    }

    private fun checkBluetoothPermission(): Boolean {
        // Implement logic to check Bluetooth permission
        return false
    }

    private fun checkLocalNetworkPermission(): Boolean {
        // Implement logic to check local network permission
        return false
    }

    private fun requestLocationPermission() {
        // Implement logic to request location permission
    }

    private fun requestBluetoothPermission() {
        // Implement logic to request Bluetooth permission
    }

    private fun requestLocalNetworkPermission() {
        // Implement logic to request local network permission
    }
    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }
}