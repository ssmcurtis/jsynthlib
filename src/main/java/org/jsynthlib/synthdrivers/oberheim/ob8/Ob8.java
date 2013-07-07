package org.jsynthlib.synthdrivers.oberheim.ob8;

import org.jsynthlib.tools.DriverUtil;
import org.jsynthlib.tools.HexaUtil;

public class Ob8 {

	private static final String[] INTERNAL_BANK_NAMES = new String[] { "01 A", "02 B", "03 AB", "04 C", "05 AC", "06 BC", "07 ABC", "08 D", "09 AD",
			"10 BD", "11 ABD", "12 CD", "13 ACD", "14 BCD", "15 ABCD" };

	public static final String VENDOR = "Oberheim";
	public static final String DEVICE = "OB 8";

	public static final int HEADER_SIZE = 5;
	public static final int FOOTER_SIZE = 1; // F7, f.e. 2 if one byte for checksum exists
	public static final int PROGRAM_SIZE_SYSEX = 60;
	public static final int PROGRAM_SIZE = PROGRAM_SIZE_SYSEX - HEADER_SIZE - FOOTER_SIZE;
	public static final int PROGRAM_COUNT_IN_BANK = 8;

	public static final int BANK_COUNT = 15;

	// ignore banks of 8 patches
	public static final int PROGRAM_COUNT_IN_SYNTH = PROGRAM_COUNT_IN_BANK * BANK_COUNT;
	public static final int BANK_SIZE_SYSEX = PROGRAM_SIZE_SYSEX * PROGRAM_COUNT_IN_BANK * BANK_COUNT;

	public static final int PATCH_NUM_AT = 4; // F0 is AT position 0

	public static final String[] BANK_NAMES_PATCHES = new String[] { "Bank" };

	public static final String DEVICE_SYSEX_ID = "F01001****";

	public static final String REQUEST_SINGLE_PATCH = "F0 10 01 00 *patchNum* F7";
	public static final byte[] PATCH_DUMP_HEADER = new byte[] { (byte) 0xF0, 0x10, 0x01, 0x01 };

	// public static final String DEFAULT_PROGRAM_STRING = "";
	// public static final String DEFAULT_BANK_STRING = "";

	public static String[] createProgrammNumbers() {
		String[] retarr = new String[PROGRAM_COUNT_IN_SYNTH];
		for (int bank = 0; bank < BANK_COUNT; bank++) {
			String[] names = DriverUtil.generateNumbers(1, PROGRAM_COUNT_IN_BANK, INTERNAL_BANK_NAMES[bank], "-#0");
			System.arraycopy(names, 0, retarr, bank * PROGRAM_COUNT_IN_BANK, PROGRAM_COUNT_IN_BANK);
		}
		return retarr;
	}

}
