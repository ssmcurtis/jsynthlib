package org.jsynthlib.synthdrivers.korg.microkorg;

import java.util.prefs.Preferences;

import org.jsynthlib.model.device.Device;

public class MicroKorgDevice extends Device {

	/** Constructor for for actual work. */
	public MicroKorgDevice(Preferences prefs) {
		super(MicroKorg.VENDOR, MicroKorg.DEVICE, null, null, "");
		this.prefs = prefs;

		setMaxProgramForLibraryStorage(MicroKorg.PATCH_COUNT_IN_BANK);

		MicroKorgSingleDriver libraryDriver = new MicroKorgSingleDriver();
		libraryDriver.setUseForStoreLibrary(true);
		addDriver(libraryDriver);
		addDriver(new MicroKorgBankDriver());
	}
}
