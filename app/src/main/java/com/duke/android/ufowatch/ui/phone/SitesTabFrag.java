package com.duke.android.ufowatch.ui.phone;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.duke.android.ufowatch.R;
import com.duke.android.ufowatch.common.Util;
import com.duke.android.ufowatch.ui.widget.CustomDialog;

public class SitesTabFrag extends Fragment {

	public static final String TAG = "SitesTabFrag";
	private String[] mUrlArray;
	private CustomDialog mListCustomDialog1;
	private String[] mStateReportsPagesArray;

	public static SitesTabFrag newInstance() {
		// Create a new fragment instance
		SitesTabFrag sitesTabFrag = new SitesTabFrag();
		return sitesTabFrag;
	}

	// @Override
	// public void onAttach(Activity activity) {
	// super.onAttach(activity);
	// // Log.d(TAG, "onAttach");
	// }

	// public void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	// Log.d(TAG, "onCreate");
	//
	// }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		// Log.d(TAG, "onCreateView");
		return inflater.inflate(R.layout.list_sites, container, false);

	}

	// @Override
	// public void onActivityCreated(Bundle savedInstanceState) {
	// super.onActivityCreated(savedInstanceState);
	// // Log.d(TAG, "onActivityCreated");
	// }

	@Override
	public void onStart() {
		super.onStart();
		// Log.d(TAG, "onStart");

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
		inflater.inflate(R.layout.list_sites, viewGroup);

		setupViews();

	}

	private void setupViews() {

		mUrlArray = getActivity().getApplicationContext().getResources().getStringArray(R.array.sitesUrlList);

		ListView list = (ListView) getActivity().findViewById(R.id.listviewsites);

		list.setAdapter(ArrayAdapter.createFromResource(getActivity(), R.array.sitesList, R.layout.list_item_standard));

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				String title = (String) ((TextView) view).getText();
				String url = mUrlArray[position];

				// if this is not the report by north american states
				// then go to the web page
				if (!title.equalsIgnoreCase(getResources().getString(R.string.title_report_nuforcstates))) {
					Intent i = Util.createIntentWebView(getActivity(), url, title);
					startActivity(i);
				} else { // otherwise popup the list of states dialog
					showStateNamesReportsDialog();
				}
			}
		});
	}

	protected void showStateNamesReportsDialog() {

		mStateReportsPagesArray = getActivity().getApplicationContext().getResources()
				.getStringArray(R.array.stateReportsPages);

		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.stateNamesReports,
				R.layout.list_item_simple);

		OnItemClickListener onItemClickListener2 = new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String stateName = (String) parent.getItemAtPosition(position);
				String stateReportPage = mStateReportsPagesArray[position];
				Intent i = Util.createIntentWebView(getActivity(),
						getResources().getString(R.string.url_report_nuforcstates) + stateReportPage + ".html",
						stateName + " " + getResources().getString(R.string.msg_sightings));
				startActivity(i);
				mListCustomDialog1.dismiss();
			}
		};

		mListCustomDialog1 = Util.createCustomDialogList(getActivity(),
				getActivity().getResources().getString(R.string.title_report_nuforcstates), adapter2,
				onItemClickListener2);

		mListCustomDialog1.show();

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
