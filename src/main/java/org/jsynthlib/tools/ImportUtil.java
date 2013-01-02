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

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;

import org.jsynthlib.menu.preferences.AppConfig;
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
		Patch[] patchArray = null;
		try {
			seq = MidiSystem.getSequence(file);
		} catch (Exception ex) {
			// If we fall in an exception the file was not a Midifile....
			return patchArray;
		}
		tr = seq.getTracks();
		// ErrorMsg.reportStatus("Track Count "+tr.length);

		for (int j = 0; j < tr.length; j++) {
			// ErrorMsg.reportStatus("Track "+j+":size "+tr[j].size());
			for (int i = 0; i < tr[j].size(); i++) {
				// PatchBasket frame = (PatchBasket) PatchBayApplication.getDesktop().getSelectedFrame();

				if (tr[j].get(i).getMessage() instanceof SysexMessage) {
					// ErrorMsg.reportStatus("Track "+j+" Event "+i+" SYSEX!!");
					patchArray = DriverUtil.createPatches(tr[j].get(i).getMessage().getMessage(), getLocation(file));

					// for (int k = 0; k < patarray.length; k++) {
					// frame.pastePatch(patarray[k]);
					// }
				}

			}
		}
		return patchArray;
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
			// ErrorMsg.reportStatus("Buffer length:" + buffer.length);
			patchArray = DriverUtil.createPatches(buffer, getLocation(file));
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