package com.duke.android.ufowatch.ui.phone;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.duke.android.ufowatch.R;
import com.duke.android.ufowatch.UfoWatchMain;
import com.duke.android.ufowatch.common.SharedConstants;
import com.duke.android.ufowatch.common.Util;
import com.duke.android.ufowatch.domain.YouTubeItem;
import com.duke.android.ufowatch.ui.adapter.YouTubeItemsArrayAdapter;
import com.duke.android.ufowatch.ui.widget.CustomDialog;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistSnippet;
import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.SubscriptionListResponse;
import com.google.api.services.youtube.model.SubscriptionSnippet;

public class VideoTabFrag extends Fragment implements OnClickListener {

	public static final String TAG = "VideoTabFrag";

	private CustomDialog mListCustomDialog1;
	private CustomDialog mListCustomDialog2;
	private Button mBtn;

	private ArrayAdapter<CharSequence> adapter1;

	private ArrayList<YouTubeItem> mYouTubeItems;
	private YouTubeItemsArrayAdapter mYouTubeItemsAdapter;
	private String[] mStringArray;
	private String[] mRegionsListArray;

	public static VideoTabFrag newInstance() {
		// Create a new fragment instance
		VideoTabFrag videoTabFrag = new VideoTabFrag();
		return videoTabFrag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		// Log.d(TAG, "onCreateView");
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.video_tab, container, false);

	}

	// @Override
	// public void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	// Log.d(TAG, "onCreate");
	// }

	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");

		setupViews();
	}

	private void setButton(int backgroundResource) {
		mBtn.setOnClickListener(this);
		mBtn.setBackgroundResource(backgroundResource);
	}

	protected void showRegionSearchItemsDialog(int titleId) {

		int itemsArrayId = -1;

		if (titleId == R.string.title_united_kingdom) {
			itemsArrayId = R.array.unitedKingdomNames;
			mStringArray = getActivity().getApplicationContext().getResources()
					.getStringArray(R.array.unitedKingdomSearchNames);
		} else if (titleId == R.string.title_united_states) {
			itemsArrayId = R.array.unitedStatesNames;
			mStringArray = getActivity().getApplicationContext().getResources()
					.getStringArray(R.array.unitedStatesSearchNames);
		} else if (titleId == R.string.title_canada) {
			itemsArrayId = R.array.canadaNames;
			mStringArray = getActivity().getApplicationContext().getResources()
					.getStringArray(R.array.canadaSearchNames);
		} else if (titleId == R.string.title_mexico) {
			itemsArrayId = R.array.mexicoNames;
			mStringArray = getActivity().getApplicationContext().getResources()
					.getStringArray(R.array.mexicoSearchNames);
		} else if (titleId == R.string.title_france) {
			itemsArrayId = R.array.franceNames;
			mStringArray = getActivity().getApplicationContext().getResources()
					.getStringArray(R.array.franceSearchNames);
		} else if (titleId == R.string.title_australia) {
			itemsArrayId = R.array.australiaNames;
			mStringArray = getActivity().getApplicationContext().getResources()
					.getStringArray(R.array.australiaSearchNames);
		} else if (titleId == R.string.title_other_countries) {
			itemsArrayId = R.array.countryNames;
			mStringArray = getActivity().getApplicationContext().getResources()
					.getStringArray(R.array.countrySearchNames);
		}

		adapter1 = ArrayAdapter.createFromResource(getActivity(), itemsArrayId, R.layout.list_item_simple);

		OnItemClickListener onItemClickListener1 = new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				// String country = (String) parent.getItemAtPosition(position);
				String searchPhrase = mStringArray[position];
				Bundle bundle = Util.createBundleVideoSearchByWordOrPhrase(searchPhrase);

				Intent i = new Intent(getActivity(), VideoList2.class);
				i.putExtras(bundle);
				startActivity(i);

				mListCustomDialog2.dismiss();
				mListCustomDialog1.dismiss();
			}
		};

		mListCustomDialog2 = Util.createCustomDialogList(getActivity(),
				getActivity().getResources().getString(titleId), adapter1, onItemClickListener1);

		mListCustomDialog2.show();

	}

	public void onClick(View v) {
		Intent i;
		Bundle bundle;
		if (v instanceof Button) {
			if (v.getId() == R.id.youTubeRegion) {
				Log.d(TAG, "Regions clicked");
				showRegionsListDialog();
			} else if (v.getId() == R.id.youTubeToday) {
				Log.d(TAG, "Today clicked");
				i = new Intent(getActivity(), VideoList2.class);

				// pass start time of 24 hours ago
				long startTime = Util.getTodayStartTimeInMilliseconds();
				long endTime = startTime + 24 * 60 * 60 * 1000;

				bundle = Util.createBundleVideoSearchByTimeWTitle(startTime, endTime, getActivity()
						.getApplicationContext().getResources().getString(R.string.title_videos_today));
				i.putExtras(bundle);
				startActivity(i);
			} else if (v.getId() == R.id.youTubeByDate) {
				Log.d(TAG, "By date clicked");
				((UfoWatchMain) getActivity()).showDatePickerDialog();
			} else if (v.getId() == R.id.youTubePlaylists) {
				Log.d(TAG, "Playlists clicked");
				GetPlaylistsForViewTask plt = new GetPlaylistsForViewTask(getActivity(), R.id.playlists, getResources()
						.getString(R.string.title_app_name));
				plt.execute(SharedConstants.CHANNEL_ID_DUKECREATIONS);
			} else if (v.getId() == R.id.myLists) {
				Log.d(TAG, "My lists clicked");
				String channelId = Util.getSharedPreferenceString(getActivity(), SharedConstants.KEY_USER_CHANNEL_ID,
						null);
				if (channelId == null) {
					AlertDialog dialog = Util.createDialogGetPermission(getActivity());
					dialog.show();
				} else {
					GetPlaylistsForViewTask plt = new GetPlaylistsForViewTask(getActivity(), R.id.myLists, "My");
					plt.execute(channelId);
				}

			} else if (v.getId() == R.id.youTubeChannels) {
				Log.d(TAG, "Channels clicked");
				GetSubscriptionsTask st = new GetSubscriptionsTask(getActivity());
				st.execute(SharedConstants.CHANNEL_ID_DUKECREATIONS);
			}

			return;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		populateViewForOrientation(inflater, (ViewGroup) getView());
	}

	private void populateViewForOrientation(LayoutInflater inflater, ViewGroup viewGroup) {
		viewGroup.removeAllViewsInLayout();
		inflater.inflate(R.layout.video_tab, viewGroup);

		setupViews();

	}

	private void setupViews() {

		mBtn = (Button) getActivity().findViewById(R.id.youTubeToday);
		setButton(R.drawable.ufo_btn_bg);

		mBtn = (Button) getActivity().findViewById(R.id.youTubeByDate);
		setButton(R.drawable.ufo_btn_bg);

		mBtn = (Button) getActivity().findViewById(R.id.youTubeRegion);
		setButton(R.drawable.ufo_btn_bg);

		mBtn = (Button) getActivity().findViewById(R.id.youTubePlaylists);
		setButton(R.drawable.ufo_btn_bg);

		mBtn = (Button) getActivity().findViewById(R.id.myLists);
		setButton(R.drawable.ufo_btn_bg);

		mBtn = (Button) getActivity().findViewById(R.id.youTubeChannels);
		setButton(R.drawable.ufo_btn_bg);

	}

	protected void showRegionsListDialog() {

		mRegionsListArray = getActivity().getApplicationContext().getResources().getStringArray(R.array.regionsList);

		adapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.regionsList, R.layout.list_item_simple);

		OnItemClickListener onItemClickListener1 = new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				// bring up the appropriate dialog list
				String regionSelected = mRegionsListArray[position];
				int titleId = -1;
				if (getActivity().getResources().getString(R.string.title_united_states)
						.equalsIgnoreCase(regionSelected)) {
					titleId = R.string.title_united_states;
				} else if (getActivity().getResources().getString(R.string.title_canada)
						.equalsIgnoreCase(regionSelected)) {
					titleId = R.string.title_canada;
				} else if (getActivity().getResources().getString(R.string.title_other_countries)
						.equalsIgnoreCase(regionSelected)) {
					titleId = R.string.title_other_countries;
				} else if (getActivity().getResources().getString(R.string.title_united_kingdom)
						.equalsIgnoreCase(regionSelected)) {
					titleId = R.string.title_united_kingdom;
				} else if (getActivity().getResources().getString(R.string.title_mexico)
						.equalsIgnoreCase(regionSelected)) {
					titleId = R.string.title_mexico;
				} else if (getActivity().getResources().getString(R.string.title_france)
						.equalsIgnoreCase(regionSelected)) {
					titleId = R.string.title_france;
				} else if (getActivity().getResources().getString(R.string.title_australia)
						.equalsIgnoreCase(regionSelected)) {
					titleId = R.string.title_australia;
				}
				showRegionSearchItemsDialog(titleId);

			}
		};

		mListCustomDialog1 = Util.createCustomDialogList(getActivity(),
				getActivity().getResources().getString(R.string.title_region), adapter1, onItemClickListener1);

		mListCustomDialog1.show();

	}
	private class GetPlaylistsForViewTask extends AsyncTask<String, Void, List<Playlist>> {

		Context ctx = null;
		int menuId;
		String channelTitle;

		public GetPlaylistsForViewTask(Context context, int menuId, String channelTitle) {
			ctx = context;
			this.menuId = menuId;
			this.channelTitle = channelTitle;
		}

		// done on worker thread (separate from UI thread)
		protected List<Playlist> doInBackground(String... channelIds) {
			String channelId = channelIds[0];

			return Util.getPlaylistsForChannel(ctx, channelId);

		}
		// can use UI thread here
		protected void onPostExecute(List<Playlist> playlistList) {

			mYouTubeItems = new ArrayList<YouTubeItem>();
			YouTubeItem yti;

			if (playlistList != null && !playlistList.isEmpty()) {
				for (Playlist playlist : playlistList) {
					PlaylistSnippet snippet = playlist.getSnippet();

					String id = playlist.getId();
					Log.d(TAG, "GetPlaylistsTask playlistid: " + id);
					String title = snippet.getTitle();

					yti = new YouTubeItem();
					yti.setId(id);
					yti.setTitle(title);

					mYouTubeItems.add(yti);

				}
			} else {
				Util.showCenteredToast(getActivity(),
						getActivity().getResources().getString(R.string.msg_no_playlists_found));
				return;
			}

			// get title
			String title = null;
			if (R.id.playlists == menuId) {
				title = getActivity().getResources().getString(R.string.title_dialog_ufowatchlists);
			} else if (R.id.mylists == menuId) {
				title = getActivity().getResources().getString(R.string.title_dialog_myplaylists);
			} else {
				title = channelTitle + " " + getActivity().getResources().getString(R.string.title_dialog_playlists);
			}

			mYouTubeItemsAdapter = new YouTubeItemsArrayAdapter(getActivity(), R.layout.list_item_simple, mYouTubeItems);

			OnItemClickListener onItemClickListener1 = new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

					String playlistId = (String) view.getTag();

					Bundle bundle = new Bundle();
					bundle.putString(SharedConstants.KEY_PLAYLIST, playlistId);
					Log.d(TAG, "Clicked playlistid: " + playlistId);

					Intent i = new Intent(getActivity(), VideoList2.class);
					i.putExtras(bundle);
					startActivity(i);

				}
			};

			mListCustomDialog1 = Util.createCustomDialogList(getActivity(), title, mYouTubeItemsAdapter,
					onItemClickListener1);

			mListCustomDialog1.show();

		}

	}

	private class GetSubscriptionsTask extends AsyncTask<String, Void, List<Subscription>> {

		Context ctx = null; // Add this and the constructor below

		public GetSubscriptionsTask(Context context) {
			ctx = context;
		}

		// done on worker thread (separate from UI thread)
		protected List<Subscription> doInBackground(String... channelIds) {
			String channelId = channelIds[0];

			YouTube youTube = Util.createYouTube(ctx);

			List<Subscription> subList = null;
			try {

				YouTube.Subscriptions.List subscriptionList = youTube.subscriptions().list("snippet");
				subscriptionList.setChannelId(channelId);
				subscriptionList.setKey(SharedConstants.UFO_WATCH_API_KEY);
				subscriptionList.setFields("items(snippet/title,snippet/resourceId/channelId)");
				subscriptionList.setMaxResults(Long.valueOf("30"));
				SubscriptionListResponse subListResp = subscriptionList.execute();
				Log.d(TAG, subListResp.toPrettyString());
				subList = subListResp.getItems();
			} catch (IOException e) {
				Log.e("Error getting subscriptions.", e.getMessage());
				return null;
			}

			return subList;

		}
		// can use UI thread here
		protected void onPostExecute(List<Subscription> subscriptionList) {

			mYouTubeItems = new ArrayList<YouTubeItem>();
			YouTubeItem yti;

			if (subscriptionList != null) {
				for (Subscription subscription : subscriptionList) {
					SubscriptionSnippet snippet = subscription.getSnippet();

					String id = snippet.getResourceId().getChannelId();
					String title = snippet.getTitle();

					yti = new YouTubeItem();
					yti.setId(id);
					yti.setTitle(title);

					mYouTubeItems.add(yti);

				}
			}

			mYouTubeItemsAdapter = new YouTubeItemsArrayAdapter(getActivity(), R.layout.list_item_simple, mYouTubeItems);

			OnItemClickListener onItemClickListener1 = new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

					String channelId = (String) view.getTag();
					String channelTitle = (String) ((TextView) view).getText();

					GetPlaylistsForViewTask plt = new GetPlaylistsForViewTask(getActivity(), R.id.channels,
							channelTitle);
					plt.execute(channelId);

				}
			};

			Log.d(TAG, "GetSubscriptionsTask channels");
			mListCustomDialog1 = Util.createCustomDialogList(getActivity(),
					ctx.getResources().getString(R.string.title_dialog_channels), mYouTubeItemsAdapter,
					onItemClickListener1);

			mListCustomDialog1.show();

		}
	}
}
