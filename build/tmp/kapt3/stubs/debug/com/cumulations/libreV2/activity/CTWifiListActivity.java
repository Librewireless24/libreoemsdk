package com.cumulations.libreV2.activity;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000\u00a8\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010%\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0002\u0018\u00002\u00020\u00012\u00020\u0002B\u0005\u00a2\u0006\u0002\u0010\u0003J\b\u0010<\u001a\u00020\u0017H\u0002J\u0010\u0010=\u001a\u00020\u00172\u0006\u0010>\u001a\u00020\u0005H\u0002J\b\u0010?\u001a\u00020\u0017H\u0002J\u000e\u0010@\u001a\u00020\u00172\u0006\u0010A\u001a\u00020\u000eJ\b\u0010B\u001a\u00020\u0017H\u0002J\b\u0010C\u001a\u00020\u0017H\u0002J\b\u0010D\u001a\u00020\u0017H\u0002J\b\u0010E\u001a\u00020\u0017H\u0016J\u0012\u0010F\u001a\u00020\u00172\b\u0010G\u001a\u0004\u0018\u000109H\u0016J\u0012\u0010H\u001a\u00020\u00172\b\u0010I\u001a\u0004\u0018\u00010JH\u0014J\b\u0010K\u001a\u00020\u0017H\u0014J\u0010\u0010L\u001a\u00020\u00172\u0006\u0010M\u001a\u000202H\u0016J\b\u0010N\u001a\u00020\u0017H\u0014J\b\u0010O\u001a\u00020\u0017H\u0014J\u0012\u0010P\u001a\u00020\u00172\b\u0010Q\u001a\u0004\u0018\u00010\u0005H\u0002J\u0016\u0010R\u001a\u00020\u00172\f\u0010S\u001a\b\u0018\u00010TR\u00020UH\u0016J\b\u0010V\u001a\u00020\u0017H\u0002J\u0018\u0010W\u001a\u00020\u00172\u000e\u0010X\u001a\n\u0012\u0004\u0012\u00020\u000e\u0018\u00010YH\u0002J\u0010\u0010Z\u001a\u00020\u00172\u0006\u0010M\u001a\u000202H\u0016R\u0016\u0010\u0004\u001a\n \u0006*\u0004\u0018\u00010\u00050\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u0012\u0010\t\u001a\u00060\nj\u0002`\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\"\u0010\f\u001a\u0016\u0012\u0004\u0012\u00020\u000e\u0018\u00010\rj\n\u0012\u0004\u0012\u00020\u000e\u0018\u0001`\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0080\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\u0013\"\u0004\b\u0014\u0010\u0015R\u0014\u0010\u0016\u001a\u00020\u00178BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0018\u0010\u0019R\u000e\u0010\u001a\u001a\u00020\u001bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u001c\u001a\u0004\u0018\u00010\u001dX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001e\u0010\u001f\"\u0004\b \u0010!R\u0010\u0010\"\u001a\u0004\u0018\u00010#X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010$\u001a\u00020\u001bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010%\u001a\u0004\u0018\u00010\u0005X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b&\u0010\'\"\u0004\b(\u0010)R\u000e\u0010*\u001a\u00020\u001bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010+\u001a\u00020,X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010-\u001a\u00020.\u00a2\u0006\b\n\u0000\u001a\u0004\b/\u00100R\u0012\u00101\u001a\u0004\u0018\u000102X\u0082\u000e\u00a2\u0006\u0004\n\u0002\u00103R\u001a\u00104\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u000505X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u00106\u001a\u0004\u0018\u000107X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u00108\u001a\u0004\u0018\u000109X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010:\u001a\u0004\u0018\u00010;X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006["}, d2 = {"Lcom/cumulations/libreV2/activity/CTWifiListActivity;", "Lcom/cumulations/libreV2/activity/CTDeviceDiscoveryActivity;", "Lcom/cumulations/libreV2/com/cumulations/libreV2/BLE/BLEServiceToApplicationInterface;", "()V", "TAG", "", "kotlin.jvm.PlatformType", "binding", "Lcom/libreAlexa/databinding/CtActivityWifiListBinding;", "constructJSonString", "Ljava/lang/StringBuilder;", "Lkotlin/text/StringBuilder;", "filteredScanResults", "Ljava/util/ArrayList;", "Lcom/cumulations/libreV2/model/ScanResultItem;", "Lkotlin/collections/ArrayList;", "handler", "Landroid/os/Handler;", "getHandler$libreoemsdk_debug", "()Landroid/os/Handler;", "setHandler$libreoemsdk_debug", "(Landroid/os/Handler;)V", "intentExtra", "", "getIntentExtra", "()Lkotlin/Unit;", "isItDying", "", "mBluetoothAdapter", "Landroid/bluetooth/BluetoothAdapter;", "getMBluetoothAdapter", "()Landroid/bluetooth/BluetoothAdapter;", "setMBluetoothAdapter", "(Landroid/bluetooth/BluetoothAdapter;)V", "mBluetoothLeService", "Lcom/cumulations/libreV2/activity/BluetoothLeService;", "mConfiguringThroughBLE", "mDeviceAddress", "getMDeviceAddress", "()Ljava/lang/String;", "setMDeviceAddress", "(Ljava/lang/String;)V", "mIntentExtraScanResults", "mServiceConnection", "Landroid/content/ServiceConnection;", "runnable", "Ljava/lang/Runnable;", "getRunnable", "()Ljava/lang/Runnable;", "scanListLength", "", "Ljava/lang/Integer;", "scanListMap", "", "taskJob", "Lkotlinx/coroutines/Job;", "value", "Landroid/bluetooth/BluetoothGattCharacteristic;", "wifiListAdapter", "Lcom/cumulations/libreV2/adapter/CTWifiListAdapter;", "cancelJob", "getScanResultsForIp", "deviceIp", "getScanResultsFromDevice", "goBackToConnectWifiScreen", "scanResultItem", "initBluetoothAdapterAndListener", "initViews", "initiateJob", "onBackPressed", "onConnectionSuccess", "cat", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "onDisconnectionSuccess", "status", "onStart", "onStop", "populateScanListMap", "scanList", "receivedBLEDataPacket", "packet", "Lcom/cumulations/libreV2/com/cumulations/libreV2/BLE/BLEPacket$BLEDataPacket;", "Lcom/cumulations/libreV2/com/cumulations/libreV2/BLE/BLEPacket;", "setListeners", "sortAndSaveScanResults", "list", "", "writeSucess", "libreoemsdk_debug"})
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
    @org.jetbrains.annotations.Nullable
    private java.lang.Integer scanListLength;
    @org.jetbrains.annotations.Nullable
    private kotlinx.coroutines.Job taskJob;
    @org.jetbrains.annotations.NotNull
    private final android.content.ServiceConnection mServiceConnection = null;
    @org.jetbrains.annotations.Nullable
    private android.os.Handler handler;
    @org.jetbrains.annotations.NotNull
    private final java.lang.Runnable runnable = null;
    
    public CTWifiListActivity() {
        super();
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
    
    @java.lang.Override
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    private final kotlin.Unit getIntentExtra() {
        return null;
    }
    
    private final void initBluetoothAdapterAndListener() {
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
    public final android.os.Handler getHandler$libreoemsdk_debug() {
        return null;
    }
    
    public final void setHandler$libreoemsdk_debug(@org.jetbrains.annotations.Nullable
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
    
    private final void populateScanListMap(java.lang.String scanList) {
    }
    
    @java.lang.Override
    public void onBackPressed() {
    }
    
    @java.lang.Override
    protected void onDestroy() {
    }
    
    private final void getScanResultsForIp(java.lang.String deviceIp) {
    }
    
    private final void initiateJob() {
    }
    
    private final void cancelJob() {
    }
}