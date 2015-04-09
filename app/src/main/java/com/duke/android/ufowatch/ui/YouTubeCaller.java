package com.duke.android.ufowatch.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.duke.android.ufowatch.R;
import com.duke.android.ufowatch.common.SharedConstants;
import com.duke.android.ufowatch.common.Util;
import com.duke.android.ufowatch.domain.Video;
import com.duke.android.ufowatch.domain.YouTubeItem;
import com.duke.android.ufowatch.ui.adapter.YouTubeItemsArrayAdapter;
import com.duke.android.ufowatch.ui.widget.CustomDialog;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistSnippet;

public class YouTubeCaller extends Activity {

	public static final String TAG = "YouTubeCaller";
	final Context ctx = this;
	private Video mVideo;
	private String mYouTubeChoice;
	private CustomDialog mListDialog;
	private String mPlaylistId;
	private String mPlaylistTitle;
	private String mNewPlaylistTitle;
	private YouTubeItemsArrayAdapter mYouTubeItemsAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Log.d(TAG, "onCreate");
		Bundle extras = getIntent().getExtras();
		mVideo = (Video) (extras != null ? (Video) extras.getSerializable(SharedConstants.KEY_VIDEO) : null);
		mYouTubeChoice = (extras != null ? extras.getString(SharedConstants.KEY_YOUTUBE_CHOICE) : "");
		mNewPlaylistTitle = (extras != null ? extras.getString(SharedConstants.NEW_PLAYLIST_TITLE) : null);
	}

	@Override
	public void onStart() {
		super.onStart();

		LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout ll = (LinearLayout) li.inflate(R.layout.youtube_caller, null);
		setContentView(ll);

		if (!mYouTubeChoice.equalsIgnoreCase(SharedConstants.VIDEO_ADD_TO)) {
			Log.d(TAG, "new CallYouTubeTask");
			new CallYouTubeTask(this).execute();
		} else {
			// Log.d(TAG, "new CreateUserPlaylistDialogTask");
			// Video add to playlist so create dialog listing the playlists
			new CreateUserPlaylistDialogTask(this).execute();
			ProgressBar pb = (ProgressBar) ll.findViewById(R.id.progressBar);
			pb.setVisibility(View.GONE);
		}
	}

	private class CallYouTubeTask extends AsyncTask<String, Void, String> {

		Context ctx = null; // Add this and the constructor below
		String mPlaylistTitle = null;

		public CallYouTubeTask(Context context) {
			ctx = context;
		}

		public CallYouTubeTask(Context context, String playlistTitle) {
			ctx = context;
			mPlaylistTitle = playlistTitle;
		}

		// done on worker thread (separate from UI thread)
		protected String doInBackground(String... strings) {

			String displayMessage = null;

			// REFRESH ACCESS TOKEN IF NECESSARY
			// Log.d(TAG, "Time to check the access token");
			if (Util.tokenRefreshIsNecessary(ctx)) {
				Log.d(TAG, "doInBackground Access token needs refresh");
				String newAccessToken = Util.getNewAccessToken(ctx);
				// Log.d(TAG, "New access token " + newAccessToken);
			}

			// VIDEO FAVORITE
			if (mYouTubeChoice.equalsIgnoreCase(SharedConstants.VIDEO_FAVORITE)) {
				// Log.d(TAG, "Time to make the favorite YouTube call");
				String likesListId = Util.getUsersPlaylistId(SharedConstants.LIST_TYPE_FAVORITES, ctx);
				displayMessage = Util.insertVideoInPlaylist(likesListId, mVideo.getId(), ctx);

				if (displayMessage == null) {
					displayMessage = "Error making a favorite.";
				} else if (displayMessage.equalsIgnoreCase(ctx.getResources().getString(
						R.string.msg_video_added_to_playlist))) {
					displayMessage += " \"Favorites\"";
				}

			} else if (mYouTubeChoice.equalsIgnoreCase(SharedConstants.VIDEO_RATING_LIKE)
					|| mYouTubeChoice.equalsIgnoreCase(SharedConstants.VIDEO_RATING_DISLIKE)) {

				displayMessage = Util.rateVideo(mVideo.getId(), mYouTubeChoice, ctx);

			} else if (mYouTubeChoice.equalsIgnoreCase(SharedConstants.VIDEO_WATCH_LATER)) {
				String listId = Util.getUsersPlaylistId(SharedConstants.LIST_TYPE_WATCH_LATER, ctx);
				displayMessage = Util.insertVideoInPlaylist(listId, mVideo.getId(), ctx);

				if (displayMessage == null) {
					displayMessage = "Error adding video to Watch Later.";
				} else if (displayMessage.equalsIgnoreCase(ctx.getResources().getString(
						R.string.msg_video_added_to_playlist))) {
					displayMessage += " \"Watch Later\"";
				}

			} else if (mYouTubeChoice.equalsIgnoreCase(SharedConstants.CHANNEL_SUBSCRIBE)) {
				// Log.d(TAG, "Time to make the subscribe YouTube call");

				displayMessage = Util.subscribeToChannel(mVideo.getChannelId(), ctx);

				if (displayMessage == null) {
					displayMessage = "Error subscribing to the channel.";
				}

			} else if (mYouTubeChoice.equalsIgnoreCase(SharedConstants.VIDEO_ADD_TO)) {

				displayMessage = Util.insertVideoInPlaylist(mPlaylistId, mVideo.getId(), ctx);

				if (displayMessage == null) {
					displayMessage = "Error inserting video in playlist.";
				} else {
					displayMessage += " \"" + mPlaylistTitle + "\"";
				}
			} else if (mYouTubeChoice.equalsIgnoreCase(SharedConstants.NEW_PLAYLIST_TITLE)) {

				displayMessage = Util.insertNewPlaylist(mNewPlaylistTitle, ctx);

				if (displayMessage == null) {
					displayMessage = "Error creating playlist \"" + mNewPlaylistTitle + "\".";
				}
			} else if (mYouTubeChoice.equalsIgnoreCase("refreshtoken")) {
				// this is always done up front so do nothing
				displayMessage = null;
			}

			return displayMessage;

		}
		// can use UI thread here
		protected void onPostExecute(String message) {
			// Log.d(TAG, "Finish activity");

			if (message != null && message.length() > 0) {
				if (message.equalsIgnoreCase(SharedConstants.REAUTHORIZATION_REQUIRED)) {
					Bundle bundle = Util.createBundleWebsite("https://accounts.google.com/o/oauth2/auth?"
							+ "client_id=" + ctx.getResources().getString(R.string.oauth2_client_id)
							+ "&redirect_uri=urn:ietf:wg:oauth:2.0:oob&"
							+ "scope=https://www.googleapis.com/auth/youtube&" + "response_type=code&"
							+ "access_type=offline", ctx.getResources().getString(R.string.title_youtube_permission));
					Intent i = new Intent(ctx, UfoWatchWebView.class);
					i.putExtras(bundle);
					ctx.startActivity(i);
				} else {
					Util.showCenteredToast(ctx, message);
				}
			}
			finish();
		}

	}// end GetYouTubeVideosTask

	private class CreateUserPlaylistDialogTask extends AsyncTask<String, Void, String> {

		Context ctx = null; // Add this and the constructor below

		public CreateUserPlaylistDialogTask(Context context) {
			ctx = context;
		}

		// done on worker thread (separate from UI thread)
		protected String doInBackground(String... strings) {

			String displayMessage = null;
			// Log.d(TAG, "CreateUserPlaylistDialogTask doInBackground A");

			ArrayList<YouTubeItem> mYouTubeItems = new ArrayList<YouTubeItem>();

			String channelId = Util.getSharedPreferenceString(ctx, SharedConstants.KEY_USER_CHANNEL_ID, null);

			if (channelId == null) {
				return "Error getting channel.";
			}

			List<Playlist> playlistList = Util.getPlaylistsForChannel(ctx, channelId);

			YouTubeItem yti;

			if (playlistList != null && !playlistList.isEmpty()) {
				for (Playlist playlist : playlistList) {
					PlaylistSnippet snippet = playlist.getSnippet();

					String id = playlist.getId();
					String title = snippet.getTitle();

					yti = new YouTubeItem();
					yti.setId(id);
					yti.setTitle(title);

					mYouTubeItems.add(yti);

				}
			} else {
				return ctx.getResources().getString(R.string.msg_no_playlists_found);
			}

			Log.d(TAG, "CreateUserPlaylistDialogTask doInBackground mYouTubeItems: " + mYouTubeItems.size());

			mYouTubeItemsAdapter = new YouTubeItemsArrayAdapter(ctx, R.layout.list_item_simple, mYouTubeItems);

			return displayMessage;

		}
		// can use UI thread here
		protected void onPostExecute(String message) {
			Log.d(TAG, "Finish activity");
			if (message == null) {

				OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

						mPlaylistId = (String) view.getTag();
						mPlaylistTitle = (String) ((TextView) view).getText();
						Log.d(TAG, "CreateUserPlaylistDialogTask doInBackground clicked playlist id: " + mPlaylistId
								+ " Title: " + mPlaylistTitle);

						new CallYouTubeTask(ctx, mPlaylistTitle).execute();

						mListDialog.dismiss();
					}
				};

				mListDialog = Util.createCustomDialogList(ctx,
						ctx.getResources().getString(R.string.youtube_action_add_to_playlist), mYouTubeItemsAdapter,
						onItemClickListener);

				mListDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						finish();
					}
				});

				mListDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						finish();
					}
				});

				mListDialog.show();

			} else {
				Util.showCenteredToast(ctx, message);

				finish();
			}

		}

	}// end GetYouTubeVideosTask

}
