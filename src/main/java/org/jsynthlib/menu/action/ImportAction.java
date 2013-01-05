package org.jsynthlib.menu.action;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.menu.Actions;
import org.jsynthlib.menu.helper.ExtensionFilter;
import org.jsynthlib.menu.preferences.AppConfig;
import org.jsynthlib.menu.window.CompatibleFileDialog;
import org.jsynthlib.model.JSynthImportFileType;
import org.jsynthlib.tools.ErrorMsgUtil;
import org.jsynthlib.tools.UiUtil;

@SuppressWarnings("serial")
public class ImportAction extends AbstractAction {
	public ImportAction(Map<Serializable, Integer> mnemonics) {
		super("Import", null);
		mnemonics.put(this, new Integer('I'));
		this.setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
		CompatibleFileDialog fileDialog = new CompatibleFileDialog();

		for (JSynthImportFileType importFileType : JSynthImportFileType.values()) {
			FileFilter type = new ExtensionFilter(importFileType.getDescription(), importFileType.getExtension());
			fileDialog.addChoosableFileFilter(type);
		}
		fileDialog.setCurrentDirectory(new File(AppConfig.getSysexPath()));

		if (fileDialog.showOpenDialog(PatchBayApplication.getInstance()) != JFileChooser.APPROVE_OPTION) {
			return;
		}
		File file = fileDialog.getSelectedFile();

		try {
			if (Actions.getSelectedFrame() == null) {
				ErrorMsgUtil.reportError("Error", "Library to Import Patch\n into Must be in Focus");
			} else {
				String name = file.getName();
				final int lastPeriodPos = name.lastIndexOf('.');
				String extension = name.substring(lastPeriodPos);
				JSynthImportFileType fileType = JSynthImportFileType.getImportFileTypeForExtension(extension);
				
				Actions.getSelectedFrame().importPatch(file, fileType);
				
			}
		} catch (IOException ex) {
			ErrorMsgUtil.reportError("Error", "Unable to Load Sysex Data", ex);
		}
	}
}