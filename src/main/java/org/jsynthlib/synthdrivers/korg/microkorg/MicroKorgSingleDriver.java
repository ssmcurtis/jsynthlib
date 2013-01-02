package org.jsynthlib.synthdrivers.korg.microkorg;

import javax.sound.midi.MidiMessage;

import org.jsynthlib.menu.helper.SysexHandler;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.HexaUtil;

/**
 * Single Voice Patch Driver for Kawai K4.
 * 
 * @version $Id$
 */
public class MicroKorgSingleDriver extends SynthDriverPatchImpl {

	private static final SysexHandler sysexHandler = new SysexHandler(MicroKorg.REQUEST_SINGLE);

	private int bankNum = 0;
	private int patchNum = 0;

	public MicroKorgSingleDriver() {
		super("Single", "ssmCurtis");
		sysexID = MicroKorg.DEVICE_SYSEX_ID;
		patchSize = MicroKorg.PATCH_DUMP_SIZE;
		patchNameStart = MicroKorg.PATCH_NAME_START_AT.position();
		patchNameSize = MicroKorg.PATCH_NAME_LENGTH.position();
		deviceIDoffset = MicroKorg.DEVICE_ID_OFFSET;

		checksumOffset = MicroKorg.PATCH_CHECKSUM_OFFSET.position();

		bankNumbers = MicroKorg.BANK_NAMES;
		patchNumbers = MicroKorg.createPatchNumbers();

		sysexHandler.setAddToBankByte(MicroKorg.TEMPLATE_ADD_TO_BANK_BYTE);
	}

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		System.out.println(">>>> store patch");

		setPatchNum(patchNum);
		try {
			Thread.sleep(100);
		} catch (Exception e) {
			// nothing
		}
		p.getSysex()[MicroKorg.PATCH_AT.position()] = (byte) (patchNum);

		sendPatchWorker(p);

		// try {
		// Thread.sleep(100);
		// } catch (Exception e) {
		// }
		// setPatchNum(patchNum);
	}

	public void sendPatch(PatchDataImpl p) {
		System.out.println(">>>> send patch");
		p.getSysex()[2] = (byte) (0x30 + (getChannel()-1));
		System.out.println(">>>" + HexaUtil.hexDumpOneLine(p.getSysex(), 0, -1, 100));

		super.sendPatch(p);
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		System.out.println(">>>> request (current) patch");

		this.bankNum = bankNum;
		this.patchNum = patchNum;

		// NameValue bank = new SysexHandler.NameValue("bankNum", bankNum << 1);
		// NameValue patch = new SysexHandler.NameValue("patchNum", patchNum);

		MidiMessage msg = sysexHandler.toSysexMessage(getChannel());

		System.out.println(">>>" + HexaUtil.hexDumpOneLine(msg.getMessage(), 0, -1, 100));
		send(msg);
	}

}
