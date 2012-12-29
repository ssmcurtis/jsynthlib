/*
 * GenericDevice.java
 */

package org.jsynthlib.synthdrivers.generic;

import java.util.prefs.Preferences;

import org.jsynthlib.menu.patch.Patch;
import org.jsynthlib.menu.patch.Device;
import org.jsynthlib.menu.patch.Driver;
import org.jsynthlib.menu.patch.SysexHandler;
import org.jsynthlib.menu.ui.JSLFrame;
import org.jsynthlib.menu.ui.window.HexDumpEditorFrame;
import org.jsynthlib.menu.ui.window.HexDumpEditorHighlighted;
import org.jsynthlib.menu.ui.window.SingleTextAreaFrame;
import org.jsynthlib.model.ManufacturerLookup;
import org.jsynthlib.tools.Utility;

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

	private class GenericDriver extends Driver {
		private GenericDriver() {
			super("-", "Brian Klock");
			patchNumbers = new String[] { "0" };
		}

		protected JSLFrame editPatch(Patch p) {
			return (new HexDumpEditorHighlighted(p));
		}
	}

	private class IdentityDriver extends Driver {
		private IdentityDriver() {
			super("Identity", "Joe Emenaker");
			patchNumbers = new String[] { "0" };
			sysexRequestDump = new SysexHandler("F0 7E 7F 06 01 F7");
			sysexID = "F07E**0602"; // Match sysex identity reply messages
		}

		protected JSLFrame editPatch(Patch p) {
			int lengthOfID = ManufacturerLookup.lengthOfID(p.getSysex(), 5);
			String manuf = ManufacturerLookup.get(p.getSysex(), 5);

			SingleTextAreaFrame f = new SingleTextAreaFrame("Identity Reply Details");
			f.append("MIDI Channel         : " + p.getSysex()[2] + "\n");
			f.append("Manuf ID             : " + Utility.hexDump(p.getSysex(), 5, lengthOfID, -1, true) + " (" + manuf
					+ ")\n");
			f.append("Family (LSB First)   : " + Utility.hexDump(p.getSysex(), 5 + lengthOfID, 2, -1, true) + "\n");
			f.append("Product (LSB First)  : " + Utility.hexDump(p.getSysex(), 7 + lengthOfID, 2, -1, true) + "\n");
			f.append("Software (LSB First) : " + Utility.hexDump(p.getSysex(), 9 + lengthOfID, 4, -1, true) + "\n");
			return (f);
			// return new HexDumpEditorFrame(p);
		}
	}
}
