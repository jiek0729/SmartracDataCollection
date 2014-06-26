package com.smartracumn.smartracdatacollection.ui;

import java.util.HashMap;
import java.util.Map;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.smartracumn.smartracdatacollection.R;
import com.smartracumn.smartracdatacollection.model.SmartracServiceState;
import com.smartracumn.smartracdatacollection.ui.MainActivity.OnStateChangeListener;

public class ModeTrackingFragment extends Fragment {
	private final String TAG = getClass().getName();

	private Button update;

	private Button stop;

	private RadioGroup modes;

	private TextView serviceMonitor;

	private Map<Integer, String> modesMap;

	private OnStateChangeListener stateChangeListener = new OnStateChangeListener() {

		@Override
		public void onStateChanged(SmartracServiceState state) {
			// TODO Auto-generated method stub
			updateHeaderColor();
			updateMonitor(state);
		}
	};

	private void initializeModesMap() {
		modesMap = new HashMap<Integer, String>();
		modesMap.put(R.id.walk, "Walk");
		modesMap.put(R.id.bike, "Bike");
		modesMap.put(R.id.bus, "Bus");
		modesMap.put(R.id.rail, "Rail");
		modesMap.put(R.id.car, "Car");
		modesMap.put(R.id.dwelling, "Dwelling");
		modesMap.put(R.id.unknown, "Unknown");
		modesMap.put(R.id.wait, "Wait");
	}

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
		initializeModesMap();
		return inflater
				.inflate(R.layout.mode_tracking_layout, container, false);
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
		update = (Button) getView().findViewById(R.id.update_service);
		stop = (Button) getView().findViewById(R.id.stop_service);
		modes = (RadioGroup) getView().findViewById(R.id.mode_tracking_modes);
		serviceMonitor = (TextView) getView()
				.findViewById(R.id.service_monitor);

		if (getActivity() != null) {
			updateHeaderColor();
			SmartracServiceState state = ((MainActivity) getActivity())
					.getCurrentState();
			updateMonitor(state);
			((MainActivity) getActivity())
					.registerStateChangeListener(stateChangeListener);
			if (state.hasServiceRunning()) {
				setCheckedMode(state);
			}
		}

		if (getActivity() != null) {

		}

		update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (getActivity() != null) {
					confirmStart();
				}
			}
		});

		stop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (getActivity() != null) {
					((MainActivity) getActivity()).stopGpsService();
					((MainActivity) getActivity()).stopAccService();
				}
			}
		});
	}

	private void updateHeaderColor() {
		// TODO Auto-generated method stub
		if (getActivity() != null) {
			if (((MainActivity) getActivity()).getCurrentState()
					.hasServiceRunning()) {
				getView().findViewById(R.id.mode_tracking_header)
						.setBackgroundColor(
								getResources().getColor(R.color.green));
			} else {
				getView().findViewById(R.id.mode_tracking_header)
						.setBackgroundColor(
								getResources().getColor(R.color.red));
			}
		}
	}

	private void updateMonitor(SmartracServiceState state) {
		serviceMonitor.setText(state.toString());
	}

	private void setCheckedMode(SmartracServiceState state) {
		for (int k : modesMap.keySet()) {
			if (modesMap.get(k).equals(state.getMode())) {
				RadioButton button = (RadioButton) getView().findViewById(k);
				button.setChecked(true);
			}
		}

	}

	private void confirmStart() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());

		// set title
		alertDialogBuilder.setTitle("Confirm Mode");

		// set dialog message
		alertDialogBuilder
				.setMessage(
						"Are you sure you update mode and proceed to sampling?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, close
								// current activity
								((MainActivity) getActivity()).setMode(modesMap
										.get(modes.getCheckedRadioButtonId()));
								((MainActivity) getActivity()).startServices();
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
		if (getActivity() != null) {
			((MainActivity) getActivity())
					.unregisterStateChangeListener(stateChangeListener);
		}
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
