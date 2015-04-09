/**
 * 
 */
package com.duke.android.ufowatch.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.duke.android.ufowatch.R;
import com.duke.android.ufowatch.common.SharedConstants;
import com.duke.android.ufowatch.ui.YouTubeCaller;

/**
 * @author Dani
 * 
 */
public class CustomDialogNewPlaylist extends Dialog implements OnClickListener {

	private Context mCtx;
	private ImageView mCloseImage;
	private Button mCreateButton;
	private Button mCancelButton;
	private EditText mEditText;

	/**
	 * @param context
	 * @param theme
	 */
	public CustomDialogNewPlaylist(Context context) {
		super(context);
		mCtx = context;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.dialog_image_close :
				break;
			case R.id.createPlaylist :
				Intent i;
				Bundle bundle = new Bundle();
				bundle.putString(SharedConstants.KEY_YOUTUBE_CHOICE, SharedConstants.NEW_PLAYLIST_TITLE);
				bundle.putString(SharedConstants.NEW_PLAYLIST_TITLE, mEditText.getText().toString());
				i = new Intent(mCtx, YouTubeCaller.class);
				i.putExtras(bundle);
				mCtx.startActivity(i);
				break;
			case R.id.cancelPlaylist :
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
		setContentView(li.inflate(R.layout.custom_dialog_new_playlist, null, false));

		mEditText = (EditText) findViewById(R.id.playlistTitle);
		// mEditText.setFocusableInTouchMode(true);
		// mEditText.requestFocus();

		mCloseImage = (ImageView) findViewById(R.id.dialog_image_close);
		mCloseImage.setOnClickListener(this);
		mCreateButton = (Button) findViewById(R.id.createPlaylist);
		mCreateButton.setOnClickListener(this);
		mCancelButton = (Button) findViewById(R.id.cancelPlaylist);
		mCancelButton.setOnClickListener(this);

	}

}
