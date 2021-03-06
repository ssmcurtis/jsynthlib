package org.jsynthlib.synthdrivers.roland.gp16;

import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;

import org.jsynthlib.model.driver.NameValue;
import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.DriverUtil;
import org.jsynthlib.tools.ErrorMsgUtil;

/**
 * Bank driver for ROLAND GP16.
 * 
 * @version $Id$
 */
public class RolandGP16BankDriver extends SynthDriverBank {
	/** Header Size */
	private static final int HSIZE = 5;
	/** Single Patch size */
	private static final int SSIZE = 121;
	/** The number of single patches in a bank patch. */
	private static final int NS = 8;
	/** The sysex message sent when requesting a patch (from a bank). */
	private static final SysexHandler SYS_REQ = new SysexHandler(
			"F0 41 @@ 2A 11 0B *patchnumber* 00 00 00 75 *checksum* F7");
	/** Time to sleep when doing sysex data transfers. */
	private static final int sleepTime = 100;
	/** Single Driver for GP16 */
	private RolandGP16SingleDriver singleDriver;

	/** The constructor. */
	public RolandGP16BankDriver(RolandGP16SingleDriver singleDriver) {
		super("Bank", "Mikael Kurula", NS, 2);
		this.singleDriver = singleDriver;

		sysexID = "F041**2A";
		deviceIDoffset = 2;

		bankNumbers = new String[NS * 2];
		System.arraycopy(DriverUtil.generateNumbers(1, NS, "Group A - Bank ##"), 0, bankNumbers, 0, NS);
		System.arraycopy(DriverUtil.generateNumbers(1, NS, "Group B - Bank ##"), 0, bankNumbers, NS, NS);
		patchNumbers = new String[NS * 1];
		System.arraycopy(DriverUtil.generateNumbers(1, NS, "Patch ##"), 0, patchNumbers, 0, NS);

		singleSysexID = sysexID;
		singleSize = HSIZE + SSIZE + 1;
		// To distinguish from a single patch, which has the same sysexID
		patchSize = singleSize * NS;
	}

	/** Return the starting index of a given patch in the bank. */
	public int getPatchStart(int patchNum) {
		return singleSize * patchNum;
	}

	/** Get patch names in bank for bank edit view. */
	public String getPatchName(PatchDataImpl p, int patchNum) {
		int nameStart = getPatchStart(patchNum);
		nameStart += 108; // offset of name in patch data
		try {
			StringBuffer s = new StringBuffer(new String(((PatchDataImpl) p).getSysex(), nameStart, 16, "US-ASCII"));
			return s.toString();
		} catch (UnsupportedEncodingException ex) {
			return "-";
		}
	}

	/** Set patch names in bank for bank edit view. */
	public void setPatchName(PatchDataImpl p, int patchNum, String name) {
		patchNameSize = 16;
		patchNameStart = getPatchStart(patchNum) + 108;

		if (name.length() < patchNameSize)
			name = name + "                ";
		byte[] namebytes = new byte[NS];
		try {
			namebytes = name.getBytes("US-ASCII");
			for (int i = 0; i < patchNameSize; i++)
				((PatchDataImpl) p).getSysex()[patchNameStart + i] = namebytes[i];

		} catch (UnsupportedEncodingException ex) {
			return;
		}
	}

	/** Calculate the checksum for all patches in the bank. */
	public void calculateChecksum(PatchDataImpl p) {
		for (int i = 0; i < NS; i++)
			calculateChecksum(p, i * singleSize + 5, i * singleSize + 124, i * singleSize + 125);
	}

	/** Insert a given patch into a given position of a given bank. */
	public void putPatch(PatchDataImpl bank, PatchDataImpl p, int patchNum) {
		if (!canHoldPatch(p)) {
			JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		System.arraycopy(((PatchDataImpl) p).getSysex(), 0, ((PatchDataImpl) bank).getSysex(), getPatchStart(patchNum), singleSize);
		calculateChecksum(bank);
	}

	/** Extract a given patch from a given bank. */
	public PatchDataImpl extractPatch(PatchDataImpl bank, int patchNum) {
		byte[] sysex = new byte[singleSize];
		System.arraycopy(((PatchDataImpl) bank).getSysex(), getPatchStart(patchNum), sysex, 0, singleSize);
		try {
			PatchDataImpl p = new PatchDataImpl(sysex, getDevice());
			singleDriver.calcChecksum(p);
			return p;
		} catch (Exception e) {
			ErrorMsgUtil.reportError("Error", "Error in GP16 Bank Driver", e);
			return null;
		}
	}

	/** A nice bank dump of the GP-16 is just all patches dumped one by one, with correct memory address. */
	public void requestPatchDump(int bankNum, int patchNum) {
		for (int i = 0; i < NS; i++)
			requestSinglePatchDump(bankNum, i);
	}

	/** Send the bank back as it was received. */
	public void storePatch(PatchDataImpl bank, int bankNum, int patchNum) {
		for (int i = 0; i < NS; i++) {
			PatchDataImpl p = extractPatch(bank, i);
			storeSinglePatch(p, bankNum, i);
		}
	}

	/** Worker for requestPatchDump. */
	public void requestSinglePatchDump(int bankNum, int patchNum) {
		try {
			Thread.sleep(sleepTime);
		} catch (Exception e) {
		}
		NameValue nVs[] = new NameValue[2];
		nVs[0] = new NameValue("patchnumber", bankNum * 8 + patchNum);
		nVs[1] = new NameValue("checksum", 0);
		PatchDataImpl p = new PatchDataImpl(SYS_REQ.toByteArray(getChannel(), nVs));
		calculateChecksum(p, 5, 10, 11); // the gp-16 requires correct checksum when requesting a patch
		send(p.getSysex());
		try {
			Thread.sleep(sleepTime);
		} catch (Exception e) {
		}
	}

	/** Worker for storePatch. */
	public void storeSinglePatch(PatchDataImpl p, int bankNum, int patchNum) {
		((PatchDataImpl) p).getSysex()[5] = (byte) 0x0B;
		((PatchDataImpl) p).getSysex()[6] = (byte) (bankNum * 8 + patchNum);
		((PatchDataImpl) p).getSysex()[7] = (byte) 0x00;
		sendPatchWorker(p);
		try {
			Thread.sleep(sleepTime);
		} catch (Exception e) {
		}
	}

	/** Create a new bank, that conforms to the format of the GP-16. */
	public PatchDataImpl createNewPatch() {
		byte[] sysex = new byte[NS * singleSize];

		RolandGP16SingleDriver patchCreator = new RolandGP16SingleDriver();
		PatchDataImpl blankPatch = patchCreator.createNewPatch();
		for (int i = 0; i < NS; i++)
			System.arraycopy(((PatchDataImpl) blankPatch).getSysex(), 0, sysex, getPatchStart(i), singleSize);
		PatchDataImpl p = new PatchDataImpl(sysex, this);
		calculateChecksum(p);
		return p;
	}

	/** The name string of the GP-16 is 16 characters long. */
	public void deletePatch(PatchDataImpl p, int patchNum) {
		setPatchName(p, patchNum, "                ");
	}

	/** Smarter bank naming, name the bank after the first patch in it. */
	public String getPatchName(PatchDataImpl p) {
		return getPatchName(p, 0);
	}

}
