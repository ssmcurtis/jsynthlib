/*
 * Copyright 2004-5 Yves Lefebvre, Bill Zwicky
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
package org.jsynthlib.synthdrivers.casio.cz1000;

import org.jsynthlib.model.driver.ConverterImpl;
import org.jsynthlib.model.patch.Patch;
import org.jsynthlib.model.patch.PatchDataImpl;

/**
 * Intercept the 263-byte messages sent by the synth, and convert them into standard 264-byte patches.
 * 
 * @author Bill Zwicky
 * @version $Id$
 */
public class CasioCZ1000RcvConverter extends ConverterImpl {
	public CasioCZ1000RcvConverter() {
		super("Converter", "Bill Zwicky");
		sysexID = "F04400007*";
		patchSize = 263;
	}

	public Patch createPatch(byte[] sysex) {
		// Casio returns a 263 byte patch, but everyone expects 264,
		// because the sysex sent to the Casio contains the bank number,
		// while the sysex sent back does not.
		byte[] sysex2 = new byte[264];
		System.arraycopy(sysex, 0, sysex2, 0, 6);
		sysex2[6] = 0x60; // default to edit buffer
		System.arraycopy(sysex, 6, sysex2, 7, sysex.length - 6);
		return new PatchDataImpl(sysex2, this);
	}

	public PatchDataImpl[] extractPatch(PatchDataImpl p) {
		return new PatchDataImpl[] { (PatchDataImpl) createPatch(p.getSysex()) };
	}
}
