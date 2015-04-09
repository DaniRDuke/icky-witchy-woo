package com.duke.android.ufowatch.ui.tablet;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duke.android.ufowatch.R;
import com.duke.android.ufowatch.UfoWatchMain;
import com.duke.android.ufowatch.common.SharedConstants;
import com.duke.android.ufowatch.common.Util;
import com.duke.android.ufowatch.domain.Video;

@SuppressLint("NewApi")
public class VideoInfoFrag extends Fragment {

	public static final String TAG = "VideoInfoFrag";

	// A reference to retrieve the data from bundle
	public static final String VIDEO = "Video";

	private Video mVideo;
	private TextView mTextViewTitle;
	private TextView mTextViewDescription;
	private TextView mTextViewStats;
	private Dialog mListDialog;

	public static VideoInfoFrag newInstance() {
		VideoInfoFrag header = new VideoInfoFrag();
		return header;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = this.getArguments();
		mVideo = (Video) (extras != null ? (Video) extras.getSerializable(SharedConstants.KEY_VIDEO) : null);

		if (savedInstanceState != null) {
			mVideo = (Video) savedInstanceState.getSerializable(SharedConstants.KEY_VIDEO);
		}

		setHasOptionsMenu(true);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.video_info, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		// Log.d(TAG, "onStart");

		mTextViewTitle = (TextView) getActivity().findViewById(R.id.videoInfoTitle);
		mTextViewDescription = (TextView) getActivity().findViewById(R.id.videoInfoDescription);
		mTextViewStats = (TextView) getActivity().findViewById(R.id.videoInfoStats);

		if (mVideo != null) {
			new FillVideoTask(getActivity()).execute();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		// Log.d(this.TAG, "onPause");
		((UfoWatchMain) getActivity()).setmDisplayShareOption(false);
		((UfoWatchMain) getActivity()).setmDisplayYouTubeOption(false);
		((UfoWatchMain) getActivity()).invalidateOptionsMenu();
	}

	@Override
	public void onResume() {
		super.onResume();
		// Log.d(this.TAG, "onResume");
		// if a video player is being used
		// then add the Share, YouTube and FullScreen menu options to the action
		// menu
		if (!((UfoWatchMain) getActivity()).playerIsDisabled()) {
			((UfoWatchMain) getActivity()).setmDisplayShareOption(true);
			((UfoWatchMain) getActivity()).setmDisplayYouTubeOption(true);
			((UfoWatchMain) getActivity()).setmDisplayVideoFullScreenOption(true);
			((UfoWatchMain) getActivity()).setmDisplayWebViewFullScreenOption(false);
			((UfoWatchMain) getActivity()).invalidateOptionsMenu();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putSerializable(SharedConstants.KEY_VIDEO, mVideo);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		if (SharedConstants.ID_MENU_YOUTUBE == item.getItemId()) {
			mListDialog = Util.createDialogYouTube(getActivity(), mVideo);
			Util.setWindowRight(mListDialog);
			mListDialog.show();
			return true;
		} else if (SharedConstants.ID_MENU_SHARE == item.getItemId()) {
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
			intent.putExtra(Intent.EXTRA_SUBJECT, mVideo.getTitle());
			intent.putExtra(Intent.EXTRA_TEXT, this.getString(R.string.url_youtube_video) + mVideo.getId());
			startActivity(Intent.createChooser(intent, getResources().getString(R.string.msg_share_video)));
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
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

				// vidList = Util.createVideosList(ctx, mVideo.getId());
				//
				// List<com.google.api.services.youtube.model.Video> videos =
				// vidList.execute().getItems();
				//
				// if (videos.isEmpty()) {
				// return null;
				// }
				// com.google.api.services.youtube.model.Video tubeVideo =
				// videos.get(0);
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

			if (result != null) {
				mTextViewTitle.setText(mVideo.getTitle());
				mTextViewDescription.setText(mVideo.getDescription());
				mTextViewStats.setText(mVideo.getChannelTitle() + " | " + mVideo.getUploadedDt()
						+ ((mVideo.getViewCount() != null) ? " | Views " + mVideo.getViewCount() : "") + " | Likes "
						+ mVideo.getLikes() + " | Dislikes " + mVideo.getDislikes());
			} else {
				mTextViewTitle.setText(mVideo.getTitle());
				mTextViewDescription.setText("");
				mTextViewStats.setText(getResources().getString(R.string.msg_error_video_missing));
			}
		}
	}

}