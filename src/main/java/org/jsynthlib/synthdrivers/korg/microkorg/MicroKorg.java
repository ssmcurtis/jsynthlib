package org.jsynthlib.synthdrivers.korg.microkorg;

import org.jsynthlib.tools.DriverUtil;

public enum MicroKorg {
	PATCH_NAME_START_AT(6),
	PATCH_NAME_LENGTH(11),
	PATCH_CHECKSUM_START_AT(0),
	PATCH_CHECKSUM_END_AT(0),
	PATCH_CHECKSUM_OFFSET(-1),
	BANK_AT(0),
	PATCH_AT(7);

	
	private final int position;

	// public byte[] CURRENT_PROGRAM_DATA_HEADER = new byte[]{(byte) 0xF0, 0x42, 0x30, 0x58, 0x40};
	
	public static final int PATCH_COUNT_IN_BANK = 128;
	public static final int PATCH_DUMP_SIZE = 297;
	public static final int BANK_DUMP_SIZE = 37163;
	public static final int PROGRAM_SIZE = 290;
	public static final int HEADER_SIZE = 0;
	public static final int DEVICE_ID_OFFSET = 0;
	
	public static final String VENDOR = "Korg";
	public static final String DEVICE = "microKorg, MS2000";
	public static final String DEVICE_SYSEX_ID = "F0423*58";
	public static final String REQUEST_SINGLE = "F0 42 ## 58 10 F7";
	public static final String REQUEST_BANK = "F0 42 ## 58 1C F7";
	public static final int TEMPLATE_ADD_TO_BANK_BYTE = 0x30;
	public static final String[] BANK_NAMES = new String[] { "Bank 1" };

	MicroKorg(int position) {
		this.position = position;
	}

	public int position() {
		return position;
	}

	public static String[] createPatchNumbers() {
		String[] retarr = new String[PATCH_COUNT_IN_BANK];
		String[] names = DriverUtil.generateNumbers(1, PATCH_COUNT_IN_BANK, "Patch ##");
		System.arraycopy(names, 0, retarr, 0, PATCH_COUNT_IN_BANK);

		return retarr;
	}

}
