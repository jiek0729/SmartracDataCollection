package com.smartracumn.smartracdatacollection.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.smartracumn.smartracdatacollection.R;
import com.smartracumn.smartracdatacollection.model.SmartracServiceState;
import com.smartracumn.smartracdatacollection.service.AccService;
import com.smartracumn.smartracdatacollection.service.GpsService;

public class MainActivity extends FragmentActivity {
	private final String TAG = getClass().getName();

	private SmartracServiceState currentState;

	private OnStateChangeListener stateChangeListener;

	public interface OnStateChangeListener {
		void onStateChanged(SmartracServiceState state);
	}

	public void setCurrentState(SmartracServiceState state) {
		currentState = state;
		if (stateChangeListener != null) {
			stateChangeListener.onStateChanged(currentState);
		}
	}

	public SmartracServiceState getCurrentState() {
		return currentState;
	}

	public void registerStateChangeListener(OnStateChangeListener listener) {
		this.stateChangeListener = listener;
	}

	public void unregisterStateChangeListener(OnStateChangeListener listener) {
		if (this.stateChangeListener.equals(listener)) {
			this.stateChangeListener = null;
		}
	}

	public void setRates(int gpsRate, int gpsFileRate, int accRate,
			int accFileRate) {
		currentState.setGpsRate(gpsRate, gpsFileRate);
		currentState.setAccRate(accRate, accFileRate);
	}

	public void startGpsService() {
		if (currentState.getGpsRate() > 0) {
			Intent service = new Intent(this, GpsService.class);
			service.putExtra(GpsService.GPS_SAMPLING_RATE,
					currentState.getGpsRate());
			service.putExtra(GpsService.GPS_WRITE_FILE_RATE,
					currentState.getGpsFileRate());
			this.startService(service);
		}
	}

	public void stopGpsService() {
		this.stopService(new Intent(this, GpsService.class));
	}

	public void startAccService() {
		if (currentState.getAccRate() > 0) {
			Intent service = new Intent(this, AccService.class);
			service.putExtra(AccService.ACC_SAMPLING_RATE,
					currentState.getAccRate());
			service.putExtra(AccService.ACC_WRITE_FILE_RATE,
					currentState.getAccFileRate());
			this.startService(service);
		}
	}

	public void stopAccService() {
		this.stopService(new Intent(this, AccService.class));
	}

	public void startServices() {
		if (!currentState.hasServiceRunning()) {
			startGpsService();
			startAccService();
			SmartracServiceState state = new SmartracServiceState(
					currentState.getGpsRate() > 0,
					currentState.getAccRate() > 0);
			state.setGpsRate(currentState.getGpsRate(),
					currentState.getGpsFileRate());
			state.setAccRate(currentState.getAccRate(),
					currentState.getAccFileRate());
			state.setMode(currentState.getMode());
			setCurrentState(state);
			Toast.makeText(getApplicationContext(), "Service Started",
					Toast.LENGTH_LONG);
		}
	}

	public void stopServices() {
		if (currentState.hasServiceRunning()) {
			stopGpsService();
			stopAccService();

			SmartracServiceState state = new SmartracServiceState(false, false);

			setCurrentState(state);
			gotoSamplingSetting();
			Toast.makeText(getApplicationContext(), "Service Stopped",
					Toast.LENGTH_LONG);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, getClass().getSimpleName() + " onCreate()");
		setContentView(R.layout.main_activity);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, getClass().getSimpleName() + " onStart()");

		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.main_container, new PrepareFragment(),
						"prepare fragment").commit();
	}

	public void gotoSamplingSetting() {
		getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.enter, R.anim.exit)
				.replace(R.id.main_container,
						new SamplingRateSettingFragment(),
						"sampling rate setting fragment").addToBackStack(null)
				.commit();
	}

	public void gotoModeTracking() {
		getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.enter, R.anim.exit)
				.replace(R.id.main_container, new ModeTrackingFragment(),
						"mode tracking fragment").addToBackStack(null).commit();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.i(TAG, getClass().getSimpleName() + " onRestart()");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, getClass().getSimpleName() + " onResume()");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, getClass().getSimpleName() + " onPause()");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i(TAG, getClass().getSimpleName() + " onStop()");
		saveServiceState();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, getClass().getSimpleName() + " onDestroy()");
	}

	private void saveServiceState() {
		new WriteFileTask().execute();
	}

	private class WriteFileTask extends AsyncTask<Void, Void, Void> {

		private String getDirectory() {
			File myDir = new File(Environment.getExternalStorageDirectory()
					.getPath() + "/SMDataCollection_data");
			if (!myDir.exists()) {
				myDir.mkdir();
			}

			if (myDir.exists()) {
				return myDir.getPath();
			}

			return null;
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String file = getDirectory();

			if (getDirectory() != null) {
				Log.i(TAG, getClass().getSimpleName() + "write to file: "
						+ file);
				try {
					PrintWriter gpsPw = new PrintWriter(new BufferedWriter(
							new FileWriter(file + "/.gps")));
					PrintWriter accPw = new PrintWriter(new BufferedWriter(
							new FileWriter(file + "/.acc")));
					PrintWriter modePw = new PrintWriter(new BufferedWriter(
							new FileWriter(file + "/.mode")));

					gpsPw.println(currentState.getGpsRate() + " "
							+ currentState.getGpsFileRate());
					accPw.println(currentState.getAccRate() + " "
							+ currentState.getAccFileRate());
					modePw.println(currentState.getMode());

					gpsPw.close();
					accPw.close();
					modePw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return null;
		}
	}
}
