/*
 * Copyright 2004,2005 Fred Jan Kraan
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
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

/*
 * WaveModel.java for Roland MT-32
 * 
 * @version $Id$
 */

package org.jsynthlib.synthdrivers.roland.mt32;

import org.jsynthlib.menu.widgets.IParamModel;
import org.jsynthlib.model.patch.PatchDataImpl;

class WaveModel implements IParamModel {
	private PatchDataImpl patch;
	private int source;

	public WaveModel(PatchDataImpl p, int s) {
		patch = p;
		source = s;
	}

	public void set(int i) {
		patch.getSysex()[34 + 8 + source] = (byte) ((patch.getSysex()[34 + 8 + source] & 254) + (byte) (i / 128));
		patch.getSysex()[38 + 8 + source] = (byte) (i % 128);
	}

	public int get() {
		return (((patch.getSysex()[34 + 8 + source] & 1) * 128) + (patch.getSysex()[38 + 8 + source]));
	}
}
