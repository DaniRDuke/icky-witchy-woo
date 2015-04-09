package com.duke.android.ufowatch.ui.tablet;

import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duke.android.ufowatch.R;
import com.duke.android.ufowatch.UfoWatchMain;
import com.duke.android.ufowatch.common.AtomXmlHandler;
import com.duke.android.ufowatch.common.SharedConstants;
import com.duke.android.ufowatch.common.Util;
import com.duke.android.ufowatch.domain.FeedStructure;
import com.duke.android.ufowatch.ui.UfoWatchWebView;
import com.duke.android.ufowatch.ui.adapter.BlogReaderGridAdapter;

@SuppressLint("NewApi")
public class BlogFeedGridFrag extends Fragment {
	/** Called when the activity is first created. */

	public static final String TAG = "BlogFeedGridFrag";

	private GridView mBlogGridView;
	List<FeedStructure> rssStr;
	private BlogReaderGridAdapter mGridAdapter;
	private String mBlogUrl;

	public static BlogFeedGridFrag newInstance() {
		BlogFeedGridFrag reader = new BlogFeedGridFrag();
		return reader;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = this.getArguments();

		mBlogUrl = extras != null ? extras.getString(SharedConstants.KEY_URL) : null;

		if (savedInstanceState != null) {
			mBlogUrl = savedInstanceState.getString(SharedConstants.KEY_URL);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.feed_grid_view, container, false);
	}

	@Override
	public void onResume() {
		super.onResume();
		// Log.d(this.TAG, "onResume");
		((UfoWatchMain) getActivity()).removeMenuOptions();
		((UfoWatchMain) getActivity()).invalidateOptionsMenu();
	}

	@Override
	public void onStart() {
		super.onStart();
		// Log.d(TAG, "onStart");
		mBlogGridView = (GridView) getActivity().findViewById(R.id.feed_grid);
		BlogFeedTask rssTask = new BlogFeedTask(getActivity());
		rssTask.execute();

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putString(SharedConstants.KEY_URL, mBlogUrl);
	}

	private class BlogFeedTask extends AsyncTask<String, Void, String> {
		Context ctx = null;
		private ProgressDialog Dialog;
		String response = "";

		public BlogFeedTask(Context context) {
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
				AtomXmlHandler rh = new AtomXmlHandler();
				rssStr = rh.getLatestArticles(mBlogUrl);
				Iterator<FeedStructure> iter = rssStr.iterator();
				while (iter.hasNext()) {
					String link = iter.next().getLink();
					if (link == null || link.length() == 0)
						iter.remove();
				}
			} catch (Exception e) {
				Log.e(TAG, "Exception getting latest blogs. " + e);
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			if (rssStr != null) {
				mGridAdapter = new BlogReaderGridAdapter(getActivity(), rssStr);
				mBlogGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

				mBlogGridView.setAdapter(mGridAdapter);
			}
			Dialog.dismiss();
		}
	}

}