package com.cumulations.libreV2.activity;

/**
 * Added ChromeCast Settings by SHAIK
 * 18/04/2023
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000\u008a\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u0000 \\2\u00020\u00012\u00020\u00022\u00020\u0003:\u0001\\B\u0005\u00a2\u0006\u0002\u0010\u0004J\u001a\u0010*\u001a\u00020+2\b\u0010,\u001a\u0004\u0018\u00010\r2\u0006\u0010-\u001a\u00020\rH\u0002J\b\u0010.\u001a\u00020+H\u0002J\u0012\u0010/\u001a\u00020+2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u0002J\u0010\u00100\u001a\u00020+2\u0006\u00101\u001a\u000202H\u0002J\u0012\u00103\u001a\u00020+2\b\u00104\u001a\u0004\u0018\u00010\rH\u0016J\b\u00105\u001a\u00020+H\u0016J\u0012\u00106\u001a\u00020+2\b\u0010,\u001a\u0004\u0018\u00010\rH\u0016J\u0010\u00107\u001a\u00020+2\u0006\u00108\u001a\u00020\rH\u0002J\u0014\u00109\u001a\u0004\u0018\u00010\r2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u0002J\b\u0010:\u001a\u00020+H\u0002J\u0010\u0010;\u001a\u00020+2\u0006\u0010<\u001a\u00020=H\u0016J\u0010\u0010>\u001a\u00020+2\u0006\u0010?\u001a\u00020\u0013H\u0016J\u0012\u0010@\u001a\u00020+2\b\u0010A\u001a\u0004\u0018\u00010BH\u0014J\b\u0010C\u001a\u00020+H\u0014J\b\u0010D\u001a\u00020+H\u0014J\b\u0010E\u001a\u00020+H\u0014J\b\u0010F\u001a\u00020+H\u0002J\u000e\u0010G\u001a\u00020+2\u0006\u0010H\u001a\u00020\rJ\b\u0010I\u001a\u00020+H\u0002J(\u0010J\u001a\u00020+2\u0006\u0010K\u001a\u00020\r2\u0006\u0010L\u001a\u00020\r2\u0006\u0010M\u001a\u00020\r2\u0006\u0010N\u001a\u00020\nH\u0002J\u0010\u0010O\u001a\u00020+2\u0006\u00101\u001a\u000202H\u0002J\u0010\u0010P\u001a\u00020+2\u0006\u0010Q\u001a\u00020\nH\u0002J\u0010\u0010R\u001a\u00020+2\u0006\u0010S\u001a\u00020TH\u0016J\u000e\u0010U\u001a\u00020+2\u0006\u0010V\u001a\u00020WJ\b\u0010X\u001a\u00020+H\u0002J\u0016\u0010Y\u001a\u00020\r2\u0006\u0010Z\u001a\u00020\r2\u0006\u0010[\u001a\u000202R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u0012\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u000e\u00a2\u0006\u0004\n\u0002\u0010\u000bR\u001d\u0010\f\u001a\u0004\u0018\u00010\r8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0010\u0010\u0011\u001a\u0004\b\u000e\u0010\u000fR\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0014\u001a\u0004\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0015\u001a\u0004\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0016\u001a\u0004\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0017\u001a\u00020\u00188BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001b\u0010\u0011\u001a\u0004\b\u0019\u0010\u001aR\u000e\u0010\u001c\u001a\u00020\u001dX\u0082.\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u001e\u001a\u00020\u001fX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b \u0010!\"\u0004\b\"\u0010#R\u0014\u0010$\u001a\b\u0012\u0004\u0012\u00020&0%X\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\'\u001a\u0004\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010(\u001a\u0004\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010)\u001a\u0004\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006]"}, d2 = {"Lcom/cumulations/libreV2/activity/CTDeviceSettingsActivity;", "Lcom/cumulations/libreV2/activity/CTDeviceDiscoveryActivity;", "Lcom/libreAlexa/netty/LibreDeviceInteractionListner;", "Lcom/cumulations/libreV2/SendDataFragmentToActivity;", "()V", "alertDialogBox", "Landroidx/appcompat/app/AlertDialog;", "binding", "Lcom/libreAlexa/databinding/CtDeviceSettingsBinding;", "crashReport", "", "Ljava/lang/Boolean;", "currentDeviceIp", "", "getCurrentDeviceIp", "()Ljava/lang/String;", "currentDeviceIp$delegate", "Lkotlin/Lazy;", "currentDeviceNode", "Lcom/libreAlexa/luci/LSSDPNodes;", "currentLocale", "deviceName", "deviceUUID", "libreVoiceDatabaseDao", "Lcom/cumulations/libreV2/roomdatabase/CastLiteDao;", "getLibreVoiceDatabaseDao", "()Lcom/cumulations/libreV2/roomdatabase/CastLiteDao;", "libreVoiceDatabaseDao$delegate", "luciControl", "Lcom/libreAlexa/luci/LUCIControl;", "mScanHandler", "Lcom/libreAlexa/Scanning/ScanningHandler;", "getMScanHandler", "()Lcom/libreAlexa/Scanning/ScanningHandler;", "setMScanHandler", "(Lcom/libreAlexa/Scanning/ScanningHandler;)V", "savedDeviceUUIDList", "", "Lcom/cumulations/libreV2/roomdatabase/CastLiteUUIDDataClass;", "seekbarVolumeValue", "switchStatus", "tosStatus", "UpdateLSSDPNodeDeviceName", "", "ipaddress", "mDeviceName", "callActivateCastActivity", "checkCastActivateStatus", "closeLoader", "progressBarId", "", "communicate", "comm", "deviceDiscoveryAfterClearingTheCacheStarted", "deviceGotRemoved", "fetchUUIDFromDB", "speakerIpAddress", "getUUIDWithIP", "initViews", "messageRecieved", "nettyData", "Lcom/libreAlexa/netty/NettyData;", "newDeviceFound", "node", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onResume", "onStart", "onStop", "requestLuciUpdates", "sendUpdatedLangToDevice", "selectedLang", "setListeners", "showAlertDialog", "title", "message", "positiveButton", "isForgetNetwork", "showLoader", "toggleTunnelingVisibility", "show", "tunnelDataReceived", "tunnelingData", "Lcom/cumulations/libreV2/tcp_tunneling/TunnelingData;", "updateAudioOutputOfDevice", "aqModeSelect", "Lcom/cumulations/libreV2/tcp_tunneling/enums/AQModeSelect;", "updateLang", "utf8truncate", "input", "length", "Companion", "libreoemsdk_debug"})
public final class CTDeviceSettingsActivity extends com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity implements com.libreAlexa.netty.LibreDeviceInteractionListner, com.cumulations.libreV2.SendDataFragmentToActivity {
    @org.jetbrains.annotations.Nullable
    private java.lang.String currentLocale;
    @org.jetbrains.annotations.Nullable
    private com.libreAlexa.luci.LSSDPNodes currentDeviceNode;
    private com.libreAlexa.luci.LUCIControl luciControl;
    @org.jetbrains.annotations.Nullable
    private java.lang.String switchStatus;
    @org.jetbrains.annotations.Nullable
    private java.lang.String seekbarVolumeValue;
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy currentDeviceIp$delegate = null;
    @org.jetbrains.annotations.Nullable
    private androidx.appcompat.app.AlertDialog alertDialogBox;
    private com.libreAlexa.databinding.CtDeviceSettingsBinding binding;
    @org.jetbrains.annotations.Nullable
    private java.lang.String deviceUUID;
    @org.jetbrains.annotations.Nullable
    private java.lang.String tosStatus;
    @org.jetbrains.annotations.Nullable
    private java.lang.Boolean crashReport;
    private java.util.List<com.cumulations.libreV2.roomdatabase.CastLiteUUIDDataClass> savedDeviceUUIDList;
    @org.jetbrains.annotations.Nullable
    private java.lang.String deviceName;
    private static final java.lang.String TAG = null;
    @org.jetbrains.annotations.NotNull
    private com.libreAlexa.Scanning.ScanningHandler mScanHandler;
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy libreVoiceDatabaseDao$delegate = null;
    @org.jetbrains.annotations.NotNull
    public static final com.cumulations.libreV2.activity.CTDeviceSettingsActivity.Companion Companion = null;
    
    public CTDeviceSettingsActivity() {
        super();
    }
    
    private final java.lang.String getCurrentDeviceIp() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.libreAlexa.Scanning.ScanningHandler getMScanHandler() {
        return null;
    }
    
    public final void setMScanHandler(@org.jetbrains.annotations.NotNull
    com.libreAlexa.Scanning.ScanningHandler p0) {
    }
    
    private final com.cumulations.libreV2.roomdatabase.CastLiteDao getLibreVoiceDatabaseDao() {
        return null;
    }
    
    @java.lang.Override
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override
    protected void onStart() {
    }
    
    private final void initViews() {
    }
    
    private final void setListeners() {
    }
    
    private final void callActivateCastActivity() {
    }
    
    @java.lang.Override
    protected void onResume() {
    }
    
    private final void showLoader(int progressBarId) {
    }
    
    private final void closeLoader(int progressBarId) {
    }
    
    @java.lang.Override
    public void deviceDiscoveryAfterClearingTheCacheStarted() {
    }
    
    @java.lang.Override
    public void newDeviceFound(@org.jetbrains.annotations.NotNull
    com.libreAlexa.luci.LSSDPNodes node) {
    }
    
    @java.lang.Override
    public void deviceGotRemoved(@org.jetbrains.annotations.Nullable
    java.lang.String ipaddress) {
    }
    
    @java.lang.Override
    public void messageRecieved(@org.jetbrains.annotations.NotNull
    com.libreAlexa.netty.NettyData nettyData) {
    }
    
    private final void requestLuciUpdates() {
    }
    
    private final void updateLang() {
    }
    
    public final void sendUpdatedLangToDevice(@org.jetbrains.annotations.NotNull
    java.lang.String selectedLang) {
    }
    
    public final void updateAudioOutputOfDevice(@org.jetbrains.annotations.NotNull
    com.cumulations.libreV2.tcp_tunneling.enums.AQModeSelect aqModeSelect) {
    }
    
    private final void showAlertDialog(java.lang.String title, java.lang.String message, java.lang.String positiveButton, boolean isForgetNetwork) {
    }
    
    @java.lang.Override
    protected void onStop() {
    }
    
    @java.lang.Override
    public void tunnelDataReceived(@org.jetbrains.annotations.NotNull
    com.cumulations.libreV2.tcp_tunneling.TunnelingData tunnelingData) {
    }
    
    private final void toggleTunnelingVisibility(boolean show) {
    }
    
    private final void fetchUUIDFromDB(java.lang.String speakerIpAddress) {
    }
    
    private final void checkCastActivateStatus(java.lang.String currentDeviceIp) {
    }
    
    private final java.lang.String getUUIDWithIP(java.lang.String currentDeviceIp) {
        return null;
    }
    
    private final void UpdateLSSDPNodeDeviceName(java.lang.String ipaddress, java.lang.String mDeviceName) {
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String utf8truncate(@org.jetbrains.annotations.NotNull
    java.lang.String input, int length) {
        return null;
    }
    
    @java.lang.Override
    public void communicate(@org.jetbrains.annotations.Nullable
    java.lang.String comm) {
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0016\u0010\u0003\u001a\n \u0005*\u0004\u0018\u00010\u00040\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lcom/cumulations/libreV2/activity/CTDeviceSettingsActivity$Companion;", "", "()V", "TAG", "", "kotlin.jvm.PlatformType", "libreoemsdk_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}