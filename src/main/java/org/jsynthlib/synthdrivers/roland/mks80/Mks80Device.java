package org.jsynthlib.synthdrivers.roland.mks80;

import java.util.Arrays;
import java.util.prefs.Preferences;

import javax.swing.Action;
import javax.swing.JComboBox;

import org.jsynthlib.menu.Actions;
import org.jsynthlib.model.device.Device;
import org.jsynthlib.model.patch.Patch;

public class Mks80Device extends Device {
	static final String DRIVER_INFO = Mks80.VENDOR + " " + Mks80.DEVICE + "  successfull tested using Yamaha UX16, "
			+ "BlueScreen using ESI, runs only once using Kexcon Omega";

	JComboBox<String> channelList;

	public Mks80Device() {
		super(Mks80.VENDOR, Mks80.DEVICE, null, DRIVER_INFO, "ssmCurtis");
	}

	/** Constructor for for actual work. */
	public Mks80Device(Preferences prefs) {
		this();
		this.prefs = prefs;

		setMaxProgramForLibraryStorage(Mks80.PROGRAM_COUNT_IN_BANK);

 		addDriver(new Mks80ToneSingleDriver());
 		addDriver(new Mks80TonePatchSingleDriver());

		Mks80TonePatchBankDriver libraryDriver = new Mks80TonePatchBankDriver();
		libraryDriver.setUseForStoreLibrary(true);
		addDriver(libraryDriver);

		Action specialAction = new Mks80ActionTonePatch(Actions.getMnemonics());
		Actions.addSpecialMenuAction(specialAction);
	}

	@Override
	public boolean comparePatches(Patch p1, Patch p2) {
		// do not compare checksum and header
		byte[] stay = p1.getByteArray();

		int stayLength = stay.length - Mks80.HEADER_SIZE - Mks80.FOOTER_SIZE;
		byte[] stay1 = new byte[stayLength];
		System.arraycopy(stay, Mks80.HEADER_SIZE, stay1, 0, stayLength);

		byte[] delete = p2.getByteArray();
		int deleteLength = delete.length - Mks80.HEADER_SIZE - Mks80.FOOTER_SIZE;
		byte[] delete1 = new byte[deleteLength];
		System.arraycopy(delete, Mks80.HEADER_SIZE, delete1, 0, deleteLength);

		return Arrays.equals(stay1, delete1);
	}

}
