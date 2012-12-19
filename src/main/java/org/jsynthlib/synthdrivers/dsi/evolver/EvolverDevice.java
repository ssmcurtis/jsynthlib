package org.jsynthlib.synthdrivers.dsi.evolver;

import java.util.prefs.Preferences;

import org.jsynthlib.menu.patch.Device;
import org.jsynthlib.tools.DriverUtil;

public class EvolverDevice extends Device {

	public EvolverDevice() {
		super("DSI", "Evolver", null, null, "");
	}

	/** Constructor for for actual work. */
	public EvolverDevice(Preferences prefs) {
		this();
		this.prefs = prefs;

		addDriver(new EvolverSingleDriver());
		// addDriver(new EvolverBankDriver());
	}

	public static String[] createPatchNumbers() {
		String[] retarr = new String[128];
		String[] names = DriverUtil.generateNumbers(1, 128, "Patch ##");
		System.arraycopy(names, 0, retarr, 0, 128);

		// names = DriverUtil.generateNumbers(1, 128, "2-##");
		// System.arraycopy(names, 0, retarr, 128, 128);
		//
		// names = DriverUtil.generateNumbers(1, 128, "3-##");
		// System.arraycopy(names, 0, retarr, 256, 128);
		//
		// names = DriverUtil.generateNumbers(1, 128, "4-##");
		// System.arraycopy(names, 0, retarr, 384, 128);

		return retarr;
	}
}
