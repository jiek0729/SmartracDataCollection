package com.smartracumn.smartracdatacollection.model;

import java.util.Date;

public class SmartracSensorData {
	private Date time;
	private float linearX;
	private float linearY;
	private float linearZ;
	private float linearMag;
	private float trueX;
	private float trueY;
	private float trueZ;
	private float trueMag;

	public SmartracSensorData(Date time, float[] linearAcc, float[] trueAcc) {
		this.time = time;
		this.linearX = linearAcc[0];
		this.linearY = linearAcc[1];
		this.linearZ = linearAcc[2];
		this.linearMag = linearAcc[3];
		this.trueX = trueAcc[0];
		this.trueY = trueAcc[1];
		this.trueZ = trueAcc[2];
		this.trueMag = trueAcc[3];
	}

	public Date getTime() {
		return this.time;
	}

	public float getLinearX() {
		return this.linearX;
	}

	public float getLinearY() {
		return this.linearY;
	}

	public float getLinearZ() {
		return this.linearZ;
	}

	public float getLinearMag() {
		return this.linearMag;
	}

	public float getTrueX() {
		return this.trueX;
	}

	public float getTrueY() {
		return this.trueY;
	}

	public float getTrueZ() {
		return this.trueZ;
	}

	public float getTrueMag() {
		return this.trueMag;
	}
}
