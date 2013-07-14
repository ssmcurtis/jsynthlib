// written by Kenneth L. Martinez
// 
// @version $Id$

package org.jsynthlib.synthdrivers.alesis.andromeda;

import java.util.prefs.Preferences;

import org.jsynthlib.model.device.Device;

public class AndromedaDevice extends Device {

	public AndromedaDevice(Preferences prefs) {
		super(Andromeda.VENDOR, Andromeda.DEVICE, null, Andromeda.DRIVER_INFO, "Kenneth L. Martinez, ssmCurtis");
		this.prefs = prefs;

		setMaxProgramForLibraryStorage(Andromeda.PROGRAM_COUNT_IN_BANK);

		AndromedaPgmSingleDriver libraryDriver = new AndromedaPgmSingleDriver();
		libraryDriver.setUseForStoreLibrary(true);
		addDriver(libraryDriver);

		addDriver(new AndromedaPgmBankDriver());
		addDriver(new AndromedaMixBankDriver());
		addDriver(new AndromedaMixSingleDriver());
	}
}
