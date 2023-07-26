package com.cumulations.libreV2.fragments;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001:\u0001\u0016B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\n\u001a\u00020\u000bH\u0002J$\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013H\u0016J\u001a\u0010\u0014\u001a\u00020\u000b2\u0006\u0010\u0015\u001a\u00020\r2\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013H\u0016R\u0019\u0010\u0003\u001a\n \u0005*\u0004\u0018\u00010\u00040\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"Lcom/cumulations/libreV2/fragments/CTTutorialsFragment;", "Landroidx/fragment/app/Fragment;", "()V", "TAG", "", "kotlin.jvm.PlatformType", "getTAG", "()Ljava/lang/String;", "binding", "Lcom/libreAlexa/databinding/CtFragmentTutorialsBinding;", "initViews", "", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onViewCreated", "view", "MyWebViewClient", "oemsdk_release"})
public final class CTTutorialsFragment extends androidx.fragment.app.Fragment {
    private final java.lang.String TAG = null;
    @org.jetbrains.annotations.Nullable
    private com.libreAlexa.databinding.CtFragmentTutorialsBinding binding;
    
    public CTTutorialsFragment() {
        super();
    }
    
    public final java.lang.String getTAG() {
        return null;
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public android.view.View onCreateView(@org.jetbrains.annotations.NotNull
    android.view.LayoutInflater inflater, @org.jetbrains.annotations.Nullable
    android.view.ViewGroup container, @org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
        return null;
    }
    
    @java.lang.Override
    public void onViewCreated(@org.jetbrains.annotations.NotNull
    android.view.View view, @org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    private final void initViews() {
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\b\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0018\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J\"\u0010\u000e\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010H\u0016J\u0018\u0010\u0011\u001a\u00020\u00122\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\u0004\u00a8\u0006\u0013"}, d2 = {"Lcom/cumulations/libreV2/fragments/CTTutorialsFragment$MyWebViewClient;", "Landroid/webkit/WebViewClient;", "progressBar", "Landroid/widget/ProgressBar;", "(Landroid/widget/ProgressBar;)V", "getProgressBar", "()Landroid/widget/ProgressBar;", "setProgressBar", "onPageFinished", "", "view", "Landroid/webkit/WebView;", "url", "", "onPageStarted", "favicon", "Landroid/graphics/Bitmap;", "shouldOverrideUrlLoading", "", "oemsdk_release"})
    public static final class MyWebViewClient extends android.webkit.WebViewClient {
        @org.jetbrains.annotations.NotNull
        private android.widget.ProgressBar progressBar;
        
        public MyWebViewClient(@org.jetbrains.annotations.NotNull
        android.widget.ProgressBar progressBar) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final android.widget.ProgressBar getProgressBar() {
            return null;
        }
        
        public final void setProgressBar(@org.jetbrains.annotations.NotNull
        android.widget.ProgressBar p0) {
        }
        
        @java.lang.Override
        public boolean shouldOverrideUrlLoading(@org.jetbrains.annotations.NotNull
        android.webkit.WebView view, @org.jetbrains.annotations.NotNull
        java.lang.String url) {
            return false;
        }
        
        @java.lang.Override
        public void onPageStarted(@org.jetbrains.annotations.NotNull
        android.webkit.WebView view, @org.jetbrains.annotations.NotNull
        java.lang.String url, @org.jetbrains.annotations.Nullable
        android.graphics.Bitmap favicon) {
        }
        
        @java.lang.Override
        public void onPageFinished(@org.jetbrains.annotations.NotNull
        android.webkit.WebView view, @org.jetbrains.annotations.NotNull
        java.lang.String url) {
        }
    }
}