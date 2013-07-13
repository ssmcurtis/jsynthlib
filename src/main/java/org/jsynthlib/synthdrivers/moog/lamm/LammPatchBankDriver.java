// written by Kenneth L. Martinez
// $Id$
package org.jsynthlib.synthdrivers.moog.lamm;

import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

public class LammPatchBankDriver extends SynthDriverBank {

	public LammPatchBankDriver() {
		super("Patch Bank", "ssmCurtis", Lamm.PROGRAM_COUNT_IN_BANK, 3);
		sysexID = Lamm.DEVICE_SYSEX_ID;
		singleSysexID  = Lamm.DEVICE_SYSEX_ID;
		singleSize = Lamm.PROGRAM_SIZE_SYSEX;
		
		patchSize = Lamm.PROGRAM_COUNT_IN_BANK * Lamm.PROGRAM_SIZE_SYSEX;

		bankNumbers = Lamm.BANK_NAMES_PATCHES;
		patchNumbers = Lamm.createProgrammNumbers();
		
		checksumOffset = -1;

	}

	@Override
	public void calculateChecksum(PatchDataImpl p) {
		// doesn't use checksum
	}

	@Override
	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		setBankNum(bankNum); // must set bank - sysex patch dump always stored in current bank
		sendProgramChange(patchNum); // must send program change to make bank change take effect
		
		// sendPatchWorker((PatchDataImpl) p, bankNum);
	}

	@Override
	public void putPatch(PatchDataImpl bank, PatchDataImpl p, int patchNum) {
		if (!canHoldPatch(p)) {
			ErrorMsgUtil.reportError("Error", "This type of patch does not fit in to this type of bank.");
			return;
		}

		System.arraycopy(((PatchDataImpl) p).getSysex(), 0, ((PatchDataImpl) bank).getSysex(), patchNum * singleSize, singleSize);
		((PatchDataImpl) bank).getSysex()[patchNum * singleSize + Lamm.PATCH_NUM_OFFSET] = (byte) patchNum; // set program #
	}

	@Override
	public PatchDataImpl extractPatch(PatchDataImpl bank, int patchNum) {
		byte sysex[] = new byte[singleSize];
		System.arraycopy(((PatchDataImpl) bank).getSysex(), patchNum * singleSize, sysex, 0, singleSize);
		return new PatchDataImpl(sysex);
	}

	@Override
	public String getPatchName(PatchDataImpl p, int patchNum) {
		return "-";
	}

	@Override
	public void setPatchName(PatchDataImpl p, int patchNum, String name) {
	}

//	private void sendPatchWorker(PatchDataImpl p, int bankNum) {
//		byte tmp[] = new byte[singleSize]; // send in 99 single-program messages
//		int max;
//		if (bankNum == 0) {
//			max = 40; // only the first 40 programs are writeable in the user bank
//		} else {
//			max = Lamm.PROGRAM_COUNT_IN_BANK;
//		}
//		try {
//			PatchBayApplication.showWaitDialog();
//			
//			for (int i = 0; i < max; i++) {
//				System.arraycopy(p.getSysex(), i * singleSize, tmp, 0, singleSize);
//				tmp[Lamm.GLOBAL_MIDICHANNEL_OFFSET] = (byte) (((LammDevice) getDevice()).getGlobalChannel() - 1);
//				tmp[Lamm.BANK_NUM_OFFSET] = (byte) (bankNum + 1);
//				tmp[Lamm.PATCH_NUM_OFFSET] = (byte) i; // program #
//				send(tmp);
//				Thread.sleep(50);
//			}
//			PatchBayApplication.hideWaitDialog();
//		} catch (Exception e) {
//			ErrorMsgUtil.reportStatus(e);
//			ErrorMsgUtil.reportError("Error", "Unable to send Patch");
//		}
//	}

	@Override
	public void requestPatchDump(int bankNum, int patchNum) {
//		int devID = ((LammDevice) getDevice()).getGlobalChannel();
//		for (int i = 0; i < Lamm.PROGRAM_COUNT_IN_BANK; i++) {
//			send(sysexRequestDump.toSysexMessage(devID, new NameValue("bankNum", bankNum + 11), new NameValue("patchNum", i)));
//			try {
//				Thread.sleep(50);
//			} catch (Exception e) {
//				ErrorMsgUtil.reportStatus(e);
//				ErrorMsgUtil.reportError("Error", "Unable to request Patch " + i);
//			}
//		}
	}
}
