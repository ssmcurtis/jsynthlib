package org.jsynthlib.synthdrivers.dsi.evolver;

import javax.sound.midi.MidiMessage;

import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.driver.SysexHandler.NameValue;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.synthdrivers.korg.microkorg.MicroKorg;
import org.jsynthlib.tools.HexaUtil;

/**
 * Single Voice Patch Driver for Kawai K4.
 * 
 * @version $Id$
 */
public class EvolverSingleDriver extends SynthDriverPatchImpl {
	/** Header Size */
	private static final int HSIZE = Evolver.HEADER_SIZE;
	/** Single Patch size */
	private static final int SSIZE = Evolver.PATCH_DUMP_SIZE - Evolver.HEADER_SIZE;

	private static final SysexHandler SYS_REQ = new SysexHandler(Evolver.REQUEST_SINGLE_PATCH_TEMPLATE);

	private int bankNum = 0;
	private int patchNum = 0;

	public EvolverSingleDriver() {
		super("Single", "ssmCurtis");
		sysexID = Evolver.DEVICE_SYSEX_ID;
		patchSize = Evolver.PATCH_DUMP_SIZE;
		patchNameStart = Evolver.PATCH_NAME_START_AT.number();
		patchNameSize = Evolver.PATCH_NAME_END_AT.number();
		deviceIDoffset = Evolver.DEVICE_ID_OFFSET.number();

//		checksumOffset = Evolver.CHECKSUM_START_AT.number();
		bankNumbers = Evolver.BANK_NAMES;
		patchNumbers = Evolver.createPatchNumbers();
	}

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
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

	public void sendPatch(PatchDataImpl p) {
		System.out.println(">>>> send patch");
		
		//System.out.println(">>>" + HexaUtil.hexDumpOneLine(p.getSysex(), 0, -1, 100));
		
		PatchDataImpl patchClone = (PatchDataImpl) p.clone();
		patchClone.getSysex()[Evolver.BANK_AT.number()] = 0; 
		patchClone.getSysex()[Evolver.PATCH_AT.number()] = 0; 
		
		// System.out.prtln(">>>" + HexaUtil.hexDumpOneLine(p2.getSysex(), 0, -1, 100));
		super.sendPatch(patchClone);
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		System.out.println(">>>> request patch");

		this.bankNum = bankNum;
		this.patchNum = patchNum;

		NameValue bank = new SysexHandler.NameValue("bankNum", bankNum);
		NameValue patch = new SysexHandler.NameValue("patchNum", patchNum);
		MidiMessage msg = SYS_REQ.toSysexMessage(getChannel(), bank, patch);
		System.out.println(">>>" + HexaUtil.hexDumpOneLine(msg.getMessage(), 0, -1, 100));
		send(msg);
	}
	
	public int getHeaderSize() {
		return Evolver.HEADER_SIZE;
	}

}
