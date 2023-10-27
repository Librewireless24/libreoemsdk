package com.cumulations.libreV2.com.cumulations.libreV2.BLE

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * This class is converted from java to kotlin 11/OCT/2023
 * Shaik
 */
class BluetoothStateReceiver(private val activityContext: Context) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                BluetoothAdapter.STATE_OFF -> BLEUtils.toast(activityContext, "Bluetooth is off")
                BluetoothAdapter.STATE_TURNING_OFF -> BLEUtils.toast(activityContext, "Bluetooth is turning off...")
                BluetoothAdapter.STATE_ON -> BLEUtils.toast(activityContext, "Bluetooth is on")
                BluetoothAdapter.STATE_TURNING_ON -> BLEUtils.toast(activityContext, "Bluetooth is turning on...")
            }
        }
    }
}