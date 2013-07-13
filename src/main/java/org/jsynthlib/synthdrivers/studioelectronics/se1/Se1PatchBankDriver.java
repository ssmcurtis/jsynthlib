// written by ssmCurtis
package org.jsynthlib.synthdrivers.studioelectronics.se1;

import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;
import org.jsynthlib.tools.HexaUtil;

public class Se1PatchBankDriver extends SynthDriverBank {

	public Se1PatchBankDriver() {
		super("Patch Bank", "ssmCurtis", Se1.PROGRAM_COUNT_IN_BANK, 3);
		sysexID = Se1.DEVICE_SYSEX_ID;
		singleSysexID = Se1.DEVICE_SYSEX_ID;
		singleSize = Se1.PROGRAM_SIZE_SYSEX;

		patchSize = Se1.PROGRAM_COUNT_IN_BANK * Se1.PROGRAM_SIZE + Se1.HEADER_SIZE + 1;

		bankNumbers = Se1.BANK_NAMES_PATCHES;
		patchNumbers = Se1.createProgrammNumbers();

		// no checksum
		checksumOffset = -1;

	}

	@Override
	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {

		// PatchDataImpl v = new PatchDataImpl(Se1.getDefautltBankPatch(), getDevice());
		// boolean mismatchFound = false;
		// for (int i = 0; i < v.getSysex().length; i++) {
		//
		// if (v.getSysex()[i] != p.getSysex()[i]) {
		// ErrorMsgUtil.reportStatus(i + ": Reference:" + HexaUtil.byteToHexString(v.getSysex()[i]) + " created: "
		// + HexaUtil.byteToHexString(p.getSysex()[i]));
		// mismatchFound = true;
		// }
		// }
		// if (mismatchFound) {
		// ErrorMsgUtil.reportStatus("ERRRRROR");
		// }

		sendPatch((PatchDataImpl) p);
	}

	@Override
	public void putPatch(PatchDataImpl bank, PatchDataImpl patch, int patchNum) {
		if (!canHoldPatch(patch)) {
			ErrorMsgUtil.reportError("Error", "This type of patch does not fit in to this type of bank.");
			return;
		}
		int sourceStart = Se1.HEADER_SIZE;
		int targetStart = Se1.HEADER_SIZE + (patchNum * Se1.PROGRAM_SIZE);
		// ErrorMsgUtil.reportStatus("From: " + targetStart + " to " + (targetStart + Atcx.PROGRAM_SIZE));

		System.arraycopy(((PatchDataImpl) patch).getSysex(), sourceStart, ((PatchDataImpl) bank).getSysex(), targetStart, Se1.PROGRAM_SIZE);

	}

	@Override
	public PatchDataImpl extractPatch(PatchDataImpl bank, int patchNum) {
		byte sysex[] = new byte[singleSize];

		System.arraycopy(Se1.DEFAULT_PATCH_HEADER, 0, sysex, 0, Se1.HEADER_SIZE);
		System.arraycopy(((PatchDataImpl) bank).getSysex(), Se1.HEADER_SIZE + (patchNum * Se1.PROGRAM_SIZE), sysex, Se1.HEADER_SIZE,
				Se1.PROGRAM_SIZE);
		sysex[singleSize - 1] = (byte) 0xF7;

		return new PatchDataImpl(sysex);
	}

	@Override
	public String getPatchName(PatchDataImpl p, int patchNum) {
		return "-";
	}

	@Override
	public void setPatchName(PatchDataImpl p, int patchNum, String name) {
	}

	// private void sendPatchWorker(PatchDataImpl p, int bankNum) {
	// byte tmp[] = new byte[singleSize]; // send in 99 single-program messages
	// int max;
	// if (bankNum == 0) {
	// max = 40; // only the first 40 programs are writeable in the user bank
	// } else {
	// max = Lamm.PROGRAM_COUNT_IN_BANK;
	// }
	// try {
	// PatchBayApplication.showWaitDialog();
	//
	// for (int i = 0; i < max; i++) {
	// System.arraycopy(p.getSysex(), i * singleSize, tmp, 0, singleSize);
	// tmp[Lamm.GLOBAL_MIDICHANNEL_OFFSET] = (byte) (((LammDevice) getDevice()).getGlobalChannel() - 1);
	// tmp[Lamm.BANK_NUM_OFFSET] = (byte) (bankNum + 1);
	// tmp[Lamm.PATCH_NUM_OFFSET] = (byte) i; // program #
	// send(tmp);
	// Thread.sleep(50);
	// }
	// PatchBayApplication.hideWaitDialog();
	// } catch (Exception e) {
	// ErrorMsgUtil.reportStatus(e);
	// ErrorMsgUtil.reportError("Error", "Unable to send Patch");
	// }
	// }

	@Override
	public void requestPatchDump(int bankNum, int patchNum) {
		// int devID = ((LammDevice) getDevice()).getGlobalChannel();
		// for (int i = 0; i < Lamm.PROGRAM_COUNT_IN_BANK; i++) {
		// send(sysexRequestDump.toSysexMessage(devID, new NameValue("bankNum", bankNum + 11), new NameValue("patchNum",
		// i)));
		// try {
		// Thread.sleep(50);
		// } catch (Exception e) {
		// ErrorMsgUtil.reportStatus(e);
		// ErrorMsgUtil.reportError("Error", "Unable to request Patch " + i);
		// }
		// }
	}

	@Override
	public void addToCurrentBank(PatchDataImpl singlePatch, int patchNum) {
		if (getCurrentBank() == null) {
			setCurrentBank(createNewPatch());
		}
		putPatch(getCurrentBank(), singlePatch, patchNum);
	}

	public PatchDataImpl createNewPatch() {
		byte[] sysex = new byte[Se1.BANK_SIZE_SYSEX];

		System.arraycopy(Se1.DEFAULT_BANK_HEADER, 0, sysex, 0, Se1.HEADER_SIZE);

		sysex[Se1.BANK_SIZE_SYSEX - 1] = (byte) 0xF7;

		PatchDataImpl defaultSinglePatch = new PatchDataImpl(Se1.getDefaultSinglePatch(), getDevice());
		PatchDataImpl bankPatch = new PatchDataImpl(sysex, this);

		for (int i = 0; i < getNumPatches(); i++) {
			putPatch(bankPatch, defaultSinglePatch, i);
			// debugBank(defaultSinglePatch.getSysex(), bankPatch.getSysex(), i);
		}

		return bankPatch;
	}

	private void debugBank(byte[] source, byte[] target, int patchNum) {
		ErrorMsgUtil.reportStatus("Patch:" + patchNum);
		int sourceStart = Se1.HEADER_SIZE;
		int targetStart = Se1.HEADER_SIZE + (patchNum * Se1.PROGRAM_SIZE);

		// ErrorMsgUtil.reportStatus("From: " + targetStart + " to " + (targetStart + Atcx.PROGRAM_SIZE));

		for (int i = 0; i < Se1.PROGRAM_SIZE; i++) {
			byte b = source[sourceStart + i];
			int index = targetStart + i;
			if (b != target[index]) {
				ErrorMsgUtil.reportStatus(patchNum + "/" + i + ": " + HexaUtil.byteToHexString(b) + " " + HexaUtil.byteToHexString(target[index]));
			}
		}
	}
}
