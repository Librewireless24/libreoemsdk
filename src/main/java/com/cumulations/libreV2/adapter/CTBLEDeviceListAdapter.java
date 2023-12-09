package com.cumulations.libreV2.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEDevice;
import com.libreAlexa.R;
import java.util.ArrayList;

public class CTBLEDeviceListAdapter extends ArrayAdapter<BLEDevice> {

    private Activity activity;
    private int layoutResourceID;
    private ArrayList<BLEDevice> devices;
    private OnClickInterfaceListener mListener;

    public CTBLEDeviceListAdapter(Activity activity, int resource, ArrayList<BLEDevice> objects) {
        super(activity.getApplicationContext(), resource, objects);

        this.activity = activity;
        layoutResourceID = resource;
        devices = objects;
        mListener = (OnClickInterfaceListener) activity;
    }

    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResourceID, parent, false);
        }

        BLEDevice device = devices.get(position);
        String name = device.getName();
        String address = device.getAddress();
        int rssi = device.getRssi();

        LinearLayout mLayout = (LinearLayout) convertView.findViewById(R.id.tv_bledevice_layout);
        mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onInterfaceClick(view, position);
            }
        });

        TextView tv_name = (TextView) convertView.findViewById(R.id.tv_bledevice_name);
        tv_name.setText(device.getName());

        TextView tv_rssi = (TextView) convertView.findViewById(R.id.tv_bledevice_rssi);
        tv_rssi.setText("RSSI: " + Integer.toString(rssi));

        Button mPlayToneBtn = (Button) convertView.findViewById(R.id.btnPlayTone);
        mPlayToneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onInterfaceClick(view,position);
            }
        });

        if (address != null && address.length() > 0) {
            tv_rssi.setText(device.getAddress() + "\n" + "RSSI: " + Integer.toString(rssi));

        }
        else {
            tv_rssi.setText("RSSI: " + Integer.toString(rssi));
        }

        return convertView;
    }
}
