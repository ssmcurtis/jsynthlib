package org.jsynthlib.synthdrivers.access.virus;

import java.util.prefs.Preferences;

import org.jsynthlib.model.device.Device;

/**
 * Device class for the Access Virus
 * 
 * @version $Id$
 * @author Kenneth L. Martinez
 */
public class AccessVirusDevice extends Device {
	static final String DRIVER_INFO = "Both the Virus desktop and Virus b can be used with this driver; note\n"
			+ "that the Virus desktop doesn't have program banks E thru H.\n\n"
			+ "The edit buffer for the global midi channel will be used to send and\n"
			+ "play single programs and multis.  The midi device Id must match that\n"
			+ "of the Virus to allow sysex send and receive to work.  Set the device\n"
			+ "Id on the Configuration tab of Show Details.";

	/** Creates new AccessVirus */
	public AccessVirusDevice() {
		super("Access", "Virus", null, DRIVER_INFO, "Kenneth L. Martinez");
	}

	/** Constructor for for actual work. */
	public AccessVirusDevice(Preferences prefs) {
		this();
		this.prefs = prefs;

		// setDeviceID(17); // default Device ID
		
		addDriver(new VirusProgBankDriver());
		addDriver(new VirusProgSingleDriver());
		addDriver(new VirusMultiBankDriver());
		addDriver(new VirusMultiSingleDriver());
	}

}
