/*
 * GenericDevice.java
 */

package org.jsynthlib.synthdrivers.generic;

import java.util.prefs.Preferences;

import org.jsynthlib.menu.JSLFrame;
import org.jsynthlib.menu.helper.SysexHandler;
import org.jsynthlib.menu.window.HexDumpEditorHighlighted;
import org.jsynthlib.menu.window.SingleTextAreaFrame;
import org.jsynthlib.model.JSynthManufacturerLookup;
import org.jsynthlib.model.device.Device;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.HexaUtil;

/**
 * A Null Synth Driver.
 * 
 * @author Brian Klock
 * @version $Id$
 */
public class GenericDevice extends Device {
	public GenericDevice() {
		super("Default", "Generic", null, null, "Brian Klock");
	}

	public GenericDevice(Preferences prefs) {
		this();
		this.prefs = prefs;

		addDriver(new GenericDriver());
		addDriver(new IdentityDriver());
	}

	private class GenericDriver extends SynthDriverPatchImpl {
		private GenericDriver() {
			super("-", "Brian Klock");
			patchNumbers = new String[] { "0" };
		}

		public JSLFrame editPatch(PatchDataImpl p) {
			return (new HexDumpEditorHighlighted(p));
		}
	}

	private class IdentityDriver extends SynthDriverPatchImpl {
		private IdentityDriver() {
			super("Identity", "Joe Emenaker");
			patchNumbers = new String[] { "0" };
			sysexRequestDump = new SysexHandler("F0 7E 7F 06 01 F7");
			sysexID = "F07E**0602"; // Match sysex identity reply messages
		}

		public JSLFrame editPatch(PatchDataImpl p) {
			int lengthOfID = JSynthManufacturerLookup.lengthOfID(p.getSysex(), 5);
			String manuf = JSynthManufacturerLookup.get(p.getSysex(), 5);

			SingleTextAreaFrame f = new SingleTextAreaFrame("Identity Reply Details");
			f.append("MIDI Channel         : " + p.getSysex()[2] + "\n");
			f.append("Manuf ID             : " + HexaUtil.hexDump(p.getSysex(), 5, lengthOfID, -1, true) + " (" + manuf
					+ ")\n");
			f.append("Family (LSB First)   : " + HexaUtil.hexDump(p.getSysex(), 5 + lengthOfID, 2, -1, true) + "\n");
			f.append("Product (LSB First)  : " + HexaUtil.hexDump(p.getSysex(), 7 + lengthOfID, 2, -1, true) + "\n");
			f.append("Software (LSB First) : " + HexaUtil.hexDump(p.getSysex(), 9 + lengthOfID, 4, -1, true) + "\n");
			return (f);
			// return new HexDumpEditorFrame(p);
		}
	}
}
