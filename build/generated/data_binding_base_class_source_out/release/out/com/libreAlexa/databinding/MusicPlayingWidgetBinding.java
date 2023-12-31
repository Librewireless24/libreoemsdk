// Generated by view binder compiler. Do not edit!
package com.libreAlexa.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.cumulations.libreV2.ProgressButtonImageView;
import com.libreAlexa.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class MusicPlayingWidgetBinding implements ViewBinding {
  @NonNull
  private final FrameLayout rootView;

  @NonNull
  public final LinearLayout flMusicPlayWidget;

  @NonNull
  public final AppCompatImageButton ibAlexaAvsBtn;

  @NonNull
  public final AppCompatImageView ivAlbumArt;

  @NonNull
  public final AppCompatImageView ivCurrentSource;

  @NonNull
  public final ProgressButtonImageView ivPlayPause;

  @NonNull
  public final LinearLayout llPlayingLayout;

  @NonNull
  public final AppCompatSeekBar seekBarSong;

  @NonNull
  public final AppCompatTextView tvAlbumName;

  @NonNull
  public final AppCompatTextView tvAlexaListening;

  @NonNull
  public final AppCompatTextView tvTrackName;

  private MusicPlayingWidgetBinding(@NonNull FrameLayout rootView,
      @NonNull LinearLayout flMusicPlayWidget, @NonNull AppCompatImageButton ibAlexaAvsBtn,
      @NonNull AppCompatImageView ivAlbumArt, @NonNull AppCompatImageView ivCurrentSource,
      @NonNull ProgressButtonImageView ivPlayPause, @NonNull LinearLayout llPlayingLayout,
      @NonNull AppCompatSeekBar seekBarSong, @NonNull AppCompatTextView tvAlbumName,
      @NonNull AppCompatTextView tvAlexaListening, @NonNull AppCompatTextView tvTrackName) {
    this.rootView = rootView;
    this.flMusicPlayWidget = flMusicPlayWidget;
    this.ibAlexaAvsBtn = ibAlexaAvsBtn;
    this.ivAlbumArt = ivAlbumArt;
    this.ivCurrentSource = ivCurrentSource;
    this.ivPlayPause = ivPlayPause;
    this.llPlayingLayout = llPlayingLayout;
    this.seekBarSong = seekBarSong;
    this.tvAlbumName = tvAlbumName;
    this.tvAlexaListening = tvAlexaListening;
    this.tvTrackName = tvTrackName;
  }

  @Override
  @NonNull
  public FrameLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static MusicPlayingWidgetBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static MusicPlayingWidgetBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.music_playing_widget, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static MusicPlayingWidgetBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.fl_music_play_widget;
      LinearLayout flMusicPlayWidget = ViewBindings.findChildViewById(rootView, id);
      if (flMusicPlayWidget == null) {
        break missingId;
      }

      id = R.id.ib_alexa_avs_btn;
      AppCompatImageButton ibAlexaAvsBtn = ViewBindings.findChildViewById(rootView, id);
      if (ibAlexaAvsBtn == null) {
        break missingId;
      }

      id = R.id.iv_album_art;
      AppCompatImageView ivAlbumArt = ViewBindings.findChildViewById(rootView, id);
      if (ivAlbumArt == null) {
        break missingId;
      }

      id = R.id.iv_current_source;
      AppCompatImageView ivCurrentSource = ViewBindings.findChildViewById(rootView, id);
      if (ivCurrentSource == null) {
        break missingId;
      }

      id = R.id.iv_play_pause;
      ProgressButtonImageView ivPlayPause = ViewBindings.findChildViewById(rootView, id);
      if (ivPlayPause == null) {
        break missingId;
      }

      id = R.id.ll_playing_layout;
      LinearLayout llPlayingLayout = ViewBindings.findChildViewById(rootView, id);
      if (llPlayingLayout == null) {
        break missingId;
      }

      id = R.id.seek_bar_song;
      AppCompatSeekBar seekBarSong = ViewBindings.findChildViewById(rootView, id);
      if (seekBarSong == null) {
        break missingId;
      }

      id = R.id.tv_album_name;
      AppCompatTextView tvAlbumName = ViewBindings.findChildViewById(rootView, id);
      if (tvAlbumName == null) {
        break missingId;
      }

      id = R.id.tv_alexa_listening;
      AppCompatTextView tvAlexaListening = ViewBindings.findChildViewById(rootView, id);
      if (tvAlexaListening == null) {
        break missingId;
      }

      id = R.id.tv_track_name;
      AppCompatTextView tvTrackName = ViewBindings.findChildViewById(rootView, id);
      if (tvTrackName == null) {
        break missingId;
      }

      return new MusicPlayingWidgetBinding((FrameLayout) rootView, flMusicPlayWidget, ibAlexaAvsBtn,
          ivAlbumArt, ivCurrentSource, ivPlayPause, llPlayingLayout, seekBarSong, tvAlbumName,
          tvAlexaListening, tvTrackName);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
