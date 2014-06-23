package com.smartracumn.smartracdatacollection.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ProgressBar;

import com.smartracumn.smartracdatacollection.R;
import com.smartracumn.smartracdatacollection.service.GpsService;

public class MainActivity extends FragmentActivity {
	private final String TAG = getClass().getName();

	private ProgressBar progressBar;

	public void startGpsService() {
		Intent service = new Intent(this, GpsService.class);
		service.putExtra(GpsService.GPS_SAMPLING_RATE, 1000);
		this.startService(service);
	}

	public void stopGpsService() {
		this.stopService(new Intent(this, GpsService.class));
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
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, getClass().getSimpleName() + " onDestroy()");
	}
}
