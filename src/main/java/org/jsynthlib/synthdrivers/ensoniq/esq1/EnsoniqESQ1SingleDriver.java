/*
 * @version $Id$
 */
package org.jsynthlib.synthdrivers.ensoniq.esq1;

import org.jsynthlib.menu.patch.Driver;
import org.jsynthlib.menu.patch.Patch;
import org.jsynthlib.menu.ui.JSLFrame;
import org.jsynthlib.tools.ErrorMsg;

public class EnsoniqESQ1SingleDriver extends Driver {

	public EnsoniqESQ1SingleDriver() {
		super("Single", "Brian Klock");
		sysexID = "F00F02**01";
		patchSize = 0;
		patchNameStart = 0;
		patchNameSize = 10;
		deviceIDoffset = 3;
		checksumStart = 0;
		checksumEnd = 0;
		checksumOffset = 0;
		bankNumbers = new String[] { "0-Internal", "1-Cart A", "2-Cart B" };
		patchNumbers = new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13",
				"14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
				"31", "32", "33", "34", "35", "36", "37", "38", "39", "40" };

	}

	public String getPatchName(Patch ip) {
		Patch p = (Patch) ip;
		try {
			byte[] b = new byte[8];
			b[0] = ((byte) (p.getSysex()[5] + p.getSysex()[6] * 16));
			b[1] = ((byte) (p.getSysex()[7] + p.getSysex()[8] * 16));
			b[2] = ((byte) (p.getSysex()[9] + p.getSysex()[10] * 16));
			b[3] = ((byte) (p.getSysex()[11] + p.getSysex()[12] * 16));
			b[4] = ((byte) (p.getSysex()[13] + p.getSysex()[14] * 16));
			b[5] = ((byte) (p.getSysex()[15] + p.getSysex()[16] * 16));
			StringBuffer s = new StringBuffer(new String(b, 0, 6, "US-ASCII"));
			return s.toString();
		} catch (Exception ex) {
			return "-";
		}
	}

	public void setPatchName(Patch p, String name) {
		byte[] namebytes = new byte[32];
		try {
			if (name.length() < 6)
				name = name + "        ";
			namebytes = name.getBytes("US-ASCII");
			((Patch) p).getSysex()[5] = ((byte) (namebytes[0] % 16));
			((Patch) p).getSysex()[6] = ((byte) (namebytes[0] / 16));
			((Patch) p).getSysex()[7] = ((byte) (namebytes[1] % 16));
			((Patch) p).getSysex()[8] = ((byte) (namebytes[1] / 16));
			((Patch) p).getSysex()[9] = ((byte) (namebytes[2] % 16));
			((Patch) p).getSysex()[10] = ((byte) (namebytes[2] / 16));
			((Patch) p).getSysex()[11] = ((byte) (namebytes[3] % 16));
			((Patch) p).getSysex()[12] = ((byte) (namebytes[3] / 16));
			((Patch) p).getSysex()[13] = ((byte) (namebytes[4] % 16));
			((Patch) p).getSysex()[14] = ((byte) (namebytes[4] / 16));
			((Patch) p).getSysex()[15] = ((byte) (namebytes[5] % 16));
			((Patch) p).getSysex()[16] = ((byte) (namebytes[5] / 16));
		} catch (Exception e) {
		}
	}

	/*
	 * public void choosePatch (Patch p) {storePatch(p,0,0);}
	 */

	public void storePatch(Patch p, int bankNum, int patchNum) {
		sendPatchWorker(p);
		ErrorMsg.reportWarning(
				"Ensoniq ESQ!",
				"The patch has been placed in the edit buffer\nYou must now hold the 'write' button 'exit' on the ESQ1's\nand choose a location to store the patch.");
	}

	public void sendPatch(Patch p) {
		sendPatchWorker(p);
		ErrorMsg.reportWarning("Ensoniq ESQ!",
				"You must now hit 'exit' on the ESQ1's\nfront panel before you can\nsend another patch.");
	}

	protected void calculateChecksum(Patch p, int start, int end, int ofs) {
		// This synth does not use a checksum
	}

	public Patch createNewPatch() {
		byte[] sysex = new byte[210];
		sysex[0] = (byte) 0xF0;
		sysex[1] = (byte) 0x0F;
		sysex[2] = (byte) 0x02;
		sysex[3] = (byte) 0x00;
		sysex[4] = (byte) 0x01;
		sysex[209] = (byte) 0xF7;
		Patch p = new Patch(sysex, this);
		setPatchName(p, "NEWSND");
		calculateChecksum(p);
		return p;
	}

	public JSLFrame editPatch(Patch p) {
		return null;// return new KawaiK4SingleEditor((Patch)p);
	}
}
