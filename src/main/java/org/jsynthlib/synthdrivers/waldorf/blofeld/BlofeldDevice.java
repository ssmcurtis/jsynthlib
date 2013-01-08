package org.jsynthlib.synthdrivers.waldorf.blofeld;

import java.util.prefs.Preferences;

import org.jsynthlib.model.device.Device;

public class BlofeldDevice extends Device {

	public BlofeldDevice() {
		super(Blofeld.VENDOR, Blofeld.DEVICE, null, null, "");
	}

	/** Constructor for for actual work. */
	public BlofeldDevice(Preferences prefs) {
		this();
		this.prefs = prefs;

		addDriver(new BlofeldSingleDriver());
		addDriver(new BlofeldCompleteDriver());
		addDriver(new BlofeldMultitimbralDriver());
	}
}
