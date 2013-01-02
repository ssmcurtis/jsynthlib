/*
 * SysexStoreDialog.java
 */

package org.jsynthlib.menu.window;

import org.jsynthlib.model.driver.SynthDriverPatch;
import org.jsynthlib.model.patch.Patch;

/**
 * Dialog to choose the Device, Driver, BankNumber and PatchNumber of the location, where a Patch should be stored. More
 * than one of each device is supported, but only devices/drivers are selectable, which support the patch.
 * 
 * @author Torsten Tittmann
 * @version $Id$
 */
public class SysexStoreDialog extends DevDrvPatchSelector {

	/**
	 * Constructor with choosable default patchNumber.
	 * 
	 * @param patch
	 *            The Patch to store.
	 * @param patchnum
	 *            The default patchNumber selected in the patch Combobox.
	 */
	public SysexStoreDialog(Patch patch, int banknum, int patchnum) {
		super(patch, banknum, patchnum, "Store Sysex Data");
		initDialog("Please select a Location to store...", true);
	}

	/**
	 * getPatchNumbers is overridden for SystexStoreDialog. Only storable patches are displayed.
	 */
	protected String[] getPatchNumbers(SynthDriverPatch driver) {
		return driver.getPatchNumbersForStore();
	}

	/**
	 * Makes the actual work after pressing the 'Store' button
	 */
	protected void doit() {
		patchGlobal.setDriver((SynthDriverPatch) driverComboBox.getSelectedItem());
		int bankNum = bankComboBox.getSelectedIndex();
		int patchNum = patchNumComboBox.getSelectedIndex();
		patchGlobal.send(bankNum, patchNum);

		setVisible(false);
		dispose();
	}
}
