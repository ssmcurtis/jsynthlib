package org.jsynthlib.synthdrivers.waldorf.blofeld;

import java.io.UnsupportedEncodingException;

import javax.sound.midi.MidiMessage;

import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

/**
 * Bank driver for KAWAI K4/K4r voice patch.
 * 
 * @version $Id$
 */
public class BlofeldCompleteDriver extends SynthDriverBank {

	private static final SysexHandler sysexHandler = new SysexHandler(Blofeld.REQUEST_BANK);

	public BlofeldCompleteDriver() {
		super("Complete", "ssmCurtis", Blofeld.PATCH_COUNT_IN_SYNTH, 1);
		sysexID = Blofeld.DEVICE_SYSEX_ID;
		patchSize = Blofeld.BANK_SIZE_MIDI_SYSEX;
		patchNameStart = Blofeld.PATCH_NAME_START_AT.position();
		patchNameSize = Blofeld.PATCH_NAME_LENGTH.position();
		deviceIDoffset = Blofeld.DEVICE_ID_OFFSET;

		checksumOffset = Blofeld.PATCH_CHECKSUM_OFFSET.position();
		bankNumbers = Blofeld.BANK_NAMES_COMPLETE;
		patchNumbers = Blofeld.createPatchNumbersCompleteSynth();

		sysexHandler.setAddToBankByte(Blofeld.TEMPLATE_ADD_TO_BANK_BYTE);
	}

	// @Override
	// public int getPatchSize(){
	// return MicroKorg.PATCH_SIZE;
	// }

	@Override
	public String getPatchName(PatchDataImpl p, int patchNum) {
		System.out.println(">>>> Get patch name");

		int nameStart = getPatchStart(patchNum);
		nameStart += Blofeld.PATCH_NAME_START_AT.position(); // offset of name in patch data
		try {
			StringBuffer s = new StringBuffer(new String(p.getSysex(), nameStart, Blofeld.PATCH_NAME_LENGTH.position(), "US-ASCII"));
			return s.toString();
		} catch (UnsupportedEncodingException ex) {
			return "-";
		}
	}

	@Override
	public void setPatchName(PatchDataImpl p, int patchNum, String name) {
		System.out.println(">>>> Set name");
		// patchNameSize = 10;
		// patchNameStart = getPatchStart(patchNum);
		//
		// if (name.length() < patchNameSize)
		// name = name + "            ";
		// byte[] namebytes = new byte[64];
		// try {
		// namebytes = name.getBytes("US-ASCII");
		// for (int i = 0; i < patchNameSize; i++)
		// p.sysex[patchNameStart + i] = namebytes[i];
		//
		// } catch (UnsupportedEncodingException ex) {
		// return;
		// }
	}

	@Override
	public void putPatch(PatchDataImpl bank, PatchDataImpl p, int patchNum) {
		System.out.println(">>>> put patch");
		// if (!canHoldPatch(p)) {
		// JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.", "Error",
		// JOptionPane.ERROR_MESSAGE);
		// return;
		// }
		//
		// System.arraycopy(p.getSysex(), MicroKorg.HEADER_SIZE, bank.getSysex(), getPatchStart(patchNum),
		// MicroKorg.PROGRAM_SIZE_MIDI);
		// calculateChecksum(bank);
	}

	@Override
	public PatchDataImpl extractPatch(PatchDataImpl bankPatch, int patchNum) {
		System.out.println(">>>> Get patch " + getClass().getSimpleName() + " Patch: " + patchNum);

		byte[] sysex = new byte[Blofeld.PROGRAM_SIZE_MIDI_SYSEX];
		if (bankPatch.getSysex().length - getPatchStart(patchNum) - Blofeld.PROGRAM_SIZE_MIDI_SYSEX >= 0) {
			System.arraycopy(bankPatch.getSysex(), getPatchStart(patchNum), sysex, 0, Blofeld.PROGRAM_SIZE_MIDI_SYSEX);
			try {
				PatchDataImpl p = new PatchDataImpl(sysex, getDevice());
				p.calculateChecksum();
				return p;
			} catch (Exception e) {
				ErrorMsgUtil.reportError("Error", "Error in " + getClass().getSimpleName(), e);
			}
		}
		return null;
	}

	public void calculateChecksum(PatchDataImpl p) {
		// set an always accepted checksum
		p.getSysex()[Blofeld.PATCH_CHECKSUM_OFFSET.position()] = 0x7F;
	}

	private int getPatchStart(int patchNum) {
		return Blofeld.PROGRAM_SIZE_MIDI_SYSEX * patchNum;
	}

	@Override
	public void requestPatchDump(int bankNum, int patchNum) {
		System.out.println(">>>> Send sysex");

		// NameValue bank = new SysexHandler.NameValue("bankNum", bankNum << 1);
		// NameValue patch = new SysexHandler.NameValue("patchNum", 1);
		MidiMessage msg = sysexHandler.toSysexMessage(getChannel());
		send(msg);
	}

	@Override
	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		System.out.println(">>>> store patch");

		// try {
		// Thread.sleep(100);
		// } catch (Exception e) {
		// }
		// p.getSysex()[5] = (byte) (bankNum << 1);
		// p.getSysex()[6] = (byte) (patchNum);
		// sendPatchWorker(p);
		// try {
		// Thread.sleep(100);
		// } catch (Exception e) {
		// }
	}

	@Override
	public int getHeaderSize() {
		return Blofeld.HEADER_SIZE;
	}
	
}
