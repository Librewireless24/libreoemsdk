// Generated by view binder compiler. Do not edit!
package com.libreAlexa.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.libreAlexa.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class LayoutBottomSheetBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final ConstraintLayout bottomSheet;

  @NonNull
  public final AppCompatImageButton btnBack;

  @NonNull
  public final LinearLayout layHeader;

  @NonNull
  public final AppCompatTextView txtHeading;

  private LayoutBottomSheetBinding(@NonNull ConstraintLayout rootView,
      @NonNull ConstraintLayout bottomSheet, @NonNull AppCompatImageButton btnBack,
      @NonNull LinearLayout layHeader, @NonNull AppCompatTextView txtHeading) {
    this.rootView = rootView;
    this.bottomSheet = bottomSheet;
    this.btnBack = btnBack;
    this.layHeader = layHeader;
    this.txtHeading = txtHeading;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static LayoutBottomSheetBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static LayoutBottomSheetBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.layout_bottom_sheet, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static LayoutBottomSheetBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      ConstraintLayout bottomSheet = (ConstraintLayout) rootView;

      id = R.id.btn_back;
      AppCompatImageButton btnBack = ViewBindings.findChildViewById(rootView, id);
      if (btnBack == null) {
        break missingId;
      }

      id = R.id.lay_Header;
      LinearLayout layHeader = ViewBindings.findChildViewById(rootView, id);
      if (layHeader == null) {
        break missingId;
      }

      id = R.id.txt_heading;
      AppCompatTextView txtHeading = ViewBindings.findChildViewById(rootView, id);
      if (txtHeading == null) {
        break missingId;
      }

      return new LayoutBottomSheetBinding((ConstraintLayout) rootView, bottomSheet, btnBack,
          layHeader, txtHeading);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}