// written by Kenneth L. Martinez
// @version $Id$

package org.jsynthlib.synthdrivers.sci.prophet600;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

public class P600ProgBankDriver extends SynthDriverBank {
	static final int PATCH_NUM_OFFSET = 3;
	static final int NUM_IN_BANK = 100;

	public P600ProgBankDriver() {
		super("Prog Bank", "Kenneth L. Martinez", P600ProgSingleDriver.PATCH_LIST.length, 5);
		sysexID = "F00102**";
		sysexRequestDump = new SysexHandler("F0 01 00 *patchNum* F7");
		singleSysexID = "F0010263";
		singleSize = 37;
		patchSize = singleSize * NUM_IN_BANK;
		patchNameStart = -1;
		patchNameSize = 0;
		deviceIDoffset = -1;
		bankNumbers = P600ProgSingleDriver.BANK_LIST;
		patchNumbers = P600ProgSingleDriver.PATCH_LIST;
	}

	public void calculateChecksum(PatchDataImpl p) {
		// doesn't use checksum
	}

	// protected static void calculateChecksum(Patch p, int start, int end, int ofs) {
	// // doesn't use checksum
	// }

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
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
		byte tmp[] = new byte[singleSize]; // send in 100 single-program messages
		try {
			PatchBayApplication.showWaitDialog();
			for (int i = 0; i < NUM_IN_BANK; i++) {
				System.arraycopy(p.getSysex(), i * singleSize, tmp, 0, singleSize);
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
		System.arraycopy(P600ProgSingleDriver.NEW_PATCH, 0, tmp, 0, singleSize);
		for (int i = 0; i < NUM_IN_BANK; i++) {
			tmp[PATCH_NUM_OFFSET] = (byte) i; // program #
			System.arraycopy(tmp, 0, sysex, i * singleSize, singleSize);
		}
		return new PatchDataImpl(sysex, this);
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		for (int i = 0; i < NUM_IN_BANK; i++) {
			send(sysexRequestDump.toSysexMessage(((byte) getChannel()), new SysexHandler.NameValue[] {
					new SysexHandler.NameValue("bankNum", bankNum), new SysexHandler.NameValue("patchNum", i) }));
			try {
				Thread.sleep(50);
			} catch (Exception e) {
				ErrorMsgUtil.reportStatus(e);
				ErrorMsgUtil.reportError("Error", "Unable to request Patch " + i);
			}
		}
	}
}
