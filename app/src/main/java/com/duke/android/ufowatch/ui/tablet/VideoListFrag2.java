package com.duke.android.ufowatch.ui.tablet;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.duke.android.ufowatch.R;
import com.duke.android.ufowatch.UfoWatchMain;
import com.duke.android.ufowatch.common.SharedConstants;
import com.duke.android.ufowatch.common.Util;
import com.duke.android.ufowatch.domain.Library;
import com.duke.android.ufowatch.domain.Video;
import com.duke.android.ufowatch.ui.VideoClickListener;
import com.duke.android.ufowatch.ui.widget.VideoListView2;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.SearchListResponse;

public class VideoListFrag2 extends Fragment implements OnClickListener, VideoClickListener {

	public static final String TAG = "VideoListFrag";

	// private VideoListView mVideosListView;
	private VideoListView2 mVideosListView;
	private String mKeywords;
	private int mStartIndex;
	private String mPlaylistId;
	private String mNextPageToken;
	private String mPrevPageToken;
	private long mStartTime;
	private long mEndTime;
	private String mError;
	private int mCurrentTotal = 0;
	private int mCurrentTotalDisplay = 0;
	private int mTotalResults = 0;
	private OnVideoSelectedListener mOnVideoSelectedListener;
	private Library mLib;
	protected ProgressBar mProgressBar;
	private List<String> mVideoIds;
	private ImageButton mBtnNext;
	private ImageButton mBtnPrev;
	private TextView mTvCounts;

	public static VideoListFrag2 newInstance() {
		VideoListFrag2 header = new VideoListFrag2();
		return header;
	}

	// Container Activity must implement this interface
	public interface OnVideoSelectedListener {
		public void onVideoSelected(Video video);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mOnVideoSelectedListener = (OnVideoSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnVideoSelectedListener");
		}
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");

		Bundle extras = this.getArguments();

		mNextPageToken = extras != null ? extras.getString(SharedConstants.KEY_NEXT_PAGE_TOKEN) : null;
		mPrevPageToken = extras != null ? extras.getString(SharedConstants.KEY_PREV_PAGE_TOKEN) : null;
		mPlaylistId = extras != null ? extras.getString(SharedConstants.KEY_PLAYLIST) : null;
		mKeywords = extras != null ? extras.getString(SharedConstants.VIDEO_SEARCH_PARAM_NAME_KEYWORDS) : null;
		mStartIndex = extras != null ? extras.getInt(SharedConstants.VIDEO_SEARCH_PARAM_NAME_STARTINDEX,
				SharedConstants.FIRST_VIDEO_POSITION) : SharedConstants.FIRST_VIDEO_POSITION;
		mStartTime = extras != null ? extras.getLong(SharedConstants.VIDEO_SEARCH_PARAM_NAME_START_TIME) : null;
		mEndTime = extras != null ? extras.getLong(SharedConstants.VIDEO_SEARCH_PARAM_NAME_END_TIME) : null;

		if (savedInstanceState != null) {
			mPlaylistId = savedInstanceState.getString(SharedConstants.KEY_PLAYLIST);
			mKeywords = savedInstanceState.getString(SharedConstants.VIDEO_SEARCH_PARAM_NAME_KEYWORDS);
			mStartIndex = savedInstanceState.getInt(SharedConstants.VIDEO_SEARCH_PARAM_NAME_STARTINDEX,
					SharedConstants.FIRST_VIDEO_POSITION);
			mStartTime = savedInstanceState.getLong(SharedConstants.VIDEO_SEARCH_PARAM_NAME_START_TIME);
			mEndTime = savedInstanceState.getLong(SharedConstants.VIDEO_SEARCH_PARAM_NAME_END_TIME);
			mLib = (Library) savedInstanceState.getSerializable("LIBRARY");
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		return inflater.inflate(R.layout.video_list_view_2, container, false);

	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");

		mProgressBar = (ProgressBar) getActivity().findViewById(R.id.progressBar);

		mVideosListView = (VideoListView2) getActivity().findViewById(R.id.videosListView);
		mVideosListView.setOnVideoClickListener(this);

		mBtnNext = ((ImageButton) getActivity().findViewById(R.id.next));
		mBtnNext.setOnClickListener(this);
		mBtnPrev = ((ImageButton) getActivity().findViewById(R.id.prev));
		mBtnPrev.setOnClickListener(this);
		mTvCounts = ((TextView) getActivity().findViewById(R.id.counts));

		if (mLib != null) {
			mVideosListView.setVideos(mLib);
		} else if (mPlaylistId != null) {
			new GetPlaylistVideosTask(getActivity()).execute();

		} else {
			SearchVideosTask svt = new SearchVideosTask(getActivity());
			svt.execute();
		}

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

	// @Override
	// public void onDestroy() {
	// super.onDestroy();
	// Log.d(TAG, "onDestroy");
	// }
	//
	// @Override
	// public void onPause() {
	// super.onPause();
	// Log.d(TAG, "onPause");
	// }

	// @Override
	// public void onResume() {
	// super.onResume();
	// Log.d(this.TAG, "onResume");
	// Log.d(TAG, "onResume - selected position: " +
	// mVideosListView.getSelectedPosition());
	// }

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		Log.d(TAG, "onSaveInstanceState - selected position: " + mVideosListView.getSelectedPosition());

		savedInstanceState.putString(SharedConstants.KEY_PLAYLIST, mPlaylistId);
		savedInstanceState.putString(SharedConstants.VIDEO_SEARCH_PARAM_NAME_KEYWORDS, mKeywords);
		savedInstanceState.putInt(SharedConstants.VIDEO_SEARCH_PARAM_NAME_STARTINDEX, mStartIndex);
		savedInstanceState.putLong(SharedConstants.VIDEO_SEARCH_PARAM_NAME_START_TIME, mStartTime);
		savedInstanceState.putLong(SharedConstants.VIDEO_SEARCH_PARAM_NAME_END_TIME, mEndTime);

		savedInstanceState.putSerializable("LIBRARY", mLib);
	}

