package com.cumulations.libreV2.activity.oem;

/**
 * Created by SHAIK
 * 24/04/2023
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u0000 (2\u00020\u00012\u00020\u0002:\u0001(B\u0005\u00a2\u0006\u0002\u0010\u0003J\b\u0010\u0017\u001a\u00020\u0018H\u0016J\u0012\u0010\u0019\u001a\u00020\u00182\b\u0010\u001a\u001a\u0004\u0018\u00010\u0007H\u0016J\b\u0010\u001b\u001a\u00020\u0018H\u0002J\u0012\u0010\u001c\u001a\u00020\u00182\b\u0010\u001d\u001a\u0004\u0018\u00010\u001eH\u0016J\u0012\u0010\u001f\u001a\u00020\u00182\b\u0010 \u001a\u0004\u0018\u00010!H\u0016J\u0012\u0010\"\u001a\u00020\u00182\b\u0010#\u001a\u0004\u0018\u00010$H\u0014J\b\u0010%\u001a\u00020\u0018H\u0014J\b\u0010&\u001a\u00020\u0018H\u0014J\b\u0010\'\u001a\u00020\u0018H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0006\u001a\u0004\u0018\u00010\u00078BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\n\u0010\u000b\u001a\u0004\b\b\u0010\tR\u001d\u0010\f\u001a\u0004\u0018\u00010\u00078BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000e\u0010\u000b\u001a\u0004\b\r\u0010\tR\u001d\u0010\u000f\u001a\u0004\u0018\u00010\u00078BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0011\u0010\u000b\u001a\u0004\b\u0010\u0010\tR\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0014\u001a\u0004\u0018\u00010\u00078BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0016\u0010\u000b\u001a\u0004\b\u0015\u0010\t\u00a8\u0006)"}, d2 = {"Lcom/cumulations/libreV2/activity/oem/OpenGHomeAppActivity;", "Lcom/cumulations/libreV2/activity/CTDeviceDiscoveryActivity;", "Lcom/libreAlexa/netty/LibreDeviceInteractionListner;", "()V", "binding", "Lcom/libreAlexa/databinding/ActivityOpenHomeAppBinding;", "currentDeviceUUID", "", "getCurrentDeviceUUID", "()Ljava/lang/String;", "currentDeviceUUID$delegate", "Lkotlin/Lazy;", "currentIpAddress", "getCurrentIpAddress", "currentIpAddress$delegate", "from", "getFrom", "from$delegate", "isHomeAppOpen", "", "speakerName", "getSpeakerName", "speakerName$delegate", "deviceDiscoveryAfterClearingTheCacheStarted", "", "deviceGotRemoved", "ipaddress", "exitOnBackPressed", "messageRecieved", "packet", "Lcom/libreAlexa/netty/NettyData;", "newDeviceFound", "node", "Lcom/libreAlexa/luci/LSSDPNodes;", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onResume", "onStop", "showAppNotInstalledAlertDialog", "Companion", "libreoemsdk_debug"})
public final class OpenGHomeAppActivity extends com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity implements com.libreAlexa.netty.LibreDeviceInteractionListner {
    @org.jetbrains.annotations.NotNull
    private static final java.lang.String TAG = null;
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy currentIpAddress$delegate = null;
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy currentDeviceUUID$delegate = null;
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy speakerName$delegate = null;
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy from$delegate = null;
    private boolean isHomeAppOpen = false;
    private com.libreAlexa.databinding.ActivityOpenHomeAppBinding binding;
    @org.jetbrains.annotations.NotNull
    public static final com.cumulations.libreV2.activity.oem.OpenGHomeAppActivity.Companion Companion = null;
    
    public OpenGHomeAppActivity() {
        super();
    }
    
    private final java.lang.String getCurrentIpAddress() {
        return null;
    }
    
    private final java.lang.String getCurrentDeviceUUID() {
        return null;
    }
    
    private final java.lang.String getSpeakerName() {
        return null;
    }
    
    private final java.lang.String getFrom() {
        return null;
    }
    
    @java.lang.Override
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    private final void showAppNotInstalledAlertDialog() {
    }
    
    private final void exitOnBackPressed() {
    }
    
    @java.lang.Override
    protected void onResume() {
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
    com.libreAlexa.netty.NettyData packet) {
    }
    
    @java.lang.Override
    protected void onStop() {
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/cumulations/libreV2/activity/oem/OpenGHomeAppActivity$Companion;", "", "()V", "TAG", "", "getTAG", "()Ljava/lang/String;", "libreoemsdk_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String getTAG() {
            return null;
        }
    }
}