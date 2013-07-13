package org.jsynthlib.synthdrivers.kawai.k4;

import javax.swing.JOptionPane;

import org.jsynthlib.model.driver.NameValue;
import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.DriverUtil;
import org.jsynthlib.tools.ErrorMsgUtil;

/**
 * Bank driver for KAWAI K4/K4r effect patch.
 * 
 * @author Gerrit Gehnen
 * @version $Id$
 */
public class KawaiK4EffectBankDriver extends SynthDriverBank {
	/** Header Size */
	private static final int HSIZE = 8;
	/** Single Patch size */
	private static final int SSIZE = 35;
	/** the number of single patches in a bank patch. */
	private static final int NS = 32;

	private static final SysexHandler SYS_REQ = new SysexHandler("F0 40 @@ 01 00 04 *bankNum* 00 F7");

	public KawaiK4EffectBankDriver() {
		super("EffectBank", "Gerrit Gehnen", NS, 2);

		sysexID = "F040**2100040100";
		deviceIDoffset = 2;
		bankNumbers = new String[] { "0-Internal", "1-External" };
		patchNumbers = DriverUtil.generateNumbers(1, 32, "00");

		singleSysexID = "F040**2*0004";
		singleSize = HSIZE + SSIZE + 1;
		// To distinguish from the Effect bank, which has the same sysexID
		patchSize = HSIZE + SSIZE * NS + 1;
	}

	public int getPatchStart(int patchNum) {
		return HSIZE + (SSIZE * patchNum);
	}

	public String getPatchName(PatchDataImpl p, int patchNum) {
		int nameStart = getPatchStart(patchNum);
		nameStart += 0; // offset of name in patch data
		// ErrorMsgUtil.reportStatus("Patch Num "+patchNum+ "Name Start:"+nameStart);
		String s = "Effect Type " + (p.getSysex()[nameStart] + 1);
		return s;
	}

	public void setPatchName(PatchDataImpl bank, int patchNum, String name) {
		// do nothing
	}

	protected void calculateChecksum(PatchDataImpl p, int start, int end, int ofs) {
		int sum = 0;
		for (int i = start; i <= end; i++)
			sum += p.getSysex()[i];
		sum += 0xA5;
		p.getSysex()[ofs] = (byte) (sum % 128);
	}

	public void calculateChecksum(PatchDataImpl p) {
		for (int i = 0; i < NS; i++)
			calculateChecksum(p, HSIZE + (i * SSIZE), HSIZE + (i * SSIZE) + SSIZE - 2, HSIZE + (i * SSIZE) + SSIZE - 1);
	}

	public void putPatch(PatchDataImpl bank, PatchDataImpl p, int patchNum) {
		if (!canHoldPatch(p)) {
			JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		System.arraycopy(p.getSysex(), HSIZE, bank.getSysex(), getPatchStart(patchNum), SSIZE);
		calculateChecksum(bank);
	}

	/**
	 * Extract a K4-effect patch from a K4-effect bank
	 * 
	 * @param bank
	 *            The patch containing an entire bank
	 * @param patchNum
	 *            The index of the patch to extract
	 * @return A single effect patch
	 */
	public PatchDataImpl extractPatch(PatchDataImpl bank, int patchNum) {
		byte[] sysex = new byte[HSIZE + SSIZE + 1];
		sysex[0] = (byte) 0xF0;
		sysex[1] = (byte) 0x40;
		sysex[2] = (byte) 0x00;
		sysex[3] = (byte) 0x20;
		sysex[4] = (byte) 0x00;
		sysex[5] = (byte) 0x04;
		sysex[6] = (byte) 0x01;
		sysex[7] = (byte) (patchNum);
		sysex[HSIZE + SSIZE] = (byte) 0xF7;
		System.arraycopy(bank.getSysex(), getPatchStart(patchNum), sysex, HSIZE, SSIZE);
		try {
			PatchDataImpl p = new PatchDataImpl(sysex, getDevice());
			p.calculateChecksum();
			return p;
		} catch (Exception e) {
			ErrorMsgUtil.reportError("Error", "Error in K4 EffectBank Driver", e);
			return null;
		}
	}

	/**
	 * Creates a new Effect Bank patch, with a predefined setting of the pan to the center of all patches
	 * 
	 * @return The new created patch
	 */
	public PatchDataImpl createNewPatch() {
		byte[] sysex = new byte[HSIZE + SSIZE * NS + 1];
		sysex[0] = (byte) 0xF0;
		sysex[1] = (byte) 0x40;
		sysex[2] = (byte) 0x00;
		sysex[3] = (byte) 0x21;
		sysex[4] = (byte) 0x00;
		sysex[5] = (byte) 0x04;
		sysex[6] = (byte) 0x01;
		sysex[7] = 0x00;

		for (int i = 0; i < NS; i++) {
			sysex[i * SSIZE + 18] = 0x07;
			sysex[i * SSIZE + 21] = 0x07;
			sysex[i * SSIZE + 24] = 0x07;
			sysex[i * SSIZE + 27] = 0x07;
			sysex[i * SSIZE + 30] = 0x07;
			sysex[i * SSIZE + 33] = 0x07;
			sysex[i * SSIZE + 36] = 0x07;
			sysex[i * SSIZE + 39] = 0x07;
		}

		sysex[HSIZE + SSIZE * NS] = (byte) 0xF7;
		PatchDataImpl p = new PatchDataImpl(sysex, this);
		calculateChecksum(p);
		return p;
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		send(SYS_REQ.toSysexMessage(getChannel(), new NameValue("bankNum", (bankNum << 1) + 1)));
	}

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}
		p.getSysex()[3] = (byte) 0x21;
		p.getSysex()[6] = (byte) ((bankNum << 1) + 1);
		p.getSysex()[7] = (byte) 0x0;
		sendPatchWorker(p);
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}
	}
}
