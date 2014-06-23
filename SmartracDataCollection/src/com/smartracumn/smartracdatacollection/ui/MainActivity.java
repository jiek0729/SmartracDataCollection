package com.smartracumn.smartracdatacollection.ui;

import java.util.HashMap;
import java.util.Map;

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

	private int gpsSamplingRate = 0;

	private int gpsWriteFileRate = 0;

	private int accSamplingRate = 0;

	private int accWriteFileRate = 0;

	private Map<Integer, Integer> buttonValueMapping;

	private void initializeButtonValueMapping() {
		buttonValueMapping = new HashMap<Integer, Integer>();
		buttonValueMapping.put(R.id.gps_no, 0);
		buttonValueMapping.put(R.id.gps_1s, 1);
		buttonValueMapping.put(R.id.gps_5s, 5);
		buttonValueMapping.put(R.id.gps_file_1s, 1);
		buttonValueMapping.put(R.id.gps_file_5s, 5);
		buttonValueMapping.put(R.id.gps_file_30s, 30);
		buttonValueMapping.put(R.id.acc_sampling_rate_no, 0);
		buttonValueMapping.put(R.id.acc_sampling_rate_1s, 1);
		buttonValueMapping.put(R.id.acc_sampling_rate_5s, 5);
		buttonValueMapping.put(R.id.acc_filing_rate_1s, 1);
		buttonValueMapping.put(R.id.acc_filing_rate_5s, 5);
		buttonValueMapping.put(R.id.acc_filing_rate_30s, 30);
	}

	private boolean isRecording;

	public void setIsRecording(boolean isRecording) {
		this.isRecording = isRecording;
	}

	public boolean getIsRecording() {
		return isRecording;
	}

	public void setRates(int gpsRateId, int gpsFileId, int accRateId,
			int accFileId) {
		gpsSamplingRate = buttonValueMapping.get(gpsRateId) * 1000;

		gpsWriteFileRate = buttonValueMapping.get(gpsFileId);

		accSamplingRate = buttonValueMapping.get(accRateId);

		accWriteFileRate = buttonValueMapping.get(accFileId);
	}

	public void startGpsService() {
		if (gpsSamplingRate > 0 && gpsWriteFileRate > 0) {
			Intent service = new Intent(this, GpsService.class);
			service.putExtra(GpsService.GPS_SAMPLING_RATE, gpsSamplingRate);
			service.putExtra(GpsService.GPS_WRITE_FILE_RATE, gpsWriteFileRate);
			this.startService(service);
		}
	}

	public void stopGpsService() {
		this.stopService(new Intent(this, GpsService.class));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, getClass().getSimpleName() + " onCreate()");
		initializeButtonValueMapping();
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
