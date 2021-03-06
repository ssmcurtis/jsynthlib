/*
 *  WaldorfPulseSingleDriver.java
 *
 *  Copyright (c) Scott Shedden, 2004
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.jsynthlib.synthdrivers.waldorf.pulse;

import java.io.File;
import java.io.FileInputStream;
import java.text.NumberFormat;

import org.jsynthlib.menu.JSLFrame;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;

public class WaldorfPulseSingleDriver extends SynthDriverPatchImpl {
	private String userPatchNumbers[];

	public WaldorfPulseSingleDriver() {
		super("Single", "Scott Shedden");
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMinimumIntegerDigits(2);

		sysexID = "F03E0B******";
		patchNameStart = 0;
		patchNameSize = 0;
		deviceIDoffset = 3;
		checksumStart = 6;
		checksumEnd = 74;
		checksumOffset = 75;
		patchNumbers = new String[100];
		for (int i = 0; i <= 98; i++) {
			patchNumbers[i] = nf.format(i + 1);
		}
		patchNumbers[99] = "rn";
		userPatchNumbers = new String[40];
		for (int i = 0; i <= 39; i++) {
			userPatchNumbers[i] = nf.format(i + 1);
		}
		patchSize = 77;
		sysexRequestDump = new SysexHandler("F0 3E 0B 00 40 *patchNum* F7");
	}

	protected void setBankNum(int bankNum) {
	}

	protected void sendProgramChange(int patchNum) {
		super.sendProgramChange(patchNum);
	}

	public PatchDataImpl createNewPatch() {
		byte[] sysex = new byte[77];
		try {
			FileInputStream f = new FileInputStream(new File("synthdrivers/WaldorfPulse/pulse_default.syx"));
			f.read(sysex);
			f.close();
		} catch (Exception e) {
			// Fallback on hardcoded patch if default is absent
			System.arraycopy(WaldorfPulseInitPatch.initPatch, 0, sysex, 0, 77);
		}
		sysex[3] = 0; // Device ID
		PatchDataImpl p = new PatchDataImpl(sysex, this);
		calculateChecksum(p);
		return p;
	}

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		sendProgramChange(patchNum);
		((PatchDataImpl) p).getSysex()[4] = (byte) 1;
		((PatchDataImpl) p).getSysex()[5] = (byte) patchNum;
		super.sendPatch(p);
	}

	public void sendPatch(PatchDataImpl p) {
		((PatchDataImpl) p).getSysex()[4] = (byte) 0;
		super.sendPatch(p);
	}

	public JSLFrame editPatch(PatchDataImpl p) {
		return new WaldorfPulseSingleEditor((PatchDataImpl) p);
	}

	protected void calculateChecksum(PatchDataImpl p, int start, int end, int ofs) {
		int sum = 0;
		for (int i = start; i <= end; i++)
			sum += p.getSysex()[i];
		p.getSysex()[ofs] = (byte) (sum & 0x7f);
	}

	public String[] getPatchNumbersForStore() {
		return userPatchNumbers;
	}
}
