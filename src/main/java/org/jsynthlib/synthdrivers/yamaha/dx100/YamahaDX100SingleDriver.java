/*
 * @version $Id$
 */
package org.jsynthlib.synthdrivers.yamaha.dx100;

import org.jsynthlib.menu.JSLFrame;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

public class YamahaDX100SingleDriver extends SynthDriverPatchImpl {

	public YamahaDX100SingleDriver() {
		super("Single", "Brian Klock");
		sysexID = "F043**03005D";
		patchNameStart = 83;
		patchNameSize = 10;
		deviceIDoffset = 2;
		bankNumbers = new String[] { "0-Internal" };
		patchNumbers = new String[] { "I01", "I02", "I03", "I04", "I05", "I06", "I07", "I08", "I09", "I10", "I11",
				"I12", "I13", "I14", "I15", "I16", "I17", "I18", "I19", "I20", "I21", "I22", "I23", "I24" };
	}

	public void calculateChecksum(PatchDataImpl ip) {
		PatchDataImpl p = (PatchDataImpl) ip;
		if (p.getSysex().length > 101) {
			byte[] newSysex = new byte[101];
			System.arraycopy(p.getSysex(), 0, newSysex, 0, 101);
			p.setSysex(newSysex);
		}
		calculateChecksum(p, 6, 98, 99); // calculate VCED Checksum
	}

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		setBankNum(bankNum);
		setPatchNum(patchNum);
		sendPatch(p);

		ErrorMsgUtil.reportWarning("Yamaha DX 4op",
				"The patch has been placed in the edit buffer.\n You must choose to store it from the synths\nfront panel");
	}

	public PatchDataImpl createNewPatch() {
		byte[] sysex = new byte[101];
		sysex[0] = (byte) 0xF0;
		sysex[1] = (byte) 0x43;
		sysex[2] = (byte) 0x00;
		sysex[3] = (byte) 0x03;
		sysex[4] = (byte) 0x00;
		sysex[5] = (byte) 0x5D;
		sysex[100] = (byte) 0xF7;
		PatchDataImpl p = new PatchDataImpl(sysex, this);
		setPatchName(p, "NewPatch");
		calculateChecksum(p);
		return p;
	}

	public JSLFrame editPatch(PatchDataImpl p) {
		return new YamahaDX100SingleEditor((PatchDataImpl) p);
	}
}
