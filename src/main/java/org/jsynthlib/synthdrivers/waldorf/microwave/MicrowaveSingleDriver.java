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

import javax.sound.midi.MidiMessage;

import org.jsynthlib.model.driver.NameValue;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.MidiUtil;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class MicrowaveSingleDriver extends SynthDriverPatchImpl {

	private static final SysexHandler SYS_REQ = new SysexHandler(Microwave.REQUEST_SINGLE);

	public MicrowaveSingleDriver() {
		super("Single", "ssmCurtis");

		this.sysexID = Microwave.SYSEX_ID;

		this.patchNameStart = Microwave.PATCH_NAME_START;
		this.patchNameSize = Microwave.PATCH_NAME_SIZE;
		this.deviceIDoffset = Microwave.DEVICE_ID_OFFSET;

		// The SysEx documentation said 5 but that's wrong!
		this.checksumStart = Microwave.HEADER_SIZE;
		this.checksumOffset = this.checksumStart + Microwave.PROGRAM_SIZE;
		this.checksumEnd = this.checksumOffset - 1;

		this.bankNumbers = Microwave.BANK_NAMES;

		this.patchNumbers = Microwave.createProgrammNumbers();

		this.patchSize = Microwave.PROGRAM_SIZE_SYSEX;
	}

	// TODO could be static calculateChecksum
	protected static void calculateChecksum(byte[] d, int start, int end, int ofs) {
		int sum = 0;
		for (int i = start; i <= end; i++)
			sum += d[i];
		d[ofs] = (byte) (sum & 0x7F);
	}

	protected void calculateChecksum(PatchDataImpl p, int start, int end, int ofs) {
		calculateChecksum(p.getSysex(), start, end, ofs);
	}

	public void calculateChecksum(PatchDataImpl p) {
		calculateChecksum(p.getSysex(), this.checksumStart, this.checksumEnd, this.checksumOffset);
	}

	protected void setBankNum(int bankNum) {
		
		// nothing
		
	}

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		
		System.out.println(">>>> ChangeProgram: "+ patchNum);
		sendProgramChange(patchNum);

		Microwave.wrapSingle(p.getSysex(), this.getDevice().getDeviceID());

		calculateChecksum(p);

		// TODO ssmCurtis - DeviceID-BYTE MUST is getDeviceID() AND NOT: getDeviceID() - 1 as used in psendPatchWorker
		send(p.getSysex());
		MidiUtil.waitForSevenBitTechnology();
	}

	public void sendPatch(PatchDataImpl p) {
		storePatch(p, 0, 0);
	}

	public PatchDataImpl createNewPatch() {

		byte[] sysex = Microwave.getDefaultSinglePatch();
		Microwave.wrapSingle(sysex, getDeviceID());

		PatchDataImpl p = new PatchDataImpl(sysex, this);
		setPatchName(p, "New program");
		calculateChecksum(p);

		return p;
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		NameValue deviceId = new NameValue("deviceid", getDeviceID());
		MidiMessage msg = SYS_REQ.toSysexMessage(getDeviceID(), deviceId);
		send(msg);
	}

	public int getHeaderSize() {
		return Microwave.HEADER_SIZE;
	}

}
