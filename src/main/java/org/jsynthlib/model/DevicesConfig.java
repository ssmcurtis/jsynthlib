/**
 * DevicesConfig.java - handles the data-side of figuring out what
 * devices are available.  Not responsible for display.  Reads the
 * device config from a properties file, synthdrivers.properties,
 * expected to be in /synthdrivers.properties in the jar file or
 * elsewhere on the path.
 * @author Zellyn Hunter (zellyn@zellyn.com)
 * @version $Id$
 * @see DeviceListWriter_SE
 */

package org.jsynthlib.model;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.prefs.Preferences;

import org.apache.commons.lang3.text.WordUtils;
import org.clapper.util.classutil.AbstractClassFilter;
import org.clapper.util.classutil.AndClassFilter;
import org.clapper.util.classutil.ClassFilter;
import org.clapper.util.classutil.ClassFinder;
import org.clapper.util.classutil.ClassInfo;
import org.clapper.util.classutil.InterfaceOnlyClassFilter;
import org.clapper.util.classutil.NotClassFilter;
import org.clapper.util.classutil.SubclassClassFilter;
import org.jsynthlib.Constants;
import org.jsynthlib.JSynthResource;
import org.jsynthlib.menu.patch.Device;
import org.jsynthlib.tools.DeviceListWriter_SE;
import org.jsynthlib.tools.ErrorMsg;

public class DevicesConfig {
	/* enable XML Device which are still under development. */
//	private static final boolean useXMLDevice = true;

	/** Character used in the Preferences as file separator for xml files */
	private static final char XML_FILE_SEPARATOR = ':';

	/**
	 * This should either be set to null or to "". This is what will be returned by any method that tries to look
	 * something up and return a string (like getClassNameForDeviceName()) It all depends upon how the calling methods
	 * want to have it.
	 */
	private String NOT_FOUND_STRING = "";

	/** ArrayList of device names (long name). */
	// private SortedSet deviceNames = new ArrayList();

	private TreeSet<DeviceDescriptor> descriptors = new TreeSet<DeviceDescriptor>();
	private TreeSet<String> devicenameList = new TreeSet<String>();
	private TreeSet<String> IDStringList = new TreeSet<String>();
	private MapOfLists manufacturerList = new MapOfLists();
	private MapOfLists typeList = new MapOfLists();

	/**
	 * Constructor
	 */
	public DevicesConfig() {
		readDevicesFromClasspath();
		// readDevicesFromPropertiesFile();
		// readDevicesFromXMLFile();
	}

	private void readDevicesFromClasspath() {
		// find all classes extends class Device.class
		ClassFinder finder = new ClassFinder();
		finder.addClassPath();

		ClassFilter filter = new AndClassFilter(new NotClassFilter(new InterfaceOnlyClassFilter()), new SubclassClassFilter(Device.class),
				new NotClassFilter(new AbstractClassFilter()));

		Collection<ClassInfo> foundClasses = new ArrayList<ClassInfo>();
		finder.findClasses(foundClasses, filter);

		String classPrefix = "org.jsynthlib.synthdrivers.";
		for (ClassInfo classInfo : foundClasses) {
			String IDString = "NONE";

			if (classInfo.getClassName().startsWith(classPrefix)) {
				String deviceString = classInfo.getClassName().substring(classPrefix.length());
				// System.out.println("deviceClass " + deviceString);
				String[] deviceArr = deviceString.split("\\.");
				if (deviceArr.length >= 2) {
					deviceArr[0] = WordUtils.capitalize(deviceArr[0]);
					deviceArr[1] = WordUtils.capitalize(deviceArr[1]);

					String shortName = deviceArr[0] + deviceArr[1];
					String deviceName = deviceArr[1];
					String deviceClass = classInfo.getClassName();
					String manufacturer = deviceArr[0];
					String type = manufacturer.substring(0, 1);
					addDevice(deviceName, shortName, deviceClass, IDString, manufacturer, type);
				} else {
					// no org.jsynthlib.synthdrivers.<vendor>.<type> convention
					addDevice(classInfo.getClassName(), classInfo.getClassName(), classInfo.getClassName(), IDString, "Unknown", "U");
				}
			} else {
				// no org.jsynthlib.synthdrivers.<vendor>.<type> convention
				addDevice(classInfo.getClassName(), classInfo.getClassName(), classInfo.getClassName(), IDString, "Unknown", "U");
			}

		}

	}

