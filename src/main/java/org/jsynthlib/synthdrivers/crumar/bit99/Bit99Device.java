package org.jsynthlib.synthdrivers.crumar.bit99;

import java.util.prefs.Preferences;

import org.jsynthlib.model.device.Device;

public class Bit99Device extends Device {

	public Bit99Device() {
		super(Bit99.VENDOR, Bit99.DEVICE, null, null, "");
	}

	/** Constructor for for actual work. */
	public Bit99Device(Preferences prefs) {
		this();
		this.prefs = prefs;

		addDriver(new Bit99SingleDriver());
		// addDriver(new EvolverBankDriver());
	}
}
