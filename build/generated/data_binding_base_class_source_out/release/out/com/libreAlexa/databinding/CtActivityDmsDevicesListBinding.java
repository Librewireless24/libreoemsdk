// Generated by view binder compiler. Do not edit!
package com.libreAlexa.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.appbar.AppBarLayout;
import com.libreAlexa.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class CtActivityDmsDevicesListBinding implements ViewBinding {
  @NonNull
  private final CoordinatorLayout rootView;

  @NonNull
  public final AppBarLayout appBar;

  @NonNull
  public final ListView deviceList;

  @NonNull
  public final AppCompatImageView ivBack;

  @NonNull
  public final ImageView refresh;

  @NonNull
  public final Toolbar toolbar;

  @NonNull
  public final AppCompatTextView tvFolderName;

  @NonNull
  public final AppCompatTextView tvNoData;

  private CtActivityDmsDevicesListBinding(@NonNull CoordinatorLayout rootView,
      @NonNull AppBarLayout appBar, @NonNull ListView deviceList,
      @NonNull AppCompatImageView ivBack, @NonNull ImageView refresh, @NonNull Toolbar toolbar,
      @NonNull AppCompatTextView tvFolderName, @NonNull AppCompatTextView tvNoData) {
    this.rootView = rootView;
    this.appBar = appBar;
    this.deviceList = deviceList;
    this.ivBack = ivBack;
    this.refresh = refresh;
    this.toolbar = toolbar;
    this.tvFolderName = tvFolderName;
    this.tvNoData = tvNoData;
  }

  @Override
  @NonNull
  public CoordinatorLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static CtActivityDmsDevicesListBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static CtActivityDmsDevicesListBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.ct_activity_dms_devices_list, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static CtActivityDmsDevicesListBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.appBar;
      AppBarLayout appBar = ViewBindings.findChildViewById(rootView, id);
      if (appBar == null) {
        break missingId;
      }

      id = R.id.deviceList;
      ListView deviceList = ViewBindings.findChildViewById(rootView, id);
      if (deviceList == null) {
        break missingId;
      }

      id = R.id.iv_back;
      AppCompatImageView ivBack = ViewBindings.findChildViewById(rootView, id);
      if (ivBack == null) {
        break missingId;
      }

      id = R.id.refresh;
      ImageView refresh = ViewBindings.findChildViewById(rootView, id);
      if (refresh == null) {
        break missingId;
      }

      id = R.id.toolbar;
      Toolbar toolbar = ViewBindings.findChildViewById(rootView, id);
      if (toolbar == null) {
        break missingId;
      }

      id = R.id.tv_folder_name;
      AppCompatTextView tvFolderName = ViewBindings.findChildViewById(rootView, id);
      if (tvFolderName == null) {
        break missingId;
      }

      id = R.id.tv_no_data;
      AppCompatTextView tvNoData = ViewBindings.findChildViewById(rootView, id);
      if (tvNoData == null) {
        break missingId;
      }

      return new CtActivityDmsDevicesListBinding((CoordinatorLayout) rootView, appBar, deviceList,
          ivBack, refresh, toolbar, tvFolderName, tvNoData);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
