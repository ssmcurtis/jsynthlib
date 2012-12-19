package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.jsynthlib.menu.PatchBayApplication;
import org.jsynthlib.menu.preferences.AppConfig;
import org.jsynthlib.menu.ui.ExtensionFilter;
import org.jsynthlib.menu.ui.window.CompatibleFileDialog;
import org.jsynthlib.tools.ErrorMsg;

public class ImportAction extends AbstractAction {
	public ImportAction(Map<Serializable, Integer> mnemonics) {
		super("Import...", null);
		mnemonics.put(this, new Integer('I'));
		this.setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
		CompatibleFileDialog fc2 = new CompatibleFileDialog();

		FileFilter type1 = new ExtensionFilter("Sysex Files (*.syx)", ".syx");
		// core.ImportMidiFile extracts Sysex Messages from MidiFile
		FileFilter type2 = new ExtensionFilter("MIDI Files (*.mid)", ".mid");

		fc2.addChoosableFileFilter(type1);
		fc2.addChoosableFileFilter(type2);

		fc2.setFileFilter(type1);
		fc2.setCurrentDirectory(new File(AppConfig.getSysexPath()));

		if (fc2.showOpenDialog(PatchBayApplication.getInstance()) != JFileChooser.APPROVE_OPTION)
			return;
		File file = fc2.getSelectedFile();
		try {
			if (Actions.getSelectedFrame() == null) {
				ErrorMsg.reportError("Error", "Library to Import Patch\n into Must be in Focus");
			} else {
				Actions.getSelectedFrame().importPatch(file);
			}
		} catch (IOException ex) {
			ErrorMsg.reportError("Error", "Unable to Load Sysex Data", ex);
		}
	}
}