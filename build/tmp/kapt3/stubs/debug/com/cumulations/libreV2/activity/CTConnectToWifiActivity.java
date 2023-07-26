package com.cumulations.libreV2.activity;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000d\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u0000 /2\u00020\u00012\u00020\u0002:\u0001/B\u0005\u00a2\u0006\u0002\u0010\u0003J\"\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u00162\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u0018H\u0014J$\u0010\u001a\u001a\u00020\u00142\u0006\u0010\u001b\u001a\u00020\u001c2\u0012\u0010\u001d\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u001eH\u0002J\b\u0010\u001f\u001a\u00020\rH\u0002J\u0012\u0010 \u001a\u0004\u0018\u00010\u00052\u0006\u0010!\u001a\u00020\u0005H\u0002J\u0010\u0010\"\u001a\u00020\u00142\u0006\u0010#\u001a\u00020\u0005H\u0002J\b\u0010$\u001a\u00020\u0014H\u0002J\u0012\u0010%\u001a\u00020\u00142\b\u0010&\u001a\u0004\u0018\u00010\'H\u0016J\u0012\u0010(\u001a\u00020\u00142\b\u0010)\u001a\u0004\u0018\u00010*H\u0014J\b\u0010+\u001a\u00020\u0014H\u0014J\b\u0010,\u001a\u00020\u0014H\u0002J\u0010\u0010-\u001a\u00020\u00142\u0006\u0010.\u001a\u00020\u0005H\u0002R\u001d\u0010\u0004\u001a\u0004\u0018\u00010\u00058BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007R\u000e\u0010\n\u001a\u00020\u000bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0010\u001a\n \u0012*\u0004\u0018\u00010\u00110\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u00060"}, d2 = {"Lcom/cumulations/libreV2/activity/CTConnectToWifiActivity;", "Lcom/cumulations/libreV2/activity/CTDeviceDiscoveryActivity;", "Landroid/view/View$OnClickListener;", "()V", "activityName", "", "getActivityName", "()Ljava/lang/String;", "activityName$delegate", "Lkotlin/Lazy;", "binding", "Lcom/libreAlexa/databinding/CtActivityConnectToWifiBinding;", "mDeviceNameChanged", "", "mHandler", "Landroid/os/Handler;", "wifiConnect", "Lcom/cumulations/libreV2/model/WifiConnection;", "kotlin.jvm.PlatformType", "customOnActivityResult", "", "data", "Landroid/content/Intent;", "requestCode", "", "resultCode", "doSacConfiguration", "deviceNameService", "Lcom/libreAlexa/serviceinterface/LSDeviceClient$DeviceNameService;", "params", "Ljava/util/LinkedHashMap;", "fieldsValid", "getSSIDPasswordFromSharedPreference", "deviceSSID", "goToSpeakerSetupScreen", "message", "initViews", "onClick", "p0", "Landroid/view/View;", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "setListeners", "writeSacConfig", "etDeviceName", "Companion", "oemsdk_debug"})
public final class CTConnectToWifiActivity extends com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity implements android.view.View.OnClickListener {
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy activityName$delegate = null;
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String TAG = "CTConnectToWifiActivity";
    private com.libreAlexa.databinding.CtActivityConnectToWifiBinding binding;
    private boolean mDeviceNameChanged = false;
    @org.jetbrains.annotations.NotNull
    private final android.os.Handler mHandler = null;
    private com.cumulations.libreV2.model.WifiConnection wifiConnect;
    @org.jetbrains.annotations.NotNull
    public static final com.cumulations.libreV2.activity.CTConnectToWifiActivity.Companion Companion = null;
    
    public CTConnectToWifiActivity() {
        super();
    }
    
    private final java.lang.String getActivityName() {
        return null;
    }
    
    @java.lang.Override
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setListeners() {
    }
    
    private final void initViews() {
    }
    
    @java.lang.Override
    public void onClick(@org.jetbrains.annotations.Nullable
    android.view.View p0) {
    }
    
    private final boolean fieldsValid() {
        return false;
    }
    
    @java.lang.Override
    protected void customOnActivityResult(@org.jetbrains.annotations.Nullable
    android.content.Intent data, int requestCode, int resultCode) {
    }
    
    private final void writeSacConfig(java.lang.String etDeviceName) {
    }
    
    private final void doSacConfiguration(com.libreAlexa.serviceinterface.LSDeviceClient.DeviceNameService deviceNameService, java.util.LinkedHashMap<java.lang.String, java.lang.String> params) {
    }
    
    private final void goToSpeakerSetupScreen(java.lang.String message) {
    }
    
    private final java.lang.String getSSIDPasswordFromSharedPreference(java.lang.String deviceSSID) {
        return null;
    }
    
    @java.lang.Override
    protected void onDestroy() {
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/cumulations/libreV2/activity/CTConnectToWifiActivity$Companion;", "", "()V", "TAG", "", "oemsdk_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}