	@Deprecated
	private void readDevicesFromPropertiesFile() {
		// Load properties file
		InputStream in = this.getClass().getResourceAsStream(JSynthResource.RESOURCE_NAME_DEVICES_CONFIG.getUri());
		Properties configProps = new Properties();
		try {
			configProps.load(in);
		} catch (IOException e) {
			ErrorMsg.reportError("Failed Loading Devices", "Failed loading devices property file. " + "Resource "
					+ JSynthResource.RESOURCE_NAME_DEVICES_CONFIG.getUri() + " cannot be loaded", e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// do nothing
			}
		}

		// Cycle through all of the items in the properties file to find
		// devicenames
		Enumeration propNames = configProps.propertyNames();
		while (propNames.hasMoreElements()) {
			String propName = (String) propNames.nextElement();
			if (propName.startsWith(Constants.PROP_PREFIX_DEVICE_NAME)) {
				// We found a devicename. Get the other properties for this
				// device
				String shortName = propName.substring(Constants.PROP_PREFIX_DEVICE_NAME.length());
				String deviceName = configProps.getProperty(propName);
				String deviceClass = configProps.getProperty(Constants.PROP_PREFIX_DEVICE_CLASS + shortName);
				String IDString = configProps.getProperty(Constants.PROP_PREFIX_ID_STRING + shortName);
				String manufacturer = configProps.getProperty(Constants.PROP_PREFIX_MANUFACTURER + shortName);
				// Since Devices don't have types yet, just use the first letter
				// of the manufacturer
				// so that we can test it.
				String type = manufacturer.substring(0, 1);

				// If the device has a classname and an IDstring, then add it to
				// the descriptors TreeSet
				if ((deviceClass != null) && (IDString != null)) {
					addDevice(deviceName, shortName, deviceClass, IDString, manufacturer, type);
				} else {
					ErrorMsg.reportError("Failed loading Devices", "Config file inconsistency found " + "for '" + shortName + "' ID string");
				}
			}
		}
	}

	/**
	 * This method makes a descriptor from the given parameters and adds it to the list of device descriptors
	 */
	private void addDevice(String deviceName, String shortName, String deviceClass, String IDString, String manufacturer, String type) {
		DeviceDescriptor descriptor = createDeviceDescriptor(deviceName, shortName, deviceClass, IDString, manufacturer, type);
		descriptors.add(descriptor);
		devicenameList.add(deviceName);
		IDStringList.add(IDString);
		manufacturerList.putIntoList(manufacturer, descriptor);
		typeList.putIntoList(type, descriptor);
	}

	private DeviceDescriptor createDeviceDescriptor(String deviceName, String shortName, String deviceClass, String IDString,
			String manufacturer, String type) {
		DeviceDescriptor descriptor = new DeviceDescriptor();
		descriptor.setDeviceName(deviceName);
		descriptor.setShortName(shortName);
		descriptor.setDeviceClass(deviceClass);
		descriptor.setIDString(IDString);
		descriptor.setManufacturer(manufacturer);
		descriptor.setType(type);
		return (descriptor);
	}

	public static final String getShortNameForClassName(String s) {
		String shortName;
		if (s.charAt(0) == XML_FILE_SEPARATOR) {
			int start = s.lastIndexOf(XML_FILE_SEPARATOR);
			int end = s.lastIndexOf(".xml");
			shortName = s.substring(start + 1, end) + "(XML)";
		} else {
			shortName = s.substring(s.lastIndexOf('.') + 1, s.lastIndexOf("Device"));
		}
		return shortName;
	}

	/**
	 * ==================================== ===== Methods that return lists of properties
	 * ====================================
	 */

	/**
	 * Return the device names (long name).
	 * 
	 * @return A Collection of all DeviceDescriptors in the devicelist
	 */
	public Collection<DeviceDescriptor> getDeviceDescriptors() {
		return (descriptors);
	}

	/**
	 * @return A Collection of all manufacturers in the devicelist as Strings
	 */
	Collection<String> getManufacturers() {
		return (manufacturerList.keySet());
	}

	/**
	 * @return A Collection of all types in the devicelist as Strings
	 */
	Collection getTypes() {
		return (typeList.keySet());
	}

	/**
	 * 
	 * @return A Collection of the names of all IDStrings in the devicelist as Strings
	 */
	public Collection getIDStrings() {
		return (IDStringList);
	}

	/**
	 * ==================================== ===== Methods that look up a descriptor by some property...
	 * ====================================
	 */

	private DeviceDescriptor getDeviceDescriptorForDeviceName(String deviceName) {
		DeviceDescriptor descriptor;
		for (Iterator i = descriptors.iterator(); i.hasNext();) {
			descriptor = (DeviceDescriptor) i.next();
			if (descriptor.getDeviceName().equals(deviceName)) {
				return (descriptor);
			}
		}
		return (null);
	}

	private DeviceDescriptor getDeviceDescriptorForIDString(String IDString) {
		DeviceDescriptor descriptor;
		for (Iterator i = descriptors.iterator(); i.hasNext();) {
			descriptor = (DeviceDescriptor) i.next();
			if (descriptor.getIDString().equals(IDString)) {
				return (descriptor);
			}
		}
		return (null);
	}

	private DeviceDescriptor getDeviceDescriptorForShortName(String shortName) {
		DeviceDescriptor descriptor;
		shortName = shortName.toUpperCase();
		for (Iterator i = descriptors.iterator(); i.hasNext();) {
			descriptor = (DeviceDescriptor) i.next();
			if (descriptor.getShortName().toUpperCase().equals(shortName)) {
				return (descriptor);
			}
		}
		return (null);
	}

	/**
	 * This takes a manufacturer by String and returns a Collection containing the DeviceDescriptor objects of all
	 * devices by that manufacturer
	 * 
	 * @param manufacturer
	 *            A string containing the manufacturer
	 * @return A Collection of DeviceDescriptors
	 */
	public Collection getDescriptorsForManufacturer(String manufacturer) {
		return (manufacturerList.getList(manufacturer));
	}

