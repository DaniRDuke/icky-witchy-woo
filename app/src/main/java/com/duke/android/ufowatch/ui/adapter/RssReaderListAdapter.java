package com.duke.android.ufowatch.ui.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.duke.android.ufowatch.R;
import com.duke.android.ufowatch.common.Util;
import com.duke.android.ufowatch.domain.FeedStructure;

public class RssReaderListAdapter extends ArrayAdapter<FeedStructure> {

	public static final String TAG = "RssReaderListAdapter";
	List<FeedStructure> imageAndTexts1 = null;

	public RssReaderListAdapter(Activity activity, List<FeedStructure> imageAndTexts) {
		super(activity, 0, imageAndTexts);
		imageAndTexts1 = imageAndTexts;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.feed_layout, null);
		TextView textView = (TextView) rowView.findViewById(R.id.feed_text);
		textView.setTag(imageAndTexts1.get(position).getLink());
		textView.setText(imageAndTexts1.get(position).getTitle());

		String pubDate = Util.formatStandardDateTimeToDateOnly(imageAndTexts1.get(position).getPubDate());

		TextView timeFeedText = (TextView) rowView.findViewById(R.id.feed_updatetime);
		timeFeedText.setText(pubDate);

		return rowView;

	}

}