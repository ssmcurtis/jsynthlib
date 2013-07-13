// written by Kenneth L. Martinez
// $Id$
package org.jsynthlib.synthdrivers.clavia.nordlead;

import org.jsynthlib.model.driver.NameValue;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

public class NLPatchSingleDriver extends SynthDriverPatchImpl {
	static final String BANK_LIST[] = new String[] { "User 1", "User 2/PCMCIA 1", "User 3/PCMCIA 2", "User 4/PCMCIA 3" };
	static final String PATCH_LIST[] = new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
			"12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
			"30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47",
			"48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65",
			"66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83",
			"84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99" };
	static final int BANK_NUM_OFFSET = 4;
	static final int PATCH_NUM_OFFSET = 5;
	static final byte NEW_PATCH[] = { (byte) 0xF0, (byte) 0x33, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x00,
			(byte) 0x0C, (byte) 0x03, (byte) 0x03, (byte) 0x04, (byte) 0x09, (byte) 0x03, (byte) 0x0D, (byte) 0x03,
			(byte) 0x00, (byte) 0x00, (byte) 0x0A, (byte) 0x03, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x0C, (byte) 0x00, (byte) 0x0E, (byte) 0x04, (byte) 0x02, (byte) 0x01, (byte) 0x00, (byte) 0x07,
			(byte) 0x02, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x0E, (byte) 0x07, (byte) 0x08, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x0D, (byte) 0x03, (byte) 0x01, (byte) 0x01, (byte) 0x08, (byte) 0x04,
			(byte) 0x03, (byte) 0x04, (byte) 0x08, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x0F, (byte) 0x02,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x02, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x02, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0xF7 };

	// NordLeadConfig nlConfig;

	public NLPatchSingleDriver() {
		super("Patch Single", "Kenneth L. Martinez");
		// public NLPatchSingleDriver(NordLeadConfig nlc) {
		// nlConfig = nlc;
		sysexID = "F033**04**";
		sysexRequestDump = new SysexHandler("F0 33 @@ 04 *bankNum* *patchNum* F7");

		patchSize = 139;
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
		return "prog" + (((PatchDataImpl) ip).getSysex()[PATCH_NUM_OFFSET] + 1);
	}

	public void setPatchName(PatchDataImpl p, String name) {
	}

	public void sendPatch(PatchDataImpl p) {
		sendPatch((PatchDataImpl) p, 0, 0); // using edit buffer for slot A
	}

	public void sendPatch(PatchDataImpl p, int bankNum, int patchNum) {
		PatchDataImpl p2 = new PatchDataImpl(p.getSysex());
		p2.getSysex()[BANK_NUM_OFFSET] = (byte) bankNum;
		p2.getSysex()[PATCH_NUM_OFFSET] = (byte) patchNum;
		mySendPatch(p2);
	}

	// Sends a patch to a set location in the user bank
	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		setBankNum(bankNum); // must set bank - sysex patch dump always stored in current bank
		sendProgramChange(patchNum); // must send program change to make bank change take effect
		sendPatch((PatchDataImpl) p, bankNum + 1, patchNum);
		sendProgramChange(patchNum); // send another program change to get new sound in edit buffer
	}

	public void playPatch(PatchDataImpl p) {
		byte sysex[] = new byte[patchSize];
		System.arraycopy(((PatchDataImpl) p).getSysex(), 0, sysex, 0, patchSize);
		sysex[BANK_NUM_OFFSET] = 0; // edit buffer
		sysex[PATCH_NUM_OFFSET] = 0; // slot A
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
		ErrorMsgUtil.reportStatus("Bank: " + bankNum);
		ErrorMsgUtil.reportStatus("Patch: " + patchNum);

		send(sysexRequestDump.toSysexMessage(((NordLeadDevice) getDevice()).getGlobalChannel(),
				new NameValue("bankNum", bankNum + 11), new NameValue("patchNum", patchNum)));
	}
}
