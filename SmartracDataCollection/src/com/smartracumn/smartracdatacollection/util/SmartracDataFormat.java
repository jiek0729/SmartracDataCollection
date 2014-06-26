package com.smartracumn.smartracdatacollection.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.smartracumn.smartracdatacollection.model.LocationWrapper;
import com.smartracumn.smartracdatacollection.model.SmartracSensorData;

public class SmartracDataFormat {
	private static final SimpleDateFormat MY_FORMAT = new SimpleDateFormat(
			"yyyy/MM/dd, HH:mm:ss");

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd");

	private final String SPLITER = ", ";

	private final String FILE_SPLITER = "&";

	public static SimpleDateFormat getDateTimeFormat() {
		return MY_FORMAT;
	}

	public static SimpleDateFormat getDateFormat() {
		return DATE_FORMAT;
	}

	public String getModeHeader() {
		return "Date, Time, Mode" + SPLITER + "Note";
	}

	public String getFileName(String imei, Date time, String fileName) {
		return imei + FILE_SPLITER + DATE_FORMAT.format(time) + FILE_SPLITER
				+ fileName;
	}

	public String formatModeAndNote(Date time, String mode, String note) {
		StringBuilder data = new StringBuilder();
		data.append(MY_FORMAT.format(time));
		data.append(SPLITER);
		data.append(mode);
		if (note != null && !note.equals("")) {
			data.append(SPLITER);
			data.append(note);
		}

		return data.toString();
	}

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

		return data.toString();
	}

	public String getGpsHeader() {
		StringBuilder data = new StringBuilder();
		data.append("Date, Time");
		data.append(SPLITER);
		data.append("Latitude");
		data.append(SPLITER);
		data.append("Longitude");
		data.append(SPLITER);
		data.append("Speed");
		data.append(SPLITER);
		data.append("Provider");
		data.append(SPLITER);
		data.append("Accuracy");
		data.append(SPLITER);
		data.append("Altitude");
		data.append(SPLITER);
		data.append("Bearing");

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

	public String getAccHeader() {
		StringBuilder data = new StringBuilder();

		data.append("Date, Time");
		data.append(SPLITER);
		data.append("LinearX");
		data.append(SPLITER);
		data.append("LinearY");
		data.append(SPLITER);
		data.append("LinearZ");
		data.append(SPLITER);
		data.append("LinearMagnitude");
		data.append(SPLITER);
		data.append("TrueX");
		data.append(SPLITER);
		data.append("TrueY");
		data.append(SPLITER);
		data.append("TrueZ");
		data.append(SPLITER);
		data.append("TrueMagnitude");

		return data.toString();
	}
}
