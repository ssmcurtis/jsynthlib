package org.jsynthlib.synthdrivers.waldorf.blofeld;

import java.awt.Robot;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import org.jsynthlib.advanced.midi.MidiNote;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.Patch;
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

		patchNameStart = Blofeld.PATCH_NAME_START_AT.position();
		patchNameSize = Blofeld.PATCH_NAME_LENGTH.position();
		deviceIDoffset = Blofeld.DEVICE_ID_OFFSET;

		checksumOffset = Blofeld.PATCH_CHECKSUM_OFFSET.position();

		bankNumbers = Blofeld.BANK_NAMES;
		patchNumbers = Blofeld.createPatchNumbers();

	}

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		System.out.println(">>>> store patch");

		// setPatchNum(patchNum);
		// try {
		// Thread.sleep(100);
		// } catch (Exception e) {
		// // nothing
		// }
		// p.getSysex()[MicroKorg.PATCH_AT.position()] = (byte) (patchNum);
		//
		// sendPatchWorker(p);
		// try {
		// Thread.sleep(100);
		// } catch (Exception e) {
		// }
		// setPatchNum(patchNum);
	}

	public void sendPatch(PatchDataImpl p) {
		System.out.println(">>>> send patch");

		PatchDataImpl toSend = p.clone();

		toSend.getSysex()[Blofeld.PATCH_ID_MESSAGE.position()] = 0x10;
		toSend.getSysex()[Blofeld.BANK_AT.position()] = 0x7F;
		toSend.getSysex()[Blofeld.PATCH_AT.position()] = 0x00;

		System.out.println("TO SEND " + HexaUtil.hexDump(toSend.getSysex(), 0, toSend.getSysex().length, toSend.getSysex().length));

		super.sendPatch(toSend);
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		send(sysexHandler.toSysexMessage(getChannel(), new SysexHandler.NameValue("bankNum", bankNum << 1), new SysexHandler.NameValue(
				"patchNum", patchNum)));
	}

	public int getHeaderSize() {
		return Blofeld.HEADER_SIZE;
	}

}
