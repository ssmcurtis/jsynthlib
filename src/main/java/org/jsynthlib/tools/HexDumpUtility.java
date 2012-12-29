package org.jsynthlib.tools;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.jsynthlib.example.style.FormatedString;

/**
 * hex utility functions.
 * 
 * @version $Id$
 */
public class HexDumpUtility {

	public static List<FormatedString> hexDumpFormated(byte[] d, int offset, int len, int bytesPerLine, boolean addresses,
			boolean characters) {
		// Is offset beyond the end of array?
		if (offset >= d.length) {
			return Arrays.asList();
		}
		// Is offset+len beyond the end of the array?
		if (offset + len >= d.length || len < 0) {
			len = d.length - offset; // Set len to get remaining bytes
		}
		// If bytes <=0, set it to len so that we get the remaining bytes on one
		// line
		if (bytesPerLine <= 0) {
			bytesPerLine = len;
		}

		// How many digits to use for address?
		int addresslen = 0;
		// Keep adding 2 digits until we can represent the highest number we need
		if (addresses) {
			int maxnumber = 1;
			while (maxnumber < offset + len) {
				addresslen += 2;
				maxnumber *= 100;
			}
		}

		// if (len <= bytesPerLine) {
		// FormatedString[] output=
		// } else {
		// The bytes won't fit. Split them up and call hexDump for each one
		// for (int i = 0; i < len; i += bytesPerLine) {
		// outputString = outputString + hexDump(d, offset + i, bytesPerLine, bytesPerLine, addresslen, characters) +
		// "\n";
		// }
		// }

		// output.setText(outputString);

		return (hexDump(d, offset, len, bytesPerLine, addresslen, characters));
	}

	public static List<FormatedString> hexDump(byte[] d, int offset, int len, int bytesPerLine, int addressLength, boolean characters) {
		List<FormatedString> outputList = new ArrayList<FormatedString>();
		FormatedString item = new FormatedString();
		String output = "";
		if (offset > d.length) {
			ErrorMsg.reportStatus("offset out of bounds of byte array");
		}
		if (offset + len > d.length) {
			len = d.length - offset;
		}

		StringBuffer buf = new StringBuffer();

		if (addressLength > 0) {
			String addressstring = Integer.toHexString(offset);
			while (addressstring.length() < addressLength) {
				addressstring = "0" + addressstring;
			}
			buf.append(addressstring);
			buf.append(" (");
			addressstring = Integer.toString(offset);
			
			while (addressstring.length() < addressLength) {
				addressstring = "0" + addressstring;
			}
			buf.append(addressstring);
			buf.append(") - ");
		}

		// String hexdigits = hexDump(d, offset, len, bytesPerLine, true);
		boolean wantspaces = true;
		int counter = 0;

		int backgoundLength = 0;
		boolean isYellow = false;

		for (int i = 0; i < len; i++) {

			int c = (d[offset + i] & 0xff);
			if (isYellow) {
				backgoundLength--;
			}

			if (Integer.parseInt("F0", 16) == c || Integer.parseInt("F7", 16) == c) {
				backgoundLength = 1;
				item.setText(buf.toString());
				buf = new StringBuffer();
				outputList.add(item);
				item = new FormatedString();

				SimpleAttributeSet attribute = new SimpleAttributeSet();
				StyleConstants.setBackground(attribute, Color.YELLOW);
				item.setAttributeSet(attribute);

				isYellow = true;
			} else if (backgoundLength == 0) {
				item.setText(buf.toString());
				buf = new StringBuffer();
				outputList.add(item);
				item = new FormatedString();
				isYellow = false;
			}

			if (c < 0x10) {
				buf.append("0");
			}
			buf.append(Integer.toHexString(c).toUpperCase());

			if (bytesPerLine > 0 && (i % bytesPerLine == bytesPerLine - 1 && i != len - 1)) {
				if (characters) {
					buf.append("  ");
					for (int j = offset + i - bytesPerLine + 1; j < offset + i + 1; j++) {
						if (j < len) {
							if (d[j] >= 32 && d[j] <= 126) {
								buf.append(new Character((char) d[j]).toString());
							} else {
								buf.append(".");
							}
						}
					}
				}

				buf.append("\n");

				if (addressLength > 0) {
					String addressstring = Integer.toHexString(offset+i+1);
					while (addressstring.length() < addressLength) {
						addressstring = "0" + addressstring;
					}
					buf.append(addressstring);
					buf.append(" (");
					addressstring = Integer.toString(offset+i+1);
					while (addressstring.length() < addressLength) {
						addressstring = "0" + addressstring;
					}
					buf.append(addressstring);
					buf.append(") - ");
				}

			} else if (i != len - 1 && wantspaces) {
				buf.append(" ");
			}
			counter = i;
		}

		item.setText(buf.toString());
		buf = new StringBuffer();
		outputList.add(item);
		item = new FormatedString();

		int bytesInLastLine = counter % bytesPerLine;

		if (characters) {
			for (int i = bytesInLastLine; i + 1 < bytesPerLine; i++) {
				// while (hexdigits.length() < 3 * bytesPerLine - 1) {
				buf.append("   ");
			}
		}

		if (characters) {
			buf.append("  ");
			for (int j = offset + counter - bytesInLastLine; j < offset + counter + 1; j++) {
				if (j < len) {
					if (d[j] >= 32 && d[j] <= 126) {
						buf.append(new Character((char) d[j]).toString());
					} else {
						buf.append(".");
					}
				}
			}
		}
		buf.append("\n");
		output += buf.toString();

		// output += hexdigits;
		item.setText(output);
		outputList.add(item);

		return (outputList);
	}

//	/**
//	 * convert a byte array into a hexa-dump string, with or without spaces between the bytes.
//	 * 
//	 * @param d
//	 *            a <code>byte[]</code> array to be converted.
//	 * @param offset
//	 *            array index from which dump starts.
//	 * @param len
//	 *            number of bytes to be dumped. If -1, dumps to the end of the array.
//	 * @param bytesPerLine
//	 *            number of bytes per line. If equal or less than 0, no newlines are inserted.
//	 * @param wantspaces
//	 *            whether or not to insert spaces between bytes
//	 * @return hexa-dump string.
//	 */
//	public static String hexDump(byte[] d, int offset, int len, int bytesPerLine, boolean wantspaces) {
//		StringBuffer buf = new StringBuffer();
//		if (len == -1 || offset + len > d.length)
//			len = d.length - offset;
//		for (int i = 0; i < len; i++) {
//			int c = (d[offset + i] & 0xff);
//			if (c < 0x10) {
//				buf.append("0");
//			}
//			buf.append(Integer.toHexString(c).toUpperCase());
//			if (bytesPerLine > 0 && (i % bytesPerLine == bytesPerLine - 1 && i != len - 1)) {
//				// buf.append("\n");
//			} else if (i != len - 1 && wantspaces) {
//				buf.append(" ");
//			}
//		}
//		return buf.toString();
//	}

}