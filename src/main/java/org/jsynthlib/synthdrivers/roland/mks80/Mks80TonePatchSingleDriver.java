// written by ssmCurtis
package org.jsynthlib.synthdrivers.roland.mks80;

import java.io.UnsupportedEncodingException;

import org.jsynthlib.menu.JSLFrame;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

public class Mks80TonePatchSingleDriver extends SynthDriverPatchImpl {

	public Mks80TonePatchSingleDriver() {
		super("Tone/Patch Single*", "ssmCurtis");

		sysexID = Mks80.DEVICE_SYSEX_ID;

		patchSize = Mks80.DATA_PACKAGE_IN_BANK;
		bankNumbers = Mks80.BANK_NAMES_PATCHES;
		patchNumbers = Mks80.createProgrammNumbers();
	}

	@Override
	public void sendPatch(PatchDataImpl p) {
		System.out.println("NOT IMPLEMENTED 1");
		// ErrorMsgUtil.reportMissingFunctionality(Mks80.VENDOR, Mks80.DEVICE);
	}

	@Override
	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		System.out.println("NOT IMPLEMENTED 2");
		ErrorMsgUtil.reportMissingFunctionality(Mks80.VENDOR, Mks80.DEVICE);
	}

	@Override
	public void requestPatchDump(int bankNum, int patchNum) {
		System.out.println("Bank: " + bankNum);
		System.out.println("Patch: " + patchNum);
		System.out.println("NOT IMPLEMENTED 3");
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

	public boolean supportsPatch(String patchString, byte[] sysex) {
		if (sysex.length == Mks80.DATA_PACKAGE_IN_BANK) {
			return true;
		}
		return false;
	}
	
	public JSLFrame editPatch(PatchDataImpl p) {
		return new Mks80TonePatchSingleEditor((PatchDataImpl) p);
	}

}
