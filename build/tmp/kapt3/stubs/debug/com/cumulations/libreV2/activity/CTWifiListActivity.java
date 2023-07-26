package com.cumulations.libreV2.activity;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000\u00a6\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010%\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0002\u0018\u00002\u00020\u00012\u00020\u0002B\u0005\u00a2\u0006\u0002\u0010\u0003J\u0010\u0010C\u001a\u00020\u001b2\u0006\u0010D\u001a\u00020\u0005H\u0002J\b\u0010E\u001a\u00020\u001bH\u0002J\u000e\u0010F\u001a\u00020\u001b2\u0006\u0010G\u001a\u00020\u0012J\u0006\u0010H\u001a\u00020\u001bJ\b\u0010I\u001a\u00020\u001bH\u0002J\b\u0010J\u001a\u00020\u001bH\u0016J\u0012\u0010K\u001a\u00020\u001b2\b\u0010L\u001a\u0004\u0018\u00010@H\u0016J\u0012\u0010M\u001a\u00020\u001b2\b\u0010N\u001a\u0004\u0018\u00010OH\u0014J\b\u0010P\u001a\u00020\u001bH\u0014J\u0010\u0010Q\u001a\u00020\u001b2\u0006\u0010R\u001a\u00020SH\u0016J\b\u0010T\u001a\u00020\u001bH\u0014J\b\u0010U\u001a\u00020\u001bH\u0014J\u0012\u0010V\u001a\u00020\u001b2\b\u0010W\u001a\u0004\u0018\u00010\u0005H\u0002J\u0016\u0010X\u001a\u00020\u001b2\f\u0010Y\u001a\b\u0018\u00010ZR\u00020[H\u0016J\b\u0010\\\u001a\u00020\u001bH\u0002J\u0018\u0010]\u001a\u00020\u001b2\u000e\u0010^\u001a\n\u0012\u0004\u0012\u00020\u0012\u0018\u00010_H\u0002J\u0010\u0010`\u001a\u00020\u001b2\u0006\u0010R\u001a\u00020SH\u0016R\u0016\u0010\u0004\u001a\n \u0006*\u0004\u0018\u00010\u00050\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u001e\u0010\t\u001a\u00060\nj\u0002`\u000bX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000fR\"\u0010\u0010\u001a\u0016\u0012\u0004\u0012\u00020\u0012\u0018\u00010\u0011j\n\u0012\u0004\u0012\u00020\u0012\u0018\u0001`\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u0014\u001a\u0004\u0018\u00010\u0015X\u0080\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019R\u0014\u0010\u001a\u001a\u00020\u001b8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u001c\u0010\u001dR\u000e\u0010\u001e\u001a\u00020\u001fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010 \u001a\u0004\u0018\u00010!X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\"\u0010#\"\u0004\b$\u0010%R\u0010\u0010&\u001a\u0004\u0018\u00010\'X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010(\u001a\u00020\u001fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010)\u001a\u0004\u0018\u00010\u0005X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b*\u0010+\"\u0004\b,\u0010-R\u001a\u0010.\u001a\u00020\u001fX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b/\u00100\"\u0004\b1\u00102R\u000e\u00103\u001a\u000204X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u00105\u001a\u000206\u00a2\u0006\b\n\u0000\u001a\u0004\b7\u00108R&\u00109\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050:X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b;\u0010<\"\u0004\b=\u0010>R\u0010\u0010?\u001a\u0004\u0018\u00010@X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010A\u001a\u0004\u0018\u00010BX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006a"}, d2 = {"Lcom/cumulations/libreV2/activity/CTWifiListActivity;", "Lcom/cumulations/libreV2/activity/CTDeviceDiscoveryActivity;", "Lcom/cumulations/libreV2/com/cumulations/libreV2/BLE/BLEServiceToApplicationInterface;", "()V", "TAG", "", "kotlin.jvm.PlatformType", "binding", "Lcom/libreAlexa/databinding/CtActivityWifiListBinding;", "constructJSonString", "Ljava/lang/StringBuilder;", "Lkotlin/text/StringBuilder;", "getConstructJSonString", "()Ljava/lang/StringBuilder;", "setConstructJSonString", "(Ljava/lang/StringBuilder;)V", "filteredScanResults", "Ljava/util/ArrayList;", "Lcom/cumulations/libreV2/model/ScanResultItem;", "Lkotlin/collections/ArrayList;", "handler", "Landroid/os/Handler;", "getHandler$oemsdk_debug", "()Landroid/os/Handler;", "setHandler$oemsdk_debug", "(Landroid/os/Handler;)V", "intentExtra", "", "getIntentExtra", "()Lkotlin/Unit;", "isItDying", "", "mBluetoothAdapter", "Landroid/bluetooth/BluetoothAdapter;", "getMBluetoothAdapter", "()Landroid/bluetooth/BluetoothAdapter;", "setMBluetoothAdapter", "(Landroid/bluetooth/BluetoothAdapter;)V", "mBluetoothLeService", "Lcom/cumulations/libreV2/activity/BluetoothLeService;", "mConfiguringThroughBLE", "mDeviceAddress", "getMDeviceAddress", "()Ljava/lang/String;", "setMDeviceAddress", "(Ljava/lang/String;)V", "mIntentExtraScanResults", "getMIntentExtraScanResults", "()Z", "setMIntentExtraScanResults", "(Z)V", "mServiceConnection", "Landroid/content/ServiceConnection;", "runnable", "Ljava/lang/Runnable;", "getRunnable", "()Ljava/lang/Runnable;", "scanListMap", "", "getScanListMap", "()Ljava/util/Map;", "setScanListMap", "(Ljava/util/Map;)V", "value", "Landroid/bluetooth/BluetoothGattCharacteristic;", "wifiListAdapter", "Lcom/cumulations/libreV2/adapter/CTWifiListAdapter;", "getScanResultsForIp", "deviceIp", "getScanResultsFromDevice", "goBackToConnectWifiScreen", "scanResultItem", "initBluetoothAdapterAndListener", "initViews", "onBackPressed", "onConnectionSuccess", "cat", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "onDisconnectionSuccess", "status", "", "onStart", "onStop", "populateScanlistMap", "scanList", "receivedBLEDataPacket", "packet", "Lcom/cumulations/libreV2/com/cumulations/libreV2/BLE/BLEPacket$BLEDataPacket;", "Lcom/cumulations/libreV2/com/cumulations/libreV2/BLE/BLEPacket;", "setListeners", "sortAndSaveScanResults", "list", "", "writeSucess", "oemsdk_debug"})
public final class CTWifiListActivity extends com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity implements com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEServiceToApplicationInterface {
    @org.jetbrains.annotations.Nullable
    private com.cumulations.libreV2.adapter.CTWifiListAdapter wifiListAdapter;
    @org.jetbrains.annotations.Nullable
    private java.util.ArrayList<com.cumulations.libreV2.model.ScanResultItem> filteredScanResults;
    private boolean mConfiguringThroughBLE = false;
    private com.libreAlexa.databinding.CtActivityWifiListBinding binding;
    private final java.lang.String TAG = null;
    @org.jetbrains.annotations.Nullable
    private android.bluetooth.BluetoothGattCharacteristic value;
    private boolean mIntentExtraScanResults = false;
    @org.jetbrains.annotations.Nullable
    private android.bluetooth.BluetoothAdapter mBluetoothAdapter;
    @org.jetbrains.annotations.Nullable
    private java.lang.String mDeviceAddress;
    private boolean isItDying = false;
    @org.jetbrains.annotations.Nullable
    private com.cumulations.libreV2.activity.BluetoothLeService mBluetoothLeService;
    @org.jetbrains.annotations.NotNull
    private java.lang.StringBuilder constructJSonString;
    @org.jetbrains.annotations.NotNull
    private java.util.Map<java.lang.String, java.lang.String> scanListMap;
    @org.jetbrains.annotations.NotNull
    private final android.content.ServiceConnection mServiceConnection = null;
    @org.jetbrains.annotations.Nullable
    private android.os.Handler handler;
    @org.jetbrains.annotations.NotNull
    private final java.lang.Runnable runnable = null;
    
