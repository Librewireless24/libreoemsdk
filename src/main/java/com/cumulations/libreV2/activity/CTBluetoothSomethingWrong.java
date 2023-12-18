package com.cumulations.libreV2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.libreAlexa.R;
import com.libreAlexa.constants.AppConstants;
import com.libreAlexa.constants.Constants;

public class CTBluetoothSomethingWrong extends AppCompatActivity {
    ImageView ivBack ;
    TextView ivToolbarTitle;
    String mDeviceName = "";
    String fromActivity = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_something_wrong);

        mDeviceName = getIntent().getStringExtra(AppConstants.DEVICE_NAME);
        fromActivity = getIntent().getStringExtra(Constants.FROM_ACTIVITY);
        if(fromActivity == null){
            fromActivity = "";
        }
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fromActivity.length()>0) {
                    onBackPressed();
                }else {
                    callBluetoothDeviceListActivity();
                }
            }
        });

        ivToolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        ivToolbarTitle.setText(getString(R.string.setup_name));
    }



    private void callBluetoothDeviceListActivity() {
        Intent intent = new Intent(this, CTBluetoothDeviceListActivity.class);
        startActivity(intent);
        finish();
    }
}
