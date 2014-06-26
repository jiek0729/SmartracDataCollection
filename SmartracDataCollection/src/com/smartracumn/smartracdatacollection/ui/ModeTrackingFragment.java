package com.smartracumn.smartracdatacollection.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.smartracumn.smartracdatacollection.R;
import com.smartracumn.smartracdatacollection.model.SmartracServiceState;
import com.smartracumn.smartracdatacollection.ui.MainActivity.OnStateChangeListener;
import com.smartracumn.smartracdatacollection.util.SmartracDataFormat;

public class ModeTrackingFragment extends Fragment {
	private final String TAG = getClass().getName();

	private Button update;

	private Button stop;

	private RadioGroup modes;

	private TextView serviceMonitor;

	private EditText note;

	private Map<Integer, String> modesMap;

	private String imei;

	private String getImei() {
		if (imei == null) {
			TelephonyManager telephonyManager = (TelephonyManager) getActivity()
					.getSystemService(Context.TELEPHONY_SERVICE);
			imei = telephonyManager.getDeviceId().toString();
		}

		return imei;
	}

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
		note = (EditText) getView().findViewById(R.id.mode_tracking_note);

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
					confirmStop();
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

	private void confirmStop() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());

		// set title
		alertDialogBuilder.setTitle("Confirm Mode");

		// set dialog message
		alertDialogBuilder
				.setMessage("Are you sure you wanna stop sampling?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, close
								// current activity
								((MainActivity) getActivity()).stopServices();
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

	private void confirmStart() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());

		// set title
		alertDialogBuilder.setTitle("Confirm Mode");

		// set dialog message
		alertDialogBuilder
				.setMessage(
						"Are you sure you wanna update mode and proceed to sampling?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, close
								// current activity
								((MainActivity) getActivity())
										.getCurrentState()
										.setMode(
												modesMap.get(modes
														.getCheckedRadioButtonId()));
								((MainActivity) getActivity()).startServices();
								new WriteFileTask()
										.execute(((MainActivity) getActivity())
												.getCurrentState());
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

	private class WriteFileTask extends
			AsyncTask<SmartracServiceState, Void, Void> {

		private File getDirectory() {
			File myDir = new File(Environment.getExternalStorageDirectory()
					.getPath() + "/SMDataCollection_data");
			if (!myDir.exists()) {
				myDir.mkdir();
			}

			if (myDir.exists()) {
				String fileName = myDir.getPath()
						+ "/"
						+ new SmartracDataFormat().getFileName(getImei(),
								new Date(), "Mode and Note.txt");
				return new File(fileName);
			}

			return null;
		}

		@Override
		protected Void doInBackground(SmartracServiceState... params) {
			// TODO Auto-generated method stub
			File file = getDirectory();
			if (file != null) {
				Log.i(TAG, getClass().getSimpleName() + "write to file: "
						+ file.getPath());
				try {
					boolean writeHeader = !file.exists();
					FileWriter fw = new FileWriter(file, true);
					BufferedWriter bw = new BufferedWriter(fw);
					PrintWriter pw = new PrintWriter(bw);
					if (writeHeader) {
						pw.println(new SmartracDataFormat().getModeHeader());
					}

					pw.println(new SmartracDataFormat().formatModeAndNote(
							new Date(), params[0].getMode(), note.getText()
									.toString()));

					pw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			note.setText("");

			Toast.makeText(getActivity(), "Mode and Note Updated",
					Toast.LENGTH_SHORT).show();
		}
	}
}
