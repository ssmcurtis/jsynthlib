// written by Kenneth L. Martinez
// 
// @version $Id$

package org.jsynthlib.synthdrivers.alesis.andromeda;

import java.util.prefs.Preferences;

import org.jsynthlib.model.device.Device;

public class AlesisA6Device extends Device {
	static final String DRIVER_INFO = "The A6 lacks a MIDI addressable patch buffer. Therefore, when you\n"
			+ "send or play a patch from within JSynthLib, user program 1 in bnak user 1 will be\n"
			+ "overwritten. JSynthLib treats this location as an edit buffer.";

	public AlesisA6Device(Preferences prefs) {
		super(Andromeda.VENDOR, Andromeda.DEVICE, null, DRIVER_INFO, "Kenneth L. Martinez, ssmCurtis");
		this.prefs = prefs;

		setMaxProgramForLibraryStorage(Andromeda.PROGRAM_COUNT_IN_BANK);

		AlesisA6PgmSingleDriver libraryDriver = new AlesisA6PgmSingleDriver();
		libraryDriver.setUseForStoreLibrary(true);
		addDriver(libraryDriver);

		addDriver(new AlesisA6PgmBankDriver());
		addDriver(new AlesisA6MixBankDriver());
		addDriver(new AlesisA6MixSingleDriver());
	}
}
