// written by Kenneth L. Martinez
// $Id$
package org.jsynthlib.synthdrivers.clavia.nordlead;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

public class NLPatchBankDriver extends SynthDriverBank {
	static final int BANK_NUM_OFFSET = 4;
	static final int PATCH_NUM_OFFSET = 5;
	static final int NUM_IN_BANK = 99;

	public NLPatchBankDriver() {
		super("Patch Bank", "Kenneth L. Martinez", NLPatchSingleDriver.PATCH_LIST.length, 3);
		sysexID = "F033**04**";
		sysexRequestDump = new SysexHandler("F0 33 @@ 04 *bankNum* *patchNum* F7");
		singleSysexID = "F033**04**";
		singleSize = 139;
		patchSize = singleSize * NUM_IN_BANK;
		patchNameStart = -1;
		patchNameSize = 0;
		deviceIDoffset = 2;
		bankNumbers = NLPatchSingleDriver.BANK_LIST;
		patchNumbers = NLPatchSingleDriver.PATCH_LIST;
	}

	public void calculateChecksum(PatchDataImpl p) {
		// doesn't use checksum
	}

	// protected static void calculateChecksum(Patch p, int start, int end, int ofs) {
	// // doesn't use checksum
	// }

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		setBankNum(bankNum); // must set bank - sysex patch dump always stored in current bank
		setPatchNum(patchNum); // must send program change to make bank change take effect
		sendPatchWorker((PatchDataImpl) p, bankNum);
	}

	public void putPatch(PatchDataImpl bank, PatchDataImpl p, int patchNum) {
		if (!canHoldPatch(p)) {
			ErrorMsgUtil.reportError("Error", "This type of patch does not fit in to this type of bank.");
			return;
		}

		System.arraycopy(((PatchDataImpl) p).getSysex(), 0, ((PatchDataImpl) bank).getSysex(), patchNum * singleSize, singleSize);
		((PatchDataImpl) bank).getSysex()[patchNum * singleSize + PATCH_NUM_OFFSET] = (byte) patchNum; // set program #
	}

	public PatchDataImpl getPatch(PatchDataImpl bank, int patchNum) {
		byte sysex[] = new byte[singleSize];
		System.arraycopy(((PatchDataImpl) bank).getSysex(), patchNum * singleSize, sysex, 0, singleSize);
		return new PatchDataImpl(sysex);
	}

	public String getPatchName(PatchDataImpl p, int patchNum) {
		return "-";
	}

	public void setPatchName(PatchDataImpl p, int patchNum, String name) {
	}

	// protected void sendPatch (Patch p) {
	// sendPatchWorker((Patch)p, 0);
	// }

	protected void sendPatchWorker(PatchDataImpl p, int bankNum) {
		byte tmp[] = new byte[singleSize]; // send in 99 single-program messages
		int max;
		if (bankNum == 0) {
			max = 40; // only the first 40 programs are writeable in the user bank
		} else {
			max = NUM_IN_BANK;
		}
		try {
			PatchBayApplication.showWaitDialog();
			for (int i = 0; i < max; i++) {
				System.arraycopy(p.getSysex(), i * singleSize, tmp, 0, singleSize);
				tmp[deviceIDoffset] = (byte) (((NordLeadDevice) getDevice()).getGlobalChannel() - 1);
				tmp[BANK_NUM_OFFSET] = (byte) (bankNum + 1);
				tmp[PATCH_NUM_OFFSET] = (byte) i; // program #
				send(tmp);
				Thread.sleep(50);
			}
			PatchBayApplication.hideWaitDialog();
		} catch (Exception e) {
			ErrorMsgUtil.reportStatus(e);
			ErrorMsgUtil.reportError("Error", "Unable to send Patch");
		}
	}

	public PatchDataImpl createNewPatch() {
		byte tmp[] = new byte[singleSize];
		byte sysex[] = new byte[patchSize];
		System.arraycopy(NLPatchSingleDriver.NEW_PATCH, 0, tmp, 0, singleSize);
		for (int i = 0; i < NUM_IN_BANK; i++) {
			tmp[PATCH_NUM_OFFSET] = (byte) i; // program #
			System.arraycopy(tmp, 0, sysex, i * singleSize, singleSize);
		}
		return new PatchDataImpl(sysex, this);
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		int devID = ((NordLeadDevice) getDevice()).getGlobalChannel();
		for (int i = 0; i < NUM_IN_BANK; i++) {
			send(sysexRequestDump.toSysexMessage(devID, new SysexHandler.NameValue("bankNum", bankNum + 11),
					new SysexHandler.NameValue("patchNum", i)));
			try {
				Thread.sleep(50);
			} catch (Exception e) {
				ErrorMsgUtil.reportStatus(e);
				ErrorMsgUtil.reportError("Error", "Unable to request Patch " + i);
			}
		}
	}
}
