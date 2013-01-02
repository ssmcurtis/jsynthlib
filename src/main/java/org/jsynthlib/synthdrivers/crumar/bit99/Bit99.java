package org.jsynthlib.synthdrivers.crumar.bit99;

import org.jsynthlib.tools.DriverUtil;

public enum Bit99 {
	PATCH_COUNT_IN_BANK(75),
	PATCH_SIZE(80),
	DEVICE_ID_OFFSET(0),
	PATCH_NAME_START_AT(0),
	PATCH_NAME_END_AT(0),
	CHECKSUM_START_AT(0),
	CHECKSUM_END_AT(0),
	CHECKSUM_OFFSET(-1),
	HEADER_SIZE(0),
	BANK_AT(0),
	PATCH_AT(4);

	private final int position;

	public static final String VENDOR = "Crumar";
	public static final String DEVICE = "Bit99, Bit01";
	public static final String DEVICE_SYSEX_ID = "F0252007";
	public static final String REQUEST_SINGLE_PATCH_TEMPLATE = "F0 25 20 07 *patchNum* F7";
	public static final String[] BANK_NAMES = new String[] { "Bank 1" };

	Bit99(int position) {
		this.position = position;
	}

	public int number() {
		return position;
	}

	public static String[] createPatchNumbers() {
		String[] retarr = new String[PATCH_COUNT_IN_BANK.number()];
		String[] names = DriverUtil.generateNumbers(1, PATCH_COUNT_IN_BANK.number(), "Patch ##");
		System.arraycopy(names, 0, retarr, 0, PATCH_COUNT_IN_BANK.number());
	
		return retarr;
	}

}
