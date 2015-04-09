package com.duke.android.ufowatch.ui.tablet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
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

public class HeaderFrag extends Fragment implements OnClickListener {

	public static final String TAG = "HeaderFrag";

	private String[] mStringArray;
	private String[] mRegionsListArray;
	// private String[] mStringArray;

	private CustomDialog mListCustomDialog1;
	private CustomDialog mListCustomDialog2;
	private ArrayAdapter<CharSequence> adapter1;
	private ArrayAdapter<CharSequence> adapter2;
	OnItemClickListener onItemClickListener1;
	OnItemClickListener onItemClickListener2;
	private String mDialogTitle = "";

	private TextView mTvMylists;
	private TextView mTvMore;
	private ImageView mIvCalendar;
	private ImageView mIvByRegion;
	private ImageView mIvPlaylists;
	private ImageView mIvChannels;
	private ImageView mIvFeeds;
	private ImageView mIvBlogs;

	private ArrayList<YouTubeItem> mYouTubeItems;
	private YouTubeItemsArrayAdapter mYouTubeItemsAdapter;

	public static HeaderFrag newInstance() {
		HeaderFrag header = new HeaderFrag();
		return header;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.header, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		// Log.d(TAG, "onStart");

		mIvCalendar = ((ImageView) getActivity().findViewById(R.id.calendar));
		mIvCalendar.setOnClickListener(this);

		mIvByRegion = ((ImageView) getActivity().findViewById(R.id.byregion));
		mIvByRegion.setOnClickListener(this);
		mIvPlaylists = ((ImageView) getActivity().findViewById(R.id.playlists));
		mIvPlaylists.setOnClickListener(this);

		mTvMylists = ((TextView) getActivity().findViewById(R.id.mylists));
		mTvMylists.setOnClickListener(this);
		mIvChannels = ((ImageView) getActivity().findViewById(R.id.channels));
		mIvChannels.setOnClickListener(this);
		mIvFeeds = ((ImageView) getActivity().findViewById(R.id.feeds));
		mIvFeeds.setOnClickListener(this);
		mIvBlogs = ((ImageView) getActivity().findViewById(R.id.blogs));
		mIvBlogs.setOnClickListener(this);
		mTvMore = ((TextView) getActivity().findViewById(R.id.more));
		mTvMore.setOnClickListener(this);

	}

	public void setSelectedHeaderItemColor(int itemId) {

		int normalBackgroundColor = getActivity().getResources().getColor(android.R.color.transparent);
		int selectedBackgroundColor = getActivity().getResources().getColor(R.color.selected_background_color);
		mIvCalendar.setBackgroundColor(normalBackgroundColor);
		mIvByRegion.setBackgroundColor(normalBackgroundColor);
		mIvPlaylists.setBackgroundColor(normalBackgroundColor);
		mTvMylists.setBackgroundColor(normalBackgroundColor);
		mIvChannels.setBackgroundColor(normalBackgroundColor);
		mIvFeeds.setBackgroundColor(normalBackgroundColor);
		mIvBlogs.setBackgroundColor(normalBackgroundColor);
		mTvMore.setBackgroundColor(normalBackgroundColor);

		switch (itemId) {
			case R.id.calendar :
				mIvCalendar.setBackgroundColor(selectedBackgroundColor);
				return;
			case R.id.byregion :
				mIvByRegion.setBackgroundColor(selectedBackgroundColor);
				return;
			case R.id.playlists :
				mIvPlaylists.setBackgroundColor(selectedBackgroundColor);
				return;
			case R.id.mylists :
				// mTvMylists.setTextColor(selectedColor);
				mTvMylists.setBackgroundColor(selectedBackgroundColor);
				return;
			case R.id.channels :
				mIvChannels.setBackgroundColor(selectedBackgroundColor);
				return;
			case R.id.feeds :
				mIvFeeds.setBackgroundColor(selectedBackgroundColor);
				return;
			case R.id.blogs :
				mIvBlogs.setBackgroundColor(selectedBackgroundColor);
				return;
			case R.id.more :
				mTvMore.setBackgroundColor(selectedBackgroundColor);
				return;
			default :
				return;

		}
	}

