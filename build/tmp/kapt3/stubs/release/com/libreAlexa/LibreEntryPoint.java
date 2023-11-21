package com.libreAlexa;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0010\u0018\u0000 %2\u00020\u0001:\u0001%B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0015\u001a\u00020\u0016J\b\u0010\u0017\u001a\u00020\u0016H\u0002J\b\u0010\u0018\u001a\u00020\u0016H\u0002J\u0010\u0010\u0019\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u001a\u001a\u00020\u0004J\u0010\u0010\u001b\u001a\u00020\u000e2\u0006\u0010\u001a\u001a\u00020\u0004H\u0002J\b\u0010\u001c\u001a\u0004\u0018\u00010\u000eJ\b\u0010\u001d\u001a\u0004\u0018\u00010\u000eJ\u000e\u0010\u001e\u001a\u00020\u00162\u0006\u0010\u001a\u001a\u00020\u0004J\u0006\u0010\u001f\u001a\u00020\u0016J\u000e\u0010 \u001a\u00020\u00162\u0006\u0010!\u001a\u00020\fJ\u0010\u0010\"\u001a\u00020\u00162\b\u0010#\u001a\u0004\u0018\u00010\u000eJ\u0006\u0010$\u001a\u00020\u0016R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0005\u001a\u00020\u0006X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u0010\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014\u00a8\u0006&"}, d2 = {"Lcom/libreAlexa/LibreEntryPoint;", "", "()V", "appContext", "Landroid/content/Context;", "mExecuted", "", "getMExecuted", "()Z", "setMExecuted", "(Z)V", "micExceptionActivityListener", "Lcom/libreAlexa/alexa/MicExceptionListener;", "registerData", "", "scanthread", "Ljava/lang/Thread;", "getScanthread", "()Ljava/lang/Thread;", "setScanthread", "(Ljava/lang/Thread;)V", "clearApplicationCollections", "", "generateKeyForDataStore", "generateRegisterDataForMB3", "getIpAddress", "context", "getIpAddressOldAPI", "getKey", "getRegisterMB3Data", "init", "initLUCIServices", "registerForMicException", "listener", "setKey", "key_alias", "unregisterMicException", "Companion", "libreoemsdk_release"})
public final class LibreEntryPoint {
    @org.jetbrains.annotations.Nullable
    private android.content.Context appContext;
    @org.jetbrains.annotations.Nullable
    private java.lang.Thread scanthread;
    private boolean mExecuted = false;
    @org.jetbrains.annotations.Nullable
    private com.libreAlexa.alexa.MicExceptionListener micExceptionActivityListener;
    @org.jetbrains.annotations.Nullable
    private java.lang.String registerData;
    @org.jetbrains.annotations.Nullable
    private static com.libreAlexa.Scanning.ScanThread wt;
    @org.jetbrains.annotations.Nullable
    private static java.lang.String key;
    private static final java.lang.String TAG = null;
    @org.jetbrains.annotations.NotNull
    private static final java.lang.String TAG_IP = "TAG_IP";
    @kotlin.jvm.Volatile
    private static volatile com.libreAlexa.LibreEntryPoint instance;
    @org.jetbrains.annotations.NotNull
    public static final com.libreAlexa.LibreEntryPoint.Companion Companion = null;
    
    public LibreEntryPoint() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Thread getScanthread() {
        return null;
    }
    
    public final void setScanthread(@org.jetbrains.annotations.Nullable
    java.lang.Thread p0) {
    }
    
    public final boolean getMExecuted() {
        return false;
    }
    
    public final void setMExecuted(boolean p0) {
    }
    
    public final void init(@org.jetbrains.annotations.NotNull
    android.content.Context context) {
    }
    
    public final void initLUCIServices() {
    }
    
    /**
     * clearing all collections related to application
     */
    public final void clearApplicationCollections() {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getIpAddress(@org.jetbrains.annotations.NotNull
    android.content.Context context) {
        return null;
    }
    
    private final java.lang.String getIpAddressOldAPI(android.content.Context context) {
        return null;
    }
    
    public final void registerForMicException(@org.jetbrains.annotations.NotNull
    com.libreAlexa.alexa.MicExceptionListener listener) {
    }
    
    public final void unregisterMicException() {
    }
    
    private final void generateKeyForDataStore() {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getKey() {
        return null;
    }
    
    public final void setKey(@org.jetbrains.annotations.Nullable
    java.lang.String key_alias) {
    }
    
    private final void generateRegisterDataForMB3() {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getRegisterMB3Data() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0010\u001a\u00020\bR\u0016\u0010\u0003\u001a\n \u0005*\u0004\u0018\u00010\u00040\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000f\u00a8\u0006\u0011"}, d2 = {"Lcom/libreAlexa/LibreEntryPoint$Companion;", "", "()V", "TAG", "", "kotlin.jvm.PlatformType", "TAG_IP", "instance", "Lcom/libreAlexa/LibreEntryPoint;", "key", "wt", "Lcom/libreAlexa/Scanning/ScanThread;", "getWt", "()Lcom/libreAlexa/Scanning/ScanThread;", "setWt", "(Lcom/libreAlexa/Scanning/ScanThread;)V", "getInstance", "libreoemsdk_release"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.Nullable
        public final com.libreAlexa.Scanning.ScanThread getWt() {
            return null;
        }
        
        public final void setWt(@org.jetbrains.annotations.Nullable
        com.libreAlexa.Scanning.ScanThread p0) {
        }
        
        @org.jetbrains.annotations.NotNull
        public final com.libreAlexa.LibreEntryPoint getInstance() {
            return null;
        }
    }
}