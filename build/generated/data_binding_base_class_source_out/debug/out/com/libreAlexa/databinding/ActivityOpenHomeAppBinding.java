// Generated by view binder compiler. Do not edit!
package com.libreAlexa.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.libreAlexa.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityOpenHomeAppBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final AppCompatButton btnOpenHomeApp;

  @NonNull
  public final AppCompatButton btnSkip;

  @NonNull
  public final AppCompatImageView imageView;

  @NonNull
  public final AppCompatImageView imgWorkBadge;

  /**
   * This binding is not available in all configurations.
   * <p>
   * Present:
   * <ul>
   *   <li>layout/</li>
   * </ul>
   *
   * Absent:
   * <ul>
   *   <li>layout-sw600dp/</li>
   * </ul>
   */
  @Nullable
  public final LinearLayout layButtons;

  @NonNull
  public final RelativeLayout layLoader;

  @NonNull
  public final ConstraintLayout layParent;

  @NonNull
  public final ProgressBar progressBar;

  /**
   * This binding is not available in all configurations.
   * <p>
   * Present:
   * <ul>
   *   <li>layout/</li>
   * </ul>
   *
   * Absent:
   * <ul>
   *   <li>layout-sw600dp/</li>
   * </ul>
   */
  @Nullable
  public final ScrollView scrollview;

  @NonNull
  public final AppCompatTextView textView;

  @NonNull
  public final AppCompatTextView txtSetUpVoice;

  private ActivityOpenHomeAppBinding(@NonNull ConstraintLayout rootView,
      @NonNull AppCompatButton btnOpenHomeApp, @NonNull AppCompatButton btnSkip,
      @NonNull AppCompatImageView imageView, @NonNull AppCompatImageView imgWorkBadge,
      @Nullable LinearLayout layButtons, @NonNull RelativeLayout layLoader,
      @NonNull ConstraintLayout layParent, @NonNull ProgressBar progressBar,
      @Nullable ScrollView scrollview, @NonNull AppCompatTextView textView,
      @NonNull AppCompatTextView txtSetUpVoice) {
    this.rootView = rootView;
    this.btnOpenHomeApp = btnOpenHomeApp;
    this.btnSkip = btnSkip;
    this.imageView = imageView;
    this.imgWorkBadge = imgWorkBadge;
    this.layButtons = layButtons;
    this.layLoader = layLoader;
    this.layParent = layParent;
    this.progressBar = progressBar;
    this.scrollview = scrollview;
    this.textView = textView;
    this.txtSetUpVoice = txtSetUpVoice;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityOpenHomeAppBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityOpenHomeAppBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_open_home_app, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityOpenHomeAppBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btn_openHomeApp;
      AppCompatButton btnOpenHomeApp = ViewBindings.findChildViewById(rootView, id);
      if (btnOpenHomeApp == null) {
        break missingId;
      }

      id = R.id.btn_skip;
      AppCompatButton btnSkip = ViewBindings.findChildViewById(rootView, id);
      if (btnSkip == null) {
        break missingId;
      }

      id = R.id.imageView;
      AppCompatImageView imageView = ViewBindings.findChildViewById(rootView, id);
      if (imageView == null) {
        break missingId;
      }

      id = R.id.img_workBadge;
      AppCompatImageView imgWorkBadge = ViewBindings.findChildViewById(rootView, id);
      if (imgWorkBadge == null) {
        break missingId;
      }

      id = R.id.lay_buttons;
      LinearLayout layButtons = ViewBindings.findChildViewById(rootView, id);

      id = R.id.lay_Loader;
      RelativeLayout layLoader = ViewBindings.findChildViewById(rootView, id);
      if (layLoader == null) {
        break missingId;
      }

      id = R.id.lay_parent;
      ConstraintLayout layParent = ViewBindings.findChildViewById(rootView, id);
      if (layParent == null) {
        break missingId;
      }

      id = R.id.progress_bar;
      ProgressBar progressBar = ViewBindings.findChildViewById(rootView, id);
      if (progressBar == null) {
        break missingId;
      }

      id = R.id.scrollview;
      ScrollView scrollview = ViewBindings.findChildViewById(rootView, id);

      id = R.id.textView;
      AppCompatTextView textView = ViewBindings.findChildViewById(rootView, id);
      if (textView == null) {
        break missingId;
      }

      id = R.id.txt_setUpVoice;
      AppCompatTextView txtSetUpVoice = ViewBindings.findChildViewById(rootView, id);
      if (txtSetUpVoice == null) {
        break missingId;
      }

      return new ActivityOpenHomeAppBinding((ConstraintLayout) rootView, btnOpenHomeApp, btnSkip,
          imageView, imgWorkBadge, layButtons, layLoader, layParent, progressBar, scrollview,
          textView, txtSetUpVoice);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
