package org.jsynthlib.synthdrivers.korg.microkorg;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.jsynthlib.tools.DriverUtil;

public enum MicroKorg {
	PATCH_NAME_START_AT(5),
	PATCH_NAME_LENGTH(11),
	PATCH_CHECKSUM_START_AT(0),
	PATCH_CHECKSUM_END_AT(0),
	PATCH_CHECKSUM_OFFSET(-1),
	BANK_AT(0),
	PATCH_AT(7);

	private final int position;

	// public byte[] CURRENT_PROGRAM_DATA_HEADER = new byte[]{(byte) 0xF0, 0x42, 0x30, 0x58, 0x40};

	public static final int PATCH_COUNT_IN_BANK = 128;

	public static final int HEADER_SIZE = 5;
	public static final int PROGRAM_SIZE_MIDI = 291;
	public static final int PROGRAM_SIZE_COMPRESSED = 254;
	public static final int PROGRAM_SIZE_MIDI_SYSEX = HEADER_SIZE + PROGRAM_SIZE_MIDI + 1;
	public static final int PROGRAM_COMPRESSED_SYSEX = HEADER_SIZE + PROGRAM_SIZE_COMPRESSED + 1;

	public static final int BANK_SIZE_MIDI = 37157;
	public static final int BANK_SIZE_COMPRESSED = 32712;
	public static final int BANK_SIZE_MIDI_SYSEX = HEADER_SIZE + BANK_SIZE_MIDI + 1;;
	public static final int BANK_SIZE_COMPRESSED_SYSEX = HEADER_SIZE + BANK_SIZE_COMPRESSED + 1;;

	public static final int BANK_ALL_SIZE_MIDI = 37386;
	public static final int BANK_ALL_SIZE_COMPRESSED = 32512;
	public static final int BANK_ALL_SIZE_MIDI_SYSEX = HEADER_SIZE + BANK_ALL_SIZE_MIDI + 1;;
	public static final int BANK_ALL_SIZE_COMPRESSED_SYSEX = HEADER_SIZE + BANK_ALL_SIZE_COMPRESSED + 1;;

	public static final int DEVICE_ID_OFFSET = 0;

	public static final String VENDOR = "Korg";
	public static final String DEVICE = "microKorg";
	public static final String DEVICE_SYSEX_ID = "F0423*58";

	public static final String REQUEST_SINGLE = "F0 42 *midiChannel* 58 10 F7";
	public static final String REQUEST_BANK = "F0 42 *midiChannel* 58 1C F7";

	// public static final String PROGRAM_DATA_DUMP_HEADER_STRING = "F0 42 *midiChannel* 58 4C";
	public static final String WRITE_SINGLE = "F0 42 *midiChannel* 58 11 00 *patchNum* F7";
	public static byte[] WRITE_SINGLE_BYTES = new byte[] { (byte) 0xF0, 0x42, 0x30, 0x58, 0x11, 0x00, 0x00, (byte) 0xF7 };
	public static final byte[] PROGRAM_DATA_DUMP_HEADER = new byte[] { (byte) 0xF0, 0x42, 0x30, 0x58, 0x4C };

	public static final int TEMPLATE_ADD_TO_BANK_BYTE = 0x30;

	public static final String[] BANK_NAMES = new String[] { "A" };

	public static String defaultPatch = "526F79616C205061642020200206000040003C004650021E2D00144A0A470078001063030000FF6000404240400100732600004040007F7301010F0A514A4A7F40004A40466E4666235A6564221C0E02330E7547634F4736103843F1010140404040404040404040404040404040000140404040404040404040404040404040000140404040404040404040404040404040FF700A404240450000000000004040007F0000017F0A4040407F4000404000407F0000407F00020A0302460C024003404240434043F1010140404040404040404040404040404040000140404040404040404040404040404040000140404040404040404040404040404040";

	MicroKorg(int position) {
		this.position = position;
	}

	public int position() {
		return position;
	}

	public static boolean singlePatchSizeIsSupported(int size) {
		switch (size) {
		case PROGRAM_SIZE_MIDI_SYSEX:
		case PROGRAM_COMPRESSED_SYSEX:
			return true;
		}
		return false;
	}

