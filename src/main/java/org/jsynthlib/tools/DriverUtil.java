/*
 * Copyright 2004 Hiroo Hayashi
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
package org.jsynthlib.tools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.jsynthlib.menu.preferences.AppConfig;
import org.jsynthlib.model.device.Device;
import org.jsynthlib.model.driver.Converter;
import org.jsynthlib.model.driver.SynthDriver;
import org.jsynthlib.model.driver.SynthDriverPatch;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.patch.Patch;

/**
 * Utility Routines for Synth Drivers.
 * 
 * @version $Id$
 * @author Hiroo Hayashi
 */
public class DriverUtil {

	/**
	 * Factory method of Patch. Look up the driver for sysex byte array, and create a patch by using the driver found.
	 * This is used for a byte array read from a Sysex file, for which a Driver is not known.
	 */
	public static Patch[] createPatches(byte[] sysex, String filename) {
		// INFO FIND DRIVER AND CREATE PATCHES
		System.out.println("Size: " + sysex.length + "-" + HexaUtil.hexDumpOneLine(sysex, 0, -1, 100));
		SynthDriver driver = chooseDriver(sysex);

		return createPatches(sysex, driver, filename);
	}

	/**
	 * Factory method of Patch. Look up the driver of the specified Device for sysex byte array, and create a patch by
	 * using the driver found.
	 * 
	 * @param device
	 *            Device whose driver is looked up.
	 */
	public static Patch[] createPatches(byte[] sysex, Device device, String filename) {
		SynthDriver driver = chooseDriver(sysex, device);
		return createPatches(sysex, driver, filename);
	}

	public static Patch[] createPatches(byte[] sysex, Device device) {
		SynthDriver driver = chooseDriver(sysex, device);
		return createPatches(sysex, driver, "");
	}

	// TODO ssmCurtis ... split always F0..F7 ?
	private static Patch[] createPatches(byte[] sysex, SynthDriver driver, String filename) {

		// INFO CREATE PATCH FOR SYSEX

		if (driver == null) {
			return null;
		} else if (driver.isConverter()) {
			// TODO ssmCurtis - support removed
			// ErrorMsg.reportStatus(">>> IS Converter in " + DriverUtil.class.getName());
			return ((Converter) driver).createPatches(sysex);
		} else {

			// introduces by microKorg
			ByteBuffer byteBuffer = driver.processDumpDataConversion(sysex);

			// TODO ssmCurtis - split always patches ...
			// List<Patch> li = new ArrayList<Patch>();
			// for (byte[] sysexSplit : splitSysexBytearray(byteBuffer)) {
			// li.add(((SynthDriverPatch) driver).createPatch(sysexSplit, filename));
			// }
			// return li.toArray(new Patch[] {});

			// TODO ssmCurtis ... or only one patch
			Patch p = ((SynthDriverPatch) driver).createPatch(byteBuffer.array(), filename);
			return new Patch[] { p };
		}
	}

	public static Patch createPatch(byte[] sysex, String filename) {
		SynthDriverPatch driver = (SynthDriverPatch) chooseDriver(sysex);
		return driver != null ? driver.createPatch(sysex, filename) : null;
	}

	public static List<byte[]> splitSysexBytearray(ByteBuffer byteBuffer) {
		int bufSize = byteBuffer.capacity();
		int cursor = 0;
		List<byte[]> li = new ArrayList<byte[]>();
		List<Byte> sysexList = new ArrayList<Byte>();
		byte[] sysex = null;
		boolean select = false;
		while (cursor < bufSize) {
			byte b = byteBuffer.get(cursor);
			if (HexaUtil.isStartSysex(b)) {
				select = true;
			}
			if (select) {
				sysexList.add(b);
			}
			if (HexaUtil.isEndSysex(b)) {
				select = false;
				sysex = new byte[sysexList.size()];
				int bc = 0;
				for (Byte byteFromList : sysexList) {
					sysex[bc] = byteFromList.byteValue();
					bc++;
				}
				li.add(sysex);
				sysexList = new ArrayList<Byte>();
			}
			cursor++;
		}
		return li;
	}

	/**
	 * choose proper driver for sysex byte array.
	 * 
	 * @param sysex
	 *            System Exclusive data byte array.
	 * @return Driver object chosen
	 * @see SynthDriver#supportsPatch
	 */
	public static SynthDriver chooseDriver(byte[] sysex) {
		// INFO FIND DRIVER BY SYSEX HEADER
		return chooseDriver(sysex, null);
	}

	/**
	 * choose proper driver in a given device for sysex byte array.
	 * 
	 * @param sysex
	 *            System Exclusive data byte array.
	 * @param dev
	 *            Device - can be null
	 * @return Driver object chosen
	 * @see SynthDriver#supportsPatch
	 */
	public static SynthDriver chooseDriver(byte[] sysex, Device dev) {
		// INFO FIND DRIVER BY DEVICE

		System.out.println("Find driver by device");

		String patchString = getPatchHeader(sysex);
		if (dev == null) {
			for (int idev = 0; idev < AppConfig.deviceCount(); idev++) {
				// Outer Loop, iterating over all installed devices
				dev = AppConfig.getDevice(idev);
				for (int idrv = 0; idrv < dev.driverCount(); idrv++) {
					SynthDriverPatch drv = (SynthDriverPatch) dev.getDriver(idrv);
					// Inner Loop, iterating over all Drivers of a device
					if (drv.supportsPatch(patchString, sysex)) {
						return drv;
					}
				}
			}
		} else {
			for (int idrv = 0; idrv < dev.driverCount(); idrv++) {
				SynthDriverPatch drv = (SynthDriverPatch) dev.getDriver(idrv);
				// Inner Loop, iterating over all Drivers of a device
				if (drv.supportsPatch(patchString, sysex)) {
					return drv;
				}
			}
		}
		return AppConfig.getNullDriver();
	}

