/*
 * JSynthlib -	generic Additional "Voice" Bank Driver for DX7 Family
 *		(used by DX7-II, DX7s, TX802)
 * ==================================================================
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

import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

public class DX7FamilyAdditionalVoiceBankDriver extends SynthDriverBank {
	byte[] initSysex;
	String[] dxPatchNumbers;
	String[] dxBankNumbers;

	private static final int dxSinglePackedSize = 35; // size of single patch in packed bank format
	private static final int dxSysexHeaderSize = 6; // length of sysex header

	public DX7FamilyAdditionalVoiceBankDriver(byte[] initSysex, String[] dxPatchNumbers, String[] dxBankNumbers) {
		super("Additional Voice Bank", "Torsten Tittmann", dxPatchNumbers.length, 4);

		this.initSysex = initSysex;
		this.dxPatchNumbers = dxPatchNumbers;
		this.dxBankNumbers = dxBankNumbers;

		sysexID = "F0430*060860";
		sysexRequestDump = new SysexHandler("F0 43 @@ 06 F7");
		deviceIDoffset = 2;
		patchNameStart = 0;
		patchNameSize = 0;
		bankNumbers = dxBankNumbers;
		patchNumbers = dxPatchNumbers;
		singleSysexID = "F0430*050031";
		singleSize = 57;
		checksumOffset = 1126;
		checksumStart = 6;
		checksumEnd = 1125;
		numSysexMsgs = 1;
		patchSize = 1128;
		trimSize = patchSize;
	}

	public int getPatchStart(int patchNum) {
		return (dxSinglePackedSize * patchNum) + dxSysexHeaderSize;
	}

	public void putPatch(PatchDataImpl bank, PatchDataImpl p, int patchNum) // puts a patch into the bank, converting it
																			// as needed
	{
		if (!canHoldPatch(p)) {
			DX7FamilyStrings.dxShowError(toString(), "This type of patch does not fit in to this type of bank.");
			return;
		}

		// Transform Voice Data to Bulk Dump Packed Format
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 0] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 0] + // OP6
																												// Scaling
																												// Mode
																												// normal/fractional...(0-
																												// 1)
				((PatchDataImpl) p).getSysex()[6 + 1] * 2 + // OP5 Scaling Mode normal/fractional...(0- 1)
				((PatchDataImpl) p).getSysex()[6 + 2] * 4 + // OP4 Scaling Mode normal/fractional...(0- 1)
				((PatchDataImpl) p).getSysex()[6 + 3] * 8 + // OP3 Scaling Mode normal/fractional...(0- 1)
				((PatchDataImpl) p).getSysex()[6 + 4] * 16 + // OP2 Scaling Mode normal/fractional...(0- 1)
		((PatchDataImpl) p).getSysex()[6 + 5] * 32)); // OP1 Scaling Mode normal/fractional...(0- 1)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 1] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 6] + // OP6
																												// amplitude
																												// modulation
																												// sensitivity
																												// (0-
																												// 7)
		((PatchDataImpl) p).getSysex()[6 + 7] * 8)); // OP5 amplitude modulation sensitivity (0- 7)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 2] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 8] + // OP4
																												// amplitude
																												// modulation
																												// sensitivity
																												// (0-
																												// 7)
		((PatchDataImpl) p).getSysex()[6 + 9] * 8)); // OP3 amplitude modulation sensitivity (0- 7)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 3] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 10] + // OP2
																												// amplitude
																												// modulation
																												// sensitivity
																												// (0-
																												// 7)
		((PatchDataImpl) p).getSysex()[6 + 11] * 8)); // OP1 amplitude modulation sensitivity (0- 7)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 4] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 12] + // Pitch
																												// EG
																												// Range
																												// ......................(0-
																												// 3)
				((PatchDataImpl) p).getSysex()[6 + 13] * 4 + // LFO key trigger (delay) .............(0- 1)
				((PatchDataImpl) p).getSysex()[6 + 14] * 8 + // Pitch EG by velocity switch .........(0- 1)
		((PatchDataImpl) p).getSysex()[6 + 19] * 16)); // Random Pitch Depth ..................(0- 7)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 5] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 15] + // bit1:Poly/Mono,
																												// bit0:
																												// Unison
																												// off/on
																												// .(0-
																												// 3)
		((PatchDataImpl) p).getSysex()[6 + 16] * 4)); // Pitch Bend Range ....................(0-12)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 6] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 17] + // Pitch
																												// Bend
																												// Step
																												// .....................(0-12)
		((PatchDataImpl) p).getSysex()[6 + 18] * 16)); // Pitch Bend Mode .....................(0- 3)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 7] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 20] + // Portamento
																												// Mode
																												// .....................(0-
																												// 1)
		((PatchDataImpl) p).getSysex()[6 + 21] * 2)); // Portamento Step .....................(0-12)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 8] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 22])); // Portamento
																												// Time
																												// .....................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 9] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 23])); // Modulation
																												// Wheel
																												// Pitch
																												// Mod
																												// Range
																												// ....(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 10] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 24])); // Modulation
																													// Wheel
																													// Ampl.
																													// Mod
																													// Range
																													// ....(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 11] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 25])); // Modulation
																													// Wheel
																													// EG
																													// Bias
																													// Range
																													// ......(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 12] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 26])); // Foot
																													// Control
																													// 1
																													// Pitch
																													// Mod
																													// Range
																													// ....(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 13] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 27])); // Foot
																													// Control
																													// 1
																													// Ampl.
																													// Mod
																													// Range
																													// ....(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 14] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 28])); // Foot
																													// Control
																													// 1
																													// EG
																													// Bias
																													// Range
																													// ......(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 15] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 29])); // Foot
																													// Control
																													// 1
																													// Volume
																													// Range
																													// .......(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 16] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 30])); // Breath
																													// Control
																													// Pitch
																													// Mod
																													// Range
																													// ....(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 17] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 31])); // Breath
																													// Control
																													// Ampl.
																													// Mod
																													// Range
																													// ....(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 18] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 32])); // Breath
																													// Control
																													// EG
																													// Bias
																													// Range
																													// ......(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 19] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 33])); // Breath
																													// Control
																													// Pitch
																													// Bias
																													// Range
																													// ...(0-100)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 20] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 34])); // After
																													// Touch
																													// Pitch
																													// Mod
																													// Range
																													// ....(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 21] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 35])); // After
																													// Touch
																													// Ampl.
																													// Mod
																													// Range
																													// ....(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 22] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 36])); // After
																													// Touch
																													// EG
																													// Bias
																													// Range
																													// ......(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 23] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 37])); // After
																													// Touch
																													// Pitch
																													// Bias
																													// Range
																													// ...(0-100)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 24] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 38])); // Pitch
																													// EG
																													// Rate
																													// Scaling
																													// ..............(0-
																													// 7)
		// bank.sysex[getPatchStart(patchNum)+ 25]=(byte)(); // !!! RESERVED !!!
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 26] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 39])); // Foot
																													// Control
																													// 2
																													// Pitch
																													// Mod
																													// Range
																													// ....(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 27] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 40])); // Foot
																													// Control
																													// 2
																													// Ampl.
																													// Mod
																													// Range
																													// ....(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 28] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 41])); // Foot
																													// Control
																													// 2
																													// EG
																													// Bias
																													// Range
																													// ......(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 29] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 42])); // Foot
																													// Control
																													// 2
																													// Volume
																													// Range
																													// .......(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 30] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 43])); // MIDI
																													// IN
																													// Control
																													// Pitch
																													// Mod
																													// Range
																													// ....(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 31] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 44])); // MIDI
																													// IN
																													// Control
																													// Ampl.
																													// Mod
																													// Range
																													// ....(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 32] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 45])); // MIDI
																													// IN
																													// Control
																													// EG
																													// Bias
																													// Range
																													// ......(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 33] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 46])); // MIDI
																													// IN
																													// Control
																													// Volume
																													// Range
																													// .......(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 34] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 47] + // Unison
																												// detune
																												// depth
																												// .................(0-
																												// 7)
		((PatchDataImpl) p).getSysex()[6 + 48] * 8)); // Foot Control 1 use as CS1 switch ....(0- 1)

		calculateChecksum(bank);
	}

	public PatchDataImpl extractPatch(PatchDataImpl bank, int patchNum) // Gets a patch from the bank, converting it as needed
	{
		try {
			byte[] sysex = new byte[singleSize];

			// transform bulk-dump-packed-format to voice data

			sysex[0] = (byte) 0xF0;
			sysex[1] = (byte) 0x43;
			sysex[2] = (byte) 0x00;
			sysex[3] = (byte) 0x05;
			sysex[4] = (byte) 0x00;
			sysex[5] = (byte) 0x31;

			sysex[6 + 0] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 0] & 1)); // OP6 scaling mode
																									// normal/fractional
																									// ..(0- 1)
			sysex[6 + 1] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 0] & 2) / 2); // OP5 scaling
																										// mode
																										// normal/fractional
																										// ..(0- 1)
			sysex[6 + 2] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 0] & 4) / 4); // OP4 scaling
																										// mode
																										// normal/fractional
																										// ..(0- 1)
			sysex[6 + 3] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 0] & 8) / 8); // OP3 scaling
																										// mode
																										// normal/fractional
																										// ..(0- 1)
			sysex[6 + 4] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 0] & 16) / 16); // OP2 scaling
																											// mode
																											// normal/fractional
																											// ..(0- 1)
			sysex[6 + 5] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 0] & 32) / 32); // OP1 scaling
																											// mode
																											// normal/fractional
																											// ..(0- 1)
			sysex[6 + 6] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 1] & 7)); // OP6 amplitude
																									// modulation
																									// sensitivity (0-
																									// 7)
			sysex[6 + 7] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 1] & 56) / 8); // OP5
																											// amplitude
																											// modulation
																											// sensitivity
																											// (0- 7)
			sysex[6 + 8] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 2] & 7)); // OP4 amplitude
																									// modulation
																									// sensitivity (0-
																									// 7)
			sysex[6 + 9] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 2] & 56) / 8); // OP3
																											// amplitude
																											// modulation
																											// sensitivity
																											// (0- 7)
			sysex[6 + 10] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 3] & 7)); // OP2 amplitude
																										// modulation
																										// sensitivity
																										// (0- 7)
			sysex[6 + 11] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 3] & 56) / 8); // OP1
																											// amplitude
																											// modulation
																											// sensitivity
																											// (0- 7)
			sysex[6 + 12] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 4] & 3)); // Pitch EG Range
																										// ......................(0-
																										// 3)
			sysex[6 + 13] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 4] & 4) / 4); // LFO key
																											// trigger
																											// (delay)
																											// .............(0-
																											// 1)
			sysex[6 + 14] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 4] & 8) / 8); // Pitch EG by
																											// velocity
																											// switch
																											// .........(0-
																											// 1)
			sysex[6 + 15] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 5] & 3)); // bit1:Poly/Mono,
																										// bit0: Unison
																										// off/on .(0-
																										// 3)
			sysex[6 + 16] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 5] & 60) / 4); // Pitch Bend
																											// Range
																											// ....................(0-12)
			sysex[6 + 17] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 6] & 15)); // Pitch Bend Step
																										// .....................(0-12)
			sysex[6 + 18] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 6] & 48) / 16); // Pitch Bend
																											// Mode
																											// .....................(0-
																											// 3)
			sysex[6 + 19] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 4] & 112) / 16); // Random
																											// Pitch
																											// Depth
																											// ..................(0-
																											// 7)
			sysex[6 + 20] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 7] & 1)); // Portamento Mode
																										// .....................(0-
																										// 1)
			sysex[6 + 21] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 7] & 30) / 2); // Portamento
																											// Step
																											// .....................(0-12)
			sysex[6 + 22] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 8] & 127)); // Portamento
																										// Time
																										// .....................(0-99)
			sysex[6 + 23] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 9] & 127)); // Modulation
																										// Wheel Pitch
																										// Mod Range
																										// ....(0-99)
			sysex[6 + 24] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 10] & 127)); // Modulation
																										// Wheel Ampl.
																										// Mod Range
																										// ....(0-99)
			sysex[6 + 25] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 11] & 127)); // Modulation
																										// Wheel EG Bias
																										// Range
																										// ......(0-99)
			sysex[6 + 26] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 12] & 127)); // Foot Control
																										// 1 Pitch Mod
																										// Range
																										// ....(0-99)
			sysex[6 + 27] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 13] & 127)); // Foot Control
																										// 1 Ampl. Mod
																										// Range
																										// ....(0-99)
			sysex[6 + 28] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 14] & 127)); // Foot Control
																										// 1 EG Bias
																										// Range
																										// ......(0-99)
			sysex[6 + 29] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 15] & 127)); // Foot Control
																										// 1 Volume
																										// Range
																										// .......(0-99)
			sysex[6 + 30] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 16] & 127)); // Breath
																										// Control Pitch
																										// Mod Range
																										// ....(0-99)
			sysex[6 + 31] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 17] & 127)); // Breath
																										// Control Ampl.
																										// Mod Range
																										// ....(0-99)
			sysex[6 + 32] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 18] & 127)); // Breath
																										// Control EG
																										// Bias Range
																										// ......(0-99)
			sysex[6 + 33] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 19] & 127)); // Breath
																										// Control Pitch
																										// Bias Range
																										// ...(0-100)
			sysex[6 + 34] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 20] & 127)); // After Touch
																										// Pitch Mod
																										// Range
																										// ....(0-99)
			sysex[6 + 35] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 21] & 127)); // After Touch
																										// Ampl. Mod
																										// Range
																										// ....(0-99)
			sysex[6 + 36] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 22] & 127)); // After Touch
																										// EG Bias Range
																										// ......(0-99)
			sysex[6 + 37] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 23] & 127)); // After Touch
																										// Pitch Bias
																										// Range
																										// ...(0-100)
			sysex[6 + 38] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 24] & 7)); // Pitch EG Rate
																										// Scaling
																										// ..............(0-
																										// 7)
			sysex[6 + 39] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 26] & 127)); // Foot Control
																										// 2 Pitch Mod
																										// Range
																										// ....(0-99)
			sysex[6 + 40] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 27] & 127)); // Foot Control
																										// 2 Ampl. Mod
																										// Range
																										// ....(0-99)
			sysex[6 + 41] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 28] & 127)); // Foot Control
																										// 2 EG Bias
																										// Range
																										// ......(0-99)
			sysex[6 + 42] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 29] & 127)); // Foot Control
																										// 2 Volume
																										// Range
																										// .......(0-99)
			sysex[6 + 43] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 30] & 127)); // MIDI IN
																										// Control Pitch
																										// Mod Range
																										// ....(0-99)
			sysex[6 + 44] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 31] & 127)); // MIDI IN
																										// Control Ampl.
																										// Mod Range
																										// ....(0-99)
			sysex[6 + 45] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 32] & 127)); // MIDI IN
																										// Control EG
																										// Bias Range
																										// ......(0-99)
			sysex[6 + 46] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 33] & 127)); // MIDI IN
																										// Control
																										// Volume Range
																										// .......(0-99)
			sysex[6 + 47] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 34] & 7)); // Unison detune
																										// depth
																										// .................(0-
																										// 7)
			sysex[6 + 48] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 34] & 8) / 8); // Foot
																											// Control 1
																											// use as
																											// CS1
																											// switch
																											// ....(0-
																											// 1)

			sysex[singleSize - 1] = (byte) 0xF7;

			PatchDataImpl p = new PatchDataImpl(sysex, getDevice()); // single sysex
			p.calculateChecksum();

			return p;
		} catch (Exception e) {
			ErrorMsgUtil.reportError(getManufacturerName() + " " + getModelName(), "Error in " + toString(), e);
			return null;
		}
	}

	public PatchDataImpl createNewPatch() // create a bank with 32 additional voice patches
	{
		byte[] sysex = new byte[trimSize];

		sysex[00] = (byte) 0xF0;
		sysex[01] = (byte) 0x43;
		sysex[02] = (byte) 0x00;
		sysex[03] = (byte) 0x06;
		sysex[04] = (byte) 0x08;
		sysex[05] = (byte) 0x60;
		sysex[trimSize - 1] = (byte) 0xF7;

		PatchDataImpl v = new PatchDataImpl(initSysex, getDevice()); // single sysex
		PatchDataImpl p = new PatchDataImpl(sysex, this); // bank sysex

		for (int i = 0; i < getNumPatches(); i++)
			putPatch(p, v, i);

		calculateChecksum(p);

		return p;
	}

	public String getPatchName(PatchDataImpl bank, int patchNum) {
		return "-";
	}

	public void setPatchName(PatchDataImpl bank, int patchNum, String name) {
		// do nothing
	}
}
