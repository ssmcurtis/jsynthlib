package org.jsynthlib.synthdrivers.roland.mks80;

import java.io.InputStream;

import org.jsynthlib.synthdrivers.waldorf.microwave.Microwave;
import org.jsynthlib.tools.DriverUtil;
import org.jsynthlib.tools.HexaUtil;

public class Mks80 {

	public static final String VENDOR = "Roland";
	public static final String DEVICE = "MKS 80";

	public static final int HEADER_SIZE = 5;
	public static final int FOOTER_SIZE = 2;
	// public static final int PROGRAM_SIZE_SYSEX = 135;
	// public static final int PROGRAM_SIZE = PROGRAM_SIZE_SYSEX - HEADER_SIZE - FOOTER_SIZE;

	public static final int BANK_COUNT = 8;
	public static final int PATCH_COUNT_IN_BANK = 8;
	public static final int PROGRAM_COUNT_IN_BANK = 64;

	public static final int TONE_SIZE_IN_BANK = 39;
	public static final int TONE_SIZE_IN_BANK_SYSEX_CONTAINER = HEADER_SIZE + TONE_SIZE_IN_BANK + FOOTER_SIZE;

	public static final int PATCH_SIZE_IN_BANK = 23;
	public static final int PATCH_SIZE_IN_BANK_SYSEX_CONTAINER = HEADER_SIZE + PATCH_SIZE_IN_BANK + FOOTER_SIZE;

	public static final int DATA_PACKAGE_IN_BANK = TONE_SIZE_IN_BANK + PATCH_SIZE_IN_BANK;

	public static final int BANK_DATA_PACKAGE_COUNT = 16;
	public static final int BANK_DATA_PACKAGE_SIZE = 255;
	public static final int BANK_DATA_PROGRAM_DATA_SIZE = BANK_DATA_PACKAGE_SIZE - HEADER_SIZE - FOOTER_SIZE;

	// or 128 includes card ?
	public static final int PROGRAM_COUNT_IN_SYNTH = PROGRAM_COUNT_IN_BANK;

	public static final int BANK_SIZE_SYSEX_64 = (HEADER_SIZE + (DATA_PACKAGE_IN_BANK * 4) + FOOTER_SIZE) * 16;
	public static final int BANK_SIZE_SYSEX_AND_EOF = BANK_SIZE_SYSEX_64 + 6;

	// public static final int BANK_SIZE_SYSEX_128 = BANK_SIZE_SYSEX_64 * 2;

	public static final String[] BANK_NAMES_PATCHES = new String[] { "All" };

	public static final String DEVICE_SYSEX_ID = "F041";

	public static final String WSF = "F0 41 40 *midiChannel* 20 4D 4B 53 2D 38 30 00 F7";
	public static final String RQF = "F0 41 41 *midiChannel* 20 4D 4B 53 2D 38 30 00 F7";
	public static final String ACK = "F0 41 43 *midiChannel* 20 F7";
	public static final String EOF = "F0 41 45 *midiChannel* 20 F7";

	public static final String DEFAULT_PATCH_STRING = "033C360000000F0F310048032033030205050000123237";
	public static final String DEFAULT_TONE_STRING = "32002A5D0000000000180D32006421003B00002E00000000392B4E3000294A522F0707060F0E04";

	// private

	public static String[] createProgrammNumbers() {
		String[] retarr = new String[PROGRAM_COUNT_IN_SYNTH];
		for (int bank = 0; bank < BANK_COUNT; bank++) {
			String[] names = DriverUtil.generateNumbers(1, BANK_COUNT, (bank + 1) + "/#0");
			System.arraycopy(names, 0, retarr, bank * BANK_COUNT, BANK_COUNT);
		}

		// 65..128
		// for (int bank = 0; bank < BANK_COUNT; bank++) {
		// String[] names = DriverUtil.generateNumbers(1, BANK_COUNT, (bank + 1) + "/#0");
		// System.arraycopy(names, 0, retarr, (bank + 8) * BANK_COUNT, BANK_COUNT);
		// }

		return retarr;
	}

	public static void wrapSingleTone(byte[] sysex) {

		if (sysex.length == TONE_SIZE_IN_BANK_SYSEX_CONTAINER) {
			sysex[0] = (byte) 0xF0;
			sysex[1] = (byte) 0x41;
			sysex[2] = (byte) 0x99;
			sysex[3] = (byte) 0x00;
			sysex[4] = (byte) 0x99;

			sysex[TONE_SIZE_IN_BANK_SYSEX_CONTAINER - 2] = (byte) 0x00;
			sysex[TONE_SIZE_IN_BANK_SYSEX_CONTAINER - 1] = (byte) 0xF7;
		} else {
			System.out.println(">>>> WRAP ERROR");
		}
	}

	public static void wrapSinglePatch(byte[] sysex) {

		if (sysex.length == PATCH_SIZE_IN_BANK_SYSEX_CONTAINER) {
			sysex[0] = (byte) 0xF0;
			sysex[1] = (byte) 0x41;
			sysex[2] = (byte) 0x99;
			sysex[3] = (byte) 0x00;
			sysex[4] = (byte) 0x99;

			sysex[PATCH_SIZE_IN_BANK_SYSEX_CONTAINER - 2] = (byte) 0x00;
			sysex[PATCH_SIZE_IN_BANK_SYSEX_CONTAINER - 1] = (byte) 0xF7;
		} else {
			System.out.println(">>>> WRAP ERROR");
		}
	}

	public static byte[] getDefaultSinglePatch(int lower, int upper) {
		byte[] data = HexaUtil.convertStringToSyex(DEFAULT_PATCH_STRING);
		data[3] = HexaUtil.intToByte(upper);
		data[4] = HexaUtil.intToByte(lower);
		return data;
	}

	public static byte[] getDefaultSingleTone() {
		byte[] data = HexaUtil.convertStringToSyex(DEFAULT_TONE_STRING);
		return data;
	}

	public static byte[] getDefaultBankPatch() {
		byte[] data = HexaUtil.convertStringToSyex(DEFAULT_TONE_STRING + DEFAULT_PATCH_STRING);
		return data;
	}

	public static Integer[] getBankNumbers() {
		Integer[] number = new Integer[BANK_COUNT];
		for (int i = 1; i <= BANK_COUNT; i++) {
			number[i - 1] = i;
		}
		return number;
	}

	public static Integer[] getPatchNumbersInBank() {
		Integer[] number = new Integer[PATCH_COUNT_IN_BANK];
		for (int i = 1; i <= PATCH_COUNT_IN_BANK; i++) {
			number[i - 1] = i;
		}
		return number;
	}

	public static Integer[] getToneNumbers() {
		Integer[] number = new Integer[PROGRAM_COUNT_IN_BANK];
		for (int i = 1; i <= PROGRAM_COUNT_IN_BANK; i++) {
			number[i - 1] = i;
		}
		return number;
	}

	public static Integer getBankNumber(int bi) {
		Integer i1 = (bi / 8) + 1;
		return i1;
	}

	public static Integer getPatchnumber(int bi) {
		Integer i1 = (bi % 8) + 1;
		return i1;
	}

	public static String byteToBankPatchNumber(byte b) {
		return getBankNumber(b).toString() + getPatchnumber(b).toString();
	}

}
