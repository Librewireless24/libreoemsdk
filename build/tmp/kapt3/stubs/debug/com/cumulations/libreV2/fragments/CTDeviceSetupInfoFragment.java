package com.cumulations.libreV2.fragments;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\u0018\u00002\u00020\u00012\u00020\u0002B\u0005\u00a2\u0006\u0002\u0010\u0003J\u0010\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0005H\u0002J\b\u0010\u0019\u001a\u00020\u0017H\u0002J\b\u0010\u001a\u001a\u00020\u0017H\u0002J\u0012\u0010\u001b\u001a\u00020\u00172\b\u0010\u001c\u001a\u0004\u0018\u00010\u001dH\u0016J$\u0010\u001e\u001a\u00020\u001d2\u0006\u0010\u001f\u001a\u00020 2\b\u0010!\u001a\u0004\u0018\u00010\"2\b\u0010#\u001a\u0004\u0018\u00010$H\u0016J\b\u0010%\u001a\u00020\u0017H\u0016J\b\u0010&\u001a\u00020\u0017H\u0016J\b\u0010\'\u001a\u00020\u0017H\u0016J\u001a\u0010(\u001a\u00020\u00172\u0006\u0010)\u001a\u00020\u001d2\b\u0010#\u001a\u0004\u0018\u00010$H\u0016J\b\u0010*\u001a\u00020\u0017H\u0002J\b\u0010+\u001a\u00020\u0017H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\b\u001a\u00020\t8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\f\u0010\r\u001a\u0004\b\n\u0010\u000bR\u001c\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0080\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u0010\u0010\u0014\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0015\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006,"}, d2 = {"Lcom/cumulations/libreV2/fragments/CTDeviceSetupInfoFragment;", "Landroidx/fragment/app/Fragment;", "Landroid/view/View$OnClickListener;", "()V", "TAG", "", "binding", "Lcom/libreAlexa/databinding/CtFragmentDeviceSetupInstructionsBinding;", "deviceDiscoveryActivity", "Lcom/cumulations/libreV2/activity/CTDeviceDiscoveryActivity;", "getDeviceDiscoveryActivity", "()Lcom/cumulations/libreV2/activity/CTDeviceDiscoveryActivity;", "deviceDiscoveryActivity$delegate", "Lkotlin/Lazy;", "handler", "Landroid/os/Handler;", "getHandler$libreoemsdk_debug", "()Landroid/os/Handler;", "setHandler$libreoemsdk_debug", "(Landroid/os/Handler;)V", "mDeviceName", "ssid", "callPlayToneRequest", "", "connectedSSID", "getDeviceName", "initViews", "onClick", "p0", "Landroid/view/View;", "onCreateView", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onResume", "onStart", "onStop", "onViewCreated", "view", "retrieveDeviceName", "setListeners", "libreoemsdk_debug"})
public final class CTDeviceSetupInfoFragment extends androidx.fragment.app.Fragment implements android.view.View.OnClickListener {
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy deviceDiscoveryActivity$delegate = null;
    @org.jetbrains.annotations.Nullable
    private java.lang.String mDeviceName;
    @org.jetbrains.annotations.Nullable
    private java.lang.String ssid;
    @org.jetbrains.annotations.Nullable
    private com.libreAlexa.databinding.CtFragmentDeviceSetupInstructionsBinding binding;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String TAG = null;
    @org.jetbrains.annotations.Nullable
    private android.os.Handler handler;
    
    public CTDeviceSetupInfoFragment() {
        super();
    }
    
    private final com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity getDeviceDiscoveryActivity() {
        return null;
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
    public void onStart() {
    }
    
    @java.lang.Override
    public void onResume() {
    }
    
    @java.lang.Override
    public void onClick(@org.jetbrains.annotations.Nullable
    android.view.View p0) {
    }
    
    private final void retrieveDeviceName() {
    }
    
    private final void getDeviceName() {
    }
    
    private final void callPlayToneRequest(java.lang.String connectedSSID) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final android.os.Handler getHandler$libreoemsdk_debug() {
        return null;
    }
    
    public final void setHandler$libreoemsdk_debug(@org.jetbrains.annotations.Nullable
    android.os.Handler p0) {
    }
    
    @java.lang.Override
    public void onStop() {
    }
}