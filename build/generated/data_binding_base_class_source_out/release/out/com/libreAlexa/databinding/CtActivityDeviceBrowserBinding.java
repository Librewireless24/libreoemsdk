// Generated by view binder compiler. Do not edit!
package com.libreAlexa.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.libreAlexa.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class CtActivityDeviceBrowserBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final AppCompatTextView browserTitle;

  @NonNull
  public final AppCompatImageButton ibBack;

  @NonNull
  public final AppCompatImageView ibHome;

  @NonNull
  public final MusicPlayingWidgetBinding idMusicWidget;

  @NonNull
  public final LinearLayout idPrevNextLayout;

  @NonNull
  public final TextView idTvNext;

  @NonNull
  public final TextView idTvPrevious;

  @NonNull
  public final AppCompatImageView ivSourceIcon;

  @NonNull
  public final RecyclerView rvDeviceBrowser;

  @NonNull
  public final Toolbar toolbar;

  @NonNull
  public final AppCompatTextView tvNoData;

  private CtActivityDeviceBrowserBinding(@NonNull RelativeLayout rootView,
      @NonNull AppCompatTextView browserTitle, @NonNull AppCompatImageButton ibBack,
      @NonNull AppCompatImageView ibHome, @NonNull MusicPlayingWidgetBinding idMusicWidget,
      @NonNull LinearLayout idPrevNextLayout, @NonNull TextView idTvNext,
      @NonNull TextView idTvPrevious, @NonNull AppCompatImageView ivSourceIcon,
      @NonNull RecyclerView rvDeviceBrowser, @NonNull Toolbar toolbar,
      @NonNull AppCompatTextView tvNoData) {
    this.rootView = rootView;
    this.browserTitle = browserTitle;
    this.ibBack = ibBack;
    this.ibHome = ibHome;
    this.idMusicWidget = idMusicWidget;
    this.idPrevNextLayout = idPrevNextLayout;
    this.idTvNext = idTvNext;
    this.idTvPrevious = idTvPrevious;
    this.ivSourceIcon = ivSourceIcon;
    this.rvDeviceBrowser = rvDeviceBrowser;
    this.toolbar = toolbar;
    this.tvNoData = tvNoData;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static CtActivityDeviceBrowserBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static CtActivityDeviceBrowserBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.ct_activity_device_browser, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static CtActivityDeviceBrowserBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.browser_title;
      AppCompatTextView browserTitle = ViewBindings.findChildViewById(rootView, id);
      if (browserTitle == null) {
        break missingId;
      }

      id = R.id.ib_back;
      AppCompatImageButton ibBack = ViewBindings.findChildViewById(rootView, id);
      if (ibBack == null) {
        break missingId;
      }

      id = R.id.ib_home;
      AppCompatImageView ibHome = ViewBindings.findChildViewById(rootView, id);
      if (ibHome == null) {
        break missingId;
      }

      id = R.id.id_music_widget;
      View idMusicWidget = ViewBindings.findChildViewById(rootView, id);
      if (idMusicWidget == null) {
        break missingId;
      }
      MusicPlayingWidgetBinding binding_idMusicWidget = MusicPlayingWidgetBinding.bind(idMusicWidget);

      id = R.id.id_prev_next_layout;
      LinearLayout idPrevNextLayout = ViewBindings.findChildViewById(rootView, id);
      if (idPrevNextLayout == null) {
        break missingId;
      }

      id = R.id.id_tv_next;
      TextView idTvNext = ViewBindings.findChildViewById(rootView, id);
      if (idTvNext == null) {
        break missingId;
      }

      id = R.id.id_tv_previous;
      TextView idTvPrevious = ViewBindings.findChildViewById(rootView, id);
      if (idTvPrevious == null) {
        break missingId;
      }

      id = R.id.iv_source_icon;
      AppCompatImageView ivSourceIcon = ViewBindings.findChildViewById(rootView, id);
      if (ivSourceIcon == null) {
        break missingId;
      }

      id = R.id.rv_device_browser;
      RecyclerView rvDeviceBrowser = ViewBindings.findChildViewById(rootView, id);
      if (rvDeviceBrowser == null) {
        break missingId;
      }

      id = R.id.toolbar;
      Toolbar toolbar = ViewBindings.findChildViewById(rootView, id);
      if (toolbar == null) {
        break missingId;
      }

      id = R.id.tv_no_data;
      AppCompatTextView tvNoData = ViewBindings.findChildViewById(rootView, id);
      if (tvNoData == null) {
        break missingId;
      }

      return new CtActivityDeviceBrowserBinding((RelativeLayout) rootView, browserTitle, ibBack,
          ibHome, binding_idMusicWidget, idPrevNextLayout, idTvNext, idTvPrevious, ivSourceIcon,
          rvDeviceBrowser, toolbar, tvNoData);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
