/*
 * Copyright 2004 Jeff Weber
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package org.jsynthlib.synthdrivers.alesis.dm5;

import org.jsynthlib.menu.widgets.IParamModel;
import org.jsynthlib.menu.widgets.ISender;
import org.jsynthlib.menu.widgets.ScrollBarLookupWidget;
import org.jsynthlib.model.patch.Patch;

/**
 * DM5ScrollBarLookupWidget. Adds functionality to the standard JSynthLib ScrollBarLookupWidget to allow dynamic
 * updating of the options list.
 * 
 * @author Jeff Weber
 */
class DM5ScrollBarLookupWidget extends ScrollBarLookupWidget {
	private static final String[] noteNames = new String[] { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A",
			"A#", "B" };

	private int numNotes;

	/**
	 * Constructs a new DM5ScrollBarLookupWidget given the standard parameters for a JSynthLib ScrollBarLookupWidget.
	 */
	DM5ScrollBarLookupWidget(String label, Patch patch, int min, int max, int labelWidth, IParamModel pmodel,
			ISender sender, int startValue, int numNotes) {
		super(label, patch, min, max, labelWidth, pmodel, sender, new String[numNotes]);
		this.numNotes = numNotes;
		text.setColumns(text.getColumns() + 1);
		updateRootNote(startValue);
	}

	/**
	 * Updates the display value of the ScrollBarLookupWidget (not the actual control value) using the current value of
	 * the control as the nth element of the array given by getNoteNames(newRootNoteValue, numNotes)
	 */
	void updateRootNote(int newRootNoteValue) {
		options = getNoteNames(newRootNoteValue, numNotes);
		int v = slider.getValue();
		text.setText(options[v]);
	}

	/**
	 * Returns an array of strings, each element of which represents a note name given by num/name, where num is the
	 * numeric note value and name is the name of the note. The first element of the array is determined by startValue
	 * and the number of elements is determined by numNotes.
	 */
	private String[] getNoteNames(int startValue, int numNotes) {
		String noteTitle[] = new String[numNotes];
		for (int i = 0; i < numNotes; i++) {
			noteTitle[i] = String.valueOf(i + startValue) + "/" + noteNames[(i + startValue) % 12]
					+ String.valueOf(((i + startValue) / 12) - 2);
		}
		return noteTitle;
	}
}