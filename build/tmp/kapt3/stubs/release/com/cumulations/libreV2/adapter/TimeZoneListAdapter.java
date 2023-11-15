package com.cumulations.libreV2.adapter;

/**
 * Created By Shaik
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0002\u0014\u0015B%\u0012\u0016\u0010\u0003\u001a\u0012\u0012\u0004\u0012\u00020\u00050\u0004j\b\u0012\u0004\u0012\u00020\u0005`\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u00a2\u0006\u0002\u0010\tJ\b\u0010\n\u001a\u00020\u000bH\u0016J\u0018\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u00022\u0006\u0010\u000f\u001a\u00020\u000bH\u0016J\u0018\u0010\u0010\u001a\u00020\u00022\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u000bH\u0016R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u0003\u001a\u0012\u0012\u0004\u0012\u00020\u00050\u0004j\b\u0012\u0004\u0012\u00020\u0005`\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/cumulations/libreV2/adapter/TimeZoneListAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/cumulations/libreV2/adapter/TimeZoneListAdapter$TimeZoneViewHolder;", "timeZoneList", "Ljava/util/ArrayList;", "Lcom/cumulations/libreV2/model/TimeZoneDataClass;", "Lkotlin/collections/ArrayList;", "timeZoneFragment", "Lcom/cumulations/libreV2/fragments/TimeZoneFragment;", "(Ljava/util/ArrayList;Lcom/cumulations/libreV2/fragments/TimeZoneFragment;)V", "getItemCount", "", "onBindViewHolder", "", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "p1", "SendDataToFragment", "TimeZoneViewHolder", "libreoemsdk_release"})
public final class TimeZoneListAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.cumulations.libreV2.adapter.TimeZoneListAdapter.TimeZoneViewHolder> {
    @org.jetbrains.annotations.NotNull
    private java.util.ArrayList<com.cumulations.libreV2.model.TimeZoneDataClass> timeZoneList;
    @org.jetbrains.annotations.NotNull
    private com.cumulations.libreV2.fragments.TimeZoneFragment timeZoneFragment;
    
    public TimeZoneListAdapter(@org.jetbrains.annotations.NotNull
    java.util.ArrayList<com.cumulations.libreV2.model.TimeZoneDataClass> timeZoneList, @org.jetbrains.annotations.NotNull
    com.cumulations.libreV2.fragments.TimeZoneFragment timeZoneFragment) {
        super();
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public com.cumulations.libreV2.adapter.TimeZoneListAdapter.TimeZoneViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull
    android.view.ViewGroup parent, int p1) {
        return null;
    }
    
    @java.lang.Override
    public int getItemCount() {
        return 0;
    }
    
    @java.lang.Override
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull
    com.cumulations.libreV2.adapter.TimeZoneListAdapter.TimeZoneViewHolder holder, int position) {
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&\u00a8\u0006\u0006"}, d2 = {"Lcom/cumulations/libreV2/adapter/TimeZoneListAdapter$SendDataToFragment;", "", "handleUserData", "", "data", "", "libreoemsdk_release"})
    public static abstract interface SendDataToFragment {
        
        public abstract void handleUserData(@org.jetbrains.annotations.NotNull
        java.lang.String data);
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001e\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0012"}, d2 = {"Lcom/cumulations/libreV2/adapter/TimeZoneListAdapter$TimeZoneViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "view", "Landroid/view/View;", "(Landroid/view/View;)V", "txtTimeZoneID", "Landroidx/appcompat/widget/AppCompatTextView;", "txtTimeZoneName", "txtTimeZoneParent", "Landroidx/constraintlayout/widget/ConstraintLayout;", "bind", "", "timeZones", "Lcom/cumulations/libreV2/model/TimeZoneDataClass;", "position", "", "timeZoneFragment", "Lcom/cumulations/libreV2/fragments/TimeZoneFragment;", "libreoemsdk_release"})
    public static final class TimeZoneViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @org.jetbrains.annotations.NotNull
        private final androidx.appcompat.widget.AppCompatTextView txtTimeZoneID = null;
        @org.jetbrains.annotations.NotNull
        private final androidx.appcompat.widget.AppCompatTextView txtTimeZoneName = null;
        @org.jetbrains.annotations.NotNull
        private final androidx.constraintlayout.widget.ConstraintLayout txtTimeZoneParent = null;
        
        public TimeZoneViewHolder(@org.jetbrains.annotations.NotNull
        android.view.View view) {
            super(null);
        }
        
        public final void bind(@org.jetbrains.annotations.NotNull
        com.cumulations.libreV2.model.TimeZoneDataClass timeZones, int position, @org.jetbrains.annotations.NotNull
        com.cumulations.libreV2.fragments.TimeZoneFragment timeZoneFragment) {
        }
    }
}