/*
 * JSynthlib -	generic "PerformanceIII" Bank Driver for DX7 Family
 *		(used by TX802)
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

import java.io.UnsupportedEncodingException;

import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

public class DX7FamilyPerformanceIIIBankDriver extends SynthDriverBank {
	byte[] initSysex;
	String[] dxPatchNumbers;
	String[] dxBankNumbers;

	private static final int dxPatchNameSize = 20; // size of patchname of single patch == patchNameSize/2
	private static final int dxPatchNameOffset = 12 + (2 * 64); // offset in packed bank format
	private static final int dxSinglePackedSize = 3 + 10 + (2 * 84); // size of single patch in packed bank format
	private static final int dxSysexHeaderSize = 4; // length of sysex header

	public DX7FamilyPerformanceIIIBankDriver(byte[] initSysex, String[] dxPatchNumbers, String[] dxBankNumbers) {
		super("Performance Bank", "Torsten Tittmann", dxPatchNumbers.length, 4);

		this.initSysex = initSysex;
		this.dxPatchNumbers = dxPatchNumbers;
		this.dxBankNumbers = dxBankNumbers;

		sysexID = "F0430*7E01284C4D202038393532504d";
		sysexRequestDump = new SysexHandler("F0 43 @@ 7E 4C 4D 20 20 38 39 35 32 50 4D F7");
		deviceIDoffset = 2;
		patchNameStart = 0;
		patchNameSize = 0;
		bankNumbers = dxBankNumbers;
		patchNumbers = dxPatchNumbers;
		singleSysexID = "F0430*7E01684C4D2020383935325045";
		singleSize = 250;
		// checksumOffset=11587; // This patch doesn't uses an over-all checksum for bank bulk data
		// checksumStart=6;
		// checksumEnd=11586;
		numSysexMsgs = 1;
		patchSize = 11589;
		trimSize = patchSize;
	}

	public void calculateChecksum(PatchDataImpl p) {
		// This patch doesn't uses an over-all checksum for bank bulk data
	}

	public int getPatchStart(int patchNum) {
		return (dxSinglePackedSize * patchNum) + dxSysexHeaderSize;
	}

	public int getPatchNameStart(int patchNum) {
		return getPatchStart(patchNum) + dxPatchNameOffset;
	}

	public String getPatchName(PatchDataImpl p, int patchNum) {
		int patchNameStart = getPatchNameStart(patchNum);

		try {
			byte[] b = new byte[dxPatchNameSize];

			for (int i = 0; i < b.length; i++) {
				b[i] = (byte) (DX7FamilyByteEncoding.AsciiHex2Value(((PatchDataImpl) p).getSysex()[patchNameStart + (2 * i)]) * 16 + DX7FamilyByteEncoding
						.AsciiHex2Value(((PatchDataImpl) p).getSysex()[patchNameStart + (2 * i) + 1]));
			}

			StringBuffer s = new StringBuffer(new String(b, 0, dxPatchNameSize, "US-ASCII"));

			return s.toString();
		} catch (Exception ex) {
			return "-";
		}
	}

	public void setPatchName(PatchDataImpl p, int patchNum, String name) {
		int patchNameStart = getPatchNameStart(patchNum);

		while (name.length() < dxPatchNameSize)
			name = name + " ";

		byte[] namebytes = new byte[dxPatchNameSize];

		try {
			namebytes = name.getBytes("US-ASCII");

			for (int i = 0; i < dxPatchNameSize; i++) {
				((PatchDataImpl) p).getSysex()[patchNameStart + (2 * i)] = (byte) (DX7FamilyByteEncoding
						.Value2AsciiHexHigh(namebytes[i]));
				((PatchDataImpl) p).getSysex()[patchNameStart + (2 * i) + 1] = (byte) (DX7FamilyByteEncoding
						.Value2AsciiHexLow(namebytes[i]));
			}

		} catch (UnsupportedEncodingException ex) {
			return;
		}
	}

	// returns the byte value of the ASCII Hex encoded high and low nibble
	public int getByte(PatchDataImpl p, int index) {
		return (DX7FamilyByteEncoding.AsciiHex2Value(p.getSysex()[index]) * 16 + DX7FamilyByteEncoding
				.AsciiHex2Value(p.getSysex()[index + 1]));
	}

	public void putPatch(PatchDataImpl bank, PatchDataImpl ip, int patchNum) // puts a patch into the bank, converting it
																			// as needed
	{
		PatchDataImpl p = (PatchDataImpl) ip;
		if (!canHoldPatch(p)) {
			DX7FamilyStrings.dxShowError(toString(), "This type of patch does not fit in to this type of bank.");
			return;
		}

		// Transform Voice Data to Bulk Dump Packed Format
		int value;

		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 0] = (byte) (0x01); // Byte Count MSB
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 1] = (byte) (0x28); // Byte Count LSB
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 2] = (byte) (0x4C); // "L"
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 3] = (byte) (0x4D); // "M"
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 4] = (byte) (0x20); // " "
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 5] = (byte) (0x20); // " "
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 6] = (byte) (0x38); // "8"
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 7] = (byte) (0x39); // "9"
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 8] = (byte) (0x35); // "5"
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 9] = (byte) (0x32); // "2"
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 10] = (byte) (0x50); // "P"
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 11] = (byte) (0x4D); // "M"

		for (int i = 0; i < 8; i++) { // TG1-8 - Voice Channel Offset (0-7) | MIDI Receive Channel (0-16)
			value = getByte(p, 16 + (2 * 0) + (2 * i)) * 32 + getByte(p, 16 + (2 * 8) + (2 * i));

			((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 12 + (2 * 0) + (2 * i)] = (byte) (DX7FamilyByteEncoding
					.Value2AsciiHexHigh(value));
			((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 12 + (2 * 0) + (2 * i) + 1] = (byte) (DX7FamilyByteEncoding
					.Value2AsciiHexLow(value));
		}

		for (int i = 0; i < 8; i++) { // TG1-8 - Voice Number (0-255)
			value = getByte(p, 16 + (2 * 16) + (2 * i));

			((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 12 + (2 * 8) + (2 * i)] = (byte) (DX7FamilyByteEncoding
					.Value2AsciiHexHigh(value));
			((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 12 + (2 * 8) + (2 * i) + 1] = (byte) (DX7FamilyByteEncoding
					.Value2AsciiHexLow(value));
		}

		for (int i = 0; i < 8; i++) { // TG1-8 - Micro Tuning Table # (0-254)
			value = getByte(p, 16 + (2 * 88) + (2 * i));

			((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 12 + (2 * 16) + (2 * i)] = (byte) (DX7FamilyByteEncoding
					.Value2AsciiHexHigh(value));
			((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 12 + (2 * 16) + (2 * i) + 1] = (byte) (DX7FamilyByteEncoding
					.Value2AsciiHexLow(value));
		}

		for (int i = 0; i < 8; i++) { // TG1-8 - Output Volume (0-99)
			value = getByte(p, 16 + (2 * 32) + (2 * i));

			((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 12 + (2 * 24) + (2 * i)] = (byte) (DX7FamilyByteEncoding
					.Value2AsciiHexHigh(value));
			((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 12 + (2 * 24) + (2 * i) + 1] = (byte) (DX7FamilyByteEncoding
					.Value2AsciiHexLow(value));
		}

		for (int i = 0; i < 8; i++) { // TG1-8 - Detune (0-14) | Key Assign Group (0-1) | Output Assign (0-3)
			value = getByte(p, 16 + (2 * 24) + (2 * i)) * 16 + getByte(p, 16 + (2 * 80) + (2 * i)) * 8
					+ getByte(p, 16 + (2 * 40) + (2 * i));

			((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 12 + (2 * 32) + (2 * i)] = (byte) (DX7FamilyByteEncoding
					.Value2AsciiHexHigh(value));
			((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 12 + (2 * 32) + (2 * i) + 1] = (byte) (DX7FamilyByteEncoding
					.Value2AsciiHexLow(value));
		}

		for (int i = 0; i < 8; i++) { // TG1-8 - Note Limit Low (0-127)
			value = getByte(p, 16 + (2 * 48) + (2 * i));

			((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 12 + (2 * 40) + (2 * i)] = (byte) (DX7FamilyByteEncoding
					.Value2AsciiHexHigh(value));
			((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 12 + (2 * 40) + (2 * i) + 1] = (byte) (DX7FamilyByteEncoding
					.Value2AsciiHexLow(value));
		}

		for (int i = 0; i < 8; i++) { // TG1-8 - Note Limit High (0-127)
			value = getByte(p, 16 + (2 * 56) + (2 * i));

			((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 12 + (2 * 48) + (2 * i)] = (byte) (DX7FamilyByteEncoding
					.Value2AsciiHexHigh(value));
			((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 12 + (2 * 48) + (2 * i) + 1] = (byte) (DX7FamilyByteEncoding
					.Value2AsciiHexLow(value));
		}

		for (int i = 0; i < 8; i++) { // TG1-8 - EG forced Damp (0-1) | Note Shift (0-48)
			value = getByte(p, 16 + (2 * 72) + (2 * i)) * 64 + getByte(p, 16 + (2 * 64) + (2 * i));

			((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 12 + (2 * 56) + (2 * i)] = (byte) (DX7FamilyByteEncoding
					.Value2AsciiHexHigh(value));
			((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 12 + (2 * 56) + (2 * i) + 1] = (byte) (DX7FamilyByteEncoding
					.Value2AsciiHexLow(value));
		}

		for (int i = 0; i < 20; i++) { // Performance Name (ASCII)
			value = getByte(p, 16 + (2 * 96) + (2 * i));

			((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 12 + (2 * 64) + (2 * i)] = (byte) (DX7FamilyByteEncoding
					.Value2AsciiHexHigh(value));
			((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 12 + (2 * 64) + (2 * i) + 1] = (byte) (DX7FamilyByteEncoding
					.Value2AsciiHexLow(value));
		}

		// Calculate checkSum of single bulk data
		calculateChecksum(bank, getPatchStart(patchNum) + 2, getPatchStart(patchNum) + 179,
				getPatchStart(patchNum) + 180);
	}

	public PatchDataImpl extractPatch(PatchDataImpl b, int patchNum) // Gets a patch from the bank, converting it as needed
	{
		try {
			PatchDataImpl bank = (PatchDataImpl) b;
			byte[] sysex = new byte[singleSize];
			int value;

			// transform bulk-dump-packed-format to voice data
			sysex[0] = (byte) 0xF0;
			sysex[1] = (byte) 0x43;
			sysex[2] = (byte) 0x00;
			sysex[3] = (byte) 0x7E;
			sysex[4] = (byte) 0x01;
			sysex[5] = (byte) 0x68;
			sysex[6] = (byte) 0x4C; // "L"
			sysex[7] = (byte) 0x4D; // "M"
			sysex[8] = (byte) 0x20; // " "
			sysex[9] = (byte) 0x20; // " "
			sysex[10] = (byte) 0x38; // "8"
			sysex[11] = (byte) 0x39; // "9"
			sysex[12] = (byte) 0x35; // "5"
			sysex[13] = (byte) 0x32; // "2"
			sysex[14] = (byte) 0x50; // "P"
			sysex[15] = (byte) 0x45; // "E"

			sysex[singleSize - 1] = (byte) 0xF7;

			for (int i = 0; i < 8; i++) { // TG1-8 - Voice Channel Offset (0-7)
				value = (getByte(bank, getPatchStart(patchNum) + 12 + (2 * 0) + (2 * i)) & 224) / 32;

				sysex[16 + (2 * 0) + (2 * i)] = (byte) (DX7FamilyByteEncoding.Value2AsciiHexHigh(value));
				sysex[16 + (2 * 0) + (2 * i) + 1] = (byte) (DX7FamilyByteEncoding.Value2AsciiHexLow(value));
			}

			for (int i = 0; i < 8; i++) { // TG1-8 - MIDI Receive Channel (0-16)
				value = (getByte(bank, getPatchStart(patchNum) + 12 + (2 * 0) + (2 * i)) & 31);

				sysex[16 + (2 * 8) + (2 * i)] = (byte) (DX7FamilyByteEncoding.Value2AsciiHexHigh(value));
				sysex[16 + (2 * 8) + (2 * i) + 1] = (byte) (DX7FamilyByteEncoding.Value2AsciiHexLow(value));
			}

			for (int i = 0; i < 8; i++) { // TG1-8 - Voice Number (0-255)
				value = (getByte(bank, getPatchStart(patchNum) + 12 + (2 * 8) + (2 * i)) & 255);

				sysex[16 + (2 * 16) + (2 * i)] = (byte) (DX7FamilyByteEncoding.Value2AsciiHexHigh(value));
				sysex[16 + (2 * 16) + (2 * i) + 1] = (byte) (DX7FamilyByteEncoding.Value2AsciiHexLow(value));
			}

			for (int i = 0; i < 8; i++) { // TG1-8 - Detune (0-14)
				value = (getByte(bank, getPatchStart(patchNum) + 12 + (2 * 32) + (2 * i)) & 112) / 16;

				sysex[16 + (2 * 24) + (2 * i)] = (byte) (DX7FamilyByteEncoding.Value2AsciiHexHigh(value));
				sysex[16 + (2 * 24) + (2 * i) + 1] = (byte) (DX7FamilyByteEncoding.Value2AsciiHexLow(value));
			}

			for (int i = 0; i < 8; i++) { // TG1-8 - Output Volume (0-99)
				value = (getByte(bank, getPatchStart(patchNum) + 12 + (2 * 24) + (2 * i)) & 127);

				sysex[16 + (2 * 32) + (2 * i)] = (byte) (DX7FamilyByteEncoding.Value2AsciiHexHigh(value));
				sysex[16 + (2 * 32) + (2 * i) + 1] = (byte) (DX7FamilyByteEncoding.Value2AsciiHexLow(value));
			}

			for (int i = 0; i < 8; i++) { // TG1-8 - Output Assign (0-3)
				value = (getByte(bank, getPatchStart(patchNum) + 12 + (2 * 32) + (2 * i)) & 7);

				sysex[16 + (2 * 40) + (2 * i)] = (byte) (DX7FamilyByteEncoding.Value2AsciiHexHigh(value));
				sysex[16 + (2 * 40) + (2 * i) + 1] = (byte) (DX7FamilyByteEncoding.Value2AsciiHexLow(value));
			}

			for (int i = 0; i < 8; i++) { // TG1-8 - Note Limit low (0-127)
				value = (getByte(bank, getPatchStart(patchNum) + 12 + (2 * 40) + (2 * i)) & 127);

				sysex[16 + (2 * 48) + (2 * i)] = (byte) (DX7FamilyByteEncoding.Value2AsciiHexHigh(value));
				sysex[16 + (2 * 48) + (2 * i) + 1] = (byte) (DX7FamilyByteEncoding.Value2AsciiHexLow(value));
			}

			for (int i = 0; i < 8; i++) { // TG1-8 - Note Limit high (0-127)
				value = (getByte(bank, getPatchStart(patchNum) + 12 + (2 * 48) + (2 * i)) & 127);

				sysex[16 + (2 * 56) + (2 * i)] = (byte) (DX7FamilyByteEncoding.Value2AsciiHexHigh(value));
				sysex[16 + (2 * 56) + (2 * i) + 1] = (byte) (DX7FamilyByteEncoding.Value2AsciiHexLow(value));
			}

			for (int i = 0; i < 8; i++) { // TG1-8 - Note Shift (0-48)
				value = (getByte(bank, getPatchStart(patchNum) + 12 + (2 * 56) + (2 * i)) & 63);

				sysex[16 + (2 * 64) + (2 * i)] = (byte) (DX7FamilyByteEncoding.Value2AsciiHexHigh(value));
				sysex[16 + (2 * 64) + (2 * i) + 1] = (byte) (DX7FamilyByteEncoding.Value2AsciiHexLow(value));
			}

			for (int i = 0; i < 8; i++) { // TG1-8 - EG forced damp (0-1)
				value = (getByte(bank, getPatchStart(patchNum) + 12 + (2 * 56) + (2 * i)) & 64) / 64;

				sysex[16 + (2 * 72) + (2 * i)] = (byte) (DX7FamilyByteEncoding.Value2AsciiHexHigh(value));
				sysex[16 + (2 * 72) + (2 * i) + 1] = (byte) (DX7FamilyByteEncoding.Value2AsciiHexLow(value));
			}

			for (int i = 0; i < 8; i++) { // TG1-8 - Key Assign Group (0-1)
				value = (getByte(bank, getPatchStart(patchNum) + 12 + (2 * 32) + (2 * i)) & 8) / 8;

				sysex[16 + (2 * 80) + (2 * i)] = (byte) (DX7FamilyByteEncoding.Value2AsciiHexHigh(value));
				sysex[16 + (2 * 80) + (2 * i) + 1] = (byte) (DX7FamilyByteEncoding.Value2AsciiHexLow(value));
			}

			for (int i = 0; i < 8; i++) { // TG1-8 - Micro Tuning Table # (0-254)
				value = (getByte(bank, getPatchStart(patchNum) + 12 + (2 * 16) + (2 * i)) & 255);

				sysex[16 + (2 * 88) + (2 * i)] = (byte) (DX7FamilyByteEncoding.Value2AsciiHexHigh(value));
				sysex[16 + (2 * 88) + (2 * i) + 1] = (byte) (DX7FamilyByteEncoding.Value2AsciiHexLow(value));
			}

			for (int i = 0; i < 20; i++) { // Performance Name (ASCII)
				value = (getByte(bank, getPatchStart(patchNum) + 12 + (2 * 64) + (2 * i)) & 255);

				sysex[16 + (2 * 96) + (2 * i)] = (byte) (DX7FamilyByteEncoding.Value2AsciiHexHigh(value));
				sysex[16 + (2 * 96) + (2 * i) + 1] = (byte) (DX7FamilyByteEncoding.Value2AsciiHexLow(value));
			}

			PatchDataImpl p = new PatchDataImpl(sysex, getDevice()); // single sysex
			p.calculateChecksum();

			return p;
		} catch (Exception e) {
			ErrorMsgUtil.reportError(getManufacturerName() + " " + getModelName(), "Error in " + toString(), e);
			return null;
		}
	}

	public PatchDataImpl createNewPatch() // create a bank with 64 performance patches
	{
		byte[] sysex = new byte[trimSize];

		sysex[0] = (byte) 0xF0;
		sysex[1] = (byte) 0x43;
		sysex[2] = (byte) 0x00;
		sysex[3] = (byte) 0x7E;

		sysex[trimSize - 1] = (byte) 0xF7;

		PatchDataImpl v = new PatchDataImpl(initSysex, getDevice()); // single sysex
		PatchDataImpl p = new PatchDataImpl(sysex, this); // bank sysex

		for (int i = 0; i < getNumPatches(); i++)
			putPatch(p, v, i);

		return p;
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		send(sysexRequestDump.toSysexMessage(getChannel() + 0x20));
	}
}
