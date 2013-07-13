// written by ssmCurtis
package org.jsynthlib.synthdrivers.roland.mks80;

import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

public class Mks80ToneSingleDriver extends SynthDriverPatchImpl {

	public Mks80ToneSingleDriver() {
		super("Tone Single*", "ssmCurtis");

		sysexID = Mks80.DEVICE_SYSEX_ID;

		patchSize = Mks80.TONE_SIZE_IN_BANK_SYSEX_CONTAINER;
		bankNumbers = Mks80.BANK_NAMES_PATCHES;
		patchNumbers = Mks80.createProgrammNumbers();
	}

	@Override
	public void sendPatch(PatchDataImpl p) {
		ErrorMsgUtil.reportStatus("NOT IMPLEMENTED");
		ErrorMsgUtil.reportMissingFunctionality(Mks80.VENDOR, Mks80.DEVICE);
	}

	@Override
	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		ErrorMsgUtil.reportStatus("NOT IMPLEMENTED");
		ErrorMsgUtil.reportMissingFunctionality(Mks80.VENDOR, Mks80.DEVICE);
	}

	@Override
	public void requestPatchDump(int bankNum, int patchNum) {
		ErrorMsgUtil.reportStatus("Bank: " + bankNum);
		ErrorMsgUtil.reportStatus("Patch: " + patchNum);
		ErrorMsgUtil.reportStatus("NOT IMPLEMENTED");
		ErrorMsgUtil.reportMissingFunctionality(Mks80.VENDOR, Mks80.DEVICE);
	}

	public int getHeaderSize() {
		return Mks80.HEADER_SIZE;
	}

	@Override
	public String getPatchName(PatchDataImpl p) {
		return "-";
	}

	public void setPatchName(PatchDataImpl p, String name) {

	}

}
