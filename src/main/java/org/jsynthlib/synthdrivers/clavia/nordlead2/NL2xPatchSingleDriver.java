// written by Kenneth L. Martinez
// $Id$
package org.jsynthlib.synthdrivers.clavia.nordlead2;

import org.jsynthlib.model.driver.NameValue;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;
import org.jsynthlib.tools.HexaUtil;
import org.jsynthlib.tools.MidiUtil;

public class NL2xPatchSingleDriver extends SynthDriverPatchImpl {

	public NL2xPatchSingleDriver() {
		super("Patch Single", "ssmCurtis");
		sysexRequestDump = new SysexHandler("F0 33 @@ 04 *bankNum* *patchNum* F7");

		sysexID = NordLead2x.DEVICE_SYSEX_ID;

		patchSize = NordLead2x.PROGRAM_SIZE_SYSEX;
		bankNumbers = NordLead2x.BANK_NAMES_PATCHES;
		patchNumbers = NordLead2x.createProgrammNumbers();

		checksumOffset = -1;
	}

	@Override
	public void sendPatch(PatchDataImpl p) {
		// must set bank - sysex patch dump always stored in current bank
		setBankNum(0);
		// must send program change to make bank change take effect
		sendProgramChange(1);
		// using edit buffer for slot A
		processPatch((PatchDataImpl) p, 1, 0);
		// send another program change to get new sound in edit buffer
		sendProgramChange(0);
	}

	private void processPatch(PatchDataImpl p, int bankNum, int patchNum) {

		byte[] sysex = p.getSysex();

		sysex[NordLead2x.BANK_NUM_OFFSET] = HexaUtil.intToByte(bankNum);
		sysex[NordLead2x.PATCH_NUM_OFFSET] = HexaUtil.intToByte(patchNum);

		sysex[NordLead2x.GLOBAL_MIDICHANNEL_OFFSET] = (byte) (((NordLeadDevice) getDevice()).getGlobalChannel() - 1);

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

		processPatch((PatchDataImpl) p, bankNum + 1, patchNum);

		sendProgramChange(patchNum);
		MidiUtil.waitForSevenBitTechnology();
	}

	@Override
	public void requestPatchDump(int bankNum, int patchNum) {
		System.out.println("Bank: " + bankNum);
		System.out.println("Patch: " + patchNum);

		send(sysexRequestDump.toSysexMessage(((NordLeadDevice) getDevice()).getGlobalChannel(), new NameValue("bankNum", bankNum + 11),
				new NameValue("patchNum", patchNum)));
	}

	public int getHeaderSize() {
		return NordLead2x.HEADER_SIZE;
	}

}
