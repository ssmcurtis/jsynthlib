package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.menu.preferences.AppConfig;
import org.jsynthlib.menu.ui.window.CompatibleFileDialog;
import org.jsynthlib.menu.ui.window.ImportAllDialog;
import org.jsynthlib.tools.ErrorMsg;

public class ImportAllAction extends AbstractAction {
	public ImportAllAction(Map<Serializable, Integer> mnemonics) {
		super("Import directory", null);
		this.setEnabled(false);
		mnemonics.put(this, new Integer('A'));
	}

	public void actionPerformed(ActionEvent e) {
		try {
			CompatibleFileDialog fileDialog = new CompatibleFileDialog();
			fileDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			if (AppConfig.getSysexPath() != null) {
				fileDialog.setCurrentDirectory(new File(AppConfig.getSysexPath()));
				//fc.setSelectedFile(new File(AppConfig.getSysexPath()));
			}

			if (fileDialog.showDialog(PatchBayApplication.getInstance(), "Choose Import All Directory") != JFileChooser.APPROVE_OPTION) {
				return;
			}
			File file = fileDialog.getSelectedFile();

			ImportAllDialog sd = new ImportAllDialog(PatchBayApplication.getInstance(), file);
			sd.setVisible(true);
		} catch (Exception ex) {
			ErrorMsg.reportError("Error", "Unable to Import Patches", ex);
		}
	}
}