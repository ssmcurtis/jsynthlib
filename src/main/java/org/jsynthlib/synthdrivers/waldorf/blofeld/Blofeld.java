package org.jsynthlib.synthdrivers.waldorf.blofeld;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.jsynthlib.tools.DriverUtil;
import org.jsynthlib.tools.HexaUtil;

import com.sun.xml.internal.messaging.saaj.packaging.mime.Header;

public enum Blofeld {
	PATCH_NAME_START_AT(370),
	PATCH_NAME_LENGTH(16),
	PATCH_CHECKSUM_START_AT(0),
	PATCH_CHECKSUM_END_AT(390),
	PATCH_CHECKSUM_OFFSET(-1),
	PATCH_ID_MESSAGE(4),
	BANK_AT(5),
	PATCH_AT(6);

	private final int position;

	// public byte[] CURRENT_PROGRAM_DATA_HEADER = new byte[]{(byte) 0xF0, 0x42, 0x30, 0x58, 0x40};

	public static final int PATCH_COUNT_IN_BANK = 128;
	public static final int PATCH_COUNT_IN_SYNTH = 128 * 8;

	public static final int HEADER_SIZE = 7;
	public static final int PROGRAM_SIZE_MIDI = 384;
	public static final int PROGRAM_SIZE_MIDI_SYSEX = HEADER_SIZE + PROGRAM_SIZE_MIDI + 1;

	public static final int BANK_SIZE_MIDI = 401400;
	public static final int BANK_SIZE_MIDI_SYSEX = HEADER_SIZE + BANK_SIZE_MIDI + 1;;

	public static final int DEVICE_ID_OFFSET = 0;

	public static final String VENDOR = "Waldorf";
	public static final String DEVICE = "Blofeld";
	public static final String DEVICE_SYSEX_ID = "F03E13";
	public static final String REQUEST_SINGLE = "F0 3E 13 @@ 00 *bankNum* *patchNum* F7";
	public static final String REQUEST_BANK = "F0 3E 13 @@ 00 40 00 F7";

	public static final int TEMPLATE_ADD_TO_BANK_BYTE = 0x00;

	public static final String[] BANK_NAMES = new String[] { "A", "B", "C", "D", "E", "F", "G", "H" };
	public static final String[] BANK_MULTI = new String[] { "MULTI-TIMBRAL"  };
	public static final String[] BANK_NAMES_COMPLETE = new String[] { "COMPLETE" };

	Blofeld(int position) {
		this.position = position;
	}

	public int position() {
		return position;
	}

	public static String[] createPatchNumbers() {
		String[] retarr = new String[PATCH_COUNT_IN_BANK];
		String[] names = DriverUtil.generateNumbers(1, PATCH_COUNT_IN_BANK, "Patch #000");
		System.arraycopy(names, 0, retarr, 0, PATCH_COUNT_IN_BANK);

		return retarr;
	}

	public static String[] createPatchNumbersCompleteSynth() {
		String[] retarr = new String[PATCH_COUNT_IN_SYNTH];
		String[] names = DriverUtil.generateNumbers(1, PATCH_COUNT_IN_SYNTH, "Patch #0000");
		System.arraycopy(names, 0, retarr, 0, PATCH_COUNT_IN_SYNTH);

		return retarr;
	}

	public static String[] createPatchNumbersMultitimbral() {
		String[] retarr = new String[16];
		String[] names = DriverUtil.generateNumbers(1, 16, "Channel #00");
		System.arraycopy(names, 0, retarr, 0, 16);

		return retarr;
	}

}
