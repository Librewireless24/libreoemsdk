package com.cumulations.libreV2.activity;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000\u0086\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0017\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 T2\u00020\u00012\u00020\u0002:\u0001TB\u0005\u00a2\u0006\u0002\u0010\u0003J\b\u0010*\u001a\u00020+H\u0002J\b\u0010,\u001a\u00020+H\u0016J\u0010\u0010-\u001a\u00020+2\u0006\u0010.\u001a\u00020\u0005H\u0016J\u0006\u0010/\u001a\u00020+J\u0018\u00100\u001a\u00020+2\u0006\u00101\u001a\u00020\u00052\u0006\u00102\u001a\u00020\u0011H\u0002J\u0010\u00103\u001a\u00020+2\u0006\u00104\u001a\u000205H\u0016J\u0010\u00106\u001a\u00020+2\u0006\u00107\u001a\u000208H\u0016J\u0012\u00109\u001a\u00020+2\b\u0010:\u001a\u0004\u0018\u00010;H\u0015J\u000e\u0010<\u001a\u00020+2\u0006\u0010=\u001a\u00020\u0011J\u000e\u0010>\u001a\u00020+2\u0006\u0010=\u001a\u00020\u0011J\b\u0010?\u001a\u00020+H\u0014J\b\u0010@\u001a\u00020+H\u0014J\b\u0010A\u001a\u00020+H\u0014J\u0012\u0010B\u001a\u00020+2\b\u0010C\u001a\u0004\u0018\u00010\u0005H\u0002J\u0012\u0010D\u001a\u00020+2\b\u0010C\u001a\u0004\u0018\u00010\u0005H\u0002J\u0010\u0010E\u001a\u00020\u00182\u0006\u0010F\u001a\u00020\u0011H\u0002J\u0010\u0010G\u001a\u00020+2\u0006\u0010=\u001a\u00020\u0011H\u0002J\u000e\u0010H\u001a\u00020+2\u0006\u0010I\u001a\u00020\u0011J\u000e\u0010J\u001a\u00020+2\u0006\u0010K\u001a\u00020\u0005J\b\u0010L\u001a\u00020+H\u0002J\b\u0010M\u001a\u00020+H\u0002J\u0010\u0010N\u001a\u00020+2\u0006\u0010\u0010\u001a\u00020\u0011H\u0002J\u0010\u0010O\u001a\u00020+2\u0006\u0010P\u001a\u00020\u0011H\u0002J\u0010\u0010Q\u001a\u00020+2\u0006\u0010R\u001a\u00020SH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0080\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u000e\u0010\f\u001a\u00020\rX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0012\u001a\n\u0012\u0004\u0012\u00020\u0014\u0018\u00010\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0015\u001a\u0004\u0018\u00010\u0016X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0018X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u0019\u001a\u00020\u001a8\u0000@\u0000X\u0081\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u001c\"\u0004\b\u001d\u0010\u001eR\u000e\u0010\u001f\u001a\u00020\u0018X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010 \u001a\u0004\u0018\u00010!X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\"\u001a\u0004\u0018\u00010#X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010$\u001a\n &*\u0004\u0018\u00010%0%X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\'\u001a\u00020\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010(\u001a\u00020\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010)\u001a\u00020\u0018X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006U"}, d2 = {"Lcom/cumulations/libreV2/activity/CTDeviceBrowserActivity;", "Lcom/cumulations/libreV2/activity/CTDeviceDiscoveryActivity;", "Lcom/libreAlexa/netty/LibreDeviceInteractionListner;", "()V", "TAG", "", "alert", "Landroidx/appcompat/app/AlertDialog;", "getAlert$libreoemsdk_release", "()Landroidx/appcompat/app/AlertDialog;", "setAlert$libreoemsdk_release", "(Landroidx/appcompat/app/AlertDialog;)V", "binding", "Lcom/libreAlexa/databinding/CtActivityDeviceBrowserBinding;", "browser", "currentIpaddress", "current_source_index_selected", "", "dataItems", "Ljava/util/ArrayList;", "Lcom/cumulations/libreV2/model/DataItem;", "deviceBrowserListAdapter", "Lcom/cumulations/libreV2/adapter/CTDeviceBrowserListAdapter;", "gotolastpostion", "", "handler", "Landroid/os/Handler;", "getHandler$libreoemsdk_release", "()Landroid/os/Handler;", "setHandler$libreoemsdk_release", "(Landroid/os/Handler;)V", "isSongSelected", "luciControl", "Lcom/libreAlexa/luci/LUCIControl;", "mLayoutManager", "Landroidx/recyclerview/widget/LinearLayoutManager;", "mScanHandler", "Lcom/libreAlexa/Scanning/ScanningHandler;", "kotlin.jvm.PlatformType", "presentJsonHashCode", "searchJsonHashCode", "searchOptionClicked", "closeLoader", "", "deviceDiscoveryAfterClearingTheCacheStarted", "deviceGotRemoved", "mIpAddress", "exitOnBackPressed", "gotoSourcesOption", "ipaddress", "currentSource", "messageRecieved", "dataRecived", "Lcom/libreAlexa/netty/NettyData;", "newDeviceFound", "node", "Lcom/libreAlexa/luci/LSSDPNodes;", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onDataItemClicked", "position", "onFavClicked", "onResume", "onStart", "onStop", "parseJsonAndReflectInUI", "jsonStr", "parseJsonAndReflectInUIUSB", "saveInSharedPreference", "hashResult", "searchingDialog", "selectItem", "row", "sendData", "str", "sendScrollDown", "sendScrollUp", "setTitleForTheBrowser", "showLoader", "messageId", "showNextPreviousButtons", "window", "Lorg/json/JSONObject;", "Companion", "libreoemsdk_release"})
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
    public static final java.lang.String TAG_TOTALITEM = "TotalItems";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String TAG_PAGE_NO = "PageNo";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String GET_PLAY = "GETUI:PLAY";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String SELECTITEM = "SELECTITEM:";
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
    private final com.libreAlexa.Scanning.ScanningHandler mScanHandler = null;
    @org.jetbrains.annotations.NotNull
    private java.lang.String TAG = "==CTDeviceBrowserActivity==";
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
    public final androidx.appcompat.app.AlertDialog getAlert$libreoemsdk_release() {
        return null;
    }
    
    public final void setAlert$libreoemsdk_release(@org.jetbrains.annotations.Nullable
    androidx.appcompat.app.AlertDialog p0) {
    }
    
    @org.jetbrains.annotations.NotNull
    public final android.os.Handler getHandler$libreoemsdk_release() {
        return null;
    }
    
    public final void setHandler$libreoemsdk_release(@org.jetbrains.annotations.NotNull
    android.os.Handler p0) {
    }
    
    @java.lang.Override
    @android.annotation.SuppressLint(value = {"ClickableViewAccessibility"})
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    private final void gotoSourcesOption(java.lang.String ipaddress, int currentSource) {
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
    
    public final void sendData(@org.jetbrains.annotations.NotNull
    java.lang.String str) {
    }
    
    private final void showNextPreviousButtons(org.json.JSONObject window) {
    }
    
    /**
     * This function gets the Json string
     */
    @kotlin.jvm.Throws(exceptionClasses = {org.json.JSONException.class})
    private final void parseJsonAndReflectInUIUSB(java.lang.String jsonStr) throws org.json.JSONException {
    }
    
    private final boolean saveInSharedPreference(int hashResult) {
        return false;
    }
    
    @java.lang.Override
    protected void onStop() {
    }
    
    public final void exitOnBackPressed() {
    }
    
    private final void setTitleForTheBrowser(int current_source_index_selected) {
    }
    
    public final void onDataItemClicked(int position) {
    }
    
    public final void onFavClicked(int position) {
    }
    
    public final void selectItem(int row) {
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u000f\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0013"}, d2 = {"Lcom/cumulations/libreV2/activity/CTDeviceBrowserActivity$Companion;", "", "()V", "GET_PLAY", "", "SELECTITEM", "TAG_BROWSER", "TAG_CMD_ID", "TAG_CUR_INDEX", "TAG_ITEM_ALBUMURL", "TAG_ITEM_COUNT", "TAG_ITEM_FAVORITE", "TAG_ITEM_ID", "TAG_ITEM_LIST", "TAG_ITEM_NAME", "TAG_ITEM_TYPE", "TAG_PAGE_NO", "TAG_TOTALITEM", "TAG_WINDOW_CONTENT", "libreoemsdk_release"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}