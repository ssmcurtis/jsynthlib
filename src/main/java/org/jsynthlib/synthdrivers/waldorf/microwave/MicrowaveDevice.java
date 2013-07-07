/*
 * Copyright 2013 ssmCurtis, 2005 Joachim Backhaus
 *
 * This file is part of JSynthLib2.
 *
 * JSynthLib2 is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * JSynthLib2 is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package org.jsynthlib.synthdrivers.waldorf.microwave;

import java.util.prefs.Preferences;

import javax.swing.Action;

import org.jsynthlib.menu.Actions;
import org.jsynthlib.model.device.Device;
import org.jsynthlib.synthdrivers.korg.microkorg.MicroKorg;
import org.jsynthlib.synthdrivers.roland.mks80.Mks80ActionTonePatch;

public class MicrowaveDevice extends Device {
	private static final String INFO_TEXT = "Microwave 1";

	public MicrowaveDevice() {
		super(Microwave.VENDOR, Microwave.DEVICE, null, INFO_TEXT, "ssmCurtis");
	}

	public MicrowaveDevice(Preferences prefs) {
		this();
		this.prefs = prefs;

		setMaxProgramForLibraryStorage(Microwave.PROGRAM_COUNT_IN_SYNTH);

		addDriver(new MicrowaveSingleDriver());

		MicrowaveBankDriver libraryDriver = new MicrowaveBankDriver();
		libraryDriver.setUseForStoreLibrary(true);
		addDriver(libraryDriver);
		
		Action specialAction = new MicrowaveActionInfopanel(Actions.getMnemonics());
		Actions.addSpecialMenuAction(specialAction);

	}
}
