package com.duke.android.ufowatch.ui.adapter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import javax.net.ssl.HttpsURLConnection;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.duke.android.ufowatch.R;
import com.duke.android.ufowatch.common.Util;
import com.duke.android.ufowatch.domain.Library;
import com.duke.android.ufowatch.domain.Video;

public class VideoListAdapter2 extends BaseAdapter {

	String TAG = "VideoListAdapter2";

	// The list of videos to display
	private Library videos;

	// An inflator to use when creating rows
	private LayoutInflater mInflater;

	private LruCache<String, Bitmap> cache;

	@SuppressWarnings("unused")
	private Context ctx;

	/**
	 * @param context
	 *            this is the context that the list will be shown in - used to
	 *            create new list rows
	 * @param videos
	 *            this is a list of videos to display
	 */
	public VideoListAdapter2(Context context, Library videos) {
		this.ctx = context;
		this.videos = videos;
		this.mInflater = LayoutInflater.from(context);
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		int memoryClass = am.getMemoryClass() * 1024 * 1024;
		cache = new LruCache<String, Bitmap>(memoryClass);

	}

	@Override
	public int getCount() {
		return videos.size();
	}

	@Override
	public Object getItem(int position) {
		return videos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// If convertView wasn't null it means we have already set it to our
		// list_item_user_video so no need to do it again
		if (convertView == null) {
			// This is the layout we are using for each row in our list
			convertView = mInflater.inflate(R.layout.list_item_user_video_2, null);
		}

		TextView title = (TextView) convertView.findViewById(R.id.userVideoTitle);

		// Get a single video from our list
		Video video = videos.get(position);

		ImageView imageView = (ImageView) convertView.findViewById(R.id.userVideoThumbImageView);

		// NEW STUFF
		String url = video.getThumbUrl();

		if (url != null) {
			Log.d(TAG, "getView thumb url is not null");
			Bitmap image = (Bitmap) cache.get(url);
			if (image != null) {
				Log.d(TAG, "getView image is not null");
				imageView.setImageBitmap(image);
			} else {
				Log.d(TAG, "getView image is null");
				new ImageDownloaderTask(imageView).execute(url);
			}
		} else {
			Log.d(TAG, "getView thumb url is null");
			// use default image
			// This constant was deprecated in API level 21. (Lollipop 5.0)
			// As of API level 21 requires a Theme to be passed in (or null)
			if (Util.isLollipopOrAbove()) {
				imageView.setImageDrawable(imageView.getContext().getResources().getDrawable(R.drawable.youtube, null));
			} else {
				imageView.setImageDrawable(imageView.getContext().getResources().getDrawable(R.drawable.youtube));
			}

		}

		// Set the title for the list item
		title.setText(Util.truncateStringAddEllipsis(video.getTitle(), 64));

		TextView updateTime = (TextView) convertView.findViewById(R.id.userVideoUpdateTime);

		String otherInfo = "";

		if (video.getUploadedDt() != null) {
			otherInfo += video.getUploadedDt();
		}
		if (video.getChannelTitle() != null) {
			if (video.getUploadedDt() != null) {
				otherInfo += " | ";
			}
			otherInfo += video.getChannelTitle();
		}
		if (video.getAddedToPlaylistDt() != null) {
			if (video.getChannelTitle() != null) {
				otherInfo += " | ";
			}
			otherInfo += video.getAddedToPlaylistDt();
		}

		updateTime.setText(otherInfo);

		return convertView;
	}

	private class ImageDownloaderTask extends AsyncTask<String, Void, Integer> {
		private ImageView imageView;
		private Bitmap bitmap;

		public ImageDownloaderTask(ImageView imageView) {
			this.imageView = imageView;
		}

		@Override
		// Actual download method, run in the task thread
		protected Integer doInBackground(String... params) {
			// params comes from the execute() call: params[0] is the url.
			return downloadBitmap(params[0]);
		}

		@SuppressWarnings("deprecation")
		@SuppressLint("NewApi")
		@Override
		// Once the image is downloaded, associates it to the imageView
		protected void onPostExecute(Integer result) {
			if (isCancelled()) {
				Log.d(TAG, "onPostExecute is cancelled");
				bitmap = null;
			}
			Log.d(TAG, "onPostExecute result: " + result);
			Log.d(TAG, "onPostExecute bitmap is null? " + (bitmap == null));
			if (result != null && result == 1 && bitmap != null) {
				imageView.setImageBitmap(bitmap);
			} else {
				// use default image
				// This constant was deprecated in API level 21. (Lollipop 5.0)
				// As of API level 21 requires a Theme to be passed in (or null)
				if (Util.isLollipopOrAbove()) {
					imageView.setImageDrawable(imageView.getContext().getResources()
							.getDrawable(R.drawable.youtube, null));
				} else {
					imageView.setImageDrawable(imageView.getContext().getResources().getDrawable(R.drawable.youtube));
				}
			}
			super.onPostExecute(result);

		}

		protected Integer downloadBitmap(String url) {

			HttpsURLConnection urlConnection = null;
			InputStream inputStream = null;

			try {

				urlConnection = Util.createHttpsURLConnectionGet(url);

				// Connect
				urlConnection.connect();

				int statusCode = urlConnection.getResponseCode();

				// ok status code
				if (statusCode != HttpURLConnection.HTTP_OK) {
					return null;
				}

				bitmap = BitmapFactory.decodeStream(urlConnection.getInputStream());
				bitmap = createScaledBitmap(bitmap, 150, 150, true, false);

				if (bitmap != null) {
					cache.put(url, bitmap);
				} else {
					return 0;
				}
			} catch (IOException ioe) {
				// getRequest.abort();
				Log.w(TAG, "Error while retrieving bitmap from " + url);
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						Log.w("downloadBitmap", "Unable to close inputstream");
					}
				}
				urlConnection.disconnect();
			}

