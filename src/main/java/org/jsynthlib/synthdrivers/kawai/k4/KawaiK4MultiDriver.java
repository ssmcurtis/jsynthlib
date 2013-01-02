package org.jsynthlib.synthdrivers.kawai.k4;

import org.jsynthlib.menu.JSLFrame;
import org.jsynthlib.menu.helper.SysexHandler;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.DriverUtil;

/**
 * Single Multi Patch Driver for Kawai K4.
 * 
 * @author Gerrit Gehnen
 * @version $Id$
 */

public class KawaiK4MultiDriver extends SynthDriverPatchImpl {
	/** Header Size */
	private static final int HSIZE = 8;
	/** Single Patch size */
	private static final int SSIZE = 77;

	private static final SysexHandler SYS_REQ = new SysexHandler("F0 40 @@ 00 00 04 *bankNum* *patchNum* F7");

	public KawaiK4MultiDriver() {
		super("Multi", "Gerrit Gehnen");
		sysexID = "F040**2*0004";

		patchSize = HSIZE + SSIZE + 1;
		patchNameStart = HSIZE;
		patchNameSize = 10;
		deviceIDoffset = 2;
		checksumStart = HSIZE;
		checksumEnd = HSIZE + SSIZE - 2;
		checksumOffset = HSIZE + SSIZE - 1;
		bankNumbers = new String[] { "0-Internal", "1-External" };
		patchNumbers = new String[16 * 4];
		System.arraycopy(DriverUtil.generateNumbers(1, 16, "A-##"), 0, patchNumbers, 0, 16);
		System.arraycopy(DriverUtil.generateNumbers(1, 16, "B-##"), 0, patchNumbers, 16, 16);
		System.arraycopy(DriverUtil.generateNumbers(1, 16, "C-##"), 0, patchNumbers, 32, 16);
		System.arraycopy(DriverUtil.generateNumbers(1, 16, "D-##"), 0, patchNumbers, 48, 16);
	}

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		setBankNum(bankNum);
		setPatchNum(patchNum);
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}
		p.getSysex()[3] = (byte) 0x20;
		p.getSysex()[6] = (byte) (bankNum << 1);
		p.getSysex()[7] = (byte) (patchNum + 0x40);
		sendPatchWorker(p);
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}
		setPatchNum(patchNum);
	}

	public void sendPatch(PatchDataImpl p) {
		p.getSysex()[3] = (byte) 0x23;
		p.getSysex()[7] = (byte) 0x40;
		sendPatchWorker(p);
	}

	protected void calculateChecksum(PatchDataImpl p, int start, int end, int ofs) {
		int sum = 0;
		for (int i = start; i <= end; i++) {
			sum += p.getSysex()[i];
		}
		sum += 0xA5;
		p.getSysex()[ofs] = (byte) (sum % 128);
	}

	public PatchDataImpl createNewPatch() {
		byte[] sysex = new byte[HSIZE + SSIZE + 1];
		sysex[0] = (byte) 0xF0;
		sysex[1] = (byte) 0x40;
		sysex[2] = (byte) 0x00;
		sysex[3] = (byte) 0x20;
		sysex[4] = (byte) 0x00;
		sysex[5] = (byte) 0x04;
		sysex[6] = (byte) 0x0;
		sysex[7] = 0x40;
		sysex[HSIZE + SSIZE] = (byte) 0xF7;
		for (int i = 0; i < 8; i++) {
			sysex[12 + 6 + 8 + i * 8] = 24;
			sysex[12 + 7 + 8 + i * 8] = 50;
		}
		PatchDataImpl p = new PatchDataImpl(sysex, this);
		setPatchName(p, "New Patch");
		calculateChecksum(p);
		return p;
	}

	public JSLFrame editPatch(PatchDataImpl p) {
		return new KawaiK4MultiEditor(p);
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		send(SYS_REQ.toSysexMessage(getChannel(), new SysexHandler.NameValue("bankNum", bankNum << 1),
				new SysexHandler.NameValue("patchNum", patchNum + 0x40)));
	}
}
