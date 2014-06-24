package com.smartracumn.smartracdatacollection.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.location.Location;

import com.smartracumn.smartracdatacollection.model.SmartracSensorData;

public class SmartracDataFormat {
	private final SimpleDateFormat MY_FORMAT = new SimpleDateFormat(
			"yyyy/MM/dd, HH:mm:ss");

	private final String SPLITER = ", ";

	public String formatLocation(Location loc) {
		StringBuilder data = new StringBuilder();
		data.append(formatDateTime(new Date(loc.getTime())));
		data.append(SPLITER);
		data.append(loc.getLatitude());
		data.append(SPLITER);
		data.append(loc.getLongitude());
		data.append(SPLITER);
		data.append(loc.getSpeed());
		data.append(SPLITER);
		data.append(loc.getProvider());
		data.append(SPLITER);
		data.append(loc.getAccuracy());
		data.append(SPLITER);
		data.append(loc.getAltitude());
		data.append(SPLITER);
		data.append(loc.getBearing());
		data.append(SPLITER);

		return data.toString();
	}

	public String formatSensorData(SmartracSensorData sensor) {
		StringBuilder data = new StringBuilder();

		data.append(formatDateTime(sensor.getTime()));
		data.append(SPLITER);
		data.append(sensor.getLinearX());
		data.append(SPLITER);
		data.append(sensor.getLinearY());
		data.append(SPLITER);
		data.append(sensor.getLinearZ());
		data.append(SPLITER);
		data.append(sensor.getLinearMag());
		data.append(SPLITER);
		data.append(sensor.getTrueX());
		data.append(SPLITER);
		data.append(sensor.getTrueY());
		data.append(SPLITER);
		data.append(sensor.getTrueZ());
		data.append(SPLITER);
		data.append(sensor.getTrueMag());

		return data.toString();
	}

	public String formatDateTime(Date date) {
		return MY_FORMAT.format(date);
	}
}
