package com.cumulations.libreV2;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0007\u0018\u0000 \u00142\u00020\u0001:\u0001\u0014B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eJ\u0016\u0010\u000f\u001a\u00020\n2\u0006\u0010\u0010\u001a\u00020\f2\u0006\u0010\u0011\u001a\u00020\u000eJ\u000e\u0010\u0012\u001a\u00020\u000e2\u0006\u0010\u000b\u001a\u00020\fJ\u000e\u0010\u0013\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"Lcom/cumulations/libreV2/SharedPreferenceHelper;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "getContext", "()Landroid/content/Context;", "sharePreference", "Landroid/content/SharedPreferences;", "alexaLoginAlertDontAsk", "", "ipAddress", "", "dontAsk", "", "firstTimeAskedPermission", "permission", "isFirstTime", "isAlexaLoginAlertDontAskChecked", "isFirstTimeAskingPermission", "Companion", "oemsdk_debug"})
public final class SharedPreferenceHelper {
    @org.jetbrains.annotations.NotNull
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String ALEXA_ALERT_DONT_ASK = "alexaAlertDon\'tAsk";
    @org.jetbrains.annotations.NotNull
    private android.content.SharedPreferences sharePreference;
    @org.jetbrains.annotations.NotNull
    public static final com.cumulations.libreV2.SharedPreferenceHelper.Companion Companion = null;
    
    public SharedPreferenceHelper(@org.jetbrains.annotations.NotNull
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final android.content.Context getContext() {
        return null;
    }
    
    public final void firstTimeAskedPermission(@org.jetbrains.annotations.NotNull
    java.lang.String permission, boolean isFirstTime) {
    }
    
    public final boolean isFirstTimeAskingPermission(@org.jetbrains.annotations.NotNull
    java.lang.String permission) {
        return false;
    }
    
    public final void alexaLoginAlertDontAsk(@org.jetbrains.annotations.NotNull
    java.lang.String ipAddress, boolean dontAsk) {
    }
    
    public final boolean isAlexaLoginAlertDontAskChecked(@org.jetbrains.annotations.NotNull
    java.lang.String ipAddress) {
        return false;
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/cumulations/libreV2/SharedPreferenceHelper$Companion;", "", "()V", "ALEXA_ALERT_DONT_ASK", "", "oemsdk_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}