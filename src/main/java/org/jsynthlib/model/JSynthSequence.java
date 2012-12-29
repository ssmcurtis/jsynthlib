package org.jsynthlib.model;

import java.util.ArrayList;
import java.util.List;

public enum JSynthSequence {
	S001("Sequence 1 (1/16)"),
	S002("Sequence 2 (1/16)"),
	S003("Sequence 3 (1/16)"),
	S004("Sequence 4 (1/32)"),
	S005("Pad 1");

	private final String title;

	JSynthSequence(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public static String[] getNames() {
		List<String> li = new ArrayList<String>();
		
		for (JSynthSequence seq : JSynthSequence.values()) {
			li.add(seq.getTitle());
		}
		return li.toArray(new String[] {});
	}
	
	public static JSynthSequence getSequenceId(int ordinal) {
		for (JSynthSequence seq : JSynthSequence.values()) {
			if (seq.ordinal() == ordinal) {
				return seq;
			}
		}
		return JSynthSequence.S001;
	}
}
