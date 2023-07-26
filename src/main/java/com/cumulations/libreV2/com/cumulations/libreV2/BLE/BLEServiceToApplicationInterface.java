package com.cumulations.libreV2.com.cumulations.libreV2.BLE;

import android.bluetooth.BluetoothGattCharacteristic;

public interface BLEServiceToApplicationInterface {

    void onConnectionSuccess(BluetoothGattCharacteristic value);

    void receivedBLEDataPacket(BLEPacket.BLEDataPacket packet);

    void writeSucess(int status);

    void onDisconnectionSuccess(int status);
}
