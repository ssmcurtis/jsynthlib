package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.tools.ErrorMsg;

public class CopyAction extends AbstractAction {
	public CopyAction(Map<Serializable, Integer> mnemonics) {
		super("Copy", null);
		this.setEnabled(false);
		mnemonics.put(this, new Integer('C'));
	}

	public void actionPerformed(ActionEvent e) {
		try {
			Actions.getSelectedFrame().copySelectedPatch();
		} catch (Exception ex) {
			ErrorMsg.reportError("Error", "Patch to copy must be highlighted\nin the focused Window.", ex);
		}
	}
}