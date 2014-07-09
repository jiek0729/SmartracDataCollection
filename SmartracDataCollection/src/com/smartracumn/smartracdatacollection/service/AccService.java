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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Matrix;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.smartracumn.smartracdatacollection.R;
import com.smartracumn.smartracdatacollection.model.SmartracSensorData;
import com.smartracumn.smartracdatacollection.ui.MainActivity;
import com.smartracumn.smartracdatacollection.util.SmartracDataFormat;

public class AccService extends Service implements SensorEventListener {
	private final String TAG = getClass().getName();

	public static final String ACC_SAMPLING_RATE = "ACC sampling rate";

	public static final String ACC_WRITE_FILE_RATE = "ACC write file rate";

	private MyBinder myBinder = new MyBinder();

	private int accSamplingRate = 0;

	private int accWriteFileRate = 0;

	private String imei;

	private String getImei() {
		if (imei == null) {
			TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			imei = telephonyManager.getDeviceId().toString();
		}

		return imei;
	}

	private List<SmartracSensorData> cachedSensorData = new ArrayList<SmartracSensorData>();

	private SmartracSensorData currentSensorData;

	private Sensor gravitySensor;

	private Sensor magSensor;

	private Sensor linearAccSensor;

	Handler accUpdateHandler = new Handler();

	Runnable accRecorderRunnable = new Runnable() {

		@Override
		public void run() {
			if (accSamplingRate > 0) {
				if (currentSensorData != null) {
					cachedSensorData.add(currentSensorData);
					if (cachedSensorData.size() == accWriteFileRate) {
						List<SmartracSensorData> temp = new ArrayList<SmartracSensorData>(
								cachedSensorData);
						cachedSensorData.clear();
						if (accWriteFileRate > 0) {
							writeToFile(temp);
						}
					}
				}

				accUpdateHandler.postDelayed(this, accSamplingRate);
			}
		}

		private void writeToFile(final List<SmartracSensorData> sensorData) {
			// TODO Auto-generated method stub
			Handler handler = new Handler(Looper.getMainLooper());
			handler.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					new WriteFileTask().execute(sensorData);
				}
			});
		}
	};

	@Override
	public void onCreate() {
		Log.i(TAG, getClass().getSimpleName() + ":entered onCreate()");
		super.onCreate();
		registerSensorListener();

		Notification notification = new Notification(
				R.drawable.smartrac_data_collection_icon,
				getText(R.string.acc_service_started),
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
		if (accSamplingRate > 0) {
			new WriteFileTask().execute(cachedSensorData);
			accSamplingRate = 0;
		}

		accUpdateHandler.removeCallbacks(accRecorderRunnable);
		unregisterSensorListener();

		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO do something useful

		if (intent != null) {
			accSamplingRate = intent.getIntExtra(ACC_SAMPLING_RATE,
					accSamplingRate);

			accWriteFileRate = intent.getIntExtra(ACC_WRITE_FILE_RATE,
					accWriteFileRate);
		}

		Log.i(TAG, getClass().getSimpleName()
				+ "service on start command with Rate: " + accSamplingRate
				+ " File rate: " + accWriteFileRate);

		if (accSamplingRate > 0) {
			accUpdateHandler.removeCallbacks(accRecorderRunnable);

			accUpdateHandler.postDelayed(accRecorderRunnable, accSamplingRate);
		}

		return Service.START_NOT_STICKY;
	}

	public boolean hasSensor() {
		return gravitySensor != null && magSensor != null
				&& linearAccSensor != null;
	}

	private void getAccelorometer() {
		SensorManager mngr = (SensorManager) this
				.getSystemService(Context.SENSOR_SERVICE);
		gravitySensor = mngr.getDefaultSensor(Sensor.TYPE_GRAVITY);
		magSensor = mngr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		linearAccSensor = mngr
				.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
	}

	public void registerSensorListener() {
		SensorManager mngr = (SensorManager) this
				.getSystemService(Context.SENSOR_SERVICE);
		getAccelorometer();
		List<Sensor> list = new ArrayList<Sensor>();
		list.add(gravitySensor);
		list.add(magSensor);
		list.add(linearAccSensor);
		if (list != null) {
			for (Sensor sensor : list) {
				mngr.registerListener(this, sensor,
						SensorManager.SENSOR_DELAY_UI);
			}
		}
	}

	public void unregisterSensorListener() {
		if (hasSensor()) {
			SensorManager mngr = (SensorManager) this
					.getSystemService(Context.SENSOR_SERVICE);
			mngr.unregisterListener(this);
		}
	}

	private float[] gravity = new float[4];
	private float[] last_gravity = new float[4];
	private float[] geomag = new float[4];

	private float[] trueAcceleration = new float[4];
	private float[] linearAcceleration = new float[4];

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
			// geomag is an float number array of length 4
			geomag = event.values.clone();

		if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
			// gravity is an float number array of length 4
			gravity = event.values.clone();
		}

		if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

			linearAcceleration[0] = event.values[0];
			linearAcceleration[1] = event.values[1];
			linearAcceleration[2] = event.values[2];
			linearAcceleration[3] = (float) Math.sqrt(Math.pow(
					linearAcceleration[0], 2)
					+ Math.pow(linearAcceleration[1], 2)
					+ Math.pow(linearAcceleration[2], 2));
		}

		if (gravity != null && geomag != null && last_gravity != gravity) {
			// checks that the rotation matrix is found
			float[] inR = new float[16];
			float[] inR_inverse = new float[16];
			float[] I = new float[16];

			boolean success = SensorManager.getRotationMatrix(inR, I, gravity,
					geomag);

			if (success) {
				Matrix.invertM(inR_inverse, 0, inR, 0);
				Matrix.multiplyMV(trueAcceleration, 0, inR_inverse, 0,
						linearAcceleration, 0);

				last_gravity = gravity;
				trueAcceleration[3] = (float) Math.sqrt(Math.pow(
						trueAcceleration[0], 2)
						+ Math.pow(trueAcceleration[1], 2));
			}
		}

		currentSensorData = new SmartracSensorData(new Date(),
				linearAcceleration, trueAcceleration);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return myBinder;
	}

	public class MyBinder extends Binder {
		AccService getService() {
			return AccService.this;
		}
	}

	private class WriteFileTask extends
			AsyncTask<List<SmartracSensorData>, Void, Void> {

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
								new Date(), "Acc.txt");
				return new File(fileName);
			}

			return null;
		}

		@Override
		protected Void doInBackground(List<SmartracSensorData>... params) {
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
						pw.println(new SmartracDataFormat().getAccHeader());
					}

					for (SmartracSensorData sensorData : params[0]) {
						pw.println(new SmartracDataFormat()
								.formatSensorData(sensorData));
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
}
