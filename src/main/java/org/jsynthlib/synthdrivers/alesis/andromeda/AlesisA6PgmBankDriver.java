// written by Kenneth L. Martinez
//
// @version $Id$

package org.jsynthlib.synthdrivers.alesis.andromeda;

import javax.swing.JOptionPane;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.model.driver.NameValue;
import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

public class AlesisA6PgmBankDriver extends SynthDriverBank {

	public AlesisA6PgmBankDriver() {
		super("Prog Bank", "Kenneth L. Martinez", Andromeda.PROGRAM_COUNT_IN_BANK, 4);
		sysexID = "F000000E1D00**00";
		sysexRequestDump = new SysexHandler(Andromeda.REQUEST_PRG_BANK);
		patchSize = 300800;
		patchNameStart = 2; // does NOT include sysex header
		patchNameSize = 16;
		deviceIDoffset = -1;
		bankNumbers = Andromeda.BANK_NAMES;
		patchNumbers = Andromeda.createPatchNumbers();
		singleSize = 2350;
		singleSysexID = "F000000E1D00";
	}

	public void calculateChecksum(PatchDataImpl p) {
		// A6 doesn't use checksum
	}

	// protected static void calculateChecksum(Patch p, int start, int end, int ofs)
	// {
	// // A6 doesn't use checksum
	// }

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		if (bankNum == 1 || bankNum == 2)
			JOptionPane.showMessageDialog(PatchBayApplication.getInstance(), "Cannot send to a preset bank", "Store Patch",
					JOptionPane.WARNING_MESSAGE);
		else
			sendPatchWorker((PatchDataImpl) p, bankNum);
	}

	public void putPatch(PatchDataImpl bank, PatchDataImpl p, int patchNum) {
		if (!canHoldPatch(p)) {
			ErrorMsgUtil.reportError("Error", "This type of patch does not fit in to this type of bank.");
			return;
		}

		System.arraycopy(((PatchDataImpl) p).getSysex(), 0, ((PatchDataImpl) bank).getSysex(), patchNum * 2350, 2350);
		((PatchDataImpl) bank).getSysex()[patchNum * 2350 + 6] = 0; // user bank
		((PatchDataImpl) bank).getSysex()[patchNum * 2350 + 7] = (byte) patchNum; // set program #
	}

	public PatchDataImpl extractPatch(PatchDataImpl bank, int patchNum) {
		byte sysex[] = new byte[2350];
		System.arraycopy(((PatchDataImpl) bank).getSysex(), patchNum * 2350, sysex, 0, 2350);
		return new PatchDataImpl(sysex, getDevice());
	}

	public String getPatchName(PatchDataImpl p, int patchNum) {
		return Andromeda.getPatchname(p.getSysex());
	}

	public void setPatchName(PatchDataImpl p, int patchNum, String name) {
		Andromeda.setPatchname(p.getSysex(), name);
	}

	// protected void sendPatch (Patch p)
	// {
	// sendPatchWorker((Patch)p, 0);
	// }

	protected void sendPatchWorker(PatchDataImpl p, int bankNum) {
		byte tmp[] = new byte[2350]; // send in 128 single-program messages
		try {
			PatchBayApplication.showWaitDialog();
			for (int i = 0; i < 128; i++) {
				System.arraycopy(p.getSysex(), i * 2350, tmp, 0, 2350);
				tmp[6] = (byte) bankNum;
				tmp[7] = (byte) i; // program #
				send(tmp);
				Thread.sleep(15);
			}
			PatchBayApplication.hideWaitDialog();
		} catch (Exception e) {
			ErrorMsgUtil.reportStatus(e);
			ErrorMsgUtil.reportError("Error", "Unable to send Patch");
		}
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		send(sysexRequestDump.toSysexMessage(((byte) getChannel()), new NameValue[] { new NameValue("bankNum", bankNum),
				new NameValue("patchNum", patchNum) }));
	}
}
