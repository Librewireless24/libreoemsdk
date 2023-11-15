package com.cumulations.libreV2.activity;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000\u0080\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\n\n\u0002\u0010$\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u000f\u0018\u0000 M2\u00020\u00012\u00020\u0002:\u0001MB\u0005\u00a2\u0006\u0002\u0010\u0003J\u0012\u0010-\u001a\u00020.2\b\u0010/\u001a\u0004\u0018\u00010\u0017H\u0002J\u0012\u00100\u001a\u00020.2\b\u00101\u001a\u0004\u0018\u00010\u000bH\u0002J\b\u00102\u001a\u00020.H\u0002J\u000e\u00103\u001a\u00020.2\u0006\u00104\u001a\u00020\u0005J\b\u00105\u001a\u00020.H\u0016J,\u00106\u001a\u00020.2\b\u00107\u001a\u0004\u0018\u00010\u000b2\u0018\u00108\u001a\u0014\u0012\u0004\u0012\u00020\u000b\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00170:09H\u0016J\u0010\u0010;\u001a\u00020.2\u0006\u0010<\u001a\u00020\u000bH\u0016J\u0012\u0010=\u001a\u00020.2\b\u0010>\u001a\u0004\u0018\u00010?H\u0015J\u0012\u0010@\u001a\u00020.2\b\u0010)\u001a\u0004\u0018\u00010*H\u0016J\u0010\u0010A\u001a\u00020.2\u0006\u0010B\u001a\u00020*H\u0016J\b\u0010C\u001a\u00020.H\u0014J\b\u0010D\u001a\u00020.H\u0016J\b\u0010E\u001a\u00020.H\u0014J\u0010\u0010F\u001a\u00020.2\u0006\u0010G\u001a\u00020\tH\u0002J\u000e\u0010H\u001a\u00020.2\u0006\u0010/\u001a\u00020\u0017J\b\u0010I\u001a\u00020.H\u0002J\b\u0010J\u001a\u00020.H\u0002J\u0010\u0010K\u001a\u00020.2\u0006\u0010L\u001a\u00020\u000bH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001d\u0010\n\u001a\u0004\u0018\u00010\u000b8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000e\u0010\u000f\u001a\u0004\b\f\u0010\rR\u001d\u0010\u0010\u001a\u0004\u0018\u00010\u000b8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0012\u0010\u000f\u001a\u0004\b\u0011\u0010\rR\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0015\u001a\n\u0012\u0004\u0012\u00020\u0017\u0018\u00010\u0016X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0018\u001a\n\u0012\u0004\u0012\u00020\u0017\u0018\u00010\u0019X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u001bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001c\u001a\u0004\u0018\u00010\u001dX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u001e\u001a\u00020\u001fX\u0080\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b \u0010!\"\u0004\b\"\u0010#R\u000e\u0010$\u001a\u00020%X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010&\u001a\u0004\u0018\u00010\u001fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\'\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010(\u001a\u00020\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010)\u001a\u0004\u0018\u00010*X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010+\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010,\u001a\u0004\u0018\u00010\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006N"}, d2 = {"Lcom/cumulations/libreV2/activity/CTUpnpFileBrowserActivity;", "Lcom/cumulations/libreV2/activity/CTDeviceDiscoveryActivity;", "Lcom/libreAlexa/app/dlna/dmc/processor/interfaces/DMSProcessor$DMSProcessorListener;", "()V", "adapterClickedPosition", "", "binding", "Lcom/libreAlexa/databinding/CtActivityFileBrowserBinding;", "browsingCancelled", "", "clickedDIDLId", "", "getClickedDIDLId", "()Ljava/lang/String;", "clickedDIDLId$delegate", "Lkotlin/Lazy;", "currentIpAddress", "getCurrentIpAddress", "currentIpAddress$delegate", "didlObjectArrayAdapter", "Lcom/cumulations/libreV2/adapter/CTDIDLObjectListAdapter;", "didlObjectList", "Ljava/util/ArrayList;", "Lorg/fourthline/cling/support/model/DIDLObject;", "didlObjectStack", "Ljava/util/Stack;", "dmsBrowseHelper", "Lcom/libreAlexa/app/dlna/dmc/utility/DMSBrowseHelper;", "dmsProcessor", "Lcom/libreAlexa/app/dlna/dmc/processor/interfaces/DMSProcessor;", "handler", "Landroid/os/Handler;", "getHandler$libreoemsdk_debug", "()Landroid/os/Handler;", "setHandler$libreoemsdk_debug", "(Landroid/os/Handler;)V", "mMyTaskRunnable", "Ljava/lang/Runnable;", "mTaskHandler", "needSetListViewScroll", "position", "remoteDevice", "Lorg/fourthline/cling/model/meta/RemoteDevice;", "remoteDeviceUDN", "selectedDIDLObject", "browse", "", "didlObject", "browseByDIDLId", "didlObjectId", "createOrUpdatePlaybackHelperAndBrowser", "handleDIDLObjectClick", "clickedDIDLPosition", "onBackPressed", "onBrowseComplete", "parentObjectId", "result", "", "", "onBrowseFail", "message", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onRemoteDeviceAdded", "onRemoteDeviceRemoved", "device", "onResume", "onStartComplete", "onStop", "openNowPlaying", "setDMR", "play", "setListeners", "setViews", "updateTitle", "title", "Companion", "libreoemsdk_debug"})
public final class CTUpnpFileBrowserActivity extends com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity implements com.libreAlexa.app.dlna.dmc.processor.interfaces.DMSProcessor.DMSProcessorListener {
    private static final java.lang.String TAG = null;
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy currentIpAddress$delegate = null;
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy clickedDIDLId$delegate = null;
    @org.jetbrains.annotations.Nullable
    private com.libreAlexa.app.dlna.dmc.processor.interfaces.DMSProcessor dmsProcessor;
    @org.jetbrains.annotations.Nullable
    private java.util.Stack<org.fourthline.cling.support.model.DIDLObject> didlObjectStack;
    @org.jetbrains.annotations.Nullable
    private com.libreAlexa.app.dlna.dmc.utility.DMSBrowseHelper dmsBrowseHelper;
    private int position = 0;
    private boolean browsingCancelled = false;
    private boolean needSetListViewScroll = false;
    @org.jetbrains.annotations.Nullable
    private org.fourthline.cling.support.model.DIDLObject selectedDIDLObject;
    @org.jetbrains.annotations.Nullable
    private java.util.ArrayList<org.fourthline.cling.support.model.DIDLObject> didlObjectList;
    @org.jetbrains.annotations.Nullable
    private com.cumulations.libreV2.adapter.CTDIDLObjectListAdapter didlObjectArrayAdapter;
    @org.jetbrains.annotations.Nullable
    private java.lang.String remoteDeviceUDN;
    @org.jetbrains.annotations.Nullable
    private org.fourthline.cling.model.meta.RemoteDevice remoteDevice;
    private com.libreAlexa.databinding.CtActivityFileBrowserBinding binding;
    @org.jetbrains.annotations.NotNull
    private android.os.Handler handler;
    @org.jetbrains.annotations.Nullable
    private android.os.Handler mTaskHandler;
    @org.jetbrains.annotations.NotNull
    private java.lang.Runnable mMyTaskRunnable;
    private int adapterClickedPosition = -1;
    @org.jetbrains.annotations.NotNull
    public static final com.cumulations.libreV2.activity.CTUpnpFileBrowserActivity.Companion Companion = null;
    
    public CTUpnpFileBrowserActivity() {
        super();
    }
    
    private final java.lang.String getCurrentIpAddress() {
        return null;
    }
    
    private final java.lang.String getClickedDIDLId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final android.os.Handler getHandler$libreoemsdk_debug() {
        return null;
    }
    
    public final void setHandler$libreoemsdk_debug(@org.jetbrains.annotations.NotNull
    android.os.Handler p0) {
    }
    
    @java.lang.Override
    @android.annotation.SuppressLint(value = {"ClickableViewAccessibility"})
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override
    public void onStartComplete() {
    }
    
    private final void setViews() {
    }
    
    private final void setListeners() {
    }
    
    private final void updateTitle(java.lang.String title) {
    }
    
    @java.lang.Override
    protected void onResume() {
    }
    
    private final void browseByDIDLId(java.lang.String didlObjectId) {
    }
    
    private final void browse(org.fourthline.cling.support.model.DIDLObject didlObject) {
    }
    
    public final void play(@org.jetbrains.annotations.NotNull
    org.fourthline.cling.support.model.DIDLObject didlObject) {
    }
    
    private final void createOrUpdatePlaybackHelperAndBrowser() {
    }
    
    @java.lang.Override
    public void onBrowseComplete(@org.jetbrains.annotations.Nullable
    java.lang.String parentObjectId, @org.jetbrains.annotations.NotNull
    java.util.Map<java.lang.String, ? extends java.util.List<? extends org.fourthline.cling.support.model.DIDLObject>> result) {
    }
    
    @java.lang.Override
    public void onBrowseFail(@org.jetbrains.annotations.NotNull
    java.lang.String message) {
    }
    
    @java.lang.Override
    public void onBackPressed() {
    }
    
    @java.lang.Override
    public void onRemoteDeviceAdded(@org.jetbrains.annotations.Nullable
    org.fourthline.cling.model.meta.RemoteDevice remoteDevice) {
    }
    
    @java.lang.Override
    protected void onStop() {
    }
    
    @java.lang.Override
    public void onRemoteDeviceRemoved(@org.jetbrains.annotations.NotNull
    org.fourthline.cling.model.meta.RemoteDevice device) {
    }
    
    public final void handleDIDLObjectClick(int clickedDIDLPosition) {
    }
    
    private final void openNowPlaying(boolean setDMR) {
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0016\u0010\u0003\u001a\n \u0005*\u0004\u0018\u00010\u00040\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lcom/cumulations/libreV2/activity/CTUpnpFileBrowserActivity$Companion;", "", "()V", "TAG", "", "kotlin.jvm.PlatformType", "libreoemsdk_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}