package core;
import java.io.*;
import java.awt.datatransfer.*;

/**
 * A class for MIDI System Exclusive Message patch data.<p>
 *
 * There are many kinds of constructors.  Driver can use one of
 * the follows (in preferred order).
 * <ol>
 * <li> <code>Patch(byte[], Driver)</code>
 * <li> <code>Patch(byte[], Device)</code>
 * <li> <code>Patch(byte[])</code>
 * </ol>
 *
 * The latter two constructors guess the proper driver by using the
 * <code>Driver.supportsPatch</code> method to set
 * <code>deviceNum</code> and <code>driverNum</code> fields.  If you
 * know that the patch you are creating does not correspond to any
 * driver, use <code>Patch(byte[], (Driver) null)</code>, since it is
 * much more efficient than <code>Patch(byte[])</code>.
 *
 * @author ???
 * @version $Id$
 * @see Driver#supportsPatch
 */
public class Patch extends Object implements Serializable, Transferable {
    /**
     * device number.
     * Use <code>patch.getDriver()</code> instead of
     * <code>PatchEdit.getDriver(patch.deviceNum, patch.driverNum)</code>.
     * Use <code>patch.getDevice()</code> instead of
     * <code>PatchEdit.appConfig.getDevice(patch.deviceNum)</code>.
     */
    public transient int deviceNum;
    /**
     * driver number.
     * Use <code>patch.getDriver()</code> instead of
     * <code>PatchEdit.getDriver(patch.deviceNum, patch.driverNum)</code>.
     */
    public transient int driverNum;
    /**
     * MIDI System Exclusive Message byte array.
     */
    public byte[] sysex ;

    // Why StringBuffer?  And for what are those?
    /** a comment. */
    StringBuffer date;
    /** another comment. */
    StringBuffer author;
    /** a last comment. */
    StringBuffer comment;

    // not used.  What's this?
    static final long serialVersionUID = 2220769917598497681L;

    /**
     * Constructor - Driver is known.  This is often used by a Single
     * Driver and its subclass.
     * @param gsysex The MIDI SysEx message.
     * @param driver a <code>Driver</code> instance.
     */
    public Patch(byte[] gsysex, Driver driver) {
	this(gsysex,
	     (driver == null) ? 0 : driver.getDeviceNum(),
	     (driver == null) ? 0 : driver.getDriverNum());
    }

    /**
     * Constructor - Device is known but Driver is not.  This is often
     * used by a Bank Driver and its subclass.
     * @param gsysex The MIDI SysEx message.
     * @param device a <code>Device</code> instance.
     */
    public Patch(byte[] gsysex, Device device) {
	this(device.getDeviceNum(), gsysex);
    }

    /**
     * Constructor - all parameters are unknown.
     */
    // replaced by 'Patch(new byte[1024], (Driver) null)'.
//   Patch() {			// called by Scene
// 	this(new byte[1024]);
//   }

    /**
     * Constructor - only sysex is known.  Consider using
     * <code>Patch(byte[], Driver)</code> or <code>Patch(byte[],
     * Device)</code>.  If you know that the patch you are creating
     * does not correspond to any driver, use <code>Patch(byte[],
     * (Driver) null)</code>, since it is much more efficient than
     * this.
     * @param gsysex The MIDI SysEx message.
     */
    // @deprecated Use <code>Patch(byte[], Driver)</code> or
    // <code>Patch(byte[], Device)</code>.
    public Patch(byte[] gsysex) {
        comment = new StringBuffer();
        date = new StringBuffer();
        author = new StringBuffer();
        sysex = gsysex;
        ChooseDriver();
    }

    /**
     * Constructor - The device number is known, but not the driver
     * number.  Consider using <code>Patch(byte[], Device)</code>.
     * @param deviceNum The known device number.
     * @param gsysex The MIDI SysEx message.
     */
    // @deprecated Use <code>Patch(byte[], Device)</code>.
    // The signature Patch(byte[], int) is conflict with Patch(byte[]
    // gsysex, int offset).  A kind of ugry...
    public Patch(int deviceNum, byte[] gsysex) {
	comment = new StringBuffer();
	date = new StringBuffer();
	author = new StringBuffer();
	sysex = gsysex;
 	this.deviceNum = deviceNum;
        ChooseDriver(deviceNum);
    }