			return 1;

			// final AndroidHttpClient client =
			// AndroidHttpClient.newInstance("Android");
			//
			// final HttpGet getRequest = new HttpGet(url);
			// try {
			// HttpResponse response = client.execute(getRequest);
			// final int statusCode = response.getStatusLine().getStatusCode();
			// if (statusCode != HttpURLConnection.HTTP_OK) {
			// Log.w("ImageDownloader", "Error " + statusCode +
			// " while retrieving bitmap from " + url);
			// return null;
			// }
			//
			// HttpEntity entity = response.getEntity();
			// if (entity != null) {
			// InputStream inputStream = null;
			// byte[] bytes = null;
			// try {
			// inputStream = entity.getContent();
			//
			// bytes = inputStreamToByteArray(inputStream);
			//
			// bitmap = decodeFile(bytes, 150, 150, false);
			//
			// bitmap = createScaledBitmap(bitmap, 150, 150, true, false);
			//
			// if (bitmap != null) {
			// cache.put(url, bitmap);
			// } else {
			// return 0;
			// }
			// } finally {
			// if (inputStream != null) {
			// inputStream.close();
			// }
			// entity.consumeContent();
			// }
			// }
			// } catch (Exception e) {
			// getRequest.abort();
			// Log.w("ImageDownloader", "Error while retrieving bitmap from " +
			// url);
			// } finally {
			// if (client != null) {
			// client.close();
			// }
			//
			// }
			// return 1;
		}

		// protected Bitmap decodeFile(byte[] bytes, int dstWidth, int
		// dstHeight, boolean fitIt) {
		// Options options = new Options();
		// options.inJustDecodeBounds = true;
		// ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		// BitmapFactory.decodeStream(bais, null, options);
		// options.inJustDecodeBounds = false;
		// options.inSampleSize = calculateSampleSize(options.outWidth,
		// options.outHeight, dstWidth, dstHeight, fitIt);
		// bais = new ByteArrayInputStream(bytes);
		// Bitmap unscaledBitmap = BitmapFactory.decodeStream(bais, null,
		// options);
		// if (unscaledBitmap == null)
		// Log.e("ImageDownloader", "decodeFile - no unscaled bitmap");
		//
		// return unscaledBitmap;
		// }

		protected Bitmap createScaledBitmap(Bitmap unscaledBitmap, int dstWidth, int dstHeight, boolean cropIt,
				boolean fitIt) {
			Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight,
					cropIt);
			Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight,
					fitIt);
			Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(), Config.ARGB_8888);
			Canvas canvas = new Canvas(scaledBitmap);
			canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));

			if (scaledBitmap == null)
				Log.e("ImageDownloader", "createScaledBitmap - no scaled bitmap");

			return scaledBitmap;
		}

		protected Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight, boolean cropIt) {
			if (cropIt) {
				final float srcAspect = (float) srcWidth / (float) srcHeight;
				final float dstAspect = (float) dstWidth / (float) dstHeight;

				if (srcAspect > dstAspect) {
					final int srcRectWidth = (int) (srcHeight * dstAspect);
					final int srcRectLeft = (srcWidth - srcRectWidth) / 2;
					return new Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth, srcHeight);
				} else {
					final int srcRectHeight = (int) (srcWidth / dstAspect);
					final int scrRectTop = (int) (srcHeight - srcRectHeight) / 2;
					return new Rect(0, scrRectTop, srcWidth, scrRectTop + srcRectHeight);
				}
			} else {
				return new Rect(0, 0, srcWidth, srcHeight);
			}
		}

		protected Rect calculateDstRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight, boolean fitIt) {
			if (fitIt) {
				final float srcAspect = (float) srcWidth / (float) srcHeight;
				final float dstAspect = (float) dstWidth / (float) dstHeight;

				if (srcAspect > dstAspect) {
					return new Rect(0, 0, dstWidth, (int) (dstWidth / srcAspect));
				} else {
					return new Rect(0, 0, (int) (dstHeight * srcAspect), dstHeight);
				}
			} else {
				return new Rect(0, 0, dstWidth, dstHeight);
			}
		}

		protected int calculateSampleSize(int srcWidth, int srcHeight, int dstWidth, int dstHeight, boolean fitIt) {
			if (fitIt) {
				final float srcAspect = (float) srcWidth / (float) srcHeight;
				final float dstAspect = (float) dstWidth / (float) dstHeight;

				if (srcAspect > dstAspect) {
					return srcWidth / dstWidth;
				} else {
					return srcHeight / dstHeight;
				}
			} else {
				final float srcAspect = (float) srcWidth / (float) srcHeight;
				final float dstAspect = (float) dstWidth / (float) dstHeight;

				if (srcAspect > dstAspect) {
					return srcHeight / dstHeight;
				} else {
					return srcWidth / dstWidth;
				}
			}
		}

		protected byte[] inputStreamToByteArray(InputStream input) throws IOException {
			byte[] buffer = new byte[8192];
			int bytesRead;
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			while ((bytesRead = input.read(buffer)) != -1) {
				output.write(buffer, 0, bytesRead);
			}
			return output.toByteArray();
		}

	}

}