/*
 * ReassignPatchDialog.java
 */

package org.jsynthlib.menu.window;

import java.nio.ByteBuffer;
import java.util.List;

import org.jsynthlib.model.driver.SynthDriver;
import org.jsynthlib.model.driver.SynthDriverPatch;
import org.jsynthlib.model.patch.Patch;
import org.jsynthlib.tools.DriverUtil;
import org.jsynthlib.tools.TableUtil;

/**
 * If more than two devices are loaded which supports the given patch, show this Dialog to choose a new Device/Driver
 * combination for the patch. The internal patch assignment is used to send/play a patch.
 * 
 * @author Torsten Tittmann
 * @version $Id$
 */
public class ExtractPatchesDialog extends DevDrvPatchSelector {

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
		System.out.println(">>>> Action ");
		SynthDriverPatch driver = (SynthDriverPatch) driverComboBox.getSelectedItem();

//		int bufSize = byteBuffer.capacity();
//		int cursor = 0;
//		List<Patch> li = new ArrayList<Patch>();
//		List<Byte> sysexList = new ArrayList<Byte>();
//		byte[] sysex = null;
//		boolean select = false;
//		while (cursor < bufSize) {
//			byte b = byteBuffer.get(cursor);
//			if (HexaUtil.isStartSysex(b)) {
//				select = true;
//			}
//			if (select) {
//				sysexList.add(b);
//			}
//			if (HexaUtil.isEndSysex(b)) {
//				select = false;
//				sysex = new byte[sysexList.size()];
//				int bc = 0;
//				for (Byte byteFromList : sysexList) {
//					sysex[bc] = byteFromList.byteValue();
//					bc++;
//				}
//				Patch patch = driver.createPatch(sysex, filename);
//				li.add(patch);
//				sysexList = new ArrayList<Byte>();
//			}
//			cursor++;
//		}
		
		List<byte[]> setOfSysex = DriverUtil.splitSysexBytearray(byteBuffer);
		Patch[] patcharray = new Patch[setOfSysex.size()];
		int index = 0;
		for(byte[] oneSysex : setOfSysex) {
			Patch patch = driver.createPatch(oneSysex, filename);
			patcharray[index] = patch;
			index++;
		}
		
		TableUtil.addPatchToTable(patcharray);

		setVisible(false);
		dispose();
	}

	@Override
	protected boolean patchIsSupported(SynthDriver driver) {
		return driver.isSingleDriver() && driver.supportsHeader(getPatchHeaderString());
	}

}
