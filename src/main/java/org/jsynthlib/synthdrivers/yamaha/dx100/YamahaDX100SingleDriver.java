/*
 * @version $Id$
 */
package org.jsynthlib.synthdrivers.yamaha.dx100;

import org.jsynthlib.menu.patch.Patch;
import org.jsynthlib.menu.patch.Driver;
import org.jsynthlib.menu.ui.JSLFrame;
import org.jsynthlib.tools.ErrorMsg;

public class YamahaDX100SingleDriver extends Driver {

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

	public void calculateChecksum(Patch ip) {
		Patch p = (Patch) ip;
		if (p.getSysex().length > 101) {
			byte[] newSysex = new byte[101];
			System.arraycopy(p.getSysex(), 0, newSysex, 0, 101);
			p.setSysex(newSysex);
		}
		calculateChecksum(p, 6, 98, 99); // calculate VCED Checksum
	}

	public void storePatch(Patch p, int bankNum, int patchNum) {
		setBankNum(bankNum);
		setPatchNum(patchNum);
		sendPatch(p);

		ErrorMsg.reportWarning("Yamaha DX 4op",
				"The patch has been placed in the edit buffer.\n You must choose to store it from the synths\nfront panel");
	}

	public Patch createNewPatch() {
		byte[] sysex = new byte[101];
		sysex[0] = (byte) 0xF0;
		sysex[1] = (byte) 0x43;
		sysex[2] = (byte) 0x00;
		sysex[3] = (byte) 0x03;
		sysex[4] = (byte) 0x00;
		sysex[5] = (byte) 0x5D;
		sysex[100] = (byte) 0xF7;
		Patch p = new Patch(sysex, this);
		setPatchName(p, "NewPatch");
		calculateChecksum(p);
		return p;
	}

	public JSLFrame editPatch(Patch p) {
		return new YamahaDX100SingleEditor((Patch) p);
	}
}
