package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.tools.ErrorMsg;

public class CutAction extends AbstractAction {
	public CutAction(Map<Serializable, Integer> mnemonics) {
		super("Cut", null);
		this.setEnabled(false);
		mnemonics.put(this, new Integer('T'));
	}

	public void actionPerformed(ActionEvent e) {
		try {
			Actions.getSelectedFrame().copySelectedPatch();
			Actions.getSelectedFrame().deleteSelectedPatch();
		} catch (Exception ex) {
			ErrorMsg.reportError("Error", "Patch to cut must be hilighted\nin the focused Window.", ex);
		}
	}
}