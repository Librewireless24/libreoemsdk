package com.cumulations.libreV2.fragments;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u00002\u00020\u00012\u00020\u00022\u00020\u0003B\u0005\u00a2\u0006\u0002\u0010\u0004J\b\u0010\u0010\u001a\u00020\u0011H\u0016J\u0010\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\u0014H\u0016J\b\u0010\u0015\u001a\u00020\u0011H\u0002J\u0010\u0010\u0016\u001a\u00020\u00112\u0006\u0010\u0017\u001a\u00020\u0018H\u0016J\u0010\u0010\u0019\u001a\u00020\u00112\u0006\u0010\u001a\u001a\u00020\u001bH\u0016J\u0012\u0010\u001c\u001a\u00020\u00112\b\u0010\u001d\u001a\u0004\u0018\u00010\u001eH\u0016J$\u0010\u001f\u001a\u00020\u001e2\u0006\u0010 \u001a\u00020!2\b\u0010\"\u001a\u0004\u0018\u00010#2\b\u0010$\u001a\u0004\u0018\u00010%H\u0016J\b\u0010&\u001a\u00020\u0011H\u0016J\b\u0010\'\u001a\u00020\u0011H\u0016J\u001a\u0010(\u001a\u00020\u00112\u0006\u0010)\u001a\u00020\u001e2\b\u0010$\u001a\u0004\u0018\u00010%H\u0016J\b\u0010*\u001a\u00020\u0011H\u0002J\u0010\u0010+\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\u0014H\u0002R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0080\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u0016\u0010\r\u001a\n \u000f*\u0004\u0018\u00010\u000e0\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006,"}, d2 = {"Lcom/cumulations/libreV2/fragments/CTNoWifiFragment;", "Landroidx/fragment/app/Fragment;", "Lcom/libreAlexa/netty/LibreDeviceInteractionListner;", "Landroid/view/View$OnClickListener;", "()V", "binding", "Lcom/libreAlexa/databinding/CtFragmentNoWifiBinding;", "handler", "Landroid/os/Handler;", "getHandler$libreoemsdk_release", "()Landroid/os/Handler;", "setHandler$libreoemsdk_release", "(Landroid/os/Handler;)V", "mScanHandler", "Lcom/libreAlexa/Scanning/ScanningHandler;", "kotlin.jvm.PlatformType", "deviceDiscoveryAfterClearingTheCacheStarted", "", "deviceGotRemoved", "ipaddress", "", "initViews", "messageRecieved", "packet", "Lcom/libreAlexa/netty/NettyData;", "newDeviceFound", "node", "Lcom/libreAlexa/luci/LSSDPNodes;", "onClick", "p0", "Landroid/view/View;", "onCreateView", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onResume", "onStop", "onViewCreated", "view", "setListeners", "updateSceneObjectAndOpenDeviceList", "libreoemsdk_release"})
public final class CTNoWifiFragment extends androidx.fragment.app.Fragment implements com.libreAlexa.netty.LibreDeviceInteractionListner, android.view.View.OnClickListener {
    private final com.libreAlexa.Scanning.ScanningHandler mScanHandler = null;
    @org.jetbrains.annotations.Nullable
    private com.libreAlexa.databinding.CtFragmentNoWifiBinding binding;
    @org.jetbrains.annotations.Nullable
    private android.os.Handler handler;
    
    public CTNoWifiFragment() {
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
    
    private final void setListeners() {
    }
    
    private final void initViews() {
    }
    
    @java.lang.Override
    public void onResume() {
    }
    
    @java.lang.Override
    public void onClick(@org.jetbrains.annotations.Nullable
    android.view.View p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final android.os.Handler getHandler$libreoemsdk_release() {
        return null;
    }
    
    public final void setHandler$libreoemsdk_release(@org.jetbrains.annotations.Nullable
    android.os.Handler p0) {
    }
    
    @java.lang.Override
    public void deviceDiscoveryAfterClearingTheCacheStarted() {
    }
    
    @java.lang.Override
    public void newDeviceFound(@org.jetbrains.annotations.NotNull
    com.libreAlexa.luci.LSSDPNodes node) {
    }
    
    private final void updateSceneObjectAndOpenDeviceList(java.lang.String ipaddress) {
    }
    
    @java.lang.Override
    public void deviceGotRemoved(@org.jetbrains.annotations.NotNull
    java.lang.String ipaddress) {
    }
    
    @java.lang.Override
    public void messageRecieved(@org.jetbrains.annotations.NotNull
    com.libreAlexa.netty.NettyData packet) {
    }
    
    @java.lang.Override
    public void onStop() {
    }
}