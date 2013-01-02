/*
 * SysexSendToDialog.java
 */

package org.jsynthlib.menu.window;

import org.jsynthlib.model.driver.SynthDriverPatch;
import org.jsynthlib.model.patch.Patch;
import org.jsynthlib.model.patch.PatchSingle;

/**
 * Dialog to choose a Device and Driver to send the patch into an Edit buffer. More than one of each device is
 * supported, but only devices/drivers are selectable, which support the patch.
 * 
 * @author Torsten Tittmann
 * @version $Id$
 */
public class SysexSendToDialog extends DevDrvPatchSelector {
	/**
	 * Constructor
	 * 
	 * @param patch
	 *            The Patch to 'send to...'
	 */
	public SysexSendToDialog(Patch patch) {
		super(patch, "Send Sysex Data into Edit Buffer of a specified device");
		initDialog("Please select a Location to send To...", false);
	}

	/**
	 * Makes the actual work after pressing the 'Send to...' button
	 */
	protected void doit() {
		patchGlobal.setDriver((SynthDriverPatch) driverComboBox.getSelectedItem());
		((PatchSingle) patchGlobal).send();

		setVisible(false);
		dispose();
	}
}
