package com.smartracumn.smartracdatacollection.util;

import java.text.SimpleDateFormat;

import com.smartracumn.smartracdatacollection.model.LocationWrapper;
import com.smartracumn.smartracdatacollection.model.SmartracSensorData;

public class SmartracDataFormat {
	private static final SimpleDateFormat MY_FORMAT = new SimpleDateFormat(
			"yyyy/MM/dd, HH:mm:ss");

	private final String SPLITER = ", ";

	public String formatLocation(LocationWrapper loc) {
		StringBuilder data = new StringBuilder();
		data.append(MY_FORMAT.format(loc.getTime()));
		data.append(SPLITER);
		data.append(loc.getLocation().getLatitude());
		data.append(SPLITER);
		data.append(loc.getLocation().getLongitude());
		data.append(SPLITER);
		data.append(loc.getLocation().getSpeed());
		data.append(SPLITER);
		data.append(loc.getLocation().getProvider());
		data.append(SPLITER);
		data.append(loc.getLocation().getAccuracy());
		data.append(SPLITER);
		data.append(loc.getLocation().getAltitude());
		data.append(SPLITER);
		data.append(loc.getLocation().getBearing());
		data.append(SPLITER);

		return data.toString();
	}

	public String formatSensorData(SmartracSensorData sensor) {
		StringBuilder data = new StringBuilder();

		data.append(MY_FORMAT.format(sensor.getTime()));
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

	public static SimpleDateFormat getDateTimeFormat() {
		return MY_FORMAT;
	}
}
