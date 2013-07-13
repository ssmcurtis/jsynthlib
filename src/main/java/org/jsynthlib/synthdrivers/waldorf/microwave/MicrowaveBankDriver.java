/*
 * Copyright 2013 ssmCurtis, 2005 Joachim Backhaus
 *
 * This file is part of JSynthLib2.
 *
 * JSynthLib2 is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * JSynthLib2 is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
package org.jsynthlib.synthdrivers.waldorf.microwave;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.SysexMessage;
import javax.swing.JOptionPane;

import org.jsynthlib.model.driver.NameValue;
import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class MicrowaveBankDriver extends SynthDriverBank {

	private static final SysexHandler SYS_REQ = new SysexHandler(Microwave.REQUEST_SINGLE_BANK_BPBR);

	public MicrowaveBankDriver() {
		super("Bank", "ssmCurtis", Microwave.PROGRAM_COUNT_IN_SYNTH, 4);

		this.sysexID = Microwave.SYSEX_ID;

		this.patchNameStart = 0;
		this.patchNameSize = 0;
		this.deviceIDoffset = Microwave.DEVICE_ID_OFFSET;

		this.singleSysexID = Microwave.SYSEX_ID;
		this.singleSize = Microwave.PROGRAM_SIZE_SYSEX;

		this.bankNumbers = Microwave.BANK_NAMES;
		this.patchNumbers = Microwave.createProgrammNumbers();

		this.patchSize = Microwave.BANK_SIZE_SYSEX;

		this.checksumStart = Microwave.HEADER_SIZE;
		this.checksumOffset = Microwave.BANK_SIZE_SYSEX - Microwave.FOOTER_SIZE;
		this.checksumEnd = this.checksumOffset - 1;
	}

	// private void calculateChecksum(PatchDataImpl p, int patchNo) {
	// int sum = 0;
	// int offset = patchNo * this.singleSize;
	//
	// for (int i = this.checksumStart; i <= this.checksumEnd; i++)
	// sum += p.getSysex()[offset + i];
	// p.getSysex()[offset + this.checksumOffset] = (byte) (sum & 0x7F);
	// }
	//
	public void calculateChecksum(PatchDataImpl p) {
		MicrowaveSingleDriver.calculateChecksum(p.getSysex(), checksumStart, checksumEnd, checksumOffset);
	}

	@Override
	public String getPatchName(PatchDataImpl p, int patchNum) {
		throw new NotImplementedException();

		// int nameStart = getPatchStart(patchNum) + Microwave.PATCH_NAME_START - Microwave.HEADER_SIZE;
		//
		// try {
		// StringBuffer s = new StringBuffer(new String(p.getSysex(), nameStart, Microwave.PATCH_NAME_SIZE,
		// "US-ASCII"));
		// return s.toString();
		// } catch (UnsupportedEncodingException ex) {
		// return "-";
		// }
	}

	@Override
	public void setPatchName(PatchDataImpl p, int patchNum, String name) {
		throw new NotImplementedException();

		// setPatchName(p.getSysex(), patchNum, name);
		// calculateChecksum(p, patchNum);
	}

	// protected void setPatchName(byte[] tempSysex, int patchNum, String name) {
	// int tempPatchNameStart = getPatchStart(patchNum) + Microwave.PATCH_NAME_START - Microwave.HEADER_SIZE;
	//
	// while (name.length() < Microwave.PATCH_NAME_SIZE)
	// name = name + " ";
	//
	// byte[] namebytes = new byte[Microwave.PATCH_NAME_SIZE];
	// try {
	// namebytes = name.getBytes("US-ASCII");
	// for (int i = 0; i < Microwave.PATCH_NAME_SIZE; i++)
	// tempSysex[tempPatchNameStart + i] = namebytes[i];
	// } catch (UnsupportedEncodingException ex) {
	// return;
	// }
	// }

	public void putPatch(PatchDataImpl bankPatch, PatchDataImpl singlePatch, int patchNum) {

		if (!canHoldPatch(singlePatch)) {
			JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.", "Error",
					JOptionPane.ERROR_MESSAGE);

			ErrorMsgUtil.reportStatus(">>> MW1 PutPatch Size: " + singlePatch.getSysex().length);
			return;
		}

		int targetStart = Microwave.HEADER_SIZE + (patchNum * Microwave.PROGRAM_SIZE);

		System.arraycopy(((PatchDataImpl) singlePatch).getSysex(), Microwave.HEADER_SIZE, ((PatchDataImpl) bankPatch).getSysex(),
				targetStart, Microwave.PROGRAM_SIZE);
	}

	public void sendPatch(PatchDataImpl p) {
		// TODO ssmCurtis - Device ID starts at 0 or 1

		p.getSysex()[Microwave.DEVICE_ID_OFFSET] = (byte) getDeviceID();
		calculateChecksum(p);

		// TODO ssmCurtis - had problems sending complete Banks using ESI midi equipment
		SysexMessage m = new SysexMessage();
		try {

			m.setMessage(p.getSysex(), p.getSysex().length);
			send(m);

		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}

	}

	public void storePatch(PatchDataImpl bank, int bankNum, int patchNum) {

		sendPatch(bank);
	}

	public PatchDataImpl extractPatch(PatchDataImpl bank, int patchNum) {
		return getPatch(bank, 0, patchNum);
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		NameValue deviceId = new NameValue("deviceid", getDeviceID());
		MidiMessage msg = SYS_REQ.toSysexMessage(getDeviceID(), deviceId);

		send(msg);
	}

	@Override
	public void addToCurrentBank(PatchDataImpl singlePatch, int patchNum) {
		if (getCurrentBank() == null) {
			setCurrentBank(createNewPatch());
		}
		putPatch(getCurrentBank(), singlePatch, patchNum);
	}

	protected PatchDataImpl createNewPatch() {

		byte[] sysex = new byte[Microwave.BANK_SIZE_SYSEX];

		Microwave.wrapBank(sysex, getDeviceID());

		byte[] singleProgram = Microwave.getDefaultSinglePatch();

		PatchDataImpl defaultSinglePatch = new PatchDataImpl(singleProgram, getDevice());
		PatchDataImpl bankPatch = new PatchDataImpl(sysex, this);

		for (int i = 0; i < getNumPatches(); i++) {
			putPatch(bankPatch, defaultSinglePatch, i);
		}
		// ErrorMsgUtil.reportStatus(HexaUtil.hexDump(bankPatch.getSysex(), 0, -1, 16));

		return bankPatch;

	}

	private PatchDataImpl getPatch(PatchDataImpl bank, int bankNum, int patchNum) {
		try {
			byte[] sysex = new byte[Microwave.PROGRAM_SIZE_SYSEX];

			Microwave.wrapSingle(sysex, this.getDevice().getDeviceID());
			System.arraycopy(bank.getSysex(), getPatchStart(patchNum), sysex, Microwave.HEADER_SIZE, Microwave.PROGRAM_SIZE);

			PatchDataImpl p = new PatchDataImpl(sysex);

			// p.getSysex()[Microwave.PROGRAM_SIZE_SYSEX - 1] = (byte) 0xF7;
			MicrowaveSingleDriver.calculateChecksum(p.getSysex(), Microwave.HEADER_SIZE,
					Microwave.HEADER_SIZE + Microwave.PROGRAM_SIZE - 1, Microwave.CHECKSUM_AT);

			return p;
		} catch (Exception e) {
			ErrorMsgUtil.reportError("Error", "Error in WaldorfMW2 bank driver", e);
			return null;
		}
	}

	private int getPatchStart(int patchNum) {
		return (Microwave.PROGRAM_SIZE * patchNum) + Microwave.HEADER_SIZE;
	}

}
