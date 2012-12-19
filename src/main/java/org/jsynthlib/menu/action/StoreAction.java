package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.tools.ErrorMsg;

public class StoreAction extends AbstractAction {
	public StoreAction(Map<Serializable, Integer> mnemonics) {
		super("Store...", null);
		mnemonics.put(this, new Integer('R'));
		this.setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
		try {
			Actions.getSelectedFrame().storeSelectedPatch();
		} catch (Exception ex) {
			ErrorMsg.reportError("Error", "Patch to Store must be highlighted in the focused Window.", ex);
		}
	}
}