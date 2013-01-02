package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.menu.Actions;
import org.jsynthlib.tools.ErrorMsgUtil;

public class ReassignAction extends AbstractAction {
	public ReassignAction(Map<Serializable, Integer> mnemonics) {
		super("Reassign...", null); // show a dialog frame???
		// mnemonics.put(this, new Integer('R'));
		this.setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
		try {
			Actions.getSelectedFrame().reassignSelectedPatch();
		} catch (Exception ex) {
			ErrorMsgUtil.reportError("Error", "Patch to Reassign must be highlighted in the focused Window."+ getClass().getSimpleName(), ex);
		}
	}
}