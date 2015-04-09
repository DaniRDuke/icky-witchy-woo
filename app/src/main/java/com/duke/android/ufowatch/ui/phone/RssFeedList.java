package com.duke.android.ufowatch.ui.phone;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.duke.android.ufowatch.R;
import com.duke.android.ufowatch.common.RssFeedXmlHandler;
import com.duke.android.ufowatch.common.SharedConstants;
import com.duke.android.ufowatch.common.Util;
import com.duke.android.ufowatch.domain.FeedStructure;
import com.duke.android.ufowatch.ui.UfoWatchWebView;
import com.duke.android.ufowatch.ui.adapter.RssReaderListAdapter;

public class RssFeedList extends Activity {

	public static final String TAG = "RssFeedList";

	ListView mRssFeedListView;
	List<FeedStructure> mRssStruct;
	private RssReaderListAdapter mListAdapter;
	private String mFeedUrl;
	private String mFeedName;
	private BroadcastReceiver mBroadcastReceiver;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Log.d(TAG, "onCreate");

		Bundle extras = getIntent().getExtras();

		mFeedUrl = extras != null ? extras.getString(SharedConstants.KEY_URL) : null;
		mFeedName = extras != null ? extras.getString(SharedConstants.KEY_TITLE) : null;

		if (savedInstanceState != null) {
			mFeedUrl = savedInstanceState.getString(SharedConstants.KEY_URL);
			mFeedName = savedInstanceState.getString(SharedConstants.KEY_TITLE);
		}

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(SharedConstants.ACTION_RETURN_HOME);
		mBroadcastReceiver = Util.createBroadcastReceiver(this);
		registerReceiver(mBroadcastReceiver, intentFilter);
		this.getActionBar().setHomeButtonEnabled(true);
		this.getActionBar().setTitle(mFeedName);

	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");

		LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout rl = (LinearLayout) li.inflate(R.layout.feed_list_view, null);
		setContentView(rl);
		mRssFeedListView = (ListView) rl.findViewById(R.id.rssfeed_listview);
		RssFeedTask rssTask = new RssFeedTask(this);
		rssTask.execute();

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
		savedInstanceState.putString(SharedConstants.KEY_URL, mFeedUrl);
		savedInstanceState.putString(SharedConstants.KEY_TITLE, mFeedName);
	}

	// @Override
	// public void onStop() {
	// super.onStop();
	// // Log.d(TAG, "onStop");
	// }
	//
	// @Override
	// public void onResume() {
	// super.onResume();
	// // Log.d(TAG, "onResume");
	//
	// }

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Log.d(TAG, "onDestroy");
		unregisterReceiver(mBroadcastReceiver);
	}

	private class RssFeedTask extends AsyncTask<String, Void, String> {
		Context ctx = null;
		private ProgressDialog Dialog;
		String response = "";

		public RssFeedTask(Context context) {
			ctx = context;
		}

		@Override
		protected void onPreExecute() {
			Dialog = new ProgressDialog(ctx);
			Dialog.setMessage(getResources().getString(R.string.msg_loading));
			Dialog.show();
		}

		@Override
		protected String doInBackground(String... urls) {
			try {
				RssFeedXmlHandler rh = new RssFeedXmlHandler();
				mRssStruct = rh.getLatestArticles(mFeedUrl);
			} catch (Exception e) {
				Log.d(TAG, "Exception getLatestArticles");
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d(TAG, "onPostExecute");
			if (mRssStruct != null) {
				mListAdapter = new RssReaderListAdapter(RssFeedList.this, mRssStruct);
				mRssFeedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						String feedItemUrl = (String) ((TextView) ((LinearLayout) view).findViewById(R.id.feed_text))
								.getTag();
						String feedItemTitle = (String) ((TextView) ((LinearLayout) view).findViewById(R.id.feed_text))
								.getText();

						Intent i = new Intent(ctx, UfoWatchWebView.class);
						Bundle bundle = Util.createBundleWebsite(feedItemUrl, feedItemTitle);
						i.putExtras(bundle);
						startActivity(i);
					}
				});
				Log.d(TAG, "onPostExecute adapter count: " + mListAdapter.getCount());
				mRssFeedListView.setAdapter(mListAdapter);
			}
			Dialog.dismiss();

		}
	}

}
