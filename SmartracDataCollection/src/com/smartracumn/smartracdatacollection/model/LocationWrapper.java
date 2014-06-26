package com.smartracumn.smartracdatacollection.model;

import java.util.Date;

import android.location.Location;

public class LocationWrapper {
	private Date time;
	private Location location;

	public LocationWrapper(Location location) {
		this.location = location;
		this.time = new Date();
	}

	public Date getTime() {
		return time;
	}

	public Location getLocation() {
		return location;
	}
}