	// This is the interface method that is called when a video in the listview
	// is clicked!
	// The interface is a contract between this activity and the listview
	@Override
	public void onVideoClicked(Video video) {
		mOnVideoSelectedListener.onVideoSelected(video);
	}

	// PLAYLIST ASSOCIATED METHODS
	// *************************************************
	public void setPlaylistPosition(int position) {
		mVideosListView.setSelectedPosition(position);
	}

	/**
	 * Call VideoListView.setNextSelectedPosition()
	 */
	public void setNextPlaylistPosition() {
		mVideosListView.setNextSelectedPosition();
	}

	/**
	 * Call VideoListView.setPreviousSelectedPosition()
	 */
	public void setPreviousPlaylistPosition() {
		mVideosListView.setPreviousSelectedPosition();
	}
	// *************************************************

	/**
	 * Click the "Next" button if it is visible
	 */
	public void getNextList() {
		if (mBtnNext.getVisibility() == View.VISIBLE) {
			mBtnNext.performClick();
		}
	}

	/**
	 * Call VideoListView.resetBackgrounds()
	 */
	public void resetBackgrounds() {
		mVideosListView.resetBackgrounds();
	}

	public void updateVideosList(Bundle bundle) {
		mNextPageToken = bundle != null ? bundle.getString(SharedConstants.KEY_NEXT_PAGE_TOKEN) : null;
		mPrevPageToken = bundle != null ? bundle.getString(SharedConstants.KEY_PREV_PAGE_TOKEN) : null;

		if (mPlaylistId != null) {
			new GetPlaylistVideosTask(getActivity()).execute();
		} else {
			SearchVideosTask svt = new SearchVideosTask(getActivity());
			svt.execute();
		}
	}

	private class SearchVideosTask extends AsyncTask<Void, Void, Library> {

		Context ctx = null; // Add this and the constructor below

		public SearchVideosTask(Context context) {
			ctx = context;
		}

		// can use UI thread here
		protected void onPreExecute() {
			if (!isRemoving()) {
				mProgressBar.setEnabled(true);
				mProgressBar.setIndeterminate(true);
				mProgressBar.setVisibility(View.VISIBLE);
				mVideosListView.setVisibility(View.GONE);
			}
		}

