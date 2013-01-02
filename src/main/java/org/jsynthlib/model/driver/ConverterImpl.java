package org.jsynthlib.model.driver;

import org.jsynthlib.model.device.Device;
import org.jsynthlib.model.patch.Patch;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.DriverUtil;

/**
 * An implementation of IConverter interface for Patch class.
 * 
 * @author ???
 * @version $Id$
 * @see SynthDriver
 * @see Device#addDriver(SynthDriver)
 */
abstract public class ConverterImpl extends SynthDriverPatchImpl implements Converter {
	public ConverterImpl(String patchType, String authors) {
		super(patchType, authors);
	}

	public ConverterImpl() {
		this("Converter", "JSynthLib"); // Who is the auther?
	}

	// If extractPatch returns an array of Patches whose drivers are set
	// properly, override this by;
	// public IPatch[] createPatch(byte[] sysex) {
	// return extractPatch(new Patch(sysex, this));
	// }
	public Patch[] createPatches(byte[] sysex) {
		PatchDataImpl patch = new PatchDataImpl(sysex, this);
		PatchDataImpl[] patarray = extractPatch(patch);
		if (patarray == null)
			return new PatchDataImpl[] { patch };
		// Conversion was sucessfull, we have at least one
		// converted patch. Assign a proper driver to each patch of patarray
		Device dev = getDevice();
		for (int i = 0; i < patarray.length; i++) {
			byte[] d = patarray[i].getSysex();
			patarray[i].setDriver((SynthDriverPatch) DriverUtil.chooseDriver(d, dev));
		}
		return patarray;
	}

	/**
	 * Convert a bulk patch into an array of single and/or bank patches.
	 */
	abstract public PatchDataImpl[] extractPatch(PatchDataImpl p);

	public final boolean isSingleDriver() {
		return false;
	}

	public final boolean isBankDriver() {
		return false;
	}

	public final boolean isConverter() {
		return true;
	}
}
