package org.jsynthlib.synthdrivers.crumar.bit99;

import javax.sound.midi.MidiMessage;

import org.jsynthlib.model.driver.NameValue;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.HexaUtil;

import org.jsynthlib.tools.ErrorMsgUtil;

/**
 * Single Voice Patch Driver for Kawai K4.
 * 
 * @version $Id$
 */
public class Bit99SingleDriver extends SynthDriverPatchImpl {
	/** Header Size */
	private static final int HSIZE = Bit99.HEADER_SIZE.number();
	/** Single Patch size */
	private static final int SSIZE = Bit99.PATCH_SIZE.number() - Bit99.HEADER_SIZE.number();

	private static final SysexHandler SYS_REQ = new SysexHandler(Bit99.REQUEST_SINGLE_PATCH_TEMPLATE);

	private int bankNum = 0;
	private int patchNum = 0;

	public Bit99SingleDriver() {
		super("Single", "ssmCurtis");
		sysexID = Bit99.DEVICE_SYSEX_ID;
		patchSize = Bit99.PATCH_SIZE.number();
		patchNameStart = Bit99.PATCH_NAME_START_AT.number();
		patchNameSize = Bit99.PATCH_NAME_END_AT.number();
		deviceIDoffset = Bit99.DEVICE_ID_OFFSET.number();
		
		checksumOffset = Bit99.CHECKSUM_OFFSET.number();
		
		bankNumbers = Bit99.BANK_NAMES;
		patchNumbers = Bit99.createPatchNumbers();
	}

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		ErrorMsgUtil.reportStatus(">>>> store patch");

		sendProgramChange(patchNum);
		try {
			Thread.sleep(100);
		} catch (Exception e) {
			// nothing
		}
		p.getSysex()[Bit99.PATCH_AT.number()] = (byte) (patchNum);

		sendPatchWorker(p);

		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}
		sendProgramChange(patchNum);
	}

	public void sendPatch(PatchDataImpl p) {
		ErrorMsgUtil.reportStatus(">>>> send patch");

		super.sendPatch(p);
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		ErrorMsgUtil.reportStatus(">>>> request patch");

		this.bankNum = bankNum;
		this.patchNum = patchNum;

		NameValue bank = new NameValue("bankNum", bankNum << 1);
		NameValue patch = new NameValue("patchNum", patchNum);

		MidiMessage msg = SYS_REQ.toSysexMessage(getChannel(), bank, patch);

		ErrorMsgUtil.reportStatus(">>>" + HexaUtil.hexDumpOneLine(msg.getMessage(), 0, -1, 100));
		send(msg);
	}

}