		// done on worker thread (separate from UI thread)
		protected Library doInBackground(Void... arg) {
			Library lib = null;

			try {

				YouTube.Search.List search = Util.createSearchList(ctx, mNextPageToken, mPrevPageToken, mKeywords, ctx
						.getResources().getInteger(R.integer.video_list_length), mStartTime, mEndTime);

				// Log.d(TAG, "SEARCH: " + search.toString());

				SearchListResponse searchResponse = search.execute();
				Log.d(TAG, "doInBackground() searchResponse:\n" + searchResponse.toPrettyString());
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

				// Log.d(TAG, "Previous NextPageToken: " + mNextPageToken);
				mNextPageToken = searchResponse.getNextPageToken();
				mPrevPageToken = searchResponse.getPrevPageToken();
				// Log.d(TAG, searchResponse.toPrettyString());

				lib = Util.getVideosFromSearchResponse(searchResponse, ctx);

				/*
				 * if (videos != null) { lib = new Library(videos); }
				 */
			} catch (IOException e) {
				Log.e("Error retrieving videos.", e.getMessage());
				mError = "Unable to retrieve videos.";
				return null;
			}

			return lib;

		}
		// can use UI thread here
		protected void onPostExecute(final Library lib) {

			mProgressBar.setEnabled(true);
			mProgressBar.setIndeterminate(true);
			mProgressBar.setVisibility(View.GONE);

			mVideosListView.setVisibility(View.VISIBLE);

			if (lib != null) {
				// marry the Videos up with the ListView
				mLib = lib;
				mVideosListView.setVideos(mLib);
				// Log.d(TAG, "vids in library: " + lib.getVideos().size());

				setNextPrevHeaderButtons();

				if (!mLib.isEmpty()) {
					((UfoWatchMain) ctx).setPlayerIsListPlayer(false);
				} else {
					Util.showCenteredToast(ctx, ctx.getResources().getString(R.string.msg_no_videos_found));
				}
			} else {
				if (mError == null) {
					mError = "Unknown error";
				}
			}
		}

	}// end SearchVideosTask

	private class GetPlaylistVideosTask extends AsyncTask<Void, Void, Library> {

		Context ctx = null;
		public GetPlaylistVideosTask(Context context) {
			ctx = context;
		}

		// can use UI thread here
		protected void onPreExecute() {
			if (!isRemoving()) {
				mProgressBar.setEnabled(true);
				mProgressBar.setIndeterminate(true);
				mProgressBar.setVisibility(View.VISIBLE);
				mVideosListView.setVisibility(View.GONE);

			}
		}

		// done on worker thread (separate from UI thread)
		protected Library doInBackground(Void... arg) {
			Library lib = null;

			YouTube youTube = Util.createYouTube(ctx);

			try {

				YouTube.PlaylistItems.List playlistItemList = Util.createPlaylistItemsList(youTube, mPlaylistId,
						mNextPageToken, mPrevPageToken, ctx.getResources().getInteger(R.integer.video_list_length));

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

				// Log.d(TAG, resp.toPrettyString());

				lib = Util.getVideosFromPlaylistItems(resp);
				mVideoIds = Util.getVideoIdsFromPlaylistItems(resp);

			} catch (IOException e) {
				Log.e("Unable to retrieve videos.", e.getMessage());
				mError = "Unable to retrieve videos.";
				return null;
			}

			return lib;

		}
		// can use UI thread here
		protected void onPostExecute(final Library lib) {

			mProgressBar.setEnabled(true);
			mProgressBar.setIndeterminate(true);
			mProgressBar.setVisibility(View.GONE);

			mVideosListView.setVisibility(View.VISIBLE);

			if (lib != null) {
				// marry the Videos up with the ListView
				mLib = lib;
				mVideosListView.setVideos(mLib);
				if (!mLib.getVideos().isEmpty()) {
					if (((UfoWatchMain) ctx).getCurrentYoutubeInitializationResult().equals(
							YouTubeInitializationResult.SUCCESS)) {
						((UfoWatchMain) ctx).setPlayerFragmentList(mVideoIds);
						((UfoWatchMain) ctx).setPlayerIsListPlayer(true);
						// setPlaylistPosition(0);
					} else {
						((UfoWatchMain) ctx).setPlayerIsListPlayer(false);
					}
				} else {
					Util.showCenteredToast(ctx, ctx.getResources().getString(R.string.msg_no_videos_found));
				}
				setNextPrevHeaderButtons();

			} else {
				if (mError == null) {
					mError = "Unknown error";
				}
			}
		}

	}// end GetPlaylistVideosTask

	@Override
	public void onClick(View v) {
		if (v instanceof ImageButton) {
			if (v.getId() == R.id.next) {
				Bundle bundle = new Bundle();
				bundle.putString(SharedConstants.KEY_NEXT_PAGE_TOKEN, (String) v.getTag());
				Log.d(TAG, "Next page token: " + (String) v.getTag());
				updateVideosList(bundle);
			} else if (v.getId() == R.id.prev) {
				Bundle bundle = new Bundle();
				bundle.putString(SharedConstants.KEY_PREV_PAGE_TOKEN, (String) v.getTag());
				Log.d(TAG, "Prev page token: " + (String) v.getTag());
				updateVideosList(bundle);
			}
		}
	}

}