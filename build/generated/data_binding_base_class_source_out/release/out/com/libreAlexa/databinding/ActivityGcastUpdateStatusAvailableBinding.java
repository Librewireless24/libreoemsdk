// Generated by view binder compiler. Do not edit!
package com.libreAlexa.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.libreAlexa.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityGcastUpdateStatusAvailableBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final ListView GcastUpdateListView;

  @NonNull
  public final Button btnDone;

  @NonNull
  public final Toolbar toolbar;

  private ActivityGcastUpdateStatusAvailableBinding(@NonNull RelativeLayout rootView,
      @NonNull ListView GcastUpdateListView, @NonNull Button btnDone, @NonNull Toolbar toolbar) {
    this.rootView = rootView;
    this.GcastUpdateListView = GcastUpdateListView;
    this.btnDone = btnDone;
    this.toolbar = toolbar;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityGcastUpdateStatusAvailableBinding inflate(
      @NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityGcastUpdateStatusAvailableBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_gcast_update_status_available, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityGcastUpdateStatusAvailableBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.GcastUpdateListView;
      ListView GcastUpdateListView = ViewBindings.findChildViewById(rootView, id);
      if (GcastUpdateListView == null) {
        break missingId;
      }

      id = R.id.btnDone;
      Button btnDone = ViewBindings.findChildViewById(rootView, id);
      if (btnDone == null) {
        break missingId;
      }

      id = R.id.toolbar;
      Toolbar toolbar = ViewBindings.findChildViewById(rootView, id);
      if (toolbar == null) {
        break missingId;
      }

      return new ActivityGcastUpdateStatusAvailableBinding((RelativeLayout) rootView,
          GcastUpdateListView, btnDone, toolbar);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
