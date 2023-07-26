package com.cumulations.libreV2.fragments;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000z\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u00002\u00020\u00012\u00020\u00022\u00020\u0003B\u0005\u00a2\u0006\u0002\u0010\u0004J\b\u0010\u0017\u001a\u00020\u0018H\u0002J\b\u0010\u0019\u001a\u00020\u0018H\u0016J\u0010\u0010\u001a\u001a\u00020\u00182\u0006\u0010\u001b\u001a\u00020\u0006H\u0016J\u0012\u0010\u001c\u001a\u00020\u00182\b\u0010\u001d\u001a\u0004\u0018\u00010\u001eH\u0002J\u0010\u0010\u001f\u001a\u00020\u00182\u0006\u0010 \u001a\u00020!H\u0016J\u0010\u0010\"\u001a\u00020\u00182\u0006\u0010#\u001a\u00020$H\u0016J$\u0010%\u001a\u00020\u001e2\u0006\u0010&\u001a\u00020\'2\b\u0010(\u001a\u0004\u0018\u00010)2\b\u0010*\u001a\u0004\u0018\u00010+H\u0016J\b\u0010,\u001a\u00020\u0018H\u0016J\u0012\u0010-\u001a\u00020\u00182\b\u0010.\u001a\u0004\u0018\u00010/H\u0016J\b\u00100\u001a\u00020\u0018H\u0016J\u001a\u00101\u001a\u00020\u00182\u0006\u0010\u001d\u001a\u00020\u001e2\b\u0010*\u001a\u0004\u0018\u00010+H\u0016J\u0010\u00102\u001a\u00020\u00182\u0006\u00103\u001a\u00020\u0006H\u0002J\u0010\u00104\u001a\u00020\u00182\u0006\u00103\u001a\u00020\u0006H\u0002J\u0006\u00105\u001a\u00020\u0018R\u0016\u0010\u0005\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082.\u00a2\u0006\u0002\n\u0000R\u001a\u0010\f\u001a\u00020\rX\u0080\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0015\u001a\n \u0007*\u0004\u0018\u00010\u00160\u0016X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u00066"}, d2 = {"Lcom/cumulations/libreV2/fragments/CTActiveDevicesFragment;", "Landroidx/fragment/app/Fragment;", "Lcom/libreAlexa/netty/LibreDeviceInteractionListner;", "Lcom/cumulations/libreV2/tcp_tunneling/TunnelingFragmentListener;", "()V", "TAG", "", "kotlin.jvm.PlatformType", "binding", "Lcom/libreAlexa/databinding/CtFragmentDiscoveryListBinding;", "deviceListAdapter", "Lcom/cumulations/libreV2/adapter/CTDeviceListAdapter;", "handler", "Landroid/os/Handler;", "getHandler$oemsdk_debug", "()Landroid/os/Handler;", "setHandler$oemsdk_debug", "(Landroid/os/Handler;)V", "mDeviceFound", "", "mMasterFound", "mScanHandler", "Lcom/libreAlexa/Scanning/ScanningHandler;", "clearScenePreparationFlags", "", "deviceDiscoveryAfterClearingTheCacheStarted", "deviceGotRemoved", "ipaddress", "initViews", "view", "Landroid/view/View;", "messageRecieved", "nettyData", "Lcom/libreAlexa/netty/NettyData;", "newDeviceFound", "node", "Lcom/libreAlexa/luci/LSSDPNodes;", "onCreateView", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onDestroyView", "onFragmentTunnelDataReceived", "tunnelingData", "Lcom/cumulations/libreV2/tcp_tunneling/TunnelingData;", "onResume", "onViewCreated", "removeTimeOutTriggerForIpaddress", "ipAddress", "triggerTimeOutHandlerForIpAddress", "updateFromCentralRepositryDeviceList", "oemsdk_debug"})
public final class CTActiveDevicesFragment extends androidx.fragment.app.Fragment implements com.libreAlexa.netty.LibreDeviceInteractionListner, com.cumulations.libreV2.tcp_tunneling.TunnelingFragmentListener {
    private final com.libreAlexa.Scanning.ScanningHandler mScanHandler = null;
    private com.cumulations.libreV2.adapter.CTDeviceListAdapter deviceListAdapter;
    private com.libreAlexa.databinding.CtFragmentDiscoveryListBinding binding;
    private final java.lang.String TAG = null;
    @org.jetbrains.annotations.NotNull
    private android.os.Handler handler;
    private boolean mDeviceFound = false;
    private boolean mMasterFound = false;
    
    public CTActiveDevicesFragment() {
        super();
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public android.view.View onCreateView(@org.jetbrains.annotations.NotNull
    android.view.LayoutInflater inflater, @org.jetbrains.annotations.Nullable
    android.view.ViewGroup container, @org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
        return null;
    }
    
    @java.lang.Override
    public void onViewCreated(@org.jetbrains.annotations.NotNull
    android.view.View view, @org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    private final void initViews(android.view.View view) {
    }
    
    @java.lang.Override
    public void onResume() {
    }
    
    @org.jetbrains.annotations.NotNull
    public final android.os.Handler getHandler$oemsdk_debug() {
        return null;
    }
    
    public final void setHandler$oemsdk_debug(@org.jetbrains.annotations.NotNull
    android.os.Handler p0) {
    }
    
    private final void removeTimeOutTriggerForIpaddress(java.lang.String ipAddress) {
    }
    
    private final void triggerTimeOutHandlerForIpAddress(java.lang.String ipAddress) {
    }
    
    public final void updateFromCentralRepositryDeviceList() {
    }
    
    @java.lang.Override
    public void deviceDiscoveryAfterClearingTheCacheStarted() {
    }
    
    @java.lang.Override
    public void newDeviceFound(@org.jetbrains.annotations.NotNull
    com.libreAlexa.luci.LSSDPNodes node) {
    }
    
    @java.lang.Override
    public void deviceGotRemoved(@org.jetbrains.annotations.NotNull
    java.lang.String ipaddress) {
    }
    
    @java.lang.Override
    public void messageRecieved(@org.jetbrains.annotations.NotNull
    com.libreAlexa.netty.NettyData nettyData) {
    }
    
    private final void clearScenePreparationFlags() {
    }
    
    @java.lang.Override
    public void onDestroyView() {
    }
    
    @java.lang.Override
    public void onFragmentTunnelDataReceived(@org.jetbrains.annotations.Nullable
    com.cumulations.libreV2.tcp_tunneling.TunnelingData tunnelingData) {
    }
}