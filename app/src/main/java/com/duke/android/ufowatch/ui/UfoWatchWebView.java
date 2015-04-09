package com.duke.android.ufowatch.ui;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.duke.android.ufowatch.R;
import com.duke.android.ufowatch.common.SharedConstants;
import com.duke.android.ufowatch.common.Util;

/**
 * A fragment that displays a WebView.
 * <p>
 * The WebView is automically paused or resumed when the Fragment is paused or
 * resumed.
 */
public class UfoWatchWebView extends Activity {

	public static final String TAG = "UfoWatchWebView";

	private WebView mWebView;
	private String mTitle;
	private String mUrl;
	private BroadcastReceiver mBroadcastReceiver;
	private final Activity thisActivity = this;

	/**
	 * Called to instantiate the view. Creates and returns the WebView.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Log.d(TAG, "onCreateView");
		this.getWindow().requestFeature(Window.FEATURE_PROGRESS);

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(SharedConstants.ACTION_RETURN_HOME);
		mBroadcastReceiver = Util.createBroadcastReceiver(this);
		registerReceiver(mBroadcastReceiver, intentFilter);
		this.getActionBar().setHomeButtonEnabled(true);

	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onStart() {
		super.onStart();
		// Log.d(TAG, "onStart");

		// get the stops criteria
		Bundle extras = this.getIntent().getExtras();
		mUrl = extras != null ? extras.getString(SharedConstants.KEY_URL) : null;
		mTitle = extras != null ? extras.getString(SharedConstants.KEY_TITLE) : null;

		setContentView(R.layout.ufo_watch_webview);

		// Makes Progress bar Visible
		getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);

		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				// Make the bar disappear after URL is loaded, and changes
				// string to Loading...
				thisActivity.setTitle(getResources().getString(R.string.msg_loading) + " " + progress + "%");

				// Return the app name after finish loading
				if (progress == 100)
					thisActivity.setTitle(mTitle);
			}
		});

		// mWebView.getSettings().setDefaultTextEncodingName("utf-8");
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.setWebViewClient(new MyWebViewClient());
		Log.d(TAG, "onStart loading URL: " + mUrl);
		mWebView.loadUrl(mUrl);

	}

	private class MyWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {

			// view.getTitle()
			Log.d(TAG, "URL:" + url);
			Log.d(TAG, "TITLE:" + view.getTitle());
			String message = null;
			if (url.startsWith(SharedConstants.URL_OAUTH2_APPROVAL)) {
				try {

					String title = view.getTitle();
					// permission has been granted
					if (title.indexOf("code=") != -1) {

						String code = extractCodeFromTitle(title);
						// Log.d(TAG, "CODE:" + code);

						String[] codes = new String[]{code};
						new GetAccessTokensTask().execute(codes);
						message = "Access granted.";
					} else if (title.indexOf("denied") != -1) { // user has
																// denied access
						Util.removeYouTubeAccess(thisActivity);
						message = "Access not allowed.";
					} else if (title.indexOf("error=") != -1) {
						// do nothing?
					}

				} finally {
					// nothing
				}

				finish();
				if (message != null) {
					Util.showCenteredToast(thisActivity, message);
				}

			}

		}
	}

	public String extractCodeFromTitle(String title) {
		return title.substring(title.indexOf("code=") + 5, title.length());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.share_options, menu);
		return true;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("InlinedApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		Intent intent = null;

		if (R.id.about == item.getItemId()) {
			AlertDialog alertDialog = Util.createDialogAbout(this);
			alertDialog.show();
		} else if (R.id.share == item.getItemId()) {

			intent = new Intent(android.content.Intent.ACTION_SEND);
			intent.setType("text/plain");
			// This constant was deprecated in API level 21. (Lollipop 5.0)
			// As of API 21 this performs identically to
			// FLAG_ACTIVITY_NEW_DOCUMENT
			// which should be used instead of this.
			if (!Util.isLollipopOrAbove()) {
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			} else {
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
			}
			intent.putExtra(Intent.EXTRA_SUBJECT, mTitle);
			intent.putExtra(Intent.EXTRA_TEXT, mUrl);
			startActivity(Intent.createChooser(intent, getResources().getString(R.string.msg_share_web_page)));
		} else if (android.R.id.home == item.getItemId()) {
			intent = new Intent();
			intent.setAction(SharedConstants.ACTION_RETURN_HOME);
			sendBroadcast(intent);
		}
		return true;

	}

	/**
	 * Called when the fragment is visible to the user and actively running.
	 * Resumes the WebView.
	 */
	@Override
	public void onPause() {
		super.onPause();
		// Log.d(TAG, "onPause");
		// Stops any video that may be playing
		try {
			Class.forName("android.webkit.WebView").getMethod("onPause", (Class[]) null)
					.invoke(mWebView, (Object[]) null);

		} catch (ClassNotFoundException cnfe) {
			//
		} catch (NoSuchMethodException nsme) {
			//
		} catch (InvocationTargetException ite) {
			//
		} catch (IllegalAccessException iae) {
			//
		}
	}