	// TODO ADDRESS MS2000 issues - last bytes ...
	public static boolean bankPatchSizeIsSupported(int size) {
		// System.out.println(">>> Bank size " + size);
		switch (size) {
		case PROGRAM_COMPRESSED_SYSEX:
		case BANK_SIZE_MIDI_SYSEX:
		case BANK_SIZE_COMPRESSED_SYSEX:
		case BANK_ALL_SIZE_MIDI_SYSEX:
		case BANK_ALL_SIZE_COMPRESSED_SYSEX:
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

	public static byte getMidiChannelByte(int channel) {
		return (byte) (channel - 1 + TEMPLATE_ADD_TO_BANK_BYTE);
	}

	public static ByteBuffer processDumpDataDecrypt(byte[] source, int dummyBytesToAdd, int coreProgrammSize) {
		// 7bit/8byte to 8bit/7Byte

		byte[] header = new byte[MicroKorg.HEADER_SIZE];
		System.arraycopy(source, 0, header, 0, MicroKorg.HEADER_SIZE);

		// System.out.println("Source: " + source.length);

		byte[] coreProgram = new byte[coreProgrammSize];
		System.arraycopy(source, MicroKorg.HEADER_SIZE, coreProgram, 0, coreProgrammSize);

		List<Byte> li = new ArrayList<Byte>();

		for (int i = 0; i < coreProgram.length; i += 8) {
			if (i + 8 <= coreProgram.length) {
				byte[] sourceSet = new byte[8];
				System.arraycopy(coreProgram, i, sourceSet, 0, 8);

				byte[] targetSet = new byte[7];

				byte firstMidiByte = sourceSet[0];

				if ((0x01 & firstMidiByte) == 0x01) {
					targetSet[0] = (byte) (sourceSet[1] | 0x80);
				} else {
					targetSet[0] = (byte) (sourceSet[1]);
				}
				if ((0x02 & firstMidiByte) == 0x02) {
					targetSet[1] = (byte) (sourceSet[2] | 0x80);
				} else {
					targetSet[1] = (byte) (sourceSet[2]);
				}
				if ((0x04 & firstMidiByte) == 0x04) {
					targetSet[2] = (byte) (sourceSet[3] | 0x80);
				} else {
					targetSet[2] = (byte) (sourceSet[3]);
				}
				if ((0x08 & firstMidiByte) == 0x08) {
					targetSet[3] = (byte) (sourceSet[4] | 0x80);
				} else {
					targetSet[3] = (byte) (sourceSet[4]);
				}
				if ((0x10 & firstMidiByte) == 0x10) {
					targetSet[4] = (byte) (sourceSet[5] | 0x80);
				} else {
					targetSet[4] = (byte) (sourceSet[5]);
				}
				if ((0x20 & firstMidiByte) == 0x20) {
					targetSet[5] = (byte) (sourceSet[6] | 0x80);
				} else {
					targetSet[5] = (byte) (sourceSet[6]);
				}
				if ((0x40 & firstMidiByte) == 0x40) {
					targetSet[6] = (byte) (sourceSet[7] | 0x80);
				} else {
					targetSet[6] = (byte) (sourceSet[7]);
				}

				for (byte b : targetSet) {
					li.add(b);
				}

				// System.out.print((i / 8) + ": ");
				// for (byte b : targetSet) {
				// System.out.print(HexaUtil.byteToBinString(b) + " ");
				// }
				// System.out.println();
				// System.out.print((i / 8) + ": ");
				// for (byte b : sourceSet) {
				// System.out.print(HexaUtil.byteToBinString(b) + " ");
				// }
				// System.out.println("\n");

			} else {
				// System.out.println("Rest: " + (coreProgram.length - i) + " last used Pos. " + i);
			}
		}
		ByteBuffer buffer = ByteBuffer.allocate(li.size() + MicroKorg.HEADER_SIZE + 1 + dummyBytesToAdd);

		byte[] target = new byte[li.size() + dummyBytesToAdd];

		int pointer = 0;
		for (Byte b : li) {
			target[pointer] = b;
			pointer++;
		}

		for (int i = 0; i < dummyBytesToAdd; i++) {
			target[pointer + i] = 0x40;
		}

		// System.out.println("Source programm size : " + coreProgram.length);
		// System.out.println("Target programm size:  " + target.length);

		buffer.put(header);
		buffer.put(target);
		buffer.put((byte) 0xF7);

		// System.out.println("Size source: " + (source.length) + " target: " + buffer.array().length);
		return buffer;
	}

	// TODO ssmCurtis - only for Single Patch
	public static ByteBuffer processDumpDataEncrypt(byte[] source, int channel, int dummyBytesToAdd) {
		// 8bit/7Byte to 7bit/8byte

		byte[] header = new byte[MicroKorg.HEADER_SIZE];
		System.arraycopy(source, 0, header, 0, MicroKorg.HEADER_SIZE);
		header[2] = (byte) (0x30 + (channel - 1));

		byte[] coreProgram = new byte[MicroKorg.PROGRAM_SIZE_COMPRESSED];
		System.arraycopy(source, MicroKorg.HEADER_SIZE, coreProgram, 0, MicroKorg.PROGRAM_SIZE_COMPRESSED);

		List<Byte> li = new ArrayList<Byte>();
		for (int i = 0; i < coreProgram.length; i += 7) {
			if (i + 7 < coreProgram.length) {
				byte[] sourceSet = new byte[7];
				byte[] targetSet = new byte[8];

				System.arraycopy(coreProgram, i, sourceSet, 0, 7);

				byte firstMidiByte = 0x00;
				for (int j = 0; j < 7; j++) {
					firstMidiByte = (byte) (firstMidiByte | (sourceSet[j] & (0x80)) >> 7 - j);
				}

				// firstMidiByte = (byte) (firstMidiByte | (sourceSet[0] & (0x80)) << 0);
				// System.out.println(HexaUtil.byteToBinString(firstMidiByte));
				// firstMidiByte = (byte) (firstMidiByte | (sourceSet[1] & (0x80)));
				// firstMidiByte = (byte) (firstMidiByte | (sourceSet[2] & (0x80)));
				// firstMidiByte = (byte) (firstMidiByte | (sourceSet[3] & (0x80)));
				// firstMidiByte = (byte) (firstMidiByte | (sourceSet[4] & (0x80)));
				// firstMidiByte = (byte) (firstMidiByte | (sourceSet[4] & (0x80)));
				// firstMidiByte = (byte) (firstMidiByte | (sourceSet[6] & (0x80)));

				targetSet[0] = firstMidiByte;
				targetSet[1] = (byte) (sourceSet[0] & (0x7F));
				targetSet[2] = (byte) (sourceSet[1] & (0x7F));
				targetSet[3] = (byte) (sourceSet[2] & (0x7F));
				targetSet[4] = (byte) (sourceSet[3] & (0x7F));
				targetSet[5] = (byte) (sourceSet[4] & (0x7F));
				targetSet[6] = (byte) (sourceSet[5] & (0x7F));
				targetSet[7] = (byte) (sourceSet[6] & (0x7F));

				for (byte b : targetSet) {
					li.add(b);
				}

				// System.out.print((i / 7) + ": ");
				// for (byte b : targetSet) {
				// System.out.print(HexaUtil.byteToBinString(b) + " ");
				// }
				// System.out.println();
				// System.out.print((i / 7) + ": ");
				// for (byte b : sourceSet) {
				// System.out.print(HexaUtil.byteToBinString(b) + " ");
				// }
				// System.out.println("\n");
			} else {
				// System.out.println("Rest: " + (coreProgram.length - i) + " last used Pos. " + i);
			}
		}
		int targetSize = li.size() + dummyBytesToAdd;

		ByteBuffer buffer = ByteBuffer.allocate(MicroKorg.HEADER_SIZE + targetSize + 1);

		byte[] target = new byte[targetSize];
		int pointer = 0;

		for (Byte b : li) {
			target[pointer] = b;
			pointer++;
		}

		for (int i = 0; i < dummyBytesToAdd; i++) {
			target[pointer + i] = 0x40;
		}

		buffer.put(header);

		buffer.put(target);

		buffer.put((byte) 0xF7);

		// System.out.println("Source programm size : " + coreProgram.length);
		// System.out.println("Target programm size:  " + target.length);

		// System.out.println("Size source: " + coreProgram.length + " target: " + buffer.array().length);

		// System.out.println(HexaUtil.hexDumpOneLine(buffer.array(), 0, -1, 297));
		return buffer;
	}

}
