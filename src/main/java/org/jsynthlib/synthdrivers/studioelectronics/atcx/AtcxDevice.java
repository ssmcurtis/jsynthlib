package org.jsynthlib.synthdrivers.studioelectronics.atcx;

import java.util.Arrays;
import java.util.prefs.Preferences;

import javax.swing.JComboBox;

import org.jsynthlib.model.device.Device;
import org.jsynthlib.model.patch.Patch;
import org.jsynthlib.synthdrivers.studioelectronics.se1.Se1;

public class AtcxDevice extends Device {
	static final String DRIVER_INFO = Atcx.VENDOR + " " + Atcx.DEVICE
			+ "  Dumps must be initialted from synth. \n save & edit bank";

	JComboBox<String> channelList;

	public AtcxDevice() {
		super(Atcx.VENDOR, Atcx.DEVICE, null, DRIVER_INFO, "ssmCurtis");
	}

	/** Constructor for for actual work. */
	public AtcxDevice(Preferences prefs) {
		this();
		this.prefs = prefs;

		// setMaxProgramForLibraryStorage(Lamm.PROGRAM_COUNT_IN_BANK);
		setMaxProgramForLibraryStorage(Atcx.PROGRAM_COUNT_IN_BANK);

		addDriver(new AtcxPatchSingleDriver());

		AtcxPatchBankDriver libraryDriver = new AtcxPatchBankDriver();
		libraryDriver.setUseForStoreLibrary(true);
		addDriver(libraryDriver);
	}

	@Override
	public boolean comparePatches(Patch p1, Patch p2) {
		// do not compare footer and header
		byte[] stay = p1.getByteArray();

		int stayLength = stay.length - 1 - Atcx.HEADER_SIZE;
		byte[] stay1 = new byte[stayLength];
		System.arraycopy(stay, Atcx.HEADER_SIZE, stay1, 0, stayLength);

		byte[] delete = p2.getByteArray();

		int deleteLength = delete.length - 1 - Atcx.HEADER_SIZE;
		byte[] delete1 = new byte[deleteLength];
		System.arraycopy(delete, Atcx.HEADER_SIZE, delete1, 0, deleteLength);

		return Arrays.equals(stay1, delete1);
	}

}
