package org.jsynthlib.menu.patch;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.SysexMessage;
import javax.swing.JOptionPane;

import org.jsynthlib.menu.preferences.AppConfig;
import org.jsynthlib.menu.ui.JSLFrame;
import org.jsynthlib.menu.ui.PatchTransferHandler;
import org.jsynthlib.model.ManufacturerLookup;
import org.jsynthlib.tools.DriverUtil;
import org.jsynthlib.tools.ErrorMsg;
import org.jsynthlib.tools.HexDumpUtility;
import org.jsynthlib.tools.Utility;
import org.jsynthlib.tools.midi.MidiUtil;

/**
 * A class for MIDI System Exclusive Message patch data.
 * <p>
 * 
 * There are many kinds of constructors. Driver can use one of the follows (in preferred order).
 * <ol>
 * <li><code>Patch(byte[], Driver)</code>
 * <li><code>Patch(byte[], Device)</code>
 * <li><code>Patch(byte[])</code>
 * </ol>
 * 
 * Use <code>Patch(byte[], Driver)</code> form if possible. The latter two constructors <b>guesses </b> the proper
 * driver by using the <code>Driver.supportsPatch</code> method. It is not efficient.
 * <p>
 * 
 * Use <code>Patch(byte[])</code> only when you have no idea about either Driver or Device for which your Patch is. If
 * you know that the patch you are creating does not correspond to any driver, use
 * <code>Patch(byte[], (Driver) null)</code>, since it is much more efficient than <code>Patch(byte[])</code>.
 * 
 * @author ???
 * @version $Id$
 * @see Driver#supportsPatch
 */
public class Patch implements PatchSingle, PatchBank {
	/** Driver for this Patch. */
	private transient Driver driver;

	DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.ENGLISH);// new SimpleDateFormat("");
	/**
	 * MIDI System Exclusive Message byte array.
	 */
	private byte[] sysex;

	private StringBuffer fileName;

	private StringBuffer footprint;

	private StringBuffer info;

	private StringBuffer patchId;

	// 'String' is better. But 'StringBuffer' is used to keep
	// the compatibility for serialized files
	/** "Field 1" comment. */
	private StringBuffer date;

	/** "Field 2" comment. */
	private StringBuffer author;

	/** "Comment" comment. */
	private StringBuffer comment;

	// This is used by java to maintain backwords compatibility.
	static final long serialVersionUID = 2220769917598497681L;

	/**
	 * Constructor - Driver is known. This is often used by a Single Driver and its subclass.
	 * 
	 * @param gsysex
	 *            The MIDI SysEx message.
	 * @param driver
	 *            a <code>Driver</code> instance. If <code>null</code>, a null driver (Generic Driver) is used.
	 */
	public Patch(byte[] gsysex, Driver driver) {
		this(gsysex, driver, "");
	}

	public Patch(byte[] gsysex, Driver driver, String fileName) {
		date = new StringBuffer(df.format(Calendar.getInstance().getTime()));
		author = new StringBuffer(AppConfig.getRepositoryUser());
		comment = new StringBuffer("");
		setSysex(gsysex);
		setPatchId(UUID.randomUUID().toString());
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			byte[] digest = md.digest("abd".getBytes());

			String footprint = "";
			for (byte d : digest) {
				footprint += Integer.toHexString(d & 0xFF);
			}
			setFootprint(footprint);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		setFileName(fileName);
		setDriver(driver);
		// commented out not to break backward compatibility
		// driver.trimSysex(this);
	}

	/**
	 * Constructor - Device is known but Driver is not. This is often used by a Bank Driver and its subclass.
	 * 
	 * @param gsysex
	 *            The MIDI SysEx message.
	 * @param device
	 *            a <code>Device</code> instance.
	 */
	public Patch(byte[] gsysex, Device device) {
		date = new StringBuffer(df.format(Calendar.getInstance().getTime()));
		author = new StringBuffer(AppConfig.getRepositoryUser());
		comment = new StringBuffer("");
		setSysex(gsysex);
		setPatchId(UUID.randomUUID().toString());
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			byte[] digest = md.digest("abd".getBytes());

			String footprint = "";
			for (byte d : digest) {
				footprint += Integer.toHexString(d & 0xFF);
			}
			setFootprint(footprint);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		setDriver((Driver) DriverUtil.chooseDriver(getSysex(), device));
		driver.trimSysex(this);
	}

	/**
	 * Constructor - Either Device nor Driver is not known. Consider using <code>Patch(byte[], Driver)</code> or
	 * <code>Patch(byte[],
	 * Device)</code>. If you know that the patch you are creating does not correspond to any driver, use
	 * <code>Patch(byte[],
	 * (Driver) null)</code>, since it is much more efficient than this.
	 * 
	 * @param gsysex
	 *            The MIDI SysEx message.
	 */
	public Patch(byte[] gsysex) {
		date = new StringBuffer(df.format(Calendar.getInstance().getTime()));
		author = new StringBuffer(AppConfig.getRepositoryUser());
		comment = new StringBuffer("");
		setSysex(gsysex);
		setPatchId(UUID.randomUUID().toString());

		setDriver((Driver) DriverUtil.chooseDriver(getSysex()));
		driver.trimSysex(this);
	}

	// IPatch interface methods
	public final String getDate() {
		return date.toString();
	}

	public final void setDate(String date) {
		this.date = new StringBuffer(date);
	}

	public final String getAuthor() {
		return author.toString();
	}

	public final void setAuthor(String author) {
		this.author = new StringBuffer(author);
	}

	public final String getComment() {
		return comment.toString();
	}

	public final void setComment(String comment) {
		this.comment = new StringBuffer(comment);
	}

	public final Device getDevice() {
		return driver.getDevice();
	}

	public final IPatchDriver getDriver() {
		return driver;
	}

	public final void setDriver(IPatchDriver driver) {
		this.driver = (driver == null) ? (Driver) AppConfig.getNullDriver() : (Driver) driver;
	}

	public final void setDriver() {
		setDriver((IPatchDriver) DriverUtil.chooseDriver(getSysex()));
	}

	public final boolean hasNullDriver() {
		return driver == AppConfig.getNullDriver();
	}

	public String getPatchHeader() {
		return DriverUtil.getPatchHeader(getSysex());
	}

	public final String getName() {
		return driver.getPatchName(this);
	}

	public final void setName(String s) {
		driver.setPatchName(this, s);
	}

	public final boolean hasEditor() {
		return driver.hasEditor();
	}

	public final JSLFrame edit() {
		return driver.editPatch(this);
	}

	public final void send(int bankNum, int patchNum) {
		driver.calculateChecksum(this);
		driver.storePatch(this, bankNum, patchNum);
	}

	public final SysexMessage[] getMessages() {
		try {
			return MidiUtil.byteArrayToSysexMessages(getSysex());
		} catch (InvalidMidiDataException ex) {
			return null;
		}
	}

	public final byte[] export() {
		driver.calculateChecksum(this);
		return this.getSysex();
	}

	public final byte[] getByteArray() {
		return getSysex();
	}

	public int getSize() {
		return getSysex().length;
	}

	public String getType() {
		return driver.getPatchType();
	}

	public int getNameSize() {
		return driver.getPatchNameSize();
	}

	public final String lookupManufacturer() {
		return ManufacturerLookup.get(getSysex()[1], getSysex()[2], getSysex()[3]);
	}

	public final boolean isSinglePatch() {
		return driver.isSingleDriver();
	}

	public final boolean isBankPatch() {
		return driver.isBankDriver();
	}

	public void useSysexFromPatch(IPatch ip) {
		if (ip.getSize() != getSysex().length)
			throw new IllegalArgumentException();
		setSysex(ip.getByteArray());
	}

	// end of IPatch interface methods

	// ISinglePatch interface methods
	public final void play() {
		driver.playPatch(this);
	}

	public final void send() {
		driver.calculateChecksum(this);
		driver.sendPatch(this);
	}

	// end of ISinglePatch interface methods

	// IBankPatch interface methods
	public final int getNumPatches() {
		return ((BankDriver) driver).getNumPatches();
	}

	public final int getNumColumns() {
		return ((BankDriver) driver).getNumColumns();
	}

	public final void put(IPatch singlePatch, int patchNum) {
		if (((BankDriver) driver).canHoldPatch((Patch) singlePatch)) {
			((BankDriver) driver).putPatch(this, (Patch) singlePatch, patchNum);
		} else {
			JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	public final void delete(int patchNum) {
		((BankDriver) driver).deletePatch(this, patchNum);
	}

	public final PatchSingle get(int patchNum) {
		return ((BankDriver) driver).getPatch(this, patchNum);
	}

	public final String getName(int patchNum) {
		return ((BankDriver) driver).getPatchName(this, patchNum);
	}

	public final void setName(int patchNum, String name) {
		((BankDriver) driver).setPatchName(this, patchNum, name);
	}

	// Transferable interface methods

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
		if (flavor.match(PatchTransferHandler.PATCH_FLAVOR))
			return this;
		else
			throw new UnsupportedFlavorException(flavor);
	}

	public boolean isDataFlavorSupported(final DataFlavor flavor) {
		ErrorMsg.reportStatus("Patch.isDataFlavorSupported " + flavor);
		return flavor.match(PatchTransferHandler.PATCH_FLAVOR);
	}

	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { PatchTransferHandler.PATCH_FLAVOR };
	}

	// end of Transferable interface methods

	// Clone interface method
	public final Object clone() {
		try {
			Patch p = (Patch) super.clone();
			p.setSysex((byte[]) getSysex().clone());
			return p;
		} catch (CloneNotSupportedException e) {
			// Cannot happen -- we support clone, and so do arrays
			throw new InternalError(e.toString());
		}
	}

	// end of Clone interface method

	//
	// delegation methods
	//
	public final void calculateChecksum() {
		driver.calculateChecksum(this);
	}

	/**
	 * Dump byte data array. Only for debugging.
	 * 
	 * @return string like "[2,3] f0 a3 00"
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("[" + driver + "] " + Utility.hexDumpOneLine(getSysex(), 0, -1, 20));
		return buf.toString();
	}

	@Override
	public String getFileName() {
		return fileName.toString();
	}

	@Override
	public void setFileName(String fileName) {
		this.fileName = new StringBuffer(fileName);
	}

	@Override
	public String getPatchId() {
		return patchId.toString();
	}

	@Override
	public void setPatchId(String patchId) {
		this.patchId = new StringBuffer(patchId);
	}

	@Override
	public String getInfo() {
		return info.toString();
	}

	@Override
	public void setInfo(String info) {
		this.info = new StringBuffer(info);
	}

	public String getFootprint() {
		return footprint.toString();
	}

	public void setFootprint(String footprint) {
		this.footprint = new StringBuffer(footprint);
	}

	public byte[] getSysex() {
//		System.out.println("GET sysex in Patch " + Utility.hexDump(sysex, 0, -1, -1));
		return sysex;
	}

	public void setSysex(byte[] sysex) {
//		System.out.println("SET sysex in Patch" + Utility.hexDump(sysex, 0, -1, -1));
		this.sysex = sysex;
	}
}