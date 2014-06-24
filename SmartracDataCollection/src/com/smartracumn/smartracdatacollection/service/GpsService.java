package com.smartracumn.smartracdatacollection.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.smartracumn.smartracdatacollection.util.SmartracDataFormat;

public class GpsService extends Service implements LocationListener {
	private final String TAG = getClass().getName();

	public static final String GPS_SAMPLING_RATE = "GPS sampling rate";

	public static final String GPS_WRITE_FILE_RATE = "GPS write file rate";

	private MyBinder myBinder = new MyBinder();

	private int gpsSamplingRate = 0;

	private int gpsWriteFileRate = 0;

	private List<Location> cachedLocations = new ArrayList<Location>();

	private Location currentLocation;

	Handler gpsUpdateHandler = new Handler();

	Runnable gpsRecorderRunnable = new Runnable() {

		@Override
		public void run() {
			if (gpsSamplingRate > 0) {
				if (currentLocation != null) {
					cachedLocations.add(currentLocation);
					if (cachedLocations.size() == gpsWriteFileRate) {
						List<Location> temp = new ArrayList<Location>(
								cachedLocations);
						cachedLocations.clear();
						writeToFile(temp);
					}
				}

				gpsUpdateHandler.postDelayed(this, gpsSamplingRate);
			}
		}

		private void writeToFile(final List<Location> locations) {
			// TODO Auto-generated method stub
			Handler handler = new Handler(Looper.getMainLooper());
			handler.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					new WriteFileTask().execute(locations);
				}
			});
		}
	};

	public void registerLocationListener() {
		LocationManager mngr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mngr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0.01f,
				this);
	}

	public void unregisterLocationListener() {
		LocationManager mngr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mngr.removeUpdates(this);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return myBinder;
	}

	@Override
	public void onCreate() {
		Log.i(TAG, getClass().getSimpleName() + ":entered onCreate()");
		super.onCreate();
		registerLocationListener();
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, getClass().getSimpleName() + ":entered onDestroy()");
		gpsUpdateHandler.removeCallbacks(gpsRecorderRunnable);
		unregisterLocationListener();
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO do something useful
		Log.i(TAG, getClass().getSimpleName() + "service on start command.");

		if (intent != null) {
			gpsSamplingRate = intent.getIntExtra(GPS_SAMPLING_RATE,
					gpsSamplingRate);

			gpsWriteFileRate = intent.getIntExtra(GPS_WRITE_FILE_RATE,
					gpsWriteFileRate);
		}

		if (gpsSamplingRate > 0) {
			gpsUpdateHandler.removeCallbacks(gpsRecorderRunnable);

			gpsUpdateHandler.postDelayed(gpsRecorderRunnable, gpsSamplingRate);
		}

		return Service.START_STICKY;
	}

	public class MyBinder extends Binder {
		GpsService getService() {
			return GpsService.this;
		}
	}

	private class WriteFileTask extends AsyncTask<List<Location>, Void, Void> {

		private File getDirectory() {
			File myDir = new File(Environment.getExternalStorageDirectory()
					.getPath() + "/SMDataCollection_data");
			if (!myDir.exists()) {
				myDir.mkdir();
			}

			if (myDir.exists()) {
				return new File(myDir.getPath() + "/Gps.txt");
			}

			return null;
		}

		@Override
		protected Void doInBackground(List<Location>... params) {
			// TODO Auto-generated method stub

			if (getDirectory() != null) {
				Log.i(TAG, getClass().getSimpleName() + "write to file: "
						+ getDirectory().getPath());
				try {
					FileWriter fw = new FileWriter(getDirectory(), true);
					BufferedWriter bw = new BufferedWriter(fw);
					PrintWriter pw = new PrintWriter(bw);
					for (Location location : params[0]) {
						pw.println(new SmartracDataFormat()
								.formatLocation(location));
					}
					pw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return null;
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		currentLocation = location;
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}
}
