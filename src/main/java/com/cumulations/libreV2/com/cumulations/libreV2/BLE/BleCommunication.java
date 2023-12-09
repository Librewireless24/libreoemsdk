package com.cumulations.libreV2.com.cumulations.libreV2.BLE;

import static com.cumulations.libreV2.activity.BluetoothLeService.mBluetoothGatt;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import androidx.core.app.ActivityCompat;

import com.cumulations.libreV2.activity.CTBluetoothDeviceListActivity;
import com.libreAlexa.LibreApplication;
import com.libreAlexa.util.LibreLogger;

import java.util.List;
import java.util.UUID;

public class BleCommunication {

    private static final String TAG = "BleCommunication";
    private static UUID UUID_CHAR;

    public BleCommunication(CTBluetoothDeviceListActivity mainActivity) {
        LibreApplication.ContextActivity=mainActivity;
        LibreLogger.d(TAG,"Activity Context \n"+LibreApplication.ContextActivity);

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void writeDataToBLEDevice(BLEPacket mPacket) {
        LibreLogger.d(TAG, "writeDataToBLEDevice:mPacket "+mPacket);
        BluetoothGattService service = mBluetoothGatt.getService(BLEGattAttributes.RIVA_BLE_SERVICE);
        List<BluetoothGattCharacteristic> mBleGattServiceCharacteristics = service.getCharacteristics();
        for (BluetoothGattCharacteristic mServiceCharacteristic : mBleGattServiceCharacteristics) {
            LibreLogger.d(TAG, "started to write data " + mServiceCharacteristic.getUuid());
            //if(BLEGattAttributes.RIVA_BLE_CHARACTERISTICS == mServiceCharacteristic.getUuid()) {
            {
                UUID_CHAR = mServiceCharacteristic.getUuid();
                BluetoothGattCharacteristic charac = service
                        .getCharacteristic(mServiceCharacteristic.getUuid());

                charac.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                charac.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

                if (Build.VERSION.SDK_INT >= 31) {
                    if (ActivityCompat.checkSelfPermission(LibreApplication.ContextActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        //suma perm(check may change based on future target SDK)
                    }
                }
                mBluetoothGatt.setCharacteristicNotification(charac, true);
                LibreLogger.d(TAG, "writeDataToBLEDevice:Outside "+charac);
               if(charac!=null) {
                   LibreLogger.d(TAG, "writeDataToBLEDevice:if ");
                   charac.setValue(mPacket.getpayload());
                   mBluetoothGatt.writeCharacteristic(charac);
               }else{
                   LibreLogger.d(TAG, "writeDataToBLEDevice:else ");
               }

            }
        }
    }


    private static final BluetoothGattCharacteristic mNotifyCharacteristic = null;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void writeDataToBLEDevice(BluetoothGattCharacteristic characteristic, BLEPacket mPacket) {
        if(characteristic!=null) {
            LibreLogger.d("SUMA_BLESCAN","BLE Characteristics"+mPacket.Command);
            final int charaProp = characteristic.getProperties();
            LibreLogger.d(TAG,"SUMA_BLESCAN " + charaProp);
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                LibreLogger.d(TAG,"SUMA_BLESCAN sending Data "+ printDataSentValue(mPacket.getpayload()));
                characteristic.setValue(mPacket.getpayload());
                LibreLogger.d(TAG,"SUMA_BLESCAN sending Data "+ mPacket.getpayload());

                if (Build.VERSION.SDK_INT >= 31) {
                    if (ActivityCompat.checkSelfPermission(LibreApplication.ContextActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        //suma perm(check may change based on future target SDK)
                    }
                }
                if(mBluetoothGatt!=null) {
                    LibreLogger.d(TAG,"SUMA_BLESCAN write characteristics "+ mPacket.getpayload());

                    mBluetoothGatt.writeCharacteristic(characteristic);
                }
            }
        }
    }

    private static String printDataSentValue(byte[] hashInBytes) {

        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            //System.out.println(b & 0xFF);
            // System.out.println(String.format("%02x", b));
            sb.append(String.format("%02x", b));
        }
        return sb.toString();

    }


    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public static void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                                     boolean enabled) {
        if (Build.VERSION.SDK_INT >= 31) {
            if (ActivityCompat.checkSelfPermission(LibreApplication.ContextActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                //suma perm(check may change based on future target SDK)
            }
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
    }

//    /**
//     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
//     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
//     * callback.
//     *
//     * @param characteristic The characteristic to read from.
//     */

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void writeDataToBLEDevice(byte[] mPacket) {
        if (Build.VERSION.SDK_INT >= 31) {
            if (ActivityCompat.checkSelfPermission(LibreApplication.ContextActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                //suma  perm
            }
        }
        BluetoothGattService service = mBluetoothGatt.getService(BLEGattAttributes.RIVA_BLE_SERVICE);
        List<BluetoothGattCharacteristic> mBleGattServiceCharacteristics = service.getCharacteristics();
        for (BluetoothGattCharacteristic mServiceCharacteristic : mBleGattServiceCharacteristics) {
            LibreLogger.d(TAG, "started to write data " + mServiceCharacteristic.getUuid());
            //if(BLEGattAttributes.RIVA_BLE_CHARACTERISTICS == mServiceCharacteristic.getUuid()) {
            {
                UUID_CHAR = mServiceCharacteristic.getUuid();
                BluetoothGattCharacteristic charac = service
                        .getCharacteristic(mServiceCharacteristic.getUuid());

                charac.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                charac.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.setCharacteristicNotification(charac, true);
                /*if (charac != null) {
                    mBluetoothGatt.setCharacteristicNotification(charac, true);
                    BluetoothGattDescriptor descriptor = charac.getDescriptor(BLEGattAttributes.RIVA_BLE_CHARACTERISTICS);
                    if (descriptor != null) {
                        //descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        mBluetoothGatt.writeDescriptor(descriptor);
                    }
                }*/
                charac.setValue(mPacket);
                mBluetoothGatt.writeCharacteristic(charac);
            }
        }
    }

}
