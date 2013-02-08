// written by ssmCurtis
package org.jsynthlib.synthdrivers.studioelectronics.se1;

import java.io.UnsupportedEncodingException;

import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

public class Se1PatchSingleDriver extends SynthDriverPatchImpl {

	public Se1PatchSingleDriver() {
		super("Patch Single", "ssmCurtis");

		sysexID = Se1.DEVICE_SYSEX_ID;

		patchSize = Se1.PROGRAM_SIZE_SYSEX;
		bankNumbers = new String[] { "" };
		patchNumbers = new String[] { "" };

		checksumOffset = -1;
	}

	@Override
	public void sendPatch(PatchDataImpl p) {
		System.out.println("NOT IMPLEMENTED");
		ErrorMsgUtil.reportMissingFunctionality(Se1.VENDOR, Se1.DEVICE);
	}

	@Override
	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		System.out.println("NOT IMPLEMENTED");
		ErrorMsgUtil.reportMissingFunctionality(Se1.VENDOR, Se1.DEVICE);
	}

	@Override
	public void requestPatchDump(int bankNum, int patchNum) {
		System.out.println("Bank: " + bankNum);
		System.out.println("Patch: " + patchNum);
		System.out.println("NOT IMPLEMENTED");
		ErrorMsgUtil.reportMissingFunctionality(Se1.VENDOR, Se1.DEVICE);
	}

	public int getHeaderSize() {
		return Se1.HEADER_SIZE;
	}

	@Override
	public String getPatchName(PatchDataImpl p) {
		try {
			StringBuffer s = new StringBuffer(new String(((PatchDataImpl) p).getSysex(), Se1.PATCH_NAME_START_AT, Se1.PATCH_NAME_LENGTH,
					"US-ASCII"));
			return s.toString();
		} catch (UnsupportedEncodingException ex) {
			return "-";
		}
	}

	public void setPatchName(PatchDataImpl p, String name) {

		while (name.length() < Se1.PATCH_NAME_LENGTH) {
			name = name + " ";
		}
		byte[] namebytes = new byte[Se1.PATCH_NAME_LENGTH];
		try {
			namebytes = name.getBytes("US-ASCII");
			for (int i = 0; i < Se1.PATCH_NAME_LENGTH; i++) {
				((PatchDataImpl) p).getSysex()[Se1.PATCH_NAME_START_AT + i] = namebytes[i];
			}
		} catch (UnsupportedEncodingException ex) {
			return;
		}
	}

}
