// written by Kenneth L. Martinez
// $Id$
package org.jsynthlib.synthdrivers.clavia.nordlead2;

import javax.swing.JOptionPane;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.model.driver.NameValue;
import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

public class NL2xPerfBankDriver extends SynthDriverBank {

	public NL2xPerfBankDriver() {
		super("Perf Bank", "ssmCurtis", NordLead2x.PERFORMANCES_COUNT_IN_BANK, 5);
		
		sysexRequestDump = new SysexHandler("F0 33 @@ 04 *bankNum* *patchNum* F7");
		
		sysexID = NordLead2x.DEVICE_SYSEX_ID;
		singleSysexID  = NordLead2x.DEVICE_SYSEX_ID;
		
		singleSize = NordLead2x.PERFORMANCE_SIZE_SYSEX;
		patchSize = NordLead2x.PERFORMANCES_COUNT_IN_BANK;
		
		bankNumbers = NordLead2x.BANK_NAMES_PERFORMANCE;
		patchNumbers = NordLead2x.createPreformanceNumbers();
		
		checksumOffset = -1;
	}

	@Override
	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		if (bankNum == 0) {
			JOptionPane.showMessageDialog(PatchBayApplication.getInstance(), "Cannot send to ROM bank", "Store Patch",
					JOptionPane.WARNING_MESSAGE);
		} else {
			setBankNum(bankNum); // must set bank - sysex patch dump always stored in current bank
			sendProgramChange(patchNum); // must send program change to make bank change take effect
			sendPatchWorker((PatchDataImpl) p, bankNum);
		}
	}

	@Override
	public void putPatch(PatchDataImpl bank, PatchDataImpl p, int patchNum) {
		if (!canHoldPatch(p)) {
			ErrorMsgUtil.reportError("Error", "This type of patch does not fit in to this type of bank.");
			return;
		}

		System.arraycopy(((PatchDataImpl) p).getSysex(), 0, ((PatchDataImpl) bank).getSysex(), patchNum * singleSize, singleSize);
		((PatchDataImpl) bank).getSysex()[patchNum * singleSize + NordLead2x.PATCH_NUM_OFFSET] = (byte) patchNum; // set program #
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

	// protected void sendPatch (Patch p) {
	// sendPatchWorker((Patch)p, 0);
	// }

	private void sendPatchWorker(PatchDataImpl p, int bankNum) {
		byte tmp[] = new byte[singleSize]; // send in 100 single-performance messages
		try {
			PatchBayApplication.showWaitDialog();
			for (int i = 0; i < NordLead2x.PERFORMANCES_COUNT_IN_BANK; i++) {
				System.arraycopy(p.getSysex(), i * singleSize, tmp, 0, singleSize);
				tmp[NordLead2x.GLOBAL_MIDICHANNEL_OFFSET] = (byte) (((NordLeadDevice) (getDevice())).getGlobalChannel() - 1);
				tmp[NordLead2x.BANK_NUM_OFFSET] = (byte) 31;
				tmp[NordLead2x.PATCH_NUM_OFFSET] = (byte) i; // performance #
				send(tmp);
				Thread.sleep(50);
			}
			PatchBayApplication.hideWaitDialog();
		} catch (Exception e) {
			ErrorMsgUtil.reportStatus(e);
			ErrorMsgUtil.reportError("Error", "Unable to send Patch");
		}
	}

	@Override
	public void requestPatchDump(int bankNum, int patchNum) {
		int devID = ((NordLeadDevice) (getDevice())).getGlobalChannel();
		for (int i = 0; i < NordLead2x.PERFORMANCES_COUNT_IN_BANK; i++) {
			send(sysexRequestDump.toSysexMessage(devID, new NameValue("bankNum", 41), new NameValue("patchNum", i)));
			try {
				Thread.sleep(250); // it takes some time for each performance to be sent
			} catch (Exception e) {
				ErrorMsgUtil.reportStatus(e);
				ErrorMsgUtil.reportError("Error", "Unable to request Patch " + i);
			}
		}
	}

}
