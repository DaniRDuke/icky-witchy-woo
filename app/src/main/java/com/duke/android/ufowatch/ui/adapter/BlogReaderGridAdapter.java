package com.duke.android.ufowatch.ui.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.duke.android.ufowatch.R;
import com.duke.android.ufowatch.common.Util;
import com.duke.android.ufowatch.domain.FeedStructure;

public class BlogReaderGridAdapter extends ArrayAdapter<FeedStructure> {

	public static final String TAG = "BlogReaderListAdapter";
	List<FeedStructure> imageAndTexts1 = null;
	Context ctx;

	public BlogReaderGridAdapter(Activity activity, List<FeedStructure> imageAndTexts) {
		super(activity, 0, imageAndTexts);
		imageAndTexts1 = imageAndTexts;
		ctx = activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.feed_layout, null);
		TextView textView = (TextView) rowView.findViewById(R.id.feed_text);
		// set the tag to the URL link to the item
		textView.setTag(imageAndTexts1.get(position).getLink());

		String title = imageAndTexts1.get(position).getTitle();
		if (title.length() > ctx.getResources().getInteger(R.integer.feed_title_char_limit)) {
			title = title.substring(0, ctx.getResources().getInteger(R.integer.feed_title_char_limit));
			title = title.substring(0, title.lastIndexOf(' ')) + "...";
		}
		textView.setText(title);

		TextView timeFeedText = (TextView) rowView.findViewById(R.id.feed_updatetime);
		String pubDate = imageAndTexts1.get(position).getPubDate();
		pubDate = Util.formatStandardDateTimeToDateOnly(pubDate);
		timeFeedText.setText(pubDate);

		return rowView;

	}

}