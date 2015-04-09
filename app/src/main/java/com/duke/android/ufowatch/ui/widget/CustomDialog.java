/**
 * 
 */
package com.duke.android.ufowatch.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.duke.android.ufowatch.R;
import com.duke.android.ufowatch.ui.adapter.YouTubeItemsArrayAdapter;

/**
 * Can handle a dialog with 1 or 2 lists.
 * 
 * @author Dani
 * 
 */
public class CustomDialog extends Dialog implements OnClickListener {

	private Context mCtx;
	private String mTitle;
	private TextView mTitleView;
	private ImageView mCloseImage;
	private OnItemClickListener mOnItemClickListener1;
	private ArrayAdapter<?> mAdapter1;
	private ListView mListView1;
	private OnItemClickListener mOnItemClickListener2;
	private ArrayAdapter<?> mAdapter2;
	private ListView mListView2;
	private TextView mListDividerView;

	/**
	 * @param context
	 * @param theme
	 */
	public CustomDialog(Context context, String title, ArrayAdapter<?> adapter1,
			OnItemClickListener onItemClickListener1, ArrayAdapter<?> adapter2, OnItemClickListener onItemClickListener2) {
		super(context);
		mCtx = context;
		mTitle = title;
		mAdapter1 = adapter1;
		mOnItemClickListener1 = onItemClickListener1;
		mAdapter2 = adapter2;
		mOnItemClickListener2 = onItemClickListener2;
	}

	/**
	 * @param context
	 * @param theme
	 */
	public CustomDialog(Context context, String title, YouTubeItemsArrayAdapter adapter1,
			OnItemClickListener onItemClickListener1) {
		super(context);
		mCtx = context;
		mTitle = title;
		mAdapter1 = adapter1;
		mOnItemClickListener1 = onItemClickListener1;
		mAdapter2 = null;
		mOnItemClickListener2 = null;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.dialog_image_close :
				dismiss();
				break;
			default :
				break;
		}
		dismiss();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setCancelable(true);

		LayoutInflater li = (LayoutInflater) mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// if there are two lists
		if (mAdapter2 != null && mOnItemClickListener2 != null) {
			setContentView(li.inflate(R.layout.dual_list_custom_dialog, null, false));
		} else {
			setContentView(li.inflate(R.layout.custom_dialog, null, false));
		}
		mCloseImage = (ImageView) findViewById(R.id.dialog_image_close);
		mCloseImage.setOnClickListener(this);
		mTitleView = (TextView) findViewById(R.id.dialog_title);
		mTitleView.setText(mTitle);
		mListView1 = (ListView) findViewById(R.id.dialog_listview1);
		mListView1.setAdapter(mAdapter1);
		mListView1.setOnItemClickListener(mOnItemClickListener1);

		// if there are two lists
		// set the second list
		if (mAdapter2 != null && mOnItemClickListener2 != null) {
			mListView2 = (ListView) findViewById(R.id.dialog_listview2);
			mListView2.setAdapter(mAdapter2);
			mListView2.setOnItemClickListener(mOnItemClickListener2);
			mListView2.setVisibility(View.VISIBLE);
			// show the list separator
			mListDividerView = (TextView) findViewById(R.id.dialog_list_divider);
			mListDividerView.setVisibility(View.VISIBLE);
		}

		final Resources res = mCtx.getResources();
		final int dialogTitleColor = res.getColor(R.color.ufo_tab_highlight);

		// Title
		final int titleId = res.getIdentifier("alertTitle", "id", "android");
		final View title = this.findViewById(titleId);
		if (title != null) {
			((TextView) title).setTextColor(dialogTitleColor);
		}

		// Title divider
		final int titleDividerId = res.getIdentifier("titleDivider", "id", "android");
		final View titleDivider = this.findViewById(titleDividerId);
		if (titleDivider != null) {
			titleDivider.setBackgroundColor(dialogTitleColor);
		}

	}

}
