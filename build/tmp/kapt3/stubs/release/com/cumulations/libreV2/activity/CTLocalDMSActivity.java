package com.cumulations.libreV2.activity;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001:\u0001\u001aB\u0005\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013H\u0014J\b\u0010\u0014\u001a\u00020\u0011H\u0014J\u0010\u0010\u0015\u001a\u00020\u00112\u0006\u0010\u0016\u001a\u00020\u0017H\u0016J\b\u0010\u0018\u001a\u00020\u0011H\u0016J\b\u0010\u0019\u001a\u00020\u0011H\u0002R\u0016\u0010\u0003\u001a\n \u0005*\u0004\u0018\u00010\u00040\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0018\u00010\u0007R\u00020\u0000X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001e\u0010\n\u001a\u00020\u000b8\u0000@\u0000X\u0081\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000f\u00a8\u0006\u001b"}, d2 = {"Lcom/cumulations/libreV2/activity/CTLocalDMSActivity;", "Lcom/cumulations/libreV2/activity/CTDeviceDiscoveryActivity;", "()V", "TAG", "", "kotlin.jvm.PlatformType", "mServiceHandler", "Lcom/cumulations/libreV2/activity/CTLocalDMSActivity$ServiceHandler;", "mThread", "Landroid/os/HandlerThread;", "mediaHandler", "Landroid/os/Handler;", "getMediaHandler$oemsdk_release", "()Landroid/os/Handler;", "setMediaHandler$oemsdk_release", "(Landroid/os/Handler;)V", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "onLocalDeviceAdded", "device", "Lorg/fourthline/cling/model/meta/LocalDevice;", "onStartComplete", "openDMSBrowser", "ServiceHandler", "oemsdk_release"})
public final class CTLocalDMSActivity extends com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity {
    @org.jetbrains.annotations.Nullable
    private android.os.HandlerThread mThread;
    @org.jetbrains.annotations.Nullable
    private com.cumulations.libreV2.activity.CTLocalDMSActivity.ServiceHandler mServiceHandler;
    private final java.lang.String TAG = null;
    @android.annotation.SuppressLint(value = {"HandlerLeak"})
    @org.jetbrains.annotations.NotNull
    private android.os.Handler mediaHandler;
    
    public CTLocalDMSActivity() {
        super();
    }
    
    @java.lang.Override
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override
    public void onLocalDeviceAdded(@org.jetbrains.annotations.NotNull
    org.fourthline.cling.model.meta.LocalDevice device) {
    }
    
    @java.lang.Override
    public void onStartComplete() {
    }
    
    @org.jetbrains.annotations.NotNull
    public final android.os.Handler getMediaHandler$oemsdk_release() {
        return null;
    }
    
    public final void setMediaHandler$oemsdk_release(@org.jetbrains.annotations.NotNull
    android.os.Handler p0) {
    }
    
    private final void openDMSBrowser() {
    }
    
    @java.lang.Override
    protected void onDestroy() {
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0080\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016\u00a8\u0006\t"}, d2 = {"Lcom/cumulations/libreV2/activity/CTLocalDMSActivity$ServiceHandler;", "Landroid/os/Handler;", "looper", "Landroid/os/Looper;", "(Lcom/cumulations/libreV2/activity/CTLocalDMSActivity;Landroid/os/Looper;)V", "handleMessage", "", "msg", "Landroid/os/Message;", "oemsdk_release"})
    public final class ServiceHandler extends android.os.Handler {
        
        public ServiceHandler(@org.jetbrains.annotations.NotNull
        android.os.Looper looper) {
            super();
        }
        
        @java.lang.Override
        public void handleMessage(@org.jetbrains.annotations.NotNull
        android.os.Message msg) {
        }
    }
}