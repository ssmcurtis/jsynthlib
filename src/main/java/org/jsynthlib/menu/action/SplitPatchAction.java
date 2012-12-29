package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.tools.ErrorMsg;

public class SplitPatchAction extends AbstractAction {
	public SplitPatchAction(Map<Serializable, Integer> mnemonics) {
		super("Delete", null);
		this.setEnabled(false);
		mnemonics.put(this, new Integer('D'));
	}

	public void actionPerformed(ActionEvent e) {
		try {
			Actions.getSelectedFrame().splitSelectedPatches();
		} catch (Exception ex) {
			ErrorMsg.reportError("Error", "Patch to delete must be hilighted\nin the focused Window.", ex);
		}
	}
}