    /**
     * Constructor - Device and driver number are known.  Consider
     * using <code>Patch(byte[], Driver)</code>.
     * @param gsysex The MIDI SysEx message.
     * @param deviceNum The known device number.
     * @param driverNum The known driver number.
     */
    // @deprecated Use <code>Patch(byte[], Driver)</code>.
    public Patch(byte[] gsysex, int deviceNum, int driverNum) {
	comment = new StringBuffer();
	date = new StringBuffer();
	author = new StringBuffer();
	sysex = gsysex;
 	this.deviceNum = deviceNum;
 	this.driverNum = driverNum;
    }

    /**
     * Constructor - only sysex is known.
     * @param gsysex The MIDI SysEx message.
     * @param offset offset address in <code>gsysex</code>.
     */
    // called by LibraryFrame and SceneFrame
    Patch(byte[] gsysex, int offset) {
	comment = new StringBuffer();
        date = new StringBuffer();
        author = new StringBuffer();
        sysex = new byte[gsysex.length - offset];
        System.arraycopy(gsysex, offset, sysex, 0, gsysex.length - offset);
        ChooseDriver();
    }

    /**
     * Constructor
     * @param gsysex The MIDI SysEx message.
     * @param gdate A comment.
     * @param gauthor Another comment.
     * @param gcomment A last comment.
     */
    // called by BankEditorFrame and PatchEditorFrame
    Patch(byte[] gsysex,
	  String gdate, String gauthor, String gcomment) {
        this.comment = new StringBuffer(gcomment);
        this.date = new StringBuffer(gdate);
        this.author = new StringBuffer(gauthor);
        this.sysex = gsysex;
        ChooseDriver();
    }

    /**
     * Constructor - all parameters are known
     * @param gsysex The MIDI SysEx message.
     * @param deviceNum The known device number.
     * @param driverNum The known driver number.
     * @param gdate A comment.
     * @param gauthor Another comment.
     * @param gcomment A last comment.
     */
    // called by LibraryFrame and SceneFrame
    Patch(byte[] gsysex, int deviceNum, int driverNum,
	  String gdate, String gauthor, String gcomment) {
	this.comment = new StringBuffer(gcomment);
	this.date = new StringBuffer(gdate);
	this.author = new StringBuffer(gauthor);
	this.sysex = gsysex;
 	this.deviceNum = deviceNum;
 	this.driverNum = driverNum;
    }

    /**
     * Set <code>driverNum</code> by guessing from <code>sysex</code>
     * by using <code>Driver.suportsPatch</code> method.
     * @param deviceNum The known device number
     * @see Driver#supportsPatch
     */
    private void ChooseDriver(int deviceNum) {
        this.deviceNum = deviceNum;
        StringBuffer patchString = this.getPatchHeader();

        Device dev = PatchEdit.appConfig.getDevice(deviceNum);
        for (int idrv = 0; idrv < dev.driverList.size(); idrv++) {
	    // iterating over all Drivers of the given device
	    if (((Driver) dev.driverList.get(idrv)).supportsPatch(patchString, this)) {
		this.driverNum = idrv;
		getDriver().trimSysex(this);
		return;
	    }
        }
        driverNum = 0;
        // Unkown patch, try to guess at least the manufacturer
        comment = new StringBuffer("Probably a "
				   + LookupManufacturer.get(sysex[1], sysex[2], sysex[3])
				   + " Patch, Size: " + sysex.length);
    }

    /**
     * Set <code>deviceNum</code> and <code>driverNum</code> by
     * guessing from <code>sysex</code> by using
     * <code>Driver.suportsPatch</code> method.
     */
    public void ChooseDriver() { // should be chooseDriver()
        //Integer intg = new Integer(0);
        //StringBuffer driverString = new StringBuffer();
        StringBuffer patchString = this.getPatchHeader();

        for (int idev = 0; idev < PatchEdit.appConfig.deviceCount(); idev++) {
            // Outer Loop, iterating over all installed devices
	    Device dev = PatchEdit.appConfig.getDevice(idev);
	    for (int idrv = 0; idrv < dev.driverList.size(); idrv++) {
                // Inner Loop, iterating over all Drivers of a device
		if (((Driver) dev.driverList.get(idrv)).supportsPatch(patchString, this)) {
		    driverNum = idrv;
		    deviceNum = idev;
                    getDriver().trimSysex(this);
                    return;
                }
            }
        }
        driverNum = 0;
        deviceNum = 0;
        // Unkown patch, try to guess at least the manufacturer
        comment = new StringBuffer("Probably a "
				   + LookupManufacturer.get(sysex[1], sysex[2], sysex[3])
				   + " Patch, Size: " + sysex.length);
    }

