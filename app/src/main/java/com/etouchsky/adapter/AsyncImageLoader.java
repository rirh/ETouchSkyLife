package com.etouchsky.adapter;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class AsyncImageLoader {
	private static final String TAG = "AsyncImageLoader";
	private HashMap<String, SoftReference<Drawable>> imageCache;

	public AsyncImageLoader() {
		imageCache = new HashMap<String, SoftReference<Drawable>>();
	}

	public Drawable loadDrawable(final String imageUrl,
								 final ImageCallback imageCallback) {
		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			Drawable drawable = softReference.get();
			if (drawable != null) {
				return drawable;
			}
		}
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {

				imageCallback.imageLoaded((Drawable) message.obj, imageUrl);

			}
		};
		new Thread() {
			@Override
			public void run() {

				try {
					Drawable drawable = loadImageFromUrl(imageUrl);
					if (drawable != null) {

//						BitmapDrawable bd = (BitmapDrawable) (Drawable) drawable;
//						Bitmap bm = bd.getBitmap();
//
//						String nameString = DownloaderImage.SDname(imageUrl);
//						DownloaderImage.writeImage(bm, nameString);
//
//						if (bm != null) {
//							bm=null;
//							nameString = null;
//						}
						imageCache.put(imageUrl, new SoftReference<Drawable>(
								drawable));
						Message message = handler.obtainMessage(0, drawable);
						handler.sendMessage(message);

					}

					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
		return null;
	}

	public static Drawable loadImageFromUrl(String url) {
		if (null == url || url.trim().length() == 0) {
			return null;
		}
		URL m;
		InputStream i = null;
		Drawable d;
		try {
			String t = new String(url.getBytes("UTF-8"), "ISO-8859-1");
			m = new URL(t);
			i = (InputStream) m.getContent();
			d = Drawable.createFromStream(i, "src");

		} catch (MalformedURLException e1) {
			Log.i(TAG, "e:" + e1);
			return null;
		} catch (IOException ioe) {
			Log.i(TAG, "e:" + ioe);
			return null;
		} catch (Exception e) {
			Log.i(TAG, "e:" + e);
			return null;
		}
		return d;
	}

	public interface ImageCallback {
		public void imageLoaded(Drawable imageDrawable, String imageUrl);
	}

}
