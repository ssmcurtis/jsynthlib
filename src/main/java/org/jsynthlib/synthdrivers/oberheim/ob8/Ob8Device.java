package org.jsynthlib.synthdrivers.oberheim.ob8;

import java.util.Arrays;
import java.util.prefs.Preferences;

import javax.swing.JComboBox;

import org.jsynthlib.model.device.Device;
import org.jsynthlib.model.patch.Patch;

public class Ob8Device extends Device {
	static final String DRIVER_INFO = Ob8.VENDOR + " " + Ob8.DEVICE;

	JComboBox<String> channelList;

	public Ob8Device() {
		super(Ob8.VENDOR, Ob8.DEVICE, null, DRIVER_INFO, "ssmCurtis");
	}

	public Ob8Device(Preferences prefs) {
		this();
		this.prefs = prefs;

		setMaxProgramForLibraryStorage(Ob8.PROGRAM_COUNT_IN_SYNTH);

		Ob8SingleDriver libraryDriver = new Ob8SingleDriver();
		libraryDriver.setUseForStoreLibrary(true);
		addDriver(libraryDriver);

		addDriver(new Ob8BankDriver());
	}

	@Override
	public boolean comparePatches(Patch p1, Patch p2) {
		// only compare program data
		byte[] stay = p1.getByteArray();

		int stayLength = stay.length - Ob8.FOOTER_SIZE - Ob8.HEADER_SIZE;
		byte[] stay1 = new byte[stayLength];
		System.arraycopy(stay, Ob8.HEADER_SIZE, stay1, 0, stayLength);

		byte[] delete = p2.getByteArray();

		int deleteLength = delete.length - Ob8.FOOTER_SIZE - Ob8.HEADER_SIZE;
		byte[] delete1 = new byte[deleteLength];
		System.arraycopy(delete, Ob8.HEADER_SIZE, delete1, 0, deleteLength);

		return Arrays.equals(stay1, delete1);
	}

}
