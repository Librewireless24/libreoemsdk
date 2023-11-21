package com.cumulations.libreV2.activity;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017H\u0014J\u0018\u0010\u0018\u001a\u00020\u00152\u0006\u0010\u0019\u001a\u00020\u00042\u0006\u0010\t\u001a\u00020\u0004H\u0002R\u0014\u0010\u0003\u001a\u00020\u0004X\u0086D\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u001d\u0010\t\u001a\u0004\u0018\u00010\u00048BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000b\u0010\f\u001a\u0004\b\n\u0010\u0006R\u001d\u0010\r\u001a\u0004\u0018\u00010\u00048BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000f\u0010\f\u001a\u0004\b\u000e\u0010\u0006R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001a"}, d2 = {"Lcom/cumulations/libreV2/activity/IssuesReportActivity;", "Lcom/cumulations/libreV2/activity/CTDeviceDiscoveryActivity;", "()V", "TAG", "", "getTAG", "()Ljava/lang/String;", "binding", "Lcom/libreAlexa/databinding/ActivityIssuesReportBinding;", "currentDeviceIp", "getCurrentDeviceIp", "currentDeviceIp$delegate", "Lkotlin/Lazy;", "currentDeviceName", "getCurrentDeviceName", "currentDeviceName$delegate", "maxCharLimit", "", "selectedTime", "uuid", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "sendLuciCommand", "data", "libreoemsdk_debug"})
public final class IssuesReportActivity extends com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity {
    private com.libreAlexa.databinding.ActivityIssuesReportBinding binding;
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy currentDeviceIp$delegate = null;
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy currentDeviceName$delegate = null;
    private final int maxCharLimit = 250;
    @org.jetbrains.annotations.NotNull
    private java.lang.String selectedTime = "";
    @org.jetbrains.annotations.Nullable
    private java.lang.String uuid;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String TAG = "IssuesReportActivity";
    
    public IssuesReportActivity() {
        super();
    }
    
    private final java.lang.String getCurrentDeviceIp() {
        return null;
    }
    
    private final java.lang.String getCurrentDeviceName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getTAG() {
        return null;
    }
    
    @java.lang.Override
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    private final void sendLuciCommand(java.lang.String data, java.lang.String currentDeviceIp) {
    }
}