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

import org.jsynthlib.menu.widgets.SysexSender;
import org.jsynthlib.model.patch.PatchDataImpl;

class TCBitSender extends SysexSender {
	int offs, delta;
	int mask;
	int shift;
	PatchDataImpl patch;

	public TCBitSender(PatchDataImpl iPatch, int iParam, int iMask) {
		patch = iPatch;
		offs = iParam;

		mask = iMask;
		shift = 0;

		int j = mask;
		if (j != 0) {
			while ((j & 1) == 0) {
				shift++;
				j = (j >> 1);
			}
		}
	}

	public TCBitSender(PatchDataImpl iPatch, int iParam, int iMask, int idelta) {
		this(iPatch, iParam, iMask);
		delta = idelta;
	}

	protected int getValue() {
		int value = (patch.getSysex()[offs + 1] << 7);
		value = (value ^ patch.getSysex()[offs]);
		return value;
	}

	public byte[] generate(int value) {
		patch.getSysex()[7] = (byte) 0x00;
		patch.getSysex()[8] = (byte) 0x00;

		value = value + delta;

		int j = ((getValue() & (~mask)) | ((value << shift) & mask));

		patch.getSysex()[offs + 1] = (byte) ((j >> 7) & 127);
		patch.getSysex()[offs] = (byte) (j & 127);

		patch.getSysex()[TCElectronicGMajorConst.CHECKSUMOFFSET] = TCElectronicGMajorUtil.calcChecksum(patch.getSysex());
		return patch.getSysex();
	}

}
