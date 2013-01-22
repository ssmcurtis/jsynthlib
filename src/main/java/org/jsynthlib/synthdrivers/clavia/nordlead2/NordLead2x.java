package org.jsynthlib.synthdrivers.clavia.nordlead2;

import org.jsynthlib.tools.DriverUtil;

public class NordLead2x {
	
	
	public static final int GLOBAL_MIDICHANNEL_OFFSET = 2;
	public static final int BANK_NUM_OFFSET = 4;
	public static final int PATCH_NUM_OFFSET = 5;

	public static final int HEADER_SIZE = 5;
	public static final int PROGRAM_SIZE = 133;
	public static final int PROGRAM_SIZE_SYSEX = HEADER_SIZE + PROGRAM_SIZE + 1;
	public static final int PROGRAM_COUNT_IN_BANK = 99;
	public static final int PROGRAM_COUNT_IN_SYNTH = PROGRAM_COUNT_IN_BANK * 10;

	public static final int PERFORMANCE_SIZE = 709;
	public static final int PERFORMANCE_SIZE_SYSEX = HEADER_SIZE + PERFORMANCE_SIZE + 1;;
	public static final int PERFORMANCES_COUNT_IN_BANK = 100;
	public static final int PERFORMANCES_COUNT_IN_SYNTH = PROGRAM_COUNT_IN_BANK * 4;
	
	public static final String[] BANK_NAMES_PATCHES = new String[] { "User 0", "User 1", "User 2", "User 3", "ROM 4", "ROM 5", "ROM 6",
			"ROM 7", "ROM 8", "ROM 9" };
	public static final String[] BANK_NAMES_PERFORMANCE = new String[] { "User 1", "ROM 2", "ROM 3", "ROM 4" };

	// INFO there is an upperCase function on the way to the user
	private static final String[] PERFORMANCE_PREFIX = new String[] { "A#", "b#", "C#", "d#", "E#", "G#", "F#", "H#", "J#", "L#" };

	public static final String VENDOR = "Nord";
	public static final String DEVICE = "Lead 2x";
	public static final String DEVICE_SYSEX_ID = "F033**04**";

	public static String[] createProgrammNumbers() {
		String[] retarr = new String[PROGRAM_COUNT_IN_BANK];
		String[] names = DriverUtil.generateNumbers(1, PROGRAM_COUNT_IN_BANK, "Patch #00");
		System.arraycopy(names, 0, retarr, 0, PROGRAM_COUNT_IN_BANK);

		return retarr;
	}

	public static String[] createPreformanceNumbers() {
		String[] retarr = new String[PERFORMANCES_COUNT_IN_BANK];
		int i = 0;
		for (String s : PERFORMANCE_PREFIX) {
			System.arraycopy(DriverUtil.generateNumbers(0, 9, s), 0, retarr, i * 10, 10);
			i++;
		}
		return retarr;
	}

}
