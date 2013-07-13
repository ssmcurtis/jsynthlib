package org.jsynthlib.synthdrivers.dsi.evolver;

import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

/**
 * Bank driver for KAWAI K4/K4r voice patch.
 * 
 * @version $Id$
 */
public class EvolverBankDriver extends SynthDriverBank {
	/** Header Size */
	private static final int HSIZE = Evolver.HEADER_SIZE;
	/** Single Patch size */
	private static final int SSIZE = Evolver.PATCH_DUMP_SIZE;

	private static final SysexHandler SYS_REQ = new SysexHandler("F0 01 20 01 05 *bankNum* *patchNum* F7");

	public EvolverBankDriver() {
		super("Bank*", "ssmCurtis", Evolver.PROGRAM_COUNT_IN_BANK, Evolver.BANK_COUNT);
		sysexID = Evolver.DEVICE_SYSEX_ID;
		singleSysexID = Evolver.DEVICE_SYSEX_ID;

		// deviceIDoffset = 2;
		bankNumbers = Evolver.BANK_NAMES;
		patchNumbers = Evolver.createPatchNumbers();
		
		checksumOffset = Evolver.CHECKSUM_OFFSET.number();

		singleSize = Evolver.PATCH_DUMP_SIZE;
		patchSize = Evolver.BANK_DUMP_SIZE;
	}

	public int getPatchStart(int patchNum) {
		ErrorMsgUtil.reportStatus(">>>> Get patch start" + HSIZE + (SSIZE * patchNum));

		return HSIZE + (SSIZE * patchNum);
	}

	public String getPatchName(PatchDataImpl p, int patchNum) {
		ErrorMsgUtil.reportStatus(">>>> Get patch name in bankdriver");

		// int nameStart = getPatchStart(patchNum);
		// nameStart += 0; // offset of name in patch data
		// try {
		// StringBuffer s = new StringBuffer(new String(p.sysex, nameStart, 10,
		// "US-ASCII"));
		// return s.toString();
		// } catch (UnsupportedEncodingException ex) {
		// return "-";
		// }
		return "NO NAME";
	}

	public void setPatchName(PatchDataImpl p, int patchNum, String name) {
		ErrorMsgUtil.reportStatus(">>>> Set name in bankdriver");
		// patchNameSize = 10;
		// patchNameStart = getPatchStart(patchNum);
		//
		// if (name.length() < patchNameSize)
		// name = name + "            ";
		// byte[] namebytes = new byte[64];
		// try {
		// namebytes = name.getBytes("US-ASCII");
		// for (int i = 0; i < patchNameSize; i++)
		// p.sysex[patchNameStart + i] = namebytes[i];
		//
		// } catch (UnsupportedEncodingException ex) {
		// return;
		// }
	}

	public void putPatch(PatchDataImpl bank, PatchDataImpl p, int patchNum) {
		ErrorMsgUtil.reportStatus(">>>> put patch in bankdriver");
//		if (!canHoldPatch(p)) {
//			JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.", "Error",
//					JOptionPane.ERROR_MESSAGE);
//			return;
//		}
//
//		System.arraycopy(p.getSysex(), HSIZE, bank.getSysex(), getPatchStart(patchNum), SSIZE);
//		calculateChecksum(bank);
	}

	public PatchDataImpl extractPatch(PatchDataImpl bank, int patchNum) {
		byte sysex[] = new byte[singleSize];
		System.arraycopy(((PatchDataImpl) bank).getSysex(), patchNum * singleSize, sysex, 0, singleSize);
		return new PatchDataImpl(sysex, getDevice());
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		ErrorMsgUtil.reportError("No useable for sysex-request", "Evolver does not support banks-requests.");
	}

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		ErrorMsgUtil.reportStatus(">>>> store patch in bankdriver");

//		try {
//			Thread.sleep(100);
//		} catch (Exception e) {
//		}
//		p.getSysex()[5] = (byte) (bankNum << 1);
//		p.getSysex()[6] = (byte) (patchNum);
//		sendPatchWorker(p);
//		try {
//			Thread.sleep(100);
//		} catch (Exception e) {
//		}
	}
}
