package org.jsynthlib.synthdrivers.dsi.evolver;

import java.util.prefs.Preferences;

import org.jsynthlib.menu.patch.Device;
import org.jsynthlib.tools.DriverUtil;

public class EvolverDevice extends Device {

	public EvolverDevice() {
		super(Evolver.VENDOR, Evolver.DEVICE, null, null, "");
	}

	/** Constructor for for actual work. */
	public EvolverDevice(Preferences prefs) {
		this();
		this.prefs = prefs;

		addDriver(new EvolverSingleDriver());
		// addDriver(new EvolverBankDriver());
	}

	public static String[] createPatchNumbers() {
		String[] retarr = new String[Evolver.PATCH_COUNT_IN_BANK.number()];
		String[] names = DriverUtil.generateNumbers(1, Evolver.PATCH_COUNT_IN_BANK.number(), "Patch ##");
		System.arraycopy(names, 0, retarr, 0, Evolver.PATCH_COUNT_IN_BANK.number());

		return retarr;
	}
}
