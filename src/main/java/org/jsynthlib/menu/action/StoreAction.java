package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.menu.Actions;
import org.jsynthlib.tools.ErrorMsgUtil;

public class StoreAction extends AbstractAction {
	public StoreAction(Map<Serializable, Integer> mnemonics) {
		super("Store patch", null);
		mnemonics.put(this, new Integer('R'));
		this.setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
		try {
			Actions.getSelectedFrame().storeSelectedPatch();
		} catch (Exception ex) {
			ErrorMsgUtil.reportError("Error", "Patch to Store must be highlighted in the focused Window."+ getClass().getSimpleName(), ex);
		}
	}
}