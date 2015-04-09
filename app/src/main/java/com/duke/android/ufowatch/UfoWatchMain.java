package com.duke.android.ufowatch;

import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.duke.android.ufowatch.common.SharedConstants;
import com.duke.android.ufowatch.common.Util;
import com.duke.android.ufowatch.domain.Video;
import com.duke.android.ufowatch.ui.adapter.UfoTabsAdapter;
import com.duke.android.ufowatch.ui.phone.BlogsTabFrag;
import com.duke.android.ufowatch.ui.phone.RssTabFrag;
import com.duke.android.ufowatch.ui.phone.SearchTabFrag;
import com.duke.android.ufowatch.ui.phone.SitesTabFrag;
import com.duke.android.ufowatch.ui.phone.VideoList2;
import com.duke.android.ufowatch.ui.phone.VideoTabFrag;
import com.duke.android.ufowatch.ui.tablet.BlogFeedGridFrag;
import com.duke.android.ufowatch.ui.tablet.HeaderFrag;
import com.duke.android.ufowatch.ui.tablet.RssFeedGridFrag;
import com.duke.android.ufowatch.ui.tablet.UfoWebViewFrag;
import com.duke.android.ufowatch.ui.tablet.VideoInfoFrag;
import com.duke.android.ufowatch.ui.tablet.VideoListFrag2;
import com.duke.android.ufowatch.ui.tablet.VideoListFrag2.OnVideoSelectedListener;
import com.duke.android.ufowatch.ui.viewpager.extensions.SwipeyTabsView;
import com.duke.android.ufowatch.ui.viewpager.extensions.TabsAdapter;
import com.duke.android.ufowatch.ui.widget.DatePickerFragment;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

