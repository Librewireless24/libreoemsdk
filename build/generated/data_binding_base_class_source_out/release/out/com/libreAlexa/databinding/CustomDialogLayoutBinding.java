// Generated by view binder compiler. Do not edit!
package com.libreAlexa.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.libreAlexa.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class CustomDialogLayoutBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final AppCompatButton btnAccept;

  @NonNull
  public final AppCompatButton btnSkip;

  @NonNull
  public final AppCompatEditText edtSpeakerName;

  @NonNull
  public final LinearLayout layHeader;

  @NonNull
  public final AppCompatTextView txtHeader;

  private CustomDialogLayoutBinding(@NonNull ConstraintLayout rootView,
      @NonNull AppCompatButton btnAccept, @NonNull AppCompatButton btnSkip,
      @NonNull AppCompatEditText edtSpeakerName, @NonNull LinearLayout layHeader,
      @NonNull AppCompatTextView txtHeader) {
    this.rootView = rootView;
    this.btnAccept = btnAccept;
    this.btnSkip = btnSkip;
    this.edtSpeakerName = edtSpeakerName;
    this.layHeader = layHeader;
    this.txtHeader = txtHeader;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static CustomDialogLayoutBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static CustomDialogLayoutBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.custom_dialog_layout, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static CustomDialogLayoutBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btn_accept;
      AppCompatButton btnAccept = ViewBindings.findChildViewById(rootView, id);
      if (btnAccept == null) {
        break missingId;
      }

      id = R.id.btn_skip;
      AppCompatButton btnSkip = ViewBindings.findChildViewById(rootView, id);
      if (btnSkip == null) {
        break missingId;
      }

      id = R.id.edt_speaker_Name;
      AppCompatEditText edtSpeakerName = ViewBindings.findChildViewById(rootView, id);
      if (edtSpeakerName == null) {
        break missingId;
      }

      id = R.id.lay_Header;
      LinearLayout layHeader = ViewBindings.findChildViewById(rootView, id);
      if (layHeader == null) {
        break missingId;
      }

      id = R.id.txt_header;
      AppCompatTextView txtHeader = ViewBindings.findChildViewById(rootView, id);
      if (txtHeader == null) {
        break missingId;
      }

      return new CustomDialogLayoutBinding((ConstraintLayout) rootView, btnAccept, btnSkip,
          edtSpeakerName, layHeader, txtHeader);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
