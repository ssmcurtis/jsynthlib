package org.jsynthlib.synthdrivers.yamaha.fs1r;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

import javax.swing.JOptionPane;

import org.jsynthlib.menu.JSLFrame;
import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.patch.PatchDataImpl;

/**
 * Bank driver for Yamaha FS1R Il n'y a pas de format SYSEX de banque donc ? On reunit 128 perf + 128 voices dans la
 * meme banque. 0..127 performances 128..255 voices
 * 
 * @author Denis Queffeulou mailto:dqueffeulou@free.fr
 * @version $Id$
 */
public class YamahaFS1RBankDriver extends SynthDriverBank {
	static final int PATCHNAME_SIZE = 10;
	static final int BANK_NB_PATCHES = 256;
	static final int HEADER_SIZE = 6 + PATCHNAME_SIZE;
	static final int DATA_START = HEADER_SIZE;
	static final int PATCHNAME_OFFSET = 6;
	static final int BANK_SIZE = 128 * YamahaFS1RVoiceDriver.PATCH_AND_HEADER_SIZE + 128
			* YamahaFS1RPerformanceDriver.PATCH_AND_HEADER_SIZE;
	static final int BANK_AND_HEADER_SIZE = BANK_SIZE + HEADER_SIZE + 1;
	static final int NB_COLUMNS = 4;
	static final int NB_ROWS = BANK_NB_PATCHES / NB_COLUMNS;

	static private YamahaFS1RBankDriver mInstance;

	/**
	 * Constructor for the YamahaFS1RBankDriver object
	 */
	public YamahaFS1RBankDriver() {
		super("Bank", "Denis Queffeulou", BANK_NB_PATCHES, NB_COLUMNS);
		sysexID = "F00F057F7F7F";
		// inquiryID="F07E**06020F0200*************F7";
		deviceIDoffset = -1;

		// singleSysexID = YamahaFS1RVoiceDriver.sysexID;
		singleSize = -1;
		bankNumbers = new String[] { "Internal" };
		patchNumbers = new String[256];
		DecimalFormat oFormat = new DecimalFormat("000");
		for (int i = 0; i < 128; i++) {
			patchNumbers[i] = "P" + oFormat.format(i);
		}
		for (int i = 0; i < 128; i++) {
			patchNumbers[128 + i] = "V" + oFormat.format(i);
		}

		patchNameStart = PATCHNAME_OFFSET;
		patchNameSize = PATCHNAME_SIZE;

		patchSize = BANK_AND_HEADER_SIZE;
	}

	static YamahaFS1RBankDriver getInstance() {
		if (mInstance == null) {
			mInstance = new YamahaFS1RBankDriver();
		}
		return mInstance;
	}

	public void setPatchName(PatchDataImpl p, String name) {
		if (name.length() < patchNameSize)
			name = name + "            ";
		byte[] namebytes = new byte[64];
		try {
			namebytes = name.getBytes("US-ASCII");
			for (int i = 0; i < patchNameSize; i++)
				((PatchDataImpl) p).getSysex()[patchNameStart + i] = namebytes[i];
		} catch (UnsupportedEncodingException ex) {
			return;
		}
		calculateChecksum(p);
	}

	public String getPatchName(PatchDataImpl ip) {
		try {
			StringBuffer s = new StringBuffer(new String(((PatchDataImpl) ip).getSysex(), patchNameStart, patchNameSize,
					"US-ASCII"));
			return s.toString();
		} catch (UnsupportedEncodingException ex) {
			return "-";
		}
	}

	/**
	 * Gets the patchStart attribute of the YamahaFS1RBankDriver object
	 * 
	 * @param patchNum
	 *            Description of the Parameter
	 * @return The patchStart value
	 */
	public int getPatchStart(int patchNum) {
		int start = HEADER_SIZE;
		if (patchNum > 127) {
			start += YamahaFS1RPerformanceDriver.PATCH_AND_HEADER_SIZE * 128 + (patchNum - 128)
					* YamahaFS1RVoiceDriver.PATCH_AND_HEADER_SIZE;
		} else {
			start += patchNum * YamahaFS1RPerformanceDriver.PATCH_AND_HEADER_SIZE;
		}
		return start;
	}

	/** Gets a patch from the bank, converting it as needed */
	public PatchDataImpl extractPatch(PatchDataImpl bank, int patchNum) {
		int oStart = getPatchStart(patchNum);
		int oSize = (patchNum > 127 ? YamahaFS1RVoiceDriver.PATCH_AND_HEADER_SIZE
				: YamahaFS1RPerformanceDriver.PATCH_AND_HEADER_SIZE);
		byte oPatch[] = new byte[oSize];
		System.arraycopy(((PatchDataImpl) bank).getSysex(), oStart, oPatch, 0, oSize);
		return new PatchDataImpl(oPatch);
	}

