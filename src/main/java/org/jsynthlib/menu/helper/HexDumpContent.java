package org.jsynthlib.menu.helper;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.jsynthlib.advanced.style.FormatedString;
import org.jsynthlib.model.patch.Patch;
import org.jsynthlib.tools.ErrorMsgUtil;

/**
 * hex utility functions.
 * 
 * @version $Id$
 */
public class HexDumpContent {

	private static List<Integer> sysex = Arrays.asList(Integer.parseInt("F0", 16), Integer.parseInt("F7", 16));

	private Patch patch;

	private float differnce = 0f;

	public HexDumpContent(Patch patch) {
		this.patch = patch;
	}

	/**
	 * 
	 * @param mainSysex
	 * @param compareSysex
	 *            - can be null
	 * @param offset
	 * @param len
	 * @param bytesPerLine
	 * @param addresses
	 * @param characters
	 * @return
	 */
	public List<FormatedString> hexDumpFormated(byte[] mainSysex, byte[] compareSysex, int offset, int len, int bytesPerLine,
			boolean addresses, boolean characters) {
		if (offset >= mainSysex.length) {
			ErrorMsgUtil.reportStatus("offset out of bounds of byte array");
			return Arrays.asList();
		}
		if (offset + len >= mainSysex.length || len < 0) {
			len = mainSysex.length - offset;
		}
		if (bytesPerLine <= 0) {
			bytesPerLine = len;
		}

		int addresslen = 0;
		if (addresses) {
			int maxnumber = 1;
			while (maxnumber < offset + len) {
				addresslen += 2;
				maxnumber *= 100;
			}
		}
		addresslen = addresslen < 4 ? 4 : addresslen;

		return (hexDump(mainSysex, compareSysex, offset, len, bytesPerLine, addresslen, characters));
	}

	private List<FormatedString> hexDump(byte[] mainSysex, byte[] compareSysex, int offset, int len, int bytesPerLine, int addressLength,
			boolean characters) {
		List<FormatedString> outputList = new ArrayList<FormatedString>();

		FormatedString item;
		int rowMax = mainSysex.length / bytesPerLine;

		if (mainSysex.length % bytesPerLine > 0) {
			rowMax++;
		}
		StringBuilder sb;

		int headerLength = patch.getDriver().getHeaderSize();
		
		int checksumPosition = patch.getDriver().getChecksumBytePos();
		Integer notSameBytes = 0;
		for (int rowCount = 0; rowCount < rowMax; rowCount++) {

			if (addressLength > 0) {
				sb = new StringBuilder();
				if (rowCount < 10) {
					sb.append(" " + rowCount);
				} else {
					sb.append(rowCount);

				}
				sb.append(": ");

				String addressstring = Integer.toHexString(offset + (rowCount * bytesPerLine));

				while (addressstring.length() < addressLength) {
					addressstring = "0" + addressstring;
				}
				sb.append(addressstring);
				sb.append(" (");
				addressstring = Integer.toString(offset + (rowCount * bytesPerLine));

				while (addressstring.length() < addressLength) {
					addressstring = "0" + addressstring;
				}
				sb.append(addressstring);
				sb.append(") - ");

				item = new FormatedString(sb.toString());
				outputList.add(item);
				item = new FormatedString();

			}

			for (int i = 0; i < bytesPerLine; i++) {
				int position = offset + i + (rowCount * bytesPerLine);

				if (position < mainSysex.length) {
					int c = (mainSysex[position] & 0xff);
					item = new FormatedString();
					if (c < 0x10) {
						item.setText("0" + Integer.toHexString(c).toUpperCase().toString() + " ");
					} else {
						item.setText(Integer.toHexString(c).toUpperCase() + " ");
					}
					if (sysex.contains(c)) {
						SimpleAttributeSet attribute = new SimpleAttributeSet();
						StyleConstants.setBackground(attribute, Color.YELLOW);
						item.setAttributeSet(attribute);
					} else {

						SimpleAttributeSet attribute = new SimpleAttributeSet();

						if (position < headerLength) {
							StyleConstants.setBackground(attribute, Color.LIGHT_GRAY);
							// } else if (position == headerLength) {
							// StyleConstants.setBackground(attribute, new Color(128, 180, 220));
						} else if (checksumPosition == position) {
							StyleConstants.setBackground(attribute, new Color(220, 180, 128));
						}
						if (compareSysex != null && (compareSysex[position] & 0xff) == c) {
							StyleConstants.setForeground(attribute, Color.GRAY);
							StyleConstants.setUnderline(attribute, true);
						} else {
							notSameBytes++;
						}
						item.setAttributeSet(attribute);
					}
					outputList.add(item);
				} else {
					item = new FormatedString("   ");
					outputList.add(item);
				}
			}

			// char
			if (characters) {
				// ascii characters
				sb = new StringBuilder("  ");
				for (int i = 0; i < bytesPerLine; i++) {
					int position = offset + i + (rowCount * bytesPerLine);
					if (position < len) {
						if (mainSysex[position] >= 32 && mainSysex[position] <= 126) {
							sb.append(new Character((char) mainSysex[position]).toString());
						} else {
							sb.append(".");
						}
					}
				}
				item = new FormatedString(sb.toString());
				outputList.add(item);

			}
			item = new FormatedString("\n");
			outputList.add(item);
		}
		ErrorMsgUtil.reportStatus(mainSysex.length + " " + notSameBytes);
		differnce = notSameBytes.floatValue() / mainSysex.length;
		return (outputList);
	}

	public float getDiffernce() {
		return differnce;
	}

	public void setDiffernce(float differnce) {
		this.differnce = differnce;
	}

}