// written by ssmcurtis
// $Id$
package org.jsynthlib.synthdrivers.moog.lamm;

import javax.sound.midi.MidiMessage;

import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;
import org.jsynthlib.tools.HexaUtil;
import org.jsynthlib.tools.MidiUtil;

public class LammPatchSingleDriver extends SynthDriverPatchImpl {

	public LammPatchSingleDriver() {
		super("Patch Single", "ssmCurtis");

		sysexID = Lamm.DEVICE_SYSEX_ID;

		patchSize = Lamm.PROGRAM_SIZE_SYSEX;
		bankNumbers = Lamm.BANK_NAMES_PATCHES;
		patchNumbers = Lamm.createProgrammNumbers();

		checksumOffset = -1;
	}

	@Override
	public void sendPatch(PatchDataImpl p) {
//		// must set bank - sysex patch dump always stored in current bank
//		setBankNum(0);
//		// must send program change to make bank change take effect
//		sendProgramChange(1);
		// using edit buffer for slot A
		processPatch((PatchDataImpl) p, 100);
		// send another program change to get new sound in edit buffer
//		sendProgramChange(0);
//		send(p.getSysex());
	}

	private void processPatch(PatchDataImpl p,int patchNum) {

		byte[] sysex = p.getSysex();

		sysex[Lamm.PATCH_NUM_OFFSET] = HexaUtil.intToByte(patchNum);
		
		ErrorMsgUtil.reportStatus(HexaUtil.hexDumpOneLine(sysex));
		
		try {
			send(sysex);
		} catch (Exception e) {
			ErrorMsgUtil.reportStatus(e);
		}
	}

	@Override
	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		setBankNum(bankNum);
		sendProgramChange(patchNum);

		processPatch((PatchDataImpl) p, patchNum);

		sendProgramChange(patchNum);
		MidiUtil.waitForSevenBitTechnology();
	}

	@Override
	public void requestPatchDump(int bankNum, int patchNum) {
		ErrorMsgUtil.reportStatus("Bank: " + bankNum);
		ErrorMsgUtil.reportStatus("Patch: " + patchNum);

//		MidiMessage msg = sysexHandler.toSysexMessage(getChannel());
//		send(msg);
//
//		
//		send(sysexRequestDump.toSysexMessage(((LammDevice) getDevice()).getGlobalChannel(), new NameValue("bankNum", bankNum + 11),
//				new NameValue("patchNum", patchNum)));
	}

	public int getHeaderSize() {
		return Lamm.HEADER_SIZE;
	}

}
