package com.cumulations.libreV2.com.cumulations.libreV2.BLE;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BroadcastReceiver_BTState extends BroadcastReceiver {

    private Context activityContext ;

    public BroadcastReceiver_BTState(Context mActivityContext) {
        this.activityContext = mActivityContext;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // an Intent broadcast.
        final String action = intent.getAction();

        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    BLEUtils.toast(activityContext, "Bluetooth is off");
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    BLEUtils.toast(activityContext, "Bluetooth is turning off...");
                    break;
                case BluetoothAdapter.STATE_ON:
                    BLEUtils.toast(activityContext, "Bluetooth is on");
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    BLEUtils.toast(activityContext, "Bluetooth is turning on...");
                    break;
            }
        }
    }
}
