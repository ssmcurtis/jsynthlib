// written by Kenneth L. Martinez
// @version $Id$

package org.jsynthlib.synthdrivers.roland.mks50;

import org.jsynthlib.menu.JSLFrame;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;

public class MKS50PatchSingleDriver extends SynthDriverPatchImpl {
	public MKS50PatchSingleDriver() {
		super("Patch Single", "Kenneth L. Martinez");
		sysexID = "F041350*233001";
		sysexRequestDump = new SysexHandler("F0 10 06 04 01 *patchNum* F7");

		patchSize = 31;
		patchNameStart = 20;
		patchNameSize = 10;
		deviceIDoffset = 3;
		bankNumbers = new String[] { "Patch Bank" };
		patchNumbers = new String[] { "11-", "12-", "13-", "14-", "15-", "16-", "17-", "18-", "21-", "22-", "23-",
				"24-", "25-", "26-", "27-", "28-", "31-", "32-", "33-", "34-", "35-", "36-", "37-", "38-", "41-",
				"42-", "43-", "44-", "45-", "46-", "47-", "48-", "51-", "52-", "53-", "54-", "55-", "56-", "57-",
				"58-", "61-", "62-", "63-", "64-", "65-", "66-", "67-", "68-", "71-", "72-", "73-", "74-", "75-",
				"76-", "77-", "78-", "81-", "82-", "83-", "84-", "85-", "86-", "87-", "88-" };
	}

	public void calculateChecksum(PatchDataImpl p) {
		// MKS-50 doesn't use checksum
	}

	// protected static void calculateChecksum(Patch p, int start, int end, int ofs)
	// {
	// // MKS-50 doesn't use checksum
	// }

	public void setBankNum(int bankNum) {
		// MKS-50 doesn't have banks: pgm# 0-63 is group A, 64-127 is group B
	}

	public String getPatchName(PatchDataImpl ip) {
		try {
			char c[] = new char[patchNameSize];
			for (int i = 0; i < patchNameSize; i++)
				c[i] = MKS50ToneSingleDriver.nameChars[((PatchDataImpl) ip).getSysex()[i + patchNameStart]];
			return new String(c);
		} catch (Exception ex) {
			return "-";
		}
	}

	public void setPatchName(PatchDataImpl p, String name) {
		String s = new String(MKS50ToneSingleDriver.nameChars);
		for (int i = 0; i < patchNameSize; i++) {
			int j;
			if (i < name.length()) {
				j = s.indexOf(name.charAt(i));
				if (j == -1)
					j = 62; // convert invalid character to space
			} else
				j = 62; // pad with spaces
			((PatchDataImpl) p).getSysex()[i + patchNameStart] = (byte) j;
		}
	}

	public PatchDataImpl createNewPatch() {
		byte sysex[] = { (byte) 0xF0, (byte) 0x41, (byte) 0x35, (byte) 0x00, (byte) 0x23, (byte) 0x30, (byte) 0x01,
				(byte) 0x00, (byte) 0x0C, (byte) 0x6D, (byte) 0x14, (byte) 0x00, (byte) 0x20, (byte) 0x00, (byte) 0x7F,
				(byte) 0x00, (byte) 0x00, (byte) 0x0C, (byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0x27, (byte) 0x22,
				(byte) 0x2D, (byte) 0x3E, (byte) 0x3E, (byte) 0x3E, (byte) 0x3E, (byte) 0x3E, (byte) 0x3E, (byte) 0xF7 };
		PatchDataImpl p = new PatchDataImpl(sysex, this);
		setPatchName(p, "NewPatch");
		return p;
	}

	public JSLFrame editPatch(PatchDataImpl p) {
		return new MKS50PatchSingleEditor((PatchDataImpl) p);
	}
}
