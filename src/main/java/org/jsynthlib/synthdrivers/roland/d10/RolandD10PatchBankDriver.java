/*
 * Copyright 2006 Roger Westerlund
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
package org.jsynthlib.synthdrivers.roland.d10;

import static org.jsynthlib.synthdrivers.roland.d10.D10Constants.BASE_PATCH_MEMORY;
import static org.jsynthlib.synthdrivers.roland.d10.D10Constants.OFS_ADDRESS;
import static org.jsynthlib.synthdrivers.roland.d10.D10Constants.OFS_DEVICE_ID;
import static org.jsynthlib.synthdrivers.roland.d10.D10Constants.PATCH_COUNT;
import static org.jsynthlib.synthdrivers.roland.d10.D10Constants.PATCH_SIZE;
import static org.jsynthlib.synthdrivers.roland.d10.D10Constants.SIZE_HEADER_DT1;
import static org.jsynthlib.synthdrivers.roland.d10.D10Constants.SIZE_TRAILER;

import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.synthdrivers.roland.d10.message.D10DataSetMessage;
import org.jsynthlib.synthdrivers.roland.d10.message.D10RequestMessage;
import org.jsynthlib.synthdrivers.roland.d10.message.D10TransferMessage;

public class RolandD10PatchBankDriver extends SynthDriverBank {

	private static final int COLUMN_COUNT = 4;

	private RolandD10PatchDriver patchDriver;

	public RolandD10PatchBankDriver(RolandD10PatchDriver patchDriver) {
		super("Patch Bank", "Roger Westerlund", PATCH_COUNT, COLUMN_COUNT);
		this.patchDriver = patchDriver;
		sysexID = "F041**1612";

		singleSysexID = "F041**1612";
		patchSize = 19 * (SIZE_HEADER_DT1 + SIZE_TRAILER) + PATCH_COUNT * PATCH_SIZE.getIntValue();
		deviceIDoffset = OFS_DEVICE_ID;
		checksumOffset = patchSize - SIZE_TRAILER;
		checksumStart = OFS_ADDRESS;
		checksumEnd = checksumOffset - 1;
		bankNumbers = new String[] {};
		patchNumbers = RolandD10Support.createPatchNumbers();
	}

	public PatchDataImpl createNewPatch() {
		D10TransferMessage message = new D10DataSetMessage(patchSize - (SIZE_HEADER_DT1 + SIZE_TRAILER),
				BASE_PATCH_MEMORY.getDataValue());
		PatchDataImpl bank = new PatchDataImpl(message.getBytes(), this);
		for (int patchNumber = 0; patchNumber < PATCH_COUNT; patchNumber++) {
			putPatch(bank, patchDriver.createNewPatch(), patchNumber);
		}
		return bank;
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		D10RequestMessage requestMessage = new D10RequestMessage(BASE_PATCH_MEMORY, Entity.createFromIntValue(
				PATCH_COUNT).multiply(PATCH_SIZE));
		send(requestMessage.getBytes());
	}

	public PatchDataImpl extractPatch(PatchDataImpl bank, int patchNum) {
		PatchDataImpl patch = patchDriver.createNewPatch();
		RolandD10Support.copyPatchFromBank(patchNum, bank.getSysex(), patch.getSysex());
		return patch;
	}

	public void putPatch(PatchDataImpl bank, PatchDataImpl patch, int patchNum) {
		RolandD10Support.copyPatchToBank(patchNum, bank.getSysex(), patch.getSysex());
	}

	public String getPatchName(PatchDataImpl bank, int patchNum) {
		PatchDataImpl patch = extractPatch(bank, patchNum);
		return patch.getName();
	}

	public void setPatchName(PatchDataImpl bank, int patchNum, String name) {
		PatchDataImpl patch = extractPatch(bank, patchNum);
		patch.setName(name);
		putPatch(bank, patch, patchNum);
	}

}
