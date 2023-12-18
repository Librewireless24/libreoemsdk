package com.cumulations.libreV2.activity;

import static android.bluetooth.BluetoothGattDescriptor.ENABLE_INDICATION_VALUE;
import static android.bluetooth.BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
import static com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEGattAttributes.MTU_SIZE;
import static com.libreAlexa.LibreApplication.betweenDisconnectedCount;
import static com.libreAlexa.LibreApplication.disconnectedCount;
import android.Manifest.permission;
import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEGattAttributes;
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEPacket;
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEPacket.BLEDataPacket;
import com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEServiceToApplicationInterface;
import com.libreAlexa.LibreApplication;
import com.libreAlexa.util.LibreLogger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();
    public BluetoothManager mBluetoothManager;
    public static BluetoothAdapter mBluetoothAdapter;
    public String mBluetoothDeviceAddress;
    public static BluetoothGatt mBluetoothGatt;
    public int mConnectionState = STATE_DISCONNECTED;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private final HashMap<String, BluetoothGattCharacteristic> BluetoothDeviceHashMapGattChar = new HashMap<String, BluetoothGattCharacteristic>();
    private final ArrayList<BLEServiceToApplicationInterface> mBLEServiceInterfaceListenerList = new ArrayList<BLEServiceToApplicationInterface>();

    public void addBLEServiceToApplicationInterfaceListener(
            BLEServiceToApplicationInterface mBLEServiceInterfaceListener) {
        LibreLogger.d(TAG, "Add Ble Service To app Interface Listener. 11");
        if (!mBLEServiceInterfaceListenerList.contains(mBLEServiceInterfaceListener))
            mBLEServiceInterfaceListenerList.add(mBLEServiceInterfaceListener);
    }

    public void removeBLEServiceToApplicationInterfaceListener(
            BLEServiceToApplicationInterface mBLEServiceInterfaceListener) {
        LibreLogger.d(TAG, "Remove Ble Service To app Interface Listener.");
        mBLEServiceInterfaceListenerList.remove(mBLEServiceInterfaceListener);
    }

    public void fireOnBLEConnectionSuccess(BluetoothGattCharacteristic status) {
        LibreLogger.d(TAG, "fire On BLE Conenction Success ");
        for (BLEServiceToApplicationInterface mListener : mBLEServiceInterfaceListenerList) {
            mListener.onConnectionSuccess(status);
        }

    }

    public void fireOnBLEDisConnectionSuccess(int status) {
        LibreLogger.d(TAG, "fire On BLE Disconenction Success");
        for (BLEServiceToApplicationInterface mListener : mBLEServiceInterfaceListenerList) {
            mListener.onDisconnectionSuccess(status);
        }

    }

    public void fireOnBLEreceivedBLEDataPacket(BLEDataPacket packet, String hexData) {
        LibreLogger.d(TAG, "fire On BLE Data Packet"+packet.getCommand()+" hexData "+hexData);
        for (BLEServiceToApplicationInterface mListener : mBLEServiceInterfaceListenerList) {
            mListener.receivedBLEDataPacket(packet,hexData);
        }

    }

    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                LibreLogger.d(TAG, "Connected to GATT client. Attempting to start service discovery 2");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (VERSION.SDK_INT >= 31) {
                        if (ActivityCompat.checkSelfPermission(BluetoothLeService.this, permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            //suma perm(check may change based on future target SDK)
                            return;
                        }
                    }
                    gatt.requestMtu(MTU_SIZE);
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                LibreLogger.d(TAG, "Disconnected from GATT client");
                if (status == 133) {

                    if (!LibreApplication.noReconnectionRuleToApply) {
                        betweenDisconnectedCount++;
                        if (betweenDisconnectedCount <= 4) {
                            final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mBluetoothDeviceAddress);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                mBluetoothGatt = device.connectGatt(getApplicationContext(), true, mGattCallback, BluetoothDevice.TRANSPORT_LE);
                                LibreLogger.d(TAG, "suma in 133 disconnection inside if");
                            }
                            LibreLogger.d(TAG, "suma in 133 disconnection outside");

                        } else {
                            LibreLogger.d(TAG, "suma in 133 disconnection outside else reached 5 attempts");
                            //   Toast.makeText(getApplicationContext(), "BLE DEVICE CONNECT FAILURE(133) ", Toast.LENGTH_SHORT).show();
                            fireOnBLEDisConnectionSuccess(status);
                        }
                    }
                } else {
                    mConnectionState = STATE_DISCONNECTED;
                    LibreLogger.d(TAG, "Disconnected from GATT server.");

                    disconnectedCount++;
                    if (disconnectedCount < 5) {
                        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mBluetoothDeviceAddress);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            mBluetoothGatt = device.connectGatt(getApplicationContext(), true, mGattCallback, BluetoothDevice.TRANSPORT_LE);
                            LibreLogger.d(TAG, "Disconnected inside if");

                        }

                        fireOnBLEDisConnectionSuccess(status);
                        close();
                        LibreLogger.d(TAG, "Disconnected outside");

                    } else {
                        LibreLogger.d(TAG, "Disconnected outside else reached 5 attempts");
                        //  broadcastUpdate(intentAction);
                        fireOnBLEDisConnectionSuccess(status);
                    }
                }
                fireOnBLEDisConnectionSuccess(status);
            }

            if (status == 22) {
/*For now we didnt get such status number will add check later if any  */
            }
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            LibreLogger.d(TAG, "onServicesDiscovered  " + status);
            gatServiceDiscovered(gatt, status);
        }

        public void gatServiceDiscovered(BluetoothGatt mGatt, int mStatus) {
            displayGattServices(mGatt.getServices());
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (VERSION.SDK_INT >= 31) {
                    if (ActivityCompat.checkSelfPermission(BluetoothLeService.this, permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        //suma perm(check may change based on future target SDK)
                        return;
                    }
                }
                gatt.discoverServices();
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            LibreLogger.d(TAG, "onCharacteristicRead received: " + status);
            if (status == 133) {
                // connectGatt(mBluetoothDeviceAddress);
            }
            //    gatt.setCharacteristicNotification(characteristic, true);
            readCounterCharacteristic(characteristic);

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            LibreLogger.d(TAG, "onCharacteristicChanged received ");
            /**
             * The Below the is to check the packet lose as we requested BLE developer help
             * we have to debug with him, the below code may help on that time
             * As of now we are not doing anything with the below code
             */
            /*// Check for packet loss.
            byte[] previousValue = characteristic.getValue();
            if (previousValue != null) {
                byte[] currentValue = characteristic.getValue();
                if (currentValue != null && currentValue != previousValue) {
                    // A packet was received.
                    LibreLogger.d(TAG,"A packet was received.");
                } else {
                    // A packet was lost.
                    LibreLogger.d(TAG,"A packet was lost");
                }
            }*/
            readCounterCharacteristic(characteristic);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
                                      int status) {
            LibreLogger.d(TAG, "onDescriptorWrite received " + status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            LibreLogger.d(TAG, "onCharacteristicWrite received: " + status);
        }

        private void readCounterCharacteristic(BluetoothGattCharacteristic characteristic) {
            byte[] data = characteristic.getValue();

            try {
                byte[] value = data;
                BLEPacket mPacket = new BLEPacket();
                BLEPacket.BLEDataPacket mDataPacket = mPacket.createBlePacketFromMessage(value);
                LibreLogger.d(TAG, "Received BLE data" + "Value received:  " + value.length +
                        " String Received msg " + bytesToHex(value) +
                        " BLE Command received " + mDataPacket.getCommand());
                fireOnBLEreceivedBLEDataPacket(mDataPacket,bytesToHex(value));
            } catch (Exception e) {
                e.printStackTrace();
                LibreLogger.d(TAG,"Exception: "+e.getMessage());
            }
        }
    };

     static String bytesToHex(byte[] hashInBytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            // System.out.println(String.format("%02x", b));
            sb.append(String.format("%02x", b));
        }
        return sb.toString();

    }

    public int getDataLength(byte[] buf) {
        byte b1 = buf[3];
        byte b2 = buf[4];
        short s = (short) (b1 << 8 | b2 & 0xFF);

        LibreLogger.d(TAG, "Data length is returned as s" + String.valueOf(s));
        return s;
    }

    public class LocalBinder extends Binder {

        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        LibreLogger.d(TAG, "onBind ");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // SUMA : After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        LibreLogger.d(TAG, "onUnbind: ");
        close();
        return super.onUnbind(intent);
    }

    public final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize(BluetoothAdapter mAdapter,
                              BLEServiceToApplicationInterface mBLEServiceInterfaceListener) {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                LibreLogger.d(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        addBLEServiceToApplicationInterfaceListener(mBLEServiceInterfaceListener);
        mBluetoothAdapter = mAdapter;

        if (mBluetoothAdapter == null) {
            LibreLogger.d(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            LibreLogger.d(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        if (BluetoothDeviceHashMapGattChar.containsKey(address)) {
            LibreLogger.d(TAG, "connect ");
            fireOnBLEConnectionSuccess(BluetoothDeviceHashMapGattChar.get(address));
            return true;
        }
        LibreLogger.d(TAG, "Trying to create a new connection. 1");
        connectGatt(address);

        mConnectionState = STATE_CONNECTING;
        return true;
    }

    private void connectGatt(String address) {
        mBluetoothDeviceAddress = address;
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            LibreLogger.d(TAG, "Device not found.  Unable to connect.");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (VERSION.SDK_INT >= 31) {
                if (ActivityCompat.checkSelfPermission(this, permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    //suma perm(check may change based on future target SDK)
                }
            }
            mBluetoothGatt = device.connectGatt(this, false, mGattCallback, BluetoothDevice.TRANSPORT_LE);

        } else {
            mBluetoothGatt = device.connectGatt(this, false, mGattCallback);

        }


    }

    public void removelistener(BLEServiceToApplicationInterface toApplicationInterface) {
        removeBLEServiceToApplicationInterfaceListener(toApplicationInterface);
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */

    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            LibreLogger.d(TAG, "BluetoothAdapter not initialized");
            return;
        }
        BluetoothDeviceHashMapGattChar.clear();
        if (VERSION.SDK_INT >= 31) {
            if (ActivityCompat.checkSelfPermission(this, permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
        }
        mBluetoothGatt.disconnect();

    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        LibreLogger.d(TAG, "close");
        if (mBluetoothGatt == null) {
            return;
        }
        BluetoothDeviceHashMapGattChar.clear();
        if (VERSION.SDK_INT >= 31) {
            if (ActivityCompat.checkSelfPermission(BluetoothLeService.this, permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                //suma perm(check may change based on future target SDK)
                return;
            }
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    private final ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
        new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

    public static BluetoothGattCharacteristic mDeviceCharacteristic;

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            uuid = gattService.getUuid().toString();
            Log.v(TAG + " service " , uuid);
            if(gattService.getUuid().equals(BLEGattAttributes.RIVA_BLE_SERVICE))  {
                List<BluetoothGattCharacteristic> gattCharacteristics =
                        gattService.getCharacteristics();

                // Loops through available Characteristics.
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    uuid = gattCharacteristic.getUuid().toString();
                    Log.v(TAG + " characteristics ", uuid);
                    for (BluetoothGattDescriptor descriptor : gattCharacteristic.getDescriptors()) {
                        Log.v(TAG + " Descriptor ", descriptor.getUuid().toString());
                        descriptor.setValue( ENABLE_NOTIFICATION_VALUE);
                        gattCharacteristic.setValue(ENABLE_INDICATION_VALUE);
                        gattCharacteristic.setValue(ENABLE_NOTIFICATION_VALUE);
                        gattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);

                        if (VERSION.SDK_INT >= 31) {
                            if (ActivityCompat.checkSelfPermission(BluetoothLeService.this, permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                //suma perm(check may change based on future target SDK)
                                return;
                            }
                        }

                        mBluetoothGatt.writeDescriptor(descriptor);
                        putGattCharacteristic(gattCharacteristic, mBluetoothDeviceAddress);
                        mDeviceCharacteristic = gattCharacteristic;
                        mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
                        mBluetoothGatt.readCharacteristic(gattCharacteristic);

                        // byte data[] = new byte[0];
                        // BLEPacket mBlePlayToneButtonClicked = new BLEPacket(data, (byte) BLE_SAC_APP2DEV_SCAN_WIFI);
                        // BleCommunication.writeDataToBLEDevice(BluetoothLeService.mDeviceCharacteristic, mBlePlayToneButtonClicked);
                        // BleCommunication.writeDataToBLEDevice(gattCharacteristic, mBlePlayToneButtonClicked);
                    }
                    LibreLogger.d(TAG, "displayGattServicess ");
                    fireOnBLEConnectionSuccess(gattCharacteristic);
                    LibreLogger.d(TAG,"suma in get the fireonConnection Sucess gatt Service ");

                }
            }
        }
    }


    private void putGattCharacteristic(BluetoothGattCharacteristic gattCharacteristic , String btMacAddress) {
        LibreLogger.d(TAG, "putGattCharacteristic   "  + btMacAddress + " Id " + gattCharacteristic.hashCode());
        if(!BluetoothDeviceHashMapGattChar.containsKey(btMacAddress)) {
            LibreLogger.d(TAG, "putGattCharacteristic  "  + btMacAddress + " Id " + "is it removing");
           BluetoothDeviceHashMapGattChar.remove(btMacAddress);
        }
        BluetoothDeviceHashMapGattChar.put(btMacAddress, gattCharacteristic);
        LibreApplication application =new LibreApplication();
        application.setBTGattCharacteristic(gattCharacteristic);
    }
    public BluetoothGattCharacteristic getCharacteristic(String btMacAddress) {
        return BluetoothDeviceHashMapGattChar.get(btMacAddress);
    }


}