@SuppressLint("NewApi")
public class UfoWatchMain extends FragmentActivity
		implements
			OnVideoSelectedListener,
			DatePickerDialog.OnDateSetListener {

	public static final String TAG = "UfoWatchMain";
	private Context ctx = this;
	private boolean mFullScreen = false;
	private UfoWebViewFrag mWebViewFrag;

	public boolean mIsTablet = false;
	public YouTubeInitializationResult mYouTubeInitResult;
	public static final int INIT_PAGER_POS = 0;

	private Fragment mListFragment, mInfoFragment, mPlayFragment, mMenuFragment;
	private FragmentTransaction mTransaction;
	private FragmentManager mFragmentManager;
	private Bundle mBundle;

	private ViewPager mPager;
	private SwipeyTabsView mUfoTabs;
	private PagerAdapter mPagerAdapter;
	private TabsAdapter mUfoTabsAdapter;
	private String mSearchQuery;

	private boolean mPlayerIsDisabled;
	public boolean playerIsDisabled() {
		return mPlayerIsDisabled;
	}
	boolean initLoad = true;

	public boolean isInitLoad() {
		return initLoad;
	}

	public void setInitLoad(boolean initLoad) {
		this.initLoad = initLoad;
	}

	public boolean isTablet() {
		return mIsTablet;
	}

	public YouTubeInitializationResult getCurrentYoutubeInitializationResult() {
		return mYouTubeInitResult;
	}

	public void setCurrentYoutubeInitResult(YouTubeInitializationResult youTubeInitializationResult) {
		mYouTubeInitResult = youTubeInitializationResult;
	}

	public void setPlayerIsDisabled(boolean playerIsDisabled) {
		this.mPlayerIsDisabled = playerIsDisabled;
	}

	private boolean mPlayerIsListPlayer;
	public boolean playerIsListPlayer() {
		return mPlayerIsListPlayer;
	}

	public void setPlayerIsListPlayer(boolean isListPlayer) {
		this.mPlayerIsListPlayer = isListPlayer;
	}

	private int selectedHeaderItemId;
	public int getSelectedHeaderItemId() {
		return selectedHeaderItemId;
	}

	public void setSelectedHeaderItemId(int selectedHeaderItemId) {
		this.selectedHeaderItemId = selectedHeaderItemId;
	}

	private boolean mDisplayYouTubeOption = false;
	public void setmDisplayYouTubeOption(boolean mDisplayYouTubeOption) {
		this.mDisplayYouTubeOption = mDisplayYouTubeOption;
	}

	private boolean mDisplayVideoFullScreenOption = false;
	public void setmDisplayVideoFullScreenOption(boolean displayVideoFullScreenOption) {
		this.mDisplayVideoFullScreenOption = displayVideoFullScreenOption;
	}

	private boolean mDisplayWebViewFullScreenOption = false;
	public void setmDisplayWebViewFullScreenOption(boolean displayWebViewFullScreenOption) {
		this.mDisplayWebViewFullScreenOption = displayWebViewFullScreenOption;
	}

	private boolean mDisplayShareOption = false;
	public void setmDisplayShareOption(boolean mDisplayShareOption) {
		this.mDisplayShareOption = mDisplayShareOption;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ufo_watch_main);

		// This is a tablet if section details framelayout exists
		View sectionInfo = findViewById(R.id.frame_info);
		mIsTablet = sectionInfo != null && sectionInfo.getVisibility() == View.VISIBLE;

		mFragmentManager = this.getSupportFragmentManager();
		// If this is a tablet
		if (mIsTablet) {
			// enable the home button to return all the way back to home
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			ActionBar bar = this.getActionBar();
			bar.setDisplayHomeAsUpEnabled(false);
			bar.setHomeButtonEnabled(true);
		} else {
			initViewPager(UfoTabsAdapter.NUMBER_OF_TABS);
			mUfoTabs = (SwipeyTabsView) findViewById(R.id.swipey_tabs);
			mUfoTabsAdapter = new UfoTabsAdapter(this);
			mUfoTabs.setAdapter(mUfoTabsAdapter);
			mUfoTabs.setViewPager(mPager);
		}

		// Refresh token if the user has been logged in
		new RefreshTokenTask(this).execute();

		// set the initial YouTube service availability result
		setCurrentYoutubeInitResult(YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(this));

		selectedHeaderItemId = SharedConstants.NO_HEADER_ITEM_SELECTED;
		if (savedInstanceState != null) {
			selectedHeaderItemId = savedInstanceState.getInt("SELECTED_HEADER_ITEM_ID",
					SharedConstants.NO_HEADER_ITEM_SELECTED);
		}

		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {

		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			//
			String search = intent.getStringExtra(SearchManager.QUERY).trim();
			mSearchQuery = SharedConstants.VIDEO_SEARCH_KEYWORDS + "+" + search.replace(" ", "+");

			// Log.d(TAG, "mSearchQuery: " + mSearchQuery);
			// if not a tablet then start videolist activity
			if (!mIsTablet) {
				Intent i;
				i = new Intent(this, VideoList2.class);
				i.putExtra(SharedConstants.VIDEO_SEARCH_PARAM_NAME_KEYWORDS, mSearchQuery);
				startActivity(i);
			} else { // tablet

				getActionBar().setTitle("Search: " + search);
				mBundle = new Bundle();
				mBundle.putString(SharedConstants.VIDEO_SEARCH_PARAM_NAME_KEYWORDS, mSearchQuery);
				this.displayVideoSearchList(mBundle);

			}
		} else { // this is not a user search, it happens on launch
			if (mIsTablet) {
				if (selectedHeaderItemId == SharedConstants.NO_HEADER_ITEM_SELECTED) {

					long startTime = Util.getTodayStartTimeInMilliseconds();
					long endTime = startTime + 24 * 60 * 60 * 1000;

					mBundle = Util.createBundleVideoSearchByTime(startTime, endTime);

					this.displayInitialVideoList(mBundle);
				}
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt("SELECTED_HEADER_ITEM_ID", selectedHeaderItemId);
	}

	@Override
	public void onStart() {
		super.onStart();
		// Log.d(TAG, "onStart");
		if (mIsTablet) {
			if (selectedHeaderItemId == R.id.byregion
					|| selectedHeaderItemId == R.id.channels
					// || selectedHeaderItemId == R.id.today
					// || selectedHeaderItemId == R.id.bydate
					|| selectedHeaderItemId == R.id.calendar || selectedHeaderItemId == R.id.playlists
					|| selectedHeaderItemId == R.id.mylists || selectedHeaderItemId == R.id.feeds
					|| selectedHeaderItemId == R.id.blogs) {
				setHeaderItemColorSelected(selectedHeaderItemId);
			}
		}
	}

	@Override
	public void onRestart() {
		super.onRestart();
		// Log.d(TAG, "onRestart");
	}

	@Override
	public void onResume() {
		super.onResume();
		// Log.d(TAG, "onRestart");
		// set the initial YouTube service availability
		// in case it was changed by user
		setCurrentYoutubeInitResult(YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(this));

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// if this is a tablet and user goes to portrait, reset to landscape
		if (mIsTablet && newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}

	private void initViewPager(int pageCount) {
		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new MyFragmentPagerAdapter(mFragmentManager, pageCount);
		mPager.setAdapter(mPagerAdapter);
		mPager.setCurrentItem(INIT_PAGER_POS);
		mPager.setPageMargin(1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// super.onCreateOptionsMenu(menu);
		MenuInflater inflater = this.getMenuInflater();

		if (mDisplayYouTubeOption) {
			menu.add(Menu.FIRST, SharedConstants.ID_MENU_YOUTUBE, 0,
					getResources().getString(R.string.lbl_btn_youtube_options)).setIcon(R.drawable.youtube)
					.setShowAsAction(MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
		}
		if (mDisplayShareOption) {
			menu.add(Menu.FIRST, SharedConstants.ID_MENU_SHARE, 1, getResources().getString(R.string.lbl_btn_share))
					.setIcon(R.drawable.ic_menu_share).setShowAsAction(MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
		}
		if (mDisplayVideoFullScreenOption) {
			menu.add(Menu.FIRST, SharedConstants.ID_MENU_FULL_SCREEN_VIDEO, 2,
					getResources().getString(R.string.lbl_btn_full_screen)).setIcon(R.drawable.ic_media_fullscreen)
					.setShowAsAction(MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
		}
		if (mDisplayWebViewFullScreenOption) {
			menu.add(Menu.FIRST, SharedConstants.ID_MENU_FULL_SCREEN_WEBVIEW, 2,
					getResources().getString(R.string.lbl_btn_full_screen)).setIcon(R.drawable.ic_media_fullscreen)
					.setShowAsAction(MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
		}

		inflater.inflate(R.menu.ufo_watch_main_options, menu);

		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// Intent i = null;
		AlertDialog alertDialog;
		if (item.getItemId() == R.id.search) {
			return onSearchRequested();
		} else if (item.getItemId() == R.id.about) {
			alertDialog = Util.createDialogAbout(this);
			alertDialog.show();
			return true;
		} else if (item.getItemId() == R.id.account) {
			Intent i = Util.createIntentWebView(this, "https://accounts.google.com/o/oauth2/auth?" + "client_id="
					+ getResources().getString(R.string.oauth2_client_id) + "&"
					+ "redirect_uri=urn:ietf:wg:oauth:2.0:oob&"
					+ "scope=https://www.googleapis.com/auth/youtube&response_type=code&" + "access_type=offline",
					getResources().getString(R.string.title_youtube_permission));
			startActivity(i);
			return true;
		} else if (item.getItemId() == android.R.id.home) {
			if (mIsTablet) {
				Intent intent = new Intent(this, UfoWatchMain.class);
				setSelectedHeaderItemId(SharedConstants.NO_HEADER_ITEM_SELECTED);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
			return true;
		} else if (SharedConstants.ID_MENU_FULL_SCREEN_VIDEO == item.getItemId()) {
			// if play section is showing full screen then show all sections
			// otherwise make the video play section go full screen
			if (mFullScreen) {
				showAllVideoSections();
			} else {
				showPlaySectionFullScreen();
			}
			mFullScreen = !mFullScreen;

			return true;
		} else if (SharedConstants.ID_MENU_FULL_SCREEN_WEBVIEW == item.getItemId()) {
			// if play section is showing full screen then show all sections
			// otherwise make the video play section go full screen
			if (mFullScreen) {
				showAllWebViewSections();
			} else {
				showPlaySectionFullScreen();
			}
			mFullScreen = !mFullScreen;

			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
	static class MyFragmentPagerAdapter extends FragmentPagerAdapter {

		private int mLength = 0;

		public MyFragmentPagerAdapter(FragmentManager fm, int length) {
			super(fm);
			mLength = length;
		}

		@Override
		public int getCount() {
			return mLength;
		}

		@Override
		public Fragment getItem(int position) {
			Fragment f;
			Log.d(TAG, "getItem() position:" + position);
			switch (position) {
				case 0 :
					f = new VideoTabFrag();
					break;
				case 1 :
					f = new SearchTabFrag();
					break;
				case 2 :
					f = new RssTabFrag();
					break;
				case 3 :
					f = new BlogsTabFrag();
					break;
				case 4 :
					f = new SitesTabFrag();
					break;
				default :
					throw new IllegalArgumentException("not this many fragments: " + position);
			}
			return f;
		}
	}

	@Override
	public void onVideoSelected(final Video video) {

		// Log.d(TAG, "onVideoSelected()");
		if (mYouTubeInitResult.equals(YouTubeInitializationResult.SUCCESS)) {

			// Log.d(TAG,
			// "onVideoSelected() YouTubeInitializationResult.SUCCESS");
			// The device has the Youtube API Service (the app) and you
			// are safe to launch it.
			removeMenuOptions();

			// this is a selected video so not a playlist player
			setPlayerIsListPlayer(false);
			setPlayerIsDisabled(false);

			this.setFadeAnimations();

			mTransaction = mFragmentManager.beginTransaction();

			mInfoFragment = VideoInfoFrag.newInstance();
			mBundle = new Bundle();
			mBundle.putSerializable(SharedConstants.KEY_VIDEO, video);
			mInfoFragment.setArguments(mBundle);
			mTransaction.replace(R.id.frame_info, mInfoFragment);

			YouTubePlayerSupportFragment youtubeFrag = YouTubePlayerSupportFragment.newInstance();
			youtubeFrag.initialize(SharedConstants.UFO_WATCH_API_KEY,
					Util.createPlayerListenerForVideo(getParent(), video.getId()));

			mTransaction.replace(R.id.frame_play, youtubeFrag);
			mTransaction.commit();

			this.showAllVideoSections();

		} else {
			Log.d(TAG, "onVideoSelected() YouTubeInitializationResult. FAILURE");
			if (mYouTubeInitResult.isUserRecoverableError()) {
				Dialog dialog = mYouTubeInitResult.getErrorDialog(this, 1, new DialogInterface.OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						try {
							// start a YouTube action intent
							Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + video.getId()));
							startActivity(intent);
						} catch (ActivityNotFoundException e) {
							Util.showCenteredToast(ctx, "No activity found to play video.");
						}
					}
				});
				dialog.show();
			} else {
				Util.showCenteredToast(this, "YouTube initialization result: " + mYouTubeInitResult.name());
				Log.d(TAG, "YouTubeInitializationResult: " + mYouTubeInitResult.name());
			}
		}

	}

	public void displayInitialVideoList(Bundle searchBundle) {

		// this.showHomeSectionFullScreen();
		setPlayerIsDisabled(true);

		// if not a search then is initial start so get landing screen
		mTransaction = mFragmentManager.beginTransaction();
		this.setFadeAnimations();

		mMenuFragment = HeaderFrag.newInstance();
		mTransaction.replace(R.id.frame_menu, mMenuFragment);

		mListFragment = VideoListFrag2.newInstance();
		mListFragment.setArguments(searchBundle);
		mTransaction.replace(R.id.frame_list, mListFragment);

		mTransaction.commit();

		this.getActionBar().setTitle(getResources().getString(R.string.title_videos_today));

		this.showListSectionFullScreen();

	}

	public void displayBlogGrid(Bundle searchBundle) {

		mTransaction = mFragmentManager.beginTransaction();
		this.setFadeAnimations();

		mListFragment = BlogFeedGridFrag.newInstance();
		mListFragment.setArguments(searchBundle);
		mTransaction.replace(R.id.frame_list, mListFragment);

		mTransaction.commit();

		this.showListSectionFullScreen();

	}

	public void displayRssGrid(Bundle searchBundle) {

		mTransaction = mFragmentManager.beginTransaction();
		this.setFadeAnimations();

		mListFragment = RssFeedGridFrag.newInstance();
		mListFragment.setArguments(searchBundle);
		mTransaction.replace(R.id.frame_list, mListFragment);

		mTransaction.commit();

		this.showListSectionFullScreen();

	}

	public void displayVideoSearchList(Bundle searchBundle) {

		removeContentFragments();
		setPlayerIsDisabled(true);

		// if not a search then is initial start so get landing screen
		mTransaction = mFragmentManager.beginTransaction();
		this.setFadeAnimations();

		mListFragment = VideoListFrag2.newInstance();
		mListFragment.setArguments(searchBundle);
		mTransaction.replace(R.id.frame_list, mListFragment);

		mMenuFragment = HeaderFrag.newInstance();
		mTransaction.replace(R.id.frame_menu, mMenuFragment);
		mTransaction.commit();

		this.showListSectionFullScreen();

	}

	public void displayVideoList(Bundle parametersBundle) {

		removeContentFragments();
		removeMenuOptions();
		setPlayerIsDisabled(true);

		mTransaction = mFragmentManager.beginTransaction();
		this.setFadeAnimations();

		mListFragment = VideoListFrag2.newInstance();
		mListFragment.setArguments(parametersBundle);
		mTransaction.replace(R.id.frame_list, mListFragment);

		mTransaction.commit();

		this.showListSectionFullScreen();

	}

	public void displayVideoPlaylist(Bundle parametersBundle) {

		removeContentFragments();
		removeMenuOptions();
		setPlayerIsDisabled(true);
		this.setFadeAnimations();

		mTransaction = mFragmentManager.beginTransaction();

		mListFragment = VideoListFrag2.newInstance();
		mListFragment.setArguments(parametersBundle);
		mTransaction.replace(R.id.frame_list, mListFragment);

		mTransaction.commit();

		this.showAllVideoSections();

	}

	public void setPlaylistPosition(int pos) {
		mListFragment = mFragmentManager.findFragmentById(R.id.frame_list);
		((VideoListFrag2) mListFragment).setPlaylistPosition(pos);
	}

	public void setNextPlaylistPosition() {
		mListFragment = mFragmentManager.findFragmentById(R.id.frame_list);
		((VideoListFrag2) mListFragment).setNextPlaylistPosition();
	}

	public void setPreviousPlaylistPosition() {
		mListFragment = mFragmentManager.findFragmentById(R.id.frame_list);
		((VideoListFrag2) mListFragment).setPreviousPlaylistPosition();
	}

	/**
	 * Call VideoListFrag.getNextList()
	 */
	public void getNextList() {
		mListFragment = mFragmentManager.findFragmentById(R.id.frame_list);
		((VideoListFrag2) mListFragment).getNextList();
	}

	public void resetPlaylistBackgrounds() {
		mListFragment = mFragmentManager.findFragmentById(R.id.frame_list);
		((VideoListFrag2) mListFragment).resetBackgrounds();
	}

	public void showListSectionFullScreen() {
		Log.d(TAG, "showListSectionFullScreen()");

		FrameLayout frame = (FrameLayout) findViewById(R.id.frame_menu);
		frame.setVisibility(View.VISIBLE);

		// expand list to full width
		frame = (FrameLayout) findViewById(R.id.frame_list);
		android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		frame.setLayoutParams(params);
		frame.setVisibility(View.VISIBLE);

		// REMOVE INFO and PLAY
		LinearLayout detailsAndPlayLayout = (LinearLayout) findViewById(R.id.layout_play_info_frames);
		detailsAndPlayLayout.setVisibility(View.GONE);
		frame = (FrameLayout) findViewById(R.id.frame_info);
		frame.setVisibility(View.GONE);
		frame = (FrameLayout) findViewById(R.id.frame_play);
		frame.setVisibility(View.GONE);
	}

	public void showPlaySectionFullScreen() {
		Log.d(TAG, "showPlaySectionFullScreen()");
		FrameLayout frame = (FrameLayout) findViewById(R.id.frame_play);
		frame.setVisibility(View.VISIBLE);
		frame = (FrameLayout) findViewById(R.id.frame_info);
		frame.setVisibility(View.GONE);
		frame = (FrameLayout) findViewById(R.id.frame_list);
		frame.setVisibility(View.GONE);
		frame = (FrameLayout) findViewById(R.id.frame_menu);
		frame.setVisibility(View.GONE);
	}

	public void showPlaySectionFullHeight() {
		Log.d(TAG, "showPlaySectionFullScreen()");
		FrameLayout frame = (FrameLayout) findViewById(R.id.frame_play);
		frame.setVisibility(View.VISIBLE);
		frame = (FrameLayout) findViewById(R.id.frame_menu);
		frame.setVisibility(View.VISIBLE);

		// Hide INFO and LIST
		frame = (FrameLayout) findViewById(R.id.frame_info);
		frame.setVisibility(View.GONE);
		frame = (FrameLayout) findViewById(R.id.frame_list);
		frame.setVisibility(View.GONE);
	}

	public void showAllVideoSections() {
		Log.d(TAG, "showAllVideoSections()");
		FrameLayout frame = (FrameLayout) findViewById(R.id.frame_list);
		// width set by
		android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(getResources()
				.getInteger(R.integer.list_width_layout_param), LayoutParams.MATCH_PARENT);
		frame.setLayoutParams(params);

		frame.setVisibility(View.VISIBLE);

		frame = (FrameLayout) findViewById(R.id.frame_menu);
		frame.setVisibility(View.VISIBLE);

		LinearLayout detailsAndPlayLayout = (LinearLayout) findViewById(R.id.layout_play_info_frames);
		detailsAndPlayLayout.setVisibility(View.VISIBLE);

		// if there is a list being played in the player
		// or this is not YouTube (is webview)
		// don't show VideoInfoFrag
		frame = (FrameLayout) findViewById(R.id.frame_info);
		if (playerIsListPlayer()) {
			frame.setVisibility(View.GONE);
		} else {
			frame.setVisibility(View.VISIBLE);
		}

		frame = (FrameLayout) findViewById(R.id.frame_play);
		frame.setVisibility(View.VISIBLE);
	}

	public void showAllWebViewSections() {
		Log.d(TAG, "showAllWebViewSections()");
		FrameLayout frame = (FrameLayout) findViewById(R.id.frame_list);
		frame.setVisibility(View.VISIBLE);
		frame = (FrameLayout) findViewById(R.id.frame_play);
		frame.setVisibility(View.VISIBLE);
		frame = (FrameLayout) findViewById(R.id.frame_menu);
		frame.setVisibility(View.VISIBLE);

		// never show info frame for webview
		frame = (FrameLayout) findViewById(R.id.frame_info);
		frame.setVisibility(View.GONE);

	}

	public void framesAllInvisible() {
		Log.d(TAG, "framesAllInvisible()");
		FrameLayout frame = (FrameLayout) findViewById(R.id.frame_info);
		frame.setVisibility(View.INVISIBLE);
		frame = (FrameLayout) findViewById(R.id.frame_play);
		frame.setVisibility(View.INVISIBLE);
		frame = (FrameLayout) findViewById(R.id.frame_list);
		frame.setVisibility(View.INVISIBLE);

	}

	public void removeMenuOptions() {
		setmDisplayYouTubeOption(false);
		setmDisplayShareOption(false);
		setmDisplayVideoFullScreenOption(false);
		setmDisplayWebViewFullScreenOption(false);
		invalidateOptionsMenu();

	}
	private void setFadeAnimations() {
		mTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in,
				android.R.anim.fade_out);
	}

	public void setHeaderItemColorSelected(int headerItemId) {
		mMenuFragment = mFragmentManager.findFragmentById(R.id.frame_menu);
		if (mMenuFragment != null && mMenuFragment.getClass() == HeaderFrag.class) {
			((HeaderFrag) mMenuFragment).setSelectedHeaderItemColor(headerItemId);
		}
	}

	public void setPlayerFragmentList(List<String> mVideoIds) {
		setmDisplayVideoFullScreenOption(true);
		setmDisplayWebViewFullScreenOption(false);
		invalidateOptionsMenu();

		Log.d(TAG, "setPlayerFragmentList() setting frame visibility");
		FrameLayout frame = (FrameLayout) findViewById(R.id.frame_play);
		frame.setVisibility(View.VISIBLE);
		frame = (FrameLayout) findViewById(R.id.frame_info);
		frame.setVisibility(View.GONE);

		mTransaction = mFragmentManager.beginTransaction();
		this.setFadeAnimations();

		YouTubePlayerSupportFragment youtubeFrag = YouTubePlayerSupportFragment.newInstance();
		youtubeFrag.initialize(SharedConstants.UFO_WATCH_API_KEY,
				Util.createPlayerListenerForVideoList(this, mVideoIds));

		mTransaction.replace(R.id.frame_play, youtubeFrag);
		mTransaction.commit();

	}

	/**
	 * Used by UfoWebViewFrag to get a url overloading WebView that has
	 * javascript enabled.
	 */
	@SuppressLint("SetJavaScriptEnabled")
	public WebView getWebView(String url) {
		// Log.d(TAG, "getWebView");
		WebView webView = new WebView(this);
		webView.setWebViewClient(new UfoWebViewClient());
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setUseWideViewPort(true);

		return webView;
	}

	private class UfoWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// Log.d(TAG, "shouldOverrideUrlLoading");
			view.loadUrl(url);
			return true;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Log.d(TAG, "onKeyDown");

		// if this is the webview fragment and this was a back button
		// pass the message back to the WebViewFragment so it can handle
		// the browser back
		if (mIsTablet) {

			mPlayFragment = mFragmentManager.findFragmentById(R.id.frame_play);

			if (mPlayFragment != null && mPlayFragment.getClass() == UfoWebViewFrag.class
					&& keyCode == KeyEvent.KEYCODE_BACK) {

				// if the fragment onBackButtonDown returns true, then it has
				// handled the back button using a browser history back
				// So onKeyDown can be considered handled (so return true)
				if (((UfoWebViewFrag) mPlayFragment).onBackButtonDown()) {
					return true;
				}
			}

		}
		// otherwise process regularly
		return super.onKeyDown(keyCode, event);
	}

	private void removeContentFragments() {
		mPlayFragment = mFragmentManager.findFragmentById(R.id.frame_play);
		mInfoFragment = mFragmentManager.findFragmentById(R.id.frame_info);
		if (mPlayFragment != null || mInfoFragment != null) {
			mTransaction = mFragmentManager.beginTransaction();
			this.setFadeAnimations();
			if (mPlayFragment != null) {

				if (mPlayFragment.getClass() == UfoWebViewFrag.class) {
					((UfoWebViewFrag) mPlayFragment).clearWebView();
				}

				mTransaction.remove(mPlayFragment);
			}
			if (mInfoFragment != null) {
				mTransaction.remove(mInfoFragment);
			}
			mTransaction.commit();
		}
	}

	public void displayWebsite(Bundle bundle) {
		Log.d(TAG, "displayWebsite() setting frame visibility");
		FrameLayout frame = (FrameLayout) findViewById(R.id.frame_play);
		frame.setVisibility(View.VISIBLE);
		frame = (FrameLayout) findViewById(R.id.frame_info);
		frame.setVisibility(View.GONE);

		mTransaction = mFragmentManager.beginTransaction();
		this.setFadeAnimations();

		// remove VideoInfoFrag from details section if needed
		mInfoFragment = mFragmentManager.findFragmentById(R.id.frame_info);
		if (mInfoFragment != null && mInfoFragment.getClass() == VideoInfoFrag.class) {
			mTransaction.remove(mInfoFragment);
		}

		mWebViewFrag = new UfoWebViewFrag();
		mWebViewFrag.setArguments(bundle);
		mTransaction.add(R.id.frame_play, mWebViewFrag);

		// Log.d(TAG, "displayWebsite committing");
		mTransaction.commit();

	}

	public void showDatePickerDialog() {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(mFragmentManager, "datePicker");
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		// create a Calendar date starting at midnight
		final Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		c.set(year, month, day, 0, 0, 0);

		long startTime = c.getTimeInMillis();
		long endTime = startTime + 24 * 60 * 60 * 1000;
		month += 1;

		String title = year + "-" + month + "-" + day;

		if (mIsTablet) {
			// set the start and end time for the search (a one day span)

			this.getActionBar().setTitle(title);

			// Do the search here
			displayVideoList(Util.createBundleVideoSearchByTime(startTime, endTime));
			setHeaderItemColorSelected(R.id.calendar);
		} else {

			Intent i = new Intent(this, VideoList2.class);
			// pass start time of 24 hours ago
			Bundle bundle = Util.createBundleVideoSearchByTimeWTitle(startTime, endTime, title);
			i.putExtras(bundle);
			startActivity(i);

		}

	}

	private class RefreshTokenTask extends AsyncTask<Void, Void, Void> {

		Context ctx = null; // Add this and the constructor below

		public RefreshTokenTask(Context context) {
			ctx = context;
		}

		@Override
		protected Void doInBackground(Void... params) {
			// REFRESH ACCESS TOKEN IF NECESSARY
			Log.d(TAG, "Time to check the access token");
			if (Util.tokenRefreshIsNecessary(ctx)) {
				Log.d(TAG, "Access token needs refresh");
				String newAccessToken = Util.getNewAccessToken(ctx);
				Log.d(TAG, "New access token " + newAccessToken);
			}
			return null;
		}

	}// end GetYouTubeVideosTask

}