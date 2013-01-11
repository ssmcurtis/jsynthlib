package org.jsynthlib.synthdrivers.alesis.andromeda;

import java.nio.ByteBuffer;

import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.DriverUtil;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public enum Andromeda {
	PATCH_NAME_START_AT(2),
	PATCH_NAME_LENGTH(16),
	PATCH_CHECKSUM_OFFSET(-1),
	BANK_AT(6),
	PATCH_AT(7);

	private final int position;

	public static final int PATCH_COUNT_IN_BANK = 128;

	public static final int HEADER_SIZE = 8;

	public static final int PROGRAM_SIZE_7BIT = 2341;
	public static final int PROGRAM_SIZE_7BIT_SYSEX = HEADER_SIZE + PROGRAM_SIZE_7BIT + 1;

	public static final int PROGRAM_SIZE_8BIT = 2048;
	public static final int PROGRAM_SIZE_8BIT_SYSEX = HEADER_SIZE + PROGRAM_SIZE_8BIT + 1;

	public static final int COUNT_56BIT_BUNDLES = PROGRAM_SIZE_8BIT / 7;

	// public static final int BANK_SIZE_7BIT = 0;
	// public static final int BANK_SIZE_7BIT_SYSEX = HEADER_SIZE + BANK_SIZE_7BIT + 1;;
	//
	// public static final int BANK_SIZE_8BIT = 0;
	// public static final int BANK_SIZE_8BIT_SYSEX = HEADER_SIZE + BANK_SIZE_8BIT + 1;;
	//
	// public static final int BANK_ALL_SIZE_7BIT = 0;
	// public static final int BANK_ALL_SIZE_7BIT_SYSEX = HEADER_SIZE + BANK_ALL_SIZE_7BIT + 1;;
	//
	// public static final int BANK_ALL_SIZE_8BIT = 0;
	// public static final int BANK_ALL_SIZE_8BIT_SYSEX = HEADER_SIZE + BANK_ALL_SIZE_8BIT + 1;;

	public static final int DEVICE_ID_OFFSET = 0;

	public static final String VENDOR = "Alesis";
	public static final String DEVICE = "Andromeda A6";
	public static final String DEVICE_SYSEX_ID = "F000000E1D00****";

	public static final String REQUEST_PRG_SINGLE = "F0 00 00 0E 1D 01 *bankNum* *patchNum* F7";
	public static final String REQUEST_PRG_BANK = "F0 00 00 0E 1D 0A *bankNum* F7";
	public static final String REQUEST_MIX_SINGLE = "F0 00 00 0E 1D 05 *bankNum* *patchNum* F7";
	public static final String REQUEST_MIX_BANK = "F0 00 00 0E 1D 0B *bankNum* F7";

	public static final String PROGRAM_DATA_DUMP_HEADER_STRING = "F0 42 *midiChannel* 58 4C";

	public static final String[] BANK_NAMES = new String[] { "User", "Preset1", "Preset2", "Card 1", "Card 2", "Card 3", "Card 4",
			"Card 5", "Card 6", "Card 7", "Card 8" };

	Andromeda(int position) {
		this.position = position;
	}

	public int position() {
		return position;
	}

	public static boolean singlePatchSizeIsSupported(int size) {
		switch (size) {
		case PROGRAM_SIZE_7BIT_SYSEX:
			// case PROGRAM_SIZE_8BIT_SYSEX:
			return true;
		}
		return false;
	}

	public static boolean bankPatchSizeIsSupported(int size) {
		System.out.println(">>> Bank size " + size);
		switch (size) {
		case (PROGRAM_SIZE_7BIT * PATCH_COUNT_IN_BANK):
			// case (PROGRAM_SIZE_8BIT * PATCH_COUNT_IN_BANK):
			return true;
		}
		return false;
	}

	public static String[] createPatchNumbers() {
		String[] retarr = new String[PATCH_COUNT_IN_BANK];
		String[] names = DriverUtil.generateNumbers(1, PATCH_COUNT_IN_BANK, "Patch #000");
		System.arraycopy(names, 0, retarr, 0, PATCH_COUNT_IN_BANK);

		return retarr;
	}

	public static ByteBuffer processDumpDataDecrypt(byte[] source) {
		// 7bit/8byte to 8bit/7Byte

		byte[] target = new byte[PROGRAM_SIZE_8BIT_SYSEX];

		System.arraycopy(source, 0, target, 0, HEADER_SIZE);

		byte[] coreProgram = new byte[PROGRAM_SIZE_7BIT];
		System.arraycopy(source, HEADER_SIZE, coreProgram, 0, PROGRAM_SIZE_7BIT);

		int targetPointer = HEADER_SIZE;
		for (int i = 0; i < COUNT_56BIT_BUNDLES; i++) {
			System.out.println("Start:" + (i * 7) + " End:" + ((i * 7) + 8) + " Max: " + coreProgram.length);

			byte[] b8 = new byte[8];
			System.arraycopy(coreProgram, i * 8, b8, 0, 8);

			byte[] b7 = convert8Bytesto7Bytes(b8);

			System.arraycopy(b7, 0, target, targetPointer, 7);
			targetPointer += 7;
		}
		target[PROGRAM_SIZE_8BIT_SYSEX - 1] = (byte) 0xF7;

		ByteBuffer buffer = ByteBuffer.allocate(PROGRAM_SIZE_8BIT_SYSEX);
		buffer.put(target);

		// System.out.println("Target programm size:  " + target.length);

		return buffer;
	}

	public static ByteBuffer processDumpDataEncrypt(byte[] source) {
		// 8bit/7Byte to 7bit/8byte

		byte[] target = new byte[PROGRAM_SIZE_7BIT_SYSEX];

		System.arraycopy(source, 0, target, 0, HEADER_SIZE);

		byte[] coreProgram = new byte[PROGRAM_SIZE_8BIT];
		System.arraycopy(source, HEADER_SIZE, coreProgram, 0, PROGRAM_SIZE_8BIT);

		int targetPointer = HEADER_SIZE;
		for (int i = 0; i < COUNT_56BIT_BUNDLES; i++) {
			byte[] b7 = new byte[7];
			System.arraycopy(coreProgram, (i * 7), b7, 0, 7);

			byte[] b8 = convert7Bytesto8Bytes(b7);

			System.arraycopy(b8, 0, target, targetPointer, 8);
			targetPointer += 8;
		}
		target[PROGRAM_SIZE_7BIT_SYSEX - 1] = (byte) 0xF7;

		ByteBuffer buffer = ByteBuffer.allocate(PROGRAM_SIZE_7BIT_SYSEX);
		buffer.put(target);

		// System.out.println("Target programm size:  " + target.length);
		return buffer;
	}

	public static byte[] convert8Bytesto7Bytes(byte[] b8) {

		byte[] ret = new byte[7];
		if (b8.length != 8) {
			throw new NotImplementedException();
		}

		byte filter1 = 0x7F;
		byte filter2 = 0x01;

		for (int i = 0; i < 7; i++) {
			filter1 = (byte) ((filter1 >> i) << i);
			filter2 = (byte) (~((filter1 >> (i + 1)) << (i + 1)) & 0x7F);
			ret[i] = (byte) (((b8[i] & filter1) >> i) | (b8[i + 1] & filter2) << 7 - i);
		}
		return ret;

	}

	public static byte[] convert7Bytesto8Bytes(byte[] b7) {
		byte[] ret = new byte[8];
		if (b7.length != 7) {
			throw new NotImplementedException();
		}

		byte filter1 = 0x7F;
		byte filter2 = (byte) 0x80;
		byte filter3 = (byte) 0xFF;

		for (int i = 0; i < 7; i++) {
			byte f1 = (byte) ((filter1 >> i) << i);
			byte f2 = (byte) ((filter2 >> i)); // use shift signed bit ...
			byte f3 = (byte) ~((filter3 << (i + 1)));

			ret[i] = (byte) ((byte) ((((b7[i] << i) & f1)) | ret[i]));

			// >>> does not work (why?) so we use f3 as a workaround
			ret[i + 1] = (byte) (((b7[i] & f2) >>> 7 - i) & f3);

		}
		return ret;
	}

	public static void setPatchname(byte[] sysex, String name) {
		byte nameByte[] = name.getBytes();
		for (int i = 0; i < PATCH_NAME_LENGTH.position(); i++) {
			sysex[PATCH_NAME_START_AT.position() + i] = nameByte[i];
		}

	}

	public static String getPatchname(byte[] sysex) {
		try {
			char c[] = new char[PATCH_NAME_LENGTH.position()];
			for (int i = 0; i < PATCH_NAME_LENGTH.position(); i++) {
				c[i] = (char) (sysex[i + PATCH_NAME_START_AT.position()]);
			}
			return new String(c);
		} catch (Exception ex) {
			return "-";
		}
	}

	public static String getPatchNameLegacy(byte[] sysex) {
		try {
			char c[] = new char[PATCH_NAME_LENGTH.position()];
			for (int i = 0; i < PATCH_NAME_LENGTH.position(); i++)
				c[i] = (char) (getA6PgmByte(sysex, i + PATCH_NAME_START_AT.position()));
			return new String(c);
		} catch (Exception ex) {
			return "-";
		}
	}

	public static void setPatchNameLegacy(byte[] sysex, String name) {
		if (name.length() < PATCH_NAME_LENGTH.position() + 4)
			name = name + "                ";
		byte nameByte[] = name.getBytes();
		for (int i = 0; i < PATCH_NAME_LENGTH.position(); i++) {
			setA6PgmByte(nameByte[i], sysex, i + PATCH_NAME_START_AT.position());
		}
	}

	private static byte getA6PgmByte(byte sysex[], int i) {
		int modulus = i % 7;
		int mask1 = (0xFF << modulus) & 0x7F;
		int mask2 = 0xFF >> (7 - modulus);
		int offset = i * 8 / 7;
		return (byte) (((sysex[8 + offset]) & mask1) >> modulus | ((sysex[9 + offset]) & mask2) << (7 - modulus));
	}

	private static void setA6PgmByte(byte b, byte sysex[], int i) {
		int modulus = i % 7;
		int dstMask1 = (0xFF << modulus) & 0x7F;
		int dstMask2 = 0xFF >> (7 - modulus);
		int srcMask1 = 0xFF >> (modulus + 1);
		int srcMask2 = (~srcMask1) & 0xFF;
		int offset = i * 8 / 7;
		sysex[8 + offset] = (byte) ((sysex[8 + offset] & ~dstMask1) | ((b & srcMask1) << modulus));
		sysex[9 + offset] = (byte) ((sysex[9 + offset] & ~dstMask2) | ((b & srcMask2) >> (7 - modulus)));
	}

}
