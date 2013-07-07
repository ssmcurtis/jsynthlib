package org.jsynthlib.menu.widgets;

import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.synthdrivers.roland.mks80.Mks80;
import org.jsynthlib.tools.HexaUtil;

/**
 * An implementation of IParamModel for Patch class.
 * 
 * @see IParamModel
 * @see PatchDataImpl
 */
public class ParamModel implements IParamModel {

	/** <code>Patch</code> data. */
	protected PatchDataImpl patch;
	/** Offset of the data for which this model is. */
	protected int ofs;

	/**
	 * Creates a new <code>ParamModel</code> instance.
	 * 
	 * @param patch
	 *            a <code>Patch</code> value
	 * @param offset
	 *            an offset in <code>patch.sysex</code>.
	 */
	public ParamModel(PatchDataImpl patch, int offset) {
		this.ofs = offset;
		this.patch = patch;
	}

	// SysexWidget.IParamModel interface methods
	/** Set a parameter value <code>value</code>. */
	public void set(int value) {
		System.out.println("SET: " + value + " hx:" +HexaUtil.byteToHexString(HexaUtil.intToByte(value)) + " rol:"
				+ Mks80.byteToBankPatchNumber(HexaUtil.intToByte(value)));

		patch.getSysex()[ofs] = (byte) value;
	}

	/** Get a parameter value. */
	public int get() {
		Integer value = (int) patch.getSysex()[ofs];
		System.out.println("GET: " + value + " hx:" +HexaUtil.byteToHexString(HexaUtil.intToByte(value)) + " rol:"
				+ Mks80.byteToBankPatchNumber(HexaUtil.intToByte(value)));
		return value;
	}
}
