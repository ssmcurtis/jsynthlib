/*
 * Copyright 2004 Sander Brandenburg
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

package org.jsynthlib.synthdrivers.roland.jv80;

import javax.swing.JOptionPane;

import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.DriverUtil;

/**
 * @author Sander Brandenburg
 * @version $Id$
 */
public class RolandJV80BankDriver extends SynthDriverBank {

	final RolandJV80PatchDriver patchDriver;

	final static int PATCHES_PER_BANK = 64; // number of patches per bank

	public RolandJV80BankDriver(RolandJV80PatchDriver patchDriver) {
		super("Bank", "Sander Brandenburg", PATCHES_PER_BANK, 4);

		bankNumbers = JV80Constants.BANKS;
		deviceIDoffset = JV80Constants.DEVICEIDOFFSET;
		patchNameStart = JV80Constants.PATCH_NAME_START;
		patchNameSize = JV80Constants.PATCH_NAME_SIZE;
		patchNumbers = DriverUtil.generateNumbers(1, PATCHES_PER_BANK, "00");
		singleSysexID = JV80Constants.SYSEXID;
		sysexID = JV80Constants.SYSEXID;

		this.patchDriver = patchDriver;
	}

	void setup() {
		singleSize = patchDriver.getPatchSize();
		patchSize = PATCHES_PER_BANK * singleSize;
	}

	RolandJV80PatchDriver getPatchDriver() {
		return patchDriver;
	}

	public void calculateChecksum(PatchDataImpl p) {
		for (int i = 0; i < PATCHES_PER_BANK; i++) {
			patchDriver.calculateChecksum(p, getStartOffset(i));
		}
	}

	public PatchDataImpl createNewPatch() {
		byte[] bankSysex = new byte[patchSize];

		// get a single patch
		PatchDataImpl patch = patchDriver.createNewPatch();

		for (int i = 0; i < PATCHES_PER_BANK; i++) {
			patchDriver.setPatchNum(patch.getSysex(), 0, i);
			System.arraycopy(patch.getSysex(), 0, bankSysex, i * singleSize, patch.getSysex().length);
		}

		PatchDataImpl bankPatch = new PatchDataImpl(bankSysex, this);
		calculateChecksum(bankPatch);
		return bankPatch;
	}

	public PatchDataImpl getPatch(PatchDataImpl bank, int patchNum) {
		byte[] sysex = new byte[singleSize];
		System.arraycopy(bank.getSysex(), getStartOffset(patchNum), sysex, 0, sysex.length);
		return new PatchDataImpl(sysex, patchDriver);
	}

	public String getPatchName(PatchDataImpl p, int patchNum) {
		int offset = getStartOffset(patchNum);
		return new String(p.getSysex(), offset + patchNameStart, patchNameSize);
	}

	public int getStartOffset(int patchnr) {
		return patchnr * singleSize;
	}

	public void putPatch(PatchDataImpl bank, PatchDataImpl p, int patchNum) {
		if (!canHoldPatch(p)) {
			JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		int offset = getStartOffset(patchNum);
		System.arraycopy(p.getSysex(), 0, bank.getSysex(), offset, p.getSysex().length);
		patchDriver.calculateChecksum(bank, offset);
	}

	// this request a dump of the entire bank actually
	public void requestPatchDump(final int bankNum, int patchNum) {
		new Thread() {
			public void run() {
				setPriority(MIN_PRIORITY);

				for (int i = 0; i < PATCHES_PER_BANK; i++) {
					patchDriver.requestPatchDump(bankNum, i);
					try {
						Thread.sleep(80);
					} catch (Exception ignore) {
					}
				}
			}
		}.start();
	}

	public void setPatchNum(byte[] sysex, int bankNum) {
		for (int i = 0; i < PATCHES_PER_BANK; i++) {
			patchDriver.setPatchNum(sysex, getStartOffset(i), bankNum, i);
		}
	}

	public void setPatchName(PatchDataImpl p, int patchNum, String name) {
		int offset = getStartOffset(patchNum);
		byte data[] = name.getBytes();
		if (data.length > patchNameSize)
			return;

		System.arraycopy(data, 0, p.getSysex(), offset + patchNameStart, data.length);
	}

	// stores entire bank
	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		setPatchNum(p.getSysex(), bankNum);
		calculateChecksum(p);
		sendPatchWorker(p);
	}

	public void deletePatch(PatchDataImpl bank, int patchNum) {
		PatchDataImpl patch = patchDriver.createNewPatch();
		int offset = getStartOffset(patchNum);
		System.arraycopy(patch.getSysex(), 0, bank.getSysex(), offset, patch.getSysex().length);
	}

}
