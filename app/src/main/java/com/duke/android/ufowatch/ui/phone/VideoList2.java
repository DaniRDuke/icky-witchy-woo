package com.duke.android.ufowatch.ui.phone;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duke.android.ufowatch.R;
import com.duke.android.ufowatch.common.SharedConstants;
import com.duke.android.ufowatch.common.Util;
import com.duke.android.ufowatch.domain.Library;
import com.duke.android.ufowatch.domain.Video;
import com.duke.android.ufowatch.ui.VideoClickListener;
import com.duke.android.ufowatch.ui.widget.VideoListView2;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.SearchListResponse;

public class VideoList2 extends Activity implements VideoClickListener, OnClickListener {

	public static final String TAG = "VideoList";

	// A reference to our list that will hold the video details
	private VideoListView2 mVideosListView;
	private ImageButton mBtnNext;
	private ImageButton mBtnPrev;
	private TextView mTvCounts;
	private TextView mTvPlayAll;
	private String mKeywords;
	private int mStartIndex;
	private String mPlaylistId;
	private String mNextPageToken;
	private String mPrevPageToken;
	private int mCurrentTotal = 0;
	private int mCurrentTotalDisplay = 0;
	private int mTotalResults = 0;
	private long mStartTime;
	private long mEndTime;
	private BroadcastReceiver mBroadcastReceiver;
	private Library mLib;
	// private List<Video> mVideoList;
	private String mError;
	private ProgressDialog mProgressDialog;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Log.d(TAG, "onCreate");

		Bundle extras = getIntent().getExtras();

		mPlaylistId = extras != null ? extras.getString(SharedConstants.KEY_PLAYLIST) : null;
		// Log.d(TAG, "Clicked playlistid AAA: " + mPlaylistId);
		mKeywords = extras != null ? extras.getString(SharedConstants.VIDEO_SEARCH_PARAM_NAME_KEYWORDS) : null;
		mStartIndex = extras != null ? extras.getInt(SharedConstants.VIDEO_SEARCH_PARAM_NAME_STARTINDEX,
				SharedConstants.FIRST_VIDEO_POSITION) : SharedConstants.FIRST_VIDEO_POSITION;
		mStartTime = extras != null ? extras.getLong(SharedConstants.VIDEO_SEARCH_PARAM_NAME_START_TIME) : null;
		mEndTime = extras != null ? extras.getLong(SharedConstants.VIDEO_SEARCH_PARAM_NAME_END_TIME) : null;

