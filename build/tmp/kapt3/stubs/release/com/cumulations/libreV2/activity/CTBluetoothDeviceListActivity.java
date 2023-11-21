package com.cumulations.libreV2.activity;

/**
 * This activity is converted from java to kotlin
 * Shaik
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000\u00cc\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0011\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\t\u0018\u0000 `2\u00020\u00012\u00020\u00022\u00020\u0003:\u0001`B\u0005\u00a2\u0006\u0002\u0010\u0004J\u0016\u00104\u001a\u0002052\u0006\u00106\u001a\u0002072\u0006\u00108\u001a\u00020\nJ\b\u00109\u001a\u000205H\u0002J\b\u0010:\u001a\u000205H\u0002J\b\u0010;\u001a\u000205H\u0002J\b\u0010<\u001a\u00020\u0010H\u0002J\"\u0010=\u001a\u0002052\b\u0010>\u001a\u0004\u0018\u00010?2\u0006\u0010@\u001a\u00020\n2\u0006\u0010A\u001a\u00020\nH\u0014J\u000e\u0010B\u001a\u00020\u00102\u0006\u00106\u001a\u000207J\b\u0010C\u001a\u000205H\u0016J\u0010\u0010D\u001a\u0002052\u0006\u0010E\u001a\u00020FH\u0016J\u0012\u0010G\u001a\u0002052\b\u0010H\u001a\u0004\u0018\u00010IH\u0014J\b\u0010J\u001a\u000205H\u0014J\u0010\u0010K\u001a\u0002052\u0006\u0010L\u001a\u00020\nH\u0016J\u0018\u0010M\u001a\u0002052\u0006\u0010N\u001a\u00020O2\u0006\u0010P\u001a\u00020\nH\u0016J\b\u0010Q\u001a\u000205H\u0014J\b\u0010R\u001a\u000205H\u0014J\b\u0010S\u001a\u000205H\u0014J\b\u0010T\u001a\u000205H\u0002J\u0014\u0010U\u001a\u0002052\n\u0010V\u001a\u00060WR\u00020XH\u0016J(\u0010Y\u001a\u0002052\u0006\u0010Z\u001a\u00020\b2\u0006\u0010[\u001a\u00020\b2\u0006\u0010\\\u001a\u00020\b2\u0006\u0010@\u001a\u00020\nH\u0002J\u0006\u0010]\u001a\u000205J\u0006\u0010^\u001a\u000205J\u0010\u0010_\u001a\u0002052\u0006\u0010L\u001a\u00020\nH\u0016R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082D\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0013\u001a\u00020\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u0017\u001a\u00020\u0018\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0010\u0010\u001b\u001a\u0004\u0018\u00010\u001cX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u001d\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u001f0\u001eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010 \u001a\u0004\u0018\u00010!X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\"\u001a\u0004\u0018\u00010#X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010$\u001a\b\u0012\u0004\u0012\u00020\u001f0%X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010&\u001a\u0004\u0018\u00010\'X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010(\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010)\u001a\u0004\u0018\u00010\u001fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010*\u001a\u00020+X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010,\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0.0-X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010/\u001a\u00020\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b0\u0010\u0016R\u000e\u00101\u001a\u000202X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u00103\u001a\u00020\u0014X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006a"}, d2 = {"Lcom/cumulations/libreV2/activity/CTBluetoothDeviceListActivity;", "Lcom/cumulations/libreV2/activity/CTDeviceDiscoveryActivity;", "Lcom/cumulations/libreV2/com/cumulations/libreV2/BLE/BLEServiceToApplicationInterface;", "Lcom/cumulations/libreV2/adapter/OnClickInterfaceListener;", "()V", "BTReceiver", "Landroid/content/BroadcastReceiver;", "TAG", "", "TIMEOUT_WIFI", "", "alertDialog", "Landroid/app/AlertDialog;", "binding", "Lcom/libreAlexa/databinding/ActivityCtbluetoothDeviceListBinding;", "blPlayToneButtonClicked", "", "bluetoothStateReceiver", "Lcom/cumulations/libreV2/com/cumulations/libreV2/BLE/BluetoothStateReceiver;", "disconnectBle", "Ljava/lang/Runnable;", "getDisconnectBle", "()Ljava/lang/Runnable;", "handler", "Landroid/os/Handler;", "getHandler", "()Landroid/os/Handler;", "mBLEListAdapter", "Lcom/cumulations/libreV2/adapter/CTBLEDeviceListAdapter;", "mBTDevicesHashMap", "Ljava/util/HashMap;", "Lcom/cumulations/libreV2/com/cumulations/libreV2/BLE/BLEDevice;", "mBTLeScanner", "Lcom/cumulations/libreV2/com/cumulations/libreV2/BLE/ScannerBLE;", "mBleCommunication", "Lcom/cumulations/libreV2/com/cumulations/libreV2/BLE/BleCommunication;", "mBleDevices", "Ljava/util/ArrayList;", "mBluetoothLeService", "Lcom/cumulations/libreV2/activity/BluetoothLeService;", "mDeviceAddress", "mDeviceClicked", "mServiceConnection", "Landroid/content/ServiceConnection;", "requestPermissionLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "", "runnable", "getRunnable", "sharedPreference", "Lcom/cumulations/libreV2/SharedPreferenceHelper;", "showWifiConfigurationScreen", "addDevice", "", "device", "Landroid/bluetooth/BluetoothDevice;", "rssi", "callConnectToWifiActivityPage", "callConnectToWifiConfiguration", "checkPermissions", "checkPermissionsGranted", "customOnActivityResult", "data", "Landroid/content/Intent;", "requestCode", "resultCode", "isRivaSpeaker", "onBackPressed", "onConnectionSuccess", "btGattChar", "Landroid/bluetooth/BluetoothGattCharacteristic;", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "onDisconnectionSuccess", "status", "onInterfaceClick", "view", "Landroid/view/View;", "position", "onResume", "onStart", "onStop", "promptBluetoothEnable", "receivedBLEDataPacket", "packet", "Lcom/cumulations/libreV2/com/cumulations/libreV2/BLE/BLEPacket$BLEDataPacket;", "Lcom/cumulations/libreV2/com/cumulations/libreV2/BLE/BLEPacket;", "showAlertDialog", "message", "positiveButtonString", "negativeButtonString", "startScan", "stopScan", "writeSucess", "Companion", "libreoemsdk_release"})
public final class CTBluetoothDeviceListActivity extends com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity implements com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEServiceToApplicationInterface, com.cumulations.libreV2.adapter.OnClickInterfaceListener {
    @org.jetbrains.annotations.NotNull
    private final java.lang.String TAG = "BluetoothListActivity";
    @org.jetbrains.annotations.Nullable
    private com.cumulations.libreV2.adapter.CTBLEDeviceListAdapter mBLEListAdapter;
    @org.jetbrains.annotations.NotNull
    private java.util.ArrayList<com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEDevice> mBleDevices;
    @org.jetbrains.annotations.NotNull
    private final java.util.HashMap<java.lang.String, com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEDevice> mBTDevicesHashMap = null;
    @org.jetbrains.annotations.Nullable
    private com.cumulations.libreV2.com.cumulations.libreV2.BLE.ScannerBLE mBTLeScanner;
    @org.jetbrains.annotations.Nullable
    private com.cumulations.libreV2.com.cumulations.libreV2.BLE.BleCommunication mBleCommunication;
    @org.jetbrains.annotations.Nullable
    private com.cumulations.libreV2.activity.BluetoothLeService mBluetoothLeService;
    private final int TIMEOUT_WIFI = 10000;
    private com.libreAlexa.databinding.ActivityCtbluetoothDeviceListBinding binding;
    private com.cumulations.libreV2.SharedPreferenceHelper sharedPreference;
    @org.jetbrains.annotations.Nullable
    private android.app.AlertDialog alertDialog;
    @org.jetbrains.annotations.Nullable
    private com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEDevice mDeviceClicked;
    @org.jetbrains.annotations.NotNull
    private java.lang.String mDeviceAddress = "";
    @org.jetbrains.annotations.Nullable
    private com.cumulations.libreV2.com.cumulations.libreV2.BLE.BluetoothStateReceiver bluetoothStateReceiver;
    @org.jetbrains.annotations.NotNull
    private final android.content.ServiceConnection mServiceConnection = null;
    @org.jetbrains.annotations.NotNull
    private final android.content.BroadcastReceiver BTReceiver = null;
    @org.jetbrains.annotations.NotNull
    private final android.os.Handler handler = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.Runnable runnable = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.Runnable showWifiConfigurationScreen = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.Runnable disconnectBle = null;
    private boolean blPlayToneButtonClicked = false;
    @org.jetbrains.annotations.NotNull
    private static final java.lang.String[] permissions = {"android.permission.BLUETOOTH_CONNECT", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.BLUETOOTH_SCAN", "android.permission.BLUETOOTH_CONNECT"};
    @org.jetbrains.annotations.NotNull
    private final androidx.activity.result.ActivityResultLauncher<java.lang.String[]> requestPermissionLauncher = null;
    @org.jetbrains.annotations.NotNull
    public static final com.cumulations.libreV2.activity.CTBluetoothDeviceListActivity.Companion Companion = null;
    
    public CTBluetoothDeviceListActivity() {
        super();
    }
    
    @java.lang.Override
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override
    protected void onStart() {
    }
    
    @java.lang.Override
    protected void onDestroy() {
    }
    
    @java.lang.Override
    protected void onResume() {
    }
    
    @java.lang.Override
    public void onBackPressed() {
    }
    
    /**
     * StartScan will call after permissions allowed if no permissions, checkPermissions will
     * trigger
     */
    public final void startScan() {
    }
    
    public final void stopScan() {
    }
    
    @java.lang.Override
    protected void onStop() {
    }
    
    public final boolean isRivaSpeaker(@org.jetbrains.annotations.NotNull
    android.bluetooth.BluetoothDevice device) {
        return false;
    }
    
    public final void addDevice(@org.jetbrains.annotations.NotNull
    android.bluetooth.BluetoothDevice device, int rssi) {
    }
    
    @org.jetbrains.annotations.NotNull
    public final android.os.Handler getHandler() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.Runnable getRunnable() {
        return null;
    }
    
    private final void callConnectToWifiConfiguration() {
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.Runnable getDisconnectBle() {
        return null;
    }
    
    @java.lang.Override
    public void onConnectionSuccess(@org.jetbrains.annotations.NotNull
    android.bluetooth.BluetoothGattCharacteristic btGattChar) {
    }
    
    @java.lang.Override
    public void receivedBLEDataPacket(@org.jetbrains.annotations.NotNull
    com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEPacket.BLEDataPacket packet) {
    }
    
    @java.lang.Override
    public void writeSucess(int status) {
    }
    
    @java.lang.Override
    public void onDisconnectionSuccess(int status) {
    }
    
    private final void callConnectToWifiActivityPage() {
    }
    
    @java.lang.Override
    public void onInterfaceClick(@org.jetbrains.annotations.NotNull
    android.view.View view, int position) {
    }
    
    private final void promptBluetoothEnable() {
    }
    
    private final void checkPermissions() {
    }
    
    private final boolean checkPermissionsGranted() {
        return false;
    }
    
    private final void showAlertDialog(java.lang.String message, java.lang.String positiveButtonString, java.lang.String negativeButtonString, int requestCode) {
    }
    
    @java.lang.Override
    protected void customOnActivityResult(@org.jetbrains.annotations.Nullable
    android.content.Intent data, int requestCode, int resultCode) {
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0019\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\n\n\u0002\u0010\b\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\t"}, d2 = {"Lcom/cumulations/libreV2/activity/CTBluetoothDeviceListActivity$Companion;", "", "()V", "permissions", "", "", "getPermissions", "()[Ljava/lang/String;", "[Ljava/lang/String;", "libreoemsdk_release"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String[] getPermissions() {
            return null;
        }
    }
}