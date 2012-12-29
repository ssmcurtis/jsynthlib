package org.jsynthlib.synthdrivers.dsi.evolver;

import javax.sound.midi.MidiMessage;

import org.jsynthlib.menu.patch.Driver;
import org.jsynthlib.menu.patch.Patch;
import org.jsynthlib.menu.patch.SysexHandler;
import org.jsynthlib.menu.patch.SysexHandler.NameValue;
import org.jsynthlib.tools.Utility;

/**
 * Single Voice Patch Driver for Kawai K4.
 * 
 * @version $Id$
 */
public class EvolverSingleDriver extends Driver {
	/** Header Size */
	private static final int HSIZE = Evolver.HEADER_SIZE.number();
	/** Single Patch size */
	private static final int SSIZE =  Evolver.PATCH_SIZE.number() - Evolver.HEADER_SIZE.number();

	private static final SysexHandler SYS_REQ = new SysexHandler(Evolver.REQUEST_SINGLE_PATCH_TEMPLATE);

	private int bankNum = 0;
	private int patchNum = 0;

	public EvolverSingleDriver() {
		super("Single", "ssmcurtis");
		sysexID = Evolver.DEVICE_SYSEX_ID;
		patchSize = Evolver.PATCH_SIZE.number(); // HSIZE + SSIZE + 1;
		patchNameStart = Evolver.PATCH_NAME_START_AT.number(); // = HSIZE;
		patchNameSize = Evolver.PATCH_NAME_END_AT.number();
		deviceIDoffset = Evolver.DEVICE_ID_OFFSET.number();
		// checksumStart = HSIZE;
		// checksumEnd = HSIZE + SSIZE - 2;
		// checksumOffset = HSIZE + SSIZE - 1;
		bankNumbers = Evolver.BANK_NAMES;
		patchNumbers = EvolverDevice.createPatchNumbers();
	}

	public void storePatch(Patch p, int bankNum, int patchNum) {
		System.out.println(">>>> store patch");

		setBankNum(bankNum);
		setPatchNum(patchNum);
		try {
			Thread.sleep(100);
		} catch (Exception e) {
			// nothing
		}
		p.getSysex()[Evolver.BANK_AT.number()] = (byte) (bankNum << 1);
		p.getSysex()[Evolver.PATCH_AT.number()] = (byte) (patchNum);

		sendPatchWorker(p);

		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}
		setPatchNum(patchNum);
	}

	public void sendPatch(Patch p) {
		System.out.println(">>>> send patch");

		super.sendPatch(p);
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		System.out.println(">>>> request patch");

		this.bankNum = bankNum;
		this.patchNum = patchNum;

		NameValue bank = new SysexHandler.NameValue("bankNum", bankNum << 1);
		NameValue patch = new SysexHandler.NameValue("patchNum", patchNum);

		MidiMessage msg = SYS_REQ.toSysexMessage(getChannel(), bank, patch);

		System.out.println(">>>" + Utility.hexDumpOneLine(msg.getMessage(), 0, -1, 100));
		send(msg);
	}

}
