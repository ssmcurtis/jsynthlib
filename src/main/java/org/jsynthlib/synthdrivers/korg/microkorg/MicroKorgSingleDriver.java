package org.jsynthlib.synthdrivers.korg.microkorg;

import java.lang.annotation.Annotation;
import java.nio.ByteBuffer;

import javax.sound.midi.MidiMessage;

import org.jsynthlib.model.driver.NameValue;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.synthdrivers.waldorf.blofeld.Blofeld;
import org.jsynthlib.tools.HexaUtil;
import org.jsynthlib.tools.MidiUtil;

/**
 * 
 * @version $Id$
 */
public class MicroKorgSingleDriver extends SynthDriverPatchImpl {

	private static final SysexHandler sysexHandler = new SysexHandler(MicroKorg.REQUEST_SINGLE);
	private static final SysexHandler writeHandler = new SysexHandler(MicroKorg.WRITE_SINGLE);

	public MicroKorgSingleDriver() {
		super("Single", "ssmCurtis");
		sysexID = MicroKorg.DEVICE_SYSEX_ID;
		patchSize = MicroKorg.PROGRAM_SIZE_MIDI_SYSEX;

		patchNameStart = MicroKorg.PATCH_NAME_START_AT.position();
		patchNameSize = MicroKorg.PATCH_NAME_LENGTH.position();
		deviceIDoffset = MicroKorg.DEVICE_ID_OFFSET;

		checksumOffset = MicroKorg.PATCH_CHECKSUM_OFFSET.position();

		bankNumbers = MicroKorg.BANK_NAMES;
		patchNumbers = MicroKorg.createPatchNumbers();

		sysexHandler.setAddToBankByte(MicroKorg.TEMPLATE_ADD_TO_BANK_BYTE);
	}

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		System.out.println(">>>> store patch ... micor produces Bluescreen on Windows");
		
		// System.out.println(HexaUtil.hexDumpOneLine(p.getByteArray()));

		sendPatch(p);
		
//		PatchDataImpl toSend = p.clone();
//		ByteBuffer midi = MicroKorg.processDumpDataEncrypt(p.getSysex(), getChannel(), 3);

		NameValue channel = new NameValue("midiChannel", MicroKorg.getMidiChannelByte(getChannel()));
		NameValue patchNumber = new NameValue("patchNum", patchNum);
		MidiMessage msg = writeHandler.toSysexMessage(getDeviceID(), channel, patchNumber);

//		ByteBuffer patchAndStore = ByteBuffer.allocate(midi.limit() + msg.getMessage().length);
//		patchAndStore.put(midi.array());
//		patchAndStore.put(msg.getMessage());
//
		System.out.println(HexaUtil.hexDumpOneLine(msg.getMessage()));

//		toSend.setSysex(patchAndStore.array());
		MidiUtil.waitForSevenBitTechnology(2000);

		send(msg);
		
	}

	public void sendPatch(PatchDataImpl p) {
		System.out.println(">>>> send patch");

		PatchDataImpl toSend = p.clone();
		ByteBuffer midi = MicroKorg.processDumpDataEncrypt(p.getSysex(), getChannel(), 3);
		toSend.setSysex(midi.array());

		super.sendPatch(toSend);
	}

	public void requestPatchDump(int bankNum, int patchNum) {

		MidiUtil.changeProgram(this, patchNum);

		MidiMessage msg = sysexHandler.toSysexMessage(getChannel());
		send(msg);
	}

	@Override
	public ByteBuffer processDumpDataConversion(byte[] sysexBuffer) {
		return MicroKorg.processDumpDataDecrypt(sysexBuffer, 2, MicroKorg.PROGRAM_SIZE_MIDI);
	}

	@Override
	public boolean supportsPatch(String patchString, byte[] sysex) {
		if ((patchSize != 0) && !MicroKorg.singlePatchSizeIsSupported(sysex.length)) {
			return false;
		}

		if (patchString.length() < sysexID.length()) {
			return false;
		}

		StringBuffer compareString = new StringBuffer();
		for (int i = 0; i < sysexID.length(); i++) {
			switch (sysexID.charAt(i)) {
			case '*':
				compareString.append(patchString.charAt(i));
				break;
			default:
				compareString.append(sysexID.charAt(i));
			}
		}
		return (compareString.toString().equalsIgnoreCase(patchString.substring(0, sysexID.length())));
	}

	@Override
	public int getHeaderSize() {
		return MicroKorg.HEADER_SIZE;
	}

	@Override
	public boolean isUseableForLibrary() {
		return true;
	}

}
