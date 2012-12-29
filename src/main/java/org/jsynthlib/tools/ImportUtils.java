/*
 * ImportMidiFile.java
 *
 */

package org.jsynthlib.tools;

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

import org.jsynthlib.menu.patch.IPatch;

/**
 * 
 * @author Gerrit Gehnen
 * @version $Id$
 */

public class ImportUtils {

	public static IPatch[] getPatchesFromMidi(java.io.File file) {
		Sequence seq;
		Track[] tr;
		IPatch[] patchArray = null;
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
					patchArray = DriverUtil.createPatches(tr[j].get(i).getMessage().getMessage(), file.getName());

					// for (int k = 0; k < patarray.length; k++) {
					// frame.pastePatch(patarray[k]);
					// }
				}

			}
		}
		return patchArray;
	}

	public static IPatch[] getPatchesFromTexhex(java.io.File file) {
		IPatch[] patchArray = null;

		String texhex = "";
		try {
			FileInputStream fileIn = new FileInputStream(file);
			FileChannel fc = fileIn.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			texhex = Charset.defaultCharset().decode(bb).toString();
			fileIn.close();
			texhex = texhex.trim().replace(" ", "");
			patchArray = DriverUtil.createPatches(Utility.convertStringToSyex(texhex), file.getName());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return patchArray;
	}

}