	/**
	 * Return a hexadecimal string for {@link SynthDriver#supportsPatch IDriver.suppportsPatch} at most 16 byte sysex
	 * data.
	 * 
	 * @see SynthDriver#supportsPatch
	 */
	public static String getPatchHeader(byte[] sysex) {
		StringBuffer patchstring = new StringBuffer("F0");

		for (int i = 1; i < Math.min(16, sysex.length); i++) {
			if ((sysex[i] & 0xff) < 0x10) {
				patchstring.append("0");
			}
			patchstring.append(Integer.toHexString((sysex[i] & 0xff)));
		}
		return patchstring.toString();
	}

	/**
	 * Caluculate check sum of a byte array <code>sysex</code>.
	 * <p>
	 * 
	 * The checksum calculation method of this method is used by Roland, YAMAHA, etc.
	 * <p>
	 * 
	 * Compatibility Note: This method became 'static' method.
	 * 
	 * @param sysex
	 *            a byte array
	 * @param start
	 *            start offset
	 * @param end
	 *            end offset
	 * @param ofs
	 *            offset of the checksum data
	 * @see SynthDriverPatchImpl#calculateChecksum(Patch)
	 */
	public static void calculateChecksum(byte[] sysex, int start, int end, int ofs) {
		int sum = 0;
		for (int i = start; i <= end; i++) {
			sum += sysex[i];
		}
		sysex[ofs] = (byte) (-sum & 0x7f);
		/*
		 * Equivalent with above. p.sysex[ofs] = (byte) (sum & 0x7f); p.sysex[ofs] = (byte) (p.sysex[ofs] ^ 0x7f);
		 * p.sysex[ofs] = (byte) (p.sysex[ofs] + 1); p.sysex[ofs] = (byte) (p.sysex[ofs] & 0x7f);
		 */
	}

	/**
	 * A utility method to generates an array of formatted numbers. For example,
	 * 
	 * <pre>
	 * patchNumbers = generateNumbers(1, 10, &quot;Patch 00&quot;);
	 * </pre>
	 * 
	 * setups the following array,
	 * 
	 * <pre>
	 *   {
	 *     "Patch 01", "Patch 02", "Patch 03", "Patch 04", "Patch 05"
	 *     "Patch 06", "Patch 07", "Patch 08", "Patch 09", "Patch 10"
	 *   }
	 * </pre>
	 * 
	 * @param min
	 *            minumux value
	 * @param max
	 *            maximum value
	 * @param format
	 *            pattern String for java.text.DecimalFormat
	 * @return an array of formatted numbers.
	 * @see java.text.DecimalFormat
	 * @see SynthDriverPatch#getPatchNumbers
	 * @see SynthDriverPatch#getPatchNumbersForStore
	 * @see SynthDriverPatch#getBankNumbers
	 */
	public static String[] generateNumbers(int min, int max, String format) {
		String retval[] = new String[max - min + 1];

		DecimalFormat df = (DecimalFormat) NumberFormat.getInstance().clone();

		df.applyPattern(format);

		while (max >= min) {
			retval[max - min] = df.format(max--);
		}
		return retval;
	}

	/**
	 * Create new patch using a patch file <code>patchFileName</code>.
	 * 
	 * @param driver
	 *            IPatchDriver object
	 * @param fileName
	 *            file name (relative path to driver directory)
	 * @param size
	 *            Sysex data size
	 * @return IPatch object
	 * @see SynthDriverPatch#createPatch()
	 */
	public static Patch createNewPatch(SynthDriverPatch driver, String fileName, int size) { // Borrowed from DR660
																								// driver
		byte[] buffer = new byte[size];

		try {
			InputStream fileIn = driver.getClass().getResourceAsStream(fileName);

			if (fileIn != null) {
				fileIn.read(buffer);
				fileIn.close();
				return driver.createPatch(buffer, fileName);
			} else {
				throw new FileNotFoundException("File: " + fileName + " does not exist!");
			}
		} catch (IOException e) {
			ErrorMsgUtil.reportError("Error", "Unable to open " + fileName, e);
			return null;
		}
	}

	public static void main(String[] args) {
		int intHex = 0xF0;
		byte hex = (byte) intHex;

		byte[] test = new byte[] { hex };

		System.out.printf("%d %32s%n", intHex, Integer.toBinaryString(intHex));
		System.out.printf("%d %32s%n", hex & 0xff, Integer.toBinaryString(HexaUtil.byteToInt(hex)));
		System.out.printf("%d %32s%n", hex & 0xff, Integer.toBinaryString(HexaUtil.byteToChar(hex)));
		System.out.println(HexaUtil.byteToHexString(hex));
		HexaUtil.isStartSysex(hex);
	}

}
