package org.jsynthlib.synthdrivers.waldorf.blofeld;

import org.jsynthlib.model.driver.NameValue;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.HexaUtil;
import org.jsynthlib.tools.MidiUtil;

/**
 * Single Voice Patch Driver for Kawai K4.
 * 
 * @version $Id$
 */
public class BlofeldSingleDriver extends SynthDriverPatchImpl {

	private static final SysexHandler sysexHandler = new SysexHandler(Blofeld.REQUEST_SINGLE);

	public BlofeldSingleDriver() {
		super("Single", "ssmCurtis");
		sysexID = Blofeld.DEVICE_SYSEX_ID;
		patchSize = Blofeld.PROGRAM_SIZE_MIDI_SYSEX;

		patchNameStart = Blofeld.P_NAME_START_AT.position();
		patchNameSize = Blofeld.P_NAME_LENGTH.position();
		deviceIDoffset = Blofeld.DEVICE_ID_OFFSET;

		checksumOffset = Blofeld.P_CHECKSUM_OFFSET.position();

		bankNumbers = Blofeld.BANK_NAMES;
		patchNumbers = Blofeld.createPatchNumbers();

	}

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		processPatch(p, bankNum, patchNum);
	}

	public void sendPatch(PatchDataImpl p) {
		processPatch(p, 0, 0);
	}

	private void processPatch(PatchDataImpl p, int bankNum, int patchNum) {
		PatchDataImpl toSend = p.clone();

		toSend.getSysex()[Blofeld.P_ID_MESSAGE.position()] = 0x10;
		
		toSend.getSysex()[Blofeld.P_BANK_AT.position()] = HexaUtil.intToByte(bankNum);
		toSend.getSysex()[Blofeld.P_PATCH_AT.position()] = HexaUtil.intToByte(patchNum);

		// ErrorMsgUtil.reportStatus("TO SEND " + HexaUtil.hexDump(toSend.getSysex(), 0, toSend.getSysex().length, toSend.getSysex().length));

		send(toSend.getSysex());
		MidiUtil.waitForSevenBitTechnology(2000);

	}

	public void requestPatchDump(int bankNum, int patchNum) {
		send(sysexHandler.toSysexMessage(getChannel(), new NameValue("bankNum", bankNum << 1), new NameValue("patchNum", patchNum)));
	}

	public int getHeaderSize() {
		return Blofeld.HEADER_SIZE;
	}

}