    public CTWifiListActivity() {
        super();
    }
    
    public final boolean getMIntentExtraScanResults() {
        return false;
    }
    
    public final void setMIntentExtraScanResults(boolean p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final android.bluetooth.BluetoothAdapter getMBluetoothAdapter() {
        return null;
    }
    
    public final void setMBluetoothAdapter(@org.jetbrains.annotations.Nullable
    android.bluetooth.BluetoothAdapter p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getMDeviceAddress() {
        return null;
    }
    
    public final void setMDeviceAddress(@org.jetbrains.annotations.Nullable
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.StringBuilder getConstructJSonString() {
        return null;
    }
    
    public final void setConstructJSonString(@org.jetbrains.annotations.NotNull
    java.lang.StringBuilder p0) {
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.Map<java.lang.String, java.lang.String> getScanListMap() {
        return null;
    }
    
    public final void setScanListMap(@org.jetbrains.annotations.NotNull
    java.util.Map<java.lang.String, java.lang.String> p0) {
    }
    
    @java.lang.Override
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    private final kotlin.Unit getIntentExtra() {
        return null;
    }
    
    public final void initBluetoothAdapterAndListener() {
    }
    
    private final void setListeners() {
    }
    
    private final void initViews() {
    }
    
    @java.lang.Override
    protected void onStart() {
    }
    
    private final void getScanResultsFromDevice() {
    }
    
    @org.jetbrains.annotations.Nullable
    public final android.os.Handler getHandler$oemsdk_debug() {
        return null;
    }
    
    public final void setHandler$oemsdk_debug(@org.jetbrains.annotations.Nullable
    android.os.Handler p0) {
    }
    
    private final void sortAndSaveScanResults(java.util.List<com.cumulations.libreV2.model.ScanResultItem> list) {
    }
    
    public final void goBackToConnectWifiScreen(@org.jetbrains.annotations.NotNull
    com.cumulations.libreV2.model.ScanResultItem scanResultItem) {
    }
    
    @java.lang.Override
    protected void onStop() {
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.Runnable getRunnable() {
        return null;
    }
    
    @java.lang.Override
    public void onConnectionSuccess(@org.jetbrains.annotations.Nullable
    android.bluetooth.BluetoothGattCharacteristic cat) {
    }
    
    @java.lang.Override
    public void receivedBLEDataPacket(@org.jetbrains.annotations.Nullable
    com.cumulations.libreV2.com.cumulations.libreV2.BLE.BLEPacket.BLEDataPacket packet) {
    }
    
    @java.lang.Override
    public void writeSucess(int status) {
    }
    
    @java.lang.Override
    public void onDisconnectionSuccess(int status) {
    }
    
    private final void populateScanlistMap(java.lang.String scanList) {
    }
    
    @java.lang.Override
    public void onBackPressed() {
    }
    
    @java.lang.Override
    protected void onDestroy() {
    }
    
    private final void getScanResultsForIp(java.lang.String deviceIp) {
    }
}