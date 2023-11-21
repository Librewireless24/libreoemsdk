package com.cumulations.libreV2.com.cumulations.libreV2.BLE;

/**
 * This class is converted from java to kotlin 11/OCT/2023
 * Shaik
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 \"2\u00020\u0001:\u0001\"B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u0006\u0010\u0019\u001a\u00020\u0011J\u0006\u0010\u001a\u001a\u00020\u000fJ\u0010\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\nH\u0002J\u000e\u0010\u001e\u001a\u00020\u001c2\u0006\u0010\u001f\u001a\u00020 J\u0006\u0010!\u001a\u00020\u001cR\u001a\u0010\t\u001a\u00020\nX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\u000b\"\u0004\b\f\u0010\rR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0016\u001a\n \u0018*\u0004\u0018\u00010\u00170\u0017X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006#"}, d2 = {"Lcom/cumulations/libreV2/com/cumulations/libreV2/BLE/ScannerBLE;", "", "mBLActivity", "Lcom/cumulations/libreV2/activity/CTBluetoothDeviceListActivity;", "scanPeriod", "", "signalStrength", "", "(Lcom/cumulations/libreV2/activity/CTBluetoothDeviceListActivity;JI)V", "isScanning", "", "()Z", "setScanning", "(Z)V", "mBluetoothAdapter", "Landroid/bluetooth/BluetoothAdapter;", "mBluetoothScaneer", "Landroid/bluetooth/le/BluetoothLeScanner;", "mHandler", "Landroid/os/Handler;", "scanCallback", "Landroid/bluetooth/le/ScanCallback;", "scanSettings", "Landroid/bluetooth/le/ScanSettings;", "kotlin.jvm.PlatformType", "getBluetoothScaneer", "getmBluetoothAdapter", "scanLeDevice", "", "enable", "start", "activity", "Landroid/app/Activity;", "stop", "Companion", "libreoemsdk_debug"})
public final class ScannerBLE {
    @org.jetbrains.annotations.NotNull
    private final com.cumulations.libreV2.activity.CTBluetoothDeviceListActivity mBLActivity = null;
    private final long scanPeriod = 0L;
    private final int signalStrength = 0;
    @org.jetbrains.annotations.NotNull
    private android.bluetooth.BluetoothAdapter mBluetoothAdapter;
    @org.jetbrains.annotations.Nullable
    private android.bluetooth.le.BluetoothLeScanner mBluetoothScaneer;
    private boolean isScanning = false;
    @org.jetbrains.annotations.NotNull
    private final android.os.Handler mHandler = null;
    private final android.bluetooth.le.ScanSettings scanSettings = null;
    private static final java.lang.String TAG = null;
    @org.jetbrains.annotations.NotNull
    private final android.bluetooth.le.ScanCallback scanCallback = null;
    @org.jetbrains.annotations.NotNull
    public static final com.cumulations.libreV2.com.cumulations.libreV2.BLE.ScannerBLE.Companion Companion = null;
    
    public ScannerBLE(@org.jetbrains.annotations.NotNull
    com.cumulations.libreV2.activity.CTBluetoothDeviceListActivity mBLActivity, long scanPeriod, int signalStrength) {
        super();
    }
    
    public final boolean isScanning() {
        return false;
    }
    
    public final void setScanning(boolean p0) {
    }
    
    @org.jetbrains.annotations.NotNull
    public final android.bluetooth.BluetoothAdapter getmBluetoothAdapter() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final android.bluetooth.le.BluetoothLeScanner getBluetoothScaneer() {
        return null;
    }
    
    public final void start(@org.jetbrains.annotations.NotNull
    android.app.Activity activity) {
    }
    
    public final void stop() {
    }
    
    private final void scanLeDevice(boolean enable) {
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0016\u0010\u0003\u001a\n \u0005*\u0004\u0018\u00010\u00040\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lcom/cumulations/libreV2/com/cumulations/libreV2/BLE/ScannerBLE$Companion;", "", "()V", "TAG", "", "kotlin.jvm.PlatformType", "libreoemsdk_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}