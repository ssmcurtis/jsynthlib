package org.jsynthlib.synthdrivers.korg.microkorg;

import java.util.prefs.Preferences;

import org.jsynthlib.model.device.Device;

public class MicroKorgDevice extends Device {

	public MicroKorgDevice() {
		super(MicroKorg.VENDOR, MicroKorg.DEVICE, null, null, "");
	}

	/** Constructor for for actual work. */
	public MicroKorgDevice(Preferences prefs) {
		this();
		this.prefs = prefs;

		addDriver(new MicroKorgSingleDriver());
		addDriver(new MicroKorgBankDriver());
	}
}
