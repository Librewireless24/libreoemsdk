// Generated by view binder compiler. Do not edit!
package com.libreAlexa.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.textfield.TextInputLayout;
import com.libreAlexa.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityIssuesReportBinding implements ViewBinding {
  @NonNull
  private final CoordinatorLayout rootView;

  @NonNull
  public final AppCompatImageButton btnBack;

  @NonNull
  public final AppCompatButton btnSubmitIssue;

  @NonNull
  public final AppCompatEditText edtIssueReport;

  @NonNull
  public final CardView layCard;

  @NonNull
  public final LinearLayout layHeader;

  @NonNull
  public final RelativeLayout layLoader;

  @NonNull
  public final RelativeLayout layMain;

  @NonNull
  public final TextInputLayout layTimeframe;

  @NonNull
  public final ProgressBar progressBar;

  @NonNull
  public final AppCompatTextView txtCharCount;

  @NonNull
  public final AppCompatTextView txtHeader;

  @NonNull
  public final AppCompatTextView txtHeading;

  @NonNull
  public final AppCompatTextView txtTimeFrameHeader;

  @NonNull
  public final AutoCompleteTextView txtTimeframe;

  private ActivityIssuesReportBinding(@NonNull CoordinatorLayout rootView,
      @NonNull AppCompatImageButton btnBack, @NonNull AppCompatButton btnSubmitIssue,
      @NonNull AppCompatEditText edtIssueReport, @NonNull CardView layCard,
      @NonNull LinearLayout layHeader, @NonNull RelativeLayout layLoader,
      @NonNull RelativeLayout layMain, @NonNull TextInputLayout layTimeframe,
      @NonNull ProgressBar progressBar, @NonNull AppCompatTextView txtCharCount,
      @NonNull AppCompatTextView txtHeader, @NonNull AppCompatTextView txtHeading,
      @NonNull AppCompatTextView txtTimeFrameHeader, @NonNull AutoCompleteTextView txtTimeframe) {
    this.rootView = rootView;
    this.btnBack = btnBack;
    this.btnSubmitIssue = btnSubmitIssue;
    this.edtIssueReport = edtIssueReport;
    this.layCard = layCard;
    this.layHeader = layHeader;
    this.layLoader = layLoader;
    this.layMain = layMain;
    this.layTimeframe = layTimeframe;
    this.progressBar = progressBar;
    this.txtCharCount = txtCharCount;
    this.txtHeader = txtHeader;
    this.txtHeading = txtHeading;
    this.txtTimeFrameHeader = txtTimeFrameHeader;
    this.txtTimeframe = txtTimeframe;
  }

  @Override
  @NonNull
  public CoordinatorLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityIssuesReportBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityIssuesReportBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_issues_report, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityIssuesReportBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btn_back;
      AppCompatImageButton btnBack = ViewBindings.findChildViewById(rootView, id);
      if (btnBack == null) {
        break missingId;
      }

      id = R.id.btn_submit_issue;
      AppCompatButton btnSubmitIssue = ViewBindings.findChildViewById(rootView, id);
      if (btnSubmitIssue == null) {
        break missingId;
      }

      id = R.id.edt_issueReport;
      AppCompatEditText edtIssueReport = ViewBindings.findChildViewById(rootView, id);
      if (edtIssueReport == null) {
        break missingId;
      }

      id = R.id.lay_card;
      CardView layCard = ViewBindings.findChildViewById(rootView, id);
      if (layCard == null) {
        break missingId;
      }

      id = R.id.lay_Header;
      LinearLayout layHeader = ViewBindings.findChildViewById(rootView, id);
      if (layHeader == null) {
        break missingId;
      }

      id = R.id.lay_Loader;
      RelativeLayout layLoader = ViewBindings.findChildViewById(rootView, id);
      if (layLoader == null) {
        break missingId;
      }

      id = R.id.lay_main;
      RelativeLayout layMain = ViewBindings.findChildViewById(rootView, id);
      if (layMain == null) {
        break missingId;
      }

      id = R.id.lay_timeframe;
      TextInputLayout layTimeframe = ViewBindings.findChildViewById(rootView, id);
      if (layTimeframe == null) {
        break missingId;
      }

      id = R.id.progress_bar;
      ProgressBar progressBar = ViewBindings.findChildViewById(rootView, id);
      if (progressBar == null) {
        break missingId;
      }

      id = R.id.txt_charCount;
      AppCompatTextView txtCharCount = ViewBindings.findChildViewById(rootView, id);
      if (txtCharCount == null) {
        break missingId;
      }

      id = R.id.txt_header;
      AppCompatTextView txtHeader = ViewBindings.findChildViewById(rootView, id);
      if (txtHeader == null) {
        break missingId;
      }

      id = R.id.txt_heading;
      AppCompatTextView txtHeading = ViewBindings.findChildViewById(rootView, id);
      if (txtHeading == null) {
        break missingId;
      }

      id = R.id.txt_timeFrameHeader;
      AppCompatTextView txtTimeFrameHeader = ViewBindings.findChildViewById(rootView, id);
      if (txtTimeFrameHeader == null) {
        break missingId;
      }

      id = R.id.txt_timeframe;
      AutoCompleteTextView txtTimeframe = ViewBindings.findChildViewById(rootView, id);
      if (txtTimeframe == null) {
        break missingId;
      }

      return new ActivityIssuesReportBinding((CoordinatorLayout) rootView, btnBack, btnSubmitIssue,
          edtIssueReport, layCard, layHeader, layLoader, layMain, layTimeframe, progressBar,
          txtCharCount, txtHeader, txtHeading, txtTimeFrameHeader, txtTimeframe);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
