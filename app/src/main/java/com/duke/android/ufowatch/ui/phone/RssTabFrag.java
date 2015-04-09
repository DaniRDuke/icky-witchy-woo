package com.duke.android.ufowatch.ui.phone;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.duke.android.ufowatch.R;
import com.duke.android.ufowatch.common.Util;

public class RssTabFrag extends Fragment {

	public static final String TAG = "RssTabFrag";
	private String[] mFeedsUrlArray;

	public static RssTabFrag newInstance() {
		RssTabFrag rssTabFrag = new RssTabFrag();
		return rssTabFrag;
	}

	// @Override
	// public void onAttach(Activity activity) {
	// super.onAttach(activity);
	// // Log.d(TAG, "onAttach");
	// }
	//
	// public void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	// //Log.d(TAG, "onCreate");
	//
	// }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		// Log.d(TAG, "onCreateView");
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.list_feeds, container, false);

	}

	// @Override
	// public void onActivityCreated(Bundle savedInstanceState) {
	// super.onActivityCreated(savedInstanceState);
	// // Log.d(TAG, "onActivityCreated");
	// }

	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");

		setupViews();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		populateViewForOrientation(inflater, (ViewGroup) getView());
	}

	private void populateViewForOrientation(LayoutInflater inflater, ViewGroup viewGroup) {
		viewGroup.removeAllViewsInLayout();
		inflater.inflate(R.layout.list_feeds, viewGroup);

		setupViews();

	}

	private void setupViews() {

		mFeedsUrlArray = getActivity().getApplicationContext().getResources().getStringArray(R.array.rssUrlList);

		ListView list = (ListView) getActivity().findViewById(R.id.listviewfeeds);

		list.setAdapter(ArrayAdapter.createFromResource(getActivity(), R.array.rssList, R.layout.list_item_standard));

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				String feedName = (String) ((TextView) view).getText();
				String feedUrl = mFeedsUrlArray[position];

				Intent i = new Intent(getActivity(), RssFeedList.class);
				Bundle bundle = Util.createBundleWebsite(feedUrl, feedName);
				i.putExtras(bundle);
				startActivity(i);

			}

		});

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
	// }
	//
	// @Override
	// public void onDestroy() {
	// super.onDestroy();
	// // Log.d(TAG, "onDestroy");
	// }

}
