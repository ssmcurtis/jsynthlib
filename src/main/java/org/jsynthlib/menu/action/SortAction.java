package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.menu.PatchBayApplication;
import org.jsynthlib.menu.ui.window.SortDialog;
import org.jsynthlib.tools.ErrorMsg;

public class SortAction extends AbstractAction {
	public SortAction(Map<Serializable, Integer> mnemonics) {
		super("Sort...", null);
		this.setEnabled(false);
		mnemonics.put(this, new Integer('R'));
	}

	public void actionPerformed(ActionEvent e) {
		try {
			SortDialog sd = new SortDialog(PatchBayApplication.getInstance());
			sd.setVisible(true);
		} catch (Exception ex) {
			ErrorMsg.reportError("Error", "Library to Sort must be Focused", ex);
		}
	}
}