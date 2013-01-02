package org.jsynthlib.synthdrivers.dsi.evolver;

import org.jsynthlib.tools.DriverUtil;

public enum Evolver {
	DEVICE_ID_OFFSET(0),
	PATCH_NAME_START_AT(0),
	PATCH_NAME_END_AT(0),
	CHECKSUM_START_AT(0),
	CHECKSUM_END_AT(0),
	CHECKSUM_OFFSET(-1),
	BANK_AT(5),
	PATCH_AT(6);

	private final int position;

	public static final int PATCH_COUNT_IN_BANK = 128;
	public static final int BANK_COUNT = 4;
 	public static final int PATCH_DUMP_SIZE = 228;
 	public static final int BANK_DUMP_SIZE = 29184;
 	public static final int HEADER_SIZE = 6;
	public static final String VENDOR = "DSI";
	public static final String DEVICE = "Evolver";
	public static final String DEVICE_SYSEX_ID = "F0012001******";
	public static final String REQUEST_SINGLE_PATCH_TEMPLATE = "F0 01 20 01 05 *bankNum* *patchNum* F7";
	public static final String[] BANK_NAMES = new String[] { "A", "B", "C", "D" };

	Evolver(int position) {
		this.position = position;
	}

	public int number() {
		return position;
	}

	public static String[] createPatchNumbers() {
		String[] retarr = new String[PATCH_COUNT_IN_BANK];
		String[] names = DriverUtil.generateNumbers(1, PATCH_COUNT_IN_BANK, "#000");
		System.arraycopy(names, 0, retarr, 0, PATCH_COUNT_IN_BANK);

		return retarr;
	}

}
