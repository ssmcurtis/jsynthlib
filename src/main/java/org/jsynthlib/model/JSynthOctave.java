package org.jsynthlib.model;

import java.util.ArrayList;
import java.util.List;

public enum JSynthOctave {
	OCTVAE0("0", 0),
	OCTVAE1("+1", 12),
	OCTVAE2("+2", 24),
	OCTVAE_1("-1", -12),
	OCTVAE_2("-2", -24),
	OCTVAE_3("-3", -36),
	OCTVAE_4("-4", -48);

	private final String title;
	private final int addOctave;

	JSynthOctave(String title, int addOctave) {
		this.title = title;
		this.addOctave = addOctave;
	}

	public String getTitle() {
		return title;
	}

	public static String[] getNames() {
		List<String> li = new ArrayList<String>();

		for (JSynthOctave seq : JSynthOctave.values()) {
			li.add(seq.getTitle());
		}
		return li.toArray(new String[] {});
	}

	public static JSynthOctave getOctaveId(int ordinal) {
		for (JSynthOctave seq : JSynthOctave.values()) {
			if (seq.ordinal() == ordinal) {
				return seq;
			}
		}
		return JSynthOctave.OCTVAE0;
	}

	public int addOctave(int key) {
		int newKey = key + addOctave;
		if (newKey >= 127 || newKey < 0) {
			return key;
		}
		return newKey;
	}
}
