// Generated by view binder compiler. Do not edit!
package com.libreAlexa.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.libreAlexa.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class CtActivityDeviceConnectingBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final AppCompatTextView pleaseWaitLabel;

  @NonNull
  public final ProgressBar setupProgressBar;

  @NonNull
  public final AppCompatImageView setupProgressImage;

  @NonNull
  public final AppCompatTextView tvSetupInfo;

  private CtActivityDeviceConnectingBinding(@NonNull LinearLayout rootView,
      @NonNull AppCompatTextView pleaseWaitLabel, @NonNull ProgressBar setupProgressBar,
      @NonNull AppCompatImageView setupProgressImage, @NonNull AppCompatTextView tvSetupInfo) {
    this.rootView = rootView;
    this.pleaseWaitLabel = pleaseWaitLabel;
    this.setupProgressBar = setupProgressBar;
    this.setupProgressImage = setupProgressImage;
    this.tvSetupInfo = tvSetupInfo;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static CtActivityDeviceConnectingBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static CtActivityDeviceConnectingBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.ct_activity_device_connecting, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static CtActivityDeviceConnectingBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.please_wait_label;
      AppCompatTextView pleaseWaitLabel = ViewBindings.findChildViewById(rootView, id);
      if (pleaseWaitLabel == null) {
        break missingId;
      }

      id = R.id.setup_progress_bar;
      ProgressBar setupProgressBar = ViewBindings.findChildViewById(rootView, id);
      if (setupProgressBar == null) {
        break missingId;
      }

      id = R.id.setup_progress_image;
      AppCompatImageView setupProgressImage = ViewBindings.findChildViewById(rootView, id);
      if (setupProgressImage == null) {
        break missingId;
      }

      id = R.id.tv_setup_info;
      AppCompatTextView tvSetupInfo = ViewBindings.findChildViewById(rootView, id);
      if (tvSetupInfo == null) {
        break missingId;
      }

      return new CtActivityDeviceConnectingBinding((LinearLayout) rootView, pleaseWaitLabel,
          setupProgressBar, setupProgressImage, tvSetupInfo);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}