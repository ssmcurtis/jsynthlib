package org.jsynthlib.synthdrivers.roland.mks80.model;

import org.jsynthlib.menu.widgets.IParamModel;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.synthdrivers.roland.mks80.Mks80;
import org.jsynthlib.tools.HexaUtil;

public class BankNumberForToneInPatch implements IParamModel {
	private PatchDataImpl patch;
	private int ofs;
	private boolean bank = false;

	public BankNumberForToneInPatch(PatchDataImpl patch, int offset, boolean bank) {
		this.ofs = offset;
		this.patch = patch;
		this.bank = bank;
	}

	@Override
	public void set(int v) {
		Integer value = 0;
		if (bank) {
			value = Mks80.getBankNumber(v) + Mks80.getPatchnumber(ofs);
		} else {
			value = Mks80.getBankNumber(ofs) + Mks80.getPatchnumber(v);
		}

		System.out.println("SET: " + value + " hx:" + HexaUtil.byteToHexString(HexaUtil.intToByte(value)));
		patch.getSysex()[ofs] = value.byteValue();

	}

	@Override
	public int get() {
		Integer v = (int) patch.getSysex()[ofs];
		Integer value = 0;
		if (bank) {
			value = Mks80.getBankNumber(v) - 1;
		} else {
			value = Mks80.getPatchnumber(v) - 1;
		}
		System.out.println("GET: " + value + " hx:" + HexaUtil.byteToHexString(HexaUtil.intToByte(value)));
		return value;
	}

}
