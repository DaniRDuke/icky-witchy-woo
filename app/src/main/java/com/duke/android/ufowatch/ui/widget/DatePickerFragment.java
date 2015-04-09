package com.duke.android.ufowatch.ui.widget;

import java.lang.reflect.Field;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.duke.android.ufowatch.R;
import com.duke.android.ufowatch.common.Util;

public class DatePickerFragment extends DialogFragment {

	final String TAG = "DatePickerFragment";

	// Use the current date as the default date in the picker
	public static final Calendar c = Calendar.getInstance();
	public static final int year = c.get(Calendar.YEAR);
	public static final int month = c.get(Calendar.MONTH);
	public static final int day = c.get(Calendar.DAY_OF_MONTH);

	private OnDateSetListener mListener;

	// Jelly Bean introduced a bug in DatePickerDialog (and possibly
	// TimePickerDialog as well), and one of the possible solutions is
	// to postpone the creation of both the listener and the BUTTON_* .
	//
	// Passing a null here won't harm because DatePickerDialog checks for a
	// null
	// whenever it reads the listener that was passed here. >>> This seems
	// to be
	// true down to 1.5 / API 3, up to 4.1.1 / API 16. <<< No worries. For
	// now.
	//
	// See my own question and answer, and details I included for the issue:
	//
	// http://stackoverflow.com/a/11493752/489607
	// http://code.google.com/p/android/issues/detail?id=34833
	//
	// Of course, suggestions welcome.

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mListener = (OnDateSetListener) activity;
	}

	@Override
	public void onDetach() {
		this.mListener = null;
		super.onDetach();
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
        final DatePickerDialog dpDialog = new DatePickerDialog(getActivity(), getConstructorListener(), year, month,
                day);

        dpDialog.setCanceledOnTouchOutside(true);
        dpDialog.setCancelable(true);

        if (Util.isJellyBeanOrAbove()) {
            dpDialog.setButton(DialogInterface.BUTTON_POSITIVE, getActivity().getString(android.R.string.ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DatePicker dp = dpDialog.getDatePicker();
                            mListener.onDateSet(dp, dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
                        }
                    });
            dpDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getActivity().getString(android.R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
        }

        return dpDialog;
	}

	private OnDateSetListener getConstructorListener() {
		return Util.isJellyBeanOrAbove() ? null : mListener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setStyle(DialogFragment.STYLE_NORMAL, R.style.UfoDialog);
	}

}
