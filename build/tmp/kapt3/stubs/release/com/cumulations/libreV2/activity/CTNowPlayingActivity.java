package com.cumulations.libreV2.activity;

/**
 * Created by AMit on 13/5/2019.
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000\u00c0\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0013\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u0000 \u0089\u00012\u00020\u00012\u00020\u00022\u00020\u00032\u00020\u0004:\u0002\u0089\u0001B\u0005\u00a2\u0006\u0002\u0010\u0005J\u0010\u0010+\u001a\u00020\u00072\u0006\u0010,\u001a\u00020-H\u0002J\b\u0010.\u001a\u00020/H\u0016J\u0010\u00100\u001a\u00020/2\u0006\u00101\u001a\u00020\u0007H\u0016J\b\u00102\u001a\u00020/H\u0002J\"\u00103\u001a\u00020/2\u0006\u00104\u001a\u0002052\b\u00106\u001a\u0004\u0018\u00010\u00072\u0006\u00107\u001a\u000205H\u0002J\u0010\u00108\u001a\u00020/2\u0006\u00109\u001a\u00020\u001aH\u0002J\u0015\u0010:\u001a\u00020\u001a2\u0006\u0010;\u001a\u000205H\u0000\u00a2\u0006\u0002\b<J\u0015\u0010=\u001a\u00020\u001a2\u0006\u0010>\u001a\u000205H\u0000\u00a2\u0006\u0002\b?J\u0010\u0010@\u001a\u00020/2\u0006\u0010A\u001a\u00020\u001aH\u0002J\b\u0010B\u001a\u00020/H\u0002J\u0012\u0010C\u001a\u0004\u0018\u00010D2\b\u0010E\u001a\u0004\u0018\u00010\u0007J\u001a\u0010F\u001a\u00020/2\b\u0010G\u001a\u0004\u0018\u00010\u00072\u0006\u0010H\u001a\u000205H\u0002J\u0010\u0010I\u001a\u00020/2\u0006\u00109\u001a\u00020\u001aH\u0002J\b\u0010J\u001a\u00020/H\u0002J\b\u0010K\u001a\u00020/H\u0002J\b\u0010L\u001a\u00020/H\u0002J\u0012\u0010M\u001a\u00020\u001a2\b\u0010N\u001a\u0004\u0018\u00010\u0014H\u0002J\u0010\u0010O\u001a\u00020\u001a2\u0006\u0010N\u001a\u00020\u0014H\u0002J\u0010\u0010P\u001a\u00020/2\u0006\u0010Q\u001a\u00020\u0007H\u0002J\u0010\u0010R\u001a\u00020/2\u0006\u0010S\u001a\u00020TH\u0016J\u0010\u0010U\u001a\u00020/2\u0006\u0010V\u001a\u00020\rH\u0016J$\u0010W\u001a\u00020/2\b\u0010X\u001a\u0004\u0018\u00010\u00072\u0006\u0010Y\u001a\u00020Z2\b\u0010[\u001a\u0004\u0018\u00010\u0007H\u0016J\u0014\u0010\\\u001a\u00020/2\n\u0010]\u001a\u0006\u0012\u0002\b\u00030^H\u0016J\u0010\u0010_\u001a\u00020/2\u0006\u0010`\u001a\u00020aH\u0016J\u0012\u0010b\u001a\u00020/2\b\u0010c\u001a\u0004\u0018\u00010dH\u0014J$\u0010e\u001a\u00020/2\n\u0010X\u001a\u0006\u0012\u0002\b\u00030^2\u0006\u0010f\u001a\u00020\u00072\u0006\u0010[\u001a\u00020\u0007H\u0016J\u001a\u0010g\u001a\u00020\u001a2\u0006\u0010h\u001a\u0002052\b\u0010i\u001a\u0004\u0018\u00010jH\u0016J\b\u0010k\u001a\u00020/H\u0016J\b\u0010l\u001a\u00020/H\u0016J\b\u0010m\u001a\u00020/H\u0016J\u0012\u0010n\u001a\u00020/2\b\u0010\u001f\u001a\u0004\u0018\u00010 H\u0016J\b\u0010o\u001a\u00020/H\u0014J\b\u0010p\u001a\u00020/H\u0016J\b\u0010q\u001a\u00020/H\u0014J\b\u0010r\u001a\u00020/H\u0014J\b\u0010s\u001a\u00020/H\u0016J\u0018\u0010t\u001a\u00020/2\u0006\u0010u\u001a\u00020-2\u0006\u0010v\u001a\u00020-H\u0016J\u0010\u0010w\u001a\u00020/2\u0006\u0010x\u001a\u000205H\u0016J\b\u0010y\u001a\u00020\u001aH\u0002J\u0010\u0010z\u001a\u00020/2\u0006\u0010{\u001a\u00020\u001aH\u0002J\u001a\u0010|\u001a\u00020/2\b\u0010}\u001a\u0004\u0018\u00010~2\u0006\u0010Q\u001a\u00020\u0007H\u0002J\u0012\u0010\u007f\u001a\u00020/2\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014H\u0002J\u0011\u0010\u0080\u0001\u001a\u00020/2\u0006\u00107\u001a\u000205H\u0002J\t\u0010\u0081\u0001\u001a\u00020/H\u0002J\u0012\u0010\u0082\u0001\u001a\u00020/2\u0007\u0010\u0083\u0001\u001a\u00020\u001aH\u0002J\u0013\u0010\u0084\u0001\u001a\u00020/2\b\u0010\u0085\u0001\u001a\u00030\u0086\u0001H\u0016J\t\u0010\u0087\u0001\u001a\u00020/H\u0002J\u0013\u0010\u0088\u0001\u001a\u00020/2\b\u0010N\u001a\u0004\u0018\u00010\u0014H\u0002R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082D\u00a2\u0006\u0002\n\u0000R\u0016\u0010\b\u001a\n \t*\u0004\u0018\u00010\u00070\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u000e\u001a\u0004\u0018\u00010\u00078BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0011\u0010\u0012\u001a\u0004\b\u000f\u0010\u0010R\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u001aX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\u001aX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\u001aX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001d\u001a\u0004\u0018\u00010\u001eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001f\u001a\u0004\u0018\u00010 X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010!\u001a\u00020\"X\u0080\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b#\u0010$\"\u0004\b%\u0010&R\u000e\u0010\'\u001a\u00020(X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010)\u001a\u00020\"X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010*\u001a\u00020\"8\u0002X\u0083\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u008a\u0001"}, d2 = {"Lcom/cumulations/libreV2/activity/CTNowPlayingActivity;", "Lcom/cumulations/libreV2/activity/CTDeviceDiscoveryActivity;", "Landroid/view/View$OnClickListener;", "Lcom/libreAlexa/netty/LibreDeviceInteractionListner;", "Lcom/libreAlexa/app/dlna/dmc/processor/interfaces/DMRProcessor$DMRProcessorListener;", "()V", "META_DATA", "", "TAG", "kotlin.jvm.PlatformType", "binding", "Lcom/libreAlexa/databinding/CtAcitvityNowPlayingBinding;", "currentDeviceNode", "Lcom/libreAlexa/luci/LSSDPNodes;", "currentIpAddress", "getCurrentIpAddress", "()Ljava/lang/String;", "currentIpAddress$delegate", "Lkotlin/Lazy;", "currentSceneObject", "Lcom/cumulations/libreV2/model/SceneObject;", "currentTrackName", "durationInSeeconds", "", "durationInSeeconds1", "is49MsgBoxReceived", "", "isLocalDMRPlayback", "isStillPlaying", "mScanHandler", "Lcom/libreAlexa/Scanning/ScanningHandler;", "remoteDevice", "Lorg/fourthline/cling/model/meta/RemoteDevice;", "showLoaderHandler", "Landroid/os/Handler;", "getShowLoaderHandler$libreoemsdk_release", "()Landroid/os/Handler;", "setShowLoaderHandler$libreoemsdk_release", "(Landroid/os/Handler;)V", "startPlaybackTimerRunnable", "Ljava/lang/Runnable;", "startPlaybackTimerhandler", "timeOutHandler", "convertMillisToSongTime", "time", "", "deviceDiscoveryAfterClearingTheCacheStarted", "", "deviceGotRemoved", "mIpAddress", "disablePlayback", "disableViews", "currentSrc", "", "message", "i", "doNextPrevious", "isNextPressed", "doSeek", "pos", "doSeek$libreoemsdk_release", "doVolumeChange", "currentVolumePosition", "doVolumeChange$libreoemsdk_release", "enableViews", "enable", "exitOnBackPressed", "getTheRenderer", "Lcom/libreAlexa/app/dlna/dmc/processor/interfaces/DMRProcessor;", "ipAddress", "gotoSourcesOption", "ipaddress", "currentSource", "handleNextPrevForMB", "handleThePlayIconsForGrayoutOption", "hideShuffleRepeat", "initViews", "isActivePlayListNotAvailable", "sceneObject", "isLocalDMR", "launchTheApp", "appPackageName", "messageRecieved", "nettyData", "Lcom/libreAlexa/netty/NettyData;", "newDeviceFound", "node", "onActionFail", "actionCallback", "response", "Lorg/fourthline/cling/model/message/UpnpResponse;", "cause", "onActionSuccess", "action", "Lorg/fourthline/cling/model/meta/Action;", "onClick", "view", "Landroid/view/View;", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onExceptionHappend", "mTitle", "onKeyDown", "keyCode", "event", "Landroid/view/KeyEvent;", "onPaused", "onPlayCompleted", "onPlaying", "onRemoteDeviceAdded", "onResume", "onSetURI", "onStart", "onStop", "onStoped", "onUpdatePosition", "position", "duration", "onUpdateVolume", "currentVolume", "playPauseNextPrevAllowed", "preparingToPlay", "value", "redirectingToPlayStore", "intent", "Landroid/content/Intent;", "setSourceIconsForAlexaSource", "setTheSourceIconFromCurrentSceneObject", "setViews", "showLoader", "show", "tunnelDataReceived", "tunnelingData", "Lcom/cumulations/libreV2/tcp_tunneling/TunnelingData;", "updateAlbumArt", "updatePlayPauseNextPrevForCurrentSource", "Companion", "libreoemsdk_release"})
public final class CTNowPlayingActivity extends com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity implements android.view.View.OnClickListener, com.libreAlexa.netty.LibreDeviceInteractionListner, com.libreAlexa.app.dlna.dmc.processor.interfaces.DMRProcessor.DMRProcessorListener {
    public static final int REPEAT_OFF = 0;
    public static final int REPEAT_ONE = 1;
    public static final int REPEAT_ALL = 2;
    public static final int PLAYBACK_TIMER_TIMEOUT = 5000;
    @org.jetbrains.annotations.Nullable
    private com.cumulations.libreV2.model.SceneObject currentSceneObject;
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy currentIpAddress$delegate = null;
    private boolean isLocalDMRPlayback = false;
    @org.jetbrains.annotations.Nullable
    private com.libreAlexa.luci.LSSDPNodes currentDeviceNode;
    @org.jetbrains.annotations.NotNull
    private java.lang.String currentTrackName = "-1";
    private boolean isStillPlaying = false;
    private boolean is49MsgBoxReceived = false;
    private float durationInSeeconds = 0.0F;
    private float durationInSeeconds1 = 0.0F;
    @org.jetbrains.annotations.Nullable
    private com.libreAlexa.Scanning.ScanningHandler mScanHandler;
    @org.jetbrains.annotations.Nullable
    private org.fourthline.cling.model.meta.RemoteDevice remoteDevice;
    private com.libreAlexa.databinding.CtAcitvityNowPlayingBinding binding;
    @org.jetbrains.annotations.NotNull
    private final android.os.Handler startPlaybackTimerhandler = null;
    private final java.lang.String TAG = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String META_DATA = "-->SHAIKCT:";
    @org.jetbrains.annotations.NotNull
    private final java.lang.Runnable startPlaybackTimerRunnable = null;
    @org.jetbrains.annotations.NotNull
    private android.os.Handler showLoaderHandler;
    @android.annotation.SuppressLint(value = {"HandlerLeak"})
    @org.jetbrains.annotations.NotNull
    private final android.os.Handler timeOutHandler = null;
    @org.jetbrains.annotations.NotNull
    public static final com.cumulations.libreV2.activity.CTNowPlayingActivity.Companion Companion = null;
    
    public CTNowPlayingActivity() {
        super();
    }
    
    private final java.lang.String getCurrentIpAddress() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final android.os.Handler getShowLoaderHandler$libreoemsdk_release() {
        return null;
    }
    
    public final void setShowLoaderHandler$libreoemsdk_release(@org.jetbrains.annotations.NotNull
    android.os.Handler p0) {
    }
    
    private final void disablePlayback() {
    }
    
    @java.lang.Override
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override
    protected void onStart() {
    }
    
    private final void exitOnBackPressed() {
    }
    
    private final void gotoSourcesOption(java.lang.String ipaddress, int currentSource) {
    }
    
    private final boolean isLocalDMR(com.cumulations.libreV2.model.SceneObject sceneObject) {
        return false;
    }
    
    private final boolean isActivePlayListNotAvailable(com.cumulations.libreV2.model.SceneObject sceneObject) {
        return false;
    }
    
    private final void initViews() {
    }
    
    private final void showLoader(boolean show) {
    }
    
    private final void preparingToPlay(boolean value) {
    }
    
    private final void disableViews(int currentSrc, java.lang.String message, int i) {
    }
    
    private final void enableViews(boolean enable) {
    }
    
    private final boolean playPauseNextPrevAllowed() {
        return false;
    }
    
    @java.lang.Override
    public void onExceptionHappend(@org.jetbrains.annotations.NotNull
    org.fourthline.cling.model.meta.Action<?> actionCallback, @org.jetbrains.annotations.NotNull
    java.lang.String mTitle, @org.jetbrains.annotations.NotNull
    java.lang.String cause) {
    }
    
    @java.lang.Override
    public void onClick(@org.jetbrains.annotations.NotNull
    android.view.View view) {
    }
    
    @java.lang.Override
    public void onRemoteDeviceAdded(@org.jetbrains.annotations.Nullable
    org.fourthline.cling.model.meta.RemoteDevice remoteDevice) {
    }
    
    @java.lang.Override
    protected void onResume() {
    }
    
    @java.lang.Override
    protected void onStop() {
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
    java.lang.String mIpAddress) {
    }
    
    @java.lang.Override
    public void messageRecieved(@org.jetbrains.annotations.NotNull
    com.libreAlexa.netty.NettyData nettyData) {
    }
    
    private final void setViews() {
    }
    
    private final void hideShuffleRepeat() {
    }
    
    private final void setSourceIconsForAlexaSource(com.cumulations.libreV2.model.SceneObject currentSceneObject) {
    }
    
    private final void updateAlbumArt() {
    }
    
    private final void setTheSourceIconFromCurrentSceneObject(int i) {
    }
    
    private final void handleThePlayIconsForGrayoutOption() {
    }
    
    private final void updatePlayPauseNextPrevForCurrentSource(com.cumulations.libreV2.model.SceneObject sceneObject) {
    }
    
    private final java.lang.String convertMillisToSongTime(long time) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final com.libreAlexa.app.dlna.dmc.processor.interfaces.DMRProcessor getTheRenderer(@org.jetbrains.annotations.Nullable
    java.lang.String ipAddress) {
        return null;
    }
    
    public final boolean doSeek$libreoemsdk_release(int pos) {
        return false;
    }
    
    private final void doNextPrevious(boolean isNextPressed) {
    }
    
    private final void handleNextPrevForMB(boolean isNextPressed) {
    }
    
    public final boolean doVolumeChange$libreoemsdk_release(int currentVolumePosition) {
        return false;
    }
    
    @java.lang.Override
    public void onUpdatePosition(long position, long duration) {
    }
    
    @java.lang.Override
    public void onUpdateVolume(int currentVolume) {
    }
    
    @java.lang.Override
    public void onPaused() {
    }
    
    @java.lang.Override
    public void onStoped() {
    }
    
    @java.lang.Override
    public void onSetURI() {
    }
    
    @java.lang.Override
    public void onPlayCompleted() {
    }
    
    @java.lang.Override
    public void onPlaying() {
    }
    
    @java.lang.Override
    public void onActionSuccess(@org.jetbrains.annotations.NotNull
    org.fourthline.cling.model.meta.Action<?> action) {
    }
    
    @java.lang.Override
    public void onActionFail(@org.jetbrains.annotations.Nullable
    java.lang.String actionCallback, @org.jetbrains.annotations.NotNull
    org.fourthline.cling.model.message.UpnpResponse response, @org.jetbrains.annotations.Nullable
    java.lang.String cause) {
    }
    
    @java.lang.Override
    public void tunnelDataReceived(@org.jetbrains.annotations.NotNull
    com.cumulations.libreV2.tcp_tunneling.TunnelingData tunnelingData) {
    }
    
    /**
     * Handling volume changes from phone volume hardware buttons
     * Called only when button is pressed, not when released
     */
    @java.lang.Override
    public boolean onKeyDown(int keyCode, @org.jetbrains.annotations.Nullable
    android.view.KeyEvent event) {
        return false;
    }
    
    private final void launchTheApp(java.lang.String appPackageName) {
    }
    
    private final void redirectingToPlayStore(android.content.Intent intent, java.lang.String appPackageName) {
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/cumulations/libreV2/activity/CTNowPlayingActivity$Companion;", "", "()V", "PLAYBACK_TIMER_TIMEOUT", "", "REPEAT_ALL", "REPEAT_OFF", "REPEAT_ONE", "libreoemsdk_release"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}