		if (savedInstanceState != null) {
			if (mPlaylistId == null) {
				mPlaylistId = savedInstanceState.getString(SharedConstants.KEY_PLAYLIST);
			}
			mKeywords = savedInstanceState.getString(SharedConstants.VIDEO_SEARCH_PARAM_NAME_KEYWORDS);
			mStartIndex = savedInstanceState.getInt(SharedConstants.VIDEO_SEARCH_PARAM_NAME_STARTINDEX,
					SharedConstants.FIRST_VIDEO_POSITION);
			mStartTime = savedInstanceState.getLong(SharedConstants.VIDEO_SEARCH_PARAM_NAME_START_TIME);
			mEndTime = savedInstanceState.getLong(SharedConstants.VIDEO_SEARCH_PARAM_NAME_END_TIME);
			mLib = (Library) savedInstanceState.getSerializable("LIBRARY");
			mCurrentTotalDisplay = savedInstanceState.getInt(SharedConstants.KEY_CURRENT_TOTAL_DISPLAY);
			mTotalResults = savedInstanceState.getInt(SharedConstants.KEY_TOTAL_RESULTS);
			mNextPageToken = savedInstanceState.getString(SharedConstants.KEY_NEXT_PAGE_TOKEN);
			mPrevPageToken = savedInstanceState.getString(SharedConstants.KEY_PREV_PAGE_TOKEN);
		}

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(SharedConstants.ACTION_RETURN_HOME);
		mBroadcastReceiver = Util.createBroadcastReceiver(this);
		registerReceiver(mBroadcastReceiver, intentFilter);
		this.getActionBar().setHomeButtonEnabled(true);

	}

	@Override
	public void onStart() {
		super.onStart();
		// Log.d(TAG, "onStart");

		LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout rl = (RelativeLayout) li.inflate(R.layout.video_list_view_2, null);

		mVideosListView = (VideoListView2) rl.findViewById(R.id.videosListView);
		// Here we are adding this activity as a listener for when any row in
		// the List is 'clicked'. The activity will be sent back the video that
		// has been pressed to do whatever it wants with in this case we will
		// retrieve the URL of the video and fire off an intent to view it
		mVideosListView.setOnVideoClickListener(this);

		mTvPlayAll = (TextView) rl.findViewById(R.id.playall);
		mTvPlayAll.setOnClickListener(this);
		mTvCounts = (TextView) rl.findViewById(R.id.counts);
		mTvCounts.setOnClickListener(this);
		mBtnNext = (ImageButton) rl.findViewById(R.id.next);
		mBtnNext.setOnClickListener(this);
		mBtnPrev = (ImageButton) rl.findViewById(R.id.prev);
		mBtnPrev.setOnClickListener(this);

		setContentView(rl);

		if (mPlaylistId != null) {
			mTvPlayAll.setVisibility(View.VISIBLE);
		} else {
			mTvPlayAll.setVisibility(View.GONE);
		}

		if (mLib != null) {
			if (mLib.size() == 1) {
				// Log.d(TAG, "Clicked playlistid A: " + mPlaylistId);
				// if only one, go directly to the VideoInfo
				Intent i = new Intent(this, VideoInfo.class);
				i.putExtra(SharedConstants.KEY_VIDEO, (Video) mLib.get(0));
				startActivity(i);
				finish();

			} else {
				mVideosListView.setVideos(mLib);
			}
		} else if (mPlaylistId != null) {
			new GetPlaylistVideosTask(this).execute();
		} else {
			new SearchVideosTask(this).execute();
		}

	}

	// This is the interface method that is called when a video in the listview
	// is clicked!
	@Override
	public void onVideoClicked(Video video) {
		Intent i = new Intent(this, VideoInfo.class);
		i.putExtra(SharedConstants.KEY_VIDEO, video);
		startActivity(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.standard_options, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		Intent intent = null;

		if (R.id.about == item.getItemId()) {
			AlertDialog alertDialog = Util.createDialogAbout(this);
			alertDialog.show();
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
		savedInstanceState.putString(SharedConstants.KEY_PLAYLIST, mPlaylistId);
		savedInstanceState.putString(SharedConstants.VIDEO_SEARCH_PARAM_NAME_KEYWORDS, mKeywords);
		savedInstanceState.putInt(SharedConstants.VIDEO_SEARCH_PARAM_NAME_STARTINDEX, mStartIndex);
		savedInstanceState.putLong(SharedConstants.VIDEO_SEARCH_PARAM_NAME_START_TIME, mStartTime);
		savedInstanceState.putLong(SharedConstants.VIDEO_SEARCH_PARAM_NAME_END_TIME, mEndTime);
		savedInstanceState.putSerializable("LIBRARY", mLib);
		savedInstanceState.putString(SharedConstants.KEY_NEXT_PAGE_TOKEN, mNextPageToken);
		savedInstanceState.putString(SharedConstants.KEY_PREV_PAGE_TOKEN, mPrevPageToken);
		savedInstanceState.putInt(SharedConstants.KEY_CURRENT_TOTAL_DISPLAY, mCurrentTotalDisplay);
		savedInstanceState.putInt(SharedConstants.KEY_TOTAL_RESULTS, mTotalResults);
	}

	// @Override
	// public void onStop() {
	// super.onStop();
	// // Log.d(TAG, "onStop");
	// }

	@Override
	public void onResume() {
		super.onResume();
		// Log.d(TAG, "onResume");
		setNextPrevHeaderButtons();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Log.d(TAG, "onDestroy");
		unregisterReceiver(mBroadcastReceiver);
	}

	public void setNextPrevHeaderButtons() {
		if (mNextPageToken != null) {
			mBtnNext.setTag(mNextPageToken);
			mBtnNext.setVisibility(View.VISIBLE);
		} else {
			mBtnNext.setVisibility(View.INVISIBLE);
		}
		if (mPrevPageToken != null) {
			mBtnPrev.setTag(mPrevPageToken);
			mBtnPrev.setVisibility(View.VISIBLE);
		} else {
			mBtnPrev.setVisibility(View.INVISIBLE);
		}
		mTvCounts.setText(mCurrentTotalDisplay + "/" + mTotalResults);

	}

	private class SearchVideosTask extends AsyncTask<Void, Void, Library> {

		Context ctx = null; // Add this and the constructor below

		public SearchVideosTask(Context context) {
			ctx = context;
		}
		// can use UI thread here
		protected void onPreExecute() {

			if (!isFinishing()) {
				mProgressDialog = ProgressDialog.show(VideoList2.this, null,
						getResources().getString(R.string.msg_retrieving_videos), true, true,
						new DialogInterface.OnCancelListener() {
							public void onCancel(DialogInterface dialog) {
								SearchVideosTask.this.cancel(true);
								finish();
							}
						});
			}
		}

		// done on worker thread (separate from UI thread)
		protected Library doInBackground(Void... arg) {
			Library videos = null;
			// ArrayList<Video> videos = null;

			mError = "";

			try {

				YouTube.Search.List search = Util.createSearchList(ctx, mNextPageToken, mPrevPageToken, mKeywords,
						SharedConstants.RESULTS_PER_PAGE_PHONE, mStartTime, mEndTime);

				SearchListResponse searchResponse = search.execute();
				// Log.d(TAG, searchResponse.toPrettyString());
				mTotalResults = searchResponse.getPageInfo().getTotalResults();

				if (mPrevPageToken != null) {
					mCurrentTotal -= searchResponse.getPageInfo().getResultsPerPage();
				} else {
					mCurrentTotal += searchResponse.getPageInfo().getResultsPerPage();
				}

				mCurrentTotalDisplay = mCurrentTotal;
				if (mCurrentTotalDisplay > mTotalResults) {
					mCurrentTotalDisplay = mTotalResults;
				}

				mNextPageToken = searchResponse.getNextPageToken();
				mPrevPageToken = searchResponse.getPrevPageToken();
				Log.d(TAG, searchResponse.toPrettyString());

				videos = Util.getVideosFromSearchResponse(searchResponse, ctx);

			} catch (IOException e) {
				Log.e("Unable to retrieve videos.", e.getMessage());
				mError = "Unable to retrieve videos.";
				return null;
			}

			return videos;

		}

		// can use UI thread here
		protected void onPostExecute(final Library videos) {
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}

			if (videos != null) {
				// marry the Videos up with the ListView or go to VideoInfo if
				// only one
				mLib = videos;
				if (!mLib.isEmpty()) {
					mVideosListView.setVideos(mLib);
					setNextPrevHeaderButtons();
				} else {
					Util.showCenteredToast(ctx, ctx.getResources().getString(R.string.msg_no_videos_found));

					finish();
				}

			} else {
				if (mError == null) {
					mError = "Unknown error";
				}
				Util.showCenteredToast(ctx, mError);

				finish();
			}
		}

	}// end SearchVideosTask

	private class GetPlaylistVideosTask extends AsyncTask<Void, Void, Library> {

		Context ctx = null; // Add this and the constructor below
		public GetPlaylistVideosTask(Context context) {
			ctx = context;
		}

		// can use UI thread here
		protected void onPreExecute() {
			if (!isFinishing()) {
				mProgressDialog = ProgressDialog.show(VideoList2.this, null,
						getResources().getString(R.string.msg_retrieving_videos), true, true,
						new DialogInterface.OnCancelListener() {
							public void onCancel(DialogInterface dialog) {
								GetPlaylistVideosTask.this.cancel(true);
								finish();
							}
						});
			}
		}

		// done on worker thread (separate from UI thread)
		protected Library doInBackground(Void... arg) {
			// Library lib = null;
			Library videos = null;

			YouTube youTube = Util.createYouTube(ctx);

			try {

				YouTube.PlaylistItems.List playlistItemList = Util.createPlaylistItemsList(youTube, mPlaylistId,
						mNextPageToken, mPrevPageToken, SharedConstants.RESULTS_PER_PAGE_PHONE);

				PlaylistItemListResponse resp = playlistItemList.execute();

				mTotalResults = resp.getPageInfo().getTotalResults();
				if (mPrevPageToken != null) {
					// this was a prev so subtract from the current total
					mCurrentTotal -= resp.getPageInfo().getResultsPerPage();
				} else {
					mCurrentTotal += resp.getPageInfo().getResultsPerPage();
				}

				mCurrentTotalDisplay = mCurrentTotal;
				if (mCurrentTotalDisplay > mTotalResults) {
					mCurrentTotalDisplay = mTotalResults;
				}

				mNextPageToken = resp.getNextPageToken();
				mPrevPageToken = resp.getPrevPageToken();

				Log.d(TAG, resp.toPrettyString());

				videos = Util.getVideosFromPlaylistItems(resp);

			} catch (IOException e) {
				Log.e("Unable to retrieve videos", e.getMessage());
				mError = "Unable to retrieve videos.";
				return null;
			}

			return videos;

		}
		// can use UI thread here
		protected void onPostExecute(final Library videos) {
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}

			if (videos != null) {
				// marry the Videos up with the ListView or go to VideoInfo if
				// only one
				mLib = videos;
				if (!mLib.isEmpty()) {
					mVideosListView.setVideos(mLib);
					setNextPrevHeaderButtons();
				} else {
					Util.showCenteredToast(ctx, ctx.getResources().getString(R.string.msg_no_videos_found));
					finish();
				}

			} else {
				if (mError == null) {
					mError = "Unknown error";
				}
				Util.showCenteredToast(ctx, mError);
				finish();
			}
		}

	}// end GetPlaylistVideosTask

	@Override
	public void onClick(View v) {
		if (v instanceof ImageButton) {
			if (v.getId() == R.id.next) {
				mNextPageToken = (String) v.getTag();
				mPrevPageToken = null;

				if (mPlaylistId != null) {
					new GetPlaylistVideosTask(this).execute();
				} else {
					SearchVideosTask svt = new SearchVideosTask(this);
					svt.execute();
				}
			} else if (v.getId() == R.id.prev) {
				mNextPageToken = null;
				mPrevPageToken = (String) v.getTag();

				if (mPlaylistId != null) {
					new GetPlaylistVideosTask(this).execute();
				} else {
					SearchVideosTask svt = new SearchVideosTask(this);
					svt.execute();
				}
			}
		} else if (v instanceof TextView) {
			if (v.getId() == R.id.playall) {
				if (mPlaylistId != null) {
					YouTubeInitializationResult initResult = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(this);
					if (initResult.equals(YouTubeInitializationResult.SUCCESS)) {
						// The device has the Youtube API Service (the app) and
						// you are safe to launch it.
						Intent intent = YouTubeStandalonePlayer.createPlaylistIntent(this,
								SharedConstants.UFO_WATCH_API_KEY, mPlaylistId, 0, 0, true, false);
						startActivity(intent);
					} else {
						if (initResult.isUserRecoverableError()) {
							Dialog dialog = initResult.getErrorDialog(this, 1);
							dialog.show();
						} else {
							Util.showCenteredToast(this, "Unable to Play All (" + initResult.name() + ")");
							Log.d(TAG, "YouTubeInitializationResult: " + initResult.name());
						}
					}
				}
			}

		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

}
