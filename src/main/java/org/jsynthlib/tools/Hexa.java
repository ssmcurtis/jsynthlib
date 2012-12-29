package org.jsynthlib.tools;

public class Hexa {

	private static int filter = 0x00ff;

	private static byte sysex_start = (byte) 0xf0;
	private static byte sysex_end = (byte) 0xf7;

	static int byteToInt(byte b) {
		return b & filter;
	}

	static char byteToChar(byte b) {
		return (char) (b & filter);
	}

	static String byteToHexString(byte b) {
		return Integer.toHexString(b & filter).toUpperCase();
	}

	static boolean isStartSysex(byte b) {
		// ? Byte by = new Byte(b);
		return b == sysex_start;
	}

	static boolean isEndSysex(byte b) {
		return b == sysex_end;
	}


}
