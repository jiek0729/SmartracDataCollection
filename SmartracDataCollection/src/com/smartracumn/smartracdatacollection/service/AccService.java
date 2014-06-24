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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.opengl.Matrix;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.smartracumn.smartracdatacollection.model.SmartracSensorData;
import com.smartracumn.smartracdatacollection.util.SmartracDataFormat;

public class AccService extends Service implements SensorEventListener {
	private final String TAG = getClass().getName();

	private MyBinder myBinder = new MyBinder();

	private int accSamplingRate = 0;

	private int accWriteFileRate = 0;

	private List<Location> cachedLocations = new ArrayList<Location>();

	private Location currentLocation;

	public boolean hasSensor() {
		return getAccelorometer() != null;
	}

	private List<Sensor> getAccelorometer() {
		SensorManager mngr = (SensorManager) this
				.getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> list = mngr.getSensorList(Sensor.TYPE_GRAVITY);
		list.addAll(mngr.getSensorList(Sensor.TYPE_MAGNETIC_FIELD));
		list.addAll(mngr.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION));
		return list != null && !list.isEmpty() ? list : null;
	}

	public void registerListener() {
		SensorManager mngr = (SensorManager) this
				.getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> list = getAccelorometer();
		if (list != null) {
			for (Sensor sensor : list) {
				mngr.registerListener(this, sensor,
						SensorManager.SENSOR_DELAY_UI);
			}
		}
	}

	public void unregisterListener() {
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
				return new File(myDir.getPath() + "/Acc.txt");
			}

			return null;
		}

		@Override
		protected Void doInBackground(List<SmartracSensorData>... params) {
			// TODO Auto-generated method stub

			if (getDirectory() != null) {
				Log.i(TAG, getClass().getSimpleName() + "write to file: "
						+ getDirectory().getPath());
				try {
					FileWriter fw = new FileWriter(getDirectory(), true);
					BufferedWriter bw = new BufferedWriter(fw);
					PrintWriter pw = new PrintWriter(bw);
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
