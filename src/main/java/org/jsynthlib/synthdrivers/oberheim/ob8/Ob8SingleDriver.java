// written by ssmCurtis
package org.jsynthlib.synthdrivers.oberheim.ob8;

import javax.sound.midi.MidiMessage;

import org.jsynthlib.model.driver.NameValue;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.HexaUtil;

public class Ob8SingleDriver extends SynthDriverPatchImpl {

	private static final SysexHandler SYS_REQ = new SysexHandler(Ob8.REQUEST_SINGLE_PATCH);

	public Ob8SingleDriver() {
		super("Single", "ssmCurtis");
		sysexID = Ob8.DEVICE_SYSEX_ID;

		patchSize = Ob8.PROGRAM_SIZE_SYSEX;

		bankNumbers = Ob8.BANK_NAMES_PATCHES;
		patchNumbers = Ob8.createProgrammNumbers();
	}

	@Override
	public void sendPatch(PatchDataImpl p) {
		PatchDataImpl patchClone = (PatchDataImpl) p.clone();

		patchClone.getSysex()[Ob8.PATCH_NUM_AT] = HexaUtil.intToByte(0);

		super.sendPatch(patchClone);

		// TODO ssmCurtis Check ..
		// sendProgramChange(0);
	}

	@Override
	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		PatchDataImpl patchClone = (PatchDataImpl) p.clone();

		patchClone.getSysex()[Ob8.PATCH_NUM_AT] = HexaUtil.intToByte(patchNum);

		super.sendPatch(patchClone);
	}

	@Override
	public void requestPatchDump(int bankNum, int patchNum) {

		NameValue patch = new NameValue("patchNum", patchNum);
		MidiMessage msg = SYS_REQ.toSysexMessage(getChannel(), patch);
		// ErrorMsgUtil.reportStatus(">>>" + HexaUtil.hexDumpOneLine(msg.getMessage(), 0, -1, 100));
		send(msg);
		// MidiUtil.waitForSevenBitTechnology();

	}

	public int getHeaderSize() {
		return Ob8.HEADER_SIZE;
	}

	@Override
	public String getPatchName(PatchDataImpl p) {
		return "-";
	}

	@Override
	public void setPatchName(PatchDataImpl p, String name) {
	}

}
