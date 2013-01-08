package org.jsynthlib.synthdrivers.roland.gp16;

import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;

import org.jsynthlib.model.driver.NameValue;
import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

/**
 * All-memory driver for ROLAND GP16.
 * 
 * @version $Id$
 */
public class RolandGP16AllDriver extends SynthDriverBank {
	/** Header Size */
	private static final int HSIZE = 5;
	/** Single Patch size */
	private static final int SSIZE = 121;
	/** The number of group patches in an all-memory patch. */
	private static final int NS = 2;
	/** The sysex message sent when requesting a patch (from all memory). */
	private static final SysexHandler SYS_REQ = new SysexHandler(
			"F0 41 @@ 2A 11 0F *patchnumber* 00 00 00 75 *checksum* F7");
	/** Time to sleep when doing sysex data transfers. */
	private static final int sleepTime = 100;
	/** Single Driver for GP16 */
	private RolandGP16SingleDriver singleDriver;

	/** The constructor. */
	public RolandGP16AllDriver(RolandGP16SingleDriver singleDriver) {
		super("All", "Mikael Kurula", NS, 2);
		this.singleDriver = singleDriver;

		sysexID = "F041**2A";
		deviceIDoffset = 2;

		bankNumbers = new String[1];
		bankNumbers[0] = "All Memory";
		patchNumbers = new String[] { "Group A", "Group B" };

		singleSysexID = sysexID;
		singleSize = (HSIZE + SSIZE + 1) * 64;
		// To distinguish from a bank, which has the same sysexID
		patchSize = singleSize * NS;
	}

	/** Return the starting index of a given group in the memory patch. */
	public int getPatchStart(int patchNum) {
		return singleSize * patchNum;
	}

	/** Get group names in the memory patch for memory edit view. */
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

	public void setPatchName(PatchDataImpl bank, int patchNum, String name) {
		// do nothing
	}

	/** Calculate the checksum for all patches in the memory. */
	public void calculateChecksum(PatchDataImpl p) {
		for (int i = 0; i < 64 * NS; i++)
			calculateChecksum(p, i * 127 + 5, i * 127 + 124, i * 127 + 125);
	}

	/** Insert a given group into a given position of a given memory patch. */
	public void putPatch(PatchDataImpl bank, PatchDataImpl p, int patchNum) {
		if (!canHoldPatch(p)) {
			JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		System.arraycopy(((PatchDataImpl) p).getSysex(), 0, ((PatchDataImpl) bank).getSysex(), getPatchStart(patchNum), singleSize);
		calculateChecksum(bank);
	}

	/** Extract a given group from a given memory patch. */
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

	/** A nice memory dump of the GP-16 is just all patches dumped one by one, with correct memory address. */
	public void requestPatchDump(int bankNum, int patchNum) {
		for (int i = 0; i < NS; i++)
			requestSingleGroupDump(0, i);
	}

	/** Send the memory patch back as it was received. */
	public void storePatch(PatchDataImpl group, int groupNum, int bankNum) {
		for (int i = 0; i < NS; i++) {
			PatchDataImpl p = extractPatch(group, i);
			storeSingleGroup(p, i, 0);
		}
	}

	/** Worker for requestPatchDump. */
	public void requestSingleGroupDump(int groupNum, int bankNum) {
		for (int i = 0; i < 64; i++) {
			try {
				Thread.sleep(sleepTime);
			} catch (Exception e) {
			}
			NameValue nVs[] = new NameValue[2];
			nVs[0] = new NameValue("patchnumber", groupNum * 128 + bankNum * 64 + i);
			nVs[1] = new NameValue("checksum", 0);
			PatchDataImpl p = new PatchDataImpl(SYS_REQ.toByteArray(getChannel(), nVs));
			calculateChecksum(p, 5, 10, 11); // the gp-16 requires correct checksum when requesting a patch
			send(p.getSysex());
			try {
				Thread.sleep(sleepTime);
			} catch (Exception e) {
			}
		}
	}

	/** Worker for storePatch. */
	public void storeSingleGroup(PatchDataImpl p, int groupNum, int bankNum) {
		byte[] gsysex = ((PatchDataImpl) p).getSysex();
		byte[] ggsysex = new byte[127];
		for (int i = 0; i < 64; i++) {
			gsysex[127 * i + 5] = (byte) 0x0F;
			gsysex[127 * i + 6] = (byte) (groupNum * 64 + bankNum * 8 + i);
			gsysex[127 * i + 7] = (byte) 0x00;
			System.arraycopy(gsysex, 127 * i, ggsysex, 0, 127);
			sendPatchWorker(new PatchDataImpl(ggsysex, this));
			try {
				Thread.sleep(sleepTime);
			} catch (Exception e) {
			}
		}
	}

	/** Create a new all memory patch, that conforms to the format of the GP-16. */
	public PatchDataImpl createNewPatch() {
		byte[] sysex = new byte[NS * singleSize];

		RolandGP16SingleDriver patchCreator = new RolandGP16SingleDriver();
		PatchDataImpl blankPatch = patchCreator.createNewPatch();
		for (int i = 0; i < NS * 64; i++)
			System.arraycopy(((PatchDataImpl) blankPatch).getSysex(), 0, sysex, getPatchStart(i) / 64, singleSize / 64);
		PatchDataImpl p = new PatchDataImpl(sysex, this);
		calculateChecksum(p);
		return p;
	}

	/** The name string of the GP-16 is 16 characters long. */
	public void deletePatch(PatchDataImpl p, int patchNum) {
		setPatchName(p, patchNum, "                ");
	}

	/** Smarter all memory naming, name the group after the first patch in it. */
	public String getPatchName(PatchDataImpl p) {
		return getPatchName(p, 0);
	}

}
