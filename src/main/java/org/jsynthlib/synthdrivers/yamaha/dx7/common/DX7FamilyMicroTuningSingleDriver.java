/*
 * JSynthlib -	generic "Micro Tuning" Single Driver for Yamaha DX7 Family
 *		(used by DX7-II, DX7s, TX802)
 * =======================================================================
 * @version $Id$
 * @author  Torsten Tittmann
 *
 * Copyright (C) 2002-2004 Torsten.Tittmann@gmx.de
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.jsynthlib.synthdrivers.yamaha.dx7.common;

import org.jsynthlib.menu.JSLFrame;
import org.jsynthlib.menu.helper.SysexHandler;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.patch.PatchDataImpl;

public class DX7FamilyMicroTuningSingleDriver extends SynthDriverPatchImpl {
	byte[] initSysex;
	String[] dxPatchNumbers;
	String[] dxBankNumbers;

	protected final static SysexHandler SYSEX_REQUEST_DUMP[] = {
			new SysexHandler("F0 43 @@ 7E 4C 4D 20 20 4D 43 52 59 45 20 F7"),
			new SysexHandler("F0 43 @@ 7E 4C 4D 20 20 4D 43 52 59 4D 00 F7"),
			new SysexHandler("F0 43 @@ 7E 4C 4D 20 20 4D 43 52 59 4D 01 F7") };

	public DX7FamilyMicroTuningSingleDriver(byte[] initSysex, String[] dxPatchNumbers, String[] dxBankNumbers) {
		super("Single Micro Tuning", "Torsten Tittmann");

		this.initSysex = initSysex;
		this.dxPatchNumbers = dxPatchNumbers;
		this.dxBankNumbers = dxBankNumbers;

		sysexID = "F0430*7E020A4C4D20204d4352594***";
		patchNameStart = 0; // !!!! no patchName !!!!
		patchNameSize = 0; // !!!! no patchName !!!!
		deviceIDoffset = 2;
		checksumOffset = 272;
		checksumStart = 6;
		checksumEnd = 271;
		patchNumbers = dxPatchNumbers;
		bankNumbers = dxBankNumbers;
		patchSize = 274;
		trimSize = 274;
		numSysexMsgs = 1;
	}

	public PatchDataImpl createNewPatch() {
		return new PatchDataImpl(initSysex, this);
	}

	public JSLFrame editPatch(PatchDataImpl p) {
		return new DX7FamilyMicroTuningEditor(getManufacturerName() + " " + getModelName() + " \"" + getPatchType()
				+ "\" Editor", (PatchDataImpl) p);
	}

	public void sendPatch(PatchDataImpl p) {
		// This is an edit buffer patch!
		((PatchDataImpl) p).getSysex()[14] = (byte) (0x45);
		((PatchDataImpl) p).getSysex()[15] = (byte) (0x20);

		super.sendPatch(p);
	}

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		// Is it necessary to switch off Memory Protection for edit buffer and/or User 1,2?
		if (patchNum == 0) { // edit buffer
			((PatchDataImpl) p).getSysex()[14] = (byte) (0x45);
			((PatchDataImpl) p).getSysex()[15] = (byte) (0x20);
		} else { // User 1,2
			((PatchDataImpl) p).getSysex()[14] = (byte) (0x4D);
			((PatchDataImpl) p).getSysex()[15] = (byte) (0x00 + patchNum - 1);
		}

		super.sendPatch(p);
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		send(SYSEX_REQUEST_DUMP[patchNum].toSysexMessage(getChannel() + 0x20));
	}
}