	// @Override
	// public void onResume() {
	// super.onResume();
	// Log.d(TAG, "onPause");
	// }
	//
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
	//
	// @Override
	// public void onStop() {
	// super.onStop();
	// Log.d(TAG, "onPause");
	// }

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		if (v instanceof TextView) {

			if (v.getId() == R.id.mylists) {

				String channelId = Util.getSharedPreferenceString(getActivity(), SharedConstants.KEY_USER_CHANNEL_ID,
						null);
				if (channelId == null) {
					AlertDialog dialog = Util.createDialogGetPermission(getActivity());
					dialog.show();
				} else {
					GetPlaylistsForViewTask plt = new GetPlaylistsForViewTask(getActivity(), R.id.mylists, "");
					plt.execute(channelId);
				}
			} else if (v.getId() == R.id.more) {
				this.showMoreListDialog();
			}
		} else if (v instanceof ImageView) {
			if (v.getId() == R.id.calendar) {
				((UfoWatchMain) getActivity()).showDatePickerDialog();
			} else if (v.getId() == R.id.byregion) {
				this.showRegionsListDialog();
			} else if (v.getId() == R.id.playlists) {
				GetPlaylistsForViewTask plt = new GetPlaylistsForViewTask(getActivity(), R.id.playlists, getResources()
						.getString(R.string.title_app_name));
				plt.execute(SharedConstants.CHANNEL_ID_DUKECREATIONS);
			} else if (v.getId() == R.id.channels) {
				GetSubscriptionsTask st = new GetSubscriptionsTask(getActivity());
				st.execute(SharedConstants.CHANNEL_ID_DUKECREATIONS);
			} else if (v.getId() == R.id.feeds) {
				this.showFeedsListDialog();
			} else if (v.getId() == R.id.blogs) {
				this.showBlogsListDialog();
			}
		}
	}

	protected void showStateNamesReportsDialog() {

		mStringArray = getActivity().getApplicationContext().getResources().getStringArray(R.array.stateReportsPages);

		adapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.stateNamesReports, R.layout.list_item_simple);

		onItemClickListener2 = new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String stateName = (String) parent.getItemAtPosition(position);
				String stateReportPage = mStringArray[position];
				Intent i = Util.createIntentWebView(getActivity(),
						getResources().getString(R.string.url_report_nuforcstates) + stateReportPage + ".html",
						stateName + " " + getResources().getString(R.string.msg_sightings));
				startActivity(i);

				mListCustomDialog2.dismiss();
			}
		};

		mListCustomDialog2 = Util.createCustomDialogList(getActivity(),
				getActivity().getResources().getString(R.string.title_report_nuforcstates), adapter2,
				onItemClickListener2);

		mListCustomDialog2.show();

	}

	protected void showMoreListDialog() {

		mStringArray = getActivity().getApplicationContext().getResources().getStringArray(R.array.sitesUrlList);

		adapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.sitesList, R.layout.list_item_simple);

		onItemClickListener1 = new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				String title = (String) ((TextView) view).getText();
				String url = mStringArray[position];

				// if this is not the report by north american states
				// then go to the web page
				if (!title.equalsIgnoreCase(getResources().getString(R.string.title_report_nuforcstates))) {
					Intent i = Util.createIntentWebView(getActivity(), url, title);
					startActivity(i);
				} else { // otherwise popup the list of states
					mListCustomDialog1.dismiss();
					showStateNamesReportsDialog();
				}

				mListCustomDialog1.dismiss();
			}
		};

		mListCustomDialog1 = Util.createCustomDialogList(getActivity(),
				getActivity().getResources().getString(R.string.title_dialog_more), adapter1, onItemClickListener1);

		mListCustomDialog1.show();

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

		mDialogTitle = getActivity().getResources().getString(titleId);

		adapter2 = ArrayAdapter.createFromResource(getActivity(), itemsArrayId, R.layout.list_item_simple);

		onItemClickListener2 = new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				setSelectedHeaderItemColor(R.id.byregion);
				((UfoWatchMain) getActivity()).setSelectedHeaderItemId(R.id.byregion);
				String searchPhrase = mStringArray[position];
				((UfoWatchMain) getActivity()).displayVideoList(Util
						.createBundleVideoSearchByWordOrPhrase(searchPhrase));
				((UfoWatchMain) getActivity()).getActionBar().setTitle(
						mDialogTitle + " - " + ((TextView) view).getText());
				mListCustomDialog2.dismiss();
				mListCustomDialog1.dismiss();
			}
		};

		mListCustomDialog2 = Util.createCustomDialogList(getActivity(), mDialogTitle, adapter2, onItemClickListener2);

		mListCustomDialog2.show();

	}

	protected void showRegionsListDialog() {

		mRegionsListArray = getActivity().getApplicationContext().getResources().getStringArray(R.array.regionsList);

		adapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.regionsList, R.layout.list_item_simple);

		onItemClickListener1 = new AdapterView.OnItemClickListener() {
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

	protected void showFeedsListDialog() {

		mStringArray = getActivity().getApplicationContext().getResources().getStringArray(R.array.rssUrlList);

		adapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.rssList, R.layout.list_item_simple);

		onItemClickListener1 = new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				String feedTitle = (String) ((TextView) view).getText();

				setSelectedHeaderItemColor(R.id.feeds);
				((UfoWatchMain) getActivity()).setSelectedHeaderItemId(R.id.feeds);
				String feedUrl = mStringArray[position];
				Bundle bundle = new Bundle();
				bundle.putString(SharedConstants.KEY_URL, feedUrl);
				((UfoWatchMain) getActivity()).displayRssGrid(bundle);

				((UfoWatchMain) getActivity()).getActionBar().setTitle(feedTitle);

				mListCustomDialog1.dismiss();
			}
		};

		mListCustomDialog1 = Util.createCustomDialogList(getActivity(),
				getActivity().getResources().getString(R.string.title_dialog_feeds), adapter1, onItemClickListener1);

		mListCustomDialog1.show();

	}

	protected void showBlogsListDialog() {

		mStringArray = getActivity().getApplicationContext().getResources().getStringArray(R.array.blogsUrlList);

		adapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.blogsList, R.layout.list_item_simple);

		onItemClickListener1 = new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				String feedTitle = (String) ((TextView) view).getText();

				setSelectedHeaderItemColor(R.id.blogs);
				((UfoWatchMain) getActivity()).setSelectedHeaderItemId(R.id.blogs);
				String url = mStringArray[position];
				Bundle bundle = new Bundle();
				bundle.putString(SharedConstants.KEY_URL, url);
				((UfoWatchMain) getActivity()).displayBlogGrid(bundle);

				((UfoWatchMain) getActivity()).getActionBar().setTitle(feedTitle);

				mListCustomDialog1.dismiss();
			}
		};

		mListCustomDialog1 = Util.createCustomDialogList(getActivity(),
				getActivity().getResources().getString(R.string.title_dialog_blogs), adapter1, onItemClickListener1);

		mListCustomDialog1.show();

	}

	private class GetPlaylistsForViewTask extends AsyncTask<String, Void, List<Playlist>> {

		Context ctx = null; // Add this and the constructor below
		int headerViewId;
		String channelTitle;

		public GetPlaylistsForViewTask(Context context, int headerViewId, String channelTitle) {
			ctx = context;
			this.headerViewId = headerViewId;
			this.channelTitle = channelTitle;
		}

		// can use UI thread here
		protected void onPreExecute() {

		}

		protected List<Playlist> doInBackground(String... channelIds) {
			String channelId = channelIds[0];

			return Util.getPlaylistsForChannel(ctx, channelId);

		}

		protected void onPostExecute(List<Playlist> playlistList) {

			// mRowView = (TextView) mListDialog.findViewById(R.id.listRow);
			mYouTubeItems = new ArrayList<YouTubeItem>();
			YouTubeItem yti;

			if (playlistList != null && !playlistList.isEmpty()) {
				for (Playlist playlist : playlistList) {
					PlaylistSnippet snippet = playlist.getSnippet();

					String id = playlist.getId();
					String title2 = snippet.getTitle();

					yti = new YouTubeItem();
					yti.setId(id);
					yti.setTitle(title2);

					mYouTubeItems.add(yti);

				}
			} else {
				Util.showCenteredToast(getActivity(),
						getActivity().getResources().getString(R.string.msg_no_playlists_found));
				return;
			}

			String title = "";
			if (R.id.playlists == headerViewId) {
				title = ctx.getResources().getString(R.string.title_dialog_ufowatchlists);
			} else if (R.id.mylists == headerViewId) {
				title = "My Playlists";
			} else {
				title = channelTitle + " Playlists";
			}

			mYouTubeItemsAdapter = new YouTubeItemsArrayAdapter(getActivity(), R.layout.list_item_simple, mYouTubeItems);

			onItemClickListener2 = new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

					setSelectedHeaderItemColor(headerViewId);
					((UfoWatchMain) getActivity()).setSelectedHeaderItemId(headerViewId);

					String playlistId = (String) view.getTag();

					Bundle bundle = new Bundle();
					bundle.putString(SharedConstants.KEY_PLAYLIST, playlistId);
					Log.d(TAG, "playlistid: " + playlistId);

					((UfoWatchMain) getActivity()).getActionBar().setTitle(
							channelTitle + ": " + ((TextView) view).getText());

					((UfoWatchMain) getActivity()).displayVideoPlaylist(bundle);

					mListCustomDialog2.dismiss();

					if (mListCustomDialog1 != null && mListCustomDialog1.isShowing()) {
						mListCustomDialog1.dismiss();
					}

				}

			};

			mListCustomDialog2 = Util.createCustomDialogList(getActivity(), title, mYouTubeItemsAdapter,
					onItemClickListener2);

			mListCustomDialog2.show();

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

			YouTube youTube = Util.createYouTube(getActivity());

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

				// ****************
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

			onItemClickListener1 = new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

					String channelId = (String) view.getTag();
					String channelTitle = (String) ((TextView) view).getText();

					GetPlaylistsForViewTask plt = new GetPlaylistsForViewTask(getActivity(), R.id.channels,
							channelTitle);
					plt.execute(channelId);

				}

			};

			mListCustomDialog1 = Util.createCustomDialogList(getActivity(),
					ctx.getResources().getString(R.string.title_dialog_channels), mYouTubeItemsAdapter,
					onItemClickListener1);

			mListCustomDialog1.show();

		}

	}

}