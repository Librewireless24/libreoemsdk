package com.cumulations.libreV2.com.cumulations.libreV2.BLE;

import android.bluetooth.BluetoothDevice;

public class BLEDevice {
    private String deviceName = "";
    private int rssi = 0 ;
    private String address = "";
    private BluetoothDevice device;

    public String getAddress(){
        return address;
    }

    public void setAddress(String mAddress){
        this.address = mAddress;
    }

    public String getName() {
        return deviceName;
    }

    public void setName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public BluetoothDevice getBluetoothDevice(){
        return device;
    }
    //to be added perm after the integration testing

    public BLEDevice(BluetoothDevice device ,int rssi) {
        this.device = device;
        this.deviceName = device.getName();
        this.address = device.getAddress();
        this.rssi = rssi;
    }

    public BLEDevice(String deviceName, String address, int rssi) {
        this.deviceName = deviceName;
        this.address = address;
        this.rssi = rssi;
    }
}
