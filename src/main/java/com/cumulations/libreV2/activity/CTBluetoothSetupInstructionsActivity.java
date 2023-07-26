package com.cumulations.libreV2.activity;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEUtils;
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BroadcastReceiver_BTState;
import com.cumulations.libreV2.fragments.CTDeviceSetupInfoFragment;
import com.libreAlexa.R;
import com.libreAlexa.constants.AppConstants;

public class CTBluetoothSetupInstructionsActivity extends CTDeviceDiscoveryActivity {

    BroadcastReceiver_BTState mBTStateUpdateReceiver;
    ImageView mBackView;
    Button mOtherOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ct_activity_bluetooth_setup_instructions);

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            BLEUtils.toast(getApplicationContext(), "BLE not supported");
            finish();
        }

        mBTStateUpdateReceiver = new BroadcastReceiver_BTState(getApplicationContext());
        mBackView = findViewById(R.id.iv_back);
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentToHome(CTBluetoothSetupInstructionsActivity.this);
            }
        });

        mOtherOptions = findViewById(R.id.btn_other_option);
        mOtherOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(CTBluetoothSetupInstructionsActivity.this, CTHomeTabsActivity.class);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                newIntent.putExtra(AppConstants.LOAD_FRAGMENT, CTDeviceSetupInfoFragment.class.getSimpleName());
                startActivity(newIntent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        intentToHome(CTBluetoothSetupInstructionsActivity.this);
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onResume() {
        registerReceiver(mBTStateUpdateReceiver,new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
        if((mResultCanceled==1) && !BLEUtils.checkBluetooth(mBluetoothAdapter)) {
            BLEUtils.requestUserBluetooth(this);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mBTStateUpdateReceiver);
        super.onPause();
    }

    private int mResultCanceled = 1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BLEUtils.REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                BLEUtils.toast(getApplicationContext(), "Thank you for turning on Bluetooth");
                Intent mIntent = new Intent(this, CTBluetoothDeviceListActivity.class);
                startActivity(mIntent);
                finish();
            }
            else if (resultCode == RESULT_CANCELED) {
                mResultCanceled = resultCode;
                BLEUtils.toast(getApplicationContext(), "Please turn on Bluetooth");
            }
        }
    }
}