	/**
	 * This takes a type by String and returns a Collection containing the DeviceDescriptor objects of all devices of
	 * that type
	 * 
	 * @param type
	 *            A string containing the type
	 * @return A Collection of DeviceDescriptors
	 */
	public Collection getDescriptorsForType(String type) {
		return (typeList.getList(type));
	}

	/**
	 * ==================================== ===== Methods that return some property based upon another property...
	 * ====================================
	 */

	/**
	 * Given a inquery ID String, return its Device.
	 * 
	 * @param IDString
	 *            inquery ID String (ie, F07E..0602400000040000000000f7)
	 * @return the class name (ie, synthdrivers.KawaiK4.KawaiK4Device)
	 */
	public String getClassNameForIDString(String IDString) {
		DeviceDescriptor descriptor = getDeviceDescriptorForIDString(IDString);
		if (descriptor == null) {
			return (NOT_FOUND_STRING);
		}
		return (descriptor.getDeviceClass());
	}

	/**
	 * Given a short device name, i.e. "KawaiK4", return its class name
	 * 
	 * @param shortName
	 *            the short name of the device
	 * @return the class name (ie, synthdrivers.KawaiK4.KawaiK4Device)
	 */
	public String getClassNameForShortName(String shortName) {
		DeviceDescriptor descriptor = getDeviceDescriptorForShortName(shortName);
		if (descriptor == null) {
			ErrorMsg.reportStatus("not found " + shortName);
			return null;
		}
		return (descriptor.getDeviceClass());
	}

	/**
	 * Given a device name, i.e. "Kawai K4/K4R Driver", return its class name (ie, synthdrivers.KawaiK4.KawaiK4Device)
	 * 
	 * @param deviceName
	 *            the name of the device
	 * @return the class name
	 */
	public String getClassNameForDeviceName(String deviceName) {
		DeviceDescriptor descriptor = getDeviceDescriptorForDeviceName(deviceName);
		if (descriptor == null) {
			return (NOT_FOUND_STRING);
		}
		return (descriptor.getDeviceClass());
	}

	/**
	 * Given a device name, i.e. "Kawai K4/K4R Driver", return its manufacturer (ie, "Kawai").
	 * 
	 * @param deviceName
	 *            the name of the device
	 * @return the manufacturer
	 */
	String getManufacturerForDeviceName(String deviceName) {
		DeviceDescriptor descriptor = getDeviceDescriptorForDeviceName(deviceName);
		if (descriptor == null) {
			return (NOT_FOUND_STRING);
		}
		return (descriptor.getManufacturer());
	}

	public Device createDevice(String className, Preferences prefs) {
		if (className.charAt(0) == XML_FILE_SEPARATOR) {
			// className = className.replace(XML_FILE_SEPARATOR, File.separatorChar);
			// return XMLDeviceFactory.createDevice(className.substring(1), prefs);
			return null;
		} else {
			try {
				Device device;
				Class<?> c = Class.forName(className);
				Class[] args = { Class.forName("java.util.prefs.Preferences") };
				Constructor<?> con = c.getConstructor(args);
				device = (Device) con.newInstance(new Object[] { prefs });
				return device;
			} catch (Exception e) {
				ErrorMsg.reportError("Failed to create device", "Failed to create device of class '" + className + "'", e);
				return null;
			}
		}
	}

	/**
	 * Main method for debugging - print out all configured Devices
	 */
	public static void main(String[] args) {
		DevicesConfig devConf = new DevicesConfig();
		devConf.printAll();
	}

	/**
	 * Dump out all properties
	 */
	private void printAll() {
		DeviceDescriptor descriptor;
		for (Iterator i = descriptors.iterator(); i.hasNext();) {
			descriptor = (DeviceDescriptor) i.next();
		}
	}

	/**
	 * This class allows multiple objects to be stored under the same key in a hashtable. This is used to store multiple
	 * DeviceDescriptor objects under the same manufacturer or type
	 */
	class MapOfLists extends TreeMap {

		/**
		 * Adds the <code>val</code> object to the list referred to by <code>key</code>. If the list referred to by the
		 * key doesn't exist, it is created.
		 * 
		 * @param key
		 *            The key, usually a String
		 * @param val
		 *            The item to store in the list.
		 * @return
		 */
		public Object putIntoList(Object key, Object val) {
			LinkedList list = (LinkedList) get(key);
			if (list == null) {
				list = new LinkedList();
				put(key, list);
			}
			list.add(val);
			return (null);
		}

		/**
		 * Returns a LinkedList containing the iterms stored under <code>key</code>. If there are no items for that key,
		 * an empty LinkedList is returned.
		 * 
		 * @param key
		 *            The key to look up in the hashtable/map
		 * @return A LinkedList containing the elements for that key
		 */
		public LinkedList getList(Object key) {
			LinkedList list = (LinkedList) get(key);
			if (list == null) {
				list = new LinkedList();
			}
			return (list);
		}
	}
}
