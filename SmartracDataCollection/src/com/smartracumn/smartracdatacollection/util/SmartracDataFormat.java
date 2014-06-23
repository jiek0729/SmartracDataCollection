package com.smartracumn.smartracdatacollection.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.location.Location;

public class SmartracDataFormat {
	private final SimpleDateFormat MY_FORMAT = new SimpleDateFormat(
			"yyyy/MM/dd, HH:mm:ss");

	private final String SPLITER = ", ";

	public String formatLocation(Location loc) {
		StringBuilder data = new StringBuilder();
		data.append(MY_FORMAT.format(new Date(loc.getTime())));
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
}
