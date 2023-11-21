// Generated by view binder compiler. Do not edit!
package com.libreAlexa.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.libreAlexa.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityGoogleCastUpdateAfterSacBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final Button btnDone;

  @NonNull
  public final TextView mGcastUpdateStatus;

  @NonNull
  public final ProgressBar mProgressBarGCastUpdateStatus;

  @NonNull
  public final ProgressBar retrivingDeviceInformation;

  @NonNull
  public final Toolbar toolbar;

  private ActivityGoogleCastUpdateAfterSacBinding(@NonNull RelativeLayout rootView,
      @NonNull Button btnDone, @NonNull TextView mGcastUpdateStatus,
      @NonNull ProgressBar mProgressBarGCastUpdateStatus,
      @NonNull ProgressBar retrivingDeviceInformation, @NonNull Toolbar toolbar) {
    this.rootView = rootView;
    this.btnDone = btnDone;
    this.mGcastUpdateStatus = mGcastUpdateStatus;
    this.mProgressBarGCastUpdateStatus = mProgressBarGCastUpdateStatus;
    this.retrivingDeviceInformation = retrivingDeviceInformation;
    this.toolbar = toolbar;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityGoogleCastUpdateAfterSacBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityGoogleCastUpdateAfterSacBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_google_cast_update_after_sac, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityGoogleCastUpdateAfterSacBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btnDone;
      Button btnDone = ViewBindings.findChildViewById(rootView, id);
      if (btnDone == null) {
        break missingId;
      }

      id = R.id.mGcastUpdateStatus;
      TextView mGcastUpdateStatus = ViewBindings.findChildViewById(rootView, id);
      if (mGcastUpdateStatus == null) {
        break missingId;
      }

      id = R.id.mProgressBarGCastUpdateStatus;
      ProgressBar mProgressBarGCastUpdateStatus = ViewBindings.findChildViewById(rootView, id);
      if (mProgressBarGCastUpdateStatus == null) {
        break missingId;
      }

      id = R.id.retrivingDeviceInformation;
      ProgressBar retrivingDeviceInformation = ViewBindings.findChildViewById(rootView, id);
      if (retrivingDeviceInformation == null) {
        break missingId;
      }

      id = R.id.toolbar;
      Toolbar toolbar = ViewBindings.findChildViewById(rootView, id);
      if (toolbar == null) {
        break missingId;
      }

      return new ActivityGoogleCastUpdateAfterSacBinding((RelativeLayout) rootView, btnDone,
          mGcastUpdateStatus, mProgressBarGCastUpdateStatus, retrivingDeviceInformation, toolbar);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
