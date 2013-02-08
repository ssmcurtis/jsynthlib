package org.jsynthlib.model.driver;

import org.jsynthlib.menu.JSLFrame;
import org.jsynthlib.menu.window.BankEditorFrame;
import org.jsynthlib.model.patch.PatchDataImpl;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * This is an implementation of IBankDriver and the base class for bank drivers which use <code>Patch<IPatch>.<p>
 */
abstract public class SynthDriverBank extends SynthDriverPatchImpl {
	/**
	 * The Number of Patches the Bank holds.
	 */
	private final int numPatches;
	/**
	 * How many columns to use when displaying the patches as a table.
	 */
	private final int numColumns;

	// for default canHoldPatch
	/**
	 * The Sysex header for the patches which go in this bank. This should be same value as the <code>sysexID</code>
	 * field of the single driver. It can be up to 16 bytes and have wildcards (<code>*</code>). (ex.
	 * <code>"F041.*003F12"</code>)
	 * 
	 * @see SynthDriverPatchImpl#sysexID
	 * @see #canHoldPatch
	 */
	// This can be "private static final".
	protected String singleSysexID;
	/**
	 * The size of the patches which go in this bank.
	 * 
	 * @see #canHoldPatch
	 */
	// This can be "private static final".
	protected int singleSize;

	private PatchDataImpl currentBank = null;
	
	private byte[] lastSysex = null;
	

	/**
	 * Creates a new <code>BankDriver</code> instance.
	 * 
	 * @param patchType
	 *            The patch type. eg. "Bank", "Multi Bank", "Drum Bank", etc.
	 * @param authors
	 *            The names of the authors of this driver.
	 * @param numPatches
	 *            The Number of Patches the Bank holds.
	 * @param numColumns
	 *            How many columns to use when displaying the patches as a table.
	 */
	public SynthDriverBank(String patchType, String authors, int numPatches, int numColumns) {
		super(patchType, authors);
		this.numPatches = numPatches;
		this.numColumns = numColumns;
	}

	//
	// IDriver interface methods
	//
	public final boolean isSingleDriver() {
		return false;
	}

	public final boolean isBankDriver() {
		return true;
	}

	public final boolean isConverter() {
		return false;
	}

	// end of IDriver methods

	//
	// IPatchDriver interface methods
	//
	/**
	 * Store the bank to a given bank on the synth. Ignores the patchNum parameter. Should probably be overridden in
	 * most drivers
	 * 
	 * @see PatchDataImpl#send(int, int)
	 */
	public void storePatch(PatchDataImpl bank, int bankNum, int patchNum) {
		setBankNum(bankNum);
		super.sendPatch(bank);
	}

	/**
	 * @see PatchDataImpl#hasEditor()
	 */
	public boolean hasEditor() {
		return true;
	}

	/**
	 * Creates a default bank editor window to edit this bank.
	 * 
	 * @see PatchDataImpl#edit()
	 */
	public JSLFrame editPatch(PatchDataImpl bank) {
		return new BankEditorFrame(bank);
	}

	//
	// for IPatch interface methods
	//
	/**
	 * Get name of the bank.
	 * 
	 * @see PatchDataImpl#getName()
	 */
	public String getPatchName(PatchDataImpl bank) {
		// Most Banks have no name.
		return "-";
	}

	/**
	 * Set name of the bank.
	 * 
	 * @see PatchDataImpl#setName(String)
	 */
	public void setPatchName(PatchDataImpl bank, String name) {
		// Most Banks have no name.
	}

	// end of IPatch interface methods
	//
	// for IBankPatch interface methods
	//
	/**
	 * @see PatchDataImpl#getNumPatches()
	 */
	public final int getNumPatches() {
		return numPatches;
	}

	/**
	 * @see PatchDataImpl#getNumColumns()
	 */
	public final int getNumColumns() {
		return numColumns;
	}

	/**
	 * Compares the header & size of a Single Patch to this driver to see if this bank can hold the patch.
	 * 
	 * @see PatchDataImpl#put(IPatch, int)
	 * @see SynthDriverPatchImpl#supportsPatch
	 */
	public boolean canHoldPatch(PatchDataImpl p) {
		if ((singleSize != p.getSysex().length) && (singleSize != 0))
			return false;

		String patchString = p.getPatchHeader().toString();
		StringBuffer driverString = new StringBuffer(singleSysexID);
		for (int j = 0; j < driverString.length(); j++)
			if (driverString.charAt(j) == '*')
				driverString.setCharAt(j, patchString.charAt(j));
		return (driverString.toString().equalsIgnoreCase(patchString.substring(0, driverString.length())));
	}

	/**
	 * Puts a patch into the bank, converting it as needed. <code>single</code> is already checked by
	 * <code>canHoldPatch</code>, although it was not.
	 * 
	 * @see PatchDataImpl#put(IPatch, int)
	 */
	public abstract void putPatch(PatchDataImpl bank, PatchDataImpl single, int patchNum);

	/**
	 * Delete a patch.
	 * 
	 * @see PatchDataImpl#delete(int)
	 */
	public void deletePatch(PatchDataImpl single, int patchNum) {
		setPatchName(single, patchNum, "          ");
	}

	/**
	 * Gets a patch from the bank, converting it as needed.
	 * 
	 * @see PatchDataImpl#get(int)
	 */
	public abstract PatchDataImpl extractPatch(PatchDataImpl bank, int patchNum);

	/**
	 * Get the name of the patch at the given number <code>patchNum</code>.
	 * 
	 * @see PatchDataImpl#getName(int)
	 */
	public abstract String getPatchName(PatchDataImpl bank, int patchNum);

	/**
	 * Set the name of the patch at the given number <code>patchNum</code>.
	 * 
	 * @see PatchDataImpl#setName(int, String)
	 */
	public abstract void setPatchName(PatchDataImpl bank, int patchNum, String name);

	/**
	 * MUST overwrite this for init currentBank (if null) using correct byte[] size and put singlePatch
	 * 
	 * @param patchNum
	 * @param name
	 */
	public void addToCurrentBank(PatchDataImpl singlePatch, int patchNum) {
		throw new NotImplementedException();
	}

	public void sendCurrentbank() {
		if (currentBank != null) {
			storePatch(currentBank, 0, 0);
		}
	}

	public void resetCurrentbank() {
		currentBank = null;
	}

	public PatchDataImpl getCurrentBank() {
		return currentBank;
	}

	public void setCurrentBank(PatchDataImpl currentBank) {
		this.currentBank = currentBank;
	}


	public byte[] getLastSysex() {
		return lastSysex;
	}

	/**
	 * Keep last patch for fill bank before send.
	 * 
	 * @param lastSysex
	 */
	public void setLastSysex(byte[] lastSysex) {
		this.lastSysex = lastSysex;
	}

	// end of IBankDriver methods

}
