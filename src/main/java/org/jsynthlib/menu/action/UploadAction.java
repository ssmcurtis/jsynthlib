package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.menu.window.UploadPatchDialog;

public class UploadAction extends AbstractAction {
	public UploadAction(Map<Serializable, Integer> mnemonics) {
		super("Upload...", null);
		mnemonics.put(this, new Integer('U'));
		this.setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
		UploadPatchDialog myDialog = new UploadPatchDialog(PatchBayApplication.getInstance());
		myDialog.setVisible(true);
	}

}