	/**
	 * Called when the fragment is no longer resumed. Pauses the WebView.
	 */
	@Override
	public void onResume() {
		// Log.d(TAG, "onResume");
		super.onResume();
	}

	/**
	 * Called when the fragment is no longer in use. Destroys the internal state
	 * of the WebView.
	 */
	@Override
	public void onDestroy() {
		// Log.d(TAG, "onDestroy2");
		super.onDestroy();
		unregisterReceiver(mBroadcastReceiver);

	}

	private class GetAccessTokensTask extends AsyncTask<String, Void, JSONObject> {

		// can use UI thread here
		protected void onPreExecute() {
		}

		// done on worker thread (separate from UI thread)
		protected JSONObject doInBackground(String... codes) {
			JSONObject jasonObj = Util.getAuthorizationTokens(codes[0], thisActivity);
			Log.d(TAG, "Result Tokens:" + jasonObj.toString());

			// If tokens retrieved, then set the channel id of the user
			String accessToken = null;
			if (!jasonObj.toString().contains("error")) {
				try {
					accessToken = jasonObj.getString("access_token");
					Util.putSharedPreferenceString(thisActivity, SharedConstants.KEY_ACCESS_TOKEN, accessToken);
					Log.d(TAG, "GetAccessTokensTask doInBackground: Calling setUsersChannelId");
					Util.storeUsersChannelId(thisActivity);
				} catch (JSONException e) {
					Log.e(TAG, "GetAccessTokensTask doInBackground: ERROR (retrieving access token)");
				} catch (IOException e) {

					Log.e(TAG, "GetAccessTokensTask doInBackground: ERROR saving user channelId. " + e);
				}
			}
			return jasonObj;
		}

		// can use UI thread here
		protected void onPostExecute(final JSONObject jsonObj) {
			if (jsonObj != null) {

				try {

					if (!jsonObj.toString().contains("error")) {
						String accessToken = jsonObj.getString("access_token");
						String refreshToken = jsonObj.getString("refresh_token");
						Log.d(TAG, "Access Token:" + accessToken);
						Log.d(TAG, "Refresh Token:" + refreshToken);
						Util.putSharedPreferenceString(getBaseContext(), SharedConstants.KEY_ACCESS_TOKEN, accessToken);
						Util.putSharedPreferenceString(getBaseContext(), SharedConstants.KEY_REFRESH_TOKEN,
								refreshToken);
					} else {
						Log.e(TAG, "Authorization error:" + jsonObj.getString("error"));
					}
				} catch (JSONException e) {
					Log.e(TAG, "Authorization error:" + e.getMessage());
					// e.printStackTrace();
				}

			}

		}

	}// end GetYouTubeVideosTask

}