package org.jsynthlib.synthdrivers.clavia.nordstage;

import org.jsynthlib.menu.helper.SysexHandler;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.patch.PatchDataImpl;

/**
 * Single Voice Patch Driver for Clavia Nord Stage.
 */
public class NordStagePatchSingleDriver extends SynthDriverPatchImpl {

	private static final int HEADER_SIZE = 8;
	private static final int SINGLE_PATCH_SIZE = 471;

	private static final SysexHandler SYS_REQ = new SysexHandler("F0 33 @@ 0B 39 *bankNum* *patchNum* F7");

	public NordStagePatchSingleDriver() {
		super("Single", "Fredrik Zetterberg & James Fry");

		// the following are declared in the superclass
		this.sysexID = "F033**0b2*00";
		this.patchSize = HEADER_SIZE + SINGLE_PATCH_SIZE + 2;
		this.patchNameStart = HEADER_SIZE + 1;
		this.patchNameSize = 10;
		this.deviceIDoffset = 2;
		this.checksumStart = HEADER_SIZE - 1;
		this.checksumEnd = HEADER_SIZE + SINGLE_PATCH_SIZE;
		this.checksumOffset = HEADER_SIZE + SINGLE_PATCH_SIZE + 1;

		// this is the correct way of naming the banks and patches
		this.bankNumbers = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14",
				"15", "16", "17", "18", "19", "20", "21" };
		this.patchNumbers = new String[] { "1", "2", "3", "4", "5", "6" };
	}

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		setBankNum(bankNum);
		setPatchNum(patchNum);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		p.getSysex()[3] = (byte) 0x20;
		p.getSysex()[6] = (byte) (bankNum << 1);
		p.getSysex()[7] = (byte) (patchNum);
		sendPatchWorker(p);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setPatchNum(patchNum);
	}

	public void sendPatch(PatchDataImpl p) {
		p.getSysex()[3] = (byte) 0x23;
		p.getSysex()[7] = (byte) 0x00;
		sendPatchWorker(p);
	}

	protected void calculateChecksum(PatchDataImpl p, int start, int end, int ofs) {
		int sum = 0;
		// Nord Stage checksum is defined in the documentation as being a sum of
		// all bytes
		for (int i = start; i <= end; i++) {
			sum += p.getSysex()[i];
		}
		sum += 0xA5;
		// do we need to mod / xor it with 128/127 (as data bytes have to be
		// <128) ?
		p.getSysex()[ofs] = (byte) (sum % 128);
		// p.sysex[ofs]=(byte)(p.sysex[ofs]^127);
		// p.sysex[ofs]=(byte)(p.sysex[ofs]+1);
	}

	public PatchDataImpl createNewPatch() {
		byte[] sysex = new byte[HEADER_SIZE + SINGLE_PATCH_SIZE + 1];
		sysex[0] = (byte) 0xF0;
		sysex[1] = (byte) 0x40;
		sysex[2] = (byte) 0x00;
		sysex[3] = (byte) 0x23;
		sysex[4] = (byte) 0x00;
		sysex[5] = (byte) 0x04;
		sysex[6] = (byte) 0x0;
		sysex[HEADER_SIZE + SINGLE_PATCH_SIZE] = (byte) 0xF7;
		PatchDataImpl p = new PatchDataImpl(sysex, this);
		setPatchName(p, "New Patch");
		calculateChecksum(p);
		return p;

	}

	@Override
	public String getPatchName(PatchDataImpl p) {
		return "";
	}

	@Override
	public void setPatchName(PatchDataImpl p, String s) {
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		send(SYS_REQ.toSysexMessage(getChannel(), new SysexHandler.NameValue("bankNum", bankNum << 1),
				new SysexHandler.NameValue("patchNum", patchNum)));
	}

}
