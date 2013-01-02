package org.jsynthlib.menu.helper;

import java.util.Comparator;

import org.jsynthlib.model.patch.Patch;

// This is a comparator class used by the delete duplicated action to sort based on the sysex data
// Sorting this way makes the Dups search much easier, since the dups must be next to each other
public class SysexSort implements Comparator<Object> {
	public int compare(Object a1, Object a2) {
		String s1 = new String(((Patch) (a1)).getByteArray());
		String s2 = new String(((Patch) (a2)).getByteArray());
		return s1.compareTo(s2);
	}
}