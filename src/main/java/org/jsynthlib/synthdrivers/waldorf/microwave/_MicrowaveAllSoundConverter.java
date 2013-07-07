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

import org.jsynthlib.model.driver.ConverterImpl;
import org.jsynthlib.model.patch.PatchDataImpl;

public class _MicrowaveAllSoundConverter extends ConverterImpl {

	public _MicrowaveAllSoundConverter() {
		super("All sound converter", "Joachim Backhaus");

		this.sysexID = Microwave.SYSEX_ID + "101000";
		// Should be 65.545 Bytes
		this.patchSize = Microwave.BANK_SIZE_SYSEX;
	}

	/**
	 * Get the index where the patch starts in the banks SysEx data.
	 */
	private int getPatchStart(int patchNo) {
		return (Microwave.PROGRAM_SIZE * patchNo) + Microwave.HEADER_SIZE;
	}

	public PatchDataImpl[] extractPatch(PatchDataImpl basePatch) {
		byte[] baseSysex = basePatch.getByteArray();
		PatchDataImpl[] newPatchArray = new PatchDataImpl[2];
		byte[] temporarySysex = new byte[Microwave.PROGRAM_SIZE_SYSEX];
		PatchDataImpl tempPatch;
		byte[] bankSysex = new byte[Microwave.PROGRAM_SIZE_SYSEX * Microwave.PROGRAM_COUNT_IN_BANK];

		// Convert the sounds of bank A
		for (int patchNo = 0; patchNo < Microwave.PROGRAM_COUNT_IN_BANK; patchNo++) {
			System.arraycopy(baseSysex, getPatchStart(patchNo), temporarySysex, Microwave.HEADER_SIZE,
					Microwave.PROGRAM_SIZE + Microwave.FOOTER_SIZE);
			tempPatch = new PatchDataImpl(temporarySysex, getDevice());

			Microwave.wrapSingle(tempPatch.getSysex(), tempPatch.getDevice().getDeviceID());
			MicrowaveSingleDriver.calculateChecksum(tempPatch.getSysex(), Microwave.HEADER_SIZE,
					Microwave.HEADER_SIZE + Microwave.PROGRAM_SIZE - 1,
					Microwave.HEADER_SIZE + Microwave.PROGRAM_SIZE);

			System.arraycopy(tempPatch.getSysex(), 0, bankSysex, patchNo * Microwave.PROGRAM_SIZE_SYSEX, Microwave.PROGRAM_SIZE_SYSEX);
		}

		tempPatch = new PatchDataImpl(bankSysex, getDevice());
		tempPatch.setComment("Bank A from an all sound dump.");
		newPatchArray[0] = tempPatch;

		// Convert the sounds of bank B

		// 32.678 Bytes (Without the header!!!)
		int halfSize = Microwave.PROGRAM_SIZE * Microwave.PROGRAM_COUNT_IN_BANK;
		int index = 0;
		bankSysex = new byte[Microwave.PROGRAM_SIZE_SYSEX * Microwave.PROGRAM_COUNT_IN_BANK];

		for (int patchNo = 0; patchNo < Microwave.PROGRAM_COUNT_IN_BANK; patchNo++) {
			index = halfSize + getPatchStart(patchNo);
			System.arraycopy(baseSysex, index, temporarySysex, Microwave.HEADER_SIZE,
					Microwave.PROGRAM_SIZE + Microwave.FOOTER_SIZE);
			
			tempPatch = new PatchDataImpl(temporarySysex, getDevice());

			Microwave.wrapSingle(tempPatch.getSysex(), tempPatch.getDevice().getDeviceID());
			MicrowaveSingleDriver.calculateChecksum(tempPatch.getSysex(), Microwave.HEADER_SIZE,
					Microwave.HEADER_SIZE + Microwave.PROGRAM_SIZE - 1,
					Microwave.HEADER_SIZE + Microwave.PROGRAM_SIZE);

			System.arraycopy(tempPatch.getSysex(), 0, bankSysex, patchNo * Microwave.PROGRAM_SIZE_SYSEX, Microwave.PROGRAM_SIZE_SYSEX);
		}

		tempPatch = new PatchDataImpl(bankSysex, getDevice());
		tempPatch.setComment("Bank B from an all sound dump.");
		newPatchArray[1] = tempPatch;

		return newPatchArray;
	}
}
