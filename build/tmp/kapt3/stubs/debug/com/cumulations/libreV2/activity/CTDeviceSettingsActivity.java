package com.cumulations.libreV2.activity;

/**
 * Added ChromeCast Settings by SHAIK
 * 18/04/2023
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000t\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 G2\u00020\u00012\u00020\u0002:\u0001GB\u0005\u00a2\u0006\u0002\u0010\u0003J\b\u0010\u001f\u001a\u00020 H\u0002J\u0012\u0010!\u001a\u00020 2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0002J\u0010\u0010\"\u001a\u00020 2\u0006\u0010#\u001a\u00020$H\u0002J\b\u0010%\u001a\u00020 H\u0016J\u0012\u0010&\u001a\u00020 2\b\u0010\'\u001a\u0004\u0018\u00010\fH\u0016J\u0010\u0010(\u001a\u00020 2\u0006\u0010)\u001a\u00020\fH\u0002J\b\u0010*\u001a\u00020 H\u0002J\u0010\u0010+\u001a\u00020 2\u0006\u0010,\u001a\u00020-H\u0016J\u0010\u0010.\u001a\u00020 2\u0006\u0010/\u001a\u00020\u0012H\u0016J\u0012\u00100\u001a\u00020 2\b\u00101\u001a\u0004\u0018\u000102H\u0014J\b\u00103\u001a\u00020 H\u0014J\b\u00104\u001a\u00020 H\u0014J\b\u00105\u001a\u00020 H\u0014J\b\u00106\u001a\u00020 H\u0002J\u000e\u00107\u001a\u00020 2\u0006\u00108\u001a\u00020\fJ\b\u00109\u001a\u00020 H\u0002J\u0018\u0010:\u001a\u00020 2\u0006\u0010;\u001a\u00020\f2\u0006\u0010<\u001a\u00020\fH\u0002J\u0010\u0010=\u001a\u00020 2\u0006\u0010#\u001a\u00020$H\u0002J\u0010\u0010>\u001a\u00020 2\u0006\u0010?\u001a\u00020\tH\u0002J\u0010\u0010@\u001a\u00020 2\u0006\u0010A\u001a\u00020BH\u0016J\u000e\u0010C\u001a\u00020 2\u0006\u0010D\u001a\u00020EJ\b\u0010F\u001a\u00020 H\u0002R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082.\u00a2\u0006\u0002\n\u0000R\u0012\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0004\n\u0002\u0010\nR\u001d\u0010\u000b\u001a\u0004\u0018\u00010\f8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000f\u0010\u0010\u001a\u0004\b\r\u0010\u000eR\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0013\u001a\u0004\u0018\u00010\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0014\u001a\u0004\u0018\u00010\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0015\u001a\u00020\u00168BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0019\u0010\u0010\u001a\u0004\b\u0017\u0010\u0018R\u000e\u0010\u001a\u001a\u00020\u001bX\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001c\u001a\u0004\u0018\u00010\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001d\u001a\u0004\u0018\u00010\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001e\u001a\u0004\u0018\u00010\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006H"}, d2 = {"Lcom/cumulations/libreV2/activity/CTDeviceSettingsActivity;", "Lcom/cumulations/libreV2/activity/CTDeviceDiscoveryActivity;", "Lcom/libreAlexa/netty/LibreDeviceInteractionListner;", "()V", "awayModeSettingsDialog", "Landroidx/appcompat/app/AlertDialog;", "binding", "Lcom/libreAlexa/databinding/CtDeviceSettingsBinding;", "crashReport", "", "Ljava/lang/Boolean;", "currentDeviceIp", "", "getCurrentDeviceIp", "()Ljava/lang/String;", "currentDeviceIp$delegate", "Lkotlin/Lazy;", "currentDeviceNode", "Lcom/libreAlexa/luci/LSSDPNodes;", "currentLocale", "deviceUUID", "libreVoiceDatabaseDao", "Lcom/cumulations/libreV2/roomdatabase/CastLiteDao;", "getLibreVoiceDatabaseDao", "()Lcom/cumulations/libreV2/roomdatabase/CastLiteDao;", "libreVoiceDatabaseDao$delegate", "luciControl", "Lcom/libreAlexa/luci/LUCIControl;", "seekbarVolumeValue", "switchStatus", "tosStatus", "callActivateCastActivity", "", "checkCastActivateStatus", "closeLoader", "progressBarId", "", "deviceDiscoveryAfterClearingTheCacheStarted", "deviceGotRemoved", "ipaddress", "fetchUUIDFromDB", "speakerIpAddress", "initViews", "messageRecieved", "nettyData", "Lcom/libreAlexa/netty/NettyData;", "newDeviceFound", "node", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onResume", "onStart", "onStop", "requestLuciUpdates", "sendUpdatedLangToDevice", "selectedLang", "setListeners", "showAwayModeAlert", "ssid", "pwd", "showLoader", "toggleTunnelingVisibility", "show", "tunnelDataReceived", "tunnelingData", "Lcom/cumulations/libreV2/tcp_tunneling/TunnelingData;", "updateAudioOutputOfDevice", "aqModeSelect", "Lcom/cumulations/libreV2/tcp_tunneling/enums/AQModeSelect;", "updateLang", "Companion", "oemsdk_debug"})
public final class CTDeviceSettingsActivity extends com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity implements com.libreAlexa.netty.LibreDeviceInteractionListner {
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
    private androidx.appcompat.app.AlertDialog awayModeSettingsDialog;
    private com.libreAlexa.databinding.CtDeviceSettingsBinding binding;
    @org.jetbrains.annotations.Nullable
    private java.lang.String deviceUUID;
    @org.jetbrains.annotations.Nullable
    private java.lang.String tosStatus;
    @org.jetbrains.annotations.Nullable
    private java.lang.Boolean crashReport;
    private static final java.lang.String TAG = null;
    @org.jetbrains.annotations.NotNull
    private static final java.lang.String TAG_CHROME = "CAST STATUS";
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
    
    private final void showAwayModeAlert(java.lang.String ssid, java.lang.String pwd) {
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
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0016\u0010\u0003\u001a\n \u0005*\u0004\u0018\u00010\u00040\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082D\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/cumulations/libreV2/activity/CTDeviceSettingsActivity$Companion;", "", "()V", "TAG", "", "kotlin.jvm.PlatformType", "TAG_CHROME", "oemsdk_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}