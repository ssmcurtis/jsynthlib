package org.jsynthlib.synthdrivers.crumar.bit99;

import javax.sound.midi.MidiMessage;
import javax.swing.JOptionPane;

import org.jsynthlib.menu.patch.BankDriver;
import org.jsynthlib.menu.patch.Patch;
import org.jsynthlib.menu.patch.SysexHandler;
import org.jsynthlib.menu.patch.SysexHandler.NameValue;

/**
 * Bank driver for KAWAI K4/K4r voice patch.
 * 
 * @version $Id$
 */
public class Bit99BankDriver extends BankDriver {
	/** Header Size */
	private static final int HSIZE = 8;
	/** Single Patch size */
	private static final int SSIZE = 131;
	/** the number of single patches in a bank patch. */
	private static final int NS = 128;

	private static final SysexHandler SYS_REQ = new SysexHandler("F0 01 20 01 05 *bankNum* *patchNum* F7");

	public Bit99BankDriver() {
		super("Bank", "ssmcurtis", NS, 4);
		sysexID = Bit99.DEVICE_SYSEX_ID;

		deviceIDoffset = 2;
		bankNumbers = Bit99.BANK_NAMES;
		patchNumbers = Bit99Device.createPatchNumbers();

		singleSysexID = sysexID;
		singleSize = 0;// HSIZE + SSIZE + 1;
		patchSize = 0;
	}

	public int getPatchStart(int patchNum) {
		System.out.println(">>>> Get patch start");

		return HSIZE + (SSIZE * patchNum);
	}

	public String getPatchName(Patch p, int patchNum) {
		System.out.println(">>>> Get patch name");

		// int nameStart = getPatchStart(patchNum);
		// nameStart += 0; // offset of name in patch data
		// try {
		// StringBuffer s = new StringBuffer(new String(p.sysex, nameStart, 10,
		// "US-ASCII"));
		// return s.toString();
		// } catch (UnsupportedEncodingException ex) {
		// return "-";
		// }
		return "NO NAME";
	}

	public void setPatchName(Patch p, int patchNum, String name) {
		System.out.println(">>>> Set name");
		// patchNameSize = 10;
		// patchNameStart = getPatchStart(patchNum);
		//
		// if (name.length() < patchNameSize)
		// name = name + "            ";
		// byte[] namebytes = new byte[64];
		// try {
		// namebytes = name.getBytes("US-ASCII");
		// for (int i = 0; i < patchNameSize; i++)
		// p.sysex[patchNameStart + i] = namebytes[i];
		//
		// } catch (UnsupportedEncodingException ex) {
		// return;
		// }
	}

	public void putPatch(Patch bank, Patch p, int patchNum) {
		System.out.println(">>>> put patch");
		if (!canHoldPatch(p)) {
			JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		System.arraycopy(p.getSysex(), HSIZE, bank.getSysex(), getPatchStart(patchNum), SSIZE);
		calculateChecksum(bank);
	}

	public Patch getPatch(Patch bank, int patchNum) {
		System.out.println(">>>> Get patch");
		// byte[] sysex = new byte[HSIZE + SSIZE + 1];
		// sysex[0] = (byte) 0xF0;
		// sysex[1] = (byte) 0x40;
		// sysex[2] = (byte) 0x00;
		// sysex[3] = (byte) 0x20;
		// sysex[4] = (byte) 0x00;
		// sysex[5] = (byte) 0x04;
		// sysex[6] = (byte) 0x00;
		// sysex[7] = /* (byte)0x00+ */(byte) patchNum;
		// sysex[HSIZE + SSIZE] = (byte) 0xF7;
		// System.arraycopy(bank.sysex, getPatchStart(patchNum), sysex, HSIZE,
		// SSIZE);
		// try {
		// // pass Single Driver !!!FIXIT!!!
		// Patch p = new Patch(sysex, getDevice());
		// p.calculateChecksum();
		// return p;
		// } catch (Exception e) {
		// ErrorMsg.reportError("Error", "Error in K4 Bank Driver", e);
		// return null;
		// }
		return null;
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		System.out.println(">>>> Send sysex");
		NameValue bank = new SysexHandler.NameValue("bankNum", bankNum << 1);
		NameValue patch = new SysexHandler.NameValue("patchNum", 1);
		MidiMessage msg = SYS_REQ.toSysexMessage(getChannel(), bank, patch);
		send(msg);
	}

	public void storePatch(Patch p, int bankNum, int patchNum) {
		System.out.println(">>>> store patch");

		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}
		p.getSysex()[5] = (byte) (bankNum << 1);
		p.getSysex()[6] = (byte) (patchNum);
		sendPatchWorker(p);
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}
	}
}
