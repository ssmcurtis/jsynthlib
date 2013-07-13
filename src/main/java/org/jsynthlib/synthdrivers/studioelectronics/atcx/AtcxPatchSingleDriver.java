// written by ssmCurtis
package org.jsynthlib.synthdrivers.studioelectronics.atcx;

import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

public class AtcxPatchSingleDriver extends SynthDriverPatchImpl {

	public AtcxPatchSingleDriver() {
		super("Patch Single", "ssmCurtis");

		sysexID = Atcx.DEVICE_SYSEX_ID;

		patchSize = Atcx.PROGRAM_SIZE_SYSEX;
		bankNumbers = new String[]{""};
		patchNumbers = new String[]{""};

		checksumOffset = -1;
	}

	@Override
	public void sendPatch(PatchDataImpl p) {
		ErrorMsgUtil.reportStatus("NOT IMPLEMENTED");
		ErrorMsgUtil.reportMissingFunctionality(Atcx.VENDOR, Atcx.DEVICE);
	}

	@Override
	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		ErrorMsgUtil.reportStatus("NOT IMPLEMENTED");
		ErrorMsgUtil.reportMissingFunctionality(Atcx.VENDOR, Atcx.DEVICE);
	}

	@Override
	public void requestPatchDump(int bankNum, int patchNum) {
		ErrorMsgUtil.reportStatus("Bank: " + bankNum);
		ErrorMsgUtil.reportStatus("Patch: " + patchNum);
		ErrorMsgUtil.reportStatus("NOT IMPLEMENTED");
		ErrorMsgUtil.reportMissingFunctionality(Atcx.VENDOR, Atcx.DEVICE);
	}

	public int getHeaderSize() {
		return Atcx.HEADER_SIZE;
	}

}