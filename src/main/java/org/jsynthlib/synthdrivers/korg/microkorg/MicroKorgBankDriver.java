package org.jsynthlib.synthdrivers.korg.microkorg;

import java.nio.ByteBuffer;

import javax.sound.midi.MidiMessage;

import org.jsynthlib.model.driver.NameValue;
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

	private ByteBuffer banksysex = null;

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

	}

	@Override
	public String getPatchName(PatchDataImpl p, int patchNum) {
		ErrorMsgUtil.reportStatus(">>>> Get patch name");

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
		ErrorMsgUtil.reportStatus(">>>> Set name");
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
		ErrorMsgUtil.reportStatus(">>>> put patch " + banksysex);

		if (banksysex == null) {
			banksysex = ByteBuffer.allocate(MicroKorg.BANK_ALL_SIZE_COMPRESSED_SYSEX);

			byte[] header = MicroKorg.PROGRAM_DATA_DUMP_HEADER;
			header[2] = MicroKorg.getMidiChannelByte(getChannel());
			banksysex.put(header);

			ErrorMsgUtil.reportStatus(">>> Header " + HexaUtil.hexDumpOneLine(header));

		}

		byte[] program = new byte[MicroKorg.PROGRAM_SIZE_COMPRESSED];
		System.arraycopy(p.getByteArray(), MicroKorg.HEADER_SIZE, program, 0, MicroKorg.PROGRAM_SIZE_COMPRESSED);

		for (byte b : program) {
			// System.out.print(HexaUtil.byteToHexString(b) + " ");
		}

		banksysex.put(program);

		ErrorMsgUtil.reportStatus("> Position " + banksysex.position());

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
	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		ErrorMsgUtil.reportStatus(">>>> store patch");

		if (banksysex != null) {
			// fill dummies
			int start = banksysex.position() / MicroKorg.PROGRAM_COMPRESSED_SYSEX;
			ErrorMsgUtil.reportStatus(">>> start " + start);

			byte[] defaultpatch = HexaUtil.convertStringToSyex(MicroKorg.defaultPatch);
			ErrorMsgUtil.reportStatus("Size " + defaultpatch.length);

			for (int i = start + 1; i < MicroKorg.PATCH_COUNT_IN_BANK; i++) {
				banksysex.put(defaultpatch);
			}

			banksysex.put((byte) 0xF7);

			ErrorMsgUtil.reportStatus("Bank sysex" + banksysex);

			int counter = 0;
			for (byte b : banksysex.array()) {
				// System.out.print(HexaUtil.byteToHexString(b) + " ");
				counter++;
				if (counter % 1000 == 0) {
					ErrorMsgUtil.reportStatus("\n");
				}
			}

			// send
			ByteBuffer midi = MicroKorg.processDumpDataEncrypt(banksysex.array(), getChannel(), 3);

			send(midi.array());

			// reset
			banksysex = null;

		}
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
	public ByteBuffer processDumpDataConversion(byte[] sysexBuffer) {
		return MicroKorg.processDumpDataDecrypt(sysexBuffer, 4, MicroKorg.BANK_SIZE_MIDI);
	}

	@Override
	public PatchDataImpl extractPatch(PatchDataImpl bank, int patchNum) {
		ErrorMsgUtil.reportStatus(">>>> Get patch " + getClass().getSimpleName());

		byte[] sysex = new byte[MicroKorg.HEADER_SIZE + MicroKorg.PROGRAM_SIZE_COMPRESSED + 1];
		sysex[0] = (byte) 0xF0;
		sysex[1] = (byte) 0x42;
		// INFO sssmCurtis - midi channel is set to 1
		sysex[2] = (byte) 0x30;
		sysex[3] = (byte) 0x58;
		sysex[4] = (byte) 0x40;
		sysex[MicroKorg.HEADER_SIZE + MicroKorg.PROGRAM_SIZE_COMPRESSED] = (byte) 0xF7;

		ErrorMsgUtil.reportStatus("Patch : " + patchNum + " " + (bank.getSysex().length - getPatchStart(patchNum)));
		// ErrorMsgUtil.reportStatus(bank.getSysex().length + " -> " + getPatchStart(128));

		if ((bank.getSysex().length - MicroKorg.PROGRAM_SIZE_COMPRESSED) >= getPatchStart(patchNum)) {

			System.arraycopy(bank.getSysex(), getPatchStart(patchNum), sysex, MicroKorg.HEADER_SIZE, MicroKorg.PROGRAM_SIZE_COMPRESSED);
			try {
				// pass Single Driver !!!FIXIT!!!
				PatchDataImpl p = new PatchDataImpl(sysex, getDevice());
				return p;
			} catch (Exception e) {
				ErrorMsgUtil.reportError("Error", "Error in " + getClass().getSimpleName(), e);
			}
		}
		return null;
	}

	private int getPatchStart(int patchNum) {
		// ErrorMsgUtil.reportStatus(">>>> Get patch start " + (HSIZE + (MicroKorg.PROGRAM_SIZE * patchNum)));
		return MicroKorg.HEADER_SIZE + (MicroKorg.PROGRAM_SIZE_COMPRESSED * patchNum);
	}

	@Override
	public void requestPatchDump(int bankNum, int patchNum) {
		ErrorMsgUtil.reportStatus(">>>> Send sysex");

		NameValue kv = new NameValue("midiChannel", MicroKorg.getMidiChannelByte(getChannel()));
		MidiMessage msg = sysexHandler.toSysexMessage(getChannel(), kv);
		send(msg);
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

	public int getHeaderSize() {
		return MicroKorg.HEADER_SIZE;
	}

}
