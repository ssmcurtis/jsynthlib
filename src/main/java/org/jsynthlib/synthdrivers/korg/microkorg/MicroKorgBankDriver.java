package org.jsynthlib.synthdrivers.korg.microkorg;

import javax.sound.midi.MidiMessage;
import javax.swing.JOptionPane;

import org.jsynthlib.menu.helper.SysexHandler;
import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

/**
 * Bank driver for KAWAI K4/K4r voice patch.
 * 
 * @version $Id$
 */
public class MicroKorgBankDriver extends SynthDriverBank {
	/** Header Size */
	private static final int HSIZE = 6;
	/** Single Patch size */
	private static final int SSIZE = MicroKorg.PROGRAM_SIZE;
	/** the number of single patches in a bank patch. */
	private static final int NS = 128;

	private static final SysexHandler sysexHandler = new SysexHandler(MicroKorg.REQUEST_BANK);

	public MicroKorgBankDriver() {
		super("Bank", "ssmCurtis", MicroKorg.PATCH_COUNT_IN_BANK, 1);
		sysexID = MicroKorg.DEVICE_SYSEX_ID;
		patchSize = MicroKorg.BANK_DUMP_SIZE;
		patchNameStart = MicroKorg.PATCH_NAME_START_AT.position();
		patchNameSize = MicroKorg.PATCH_NAME_LENGTH.position();
		deviceIDoffset = MicroKorg.DEVICE_ID_OFFSET;

		checksumOffset = MicroKorg.PATCH_CHECKSUM_OFFSET.position();
		bankNumbers = MicroKorg.BANK_NAMES;
		patchNumbers = MicroKorg.createPatchNumbers();

		sysexHandler.setAddToBankByte(MicroKorg.TEMPLATE_ADD_TO_BANK_BYTE);
	}

	// @Override
	// public int getPatchSize(){
	// return MicroKorg.PATCH_SIZE;
	// }

	@Override
	public String getPatchName(PatchDataImpl p, int patchNum) {
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

	@Override
	public void setPatchName(PatchDataImpl p, int patchNum, String name) {
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

	@Override
	public void putPatch(PatchDataImpl bank, PatchDataImpl p, int patchNum) {
		System.out.println(">>>> put patch");
		if (!canHoldPatch(p)) {
			JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		System.arraycopy(p.getSysex(), HSIZE, bank.getSysex(), getPatchStart(patchNum), SSIZE);
		calculateChecksum(bank);
	}

	@Override
	public PatchDataImpl getPatch(PatchDataImpl bank, int patchNum) {
		System.out.println(">>>> Get patch " + getClass().getSimpleName());

		byte[] sysex = new byte[HSIZE + SSIZE + 1];
		// F0 42 3* 58
		sysex[0] = (byte) 0xF0;
		sysex[1] = (byte) 0x42;
		sysex[2] = (byte) 0x30; // TODO sssmCurtis
		sysex[3] = (byte) 0x58;
		sysex[4] = (byte) 0x40;
		sysex[5] = (byte) 0x00;
		sysex[HSIZE + SSIZE] = (byte) 0xF7;
		System.out.println(bank.getSysex().length + " -> "+ getPatchStart(128)); 
		System.arraycopy(bank.getSysex(), getPatchStart(patchNum), sysex, HSIZE, SSIZE);

		// ssmCurtis - fix something ... mystic
		int counter = 0;
		for (byte b : sysex) {
			if (counter + HSIZE < sysex.length) {
				if (counter > HSIZE && (counter) % 8 == 0) {
					sysex[counter - 3 + HSIZE] = sysex[counter - 2 + HSIZE];
					sysex[counter - 2 + HSIZE] = sysex[counter - 1 + HSIZE];
					sysex[counter - 1 + HSIZE] = (byte) 0x00;
				}
			}
			counter++;
		}

		try {
			// pass Single Driver !!!FIXIT!!!
			PatchDataImpl p = new PatchDataImpl(sysex, getDevice());
			p.calculateChecksum();
			return p;
		} catch (Exception e) {
			ErrorMsgUtil.reportError("Error", "Error in " + getClass().getSimpleName(), e);
		}
		return null;
	}

	public int getPatchStart(int patchNum) {
		System.out.println(">>>> Get patch start" + (HSIZE + (SSIZE * patchNum)));
		return HSIZE + (SSIZE * patchNum);
	}

	@Override
	public void requestPatchDump(int bankNum, int patchNum) {
		System.out.println(">>>> Send sysex");

		// NameValue bank = new SysexHandler.NameValue("bankNum", bankNum << 1);
		// NameValue patch = new SysexHandler.NameValue("patchNum", 1);
		MidiMessage msg = sysexHandler.toSysexMessage(getChannel());
		send(msg);
	}

	@Override
	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		System.out.println(">>>> store patch");

		// try {
		// Thread.sleep(100);
		// } catch (Exception e) {
		// }
		// p.getSysex()[5] = (byte) (bankNum << 1);
		// p.getSysex()[6] = (byte) (patchNum);
		// sendPatchWorker(p);
		// try {
		// Thread.sleep(100);
		// } catch (Exception e) {
		// }
	}
}
