package org.jsynthlib.synthdrivers.korg.microkorg;

import java.nio.ByteBuffer;

import javax.sound.midi.MidiMessage;
import javax.swing.JOptionPane;

import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;
import org.jsynthlib.tools.HexaUtil;

/**
 * Bank driver for KAWAI K4/K4r voice patch.
 * 
 * @version $Id$
 */
public class MicroKorgBankDriver extends SynthDriverBank {

	private static final SysexHandler sysexHandler = new SysexHandler(MicroKorg.REQUEST_BANK);

	public MicroKorgBankDriver() {
		super("Bank", "ssmCurtis", MicroKorg.PATCH_COUNT_IN_BANK, 1);
		sysexID = MicroKorg.DEVICE_SYSEX_ID;
		patchSize = MicroKorg.BANK_SIZE_MIDI_SYSEX;
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
		// if (!canHoldPatch(p)) {
		// JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.", "Error",
		// JOptionPane.ERROR_MESSAGE);
		// return;
		// }
		//
		// System.arraycopy(p.getSysex(), MicroKorg.HEADER_SIZE, bank.getSysex(), getPatchStart(patchNum),
		// MicroKorg.PROGRAM_SIZE_MIDI);
		// calculateChecksum(bank);
	}

	@Override
	public ByteBuffer processDumpDataConversion(byte[] sysexBuffer) {
		return MicroKorg.processDumpDataDecrypt(sysexBuffer, 4, MicroKorg.BANK_SIZE_MIDI);
	}

	@Override
	public PatchDataImpl getPatch(PatchDataImpl bank, int patchNum) {
		System.out.println(">>>> Get patch " + getClass().getSimpleName());

		byte[] sysex = new byte[MicroKorg.HEADER_SIZE + MicroKorg.PROGRAM_SIZE_COMPRESSED + 1];
		// F0 42 3* 58
		sysex[0] = (byte) 0xF0;
		sysex[1] = (byte) 0x42;
		// TODO sssmCurtis - korg program number
		sysex[2] = (byte) 0x30;
		sysex[3] = (byte) 0x58;
		sysex[4] = (byte) 0x40;
		sysex[MicroKorg.HEADER_SIZE + MicroKorg.PROGRAM_SIZE_COMPRESSED] = (byte) 0xF7;

		System.out.println("Patch : " + patchNum + " " + (bank.getSysex().length - getPatchStart(patchNum)));
		// System.out.println(bank.getSysex().length + " -> " + getPatchStart(128));

		if ((bank.getSysex().length - MicroKorg.PROGRAM_SIZE_COMPRESSED) >= getPatchStart(patchNum)) {

			System.arraycopy(bank.getSysex(), getPatchStart(patchNum), sysex, MicroKorg.HEADER_SIZE, MicroKorg.PROGRAM_SIZE_COMPRESSED);
			// System.out.println(patchNum + ": "
			// + HexaUtil.hexDumpOneLine(bank.getSysex(), getPatchStart(patchNum), -1, MicroKorg.PROGRAM_SIZE));
			// System.out.println(patchNum + ": " + HexaUtil.hexDumpOneLine(sysex, MicroKorg.HEADER_SIZE, -1,
			// sysex.length));
			// ssmCurtis - fix something ... mystic
			// int counter = 0;
			// for (byte b : sysex) {
			// if (counter + HSIZE < sysex.length) {
			// if (counter > HSIZE && (counter) % 8 == 0) {
			// sysex[counter - 3 + HSIZE] = sysex[counter - 2 + HSIZE];
			// sysex[counter - 2 + HSIZE] = sysex[counter - 1 + HSIZE];
			// sysex[counter - 1 + HSIZE] = (byte) 0x00;
			// }
			// }
			// counter++;
			// }
			try {
				// pass Single Driver !!!FIXIT!!!
				PatchDataImpl p = new PatchDataImpl(sysex, getDevice());
				// p.calculateChecksum();
				return p;
			} catch (Exception e) {
				ErrorMsgUtil.reportError("Error", "Error in " + getClass().getSimpleName(), e);
			}
		}
		return null;
	}

	public int getPatchStart(int patchNum) {
		// System.out.println(">>>> Get patch start " + (HSIZE + (MicroKorg.PROGRAM_SIZE * patchNum)));
		return MicroKorg.HEADER_SIZE + (MicroKorg.PROGRAM_SIZE_COMPRESSED * patchNum);
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

	@Override
	public boolean supportsPatch(String patchString, byte[] sysex) {
		if ((patchSize != 0) && !MicroKorg.bankPatchSizeIsSupported(sysex.length)) {
			return false;
		}

		if (patchString.length() < sysexID.length()) {
			return false;
		}

		StringBuffer compareString = new StringBuffer();
		for (int i = 0; i < sysexID.length(); i++) {
			switch (sysexID.charAt(i)) {
			case '*':
				compareString.append(patchString.charAt(i));
				break;
			default:
				compareString.append(sysexID.charAt(i));
			}
		}
		return (compareString.toString().equalsIgnoreCase(patchString.substring(0, sysexID.length())));
	}
	
	public int getHeaderSize(){
		return MicroKorg.HEADER_SIZE;
	}

}
