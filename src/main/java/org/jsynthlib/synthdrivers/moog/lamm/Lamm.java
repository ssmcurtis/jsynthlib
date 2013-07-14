package org.jsynthlib.synthdrivers.moog.lamm;

import org.jsynthlib.tools.DriverUtil;

public class Lamm {

	public static final String DRIVER_INFO = "LA MemoryMoog 2012 - Press GET and initialted dumps from synth. \nSingle: C-7-enter \nAll: C-8-enter";

	public static final int PATCH_NUM_OFFSET = 3; // Include F0, start with 0

	public static final int HEADER_SIZE = 4;
	public static final int PROGRAM_SIZE_SYSEX = 59;
	public static final int PROGRAM_SIZE = PROGRAM_SIZE_SYSEX - HEADER_SIZE - 1;
	public static final int PROGRAM_COUNT_IN_BANK = 101;
	public static final int PROGRAM_COUNT_IN_SYNTH = PROGRAM_COUNT_IN_BANK;

	public static final String[] BANK_NAMES_PATCHES = new String[] { "Bank 1" };

	public static final String VENDOR = "Moog";
	public static final String DEVICE = "LA MemoryMoog";
	public static final String DEVICE_SYSEX_ID = "F00400**";

	public static String[] createProgrammNumbers() {
		String[] retarr = new String[PROGRAM_COUNT_IN_BANK];
		String[] names = DriverUtil.generateNumbers(0, PROGRAM_COUNT_IN_BANK, "Patch #00");
		System.arraycopy(names, 0, retarr, 0, PROGRAM_COUNT_IN_BANK);

		return retarr;
	}

}
