package com.duke.android.ufowatch.ui.tablet;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.duke.android.ufowatch.R;
import com.duke.android.ufowatch.UfoWatchMain;
import com.duke.android.ufowatch.common.SharedConstants;
import com.duke.android.ufowatch.common.Util;

/**
 * A fragment that displays a WebView.
 * <p>
 * The WebView is automically paused or resumed when the Fragment is paused or
 * resumed.
 */
@TargetApi(11)
public class UfoWebViewFrag extends Fragment {

	public static final String TAG = "UfoWebViewFrag";

	private WebView mWebView;
	private String mUrl;
	private String mTitle;

	public static UfoWebViewFrag newInstance() {
		// Create a new fragment instance
		UfoWebViewFrag ufoWebViewFrag = new UfoWebViewFrag();
		return ufoWebViewFrag;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = this.getArguments();
		mUrl = extras != null ? extras.getString(SharedConstants.KEY_URL) : null;
		mTitle = extras != null ? extras.getString(SharedConstants.KEY_TITLE) : null;

		if (savedInstanceState != null) {
			mUrl = (String) savedInstanceState.getString(SharedConstants.KEY_URL);
			mTitle = (String) savedInstanceState.getString(SharedConstants.KEY_TITLE);
		}

		setHasOptionsMenu(true);

	}

	/**
	 * Called to instantiate the view. Creates and returns the WebView.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Log.d(getClass().getSimpleName(), "onCreateView");
		if (mWebView != null) {
			mWebView.destroy();
		}

		// get the activity webview
		mWebView = ((UfoWatchMain) getActivity()).getWebView(mUrl);
		// mIsWebViewAvailable = true;
		return mWebView;
	}

	@Override
	public void onStart() {
		super.onStart();
		// Log.d(getClass().getSimpleName(), "onStart");

		mWebView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				((UfoWatchMain) getActivity()).getActionBar().setTitle(getResources().getString(R.string.msg_loading));
				getActivity().setProgress(progress * 100);
				if (progress == 100)
					getActivity().getActionBar().setTitle(mTitle);
			}

		});
		// Log.d(TAG, "onStart  mWebView.loadUrl: " + mUrl);
		mWebView.loadUrl(mUrl);

	}

	/**
	 * Called when the fragment is no longer resumed. Pauses the WebView.
	 */
	@Override
	public void onPause() {
		super.onPause();
		// Log.d(TAG, "onPause");
		mWebView.onPause();
		((UfoWatchMain) getActivity()).setmDisplayShareOption(false);
		((UfoWatchMain) getActivity()).invalidateOptionsMenu();

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putString(SharedConstants.KEY_URL, mUrl);
		savedInstanceState.putString(SharedConstants.KEY_TITLE, mTitle);
	}

	@SuppressLint("InlinedApi")
	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		if (SharedConstants.ID_MENU_SHARE == item.getItemId()) {
			// SHARE email etc
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
			// Title
			intent.putExtra(Intent.EXTRA_SUBJECT, mTitle);
			// URL
			intent.putExtra(Intent.EXTRA_TEXT, mUrl);
			startActivity(Intent.createChooser(intent, getResources().getString(R.string.msg_share_web_page)));
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Called when the fragment is visible to the user and actively running.
	 * Resumes the WebView.
	 */
	@Override
	public void onResume() {
		super.onResume();
		// Log.d(this.TAG, "onResume");
		// if a video player is being used
		// then add the Share, YouTube and FullScreen menu options to the action
		// menu
		mWebView.onResume();

		((UfoWatchMain) getActivity()).setmDisplayShareOption(true);
		((UfoWatchMain) getActivity()).setmDisplayYouTubeOption(false);
		((UfoWatchMain) getActivity()).setmDisplayVideoFullScreenOption(false);
		((UfoWatchMain) getActivity()).setmDisplayWebViewFullScreenOption(true);
		((UfoWatchMain) getActivity()).invalidateOptionsMenu();

	}

	/**
	 * Called when the WebView has been detached from the fragment. The WebView
	 * is no longer available after this time.
	 */
	@Override
	public void onDestroyView() {
		Log.d(TAG, "onDestroyView");
		super.onDestroyView();
		if (mWebView != null) {
			try {
				mWebView.clearCache(true);
				mWebView.destroyDrawingCache();
			} catch (Throwable mayHappen) {
			};

		}
	}

	public void clearWebView() {
		if (mWebView != null) {
			try {
				mWebView.clearCache(true);
				mWebView.destroyDrawingCache();
			} catch (Throwable mayHappen) {
			};

		}
	}

	/**
	 * Called when the fragment is no longer in use. Destroys the internal state
	 * of the WebView.
	 */
	@Override
	public void onDestroy() {
		// Log.d(getClass().getSimpleName(), "onDestroy1");
		if (mWebView != null) {
			mWebView = null;
		}
		// Log.d(getClass().getSimpleName(), "onDestroy2");
		super.onDestroy();
	}

	/**
	 * Gets called by the BusStationMain activity to notify the webview that
	 * there was a back button selected that hasn't been handled. If the webview
	 * can go back in history, it will go back and return true otherwise it will
	 * return false
	 */
	public boolean onBackButtonDown() {
		if (mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		} else {
			return false;
		}
	}

	/*
	 * Reload the webview with the new url
	 */
	public void refreshWebView(String url) {
		mWebView.loadUrl(url);
	}

}