	/** Puts a patch into the bank, converting it as needed */
	public void putPatch(PatchDataImpl bank, PatchDataImpl p, int patchNum) {
		int oStart = getPatchStart(patchNum);
		int oSize = (patchNum > 127 ? YamahaFS1RVoiceDriver.PATCH_AND_HEADER_SIZE
				: YamahaFS1RPerformanceDriver.PATCH_AND_HEADER_SIZE);
		if (oSize != ((PatchDataImpl) p).getSysex().length) {
			JOptionPane.showMessageDialog(null, "Performances in P000-P127, Voices in V000-V127 ", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		System.arraycopy(((PatchDataImpl) p).getSysex(), 0, ((PatchDataImpl) bank).getSysex(), oStart, oSize);
	}

	/**
	 * Gets the patchName of a patch in the bank
	 * 
	 * @param ip
	 *            bank sysex
	 * @param patchNum
	 *            number of the patch in the bank
	 * @return The patchName
	 */
	public String getPatchName(PatchDataImpl ip, int patchNum) {
		PatchDataImpl p = (PatchDataImpl) ip;
		int oPatchStart = getPatchStart(patchNum);
		// ErrorMsgUtil.reportStatus("getPatchName "+patchNum+" start = "+oPatchStart);
		if (patchNum > 127)
			return YamahaFS1RVoiceDriver.getInstance().getPatchName(p, oPatchStart);
		else
			return YamahaFS1RPerformanceDriver.getInstance().getPatchName(p, oPatchStart);
	}

	/**
	 * Sets the patchName attribute of the YamahaFS1RBankDriver object
	 * 
	 * @param ip
	 *            The new patchName value
	 * @param patchNum
	 *            The new patchName value
	 * @param name
	 *            The new patchName value
	 */
	public void setPatchName(PatchDataImpl ip, int patchNum, String name) {
		PatchDataImpl p = (PatchDataImpl) ip;
		int oPatchStart = getPatchStart(patchNum);
		if (patchNum > 127)
			YamahaFS1RVoiceDriver.getInstance().setPatchName(p, name, oPatchStart);
		else
			YamahaFS1RPerformanceDriver.getInstance().setPatchName(p, name, oPatchStart);
	}

	public void calculateChecksum(PatchDataImpl p) {
		// no checksum
	}

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		// TODO patch by patch
	}

	/** Creates an editor window to edit this bank */
	public JSLFrame editPatch(PatchDataImpl p) {
		return new YamahaFS1RBankEditor(p);
	}

	/**
	 * Bank factory
	 * 
	 * @return the new "empty" bank
	 */
	public PatchDataImpl createNewPatch() {
		// ErrorMsgUtil.reportStatus("createNewPatch");
		byte[] sysex = new byte[BANK_AND_HEADER_SIZE];
		// dummy sysex header (FS1R has no bank sysex)
		sysex[0] = (byte) 0xF0;
		sysex[1] = (byte) 0x0F;
		sysex[2] = (byte) 0x05;
		sysex[3] = (byte) 0x7f;
		sysex[4] = (byte) 0x7f;
		sysex[5] = (byte) 0x7f;
		sysex[6] = (byte) 'n';
		sysex[7] = (byte) 'o';
		sysex[8] = (byte) 'n';
		sysex[9] = (byte) 'a';
		sysex[10] = (byte) 'm';
		sysex[11] = (byte) 'e';
		sysex[12] = (byte) ' ';
		sysex[13] = (byte) ' ';
		sysex[14] = (byte) ' ';
		sysex[15] = (byte) ' ';
		for (int i = 0; i < 128; i++) {
			YamahaFS1RPerformanceDriver.initPatch(sysex, HEADER_SIZE + i
					* YamahaFS1RPerformanceDriver.PATCH_AND_HEADER_SIZE);
		}
		for (int i = 0; i < 128; i++) {
			YamahaFS1RVoiceDriver.initPatch(sysex, HEADER_SIZE + 128
					* YamahaFS1RPerformanceDriver.PATCH_AND_HEADER_SIZE + i
					* YamahaFS1RVoiceDriver.PATCH_AND_HEADER_SIZE);
		}
		return new PatchDataImpl(sysex, this);
	}

	/**
	 * FS1R bank holds 2 types of patch, performance and voice.
	 */
	public boolean canHoldPatch(PatchDataImpl p) {
		// TODO
		return (((PatchDataImpl) p).getSysex().length == YamahaFS1RPerformanceDriver.PATCH_AND_HEADER_SIZE || ((PatchDataImpl) p).getSysex().length == YamahaFS1RVoiceDriver.PATCH_AND_HEADER_SIZE);
	}

}
