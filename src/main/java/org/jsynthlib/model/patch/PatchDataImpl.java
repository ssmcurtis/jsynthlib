package org.jsynthlib.model.patch;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.SysexMessage;
import javax.swing.JOptionPane;

import org.jsynthlib.menu.JSLFrame;
import org.jsynthlib.menu.PatchTransferHandler;
import org.jsynthlib.menu.preferences.AppConfig;
import org.jsynthlib.model.JSynthManufacturerLookup;
import org.jsynthlib.model.device.Device;
import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.driver.SynthDriverPatch;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.tools.DriverUtil;
import org.jsynthlib.tools.ErrorMsgUtil;
import org.jsynthlib.tools.HexaUtil;
import org.jsynthlib.tools.MidiUtil;

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
 * @see SynthDriverPatchImpl#supportsPatch
 */
public class PatchDataImpl implements PatchSingle, PatchBank {
	/** Driver for this Patch. */
	private transient SynthDriverPatchImpl driver;
	SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");
	// DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.ENGLISH);// new SimpleDateFormat("");
	/**
	 * MIDI System Exclusive Message byte array.
	 */
	private byte[] sysex;

	private StringBuffer fileName;

	private StringBuffer footprint;

	private StringBuffer info;

	private StringBuffer patchId;

	private int score = 0;

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
	public PatchDataImpl(byte[] gsysex, SynthDriverPatchImpl driver) {
		this(gsysex, driver, "");
	}

	public PatchDataImpl(byte[] gsysex, SynthDriverPatchImpl driver, String fileName) {
		
		// INFO CREATE NEW PATCH OBJECT FOR DRIVER
		
		date = new StringBuffer(df.format(Calendar.getInstance().getTime()));
		author = new StringBuffer(AppConfig.getRepositoryUser());
		comment = new StringBuffer("");
		setSysex(gsysex);
		setPatchId(UUID.randomUUID().toString());

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
	public PatchDataImpl(byte[] gsysex, Device device) {
		
		// INFO CREATE NEW PATCH OBJECT FOR DEVICE
		
		date = new StringBuffer(df.format(Calendar.getInstance().getTime()));
		author = new StringBuffer(AppConfig.getRepositoryUser());
		comment = new StringBuffer("");
		setSysex(gsysex);
		setPatchId(UUID.randomUUID().toString());

		setDriver((SynthDriverPatchImpl) DriverUtil.chooseDriver(getSysex(), device));
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
	public PatchDataImpl(byte[] gsysex) {
		
		// INFO CREATE NEW PATCH AND ASSIGN DRIVER

		date = new StringBuffer(df.format(Calendar.getInstance().getTime()));
		author = new StringBuffer(AppConfig.getRepositoryUser());
		comment = new StringBuffer("");
		setSysex(gsysex);
		setPatchId(UUID.randomUUID().toString());

		setDriver((SynthDriverPatchImpl) DriverUtil.chooseDriver(getSysex()));
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

	public final SynthDriverPatch getDriver() {
		return driver;
	}

	public final void setDriver(SynthDriverPatch driver) {
		this.driver = (driver == null) ? (SynthDriverPatchImpl) AppConfig.getNullDriver() : (SynthDriverPatchImpl) driver;
	}

	public final void findDriver() {
		setDriver((SynthDriverPatch) DriverUtil.chooseDriver(getSysex()));
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
		return JSynthManufacturerLookup.get(getSysex()[1], getSysex()[2], getSysex()[3]);
	}

	public final boolean isSinglePatch() {
		return driver.isSingleDriver();
	}

	public final boolean isBankPatch() {
		return driver.isBankDriver();
	}

	public void useSysexFromPatch(Patch ip) {
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
		return ((SynthDriverBank) driver).getNumPatches();
	}

	public final int getNumColumns() {
		return ((SynthDriverBank) driver).getNumColumns();
	}

	public final void put(Patch singlePatch, int patchNum) {
		if (((SynthDriverBank) driver).canHoldPatch((PatchDataImpl) singlePatch)) {
			((SynthDriverBank) driver).putPatch(this, (PatchDataImpl) singlePatch, patchNum);
		} else {
			JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	public final void delete(int patchNum) {
		((SynthDriverBank) driver).deletePatch(this, patchNum);
	}

	public final PatchSingle get(int patchNum) {
		return ((SynthDriverBank) driver).getPatch(this, patchNum);
	}

	public final String getName(int patchNum) {
		return ((SynthDriverBank) driver).getPatchName(this, patchNum);
	}

	public final void setName(int patchNum, String name) {
		((SynthDriverBank) driver).setPatchName(this, patchNum, name);
	}

	// Transferable interface methods

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
		if (flavor.match(PatchTransferHandler.PATCH_FLAVOR))
			return this;
		else
			throw new UnsupportedFlavorException(flavor);
	}

	public boolean isDataFlavorSupported(final DataFlavor flavor) {
		ErrorMsgUtil.reportStatus("Patch.isDataFlavorSupported " + flavor);
		return flavor.match(PatchTransferHandler.PATCH_FLAVOR);
	}

	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { PatchTransferHandler.PATCH_FLAVOR };
	}

	// end of Transferable interface methods

	// Clone interface method
	public final PatchDataImpl clone() {
		try {
			PatchDataImpl p = (PatchDataImpl) super.clone();
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
		buf.append("[" + driver + "] " + HexaUtil.hexDumpOneLine(getSysex(), 0, -1, 20));
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
		// System.out.println("GET sysex in Patch " + Utility.hexDump(sysex, 0, -1, -1));
		return sysex;
	}

	public void setSysex(byte[] sysex) {
		// System.out.println("SET sysex in Patch" + Utility.hexDump(sysex, 0, -1, -1));
		this.sysex = sysex;
	}

	@Override
	public int getPatchSize() {
		return driver.getPatchSize();
	}

	@Override
	public Integer getScore() {
		return score;
	}

	@Override
	public void addScore(int score) {
		this.score += score;
	}

	@Override
	public void setScore(int score) {
		this.score = score;
	}
}