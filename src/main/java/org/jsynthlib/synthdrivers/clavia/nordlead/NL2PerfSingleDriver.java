// written by Kenneth L. Martinez
// $Id$
package org.jsynthlib.synthdrivers.clavia.nordlead;

import org.jsynthlib.model.driver.NameValue;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

public class NL2PerfSingleDriver extends SynthDriverPatchImpl {
	static final String BANK_LIST[] = new String[] { "PCMCIA" };
	static final String PATCH_LIST[] = new String[] { "A0", "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "B0",
			"B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "B9", "C0", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8",
			"C9", "D0", "D1", "D2", "D3", "D4", "D5", "D6", "D7", "D8", "D9", "E0", "E1", "E2", "E3", "E4", "E5", "E6",
			"E7", "E8", "E9", "F0", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "G0", "G1", "G2", "G3", "G4",
			"G5", "G6", "G7", "G8", "G9", "H0", "H1", "H2", "H3", "H4", "H5", "H6", "H7", "H8", "H9", "J0", "J1", "J2",
			"J3", "J4", "J5", "J6", "J7", "J8", "J9", "L0", "L1", "L2", "L3", "L4", "L5", "L6", "L7", "L8", "L9" };
	static final int BANK_NUM_OFFSET = 4;
	static final int PATCH_NUM_OFFSET = 5;
	static final byte NEW_PATCH[] = { (byte) 0xF0, (byte) 0x33, (byte) 0x00, (byte) 0x04, (byte) 0x1F, (byte) 0x00,
			(byte) 0x07, (byte) 0x06, (byte) 0x0A, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x0D, (byte) 0x01,
			(byte) 0x03, (byte) 0x02, (byte) 0x09, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x0B, (byte) 0x06,
			(byte) 0x00, (byte) 0x00, (byte) 0x0E, (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x0A, (byte) 0x06,
			(byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x04,
			(byte) 0x00, (byte) 0x00, (byte) 0x0B, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x01,
			(byte) 0x01, (byte) 0x04, (byte) 0x08, (byte) 0x04, (byte) 0x0E, (byte) 0x03, (byte) 0x0B, (byte) 0x02,
			(byte) 0x0E, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0D, (byte) 0x0E,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x03, (byte) 0x01, (byte) 0x0F, (byte) 0x02, (byte) 0x00, (byte) 0x02, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00,
			(byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x03, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00,
			(byte) 0x03, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x0A, (byte) 0x04,
			(byte) 0x0B, (byte) 0x03, (byte) 0x06, (byte) 0x03, (byte) 0x03, (byte) 0x01, (byte) 0x0E, (byte) 0x04,
			(byte) 0x07, (byte) 0x03, (byte) 0x08, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x0E, (byte) 0x04,
			(byte) 0x0C, (byte) 0x02, (byte) 0x08, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0x05,
			(byte) 0x02, (byte) 0x06, (byte) 0x06, (byte) 0x01, (byte) 0x03, (byte) 0x02, (byte) 0x03, (byte) 0x03,
			(byte) 0x00, (byte) 0x00, (byte) 0x0D, (byte) 0x00, (byte) 0x03, (byte) 0x06, (byte) 0x03, (byte) 0x02,
			(byte) 0x00, (byte) 0x06, (byte) 0x0C, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00,
			(byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00,
			(byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x0A, (byte) 0x05, (byte) 0x00, (byte) 0x04, (byte) 0x0D, (byte) 0x03, (byte) 0x03, (byte) 0x05,
			(byte) 0x07, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x03, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x06, (byte) 0x02,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x01, (byte) 0x08, (byte) 0x01,
			(byte) 0x0E, (byte) 0x03, (byte) 0x0E, (byte) 0x02, (byte) 0x03, (byte) 0x06, (byte) 0x08, (byte) 0x03,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0B, (byte) 0x02, (byte) 0x0C, (byte) 0x01,
			(byte) 0x02, (byte) 0x02, (byte) 0x0E, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x04, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x0A, (byte) 0x05, (byte) 0x00, (byte) 0x04,
			(byte) 0x0D, (byte) 0x03, (byte) 0x03, (byte) 0x05, (byte) 0x07, (byte) 0x02, (byte) 0x00, (byte) 0x00,
			(byte) 0x04, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x06, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x03, (byte) 0x01, (byte) 0x08, (byte) 0x01, (byte) 0x0E, (byte) 0x03, (byte) 0x0E, (byte) 0x02,
			(byte) 0x03, (byte) 0x06, (byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x0B, (byte) 0x02, (byte) 0x0C, (byte) 0x01, (byte) 0x02, (byte) 0x02, (byte) 0x0E, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x04, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x03, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x01, (byte) 0x00,
			(byte) 0x07, (byte) 0x01, (byte) 0x07, (byte) 0x01, (byte) 0x07, (byte) 0x01, (byte) 0x07, (byte) 0x01,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x08, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x08, (byte) 0x00,
			(byte) 0x07, (byte) 0x01, (byte) 0x07, (byte) 0x01, (byte) 0x07, (byte) 0x01, (byte) 0x07, (byte) 0x01,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x08, (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x08, (byte) 0x00,
			(byte) 0x07, (byte) 0x01, (byte) 0x07, (byte) 0x01, (byte) 0x07, (byte) 0x01, (byte) 0x07, (byte) 0x01,
			(byte) 0x03, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x0F, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x01, (byte) 0x00, (byte) 0x0F, (byte) 0x0F, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x01,
			(byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xF7 };

	public NL2PerfSingleDriver() {
		super("Perf Single", "Kenneth L. Martinez");
		sysexID = "F033**04**";
		sysexRequestDump = new SysexHandler("F0 33 @@ 04 *bankNum* *patchNum* F7");

		patchSize = 715;
		patchNameStart = -1;
		patchNameSize = 0;
		deviceIDoffset = 2;
		bankNumbers = BANK_LIST;
		patchNumbers = PATCH_LIST;
	}

	public void calculateChecksum(PatchDataImpl p) {
		// doesn't use checksum
	}

	// protected static void calculateChecksum(Patch p, int start, int end, int ofs) {
	// // doesn't use checksum
	// }

	public String getPatchName(PatchDataImpl ip) {
		return "perf" + (((PatchDataImpl) ip).getSysex()[PATCH_NUM_OFFSET] + 1);
	}

	public void setPatchName(PatchDataImpl p, String name) {
	}

	public void sendPatch(PatchDataImpl p) {
		sendPatch((PatchDataImpl) p, 30, 0); // using edit buffer
	}

	public void sendPatch(PatchDataImpl p, int bankNum, int patchNum) {
		PatchDataImpl p2 = new PatchDataImpl(p.getSysex());
		p2.getSysex()[BANK_NUM_OFFSET] = (byte) bankNum;
		p2.getSysex()[PATCH_NUM_OFFSET] = (byte) patchNum;
		mySendPatch(p2);
	}

	// Sends a patch to a set location in the user bank
	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		sendPatch((PatchDataImpl) p, 31, patchNum);
		sendProgramChange(patchNum); // send program change to get new sound in edit buffer
	}

	public void playPatch(PatchDataImpl p) {
		byte sysex[] = new byte[patchSize];
		System.arraycopy(((PatchDataImpl) p).getSysex(), 0, sysex, 0, patchSize);
		sysex[BANK_NUM_OFFSET] = 30; // edit buffer
		sysex[PATCH_NUM_OFFSET] = 0;
		PatchDataImpl p2 = new PatchDataImpl(sysex);
		super.playPatch(p2);
	}

	public PatchDataImpl createNewPatch() {
		return new PatchDataImpl(NEW_PATCH, this);
	}

	protected void mySendPatch(PatchDataImpl p) {
		p.getSysex()[deviceIDoffset] = (byte) (((NordLeadDevice) getDevice()).getGlobalChannel() - 1);
		try {
			send(p.getSysex());
		} catch (Exception e) {
			ErrorMsgUtil.reportStatus(e);
		}
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		send(sysexRequestDump.toSysexMessage(((NordLeadDevice) getDevice()).getGlobalChannel(),
				new NameValue("bankNum", 41), new NameValue("patchNum", patchNum)));
	}
}
