/*
 * Copyright 2005 ssmCurtis, 2005 Joachim Backhaus
 *
 * This file is part of JSynthLib2.
 *
 * JSynthLib2 is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * JSynthLib2 is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package org.jsynthlib.synthdrivers.waldorf.microwave;

import java.io.InputStream;

import org.jsynthlib.tools.DriverUtil;

public class Microwave {
	private static final String[] INTERNAL_BANK_NAMES = new String[] { "A", "B" };

	public static final String VENDOR = "Waldorf";
	public static final String DEVICE = "Microwave 1 (OS 2.0)";

	public static final String REQUEST_SINGLE = "F0 3E 00 *deviceId* 02 00 F7";
	public static final String REQUEST_SINGLE_BANK_BPBR = "F0 3E 00 *deviceid* 10 00 F7";
	public static final String REQUEST_MULTI_BANK_BPBR = "F0 3E 00 *deviceid* 11 00 F7";

	public static final String SYSEX_ID = "F03E00**";

	public static final int HEADER_SIZE = 5;
	public static final int FOOTER_SIZE = 2; // F7, f.e. 2 if one byte for checksum exists
	public static final int PROGRAM_SIZE_SYSEX = 187;
	public static final int PROGRAM_SIZE = PROGRAM_SIZE_SYSEX - HEADER_SIZE - FOOTER_SIZE; 

	public static final int CHECKSUM_AT = Microwave.HEADER_SIZE + Microwave.PROGRAM_SIZE;

	public static final int MULTI_SIZE_SYSEX = 233;
	public static final int BANK_COUNT = 2;

	public static final int PATCH_NAME_SIZE = 16;

	public static final int PATCH_NAME_START = 153; // exclude header
	public static final int PROGRAM_COUNT_IN_BANK = 32;
	public static final int PROGRAM_COUNT_IN_SYNTH = PROGRAM_COUNT_IN_BANK * BANK_COUNT;

	public static final String[] BANK_NAMES = new String[] { "A and B " };

	public static final int BANK_SIZE = (BANK_COUNT * PROGRAM_COUNT_IN_BANK * PROGRAM_SIZE) ;
	public static final int BANK_SIZE_SYSEX = BANK_SIZE + HEADER_SIZE + FOOTER_SIZE;

	public static final int DEVICE_ID_OFFSET = 3;

	public static final String DEFAULT_SINGLE_FILENAME = "microwave1_default_single.syx";
	public static final String DEFAULT_SINGLE_BANK_FILENAME = "microwave1_default_single_bank.syx";
	public static final String DEFAULT_MULTI_FILENAME = "microwave1_default_multi.syx";

	public static String[] createProgrammNumbers() {

		String[] retarr = new String[PROGRAM_COUNT_IN_SYNTH];

		for (int bank = 0; bank < BANK_COUNT; bank++) {
			String[] names = DriverUtil.generateNumbers(1, PROGRAM_COUNT_IN_BANK, INTERNAL_BANK_NAMES[bank], "#00");
			System.arraycopy(names, 0, retarr, bank * PROGRAM_COUNT_IN_BANK, PROGRAM_COUNT_IN_BANK);
		}
		return retarr;
	}

	/**
	 * Wrap a sysex array given as 00 00 00 00 ...PROGRAM... 00 -> F0 XX XX XX ...PROGRAM... F7
	 * 
	 * @param sysex
	 * @param deviceId
	 */

	public static void wrapSingle(byte[] sysex, int deviceId) {

		if (sysex.length == PROGRAM_SIZE_SYSEX) {
			sysex[0] = (byte) 0xF0;
			sysex[1] = (byte) 0x3E;
			sysex[2] = (byte) 0x00;

			// TODO ssmCurtis - Device ID start
			sysex[3] = (byte) deviceId;
			sysex[4] = (byte) 0x42;

			sysex[PROGRAM_SIZE_SYSEX - 1] = (byte) 0xF7;
		} else {
			System.out.println(">>>> WRAP ERROR");
		}
	}

	public static void wrapBank(byte[] sysex, int deviceId) {

		if (sysex.length == BANK_SIZE_SYSEX) {
			sysex[0] = (byte) 0xF0;
			sysex[1] = (byte) 0x3E;
			sysex[2] = (byte) 0x00;

			// TODO ssmCurtis - Device ID start
			sysex[3] = (byte) deviceId;
			sysex[4] = (byte) 0x50;

			sysex[BANK_SIZE_SYSEX - 1] = (byte) 0xF7;
		} else {
			System.out.println(">>>> WRAP ERROR");
		}
	}

	public static byte[] extractSingleExcludeChecksum(byte[] sysex) {

		byte[] program = new byte[PROGRAM_SIZE];

		if (sysex.length == PROGRAM_SIZE_SYSEX) {
			System.arraycopy(sysex, HEADER_SIZE, program, 0, PROGRAM_SIZE);
		} else {
			System.out.println(">>>> EXTRACT ERROR");
		}
		return program;
	}

	public static byte[] getDefaultSinglePatch() {
		byte[] sysex = new byte[Microwave.PROGRAM_SIZE_SYSEX];

		try {

			InputStream sysexFile = Microwave.class.getResourceAsStream(Microwave.DEFAULT_SINGLE_FILENAME);
			
			sysexFile.read(sysex);
			sysexFile.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return sysex;
	}
	
	public static byte[] getDefaultBankPatch() {
		byte[] sysex = new byte[Microwave.BANK_SIZE_SYSEX];

		try {

			InputStream sysexFile = Microwave.class.getResourceAsStream(Microwave.DEFAULT_SINGLE_BANK_FILENAME);
			
			sysexFile.read(sysex);
			sysexFile.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return sysex;
	}

}
