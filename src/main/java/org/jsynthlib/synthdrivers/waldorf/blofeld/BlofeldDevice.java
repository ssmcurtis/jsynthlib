package org.jsynthlib.synthdrivers.waldorf.blofeld;

import java.util.Arrays;
import java.util.prefs.Preferences;

import org.jsynthlib.model.device.Device;
import org.jsynthlib.model.patch.Patch;

public class BlofeldDevice extends Device {

	public BlofeldDevice(Preferences prefs) {
		super(Blofeld.VENDOR, Blofeld.DEVICE, null, Blofeld.DRIVER_INFO, "");
		this.prefs = prefs;

		setMaxProgramForLibraryStorage(Blofeld.PROGRAM_COUNT_IN_BANK);

		BlofeldSingleDriver libraryDriver = new BlofeldSingleDriver();
		libraryDriver.setUseForStoreLibrary(true);
		addDriver(libraryDriver);

		addDriver(new BlofeldCompleteDriver());
		addDriver(new BlofeldMultitimbralDriver());
	}

	@Override
	public boolean comparePatches(Patch p1, Patch p2) {
		// do not compare checksum and header

		byte[] stay = p1.getByteArray();

		int stayLength = stay.length - 2 - Blofeld.HEADER_SIZE;
		byte[] stay1 = new byte[stayLength];
		System.arraycopy(stay, Blofeld.HEADER_SIZE, stay1, 0, stayLength);

		byte[] delete = p2.getByteArray();

		int deleteLength = delete.length - 2 - Blofeld.HEADER_SIZE;
		byte[] delete1 = new byte[deleteLength];
		System.arraycopy(delete, Blofeld.HEADER_SIZE, delete1, 0, deleteLength);

		return Arrays.equals(stay1, delete1);
	}

}
