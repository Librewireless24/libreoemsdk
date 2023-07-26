package com.cumulations.libreV2;

/**
 * Created By Shaik Mansoor
 * 02/05/2023
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\u0018\u00002\u00020\u0001:\u0001\u000eB\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\fJ\u0006\u0010\r\u001a\u00020\nR\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/cumulations/libreV2/CustomProgressDialog;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "cpTitle", "Landroid/widget/TextView;", "dialog", "Lcom/cumulations/libreV2/CustomProgressDialog$CustomDialog;", "start", "", "title", "", "stop", "CustomDialog", "oemsdk_debug"})
public final class CustomProgressDialog {
    @org.jetbrains.annotations.NotNull
    private com.cumulations.libreV2.CustomProgressDialog.CustomDialog dialog;
    @org.jetbrains.annotations.NotNull
    private android.widget.TextView cpTitle;
    
    public CustomProgressDialog(@org.jetbrains.annotations.NotNull
    android.content.Context context) {
        super();
    }
    
    public final void start(@org.jetbrains.annotations.NotNull
    java.lang.String title) {
    }
    
    public final void stop() {
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004\u00a8\u0006\u0005"}, d2 = {"Lcom/cumulations/libreV2/CustomProgressDialog$CustomDialog;", "Landroid/app/Dialog;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "oemsdk_debug"})
    public static final class CustomDialog extends android.app.Dialog {
        
        public CustomDialog(@org.jetbrains.annotations.NotNull
        android.content.Context context) {
            super(null);
        }
    }
}