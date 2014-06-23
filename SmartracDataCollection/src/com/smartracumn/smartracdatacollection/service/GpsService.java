package com.smartracumn.smartracdatacollection.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

public class GpsService extends Service {
	private final String TAG = getClass().getName();

	public static final String GPS_SAMPLING_RATE = "GPS sampling rate";

	private MyBinder myBinder = new MyBinder();

	private int gpsSamplingRate;

	Handler gpsUpdateHandler = new Handler();

	Runnable gpsRecorderRunnable = new Runnable() {

		@Override
		public void run() {
			if (gpsSamplingRate > 0) {
				writeToFile();
				gpsUpdateHandler.postDelayed(this, gpsSamplingRate);
			}
		}

		private void writeToFile() {
			// TODO Auto-generated method stub
			Handler handler = new Handler(Looper.getMainLooper());
			handler.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					new WriteFileTask().execute();
				}
			});
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return myBinder;
	}

	@Override
	public void onCreate() {
		Log.i(TAG, getClass().getSimpleName() + ":entered onCreate()");
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, getClass().getSimpleName() + ":entered onDestroy()");
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO do something useful
		Log.i(TAG, getClass().getSimpleName() + "service on start command.");
		gpsSamplingRate = intent
				.getIntExtra(GPS_SAMPLING_RATE, gpsSamplingRate);

		gpsUpdateHandler.removeCallbacks(gpsRecorderRunnable);

		gpsUpdateHandler.postDelayed(gpsRecorderRunnable, gpsSamplingRate);

		return Service.START_STICKY;
	}

	public class MyBinder extends Binder {
		GpsService getService() {
			return GpsService.this;
		}
	}

	private class WriteFileTask extends AsyncTask<Void, Void, Void> {

		private File getDirectory() {
			File myDir = new File(Environment.getExternalStorageDirectory()
					.getPath() + "/SMDataCollection_data");
			if (!myDir.exists()) {
				myDir.mkdir();
			}

			if (myDir.exists()) {
				return new File(myDir.getPath() + "/Test.txt");
			}

			return null;
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			if (getDirectory() != null) {
				Log.i(TAG, getClass().getSimpleName() + "write to file: "
						+ getDirectory().getPath());
				try {
					FileWriter fw = new FileWriter(getDirectory(), true);
					BufferedWriter bw = new BufferedWriter(fw);
					PrintWriter pw = new PrintWriter(bw);
					pw.println(new Date().toString());
					pw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return null;
		}
	}
}
