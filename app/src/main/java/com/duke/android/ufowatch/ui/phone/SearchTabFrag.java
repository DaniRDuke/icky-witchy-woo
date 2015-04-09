package com.duke.android.ufowatch.ui.phone;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.duke.android.ufowatch.R;
import com.duke.android.ufowatch.common.SharedConstants;
import com.duke.android.ufowatch.common.Util;

public class SearchTabFrag extends Fragment implements OnClickListener {

	public static final String TAG = "SearchTabFrag";

	RadioGroup radGrpQueryEngine;
	RadioGroup radGrpQuerySource;
	RadioGroup radGrpQueryRange;
	// RadioGroup radGrpQuerySubject;

	RadioButton mRadioButton;

	// Button mSubmit;

	public static SearchTabFrag newInstance() {
		// Create a new fragment instance
		SearchTabFrag frag = new SearchTabFrag();
		return frag;
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
		return inflater.inflate(R.layout.engine_search, container, false);

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
	public void onClick(View v) {

		if (v instanceof RadioButton) {

			int viewId = v.getId();
			Log.d(TAG, "onClick() RadioButton clicked: " + ((RadioButton) v).getText());

			switch (viewId) {
			// SET SEARCH ENGINE PREFERENCE
				case R.id.radQryEngineGoogle :
					Log.d(TAG, "onClick() radQryEngineGoogle");
					Util.putSharedPreferenceString(getActivity(), SharedConstants.KEY_PREF_SEARCH_ENGINE,
							SharedConstants.PREF_SEARCH_ENGINE_GOOGLE);
					break;
				case R.id.radQryEngineBing :
					Log.d(TAG, "onClick() radQryEngineBing");
					Util.putSharedPreferenceString(getActivity(), SharedConstants.KEY_PREF_SEARCH_ENGINE,
							SharedConstants.PREF_SEARCH_ENGINE_BING);
					break;
				case R.id.radQryEngineYahoo :
					Log.d(TAG, "onClick() radQryEngineYahoo");
					Util.putSharedPreferenceString(getActivity(), SharedConstants.KEY_PREF_SEARCH_ENGINE,
							SharedConstants.PREF_SEARCH_ENGINE_YAHOO);
					break;
				// SET SOURCE PREFERENCE
				case R.id.radQrySourceNews :
					Log.d(TAG, "onClick() radQrySourceNews");
					Util.putSharedPreferenceString(getActivity(), SharedConstants.KEY_PREF_SEARCH_SRC,
							SharedConstants.PREF_SEARCH_SRC_NEWS);
					break;
				case R.id.radQrySourceWeb :
					Log.d(TAG, "onClick() radQrySourceWeb");
					Util.putSharedPreferenceString(getActivity(), SharedConstants.KEY_PREF_SEARCH_SRC,
							SharedConstants.PREF_SEARCH_SRC_WEB);
					break;
				case R.id.radQrySourceVideos :
					Log.d(TAG, "onClick() radQrySourceVideos");
					Util.putSharedPreferenceString(getActivity(), SharedConstants.KEY_PREF_SEARCH_SRC,
							SharedConstants.PREF_SEARCH_SRC_VIDEOS);
					break;
				// SET RANGE PREFERENCE
				case R.id.radQryRangeDay :
					Log.d(TAG, "onClick() radQryRangeDay");
					Util.putSharedPreferenceString(getActivity(), SharedConstants.KEY_PREF_SEARCH_RANGE,
							SharedConstants.PREF_SEARCH_RANGE_24HRS);
					break;
				case R.id.radQryRangeWeek :
					Log.d(TAG, "onClick() radQryRangeWeek");
					Util.putSharedPreferenceString(getActivity(), SharedConstants.KEY_PREF_SEARCH_RANGE,
							SharedConstants.PREF_SEARCH_RANGE_WEEK);
					break;
				// SET SUBJECT PREFERENCE
				/*
				 * case R.id.radQrySubjectSightings : Log.d(TAG,
				 * "onClick() radQrySubjectSightings");
				 * Util.putSharedPreferenceString(getActivity(),
				 * SharedConstants.KEY_PREF_SEARCH_SUBJECT,
				 * SharedConstants.PREF_SEARCH_SUBJECT_SIGHTINGS); break; case
				 * R.id.radQrySubjectBeings : Log.d(TAG,
				 * "onClick() radQrySubjectBeings");
				 * Util.putSharedPreferenceString(getActivity(),
				 * SharedConstants.KEY_PREF_SEARCH_SUBJECT,
				 * SharedConstants.PREF_SEARCH_SUBJECT_BEINGS); break;
				 */
				default :
					break;
			}
		} else if (v instanceof Button) {
			Log.d(TAG, "onClick() Button clicked");
			// get the preferences and create the url and title
			String title = "";
			String baseUrl = "";
			String qry = "";

			String engine = Util.getSharedPreferenceString(getActivity(), SharedConstants.KEY_PREF_SEARCH_ENGINE,
					SharedConstants.PREF_SEARCH_ENGINE_GOOGLE);
			String source = Util.getSharedPreferenceString(getActivity(), SharedConstants.KEY_PREF_SEARCH_SRC,
					SharedConstants.PREF_SEARCH_SRC_NEWS);
			String subject = Util.getSharedPreferenceString(getActivity(), SharedConstants.KEY_PREF_SEARCH_SUBJECT,
					SharedConstants.PREF_SEARCH_SUBJECT_SIGHTINGS);
			String range = Util.getSharedPreferenceString(getActivity(), SharedConstants.KEY_PREF_SEARCH_RANGE,
					SharedConstants.PREF_SEARCH_RANGE_24HRS);

			// GOOGLE
			if (SharedConstants.PREF_SEARCH_ENGINE_GOOGLE.equalsIgnoreCase(engine)) {
				title = "Google";
				// GET SUBJECT OF SEARCH
				if (SharedConstants.PREF_SEARCH_SUBJECT_SIGHTINGS.equalsIgnoreCase(subject)) {
					qry = SharedConstants.SEARCH_GOOGLE_SUBJECT_SIGHTINGS;
				} else {
					qry = SharedConstants.SEARCH_GOOGLE_SUBJECT_BEINGS;
				}

				// IF SEARCHING NEWS
				if (SharedConstants.PREF_SEARCH_SRC_NEWS.equalsIgnoreCase(source)) {
					// USE BASE NEWS URL
					baseUrl = SharedConstants.SEARCH_GOOGLE_BASE_URL_NEWS;
					if (SharedConstants.PREF_SEARCH_RANGE_24HRS.equalsIgnoreCase(range)) {
						qry += SharedConstants.SEARCH_GOOGLE_QRY_NEWS_24HRS;
					} else {
						qry += SharedConstants.SEARCH_GOOGLE_QRY_NEWS_WEEK;
					}
				} else if (SharedConstants.PREF_SEARCH_SRC_WEB.equalsIgnoreCase(source)) {
					baseUrl = SharedConstants.SEARCH_GOOGLE_BASE_URL_WEB;
					if (SharedConstants.PREF_SEARCH_RANGE_24HRS.equalsIgnoreCase(range)) {
						qry += SharedConstants.SEARCH_GOOGLE_QRY_WEB_24HRS;
					} else {
						qry += SharedConstants.SEARCH_GOOGLE_QRY_WEB_WEEK;
					}
				} else if (SharedConstants.PREF_SEARCH_SRC_VIDEOS.equalsIgnoreCase(source)) {
					baseUrl = SharedConstants.SEARCH_GOOGLE_BASE_URL_VIDEOS;
					if (SharedConstants.PREF_SEARCH_RANGE_24HRS.equalsIgnoreCase(range)) {
						qry += SharedConstants.SEARCH_GOOGLE_QRY_VIDEOS_24HRS;
					} else {
						qry += SharedConstants.SEARCH_GOOGLE_QRY_VIDEOS_WEEK;
					}
				}
				// BING
			} else if (SharedConstants.PREF_SEARCH_ENGINE_BING.equalsIgnoreCase(engine)) {
				title = "Bing";
				if (SharedConstants.PREF_SEARCH_SUBJECT_SIGHTINGS.equalsIgnoreCase(subject)) {
					qry = SharedConstants.SEARCH_BING_SUBJECT_SIGHTINGS;
				} else {
					qry = SharedConstants.SEARCH_BING_SUBJECT_BEINGS;
				}
				if (SharedConstants.PREF_SEARCH_SRC_NEWS.equalsIgnoreCase(source)) {
					baseUrl = SharedConstants.SEARCH_BING_BASE_URL_NEWS;
					if (SharedConstants.PREF_SEARCH_RANGE_24HRS.equalsIgnoreCase(range)) {
						qry += SharedConstants.SEARCH_BING_QRY_NEWS_24HRS;
					} else {
						qry += SharedConstants.SEARCH_BING_QRY_NEWS_WEEK;
					}
				} else if (SharedConstants.PREF_SEARCH_SRC_WEB.equalsIgnoreCase(source)) {
					baseUrl = SharedConstants.SEARCH_BING_BASE_URL_WEB;
					if (SharedConstants.PREF_SEARCH_RANGE_24HRS.equalsIgnoreCase(range)) {
						qry += SharedConstants.SEARCH_BING_QRY_WEB_24HRS;
					} else {
						qry += SharedConstants.SEARCH_BING_QRY_WEB_WEEK;
					}
				} else if (SharedConstants.PREF_SEARCH_SRC_VIDEOS.equalsIgnoreCase(source)) {
					baseUrl = SharedConstants.SEARCH_GOOGLE_BASE_URL_VIDEOS;
					if (SharedConstants.PREF_SEARCH_RANGE_24HRS.equalsIgnoreCase(range)) {
						qry += SharedConstants.SEARCH_GOOGLE_QRY_VIDEOS_24HRS;
					} else {
						qry += SharedConstants.SEARCH_GOOGLE_QRY_VIDEOS_WEEK;
					}
				}
			}

			Log.d(TAG, "onClick() TITLE:" + title);
			Log.d(TAG, "onClick() URL: " + baseUrl + qry);

			// START THE ACTIVITY
			Intent i;
			i = Util.createIntentWebView(getActivity(), baseUrl + qry, title);
			startActivity(i);

		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		populateViewForOrientation(inflater, (ViewGroup) getView());
	}

	private void populateViewForOrientation(LayoutInflater inflater, ViewGroup viewGroup) {
		viewGroup.removeAllViewsInLayout();
		inflater.inflate(R.layout.engine_search, viewGroup);

		setupViews();

	}

	private void setupViews() {

		radGrpQueryEngine = (RadioGroup) getActivity().findViewById(R.id.radGrpQryEngine);
		radGrpQuerySource = (RadioGroup) getActivity().findViewById(R.id.radGrpQrySource);
		radGrpQueryRange = (RadioGroup) getActivity().findViewById(R.id.radGrpQryRange);
		// radGrpQuerySubject = (RadioGroup)
		// getActivity().findViewById(R.id.radGrpQrySubject);

		String engine = Util.getSharedPreferenceString(getActivity(), SharedConstants.KEY_PREF_SEARCH_ENGINE,
				SharedConstants.PREF_SEARCH_ENGINE_GOOGLE);
		String source = Util.getSharedPreferenceString(getActivity(), SharedConstants.KEY_PREF_SEARCH_SRC,
				SharedConstants.PREF_SEARCH_SRC_NEWS);
		// String subject = Util.getSharedPreferenceString(getActivity(),
		// SharedConstants.KEY_PREF_SEARCH_SUBJECT,
		// SharedConstants.PREF_SEARCH_SUBJECT_SIGHTINGS);
		String range = Util.getSharedPreferenceString(getActivity(), SharedConstants.KEY_PREF_SEARCH_RANGE,
				SharedConstants.PREF_SEARCH_RANGE_24HRS);

		// Add click listeners to the radio buttons and select the preferred
		// ones

		// ENGINES
		mRadioButton = (RadioButton) getActivity().findViewById(R.id.radQryEngineGoogle);
		if (SharedConstants.PREF_SEARCH_ENGINE_GOOGLE.equalsIgnoreCase(engine)) {
			radGrpQueryEngine.check(R.id.radQryEngineGoogle);
		}
		mRadioButton.setOnClickListener(this);

		mRadioButton = (RadioButton) getActivity().findViewById(R.id.radQryEngineBing);
		if (SharedConstants.PREF_SEARCH_ENGINE_BING.equalsIgnoreCase(engine)) {
			radGrpQueryEngine.check(R.id.radQryEngineBing);
		}
		mRadioButton.setOnClickListener(this);

		/*
		 * mRadioButton = (RadioButton)
		 * getActivity().findViewById(R.id.radQryEngineYahoo); if
		 * (SharedConstants.PREF_SEARCH_ENGINE_YAHOO.equalsIgnoreCase(engine)) {
		 * radGrpQueryEngine.check(R.id.radQryEngineYahoo); }
		 * mRadioButton.setOnClickListener(this);
		 */

		// SOURCES
		mRadioButton = (RadioButton) getActivity().findViewById(R.id.radQrySourceNews);
		if (SharedConstants.PREF_SEARCH_SRC_NEWS.equalsIgnoreCase(source)) {
			radGrpQuerySource.check(R.id.radQrySourceNews);
		}
		mRadioButton.setOnClickListener(this);

		mRadioButton = (RadioButton) getActivity().findViewById(R.id.radQrySourceWeb);
		if (SharedConstants.PREF_SEARCH_SRC_WEB.equalsIgnoreCase(source)) {
			radGrpQuerySource.check(R.id.radQrySourceWeb);
		}
		mRadioButton.setOnClickListener(this);

		mRadioButton = (RadioButton) getActivity().findViewById(R.id.radQrySourceVideos);
		if (SharedConstants.PREF_SEARCH_SRC_VIDEOS.equalsIgnoreCase(source)) {
			radGrpQuerySource.check(R.id.radQrySourceVideos);
		}
		mRadioButton.setOnClickListener(this);

		// RANGES
		mRadioButton = (RadioButton) getActivity().findViewById(R.id.radQryRangeDay);
		if (SharedConstants.PREF_SEARCH_RANGE_24HRS.equalsIgnoreCase(range)) {
			radGrpQueryRange.check(R.id.radQryRangeDay);
		}
		mRadioButton.setOnClickListener(this);

		mRadioButton = (RadioButton) getActivity().findViewById(R.id.radQryRangeWeek);
		if (SharedConstants.PREF_SEARCH_RANGE_WEEK.equalsIgnoreCase(range)) {
			radGrpQueryRange.check(R.id.radQryRangeWeek);
		}
		mRadioButton.setOnClickListener(this);

		// SUBJECTS
		/*
		 * mRadioButton = (RadioButton)
		 * getActivity().findViewById(R.id.radQrySubjectSightings); if
		 * (SharedConstants
		 * .PREF_SEARCH_SUBJECT_SIGHTINGS.equalsIgnoreCase(subject)) {
		 * radGrpQuerySubject.check(R.id.radQrySubjectSightings); }
		 * mRadioButton.setOnClickListener(this);
		 */
		/*
		 * mRadioButton = (RadioButton)
		 * getActivity().findViewById(R.id.radQrySubjectBeings); if
		 * (SharedConstants
		 * .PREF_SEARCH_SUBJECT_BEINGS.equalsIgnoreCase(subject)) {
		 * radGrpQuerySubject.check(R.id.radQrySubjectBeings); }
		 * mRadioButton.setOnClickListener(this);
		 */
		// SUBMIT
		// mSubmit = (Button) getActivity().findViewById(R.id.submit);
		// mSubmit.setOnClickListener(this);

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
