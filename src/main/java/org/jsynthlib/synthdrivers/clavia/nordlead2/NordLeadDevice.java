// written by Kenneth L. Martinez
// $Id$
package org.jsynthlib.synthdrivers.clavia.nordlead2;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.prefs.Preferences;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jsynthlib.model.device.Device;
import org.jsynthlib.model.patch.Patch;

public class NordLeadDevice extends Device implements ItemListener {
	static final String DRIVER_INFO = "COPY FORM NL1: Slot one's edit buffer will be used to send and play patches.\n"
			+ "Please be sure to properly set the two required midi channels - "
			+ "the global channel (default is 16), and the first slot's midi " + "channel (default is 1).  Set the global channel on the\n"
			+ "Configuration tab of Show Details.\n\n" + "If the global channel is incorrect, patches can't be sent "
			+ "or received via sysex.\n\n" + "The Nord Lead requires the correct bank to be selected before "
			+ "storing a patch; if the slot one channel is incorrect, " + "JSynthLib's attempt to select the bank will fail and the patch "
			+ "may be stored in the wrong bank!\n\n" + "When receiving performances from the Nord, you must manually "
			+ "select the ROM or card performance bank.  Patch or drum banks "
			+ "can be selected automatically by JSynthLib if the slot one midi " + "channel is correct.";
	String channels[] = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16" };

	JComboBox<String> channelList;

	/** Creates new NordLead */
	public NordLeadDevice() {
		super(NordLead2x.VENDOR, NordLead2x.DEVICE, null, DRIVER_INFO, "Kenneth L. Martinez, ssmCurtis");
	}

	/** Constructor for for actual work. */
	public NordLeadDevice(Preferences prefs) {
		this();
		this.prefs = prefs;

		setMaxProgramForLibraryStorage(NordLead2x.PROGRAM_COUNT_IN_BANK);

		NL2xPatchSingleDriver libraryDriver = new NL2xPatchSingleDriver();
		libraryDriver.setUseForStoreLibrary(true);
		addDriver(libraryDriver);

		addDriver(new NL2xPatchBankDriver());
		addDriver(new NL2xPerfBankDriver());
		addDriver(new NL2xPerfSingleDriver());
	
	}
	
	@Override
	public JPanel config() {
		JPanel panel = new JPanel();

		panel.add(new JLabel("Select Nord Lead Global Channel"));
		channelList = new JComboBox<String>(channels);
		// channelList.setMaximumSize(new Dimension(25, 25));
		channelList.setSelectedIndex(getGlobalChannel() - 1);
		channelList.addItemListener(this);
		panel.add(channelList);

		return panel;
	}

	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() != ItemEvent.SELECTED) {
			return;
		}
		if (e.getItemSelectable() == channelList) {
			setGlobalChannel(channelList.getSelectedIndex() + 1);
		}
	}

	/**
	 * Getter for property globalChannel.
	 * 
	 * @return Value of property globalChannel.
	 * 
	 */
	public int getGlobalChannel() {
		return prefs.getInt("globalChannel", 0);
	}

	/**
	 * Setter for property globalChannel.
	 * 
	 * @param globalChannel
	 *            New value of property globalChannel.
	 * 
	 */
	public void setGlobalChannel(int globalChannel) {
		prefs.putInt("globalChannel", globalChannel);
	}

	@Override
	public boolean comparePatches(Patch p1, Patch p2) {
		// do not compare checksum and header
		byte[] stay = p1.getByteArray();

		int stayLength = stay.length - 2 - NordLead2x.HEADER_SIZE;
		byte[] stay1 = new byte[stayLength];
		System.arraycopy(stay, NordLead2x.HEADER_SIZE, stay1, 0, stayLength);

		byte[] delete = p2.getByteArray();

		int deleteLength = delete.length - 2 - NordLead2x.HEADER_SIZE;
		byte[] delete1 = new byte[deleteLength];
		System.arraycopy(delete, NordLead2x.HEADER_SIZE, delete1, 0, deleteLength);

		return Arrays.equals(stay1, delete1);
	}

}
