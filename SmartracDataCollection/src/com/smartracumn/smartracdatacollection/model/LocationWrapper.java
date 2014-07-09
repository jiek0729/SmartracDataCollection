package com.smartracumn.smartracdatacollection.model;

import java.util.Date;

import android.location.Location;

/**
 * Location wrapper used to wrap location and capture the time wrapper is
 * created.
 * 
 * @author kangx385
 * 
 */
public class LocationWrapper {
	private Date time;
	private Location location;

	/**
	 * Instantiate a new instance of location wrapper.
	 * 
	 * @param location
	 *            The location.
	 */
	public LocationWrapper(Location location) {
		this.location = location;
		this.time = new Date();
	}

	/**
	 * Get the time this location wrapper instance is created.
	 * 
	 * @return The birth time of the wrapper.
	 */
	public Date getTime() {
		return time;
	}

	public Location getLocation() {
		return location;
	}
}
