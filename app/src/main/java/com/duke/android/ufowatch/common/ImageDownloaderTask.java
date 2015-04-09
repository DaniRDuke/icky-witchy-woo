package com.duke.android.ufowatch.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import javax.net.ssl.HttpsURLConnection;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

import com.duke.android.ufowatch.R;

public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
	private final ImageView imageView;

	public ImageDownloaderTask(ImageView imageView) {
		this.imageView = imageView;
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		// params comes from the execute() call: params[0] is the url.
		return downloadBitmap(params[0]);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	// Once the image is downloaded, associates it to the imageView
	protected void onPostExecute(Bitmap bitmap) {
		if (isCancelled()) {
			bitmap = null;
		}

		if (imageView != null) {
			if (bitmap != null) {
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
		}
	}

	static Bitmap downloadBitmap(String url) {

		HttpsURLConnection urlConnection = null;
		InputStream inputStream = null;

		try {

			urlConnection = Util.createHttpsURLConnectionGet(url);

			// Connect
			urlConnection.connect();

			final int statusCode = urlConnection.getResponseCode();

			// ok status code
			if (statusCode != HttpURLConnection.HTTP_OK) {
				Log.w("ImageDownloader", "Error " + statusCode + " while retrieving bitmap from " + url);
				return null;
			}

			inputStream = urlConnection.getInputStream();

			Bitmap bitmap;

			bitmap = BitmapFactory.decodeStream(inputStream);

			return bitmap;

		} catch (IOException ioe) {
			// getRequest.abort();
			Log.w("ImageDownloader", "Error while retrieving bitmap from " + url);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					Log.w("ImageDownloader", "Unable to close inputstream");
				}
			}
			urlConnection.disconnect();

		}

		return null;

		// final AndroidHttpClient client =
		// AndroidHttpClient.newInstance("Android");
		//
		// final HttpGet getRequest = new HttpGet(url);
		// try {
		// HttpResponse response = client.execute(getRequest);
		// final int statusCode = response.getStatusLine().getStatusCode();
		// if (statusCode != HttpStatus.SC_OK) {
		// Log.w("ImageDownloader", "Error " + statusCode +
		// " while retrieving bitmap from " + url);
		// return null;
		// }
		//
		// HttpEntity entity = response.getEntity();
		// if (entity != null) {
		// InputStream inputStream = null;
		// Bitmap bitmap;
		// try {
		// inputStream = entity.getContent();
		//
		// bitmap = BitmapFactory.decodeStream(inputStream);
		//
		// return bitmap;
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
		// return null;
	}

}