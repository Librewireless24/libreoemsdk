package com.cumulations.libreV2.activity;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000p\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u00012\u00020\u0002B\u0005\u00a2\u0006\u0002\u0010\u0003J\b\u0010\u0017\u001a\u00020\u0018H\u0002J\b\u0010\u0019\u001a\u00020\u0018H\u0016J\u0012\u0010\u001a\u001a\u00020\u00182\b\u0010\u001b\u001a\u0004\u0018\u00010\u0005H\u0016J\b\u0010\u001c\u001a\u00020\u0018H\u0002J\u001a\u0010\u001d\u001a\u00020\n2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001f2\u0006\u0010 \u001a\u00020\nH\u0002J\u0012\u0010!\u001a\u00020\u00182\b\u0010\"\u001a\u0004\u0018\u00010#H\u0016J\u0012\u0010$\u001a\u00020\u00182\b\u0010%\u001a\u0004\u0018\u00010&H\u0016J\b\u0010\'\u001a\u00020\u0018H\u0016J\u0012\u0010(\u001a\u00020\u00182\b\u0010)\u001a\u0004\u0018\u00010*H\u0014J\b\u0010+\u001a\u00020\u0018H\u0014J\b\u0010,\u001a\u00020\u0018H\u0014J\u0016\u0010-\u001a\u00020\u00182\u0006\u0010.\u001a\u00020\u00052\u0006\u0010 \u001a\u00020\nJ\u0006\u0010/\u001a\u00020\u0018J\b\u00100\u001a\u00020\u0018H\u0002J\u0006\u00101\u001a\u00020\u0018J\b\u00102\u001a\u00020\u0018H\u0002J\u000e\u00103\u001a\u00020\u00182\u0006\u0010\u0013\u001a\u00020\u0014J\u0010\u00104\u001a\u00020\u00182\u0006\u00105\u001a\u00020\nH\u0002J\b\u00106\u001a\u00020\u0018H\u0002J\u0006\u00107\u001a\u00020\u0018J\u0010\u00108\u001a\u00020\u00182\u0006\u00109\u001a\u00020:H\u0016J\u0010\u0010;\u001a\u00020\u00182\u0006\u0010<\u001a\u00020\nH\u0016R\u0016\u0010\u0004\u001a\n \u0006*\u0004\u0018\u00010\u00050\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0015\u001a\u0004\u0018\u00010\u0016X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006="}, d2 = {"Lcom/cumulations/libreV2/activity/CTHomeTabsActivity;", "Lcom/cumulations/libreV2/activity/CTDeviceDiscoveryActivity;", "Lcom/libreAlexa/netty/LibreDeviceInteractionListner;", "()V", "TAG", "", "kotlin.jvm.PlatformType", "binding", "Lcom/libreAlexa/databinding/CtActivityHomeTabsBinding;", "isActivityVisible", "", "isDoubleTap", "loadFragmentName", "mMyTaskRunnableForMSearch", "Ljava/lang/Runnable;", "mTaskHandlerForSendingMSearch", "Landroid/os/Handler;", "otherTabClicked", "tabSelected", "tunnelingFragmentListener", "Lcom/cumulations/libreV2/tcp_tunneling/TunnelingFragmentListener;", "wifiUtil", "Lcom/cumulations/libreV2/WifiUtil;", "clearBatteryInfoForDevices", "", "deviceDiscoveryAfterClearingTheCacheStarted", "deviceGotRemoved", "ipaddress", "initViews", "loadFragment", "fragment", "Landroidx/fragment/app/Fragment;", "animate", "messageRecieved", "packet", "Lcom/libreAlexa/netty/NettyData;", "newDeviceFound", "node", "Lcom/libreAlexa/luci/LSSDPNodes;", "onBackPressed", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onStart", "onStop", "openFragment", "fragmentClassName", "refreshDevices", "removeAllFragments", "removeTunnelFragmentListener", "setListeners", "setTunnelFragmentListener", "showLoader", "show", "showScreenAfterDelay", "toggleStopAllButtonVisibility", "tunnelDataReceived", "tunnelingData", "Lcom/cumulations/libreV2/tcp_tunneling/TunnelingData;", "wifiConnected", "connected", "libreoemsdk_release"})
public final class CTHomeTabsActivity extends com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity implements com.libreAlexa.netty.LibreDeviceInteractionListner {
    @org.jetbrains.annotations.Nullable
    private com.cumulations.libreV2.WifiUtil wifiUtil;
    @org.jetbrains.annotations.NotNull
    private java.lang.String tabSelected = "";
    @org.jetbrains.annotations.Nullable
    private java.lang.String loadFragmentName;
    private boolean isDoubleTap = false;
    @org.jetbrains.annotations.NotNull
    private final android.os.Handler mTaskHandlerForSendingMSearch = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.Runnable mMyTaskRunnableForMSearch = null;
    private boolean isActivityVisible = true;
    @org.jetbrains.annotations.Nullable
    private com.cumulations.libreV2.tcp_tunneling.TunnelingFragmentListener tunnelingFragmentListener;
    private boolean otherTabClicked = false;
    private com.libreAlexa.databinding.CtActivityHomeTabsBinding binding;
    private final java.lang.String TAG = null;
    
    public CTHomeTabsActivity() {
        super();
    }
    
    @java.lang.Override
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override
    protected void onStart() {
    }
    
    public final void toggleStopAllButtonVisibility() {
    }
    
    private final void setListeners() {
    }
    
    private final void initViews() {
    }
    
    private final void showLoader(boolean show) {
    }
    
    private final void removeAllFragments() {
    }
    
    public final void refreshDevices() {
    }
    
    private final void showScreenAfterDelay() {
    }
    
    public final void openFragment(@org.jetbrains.annotations.NotNull
    java.lang.String fragmentClassName, boolean animate) {
    }
    
    private final boolean loadFragment(androidx.fragment.app.Fragment fragment, boolean animate) {
        return false;
    }
    
    @java.lang.Override
    public void wifiConnected(boolean connected) {
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
    
    @java.lang.Override
    public void onBackPressed() {
    }
    
    public final void setTunnelFragmentListener(@org.jetbrains.annotations.NotNull
    com.cumulations.libreV2.tcp_tunneling.TunnelingFragmentListener tunnelingFragmentListener) {
    }
    
    public final void removeTunnelFragmentListener() {
    }
    
    @java.lang.Override
    public void tunnelDataReceived(@org.jetbrains.annotations.NotNull
    com.cumulations.libreV2.tcp_tunneling.TunnelingData tunnelingData) {
    }
    
    private final void clearBatteryInfoForDevices() {
    }
}