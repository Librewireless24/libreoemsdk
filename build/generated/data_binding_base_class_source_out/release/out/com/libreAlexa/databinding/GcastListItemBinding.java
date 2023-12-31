// Generated by view binder compiler. Do not edit!
package com.libreAlexa.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.libreAlexa.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class GcastListItemBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final TextView gCastDeviceName;

  @NonNull
  public final TextView gcastPerecentageUpdate;

  @NonNull
  public final GcastSeekbarLayoutListitemBinding gcastSeekbarLayout;

  @NonNull
  public final RelativeLayout mainLayout;

  @NonNull
  public final RelativeLayout managedeviceLayout;

  @NonNull
  public final TextView tvMsgCastUpdate;

  private GcastListItemBinding(@NonNull RelativeLayout rootView, @NonNull TextView gCastDeviceName,
      @NonNull TextView gcastPerecentageUpdate,
      @NonNull GcastSeekbarLayoutListitemBinding gcastSeekbarLayout,
      @NonNull RelativeLayout mainLayout, @NonNull RelativeLayout managedeviceLayout,
      @NonNull TextView tvMsgCastUpdate) {
    this.rootView = rootView;
    this.gCastDeviceName = gCastDeviceName;
    this.gcastPerecentageUpdate = gcastPerecentageUpdate;
    this.gcastSeekbarLayout = gcastSeekbarLayout;
    this.mainLayout = mainLayout;
    this.managedeviceLayout = managedeviceLayout;
    this.tvMsgCastUpdate = tvMsgCastUpdate;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static GcastListItemBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static GcastListItemBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.gcast_list_item, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static GcastListItemBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.gCastDeviceName;
      TextView gCastDeviceName = ViewBindings.findChildViewById(rootView, id);
      if (gCastDeviceName == null) {
        break missingId;
      }

      id = R.id.gcast_perecentage_update;
      TextView gcastPerecentageUpdate = ViewBindings.findChildViewById(rootView, id);
      if (gcastPerecentageUpdate == null) {
        break missingId;
      }

      id = R.id.gcastSeekbarLayout;
      View gcastSeekbarLayout = ViewBindings.findChildViewById(rootView, id);
      if (gcastSeekbarLayout == null) {
        break missingId;
      }
      GcastSeekbarLayoutListitemBinding binding_gcastSeekbarLayout = GcastSeekbarLayoutListitemBinding.bind(gcastSeekbarLayout);

      id = R.id.main_layout;
      RelativeLayout mainLayout = ViewBindings.findChildViewById(rootView, id);
      if (mainLayout == null) {
        break missingId;
      }

      RelativeLayout managedeviceLayout = (RelativeLayout) rootView;

      id = R.id.tvMsgCastUpdate;
      TextView tvMsgCastUpdate = ViewBindings.findChildViewById(rootView, id);
      if (tvMsgCastUpdate == null) {
        break missingId;
      }

      return new GcastListItemBinding((RelativeLayout) rootView, gCastDeviceName,
          gcastPerecentageUpdate, binding_gcastSeekbarLayout, mainLayout, managedeviceLayout,
          tvMsgCastUpdate);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
