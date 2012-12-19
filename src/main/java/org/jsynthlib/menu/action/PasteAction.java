package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.menu.PatchBayApplication;
import org.jsynthlib.menu.ui.JSLFrame;
import org.jsynthlib.tools.ErrorMsg;

public class PasteAction extends AbstractAction {
	public PasteAction(Map<Serializable, Integer> mnemonics) {
		super("Paste", null);
		this.setEnabled(false);
		mnemonics.put(this, new Integer('P'));
	}

	public void actionPerformed(ActionEvent e) {
		try {
			Actions.getSelectedFrame().pastePatch();
		} catch (Exception ex) {
			ErrorMsg.reportError("Error", "Library to Paste into must be the focused Window.", ex);
		}
	}

	public void setEnabled(boolean b) {
		try {
			JSLFrame f = PatchBayApplication.getDesktop().getSelectedFrame();
			b = b && f.canImport(Actions.cb.getContents(this).getTransferDataFlavors());
			super.setEnabled(b);
		} catch (Exception ex) {
			super.setEnabled(false);
		}
	}
}