    /** Return Device for this patch. */
    public Device getDevice() {
	return PatchEdit.appConfig.getDevice(deviceNum);
    }

    /** Return Driver for this patch. */
    public Driver getDriver() {
	return PatchEdit.getDriver(deviceNum, driverNum);
    }

    // Transferable interface methods

    public Object getTransferData(java.awt.datatransfer.DataFlavor p1)
	throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException {
        return this;
    }

    public boolean isDataFlavorSupported(final java.awt.datatransfer.DataFlavor p1) {
        // System.out.println("isDataFlavorSupported "+driverNum);
        if (p1.equals(new DataFlavor(getDriver().getClass(), getDriver().toString()))) {
            return true;
        }
        return false;
    }

    public java.awt.datatransfer.DataFlavor[] getTransferDataFlavors() {
        // System.out.println("getTransferDataFlavors "+driverNum);
        DataFlavor[] df = new DataFlavor[1];
	df[0] = new DataFlavor(getDriver().getClass(), getDriver().toString());

        return df;
    }

    // end of Transferable interface methods

    /**
     * Dissect a <code>Patch</code> which has a <code>Converter</code>
     * driver into an array of <code>Patch</code>.  Each patch in the
     * original patch must be for a same Device, but may be for some
     * different Drivers.
     *
     * @return a <code>Patch[]</code> value
     * @see Converter
     */
    // called by ImportAllDialog, ImportMidiFile, SysexGetDialog,
    // LibraryFrame, and SceneFrame.
    Patch[] dissect() {
	Patch[] patarray;
    search:
	{
	    StringBuffer patchString = this.getPatchHeader();

	    // When a Converter is found 'deviceNum' is used.  Why do
	    // we need this outermost loop?  - Hiroo
	    for (int idev = 0; idev < PatchEdit.appConfig.deviceCount(); idev++) {
		// Do it for all converters. They should be at the
		// beginning of the driver list!
		Device dev = PatchEdit.appConfig.getDevice(idev);
		for (int idrv = 0; idrv < dev.driverList.size(); idrv++) {
		    Driver drv = (Driver) dev.driverList.get(idrv);
		    if ((drv instanceof Converter)
			&& drv.supportsPatch(patchString, this)) {
			patarray = ((Converter) drv).extractPatch(this);
			if (patarray != null)
			    break search; // found!
		    }
		}
	    } //    idev++;
	    // No conversion. Try just the original patch....
	    return new Patch[] {this};
	}
	// Conversion was sucessfull, we have at least one
	// converted patch assign the original deviceNum and
	// individual driverNum to each patch of patarray
	for (int i = 0; i < patarray.length; i++) {
	    // set deviceNum field
	    patarray[i].deviceNum = deviceNum; // Can we use 'dev' for this instance?

	    // set driverNum field
	    Device dev = PatchEdit.appConfig.getDevice(deviceNum);
	    StringBuffer patchString = patarray[i].getPatchHeader();
	    for (int jdrv = 0; jdrv < dev.driverList.size(); jdrv++) {
		Driver drv = (Driver) dev.driverList.get(jdrv);
		if (drv.supportsPatch(patchString, patarray[i]))
		    patarray[i].driverNum = jdrv;
	    }
	}
	return patarray;
    }

    /**
     * Return a hexadecimal string for Driver.supportsPatch at most 16
     * byte sysex data.
     * @see Driver#supportsPatch
     */
    StringBuffer getPatchHeader() {
	StringBuffer patchstring = new StringBuffer("F0");

	// Some Sysex Messages are shorter than 16 Bytes!
// 	for (int i = 1; (sysex.length < 16) ? i < sysex.length : i < 16; i++) {
	for (int i = 1; i < Math.min(16, sysex.length); i++) {
	    if ((int) (sysex[i] & 0xff) < 0x10)
		patchstring.append("0");
	    patchstring.append(Integer.toHexString((int) (sysex[i] & 0xff)));
	}
	return patchstring;
    }

    /**
     * Dump byte data array.  Only for debugging.
     *
     * @return string like "[2,3] f0 a3 00 "
     */
    public String toString() {
	StringBuffer buf = new StringBuffer();
	buf.append("[" + deviceNum + "," + driverNum + "] ");
	for (int i = 0; i < sysex.length; i++) {
	    if ((int) (sysex[i] & 0xff) < 0x10)
		buf.append("0");
	    buf.append(Integer.toHexString((int) (sysex[i] & 0xff)) + " ");
	}
	return buf.toString();
    }
}
