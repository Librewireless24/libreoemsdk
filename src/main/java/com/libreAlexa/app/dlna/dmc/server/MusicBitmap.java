package com.libreAlexa.app.dlna.dmc.server;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.libreAlexa.R;
import com.libreAlexa.util.LibreLogger;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
//Can be used in future
public class MusicBitmap {
	private final static String TAG = MusicBitmap.class.getSimpleName();
	private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
	private static final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();

	public static Bitmap getBitmap(URI uri) {
		Bitmap bm = null;
		try {
			URL bmurl = uri.toURL();
			InputStream stream=bmurl.openStream();
			bm = BitmapFactory.decodeStream(stream);
			stream.close();


		} catch (Exception e) {

				e.printStackTrace();
			LibreLogger.d(TAG, "getBitmap failed:" + e.getMessage());
		}

		return bm;
	}
	
	public static Bitmap getBitmap(Context context, String mediaId) {
		//return null;
		if (mediaId != null) {
			int p = mediaId.indexOf(ContentTree.AUDIO_PREFIX) + ContentTree.AUDIO_PREFIX.length();
			CharSequence id = mediaId.subSequence(p, mediaId.length());
			Bitmap bm = null;
			Uri uri = Uri.parse("content://media/external/audio/media/" + id + "/albumart");
			ParcelFileDescriptor pfd = null;
			try {
				pfd = context.getContentResolver().openFileDescriptor(uri, "r");
				if (pfd != null) {
					FileDescriptor fd = pfd.getFileDescriptor();
					bm = BitmapFactory.decodeFileDescriptor(fd);
				}
			} catch (Exception e) {

				e.printStackTrace();
				LibreLogger.d(TAG, "getBitmap failed:" + e.getMessage());
			}
			finally {
				try {
					pfd.close();
				}
				catch (Throwable t) {}
			}
			return bm;
		}
		return null;
	}

	public static Bitmap getArtwork(Context context, long song_id,
			long album_id, boolean allowdefault) {
		if (album_id < 0) {
			// This is something that is not in the database, so get the album
			// art directly
			// from the file.
			if (song_id >= 0) {
				Bitmap bm = getArtworkFromFile(context, song_id, -1);
				if (bm != null) {
					return bm;
				}
			}
			if (allowdefault) {
				return getDefaultArtwork(context);
			}
			return null;
		}
		ContentResolver res = context.getContentResolver();
		Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
		if (uri != null) {
			InputStream in = null;
			try {
				in = res.openInputStream(uri);
				return BitmapFactory.decodeStream(in, null, sBitmapOptions);
			} catch (FileNotFoundException ex) {
				// The album art thumbnail does not actually exist. Maybe the
				// user deleted it, or
				// maybe it never existed to begin with.
				Bitmap bm = getArtworkFromFile(context, song_id, album_id);
				if (bm != null) {
					if (bm.getConfig() == null) {
						bm = bm.copy(Bitmap.Config.RGB_565, false);
						if (bm == null && allowdefault) {
							return getDefaultArtwork(context);
						}
					}
				} else if (allowdefault) {
					bm = getDefaultArtwork(context);
				}
				return bm;
			} finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch (IOException ex) {
				}
			}
		}

		return null;
	}

	private static Bitmap getArtworkFromFile(Context context, long songid,
			long albumid) {
		Bitmap bm = null;
//		byte[] art = null;
//		String path = null;
		if (albumid < 0 && songid < 0) {
			throw new IllegalArgumentException(
					"Must specify an album or a song id");
		}
		try {
			if (albumid < 0) {
				Uri uri = Uri.parse("content://media/external/audio/media/"
						+ songid + "/albumart");
				ParcelFileDescriptor pfd = context.getContentResolver()
						.openFileDescriptor(uri, "r");
				if (pfd != null) {
					FileDescriptor fd = pfd.getFileDescriptor();
					bm = BitmapFactory.decodeFileDescriptor(fd);
				}
			} else {
				Uri uri = ContentUris.withAppendedId(sArtworkUri, albumid);
				ParcelFileDescriptor pfd = context.getContentResolver()
						.openFileDescriptor(uri, "r");
				if (pfd != null) {
					FileDescriptor fd = pfd.getFileDescriptor();
					bm = BitmapFactory.decodeFileDescriptor(fd);
				}
			}
		} catch (FileNotFoundException ex) {

		}
//		if (bm != null) {
//			mCachedBit = bm;
//		}
		return bm;
	}

	@SuppressLint("ResourceType")
	private static Bitmap getDefaultArtwork(Context context) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		return BitmapFactory.decodeStream(context.getResources()
				.openRawResource(R.drawable.ic_launcher), null, opts);
	}

//	private static Bitmap mCachedBit = null;

}
