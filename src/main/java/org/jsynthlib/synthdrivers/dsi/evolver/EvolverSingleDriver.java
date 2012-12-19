package org.jsynthlib.synthdrivers.dsi.evolver;

import javax.sound.midi.MidiMessage;

import org.jsynthlib.menu.patch.Patch;
import org.jsynthlib.menu.patch.Driver;
import org.jsynthlib.menu.patch.SysexHandler;
import org.jsynthlib.menu.patch.SysexHandler.NameValue;
import org.jsynthlib.menu.ui.JSLFrame;
import org.jsynthlib.tools.Utility;

/**
 * Single Voice Patch Driver for Kawai K4.
 * 
 * @version $Id$
 */
public class EvolverSingleDriver extends Driver {
	/** Header Size */
	private static final int HSIZE = 8;
	/** Single Patch size */
	private static final int SSIZE = 131;

	private static final SysexHandler SYS_REQ = new SysexHandler("F0 01 20 01 05 *bankNum* *patchNum* F7");

	private int bankNum = 0;
	private int patchNum = 0;

	public EvolverSingleDriver() {
		super("Single", "ssmcurtis");
		sysexID = Evolver.DEVICE_SYSEX_ID;

		patchSize = 228; // HSIZE + SSIZE + 1;
		patchNameStart = 0; // = HSIZE;
		patchNameSize = 0;
		deviceIDoffset = 2;
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
		}
		p.sysex[5] = (byte) (bankNum << 1);
		p.sysex[6] = (byte) (patchNum);
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
