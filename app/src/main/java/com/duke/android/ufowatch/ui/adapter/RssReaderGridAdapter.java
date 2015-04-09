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

public class RssReaderGridAdapter extends ArrayAdapter<FeedStructure> {

	public static final String TAG = "RssReaderListAdapter";
	List<FeedStructure> imageAndTexts1 = null;
	Context ctx;

	public RssReaderGridAdapter(Activity activity, List<FeedStructure> imageAndTexts) {
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

		// UrlImageView thumb = (UrlImageView)
		// rowView.findViewById(R.id.feedThumbImageView);
		// thumb.setVisibility(View.GONE);

		// try {

		String title = imageAndTexts1.get(position).getTitle();
		if (title.length() > ctx.getResources().getInteger(R.integer.feed_title_char_limit)) {
			title = title.substring(0, ctx.getResources().getInteger(R.integer.feed_title_char_limit));
			title = title.substring(0, title.lastIndexOf(' ')) + "...";
		}
		// SpannableString content = new
		// SpannableString(imageAndTexts1.get(position).getPubDate());
		// content.setSpan(new UnderlineSpan(), 0, 13, 0);
		textView.setText(title);

		TextView timeFeedText = (TextView) rowView.findViewById(R.id.feed_updatetime);
		String pubDate = imageAndTexts1.get(position).getPubDate();
		pubDate = Util.formatStandardDateTimeToDateOnly(pubDate);
		timeFeedText.setText(pubDate);

		/*
		 * if (imageAndTexts1.get(position).getImgLink() != null) { String
		 * imgUrl = imageAndTexts1.get(position).getImgLink().toString(); URL
		 * feedImage = new URL(imgUrl); // String imageString =
		 * feedImage.toString(); // Log.d(TAG, "imageString " + imageString); if
		 * (!feedImage.toString().equalsIgnoreCase("null")) {
		 * thumb.setImageDrawable(imgUrl); thumb.setVisibility(View.VISIBLE); }
		 * }
		 * 
		 * } catch (MalformedURLException e) { }
		 */
		return rowView;

	}

}