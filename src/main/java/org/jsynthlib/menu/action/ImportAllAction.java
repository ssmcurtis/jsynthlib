package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import org.jsynthlib.menu.PatchBayApplication;
import org.jsynthlib.menu.preferences.AppConfig;
import org.jsynthlib.menu.ui.window.CompatibleFileDialog;
import org.jsynthlib.menu.ui.window.ImportAllDialog;
import org.jsynthlib.tools.ErrorMsg;

public class ImportAllAction extends AbstractAction {
	public ImportAllAction(Map<Serializable, Integer> mnemonics) {
		super("Import All...", null);
		this.setEnabled(false);
		mnemonics.put(this, new Integer('A'));
	}

	public void actionPerformed(ActionEvent e) {
		try {
			CompatibleFileDialog fc = new CompatibleFileDialog();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (AppConfig.getLibPath() != null)
				fc.setSelectedFile(new File(AppConfig.getLibPath()));
			if (fc.showDialog(PatchBayApplication.getInstance(), "Choose Import All Directory") != JFileChooser.APPROVE_OPTION)
				return;
			File file = fc.getSelectedFile();

			ImportAllDialog sd = new ImportAllDialog(PatchBayApplication.getInstance(), file);
			sd.setVisible(true);
		} catch (Exception ex) {
			ErrorMsg.reportError("Error", "Unable to Import Patches", ex);
		}
	}
}