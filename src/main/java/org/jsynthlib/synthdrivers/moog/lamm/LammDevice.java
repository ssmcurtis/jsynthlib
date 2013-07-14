package org.jsynthlib.synthdrivers.moog.lamm;

import java.util.Arrays;
import java.util.prefs.Preferences;

import javax.swing.JComboBox;

import org.jsynthlib.model.device.Device;
import org.jsynthlib.model.patch.Patch;

public class LammDevice extends Device {
	String channels[] = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16" };

	JComboBox<String> channelList;

	public LammDevice() {
		super(Lamm.VENDOR, Lamm.DEVICE, null, Lamm.DRIVER_INFO, "ssmCurtis");
	}

	/** Constructor for for actual work. */
	public LammDevice(Preferences prefs) {
		this();
		this.prefs = prefs;

		// setMaxProgramForLibraryStorage(Lamm.PROGRAM_COUNT_IN_BANK);
		setMaxProgramForLibraryStorage(32);

		LammPatchSingleDriver libraryDriver = new LammPatchSingleDriver();
		libraryDriver.setUseForStoreLibrary(true);
		addDriver(libraryDriver);

		addDriver(new LammPatchBankDriver());

	}

	@Override
	public boolean comparePatches(Patch p1, Patch p2) {
		// do not compare checksum and header
		byte[] stay = p1.getByteArray();

		int stayLength = stay.length - 2 - Lamm.HEADER_SIZE;
		byte[] stay1 = new byte[stayLength];
		System.arraycopy(stay, Lamm.HEADER_SIZE, stay1, 0, stayLength);

		byte[] delete = p2.getByteArray();

		int deleteLength = delete.length - 2 - Lamm.HEADER_SIZE;
		byte[] delete1 = new byte[deleteLength];
		System.arraycopy(delete, Lamm.HEADER_SIZE, delete1, 0, deleteLength);

		return Arrays.equals(stay1, delete1);
	}

}
