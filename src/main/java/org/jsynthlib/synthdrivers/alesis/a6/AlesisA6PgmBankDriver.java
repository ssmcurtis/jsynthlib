// written by Kenneth L. Martinez
//
// @version $Id$

package org.jsynthlib.synthdrivers.alesis.a6;

import javax.swing.JOptionPane;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.menu.helper.SysexHandler;
import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

public class AlesisA6PgmBankDriver extends SynthDriverBank {

	public AlesisA6PgmBankDriver() {
		super("Prog Bank", "Kenneth L. Martinez", AlesisA6PgmSingleDriver.patchList.length, 4);
		sysexID = "F000000E1D00**00";
		sysexRequestDump = new SysexHandler("F0 00 00 0E 1D 0A *bankNum* F7");
		patchSize = 300800;
		patchNameStart = 2; // does NOT include sysex header
		patchNameSize = 16;
		deviceIDoffset = -1;
		bankNumbers = AlesisA6PgmSingleDriver.bankList;
		patchNumbers = AlesisA6PgmSingleDriver.patchList;
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

	public PatchDataImpl getPatch(PatchDataImpl bank, int patchNum) {
		byte sysex[] = new byte[2350];
		System.arraycopy(((PatchDataImpl) bank).getSysex(), patchNum * 2350, sysex, 0, 2350);
		return new PatchDataImpl(sysex, getDevice());
	}

	public String getPatchName(PatchDataImpl p, int patchNum) {
		PatchDataImpl pgm = (PatchDataImpl) getPatch(p, patchNum);
		try {
			char c[] = new char[patchNameSize];
			for (int i = 0; i < patchNameSize; i++)
				c[i] = (char) (AlesisA6PgmSingleDriver.getA6PgmByte(pgm.getSysex(), i + patchNameStart));
			return new String(c);
		} catch (Exception ex) {
			return "-";
		}
	}

	public void setPatchName(PatchDataImpl p, int patchNum, String name) {
		PatchDataImpl pgm = (PatchDataImpl) getPatch(p, patchNum);
		if (name.length() < patchNameSize + 4)
			name = name + "                ";
		byte nameByte[] = name.getBytes();
		for (int i = 0; i < patchNameSize; i++) {
			AlesisA6PgmSingleDriver.setA6PgmByte(nameByte[i], pgm.getSysex(), i + patchNameStart);
		}
		putPatch(p, pgm, patchNum);
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
		send(sysexRequestDump.toSysexMessage(((byte) getChannel()), new SysexHandler.NameValue[] {
				new SysexHandler.NameValue("bankNum", bankNum), new SysexHandler.NameValue("patchNum", patchNum) }));
	}
}
