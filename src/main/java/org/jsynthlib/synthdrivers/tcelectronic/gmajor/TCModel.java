/*
 * Copyright 2005 Ton Holsink
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

package org.jsynthlib.synthdrivers.tcelectronic.gmajor;

import org.jsynthlib.menu.widgets.ParamModel;
import org.jsynthlib.model.patch.PatchDataImpl;

class TCModel extends ParamModel {
	private int delta;

	public TCModel(PatchDataImpl p, int offset) {
		super(p, offset);
		delta = 0;
	}

	public TCModel(PatchDataImpl p, int offset, int idelta) {
		super(p, offset);
		delta = idelta;
	}

	public void set(int i) {
		TCElectronicGMajorUtil.setValue(patch.getSysex(), i + delta, ofs);
	}

	public int get() {
		return TCElectronicGMajorUtil.getValue(patch.getSysex(), ofs) - delta;
	}

}