package com.smartracumn.smartracdatacollection.model;

public class SmartracServiceState {
	private boolean gpsRunning;
	private boolean accRunning;
	private int gpsRate;
	private int accRate;
	private int gpsFileRate;
	private int accFileRate;
	private String mode;

	public int getGpsRate() {
		return gpsRate;
	}

	public int getGpsFileRate() {
		return gpsFileRate;
	}

	public int getAccRate() {
		return accRate;
	}

	public int getAccFileRate() {
		return accFileRate;
	}

	public SmartracServiceState(boolean gpsRunning, boolean accRunning) {
		this.gpsRunning = gpsRunning;
		this.accRunning = accRunning;
	}

	public void setGpsRate(int rate, int fileRate) {
		this.gpsRate = rate;
		this.gpsFileRate = fileRate;
	}

	public void setAccRate(int rate, int fileRate) {
		this.accRate = rate;
		this.accFileRate = fileRate;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getMode() {
		return mode;
	}

	public boolean hasServiceRunning() {
		return gpsRunning || accRunning;
	}

	@Override
	public String toString() {
		String msg = "Running Service: ";
		if (gpsRunning && accRunning) {
			msg += String.format("GPS(%ds, %d), ACC(%ds, %d)", gpsRate,
					gpsFileRate, accRate, accFileRate);
		} else if (gpsRunning) {
			msg += String.format("GPS(%ds, %d)", gpsRate, gpsFileRate);
		} else if (accRunning) {
			msg += String.format("ACC(%ds, %d)", accRate, accFileRate);
		} else {
			msg += "None";
		}

		return msg;
	}
}
