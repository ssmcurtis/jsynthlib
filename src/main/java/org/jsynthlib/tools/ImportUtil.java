/*
 * ImportMidiFile.java
 *
 */

package org.jsynthlib.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;

import org.jsynthlib.menu.preferences.AppConfig;
import org.jsynthlib.model.JSynthImportFileType;
import org.jsynthlib.model.patch.Patch;

/**
 * 
 * @author Gerrit Gehnen, ssmCurtis 2013
 * @version $Id$
 */

public class ImportUtil {

	public static Patch[] getPatchesFromMidi(java.io.File file) {
		Sequence seq;
		Track[] tr;

		List<Patch> li = new ArrayList<>();
		try {
			seq = MidiSystem.getSequence(file);
		} catch (Exception ex) {
			// If we fall in an exception the file was not a Midifile....
			return li.toArray(new Patch[] {});
		}
		tr = seq.getTracks();
		// ErrorMsg.reportStatus("Track Count "+tr.length);

		for (int j = 0; j < tr.length; j++) {
			// ErrorMsg.reportStatus("Track "+j+":size "+tr[j].size());
			for (int i = 0; i < tr[j].size(); i++) {
				// PatchBasket frame = (PatchBasket) PatchBayApplication.getDesktop().getSelectedFrame();

				if (tr[j].get(i).getMessage() instanceof SysexMessage) {
					// ErrorMsg.reportStatus("Track "+j+" Event "+i+" SYSEX!!");

					System.out.println("IMPORT MIDI ... ");
					for (Patch p : DriverUtil.createPatches(tr[j].get(i).getMessage().getMessage(), getLocation(file))) {
						p.setComment(j + "-" + p.getComment());
						li.add(p);
					}

					// for (int k = 0; k < patarray.length; k++) {
					// frame.pastePatch(patarray[k]);
					// }
				}

			}
		}
		return li.toArray(new Patch[] {});
	}

	public static Patch[] getPatchesFromTexhex(java.io.File file) {
		Patch[] patchArray = null;

		String texhex = "";
		try {
			FileInputStream fileIn = new FileInputStream(file);
			FileChannel fc = fileIn.getChannel();
			MappedByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			texhex = Charset.defaultCharset().decode(byteBuffer).toString();
			fileIn.close();

			texhex = texhex.trim().replace(" ", "");

			if (texhex.startsWith("F0")) {
				patchArray = DriverUtil.createPatches(HexaUtil.convertStringToSyex(texhex), getLocation(file));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return patchArray;
	}

	public static Patch[] getPatchesFromSysex(java.io.File file) {
		Patch[] patchArray = null;
		try {
			FileInputStream fileIn = new FileInputStream(file);
			byte[] buffer = new byte[(int) file.length()];
			fileIn.read(buffer);
			fileIn.close();

			patchArray = DriverUtil.createPatches(buffer, getLocation(file));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return patchArray;
	}

	public static Patch[] getPatchesFromPrg(java.io.File file, JSynthImportFileType type) {
		Patch[] patchArray = null;
		try {
			FileInputStream fileIn = new FileInputStream(file);
			byte[] buffer = new byte[(int) file.length()];
			fileIn.read(buffer);
			fileIn.close();

			byte[] pattern = new byte[] {};
			byte[] replace = new byte[] {};

			if (JSynthImportFileType.MICROKORG_SET.equals(type)) {
				pattern = new byte[] { (byte) 0xF0, (byte) 0x82, (byte) 0xA4, (byte) 0x0F, (byte) 0x42 };
				replace = new byte[] { (byte) 0xF0, (byte) 0x42 };
			} else if (JSynthImportFileType.MICROKORG_PRG.equals(type)) {
				pattern = new byte[] { (byte) 0xF0, (byte) 0x82, (byte) 0x28, (byte) 0x42 };
				replace = new byte[] { (byte) 0xF0, (byte) 0x42 };
			}
			System.out.println(HexaUtil.hexDumpOneLine(pattern, 0, pattern.length, pattern.length));

			// ByteBuffer byteBuffer = ByteBuffer.allocate(buffer.length);
			// byteBuffer.put(buffer);

			List<Byte> li = new ArrayList<Byte>();
			boolean useByte = false;
			for (int i = 0; i < buffer.length; i++) {
				if (buffer[i] == pattern[0]) {
					useByte = true;

					// copy pattern length
					byte[] copyNexBytes = new byte[pattern.length];
					System.arraycopy(buffer, i, copyNexBytes, 0, pattern.length);
					boolean equal = true;
					for (int j = 0; j < pattern.length; j++) {
						if (pattern[j] != copyNexBytes[j]) {
							equal = false;
						}
					}
					System.out.println(HexaUtil.hexDumpOneLine(copyNexBytes, 0, -1, pattern.length));
					System.out.println(HexaUtil.hexDumpOneLine(buffer, i, pattern.length, pattern.length));

					if (equal) {
						for (int j = 0; j < replace.length; j++) {
							li.add(replace[j]);
						}
						i += pattern.length - 1;
					} else {
						li.add(buffer[i]);
					}
				} else if (useByte) {
					li.add(buffer[i]);
				}
				if (HexaUtil.isEndSysex(buffer[i])) {
					useByte = false;
				}

			}

			byte[] barr = new byte[li.size()];

			int pointer = 0;
			for (Byte b : li) {
				barr[pointer] = b;
				pointer++;
			}
			System.out.println(HexaUtil.hexDumpOneLine(barr, 0, -1, barr.length));

			patchArray = DriverUtil.createPatches(barr, getLocation(file));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return patchArray;
	}

	private static String getLocation(File file) {
		if (AppConfig.getAddParentDirectoryName() && file.getParentFile() != null) {
			return file.getParentFile().getName() + File.separator + file.getName();
		} else {
			return file.getName();
		}
	}

}