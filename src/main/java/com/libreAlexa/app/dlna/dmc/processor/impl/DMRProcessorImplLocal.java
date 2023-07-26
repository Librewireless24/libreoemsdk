package com.libreAlexa.app.dlna.dmc.processor.impl;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import com.libreAlexa.app.dlna.dmc.processor.interfaces.DMRProcessor;
import com.libreAlexa.app.dlna.dmc.processor.upnp.LoadLocalContentService;
import com.libreAlexa.util.LibreLogger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.fourthline.cling.model.ModelUtil;

public class DMRProcessorImplLocal implements DMRProcessor {
	private static final int UPDATE_INTERVAL = 1000;
	private boolean isRunning = true;
	private MediaPlayer mediaPlayer;
	private AudioManager audioManager;
	private final List<DMRProcessorListener> dmrProcessorListeners;
	String TAG = DMRProcessorImplLocal.class.getSimpleName();
	public DMRProcessorImplLocal(AudioManager audioManager) {
		LibreLogger.d(TAG,"DMRProcessorImplLocal audioManager");
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


		OnCompletionListener mediaOnCompletionListener = new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				LibreLogger.d(TAG,"DMRProcessorImplLocal onCompletion");

				fireOnPlayCompletedEvent();
			}
		};
		mediaPlayer.setOnCompletionListener(mediaOnCompletionListener);


		OnErrorListener mediaOnErrorListener = new OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {

				if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
					LibreLogger.d(TAG, "Media Error, Server Died " + extra);
				} else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
					LibreLogger.d(TAG, "Media Error, Error Unknown " + extra);
				}
				return false;
			}
		};
		mediaPlayer.setOnErrorListener(mediaOnErrorListener);


		OnPreparedListener mediaOnPreparedListener = new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				LibreLogger.d(TAG,"DMRProcessorImplLocal onPrepared "+ Arrays.toString(mp.getTrackInfo()));

				int position = mp.getCurrentPosition() / 1000;
				int duration = mp.getDuration() / 1000;
				fireUpdatePositionEvent(position, duration);

				mp.start();
				fireOnPlayingEvent();
			}
		};
		mediaPlayer.setOnPreparedListener(mediaOnPreparedListener);
		
		this.audioManager = audioManager;
		dmrProcessorListeners = new ArrayList<>();
		Thread m_updateThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (isRunning) {
					try {
						if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
							Thread.sleep(UPDATE_INTERVAL);
							continue;
						}

						int currentPostion = mediaPlayer.getCurrentPosition() / 1000;
						int duration = mediaPlayer.getDuration() / 1000;
						fireUpdatePositionEvent(currentPostion, duration);
						Thread.sleep(UPDATE_INTERVAL);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		m_updateThread.start();
	}

	@Override
	public void setURI(String uri, String musicInfo) {
		LibreLogger.d(TAG,"setURI "+uri+" musicInfo "+musicInfo);

		try {
			fireOnSetURIEvent();
			mediaPlayer.reset();
			mediaPlayer.setDataSource(uri);
			mediaPlayer.prepareAsync();
		} catch (IllegalArgumentException e) {
			LibreLogger.d(TAG, "play on local failed, illegal url");
		} catch (SecurityException e) {
			LibreLogger.d(TAG, "play on local failed, security issue");
		} catch (IllegalStateException e) {
			LibreLogger.d(TAG, "play on local failed, illegal device state");
		} catch (IOException e) {
			LibreLogger.d(TAG, "play on local failed, io exception");
		}
	}

	@Override
	public void play() {
		LibreLogger.d(TAG,"play");

		mediaPlayer.start();
	}

	@Override
	public void pause() {
		LibreLogger.d(TAG,"pause");

		mediaPlayer.pause();
	}

	@Override
	public void stop() {
		LibreLogger.d(TAG,"stop");

		mediaPlayer.stop();
	}

	@Override
	public void seek(String position) {
		LibreLogger.d(TAG,"seek String position "+position);

		seek(ModelUtil.fromTimeString(position));
	}

	@Override
	public void seek(long position) {
		LibreLogger.d(TAG,"seek long position "+position);

		mediaPlayer.seekTo((int) (position * 1000));
	}

	@Override
	public void setVolume(int newVolume) {

		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0);
	}

	@Override
	public void addListener(DMRProcessorListener listener) {
		LibreLogger.d(TAG,"addListener2 "+listener.getClass().getSimpleName());

		synchronized (dmrProcessorListeners) {
			dmrProcessorListeners.add(listener);
		}
	}

	@Override
	public void removeListener(DMRProcessorListener listener) {
		LibreLogger.d(TAG,"removeListener "+listener.getClass().getSimpleName());

		synchronized (dmrProcessorListeners) {
			dmrProcessorListeners.remove(listener);
		}
	}

	@Override
	public void dispose() {

		isRunning = false;
		mediaPlayer.release();
		LibreLogger.d(TAG,"dispose mediaPlayer.release");
	}

	@Override
	public void reset() {

		mediaPlayer.reset();
		fireOnPausedEvent();
		LibreLogger.d(TAG,"dispose mediaPlayer.reset fireOnPausedEvent");
	}

	@Override
	public int getVolume() {

		return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	@Override
	public int getMaxVolume() {

		return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}

	private void fireUpdatePositionEvent(long position, long duration) {
		synchronized (dmrProcessorListeners) {
			for (DMRProcessorListener listener : dmrProcessorListeners) {
				listener.onUpdatePosition(position, duration);
			}
		}
	}

	
	
	@SuppressWarnings("unused")
	private void fireUpdateVolumeEvent(int currentVolume) {

		synchronized (dmrProcessorListeners) {
			for (DMRProcessorListener listener : dmrProcessorListeners) {
				listener.onUpdateVolume(currentVolume);
			}
		}
	}

	@SuppressWarnings("unused")
	private void fireOnStopedEvent() {
		synchronized (dmrProcessorListeners) {
			for (DMRProcessorListener listener : dmrProcessorListeners) {
				listener.onStoped();
			}
		}
	}

	private void fireOnPausedEvent() {
		synchronized (dmrProcessorListeners) {
			for (DMRProcessorListener listener : dmrProcessorListeners) {
				listener.onPaused();
			}
		}
	}

	private void fireOnPlayingEvent() {
		synchronized (dmrProcessorListeners) {
			for (DMRProcessorListener listener : dmrProcessorListeners) {
				listener.onPlaying();
			}
		}
	}
	
	private void fireOnPlayCompletedEvent() {
		synchronized (dmrProcessorListeners) {
			for (DMRProcessorListener listener : dmrProcessorListeners) {
				listener.onPlayCompleted();
			}
		}
	}
	
	private void fireOnSetURIEvent() {
		synchronized (dmrProcessorListeners) {
			for (DMRProcessorListener listener : dmrProcessorListeners) {
				listener.onSetURI();
			}
		}
	}

}
