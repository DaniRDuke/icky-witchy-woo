package com.duke.android.ufowatch.ui.phone;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.duke.android.ufowatch.R;
import com.duke.android.ufowatch.common.ImageDownloaderTask;
import com.duke.android.ufowatch.common.SharedConstants;
import com.duke.android.ufowatch.common.Util;
import com.duke.android.ufowatch.domain.Video;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

public class VideoInfo extends Activity implements OnClickListener {

	public static final String TAG = "VideoInfo";

	private Context ctx = this;

	private Video mVideo;
	private BroadcastReceiver mBroadcastReceiver;
	private Dialog mListDialog;
	private TextView mTextViewTitle;
	private TextView mTextViewDescription;
	private TextView mTextViewStats;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Log.d(TAG, "onCreate");

		Bundle extras = getIntent().getExtras();
		mVideo = (Video) (extras != null ? (Video) extras.getSerializable(SharedConstants.KEY_VIDEO) : null);

		if (savedInstanceState != null) {
			mVideo = (Video) savedInstanceState.getSerializable(SharedConstants.KEY_VIDEO);
		}

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(SharedConstants.ACTION_RETURN_HOME);
		mBroadcastReceiver = Util.createBroadcastReceiver(this);
		registerReceiver(mBroadcastReceiver, intentFilter);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setTitle(getResources().getString(R.string.title_video_information));

	}

	@Override
	public void onStart() {
		super.onStart();
		// Log.d(TAG, "onStart");
		LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ScrollView ll = (ScrollView) li.inflate(R.layout.video_info, null);

		ImageButton btn = (ImageButton) ll.findViewById(R.id.videoInfoPlay);
		btn.setOnClickListener(this);
		btn.setBackgroundResource(R.drawable.tab_bg);

		mTextViewTitle = (TextView) ll.findViewById(R.id.videoInfoTitle);
		ImageView thumb = (ImageView) ll.findViewById(R.id.videoInfoImageView);
		new ImageDownloaderTask(thumb).execute(mVideo.getThumbUrl());
		mTextViewDescription = (TextView) ll.findViewById(R.id.videoInfoDescription);
		mTextViewStats = (TextView) ll.findViewById(R.id.videoInfoStats);

		setContentView(ll);

		if (mVideo != null) {
			new FillVideoTask(this).execute();
		}

	}

	public void onClick(View v) {

		if (v instanceof ImageButton) {
			if (v.getId() == R.id.videoInfoPlay) {

				YouTubeInitializationResult initResult = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(this);
				if (initResult.equals(YouTubeInitializationResult.SUCCESS)) {
					// The device has the Youtube API Service (the app) and you
					// are safe to launch it.
					Intent intent = YouTubeStandalonePlayer.createVideoIntent(this, SharedConstants.UFO_WATCH_API_KEY,
							mVideo.getId(), 0, true, false);
					startActivity(intent);
				} else {
					if (initResult.isUserRecoverableError()) {
						Dialog dialog = initResult.getErrorDialog(this, 1, new DialogInterface.OnCancelListener() {

							@Override
							public void onCancel(DialogInterface dialog) {
								// start a YouTube action intent
								try {
									Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://"
											+ mVideo.getId()));
									startActivity(intent);
								} catch (ActivityNotFoundException e) {
									Util.showCenteredToast(ctx, "No activity found to play video.");
									// Log.d(TAG,
									// "YouTubeInitializationResult: " +
									// initResult.name());
								}
							}
						});

						dialog.show();
					} else {
						Util.showCenteredToast(this, "YouTube initialization result: " + initResult.name());
						Log.d(TAG, "YouTubeInitializationResult: " + initResult.name());
					}
				}
			}

			return;
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.video_share_options, menu);
		return true;
	}

	@SuppressLint("InlinedApi")
	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		Intent intent = null;

		if (R.id.about == item.getItemId()) {
			AlertDialog alertDialog = Util.createDialogAbout(this);
			alertDialog.show();
		} else if (R.id.youtube == item.getItemId()) {
			// create the YouTube choices for what to do
			mListDialog = Util.createDialogYouTube(this, mVideo);
			mListDialog.show();
		} else if (R.id.share == item.getItemId()) {
			// SHARE email etc
			intent = new Intent(android.content.Intent.ACTION_SEND);
			intent.setType("text/plain");
			// FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET was deprecated in API level
			// 21. (Lollipop 5.0)
			// As of API 21 this performs identically to
			// FLAG_ACTIVITY_NEW_DOCUMENT
			// which should be used instead of this.
			if (!Util.isLollipopOrAbove()) {
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			} else {
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
			}
			intent.putExtra(Intent.EXTRA_SUBJECT, mVideo.getTitle());
			intent.putExtra(Intent.EXTRA_TEXT, this.getString(R.string.url_youtube_video) + mVideo.getId());
			startActivity(Intent.createChooser(intent, getResources().getString(R.string.msg_share_video)));
		} else if (android.R.id.home == item.getItemId()) {
			intent = new Intent();
			intent.setAction(SharedConstants.ACTION_RETURN_HOME);
			sendBroadcast(intent);
		}
		return true;

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putSerializable(SharedConstants.KEY_VIDEO, mVideo);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Log.d(TAG, "onDestroy");
		unregisterReceiver(mBroadcastReceiver);
	}

	private class FillVideoTask extends AsyncTask<Void, Void, String> {

		Context ctx = null;
		public FillVideoTask(Context context) {
			ctx = context;
		}

		// done on worker thread (separate from UI thread)
		protected String doInBackground(Void... arg) {

			// YouTube.Videos.List vidList = null;
			try {

				com.google.api.services.youtube.model.Video tubeVideo = Util.getVideo(mVideo.getId(), ctx, true);
				if (tubeVideo == null) {
					return null;
				}

				// Log.d(TAG, tubeVideo.toPrettyString());
				mVideo = Util.copyYoutubeVideoToUfoWatchVideo(mVideo, tubeVideo);

				return "";

			} catch (IOException e) {
				Log.e(TAG, e.toString());
			}

			return null;

		}

		// can use UI thread here
		protected void onPostExecute(String result) {

			if (result == null) {
				Util.showCenteredToast(ctx, getResources().getString(R.string.msg_error_video_missing));
				finish();
			} else {
				mTextViewTitle.setText(mVideo.getTitle());
				mTextViewDescription.setText(mVideo.getDescription());
				mTextViewStats.setText(mVideo.getChannelTitle() + " | " + mVideo.getUploadedDt()
						+ ((mVideo.getViewCount() != null) ? " | Views " + mVideo.getViewCount() : "") + " | Likes "
						+ mVideo.getLikes() + " | Dislikes " + mVideo.getDislikes());
			}
		}
	}

}
