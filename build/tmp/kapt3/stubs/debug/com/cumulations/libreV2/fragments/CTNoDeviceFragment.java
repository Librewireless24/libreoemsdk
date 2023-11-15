package com.cumulations.libreV2.fragments;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000d\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u00002\u00020\u00012\u00020\u00022\u00020\u0003B\u0005\u00a2\u0006\u0002\u0010\u0004J\b\u0010\u0012\u001a\u00020\u0013H\u0016J\u0010\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0015\u001a\u00020\u0006H\u0016J\b\u0010\u0016\u001a\u00020\u0013H\u0002J\u0010\u0010\u0017\u001a\u00020\u00132\u0006\u0010\u0018\u001a\u00020\u0019H\u0016J\u0010\u0010\u001a\u001a\u00020\u00132\u0006\u0010\u001b\u001a\u00020\u001cH\u0016J\u0012\u0010\u001d\u001a\u00020\u00132\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fH\u0016J$\u0010 \u001a\u00020\u001f2\u0006\u0010!\u001a\u00020\"2\b\u0010#\u001a\u0004\u0018\u00010$2\b\u0010%\u001a\u0004\u0018\u00010&H\u0016J\b\u0010\'\u001a\u00020\u0013H\u0016J\b\u0010(\u001a\u00020\u0013H\u0016J\u001a\u0010)\u001a\u00020\u00132\u0006\u0010*\u001a\u00020\u001f2\b\u0010%\u001a\u0004\u0018\u00010&H\u0016J\b\u0010+\u001a\u00020\u0013H\u0002J\u0010\u0010,\u001a\u00020\u00132\u0006\u0010\u0015\u001a\u00020\u0006H\u0002R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010\t\u001a\u0004\u0018\u00010\nX\u0080\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u0016\u0010\u000f\u001a\n \u0011*\u0004\u0018\u00010\u00100\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006-"}, d2 = {"Lcom/cumulations/libreV2/fragments/CTNoDeviceFragment;", "Landroidx/fragment/app/Fragment;", "Lcom/libreAlexa/netty/LibreDeviceInteractionListner;", "Landroid/view/View$OnClickListener;", "()V", "TAG", "", "binding", "Lcom/libreAlexa/databinding/CtFragmentNoDeviceBinding;", "handler", "Landroid/os/Handler;", "getHandler$libreoemsdk_debug", "()Landroid/os/Handler;", "setHandler$libreoemsdk_debug", "(Landroid/os/Handler;)V", "mScanHandler", "Lcom/libreAlexa/Scanning/ScanningHandler;", "kotlin.jvm.PlatformType", "deviceDiscoveryAfterClearingTheCacheStarted", "", "deviceGotRemoved", "ipaddress", "initViews", "messageRecieved", "packet", "Lcom/libreAlexa/netty/NettyData;", "newDeviceFound", "node", "Lcom/libreAlexa/luci/LSSDPNodes;", "onClick", "p0", "Landroid/view/View;", "onCreateView", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onResume", "onStop", "onViewCreated", "view", "setListeners", "updateSceneObjectAndOpenDeviceList", "libreoemsdk_debug"})
public final class CTNoDeviceFragment extends androidx.fragment.app.Fragment implements com.libreAlexa.netty.LibreDeviceInteractionListner, android.view.View.OnClickListener {
    private final com.libreAlexa.Scanning.ScanningHandler mScanHandler = null;
    @org.jetbrains.annotations.Nullable
    private com.libreAlexa.databinding.CtFragmentNoDeviceBinding binding;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String TAG = null;
    @org.jetbrains.annotations.Nullable
    private android.os.Handler handler;
    
    public CTNoDeviceFragment() {
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
    public final android.os.Handler getHandler$libreoemsdk_debug() {
        return null;
    }
    
    public final void setHandler$libreoemsdk_debug(@org.jetbrains.annotations.Nullable
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