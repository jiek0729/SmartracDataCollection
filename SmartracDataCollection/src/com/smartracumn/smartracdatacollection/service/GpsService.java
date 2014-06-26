package com.smartracumn.smartracdatacollection.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Notification;
import android.app.PendingIntent;
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
import android.telephony.TelephonyManager;
import android.util.Log;

import com.smartracumn.smartracdatacollection.R;
import com.smartracumn.smartracdatacollection.model.LocationWrapper;
import com.smartracumn.smartracdatacollection.ui.MainActivity;
import com.smartracumn.smartracdatacollection.util.SmartracDataFormat;

public class GpsService extends Service implements LocationListener {
	private final String TAG = getClass().getName();

	public static final String GPS_SAMPLING_RATE = "GPS sampling rate";

	public static final String GPS_WRITE_FILE_RATE = "GPS write file rate";

	private final String GPS_PROVIDER = "gps";

	private final String NETWORK_PROVIDER = "network";

	private MyBinder myBinder = new MyBinder();

	private int gpsSamplingRate = 0;

	private int gpsWriteFileRate = 0;

	private String imei;

	private String getImei() {
		if (imei == null) {
			TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			imei = telephonyManager.getDeviceId().toString();
		}

		return imei;
	}

	private List<LocationWrapper> cachedGpsLocations = new ArrayList<LocationWrapper>();

	private List<LocationWrapper> cachedNetworkLocations = new ArrayList<LocationWrapper>();

	private Location currentLocation;

	Handler gpsUpdateHandler = new Handler();

	Runnable gpsRecorderRunnable = new Runnable() {

		@Override
		public void run() {
			if (gpsSamplingRate > 0) {
				if (currentLocation != null) {
					if (currentLocation.getProvider().equals(GPS_PROVIDER)) {
						cachedGpsLocations.add(new LocationWrapper(
								currentLocation));
					} else {
						cachedNetworkLocations.add(new LocationWrapper(
								currentLocation));
					}

					if (cachedGpsLocations.size() == gpsWriteFileRate) {
						List<LocationWrapper> temp = new ArrayList<LocationWrapper>(
								cachedGpsLocations);
						cachedGpsLocations.clear();
						cachedNetworkLocations.clear();
						if (gpsWriteFileRate > 0) {
							writeToFile(temp);
						}
					}

					if (cachedNetworkLocations.size() == gpsWriteFileRate) {
						List<LocationWrapper> temp = new ArrayList<LocationWrapper>(
								cachedNetworkLocations);
						cachedGpsLocations.clear();
						cachedNetworkLocations.clear();
						if (gpsWriteFileRate > 0) {
							writeToFile(temp);
						}
					}
				}

				gpsUpdateHandler.postDelayed(this, gpsSamplingRate);
			}
		}

		private void writeToFile(final List<LocationWrapper> locations) {
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
		mngr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		mngr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
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

		Notification notification = new Notification(
				R.drawable.smartrac_data_collection_icon,
				getText(R.string.gps_service_started),
				System.currentTimeMillis());
		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(this,
				getText(R.string.notification_title),
				getText(R.string.notification_message), pendingIntent);
		startForeground(1234, notification);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, getClass().getSimpleName() + ":entered onDestroy()");
		gpsUpdateHandler.removeCallbacks(gpsRecorderRunnable);
		unregisterLocationListener();
		if (gpsSamplingRate > 0) {
			new WriteFileTask().execute(cachedGpsLocations);
		}
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO do something useful

		if (intent != null) {
			gpsSamplingRate = intent.getIntExtra(GPS_SAMPLING_RATE,
					gpsSamplingRate);

			gpsWriteFileRate = intent.getIntExtra(GPS_WRITE_FILE_RATE,
					gpsWriteFileRate);
		}

		Log.i(TAG, getClass().getSimpleName()
				+ "service on start command with Rate: " + gpsSamplingRate
				+ " File rate: " + gpsWriteFileRate);

		if (gpsSamplingRate > 0) {
			gpsUpdateHandler.removeCallbacks(gpsRecorderRunnable);

			gpsUpdateHandler.postDelayed(gpsRecorderRunnable, gpsSamplingRate);
		}

		return Service.START_NOT_STICKY;
	}

	public class MyBinder extends Binder {
		GpsService getService() {
			return GpsService.this;
		}
	}

	private class WriteFileTask extends
			AsyncTask<List<LocationWrapper>, Void, Void> {

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
								new Date(), "Gps.txt");
				return new File(fileName);
			}

			return null;
		}

		@Override
		protected Void doInBackground(List<LocationWrapper>... params) {
			// TODO Auto-generated method stub
			File file = getDirectory();
			if (file != null) {
				Log.i(TAG, getClass().getSimpleName() + "write to file: "
						+ file.getPath());
				try {
					boolean WriteHeader = !file.exists();
					FileWriter fw = new FileWriter(getDirectory(), true);
					BufferedWriter bw = new BufferedWriter(fw);
					PrintWriter pw = new PrintWriter(bw);
					if (WriteHeader) {
						pw.println(new SmartracDataFormat().getGpsHeader());
					}

					for (LocationWrapper location : params[0]) {
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
