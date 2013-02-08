/*
 * JSynthlib -	generic "Voice" Single Driver for Yamaha DX7 Family
 * (used by DX1, DX5, DX7 MKI, TX7, TX816, DX7-II, DX7s, TX802)
 * ================================================================
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
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.synthdrivers.korg.microkorg.MicroKorg;

public class DX7FamilyVoiceSingleDriver extends SynthDriverPatchImpl {
	byte[] initSysex;
	String[] dxPatchNumbers;
	String[] dxBankNumbers;

	public DX7FamilyVoiceSingleDriver(byte[] initSysex, String[] dxPatchNumbers, String[] dxBankNumbers) {
		super("Single Voice", "Torsten Tittmann");

		this.initSysex = initSysex;
		this.dxPatchNumbers = dxPatchNumbers;
		this.dxBankNumbers = dxBankNumbers;

		sysexID = "F0430*00011B";
		patchNameStart = 151;
		patchNameSize = 10;
		deviceIDoffset = 2;
		checksumOffset = 161;
		checksumStart = 6;
		checksumEnd = 160;
		patchNumbers = dxPatchNumbers;
		bankNumbers = dxBankNumbers;
		patchSize = 163;
		trimSize = 163;
		numSysexMsgs = 1;
		sysexRequestDump = new SysexHandler("F0 43 @@ 00 F7");
	}

	public PatchDataImpl createNewPatch() {
		return new PatchDataImpl(initSysex, this);
	}

	// public JSLFrame editPatch(PatchDataImpl p) {
	// return new DX7FamilyVoiceEditor(getManufacturerName() + " " + getModelName() + " \"" + getPatchType()
	// + "\" Editor", (PatchDataImpl) p);
	// }

	@Override
	public int getHeaderSize() {
		return DX7FamilyVoiceBankDriver.dxSysexHeaderSize;
	}

}
