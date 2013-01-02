package org.jsynthlib.model;

import java.util.ArrayList;
import java.util.List;

public enum JSynthBpm {
	
	BPM120("120 bpm", 120f),
	BPM80("80 bpm", 80f),
	BPM105("105 bpm", 105f),
	BPM140("140 bpm", 140f),
	BPM160("160 bpm", 160f);

	private final String title;
	private final float bpm;

	JSynthBpm(String title, float bpm) {
		this.title = title;
		this.bpm= bpm;
	}

	public String getTitle() {
		return title;
	}

	public static String[] getNames() {
		List<String> li = new ArrayList<String>();
		
		for (JSynthBpm seq : JSynthBpm.values()) {
			li.add(seq.getTitle());
		}
		return li.toArray(new String[] {});
	}
	
	public static JSynthBpm getBpmId(int ordinal) {
		for (JSynthBpm seq : JSynthBpm.values()) {
			if (seq.ordinal() == ordinal) {
				return seq;
			}
		}
		return JSynthBpm.BPM120;
	}

	public float getBpm() {
		return bpm;
	}
}
