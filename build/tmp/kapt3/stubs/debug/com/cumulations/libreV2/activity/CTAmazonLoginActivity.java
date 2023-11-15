package com.cumulations.libreV2.activity;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000\u0084\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000b\u0018\u0000 L2\u00020\u00012\u00020\u00022\u00020\u0003:\u0002KLB\u0005\u00a2\u0006\u0002\u0010\u0004J\b\u0010,\u001a\u00020-H\u0002J\b\u0010.\u001a\u00020-H\u0016J\u0010\u0010/\u001a\u00020-2\u0006\u00100\u001a\u00020\u0006H\u0016J\b\u00101\u001a\u00020-H\u0002J\b\u00102\u001a\u00020-H\u0002J\b\u00103\u001a\u00020-H\u0002J\u0006\u0010\"\u001a\u00020\u0011J\u0010\u00104\u001a\u00020-2\u0006\u00105\u001a\u000206H\u0016J\u0010\u00107\u001a\u00020-2\u0006\u00108\u001a\u00020+H\u0016J\b\u00109\u001a\u00020-H\u0016J\u0010\u0010:\u001a\u00020-2\u0006\u0010;\u001a\u00020<H\u0016J\u0012\u0010=\u001a\u00020-2\b\u0010>\u001a\u0004\u0018\u00010?H\u0014J\u0012\u0010@\u001a\u00020-2\b\u0010A\u001a\u0004\u0018\u00010BH\u0014J\b\u0010C\u001a\u00020-H\u0014J\b\u0010D\u001a\u00020-H\u0014J\b\u0010E\u001a\u00020-H\u0002J\u000e\u0010F\u001a\u00020-2\u0006\u0010G\u001a\u00020\u0011J\u0012\u0010H\u001a\u00020-2\b\u0010I\u001a\u0004\u0018\u00010\u0006H\u0002J\b\u0010J\u001a\u00020-H\u0002R\u0016\u0010\u0005\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010\n\u001a\u00020\u000bX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000fR\u001e\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0086\u000e\u00a2\u0006\u0010\n\u0002\u0010\u0016\u001a\u0004\b\u0012\u0010\u0013\"\u0004\b\u0014\u0010\u0015R\u000e\u0010\u0017\u001a\u00020\u0018X\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0019\u001a\u0004\u0018\u00010\u001aX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u001b\u001a\u0004\u0018\u00010\u00068BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001e\u0010\u001f\u001a\u0004\b\u001c\u0010\u001dR\u0010\u0010 \u001a\u00020!8\u0002X\u0083\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010#\u001a\u0004\u0018\u00010$X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010%\u001a\u00020&X\u0082.\u00a2\u0006\u0002\n\u0000R\u001d\u0010\'\u001a\u0004\u0018\u00010\u00068BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b)\u0010\u001f\u001a\u0004\b(\u0010\u001dR\u0010\u0010*\u001a\u0004\u0018\u00010+X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006M"}, d2 = {"Lcom/cumulations/libreV2/activity/CTAmazonLoginActivity;", "Lcom/cumulations/libreV2/activity/CTDeviceDiscoveryActivity;", "Landroid/view/View$OnClickListener;", "Lcom/libreAlexa/netty/LibreDeviceInteractionListner;", "()V", "TAG", "", "kotlin.jvm.PlatformType", "alertDialog", "Landroidx/appcompat/app/AlertDialog;", "alexaLogginginCount", "", "getAlexaLogginginCount", "()I", "setAlexaLogginginCount", "(I)V", "alexaLoginStatus", "", "getAlexaLoginStatus", "()Ljava/lang/Boolean;", "setAlexaLoginStatus", "(Ljava/lang/Boolean;)V", "Ljava/lang/Boolean;", "binding", "Lcom/libreAlexa/databinding/CtActivityAmazonLoginBinding;", "deviceProvisioningInfo", "Lcom/libreAlexa/alexa/DeviceProvisioningInfo;", "from", "getFrom", "()Ljava/lang/String;", "from$delegate", "Lkotlin/Lazy;", "handler", "Landroid/os/Handler;", "isMetaDateRequestSent", "mAuthManager", "Lcom/amazon/identity/auth/device/authorization/api/AmazonAuthorizationManager;", "requestContext", "Lcom/amazon/identity/auth/device/api/workflow/RequestContext;", "speakerIpAddress", "getSpeakerIpAddress", "speakerIpAddress$delegate", "speakerNode", "Lcom/libreAlexa/luci/LSSDPNodes;", "closeLoader", "", "deviceDiscoveryAfterClearingTheCacheStarted", "deviceGotRemoved", "ipaddress", "disableViews", "handleBackPressed", "intentToThingToTryActivity", "messageRecieved", "packet", "Lcom/libreAlexa/netty/NettyData;", "newDeviceFound", "node", "onBackPressed", "onClick", "view", "Landroid/view/View;", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onNewIntent", "intent", "Landroid/content/Intent;", "onResume", "onStop", "setAlexaViews", "setMetaDateRequestSent", "metaDateRequestSent", "showAlertDialog", "error", "showLoader", "AuthListener", "Companion", "libreoemsdk_debug"})
public final class CTAmazonLoginActivity extends com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity implements android.view.View.OnClickListener, com.libreAlexa.netty.LibreDeviceInteractionListner {
    public static final int ALEXA_META_DATA_TIMER = 18;
    public static final int ACCESS_TOKEN_TIMEOUT = 301;
    @org.jetbrains.annotations.Nullable
    private com.amazon.identity.auth.device.authorization.api.AmazonAuthorizationManager mAuthManager;
    private com.amazon.identity.auth.device.api.workflow.RequestContext requestContext;
    @org.jetbrains.annotations.Nullable
    private com.libreAlexa.alexa.DeviceProvisioningInfo deviceProvisioningInfo;
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy speakerIpAddress$delegate = null;
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy from$delegate = null;
    @org.jetbrains.annotations.Nullable
    private androidx.appcompat.app.AlertDialog alertDialog;
    @org.jetbrains.annotations.Nullable
    private com.libreAlexa.luci.LSSDPNodes speakerNode;
    private boolean isMetaDateRequestSent = false;
    @org.jetbrains.annotations.Nullable
    private java.lang.Boolean alexaLoginStatus = false;
    private int alexaLogginginCount = 0;
    private com.libreAlexa.databinding.CtActivityAmazonLoginBinding binding;
    private java.lang.String TAG;
    @android.annotation.SuppressLint(value = {"HandlerLeak"})
    @org.jetbrains.annotations.NotNull
    private final android.os.Handler handler = null;
    @org.jetbrains.annotations.NotNull
    public static final com.cumulations.libreV2.activity.CTAmazonLoginActivity.Companion Companion = null;
    
    public CTAmazonLoginActivity() {
        super();
    }
    
    private final java.lang.String getSpeakerIpAddress() {
        return null;
    }
    
    private final java.lang.String getFrom() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Boolean getAlexaLoginStatus() {
        return null;
    }
    
    public final void setAlexaLoginStatus(@org.jetbrains.annotations.Nullable
    java.lang.Boolean p0) {
    }
    
    public final int getAlexaLogginginCount() {
        return 0;
    }
    
    public final void setAlexaLogginginCount(int p0) {
    }
    
    @java.lang.Override
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override
    protected void onNewIntent(@org.jetbrains.annotations.Nullable
    android.content.Intent intent) {
    }
    
    @java.lang.Override
    protected void onResume() {
    }
    
    private final void disableViews() {
    }
    
    @java.lang.Override
    public void onClick(@org.jetbrains.annotations.NotNull
    android.view.View view) {
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
    com.libreAlexa.netty.NettyData packet) {
    }
    
    private final void setAlexaViews() {
    }
    
    private final void intentToThingToTryActivity() {
    }
    
    private final void closeLoader() {
    }
    
    public final boolean isMetaDateRequestSent() {
        return false;
    }
    
    private final void showLoader() {
    }
    
    @java.lang.Override
    protected void onStop() {
    }
    
    private final void showAlertDialog(java.lang.String error) {
    }
    
    private final void handleBackPressed() {
    }
    
    @java.lang.Override
    public void onBackPressed() {
    }
    
    public final void setMetaDateRequestSent(boolean metaDateRequestSent) {
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0082\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u0016J\u0010\u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\tH\u0016J\u0012\u0010\n\u001a\u00020\u00042\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0016\u00a8\u0006\r"}, d2 = {"Lcom/cumulations/libreV2/activity/CTAmazonLoginActivity$AuthListener;", "Lcom/amazon/identity/auth/device/api/authorization/AuthorizeListener;", "(Lcom/cumulations/libreV2/activity/CTAmazonLoginActivity;)V", "onCancel", "", "cause", "Lcom/amazon/identity/auth/device/api/authorization/AuthCancellation;", "onError", "ae", "Lcom/amazon/identity/auth/device/AuthError;", "onSuccess", "response", "Lcom/amazon/identity/auth/device/api/authorization/AuthorizeResult;", "libreoemsdk_debug"})
    final class AuthListener extends com.amazon.identity.auth.device.api.authorization.AuthorizeListener {
        
        public AuthListener() {
            super();
        }
        
        @java.lang.Override
        public void onSuccess(@org.jetbrains.annotations.Nullable
        com.amazon.identity.auth.device.api.authorization.AuthorizeResult response) {
        }
        
        @java.lang.Override
        public void onCancel(@org.jetbrains.annotations.Nullable
        com.amazon.identity.auth.device.api.authorization.AuthCancellation cause) {
        }
        
        @java.lang.Override
        public void onError(@org.jetbrains.annotations.NotNull
        com.amazon.identity.auth.device.AuthError ae) {
        }
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lcom/cumulations/libreV2/activity/CTAmazonLoginActivity$Companion;", "", "()V", "ACCESS_TOKEN_TIMEOUT", "", "ALEXA_META_DATA_TIMER", "libreoemsdk_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}