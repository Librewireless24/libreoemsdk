package com.cumulations.libreV2.activity.oem;

/**
 * Created by SHAIK
 * 26/04/2023
 *
 * TC
 * Lazy variables, speakerNode, Ipaddress
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u0000 +2\u00020\u00012\u00020\u0002:\u0001+B\u0005\u00a2\u0006\u0002\u0010\u0003J\b\u0010\u001a\u001a\u00020\u001bH\u0016J\u0012\u0010\u001c\u001a\u00020\u001b2\b\u0010\u001d\u001a\u0004\u0018\u00010\u0007H\u0016J\u0010\u0010\u001e\u001a\u00020\u001b2\u0006\u0010\u0012\u001a\u00020\u0007H\u0002J\u0012\u0010\u001f\u001a\u00020\u001b2\b\u0010 \u001a\u0004\u0018\u00010!H\u0016J\u0012\u0010\"\u001a\u00020\u001b2\b\u0010#\u001a\u0004\u0018\u00010\u0019H\u0016J\u0012\u0010$\u001a\u00020\u001b2\b\u0010%\u001a\u0004\u0018\u00010&H\u0014J\b\u0010\'\u001a\u00020\u001bH\u0014J\b\u0010(\u001a\u00020\u001bH\u0014J\u0010\u0010)\u001a\u00020\u001b2\u0006\u0010*\u001a\u00020\u0007H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001d\u0010\b\u001a\u0004\u0018\u00010\u00078BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000b\u0010\f\u001a\u0004\b\t\u0010\nR\u001b\u0010\r\u001a\u00020\u000e8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0011\u0010\f\u001a\u0004\b\u000f\u0010\u0010R\u001d\u0010\u0012\u001a\u0004\u0018\u00010\u00078BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0014\u0010\f\u001a\u0004\b\u0013\u0010\nR\u001d\u0010\u0015\u001a\u0004\u0018\u00010\u00078BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0017\u0010\f\u001a\u0004\b\u0016\u0010\nR\u0010\u0010\u0018\u001a\u0004\u0018\u00010\u0019X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006,"}, d2 = {"Lcom/cumulations/libreV2/activity/oem/SetUpDeviceActivity;", "Lcom/cumulations/libreV2/activity/CTDeviceDiscoveryActivity;", "Lcom/libreAlexa/netty/LibreDeviceInteractionListner;", "()V", "binding", "Lcom/libreAlexa/databinding/ActivitySetUpDeviceBinding;", "deviceUUID", "", "from", "getFrom", "()Ljava/lang/String;", "from$delegate", "Lkotlin/Lazy;", "libreVoiceDatabaseDao", "Lcom/cumulations/libreV2/roomdatabase/CastLiteDao;", "getLibreVoiceDatabaseDao", "()Lcom/cumulations/libreV2/roomdatabase/CastLiteDao;", "libreVoiceDatabaseDao$delegate", "speakerIpAddress", "getSpeakerIpAddress", "speakerIpAddress$delegate", "speakerName", "getSpeakerName", "speakerName$delegate", "speakerNode", "Lcom/libreAlexa/luci/LSSDPNodes;", "deviceDiscoveryAfterClearingTheCacheStarted", "", "deviceGotRemoved", "ipaddress", "fetchUUIDFromDB", "messageRecieved", "nettyData", "Lcom/libreAlexa/netty/NettyData;", "newDeviceFound", "node", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onResume", "onStop", "sendLuciCommand", "data", "Companion", "oemsdk_release"})
public final class SetUpDeviceActivity extends com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity implements com.libreAlexa.netty.LibreDeviceInteractionListner {
    private com.libreAlexa.databinding.ActivitySetUpDeviceBinding binding;
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy speakerIpAddress$delegate = null;
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy from$delegate = null;
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy speakerName$delegate = null;
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy libreVoiceDatabaseDao$delegate = null;
    @org.jetbrains.annotations.Nullable
    private java.lang.String deviceUUID;
    @kotlin.jvm.JvmField
    @org.jetbrains.annotations.NotNull
    public static java.lang.String TAG;
    @org.jetbrains.annotations.Nullable
    private com.libreAlexa.luci.LSSDPNodes speakerNode;
    @org.jetbrains.annotations.NotNull
    public static final com.cumulations.libreV2.activity.oem.SetUpDeviceActivity.Companion Companion = null;
    
    public SetUpDeviceActivity() {
        super();
    }
    
    private final java.lang.String getSpeakerIpAddress() {
        return null;
    }
    
    private final java.lang.String getFrom() {
        return null;
    }
    
    private final java.lang.String getSpeakerName() {
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
    protected void onResume() {
    }
    
    @java.lang.Override
    protected void onStop() {
    }
    
    private final void fetchUUIDFromDB(java.lang.String speakerIpAddress) {
    }
    
    private final void sendLuciCommand(java.lang.String data) {
    }
    
    @java.lang.Override
    public void deviceDiscoveryAfterClearingTheCacheStarted() {
    }
    
    @java.lang.Override
    public void newDeviceFound(@org.jetbrains.annotations.Nullable
    com.libreAlexa.luci.LSSDPNodes node) {
    }
    
    @java.lang.Override
    public void deviceGotRemoved(@org.jetbrains.annotations.Nullable
    java.lang.String ipaddress) {
    }
    
    @java.lang.Override
    public void messageRecieved(@org.jetbrains.annotations.Nullable
    com.libreAlexa.netty.NettyData nettyData) {
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0012\u0010\u0003\u001a\u00020\u00048\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/cumulations/libreV2/activity/oem/SetUpDeviceActivity$Companion;", "", "()V", "TAG", "", "oemsdk_release"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}