// written by Kenneth L. Martinez
// $Id$
package org.jsynthlib.synthdrivers.clavia.nordlead2;

import org.jsynthlib.model.driver.NameValue;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

public class NL2xPerfSingleDriver extends SynthDriverPatchImpl {

	public NL2xPerfSingleDriver() {
		super("Perf Single", "ssmCurtis");
		sysexID = NordLead2x.DEVICE_SYSEX_ID;
		sysexRequestDump = new SysexHandler("F0 33 @@ 04 *bankNum* *patchNum* F7");

		patchSize = NordLead2x.PERFORMANCE_SIZE_SYSEX;
		bankNumbers = NordLead2x.BANK_NAMES_PERFORMANCE;
		patchNumbers = NordLead2x.createPreformanceNumbers();

		checksumOffset = -1;
	}

	// public String getPatchName(PatchDataImpl ip) {
	// return "perf" + (((PatchDataImpl) ip).getSysex()[NordLead2x.PATCH_NUM_OFFSET] + 1);
	// }
	//
	// public void setPatchName(PatchDataImpl p, String name) {
	// }

	@Override
	public void sendPatch(PatchDataImpl p) {
		processPatch((PatchDataImpl) p, 30, 0); // using edit buffer
	}

	private void processPatch(PatchDataImpl p, int bankNum, int patchNum) {
		PatchDataImpl p2 = new PatchDataImpl(p.getSysex());
		p2.getSysex()[NordLead2x.BANK_NUM_OFFSET] = (byte) bankNum;
		p2.getSysex()[NordLead2x.PATCH_NUM_OFFSET] = (byte) patchNum;
		mySendPatch(p2);
	}

	@Override
	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		processPatch((PatchDataImpl) p, 31, patchNum);
		sendProgramChange(patchNum); // send program change to get new sound in edit buffer
	}

	// @Override
	// public void playPatch(PatchDataImpl p) {
	// byte sysex[] = new byte[patchSize];
	// System.arraycopy(((PatchDataImpl) p).getSysex(), 0, sysex, 0, patchSize);
	// sysex[NordLead2x.BANK_NUM_OFFSET] = 30; // edit buffer
	// sysex[NordLead2x.PATCH_NUM_OFFSET] = 0;
	// PatchDataImpl p2 = new PatchDataImpl(sysex);
	// super.playPatch(p2);
	// }

	private void mySendPatch(PatchDataImpl p) {
		p.getSysex()[NordLead2x.GLOBAL_MIDICHANNEL_OFFSET] = (byte) (((NordLeadDevice) getDevice()).getGlobalChannel() - 1);
		try {
			send(p.getSysex());
		} catch (Exception e) {
			ErrorMsgUtil.reportStatus(e);
		}
	}

	@Override
	public void requestPatchDump(int bankNum, int patchNum) {

		send(sysexRequestDump.toSysexMessage(((NordLeadDevice) getDevice()).getGlobalChannel(), new NameValue("bankNum", 41),
				new NameValue("patchNum", patchNum)));
	}
}
