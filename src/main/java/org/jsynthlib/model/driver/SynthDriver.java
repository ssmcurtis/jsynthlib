package org.jsynthlib.model.driver;

import java.nio.ByteBuffer;

import org.jsynthlib.model.device.Device;
import org.jsynthlib.model.patch.Patch;


/**
 * This is an interface for Device.driverList. All of drivers (single driver and bank driver) implement this.
 * 
 * @author ribrdb
 * @version $Id$
 * @see SynthDriverPatch
 * @see Converter
 */
public interface SynthDriver {
	/**
	 * return type of patch which the driver handles. eg. "Single", "Bank", "Drumkit", "Converter", etc.
	 */
	String getPatchType();

	/** return the names of the authors of this driver. */
	String getAuthors();

	/** Set <code>Device</code> with which this driver go. */
	void setDevice(Device device);

	/** Return <code>Device</code> with which this driver go.. */
	Device getDevice();

	/**
	 * Compares the header & size of a Patch to this driver to see if this driver is the correct one to support the
	 * patch.
	 * 
	 * @param patchString
	 *            the result of {@link Patch#getPatchHeader() IPatch.getPatchHeader()}.
	 * @param sysex
	 *            a byte array of sysex message
	 * @return <code>true</code> if this driver supports the Patch.
	 */
	boolean supportsPatch(String patchString, byte[] sysex);
	
	boolean supportsHeader(String patchString);

	/**
	 * Returns whether this driver is a Single Driver.
	 */
	boolean isSingleDriver();

	/**
	 * Returns whether this driver is a Bank Driver.
	 */
	boolean isBankDriver();

	/**
	 * Returns whether this driver is a Converter. Equivalent with <code>instanceof IConverter</code>. Note that there
	 * can be a Driver which are both Single Driver and Converter.
	 * 
	 * @see Converter
	 */
	boolean isConverter();
	
	boolean isUseForStoreLibrary();
	
	public ByteBuffer processDumpDataConversion(byte[] sysexBuffer);
}
