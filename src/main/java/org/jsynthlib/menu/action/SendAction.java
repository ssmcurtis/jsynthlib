package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.tools.ErrorMsg;

public class SendAction extends AbstractAction {
	public SendAction(Map<Serializable, Integer> mnemonics) {
		super("Send", null);
		mnemonics.put(this, new Integer('S'));
		this.setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
		try {
			Actions.getSelectedFrame().sendSelectedPatch();
		} catch (Exception ex) {
			ErrorMsg.reportError("Error", "Patch to Send must be highlighted in the focused Window.", ex);
		}
	}
}