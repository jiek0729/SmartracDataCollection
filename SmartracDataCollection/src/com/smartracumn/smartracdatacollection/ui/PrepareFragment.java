package com.smartracumn.smartracdatacollection.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.smartracumn.smartracdatacollection.R;
import com.smartracumn.smartracdatacollection.service.GpsService;

public class PrepareFragment extends Fragment {
	private final String TAG = getClass().getName();

	private ProgressBar progressBar;

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
		return inflater.inflate(R.layout.prepare_layout, container, false);
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
		progressBar = (ProgressBar) getView().findViewById(
				R.id.prepare_progress_bar);
		new PrepareTask().execute();
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

	private class PrepareTask extends AsyncTask<Void, Integer, Boolean> {
		private boolean isMyServiceRunning(Class<?> serviceClass) {
			ActivityManager manager = (ActivityManager) getActivity()
					.getSystemService(Context.ACTIVITY_SERVICE);
			for (RunningServiceInfo service : manager
					.getRunningServices(Integer.MAX_VALUE)) {
				if (serviceClass.getName().equals(
						service.service.getClassName())) {
					return true;
				}
			}
			return false;
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			publishProgress(50);
			return isMyServiceRunning(GpsService.class);
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			progressBar.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(Boolean isRunning) {
			publishProgress(100);
			if (getActivity() != null) {
				((MainActivity) getActivity()).setIsRecording(isRunning);
				if (isRunning) {
					((MainActivity) getActivity()).gotoModeTracking();
				} else {
					((MainActivity) getActivity()).gotoSamplingSetting();
				}
			}
		}
	}
}
