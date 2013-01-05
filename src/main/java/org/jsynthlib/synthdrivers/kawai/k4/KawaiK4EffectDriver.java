package org.jsynthlib.synthdrivers.kawai.k4;

import org.jsynthlib.menu.JSLFrame;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.DriverUtil;

/**
 * Single Effect Patch Driver for Kawai K4.
 * 
 * @author Gerrit Gehnen
 * @version $Id$
 */

public class KawaiK4EffectDriver extends SynthDriverPatchImpl {
	/** Header Size */
	private static final int HSIZE = 8;
	/** Single Patch size */
	private static final int SSIZE = 35;

	private static final SysexHandler SYS_REQ = new SysexHandler("F0 40 @@ 00 00 04 01 *patchNum* F7");

	public KawaiK4EffectDriver() {
		super("Effect", "Gerrit Gehnen");
		sysexID = "F040**2*0004";

		patchSize = HSIZE + SSIZE + 1;
		patchNameStart = 0;
		patchNameSize = 0;
		deviceIDoffset = 2;
		checksumStart = HSIZE;
		checksumEnd = HSIZE + SSIZE - 2;
		checksumOffset = HSIZE + SSIZE - 1;
		bankNumbers = new String[] { "0-Internal", "1-External" };
		// Is this correct? BulkConverter has 32 patches.
		/*
		 * patchNumbers = new String[31]; for (int i = 0; i < 31; i++) patchNumbers[i] = String.valueOf(i + 1);
		 */
		patchNumbers = DriverUtil.generateNumbers(1, 31, "00");
	}

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		setBankNum(bankNum);
		setPatchNum(patchNum);
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}
		p.getSysex()[3] = (byte) 0x20;
		p.getSysex()[6] = (byte) ((bankNum << 1) + 1);
		p.getSysex()[7] = (byte) (patchNum);
		sendPatchWorker(p);
		try {
			Thread.sleep(100);
		} catch (Exception e) {
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
		sysex[3] = (byte) 0x23;
		sysex[4] = (byte) 0x00;
		sysex[5] = (byte) 0x04;
		sysex[6] = (byte) 0x01;

		sysex[18] = 0x07;
		sysex[21] = 0x07;
		sysex[24] = 0x07;
		sysex[27] = 0x07;
		sysex[30] = 0x07;
		sysex[33] = 0x07;
		sysex[36] = 0x07;
		sysex[39] = 0x07;

		sysex[HSIZE + SSIZE] = (byte) 0xF7;
		PatchDataImpl p = new PatchDataImpl(sysex, this);
		// setPatchName(p,"New Effect");
		calculateChecksum(p);
		return p;
	}

	public JSLFrame editPatch(PatchDataImpl p) {
		return new KawaiK4EffectEditor(p);
	}

	public String getPatchName(PatchDataImpl p) {
		String s = "Effect Type " + (p.getSysex()[HSIZE] + 1);
		return s;
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		send(SYS_REQ.toSysexMessage(getChannel(), new SysexHandler.NameValue("bankNum", (bankNum << 1) + 1),
				new SysexHandler.NameValue("patchNum", patchNum)));
	}
}
