package org.jsynthlib;

/**
 * Constants.java - Central place for definition of constants
 * 
 * @author Zellyn Hunter (zellyn@zellyn.com)
 * @version $Id$
 */
public class JSynthConstants {
	/** JSynthLib version number */
	public static final String VERSION = "0.21-alpha";
	public static final String VERSION_2 = "01";
	
	public static final String SYNTLIB_CLASS_PACKAGE_PREFIX = "org.jsynthlib.synthdrivers.";
	public static final String SYNTLIB_CLASS_GENERIC = "org.jsynthlib.synthdrivers.generic.GenericDevice";
	
	public static final String SYNTLIB_CLASS_PACKAGE = "01";

	/** App Config properties file name */
	public static final String FILE_NAME_APP_CONFIG = "JSynthLib.properties";
	/** App Config file header */
	public static final String APP_CONFIG_HEADER = "JSynthLib Saved Properties";

	/** Driver properties name prefix */
	public static final String PROP_PREFIX_DEVICE_NAME = "deviceName.";
	/** Device properties class prefix */
	public static final String PROP_PREFIX_DEVICE_CLASS = "deviceClass.";
	/** ID properties prefix */
	public static final String PROP_PREFIX_ID_STRING = "inquiryID.";
	/** manufacturer name prefix */
	public static final String PROP_PREFIX_MANUFACTURER = "manufacturer.";

	/** Number of faders */
	public static final int NUM_FADERS = 33;
		
	public static final int MIDI_SYSEX_WAIT_FOR_DATA = 10;
	public static final int maxCountForImportFromDirectory = Integer.MAX_VALUE;
	public static final int widthFileChooser =600;
	public static final int heightFileChooser =500;
	
	
}
