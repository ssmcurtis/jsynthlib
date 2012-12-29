package org.jsynthlib.synthdrivers.dsi.evolver;

public enum Evolver{
	PATCH_COUNT_IN_BANK(128),
	PATCH_SIZE(228),
	DEVICE_ID_OFFSET(0),
	PATCH_NAME_START_AT(0),
	PATCH_NAME_END_AT(0),
	CHECKSUM_START_AT(0),
	CHECKSUM_END_AT(0),
	CHECKSUM_OFFSET(0),
	HEADER_SIZE(0),
	BANK_AT(5),
	PATCH_AT(6);

	private final int position;

	public static final String VENDOR = "DSI";
	public static final String DEVICE = "Evolver";
	public static final String DEVICE_SYSEX_ID = "F0012001";
	public static final String REQUEST_SINGLE_PATCH_TEMPLATE = "F0 01 20 01 05 *bankNum* *patchNum* F7";
	public static final String[] BANK_NAMES = new String[] { "Bank 1", "Bank 2", "Bank 3", "Bank 4" };

	Evolver(int position) {
		this.position = position;
	}

	public int number() {
		return position;
	}

}
