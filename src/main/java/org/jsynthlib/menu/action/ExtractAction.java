package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.menu.ui.window.AbstractLibraryFrame;
import org.jsynthlib.tools.ErrorMsg;

public class ExtractAction extends AbstractAction {
	public ExtractAction(Map<Serializable, Integer> mnemonics) {
		super("Extract", null);
		mnemonics.put(this, new Integer('E'));
		this.setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
		try {
			((AbstractLibraryFrame) Actions.getSelectedFrame()).extractSelectedPatch();
		} catch (Exception ex) {
			ErrorMsg.reportError("Error", "Can not Extract (Maybe its not a bank?)", ex);
		}
	}
}