package com.duke.android.ufowatch.ui.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import com.duke.android.ufowatch.R;
import com.duke.android.ufowatch.ui.viewpager.extensions.SwipeyTabButton;
import com.duke.android.ufowatch.ui.viewpager.extensions.TabsAdapter;

public class UfoTabsAdapter implements TabsAdapter {

	public static final String TAG = "UfoTabsAdapter";

	private Activity mContext;

	public static final int NUMBER_OF_TABS = 5;

	private String[] mTitles = new String[NUMBER_OF_TABS];

	public UfoTabsAdapter(Activity ctx) {
		this.mContext = ctx;
		mTitles[0] = mContext.getString(R.string.title_tab_video);
		mTitles[1] = mContext.getString(R.string.title_tab_search_engines);
		mTitles[2] = mContext.getString(R.string.title_tab_feeds);
		mTitles[3] = mContext.getString(R.string.title_tab_blogs);
		mTitles[4] = mContext.getString(R.string.title_tab_sites);
	}

	@Override
	public View getView(int position) {
		// Log.d(TAG, "getView() position:" + position);

		SwipeyTabButton tab;

		LayoutInflater inflater = mContext.getLayoutInflater();
		tab = (SwipeyTabButton) inflater.inflate(R.layout.tab_swipey, null);

		// set the line and text color for the SwipeyTabButton in view
		// (selected)
		tab.setTextColorCenter(mContext.getResources().getColor(R.color.ufo_tab_highlight));
		tab.setLineColorCenter(mContext.getResources().getColor(R.color.ufo_tab_highlight));

		if (position < mTitles.length) {
			// Log.d(TAG, "getView() title text:" + mTitles[position] +
			// " at position:" + position);
			tab.setText(mTitles[position]);
		}

		return tab;

	}

}
