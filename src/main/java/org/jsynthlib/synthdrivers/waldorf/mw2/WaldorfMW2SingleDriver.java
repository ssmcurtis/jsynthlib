/*
 * Copyright 2005 Joachim Backhaus
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

package org.jsynthlib.synthdrivers.waldorf.mw2;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import javax.swing.JOptionPane;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.model.driver.NameValue;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.model.patch.PatchSingle;
import org.jsynthlib.tools.DriverUtil;
import org.jsynthlib.tools.ErrorMsgUtil;

/**
 * Driver for Microwave 2 / XT / XTK single programs
 * 
 * @author Joachim Backhaus
 * @version $Id$
 */
public class WaldorfMW2SingleDriver extends SynthDriverPatchImpl {

	public WaldorfMW2SingleDriver() {
		super("Single program", "Joachim Backhaus");

		this.sysexID = MW2Constants.SYSEX_ID + "10";

		this.sysexRequestDump = new SysexHandler("F0 3E 0E @@ 00 *BB* *NN* *XSUM* F7");

		this.patchNameStart = MW2Constants.PATCH_NAME_START;
		this.patchNameSize = MW2Constants.PATCH_NAME_SIZE;
		this.deviceIDoffset = MW2Constants.DEVICE_ID_OFFSET;

		// The SysEx documentation said 5 but that's wrong!
		this.checksumStart = MW2Constants.SYSEX_HEADER_OFFSET;
		this.checksumOffset = this.checksumStart + MW2Constants.PURE_PATCH_SIZE;
		this.checksumEnd = this.checksumOffset - 1;

		this.bankNumbers = new String[] { "A", "B" };

		this.patchNumbers = DriverUtil.generateNumbers(1, 128, "#");

		// Patch size (265 Bytes)
		this.patchSize = MW2Constants.PATCH_SIZE;
	}

	protected static void calculateChecksum(byte[] d, int start, int end, int ofs) {
		int sum = 0;
		for (int i = start; i <= end; i++)
			sum += d[i];
		d[ofs] = (byte) (sum & 0x7F);
	}

	protected void calculateChecksum(PatchDataImpl p, int start, int end, int ofs) {
		calculateChecksum(p.getSysex(), start, end, ofs);
	}

	/**
	 * Calculate check sum of a <code>Patch</code>.
	 * <p>
	 * 
	 * @param p
	 *            a <code>Patch</code> value
	 */
	public void calculateChecksum(PatchDataImpl p)  {
		calculateChecksum(p.getSysex(), this.checksumStart, this.checksumEnd, this.checksumOffset);
	}

	/**
	 * Send Control Change (Bank Select) MIDI message.
	 * 
	 * @see #storePatch(PatchDataImpl, int, int)
	 */
	protected void setBankNum(int bankNum) {
		try {
			ShortMessage msg = new ShortMessage();
			msg.setMessage(ShortMessage.CONTROL_CHANGE, getChannel() - 1, 0x20, // Bank Select (LSB)
					bankNum); // Bank Number (MSB)
			send(msg);
		} catch (InvalidMidiDataException e) {
			ErrorMsgUtil.reportStatus(e);
		}
	}

	/**
	 * Sends a patch to a set location on a synth.
	 * <p>
	 * 
	 * @see PatchDataImpl#send(int, int)
	 */
	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		setBankNum(bankNum);
		sendProgramChange(patchNum);

		p.getSysex()[5] = (byte) bankNum; // Location
		p.getSysex()[6] = (byte) patchNum; // Location
		calculateChecksum(p);

		sendPatchWorker(p);
	}

	/**
	 * Sends a patch to the synth's edit buffer.
	 * <p>
	 * 
	 * @see PatchDataImpl#send()
	 * @see PatchSingle#send()
	 */
	public void sendPatch(PatchDataImpl p) {
		p.getSysex()[5] = (byte) 0x20; // Location (use Edit Buffer)
		p.getSysex()[6] = (byte) 0x00; // Location (use Edit Buffer)
		calculateChecksum(p);

		sendPatchWorker(p);
	}

	protected static void createPatchHeader(PatchDataImpl tempPatch, int bankNo, int patchNo) {
		if (tempPatch.getSysex().length > 8) {
			tempPatch.getSysex()[0] = MW2Constants.SYSEX_START_BYTE;
			tempPatch.getSysex()[1] = (byte) 0x3E; // Waldorf Electronics GmbH ID
			tempPatch.getSysex()[2] = (byte) 0x0E; // Microwave 2 ID
			tempPatch.getSysex()[3] = (byte) tempPatch.getDevice().getDeviceID(); // Device ID
			tempPatch.getSysex()[4] = (byte) 0x10; // Sound Dump
			tempPatch.getSysex()[5] = (byte) bankNo; // Location
			tempPatch.getSysex()[6] = (byte) patchNo; // Location
			tempPatch.getSysex()[7] = (byte) 0x01; // Sound format (has to be 1, as 0 doesn't work!)
		}
	}

	protected void createPatchHeader(PatchDataImpl tempPatch) {
		// Location (use Edit Buffer)
		createPatchHeader(tempPatch, 0x20, 0x00);
	}

	/**
	 * @see org.jsynthlib.model.driver.SynthDriverPatchImpl#createNewPatch()
	 */
	public PatchDataImpl createNewPatch() {
		byte[] sysex = new byte[MW2Constants.PATCH_SIZE];
		PatchDataImpl p;

		try {
			java.io.InputStream fileIn = getClass().getResourceAsStream(MW2Constants.DEFAULT_SYSEX_FILENAME);
			fileIn.read(sysex);
			fileIn.close();
			p = new PatchDataImpl(sysex, this);

		} catch (Exception e) {
			System.err.println("Unable to find " + MW2Constants.DEFAULT_SYSEX_FILENAME + " using hardcoded default.");

			p = new PatchDataImpl(sysex, this);
			createPatchHeader(p);
			// createPatchFooter(p);
			// p.sysex[263] = (byte) 0x00; // Checksum
			p.getSysex()[264] = MW2Constants.SYSEX_END_BYTE;
			setPatchName(p, "New program");
			calculateChecksum(p);
		}

		return p;
	}

	/**
	 * Request the dump of a single program
	 * 
	 * @param bankNum
	 *            The bank number (0 = A, 1 = B)
	 * @param patchNum
	 *            The number of the requested single program
	 */
	public void requestPatchDump(int bankNum, int patchNum) {

		if (sysexRequestDump == null) {
			JOptionPane.showMessageDialog(PatchBayApplication.getInstance(), "The " + toString()
					+ " driver does not support patch getting.\n\n" + "Please start the patch dump manually...",
					"Get Patch", JOptionPane.WARNING_MESSAGE);
		} else {
			NameValue[] nameValues = { new NameValue("BB", bankNum),
					new NameValue("NN", patchNum),
					new NameValue("XSUM", ((byte) (bankNum + patchNum)) & 0x7F) };

			send(sysexRequestDump.toSysexMessage(getDeviceID(), nameValues));
		}
	}
}
