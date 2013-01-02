package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.menu.Actions;
import org.jsynthlib.tools.ErrorMsgUtil;

public class DeleteAction extends AbstractAction {
	public DeleteAction(Map<Serializable, Integer> mnemonics) {
		super("Delete", null);
		this.setEnabled(false);
		mnemonics.put(this, new Integer('D'));
	}

	public void actionPerformed(ActionEvent e) {
		try {
			Actions.getSelectedFrame().deleteSelectedPatches();
		} catch (Exception ex) {
			ErrorMsgUtil.reportError("Error", "Patch to delete must be hilighted\nin the focused Window."+ getClass().getSimpleName(), ex);
		}
	}
}