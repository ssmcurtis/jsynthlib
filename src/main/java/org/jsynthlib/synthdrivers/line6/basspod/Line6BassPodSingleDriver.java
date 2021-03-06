/*
 * Copyright 2004 Jeff Weber
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

package org.jsynthlib.synthdrivers.line6.basspod;

import java.io.UnsupportedEncodingException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jsynthlib.menu.JSLFrame;
import org.jsynthlib.model.driver.NameValue;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;
import org.jsynthlib.tools.HexaUtil;

/**
 * Line6 Single Driver. Used for Line6 program patch.
 * 
 * @author Jeff Weber
 */
public class Line6BassPodSingleDriver extends SynthDriverPatchImpl {

	/**
	 * Single Program Dump Request
	 */
	private static final SysexHandler SYS_REQ = new SysexHandler(Constants.SIGL_DUMP_REQ_ID); // Program Patch Dump
																								// Request

	/** Offset of the patch name in the sysex record, not including the sysex header. */
	private static int nameStart = Constants.PATCH_NAME_START;

	/**
	 * Constructs a Line6BassPodSingleDriver.
	 */
	public Line6BassPodSingleDriver() {
		super(Constants.SIGL_PATCH_TYP_STR, Constants.AUTHOR);
		sysexID = Constants.SIGL_SYSEX_MATCH_ID;

		patchSize = Constants.PDMP_HDR_SIZE + Constants.SIGL_SIZE + 1;
		patchNameStart = Constants.PDMP_HDR_SIZE + Constants.PATCH_NAME_START; // DOES include sysex header
		patchNameSize = Constants.PATCH_NAME_SIZE;
		deviceIDoffset = Constants.DEVICE_ID_OFFSET;
		bankNumbers = Constants.PRGM_BANK_LIST;
		patchNumbers = Constants.PRGM_PATCH_LIST;
	}

	/**
	 * Constructs a Line6BassPodSingleDriver. Called by Line6BassPodEdBufDriver
	 */
	public Line6BassPodSingleDriver(String patchType, String authors) {
		super(patchType, authors);
	}

	/**
	 * Null method. Line6 devices do not use checksum.
	 */
	public void calculateChecksum(PatchDataImpl p)  {
		// Pod doesn't use checksum
	}

	/**
	 * Null method. Line6 devices do not use checksum.
	 */
	protected void calculateChecksum(PatchDataImpl p, int start, int end, int ofs) {
		// Pod doesn't use checksum
	}

	/**
	 * Gets the name of the program patch. Patch p is the target program patch.
	 */
	public String getPatchName(PatchDataImpl p) {
		char c[] = new char[patchNameSize];
		for (int i = 0; i < patchNameSize; i++) {
			c[i] = (char) PatchBytes.getSysexByte(p.getSysex(), Constants.PDMP_HDR_SIZE, Constants.PDMP_HDR_SIZE + i
					+ nameStart);
		}
		return new String(c);
	}

	/**
	 * Sets the name of the program patch. Patch p is the target program patch. String name contains the name to be
	 * assigned to the patch.
	 */
	public void setPatchName(PatchDataImpl p, String name) {
		if (name.length() < patchNameSize)
			name = name + "                ";
		byte nameBytes[] = new byte[patchNameSize];
		try {
			nameBytes = name.getBytes("US-ASCII");
			for (int i = 0; i < patchNameSize; i++) {
				PatchBytes.setSysexByte(p, Constants.PDMP_HDR_SIZE, Constants.PDMP_HDR_SIZE + i + nameStart,
						nameBytes[i]);
			}
		} catch (UnsupportedEncodingException ex) {
			return;
		}
	}

	/**
	 * Converts a single program patch to an edit buffer patch and sends it to the edit buffer. Patch p is the patch to
	 * be sent.
	 */
	public void sendPatch(PatchDataImpl p) {
		byte[] saveSysex = p.getSysex(); // Save the patch to a temp save area

		// Convert to a edit buffer patch
		int newSysexLength = p.getSysex().length - 1;
		byte newSysex[] = new byte[newSysexLength];
		System.arraycopy(Constants.EDIT_DUMP_HDR_BYTES, 0, newSysex, 0, Constants.EDMP_HDR_SIZE);
		System.arraycopy(p.getSysex(), Constants.PDMP_HDR_SIZE, newSysex, Constants.EDMP_HDR_SIZE, newSysexLength
				- Constants.EDMP_HDR_SIZE);
		p.setSysex(newSysex);
		sendPatchWorker(p);

		p.setSysex(saveSysex); // Restore the patch from the temp save area
	}

	/**
	 * Sends a a single program patch to a set patch location in the device. bankNum is a user bank number in the range
	 * 0 to 9. patchNum is a patch number within the bank, in the range 0 to 3.
	 */
	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		int progNum = bankNum * 4 + patchNum;
		p.getSysex()[0] = (byte) 0xF0;
		p.getSysex()[7] = (byte) progNum;
		sendPatchWorker(p);
		try {
			Thread.sleep(Constants.PATCH_SEND_INTERVAL); // Delay so POD can keep up (pauses between each patch when
															// sending a whole bank of patches)
		} catch (Exception e) {
		}
	}

	/**
	 * Presents a dialog instructing the user to play his instrument. Line6 Pod devices do not "Play" patches, so a
	 * dialog is presented instead.
	 */
	public void playPatch(PatchDataImpl p) {
		ErrorMsgUtil.reportStatus(getPatchName(p) + "  Header -- " + "  "
				+ HexaUtil.hexDump(p.getSysex(), 0, Constants.PDMP_HDR_SIZE, 16) + "  Data -- " + "  "
				+ HexaUtil.hexDump(p.getSysex(), Constants.PDMP_HDR_SIZE, -1, 16));

		JFrame frame = new JFrame();
		JOptionPane.showMessageDialog(frame, Constants.PLAY_CMD_MSG);
	}

	/**
	 * Creates a new program patch with default values.
	 */
	protected PatchDataImpl createNewPatch() {
		PatchDataImpl p = new PatchDataImpl(Constants.NEW_SYSEX, this);
		setPatchName(p, "NewPatch        ");
		return p;
	}

	/**
	 * Requests a dump of a single program patch. Even though, from an operational standpoint, the POD has nine banks
	 * (numbered 1 through 9) of four patches each (numbered A, B, C, and D), internally there is only a single bank of
	 * 36 patch locations, referenced by program change numbers 0-35. By assigning the numbers 0 through 8 for the banks
	 * and 0 through 3 for the patches, the conversion is as follows: program number = (bank number * 4) + patch number
	 */
	public void requestPatchDump(int bankNum, int patchNum) {
		int progNum = bankNum * 4 + patchNum;
		send(SYS_REQ.toSysexMessage(getChannel(), new NameValue("progNum", progNum)));
	}

	/*
	 * public void setBankNum (int bankNum) { // Not used for POD }
	 * 
	 * public void setPatchNum (int patchNum) { // Not used for POD }
	 */

	/**
	 * Opens an edit window on the specified patch.
	 */
	public JSLFrame editPatch(PatchDataImpl p) {
		return new Line6BassPodSingleEditor((PatchDataImpl) p);
	}
}
