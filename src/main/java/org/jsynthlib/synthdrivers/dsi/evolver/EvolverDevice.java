package org.jsynthlib.synthdrivers.dsi.evolver;

import java.util.prefs.Preferences;

import org.jsynthlib.model.device.Device;

public class EvolverDevice extends Device {

	public EvolverDevice() {
		super(Evolver.VENDOR, Evolver.DEVICE, null, null, "");
	}

	/** Constructor for for actual work. */
	public EvolverDevice(Preferences prefs) {
		this();
		this.prefs = prefs;

		addDriver(new EvolverSingleDriver());
		addDriver(new EvolverBankDriver());
	}
}
