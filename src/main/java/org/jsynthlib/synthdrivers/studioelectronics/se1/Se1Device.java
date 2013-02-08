package org.jsynthlib.synthdrivers.studioelectronics.se1;

import java.util.Arrays;
import java.util.prefs.Preferences;

import javax.swing.JComboBox;

import org.jsynthlib.model.device.Device;
import org.jsynthlib.model.patch.Patch;

public class Se1Device extends Device {
	static final String DRIVER_INFO = Se1.VENDOR + " " + Se1.DEVICE
			+ "  Dumps must be initialted from synth. \n save & edit bank";

	JComboBox<String> channelList;

	public Se1Device() {
		super(Se1.VENDOR, Se1.DEVICE, null, DRIVER_INFO, "ssmCurtis");
	}

	/** Constructor for for actual work. */
	public Se1Device(Preferences prefs) {
		this();
		this.prefs = prefs;

		// setMaxProgramForLibraryStorage(Lamm.PROGRAM_COUNT_IN_BANK);
		setMaxProgramForLibraryStorage(Se1.PROGRAM_COUNT_IN_BANK);

		addDriver(new Se1PatchSingleDriver());

		Se1PatchBankDriver libraryDriver = new Se1PatchBankDriver();
		libraryDriver.setUseForStoreLibrary(true);
		addDriver(libraryDriver);
	}

	@Override
	public boolean comparePatches(Patch p1, Patch p2) {
		// do not compare checksum and header
		byte[] stay = p1.getByteArray();

		int stayLength = stay.length - 2 - Se1.HEADER_SIZE;
		byte[] stay1 = new byte[stayLength];
		System.arraycopy(stay, Se1.HEADER_SIZE, stay1, 0, stayLength);

		byte[] delete = p2.getByteArray();

		int deleteLength = delete.length - 2 - Se1.HEADER_SIZE;
		byte[] delete1 = new byte[deleteLength];
		System.arraycopy(delete, Se1.HEADER_SIZE, delete1, 0, deleteLength);

		return Arrays.equals(stay1, delete1);
	}

}
