package com.cumulations.libreV2.com.cumulations.libreV2.BLE;

import android.bluetooth.BluetoothGattCharacteristic;
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEPacket.BLEDataPacket;

public interface BLEServiceToApplicationInterface {

    void onConnectionSuccess(BluetoothGattCharacteristic value);

    void receivedBLEDataPacket(BLEDataPacket packet, String hexData);

    void writeSucess(int status);

    void onDisconnectionSuccess(int status);
}
