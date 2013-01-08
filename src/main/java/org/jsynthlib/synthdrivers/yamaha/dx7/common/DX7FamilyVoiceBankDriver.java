/*
 * JSynthlib -	generic "Voice" Bank Driver for DX7 Family
 * (used by DX1, DX5, DX7 MKI, TX7, TX816, DX7-II, DX7s, TX802)
 * =============================================================
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

import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

public class DX7FamilyVoiceBankDriver extends SynthDriverBank {
	byte[] initSysex;
	String[] dxPatchNumbers;
	String[] dxBankNumbers;

	private static final int dxPatchNameSize = 10; // size of patchname of single patch
	private static final int dxPatchNameOffset = 118; // offset in packed bank format
	private static final int dxSinglePackedSize = 128; // size of single patch in packed bank format
	private static final int dxSysexHeaderSize = 6; // length of sysex header

	public DX7FamilyVoiceBankDriver(byte[] initSysex, String[] dxPatchNumbers, String[] dxBankNumbers) {
		super("Voice Bank", "Torsten Tittmann", dxPatchNumbers.length, 4);

		this.initSysex = initSysex;
		this.dxPatchNumbers = dxPatchNumbers;
		this.dxBankNumbers = dxBankNumbers;

		sysexID = "F0430*092000";
		sysexRequestDump = new SysexHandler("F0 43 @@ 09 F7");
		deviceIDoffset = 2;
		bankNumbers = dxBankNumbers;
		patchNumbers = dxPatchNumbers;
		singleSysexID = "F0430*00011B";
		singleSize = 163;
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

	public String getPatchName(PatchDataImpl p, int patchNum) {
		int nameStart = getPatchNameStart(patchNum);

		try {
			StringBuffer s = new StringBuffer(new String(((PatchDataImpl) p).getSysex(), nameStart, dxPatchNameSize,
					"US-ASCII"));
			return s.toString();
		} catch (UnsupportedEncodingException ex) {
			return "-";
		}
	}

	public void setPatchName(PatchDataImpl p, int patchNum, String name) {
		int nameStart = getPatchNameStart(patchNum);

		while (name.length() < dxPatchNameSize)
			name = name + " ";

		byte[] namebytes = new byte[dxPatchNameSize];

		try {
			namebytes = name.getBytes("US-ASCII");
			for (int i = 0; i < dxPatchNameSize; i++)
				((PatchDataImpl) p).getSysex()[nameStart + i] = namebytes[i];

		} catch (UnsupportedEncodingException ex) {
			return;
		}
	}

	public void putPatch(PatchDataImpl bank, PatchDataImpl p, int patchNum) // puts a patch into the bank, converting it
																			// as needed
	{
		if (!canHoldPatch(p)) {
			DX7FamilyStrings.dxShowError(toString(), "This type of patch does not fit in to this type of bank.");
			return;
		}

		// Transform Voice Data to Bulk Dump Packed Format

		// ***** OPERATOR 6 *****
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 0] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 0])); // EG
																												// Rate
																												// 1
																												// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 1] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 1])); // EG
																												// Rate
																												// 2
																												// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 2] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 2])); // EG
																												// Rate
																												// 3
																												// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 3] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 3])); // EG
																												// Rate
																												// 4
																												// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 4] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 4])); // EG
																												// Level
																												// 1
																												// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 5] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 5])); // EG
																												// Level
																												// 2
																												// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 6] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 6])); // EG
																												// Level
																												// 3
																												// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 7] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 7])); // EG
																												// Level
																												// 4
																												// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 8] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 8])); // Kbd
																												// Level
																												// Scale
																												// Break
																												// Point
																												// (0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 9] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 9])); // Kbd
																												// Level
																												// Scale
																												// Left
																												// Depth
																												// .(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 10] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 10])); // Kbd
																													// Level
																													// Scale
																													// Right
																													// Depth
																													// (0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 11] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 12] * 4 + ((PatchDataImpl) p).getSysex()[6 + 11])); // Kbd
																																						// Level
																																						// Scale
																																						// Right
																																						// Curve
																																						// .(0-3)
		// | Left Curve ...............(0-3)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 12] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 20] * 8 + ((PatchDataImpl) p).getSysex()[6 + 13])); // Osc
																																						// Detune
																																						// .................(0-14)
		// | Kbd Rate Scaling .........(0-7)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 13] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 15] * 4 + ((PatchDataImpl) p).getSysex()[6 + 14])); // Key
																																						// Velocity
																																						// Sensitivity
																																						// ....(0-7)
		// | Mod Sensitivity Amplitude (0-3)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 14] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 16])); // Operator
																													// Output
																													// Level
																													// ......(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 15] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 18] * 2 + ((PatchDataImpl) p).getSysex()[6 + 17])); // Osc
																																						// Frequency
																																						// Coarse
																																						// .......(0-31)
		// | Osc Mode .................(0-1)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 16] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 19])); // Osc
																													// Frequency
																													// Fine
																													// .........(0-99)

		// ***** OPERATOR 5 *****
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 17] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 21])); // EG
																													// Rate
																													// 1
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 18] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 22])); // EG
																													// Rate
																													// 2
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 19] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 23])); // EG
																													// Rate
																													// 3
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 20] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 24])); // EG
																													// Rate
																													// 4
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 21] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 25])); // EG
																													// Level
																													// 1
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 22] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 26])); // EG
																													// Level
																													// 2
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 23] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 27])); // EG
																													// Level
																													// 3
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 24] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 28])); // EG
																													// Level
																													// 4
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 25] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 29])); // Kbd
																													// Level
																													// Scale
																													// Break
																													// Point
																													// (0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 26] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 30])); // Kbd
																													// Level
																													// Scale
																													// Left
																													// Depth
																													// .(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 27] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 31])); // Kbd
																													// Level
																													// Scale
																													// Right
																													// Depth
																													// (0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 28] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 33] * 4 + ((PatchDataImpl) p).getSysex()[6 + 32])); // Kbd
																																						// Level
																																						// Scale
																																						// Right
																																						// Curve
																																						// .(0-3)
		// | Left Curve ...............(0-3)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 29] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 41] * 8 + ((PatchDataImpl) p).getSysex()[6 + 34])); // Osc
																																						// Detune
																																						// .................(0-14)
		// | Kbd Rate Scaling .........(0-7)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 30] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 36] * 4 + ((PatchDataImpl) p).getSysex()[6 + 35])); // Key
																																						// Velocity
																																						// Sensitivity
																																						// ....(0-7)
		// | Mod Sensitivity Amplitude (0-3)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 31] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 37])); // Operator
																													// Output
																													// Level
																													// ......(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 32] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 39] * 2 + ((PatchDataImpl) p).getSysex()[6 + 38])); // Osc
																																						// Frequency
																																						// Coarse
																																						// .......(0-31)
		// | Osc Mode .................(0-1)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 33] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 40])); // Osc
																													// Frequency
																													// Fine
																													// .........(0-99)

		// ***** OPERATOR 4 *****
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 34] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 42])); // EG
																													// Rate
																													// 1
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 35] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 43])); // EG
																													// Rate
																													// 2
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 36] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 44])); // EG
																													// Rate
																													// 3
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 37] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 45])); // EG
																													// Rate
																													// 4
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 38] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 46])); // EG
																													// Level
																													// 1
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 39] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 47])); // EG
																													// Level
																													// 2
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 40] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 48])); // EG
																													// Level
																													// 3
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 41] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 49])); // EG
																													// Level
																													// 4
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 42] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 50])); // Kbd
																													// Level
																													// Scale
																													// Break
																													// Point
																													// (0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 43] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 51])); // Kbd
																													// Level
																													// Scale
																													// Left
																													// Depth
																													// .(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 44] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 52])); // Kbd
																													// Level
																													// Scale
																													// Right
																													// Depth
																													// (0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 45] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 54] * 4 + ((PatchDataImpl) p).getSysex()[6 + 53])); // Kbd
																																						// Level
																																						// Scale
																																						// Right
																																						// Curve
																																						// .(0-3)
		// | Left Curve ...............(0-3)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 46] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 62] * 8 + ((PatchDataImpl) p).getSysex()[6 + 55])); // Osc
																																						// Detune
																																						// .................(0-14)
		// | Kbd Rate Scaling .........(0-7)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 47] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 57] * 4 + ((PatchDataImpl) p).getSysex()[6 + 56])); // Key
																																						// Velocity
																																						// Sensitivity
																																						// ....(0-7)
		// | Mod Sensitivity Amplitude (0-3)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 48] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 58])); // Operator
																													// Output
																													// Level
																													// ......(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 49] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 60] * 2 + ((PatchDataImpl) p).getSysex()[6 + 59])); // Osc
																																						// Frequency
																																						// Coarse
																																						// .......(0-31)
		// | Osc Mode .................(0-1)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 50] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 61])); // Osc
																													// Frequency
																													// Fine
																													// .........(0-99)

		// ***** OPERATOR 3 *****
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 51] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 63])); // EG
																													// Rate
																													// 1
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 52] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 64])); // EG
																													// Rate
																													// 2
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 53] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 65])); // EG
																													// Rate
																													// 3
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 54] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 66])); // EG
																													// Rate
																													// 4
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 55] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 67])); // EG
																													// Level
																													// 1
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 56] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 68])); // EG
																													// Level
																													// 2
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 57] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 69])); // EG
																													// Level
																													// 3
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 58] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 70])); // EG
																													// Level
																													// 4
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 59] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 71])); // Kbd
																													// Level
																													// Scale
																													// Break
																													// Point
																													// (0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 60] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 72])); // Kbd
																													// Level
																													// Scale
																													// Left
																													// Depth
																													// .(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 61] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 73])); // Kbd
																													// Level
																													// Scale
																													// Right
																													// Depth
																													// (0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 62] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 75] * 4 + ((PatchDataImpl) p).getSysex()[6 + 74])); // Kbd
																																						// Level
																																						// Scale
																																						// Right
																																						// Curve
																																						// .(0-3)
		// | Left Curve ...............(0-3)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 63] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 83] * 8 + ((PatchDataImpl) p).getSysex()[6 + 76])); // Osc
																																						// Detune
																																						// .................(0-14)
		// | Kbd Rate Scaling .........(0-7)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 64] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 78] * 4 + ((PatchDataImpl) p).getSysex()[6 + 77])); // Key
																																						// Velocity
																																						// Sensitivity
																																						// ....(0-7)
		// | Mod Sensitivity Amplitude (0-3)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 65] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 79])); // Operator
																													// Output
																													// Level
																													// ......(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 66] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 81] * 2 + ((PatchDataImpl) p).getSysex()[6 + 80])); // Osc
																																						// Frequency
																																						// Coarse
																																						// .......(0-31)
		// | Osc Mode .................(0-1)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 67] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 82])); // Osc
																													// Frequency
																													// Fine
																													// .........(0-99)

		// ***** OPERATOR 2 *****
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 68] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 84])); // EG
																													// Rate
																													// 1
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 69] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 85])); // EG
																													// Rate
																													// 2
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 70] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 86])); // EG
																													// Rate
																													// 3
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 71] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 87])); // EG
																													// Rate
																													// 4
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 72] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 88])); // EG
																													// Level
																													// 1
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 73] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 89])); // EG
																													// Level
																													// 2
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 74] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 90])); // EG
																													// Level
																													// 3
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 75] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 91])); // EG
																													// Level
																													// 4
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 76] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 92])); // Kbd
																													// Level
																													// Scale
																													// Break
																													// Point
																													// (0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 77] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 93])); // Kbd
																													// Level
																													// Scale
																													// Left
																													// Depth
																													// .(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 78] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 94])); // Kbd
																													// Level
																													// Scale
																													// Right
																													// Depth
																													// (0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 79] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 96] * 4 + ((PatchDataImpl) p).getSysex()[6 + 95])); // Kbd
																																						// Level
																																						// Scale
																																						// Right
																																						// Curve
																																						// .(0-3)
		// | Left Curve ...............(0-3)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 80] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 104] * 8 + ((PatchDataImpl) p).getSysex()[6 + 97])); // Osc
																																							// Detune
																																							// .................(0-14)
		// | Kbd Rate Scaling .........(0-7)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 81] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 99] * 4 + ((PatchDataImpl) p).getSysex()[6 + 98])); // Key
																																						// Velocity
																																						// Sensitivity
																																						// ....(0-7)
		// | Mod Sensitivity Amplitude (0-3)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 82] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 100])); // Operator
																													// Output
																													// Level
																													// ......(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 83] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 102] * 2 + ((PatchDataImpl) p).getSysex()[6 + 101])); // Osc
																																							// Frequency
																																							// Coarse
																																							// .......(0-31)
		// | Osc Mode .................(0-1)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 84] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 103])); // Osc
																													// Frequency
																													// Fine
																													// .........(0-99)

		// ***** OPERATOR 1 *****
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 85] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 105])); // EG
																													// Rate
																													// 1
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 86] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 106])); // EG
																													// Rate
																													// 2
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 87] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 107])); // EG
																													// Rate
																													// 3
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 88] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 108])); // EG
																													// Rate
																													// 4
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 89] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 109])); // EG
																													// Level
																													// 1
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 90] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 110])); // EG
																													// Level
																													// 2
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 91] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 111])); // EG
																													// Level
																													// 3
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 92] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 112])); // EG
																													// Level
																													// 4
																													// .................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 93] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 113])); // Kbd
																													// Level
																													// Scale
																													// Break
																													// Point
																													// (0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 94] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 114])); // Kbd
																													// Level
																													// Scale
																													// Left
																													// Depth
																													// .(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 95] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 115])); // Kbd
																													// Level
																													// Scale
																													// Right
																													// Depth
																													// (0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 96] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 117] * 4 + ((PatchDataImpl) p).getSysex()[6 + 116])); // Kbd
																																							// Level
																																							// Scale
																																							// Right
																																							// Curve
																																							// .(0-3)
		// | Left Curve ...............(0-3)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 97] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 125] * 8 + ((PatchDataImpl) p).getSysex()[6 + 118])); // Osc
																																							// Detune
																																							// .................(0-14)
		// | Kbd Rate Scaling .........(0-7)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 98] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 120] * 4 + ((PatchDataImpl) p).getSysex()[6 + 119])); // Key
																																							// Velocity
																																							// Sensitivity
																																							// ....(0-7)
		// | Mod Sensitivity Amplitude (0-3)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 99] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 121])); // Operator
																													// Output
																													// Level
																													// ......(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 100] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 123] * 2 + ((PatchDataImpl) p).getSysex()[6 + 122])); // Osc
																																							// Frequency
																																							// Coarse
																																							// .......(0-31)
		// | Osc Mode .................(0-1)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 101] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 124])); // Osc
																													// Frequency
																													// Fine
																													// .........(0-99)

		// ***** other Parameters *****
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 102] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 126])); // Pitch
																													// EG
																													// Rate
																													// 1
																													// ............(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 103] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 127])); // Pitch
																													// EG
																													// Rate
																													// 2
																													// ............(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 104] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 128])); // Pitch
																													// EG
																													// Rate
																													// 3
																													// ............(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 105] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 129])); // Pitch
																													// EG
																													// Rate
																													// 4
																													// ............(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 106] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 130])); // Pitch
																													// EG
																													// Level
																													// 1
																													// ...........(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 107] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 131])); // Pitch
																													// EG
																													// Level
																													// 2
																													// ...........(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 108] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 132])); // Pitch
																													// EG
																													// Level
																													// 3
																													// ...........(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 109] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 133])); // Pitch
																													// EG
																													// Level
																													// 4
																													// ...........(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 110] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 134])); // Algorithmic
																													// Select
																													// .........(0-31)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 111] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 136] * 8 + ((PatchDataImpl) p).getSysex()[6 + 135])); // Oscillator
																																							// Sync
																																							// .............(0-1)|
																																							// Feedback
																																							// (0-7)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 112] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 137])); // LFO
																													// Speed
																													// ..................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 113] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 138])); // LFO
																													// Delay
																													// ..................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 114] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 139])); // LFO
																													// PMD
																													// ....................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 115] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 140])); // LFO
																													// AMD
																													// ....................(0-99)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 116] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 143] * 16
				+ ((PatchDataImpl) p).getSysex()[6 + 142] * 2 + ((PatchDataImpl) p).getSysex()[6 + 141]));
		// LFO Mod Sensitivity Pitch ...(0-7)
		// | LFO Wave (0-5)| LFO Sync (0-1)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 117] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 144])); // Transpose
																													// ..................(0-48)
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 118] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 145])); // Voice
																													// name
																													// 1
																													// ...............ASCII
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 119] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 146])); // Voice
																													// name
																													// 2
																													// ...............ASCII
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 120] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 147])); // Voice
																													// name
																													// 3
																													// ...............ASCII
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 121] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 148])); // Voice
																													// name
																													// 4
																													// ...............ASCII
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 122] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 149])); // Voice
																													// name
																													// 5
																													// ...............ASCII
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 123] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 150])); // Voice
																													// name
																													// 6
																													// ...............ASCII
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 124] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 151])); // Voice
																													// name
																													// 7
																													// ...............ASCII
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 125] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 152])); // Voice
																													// name
																													// 8
																													// ...............ASCII
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 126] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 153])); // Voice
																													// name
																													// 9
																													// ...............ASCII
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 127] = (byte) ((((PatchDataImpl) p).getSysex()[6 + 154])); // Voice
																													// name
																													// 10
																													// ...............ASCII

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
			sysex[3] = (byte) 0x00;
			sysex[4] = (byte) 0x01;
			sysex[5] = (byte) 0x1B;

			// ***** OPERATOR 6 *****
			sysex[6 + 0] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 0])); // EG Rate 1
																								// .................(0-99)
			sysex[6 + 1] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 1])); // EG Rate 2
																								// .................(0-99)
			sysex[6 + 2] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 2])); // EG Rate 3
																								// .................(0-99)
			sysex[6 + 3] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 3])); // EG Rate 4
																								// .................(0-99)
			sysex[6 + 4] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 4])); // EG Level 1
																								// .................(0-99)
			sysex[6 + 5] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 5])); // EG Level 2
																								// .................(0-99)
			sysex[6 + 6] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 6])); // EG Level 3
																								// .................(0-99)
			sysex[6 + 7] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 7])); // EG Level 4
																								// .................(0-99)
			sysex[6 + 8] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 8])); // Kbd Level Scale Break
																								// Point (0-99)
			sysex[6 + 9] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 9])); // Kbd Level Scale Left
																								// Depth .(0-99)
			sysex[6 + 10] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 10])); // Kbd Level Scale
																									// Right Depth
																									// (0-99)
			sysex[6 + 11] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 11] & 3)); // Kbd Level Scale
																										// Left Curve
																										// ..(0-3)
			sysex[6 + 12] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 11] & 12) / 4); // Kbd Level
																											// Scale
																											// Right
																											// Curve
																											// .(0-3)
			sysex[6 + 13] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 12] & 7)); // Kbd Rate
																										// Scaling
																										// ............(0-7)
			sysex[6 + 14] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 13] & 3)); // Mod Sensitivity
																										// Amplitude
																										// ...(0-3)
			sysex[6 + 15] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 13] & 28) / 4); // Key
																											// Velocity
																											// Sensitivity
																											// ....(0-7)
			sysex[6 + 16] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 14])); // Operator Output
																									// Level
																									// ......(0-99)
			sysex[6 + 17] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 15] & 1)); // Osc Mode
																										// ....................(0-1)
			sysex[6 + 18] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 15] & 62) / 2); // Osc
																											// Frequency
																											// Coarse
																											// .......(0-31)
			sysex[6 + 19] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 16])); // Osc Frequency Fine
																									// .........(0-99)
			sysex[6 + 20] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 12] & 120) / 8); // Osc
																											// Detune
																											// .................(0-14)

			// ***** OPERATOR 5 *****
			sysex[6 + 21] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 17])); // EG Rate 1
																									// .................(0-99)
			sysex[6 + 22] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 18])); // EG Rate 2
																									// .................(0-99)
			sysex[6 + 23] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 19])); // EG Rate 3
																									// .................(0-99)
			sysex[6 + 24] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 20])); // EG Rate 4
																									// .................(0-99)
			sysex[6 + 25] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 21])); // EG Level 1
																									// .................(0-99)
			sysex[6 + 26] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 22])); // EG Level 2
																									// .................(0-99)
			sysex[6 + 27] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 23])); // EG Level 3
																									// .................(0-99)
			sysex[6 + 28] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 24])); // EG Level 4
																									// .................(0-99)
			sysex[6 + 29] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 25])); // Kbd Level Scale
																									// Break Point
																									// (0-99)
			sysex[6 + 30] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 26])); // Kbd Level Scale
																									// Left Depth
																									// .(0-99)
			sysex[6 + 31] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 27])); // Kbd Level Scale
																									// Right Depth
																									// (0-99)
			sysex[6 + 32] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 28] & 3)); // Kbd Level Scale
																										// Left Curve
																										// ..(0-3)
			sysex[6 + 33] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 28] & 12) / 4); // Kbd Level
																											// Scale
																											// Right
																											// Curve
																											// .(0-3)
			sysex[6 + 34] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 29] & 7)); // Kbd Rate
																										// Scaling
																										// ............(0-7)
			sysex[6 + 35] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 30] & 3)); // Mod Sensitivity
																										// Amplitude
																										// ...(0-3)
			sysex[6 + 36] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 30] & 28) / 4); // Key
																											// Velocity
																											// Sensitivity
																											// ....(0-7)
			sysex[6 + 37] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 31])); // Operator Output
																									// Level
																									// ......(0-99)
			sysex[6 + 38] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 32] & 1)); // Osc Mode
																										// ....................(0-1)
			sysex[6 + 39] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 32] & 62) / 2); // Osc
																											// Frequency
																											// Coarse
																											// .......(0-31)
			sysex[6 + 40] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 33])); // Osc Frequency Fine
																									// .........(0-99)
			sysex[6 + 41] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 29] & 120) / 8); // Osc
																											// Detune
																											// .................(0-14)

			// ***** OPERATOR 4 *****
			sysex[6 + 42] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 34])); // EG Rate 1
																									// .................(0-99)
			sysex[6 + 43] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 35])); // EG Rate 2
																									// .................(0-99)
			sysex[6 + 44] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 36])); // EG Rate 3
																									// .................(0-99)
			sysex[6 + 45] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 37])); // EG Rate 4
																									// .................(0-99)
			sysex[6 + 46] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 38])); // EG Level 1
																									// .................(0-99)
			sysex[6 + 47] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 39])); // EG Level 2
																									// .................(0-99)
			sysex[6 + 48] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 40])); // EG Level 3
																									// .................(0-99)
			sysex[6 + 49] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 41])); // EG Level 4
																									// .................(0-99)
			sysex[6 + 50] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 42])); // Kbd Level Scale
																									// Break Point
																									// (0-99)
			sysex[6 + 51] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 43])); // Kbd Level Scale
																									// Left Depth
																									// .(0-99)
			sysex[6 + 52] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 44])); // Kbd Level Scale
																									// Right Depth
																									// (0-99)
			sysex[6 + 53] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 45] & 3)); // Kbd Level Scale
																										// Left Curve
																										// ..(0-3)
			sysex[6 + 54] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 45] & 12) / 4); // Kbd Level
																											// Scale
																											// Right
																											// Curve
																											// .(0-3)
			sysex[6 + 55] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 46] & 7)); // Kbd Rate
																										// Scaling
																										// ............(0-7)
			sysex[6 + 56] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 47] & 3)); // Mod Sensitivity
																										// Amplitude
																										// ...(0-3)
			sysex[6 + 57] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 47] & 28) / 4); // Key
																											// Velocity
																											// Sensitivity
																											// ....(0-7)
			sysex[6 + 58] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 48])); // Operator Output
																									// Level
																									// ......(0-99)
			sysex[6 + 59] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 49] & 1)); // Osc Mode
																										// ....................(0-1)
			sysex[6 + 60] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 49] & 62) / 2); // Osc
																											// Frequency
																											// Coarse
																											// .......(0-31)
			sysex[6 + 61] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 50])); // Osc Frequency Fine
																									// .........(0-99)
			sysex[6 + 62] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 46] & 120) / 8); // Osc
																											// Detune
																											// .................(0-14)

			// ***** OPERATOR 3 *****
			sysex[6 + 63] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 51])); // EG Rate 1
																									// .................(0-99)
			sysex[6 + 64] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 52])); // EG Rate 2
																									// .................(0-99)
			sysex[6 + 65] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 53])); // EG Rate 3
																									// .................(0-99)
			sysex[6 + 66] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 54])); // EG Rate 4
																									// .................(0-99)
			sysex[6 + 67] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 55])); // EG Level 1
																									// .................(0-99)
			sysex[6 + 68] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 56])); // EG Level 2
																									// .................(0-99)
			sysex[6 + 69] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 57])); // EG Level 3
																									// .................(0-99)
			sysex[6 + 70] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 58])); // EG Level 4
																									// .................(0-99)
			sysex[6 + 71] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 59])); // Kbd Level Scale
																									// Break Point
																									// (0-99)
			sysex[6 + 72] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 60])); // Kbd Level Scale
																									// Left Depth
																									// .(0-99)
			sysex[6 + 73] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 61])); // Kbd Level Scale
																									// Right Depth
																									// (0-99)
			sysex[6 + 74] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 62] & 3)); // Kbd Level Scale
																										// Left Curve
																										// ..(0-3)
			sysex[6 + 75] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 62] & 12) / 4); // Kbd Level
																											// Scale
																											// Right
																											// Curve
																											// .(0-3)
			sysex[6 + 76] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 63] & 7)); // Kbd Rate
																										// Scaling
																										// ............(0-7)
			sysex[6 + 77] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 64] & 3)); // Mod Sensitivity
																										// Amplitude
																										// ...(0-3)
			sysex[6 + 78] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 64] & 28) / 4); // Key
																											// Velocity
																											// Sensitivity
																											// ....(0-7)
			sysex[6 + 79] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 65])); // Operator Output
																									// Level
																									// ......(0-99)
			sysex[6 + 80] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 66] & 1)); // Osc Mode
																										// ....................(0-1)
			sysex[6 + 81] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 66] & 62) / 2); // Osc
																											// Frequency
																											// Coarse
																											// .......(0-31)
			sysex[6 + 82] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 67])); // Osc Frequency Fine
																									// .........(0-99)
			sysex[6 + 83] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 63] & 120) / 8); // Osc
																											// Detune
																											// .................(0-14)

			// ***** OPERATOR 2 *****
			sysex[6 + 84] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 68])); // EG Rate 1
																									// .................(0-99)
			sysex[6 + 85] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 69])); // EG Rate 2
																									// .................(0-99)
			sysex[6 + 86] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 70])); // EG Rate 3
																									// .................(0-99)
			sysex[6 + 87] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 71])); // EG Rate 4
																									// .................(0-99)
			sysex[6 + 88] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 72])); // EG Level 1
																									// .................(0-99)
			sysex[6 + 89] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 73])); // EG Level 2
																									// .................(0-99)
			sysex[6 + 90] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 74])); // EG Level 3
																									// .................(0-99)
			sysex[6 + 91] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 75])); // EG Level 4
																									// .................(0-99)
			sysex[6 + 92] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 76])); // Kbd Level Scale
																									// Break Point
																									// (0-99)
			sysex[6 + 93] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 77])); // Kbd Level Scale
																									// Left Depth
																									// .(0-99)
			sysex[6 + 94] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 78])); // Kbd Level Scale
																									// Right Depth
																									// (0-99)
			sysex[6 + 95] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 79] & 3)); // Kbd Level Scale
																										// Left Curve
																										// ..(0-3)
			sysex[6 + 96] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 79] & 12) / 4); // Kbd Level
																											// Scale
																											// Right
																											// Curve
																											// .(0-3)
			sysex[6 + 97] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 80] & 7)); // Kbd Rate
																										// Scaling
																										// ............(0-7)
			sysex[6 + 98] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 81] & 3)); // Mod Sensitivity
																										// Amplitude
																										// ...(0-3)
			sysex[6 + 99] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 81] & 28) / 4); // Key
																											// Velocity
																											// Sensitivity
																											// ....(0-7)
			sysex[6 + 100] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 82])); // Operator Output
																									// Level
																									// ......(0-99)
			sysex[6 + 101] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 83] & 1)); // Osc Mode
																										// ....................(0-1)
			sysex[6 + 102] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 83] & 62) / 2); // Osc
																											// Frequency
																											// Coarse
																											// .......(0-31)
			sysex[6 + 103] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 84])); // Osc Frequency Fine
																									// .........(0-99)
			sysex[6 + 104] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 80] & 120) / 8); // Osc
																												// Detune
																												// .................(0-14)

			// ***** OPERATOR 1 *****
			sysex[6 + 105] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 85])); // EG Rate 1
																									// .................(0-99)
			sysex[6 + 106] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 86])); // EG Rate 2
																									// .................(0-99)
			sysex[6 + 107] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 87])); // EG Rate 3
																									// .................(0-99)
			sysex[6 + 108] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 88])); // EG Rate 4
																									// .................(0-99)
			sysex[6 + 109] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 89])); // EG Level 1
																									// .................(0-99)
			sysex[6 + 110] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 90])); // EG Level 2
																									// .................(0-99)
			sysex[6 + 111] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 91])); // EG Level 3
																									// .................(0-99)
			sysex[6 + 112] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 92])); // EG Level 4
																									// .................(0-99)
			sysex[6 + 113] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 93])); // Kbd Level Scale
																									// Break Point
																									// (0-99)
			sysex[6 + 114] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 94])); // Kbd Level Scale
																									// Left Depth
																									// .(0-99)
			sysex[6 + 115] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 95])); // Kbd Level Scale
																									// Right Depth
																									// (0-99)
			sysex[6 + 116] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 96] & 3)); // Kbd Level
																										// Scale Left
																										// Curve ..(0-3)
			sysex[6 + 117] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 96] & 12) / 4); // Kbd Level
																											// Scale
																											// Right
																											// Curve
																											// .(0-3)
			sysex[6 + 118] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 97] & 7)); // Kbd Rate
																										// Scaling
																										// ............(0-7)
			sysex[6 + 119] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 98] & 3)); // Mod
																										// Sensitivity
																										// Amplitude
																										// ...(0-3)
			sysex[6 + 120] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 98] & 28) / 4); // Key
																											// Velocity
																											// Sensitivity
																											// ....(0-7)
			sysex[6 + 121] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 99])); // Operator Output
																									// Level
																									// ......(0-99)
			sysex[6 + 122] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 100] & 1)); // Osc Mode
																										// ....................(0-1)
			sysex[6 + 123] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 100] & 62) / 2); // Osc
																												// Frequency
																												// Coarse
																												// .......(0-31)
			sysex[6 + 124] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 101])); // Osc Frequency
																									// Fine
																									// .........(0-99)
			sysex[6 + 125] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 97] & 120) / 8); // Osc
																												// Detune
																												// .................(0-14)

			// ***** other Parameters *****
			sysex[6 + 126] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 102])); // Pitch EG Rate 1
																									// ............(0-99)
			sysex[6 + 127] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 103])); // Pitch EG Rate 2
																									// ............(0-99)
			sysex[6 + 128] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 104])); // Pitch EG Rate 3
																									// ............(0-99)
			sysex[6 + 129] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 105])); // Pitch EG Rate 4
																									// ............(0-99)
			sysex[6 + 130] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 106])); // Pitch EG Level 1
																									// ...........(0-99)
			sysex[6 + 131] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 107])); // Pitch EG Level 2
																									// ...........(0-99)
			sysex[6 + 132] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 108])); // Pitch EG Level 3
																									// ...........(0-99)
			sysex[6 + 133] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 109])); // Pitch EG Level 4
																									// ...........(0-99)
			sysex[6 + 134] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 110] & 31)); // Algorithmic
																											// Select
																											// .........(0-31)
			sysex[6 + 135] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 111] & 7)); // Feedback
																										// ....................(0-7)
			sysex[6 + 136] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 111] & 8) / 8); // Oscillator
																											// Sync
																											// .............(0-1)
			sysex[6 + 137] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 112])); // LFO Speed
																									// ..................(0-99)
			sysex[6 + 138] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 113])); // LFO Delay
																									// ..................(0-99)
			sysex[6 + 139] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 114])); // LFO PMD
																									// ....................(0-99)
			sysex[6 + 140] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 115])); // LFO AMD
																									// ....................(0-99)
			sysex[6 + 141] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 116] & 1)); // LFO Sync
																										// ....................(0-1)
			sysex[6 + 142] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 116] & 14) / 2); // LFO Wave
																												// ....................(0-5)
			sysex[6 + 143] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 116] & 112) / 16); // LFO
																												// Mod
																												// Sensitivity
																												// Pitch
																												// ...(0-7)
			sysex[6 + 144] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 117])); // Transpose
																									// .................
																									// (0-48)
			sysex[6 + 145] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 118])); // Voice name 1
																									// ..............
																									// ASCII
			sysex[6 + 146] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 119])); // Voice name 2
																									// ..............
																									// ASCII
			sysex[6 + 147] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 120])); // Voice name 3
																									// ..............
																									// ASCII
			sysex[6 + 148] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 121])); // Voice name 4
																									// ..............
																									// ASCII
			sysex[6 + 149] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 122])); // Voice name 5
																									// ..............
																									// ASCII
			sysex[6 + 150] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 123])); // Voice name 6
																									// ..............
																									// ASCII
			sysex[6 + 151] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 124])); // Voice name 7
																									// ..............
																									// ASCII
			sysex[6 + 152] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 125])); // Voice name 8
																									// ..............
																									// ASCII
			sysex[6 + 153] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 126])); // Voice name 9
																									// ..............
																									// ASCII
			sysex[6 + 154] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 127])); // Voice name 10
																									// ..............
																									// ASCII

			sysex[singleSize - 1] = (byte) 0xF7;

			PatchDataImpl p = new PatchDataImpl(sysex, getDevice()); // single sysex
			p.calculateChecksum();

			return p;
		} catch (Exception e) {
			ErrorMsgUtil.reportError(getManufacturerName() + " " + getModelName(), "Error in " + toString(), e);
			return null;
		}
	}

	public PatchDataImpl createNewPatch() // create a bank with 32 "init voice"-patches
	{
		byte[] sysex = new byte[trimSize];

		sysex[00] = (byte) 0xF0;
		sysex[01] = (byte) 0x43;
		sysex[02] = (byte) 0x00;
		sysex[03] = (byte) 0x09;
		sysex[04] = (byte) 0x20;
		sysex[05] = (byte) 0x00;
		sysex[trimSize - 1] = (byte) 0xF7;

		PatchDataImpl v = new PatchDataImpl(initSysex, getDevice()); // single sysex
		PatchDataImpl p = new PatchDataImpl(sysex, this); // bank sysex

		for (int i = 0; i < getNumPatches(); i++)
			putPatch(p, v, i);

		calculateChecksum(p);

		return p;
	}

}
