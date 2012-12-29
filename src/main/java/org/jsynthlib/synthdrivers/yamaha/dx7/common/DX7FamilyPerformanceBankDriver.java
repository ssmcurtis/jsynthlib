/*
 * JSynthlib - generic "Performance" Bank Driver for Yamaha DX7 Family
 * (used by DX1, DX5, DX7 MKI, TX7, TX816)
 * ===================================================================
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

import java.io.UnsupportedEncodingException;

import org.jsynthlib.menu.patch.BankDriver;
import org.jsynthlib.menu.patch.Patch;
import org.jsynthlib.menu.patch.SysexHandler;
import org.jsynthlib.tools.ErrorMsg;

public class DX7FamilyPerformanceBankDriver extends BankDriver {
	byte[] initSysex;
	String[] dxPatchNumbers;
	String[] dxBankNumbers;

	private static final int dxPatchNameSize = 30; // size of patchname of single patch
	private static final int dxPatchNameOffset = 34; // offset in packed bank format
	private static final int dxSinglePackedSize = 64; // size of single patch in packed bank format
	private static final int dxSysexHeaderSize = 6; // length of sysex header

	public DX7FamilyPerformanceBankDriver(byte[] initSysex, String[] dxPatchNumbers, String[] dxBankNumbers) {
		super("Performance Bank", "Torsten Tittmann", dxPatchNumbers.length, 4);

		this.initSysex = initSysex;
		this.dxPatchNumbers = dxPatchNumbers;
		this.dxBankNumbers = dxBankNumbers;

		sysexID = "F0430*022000";
		sysexRequestDump = new SysexHandler("F0 43 @@ 02 F7");
		deviceIDoffset = 2;
		patchNumbers = dxPatchNumbers;
		bankNumbers = dxBankNumbers;
		singleSysexID = "F0430*01005E";
		singleSize = 102;
		checksumOffset = 4102;
		checksumStart = 6;
		checksumEnd = 4101;
		numSysexMsgs = 1;
		patchSize = 4104;
		trimSize = patchSize;
	}

	public int getPatchStart(int patchNum) {
		return (dxSinglePackedSize * patchNum) + dxSysexHeaderSize;
	}

	public int getPatchNameStart(int patchNum) {
		return getPatchStart(patchNum) + dxPatchNameOffset;
	}

	public String getPatchName(Patch p, int patchNum) {
		int nameStart = getPatchNameStart(patchNum);

		try {
			StringBuffer s = new StringBuffer(new String(((Patch) p).getSysex(), nameStart, dxPatchNameSize,
					"US-ASCII"));

			return s.toString();
		} catch (UnsupportedEncodingException ex) {
			return "-";
		}
	}

	public void setPatchName(Patch p, int patchNum, String name) {
		int nameStart = getPatchNameStart(patchNum);

		while (name.length() < dxPatchNameSize)
			name = name + " ";
		byte[] namebytes = new byte[dxPatchNameSize];

		try {
			namebytes = name.getBytes("US-ASCII");
			for (int i = 0; i < dxPatchNameSize; i++)
				((Patch) p).getSysex()[nameStart + i] = namebytes[i];
		} catch (UnsupportedEncodingException ex) {
			return;
		}
	}

	public void putPatch(Patch bank, Patch p, int patchNum) // puts a patch into the bank, converting it
																			// as needed
	{
		if (!canHoldPatch(p)) {
			DX7FamilyStrings.dxShowError(toString(), "This type of patch does not fit in to this type of bank.");
			return;
		}

		// Transform Voice Data to Bulk Dump Packed Format

		// ***** Voice A *****
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 0] = (byte) (((((Patch) p).getSysex()[6 + 2] & 1) * 64) // Poly/Mono
																														// .......................(0-
																														// 1)
		+ (patchNum & 63)); // UNDOCUMENTED! related voice# ....(0-63)
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 1] = (byte) (((((Patch) p).getSysex()[6 + 4] & 7) * 16) // Pitch
																														// Bend
																														// Step
																														// (Low)............(0-12)
		+ (((Patch) p).getSysex()[6 + 3] & 15)); // Pitch Bend Range ................(0-12)
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 2] = (byte) ((((Patch) p).getSysex()[6 + 5] & 127)); // Portamento
																														// Time
																														// .................(0-99)
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 3] = (byte) (((((Patch) p).getSysex()[6 + 1] & 15) * 8) // Source
																														// Select
																														// ...................(0-15)
				+ ((((Patch) p).getSysex()[6 + 8] & 1) * 4) // Portamento Pedal and Knob Assign (0- 1)
				+ ((((Patch) p).getSysex()[6 + 7] & 1) * 2) // Portamento Mode .................(0- 1)
		+ (((Patch) p).getSysex()[6 + 6] & 1)); // Portamento/Glissando ............(0- 1)
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 4] = (byte) (((((Patch) p).getSysex()[6 + 10] & 7) * 16) // Modulation
																															// Wheel
																															// Assign
																															// .........(0-
																															// 7)
		+ (((Patch) p).getSysex()[6 + 9] & 15)); // Modulation Wheel Sensitivity ....(0-15)
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 5] = (byte) (((((Patch) p).getSysex()[6 + 12] & 7) * 16) // Foot
																															// Control
																															// Assign
																															// .............(0-
																															// 7)
		+ (((Patch) p).getSysex()[6 + 11] & 15)); // Foot Control Sensitivity ........(0-15)
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 6] = (byte) (((((Patch) p).getSysex()[6 + 14] & 7) * 16) // Aftertouch
																															// Assign
																															// ...............(0-
																															// 7)
		+ (((Patch) p).getSysex()[6 + 13] & 15)); // Aftertouch Sensitivity ..........(0-15)
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 7] = (byte) (((((Patch) p).getSysex()[6 + 16] & 7) * 16) // Breath
																															// Control
																															// Assign
																															// ...........(0-
																															// 7)
		+ (((Patch) p).getSysex()[6 + 15] & 15)); // Breath Control Sensitivity ......(0-15)
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 8] = (byte) (0); // not used?
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 9] = (byte) (0); // not used?
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 10] = (byte) (0); // not used?
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 11] = (byte) (0); // not used?
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 12] = (byte) (0); // not used?
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 13] = (byte) (0); // not used?
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 14] = (byte) ((((Patch) p).getSysex()[6 + 26] & 7)); // Attenuation
																														// .....................(0-
																														// 7)
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 15] = (byte) ((((((Patch) p).getSysex()[6 + 4] & 15) >> 3) * 64) // Pitch
																																	// Bend
																																	// Step
																																	// (High)
																																	// ..........(0-12)
		+ (((Patch) p).getSysex()[6 + 29] & 63)); // Performance Key Shift ...........(0-48)
		// ***** Voice B *****
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 16] = (byte) (((((Patch) p).getSysex()[6 + 32] & 1) * 64) // Poly/Mono
																															// .......................(0-
																															// 1)
		+ (patchNum & 63)); // UNDOCUMENTED! matching voice# ...(0-63)
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 17] = (byte) (((((Patch) p).getSysex()[6 + 34] & 7) * 16) // Pitch
																															// Bend
																															// Step
																															// (Low)............(0-12)
		+ (((Patch) p).getSysex()[6 + 33] & 15)); // Pitch Bend Range ................(0-12)
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 18] = (byte) ((((Patch) p).getSysex()[6 + 35] & 127)); // Portamento
																														// Time
																														// .................(0-99)
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 19] = (byte) (((((Patch) p).getSysex()[6 + 31] & 15) * 8) // Source
																															// Select
																															// ...................(0-15)
				+ ((((Patch) p).getSysex()[6 + 38] & 1) * 4) // Portamento Pedal and Knob Assign (0- 1)
				+ ((((Patch) p).getSysex()[6 + 37] & 1) * 2) // Portamento Mode .................(0- 1)
		+ (((Patch) p).getSysex()[6 + 36] & 1)); // Portamento/Glissando ............(0- 1)
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 20] = (byte) (((((Patch) p).getSysex()[6 + 40] & 7) * 16) // Modulation
																															// Wheel
																															// Assign
																															// .........(0-
																															// 7)
		+ (((Patch) p).getSysex()[6 + 39] & 15)); // Modulation Wheel Sensitivity ....(0-15)
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 21] = (byte) (((((Patch) p).getSysex()[6 + 42] & 7) * 16) // Foot
																															// Control
																															// Assign
																															// .............(0-
																															// 7)
		+ (((Patch) p).getSysex()[6 + 41] & 15)); // Foot Control Sensitivity ........(0-15)
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 22] = (byte) (((((Patch) p).getSysex()[6 + 44] & 7) * 16) // Aftertouch
																															// Assign
																															// ...............(0-
																															// 7)
		+ (((Patch) p).getSysex()[6 + 43] & 15)); // Aftertouch Sensitivity ..........(0-15)
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 23] = (byte) (((((Patch) p).getSysex()[6 + 46] & 7) * 16) // Breath
																															// Control
																															// Assign
																															// ...........(0-
																															// 7)
		+ (((Patch) p).getSysex()[6 + 45] & 15)); // Breath Control Sensitivity ......(0-15)
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 24] = (byte) (0); // not used?
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 25] = (byte) (0); // not used?
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 26] = (byte) (0); // not used?
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 27] = (byte) (0); // not used?
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 28] = (byte) (0); // not used?
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 29] = (byte) (0); // not used?
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 30] = (byte) ((((Patch) p).getSysex()[6 + 56] & 7)); // Attenuation
																														// .....................(0-
																														// 7)
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 31] = (byte) ((((((Patch) p).getSysex()[6 + 34] & 15) >> 3) * 64) // Pitch
																																	// Bend
																																	// Step
																																	// (High)
																																	// ..........(0-12)
		+ (((Patch) p).getSysex()[6 + 59] & 63)); // Performance Key Shift ...........(0-48)

		// ***** Common *****
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 32] = (byte) (((((Patch) p).getSysex()[6 + 62] & 15) * 8) // Dual
																															// Mode
																															// Detune
																															// ................(0-15)
				+ ((((Patch) p).getSysex()[6 + 61] & 1) * 4) // Voice Memory Select .............(0- 1)
		+ (((Patch) p).getSysex()[6 + 60] & 3)); // Key Assign Mode .................(0- 2)
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 33] = (byte) (((Patch) p).getSysex()[6 + 63]); // Split
																												// Point
																												// .....................(0-99)
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 34] = (byte) (((Patch) p).getSysex()[6 + 64]); // Performance
																												// name
																												// 1
																												// ..............ASCII
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 35] = (byte) (((Patch) p).getSysex()[6 + 65]); // Performance
																												// name
																												// 2
																												// ..............ASCII
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 36] = (byte) (((Patch) p).getSysex()[6 + 66]); // Performance
																												// name
																												// 3
																												// ..............ASCII
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 37] = (byte) (((Patch) p).getSysex()[6 + 67]); // Performance
																												// name
																												// 4
																												// ..............ASCII
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 38] = (byte) (((Patch) p).getSysex()[6 + 68]); // Performance
																												// name
																												// 5
																												// ..............ASCII
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 39] = (byte) (((Patch) p).getSysex()[6 + 69]); // Performance
																												// name
																												// 6
																												// ..............ASCII
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 40] = (byte) (((Patch) p).getSysex()[6 + 70]); // Performance
																												// name
																												// 7
																												// ..............ASCII
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 41] = (byte) (((Patch) p).getSysex()[6 + 71]); // Performance
																												// name
																												// 8
																												// ..............ASCII
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 42] = (byte) (((Patch) p).getSysex()[6 + 72]); // Performance
																												// name
																												// 9
																												// ..............ASCII
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 43] = (byte) (((Patch) p).getSysex()[6 + 73]); // Performance
																												// name
																												// 10
																												// ..............ASCII
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 44] = (byte) (((Patch) p).getSysex()[6 + 74]); // Performance
																												// name
																												// 11
																												// ..............ASCII
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 45] = (byte) (((Patch) p).getSysex()[6 + 75]); // Performance
																												// name
																												// 12
																												// ..............ASCII
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 46] = (byte) (((Patch) p).getSysex()[6 + 76]); // Performance
																												// name
																												// 13
																												// ..............ASCII
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 47] = (byte) (((Patch) p).getSysex()[6 + 77]); // Performance
																												// name
																												// 14
																												// ..............ASCII
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 48] = (byte) (((Patch) p).getSysex()[6 + 78]); // Performance
																												// name
																												// 15
																												// ..............ASCII
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 49] = (byte) (((Patch) p).getSysex()[6 + 79]); // Performance
																												// name
																												// 16
																												// ..............ASCII
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 50] = (byte) (((Patch) p).getSysex()[6 + 80]); // Performance
																												// name
																												// 17
																												// ..............ASCII
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 51] = (byte) (((Patch) p).getSysex()[6 + 81]); // Performance
																												// name
																												// 18
																												// ..............ASCII
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 52] = (byte) (((Patch) p).getSysex()[6 + 82]); // Performance
																												// name
																												// 19
																												// ..............ASCII
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 53] = (byte) (((Patch) p).getSysex()[6 + 83]); // Performance
																												// name
																												// 20
																												// ..............ASCII
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 54] = (byte) (((Patch) p).getSysex()[6 + 84]); // Performance
																												// name
																												// 21
																												// ..............ASCII
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 55] = (byte) (((Patch) p).getSysex()[6 + 85]); // Performance
																												// name
																												// 22
																												// ..............ASCII
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 56] = (byte) (((Patch) p).getSysex()[6 + 86]); // Performance
																												// name
																												// 23
																												// ..............ASCII
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 57] = (byte) (((Patch) p).getSysex()[6 + 87]); // Performance
																												// name
																												// 24
																												// ..............ASCII
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 58] = (byte) (((Patch) p).getSysex()[6 + 88]); // Performance
																												// name
																												// 25
																												// ..............ASCII
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 59] = (byte) (((Patch) p).getSysex()[6 + 89]); // Performance
																												// name
																												// 26
																												// ..............ASCII
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 60] = (byte) (((Patch) p).getSysex()[6 + 90]); // Performance
																												// name
																												// 27
																												// ..............ASCII
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 61] = (byte) (((Patch) p).getSysex()[6 + 91]); // Performance
																												// name
																												// 28
																												// ..............ASCII
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 62] = (byte) (((Patch) p).getSysex()[6 + 92]); // Performance
																												// name
																												// 29
																												// ..............ASCII
		((Patch) bank).getSysex()[getPatchStart(patchNum) + 63] = (byte) (((Patch) p).getSysex()[6 + 93]); // Performance
																												// name
																												// 30
																												// ..............ASCII

		calculateChecksum(bank);
	}

	public Patch getPatch(Patch bank, int patchNum) // Gets a patch from the bank, converting it as needed
	{
		try {
			byte[] sysex = new byte[singleSize];

			// transform bulk-dump-packed-format to voice data
			sysex[0] = (byte) 0xF0;
			sysex[1] = (byte) 0x43;
			sysex[2] = (byte) 0x00;
			sysex[3] = (byte) 0x01;
			sysex[4] = (byte) 0x00;
			sysex[5] = (byte) 0x5E;

			// ***** Voice A *****
			// sysex[6+ 0]=(byte)((bank.sysex[getPatchStart(patchNum)+ 0]& 63)); // UNDOCUMENTED! matching voice#
			// ...(0-63)
			sysex[6 + 0] = (byte) (0); // UNDOCUMENTED! matching voice# ...(0-63) - fixed to 0!
			sysex[6 + 1] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 3] & 120) / 8); // Source
																											// Select
																											// ...................(0-15)
			sysex[6 + 2] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 0] & 64) / 64); // Poly/Mono
																											// .......................(0-
																											// 1)
			sysex[6 + 3] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 1] & 15)); // Pitch Bend Range
																										// ................(0-12)
			sysex[6 + 4] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 1] & 112) / 16 // Pitch Bend
																											// Step
																											// (Low)
																											// ...........(0-12)
			+ (((Patch) bank).getSysex()[getPatchStart(patchNum) + 15] & 64) / 8); // Pitch Bend Step (High)
																						// ..........(0-12)
			sysex[6 + 5] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 2])); // Portamento Time
																								// .................(0-99)
			sysex[6 + 6] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 3] & 1)); // Portamento/Glissando
																									// ............(0-
																									// 1)
			sysex[6 + 7] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 3] & 2) / 2); // Portamento
																										// Mode
																										// .................(0-
																										// 1)
			sysex[6 + 8] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 3] & 4) / 4); // Portamento
																										// Pedal and
																										// Knob Assign
																										// (0- 1)
			sysex[6 + 9] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 4] & 15)); // Modulation Wheel
																										// Sensitivity
																										// ....(0-15)
			sysex[6 + 10] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 4] & 112) / 16); // Modulation
																											// Wheel
																											// Assign
																											// .........(0-
																											// 7)
			sysex[6 + 11] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 5] & 15)); // Foot Controller
																										// Sensitivity
																										// .....(0-15)
			sysex[6 + 12] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 5] & 112) / 16); // Foot
																											// Controller
																											// Assign
																											// ..........(0-
																											// 7)
			sysex[6 + 13] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 6] & 15)); // After Touch
																										// Sensitivity
																										// .........(0-15)
			sysex[6 + 14] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 6] & 112) / 16); // After
																											// Touch
																											// Assign
																											// ..............(0-
																											// 7)
			sysex[6 + 15] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 7] & 15)); // Breath
																										// Controller
																										// Sensitivity
																										// ...(0-15)
			sysex[6 + 16] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 7] & 112) / 16); // Breath
																											// Controller
																											// Assign
																											// ........(0-
																											// 7)
			sysex[6 + 17] = (byte) (0); // KIAT Sensitivity ................(0-15)
			sysex[6 + 18] = (byte) (0); // KIAT - OP1 Sensitivity ..........(0-15)
			sysex[6 + 19] = (byte) (0); // KIAT - OP2 Sensitivity ..........(0-15)
			sysex[6 + 20] = (byte) (0); // KIAT - OP3 Sensitivity ..........(0-15)
			sysex[6 + 21] = (byte) (0); // KIAT - OP4 Sensitivity ..........(0-15)
			sysex[6 + 22] = (byte) (0); // KIAT - OP5 Sensitivity ..........(0-15)
			sysex[6 + 23] = (byte) (0); // KIAT - OP6 Sensitivity ..........(0-15)
			sysex[6 + 24] = (byte) (0); // KIAT - Decay Rate ...............(0-99)
			sysex[6 + 25] = (byte) (0); // KIAT - Release Rate .............(0-99)
			sysex[6 + 26] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 14] & 7)); // Audio Output
																										// Level
																										// Attenuator
																										// ...(0- 7)
			sysex[6 + 27] = (byte) (0); // Program Output ..................(0- 1)
			sysex[6 + 28] = (byte) (0); // Sustain Pedal ...................(0- 1)
			sysex[6 + 29] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 15] & 63)); // Performance
																										// Key Shift
																										// ...........(0-48)

			// ***** Voice B *****
			// sysex[6+30]=(byte)((bank.sysex[getPatchStart(patchNum)+16]& 63)); // UNDOCUMENTED! matching voice#
			// ...(0-63)
			sysex[6 + 30] = (byte) (0); // UNDOCUMENTED! matching voice# ...(0-63) - fixed to 0!
			sysex[6 + 31] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 19] & 120) / 8); // Source
																											// Select
																											// ...................(0-15)
			sysex[6 + 32] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 16] & 64) / 64); // Poly/Mono
																											// .......................(0-
																											// 1)
			sysex[6 + 33] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 17] & 15)); // Pitch Bend
																										// Range
																										// ................(0-12)
			sysex[6 + 34] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 17] & 112) / 16 // Pitch Bend
																											// Step
																											// (Low)
																											// ...........(0-12)
			+ (((Patch) bank).getSysex()[getPatchStart(patchNum) + 31] & 64) / 8); // Pitch Bend Step (High)
																						// ..........(0-12)
			sysex[6 + 35] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 18])); // Portamento Time
																									// .................(0-99)
			sysex[6 + 36] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 19] & 1)); // Portamento/Glissando
																										// ............(0-
																										// 1)
			sysex[6 + 37] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 19] & 2) / 2); // Portamento
																											// Mode
																											// .................(0-
																											// 1)
			sysex[6 + 38] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 19] & 4) / 4); // Portamento
																											// Pedal and
																											// Knob
																											// Assign
																											// (0- 1)
			sysex[6 + 39] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 20] & 15)); // Modulation
																										// Wheel
																										// Sensitivity
																										// ....(0-15)
			sysex[6 + 40] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 20] & 112) / 16); // Modulation
																												// Wheel
																												// Assign
																												// .........(0-
																												// 7)
			sysex[6 + 41] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 21] & 15)); // Foot
																										// Controller
																										// Sensitivity
																										// .....(0-15)
			sysex[6 + 42] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 21] & 112) / 16); // Foot
																												// Controller
																												// Assign
																												// ..........(0-
																												// 7)
			sysex[6 + 43] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 22] & 15)); // After Touch
																										// Sensitivity
																										// .........(0-15)
			sysex[6 + 44] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 22] & 112) / 16); // After
																												// Touch
																												// Assign
																												// ..............(0-
																												// 7)
			sysex[6 + 45] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 23] & 15)); // Breath
																										// Controller
																										// Sensitivity
																										// ...(0-15)
			sysex[6 + 46] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 23] & 112) / 16); // Breath
																												// Controller
																												// Assign
																												// ........(0-
																												// 7)
			sysex[6 + 47] = (byte) (0); // KIAT Sensitivity ................(0-15)
			sysex[6 + 48] = (byte) (0); // KIAT - OP1 Sensitivity ..........(0-15)
			sysex[6 + 49] = (byte) (0); // KIAT - OP2 Sensitivity ..........(0-15)
			sysex[6 + 50] = (byte) (0); // KIAT - OP3 Sensitivity ..........(0-15)
			sysex[6 + 51] = (byte) (0); // KIAT - OP4 Sensitivity ..........(0-15)
			sysex[6 + 52] = (byte) (0); // KIAT - OP5 Sensitivity ..........(0-15)
			sysex[6 + 53] = (byte) (0); // KIAT - OP6 Sensitivity ..........(0-15)
			sysex[6 + 54] = (byte) (0); // KIAT - Decay Rate ...............(0-99)
			sysex[6 + 55] = (byte) (0); // KIAT - Release Rate .............(0-99)
			sysex[6 + 56] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 30] & 7)); // Audio Output
																										// Level
																										// Attenuator
																										// ...(0- 7)
			sysex[6 + 57] = (byte) (0); // Program Output ..................(0- 1)
			sysex[6 + 58] = (byte) (0); // Sustain Pedal ...................(0- 1)
			sysex[6 + 59] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 31] & 63)); // Performance
																										// Key Shift
																										// ...........(0-48)

			// ***** Common *****
			sysex[6 + 60] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 32] & 3)); // Key Assign Mode
																										// .................(0-
																										// 2)
			sysex[6 + 61] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 32] & 4) / 4); // Voice
																											// Memory
																											// Select
																											// Flag
																											// ........(0-
																											// 1)
			sysex[6 + 62] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 32] & 120) / 8); // Dual Mode
																											// Detune
																											// ................(0-15)
			sysex[6 + 63] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 33])); // Split Point
																									// .....................(0-99)
			sysex[6 + 64] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 34])); // Performance name 1
																									// ..............ASCII
			sysex[6 + 65] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 35])); // Performance name 2
																									// ..............ASCII
			sysex[6 + 66] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 36])); // Performance name 3
																									// ..............ASCII
			sysex[6 + 67] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 37])); // Performance name 4
																									// ..............ASCII
			sysex[6 + 68] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 38])); // Performance name 5
																									// ..............ASCII
			sysex[6 + 69] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 39])); // Performance name 6
																									// ..............ASCII
			sysex[6 + 70] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 40])); // Performance name 7
																									// ..............ASCII
			sysex[6 + 71] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 41])); // Performance name 8
																									// ..............ASCII
			sysex[6 + 72] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 42])); // Performance name 9
																									// ..............ASCII
			sysex[6 + 73] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 43])); // Performance name 10
																									// ..............ASCII
			sysex[6 + 74] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 44])); // Performance name 11
																									// ..............ASCII
			sysex[6 + 75] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 45])); // Performance name 12
																									// ..............ASCII
			sysex[6 + 76] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 46])); // Performance name 13
																									// ..............ASCII
			sysex[6 + 77] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 47])); // Performance name 14
																									// ..............ASCII
			sysex[6 + 78] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 48])); // Performance name 15
																									// ..............ASCII
			sysex[6 + 79] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 49])); // Performance name 16
																									// ..............ASCII
			sysex[6 + 80] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 50])); // Performance name 17
																									// ..............ASCII
			sysex[6 + 81] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 51])); // Performance name 18
																									// ..............ASCII
			sysex[6 + 82] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 52])); // Performance name 19
																									// ..............ASCII
			sysex[6 + 83] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 53])); // Performance name 20
																									// ..............ASCII
			sysex[6 + 84] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 54])); // Performance name 21
																									// ..............ASCII
			sysex[6 + 85] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 55])); // Performance name 22
																									// ..............ASCII
			sysex[6 + 86] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 56])); // Performance name 23
																									// ..............ASCII
			sysex[6 + 87] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 57])); // Performance name 24
																									// ..............ASCII
			sysex[6 + 88] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 58])); // Performance name 25
																									// ..............ASCII
			sysex[6 + 89] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 59])); // Performance name 26
																									// ..............ASCII
			sysex[6 + 90] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 60])); // Performance name 27
																									// ..............ASCII
			sysex[6 + 91] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 61])); // Performance name 28
																									// ..............ASCII
			sysex[6 + 92] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 62])); // Performance name 29
																									// ..............ASCII
			sysex[6 + 93] = (byte) ((((Patch) bank).getSysex()[getPatchStart(patchNum) + 63])); // Performance name 30
																									// ..............ASCII

			sysex[singleSize - 1] = (byte) 0xF7;

			Patch p = new Patch(sysex, getDevice()); // single sysex
			p.calculateChecksum();

			return p;
		} catch (Exception e) {
			ErrorMsg.reportError(getManufacturerName() + " " + getModelName(), "Error in " + toString(), e);
			return null;
		}
	}

	public Patch createNewPatch() // create a bank with 64 " YAMAHA TX7 FUNCTION DATA  " patches
	{
		byte[] sysex = new byte[trimSize];

		sysex[00] = (byte) 0xF0;
		sysex[01] = (byte) 0x43;
		sysex[02] = (byte) 0x00;
		sysex[03] = (byte) 0x02;
		sysex[04] = (byte) 0x20;
		sysex[05] = (byte) 0x00;
		sysex[trimSize - 1] = (byte) 0xF7;

		Patch v = new Patch(initSysex, getDevice()); // single sysex
		Patch p = new Patch(sysex, this); // bank sysex

		for (int i = 0; i < getNumPatches(); i++)
			putPatch(p, v, i);
		calculateChecksum(p);

		return p;
	}
}
