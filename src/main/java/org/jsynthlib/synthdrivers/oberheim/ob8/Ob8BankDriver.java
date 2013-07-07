// written by ssmCurtis
package org.jsynthlib.synthdrivers.oberheim.ob8;

import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.synthdrivers.studioelectronics.atcx.Atcx;
import org.jsynthlib.tools.ErrorMsgUtil;
import org.jsynthlib.tools.HexaUtil;

public class Ob8BankDriver extends SynthDriverBank {

	public Ob8BankDriver() {
		super("Patch Bank", "ssmCurtis", Ob8.PROGRAM_COUNT_IN_SYNTH, 3);
		sysexID = Ob8.DEVICE_SYSEX_ID;
		singleSysexID = Ob8.DEVICE_SYSEX_ID;

		singleSize = Ob8.PROGRAM_SIZE_SYSEX;
		patchSize = Ob8.BANK_SIZE_SYSEX;

		bankNumbers = Ob8.BANK_NAMES_PATCHES;
		patchNumbers = Ob8.createProgrammNumbers();

	}

	@Override
	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
	}

	@Override
	public void putPatch(PatchDataImpl bank, PatchDataImpl patch, int patchNum) {
		if (!canHoldPatch(patch)) {
			ErrorMsgUtil.reportError("Error", "This type of patch does not fit in to this type of bank.");
			return;
		}
		int sourceStart = Ob8.HEADER_SIZE;
		int targetStart = Ob8.HEADER_SIZE + (patchNum * Ob8.PROGRAM_SIZE);
		// System.out.println("From: " + targetStart + " to " + (targetStart + Atcx.PROGRAM_SIZE));

		System.arraycopy(((PatchDataImpl) patch).getSysex(), sourceStart, ((PatchDataImpl) bank).getSysex(), targetStart, Ob8.PROGRAM_SIZE);

	}

	@Override
	public PatchDataImpl extractPatch(PatchDataImpl bank, int patchNum) {
		byte sysex[] = new byte[Ob8.PROGRAM_SIZE_SYSEX];

		// System.arraycopy(Ob8.PATCH_DUMP_HEADER, 0, sysex, 0, Ob8.HEADER_SIZE);
		
		// keep old program position
		System.arraycopy(((PatchDataImpl) bank).getSysex(), (patchNum * Ob8.PROGRAM_SIZE_SYSEX), sysex, 0, Ob8.PROGRAM_SIZE_SYSEX);
		// sysex[singleSize - 1] = (byte) 0xF7;

		return new PatchDataImpl(sysex);

	}

	@Override
	public void requestPatchDump(int bankNum, int patchNum) {
		System.out.println("REQUEST ... ");
	}

	@Override
	public void addToCurrentBank(PatchDataImpl singlePatch, int patchNum) {
		if (getCurrentBank() == null) {
			setCurrentBank(createNewPatch());
		}
		putPatch(getCurrentBank(), singlePatch, patchNum);
	}

	private void debugBank(byte[] source, byte[] target, int patchNum) {
		System.out.println("Patch:" + patchNum);
		int sourceStart = Ob8.HEADER_SIZE;
		int targetStart = Ob8.HEADER_SIZE + (patchNum * Ob8.PROGRAM_SIZE);

		// System.out.println("From: " + targetStart + " to " + (targetStart + Atcx.PROGRAM_SIZE));

		for (int i = 0; i < Ob8.PROGRAM_SIZE; i++) {
			byte b = source[sourceStart + i];
			int index = targetStart + i;
			if (b != target[index]) {
				System.out.println(patchNum + "/" + i + ": " + HexaUtil.byteToHexString(b) + " " + HexaUtil.byteToHexString(target[index]));
			}
		}
	}

	@Override
	public String getPatchName(PatchDataImpl bank, int patchNum) {
		return "-";
	}

	@Override
	public void setPatchName(PatchDataImpl bank, int patchNum, String name) {

	}

	public int getHeaderSize() {
		return Ob8.HEADER_SIZE;
	}

}
