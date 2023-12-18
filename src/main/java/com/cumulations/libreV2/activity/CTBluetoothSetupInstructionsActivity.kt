package com.cumulations.libreV2.activity

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEUtils
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEUtils.checkBluetooth
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEUtils.toast
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BluetoothStateReceiver
import com.libreAlexa.databinding.CtActivityBluetoothSetupInstructionsBinding

class CTBluetoothSetupInstructionsActivity : CTDeviceDiscoveryActivity() {
    private var mBTStateUpdateReceiver: BluetoothStateReceiver? = null
    private lateinit var binding: CtActivityBluetoothSetupInstructionsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CtActivityBluetoothSetupInstructionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            toast(applicationContext, "BLE not supported")
            finish()
        }
        mBTStateUpdateReceiver = BluetoothStateReceiver(applicationContext)
        binding.ivBack.setOnClickListener {
            intentToHome(this@CTBluetoothSetupInstructionsActivity)
        }
        binding.btnTurnOnBt.setOnClickListener {
           // requestUserBluetooth()
        }
    }

    override fun onBackPressed() {
        intentToHome(this@CTBluetoothSetupInstructionsActivity)
        super.onBackPressed()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    override fun onResume() {
        registerReceiver(mBTStateUpdateReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val mBluetoothAdapter = bluetoothManager.adapter
        if (mResultCanceled == 1 && !checkBluetooth(this)) {
            //requestUserBluetooth(this);
        }
        super.onResume()
    }

    override fun onPause() {
        unregisterReceiver(mBTStateUpdateReceiver)
        super.onPause()
    }

    private var mResultCanceled = 1
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BLEUtils.REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                toast(applicationContext, "Thank you for turning on Bluetooth")
                val mIntent = Intent(this, CTBluetoothDeviceListActivity::class.java)
                startActivity(mIntent)
                finish()
            } else if (resultCode == RESULT_CANCELED) {
                mResultCanceled = resultCode
                toast(applicationContext, "Please turn on Bluetooth")
            }
        }
    }
}