package org.jsynthlib.synthdrivers.waldorf.blofeld;

import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

/**
 * Single Voice Patch Driver for Kawai K4.
 * 
 * @version $Id$
 */
public class BlofeldMultitimbralDriver extends SynthDriverPatchImpl {

	// private static final SysexHandler sysexHandler = new SysexHandler(Blofeld.REQUEST_SINGLE);

	public BlofeldMultitimbralDriver() {
		super("Multitimbral", "ssmCurtis");
		sysexID = Blofeld.DEVICE_SYSEX_ID;
		patchSize = Blofeld.PROGRAM_SIZE_MIDI_SYSEX;

		patchNameStart = Blofeld.P_NAME_START_AT.position();
		patchNameSize = Blofeld.P_NAME_LENGTH.position();
		deviceIDoffset = Blofeld.DEVICE_ID_OFFSET;

		checksumOffset = Blofeld.P_CHECKSUM_OFFSET.position();

		bankNumbers = Blofeld.BANK_NAMES_MULTI;
		patchNumbers = Blofeld.createPatchNumbersMultitimbral();

	}

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		// System.out.println(">>>> store patch " + HexaUtil.byteToHexString((byte) patchNum));

		PatchDataImpl toSend = p.clone();

		toSend.getSysex()[Blofeld.P_ID_MESSAGE.position()] = 0x10;
		toSend.getSysex()[Blofeld.P_BANK_AT.position()] = 0x7F;
		toSend.getSysex()[Blofeld.P_PATCH_AT.position()] = (byte) patchNum;

		// System.out.println(HexaUtil.byteToHexString(toSend.getSysex()[Blofeld.PATCH_AT.position()]));

		// System.out.println("Store ...  " + HexaUtil.hexDump(toSend.getSysex(), 0, toSend.getSysex().length, toSend.getSysex().length));

		super.sendPatch(toSend);

	}

	public void sendPatch(PatchDataImpl p) {
		ErrorMsgUtil.reportError("Function not available", "Mutlitimbral Blofeld Patch request");
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		ErrorMsgUtil.reportError("Function not available", "Mutlitimbral Blofeld Patch request");
	}

	@Override
	public int getHeaderSize() {
		return Blofeld.HEADER_SIZE;
	}

}
