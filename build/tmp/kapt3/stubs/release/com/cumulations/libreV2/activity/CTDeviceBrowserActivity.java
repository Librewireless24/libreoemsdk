package com.cumulations.libreV2.activity;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000z\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0012\u0018\u0000 G2\u00020\u00012\u00020\u0002:\u0001GB\u0005\u00a2\u0006\u0002\u0010\u0003J\b\u0010(\u001a\u00020)H\u0002J\b\u0010*\u001a\u00020)H\u0016J\u0010\u0010+\u001a\u00020)2\u0006\u0010,\u001a\u00020\u0005H\u0016J\u0010\u0010-\u001a\u00020)2\u0006\u0010.\u001a\u00020/H\u0016J\u0010\u00100\u001a\u00020)2\u0006\u00101\u001a\u000202H\u0016J\b\u00103\u001a\u00020)H\u0016J\u0012\u00104\u001a\u00020)2\b\u00105\u001a\u0004\u0018\u000106H\u0015J\u000e\u00107\u001a\u00020)2\u0006\u00108\u001a\u00020\u0012J\u000e\u00109\u001a\u00020)2\u0006\u00108\u001a\u00020\u0012J\b\u0010:\u001a\u00020)H\u0014J\b\u0010;\u001a\u00020)H\u0014J\b\u0010<\u001a\u00020)H\u0014J\u0012\u0010=\u001a\u00020)2\b\u0010>\u001a\u0004\u0018\u00010\u0005H\u0002J\u0010\u0010?\u001a\u00020\u00192\u0006\u0010@\u001a\u00020\u0012H\u0002J\u0010\u0010A\u001a\u00020)2\u0006\u00108\u001a\u00020\u0012H\u0002J\b\u0010B\u001a\u00020)H\u0002J\b\u0010C\u001a\u00020)H\u0002J\u0010\u0010D\u001a\u00020)2\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J\u0010\u0010E\u001a\u00020)2\u0006\u0010F\u001a\u00020\u0012H\u0002R\u0016\u0010\u0004\u001a\n \u0006*\u0004\u0018\u00010\u00050\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0080\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u000e\u0010\r\u001a\u00020\u000eX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0013\u001a\n\u0012\u0004\u0012\u00020\u0015\u0018\u00010\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0016\u001a\u0004\u0018\u00010\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0019X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u001a\u001a\u00020\u001b8\u0000@\u0000X\u0081\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001c\u0010\u001d\"\u0004\b\u001e\u0010\u001fR\u000e\u0010 \u001a\u00020\u0019X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010!\u001a\u0004\u0018\u00010\"X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010#\u001a\u0004\u0018\u00010$X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010%\u001a\u00020\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010&\u001a\u00020\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\'\u001a\u00020\u0019X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006H"}, d2 = {"Lcom/cumulations/libreV2/activity/CTDeviceBrowserActivity;", "Lcom/cumulations/libreV2/activity/CTDeviceDiscoveryActivity;", "Lcom/libreAlexa/netty/LibreDeviceInteractionListner;", "()V", "TAG", "", "kotlin.jvm.PlatformType", "alert", "Landroidx/appcompat/app/AlertDialog;", "getAlert$oemsdk_release", "()Landroidx/appcompat/app/AlertDialog;", "setAlert$oemsdk_release", "(Landroidx/appcompat/app/AlertDialog;)V", "binding", "Lcom/libreAlexa/databinding/CtActivityDeviceBrowserBinding;", "browser", "currentIpaddress", "current_source_index_selected", "", "dataItems", "Ljava/util/ArrayList;", "Lcom/cumulations/libreV2/model/DataItem;", "deviceBrowserListAdapter", "Lcom/cumulations/libreV2/adapter/CTDeviceBrowserListAdapter;", "gotolastpostion", "", "handler", "Landroid/os/Handler;", "getHandler$oemsdk_release", "()Landroid/os/Handler;", "setHandler$oemsdk_release", "(Landroid/os/Handler;)V", "isSongSelected", "luciControl", "Lcom/libreAlexa/luci/LUCIControl;", "mLayoutManager", "Landroidx/recyclerview/widget/LinearLayoutManager;", "presentJsonHashCode", "searchJsonHashCode", "searchOptionClicked", "closeLoader", "", "deviceDiscoveryAfterClearingTheCacheStarted", "deviceGotRemoved", "mIpAddress", "messageRecieved", "dataRecived", "Lcom/libreAlexa/netty/NettyData;", "newDeviceFound", "node", "Lcom/libreAlexa/luci/LSSDPNodes;", "onBackPressed", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onDataItemClicked", "position", "onFavClicked", "onResume", "onStart", "onStop", "parseJsonAndReflectInUI", "jsonStr", "saveInSharedPreference", "hashResult", "searchingDialog", "sendScrollDown", "sendScrollUp", "setTitleForTheBrowser", "showLoader", "messageId", "Companion", "oemsdk_release"})
public final class CTDeviceBrowserActivity extends com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity implements com.libreAlexa.netty.LibreDeviceInteractionListner {
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String TAG_CMD_ID = "CMD ID";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String TAG_WINDOW_CONTENT = "Window CONTENTS";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String TAG_BROWSER = "Browser";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String TAG_CUR_INDEX = "Index";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String TAG_ITEM_COUNT = "Item Count";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String TAG_ITEM_LIST = "ItemList";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String TAG_ITEM_ID = "Item ID";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String TAG_ITEM_TYPE = "ItemType";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String TAG_ITEM_NAME = "Name";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String TAG_ITEM_FAVORITE = "Favorite";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String TAG_ITEM_ALBUMURL = "StationImage";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String GET_PLAY = "GETUI:PLAY";
    @org.jetbrains.annotations.Nullable
    private java.lang.String currentIpaddress;
    @org.jetbrains.annotations.Nullable
    private com.libreAlexa.luci.LUCIControl luciControl;
    @org.jetbrains.annotations.Nullable
    private java.util.ArrayList<com.cumulations.libreV2.model.DataItem> dataItems;
    @org.jetbrains.annotations.Nullable
    private com.cumulations.libreV2.adapter.CTDeviceBrowserListAdapter deviceBrowserListAdapter;
    @org.jetbrains.annotations.Nullable
    private androidx.recyclerview.widget.LinearLayoutManager mLayoutManager;
    private boolean gotolastpostion = false;
    @org.jetbrains.annotations.NotNull
    private java.lang.String browser = "";
    private int current_source_index_selected = -1;
    @org.jetbrains.annotations.Nullable
    private androidx.appcompat.app.AlertDialog alert;
    private boolean isSongSelected = false;
    private int searchJsonHashCode = 0;
    private int presentJsonHashCode = 0;
    private boolean searchOptionClicked = false;
    private java.lang.String TAG;
    @android.annotation.SuppressLint(value = {"HandlerLeak"})
    @org.jetbrains.annotations.NotNull
    private android.os.Handler handler;
    private com.libreAlexa.databinding.CtActivityDeviceBrowserBinding binding;
    @org.jetbrains.annotations.NotNull
    public static final com.cumulations.libreV2.activity.CTDeviceBrowserActivity.Companion Companion = null;
    
    public CTDeviceBrowserActivity() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable
    public final androidx.appcompat.app.AlertDialog getAlert$oemsdk_release() {
        return null;
    }
    
    public final void setAlert$oemsdk_release(@org.jetbrains.annotations.Nullable
    androidx.appcompat.app.AlertDialog p0) {
    }
    
    @org.jetbrains.annotations.NotNull
    public final android.os.Handler getHandler$oemsdk_release() {
        return null;
    }
    
    public final void setHandler$oemsdk_release(@org.jetbrains.annotations.NotNull
    android.os.Handler p0) {
    }
    
    @java.lang.Override
    @android.annotation.SuppressLint(value = {"ClickableViewAccessibility"})
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override
    protected void onStart() {
    }
    
    @java.lang.Override
    protected void onResume() {
    }
    
    private final void sendScrollUp() {
    }
    
    private final void sendScrollDown() {
    }
    
    private final void searchingDialog(int position) {
    }
    
    private final void showLoader(int messageId) {
    }
    
    private final void closeLoader() {
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
    com.libreAlexa.netty.NettyData dataRecived) {
    }
    
    /**
     * This function gets the Json string
     */
    @kotlin.jvm.Throws(exceptionClasses = {org.json.JSONException.class})
    private final void parseJsonAndReflectInUI(java.lang.String jsonStr) throws org.json.JSONException {
    }
    
    private final boolean saveInSharedPreference(int hashResult) {
        return false;
    }
    
    @java.lang.Override
    protected void onStop() {
    }
    
    @java.lang.Override
    public void onBackPressed() {
    }
    
    private final void setTitleForTheBrowser(int current_source_index_selected) {
    }
    
    public final void onDataItemClicked(int position) {
    }
    
    public final void onFavClicked(int position) {
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\f\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0010"}, d2 = {"Lcom/cumulations/libreV2/activity/CTDeviceBrowserActivity$Companion;", "", "()V", "GET_PLAY", "", "TAG_BROWSER", "TAG_CMD_ID", "TAG_CUR_INDEX", "TAG_ITEM_ALBUMURL", "TAG_ITEM_COUNT", "TAG_ITEM_FAVORITE", "TAG_ITEM_ID", "TAG_ITEM_LIST", "TAG_ITEM_NAME", "TAG_ITEM_TYPE", "TAG_WINDOW_CONTENT", "oemsdk_release"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}