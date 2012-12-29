package org.jsynthlib.synthdrivers.crumar.bit99;

import java.util.prefs.Preferences;

import org.jsynthlib.menu.patch.Device;
import org.jsynthlib.tools.DriverUtil;

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

	public static String[] createPatchNumbers() {
		String[] retarr = new String[Bit99.PATCH_COUNT_IN_BANK.number()];
		String[] names = DriverUtil.generateNumbers(1, Bit99.PATCH_COUNT_IN_BANK.number(), "Patch ##");
		System.arraycopy(names, 0, retarr, 0, Bit99.PATCH_COUNT_IN_BANK.number());

		return retarr;
	}
}
