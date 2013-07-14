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

public class AndromedaMixBankDriver extends SynthDriverBank {

	public AndromedaMixBankDriver() {
		super("Mix Bank", "Kenneth L. Martinez", Andromeda.PROGRAM_COUNT_IN_BANK, 4);
		sysexID = "F000000E1D04**00";
		sysexRequestDump = new SysexHandler(Andromeda.REQUEST_MIX_BANK);
		patchSize = 151040;
		patchNameStart = 2; // does NOT include sysex header
		patchNameSize = 16;
		deviceIDoffset = -1;
		bankNumbers = Andromeda.BANK_NAMES;
		patchNumbers = Andromeda.createPatchNumbers();
		singleSize = 1180;
		singleSysexID = "F000000E1D04";
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

		System.arraycopy(((PatchDataImpl) p).getSysex(), 0, ((PatchDataImpl) bank).getSysex(), patchNum * 1180, 1180);
		((PatchDataImpl) bank).getSysex()[patchNum * 1180 + 6] = 0; // user bank
		((PatchDataImpl) bank).getSysex()[patchNum * 1180 + 7] = (byte) patchNum; // set mix #
	}

	public PatchDataImpl extractPatch(PatchDataImpl bank, int patchNum) {
		byte sysex[] = new byte[1180];
		System.arraycopy(((PatchDataImpl) bank).getSysex(), patchNum * 1180, sysex, 0, 1180);
		return new PatchDataImpl(sysex, getDevice());
	}

	public String getPatchName(PatchDataImpl p, int patchNum) {
		return Andromeda.getPatchname(p.getSysex());
	}

	public void setPatchName(PatchDataImpl p, int patchNum, String name) {
		Andromeda.setPatchname(p.getSysex(), name);
	}

	// public String getPatchName(PatchDataImpl p, int patchNum) {
	// PatchDataImpl Mix = (PatchDataImpl) extractPatch(p, patchNum);
	// try {
	// char c[] = new char[patchNameSize];
	// for (int i = 0; i < patchNameSize; i++)
	// c[i] = (char) (AlesisA6PgmSingleDriver.getA6PgmByte(Mix.getSysex(), i + patchNameStart));
	// return new String(c);
	// } catch (Exception ex) {
	// return "-";
	// }
	// }
	//
	// public void setPatchName(PatchDataImpl p, int patchNum, String name) {
	// PatchDataImpl Mix = (PatchDataImpl) extractPatch(p, patchNum);
	// if (name.length() < patchNameSize + 4)
	// name = name + "                ";
	// byte nameByte[] = name.getBytes();
	// for (int i = 0; i < patchNameSize; i++) {
	// AlesisA6PgmSingleDriver.setA6PgmByte(nameByte[i], Mix.getSysex(), i + patchNameStart);
	// }
	// putPatch(p, Mix, patchNum);
	// }

	// protected void sendPatch (Patch p)
	// {
	// sendPatchWorker((Patch)p, 0);
	// }

	protected void sendPatchWorker(PatchDataImpl p, int bankNum) {
		byte tmp[] = new byte[1180]; // send in 128 single-mix messages
		try {
			PatchBayApplication.showWaitDialog();
			for (int i = 0; i < 128; i++) {
				System.arraycopy(p.getSysex(), i * 1180, tmp, 0, 1180);
				tmp[6] = (byte) bankNum;
				tmp[7] = (byte) i; // mix #
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
