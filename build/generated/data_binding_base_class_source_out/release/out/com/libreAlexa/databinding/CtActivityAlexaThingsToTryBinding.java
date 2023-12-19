// Generated by view binder compiler. Do not edit!
package com.libreAlexa.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

public final class CtActivityAlexaThingsToTryBinding implements ViewBinding {
  @NonNull
  private final CoordinatorLayout rootView;

  @NonNull
  public final RelativeLayout RlHeaderMain;

  @NonNull
  public final AppBarLayout appbarLayout;

  @NonNull
  public final AppCompatImageView ivBack;

  @NonNull
  public final LinearLayout llAlexaApp;

  @NonNull
  public final Toolbar toolbar;

  @NonNull
  public final AppCompatTextView tvAlexaApp;

  @NonNull
  public final AppCompatTextView tvDone;

  private CtActivityAlexaThingsToTryBinding(@NonNull CoordinatorLayout rootView,
      @NonNull RelativeLayout RlHeaderMain, @NonNull AppBarLayout appbarLayout,
      @NonNull AppCompatImageView ivBack, @NonNull LinearLayout llAlexaApp,
      @NonNull Toolbar toolbar, @NonNull AppCompatTextView tvAlexaApp,
      @NonNull AppCompatTextView tvDone) {
    this.rootView = rootView;
    this.RlHeaderMain = RlHeaderMain;
    this.appbarLayout = appbarLayout;
    this.ivBack = ivBack;
    this.llAlexaApp = llAlexaApp;
    this.toolbar = toolbar;
    this.tvAlexaApp = tvAlexaApp;
    this.tvDone = tvDone;
  }

  @Override
  @NonNull
  public CoordinatorLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static CtActivityAlexaThingsToTryBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static CtActivityAlexaThingsToTryBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.ct_activity_alexa_things_to_try, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static CtActivityAlexaThingsToTryBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.Rl_header_main;
      RelativeLayout RlHeaderMain = ViewBindings.findChildViewById(rootView, id);
      if (RlHeaderMain == null) {
        break missingId;
      }

      id = R.id.appbarLayout;
      AppBarLayout appbarLayout = ViewBindings.findChildViewById(rootView, id);
      if (appbarLayout == null) {
        break missingId;
      }

      id = R.id.iv_back;
      AppCompatImageView ivBack = ViewBindings.findChildViewById(rootView, id);
      if (ivBack == null) {
        break missingId;
      }

      id = R.id.ll_alexa_app;
      LinearLayout llAlexaApp = ViewBindings.findChildViewById(rootView, id);
      if (llAlexaApp == null) {
        break missingId;
      }

      id = R.id.toolbar;
      Toolbar toolbar = ViewBindings.findChildViewById(rootView, id);
      if (toolbar == null) {
        break missingId;
      }

      id = R.id.tv_alexa_app;
      AppCompatTextView tvAlexaApp = ViewBindings.findChildViewById(rootView, id);
      if (tvAlexaApp == null) {
        break missingId;
      }

      id = R.id.tv_done;
      AppCompatTextView tvDone = ViewBindings.findChildViewById(rootView, id);
      if (tvDone == null) {
        break missingId;
      }

      return new CtActivityAlexaThingsToTryBinding((CoordinatorLayout) rootView, RlHeaderMain,
          appbarLayout, ivBack, llAlexaApp, toolbar, tvAlexaApp, tvDone);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}