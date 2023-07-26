package com.cumulations.libreV2.fragments;

/**
 * Created by Amit Tumkur on 05-06-2018.
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u00012\u00020\u0002B\u0005\u00a2\u0006\u0002\u0010\u0003J\b\u0010\u0015\u001a\u00020\u0016H\u0002J\u0012\u0010\u0017\u001a\u00020\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u001aH\u0016J\b\u0010\u001b\u001a\u00020\u0016H\u0016J\u0010\u0010\u001c\u001a\u00020\u00162\u0006\u0010\u001d\u001a\u00020\u001eH\u0016J\u0012\u0010\u001f\u001a\u00020\u00162\b\u0010 \u001a\u0004\u0018\u00010!H\u0016J\u0012\u0010\"\u001a\u00020\u00162\b\u0010 \u001a\u0004\u0018\u00010!H\u0016J\u0012\u0010#\u001a\u00020\u00162\b\u0010 \u001a\u0004\u0018\u00010$H\u0016J\u0012\u0010%\u001a\u00020\u00162\b\u0010 \u001a\u0004\u0018\u00010$H\u0016J\b\u0010&\u001a\u00020\u0016H\u0016J\b\u0010\'\u001a\u00020\u0016H\u0016J\b\u0010(\u001a\u00020\u0016H\u0016J\b\u0010)\u001a\u00020\u0016H\u0002R\u0016\u0010\u0004\u001a\n \u0006*\u0004\u0018\u00010\u00050\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\u00020\b8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\t\u0010\nR\u001d\u0010\u000b\u001a\u0004\u0018\u00010\u00058BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000e\u0010\u000f\u001a\u0004\b\f\u0010\rR\u0010\u0010\u0010\u001a\u0004\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0011\u001a\n\u0012\u0004\u0012\u00020\u0005\u0018\u00010\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u0014X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006*"}, d2 = {"Lcom/cumulations/libreV2/fragments/CTMediaServerListFragment;", "Landroidx/fragment/app/DialogFragment;", "Lcom/libreAlexa/app/dlna/dmc/processor/interfaces/UpnpProcessor$UpnpProcessorListener;", "()V", "TAG", "", "kotlin.jvm.PlatformType", "binding", "Lcom/libreAlexa/databinding/CtDlgFragmentMediaServersBinding;", "getBinding", "()Lcom/libreAlexa/databinding/CtDlgFragmentMediaServersBinding;", "currentDeviceIp", "getCurrentDeviceIp", "()Ljava/lang/String;", "currentDeviceIp$delegate", "Lkotlin/Lazy;", "dialogBinding", "listAdapter", "Landroid/widget/ArrayAdapter;", "nameToUDNMap", "Ljava/util/HashMap;", "closeLoader", "", "onCreateDialog", "Landroid/app/Dialog;", "savedInstanceState", "Landroid/os/Bundle;", "onDestroyView", "onDismiss", "dialog", "Landroid/content/DialogInterface;", "onLocalDeviceAdded", "device", "Lorg/fourthline/cling/model/meta/LocalDevice;", "onLocalDeviceRemoved", "onRemoteDeviceAdded", "Lorg/fourthline/cling/model/meta/RemoteDevice;", "onRemoteDeviceRemoved", "onResume", "onStart", "onStartComplete", "showLoader", "oemsdk_debug"})
public final class CTMediaServerListFragment extends androidx.fragment.app.DialogFragment implements com.libreAlexa.app.dlna.dmc.processor.interfaces.UpnpProcessor.UpnpProcessorListener {
    @org.jetbrains.annotations.Nullable
    private android.widget.ArrayAdapter<java.lang.String> listAdapter;
    @org.jetbrains.annotations.NotNull
    private final java.util.HashMap<java.lang.String, java.lang.String> nameToUDNMap = null;
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy currentDeviceIp$delegate = null;
    @org.jetbrains.annotations.Nullable
    private com.libreAlexa.databinding.CtDlgFragmentMediaServersBinding dialogBinding;
    private final java.lang.String TAG = null;
    
    public CTMediaServerListFragment() {
        super();
    }
    
    private final java.lang.String getCurrentDeviceIp() {
        return null;
    }
    
    private final com.libreAlexa.databinding.CtDlgFragmentMediaServersBinding getBinding() {
        return null;
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public android.app.Dialog onCreateDialog(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
        return null;
    }
    
    @java.lang.Override
    public void onStart() {
    }
    
    @java.lang.Override
    public void onStartComplete() {
    }
    
    @java.lang.Override
    public void onResume() {
    }
    
    @java.lang.Override
    public void onRemoteDeviceAdded(@org.jetbrains.annotations.Nullable
    org.fourthline.cling.model.meta.RemoteDevice device) {
    }
    
    @java.lang.Override
    public void onRemoteDeviceRemoved(@org.jetbrains.annotations.Nullable
    org.fourthline.cling.model.meta.RemoteDevice device) {
    }
    
    @java.lang.Override
    public void onLocalDeviceAdded(@org.jetbrains.annotations.Nullable
    org.fourthline.cling.model.meta.LocalDevice device) {
    }
    
    @java.lang.Override
    public void onLocalDeviceRemoved(@org.jetbrains.annotations.Nullable
    org.fourthline.cling.model.meta.LocalDevice device) {
    }
    
    private final void showLoader() {
    }
    
    private final void closeLoader() {
    }
    
    @java.lang.Override
    public void onDismiss(@org.jetbrains.annotations.NotNull
    android.content.DialogInterface dialog) {
    }
    
    @java.lang.Override
    public void onDestroyView() {
    }
}