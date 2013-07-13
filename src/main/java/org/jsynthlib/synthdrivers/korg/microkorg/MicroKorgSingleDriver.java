package org.jsynthlib.synthdrivers.korg.microkorg;

import java.nio.ByteBuffer;

import javax.sound.midi.MidiMessage;

import org.jsynthlib.model.driver.NameValue;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.HexaUtil;
import org.jsynthlib.tools.MidiUtil;

import org.jsynthlib.tools.ErrorMsgUtil;

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
		 ErrorMsgUtil.reportStatus(patchNum + " >>>> store patch ... microkrog produces windows bluescreen using som devices (f.e. M8U)");

		// ErrorMsgUtil.reportStatus(HexaUtil.hexDumpOneLine(p.getByteArray()));
		PatchDataImpl patchToSend = p.clone();
		patchToSend.getSysex()[2] = MicroKorg.getMidiChannelByte(getChannel());
		
		sendPatch(patchToSend);
		// TODO value to driver configuration
		MidiUtil.waitForSevenBitTechnology(2000);

		// ErrorMsgUtil.reportStatus(HexaUtil.hexDumpOneLine(patchToSend.getSysex()));

		// PatchDataImpl toSend = p.clone();
		// ByteBuffer midi = MicroKorg.processDumpDataEncrypt(p.getSysex(), getChannel(), 3);

		NameValue channel = new NameValue("midiChannel", MicroKorg.getMidiChannelByte(getChannel()));
		NameValue patchNumber = new NameValue("patchNum", patchNum);
		MidiMessage msg = writeHandler.toSysexMessage(getDeviceID(), channel, patchNumber);
		ErrorMsgUtil.reportStatus(HexaUtil.hexDumpOneLine(msg.getMessage()));
		
		send(msg);

		// MicroKorg.WRITE_SINGLE_BYTES[2] = MicroKorg.getMidiChannelByte(getChannel());
		// MicroKorg.WRITE_SINGLE_BYTES[6] = HexaUtil.intToByte(patchNum);
		// SysexMessage sxm = new SysexMessage();
		// try {
		// sxm.setMessage(MicroKorg.WRITE_SINGLE_BYTES, MicroKorg.WRITE_SINGLE_BYTES.length);
		// ErrorMsgUtil.reportStatus(HexaUtil.hexDumpOneLine(sxm.getData()));
		// send(sxm);
		// } catch (InvalidMidiDataException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// ByteBuffer patchAndStore = ByteBuffer.allocate(midi.limit() + msg.getMessage().length);
		// patchAndStore.put(midi.array());
		// patchAndStore.put(msg.getMessage());
		//

		// toSend.setSysex(patchAndStore.array());


	}

	public void sendPatch(PatchDataImpl p) {

		PatchDataImpl toSend = p.clone();
		ByteBuffer midi = MicroKorg.processDumpDataEncrypt(p.getSysex(), getChannel(), 3);
		toSend.setSysex(midi.array());

		super.sendPatch(toSend);
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		// TODO change to: NameValue deviceId = new NameValue("deviceId", getDeviceID());
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



}
