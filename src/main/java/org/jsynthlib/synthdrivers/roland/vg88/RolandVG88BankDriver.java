/*
 * Copyright 2006 Nacho Alonso
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
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

package org.jsynthlib.synthdrivers.roland.vg88;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.DriverUtil;
import org.jsynthlib.tools.ErrorMsgUtil;

/**
 * Bank Driver for Roland VG88
 */
public class RolandVG88BankDriver extends SynthDriverBank {

	/** Number of columns for displaying the patches in a table. */
	private static final int NUM_COLUMNS = 5;

	private static final int BANK_NAME_SIZE = 0;

	private final RolandVG88SingleDriver singleDriver;

	/** bank file name for createNewPatch() */
	private static final String bankDefFileName = "RolandVG88DefaultBank.syx";

	public RolandVG88BankDriver(RolandVG88SingleDriver singleDriver) {
		super("Bank", "Nacho Alonso", RolandVG88SingleDriver.NUM_PATCH, NUM_COLUMNS);

		this.singleDriver = singleDriver;
		singleSize = RolandVG88SingleDriver.SINGLE_SIZE;
		patchSize = RolandVG88SingleDriver.SINGLE_SIZE * RolandVG88SingleDriver.NUM_PATCH;

		patchNameStart = RolandVG88SingleDriver.SINGLE_SIZE * RolandVG88SingleDriver.NUM_PATCH;
		patchNameSize = BANK_NAME_SIZE;

		bankNumbers = RolandVG88SingleDriver.BANK_NUMBERS;
		patchNumbers = RolandVG88SingleDriver.PATCH_NUMBERS;

		sysexID = RolandVG88SingleDriver.SYSEX_ID;
		singleSysexID = RolandVG88SingleDriver.SYSEX_ID;
	}

	/**
	 * Get Bank Name (not soported, nameSize for bank is 0)
	 */
	public String getPatchName(PatchDataImpl p) {
		return bankNumbers[0];
	}

	/**
	 * Set Bank Name (not soported, nameSize for bank is 0)
	 */
	public void setPatchName(PatchDataImpl p, String name) {
		JOptionPane
				.showMessageDialog(
						(JFrame) null,
						"If you want to assign a name to this bank inside JSynth, use 'Field1' or 'Filed2' or 'Comment' fields",
						"Advice: VG88 don't use 'Bank name'", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Get a patch name
	 */
	public String getPatchName(PatchDataImpl p, int patchNum) {
		return singleDriver.getPatchName(extractPatch(p, patchNum));
	}

	/**
	 * Set a patch name
	 */
	public void setPatchName(PatchDataImpl p, int patchNum, String name) {
		PatchDataImpl pAux = extractPatch(p, patchNum);
		singleDriver.setPatchName(pAux, name);
		putPatch(p, pAux, patchNum);
	}

	/**
	 * Calculate checkSum for each patch in a bank
	 */
	public void calculateChecksum(PatchDataImpl p) {
		for (int i = 0; i < RolandVG88SingleDriver.NUM_PATCH; i++)
			singleDriver.calculateChecksum(p, singleSize * i);
	}

	/**
	 * Put a patch into a bank
	 */
	public void putPatch(PatchDataImpl bank, PatchDataImpl p, int patchNum) {
		singleDriver.arrangePatchVG88(p, patchNum);
		System.arraycopy(p.getSysex(), 0, bank.getSysex(), singleSize * patchNum, singleSize);
	}

	/**
	 * Get a patch into a bank
	 */
	public PatchDataImpl extractPatch(PatchDataImpl bank, int patchNum) {
		byte[] sysex = new byte[singleSize];
		System.arraycopy(bank.getSysex(), singleSize * patchNum, sysex, 0, singleSize);
		return new PatchDataImpl(sysex, singleDriver);
	}

	/**
	 * Create a new bank
	 */
	public PatchDataImpl createNewPatch() {
		PatchDataImpl bank = (PatchDataImpl) DriverUtil.createNewPatch(this, bankDefFileName, this.patchSize);
		// byte[] sysex = new byte[patchSize];
		// Patch bank = new Patch(sysex, this);
		// Patch p = singleDriver.createNewPatch();
		// for (int i = 0; i < singleDriver.NUM_PATCH; i++) {
		// putPatch(bank, p, i);
		// }
		bank.setComment("new bank");
		return bank;
	}

	/**
	 * Request all user Patchs BankNum nor patchNum are not used. Request all user Patchs
	 */
	public void requestPatchDump(int bankNum, int patchNum) {
		patchSize = patchSize - BANK_NAME_SIZE;
		for (int i = 0; i < RolandVG88SingleDriver.NUM_PATCH; i++) {
			singleDriver.requestPatchDump(0, i);
			try {
				Thread.sleep(600); // wait .
			} catch (Exception e) {
				ErrorMsgUtil.reportStatus(e);
			}
		}
		patchSize = patchSize + BANK_NAME_SIZE;
	}

	/**
	 * Store all user Patchs BankNum nor patchNum are not used. Request all user Patchs
	 */
	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		int ofst = 0;
		for (int i = 0; i < RolandVG88SingleDriver.NUM_PATCH; i++, ofst += singleSize) {
			singleDriver.storePatch(extractPatch(p, i), 0, i);
		}
	}
}
