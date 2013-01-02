package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.menu.Actions;
import org.jsynthlib.tools.ErrorMsgUtil;

public class PlayCompleteAction extends AbstractAction {
	public PlayCompleteAction(Map<Serializable, Integer> mnemonics) {
		super("Play all", null);
		mnemonics.put(this, new Integer('A'));
		this.setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
		try {
			
			Actions.getSelectedFrame().playAllPatches();
		} catch (Exception ex) {
			ErrorMsgUtil.reportError("Error", "Patch to Play must be highlighted in the focused Window."+ getClass().getSimpleName(), ex);
		}
	}
}