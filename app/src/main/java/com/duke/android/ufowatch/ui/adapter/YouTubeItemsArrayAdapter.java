package com.duke.android.ufowatch.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.duke.android.ufowatch.R;
import com.duke.android.ufowatch.domain.YouTubeItem;

public class YouTubeItemsArrayAdapter extends ArrayAdapter<YouTubeItem> {

	private ArrayList<YouTubeItem> mItems;
	private Context mCtx;

	public YouTubeItemsArrayAdapter(Context context, int textViewResourceId, ArrayList<YouTubeItem> items) {
		super(context, textViewResourceId, items);
		mItems = items;
		mCtx = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_item_large, null);
		}

		YouTubeItem ri = mItems.get(position);
		TextView tv;

		if (ri != null) {
			tv = (TextView) v.findViewById(R.id.listRow);
			// tv.setText(Util.createSubLabelSpannable(ri.getTitle(), null,
			// ri.getDescription()).toString());
			tv.setText(ri.getTitle());
			tv.setTag(ri.getId());
		}
		return v;
	}
}
