package com.cumulations.libreV2;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0003\u0018\u0000 &2\u00020\u0001:\u0002&\'B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001e\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\u0015J\u000e\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u0015J\u0010\u0010\u001b\u001a\u00020\u00152\u0006\u0010\u001c\u001a\u00020\u0013H\u0002J\u0012\u0010\u001d\u001a\u0004\u0018\u00010\u001e2\u0006\u0010\u001f\u001a\u00020\u0015H\u0002J\u0010\u0010 \u001a\u00020\u00152\u0006\u0010!\u001a\u00020\rH\u0002J\u0010\u0010\"\u001a\u00020\u00152\u0006\u0010\u001f\u001a\u00020\u0015H\u0002J\u0006\u0010#\u001a\u00020\u0019J\u0006\u0010$\u001a\u00020%R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u0007\u001a\u00060\bR\u00020\u0000X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006("}, d2 = {"Lcom/cumulations/libreV2/WifiUtil;", "Lcom/cumulations/libreV2/activity/CTDeviceDiscoveryActivity;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "connectivityManager", "Landroid/net/ConnectivityManager;", "myBroadCastReceiver", "Lcom/cumulations/libreV2/WifiUtil$MyBroadcastReceiver;", "suggestionsList", "", "Landroid/net/wifi/WifiNetworkSuggestion;", "wifiList", "Landroid/net/wifi/ScanResult;", "wifiManager", "Landroid/net/wifi/WifiManager;", "wifiNetworkSpecifier", "Landroid/net/NetworkSpecifier;", "connectWiFiToSSID", "", "networkSSID", "", "networkPass", "networkSec", "disconnectCurrentWifi", "", "deviceName", "formatIP", "ip", "getExistingWifiConfig", "Landroid/net/wifi/WifiConfiguration;", "ssid", "getScanResultSecurity", "scanResult", "getSecurityType", "isWifiOn", "unregister", "", "Companion", "MyBroadcastReceiver", "oemsdk_debug"})
public final class WifiUtil extends com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity {
    @org.jetbrains.annotations.NotNull
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String WPA2 = "WPA2";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String WPA = "WPA";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String WEP = "WEP";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String OPEN = "Open";
    @org.jetbrains.annotations.NotNull
    private static final java.lang.String TAG = null;
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String WPA_EAP = "WPA-EAP";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String IEEE8021X = "IEEE8021X";
    @org.jetbrains.annotations.NotNull
    private final android.net.wifi.WifiManager wifiManager = null;
    @org.jetbrains.annotations.Nullable
    private final android.net.ConnectivityManager connectivityManager = null;
    @org.jetbrains.annotations.NotNull
    private java.util.List<android.net.wifi.ScanResult> wifiList;
    @org.jetbrains.annotations.NotNull
    private final com.cumulations.libreV2.WifiUtil.MyBroadcastReceiver myBroadCastReceiver = null;
    private android.net.NetworkSpecifier wifiNetworkSpecifier;
    private java.util.List<android.net.wifi.WifiNetworkSuggestion> suggestionsList;
    @org.jetbrains.annotations.NotNull
    public static final com.cumulations.libreV2.WifiUtil.Companion Companion = null;
    
    public WifiUtil(@org.jetbrains.annotations.NotNull
    android.content.Context context) {
        super();
    }
    
    /**
     * Commented By:-  SHAIK MANSOOR
     * TRANSPORT_WIFI - Indicates this network uses a Wi-Fi transport.
     * We can check if the device is connected to wifi or a cellular network by using the
     * hasTransport() method of NetworkCapabilities class.
     * For the devices below Marshmallow (API 23 or below android 6), we can use NetworkInfo
     * .State API
     * Note: NetworkInfo has been deprecated in API 29. We are using it in marshmallow (API 23),
     * so we don’t get any problems.
     */
    public final boolean isWifiOn() {
        return false;
    }
    
    private final java.lang.String formatIP(int ip) {
        return null;
    }
    
    public final void unregister() {
    }
    
    /**
     * Commented By:-  SHAIK MANSOOR
     * The below function will call very rarely that is when securityType(WPA,WPA2,Open) is empty
     * then only the below function will call but here again the wifiList is empty because the
     * WifiList is assigning inside the "MyBroadcastReceiver" onReceive method but the
     * "MyBroadcastReceiver" is never calling
     * Discussed with SUMA.
     */
    private final java.lang.String getSecurityType(java.lang.String ssid) {
        return null;
    }
    
    private final java.lang.String getScanResultSecurity(android.net.wifi.ScanResult scanResult) {
        return null;
    }
    
    /**
     * Commented By:-  SHAIK MANSOOR
     * SHAIk Observation
     * The below disconnectCurrentWifi function will disconnect the device ssid (WIFI i;e
     * Libre16BF.d) and try to connect to available WIFI, which is user selected WIFI from the
     * ScanList(WIFI-List)and PWD.
     * BUT the "disconnect" method is deprecated in 29, So I have migrated to the latest API for
     * the above Android version 10, i;e "removeNetworkSuggestions"
     * ==========The Observation is here==========
     * In the above Android version 10 devices all are working fine, tested in the 4
     * devices (Pixel-13, OnePlusNord-CE2-13,RealMe-11,Samsung TAB-12) except "OnePlusNord" device
     * (AC2001), this device behavior is very different,after disconnecting the device SSID, the
     * "removeNetworkSuggestions" method is navigating to the WIFI select screen in the mobile,
     * discussed the same with SUMA.
     */
    public final boolean disconnectCurrentWifi(@org.jetbrains.annotations.NotNull
    java.lang.String deviceName) {
        return false;
    }
    
    public final int connectWiFiToSSID(@org.jetbrains.annotations.NotNull
    java.lang.String networkSSID, @org.jetbrains.annotations.NotNull
    java.lang.String networkPass, @org.jetbrains.annotations.NotNull
    java.lang.String networkSec) {
        return 0;
    }
    
    private final android.net.wifi.WifiConfiguration getExistingWifiConfig(java.lang.String ssid) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\t\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0006\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u000e\u0010\t\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"Lcom/cumulations/libreV2/WifiUtil$Companion;", "", "()V", "IEEE8021X", "", "OPEN", "TAG", "getTAG", "()Ljava/lang/String;", "WEP", "WPA", "WPA2", "WPA_EAP", "oemsdk_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String getTAG() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u001c\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\bH\u0016\u00a8\u0006\t"}, d2 = {"Lcom/cumulations/libreV2/WifiUtil$MyBroadcastReceiver;", "Landroid/content/BroadcastReceiver;", "(Lcom/cumulations/libreV2/WifiUtil;)V", "onReceive", "", "context", "Landroid/content/Context;", "intent", "Landroid/content/Intent;", "oemsdk_debug"})
    public final class MyBroadcastReceiver extends android.content.BroadcastReceiver {
        
        public MyBroadcastReceiver() {
            super();
        }
        
        @java.lang.Override
        public void onReceive(@org.jetbrains.annotations.Nullable
        android.content.Context context, @org.jetbrains.annotations.Nullable
        android.content.Intent intent) {
        }
    }
}