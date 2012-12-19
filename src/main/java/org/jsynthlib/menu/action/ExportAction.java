package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.jsynthlib.menu.PatchBayApplication;
import org.jsynthlib.menu.preferences.AppConfig;
import org.jsynthlib.menu.ui.ExtensionFilter;
import org.jsynthlib.menu.ui.window.CompatibleFileDialog;
import org.jsynthlib.tools.ErrorMsg;

public class ExportAction extends AbstractAction {
	public ExportAction(Map<Serializable, Integer> mnemonics) {
		super("Export...", null);
		mnemonics.put(this, new Integer('O'));
		this.setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
		CompatibleFileDialog fc3 = new CompatibleFileDialog();
		FileFilter type1 = new ExtensionFilter("Sysex Files (*.syx)", ".syx");
		fc3.addChoosableFileFilter(type1);
		fc3.setFileFilter(type1);
		fc3.setCurrentDirectory(new File(AppConfig.getSysexPath()));
		if (fc3.showSaveDialog(PatchBayApplication.getInstance()) != JFileChooser.APPROVE_OPTION)
			return;
		File file = fc3.getSelectedFile();
		try {
			if (Actions.getSelectedFrame() == null) {
				ErrorMsg.reportError("Error", "Patch to export must be hilighted\n"
						+ "in the currently focuses Library");
			} else {
				if (!file.getName().toUpperCase().endsWith(".SYX"))
					file = new File(file.getPath() + ".syx");
				if (file.exists())
					if (JOptionPane.showConfirmDialog(null, "Are you sure?", "File Exists", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
						return;

				Actions.getSelectedFrame().exportPatch(file);
			}
		} catch (IOException ex) {
			ErrorMsg.reportError("Error", "Unable to Save Exported Patch", ex);
		}
	}
}