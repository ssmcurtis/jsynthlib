/*
 * ReassignPatchDialog.java
 */

package org.jsynthlib.menu.window;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.jsynthlib.model.driver.SynthDriver;
import org.jsynthlib.model.driver.SynthDriverPatch;
import org.jsynthlib.model.patch.Patch;
import org.jsynthlib.tools.DriverUtil;
import org.jsynthlib.tools.TableUtil;

import org.jsynthlib.tools.ErrorMsgUtil;

/**
 * If more than two devices are loaded which supports the given patch, show this Dialog to choose a new Device/Driver
 * combination for the patch. The internal patch assignment is used to send/play a patch.
 * 
 * @author Torsten Tittmann
 * @version $Id$
 */
public class ExtractPatchesDialog extends DeviceDriverBankPatchSelector {

	private ByteBuffer byteBuffer;
	private String filename;

	/**
	 * Constructor
	 * 
	 * @param patch
	 *            The Patch to reassign
	 */
	public ExtractPatchesDialog(ByteBuffer byteBuffer, String patchHeaderString, String filename) {
		super(patchHeaderString, "Extract patches from generic");
		this.byteBuffer = byteBuffer;
		this.filename = filename;

		initDialog("Extract for device", false);
	}

	/**
	 * Makes the actual work after pressing the 'Reassign' button
	 */
	protected void doit() {
		ErrorMsgUtil.reportStatus(">>>> Action ");
		SynthDriverPatch driver = (SynthDriverPatch) driverComboBox.getSelectedItem();

		List<byte[]> setOfSysex = DriverUtil.splitSysexBytearray(byteBuffer);
		List<Patch> patchList = new ArrayList<>();
		
		for (byte[] oneSysex : setOfSysex) {
			
			if (oneSysex.length == driver.getPatchSize()) {

				Patch patch = driver.createPatch(oneSysex, filename);
				patchList.add(patch);
			}
			
		}

		TableUtil.addPatchToTable(patchList);

		setVisible(false);
		dispose();
	}

	@Override
	protected boolean patchIsSupported(SynthDriver driver) {
		return driver.isSingleDriver() && driver.supportsHeader(getPatchHeaderString());
	}

}
