/*
 * Copyright 2006 Robert Wirski
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

package org.jsynthlib.synthdrivers.yamaha.a01v;

import org.jsynthlib.model.driver.NameValue;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;

public class Yamaha01vSetupDriver extends SynthDriverPatchImpl {

	private static final SysexHandler SYS_REQ = new SysexHandler("F0 43 *ID* 7E 4C 4D 20 20 38 42 33 34 53 20 F7");

	public Yamaha01vSetupDriver() {
		super("Setup", "Robert Wirski");

		sysexID = "F0430*7E020A4C4D20203842333453";

		patchSize = 274;
		patchNameStart = 0;
		patchNameSize = 0;
		deviceIDoffset = 2;

		checksumStart = 6;
		checksumOffset = 271;
		checksumEnd = 272;

		bankNumbers = new String[] { "" };
		patchNumbers = new String[] { "" };

	}

	/**
	 * @see org.jsynthlib.model.driver.SynthDriverPatchImpl#createNewPatch()
	 */
	public PatchDataImpl createNewPatch() {
		byte[] sysex = new byte[patchSize];
		PatchDataImpl p;

		try {
			java.io.InputStream fileIn = getClass().getResourceAsStream("01v_Setup.syx");
			fileIn.read(sysex);
			fileIn.close();

		} catch (Exception e) {
			System.err.println("Unable to find 01v_Setup.syx.");
		}
		;

		p = new PatchDataImpl(sysex, this);
		return p;
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		send(SYS_REQ.toSysexMessage(getChannel(), new NameValue("ID", getDeviceID() + 0x1F)));
	}
}
