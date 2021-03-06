/*
 * JSynthlib - Device for Yamaha DX7s
 * ==================================
 * @version $Id$
 * @author  Torsten Tittmann
 *
 * Copyright (C) 2002-2004 Torsten.Tittmann@gmx.de
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.i
 *
 */
package org.jsynthlib.synthdrivers.yamaha.dx7s;

import java.util.prefs.Preferences;

import org.jsynthlib.synthdrivers.yamaha.dx7.common.DX7FamilyDevice;

public class YamahaDX7sDevice extends DX7FamilyDevice {
	private static final String dxInfoText = YamahaDX7sStrings.INFO_TEXT;

	/** Creates new YamahaDX7s Device */
	public YamahaDX7sDevice() {
		super("Yamaha", "DX7s", null, dxInfoText, "Torsten Tittmann");
	}

	/** Constructor for for actual work. */
	public YamahaDX7sDevice(Preferences prefs) {
		super("Yamaha", "DX7s", null, dxInfoText, "Torsten Tittmann", 0x00, 0x02, 0x03, prefs);

		// setSPBPflag(0x00); // switched off 'Enable Remote Control?' and disabled
		// setSwOffMemProtFlag(0x02); // switched off 'Disable Memory Protection?' and enabled
		// setTipsMsgFlag(0x03); // switched on 'Display Hints and Tips?' and enabled

		// voice patch
		addDriver(new YamahaDX7sVoiceSingleDriver());
		addDriver(new YamahaDX7sVoiceBankDriver());

		// additional voice patch
		addDriver(new YamahaDX7sAdditionalVoiceSingleDriver()); // experimental !!!!
		addDriver(new YamahaDX7sAdditionalVoiceBankDriver()); // experimental !!!!

		// performance patch
		addDriver(new YamahaDX7sPerformanceSingleDriver()); // experimental !!!!
		addDriver(new YamahaDX7sPerformanceBankDriver()); // experimental !!!!

		// system setup patch
		addDriver(new YamahaDX7sSystemSetupDriver()); // experimental !!!!

		// fractional scaling patch
		addDriver(new YamahaDX7sFractionalScalingSingleDriver()); // experimental !!!!
		addDriver(new YamahaDX7sFractionalScalingBankDriver()); // experimental !!!!

		// micro tuning patch
		addDriver(new YamahaDX7sMicroTuningSingleDriver()); // experimental !!!!
		addDriver(new YamahaDX7sMicroTuningBankDriver()); // experimental !!!!
	}
}
