package com.smartracumn.smartracdatacollection.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import com.smartracumn.smartracdatacollection.R;

public class SamplingRateSettingFragment extends Fragment {
	private final String TAG = getClass().getName();

	private Button next;

	private RadioGroup gpsSamplingGroup;
	private RadioGroup gpsFileGroup;
	private RadioGroup accSamplingGroup;
	private RadioGroup accFileGroup;

	@Override
	public void onAttach(Activity activity) {
		Log.i(TAG, getClass().getSimpleName() + ":entered onAttach()");
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, getClass().getSimpleName() + ":entered onCreate()");
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, getClass().getSimpleName() + ":entered onCreateView()");
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.sampling_rate_setting_layout,
				container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i(TAG, getClass().getSimpleName() + ":entered onActivityCreated()");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		Log.i(TAG, getClass().getSimpleName() + ":entered onStart()");
		super.onStart();
		next = (Button) getView().findViewById(R.id.sampling_setting_next);
		gpsSamplingGroup = (RadioGroup) getView().findViewById(
				R.id.gps_sampling_rate);
		gpsFileGroup = (RadioGroup) getView()
				.findViewById(R.id.gps_filing_rate);
		accSamplingGroup = (RadioGroup) getView().findViewById(
				R.id.acc_sampling_rate);
		accFileGroup = (RadioGroup) getView()
				.findViewById(R.id.acc_filing_rate);
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (getActivity() != null) {
					confirmSettings();
				}
			}
		});
	}

	private void confirmSettings() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());

		// set title
		alertDialogBuilder.setTitle("Confirm Sampling Settings");

		// set dialog message
		alertDialogBuilder
				.setMessage(
						"Are you sure you wanna proceed with this samping settings?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, close
								// current activity
								((MainActivity) getActivity()).setRates(
										gpsSamplingGroup
												.getCheckedRadioButtonId(),
										gpsFileGroup.getCheckedRadioButtonId(),
										accSamplingGroup
												.getCheckedRadioButtonId(),
										accFileGroup.getCheckedRadioButtonId());

								((MainActivity) getActivity())
										.gotoModeTracking();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	@Override
	public void onResume() {
		Log.i(TAG, getClass().getSimpleName() + ":entered onResume()");
		super.onResume();
	}

	@Override
	public void onPause() {
		Log.i(TAG, getClass().getSimpleName() + ":entered onPause()");
		super.onPause();
	}

	@Override
	public void onStop() {
		Log.i(TAG, getClass().getSimpleName() + ":entered onStop()");
		super.onStop();
	}

	@Override
	public void onDetach() {
		Log.i(TAG, getClass().getSimpleName() + ":entered onDetach()");
		super.onDetach();
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, getClass().getSimpleName() + ":entered onDestroy()");
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		Log.i(TAG, getClass().getSimpleName() + ":entered onDestroyView()");
		super.onDestroyView();
	}
}
