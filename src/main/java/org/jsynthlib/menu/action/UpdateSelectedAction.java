package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.menu.ui.window.SceneFrame;
import org.jsynthlib.tools.ErrorMsg;

public class UpdateSelectedAction extends AbstractAction {
	public UpdateSelectedAction(Map<Serializable, Integer> mnemonics) {
		super("Update", null);
		setEnabled(false);
		mnemonics.put(this, new Integer('U'));
	}

	public void actionPerformed(ActionEvent e) {
		try {
			// ((SceneFrame) Actions.getSelectedFrame()).updateSelected();
		} catch (Exception ex) {
			ErrorMsg.reportError("Error", "Patches to update must be highlighted\nin the focused Window.", ex);
		}
	}
}